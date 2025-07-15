package com.telergambot.infrastructure.persistence.repositories;

import com.telergambot.infrastructure.persistence.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaMessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByUserIdOrderBySentAtDesc(Long userId);
    List<MessageEntity> findByUserIdAndDeliveredToTelegramFalse(Long userId);
}