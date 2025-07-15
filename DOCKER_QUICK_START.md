# Docker Quick Start

## 🚀 Запуск за 3 шага

### 1. Настройте переменные окружения
```bash
cp .env.example .env
# Отредактируйте .env и добавьте ваши Telegram credentials
```

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

## 🔧 Для разработки

Если нужна только БД:
```bash
docker-compose -f docker-compose.dev.yml up -d
```

Затем запустите приложение локально:
```bash
./mvnw spring-boot:run
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

## 📚 Дополнительно

Подробная документация: [DOCKER_DOCUMENTATION.md](DOCKER_DOCUMENTATION.md)