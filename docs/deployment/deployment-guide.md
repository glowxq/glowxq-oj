# 部署指南

本文档详细介绍了 Glowxq-OJ 的各种部署方式。

## 🐳 Docker 部署（推荐）

### 快速部署

1. **环境准备**
```bash
# 安装 Docker 和 Docker Compose
curl -fsSL https://get.docker.com | bash -s docker
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

2. **配置环境变量**
```bash
# 创建环境配置文件
cat > .env << EOF
# 数据存储目录
OJ_DATA_DIRECTORY=/opt/glowxq/data

# 数据库配置
MYSQL_ROOT_PASSWORD=your_strong_password
MYSQL_DATABASE=glowxq_oj

# Redis配置
REDIS_PASSWORD=your_redis_password

# 应用配置
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC
EOF
```

3. **启动服务**
```bash
# 创建数据目录
sudo mkdir -p /opt/glowxq/data
sudo chown -R $USER:$USER /opt/glowxq/data

# 启动服务
docker-compose up -d
```

### 生产环境配置

#### 1. 反向代理配置 (Nginx)

```nginx
# /etc/nginx/sites-available/glowxq-oj
server {
    listen 80;
    server_name your-domain.com;
    
    # 重定向到 HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    # SSL 证书配置
    ssl_certificate /path/to/your/cert.pem;
    ssl_certificate_key /path/to/your/key.pem;
    
    # 安全配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;
    
    # 代理配置
    location / {
        proxy_pass http://localhost:7101;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # 静态文件缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

#### 2. 数据库优化

```sql
-- MySQL 性能优化配置
-- /etc/mysql/mysql.conf.d/mysqld.cnf

[mysqld]
# 基础配置
max_connections = 1000
max_connect_errors = 10000
table_open_cache = 2048
max_allowed_packet = 64M

# InnoDB 配置
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M
innodb_log_buffer_size = 16M
innodb_flush_log_at_trx_commit = 2
innodb_file_per_table = 1

# 查询缓存
query_cache_type = 1
query_cache_size = 256M
query_cache_limit = 2M

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
```

## 🖥️ 传统部署

### 系统要求

- **操作系统**: Ubuntu 20.04+ / CentOS 8+ / RHEL 8+
- **CPU**: 4核心以上
- **内存**: 8GB以上
- **存储**: 100GB以上 SSD
- **网络**: 10Mbps以上带宽

### 环境安装

#### 1. Java 21 安装

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# CentOS/RHEL
sudo dnf install java-21-openjdk java-21-openjdk-devel

# 验证安装
java -version
```

#### 2. MySQL 8.0 安装

```bash
# Ubuntu/Debian
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo dnf install mysql-server

# 启动服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置
sudo mysql_secure_installation
```

#### 3. Redis 安装

```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo dnf install redis

# 启动服务
sudo systemctl start redis
sudo systemctl enable redis
```

### 应用部署

#### 1. 构建应用

```bash
# 克隆代码
git clone https://github.com/glowxq/glowxq-oj.git
cd glowxq-oj/glowxq-api

# 构建项目
mvn clean package -DskipTests

# 创建部署目录
sudo mkdir -p /opt/glowxq
sudo cp app/app-oj/target/app-oj-*.jar /opt/glowxq/glowxq-oj.jar
```

#### 2. 配置文件

```yaml
# /opt/glowxq/application-prod.yml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:mysql://localhost:3306/glowxq_oj?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
    username: glowxq
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password

logging:
  level:
    root: INFO
  file:
    name: /opt/glowxq/logs/application.log
```

#### 3. 系统服务

```ini
# /etc/systemd/system/glowxq-oj.service
[Unit]
Description=GlowXQ OJ Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=glowxq
Group=glowxq
WorkingDirectory=/opt/glowxq
ExecStart=/usr/bin/java -jar -Xms2g -Xmx4g -XX:+UseG1GC glowxq-oj.jar --spring.config.location=application-prod.yml
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# 创建用户
sudo useradd -r -s /bin/false glowxq
sudo chown -R glowxq:glowxq /opt/glowxq

# 启动服务
sudo systemctl daemon-reload
sudo systemctl start glowxq-oj
sudo systemctl enable glowxq-oj
```

## 🔧 监控与维护

### 日志管理

```bash
# 查看应用日志
sudo journalctl -u glowxq-oj -f

# 查看错误日志
sudo journalctl -u glowxq-oj --since "1 hour ago" -p err

# 日志轮转配置
# /etc/logrotate.d/glowxq
/opt/glowxq/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 glowxq glowxq
}
```

### 性能监控

```bash
# 系统资源监控
htop
iotop
nethogs

# 应用监控
# 可以集成 Prometheus + Grafana
```

### 备份策略

```bash
#!/bin/bash
# /opt/glowxq/scripts/backup.sh

# 数据库备份
mysqldump -u root -p glowxq_oj > /backup/glowxq_oj_$(date +%Y%m%d_%H%M%S).sql

# 文件备份
tar -czf /backup/glowxq_files_$(date +%Y%m%d_%H%M%S).tar.gz /opt/glowxq/data

# 清理旧备份（保留30天）
find /backup -name "*.sql" -mtime +30 -delete
find /backup -name "*.tar.gz" -mtime +30 -delete
```

## 🚨 故障排除

### 常见问题

1. **服务启动失败**
   - 检查Java版本和环境变量
   - 检查数据库连接配置
   - 查看详细错误日志

2. **数据库连接失败**
   - 检查MySQL服务状态
   - 验证用户名密码
   - 检查防火墙设置

3. **内存不足**
   - 调整JVM堆内存参数
   - 优化数据库配置
   - 增加系统内存

4. **判题超时**
   - 检查沙箱配置
   - 调整时间限制参数
   - 优化判题队列

### 性能调优

1. **JVM调优**
```bash
# 生产环境推荐参数
JAVA_OPTS="-Xms4g -Xmx8g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler"
```

2. **数据库调优**
   - 定期分析慢查询
   - 优化索引设计
   - 调整连接池参数

3. **缓存优化**
   - 合理设置Redis过期时间
   - 使用Redis集群提高性能
   - 实施多级缓存策略
