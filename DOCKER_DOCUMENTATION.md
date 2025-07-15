# Docker Documentation

## Обзор

Проект поддерживает запуск через Docker и Docker Compose, что обеспечивает:
- Изолированное окружение
- Простое развертывание
- Масштабируемость
- Единообразие между dev/prod окружениями

## Структура Docker файлов

```
.
├── Dockerfile              # Multi-stage build для приложения
├── docker-compose.yml      # Полный стек для production
├── docker-compose.dev.yml  # Только БД для разработки
├── .env.example           # Пример переменных окружения
├── nginx.conf             # Конфигурация Nginx (опционально)
└── init-db.sql            # SQL скрипт инициализации БД
```

## Быстрый старт

### 1. Для разработки (только БД)

```bash
# Запустить PostgreSQL для локальной разработки
docker-compose -f docker-compose.dev.yml up -d

# Проверить статус
docker-compose -f docker-compose.dev.yml ps

# Остановить
docker-compose -f docker-compose.dev.yml down
```

### 2. Полный стек (Production-like)

```bash
# Скопировать и настроить переменные окружения
cp .env.example .env
# Отредактируйте .env файл, добавив ваши Telegram Bot credentials

# Запустить все сервисы
docker-compose up -d

# Или с пересборкой образа
docker-compose up -d --build

# Проверить логи
docker-compose logs -f app

# Остановить все сервисы
docker-compose down

# Остановить и удалить volumes
docker-compose down -v
```

## Конфигурация

### Переменные окружения

Создайте файл `.env` на основе `.env.example`:

```env
# Database
POSTGRES_DB=telegram_bot_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_very_long_secret_key_here
JWT_EXPIRATION=86400000

# Telegram Bot
TELEGRAM_BOT_USERNAME=your_bot_username
TELEGRAM_BOT_TOKEN=your_bot_token_from_botfather

# Application
SPRING_PROFILES_ACTIVE=docker
```

### Профили Spring

- `default` - для локальной разработки
- `docker` - для запуска в контейнере
- `prod` - для production (можно создать отдельный)

## Сервисы

### 1. PostgreSQL
- **Образ**: postgres:15-alpine
- **Порт**: 5432
- **Volume**: postgres_data
- **Healthcheck**: встроенный

### 2. Spring Boot Application
- **Build**: Multi-stage с Maven
- **Порт**: 8080
- **Зависимости**: PostgreSQL
- **Healthcheck**: через Spring Actuator

### 3. Nginx (опционально)
- **Образ**: nginx:alpine
- **Порт**: 80
- **Профиль**: with-nginx
- **Функции**: Reverse proxy, security headers

## Команды Docker

### Основные команды

```bash
# Запуск в фоне
docker-compose up -d

# Запуск с логами
docker-compose up

# Пересборка и запуск
docker-compose up --build

# Остановка
docker-compose down

# Остановка с удалением volumes
docker-compose down -v

# Просмотр логов
docker-compose logs -f [service_name]

# Выполнить команду в контейнере
docker-compose exec app sh
docker-compose exec postgres psql -U postgres -d telegram_bot_db
```

### Работа с отдельными сервисами

```bash
# Запустить только БД
docker-compose up -d postgres

# Перезапустить приложение
docker-compose restart app

# Пересобрать только приложение
docker-compose build app
```

### Запуск с Nginx

```bash
# Запустить с Nginx reverse proxy
docker-compose --profile with-nginx up -d
```

## Отладка

### Просмотр логов

```bash
# Все логи
docker-compose logs

# Логи конкретного сервиса
docker-compose logs app
docker-compose logs postgres

# Следить за логами в реальном времени
docker-compose logs -f app
```

### Подключение к контейнерам

```bash
# Зайти в контейнер приложения
docker-compose exec app sh

# Подключиться к PostgreSQL
docker-compose exec postgres psql -U postgres -d telegram_bot_db

# Выполнить SQL запрос
docker-compose exec postgres psql -U postgres -d telegram_bot_db -c "SELECT * FROM users;"
```

### Проверка состояния

```bash
# Статус контейнеров
docker-compose ps

# Использование ресурсов
docker stats

# Проверка сети
docker network ls
docker network inspect telergam-bot_telegram-bot-network
```

## Volumes и данные

### Управление volumes

```bash
# Список volumes
docker volume ls

# Информация о volume
docker volume inspect telergam-bot_postgres_data

# Backup данных
docker-compose exec postgres pg_dump -U postgres telegram_bot_db > backup.sql

# Restore данных
docker-compose exec -T postgres psql -U postgres telegram_bot_db < backup.sql
```

### Очистка

```bash
# Удалить неиспользуемые образы
docker image prune

# Удалить все остановленные контейнеры
docker container prune

# Полная очистка (осторожно!)
docker system prune -a --volumes
```

## Production рекомендации

### 1. Безопасность

- Используйте сильные пароли в `.env`
- Не коммитьте `.env` файл в git
- Используйте Docker secrets для sensitive данных
- Ограничьте доступ к портам

### 2. Оптимизация

```dockerfile
# Добавьте в Dockerfile для уменьшения размера
RUN apk add --no-cache tini
ENTRYPOINT ["/sbin/tini", "--"]
CMD ["java", "-jar", "app.jar"]
```

### 3. Мониторинг

Добавьте в docker-compose.yml:

```yaml
  prometheus:
    image: prom/prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
```

### 4. Логирование

```yaml
  app:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## Масштабирование

### Горизонтальное масштабирование

```bash
# Запустить 3 экземпляра приложения
docker-compose up -d --scale app=3
```

### С балансировщиком нагрузки

Обновите nginx.conf:

```nginx
upstream backend {
    least_conn;
    server app:8080 max_fails=3 fail_timeout=30s;
    server app2:8080 max_fails=3 fail_timeout=30s;
    server app3:8080 max_fails=3 fail_timeout=30s;
}
```

## CI/CD интеграция

### GitHub Actions пример

```yaml
name: Docker Build and Push

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Build and push Docker image
      env:
        DOCKER_REGISTRY: your-registry.com
      run: |
        docker build -t $DOCKER_REGISTRY/telegram-bot:latest .
        docker push $DOCKER_REGISTRY/telegram-bot:latest
```

## Troubleshooting

### Проблема: Приложение не может подключиться к БД

```bash
# Проверьте, что БД запущена и healthy
docker-compose ps
docker-compose logs postgres

# Проверьте сетевое подключение
docker-compose exec app ping postgres
```

### Проблема: Out of memory

```yaml
# Добавьте лимиты в docker-compose.yml
services:
  app:
    mem_limit: 512m
    memswap_limit: 1g
```

### Проблема: Порт уже занят

```bash
# Найти процесс
sudo lsof -i :8080

# Или измените порт в docker-compose.yml
ports:
  - "8081:8080"
```

## Полезные скрипты

### start.sh
```bash
#!/bin/bash
docker-compose down
docker-compose up -d --build
docker-compose logs -f app
```

### backup.sh
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker-compose exec postgres pg_dump -U postgres telegram_bot_db > backup_$DATE.sql
echo "Backup created: backup_$DATE.sql"
```

### health-check.sh
```bash
#!/bin/bash
curl -f http://localhost:8080/actuator/health || exit 1
```