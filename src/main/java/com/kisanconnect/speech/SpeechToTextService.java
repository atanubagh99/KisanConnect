package com.kisanconnect.speech;

import com.kisanconnect.speech.dto.TranscriptionResult;

/**
 * Interface for Speech-to-Text services.
 * Implementations can use Dhwani, IndicWhisper, Bhashini, or any STT provider.
 */
public interface SpeechToTextService {

    /**
     * Transcribe audio bytes to text with language detection.
     *
     * @param audioBytes   Raw audio data (WAV format, 16kHz mono preferred)
     * @param languageHint Optional language hint (ISO 639-1 code, e.g., "hi", "te")
     * @return transcription result with text, detected language, and confidence
     */
    TranscriptionResult transcribe(byte[] audioBytes, String languageHint);

    /**
     * Returns the list of supported language codes.
     */
    java.util.List<String> supportedLanguages();
}
