package com.telergambot.infrastructure.persistence.repositories;

import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.UserRepository;
import com.telergambot.infrastructure.persistence.entities.UserEntity;
import com.telergambot.infrastructure.persistence.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;
    
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    
    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        return userMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(userMapper::toDomain);
    }
}