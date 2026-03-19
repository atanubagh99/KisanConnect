package com.kisanconnect.speech;

import com.kisanconnect.speech.dto.AudioResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * HuggingFace Indic Parler-TTS text-to-speech service.
 * Uses AI4Bharat's Indic Parler-TTS model via HuggingFace Inference API.
 * <p>
 * Supports 20+ Indian languages with natural, expressive voices.
 * Apache 2.0 license — free to use.
 */
@Slf4j
@Service
public class HuggingFaceTtsService implements TextToSpeechService {

    private final RestClient restClient;
    private final String model;

    /**
     * Language code to full name mapping for Indic Parler-TTS prompt formatting.
     * The model uses descriptive prompts like: "A female speaker delivers in
     * Bengali..."
     */
    private static final Map<String, String> LANGUAGE_NAMES = Map.ofEntries(
            Map.entry("hi", "Hindi"),
            Map.entry("bn", "Bengali"),
            Map.entry("ta", "Tamil"),
            Map.entry("te", "Telugu"),
            Map.entry("kn", "Kannada"),
            Map.entry("ml", "Malayalam"),
            Map.entry("mr", "Marathi"),
            Map.entry("gu", "Gujarati"),
            Map.entry("pa", "Punjabi"),
            Map.entry("or", "Odia"),
            Map.entry("ur", "Urdu"),
            Map.entry("as", "Assamese"),
            Map.entry("en", "English"));

    public HuggingFaceTtsService(
            @Value("${kisanconnect.huggingface.api-key:}") String apiKey,
            @Value("${kisanconnect.huggingface.tts-model:ai4bharat/indic-parler-tts}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api-inference.huggingface.co")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    @Retryable(retryFor = { HttpServerErrorException.class,
            ResourceAccessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public AudioResult synthesize(String text, String language) {
        log.info("🔊 Synthesizing speech via HuggingFace TTS (model: {}, language: {}, text length: {})",
                model, language, text.length());

        try {
            // Indic Parler-TTS uses a descriptive prompt for voice control
            String languageName = LANGUAGE_NAMES.getOrDefault(language, "Hindi");
            String description = String.format(
                    "A female speaker delivers a clear, natural, and helpful advisory in %s " +
                            "with a moderate pace and warm tone.",
                    languageName);

            // Build request payload
            Map<String, Object> payload = Map.of(
                    "inputs", text,
                    "parameters", Map.of(
                            "description", description));

            byte[] audioBytes = restClient.post()
                    .uri("/models/{model}", model)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(byte[].class);

            if (audioBytes == null || audioBytes.length == 0) {
                log.warn("⚠️ TTS returned empty audio");
                return new AudioResult(new byte[0], "wav", 16000, 0);
            }

            log.info("✅ TTS complete: {} bytes of audio generated", audioBytes.length);
            return new AudioResult(audioBytes, "wav", 22050, estimateDurationMs(audioBytes));

        } catch (Exception e) {
            log.error("❌ HuggingFace TTS failed", e);
            return new AudioResult(new byte[0], "wav", 16000, 0);
        }
    }

    @Override
    public List<String> supportedLanguages() {
        return List.of("hi", "bn", "ta", "te", "kn", "ml", "mr", "gu", "pa", "or",
                "ur", "as", "en");
    }

    private long estimateDurationMs(byte[] audioBytes) {
        // Rough estimate: WAV 22050Hz 16-bit mono ≈ 44100 bytes/sec
        return (audioBytes.length * 1000L) / 44100;
    }
}
