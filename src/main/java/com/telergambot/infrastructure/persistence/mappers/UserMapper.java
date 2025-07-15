package com.telergambot.infrastructure.persistence.mappers;

import com.telergambot.domain.entities.User;
import com.telergambot.infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .name(entity.getName())
                .role(entity.getRole())
                .enabled(entity.isEnabled())
                .accountNonExpired(entity.isAccountNonExpired())
                .accountNonLocked(entity.isAccountNonLocked())
                .credentialsNonExpired(entity.isCredentialsNonExpired())
                .build();
    }
    
    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setEmail(domain.getEmail());
        entity.setName(domain.getName());
        entity.setRole(domain.getRole());
        entity.setEnabled(domain.isEnabled());
        entity.setAccountNonExpired(domain.isAccountNonExpired());
        entity.setAccountNonLocked(domain.isAccountNonLocked());
        entity.setCredentialsNonExpired(domain.isCredentialsNonExpired());
        
        return entity;
    }
}