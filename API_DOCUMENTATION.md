# API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Все защищенные endpoints требуют JWT токен в заголовке:
```
Authorization: Bearer <JWT_TOKEN>
```

## Endpoints

### 1. Регистрация пользователя
**POST** `/auth/register`

**Request Body:**
```json
{
    "username": "string",
    "password": "string", 
    "email": "string",
    "name": "string"
}
```

**Response:**
```json
{
    "token": "JWT_TOKEN",
    "username": "string"
}
```

**Status Codes:**
- 200 OK - Успешная регистрация
- 400 Bad Request - Пользователь уже существует

### 2. Авторизация
**POST** `/auth/login`

**Request Body:**
```json
{
    "username": "string",
    "password": "string"
}
```

**Response:**
```json
{
    "token": "JWT_TOKEN",
    "username": "string"
}
```

**Status Codes:**
- 200 OK - Успешная авторизация
- 401 Unauthorized - Неверные учетные данные

### 3. Генерация Telegram токена
**POST** `/telegram/token/generate`

**Headers:**
- Authorization: Bearer JWT_TOKEN

**Response:**
```json
{
    "token": "UUID_TOKEN"
}
```

**Особенности безопасности:**
- Токен действителен только 10 минут
- Токен может быть использован только один раз
- При генерации нового токена все старые токены деактивируются
- Токен должен быть отправлен боту в Telegram для привязки

**Status Codes:**
- 200 OK - Токен успешно сгенерирован
- 401 Unauthorized - Не авторизован

### 4. Отправка сообщения
**POST** `/telegram/message/send`

**Headers:**
- Authorization: Bearer JWT_TOKEN
- Content-Type: application/json

**Request Body:**
```json
{
    "message": "string"
}
```

**Response:**
- 200 OK - Сообщение отправлено
- 500 Internal Server Error - Ошибка отправки (нет токена или токен не привязан)

### 5. Получение истории сообщений
**GET** `/telegram/messages`

**Headers:**
- Authorization: Bearer JWT_TOKEN

**Response:**
```json
[
    {
        "id": 1,
        "content": "string",
        "sentAt": "2024-01-01T12:00:00",
        "deliveredToTelegram": true,
        "deliveredAt": "2024-01-01T12:00:01"
    }
]
```

**Status Codes:**
- 200 OK - Список сообщений
- 401 Unauthorized - Не авторизован

## Процесс использования

1. **Регистрация**: Создайте нового пользователя через `/auth/register`
2. **Авторизация**: Получите JWT токен через `/auth/login`
3. **Генерация токена**: Создайте токен для Telegram через `/telegram/token/generate`
4. **Привязка в Telegram**: 
   - Найдите бота в Telegram
   - Отправьте ему полученный токен
   - Бот подтвердит привязку
5. **Отправка сообщений**: Используйте `/telegram/message/send` для отправки сообщений
6. **История**: Получайте историю сообщений через `/telegram/messages`

## Ошибки

### Формат ошибок
```json
{
    "error": "Описание ошибки"
}
```

### Типичные ошибки
- `Username already exists` - При попытке регистрации с существующим username
- `Invalid credentials` - Неверный логин или пароль
- `No active token found` - Нет активного токена для пользователя
- `Token not bound to Telegram chat` - Токен не привязан к чату в Telegram

### Ошибки безопасности токенов
При попытке использовать токен в Telegram боте:
- `Этот токен уже был использован для привязки` - Токен уже привязан к другому чату
- `Срок действия токена истёк. Пожалуйста, сгенерируйте новый токен` - Прошло более 10 минут с момента генерации
- `Этот токен недействителен` - Токен был деактивирован

## Безопасность

Подробную информацию о механизмах безопасности см. в [TELEGRAM_SECURITY.md](TELEGRAM_SECURITY.md)