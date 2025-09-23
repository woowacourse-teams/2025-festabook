#!/bin/bash
APP_HOME="/home/ubuntu/app"
LOG_FILE="/tmp/app.log"
JAR_NAME=$(find $APP_HOME -name "*.jar" | head -n 1)

echo "🚀========== ApplicationStart =========="

echo "▶️ Spring WAS 실행 중..."
if [ -f "$JAR_NAME" ]; then
  nohup java -jar -Duser.timezone=Asia/Seoul "$JAR_NAME" \
    --spring.profiles.active=prod > $LOG_FILE 2>&1 &
  echo "📦 실행 파일: $JAR_NAME"
else
  echo "❌ 오류: $APP_HOME 경로에서 JAR 파일을 찾을 수 없습니다."
  exit 1
fi
