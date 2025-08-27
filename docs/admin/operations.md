# 运维指南

## 🔍 系统监控

### 服务状态监控

#### 1. 应用服务监控
```bash
# 检查应用服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f glowxq-oj

# 检查应用健康状态
curl http://localhost:7101/actuator/health
```

#### 2. 数据库监控
```bash
# 检查MySQL状态
docker-compose exec mysql mysqladmin -u root -p status

# 查看MySQL进程列表
docker-compose exec mysql mysql -u root -p -e "SHOW PROCESSLIST;"

# 检查数据库大小
docker-compose exec mysql mysql -u root -p -e "
SELECT 
    table_schema AS 'Database',
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables 
WHERE table_schema = 'glowxq_oj'
GROUP BY table_schema;"
```

#### 3. Redis监控
```bash
# 检查Redis状态
docker-compose exec redis redis-cli -a glowxq-oj-123 ping

# 查看Redis信息
docker-compose exec redis redis-cli -a glowxq-oj-123 info

# 查看Redis内存使用
docker-compose exec redis redis-cli -a glowxq-oj-123 info memory
```

### 性能监控

#### 1. 系统资源监控
```bash
# CPU使用率
top -p $(docker-compose exec glowxq-oj pidof java)

# 内存使用情况
docker stats

# 磁盘使用情况
df -h
du -sh /opt/glowxq/data/*
```

#### 2. 应用性能监控
```bash
# JVM内存使用情况
docker-compose exec glowxq-oj jstat -gc $(pidof java)

# 线程状态
docker-compose exec glowxq-oj jstack $(pidof java)

# 查看GC日志
docker-compose logs glowxq-oj | grep GC
```

## 📊 日志管理

### 日志收集
```bash
# 应用日志
docker-compose logs --tail=100 -f glowxq-oj

# 数据库日志
docker-compose logs --tail=100 -f mysql

# Redis日志
docker-compose logs --tail=100 -f redis

# 系统日志
tail -f /var/log/syslog
```

### 日志分析
```bash
# 错误日志统计
docker-compose logs glowxq-oj | grep ERROR | wc -l

# 慢查询日志
docker-compose exec mysql mysql -u root -p -e "
SELECT * FROM mysql.slow_log 
ORDER BY start_time DESC 
LIMIT 10;"

# 判题失败统计
docker-compose logs glowxq-oj | grep "Judge failed" | wc -l
```

### 日志轮转
```bash
# 配置logrotate
cat > /etc/logrotate.d/glowxq-oj << EOF
/opt/glowxq/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 root root
    postrotate
        docker-compose restart glowxq-oj
    endscript
}
EOF
```

## 💾 数据备份与恢复

### 数据库备份
```bash
#!/bin/bash
# 数据库备份脚本
BACKUP_DIR="/opt/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="glowxq_oj"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
docker-compose exec mysql mysqldump \
    -u root -pglowxq-oj-123 \
    --single-transaction \
    --routines \
    --triggers \
    $DB_NAME > $BACKUP_DIR/${DB_NAME}_${DATE}.sql

# 压缩备份文件
gzip $BACKUP_DIR/${DB_NAME}_${DATE}.sql

# 删除7天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Database backup completed: ${DB_NAME}_${DATE}.sql.gz"
```

### 文件备份
```bash
#!/bin/bash
# 文件备份脚本
BACKUP_DIR="/opt/backup/files"
DATA_DIR="/opt/glowxq/data"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据文件
tar -czf $BACKUP_DIR/data_${DATE}.tar.gz -C $DATA_DIR .

# 删除30天前的备份
find $BACKUP_DIR -name "data_*.tar.gz" -mtime +30 -delete

echo "File backup completed: data_${DATE}.tar.gz"
```

### 数据恢复
```bash
#!/bin/bash
# 数据库恢复脚本
BACKUP_FILE=$1
DB_NAME="glowxq_oj"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file>"
    exit 1
fi

# 停止应用服务
docker-compose stop glowxq-oj

# 恢复数据库
if [[ $BACKUP_FILE == *.gz ]]; then
    gunzip -c $BACKUP_FILE | docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 $DB_NAME
else
    docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 $DB_NAME < $BACKUP_FILE
fi

# 启动应用服务
docker-compose start glowxq-oj

echo "Database restore completed"
```

## 🔧 系统维护

### 定期维护任务

#### 1. 数据库维护
```bash
#!/bin/bash
# 数据库维护脚本

# 优化表
docker-compose exec mysql mysql -u root -pglowxq-oj-123 -e "
USE glowxq_oj;
OPTIMIZE TABLE submit_record;
OPTIMIZE TABLE judge_result;
OPTIMIZE TABLE user_info;
"

# 分析表
docker-compose exec mysql mysql -u root -pglowxq-oj-123 -e "
USE glowxq_oj;
ANALYZE TABLE submit_record;
ANALYZE TABLE judge_result;
ANALYZE TABLE user_info;
"

# 检查表
docker-compose exec mysql mysql -u root -pglowxq-oj-123 -e "
USE glowxq_oj;
CHECK TABLE submit_record;
CHECK TABLE judge_result;
CHECK TABLE user_info;
"
```

#### 2. 缓存清理
```bash
#!/bin/bash
# 缓存清理脚本

# 清理Redis过期键
docker-compose exec redis redis-cli -a glowxq-oj-123 --scan --pattern "*" | xargs -L 1000 docker-compose exec redis redis-cli -a glowxq-oj-123 DEL

# 清理应用缓存
curl -X POST http://localhost:7101/actuator/caches/clear

echo "Cache cleanup completed"
```

#### 3. 临时文件清理
```bash
#!/bin/bash
# 临时文件清理脚本

# 清理判题临时文件
find /tmp/judge -type f -mtime +1 -delete

# 清理日志文件
find /opt/glowxq/logs -name "*.log.*" -mtime +30 -delete

# 清理上传临时文件
find /opt/glowxq/data/temp -type f -mtime +1 -delete

echo "Temporary files cleanup completed"
```

### 系统更新

#### 1. 应用更新
```bash
#!/bin/bash
# 应用更新脚本
NEW_VERSION=$1

if [ -z "$NEW_VERSION" ]; then
    echo "Usage: $0 <new_version>"
    exit 1
fi

# 备份当前数据
./backup.sh

# 停止服务
docker-compose down

# 拉取新镜像
docker pull registry.cn-guangzhou.aliyuncs.com/glowxq/public:glowxq-oj-$NEW_VERSION

# 更新docker-compose.yml中的镜像版本
sed -i "s/glowxq-oj:.*/glowxq-oj:$NEW_VERSION/" docker-compose.yml

# 启动服务
docker-compose up -d

# 等待服务启动
sleep 30

# 健康检查
if curl -f http://localhost:7101/actuator/health; then
    echo "Update completed successfully"
else
    echo "Update failed, rolling back..."
    # 回滚操作
    docker-compose down
    sed -i "s/glowxq-oj:$NEW_VERSION/glowxq-oj:latest/" docker-compose.yml
    docker-compose up -d
fi
```

#### 2. 数据库迁移
```bash
#!/bin/bash
# 数据库迁移脚本

# 备份数据库
./backup-db.sh

# 执行迁移脚本
docker-compose exec mysql mysql -u root -pglowxq-oj-123 glowxq_oj < migration.sql

# 验证迁移结果
docker-compose exec mysql mysql -u root -pglowxq-oj-123 -e "
USE glowxq_oj;
SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'glowxq_oj';
"

echo "Database migration completed"
```

## 🚨 故障处理

### 常见故障诊断

#### 1. 应用无法启动
```bash
# 检查端口占用
netstat -tlnp | grep 7101

# 检查磁盘空间
df -h

# 检查内存使用
free -h

# 查看启动日志
docker-compose logs glowxq-oj
```

#### 2. 数据库连接失败
```bash
# 检查MySQL服务状态
docker-compose ps mysql

# 测试数据库连接
docker-compose exec mysql mysql -u root -pglowxq-oj-123 -e "SELECT 1;"

# 检查数据库配置
docker-compose exec glowxq-oj cat /app/application.yml | grep datasource
```

#### 3. 判题异常
```bash
# 检查判题队列
curl http://localhost:7101/api/admin/judge/queue

# 查看判题日志
docker-compose logs glowxq-oj | grep Judge

# 重启判题服务
curl -X POST http://localhost:7101/api/admin/judge/restart
```

### 应急处理

#### 1. 服务重启
```bash
#!/bin/bash
# 应急重启脚本

echo "Emergency restart initiated..."

# 停止所有服务
docker-compose down

# 等待5秒
sleep 5

# 启动所有服务
docker-compose up -d

# 等待服务启动
sleep 30

# 健康检查
if curl -f http://localhost:7101/actuator/health; then
    echo "Emergency restart completed successfully"
else
    echo "Emergency restart failed, please check manually"
fi
```

#### 2. 数据恢复
```bash
#!/bin/bash
# 应急数据恢复脚本
BACKUP_DATE=$1

if [ -z "$BACKUP_DATE" ]; then
    echo "Usage: $0 <backup_date>"
    echo "Available backups:"
    ls -la /opt/backup/mysql/
    exit 1
fi

# 停止应用
docker-compose stop glowxq-oj

# 恢复数据库
gunzip -c /opt/backup/mysql/glowxq_oj_${BACKUP_DATE}.sql.gz | \
    docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 glowxq_oj

# 启动应用
docker-compose start glowxq-oj

echo "Emergency data recovery completed"
```

## 📈 性能优化

### 数据库优化
```sql
-- 慢查询优化
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- 索引优化
ANALYZE TABLE submit_record;
ANALYZE TABLE judge_result;

-- 查询缓存优化
SET GLOBAL query_cache_size = 268435456;
SET GLOBAL query_cache_type = ON;
```

### 应用优化
```bash
# JVM参数优化
export JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 连接池优化
# 在application.yml中调整数据库连接池参数

# 缓存优化
# 调整Redis内存配置和过期策略
```

## 📋 运维检查清单

### 日常检查（每日）
- [ ] 检查服务运行状态
- [ ] 查看错误日志
- [ ] 检查磁盘空间使用
- [ ] 检查内存使用情况
- [ ] 验证备份是否正常

### 周检查
- [ ] 分析性能指标
- [ ] 检查数据库慢查询
- [ ] 清理临时文件
- [ ] 更新系统补丁
- [ ] 检查安全日志

### 月检查
- [ ] 数据库性能优化
- [ ] 系统资源规划
- [ ] 备份策略评估
- [ ] 安全审计
- [ ] 容量规划

## 📞 技术支持

运维相关问题请联系：
- 紧急故障热线: 400-xxx-xxxx
- GitHub Issues: [提交问题](https://github.com/glowxq/glowxq-oj/issues)
- 技术交流群: 见README.md
- 邮箱: ops-support@glowxq.com

---

**运维版本**: v1.0  
**更新时间**: 2025-08-27  
**运维团队**: GlowXQ Team
