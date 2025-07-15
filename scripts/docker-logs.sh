#!/bin/bash

# Check if service name is provided
if [ $# -eq 0 ]; then
    echo "Following logs for all services..."
    docker-compose logs -f
else
    echo "Following logs for service: $1"
    docker-compose logs -f $1
fi