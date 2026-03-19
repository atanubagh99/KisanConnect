package com.kisanconnect.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for audio file operations — downloading, format conversion,
 * and temporary file management.
 * <p>
 * Handles Telegram's OGG/OPUS format conversion to WAV for STT processing.
 */
@Slf4j
@Service
public class AudioFileService {

    /**
     * Convert OGG/OPUS audio (Telegram's format) to WAV 16kHz mono.
     * Uses FFmpeg if available, otherwise returns raw bytes for STT to handle.
     *
     * @param oggBytes Raw OGG/OPUS audio bytes from Telegram
     * @return WAV audio bytes suitable for STT processing
     */
    public byte[] convertOggToWav(byte[] oggBytes) {
        log.info("Converting OGG audio to WAV (input: {} bytes)", oggBytes.length);

        // TODO: Implement FFmpeg conversion via ProcessBuilder
        // Command: ffmpeg -i input.ogg -ar 16000 -ac 1 -f wav output.wav
        //
        // ProcessBuilder pb = new ProcessBuilder(
        // "ffmpeg", "-i", "pipe:0", "-ar", "16000", "-ac", "1", "-f", "wav", "pipe:1"
        // );
        // pb.redirectErrorStream(true);
        // Process process = pb.start();
        // process.getOutputStream().write(oggBytes);
        // process.getOutputStream().close();
        // byte[] wavBytes = process.getInputStream().readAllBytes();

        // For now, pass through raw bytes — many STT services accept OGG directly
        log.info("FFmpeg conversion not yet configured — passing OGG bytes directly to STT");
        return oggBytes;
    }

    /**
     * Convert WAV audio to OGG/OPUS for Telegram playback.
     *
     * @param wavBytes WAV audio bytes from TTS
     * @return OGG/OPUS audio bytes for Telegram
     */
    public byte[] convertWavToOgg(byte[] wavBytes) {
        log.info("Converting WAV audio to OGG (input: {} bytes)", wavBytes.length);

        // TODO: Implement FFmpeg conversion
        // Command: ffmpeg -i input.wav -c:a libopus -b:a 16k -f ogg output.ogg

        log.info("FFmpeg conversion not yet configured — passing WAV bytes directly");
        return wavBytes;
    }
}
