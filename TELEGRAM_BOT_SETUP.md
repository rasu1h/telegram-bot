# Настройка Telegram Bot API

## Создание бота в Telegram

1. Откройте Telegram и найдите @BotFather
2. Отправьте команду `/newbot`
3. Следуйте инструкциям:
   - Введите имя бота (например: "My API Message Bot")
   - Введите username бота (должен заканчиваться на "bot", например: "myapi_message_bot")
4. BotFather выдаст вам токен вида: `1234567890:ABCdefGHIjklMNOpqrsTUVwxyz`

## Настройка приложения

1. Откройте файл `src/main/resources/application.properties`
2. Замените значения:
   ```properties
   telegram.bot.username=myapi_message_bot
   telegram.bot.token=1234567890:ABCdefGHIjklMNOpqrsTUVwxyz
   ```

## Использование API

### 1. Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "name": "Test User"
  }'
```

### 2. Авторизация
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```
Ответ: `{"token": "JWT_TOKEN", "username": "testuser"}`

### 3. Генерация токена для Telegram
```bash
curl -X POST http://localhost:8080/api/telegram/token/generate \
  -H "Authorization: Bearer JWT_TOKEN"
```
Ответ: `{"token": "UUID_TOKEN"}`

### 4. Привязка токена в Telegram
1. Найдите вашего бота в Telegram
2. Нажмите Start
3. Отправьте боту токен, полученный на предыдущем шаге
4. Бот ответит: "Токен успешно привязан!"

### 5. Отправка сообщения через API
```bash
curl -X POST http://localhost:8080/api/telegram/message/send \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Привет из API!"
  }'
```

### 6. Получение истории сообщений
```bash
curl -X GET http://localhost:8080/api/telegram/messages \
  -H "Authorization: Bearer JWT_TOKEN"
```

## Структура базы данных

Приложение автоматически создаст следующие таблицы:
- `users` - пользователи системы
- `telegram_tokens` - токены для привязки к Telegram
- `messages` - история сообщений

## Примечания

- Каждый пользователь может иметь только один активный токен
- При генерации нового токена старый деактивируется
- Сообщения сохраняются с отметкой времени отправки и доставки