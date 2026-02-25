#!/bin/bash

# ============================================================
# GlowXQ OJ - All-in-One 容器启动入口
# 1. 初始化 MySQL（首次启动时自动建库、导入 init.sql）
# 2. 初始化 Redis 数据目录
# 3. 自动推导 OSS_DOMAIN
# 4. 初始化 MinIO bucket（后台异步）
# 5. 启动 supervisord（托管所有 6 个进程）
# ============================================================

echo "=========================================="
echo "  GlowXQ OJ All-in-One Container Starting"
echo "=========================================="

# ─── 0. 确保持久化数据目录存在 ───
# Docker Volume 挂载会覆盖镜像中的目录，需要在运行时创建子目录
mkdir -p /data/mysql /data/redis /data/minio

# ─── 1. MySQL 初始化 ───
MYSQL_DATA_DIR="/data/mysql"
MYSQL_INIT_MARKER="$MYSQL_DATA_DIR/.oj_initialized"

# 确保 MySQL socket 目录存在
mkdir -p /var/run/mysqld
chown mysql:mysql /var/run/mysqld

# 判断是否需要初始化：检查标记文件（而非仅检查目录）
# 这样即使上次初始化中途失败，也会重试
NEED_MYSQL_INIT=false
if [ ! -d "$MYSQL_DATA_DIR/mysql" ]; then
    NEED_MYSQL_INIT=true
    echo "[init] First run detected, need full MySQL initialization"
elif [ ! -f "$MYSQL_INIT_MARKER" ]; then
    NEED_MYSQL_INIT=true
    echo "[init] Previous initialization incomplete, will retry user/database setup"
fi

if [ "$NEED_MYSQL_INIT" = true ]; then
    echo "[init] Initializing MySQL..."

    # 确保数据目录权限正确
    chown mysql:mysql "$MYSQL_DATA_DIR"

    # 如果数据目录未初始化（没有 mysql 子目录），先执行 mysqld --initialize
    if [ ! -d "$MYSQL_DATA_DIR/mysql" ]; then
        echo "[init] Initializing MySQL data directory..."
        mysqld --initialize-insecure --user=mysql --datadir="$MYSQL_DATA_DIR"
        echo "[init] MySQL data directory initialized"
    fi

    # 临时启动 MySQL（监听 socket + TCP 127.0.0.1，方便调试）
    mysqld --user=mysql \
        --datadir="$MYSQL_DATA_DIR" \
        --socket=/var/run/mysqld/mysqld.sock \
        --bind-address=127.0.0.1 \
        --port=3306 \
        --default-authentication-plugin=mysql_native_password &
    MYSQL_TEMP_PID=$!

    # 等待 MySQL 就绪
    echo "[init] Waiting for MySQL to start..."
    MYSQL_READY=false
    for i in $(seq 1 30); do
        if mysqladmin ping --socket=/var/run/mysqld/mysqld.sock --silent 2>/dev/null; then
            echo "[init] MySQL is ready!"
            MYSQL_READY=true
            break
        fi
        sleep 1
    done

    if [ "$MYSQL_READY" = false ]; then
        echo "[init] ERROR: MySQL failed to start within 30 seconds"
        kill $MYSQL_TEMP_PID 2>/dev/null || true
        exit 1
    fi

    # ── Step 1: 设置 root@localhost 密码 ──
    echo "[init] Setting root@localhost password..."
    mysql --socket=/var/run/mysqld/mysqld.sock -u root -e \
        "ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';"
    echo "[init] root@localhost password set"

    # 后续操作都用密码连接
    MYSQL_CMD="mysql --socket=/var/run/mysqld/mysqld.sock -u root -p${MYSQL_ROOT_PASSWORD}"

    # ── Step 2: 创建数据库 ──
    echo "[init] Creating database '${MYSQL_DATABASE}'..."
    $MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS \`${MYSQL_DATABASE}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo "[init] Database created"

    # ── Step 3: 创建远程用户（允许 TCP 连接） ──
    # Spring Boot 通过 127.0.0.1 TCP 连接，需要 root@'%' 或 root@'127.0.0.1'
    echo "[init] Creating root@'%' user for TCP connections..."
    $MYSQL_CMD -e "
        CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
        GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
        CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
        GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;
        FLUSH PRIVILEGES;
    "
    echo "[init] Remote users created"

    # ── Step 4: 验证用户创建成功 ──
    echo "[init] Verifying users..."
    $MYSQL_CMD -e "SELECT user, host FROM mysql.user WHERE user='root';"

    # ── Step 5: 导入初始化 SQL ──
    if [ -f /docker-entrypoint-initdb.d/init.sql ]; then
        echo "[init] Importing init.sql..."
        $MYSQL_CMD "${MYSQL_DATABASE}" < /docker-entrypoint-initdb.d/init.sql
        echo "[init] Database initialization completed!"
    else
        echo "[init] WARNING: init.sql not found, skipping import"
    fi

    # 停止临时 MySQL
    echo "[init] Shutting down temporary MySQL..."
    mysqladmin --socket=/var/run/mysqld/mysqld.sock -u root -p"${MYSQL_ROOT_PASSWORD}" shutdown
    wait $MYSQL_TEMP_PID 2>/dev/null || true

    # 写入初始化完成标记
    touch "$MYSQL_INIT_MARKER"
    echo "[init] MySQL initialization done! (marker written)"
else
    echo "[init] MySQL already initialized, skipping"
    # 确保数据目录权限正确（volume 可能改变权限）
    chown -R mysql:mysql "$MYSQL_DATA_DIR"
fi

# ─── 2. Redis 数据目录 ───
mkdir -p /data/redis
echo "[init] Redis data directory ready"

# ─── 3. 自动推导 OSS_DOMAIN ───
if [ -n "$OSS_DOMAIN" ]; then
    echo "[init] OSS_DOMAIN explicitly set: $OSS_DOMAIN"
elif [ -n "$SITE_URL" ]; then
    export OSS_DOMAIN="${SITE_URL%/}"
    echo "[init] OSS_DOMAIN auto-set from SITE_URL: $OSS_DOMAIN"
else
    export OSS_DOMAIN="http://127.0.0.1"
    echo "[init] WARNING: SITE_URL not set, OSS_DOMAIN defaulting to http://127.0.0.1"
    echo "[init] For production, set SITE_URL to your public URL (e.g., https://oj.example.com)"
fi

# 解除系统栈大小限制（防止判题时栈溢出）
ulimit -s unlimited 2>/dev/null || true

# ─── 4. 后台初始化 MinIO bucket ───
init_minio_bucket() {
    echo "[init] Waiting for MinIO to be ready..."
    for i in $(seq 1 30); do
        if curl -sf http://127.0.0.1:9000/minio/health/live &>/dev/null; then
            echo "[init] MinIO is ready!"

            mc alias set local http://127.0.0.1:9000 "${OSS_ACCESS_KEY}" "${OSS_SECRET_KEY}" --api s3v4 2>/dev/null

            mc mb --ignore-existing local/nexus-oj 2>/dev/null && \
                echo "[init] Bucket 'nexus-oj' is ready"

            mc anonymous set download local/nexus-oj 2>/dev/null && \
                echo "[init] Bucket policy set to public download"

            return 0
        fi
        sleep 2
    done
    echo "[init] WARNING: MinIO not ready after 60s, bucket init skipped"
    return 1
}

# 后台执行（不阻塞主进程）
init_minio_bucket &

# ─── 5. 启动 supervisord ───
echo "[main] Starting supervisord (MySQL + Redis + Nginx + MinIO + go-judge + Spring Boot)..."
exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
