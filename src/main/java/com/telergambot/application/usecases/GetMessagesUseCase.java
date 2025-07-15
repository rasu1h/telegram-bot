package com.telergambot.application.usecases;

import com.telergambot.application.dto.MessageDto;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.MessageRepository;
import com.telergambot.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMessagesUseCase {
    
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    
    public List<MessageDto> execute(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return messageRepository.findByUserId(user.getId())
                .stream()
                .map(message -> MessageDto.builder()
                        .id(message.getId())
                        .content(message.getContent())
                        .sentAt(message.getSentAt())
                        .deliveredToTelegram(message.isDeliveredToTelegram())
                        .deliveredAt(message.getDeliveredAt())
                        .build())
                .collect(Collectors.toList());
    }
}