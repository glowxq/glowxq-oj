# Docker 数据卷映射说明

## 问题背景

在 Docker Compose 配置中，有两种方式来持久化容器数据：

### 1. Docker 命名卷（Named Volumes）
```yaml
volumes:
  oj-testcase:
    driver: local
    name: glowxq-oj-testcase

services:
  glowxq-oj:
    volumes:
      - oj-testcase:/goj/testcase
```

**特点：**
- ✅ Docker 自动管理存储位置
- ✅ 跨平台兼容性好
- ❌ 不方便直接访问和管理文件
- ❌ 首次创建时是空的，需要应用自己初始化
- ❌ 难以调试和备份

### 2. 宿主机目录映射（Bind Mounts）
```yaml
services:
  glowxq-oj:
    volumes:
      - ./data/testcase:/goj/testcase
```

**特点：**
- ✅ 可以直接在宿主机上查看和管理文件
- ✅ 方便调试、备份和迁移
- ✅ 可以预先准备数据
- ❌ 需要手动创建目录
- ❌ 需要注意权限问题

## 当前配置

本项目已改为使用**宿主机目录映射**方式，原因如下：

1. **便于调试**：可以直接查看测试用例文件、日志等
2. **便于备份**：数据在 `init/data/` 目录下，方便备份
3. **便于迁移**：直接复制 `data` 目录即可迁移数据
4. **解决读取问题**：确保目录存在且有正确权限

## 目录结构

```
init/
├── data/                    # 数据根目录（映射到容器）
│   ├── testcase/           # 测试用例文件（/goj/testcase 和 /judge/testcase）
│   ├── file/               # 用户上传文件（/goj/file）
│   ├── log/                # 应用日志（/goj/log 和 /judge/log）
│   ├── run/                # 判题运行目录（/judge/run）
│   ├── spj/                # Special Judge 文件（/judge/spj）
│   └── interactive/        # 交互式判题文件（/judge/interactive）
├── docker-compose.yml      # Docker Compose 配置
├── init-dirs.sh            # 目录初始化脚本（Linux/macOS）
├── init-dirs.ps1           # 目录初始化脚本（Windows）
└── start.sh / start.ps1    # 启动脚本（已集成目录创建）
```

## 使用方法

### 方法一：使用一键部署脚本（推荐）

启动脚本已自动集成目录创建功能，直接运行即可：

```bash
# Linux/macOS
./start.sh

# Windows
powershell -ExecutionPolicy Bypass -File start.ps1
```

### 方法二：手动初始化

如果需要手动部署，先运行目录初始化脚本：

```bash
# Linux/macOS
chmod +x init-dirs.sh
./init-dirs.sh

# Windows
powershell -ExecutionPolicy Bypass -File init-dirs.ps1
```

然后启动服务：

```bash
docker-compose up -d
```

## 权限问题处理

### Linux/macOS

如果遇到权限问题，运行：

```bash
chmod -R 777 ./data
```

### Windows

Windows 下通常不会有权限问题，但如果遇到，请：

1. 右键点击 `data` 目录
2. 选择"属性" → "安全"
3. 确保 "Everyone" 有完全控制权限

## 数据备份与恢复

### 备份

```bash
# 停止服务
docker-compose down

# 备份数据目录
tar -czf glowxq-oj-data-backup-$(date +%Y%m%d).tar.gz data/

# 或者直接复制
cp -r data/ data-backup/
```

### 恢复

```bash
# 停止服务
docker-compose down

# 恢复数据
tar -xzf glowxq-oj-data-backup-20250120.tar.gz

# 或者直接复制
cp -r data-backup/ data/

# 启动服务
docker-compose up -d
```

## 切换回 Docker 卷

如果需要切换回 Docker 命名卷方式，请：

1. 编辑 `docker-compose.yml`
2. 取消注释 `volumes` 部分的卷定义
3. 修改 `glowxq-oj` 服务的 `volumes` 配置
4. 运行 `docker-compose down -v && docker-compose up -d`

## 常见问题

### Q: 为什么程序读取 /goj/testcase 报错？

A: 可能原因：
1. 目录未创建 → 运行 `init-dirs.sh` 或 `start.sh`
2. 权限不足 → 运行 `chmod -R 777 ./data`
3. 容器内路径错误 → 检查 `docker-compose.yml` 中的映射配置

### Q: 如何查看容器内的实际路径？

```bash
# 进入容器
docker-compose exec glowxq-oj bash

# 查看目录
ls -la /goj/testcase
ls -la /judge/testcase
```

### Q: 数据目录占用空间太大怎么办？

可以定期清理：

```bash
# 清理旧日志
find ./data/log -name "*.log" -mtime +30 -delete

# 清理临时文件
rm -rf ./data/run/*
```

## 技术细节

### Docker Compose 配置对比

**之前（Docker 卷）：**
```yaml
volumes:
  - oj-testcase:/goj/testcase

volumes:
  oj-testcase:
    driver: local
    name: glowxq-oj-testcase
```

**现在（目录映射）：**
```yaml
volumes:
  - ./data/testcase:/goj/testcase
```

### 容器内路径说明

| 容器内路径 | 宿主机路径 | 用途 |
|-----------|-----------|------|
| /goj/testcase | ./data/testcase | 测试用例存储（应用层） |
| /judge/testcase | ./data/testcase | 测试用例存储（判题层） |
| /goj/file | ./data/file | 用户上传文件 |
| /goj/log | ./data/log | 应用日志 |
| /judge/log | ./data/log | 判题日志 |
| /judge/run | ./data/run | 判题运行目录 |
| /judge/spj | ./data/spj | Special Judge |
| /judge/interactive | ./data/interactive | 交互式判题 |

## 参考资料

- [Docker Volumes 官方文档](https://docs.docker.com/storage/volumes/)
- [Docker Bind Mounts 官方文档](https://docs.docker.com/storage/bind-mounts/)
- [Docker Compose Volumes 配置](https://docs.docker.com/compose/compose-file/compose-file-v3/#volumes)

