package com.kisanconnect.telegram.dto;

/**
 * Response sent back to the farmer via Telegram.
 *
 * @param chatId       Telegram chat ID
 * @param advisoryText The advisory text (also sent as a message)
 * @param audioBytes   Synthesized audio of the advisory (OGG format)
 * @param language     Language code of the response
 */
public record AdvisoryResponse(
        long chatId,
        String advisoryText,
        byte[] audioBytes,
        String language) {
}
