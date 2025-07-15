# Telegram Bot REST API

REST API для приема сообщений и отправки их в Telegram бот.

## Технологии
- Java 17
- Spring Boot 3.5.3
- Spring Security с JWT
- PostgreSQL
- Telegram Bot API
- JPA/Hibernate

## Функциональность

1. **Регистрация и авторизация пользователей**
2. **Генерация токена для Telegram бота**
3. **Привязка токена к чату в Telegram**
4. **Отправка сообщений через API в Telegram**
5. **Получение истории сообщений**

## Настройка

### 1. Создание Telegram бота
- Либо тестите с уже созданным тестовым ботом с готовой API
- Либо откройте Telegram и найдите @BotFather
- Создайте нового бота командой `/newbot`
- Сохраните полученный токен

### 2. Конфигурация приложения
Отредактируйте `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/telegram_bot_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# Telegram Bot
telegram.bot.username=your_bot_username
telegram.bot.token=your_bot_token
```

## 🚀 Запуск за 3 шага

### 1. Склонируйте репозиторий
```bash

### 2. Запустите приложение
```bash
docker-compose up -d
```

### 3. Откройте Swagger UI
http://localhost:8080/swagger-ui/index.html

## 📋 Основные команды

```bash
# Запустить все сервисы
docker-compose up -d

# Посмотреть логи
docker-compose logs -f app

# Остановить все
docker-compose down

# Перезапустить приложение
docker-compose restart app
```

## 🏥 Проверка здоровья

```bash
# Статус сервисов
docker-compose ps

# Health check
curl http://localhost:8080/actuator/health
```

## 🛑 Решение проблем

Если что-то не работает:
```bash
# Пересобрать и перезапустить
docker-compose down
docker-compose up -d --build

# Посмотреть логи
docker-compose logs app
docker-compose logs postgres
```


## API Endpoints

### Swagger/OpenAPI документация
После запуска приложения доступна интерактивная документация:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html (или http://localhost:8080/swagger-ui.html - автоматически перенаправит)
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Регистрация
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "password": "password",
  "email": "user@example.com",
  "name": "John Doe"
}
```

### Авторизация
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password"
}
```
Ответ: `{"token": "JWT_TOKEN", "username": "user123"}`

### Генерация токена Telegram
```bash
POST /api/telegram/token/generate
Authorization: Bearer JWT_TOKEN
```
Ответ: `{"token": "UUID_TOKEN"}`

### Привязка токена
1. Найдите вашего бота в Telegram
2. Отправьте ему полученный UUID токен
3. Бот подтвердит привязку

### Отправка сообщения
```bash
POST /api/telegram/message/send
Authorization: Bearer JWT_TOKEN
Content-Type: application/json

{
  "message": "Привет из API!"
}
```

### Получение истории сообщений
```bash
GET /api/telegram/messages
Authorization: Bearer JWT_TOKEN
```

## Схема работы

1. **Регистрация**: Пользователь регистрируется в системе с логином, паролем, email и именем
2. **Подписка на бота**: Пользователь находит бота в Telegram и нажимает Start
3. **Генерация токена**: В личном кабинете пользователь генерирует токен через API
4. **Привязка токена**: Пользователь отправляет токен боту в Telegram
5. **Отправка сообщений**: После привязки можно отправлять сообщения через API

## Формат сообщения в Telegram
```
{Имя пользователя}, я получил от тебя сообщение:
{Сообщение}
```

## Структура проекта

```
src/main/java/com/telergambot/
├── domain/                        # Доменная логика
│   ├── entities/                  # Доменные сущности
│   └── repositories/              # Интерфейсы репозиториев
├── application/                   # Бизнес-логика
│   ├── dto/                       # DTO классы
│   └── usecases/                  # Use cases
├── infrastructure/                # Инфраструктура
│   ├── persistence/               # JPA сущности и репозитории
│   ├── security/                  # JWT и безопасность
│   ├── telegram/                  # Telegram Bot интеграция
│   └── config/                    # Конфигурация
└── presentation/                  # REST контроллеры
    └── controllers/
```

## Тестирование

Запуск всех тестов:
```bash
./mvnw test
```

## Дополнительная документация

- [Настройка Telegram бота](TELEGRAM_BOT_SETUP.md)
- [API документация](API_DOCUMENTATION.md)
- [Swagger/OpenAPI документация](SWAGGER_DOCUMENTATION.md)
- [Безопасность токенов](TELEGRAM_SECURITY.md)# telegram-bot
