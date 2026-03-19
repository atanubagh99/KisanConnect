package com.kisanconnect.advisory.dto;

/**
 * Represents a farmer's query with contextual metadata.
 *
 * @param text     Transcribed query text
 * @param language Detected language code
 * @param userId   Telegram user ID (for personalization / history)
 * @param district District name (if known, for local context)
 * @param state    State name (if known)
 */
public record FarmerQuery(
        String text,
        String language,
        long userId,
        String district,
        String state) {
    /**
     * Factory for creating a query with minimal info (no geo context).
     */
    public static FarmerQuery of(String text, String language, long userId) {
        return new FarmerQuery(text, language, userId, null, null);
    }
}
