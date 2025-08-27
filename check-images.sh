#!/bin/bash

# 检查README.md中引用的所有图片文件是否存在

echo "🔍 检查README.md中的图片链接..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 计数器
total=0
missing=0
found=0

# 检查函数
check_image() {
    local image_path=$1
    total=$((total + 1))
    
    if [ -f "$image_path" ]; then
        echo -e "${GREEN}✅ $image_path${NC}"
        found=$((found + 1))
    else
        echo -e "${RED}❌ $image_path (文件不存在)${NC}"
        missing=$((missing + 1))
    fi
}

echo ""
echo "📁 检查Logo和品牌资源..."
check_image "docs/images/logo.svg"
check_image "docs/images/logo-icon.svg"

echo ""
echo "📞 检查联系方式图片..."
check_image "docs/images/contact-qr.jpg"
check_image "docs/images/group-wechat.png"

echo ""
echo "🎬 检查动画文件..."
check_image "docs/gif/glowc.gif"
check_image "docs/gif/code-push.gif"

echo ""
echo "📸 检查系统截图..."

# 检查主界面截图
echo "🏠 主界面截图..."
for i in {0..2}; do
    if [ $i -eq 0 ]; then
        check_image "docs/images/img.png"
    else
        check_image "docs/images/img_$i.png"
    fi
done

# 检查题目管理截图
echo "📝 题目管理截图..."
for i in {3..7}; do
    check_image "docs/images/img_$i.png"
done

# 检查比赛与作业截图
echo "🏆 比赛与作业截图..."
for i in {8..11}; do
    check_image "docs/images/img_$i.png"
done

# 检查测评结果截图
echo "⚡ 测评结果截图..."
for i in {12..13}; do
    check_image "docs/images/img_$i.png"
done

# 检查用户管理截图
echo "👥 用户管理截图..."
for i in {14..16}; do
    check_image "docs/images/img_$i.png"
done

# 检查GlowC功能截图
echo "🎨 GlowC功能截图..."
for i in {17..19}; do
    check_image "docs/images/img_$i.png"
done

# 检查权限管理截图
echo "🔐 权限管理截图..."
for i in {20..21}; do
    check_image "docs/images/img_$i.png"
done

# 检查系统管理截图
echo "⚙️ 系统管理截图..."
for i in {22..23}; do
    check_image "docs/images/img_$i.png"
done

# 检查班级管理截图
echo "🏫 班级管理截图..."
for i in {24..25}; do
    check_image "docs/images/img_$i.png"
done

# 检查代码监控推送截图
echo "📡 代码监控推送截图..."
check_image "docs/images/img_26.png"

# 检查其他截图
echo ""
echo "📊 其他截图..."
check_image "docs/images/dashboard.png"
check_image "docs/images/problem-list.png"

# 显示统计结果
echo ""
echo "📊 检查结果统计:"
echo "=================="
echo -e "总计: $total 个文件"
echo -e "${GREEN}存在: $found 个文件${NC}"
if [ $missing -gt 0 ]; then
    echo -e "${RED}缺失: $missing 个文件${NC}"
    echo ""
    echo -e "${YELLOW}⚠️  有图片文件缺失，请检查并补充相应的截图文件${NC}"
    exit 1
else
    echo -e "${GREEN}✅ 所有图片文件都存在！${NC}"
    echo ""
    echo "🎉 README.md中的所有图片链接都有效！"
    exit 0
fi
