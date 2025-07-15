package com.telergambot.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telergambot.application.dto.AuthResponse;
import com.telergambot.application.dto.LoginRequest;
import com.telergambot.application.dto.RegisterRequest;
import com.telergambot.application.usecases.LoginUseCase;
import com.telergambot.application.usecases.RegisterUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private RegisterUseCase registerUseCase;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ValidRequest_ReturnsAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("newuser@example.com");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .username("newuser")
                .build();

        when(registerUseCase.execute(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(registerUseCase).execute(any(RegisterRequest.class));
    }

    @Test
    void register_UsernameAlreadyExists_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");
        request.setEmail("existinguser@example.com");

        when(registerUseCase.execute(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));

        verify(registerUseCase).execute(any(RegisterRequest.class));
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .username("testuser")
                .build();

        when(loginUseCase.execute(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(loginUseCase).execute(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(loginUseCase.execute(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));

        verify(loginUseCase).execute(any(LoginRequest.class));
    }

    @Test
    void register_EmptyRequest_HandledByUseCase() throws Exception {
        RegisterRequest request = new RegisterRequest();

        when(registerUseCase.execute(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username is required"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is required"));
    }

    @Test
    void login_EmptyRequest_HandledByUseCase() throws Exception {
        LoginRequest request = new LoginRequest();

        when(loginUseCase.execute(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Username is required"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Username is required"));
    }

    @Test
    void register_InvalidJsonRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registerUseCase);
    }

    @Test
    void login_InvalidJsonRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(loginUseCase);
    }

    @Test
    void register_NoContentType_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(registerUseCase);
    }

    @Test
    void login_NoContentType_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(loginUseCase);
    }
}