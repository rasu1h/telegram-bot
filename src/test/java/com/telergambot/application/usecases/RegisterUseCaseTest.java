package com.telergambot.application.usecases;

import com.telergambot.application.dto.AuthResponse;
import com.telergambot.application.dto.RegisterRequest;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.UserRepository;
import com.telergambot.domain.services.AuthenticationService;
import com.telergambot.domain.services.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private RegisterUseCase registerUseCase;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setName("New User");
    }

    @Test
    void execute_ValidRequest_ReturnsAuthResponse() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoderService.encode("password123")).thenReturn("hashedPassword");
        when(authenticationService.generateToken("newuser")).thenReturn("jwt-token");
        
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .password("hashedPassword")
                .email("newuser@example.com")
                .name("New User")
                .role("ROLE_USER")
                .enabled(true)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = registerUseCase.execute(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("newuser", response.getUsername());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertEquals("newuser", capturedUser.getUsername());
        assertEquals("hashedPassword", capturedUser.getPassword());
        assertEquals("newuser@example.com", capturedUser.getEmail());
        assertEquals("ROLE_USER", capturedUser.getRole());
        assertTrue(capturedUser.isEnabled());
        assertTrue(capturedUser.isAccountNonExpired());
        assertTrue(capturedUser.isAccountNonLocked());
        assertTrue(capturedUser.isCredentialsNonExpired());
    }

    @Test
    void execute_UsernameAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registerUseCase.execute(registerRequest));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoderService);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void execute_SaveUserSuccessful_GeneratesToken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoderService.encode("password123")).thenReturn("hashedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .password("hashedPassword")
                .email("newuser@example.com")
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationService.generateToken("newuser")).thenReturn("jwt-token");

        AuthResponse response = registerUseCase.execute(registerRequest);

        assertEquals("jwt-token", response.getToken());
        verify(authenticationService).generateToken("newuser");
    }

    @Test
    void execute_NullUsername_HandledByRepository() {
        registerRequest.setUsername(null);
        
        when(userRepository.existsByUsername(null)).thenReturn(false);
        when(passwordEncoderService.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Username cannot be null"));

        assertThrows(RuntimeException.class, () -> registerUseCase.execute(registerRequest));
    }

    @Test
    void execute_EmptyPassword_EncodesEmptyString() {
        registerRequest.setPassword("");
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoderService.encode("")).thenReturn("hashedEmpty");
        
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .password("hashedEmpty")
                .email("newuser@example.com")
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationService.generateToken("newuser")).thenReturn("jwt-token");

        AuthResponse response = registerUseCase.execute(registerRequest);

        assertNotNull(response);
        verify(passwordEncoderService).encode("");
    }

    @Test
    void execute_PasswordEncoderThrowsException_PropagatesException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoderService.encode("password123")).thenThrow(new RuntimeException("Encoding failed"));

        assertThrows(RuntimeException.class, () -> registerUseCase.execute(registerRequest));
        verify(userRepository, never()).save(any());
    }
}