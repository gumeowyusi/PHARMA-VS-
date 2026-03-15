#!/bin/bash

# ============================================================
#   SCRIPT CHẠY NHANH DỰ ÁN - STORE (Spring Boot)
# ============================================================

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║        🚀 KHỞI ĐỘNG DỰ ÁN STORE          ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# Di chuyển đến thư mục dự án
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "📁 Thư mục dự án: $SCRIPT_DIR"
echo ""

# Kiểm tra Java
if ! command -v java &> /dev/null; then
    echo "❌ Chưa cài Java! Vui lòng cài Java 17+ trước."
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
echo "✅ Java version: $(java -version 2>&1 | head -1)"

# Kiểm tra file application.properties
if grep -q "ĐIỀN_APP_PASSWORD_GMAIL_Ở_ĐÂY" src/main/resources/application.properties; then
    echo ""
    echo "⚠️  CẢNH BÁO: Bạn chưa điền App Password Gmail!"
    echo "   Mở file: src/main/resources/application.properties"
    echo "   Tìm dòng: spring.mail.password=ĐIỀN_APP_PASSWORD_GMAIL_Ở_ĐÂY"
    echo "   → Thay bằng App Password thật của capyboy.dev@gmail.com"
    echo "   → Hướng dẫn lấy App Password: https://myaccount.google.com/apppasswords"
    echo ""
    read -p "   Bạn có muốn tiếp tục chạy không? (y/n): " choice
    if [[ "$choice" != "y" && "$choice" != "Y" ]]; then
        echo "Dừng lại. Vui lòng cấu hình App Password Gmail trước."
        exit 0
    fi
fi

echo ""
echo "🔨 Đang build và khởi động ứng dụng..."
echo "   (Lần đầu có thể mất 1-2 phút để tải dependencies)"
echo ""
echo "   Server sẽ chạy tại: http://localhost:8080"
echo "   Nhấn Ctrl+C để dừng"
echo ""
echo "──────────────────────────────────────────"

# Chạy với Maven Wrapper
./mvnw spring-boot:run
