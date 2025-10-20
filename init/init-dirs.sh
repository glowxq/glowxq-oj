#!/bin/bash

# GlowXQ OJ 数据目录初始化脚本
# 用于创建 Docker 容器所需的宿主机目录

echo "=========================================="
echo "  GlowXQ OJ 数据目录初始化"
echo "=========================================="

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DATA_DIR="${SCRIPT_DIR}/data"

echo "数据目录: ${DATA_DIR}"
echo ""

# 创建所需的目录
echo "正在创建目录..."

mkdir -p "${DATA_DIR}/testcase"
mkdir -p "${DATA_DIR}/file"
mkdir -p "${DATA_DIR}/log"
mkdir -p "${DATA_DIR}/run"
mkdir -p "${DATA_DIR}/spj"
mkdir -p "${DATA_DIR}/interactive"

echo "✓ 测试用例目录: ${DATA_DIR}/testcase"
echo "✓ 文件存储目录: ${DATA_DIR}/file"
echo "✓ 日志目录: ${DATA_DIR}/log"
echo "✓ 运行目录: ${DATA_DIR}/run"
echo "✓ SPJ目录: ${DATA_DIR}/spj"
echo "✓ 交互式目录: ${DATA_DIR}/interactive"

# 设置目录权限（确保容器可以读写）
echo ""
echo "正在设置目录权限..."
chmod -R 777 "${DATA_DIR}"
echo "✓ 权限设置完成"

echo ""
echo "=========================================="
echo "  初始化完成！"
echo "=========================================="
echo ""
echo "提示："
echo "1. 数据目录已创建在: ${DATA_DIR}"
echo "2. 现在可以运行: docker-compose up -d"
echo "3. 测试用例文件将保存在: ${DATA_DIR}/testcase"
echo "4. 日志文件将保存在: ${DATA_DIR}/log"
echo ""

