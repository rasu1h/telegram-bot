# Безопасность Telegram Bot Integration

## Обзор механизмов безопасности

Система реализует несколько уровней защиты для обеспечения безопасной привязки Telegram аккаунтов к пользователям API.

## 1. Одноразовые токены

### Характеристики:
- **UUID токены**: Генерируются случайным образом, исключая возможность подбора
- **Флаг `isBound`**: Токен может быть использован только один раз для привязки
- **Деактивация старых токенов**: При генерации нового токена все предыдущие токены пользователя деактивируются

### Пример использования:
```java
// Токен генерируется с флагом isBound = false
TelegramToken newToken = TelegramToken.builder()
    .userId(user.getId())
    .token(UUID.randomUUID().toString())
    .isActive(true)
    .isBound(false)  // Токен еще не привязан
    .createdAt(LocalDateTime.now())
    .expiresAt(LocalDateTime.now().plusMinutes(10))
    .build();
```

## 2. Ограничение срока действия (TTL)

### Параметры:
- **Срок действия**: 10 минут с момента генерации
- **Поле `expiresAt`**: Автоматически устанавливается при создании токена
- **Проверка при привязке**: Истекшие токены не могут быть использованы

### Проверка срока действия:
```java
if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
    sendTextMessage(chatId, "Срок действия токена истёк. Пожалуйста, сгенерируйте новый токен.");
    return;
}
```

## 3. Подтверждение привязки

При успешной привязке токена пользователь получает подтверждение с:
- Именем пользователя, к которому привязан токен
- Датой и временем привязки

### Формат подтверждения:
```
✅ Токен успешно привязан!

👤 Привязан к аккаунту: John Doe
📅 Дата привязки: 2025-07-16T02:30:45

Теперь вы будете получать сообщения от вашего API.
```

## 4. Защита от повторного использования

### Проверки безопасности:
1. **Токен уже привязан** (`isBound = true`)
2. **Токен истек** (`expiresAt < now`)
3. **Токен неактивен** (`isActive = false`)

### Код проверки:
```java
// Проверка безопасности
if (token.isBound()) {
    sendTextMessage(chatId, "Этот токен уже был использован для привязки.");
    return;
}

if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
    sendTextMessage(chatId, "Срок действия токена истёк. Пожалуйста, сгенерируйте новый токен.");
    return;
}

if (!token.isActive()) {
    sendTextMessage(chatId, "Этот токен недействителен.");
    return;
}
```

## 5. База данных

### Новые поля в таблице `telegram_tokens`:
```sql
ALTER TABLE telegram_tokens 
ADD COLUMN expires_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '10 minutes'),
ADD COLUMN is_bound BOOLEAN NOT NULL DEFAULT FALSE;
```

## 6. Рекомендации по безопасности

### Для пользователей:
1. **Не делитесь токенами**: Токен должен быть отправлен только в личный чат с ботом
2. **Используйте токен сразу**: У токена ограниченный срок действия (10 минут)
3. **Генерируйте новый токен при необходимости**: Старые токены автоматически деактивируются

### Для администраторов:
1. **Мониторинг**: Отслеживайте попытки использования недействительных токенов
2. **Логирование**: Все попытки привязки логируются с указанием chatId и причины отказа
3. **Настройка TTL**: При необходимости можно изменить срок действия токена в `GenerateTokenUseCase`

## 7. Логирование событий безопасности

### Успешная привязка:
```
INFO: Token {token} successfully bound to chatId {chatId} for user {userName}
```

### Попытки нарушения безопасности:
```
WARN: Attempt to bind already bound token {token} from chatId {chatId}
WARN: Attempt to bind expired token {token} from chatId {chatId}
WARN: Attempt to bind inactive token {token} from chatId {chatId}
```

## 8. API безопасности

### Проверка при отправке сообщений:
```java
if (!token.isBound() || token.getTelegramChatId() == null) {
    throw new RuntimeException("Token not bound to Telegram chat. Please send the token to your Telegram bot.");
}
```

Это гарантирует, что сообщения могут быть отправлены только после успешной привязки токена.