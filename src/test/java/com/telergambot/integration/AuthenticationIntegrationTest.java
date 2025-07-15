package com.telergambot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telergambot.application.dto.LoginRequest;
import com.telergambot.application.dto.RegisterRequest;
import com.telergambot.infrastructure.persistence.entities.UserEntity;
import com.telergambot.infrastructure.persistence.repositories.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "jwt.expiration=86400000"
})
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCompleteRegistrationFlow() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("integrationuser");
        request.setPassword("testpassword123");
        request.setEmail("integrationuser@example.com");
        request.setName("Integration User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andReturn();

        assertTrue(userRepository.existsByUsername("integrationuser"));
    }

    @Test
    void testCompleteLoginFlow() throws Exception {
        // First create a user
        UserEntity user = new UserEntity();
        user.setUsername("logintest");
        user.setEmail("logintest@example.com");
        user.setName("Login Test");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        userRepository.save(user);

        // Then try to login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("logintest");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username").value("logintest"));
    }

    @Test
    void testAuthenticatedAccessWithToken() throws Exception {
        // Register a new user
        RegisterRequest request = new RegisterRequest();
        request.setUsername("authtest");
        request.setPassword("password123");
        request.setEmail("authtest@example.com");
        request.setName("Auth Test");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("token").asText();

        // Try to access a protected endpoint (any non-auth endpoint)
        mockMvc.perform(get("/api/test")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()); // 404 because endpoint doesn't exist, but not 401
    }

    @Test
    void testAccessWithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAccessWithInvalidToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/test")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterDuplicateUsername_ReturnsBadRequest() throws Exception {
        // Register first user
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicate");
        request.setPassword("password123");
        request.setEmail("duplicate@example.com");
        request.setName("Duplicate User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Try to register with same username
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    void testLoginWithWrongPassword_ReturnsUnauthorized() throws Exception {
        // Create a user
        UserEntity user = new UserEntity();
        user.setUsername("wrongpasstest");
        user.setEmail("wrongpasstest@example.com");
        user.setName("Wrong Pass Test");
        user.setPassword(passwordEncoder.encode("correctpassword"));
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        userRepository.save(user);

        // Try to login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongpasstest");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLoginNonExistentUser_ReturnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testTokenExpiration_SimulatedByInvalidToken() throws Exception {
        // This test simulates an expired token by using an invalid one
        mockMvc.perform(get("/api/test")
                .header("Authorization", "Bearer expired.token.simulation"))
                .andExpect(status().isUnauthorized());
    }
}