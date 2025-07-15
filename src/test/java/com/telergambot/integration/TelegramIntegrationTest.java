package com.telergambot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telergambot.application.dto.MessageRequest;
import com.telergambot.application.dto.RegisterRequest;
import com.telergambot.infrastructure.persistence.entities.TelegramTokenEntity;
import com.telergambot.infrastructure.persistence.entities.UserEntity;
import com.telergambot.infrastructure.persistence.repositories.JpaTelegramTokenRepository;
import com.telergambot.infrastructure.persistence.repositories.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.telergambot.infrastructure.telegram.TelegramBotService;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "jwt.expiration=86400000",
    "telegram.bot.username=test_bot",
    "telegram.bot.token=test_token",
    "telegram.bot.enabled=false"
})
@Transactional
class TelegramIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaTelegramTokenRepository telegramTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TelegramBotService telegramBotService;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        telegramTokenRepository.deleteAll();

        // Register and login user
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setName("Test User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void testGenerateTelegramToken() throws Exception {
        mockMvc.perform(post("/api/telegram/token/generate")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

        assertTrue(telegramTokenRepository.count() > 0);
    }

    @Test
    void testSendMessage_WithBoundToken() throws Exception {
        // Generate token
        MvcResult tokenResult = mockMvc.perform(post("/api/telegram/token/generate")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String tokenResponse = tokenResult.getResponse().getContentAsString();
        String telegramToken = objectMapper.readTree(tokenResponse).get("token").asText();

        // Simulate token binding
        TelegramTokenEntity tokenEntity = telegramTokenRepository.findByToken(telegramToken).orElseThrow();
        tokenEntity.setTelegramChatId("123456789");
        tokenEntity.setBoundAt(LocalDateTime.now());
        tokenEntity.setBound(true);
        telegramTokenRepository.save(tokenEntity);

        // Send message
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage("Test message");

        doNothing().when(telegramBotService).sendMessageToUser(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/telegram/message/send")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isOk());

        verify(telegramBotService).sendMessageToUser("123456789", "Test User", "Test message");
    }

    @Test
    void testSendMessage_WithoutToken_ReturnsInternalServerError() throws Exception {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage("Test message");

        mockMvc.perform(post("/api/telegram/message/send")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetMessages() throws Exception {
        // First generate token and send a message
        MvcResult tokenResult = mockMvc.perform(post("/api/telegram/token/generate")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String tokenResponse = tokenResult.getResponse().getContentAsString();
        String telegramToken = objectMapper.readTree(tokenResponse).get("token").asText();

        // Bind token
        TelegramTokenEntity tokenEntity = telegramTokenRepository.findByToken(telegramToken).orElseThrow();
        tokenEntity.setTelegramChatId("123456789");
        tokenEntity.setBoundAt(LocalDateTime.now());
        tokenEntity.setBound(true);
        telegramTokenRepository.save(tokenEntity);

        // Send message
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage("Test message for history");

        doNothing().when(telegramBotService).sendMessageToUser(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/telegram/message/send")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isOk());

        // Get messages
        mockMvc.perform(get("/api/telegram/messages")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Test message for history"))
                .andExpect(jsonPath("$[0].deliveredToTelegram").value(true));
    }

    @Test
    void testMultipleTokenGeneration_DeactivatesOldToken() throws Exception {
        // Generate first token
        MvcResult firstTokenResult = mockMvc.perform(post("/api/telegram/token/generate")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String firstToken = objectMapper.readTree(firstTokenResult.getResponse().getContentAsString())
                .get("token").asText();

        // Generate second token
        MvcResult secondTokenResult = mockMvc.perform(post("/api/telegram/token/generate")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String secondToken = objectMapper.readTree(secondTokenResult.getResponse().getContentAsString())
                .get("token").asText();

        // Check that first token is deactivated
        TelegramTokenEntity firstTokenEntity = telegramTokenRepository.findByToken(firstToken).orElseThrow();
        TelegramTokenEntity secondTokenEntity = telegramTokenRepository.findByToken(secondToken).orElseThrow();

        assertTrue(!firstTokenEntity.isActive());
        assertTrue(secondTokenEntity.isActive());
    }
}