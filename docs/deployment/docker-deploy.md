# Glowxq-OJ Docker 一键部署指南

## 🚀 快速开始

### 系统要求

- **操作系统**: Linux / macOS / Windows (with WSL2)
- **Docker**: 20.x+
- **Docker Compose**: 2.x+
- **内存**: 4GB+ 可用内存
- **存储**: 10GB+ 可用空间

### 一键部署

1. **克隆项目**
```bash
git clone https://github.com/glowxq/glowxq-oj.git
cd glowxq-oj
```

2. **运行部署脚本**
```bash
# Linux/macOS
chmod +x start.sh
./start.sh

# Windows (使用Git Bash或WSL)
bash start.sh
```

3. **访问系统**
- 前端地址: http://localhost:7101
- API文档: http://localhost:7101/doc.html

### 开发环境部署

如果只需要数据库和缓存服务用于本地开发：

```bash
# 启动开发环境(仅MySQL和Redis)
docker-compose -f docker-compose.dev.yml up -d

# 停止开发环境
docker-compose -f docker-compose.dev.yml down
```

### 默认账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 管理员 | 13667700000 | 123456 | 系统管理员，拥有所有权限 |
| 学生 | 13667700001 | 123456 | 普通学生账号，可提交代码 |
| 校长 | 13667700002 | 123456 | 校长账号，管理学校事务 |
| 老师 | 13667700003 | 123456 | 教师账号，管理班级和学生 |
| 系统管理员 | 13667700004 | 123456 | 超级管理员，系统维护 |

## 📋 服务组件

### 包含的服务

- **MySQL 8.0**: 主数据库，自动初始化数据
- **Redis 6.0**: 缓存服务，提升性能
- **Glowxq-OJ**: 主应用服务，包含前后端
- **AutoHeal**: 健康监控，自动重启异常容器

### 端口映射

| 服务 | 内部端口 | 外部端口 | 说明 |
|------|----------|----------|------|
| MySQL | 3306 | 3306 | 数据库服务 |
| Redis | 6379 | 6379 | 缓存服务 |
| Glowxq-OJ | 7101 | 7101 | 主应用服务 |

## 🔧 配置说明

### 环境变量配置

复制 `.env.example` 为 `.env` 并根据需要修改：

```bash
cp .env.example .env
vim .env
```

主要配置项：
- `MYSQL_ROOT_PASSWORD`: MySQL root密码
- `REDIS_PASSWORD`: Redis密码
- `JAVA_OPTS`: JVM参数
- `OJ_PORT`: 应用外部端口

### 数据持久化

所有数据都通过Docker volumes持久化存储：
- `mysql-data`: MySQL数据
- `redis-data`: Redis数据
- `oj-data`: 应用文件数据
- `oj-testcase`: 测试用例数据
- `oj-logs`: 应用日志

## 🛠️ 常用操作

### 启动服务
```bash
./start.sh
```

### 停止服务
```bash
./stop.sh
```

### 停止并清理资源
```bash
./stop.sh --cleanup
```

### 测试部署
```bash
# 运行部署测试脚本
chmod +x test.sh
./test.sh

# Windows
bash test.sh
```

### 查看服务状态
```bash
docker-compose ps
```

### 查看日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f glowxq-oj
docker-compose logs -f mysql
docker-compose logs -f redis
```

### 重启服务
```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart glowxq-oj
```

### 进入容器
```bash
# 进入应用容器
docker-compose exec glowxq-oj bash

# 进入MySQL容器
docker-compose exec mysql bash

# 进入Redis容器
docker-compose exec redis sh
```

## 🔍 故障排除

### 常见问题

1. **端口被占用**
   - 修改 `.env` 文件中的端口配置
   - 或停止占用端口的服务

2. **内存不足**
   - 调整 `JAVA_OPTS` 中的内存参数
   - 确保系统有足够可用内存

3. **数据库连接失败**
   - 检查MySQL容器是否正常启动
   - 查看MySQL日志: `docker-compose logs mysql`

4. **应用启动缓慢**
   - 首次启动需要下载镜像，请耐心等待
   - 可通过日志查看启动进度

### 健康检查

系统包含自动健康检查：
- MySQL: 每10秒检查一次
- Redis: 每10秒检查一次  
- Glowxq-OJ: 每30秒检查一次

### 数据备份

```bash
# 备份MySQL数据
docker-compose exec mysql mysqldump -u root -pglowxq-oj-123 glowxq_oj > backup.sql

# 备份Redis数据
docker-compose exec redis redis-cli -a glowxq-oj-123 BGSAVE
```

## 🔒 安全建议

1. **修改默认密码**
   - 修改 `.env` 文件中的数据库密码
   - 修改应用中的默认用户密码

2. **网络安全**
   - 生产环境建议使用防火墙限制端口访问
   - 考虑使用反向代理(如Nginx)

3. **数据安全**
   - 定期备份重要数据
   - 监控磁盘空间使用情况

## 📞 技术支持

如遇到问题，可通过以下方式获取帮助：
- 查看项目文档: [README.md](../../README.md)
- 提交Issue: [GitHub Issues](https://github.com/glowxq/glowxq-oj/issues)
- 技术交流群: 见README.md中的联系方式
