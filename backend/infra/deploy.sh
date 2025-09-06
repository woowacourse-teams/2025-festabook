APP_HOME="/home/ubuntu/app"
LOG_FILE="/tmp/app.log"
JAR_NAME=$(find $APP_HOME -name "*.jar" | head -n 1)

echo "🚀========== 배포 스크립트 시작 =========="

echo "🛑 기존 Spring WAS 종료 중..."
PID=$(lsof -t -i:80 || true)
if [ -n "$PID" ]; then
  kill -SIGTERM $PID
  sleep 5
  if ps -p $PID > /dev/null; then
    echo "⚠️ 프로세스 $PID 가 아직 종료되지 않음. 강제 종료합니다..."
    kill -9 $PID
  fi
else
  echo "✅ 80 포트에서 실행 중인 프로세스가 없습니다."
fi

echo "▶️ 새로운 Spring WAS 실행 중..."
if [ -f "$JAR_NAME" ]; then
  sudo nohup java -jar -Duser.timezone=Asia/Seoul "$JAR_NAME" --spring.profiles.active=prod > $LOG_FILE 2>&1 &
  echo "📦 실행 파일: $JAR_NAME"
else
  echo "❌ 오류: $APP_HOME 경로에서 JAR 파일을 찾을 수 없습니다."
  exit 1
fi

echo "🩺 애플리케이션 상태 확인 중..."
for i in {1..90}; do
  if curl -s http://localhost/api/actuator/health | grep '"status":"UP"' > /dev/null; then
    echo "✅ 애플리케이션이 정상적으로 실행되었습니다!"
    exit 0
  fi
  echo "⏳ 애플리케이션 실행 대기 중... ($i/90)"
  sleep 1
done

echo "🚨 오류: 애플리케이션이 시작되지 않았습니다."
echo ""
echo "📋 최근 애플리케이션 로그:"
tail -n 30 $LOG_FILE 2>/dev/null || echo "로그 파일을 읽을 수 없습니다."
echo ""
echo "🔍 현재 포트 상태:"
netstat -tlnp | grep :80 || echo "포트 80에서 실행 중인 프로세스 없음"
echo ""
echo "💾 프로세스 상태:"
ps aux | grep java | grep -v grep || echo "실행 중인 Java 프로세스 없음"

exit 1
