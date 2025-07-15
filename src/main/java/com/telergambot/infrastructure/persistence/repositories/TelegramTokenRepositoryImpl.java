package com.telergambot.infrastructure.persistence.repositories;

import com.telergambot.domain.entities.TelegramToken;
import com.telergambot.domain.repositories.TelegramTokenRepository;
import com.telergambot.infrastructure.persistence.mappers.TelegramTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TelegramTokenRepositoryImpl implements TelegramTokenRepository {
    
    private final JpaTelegramTokenRepository jpaTelegramTokenRepository;
    private final TelegramTokenMapper telegramTokenMapper;
    
    @Override
    public TelegramToken save(TelegramToken token) {
        var entity = telegramTokenMapper.toEntity(token);
        var savedEntity = jpaTelegramTokenRepository.save(entity);
        return telegramTokenMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<TelegramToken> findByToken(String token) {
        return jpaTelegramTokenRepository.findByToken(token)
                .map(telegramTokenMapper::toDomain);
    }
    
    @Override
    public Optional<TelegramToken> findByUserId(Long userId) {
        return jpaTelegramTokenRepository.findByUserId(userId)
                .map(telegramTokenMapper::toDomain);
    }
    
    @Override
    public Optional<TelegramToken> findActiveByUserId(Long userId) {
        return jpaTelegramTokenRepository.findByUserIdAndIsActiveTrue(userId)
                .map(telegramTokenMapper::toDomain);
    }
    
    @Override
    public Optional<TelegramToken> findByTelegramChatId(String telegramChatId) {
        return jpaTelegramTokenRepository.findByTelegramChatId(telegramChatId)
                .map(telegramTokenMapper::toDomain);
    }
    
    @Override
    public boolean existsByToken(String token) {
        return jpaTelegramTokenRepository.existsByToken(token);
    }
}