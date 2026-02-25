#!/bin/bash
set -e

# ============================================================
# GlowXQ OJ - All-in-One 容器启动入口
# 1. 自动推导 OSS_DOMAIN（使文件 URL 在生产环境可访问）
# 2. 解除栈大小限制（判题需要）
# 3. 初始化 MinIO bucket（首次启动）
# 4. 启动 supervisord（托管所有进程）
# ============================================================

echo "=========================================="
echo "  GlowXQ OJ All-in-One Container Starting"
echo "=========================================="

# ─── 自动推导 OSS_DOMAIN ───
# 文件通过 Nginx /nexus-oj/ 代理访问 MinIO
# OSS_DOMAIN 决定了后端返回给浏览器的文件 URL 前缀
# 优先级: OSS_DOMAIN(显式设置) > SITE_URL > 空(回退到 OSS_ENDPOINT)
if [ -n "$OSS_DOMAIN" ]; then
    echo "[init] OSS_DOMAIN explicitly set: $OSS_DOMAIN"
elif [ -n "$SITE_URL" ]; then
    export OSS_DOMAIN="${SITE_URL%/}"
    echo "[init] OSS_DOMAIN auto-set from SITE_URL: $OSS_DOMAIN"
else
    # 未配置时，使用空字符串让后端回退到 OSS_ENDPOINT
    # 但 OSS_ENDPOINT 是容器内部地址，外部不可达
    # 因此改为指向容器自身 Nginx 代理（文件通过 /nexus-oj/ 路径访问）
    export OSS_DOMAIN="http://127.0.0.1"
    echo "[init] WARNING: SITE_URL not set, OSS_DOMAIN defaulting to http://127.0.0.1"
    echo "[init] For production, set SITE_URL to your public URL (e.g., https://oj.example.com)"
fi

# 解除系统栈大小限制（防止判题时栈溢出）
ulimit -s unlimited 2>/dev/null || true

# ─── 后台初始化 MinIO bucket ───
init_minio_bucket() {
    echo "[init] Waiting for MinIO to be ready..."
    # 等待 MinIO 启动（最多 60 秒）
    for i in $(seq 1 30); do
        if curl -sf http://127.0.0.1:9000/minio/health/live &>/dev/null; then
            echo "[init] MinIO is ready!"

            # 配置 mc alias
            mc alias set local http://127.0.0.1:9000 "${OSS_ACCESS_KEY}" "${OSS_SECRET_KEY}" --api s3v4 2>/dev/null

            # 创建 bucket（如不存在）
            mc mb --ignore-existing local/nexus-oj 2>/dev/null && \
                echo "[init] Bucket 'nexus-oj' is ready"

            # 设置公开读（用于头像、图片等静态资源访问）
            mc anonymous set download local/nexus-oj 2>/dev/null && \
                echo "[init] Bucket policy set to public download"

            return 0
        fi
        sleep 2
    done
    echo "[init] WARNING: MinIO not ready after 60s, bucket init skipped"
    return 1
}

# 后台执行 MinIO 初始化（不阻塞主进程）
init_minio_bucket &

# ─── 启动 supervisord ───
echo "[main] Starting supervisord..."
exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
