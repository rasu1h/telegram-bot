package com.telergambot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на отправку сообщения")
public class MessageRequest {
    
    @Schema(description = "Текст сообщения для отправки в Telegram", 
            example = "Привет из API!", 
            required = true)
    private String message;
}