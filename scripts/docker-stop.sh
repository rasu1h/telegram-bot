#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Stopping Telegram Bot Application...${NC}"

# Stop containers
docker-compose down

echo -e "${GREEN}✅ Application stopped${NC}"

# Ask if user wants to remove volumes
read -p "Do you want to remove data volumes? (y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker-compose down -v
    echo -e "${GREEN}✅ Volumes removed${NC}"
fi