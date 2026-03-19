package com.kisanconnect.speech.dto;

/**
 * Result of speech-to-text transcription.
 *
 * @param text       Transcribed text from the farmer's voice note
 * @param language   Detected language code (e.g., "hi", "te", "kn", "en")
 * @param confidence Transcription confidence score (0.0 to 1.0)
 */
public record TranscriptionResult(
        String text,
        String language,
        double confidence) {
}
