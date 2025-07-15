package com.telergambot.domain.repositories;

import com.telergambot.domain.entities.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User save(User user);
    Optional<User> findById(Long id);
}