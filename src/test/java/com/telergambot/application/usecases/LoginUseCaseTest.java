package com.telergambot.application.usecases;

import com.telergambot.application.dto.AuthResponse;
import com.telergambot.application.dto.LoginRequest;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.UserRepository;
import com.telergambot.domain.services.AuthenticationService;
import com.telergambot.domain.services.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .role("ROLE_USER")
                .enabled(true)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("rawPassword");
    }

    @Test
    void execute_ValidCredentials_ReturnsAuthResponse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoderService.matches("rawPassword", "hashedPassword")).thenReturn(true);
        when(authenticationService.generateToken("testuser")).thenReturn("jwt-token");

        AuthResponse response = loginUseCase.execute(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoderService).matches("rawPassword", "hashedPassword");
        verify(authenticationService).generateToken("testuser");
    }

    @Test
    void execute_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> loginUseCase.execute(loginRequest));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verifyNoInteractions(passwordEncoderService);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void execute_InvalidPassword_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoderService.matches("rawPassword", "hashedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> loginUseCase.execute(loginRequest));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoderService).matches("rawPassword", "hashedPassword");
        verifyNoInteractions(authenticationService);
    }

    @Test
    void execute_NullUsername_ThrowsException() {
        loginRequest.setUsername(null);
        
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> loginUseCase.execute(loginRequest));
    }

    @Test
    void execute_EmptyPassword_ValidatesAgainstStoredPassword() {
        loginRequest.setPassword("");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoderService.matches("", "hashedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> loginUseCase.execute(loginRequest));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(passwordEncoderService).matches("", "hashedPassword");
    }
}