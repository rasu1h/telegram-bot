package com.telergambot.infrastructure.persistence.mappers;

import com.telergambot.domain.entities.User;
import com.telergambot.infrastructure.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toDomain_ValidEntity_ReturnsDomainUser() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername("testuser");
        entity.setPassword("hashedpassword");
        entity.setEmail("testuser@example.com");
        entity.setRole("ROLE_USER");
        entity.setEnabled(true);
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);

        User domain = userMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals("testuser", domain.getUsername());
        assertEquals("hashedpassword", domain.getPassword());
        assertEquals("testuser@example.com", domain.getEmail());
        assertEquals("ROLE_USER", domain.getRole());
        assertTrue(domain.isEnabled());
        assertTrue(domain.isAccountNonExpired());
        assertTrue(domain.isAccountNonLocked());
        assertTrue(domain.isCredentialsNonExpired());
    }

    @Test
    void toDomain_NullEntity_ReturnsNull() {
        User domain = userMapper.toDomain(null);
        
        assertNull(domain);
    }

    @Test
    void toDomain_EntityWithNullFields_MapsCorrectly() {
        UserEntity entity = new UserEntity();
        entity.setId(null);
        entity.setUsername(null);
        entity.setPassword(null);
        entity.setEmail(null);
        entity.setRole(null);

        User domain = userMapper.toDomain(entity);

        assertNotNull(domain);
        assertNull(domain.getId());
        assertNull(domain.getUsername());
        assertNull(domain.getPassword());
        assertNull(domain.getEmail());
        assertNull(domain.getRole()); // We explicitly set it to null
        assertTrue(domain.isEnabled()); // UserEntity has default value true
        assertTrue(domain.isAccountNonExpired()); // UserEntity has default value true
        assertTrue(domain.isAccountNonLocked()); // UserEntity has default value true
        assertTrue(domain.isCredentialsNonExpired()); // UserEntity has default value true
    }

    @Test
    void toEntity_ValidDomain_ReturnsEntity() {
        User domain = User.builder()
                .id(2L)
                .username("domainuser")
                .password("domainpassword")
                .email("domainuser@example.com")
                .role("ROLE_ADMIN")
                .enabled(false)
                .accountNonExpired(false)
                .accountNonLocked(false)
                .credentialsNonExpired(false)
                .build();

        UserEntity entity = userMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("domainuser", entity.getUsername());
        assertEquals("domainpassword", entity.getPassword());
        assertEquals("domainuser@example.com", entity.getEmail());
        assertEquals("ROLE_ADMIN", entity.getRole());
        assertFalse(entity.isEnabled());
        assertFalse(entity.isAccountNonExpired());
        assertFalse(entity.isAccountNonLocked());
        assertFalse(entity.isCredentialsNonExpired());
    }

    @Test
    void toEntity_NullDomain_ReturnsNull() {
        UserEntity entity = userMapper.toEntity(null);
        
        assertNull(entity);
    }

    @Test
    void toEntity_DomainWithNullFields_MapsCorrectly() {
        User domain = new User();
        domain.setId(null);
        domain.setUsername(null);
        domain.setPassword(null);
        domain.setEmail(null);
        domain.setRole(null);

        UserEntity entity = userMapper.toEntity(domain);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getUsername());
        assertNull(entity.getPassword());
        assertNull(entity.getEmail());
        assertNull(entity.getRole());
        assertFalse(entity.isEnabled());
        assertFalse(entity.isAccountNonExpired());
        assertFalse(entity.isAccountNonLocked());
        assertFalse(entity.isCredentialsNonExpired());
    }

    @Test
    void bidirectionalMapping_MapsCorrectly() {
        User originalDomain = User.builder()
                .id(3L)
                .username("bidirectional")
                .password("password123")
                .email("bidirectional@example.com")
                .role("ROLE_USER")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        UserEntity entity = userMapper.toEntity(originalDomain);
        User mappedBackDomain = userMapper.toDomain(entity);

        assertEquals(originalDomain.getId(), mappedBackDomain.getId());
        assertEquals(originalDomain.getUsername(), mappedBackDomain.getUsername());
        assertEquals(originalDomain.getPassword(), mappedBackDomain.getPassword());
        assertEquals(originalDomain.getEmail(), mappedBackDomain.getEmail());
        assertEquals(originalDomain.getRole(), mappedBackDomain.getRole());
        assertEquals(originalDomain.isEnabled(), mappedBackDomain.isEnabled());
        assertEquals(originalDomain.isAccountNonExpired(), mappedBackDomain.isAccountNonExpired());
        assertEquals(originalDomain.isAccountNonLocked(), mappedBackDomain.isAccountNonLocked());
        assertEquals(originalDomain.isCredentialsNonExpired(), mappedBackDomain.isCredentialsNonExpired());
    }

    @Test
    void toEntity_NewUserWithoutId_MapsCorrectly() {
        User domain = User.builder()
                .username("newuser")
                .password("newpassword")
                .email("newuser@example.com")
                .role("ROLE_USER")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        UserEntity entity = userMapper.toEntity(domain);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("newuser", entity.getUsername());
        assertEquals("newpassword", entity.getPassword());
        assertEquals("newuser@example.com", entity.getEmail());
        assertEquals("ROLE_USER", entity.getRole());
        assertTrue(entity.isEnabled());
    }
}