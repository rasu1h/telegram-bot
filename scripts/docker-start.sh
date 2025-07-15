#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting Telegram Bot Application with Docker...${NC}"

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}Warning: .env file not found. Creating from .env.example...${NC}"
    cp .env.example .env
    echo -e "${RED}Please edit .env file and add your Telegram Bot credentials!${NC}"
    exit 1
fi

# Stop any running containers
echo -e "${YELLOW}Stopping existing containers...${NC}"
docker-compose down

# Build and start containers
echo -e "${GREEN}Building and starting containers...${NC}"
docker-compose up -d --build

# Wait for application to start
echo -e "${YELLOW}Waiting for application to start...${NC}"
sleep 10

# Check health
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Application is running!${NC}"
    echo -e "${GREEN}Swagger UI: http://localhost:8080/swagger-ui/index.html${NC}"
    echo -e "${GREEN}API Docs: http://localhost:8080/api-docs${NC}"
    
    # Show logs
    echo -e "\n${YELLOW}Application logs:${NC}"
    docker-compose logs --tail=20 app
else
    echo -e "${RED}❌ Application failed to start. Check logs:${NC}"
    docker-compose logs app
fi