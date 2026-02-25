#!/bin/bash
set -e  # 遇到错误立即退出

# 配置参数（修改这里即可适配新环境）
REG="${DOCKER_REGISTRY:-registry.cn-guangzhou.aliyuncs.com}"  # 镜像仓库地址
COMPOSE="/mnt/data/my/nexus/nexus-oj.yml" # compose文件路径
USER="${DOCKER_USER:-your_username}"                          # 仓库账号
PASS="${DOCKER_PASS:-your_password}"                          # 仓库密码

# 登录到镜像仓库
echo "1. 正在登录镜像仓库..."
echo "$PASS" | docker login --username "$USER" --password-stdin "$REG"

# 清理旧容器
echo "2. 正在清理旧容器..."
docker compose -f "$COMPOSE" down || docker rm -f "$SERVICE"

# 更新镜像
echo "3. 正在拉取最新镜像..."
docker compose -f "$COMPOSE" pull

# 清理无用镜像
echo "4. 正在清理缓存..."
docker image prune -f --filter "dangling=true"

# 启动服务
echo "5. 正在启动服务..."
docker compose -f "$COMPOSE" up -d

# 最终状态
echo -e "\n✅ 服务启动完成！容器列表："
docker ps --filter "name=$SERVICE" --format "table {{.Names}}\t{{.Status}}"
