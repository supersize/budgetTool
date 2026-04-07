#!/bin/bash

CURRENT_TIME=$(date +"%T")

## 기존 Spring 프로세스 종료
#echo "Stopping existing Spring application..."
#PID=$(pgrep -f 'java -jar /home/ec2-user/budgetTool-0.0.1-SNAPSHOT.jar')
#if [ -n "$PID" ]; then
#  echo "Found process with PID: $PID, terminating..."
#  kill -9 $PID
#  sleep 2
#else
#  echo "No running Spring application found."
#fi

## 새 JAR 파일 실행
#echo "Starting new Spring application..."
#nohup java -jar /home/ec2-user/budgetTool-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > /home/ec2-user/budgetTool-deploy-log.log 2>&1 &
#NEW_PID=$!
#sleep 5
#if ps -p $NEW_PID > /dev/null; then
#  echo "Application started successfully with PID: $NEW_PID"
#else
#  echo "Failed to start application. Check /home/ec2-user/budgetTool-deploy-failed.log"
#  cat /home/ec2-user/budgetTool-deploy-failed.log
#  exit 1
#fi

# 환경 변수 로드
if [ -f /home/ec2-user/.env ]; then
  echo "Loading environment variables from .env"
  set -a
  source /home/ec2-user/.env
  set +a
else
  echo "Error: .env file not found"
  exit 1
fi

# 1. Move to the directory where docker-compose.yml is located
cd /home/ec2-user # Replace with your actual path

echo "[$CURRENT_TIME] Starting Deployment..."

# 2. Pull the latest images (if using a registry like Docker Hub)
# If you are building locally, use: docker compose build
echo "Pulling latest images..."
docker compose pull

# 3. Down and Up (The simplest way to refresh)
# --remove-orphans deletes containers not defined in the current docker-compose.yml
echo "Restarting containers..."
docker compose down --remove-orphans
docker compose up -d

# 4. Cleanup unused images to save disk space (T3.micro has limited space!)
echo "Cleaning up old images..."
docker image prune -f

echo "[$CURRENT_TIME] Deployment completed successfully!"

# 5. Wait for Spring Boot Actuator to be ready
echo "Waiting for application to start..."
for i in {1..10}; do
  # Adjust the URL to match your context path and port
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/budgetTool/actuator/health)
  if [ "$STATUS" -eq 200 ]; then
    echo "Application is UP!"
    break
  else
    echo "Still waiting... (Attempt $i/10)"
    sleep 5
  fi
done

