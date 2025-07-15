package com.telergambot.presentation.controllers;

import com.telergambot.application.dto.AuthResponse;
import com.telergambot.application.dto.LoginRequest;
import com.telergambot.application.dto.RegisterRequest;
import com.telergambot.application.usecases.LoginUseCase;
import com.telergambot.application.usecases.RegisterUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints для регистрации и авторизации пользователей")
@SecurityRequirements() // No security for auth endpoints
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", 
              description = "Создает нового пользователя с указанными данными")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Успешная регистрация",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Пользователь уже существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = registerUseCase.execute(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя", 
              description = "Аутентификация пользователя и получение JWT токена")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Успешная авторизация",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", 
                    description = "Неверные учетные данные",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = loginUseCase.execute(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}