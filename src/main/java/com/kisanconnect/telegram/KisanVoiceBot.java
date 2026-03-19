package com.kisanconnect.telegram;

import com.kisanconnect.advisory.AdvisoryOrchestrator;
import com.kisanconnect.telegram.dto.AdvisoryResponse;
import com.kisanconnect.telegram.dto.VoiceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Main Telegram bot — handles incoming voice messages and routes them
 * through the advisory pipeline.
 */
@Slf4j
@Component
public class KisanVoiceBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final AdvisoryOrchestrator advisoryOrchestrator;
    private final HttpClient httpClient;

    public KisanVoiceBot(
            @Value("${kisanconnect.telegram.bot-token:}") String botToken,
            @Value("${kisanconnect.telegram.bot-username:KisanVoiceBot}") String botUsername,
            AdvisoryOrchestrator advisoryOrchestrator) {
        super(botToken);
        this.botUsername = botUsername;
        this.advisoryOrchestrator = advisoryOrchestrator;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasVoice()) {
                handleVoiceMessage(update);
            } else if (update.getMessage().hasText()) {
                handleTextMessage(update);
            } else {
                sendTextReply(chatId,
                        "🎤 Please send a voice message with your agricultural question.\n" +
                                "कृपया अपना कृषि प्रश्न वॉइस मैसेज में भेजें।");
            }
        }
    }

    /**
     * Handle incoming voice messages — the core flow.
     */
    private void handleVoiceMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        Voice voice = update.getMessage().getVoice();

        log.info("Received voice message from chat {} (duration: {}s, size: {} bytes)",
                chatId, voice.getDuration(), voice.getFileSize());

        // Send "thinking" indicator immediately
        sendTextReply(chatId, "🔄 Processing your question... / आपका प्रश्न प्रोसेस हो रहा है...");

        try {
            // Build voice message DTO
            VoiceMessage voiceMessage = new VoiceMessage(
                    chatId,
                    voice.getFileId(),
                    voice.getDuration(),
                    update.getMessage().getFrom().getId(),
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getLanguageCode());

            // Download audio from Telegram
            byte[] audioBytes = downloadVoiceFile(voice.getFileId());

            // Process through advisory pipeline
            AdvisoryResponse response = advisoryOrchestrator.processVoiceQuery(
                    audioBytes, voiceMessage);

            // Send text advisory
            sendTextReply(chatId, "📋 " + response.advisoryText());

            // Send audio advisory (if available)
            if (response.audioBytes() != null && response.audioBytes().length > 0) {
                sendVoiceReply(chatId, response.audioBytes());
            }

        } catch (Exception e) {
            log.error("Failed to process voice message from chat {}", chatId, e);
            sendTextReply(chatId,
                    "❌ Sorry, an error occurred. Please try again.\n" +
                            "क्षमा करें, एक त्रुटि हुई। कृपया पुनः प्रयास करें।");
        }
    }

    /**
     * Handle text messages — provide guidance to use voice.
     */
    private void handleTextMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if ("/start".equals(text)) {
            sendTextReply(chatId,
                    "🌾 *Welcome to KisanConnect!*\n\n" +
                            "I am your AI agricultural advisor. Send me a *voice message* " +
                            "with your farming question in any Indian language, and I'll " +
                            "provide expert advice!\n\n" +
                            "🎤 *Supported languages:* Hindi, Bengali, Telugu, Tamil, " +
                            "Kannada, Malayalam, Marathi, Gujarati, Odia, Punjabi, Urdu, English\n\n" +
                            "आप अपनी भाषा में वॉइस मैसेज भेजकर कृषि सलाह प्राप्त कर सकते हैं।");
        } else if ("/help".equals(text)) {
            sendTextReply(chatId,
                    "🎤 *How to use KisanConnect:*\n\n" +
                            "1. Hold the mic button in Telegram\n" +
                            "2. Ask your agricultural question\n" +
                            "3. Release to send\n" +
                            "4. Wait for AI advisory (text + audio)\n\n" +
                            "*Example questions:*\n" +
                            "• \"मेरे गेहूं में पीला रतुआ लगा है, क्या करूं?\"\n" +
                            "• \"Cotton pest control methods?\"\n" +
                            "• \"ನನ್ನ ರಾಗಿ ಬೆಳೆಗೆ ಯಾವ ಗೊಬ್ಬರ ಬೇಕು?\"");
        } else {
            sendTextReply(chatId,
                    "🎤 Please send a *voice message* with your question.\n" +
                            "कृपया वॉइस मैसेज भेजें।\n\n" +
                            "Type /help for instructions.");
        }
    }

    /**
     * Download a voice file from Telegram servers.
     */
    private byte[] downloadVoiceFile(String fileId) throws Exception {
        // Get file path from Telegram
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFile);

        // Download the file
        String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + telegramFile.getFilePath();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        log.info("Downloaded voice file: {} bytes", response.body().length);
        return response.body();
    }

    /**
     * Send a text reply to a chat.
     */
    private void sendTextReply(long chatId, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            message.setParseMode("Markdown");
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send text message to chat {}", chatId, e);
        }
    }

    /**
     * Send a voice reply to a chat.
     */
    private void sendVoiceReply(long chatId, byte[] audioBytes) {
        try {
            InputStream audioStream = new ByteArrayInputStream(audioBytes);
            SendVoice sendVoice = new SendVoice();
            sendVoice.setChatId(String.valueOf(chatId));
            sendVoice.setVoice(new InputFile(audioStream, "advisory.ogg"));
            execute(sendVoice);
        } catch (TelegramApiException e) {
            log.error("Failed to send voice message to chat {}", chatId, e);
        }
    }
}
