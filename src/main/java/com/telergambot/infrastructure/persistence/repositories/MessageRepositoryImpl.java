package com.telergambot.infrastructure.persistence.repositories;

import com.telergambot.domain.entities.Message;
import com.telergambot.domain.repositories.MessageRepository;
import com.telergambot.infrastructure.persistence.mappers.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {
    
    private final JpaMessageRepository jpaMessageRepository;
    private final MessageMapper messageMapper;
    
    @Override
    public Message save(Message message) {
        var entity = messageMapper.toEntity(message);
        var savedEntity = jpaMessageRepository.save(entity);
        return messageMapper.toDomain(savedEntity);
    }
    
    @Override
    public List<Message> findByUserId(Long userId) {
        return jpaMessageRepository.findByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Message> findUndeliveredByUserId(Long userId) {
        return jpaMessageRepository.findByUserIdAndDeliveredToTelegramFalse(userId)
                .stream()
                .map(messageMapper::toDomain)
                .collect(Collectors.toList());
    }
}