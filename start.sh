#!/bin/bash
# StreetConnect - Application Launcher
# ======================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
JAR_FILE="$BACKEND_DIR/target/streetconnect-backend-1.0.0.jar"
LOG_FILE="/tmp/streetconnect.log"
PID_FILE="/tmp/streetconnect.pid"

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║       StreetConnect — Launching...       ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# Check if port 8080 is already in use
if lsof -ti:8080 >/dev/null 2>&1; then
  echo "⚠️  Port 8080 is already in use."
  echo "    If StreetConnect is running, visit: http://localhost:8080"
  echo "    To stop it: kill \$(cat $PID_FILE)"
  echo ""
  exit 0
fi

# Check Java
if ! java -version >/dev/null 2>&1; then
  echo "❌ Java is not installed or not in PATH."
  echo "   Please install Java 17 or higher."
  exit 1
fi

# Check JAR
if [ ! -f "$JAR_FILE" ]; then
  echo "📦 Building project..."
  export PATH="/Users/edison/.maven/apache-maven-3.9.9/bin:$PATH"
  cd "$BACKEND_DIR" && mvn package -DskipTests -q
  if [ $? -ne 0 ]; then
    echo "❌ Build failed! Check Maven setup."
    exit 1
  fi
  echo "✅ Build successful!"
fi

# Start the server
echo "🚀 Starting StreetConnect server..."
nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

# Wait for startup
echo "⏳ Waiting for server to start..."
for i in $(seq 1 30); do
  sleep 1
  if curl -s http://localhost:8080/api/streets >/dev/null 2>&1; then
    echo ""
    echo "✅ StreetConnect is running!"
    echo ""
    echo "  🌐 App URL: http://localhost:8080/index.html"
    echo "  🔑 Admin Login:    admin@streetconnect.com / admin123"
    echo "  👮 Moderator Login: mod.tambaram@streetconnect.com / password123"
    echo "  👤 Resident Login:  arun.prakash@gmail.com / password123"
    echo ""
    echo "  📋 Server logs: $LOG_FILE"
    echo "  🛑 To stop:     kill \$(cat $PID_FILE)"
    echo ""
    open "http://localhost:8080/index.html" 2>/dev/null || true
    exit 0
  fi
done

echo "❌ Server failed to start within 30 seconds."
echo "   Check logs: $LOG_FILE"
exit 1
