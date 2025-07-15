package com.telergambot.domain.repositories;

import com.telergambot.domain.entities.TelegramToken;

import java.util.Optional;

public interface TelegramTokenRepository {
    TelegramToken save(TelegramToken token);
    Optional<TelegramToken> findByToken(String token);
    Optional<TelegramToken> findByUserId(Long userId);
    Optional<TelegramToken> findActiveByUserId(Long userId);
    Optional<TelegramToken> findByTelegramChatId(String telegramChatId);
    boolean existsByToken(String token);
}