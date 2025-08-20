APP_HOME="/home/ubuntu/app"
JAR_NAME=$(find $APP_HOME -name "*.jar" | head -n 1)
LOG_PATH="$APP_HOME/application.log"

echo "========== Deployment Script Started =========="

# 1. 이전 프로세스 종료
echo ">>> Stopping old Spring WAS..."
PID=$(lsof -t -i:80 || true)
if [ -n "$PID" ]; then
  kill -SIGTERM $PID
  sleep 5
  if ps -p $PID > /dev/null; then
    echo ">>> Process $PID still alive. Force killing..."
    kill -9 $PID
  fi
else
  echo ">>> No process found on port 80."
fi

# 2. 최신 JAR 실행
echo ">>> Starting new Spring WAS..."
if [ -f "$JAR_NAME" ]; then
  nohup java -jar -Duser.timezone=Asia/Seoul "$JAR_NAME" --spring.profiles.active=prod > "$LOG_PATH" 2>&1 &
else
  echo ">>> ERROR: No JAR file found in $APP_HOME"
  exit 1
fi

# 3. Health Check
echo ">>> Validating application health..."
for i in {1..30}; do
  if curl -s http://localhost:80/actuator/health | grep '"status":"UP"' > /dev/null; then
    echo ">>> Application is healthy!"
    exit 0
  fi
  echo "Waiting for application to be UP... ($i/30)"
  sleep 2
done

echo ">>> ERROR: Application failed to start."
exit 1
