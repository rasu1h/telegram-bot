package com.telergambot.application.usecases;

import com.telergambot.domain.entities.TelegramToken;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.TelegramTokenRepository;
import com.telergambot.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateTokenUseCase {
    
    private final UserRepository userRepository;
    private final TelegramTokenRepository telegramTokenRepository;
    
    public String execute(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Деактивируем старые токены
        telegramTokenRepository.findActiveByUserId(user.getId())
                .ifPresent(oldToken -> {
                    oldToken.setActive(false);
                    telegramTokenRepository.save(oldToken);
                });
        
        // Генерируем новый токен
        String tokenValue = UUID.randomUUID().toString();
        
        TelegramToken newToken = TelegramToken.builder()
                .userId(user.getId())
                .token(tokenValue)
                .isActive(true)
                .isBound(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10)) // Токен действителен 10 минут
                .build();
        
        telegramTokenRepository.save(newToken);
        
        return tokenValue;
    }
}