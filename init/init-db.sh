#!/bin/bash
# 获取脚本所在目录的绝对路径
BASEDIR=$(dirname "$0")

# 检查 MySQL 容器是否可用的函数
wait_for_mysql() {
    local MAX_RETRY=10
    local RETRY_COUNT=0
    until docker exec -i glowxq-mysql mysql -uroot -pglowxq-oj-123 -e "SELECT 1" > /dev/null 2>&1 || [ $RETRY_COUNT -eq $MAX_RETRY ]; do
        echo "MySQL 尚未就绪 - 等待中... ($((RETRY_COUNT+1))/$MAX_RETRY)"
        sleep 8
        RETRY_COUNT=$((RETRY_COUNT+1))
    done
    if [ $RETRY_COUNT -eq $MAX_RETRY ]; then
        echo "错误：MySQL 在 ${MAX_RETRY} 次重试后仍不可用"
        exit 1
    fi
}

# 主执行逻辑
main() {
    SQL_FILE="${BASEDIR}/init.sql"
    CONTAINER_NAME="glowxq-mysql"

    # 检查容器状态
    if ! docker ps --filter "name=${CONTAINER_NAME}" --format '{{.Names}}' | grep -q "${CONTAINER_NAME}"; then
        echo "错误：MySQL 容器 '${CONTAINER_NAME}' 未运行"
        exit 1
    fi

    # 等待 MySQL 可用
    wait_for_mysql

    # 获取 SQL 文件大小
    FILE_SIZE=$(du -h "${SQL_FILE}" | cut -f1)
    echo "开始导入 SQL 文件（大小：${FILE_SIZE}）..."

    # 增加进度显示（如果已安装 pv）
    if command -v pv >/dev/null 2>&1; then
        echo "使用 pv 显示导入进度："
        pv "${SQL_FILE}" | docker exec -i ${CONTAINER_NAME} mysql -uroot -pglowxq-oj-123
    else
        echo "提示：安装 pv 命令可显示进度（apt/yum install pv）"
        docker exec -i ${CONTAINER_NAME} mysql -uroot -pglowxq-oj-123 < "${SQL_FILE}"
    fi

    # 结果校验（示例：查询任意一个新建表）
    if [ $? -eq 0 ]; then
        echo -e "\n导入结果验证："
        docker exec -i ${CONTAINER_NAME} mysql -uroot -pglowxq-oj-123 -e "SHOW TABLES;" | head -n 5
        echo -e "\n数据库初始化成功完成！"
    else
        echo -e "\n错误：数据库初始化失败"
        exit 1
    fi
}

# 执行主函数
main