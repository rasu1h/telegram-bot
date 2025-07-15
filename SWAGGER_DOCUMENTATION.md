# Swagger/OpenAPI Documentation

## Доступ к документации

После запуска приложения, Swagger UI доступен по следующим адресам:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Альтернативный URL**: http://localhost:8080/swagger-ui.html (автоматически перенаправляет)
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

## Особенности конфигурации

### 1. Аутентификация
- Все endpoints кроме `/api/auth/*` требуют JWT токен
- В Swagger UI используйте кнопку "Authorize" и введите токен в формате: `Bearer YOUR_JWT_TOKEN`

### 2. Группировка endpoints
- **Authentication** - регистрация и авторизация
- **Telegram Integration** - работа с Telegram ботом

### 3. Try it out
- Все endpoints можно протестировать прямо из Swagger UI
- Нажмите "Try it out" для активации формы
- Заполните необходимые параметры
- Нажмите "Execute"

## Примеры использования

### 1. Регистрация нового пользователя
1. Откройте раздел **Authentication**
2. Выберите `POST /api/auth/register`
3. Нажмите "Try it out"
4. Введите данные:
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "name": "Test User"
}
```
5. Нажмите "Execute"
6. Скопируйте токен из ответа

### 2. Авторизация в Swagger UI
1. Нажмите кнопку "Authorize" (замок) в верхней части страницы
2. В поле введите: `Bearer YOUR_TOKEN_HERE`
3. Нажмите "Authorize"
4. Теперь вы можете использовать защищенные endpoints

### 3. Генерация Telegram токена
1. Убедитесь, что вы авторизованы
2. Откройте раздел **Telegram Integration**
3. Выберите `POST /api/telegram/token/generate`
4. Нажмите "Try it out" и "Execute"
5. Получите UUID токен для привязки в Telegram

### 4. Отправка сообщения
1. После привязки токена в Telegram боте
2. Выберите `POST /api/telegram/message/send`
3. Введите сообщение:
```json
{
  "message": "Привет из Swagger!"
}
```
4. Нажмите "Execute"

## Настройки Swagger UI

В `application.properties` настроены следующие параметры:

- `operations-sorter=method` - сортировка операций по HTTP методу
- `tags-sorter=alpha` - алфавитная сортировка тегов
- `try-it-out-enabled=true` - кнопка "Try it out" включена по умолчанию
- `filter=true` - включен поиск по операциям
- `display-request-duration=true` - показывать время выполнения запроса
- `doc-expansion=none` - операции свернуты по умолчанию

## Экспорт документации

### OpenAPI спецификация доступна в форматах:
- **JSON**: `GET http://localhost:8080/api-docs`
- **YAML**: `GET http://localhost:8080/api-docs.yaml`

### Импорт в Postman:
1. Скачайте OpenAPI спецификацию
2. В Postman: Import → Upload Files → выберите скачанный файл
3. Postman автоматически создаст коллекцию со всеми endpoints

### Генерация клиентского кода:
Используйте OpenAPI Generator для генерации клиентов на различных языках:
```bash
openapi-generator-cli generate -i http://localhost:8080/api-docs -g java -o ./generated-client
```

## Расширение документации

### Добавление новых endpoints:
1. Добавьте аннотацию `@Tag` к контроллеру
2. Используйте `@Operation` для описания метода
3. Добавьте `@ApiResponses` для описания возможных ответов
4. Используйте `@Schema` в DTO классах

### Пример:
```java
@Operation(summary = "Краткое описание", 
          description = "Подробное описание операции")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", 
                description = "Успешный ответ",
                content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    @ApiResponse(responseCode = "400", 
                description = "Ошибка валидации")
})
```

## Безопасность

- Swagger UI доступен только в development окружении
- Для production рекомендуется отключить или защитить доступ:
```properties
# Отключить Swagger в production
springdoc.swagger-ui.enabled=false
```

## Полезные ссылки

- [SpringDoc OpenAPI документация](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI документация](https://swagger.io/tools/swagger-ui/)