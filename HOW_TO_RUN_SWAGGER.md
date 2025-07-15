# Как запустить и проверить Swagger UI

## 1. Запуск приложения

```bash
./mvnw spring-boot:run
```

## 2. Доступ к Swagger UI

После успешного запуска приложения, откройте в браузере один из следующих URL:

- **Основной URL**: http://localhost:8080/swagger-ui/index.html
- **Альтернативный URL**: http://localhost:8080/swagger-ui.html

## 3. Проверка доступности API документации

Вы также можете проверить доступность OpenAPI спецификации:

```bash
# Получить OpenAPI документацию в JSON формате
curl http://localhost:8080/api-docs

# Проверить статус
curl -I http://localhost:8080/api-docs
```

## 4. Если получаете ошибку 401 Unauthorized

Это означает, что Spring Security блокирует доступ к Swagger. Убедитесь, что:

1. В файле `SecurityConfig.java` добавлены правильные исключения:
```java
.requestMatchers(
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/v3/api-docs/**",
    "/api-docs/**",
    "/swagger-resources/**",
    "/webjars/**"
).permitAll()
```

2. Перезапустите приложение после изменений

## 5. Работа с Swagger UI

1. **Просмотр endpoints**: Все доступные API endpoints сгруппированы по контроллерам
2. **Тестирование без авторизации**: Endpoints в группе "Authentication" доступны без токена
3. **Тестирование с авторизацией**:
   - Сначала выполните регистрацию или вход
   - Скопируйте полученный JWT токен
   - Нажмите кнопку "Authorize" (иконка замка)
   - Введите: `Bearer YOUR_TOKEN`
   - Теперь можно тестировать защищенные endpoints

## 6. Альтернативные способы доступа

Если браузер не открывается автоматически, можно использовать:

```bash
# Linux/Mac
xdg-open http://localhost:8080/swagger-ui/index.html

# Windows
start http://localhost:8080/swagger-ui/index.html

# Или просто скопируйте URL в браузер вручную
```

## 7. Отладка проблем

Если Swagger UI не открывается:

1. Проверьте логи приложения на наличие ошибок
2. Убедитесь, что приложение запущено на порту 8080
3. Проверьте, что в pom.xml есть зависимость springdoc-openapi-starter-webmvc-ui
4. Попробуйте очистить кэш браузера

## 8. Полезные команды для проверки

```bash
# Проверить, запущено ли приложение
curl http://localhost:8080/actuator/health

# Получить список всех доступных endpoints
curl http://localhost:8080/api-docs | jq '.paths | keys'

# Проверить конкретный endpoint
curl http://localhost:8080/api-docs | jq '.paths."/api/auth/register"'
```