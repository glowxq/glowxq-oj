#!/bin/bash
set -eo pipefail

# ============================================================
# GlowXQ OJ - 全栈一键部署脚本
# 前端 (Vue 3 + Nginx) + 后端 (Spring Boot + go-judge)
# 基础设施 (MySQL 8 + Redis 7 + MinIO)
#
# 仅需 Docker + Docker Compose，无需宿主机安装 Java/Maven/Node
# 所有编译均在 Docker 多阶段构建中完成
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
VERSION="2.0.0"

# ─── 颜色定义 ────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
BOLD='\033[1m'
DIM='\033[2m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[✓]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
log_error() { echo -e "${RED}[✗]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}${BOLD}$1${NC}"; }
log_dim()   { echo -e "${DIM}    $1${NC}"; }

# 安全加载 .env（防止含空格的值触发 set -e 错误）
load_env() {
    set +e
    source "$SCRIPT_DIR/.env" 2>/dev/null
    set -e
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
   ║        Online Judge 全栈部署脚本              ║
   ║     Vue 3 + Spring Boot + go-judge           ║
   ║                                              ║
   ╚══════════════════════════════════════════════╝
BANNER
    echo -e "${NC}"
    echo -e "  ${DIM}Version: ${VERSION} | $(date '+%Y-%m-%d %H:%M:%S')${NC}"
    echo ""
}

# ─── 使用说明 ────────────────────────────────────────────────
show_usage() {
    echo -e "${BOLD}用法:${NC} $0 [命令]"
    echo ""
    echo -e "${BOLD}部署命令:${NC}"
    echo -e "  ${GREEN}deploy${NC}       一键完整部署（构建 + 启动） ${DIM}[默认]${NC}"
    echo -e "  ${GREEN}start${NC}        启动所有服务（不重新构建镜像）"
    echo -e "  ${GREEN}stop${NC}         停止所有服务"
    echo -e "  ${GREEN}restart${NC}      重启所有服务"
    echo -e "  ${GREEN}update${NC}       重新构建并部署（保留数据）"
    echo ""
    echo -e "${BOLD}开发命令:${NC}"
    echo -e "  ${CYAN}dev${NC}          仅启动基础设施（MySQL + Redis + MinIO）"
    echo -e "  ${CYAN}dev-stop${NC}     停止基础设施服务"
    echo -e "  ${CYAN}build-api${NC}    仅构建后端镜像"
    echo -e "  ${CYAN}build-web${NC}    仅构建前端镜像"
    echo ""
    echo -e "${BOLD}运维命令:${NC}"
    echo -e "  ${MAGENTA}status${NC}       查看所有容器状态"
    echo -e "  ${MAGENTA}health${NC}       查看服务健康状态面板"
    echo -e "  ${MAGENTA}logs${NC}         查看所有服务日志"
    echo -e "  ${MAGENTA}logs <svc>${NC}   查看指定服务日志 (oj-api|oj-web|mysql|redis|minio)"
    echo -e "  ${MAGENTA}backup${NC}       备份数据目录"
    echo -e "  ${MAGENTA}prune${NC}        清理无用的 Docker 镜像和缓存"
    echo ""
    echo -e "${BOLD}危险命令:${NC}"
    echo -e "  ${RED}clean${NC}        停止并清理所有容器和持久化数据"
    echo -e "  ${RED}reset-db${NC}     重置数据库（删除 MySQL 数据后重新初始化）"
    echo ""
    echo -e "${BOLD}其他:${NC}"
    echo -e "  ${DIM}help${NC}         显示此帮助信息"
    echo -e "  ${DIM}version${NC}      显示版本信息"
    echo ""
    echo -e "${BOLD}示例:${NC}"
    echo -e "  $0              ${DIM}# 一键全栈部署（仅需 Docker）${NC}"
    echo -e "  $0 dev          ${DIM}# 启动数据库等基础设施，用于本地开发${NC}"
    echo -e "  $0 logs oj-api  ${DIM}# 查看后端日志${NC}"
    echo -e "  $0 update       ${DIM}# 代码更新后重新部署${NC}"
}

# ============================================================
# 初始化 Docker Compose 命令
# ============================================================
init_compose_cmd() {
    if docker compose version &> /dev/null 2>&1; then
        COMPOSE_CMD="docker compose"
    elif command -v docker-compose &> /dev/null 2>&1; then
        COMPOSE_CMD="docker-compose"
    else
        COMPOSE_CMD="docker compose"
    fi
}

# ============================================================
# 环境检查（仅需 Docker，不依赖宿主机 Java/Maven/Node）
# ============================================================
check_environment() {
    log_step ">>> [1/4] 检查运行环境"

    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装"
        log_dim "安装指南: https://docs.docker.com/get-docker/"
        exit 1
    fi
    log_info "Docker: $(docker --version | awk '{print $3}' | tr -d ',')"

    # 检查 Docker Compose
    if ! docker compose version &> /dev/null 2>&1 && ! command -v docker-compose &> /dev/null 2>&1; then
        log_error "Docker Compose 未安装"
        log_dim "安装指南: https://docs.docker.com/compose/install/"
        exit 1
    fi
    log_info "Docker Compose: $($COMPOSE_CMD version --short 2>/dev/null || $COMPOSE_CMD version)"

    # 检查 Docker 是否运行
    if ! docker info &> /dev/null 2>&1; then
        log_error "Docker 未启动，请先启动 Docker Desktop 或 Docker 服务"
        exit 1
    fi
    log_info "Docker 服务运行中"

    # 检查磁盘空间（至少需要 5GB）
    check_disk_space

    # 检查端口占用
    check_ports
}

# ─── 磁盘空间检查 ────────────────────────────────────────────
check_disk_space() {
    local required_gb=5
    local available_gb

    if [[ "$OSTYPE" == "darwin"* ]]; then
        available_gb=$(df -g "$SCRIPT_DIR" | awk 'NR==2 {print $4}')
    else
        available_gb=$(df -BG "$SCRIPT_DIR" | awk 'NR==2 {print $4}' | tr -d 'G')
    fi

    if [ -n "$available_gb" ] && [ "$available_gb" -lt "$required_gb" ] 2>/dev/null; then
        log_warn "磁盘可用空间仅 ${available_gb}GB，建议至少 ${required_gb}GB"
    else
        log_info "磁盘空间: ${available_gb:-未知}GB 可用"
    fi
}

# ─── 端口检查 ────────────────────────────────────────────────
_port_conflict=false

_check_port() {
    local port="$1"
    local name="$2"
    if lsof -i ":${port}" -sTCP:LISTEN >/dev/null 2>&1; then
        local pname
        pname=$(lsof -t -i ":${port}" -sTCP:LISTEN 2>/dev/null | head -1 | xargs -I{} ps -p {} -o comm= 2>/dev/null || true)
        case "${pname}" in
            *docker*|*com.docke*) ;;
            *) log_warn "端口 ${port} (${name}) 已被占用 (进程: ${pname:-unknown})"; _port_conflict=true ;;
        esac
    fi
}

check_ports() {
    load_env

    _port_conflict=false

    _check_port "${WEB_PORT:-80}" "前端 Web"
    _check_port "${APP_PORT:-7101}" "后端 API"
    _check_port "${MYSQL_PORT:-3306}" "MySQL"
    _check_port "${REDIS_PORT:-6379}" "Redis"
    _check_port "${MINIO_API_PORT:-9000}" "MinIO API"
    _check_port "${MINIO_CONSOLE_PORT:-9001}" "MinIO Console"

    if [ "$_port_conflict" = true ]; then
        log_warn "存在端口冲突，可在 .env 中修改端口配置"
    else
        log_info "端口检查通过"
    fi
}

# ============================================================
# 生成 .env
# ============================================================
setup_env() {
    log_step ">>> [2/4] 生成环境配置"

    cd "$SCRIPT_DIR"

    if [ ! -f .env ]; then
        if [ ! -f .env.example ]; then
            log_error "缺少 .env.example 模板文件"
            exit 1
        fi

        cp .env.example .env

        # 自动生成随机 JWT 密钥
        JWT_KEY=$(openssl rand -base64 32 2>/dev/null || cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s|your_secret_key_please_change_me|${JWT_KEY}|g" .env
        else
            sed -i "s|your_secret_key_please_change_me|${JWT_KEY}|g" .env
        fi

        log_info ".env 已生成（JWT 密钥已自动随机生成）"
    else
        log_warn ".env 已存在，跳过生成（如需重新生成请先删除 .env）"
    fi

    # 创建数据目录
    load_env
    mkdir -p "${DATA_DIR:-./data}"/{mysql,redis,minio}
    log_info "数据目录已就绪: ${DATA_DIR:-./data}/"
}

# ============================================================
# 构建并启动全栈服务（Maven 编译在 Docker 内完成）
# ============================================================
start_services() {
    log_step ">>> [3/4] 构建镜像并启动服务（Docker 多阶段构建）"
    log_dim "后端 Maven 编译 + 前端 pnpm 构建均在 Docker 内自动完成"
    log_dim "首次构建较慢（需下载依赖），后续构建会利用缓存加速"

    cd "$SCRIPT_DIR"

    # 启用 BuildKit 以支持缓存挂载
    export DOCKER_BUILDKIT=1
    export COMPOSE_DOCKER_CLI_BUILD=1

    local start_time=$(date +%s)
    $COMPOSE_CMD up -d --build 2>&1 | while IFS= read -r line; do
        echo -e "  ${DIM}${line}${NC}"
    done

    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    log_info "所有容器已启动 (构建耗时 ${duration}s)"
}

# ============================================================
# 等待服务就绪
# ============================================================
wait_for_services() {
    log_step ">>> [4/4] 等待服务就绪"

    load_env
    echo ""
    log_info "等待服务启动中（约 30-90 秒）..."
    echo ""

    local all_ready=true

    # 等待 MySQL
    echo -n "  MySQL     (:${MYSQL_PORT:-3306})   "
    local mysql_ready=false
    for i in $(seq 1 30); do
        if docker exec oj-mysql mysqladmin ping -h localhost -u root -p"${MYSQL_ROOT_PASSWORD:-glowxq123456}" --silent 2>/dev/null; then
            echo -e "${GREEN}● 就绪${NC}"
            mysql_ready=true
            break
        fi
        echo -n "."
        sleep 2
    done
    if [ "$mysql_ready" = false ]; then
        echo -e "${RED}● 超时${NC}"
        all_ready=false
    fi

    # 等待 Redis
    echo -n "  Redis     (:${REDIS_PORT:-6379})   "
    local redis_ready=false
    for i in $(seq 1 15); do
        if docker exec oj-redis redis-cli -a "${REDIS_PASSWORD:-glowxq123456}" ping 2>/dev/null | grep -q PONG; then
            echo -e "${GREEN}● 就绪${NC}"
            redis_ready=true
            break
        fi
        echo -n "."
        sleep 2
    done
    if [ "$redis_ready" = false ]; then
        echo -e "${RED}● 超时${NC}"
        all_ready=false
    fi

    # 等待 MinIO
    echo -n "  MinIO     (:${MINIO_API_PORT:-9000})   "
    local minio_ready=false
    for i in $(seq 1 15); do
        if curl -sf http://localhost:${MINIO_API_PORT:-9000}/minio/health/live &>/dev/null; then
            echo -e "${GREEN}● 就绪${NC}"
            minio_ready=true
            break
        fi
        echo -n "."
        sleep 2
    done
    if [ "$minio_ready" = false ]; then
        echo -e "${RED}● 超时${NC}"
        all_ready=false
    fi

    # 等待后端 API
    echo -n "  OJ API    (:${APP_PORT:-7101})   "
    local api_ready=false
    for i in $(seq 1 45); do
        if curl -sf http://localhost:${APP_PORT:-7101}/api/doc.html &>/dev/null; then
            echo -e "${GREEN}● 就绪${NC}"
            api_ready=true
            break
        fi
        echo -n "."
        sleep 3
    done
    if [ "$api_ready" = false ]; then
        echo -e "${YELLOW}● 仍在启动中（可查看日志: $0 logs oj-api）${NC}"
        all_ready=false
    fi

    # 等待前端
    echo -n "  OJ Web    (:${WEB_PORT:-80})     "
    local web_ready=false
    for i in $(seq 1 15); do
        if curl -sf http://localhost:${WEB_PORT:-80}/ &>/dev/null; then
            echo -e "${GREEN}● 就绪${NC}"
            web_ready=true
            break
        fi
        echo -n "."
        sleep 2
    done
    if [ "$web_ready" = false ]; then
        echo -e "${YELLOW}● 仍在启动中${NC}"
        all_ready=false
    fi

    echo ""
    if [ "$all_ready" = false ]; then
        log_warn "部分服务尚未完全就绪，可稍后用 $0 health 查看状态"
    fi
}

# ============================================================
# 显示部署结果
# ============================================================
show_result() {
    load_env

    echo -e "${GREEN}"
    echo "  ╔══════════════════════════════════════════════╗"
    echo "  ║       GlowXQ OJ 部署完成！                   ║"
    echo "  ╚══════════════════════════════════════════════╝"
    echo -e "${NC}"
    echo -e "  ${BOLD}服务访问地址:${NC}"
    echo -e "  ┌──────────────────────────────────────────────┐"
    echo -e "  │  前端页面       http://localhost:${WEB_PORT:-80}"
    echo -e "  │  后端 API       http://localhost:${APP_PORT:-7101}"
    echo -e "  │  API 文档       http://localhost:${APP_PORT:-7101}/doc.html"
    echo -e "  │  MinIO 控制台   http://localhost:${MINIO_CONSOLE_PORT:-9001}"
    echo -e "  └──────────────────────────────────────────────┘"
    echo ""
    echo -e "  ${BOLD}常用命令:${NC}"
    echo -e "  ┌──────────────────────────────────────────────┐"
    echo -e "  │  查看状态  ${CYAN}$0 status${NC}"
    echo -e "  │  健康检查  ${CYAN}$0 health${NC}"
    echo -e "  │  查看日志  ${CYAN}$0 logs${NC}"
    echo -e "  │  后端日志  ${CYAN}$0 logs oj-api${NC}"
    echo -e "  │  前端日志  ${CYAN}$0 logs oj-web${NC}"
    echo -e "  │  停止服务  ${CYAN}$0 stop${NC}"
    echo -e "  │  重启服务  ${CYAN}$0 restart${NC}"
    echo -e "  │  代码更新  ${CYAN}$0 update${NC}"
    echo -e "  └──────────────────────────────────────────────┘"
    echo ""
}

# ============================================================
# 健康状态面板
# ============================================================
cmd_health() {
    cd "$SCRIPT_DIR"
    load_env

    echo ""
    echo -e "${BOLD}  GlowXQ OJ 服务健康状态${NC}"
    echo -e "  ─────────────────────────────────────────"

    # 检查各服务
    local services=("oj-mysql:MySQL:${MYSQL_PORT:-3306}" "oj-redis:Redis:${REDIS_PORT:-6379}" "oj-minio:MinIO:${MINIO_API_PORT:-9000}" "oj-api:OJ API:${APP_PORT:-7101}" "oj-web:OJ Web:${WEB_PORT:-80}")

    for entry in "${services[@]}"; do
        local container="${entry%%:*}"
        local rest="${entry#*:}"
        local name="${rest%%:*}"
        local port="${rest#*:}"

        local status=$(docker inspect --format='{{.State.Status}}' "$container" 2>/dev/null || echo "not found")
        local health=$(docker inspect --format='{{.State.Health.Status}}' "$container" 2>/dev/null || echo "")

        local icon="${RED}●${NC}"
        local status_text="$status"

        if [ "$status" = "running" ]; then
            if [ "$health" = "healthy" ] || [ -z "$health" ]; then
                icon="${GREEN}●${NC}"
                status_text="运行中"
            elif [ "$health" = "starting" ]; then
                icon="${YELLOW}●${NC}"
                status_text="启动中"
            else
                icon="${RED}●${NC}"
                status_text="不健康"
            fi
        elif [ "$status" = "not found" ]; then
            status_text="未启动"
        fi

        printf "  %b %-12s :%s  %s\n" "$icon" "$name" "$port" "$status_text"
    done

    echo -e "  ─────────────────────────────────────────"

    # 资源使用概况
    echo ""
    echo -e "${BOLD}  资源使用:${NC}"
    docker stats --no-stream --format "  {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}" $(docker ps -q --filter "name=oj-") 2>/dev/null || echo -e "  ${DIM}(无运行中的容器)${NC}"
    echo ""
}

# ============================================================
# 命令实现
# ============================================================

# 一键全栈部署
cmd_deploy() {
    show_banner
    check_environment
    setup_env
    start_services
    wait_for_services
    show_result
}

# 启动服务（不重新构建）
cmd_start() {
    cd "$SCRIPT_DIR"
    log_info "启动所有服务..."
    $COMPOSE_CMD up -d
    log_info "服务已启动，使用 $0 health 查看状态"
}

# 停止服务
cmd_stop() {
    cd "$SCRIPT_DIR"
    log_info "停止所有服务..."
    $COMPOSE_CMD down
    log_info "服务已停止"
}

# 重启服务
cmd_restart() {
    cd "$SCRIPT_DIR"
    log_info "重启所有服务..."
    $COMPOSE_CMD restart
    log_info "服务已重启"
}

# 更新部署（代码更新后使用）
cmd_update() {
    show_banner
    log_step ">>> 更新部署"

    cd "$SCRIPT_DIR"

    # 启用 BuildKit
    export DOCKER_BUILDKIT=1
    export COMPOSE_DOCKER_CLI_BUILD=1

    log_info "重新构建 Docker 镜像并启动..."
    $COMPOSE_CMD up -d --build

    # 清理悬空镜像
    log_info "清理旧镜像..."
    docker image prune -f --filter "dangling=true" 2>/dev/null || true

    wait_for_services
    show_result
}

# 开发模式：仅启动基础设施
cmd_dev() {
    show_banner
    log_step ">>> 启动开发模式（仅基础设施）"

    cd "$SCRIPT_DIR"

    # 确保 .env 存在
    if [ ! -f .env ]; then
        setup_env
    fi

    load_env

    # 创建数据目录
    mkdir -p "${DATA_DIR:-./data}"/{mysql,redis,minio}

    $COMPOSE_CMD up -d mysql redis minio minio-init

    echo ""
    log_info "基础设施已启动"
    echo ""
    echo -e "  ${BOLD}基础设施服务:${NC}"
    echo -e "  ┌──────────────────────────────────────────────┐"
    echo -e "  │  MySQL          localhost:${MYSQL_PORT:-3306}"
    echo -e "  │  Redis          localhost:${REDIS_PORT:-6379}"
    echo -e "  │  MinIO API      localhost:${MINIO_API_PORT:-9000}"
    echo -e "  │  MinIO Console  localhost:${MINIO_CONSOLE_PORT:-9001}"
    echo -e "  └──────────────────────────────────────────────┘"
    echo ""
    echo -e "  ${BOLD}本地开发启动:${NC}"
    echo -e "  ┌──────────────────────────────────────────────┐"
    echo -e "  │  ${CYAN}后端:${NC} cd glowxq-api && mvn spring-boot:run -pl app/app-oj"
    echo -e "  │  ${CYAN}前端:${NC} cd oj-web && pnpm install && pnpm dev"
    echo -e "  └──────────────────────────────────────────────┘"
    echo ""
}

# 停止开发模式基础设施
cmd_dev_stop() {
    cd "$SCRIPT_DIR"
    log_info "停止基础设施服务..."
    $COMPOSE_CMD stop mysql redis minio
    $COMPOSE_CMD rm -f minio-init 2>/dev/null || true
    log_info "基础设施已停止"
}

# 查看状态
cmd_status() {
    cd "$SCRIPT_DIR"
    echo -e "\n${BOLD}容器状态:${NC}"
    $COMPOSE_CMD ps -a
    echo ""
}

# 查看日志
cmd_logs() {
    cd "$SCRIPT_DIR"
    local service="${1:-}"
    if [ -n "$service" ]; then
        $COMPOSE_CMD logs -f --tail=200 "$service"
    else
        $COMPOSE_CMD logs -f --tail=100
    fi
}

# 数据备份
cmd_backup() {
    cd "$SCRIPT_DIR"
    load_env

    local data_dir="${DATA_DIR:-./data}"
    local backup_dir="./backups"
    local timestamp=$(date '+%Y%m%d_%H%M%S')
    local backup_file="${backup_dir}/glowxq-oj-backup-${timestamp}.tar.gz"

    if [ ! -d "$data_dir" ]; then
        log_error "数据目录不存在: $data_dir"
        exit 1
    fi

    mkdir -p "$backup_dir"

    log_info "开始备份数据..."
    log_dim "数据目录: $data_dir"
    log_dim "备份文件: $backup_file"

    # MySQL dump（如果容器在运行）
    if docker ps --format '{{.Names}}' | grep -q "oj-mysql"; then
        log_info "导出 MySQL 数据库..."
        docker exec oj-mysql mysqldump -u root -p"${MYSQL_ROOT_PASSWORD:-glowxq123456}" \
            --all-databases --single-transaction \
            > "${backup_dir}/mysql-dump-${timestamp}.sql" 2>/dev/null
        log_info "MySQL 导出完成"
    fi

    # 打包数据目录
    tar -czf "$backup_file" -C "$(dirname "$data_dir")" "$(basename "$data_dir")" 2>/dev/null

    local size=$(du -sh "$backup_file" | awk '{print $1}')
    log_info "备份完成: $backup_file ($size)"
}

# 仅构建后端镜像
cmd_build_api() {
    show_banner
    cd "$SCRIPT_DIR"

    export DOCKER_BUILDKIT=1
    export COMPOSE_DOCKER_CLI_BUILD=1

    log_info "构建后端 Docker 镜像（Maven 编译在容器内完成）..."
    $COMPOSE_CMD build oj-api
    log_info "后端镜像构建完成"
}

# 仅构建前端镜像
cmd_build_web() {
    show_banner
    cd "$SCRIPT_DIR"
    log_info "构建前端 Docker 镜像..."
    $COMPOSE_CMD build oj-web
    log_info "前端镜像构建完成"
}

# 清理无用镜像
cmd_prune() {
    log_info "清理无用的 Docker 镜像和缓存..."
    docker image prune -f --filter "dangling=true"
    docker builder prune -f --filter "until=24h" 2>/dev/null || true
    log_info "清理完成"

    echo ""
    echo -e "${BOLD}  当前磁盘使用:${NC}"
    docker system df
    echo ""
}

# 清理所有数据
cmd_clean() {
    cd "$SCRIPT_DIR"
    load_env

    echo ""
    echo -e "  ${RED}${BOLD}╔═══════════════════════════════════════════╗${NC}"
    echo -e "  ${RED}${BOLD}║  警告：此操作将停止所有容器并删除数据！   ║${NC}"
    echo -e "  ${RED}${BOLD}╚═══════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "  将删除以下内容:"
    echo -e "    ${RED}•${NC} 所有 Docker 容器和网络"
    echo -e "    ${RED}•${NC} MySQL 数据库数据"
    echo -e "    ${RED}•${NC} Redis 缓存数据"
    echo -e "    ${RED}•${NC} MinIO 文件存储"
    echo ""
    read -p "  确认删除所有数据？(输入 YES 确认): " confirm
    if [ "$confirm" = "YES" ]; then
        $COMPOSE_CMD down -v 2>/dev/null || true
        rm -rf "${DATA_DIR:-./data}"
        log_info "已清理所有容器和数据"
    else
        log_info "已取消操作"
    fi
}

# 重置数据库
cmd_reset_db() {
    cd "$SCRIPT_DIR"
    load_env

    echo ""
    echo -e "  ${RED}${BOLD}警告：此操作将重置数据库，所有数据将丢失！${NC}"
    echo ""
    read -p "  确认重置数据库？(输入 YES 确认): " confirm
    if [ "$confirm" = "YES" ]; then
        log_info "停止 MySQL 容器..."
        $COMPOSE_CMD stop mysql
        $COMPOSE_CMD rm -f mysql

        log_info "删除 MySQL 数据..."
        rm -rf "${DATA_DIR:-./data}/mysql"
        mkdir -p "${DATA_DIR:-./data}/mysql"

        log_info "重新启动 MySQL（将自动执行 init.sql）..."
        $COMPOSE_CMD up -d mysql

        echo -n "  等待 MySQL 初始化"
        for i in $(seq 1 30); do
            if docker exec oj-mysql mysqladmin ping -h localhost -u root -p"${MYSQL_ROOT_PASSWORD:-glowxq123456}" --silent 2>/dev/null; then
                echo -e " ${GREEN}完成${NC}"
                break
            fi
            echo -n "."
            sleep 2
        done

        log_info "数据库已重置"
    else
        log_info "已取消操作"
    fi
}

# ============================================================
# 主入口
# ============================================================
init_compose_cmd

case "${1:-deploy}" in
    deploy)         cmd_deploy ;;
    start)          cmd_start ;;
    stop)           cmd_stop ;;
    restart)        cmd_restart ;;
    update)         cmd_update ;;
    dev)            cmd_dev ;;
    dev-stop)       cmd_dev_stop ;;
    status)         cmd_status ;;
    health)         cmd_health ;;
    logs)           shift; cmd_logs "$@" ;;
    backup)         cmd_backup ;;
    build-api)      cmd_build_api ;;
    build-web)      cmd_build_web ;;
    prune)          cmd_prune ;;
    clean)          cmd_clean ;;
    reset-db)       cmd_reset_db ;;
    version|-v)     echo "GlowXQ OJ Deploy Script v${VERSION}" ;;
    help|-h|--help) show_usage ;;
    *)
        log_error "未知命令: $1"
        echo ""
        show_usage
        exit 1
        ;;
esac
