package com.telergambot.domain.repositories;

import com.telergambot.domain.entities.Message;

import java.util.List;

public interface MessageRepository {
    Message save(Message message);
    List<Message> findByUserId(Long userId);
    List<Message> findUndeliveredByUserId(Long userId);
}