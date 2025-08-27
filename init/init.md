# GlowXQ OJ 系统部署指南

## 概述

本文档提供了 GlowXQ OJ 系统的完整部署指南，包含 MySQL 8.0、Redis 7.0 和 OJ 应用服务的一键部署方案。

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   GlowXQ OJ     │    │    MySQL 8.0    │    │   Redis 7.0     │
│   应用服务       │◄──►│    数据库       │    │   缓存服务       │
│   Port: 7101    │    │   Port: 3307    │    │   Port: 6380    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 端口配置说明

为了避免与用户本地已安装的 MySQL 和 Redis 服务冲突，本部署方案采用了端口偏移策略：

- **MySQL**: 使用 `3307` 端口（默认 3306 + 1）
- **Redis**: 使用 `6380` 端口（默认 6379 + 1）
- **OJ 应用**: 使用 `7101` 端口

这样的设计确保了即使用户本地运行着 MySQL (3306) 和 Redis (6379) 服务，也不会产生端口冲突。

## 前置要求

### 系统要求
- 操作系统：Linux、macOS 或 Windows
- 内存：至少 4GB RAM
- 磁盘空间：至少 10GB 可用空间
- 网络：能够访问 Docker Hub 和阿里云镜像仓库

### 软件要求
- Docker 20.10+ 
- Docker Compose 2.0+

### Docker 安装

#### Linux (Ubuntu/Debian)
```bash
# 更新包索引
sudo apt update

# 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 将用户添加到 docker 组
sudo usermod -aG docker $USER
```

#### Windows
1. 下载并安装 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
2. 启动 Docker Desktop
3. 确保启用 WSL 2 后端（推荐）

#### macOS
1. 下载并安装 [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop)
2. 启动 Docker Desktop

## 快速部署

### 方法一：一键部署脚本（推荐）

1. **进入部署目录**
   ```bash
   cd init
   ```

2. **运行部署脚本**
   ```bash
   # Linux/macOS
   chmod +x start.sh
   ./start.sh
   
   # Windows (PowerShell)
   powershell -ExecutionPolicy Bypass -File start.ps1
   ```

3. **等待部署完成**
   脚本会自动完成以下操作：
   - 检查 Docker 环境
   - 拉取所需镜像
   - 启动所有服务
   - 初始化数据库
   - 等待服务就绪

### 方法二：手动部署

1. **进入部署目录**
   ```bash
   cd init
   ```

2. **启动服务**
   ```bash
   docker-compose up -d
   ```

3. **查看服务状态**
   ```bash
   docker-compose ps
   ```

## 配置说明

### 环境变量配置

编辑 `.env` 文件来自定义配置：

```bash
# 服务端口配置
OJ_PORT=7101          # OJ 应用端口
MYSQL_PORT=3307       # MySQL 端口（避免与本地MySQL冲突）
REDIS_PORT=6380       # Redis 端口（避免与本地Redis冲突）

# 数据库配置
MYSQL_ROOT_PASSWORD=glowxq-oj-123    # MySQL root 密码
MYSQL_DATABASE=glowxq_oj             # 数据库名

# Redis 配置
REDIS_PASSWORD=glowxq-oj-123         # Redis 密码

# JVM 配置
JAVA_OPTS=-Xms1024m -Xmx2048m -XX:+UseG1GC
```

### 服务配置详情

#### MySQL 8.0 配置
- **镜像**: `mysql:8.0`
- **端口**: 3307 (宿主机) → 3306 (容器)
- **字符集**: utf8mb4
- **时区**: Asia/Shanghai
- **数据持久化**: Docker 卷 `glowxq-mysql-data`
- **初始化**: 自动执行 `init.sql` 脚本
- **远程连接**: 已启用，绑定到 `0.0.0.0`

#### Redis 7.0 配置
- **镜像**: `redis:7.0-alpine`
- **端口**: 6380 (宿主机) → 6379 (容器)
- **持久化**: RDB + AOF
- **内存限制**: 512MB
- **远程连接**: 已启用，绑定到 `0.0.0.0`
- **密码认证**: 已启用

#### OJ 应用配置
- **镜像**: `registry.cn-guangzhou.aliyuncs.com/glowxq/public:glowxq-oj`
- **端口**: 7101
- **JVM 参数**: 可通过环境变量调整
- **数据持久化**: 测试用例、文件存储、日志等

## 服务管理

### 启动服务
```bash
docker-compose up -d
```

### 停止服务
```bash
docker-compose down
```

### 重启服务
```bash
docker-compose restart
```

### 查看服务状态
```bash
docker-compose ps
```

### 查看服务日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f mysql
docker-compose logs -f redis
docker-compose logs -f glowxq-oj
```

### 进入容器
```bash
# 进入 MySQL 容器
docker-compose exec mysql bash

# 进入 Redis 容器
docker-compose exec redis sh

# 进入 OJ 应用容器
docker-compose exec glowxq-oj bash
```

## 数据库管理

### 连接数据库
```bash
# 使用 docker-compose 连接
docker-compose exec mysql mysql -u root -p

# 使用外部客户端连接
mysql -h localhost -P 3307 -u glowxq -p  # 推荐使用远程用户
mysql -h localhost -P 3307 -u root -p    # 或使用 root 用户
```

### 数据备份
```bash
# 备份数据库
docker-compose exec mysql mysqldump -u root -p glowxq_oj > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -u root -p glowxq_oj < backup.sql
```

## Redis 管理

### 连接 Redis
```bash
# 使用 docker-compose 连接
docker-compose exec redis redis-cli -a glowxq-oj-123

# 使用外部客户端连接
redis-cli -h localhost -p 6380 -a glowxq-oj-123
```

## 访问地址

部署完成后，可通过以下地址访问服务：

- **OJ 系统**: http://localhost:7101
- **MySQL 数据库**: localhost:3307
- **Redis 缓存**: localhost:6380

## 默认账号信息

### 数据库账号
- **Root 用户**: root / glowxq-oj-123 (管理员权限)
- **远程用户**: glowxq / glowxq-oj-123 (应用连接推荐)
- **数据库**: glowxq_oj

### Redis 账号
- **密码**: glowxq-oj-123

### 系统管理员账号
根据 `init.sql` 中的数据，默认管理员账号：
- **用户名**: admin
- **密码**: 请查看数据库中的 sys_user 表

## 故障排除

### 常见问题

1. **端口冲突**
   - 修改 `.env` 文件中的端口配置
   - 确保端口未被其他服务占用

2. **内存不足**
   - 调整 `JAVA_OPTS` 中的内存参数
   - 确保系统有足够的可用内存

3. **数据库连接失败**
   - 检查 MySQL 服务是否正常启动
   - 验证数据库密码是否正确

4. **Redis 连接失败**
   - 检查 Redis 服务是否正常启动
   - 验证 Redis 密码是否正确

5. **MySQL 启动失败 (sql_mode 错误)**
   - MySQL 8.0 已移除 `NO_AUTO_CREATE_USER` 模式
   - 本配置已适配 MySQL 8.0，使用兼容的 SQL 模式

6. **Redis 连接超时或健康检查失败**
   - Redis 设置了密码认证，连接时需要提供密码
   - 健康检查和脚本已配置正确的密码认证
   - 可运行 `test-redis.sh` 验证 Redis 连接

7. **数据库初始化失败**
   - init.sql 已添加 `USE glowxq_oj;` 语句确保在正确数据库中执行
   - 如需重新初始化：`docker-compose down -v && docker-compose up -d`

### 日志查看
```bash
# 查看启动日志
docker-compose logs --tail=100 -f

# 查看特定服务的详细日志
docker-compose logs --tail=200 -f glowxq-oj
```

### 重置数据
```bash
# 停止服务并删除数据卷
docker-compose down -v

# 重新启动服务
docker-compose up -d
```

## 生产环境建议

1. **安全配置**
   - 修改默认密码
   - 配置防火墙规则
   - 启用 SSL/TLS

2. **性能优化**
   - 调整 JVM 参数
   - 优化数据库配置
   - 配置 Redis 持久化策略

3. **监控告警**
   - 配置日志收集
   - 设置健康检查
   - 配置监控告警

4. **备份策略**
   - 定期备份数据库
   - 备份重要配置文件
   - 测试恢复流程

## 技术支持

如遇到问题，请：
1. 查看本文档的故障排除部分
2. 检查服务日志
3. 联系技术支持团队

## 部署文件说明

本次完善后的部署包含以下文件：

### 核心配置文件
- **`docker-compose.yml`** - Docker Compose 主配置文件，包含 MySQL 8.0、Redis 7.0 和 OJ 应用服务
- **`.env`** - 环境变量配置文件，包含端口、密码等可自定义配置
- **`init.sql`** - MySQL 数据库初始化脚本

### 部署脚本
- **`start.sh`** - Linux/macOS 一键部署脚本
- **`start.ps1`** - Windows PowerShell 一键部署脚本

### 文档文件
- **`README.md`** - 快速开始指南
- **`init.md`** - 详细部署文档（本文档）
- **`DEPLOYMENT_SUCCESS.md`** - 部署成功指南

### 必须文件
- **`HOJ题目导入测试.zip`** - 题目导入测试文件（系统必需）

### 特性亮点

✅ **一键部署** - 运行单个脚本即可完成整个系统部署
✅ **服务完整** - 包含 MySQL 8.0、Redis 7.0、OJ 应用三个核心服务
✅ **远程连接** - MySQL 和 Redis 均配置为允许远程连接
✅ **数据持久化** - 所有重要数据均使用 Docker 卷持久化存储
✅ **健康检查** - 每个服务都配置了健康检查，确保服务正常启动
✅ **环境隔离** - 使用独立的 Docker 网络，服务间通信安全
✅ **配置灵活** - 通过 .env 文件可轻松自定义端口、密码等配置
✅ **跨平台支持** - 提供 Linux/macOS 和 Windows 两套部署脚本
✅ **最小依赖** - 只需要 Docker 和 Docker Compose，无需其他依赖

---

**版本**: 1.0.0
**更新时间**: 2025-08-25
**维护团队**: GlowXQ Team
