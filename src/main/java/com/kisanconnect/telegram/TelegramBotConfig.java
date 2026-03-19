package com.kisanconnect.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Configuration for Telegram Bot registration.
 * Gracefully handles missing or invalid bot tokens.
 */
@Slf4j
@Configuration
public class TelegramBotConfig {

    @Value("${kisanconnect.telegram.bot-token:}")
    private String botToken;

    @Value("${kisanconnect.telegram.bot-username:KisanConnectBot}")
    private String botUsername;

    @Bean
    public TelegramBotsApi telegramBotsApi(KisanVoiceBot kisanVoiceBot) {
        if (botToken == null || botToken.isBlank()) {
            log.warn("⚠️ Telegram bot token not configured. Set TELEGRAM_BOT_TOKEN to enable the bot.");
            return createEmptyApi();
        }

        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(kisanVoiceBot);
            log.info("✅ Telegram bot '{}' registered successfully", botUsername);
            return api;
        } catch (TelegramApiException e) {
            log.error("❌ Failed to register Telegram bot '{}': {}", botUsername, e.getMessage());
            log.warn("The application will continue without Telegram bot functionality.");
            return createEmptyApi();
        }
    }

    private TelegramBotsApi createEmptyApi() {
        try {
            return new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Cannot initialize Telegram API", e);
        }
    }
}
