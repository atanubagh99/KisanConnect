package com.kisanconnect.speech;

import com.kisanconnect.speech.dto.TranscriptionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Groq Whisper-based Speech-to-Text service.
 * Uses Groq's OpenAI-compatible audio transcription endpoint.
 * Returns both transcribed text AND detected language.
 */
@Slf4j
@Service
public class HuggingFaceSttService implements SpeechToTextService {

    private final RestClient restClient;
    private final String model;

    public HuggingFaceSttService(
            @Value("${groq.api-key:${GROQ_API_KEY:}}") String apiKey,
            @Value("${kisanconnect.huggingface.stt-model:whisper-large-v3-turbo}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        log.info("🎤 STT initialized: Groq Whisper (model: {})", model);
    }

    @Override
    @Retryable(retryFor = { HttpServerErrorException.class,
            ResourceAccessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public TranscriptionResult transcribe(byte[] audioBytes, String languageHint) {
        log.info("🎤 Transcribing via Groq Whisper (model: {}, audio: {} bytes)", model, audioBytes.length);

        try {
            // Use verbose_json to get detected language from Whisper
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/audio/transcriptions")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(buildMultipartBody(audioBytes))
                    .retrieve()
                    .body(Map.class);

            String transcribedText = "";
            String detectedLanguage = "hi"; // fallback

            if (response != null) {
                if (response.containsKey("text")) {
                    transcribedText = ((String) response.get("text")).trim();
                }
                // verbose_json returns "language" field with ISO code
                if (response.containsKey("language")) {
                    String lang = (String) response.get("language");
                    detectedLanguage = mapWhisperLanguage(lang);
                    log.info("🌐 Whisper detected language: {} → {}", lang, detectedLanguage);
                }
            }

            if (transcribedText.isEmpty()) {
                log.warn("⚠️ Whisper returned empty text");
                transcribedText = "general agriculture question";
            }

            // Override with hint if provided (user explicitly set language)
            if (languageHint != null && !languageHint.isBlank()) {
                detectedLanguage = languageHint;
            }

            log.info("📝 Transcription: '{}' (lang: {})", truncate(transcribedText, 100), detectedLanguage);
            return new TranscriptionResult(transcribedText, detectedLanguage, 0.9);

        } catch (Exception e) {
            log.error("❌ Groq STT failed: {}", e.getMessage());
            return new TranscriptionResult(
                    "general agriculture question",
                    languageHint != null && !languageHint.isBlank() ? languageHint : "hi",
                    0.0);
        }
    }

    private org.springframework.util.MultiValueMap<String, org.springframework.http.HttpEntity<?>> buildMultipartBody(
            byte[] audioBytes) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("file", new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "audio.ogg";
            }
        }).contentType(MediaType.parseMediaType("audio/ogg"));

        builder.part("model", model);
        // Request verbose_json to get detected language
        builder.part("response_format", "verbose_json");

        return builder.build();
    }

    /**
     * Map Whisper's language name to ISO 639-1 code.
     * Whisper returns full language names like "hindi", "marathi", etc.
     */
    private String mapWhisperLanguage(String whisperLang) {
        if (whisperLang == null)
            return "hi";
        return switch (whisperLang.toLowerCase()) {
            case "hindi" -> "hi";
            case "bengali" -> "bn";
            case "telugu" -> "te";
            case "tamil" -> "ta";
            case "kannada" -> "kn";
            case "malayalam" -> "ml";
            case "marathi" -> "mr";
            case "gujarati" -> "gu";
            case "odia", "oriya" -> "or";
            case "punjabi" -> "pa";
            case "urdu" -> "ur";
            case "assamese" -> "as";
            case "sanskrit" -> "sa";
            case "nepali" -> "ne";
            case "english" -> "en";
            default -> whisperLang.length() <= 3 ? whisperLang : "hi";
        };
    }

    @Override
    public List<String> supportedLanguages() {
        return List.of("hi", "bn", "ta", "te", "kn", "ml", "mr", "gu", "pa", "or", "ur",
                "as", "sa", "ne", "sd", "ks", "doi", "mni", "en");
    }

    private String truncate(String text, int maxLength) {
        if (text == null)
            return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}
