package com.telergambot.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTestUtils {

    /**
     * Создает валидный JWT токен для тестов
     */
    public static String createValidToken(String username, String secret, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Создает истекший JWT токен
     */
    public static String createExpiredToken(String username, String secret) {
        Map<String, Object> claims = new HashMap<>();
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Создает токен с неправильной подписью
     */
    public static String createTokenWithWrongSignature(String username, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        // Используем другой секрет для подписи
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("dGhpc2lzYXZlcnlsb25nc2VjcmV0a2V5Zm9ydGVzdGluZ3B1cnBvc2Vz"));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Создает различные типы невалидных токенов для тестирования
     */
    public static class InvalidTokens {
        public static final String MALFORMED = "это.невалидный.токен";
        public static final String RANDOM_STRING = "randomstring123";
        public static final String INCOMPLETE = "eyJhbGciOiJIUzI1NiJ9"; // Только header
        public static final String SPECIAL_CHARS = "�{ږ'"; // Специальные символы
    }
}