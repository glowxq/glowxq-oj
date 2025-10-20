# GlowXQ OJ 系统一键部署

## 快速开始

### 前置要求
- 安装 Docker 和 Docker Compose
- 确保端口 3307、6380、7101 未被占用（避免与本地 MySQL/Redis 冲突）

### 一键部署

#### Linux/macOS
```bash
cd init
# 1. 初始化数据目录（首次部署必须执行）
chmod +x init-dirs.sh
./init-dirs.sh

# 2. 启动服务
chmod +x start.sh
./start.sh
```

#### Windows
```powershell
cd init
# 1. 初始化数据目录（首次部署必须执行）
powershell -ExecutionPolicy Bypass -File init-dirs.ps1

# 2. 启动服务
powershell -ExecutionPolicy Bypass -File start.ps1
```

#### 手动部署
```bash
cd init
# 1. 初始化数据目录
./init-dirs.sh  # Linux/macOS
# 或
powershell -ExecutionPolicy Bypass -File init-dirs.ps1  # Windows

# 2. 启动 Docker 服务
docker-compose up -d
```

### 访问地址
- OJ 系统: http://localhost:7101
- MySQL: localhost:3307
  - root 用户: root / glowxq-oj-123
  - 远程用户: glowxq / glowxq-oj-123 (推荐用于应用连接)
- Redis: localhost:6380 (密码: glowxq-oj-123)

### 服务管理
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

## 文件说明

- `docker-compose.yml` - Docker Compose 配置文件
- `init.sql` - MySQL 数据库初始化脚本
- `.env` - 环境变量配置文件
- `init-dirs.sh` - Linux/macOS 数据目录初始化脚本（**首次部署必须运行**）
- `init-dirs.ps1` - Windows 数据目录初始化脚本（**首次部署必须运行**）
- `start.sh` - Linux/macOS 一键部署脚本
- `start.ps1` - Windows PowerShell 一键部署脚本
- `init.md` - 详细部署文档
- `diagnose.sh` - 系统诊断脚本
- `force-init-db.sh` - 强制数据库初始化脚本
- `manual-init-db.sh` - 手动数据库初始化脚本
- `HOJ题目导入测试.zip` - 题目导入测试文件（必须保留）
- `data/` - 数据目录（由 init-dirs 脚本创建）
  - `testcase/` - 测试用例文件存储
  - `file/` - 用户上传文件存储
  - `log/` - 应用日志文件
  - `run/` - 判题运行目录
  - `spj/` - Special Judge 文件
  - `interactive/` - 交互式判题文件

## 自定义配置

编辑 `.env` 文件来修改端口、密码等配置：

```bash
# 服务端口
OJ_PORT=7101
MYSQL_PORT=3307
REDIS_PORT=6380

# 密码配置
MYSQL_ROOT_PASSWORD=glowxq-oj-123
REDIS_PASSWORD=glowxq-oj-123
```

## 故障排除

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
6. **目录读取失败**: 确保已运行 `init-dirs.sh` 或 `init-dirs.ps1` 创建数据目录
7. **权限问题**: Linux/macOS 下运行 `chmod -R 777 ./data` 设置目录权限

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

详细文档请查看：
- [快速开始指南](../docs/deployment/quick-start.md)
- [详细部署文档](../docs/deployment/deployment-guide.md)
- [Docker部署指南](../docs/deployment/docker-deploy.md)
