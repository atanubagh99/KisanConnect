package com.kisanconnect.advisory;

import com.kisanconnect.advisory.dto.FarmerQuery;
import com.kisanconnect.speech.SpeechToTextService;
import com.kisanconnect.speech.TextToSpeechService;
import com.kisanconnect.speech.dto.TranscriptionResult;
import com.kisanconnect.telegram.AudioFileService;
import com.kisanconnect.telegram.dto.AdvisoryResponse;
import com.kisanconnect.telegram.dto.VoiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Main orchestrator for the voice advisory pipeline.
 * <p>
 * Simplified flow (no translation round-trip):
 * Voice → STT (detect language) → LLM (respond in detected language) → TTS →
 * Audio
 * <p>
 * The LLM is instructed to respond directly in the farmer's language.
 * This avoids the unreliable HuggingFace translation API dependency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvisoryOrchestrator {

        private final SpeechToTextService sttService;
        private final TextToSpeechService ttsService;
        private final AdvisoryService advisoryService;
        private final AudioFileService audioFileService;

        /**
         * Process a farmer's voice query end-to-end.
         */
        public AdvisoryResponse processVoiceQuery(byte[] audioBytes, VoiceMessage voiceMessage) {
                long startTime = System.currentTimeMillis();
                log.info("🎤 Starting pipeline for user {} (chat: {})",
                                voiceMessage.userId(), voiceMessage.chatId());

                // Step 1: Convert audio format (OGG → WAV) — stub for now
                byte[] wavBytes = audioFileService.convertOggToWav(audioBytes);

                // Step 2: Speech-to-Text (Groq Whisper — detects language automatically)
                TranscriptionResult transcription = sttService.transcribe(
                                wavBytes, voiceMessage.languageCode());
                String sttText = truncate(transcription.text(), 500);
                log.info("📝 STT: lang={}, text='{}' (raw={}chars)",
                                transcription.language(), truncate(sttText, 80),
                                transcription.text().length());

                // Step 3: Generate advisory DIRECTLY in farmer's language (no translation
                // needed)
                // The LLM is smart enough to respond in Hindi, Marathi, Bengali, etc.
                FarmerQuery query = FarmerQuery.of(
                                sttText,
                                transcription.language(), // Use detected language, not "en"
                                voiceMessage.userId());
                String advisory = advisoryService.generateAdvisory(query);
                log.info("📋 Advisory: {} chars (lang: {})", advisory.length(), transcription.language());

                // Step 4: Text-to-Speech (in farmer's language)
                byte[] ttsBytes;
                try {
                        var audioResult = ttsService.synthesize(advisory, transcription.language());
                        ttsBytes = audioFileService.convertWavToOgg(audioResult.audioBytes());
                } catch (Exception e) {
                        log.warn("⚠️ TTS failed, sending text only: {}", e.getMessage());
                        ttsBytes = new byte[0]; // Empty — bot will send text only
                }

                long elapsed = System.currentTimeMillis() - startTime;
                log.info("✅ Pipeline complete in {}ms (lang: {})", elapsed, transcription.language());

                return new AdvisoryResponse(
                                voiceMessage.chatId(),
                                advisory,
                                ttsBytes,
                                transcription.language());
        }

        private String truncate(String text, int maxLength) {
                if (text == null)
                        return "";
                return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
        }
}
