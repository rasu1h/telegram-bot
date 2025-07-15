-- Добавляем новые поля для улучшения безопасности токенов
ALTER TABLE telegram_tokens 
ADD COLUMN expires_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '10 minutes'),
ADD COLUMN is_bound BOOLEAN NOT NULL DEFAULT FALSE;

-- Обновляем существующие токены
UPDATE telegram_tokens 
SET is_bound = TRUE 
WHERE telegram_chat_id IS NOT NULL;

-- Обновляем expires_at для существующих токенов
UPDATE telegram_tokens 
SET expires_at = created_at + INTERVAL '10 minutes'
WHERE expires_at = created_at;