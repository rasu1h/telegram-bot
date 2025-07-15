package com.telergambot.application.usecases;

import com.telergambot.application.dto.AuthResponse;
import com.telergambot.application.dto.RegisterRequest;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.UserRepository;
import com.telergambot.domain.services.AuthenticationService;
import com.telergambot.domain.services.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final AuthenticationService authenticationService;
    
    public AuthResponse execute(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoderService.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .role("ROLE_USER")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        User savedUser = userRepository.save(user);
        String token = authenticationService.generateToken(savedUser.getUsername());
        
        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .build();
    }
}