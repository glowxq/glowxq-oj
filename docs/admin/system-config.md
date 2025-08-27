# 系统配置指南

## 🔧 基本配置

### 系统信息配置
在管理后台的"系统设置"页面可以配置以下基本信息：

- **系统名称**: 显示在页面标题和Logo旁边
- **系统Logo**: 支持上传自定义Logo图片
- **系统描述**: 首页显示的系统介绍
- **联系方式**: 技术支持联系信息
- **备案信息**: ICP备案号等法律信息

### 网站配置
```yaml
# application.yml
server:
  port: 7101
  servlet:
    context-path: /
    
spring:
  application:
    name: glowxq-oj
  profiles:
    active: prod
```

## 🔐 安全配置

### 密码策略
```yaml
# 密码复杂度要求
password:
  min-length: 8
  require-uppercase: true
  require-lowercase: true
  require-digit: true
  require-special-char: false
  max-attempts: 5
  lockout-duration: 30m
```

### 登录限制
- **最大登录尝试次数**: 5次
- **账号锁定时间**: 30分钟
- **会话超时时间**: 2小时
- **强制登出时间**: 24小时

### IP访问控制
```yaml
# IP白名单配置
security:
  ip-whitelist:
    enabled: false
    ips:
      - 192.168.1.0/24
      - 10.0.0.0/8
```

## 📧 邮件配置

### SMTP配置
```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: noreply@example.com
    password: your_password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 邮件模板配置
- **注册验证邮件**: 用户注册时发送验证邮件
- **密码重置邮件**: 忘记密码时发送重置链接
- **系统通知邮件**: 重要系统通知
- **竞赛通知邮件**: 竞赛开始/结束通知

## ⚖️ 判题配置

### 判题机配置
```yaml
judge:
  # 判题机数量
  workers: 4
  # 最大并发判题数
  max-concurrent: 10
  # 判题超时时间
  timeout: 30s
  # 临时文件目录
  temp-dir: /tmp/judge
```

### 语言配置
```yaml
languages:
  cpp:
    enabled: true
    compile-command: "g++ -o {exe} {src} -O2 -std=c++17"
    run-command: "./{exe}"
    time-factor: 1.0
    memory-factor: 1.0
    
  java:
    enabled: true
    compile-command: "javac {src}"
    run-command: "java {class}"
    time-factor: 2.0
    memory-factor: 2.0
    
  python:
    enabled: true
    compile-command: ""
    run-command: "python3 {src}"
    time-factor: 5.0
    memory-factor: 3.0
```

### 资源限制
- **默认时间限制**: 1000ms
- **默认内存限制**: 256MB
- **最大时间限制**: 10000ms
- **最大内存限制**: 1024MB
- **最大代码长度**: 64KB

## 💾 数据库配置

### MySQL配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/glowxq_oj?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: glowxq
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  # 连接池配置
  hikari:
    maximum-pool-size: 20
    minimum-idle: 5
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
    timeout: 5000ms
    
  # 连接池配置
  lettuce:
    pool:
      max-active: 20
      max-idle: 10
      min-idle: 5
      max-wait: 5000ms
```

## 📁 文件存储配置

### 本地存储
```yaml
file:
  storage:
    type: local
    local:
      base-path: /opt/glowxq/data
      max-file-size: 10MB
      allowed-extensions:
        - jpg
        - png
        - gif
        - pdf
        - zip
```

### 对象存储（OSS）
```yaml
file:
  storage:
    type: oss
    oss:
      endpoint: https://oss-cn-hangzhou.aliyuncs.com
      access-key-id: your_access_key
      access-key-secret: your_secret_key
      bucket-name: glowxq-oj
      base-path: files/
```

## 🔄 缓存配置

### Redis缓存策略
```yaml
cache:
  redis:
    # 默认过期时间
    default-ttl: 3600
    # 缓存前缀
    key-prefix: "glowxq:oj:"
    # 缓存配置
    configs:
      user-info:
        ttl: 1800
        max-size: 10000
      problem-list:
        ttl: 300
        max-size: 1000
      submit-result:
        ttl: 86400
        max-size: 50000
```

### 本地缓存配置
```yaml
cache:
  caffeine:
    specs:
      user-info:
        maximum-size: 1000
        expire-after-write: 30m
      problem-info:
        maximum-size: 500
        expire-after-write: 1h
```

## 📊 监控配置

### 应用监控
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### 日志配置
```yaml
logging:
  level:
    com.glowxq.oj: INFO
    org.springframework: WARN
    com.baomidou.mybatisplus: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/glowxq-oj.log
    max-size: 100MB
    max-history: 30
```

## 🚀 性能配置

### JVM参数
```bash
# 生产环境推荐JVM参数
JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/"
```

### 线程池配置
```yaml
async:
  executor:
    core-pool-size: 10
    max-pool-size: 50
    queue-capacity: 1000
    thread-name-prefix: "async-"
    keep-alive-seconds: 60
```

### 数据库连接池优化
```yaml
spring:
  datasource:
    hikari:
      # 核心连接数
      minimum-idle: 10
      # 最大连接数
      maximum-pool-size: 50
      # 连接超时时间
      connection-timeout: 30000
      # 空闲超时时间
      idle-timeout: 600000
      # 连接最大生命周期
      max-lifetime: 1800000
      # 连接测试查询
      connection-test-query: SELECT 1
```

## 🔒 备份配置

### 数据库备份
```bash
#!/bin/bash
# 自动备份脚本
BACKUP_DIR="/opt/backup"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p glowxq_oj > $BACKUP_DIR/glowxq_oj_$DATE.sql
```

### 文件备份
```bash
#!/bin/bash
# 文件备份脚本
BACKUP_DIR="/opt/backup"
DATA_DIR="/opt/glowxq/data"
DATE=$(date +%Y%m%d_%H%M%S)
tar -czf $BACKUP_DIR/data_$DATE.tar.gz $DATA_DIR
```

## 🌐 多租户配置

### 租户隔离
```yaml
tenant:
  enabled: true
  # 租户识别方式
  identify-type: header  # header, domain, path
  # 默认租户
  default-tenant: default
  # 租户配置
  configs:
    tenant1:
      name: "租户1"
      database: glowxq_oj_tenant1
      domain: tenant1.example.com
    tenant2:
      name: "租户2"
      database: glowxq_oj_tenant2
      domain: tenant2.example.com
```

## 📱 移动端配置

### PWA配置
```yaml
pwa:
  enabled: true
  manifest:
    name: "Glowxq OJ"
    short-name: "GlowxqOJ"
    description: "在线编程测评系统"
    theme-color: "#1890ff"
    background-color: "#ffffff"
```

## 🔧 系统维护

### 定时任务配置
```yaml
scheduled:
  # 清理临时文件
  cleanup-temp-files:
    cron: "0 0 2 * * ?"
    enabled: true
    
  # 统计数据更新
  update-statistics:
    cron: "0 0 1 * * ?"
    enabled: true
    
  # 数据库备份
  database-backup:
    cron: "0 0 3 * * ?"
    enabled: true
```

### 健康检查
```yaml
health:
  checks:
    database:
      enabled: true
      timeout: 5s
    redis:
      enabled: true
      timeout: 3s
    disk-space:
      enabled: true
      threshold: 10GB
```

## 📞 技术支持

配置相关问题请联系：
- GitHub Issues: [提交问题](https://github.com/glowxq/glowxq-oj/issues)
- 技术交流群: 见README.md
- 邮箱: config-support@glowxq.com

---

**配置版本**: v1.0  
**更新时间**: 2025-08-27  
**维护团队**: GlowXQ Team
