package com.telergambot.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "bound_at")
    private LocalDateTime boundAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @Column(name = "is_bound", nullable = false)
    private boolean isBound = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = createdAt.plusMinutes(10); // 10 минут на привязку токена
        }
    }
}