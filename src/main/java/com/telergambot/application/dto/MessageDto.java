package com.telergambot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о сообщении")
public class MessageDto {
    
    @Schema(description = "ID сообщения", example = "1")
    private Long id;
    
    @Schema(description = "Текст сообщения", example = "Привет из API!")
    private String content;
    
    @Schema(description = "Время отправки", example = "2024-01-01T12:00:00")
    private LocalDateTime sentAt;
    
    @Schema(description = "Доставлено ли в Telegram", example = "true")
    private boolean deliveredToTelegram;
    
    @Schema(description = "Время доставки в Telegram", example = "2024-01-01T12:00:01")
    private LocalDateTime deliveredAt;
}