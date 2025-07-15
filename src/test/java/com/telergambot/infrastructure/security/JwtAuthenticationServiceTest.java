package com.telergambot.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationServiceTest {

    @InjectMocks
    private JwtAuthenticationService jwtAuthenticationService;

    @BeforeEach
    void setUp() {
        // Set the required fields using reflection since @Value won't work in tests
        ReflectionTestUtils.setField(jwtAuthenticationService, "secret", 
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtAuthenticationService, "expiration", 86400000L);
    }

    @Test
    void generateToken_ValidUsername_ReturnsToken() {
        String username = "testuser";
        
        String token = jwtAuthenticationService.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        String username = "testuser";
        String token = jwtAuthenticationService.generateToken(username);
        
        String extractedUsername = jwtAuthenticationService.extractUsername(token);
        
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_ValidTokenAndMatchingUsername_ReturnsTrue() {
        String username = "testuser";
        String token = jwtAuthenticationService.generateToken(username);
        
        boolean isValid = jwtAuthenticationService.validateToken(token, username);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_ValidTokenButDifferentUsername_ReturnsFalse() {
        String username = "testuser";
        String token = jwtAuthenticationService.generateToken(username);
        
        boolean isValid = jwtAuthenticationService.validateToken(token, "differentuser");
        
        assertFalse(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.jwt.token";
        
        boolean isValid = jwtAuthenticationService.validateToken(invalidToken, "testuser");
        
        assertFalse(isValid);
    }

    @Test
    void generateToken_NullUsername_GeneratesTokenWithNullSubject() {
        String token = jwtAuthenticationService.generateToken(null);
        
        assertNotNull(token);
        assertNull(jwtAuthenticationService.extractUsername(token));
    }

    @Test
    void generateToken_EmptyUsername_GeneratesTokenWithEmptySubject() {
        String token = jwtAuthenticationService.generateToken("");
        
        assertNotNull(token);
        assertEquals("", jwtAuthenticationService.extractUsername(token));
    }

    @Test
    void extractUsername_MalformedToken_ReturnsNull() {
        String malformedToken = "not-a-jwt";
        
        String username = jwtAuthenticationService.extractUsername(malformedToken);
        
        assertNull(username);
    }

    @Test
    void generateAndValidateToken_MultipleUsers_WorksCorrectly() {
        String user1 = "user1";
        String user2 = "user2";
        
        String token1 = jwtAuthenticationService.generateToken(user1);
        String token2 = jwtAuthenticationService.generateToken(user2);
        
        assertTrue(jwtAuthenticationService.validateToken(token1, user1));
        assertTrue(jwtAuthenticationService.validateToken(token2, user2));
        assertFalse(jwtAuthenticationService.validateToken(token1, user2));
        assertFalse(jwtAuthenticationService.validateToken(token2, user1));
    }
}