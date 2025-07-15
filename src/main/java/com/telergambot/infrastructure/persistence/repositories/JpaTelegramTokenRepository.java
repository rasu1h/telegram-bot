package com.telergambot.infrastructure.persistence.repositories;

import com.telergambot.infrastructure.persistence.entities.TelegramTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaTelegramTokenRepository extends JpaRepository<TelegramTokenEntity, Long> {
    Optional<TelegramTokenEntity> findByToken(String token);
    Optional<TelegramTokenEntity> findByUserId(Long userId);
    Optional<TelegramTokenEntity> findByUserIdAndIsActiveTrue(Long userId);
    Optional<TelegramTokenEntity> findByTelegramChatId(String telegramChatId);
    boolean existsByToken(String token);
}