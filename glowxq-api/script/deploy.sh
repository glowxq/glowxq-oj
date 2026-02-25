#!/bin/bash
set -e

# ============================================================
# GlowXQ OJ - 一键部署脚本
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "${BLUE}[STEP]${NC} $1"; }

# ==================== 1. 环境检查 ====================
log_step "1/5 检查运行环境..."

# 检查 JDK 21
REQUIRED_JAVA_MAJOR=21

check_java_version() {
    local java_cmd="$1"
    local version_output
    version_output=$("$java_cmd" -version 2>&1 | head -1)
    # 提取主版本号（兼容 "21.0.2" 和 "1.8.0_xxx" 格式）
    local major
    major=$(echo "$version_output" | grep -oE '"[0-9]+' | head -1 | tr -d '"')
    echo "$major"
}

current_java_major=$(check_java_version java)

if [ "$current_java_major" != "$REQUIRED_JAVA_MAJOR" ]; then
    log_warn "当前 Java 版本为 $current_java_major，项目需要 Java $REQUIRED_JAVA_MAJOR"

    # macOS: 尝试自动查找 JDK 21
    if [[ "$OSTYPE" == "darwin"* ]] && command -v /usr/libexec/java_home &> /dev/null; then
        JDK21_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || true)
        if [ -n "$JDK21_HOME" ] && [ -d "$JDK21_HOME" ]; then
            export JAVA_HOME="$JDK21_HOME"
            export PATH="$JAVA_HOME/bin:$PATH"
            log_info "已自动切换到 JDK 21: $JAVA_HOME"
        fi
    fi

    # Linux: 尝试查找常见 JDK 21 路径
    if [[ "$OSTYPE" == "linux"* ]]; then
        for candidate in \
            "/usr/lib/jvm/java-21-openjdk-amd64" \
            "/usr/lib/jvm/java-21-openjdk" \
            "/usr/lib/jvm/java-21" \
            "/usr/java/jdk-21"; do
            if [ -d "$candidate" ]; then
                export JAVA_HOME="$candidate"
                export PATH="$JAVA_HOME/bin:$PATH"
                log_info "已自动切换到 JDK 21: $JAVA_HOME"
                break
            fi
        done
    fi

    # 再次检查
    current_java_major=$(check_java_version java)
    if [ "$current_java_major" != "$REQUIRED_JAVA_MAJOR" ]; then
        log_error "未找到 JDK $REQUIRED_JAVA_MAJOR，请安装后重试"
        log_error "  macOS:  brew install openjdk@21"
        log_error "  Ubuntu: sudo apt install openjdk-21-jdk"
        log_error "  或手动设置: export JAVA_HOME=/path/to/jdk-21"
        exit 1
    fi
fi
log_info "Java 版本: $(java -version 2>&1 | head -1)"

# 检查 Docker
if ! command -v docker &> /dev/null; then
    log_error "Docker 未安装，请先安装 Docker: https://docs.docker.com/get-docker/"
    exit 1
fi
log_info "Docker 已安装: $(docker --version)"

# 检查 Docker Compose
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
elif command -v docker-compose &> /dev/null; then
    COMPOSE_CMD="docker-compose"
else
    log_error "Docker Compose 未安装，请先安装: https://docs.docker.com/compose/install/"
    exit 1
fi
log_info "Docker Compose 已安装: $($COMPOSE_CMD version)"

# ==================== 2. 生成 .env ====================
log_step "2/5 生成环境配置..."

cd "$SCRIPT_DIR"

if [ ! -f .env ]; then
    cp .env.example .env

    # 自动生成随机 JWT 密钥
    JWT_KEY=$(openssl rand -base64 32 2>/dev/null || cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "s|your_secret_key_please_change_me|${JWT_KEY}|g" .env
    else
        sed -i "s|your_secret_key_please_change_me|${JWT_KEY}|g" .env
    fi

    log_info ".env 文件已生成（JWT 密钥已自动生成）"
else
    log_warn ".env 文件已存在，跳过生成"
fi

# ==================== 3. Maven 编译 ====================
log_step "3/5 编译项目..."

cd "$PROJECT_DIR"

if command -v mvn &> /dev/null; then
    mvn clean package -DskipTests -q
    log_info "Maven 编译完成"
elif [ -f "./mvnw" ]; then
    chmod +x ./mvnw
    ./mvnw clean package -DskipTests -q
    log_info "Maven Wrapper 编译完成"
else
    log_error "Maven 未安装且未找到 mvnw，请先安装 Maven"
    exit 1
fi

# ==================== 4. 启动服务 ====================
log_step "4/5 启动 Docker 服务..."

cd "$SCRIPT_DIR"
$COMPOSE_CMD up -d --build

# ==================== 5. 等待服务就绪 ====================
log_step "5/5 等待服务就绪..."

echo ""
log_info "等待服务启动中（约30-60秒）..."

# 等待 MySQL
echo -n "  MySQL: "
for i in $(seq 1 30); do
    if docker exec oj-mysql mysqladmin ping -h localhost -u root -p"$(grep MYSQL_ROOT_PASSWORD .env | cut -d= -f2)" --silent 2>/dev/null; then
        echo -e "${GREEN}就绪${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

# 等待 Redis
echo -n "  Redis: "
for i in $(seq 1 15); do
    if docker exec oj-redis redis-cli -a "$(grep REDIS_PASSWORD .env | cut -d= -f2)" ping 2>/dev/null | grep -q PONG; then
        echo -e "${GREEN}就绪${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

# 等待 MinIO
echo -n "  MinIO: "
for i in $(seq 1 15); do
    if curl -sf http://localhost:${MINIO_API_PORT:-9000}/minio/health/live &>/dev/null; then
        echo -e "${GREEN}就绪${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

# 等待 OJ API
echo -n "  OJ API: "
for i in $(seq 1 30); do
    if curl -sf http://localhost:${APP_PORT:-7101}/api/doc.html &>/dev/null; then
        echo -e "${GREEN}就绪${NC}"
        break
    fi
    echo -n "."
    sleep 3
done

# ==================== 完成 ====================
echo ""
echo -e "${GREEN}============================================================${NC}"
echo -e "${GREEN} GlowXQ OJ 部署完成！${NC}"
echo -e "${GREEN}============================================================${NC}"
echo ""
echo -e "  API 地址:        http://localhost:${APP_PORT:-7101}"
echo -e "  Swagger 文档:    http://localhost:${APP_PORT:-7101}/doc.html"
echo -e "  MinIO 控制台:    http://localhost:${MINIO_CONSOLE_PORT:-9001}"
echo ""
echo -e "  停止服务: cd $SCRIPT_DIR && $COMPOSE_CMD down"
echo -e "  查看日志: cd $SCRIPT_DIR && $COMPOSE_CMD logs -f oj-api"
echo ""
