package com.kisanconnect.speech.dto;

/**
 * Result of text-to-speech synthesis.
 *
 * @param audioBytes Synthesized audio data (WAV or OGG)
 * @param format     Audio format (e.g., "wav", "ogg")
 * @param sampleRate Audio sample rate in Hz
 * @param durationMs Duration of the audio in milliseconds
 */
public record AudioResult(
        byte[] audioBytes,
        String format,
        int sampleRate,
        long durationMs) {
}
