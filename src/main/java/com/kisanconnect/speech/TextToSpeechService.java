package com.kisanconnect.speech;

import com.kisanconnect.speech.dto.AudioResult;

/**
 * Interface for Text-to-Speech services.
 * Implementations can use Airavata, IndicParler-TTS, Bhashini, or any TTS
 * provider.
 */
public interface TextToSpeechService {

    /**
     * Synthesize text into audio in the specified language.
     *
     * @param text     The text to convert to speech
     * @param language Target language code (ISO 639-1, e.g., "hi", "te", "kn")
     * @return audio result with synthesized audio bytes and metadata
     */
    AudioResult synthesize(String text, String language);

    /**
     * Returns the list of supported language codes.
     */
    java.util.List<String> supportedLanguages();
}
