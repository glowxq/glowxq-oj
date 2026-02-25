#!/bin/bash
set -eo pipefail

# ============================================================
# GlowXQ OJ - All-in-One 单容器部署脚本
#
# 将 OJ 全部服务打包成单个 Docker 容器：
# MySQL + Redis + Nginx + Spring Boot + go-judge + MinIO
# 仅暴露 80 端口，数据通过 Docker Volume 持久化
#
# 用法:
#   ./deploy-allinone.sh           构建 + 启动（默认）
#   ./deploy-allinone.sh stop      停止容器
#   ./deploy-allinone.sh restart   重启容器
#   ./deploy-allinone.sh logs      查看日志
#   ./deploy-allinone.sh status    查看状态
#   ./deploy-allinone.sh update    重新构建部署
#   ./deploy-allinone.sh reset-db  重置数据库（删除数据后重新初始化）
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
IMAGE_NAME="glowxq-oj"
IMAGE_TAG="allinone"
CONTAINER_NAME="glowxq-oj"
ENV_FILE="$SCRIPT_DIR/.env.allinone"

# ─── 颜色定义 ────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
DIM='\033[2m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[✓]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
log_error() { echo -e "${RED}[✗]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}${BOLD}$1${NC}"; }

# ─── 安全加载 .env.allinone ──────────────────────────────────
load_env() {
    if [ -f "$ENV_FILE" ]; then
        set +e
        source "$ENV_FILE" 2>/dev/null
        set -e
    fi
}

# ─── Banner ──────────────────────────────────────────────────
show_banner() {
    echo -e "${CYAN}"
    cat << 'BANNER'
   ╔══════════════════════════════════════════════╗
   ║                                              ║
   ║     ┌─┐┬  ┌─┐┬ ┬─┐ ┬┌─┐   ┌─┐ ┬           ║
   ║     │ ┬│  │ ││││┌┘ │ │├┤   │ │ │           ║
   ║     └─┘┴─┘└─┘└┴┘┴└─┴ └┘   └─┘└┘           ║
   ║                                              ║
   ║     All-in-One 全家桶部署 (v2.0)             ║
   ║     MySQL+Redis+Nginx+API+Judge+MinIO        ║
   ║                                              ║
   ╚══════════════════════════════════════════════╝
BANNER
    echo -e "${NC}"
    echo -e "  ${DIM}$(date '+%Y-%m-%d %H:%M:%S')${NC}"
    echo ""
}

# ─── 使用说明 ────────────────────────────────────────────────
show_usage() {
    echo -e "${BOLD}用法:${NC} $0 [命令]"
    echo ""
    echo -e "${BOLD}部署命令:${NC}"
    echo -e "  ${GREEN}(默认)${NC}       构建镜像 + 启动容器"
    echo -e "  ${GREEN}stop${NC}         停止容器"
    echo -e "  ${GREEN}restart${NC}      重启容器"
    echo -e "  ${GREEN}update${NC}       重新构建并部署（保留数据）"
    echo -e "  ${RED}reset-db${NC}     重置数据库（删除 MySQL 数据后重新初始化）"
    echo ""
    echo -e "${BOLD}运维命令:${NC}"
    echo -e "  ${CYAN}logs${NC}         查看容器日志"
    echo -e "  ${CYAN}status${NC}       查看容器状态"
    echo -e "  ${CYAN}shell${NC}        进入容器 Shell"
    echo ""
    echo -e "${BOLD}其他:${NC}"
    echo -e "  ${DIM}help${NC}         显示此帮助信息"
    echo ""
    echo -e "${BOLD}配置方式:${NC}"
    echo -e "  所有配置通过 ${CYAN}.env.allinone${NC} 文件管理（首次运行自动生成）"
    echo -e "  编辑 .env.allinone 修改端口映射、数据库密码、站点 URL 等"
    echo ""
    echo -e "${BOLD}数据管理:${NC}"
    echo -e "  MySQL/Redis 数据持久化在 Docker Volume 中，容器重建不丢失"
    echo -e "  如需完全重置：${RED}docker volume rm oj-data${NC}（会丢失所有数据！）"
    echo ""
    echo -e "${BOLD}示例:${NC}"
    echo -e "  $0              ${DIM}# 首次部署（全自动构建+启动）${NC}"
    echo -e "  $0 update       ${DIM}# 代码更新后重新构建部署${NC}"
    echo -e "  $0 logs         ${DIM}# 查看运行日志${NC}"
    echo ""
}

# ============================================================
# 生成 .env 配置文件（首次运行时）
# ============================================================
setup_env() {
    cd "$SCRIPT_DIR"

    if [ -f "$ENV_FILE" ]; then
        log_warn ".env.allinone 已存在，跳过生成（如需重新生成请先删除）"
        return
    fi

    log_step ">>> 生成环境配置 (.env.allinone)"

    # 自动生成随机 JWT 密钥
    local jwt_key
    jwt_key=$(openssl rand -base64 32 2>/dev/null || cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)

    cat > "$ENV_FILE" << EOF
# ============================================================
# GlowXQ OJ - All-in-One 部署配置
# 所有服务（MySQL/Redis/MinIO/API）均在容器内部运行
# 首次生成，可根据需要修改
# ============================================================

# ==================== 站点配置 ====================
# 站点公网地址（用于生成文件可访问 URL）
# 本地测试可不设置，生产部署设置为实际域名
# 示例: https://oj.example.com
SITE_URL=

# 宿主机端口映射（容器 80 端口映射到宿主机端口）
# 默认 80，如果 80 被占用可改为其他端口如 8080
WEB_PORT=80

# ==================== 数据库密码 ====================
# 容器内部 MySQL root 密码
MYSQL_ROOT_PASSWORD=glowxq123456
MYSQL_DATABASE=glowxq_oj

# 容器内部 Redis 密码
REDIS_PASSWORD=glowxq123456

# ==================== JWT 密钥（已自动生成） ====================
JWT_SECRET_KEY=${jwt_key}

# ==================== JVM 配置 ====================
JAVA_OPTS="-Xms256m -Xmx512m"

# ==================== MinIO（容器内部，一般不需改）====================
OSS_ACCESS_KEY=minioadmin
OSS_SECRET_KEY=minioadmin

# ==================== 飞书集成（可选） ====================
# FEISHU_INTERNAL_WEBHOOK=
# FEISHU_BUSINESS_WEBHOOK=
# FEISHU_APP_ID=
# FEISHU_APP_SECRET=
EOF

    log_info ".env.allinone 已生成（JWT 密钥已自动随机生成）"
}

# ============================================================
# 构建镜像
# ============================================================
build_image() {
    log_step ">>> 构建 All-in-One 镜像"

    cd "$SCRIPT_DIR"

    export DOCKER_BUILDKIT=1

    local start_time=$(date +%s)

    # 注意：管道会吞掉 docker build 的退出码，需用 pipefail（已在脚本头部设置）
    docker build \
        -f Dockerfile.allinone \
        -t "${IMAGE_NAME}:${IMAGE_TAG}" \
        . 2>&1 | while IFS= read -r line; do
        echo -e "  ${DIM}${line}${NC}"
    done

    # 双重保险：验证镜像确实构建成功
    if ! docker image inspect "${IMAGE_NAME}:${IMAGE_TAG}" &>/dev/null; then
        log_error "镜像构建失败，请检查上方错误信息"
        exit 1
    fi

    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    log_info "镜像构建完成 (耗时 ${duration}s)"
}

# ============================================================
# 启动容器
# ============================================================
start_container() {
    log_step ">>> 启动 All-in-One 容器"

    load_env

    # 检查镜像是否存在
    if ! docker image inspect "${IMAGE_NAME}:${IMAGE_TAG}" &>/dev/null; then
        log_error "镜像 ${IMAGE_NAME}:${IMAGE_TAG} 不存在，请先构建：$0 或 $0 update"
        exit 1
    fi

    # 停止已有容器
    if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        log_warn "发现已有容器，正在移除..."
        docker rm -f "${CONTAINER_NAME}" >/dev/null 2>&1 || true
    fi

    # SITE_URL
    local site_url="${SITE_URL:-}"
    if [ -n "$site_url" ]; then
        log_info "站点地址: ${site_url}"
    else
        log_warn "未配置 SITE_URL，文件 URL 将使用容器内部地址（本地测试可用）"
    fi

    docker run -d \
        --name "${CONTAINER_NAME}" \
        --privileged \
        --restart unless-stopped \
        -p "${WEB_PORT:-80}:80" \
        -v oj-data:/data \
        -v oj-judge:/goj \
        -e MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-glowxq123456}" \
        -e MYSQL_DATABASE="${MYSQL_DATABASE:-glowxq_oj}" \
        -e REDIS_PASSWORD="${REDIS_PASSWORD:-glowxq123456}" \
        -e OSS_ENDPOINT="http://127.0.0.1:9000" \
        -e OSS_ACCESS_KEY="${OSS_ACCESS_KEY:-minioadmin}" \
        -e OSS_SECRET_KEY="${OSS_SECRET_KEY:-minioadmin}" \
        -e SITE_URL="${site_url}" \
        -e JWT_SECRET_KEY="${JWT_SECRET_KEY:-please_change_me}" \
        -e JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m}" \
        -e FEISHU_INTERNAL_WEBHOOK="${FEISHU_INTERNAL_WEBHOOK:-}" \
        -e FEISHU_BUSINESS_WEBHOOK="${FEISHU_BUSINESS_WEBHOOK:-}" \
        -e FEISHU_APP_ID="${FEISHU_APP_ID:-}" \
        -e FEISHU_APP_SECRET="${FEISHU_APP_SECRET:-}" \
        "${IMAGE_NAME}:${IMAGE_TAG}"

    log_info "容器已启动: ${CONTAINER_NAME}"
}

# ============================================================
# 等待服务就绪
# ============================================================
wait_for_services() {
    log_step ">>> 等待服务就绪"

    load_env
    local port="${WEB_PORT:-80}"

    echo ""
    # 等待前端
    echo -n "  前端页面 (:${port})  "
    for i in $(seq 1 30); do
        if curl -sf "http://localhost:${port}/" &>/dev/null; then
            echo -e "${GREEN}● 就绪${NC}"
            break
        fi
        echo -n "."
        sleep 2
    done

    # 等待 API（MySQL 初始化 + Spring Boot 启动需要时间）
    echo -n "  后端 API          "
    for j in $(seq 1 60); do
        if curl -sf "http://localhost:${port}/api/doc.html" &>/dev/null; then
            echo -e "${GREEN}● 就绪${NC}"
            echo ""
            return 0
        fi
        echo -n "."
        sleep 3
    done
    echo -e "${YELLOW}● 仍在启动中（首次启动需初始化数据库，约 1-2 分钟）${NC}"
    log_warn "可使用 $0 logs 查看详细日志"
    echo ""
}

# ============================================================
# 显示部署结果
# ============================================================
show_result() {
    load_env
    local port="${WEB_PORT:-80}"

    echo -e "${GREEN}"
    echo "  ╔══════════════════════════════════════════════╗"
    echo "  ║       GlowXQ OJ 部署完成！                   ║"
    echo "  ╚══════════════════════════════════════════════╝"
    echo -e "${NC}"
    echo -e "  ${BOLD}服务访问地址:${NC}"
    echo -e "  ┌──────────────────────────────────────────────┐"
    echo -e "  │  前端页面       http://localhost:${port}"
    echo -e "  │  API 文档       http://localhost:${port}/api/doc.html"
    echo -e "  └──────────────────────────────────────────────┘"
    echo ""
    echo -e "  ${BOLD}容器内部服务:${NC}"
    echo -e "  ┌──────────────────────────────────────────────┐"
    echo -e "  │  MySQL     127.0.0.1:3306/${MYSQL_DATABASE:-glowxq_oj} (容器内部)"
    echo -e "  │  Redis     127.0.0.1:6379 (容器内部)"
    echo -e "  │  MinIO     127.0.0.1:9000 (容器内部)"
    echo -e "  │  API       127.0.0.1:7101 (容器内部)"
    echo -e "  │  go-judge  127.0.0.1:5050 (容器内部)"
    echo -e "  └──────────────────────────────────────────────┘"
    echo ""
    echo -e "  ${BOLD}常用命令:${NC}"
    echo -e "  ┌──────────────────────────────────────────────┐"
    echo -e "  │  查看日志  ${CYAN}$0 logs${NC}"
    echo -e "  │  查看状态  ${CYAN}$0 status${NC}"
    echo -e "  │  停止服务  ${CYAN}$0 stop${NC}"
    echo -e "  │  重启服务  ${CYAN}$0 restart${NC}"
    echo -e "  │  更新部署  ${CYAN}$0 update${NC}"
    echo -e "  │  进入容器  ${CYAN}$0 shell${NC}"
    echo -e "  └──────────────────────────────────────────────┘"
    echo ""
}

# ============================================================
# 命令实现
# ============================================================

# 一键部署（构建 + 启动）
cmd_deploy() {
    show_banner

    # 检查 Docker
    if ! command -v docker &>/dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    if ! docker info &>/dev/null 2>&1; then
        log_error "Docker 未启动，请先启动 Docker"
        exit 1
    fi

    setup_env
    load_env
    build_image
    start_container
    wait_for_services
    show_result
}

# 停止
cmd_stop() {
    log_info "停止容器: ${CONTAINER_NAME}..."
    docker stop "${CONTAINER_NAME}" 2>/dev/null || true
    log_info "容器已停止"
}

# 重启
cmd_restart() {
    log_info "重启容器: ${CONTAINER_NAME}..."
    docker restart "${CONTAINER_NAME}" 2>/dev/null || log_error "容器不存在，请先运行 $0"
    log_info "容器已重启"
}

# 查看日志
cmd_logs() {
    docker logs -f --tail=200 "${CONTAINER_NAME}" 2>/dev/null || log_error "容器不存在"
}

# 查看状态
cmd_status() {
    echo ""
    echo -e "${BOLD}容器状态:${NC}"
    docker ps -a --filter "name=${CONTAINER_NAME}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}\t{{.Size}}" 2>/dev/null
    echo ""

    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        echo -e "${BOLD}资源使用:${NC}"
        docker stats --no-stream --format "  CPU: {{.CPUPerc}}  Memory: {{.MemUsage}}" "${CONTAINER_NAME}" 2>/dev/null
        echo ""

        echo -e "${BOLD}内部进程:${NC}"
        docker exec "${CONTAINER_NAME}" supervisorctl status 2>/dev/null || \
            docker exec "${CONTAINER_NAME}" ps aux --no-headers 2>/dev/null
        echo ""
    fi
}

# 进入容器 shell
cmd_shell() {
    docker exec -it "${CONTAINER_NAME}" /bin/bash 2>/dev/null || log_error "容器不存在或未运行"
}

# 重置数据库
cmd_reset_db() {
    show_banner
    log_step ">>> 重置数据库"

    echo -e "${RED}${BOLD}  ⚠️  警告：此操作将删除所有 MySQL 数据！${NC}"
    echo -e "  包括：所有数据库、表、用户数据"
    echo -e "  Redis 缓存数据也将被清除"
    echo ""
    read -p "  确定要重置吗？输入 yes 继续: " confirm
    if [ "$confirm" != "yes" ]; then
        log_info "已取消"
        return
    fi

    # 停止容器
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        log_info "停止容器..."
        docker rm -f "${CONTAINER_NAME}" >/dev/null 2>&1 || true
    fi

    # 删除数据 volume
    log_info "删除数据卷 oj-data..."
    docker volume rm oj-data 2>/dev/null || true

    log_info "数据已重置，下次启动容器时将自动重新初始化数据库"
    echo ""
    echo -e "  ${BOLD}下一步:${NC} 运行 ${CYAN}$0${NC} 或 ${CYAN}$0 update${NC} 重新启动"
    echo ""
}

# 更新部署
cmd_update() {
    show_banner
    load_env
    log_step ">>> 更新部署"

    build_image

    # 停止旧容器
    docker rm -f "${CONTAINER_NAME}" >/dev/null 2>&1 || true

    start_container

    # 清理悬空镜像
    log_info "清理旧镜像..."
    docker image prune -f --filter "dangling=true" 2>/dev/null || true

    wait_for_services
    show_result
}

# ============================================================
# 主入口
# ============================================================
case "${1:-deploy}" in
    deploy|start)   cmd_deploy ;;
    stop)           cmd_stop ;;
    restart)        cmd_restart ;;
    update)         cmd_update ;;
    reset-db)       cmd_reset_db ;;
    logs)           cmd_logs ;;
    status)         cmd_status ;;
    shell)          cmd_shell ;;
    help|-h|--help) show_usage ;;
    *)
        log_error "未知命令: $1"
        echo ""
        show_usage
        exit 1
        ;;
esac
