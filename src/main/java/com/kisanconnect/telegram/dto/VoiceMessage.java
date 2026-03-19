package com.kisanconnect.telegram.dto;

/**
 * Represents an incoming voice message from Telegram.
 *
 * @param chatId       Telegram chat ID for sending responses
 * @param fileId       Telegram file_id for downloading the voice note
 * @param duration     Duration of the voice note in seconds
 * @param userId       Telegram user ID
 * @param firstName    User's first name for personalized responses
 * @param languageCode Telegram client language code (hint, may be null)
 */
public record VoiceMessage(
        long chatId,
        String fileId,
        int duration,
        long userId,
        String firstName,
        String languageCode) {
}
