package com.telergambot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegisterRequest {
    
    @Schema(description = "Имя пользователя для входа в систему", example = "user123", required = true)
    private String username;
    
    @Schema(description = "Пароль", example = "password123", required = true, minLength = 6)
    private String password;
    
    @Schema(description = "Email адрес", example = "user@example.com", required = true)
    private String email;
    
    @Schema(description = "Полное имя пользователя", example = "John Doe", required = true)
    private String name;
}