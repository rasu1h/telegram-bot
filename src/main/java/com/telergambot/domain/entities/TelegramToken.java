package com.telergambot.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramToken {
    private Long id;
    private Long userId;
    private String token;
    private String telegramChatId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime boundAt;
    private boolean isActive;
    private boolean isBound;
}