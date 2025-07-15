package com.telergambot.infrastructure.telegram;

import com.telergambot.domain.entities.TelegramToken;
import com.telergambot.domain.entities.User;
import com.telergambot.domain.repositories.TelegramTokenRepository;
import com.telergambot.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {
    
    private final TelegramBotConfig botConfig;
    private final TelegramTokenRepository telegramTokenRepository;
    private final UserRepository userRepository;
    
    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }
    
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            Long userId = update.getMessage().getFrom().getId();
            
            log.info("Received message from chatId: {}, userId: {}, text: {}", chatId, userId, messageText);
            
            // связка с чатом Telegram
            Optional<TelegramToken> tokenOpt = telegramTokenRepository.findByToken(messageText);
            if (tokenOpt.isPresent()) {
                TelegramToken token = tokenOpt.get();
                
                // проверка
                if (token.isBound()) {
                    sendTextMessage(chatId, "Этот токен уже был использован для привязки.");
                    log.warn("Attempt to bind already bound token {} from chatId {}", messageText, chatId);
                    return;
                }
                
                if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
                    sendTextMessage(chatId, "Срок действия токена истёк. Пожалуйста, сгенерируйте новый токен.");
                    log.warn("Attempt to bind expired token {} from chatId {}", messageText, chatId);
                    return;
                }
                
                if (!token.isActive()) {
                    sendTextMessage(chatId, "Этот токен недействителен.");
                    log.warn("Attempt to bind inactive token {} from chatId {}", messageText, chatId);
                    return;
                }
                
                // информация о юзера
                Optional<User> userOpt = userRepository.findById(token.getUserId());
                String userName = userOpt.map(User::getName).orElse("Пользователь");
                
                // привязка токена
                token.setTelegramChatId(chatId);
                token.setBoundAt(LocalDateTime.now());
                token.setBound(true);
                telegramTokenRepository.save(token);
                
                String confirmMessage = String.format(
                    "✅ Токен успешно привязан!\n\n" +
                    "👤 Привязан к аккаунту: %s\n" +
                    "📅 Дата привязки: %s\n\n" +
                    "Теперь вы будете получать сообщения от вашего API.",
                    userName,
                    LocalDateTime.now().toString()
                );
                
                sendTextMessage(chatId, confirmMessage);
                log.info("Token {} successfully bound to chatId {} for user {}", messageText, chatId, userName);
            } else {
                // токен не найден, проверяем привязку чата
                Optional<TelegramToken> existingToken = telegramTokenRepository.findByTelegramChatId(chatId);
                if (existingToken.isPresent()) {
                    sendTextMessage(chatId, "Ваш чат уже привязан к токену. Вы будете получать сообщения от вашего API.");
                } else {
                    sendTextMessage(chatId, "Пожалуйста, отправьте токен для привязки к вашему аккаунту.");
                }
            }
        }
    }
    
    public void sendMessageToUser(String chatId, String userName, String message) {
        String formattedMessage = String.format("%s, я получил от тебя сообщение:\n%s", userName, message);
        sendTextMessage(chatId, formattedMessage);
    }
    
    void sendTextMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        
        try {
            execute(message);
            log.info("Message sent to chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chatId: {}", chatId, e);
        }
    }
}