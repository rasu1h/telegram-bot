package com.telergambot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с токеном для Telegram")
public class TokenResponse {
    
    @Schema(description = "UUID токен для привязки Telegram чата. Действителен 10 минут.", 
            example = "550e8400-e29b-41d4-a716-446655440000")
    private String token;
}