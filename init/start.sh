#!/bin/bash

# GlowXQ OJ 系统一键部署脚本（Host网络模式）
# 作者: GlowXQ Team
# 版本: 1.1.0 - 支持Host网络模式

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker 和 Docker Compose
check_docker() {
    log_info "检查 Docker 环境..."

    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi

    log_success "Docker 环境检查通过"
}

# 检查必要文件
check_files() {
    log_info "检查必要文件..."

    if [ ! -f "docker-compose.yml" ]; then
        log_error "docker-compose.yml 文件不存在"
        exit 1
    fi

    if [ ! -f "init.sql" ]; then
        log_error "init.sql 文件不存在"
        exit 1
    fi

    if [ ! -f ".env" ]; then
        log_warning ".env 文件不存在，将使用默认配置"
    fi

    log_success "文件检查完成"
}

# 创建必要目录
create_directories() {
    log_info "创建必要目录..."

    # 创建日志目录
    mkdir -p logs/mysql
    mkdir -p logs/redis
    mkdir -p logs/oj

    log_success "目录创建完成"
}

# 停止现有服务
stop_services() {
    log_info "停止现有服务..."

    if docker-compose ps -q 2>/dev/null | grep -q .; then
        docker-compose down
        log_success "现有服务已停止"
    else
        log_info "没有运行中的服务"
    fi
}

# 检查并清理数据卷（如果需要重新初始化）
check_and_clean_volumes() {
    log_info "检查数据库初始化状态..."

    # 检查 MySQL 数据卷是否存在
    if docker volume ls | grep -q "glowxq-mysql-data"; then
        log_warning "检测到现有的 MySQL 数据卷"

        # 启动一个临时的 MySQL 容器来检查数据
        log_info "检查现有数据库内容..."

        # 临时启动 MySQL 服务检查
        docker-compose up -d mysql

        # 等待 MySQL 启动
        timeout=60
        while [ $timeout -gt 0 ]; do
            if docker-compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
                break
            fi
            sleep 2
            timeout=$((timeout-2))
        done

        if [ $timeout -gt 0 ]; then
            # 检查表数量
            table_count=$(docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD:-glowxq-oj-123} -D glowxq_oj -e "SHOW TABLES;" 2>/dev/null | wc -l)

            if [ "$table_count" -lt 5 ]; then
                log_warning "数据库表不完整，将重新初始化"
                docker-compose down
                log_info "删除现有 MySQL 数据卷..."
                docker volume rm glowxq-mysql-data 2>/dev/null || true
                log_success "数据卷已清理，将进行全新初始化"
            else
                log_success "数据库已包含 $((table_count-1)) 个表，保留现有数据"
                docker-compose down
            fi
        else
            log_warning "无法连接到现有数据库，将重新初始化"
            docker-compose down
            docker volume rm glowxq-mysql-data 2>/dev/null || true
        fi
    else
        log_info "未检测到现有数据卷，将进行全新初始化"
    fi
}

# 启动服务
start_services() {
    log_info "启动 GlowXQ OJ 系统..."

    # 拉取最新镜像
    log_info "拉取最新镜像..."
    docker-compose pull

    # 启动服务
    log_info "启动服务容器..."
    docker-compose up -d

    log_success "服务启动完成"
}

# 等待服务就绪
wait_for_services() {
    log_info "等待服务就绪..."

    # 等待 MySQL 就绪
    log_info "等待 MySQL 服务就绪..."
    timeout=300
    while [ $timeout -gt 0 ]; do
        if docker-compose exec -T mysql mysqladmin ping -h localhost --silent; then
            log_success "MySQL 服务就绪"
            break
        fi
        sleep 5
        timeout=$((timeout-5))
    done

    if [ $timeout -le 0 ]; then
        log_error "MySQL 服务启动超时"
        exit 1
    fi

    # 等待 Redis 就绪
    log_info "等待 Redis 服务就绪..."
    timeout=60
    while [ $timeout -gt 0 ]; do
        if docker-compose exec -T redis redis-cli -a ${REDIS_PASSWORD:-glowxq-oj-123} ping 2>/dev/null | grep -q PONG; then
            log_success "Redis 服务就绪"
            break
        fi
        sleep 2
        timeout=$((timeout-2))
    done

    if [ $timeout -le 0 ]; then
        log_error "Redis 服务启动超时"
        exit 1
    fi
}

# 初始化数据库
initialize_database() {
    log_info "检查数据库初始化状态..."

    # 检查数据库是否存在
    db_exists=$(docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD:-glowxq-oj-123} -e "SHOW DATABASES LIKE 'glowxq_oj';" 2>/dev/null | grep -c "glowxq_oj")

    if [ "$db_exists" -eq 0 ]; then
        log_error "数据库 glowxq_oj 不存在"
        exit 1
    fi

    log_success "数据库 glowxq_oj 已存在"

    # 检查关键表是否存在
    table_count=$(docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD:-glowxq-oj-123} -D glowxq_oj -e "SHOW TABLES;" 2>/dev/null | wc -l)

    if [ "$table_count" -lt 5 ]; then
        log_warning "数据库表数量不足，开始执行数据初始化..."

        # 检查 init.sql 文件是否存在
        if [ ! -f "init.sql" ]; then
            log_error "init.sql 文件不存在"
            exit 1
        fi

        # 执行数据库初始化
        log_info "正在执行数据库初始化脚本..."
        if docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD:-glowxq-oj-123} < init.sql; then
            log_success "数据库初始化完成"
        else
            log_error "数据库初始化失败"
            exit 1
        fi
    else
        log_success "数据库已包含 $((table_count-1)) 个表，无需重新初始化"
    fi

    # 验证关键表
    log_info "验证关键数据表..."
    tables=("sys_user" "applet_user" "code_monitor" "course" "judge" "problem")
    missing_tables=()

    for table in "${tables[@]}"; do
        table_exists=$(docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD:-glowxq-oj-123} -D glowxq_oj -e "SHOW TABLES LIKE '$table';" 2>/dev/null | grep -c "$table")
        if [ "$table_exists" -eq 0 ]; then
            missing_tables+=("$table")
        fi
    done

    if [ ${#missing_tables[@]} -eq 0 ]; then
        log_success "所有关键表验证通过"
    else
        log_warning "缺失的表: ${missing_tables[*]}"
        log_info "尝试重新执行初始化脚本..."
        docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD:-glowxq-oj-123} < init.sql
    fi
}

# 等待 OJ 应用就绪
wait_for_application() {
    log_info "等待 OJ 应用服务就绪..."
    timeout=180
    while [ $timeout -gt 0 ]; do
        if curl -f http://localhost:7101/health &>/dev/null; then
            log_success "OJ 应用服务就绪"
            return 0
        fi
        sleep 10
        timeout=$((timeout-10))
    done

    log_warning "OJ 应用服务启动可能需要更多时间，请稍后检查"
    return 1
}

# 显示服务状态
show_status() {
    log_info "服务状态："
    docker-compose ps

    echo ""
    log_info "服务访问地址："
    echo "  - OJ 系统: http://localhost:7101"
    echo "  - MySQL: localhost:3307"
    echo "  - Redis: localhost:6380"

    echo ""
    log_info "默认账号信息："
    echo "  - MySQL root 密码: glowxq-oj-123"
    echo "  - Redis 密码: glowxq-oj-123"
}

# 主函数
main() {
    echo "========================================"
    echo "    GlowXQ OJ 系统一键部署脚本"
    echo "========================================"
    echo ""

    check_docker
    check_files
    create_directories
    stop_services
    check_and_clean_volumes
    start_services
    wait_for_services
    initialize_database
    wait_for_application
    show_status

    echo ""
    log_success "GlowXQ OJ 系统部署完成！"
    echo ""
    log_info "如需停止服务，请运行: docker-compose down"
    log_info "如需查看日志，请运行: docker-compose logs -f [服务名]"
    log_info "更多信息请查看 init.md 文档"
}

# 执行主函数
main "$@"
