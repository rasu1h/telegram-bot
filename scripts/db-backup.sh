#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Create backup directory if not exists
mkdir -p backups

# Generate timestamp
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="backups/telegram_bot_db_backup_${TIMESTAMP}.sql"

echo -e "${YELLOW}Creating database backup...${NC}"

# Create backup
docker-compose exec -T postgres pg_dump -U postgres telegram_bot_db > ${BACKUP_FILE}

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Backup created successfully: ${BACKUP_FILE}${NC}"
    
    # Compress backup
    gzip ${BACKUP_FILE}
    echo -e "${GREEN}✅ Backup compressed: ${BACKUP_FILE}.gz${NC}"
    
    # Show backup size
    ls -lh ${BACKUP_FILE}.gz
else
    echo -e "${RED}❌ Backup failed${NC}"
    rm -f ${BACKUP_FILE}
fi