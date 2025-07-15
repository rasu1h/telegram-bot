package com.telergambot.presentation.controllers;

import com.telergambot.application.dto.MessageDto;
import com.telergambot.application.dto.MessageRequest;
import com.telergambot.application.dto.TokenResponse;
import com.telergambot.application.usecases.GenerateTokenUseCase;
import com.telergambot.application.usecases.GetMessagesUseCase;
import com.telergambot.application.usecases.SendMessageUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
@Tag(name = "Telegram Integration", description = "Endpoints для работы с Telegram ботом")
@SecurityRequirement(name = "bearerAuth")
public class TelegramController {
    
    private final GenerateTokenUseCase generateTokenUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    
    @PostMapping("/token/generate")
    @Operation(summary = "Генерация токена для Telegram", 
              description = "Создает уникальный токен для привязки Telegram чата. " +
                 "Токен действителен 10 минут и может быть использован только один раз.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Токен успешно сгенерирован",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
        @ApiResponse(responseCode = "401", 
                    description = "Не авторизован")
    })
    public ResponseEntity<TokenResponse> generateToken(
            @Parameter(hidden = true) Authentication authentication) {
        String token = generateTokenUseCase.execute(authentication);
        return ResponseEntity.ok(new TokenResponse(token));
    }
    
    @PostMapping("/message/send")
    @Operation(summary = "Отправка сообщения в Telegram", 
              description = "Отправляет сообщение в привязанный Telegram чат. " +
                 "Требует предварительной привязки токена в Telegram боте.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Сообщение успешно отправлено"),
        @ApiResponse(responseCode = "401", 
                    description = "Не авторизован"),
        @ApiResponse(responseCode = "500", 
                    description = "Ошибка отправки (нет токена или токен не привязан)")
    })
    public ResponseEntity<Void> sendMessage(
            @RequestBody MessageRequest request, 
            @Parameter(hidden = true) Authentication authentication) {
        try {
            sendMessageUseCase.execute(request, authentication);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/messages")
    @Operation(summary = "Получение истории сообщений", 
              description = "Возвращает список всех сообщений, отправленных пользователем")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Список сообщений",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class)))),
        @ApiResponse(responseCode = "401", 
                    description = "Не авторизован")
    })
    public ResponseEntity<List<MessageDto>> getMessages(
            @Parameter(hidden = true) Authentication authentication) {
        List<MessageDto> messages = getMessagesUseCase.execute(authentication);
        return ResponseEntity.ok(messages);
    }
}