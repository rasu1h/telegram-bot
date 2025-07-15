package com.telergambot.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordEncoderServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BCryptPasswordEncoderService passwordEncoderService;

    @Test
    void encode_ValidPassword_ReturnsEncodedPassword() {
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encoded.password.hash";
        
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        
        String result = passwordEncoderService.encode(rawPassword);
        
        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void encode_EmptyPassword_ReturnsEncodedEmptyString() {
        String emptyPassword = "";
        String encodedEmpty = "$2a$10$encoded.empty.hash";
        
        when(passwordEncoder.encode(emptyPassword)).thenReturn(encodedEmpty);
        
        String result = passwordEncoderService.encode(emptyPassword);
        
        assertEquals(encodedEmpty, result);
        verify(passwordEncoder).encode(emptyPassword);
    }

    @Test
    void encode_NullPassword_HandledByEncoder() {
        when(passwordEncoder.encode(null)).thenThrow(new IllegalArgumentException("Password cannot be null"));
        
        assertThrows(IllegalArgumentException.class, 
            () -> passwordEncoderService.encode(null));
        
        verify(passwordEncoder).encode(null);
    }

    @Test
    void matches_CorrectPassword_ReturnsTrue() {
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encoded.password.hash";
        
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        
        boolean result = passwordEncoderService.matches(rawPassword, encodedPassword);
        
        assertTrue(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void matches_IncorrectPassword_ReturnsFalse() {
        String rawPassword = "wrongpassword";
        String encodedPassword = "$2a$10$encoded.password.hash";
        
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);
        
        boolean result = passwordEncoderService.matches(rawPassword, encodedPassword);
        
        assertFalse(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void matches_EmptyRawPasswordAgainstEncodedPassword_ReturnsFalse() {
        String emptyPassword = "";
        String encodedPassword = "$2a$10$encoded.password.hash";
        
        when(passwordEncoder.matches(emptyPassword, encodedPassword)).thenReturn(false);
        
        boolean result = passwordEncoderService.matches(emptyPassword, encodedPassword);
        
        assertFalse(result);
        verify(passwordEncoder).matches(emptyPassword, encodedPassword);
    }

    @Test
    void matches_NullPasswords_HandledByEncoder() {
        when(passwordEncoder.matches(null, null)).thenThrow(new IllegalArgumentException("Arguments cannot be null"));
        
        assertThrows(IllegalArgumentException.class, 
            () -> passwordEncoderService.matches(null, null));
        
        verify(passwordEncoder).matches(null, null);
    }

    @Test
    void encode_VeryLongPassword_EncodesSuccessfully() {
        String longPassword = "a".repeat(100);
        String encodedLong = "$2a$10$encoded.long.password.hash";
        
        when(passwordEncoder.encode(longPassword)).thenReturn(encodedLong);
        
        String result = passwordEncoderService.encode(longPassword);
        
        assertEquals(encodedLong, result);
        verify(passwordEncoder).encode(longPassword);
    }

    @Test
    void matches_SpecialCharactersInPassword_WorksCorrectly() {
        String specialPassword = "p@$$w0rd!#%&*()";
        String encodedSpecial = "$2a$10$encoded.special.hash";
        
        when(passwordEncoder.matches(specialPassword, encodedSpecial)).thenReturn(true);
        
        boolean result = passwordEncoderService.matches(specialPassword, encodedSpecial);
        
        assertTrue(result);
        verify(passwordEncoder).matches(specialPassword, encodedSpecial);
    }
}