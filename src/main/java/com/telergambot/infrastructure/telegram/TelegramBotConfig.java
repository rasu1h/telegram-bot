package com.telergambot.infrastructure.telegram;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotConfig {
    private String username;
    private String token;
    private String webhookPath;
}