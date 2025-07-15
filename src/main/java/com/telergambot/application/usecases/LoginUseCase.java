package com.telergambot.application.usecases;

import com.telergambot.application.dto.AuthResponse;
import com.telergambot.application.dto.LoginRequest;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.UserRepository;
import com.telergambot.domain.services.AuthenticationService;
import com.telergambot.domain.services.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final AuthenticationService authenticationService;
    
    public AuthResponse execute(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoderService.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = authenticationService.generateToken(user.getUsername());
        
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }
}