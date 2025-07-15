package com.telergambot.application.usecases;

import com.telergambot.application.dto.MessageRequest;
import com.telergambot.domain.entities.Message;
import com.telergambot.domain.entities.TelegramToken;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.MessageRepository;
import com.telergambot.domain.repositories.TelegramTokenRepository;
import com.telergambot.domain.repositories.UserRepository;
import com.telergambot.infrastructure.telegram.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageUseCase {
    
    private final UserRepository userRepository;
    private final TelegramTokenRepository telegramTokenRepository;
    private final MessageRepository messageRepository;
    private final TelegramBotService telegramBotService;
    
    public void execute(MessageRequest request, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Сохраняем сообщение
        Message message = Message.builder()
                .userId(user.getId())
                .content(request.getMessage())
                .build();
        
        Message savedMessage = messageRepository.save(message);
        
        // Находим активный токен пользователя
        TelegramToken token = telegramTokenRepository.findActiveByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No active token found. Please generate a token first."));
        
        if (!token.isBound() || token.getTelegramChatId() == null) {
            throw new RuntimeException("Token not bound to Telegram chat. Please send the token to your Telegram bot.");
        }
        
        // Отправляем сообщение в Telegram
        try {
            telegramBotService.sendMessageToUser(token.getTelegramChatId(), user.getName(), request.getMessage());
            
            // Обновляем статус доставки
            savedMessage.setDeliveredToTelegram(true);
            savedMessage.setDeliveredAt(LocalDateTime.now());
            messageRepository.save(savedMessage);
            
            log.info("Message sent to Telegram for user: {}", username);
        } catch (Exception e) {
            log.error("Failed to send message to Telegram for user: {}", username, e);
            throw new RuntimeException("Failed to send message to Telegram");
        }
    }
}