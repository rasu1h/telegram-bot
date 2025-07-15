package com.telergambot.infrastructure.persistence.mappers;

import com.telergambot.domain.entities.Message;
import com.telergambot.infrastructure.persistence.entities.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    
    public Message toDomain(MessageEntity entity) {
        if (entity == null) return null;
        
        return Message.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .content(entity.getContent())
                .sentAt(entity.getSentAt())
                .deliveredToTelegram(entity.isDeliveredToTelegram())
                .deliveredAt(entity.getDeliveredAt())
                .build();
    }
    
    public MessageEntity toEntity(Message domain) {
        if (domain == null) return null;
        
        MessageEntity entity = new MessageEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setContent(domain.getContent());
        entity.setSentAt(domain.getSentAt());
        entity.setDeliveredToTelegram(domain.isDeliveredToTelegram());
        entity.setDeliveredAt(domain.getDeliveredAt());
        
        return entity;
    }
}