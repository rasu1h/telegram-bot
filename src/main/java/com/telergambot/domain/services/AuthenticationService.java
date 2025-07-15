package com.telergambot.domain.services;

public interface AuthenticationService {
    String generateToken(String username);
    String extractUsername(String token);
    boolean validateToken(String token, String username);
}