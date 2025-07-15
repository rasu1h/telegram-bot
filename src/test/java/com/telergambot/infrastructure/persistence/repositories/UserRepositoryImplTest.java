package com.telergambot.infrastructure.persistence.repositories;

import com.telergambot.domain.entities.User;
import com.telergambot.infrastructure.persistence.entities.UserEntity;
import com.telergambot.infrastructure.persistence.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .email("testuser@example.com")
                .role("ROLE_USER")
                .enabled(true)
                .build();

        testUserEntity = new UserEntity();
        testUserEntity.setId(1L);
        testUserEntity.setUsername("testuser");
        testUserEntity.setPassword("password");
        testUserEntity.setEmail("testuser@example.com");
        testUserEntity.setRole("ROLE_USER");
        testUserEntity.setEnabled(true);
    }

    @Test
    void findByUsername_UserExists_ReturnsUser() {
        when(jpaUserRepository.findByUsername("testuser")).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDomain(testUserEntity)).thenReturn(testUser);

        Optional<User> result = userRepository.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(jpaUserRepository).findByUsername("testuser");
        verify(userMapper).toDomain(testUserEntity);
    }

    @Test
    void findByUsername_UserNotExists_ReturnsEmpty() {
        when(jpaUserRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByUsername("nonexistent");

        assertFalse(result.isPresent());
        verify(jpaUserRepository).findByUsername("nonexistent");
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void existsByUsername_UserExists_ReturnsTrue() {
        when(jpaUserRepository.existsByUsername("testuser")).thenReturn(true);

        boolean exists = userRepository.existsByUsername("testuser");

        assertTrue(exists);
        verify(jpaUserRepository).existsByUsername("testuser");
    }

    @Test
    void existsByUsername_UserNotExists_ReturnsFalse() {
        when(jpaUserRepository.existsByUsername("nonexistent")).thenReturn(false);

        boolean exists = userRepository.existsByUsername("nonexistent");

        assertFalse(exists);
        verify(jpaUserRepository).existsByUsername("nonexistent");
    }

    @Test
    void save_ValidUser_ReturnsSavedUser() {
        User userToSave = User.builder()
                .username("newuser")
                .password("password")
                .email("newuser@example.com")
                .role("ROLE_USER")
                .build();
        
        UserEntity entityToSave = new UserEntity();
        entityToSave.setUsername("newuser");
        entityToSave.setPassword("password");
        entityToSave.setEmail("newuser@example.com");
        entityToSave.setRole("ROLE_USER");
        
        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(2L);
        savedEntity.setUsername("newuser");
        savedEntity.setPassword("password");
        savedEntity.setEmail("newuser@example.com");
        savedEntity.setRole("ROLE_USER");
        
        User savedUser = User.builder()
                .id(2L)
                .username("newuser")
                .password("password")
                .email("newuser@example.com")
                .role("ROLE_USER")
                .build();

        when(userMapper.toEntity(userToSave)).thenReturn(entityToSave);
        when(jpaUserRepository.save(entityToSave)).thenReturn(savedEntity);
        when(userMapper.toDomain(savedEntity)).thenReturn(savedUser);

        User result = userRepository.save(userToSave);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("newuser", result.getUsername());
        verify(userMapper).toEntity(userToSave);
        verify(jpaUserRepository).save(entityToSave);
        verify(userMapper).toDomain(savedEntity);
    }

    @Test
    void save_NullUser_HandledByMapper() {
        when(userMapper.toEntity(null)).thenReturn(null);
        when(jpaUserRepository.save(null)).thenThrow(new IllegalArgumentException("Entity cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> userRepository.save(null));
        
        verify(userMapper).toEntity(null);
        verify(jpaUserRepository).save(null);
    }

    @Test
    void findById_UserExists_ReturnsUser() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDomain(testUserEntity)).thenReturn(testUser);

        Optional<User> result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(jpaUserRepository).findById(1L);
        verify(userMapper).toDomain(testUserEntity);
    }

    @Test
    void findById_UserNotExists_ReturnsEmpty() {
        when(jpaUserRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findById(999L);

        assertFalse(result.isPresent());
        verify(jpaUserRepository).findById(999L);
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void findByUsername_NullUsername_HandledByJpaRepository() {
        when(jpaUserRepository.findByUsername(null)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByUsername(null);

        assertFalse(result.isPresent());
        verify(jpaUserRepository).findByUsername(null);
    }

    @Test
    void existsByUsername_NullUsername_HandledByJpaRepository() {
        when(jpaUserRepository.existsByUsername(null)).thenReturn(false);

        boolean exists = userRepository.existsByUsername(null);

        assertFalse(exists);
        verify(jpaUserRepository).existsByUsername(null);
    }
}