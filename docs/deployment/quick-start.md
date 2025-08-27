# GlowXQ OJ 系统快速开始指南

## 🚀 一键部署

### 前置要求
- 安装 Docker 和 Docker Compose
- 确保端口 3307、6380、7101 未被占用（避免与本地 MySQL/Redis 冲突）

### 快速部署

#### Linux/macOS
```bash
cd init
chmod +x start.sh
./start.sh
```

#### Windows
```powershell
cd init
powershell -ExecutionPolicy Bypass -File start.ps1
```

#### 手动部署
```bash
cd init
docker-compose up -d
```

### 访问地址
- OJ 系统: http://localhost:7101
- MySQL: localhost:3307
  - root 用户: root / glowxq-oj-123
  - 远程用户: glowxq / glowxq-oj-123 (推荐用于应用连接)
- Redis: localhost:6380 (密码: glowxq-oj-123)

## 🏗️ 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   GlowXQ OJ     │    │    MySQL 8.0    │    │   Redis 7.0     │
│   应用服务       │◄──►│    数据库       │    │   缓存服务       │
│   Port: 7101    │    │   Port: 3307    │    │   Port: 6380    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🔧 端口配置说明

为了避免与用户本地已安装的 MySQL 和 Redis 服务冲突，本部署方案采用了端口偏移策略：

- **MySQL**: 使用 `3307` 端口（默认 3306 + 1）
- **Redis**: 使用 `6380` 端口（默认 6379 + 1）
- **OJ 应用**: 使用 `7101` 端口

这样的设计确保了即使用户本地运行着 MySQL (3306) 和 Redis (6379) 服务，也不会产生端口冲突。

## ⚙️ 服务管理

### 基本操作
```bash
# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重启服务
docker-compose restart
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

## 🔧 自定义配置

编辑 `.env` 文件来修改端口、密码等配置：

```bash
# 服务端口
OJ_PORT=7101
MYSQL_PORT=3307
REDIS_PORT=6380

# 密码配置
MYSQL_ROOT_PASSWORD=glowxq-oj-123
REDIS_PASSWORD=glowxq-oj-123

# JVM 配置
JAVA_OPTS=-Xms1024m -Xmx2048m -XX:+UseG1GC
```

## 🔍 故障排除

### 快速诊断
```bash
# 运行系统诊断脚本
chmod +x diagnose.sh && ./diagnose.sh
```

### 常见问题
1. **端口冲突**: 修改 `.env` 文件中的端口配置
2. **内存不足**: 调整 `JAVA_OPTS` 参数
3. **MySQL 启动失败**: MySQL 8.0 配置已优化，移除了不兼容的 SQL 模式
4. **数据库未初始化**: 部署脚本会自动检查并初始化数据库，如仍有问题可运行强制初始化脚本
5. **服务启动失败**: 查看日志 `docker-compose logs [服务名]`

### 手动测试连接
```bash
# 测试 MySQL 连接
docker-compose exec mysql mysql -u glowxq -p  # 使用远程用户
docker-compose exec mysql mysql -u root -p    # 使用 root 用户

# 测试 Redis 连接
docker-compose exec redis redis-cli -a glowxq-oj-123 ping

# 手动初始化数据库（如果需要）
chmod +x manual-init-db.sh && ./manual-init-db.sh

# 强制重新初始化数据库（推荐）
chmod +x force-init-db.sh && ./force-init-db.sh

# 完全重新部署（删除所有数据）
docker-compose down -v && docker-compose up -d
```

## 📋 文件说明

- `docker-compose.yml` - Docker Compose 配置文件
- `init.sql` - MySQL 数据库初始化脚本
- `.env` - 环境变量配置文件
- `start.sh` - Linux/macOS 一键部署脚本
- `start.ps1` - Windows PowerShell 一键部署脚本
- `diagnose.sh` - 系统诊断脚本
- `force-init-db.sh` - 强制数据库初始化脚本
- `manual-init-db.sh` - 手动数据库初始化脚本
- `HOJ题目导入测试.zip` - 题目导入测试文件（必须保留）

## 🔐 默认账号信息

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

## 📞 技术支持

如遇到问题，请：
1. 查看本文档的故障排除部分
2. 检查服务日志
3. 查看详细部署文档: [详细部署指南](./deployment-guide.md)
4. 联系技术支持团队

---

**版本**: 1.0.0  
**更新时间**: 2025-08-27  
**维护团队**: GlowXQ Team
