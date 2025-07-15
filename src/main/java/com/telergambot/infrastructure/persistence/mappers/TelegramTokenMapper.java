package com.telergambot.infrastructure.persistence.mappers;

import com.telergambot.domain.entities.TelegramToken;
import com.telergambot.infrastructure.persistence.entities.TelegramTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class TelegramTokenMapper {
    
    public TelegramToken toDomain(TelegramTokenEntity entity) {
        if (entity == null) return null;
        
        return TelegramToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .telegramChatId(entity.getTelegramChatId())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .boundAt(entity.getBoundAt())
                .isActive(entity.isActive())
                .isBound(entity.isBound())
                .build();
    }
    
    public TelegramTokenEntity toEntity(TelegramToken domain) {
        if (domain == null) return null;
        
        TelegramTokenEntity entity = new TelegramTokenEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setToken(domain.getToken());
        entity.setTelegramChatId(domain.getTelegramChatId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setBoundAt(domain.getBoundAt());
        entity.setActive(domain.isActive());
        entity.setBound(domain.isBound());
        
        return entity;
    }
}