#!/bin/bash

# GlowXQ OJ Docker镜像构建脚本
# 用于测试修复后的Dockerfile是否能正常构建

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印函数
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker是否可用
check_docker() {
    print_info "检查Docker环境..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker服务未启动，请启动Docker服务"
        exit 1
    fi
    
    print_success "Docker环境检查通过"
}

# 检查必要文件
check_files() {
    print_info "检查必要文件..."
    
    if [ ! -f "app/app-oj/Dockerfile" ]; then
        print_error "Dockerfile不存在: app/app-oj/Dockerfile"
        exit 1
    fi
    
    print_success "Dockerfile文件检查通过"
}

# 构建镜像
build_image() {
    print_info "开始构建Docker镜像..."
    
    # 设置镜像名称和标签
    IMAGE_NAME="glowxq-oj"
    IMAGE_TAG="test-$(date +%Y%m%d-%H%M%S)"
    FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"
    
    print_info "镜像名称: ${FULL_IMAGE_NAME}"
    print_info "构建上下文: $(pwd)"
    
    # 构建镜像
    print_info "执行Docker构建..."
    if docker build -f app/app-oj/Dockerfile -t "${FULL_IMAGE_NAME}" .; then
        print_success "镜像构建成功！"
        print_success "镜像名称: ${FULL_IMAGE_NAME}"
        
        # 显示镜像信息
        print_info "镜像详细信息:"
        docker images "${FULL_IMAGE_NAME}"
        
        # 检查镜像大小
        IMAGE_SIZE=$(docker images --format "table {{.Size}}" "${FULL_IMAGE_NAME}" | tail -n 1)
        print_info "镜像大小: ${IMAGE_SIZE}"
        
        return 0
    else
        print_error "镜像构建失败！"
        return 1
    fi
}

# 测试镜像
test_image() {
    print_info "测试镜像中的Java安装..."
    
    # 测试Java版本
    if docker run --rm "${FULL_IMAGE_NAME}" /usr/local/java/bin/java -version; then
        print_success "Java安装测试通过！"
    else
        print_error "Java安装测试失败！"
        return 1
    fi
}

# 清理测试镜像
cleanup() {
    if [ -n "${FULL_IMAGE_NAME}" ]; then
        print_info "是否删除测试镜像 ${FULL_IMAGE_NAME}? (y/N)"
        read -r response
        if [[ "$response" =~ ^[Yy]$ ]]; then
            docker rmi "${FULL_IMAGE_NAME}"
            print_success "测试镜像已删除"
        else
            print_info "保留测试镜像: ${FULL_IMAGE_NAME}"
        fi
    fi
}

# 主函数
main() {
    echo
    print_success "========================================="
    print_success "GlowXQ OJ Docker镜像构建测试脚本"
    print_success "========================================="
    echo
    
    check_docker
    check_files
    
    if build_image; then
        test_image
        print_success "所有测试通过！Dockerfile修复成功！"
    else
        print_error "构建失败，请检查错误信息"
        exit 1
    fi
    
    echo
    print_info "构建完成！"
    cleanup
}

# 捕获退出信号，确保清理
trap cleanup EXIT

# 执行主函数
main "$@"
