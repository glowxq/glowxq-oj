# 目录读取问题快速修复指南

## 问题描述

程序读取 `/goj/testcase` 或 `/judge/testcase` 目录时报错。

## 原因分析

Docker Compose 配置使用了宿主机目录映射，但目录未创建或权限不正确。

## 快速修复步骤

### 步骤 1: 停止现有服务

```bash
cd init
docker-compose down
```

### 步骤 2: 创建数据目录

#### Linux/macOS

```bash
# 方法一：使用初始化脚本（推荐）
chmod +x init-dirs.sh
./init-dirs.sh

# 方法二：手动创建
mkdir -p data/{testcase,file,log,run,spj,interactive}
chmod -R 777 data
```

#### Windows PowerShell

```powershell
# 方法一：使用初始化脚本（推荐）
powershell -ExecutionPolicy Bypass -File init-dirs.ps1

# 方法二：手动创建
New-Item -ItemType Directory -Force -Path data\testcase
New-Item -ItemType Directory -Force -Path data\file
New-Item -ItemType Directory -Force -Path data\log
New-Item -ItemType Directory -Force -Path data\run
New-Item -ItemType Directory -Force -Path data\spj
New-Item -ItemType Directory -Force -Path data\interactive
```

### 步骤 3: 重新启动服务

```bash
docker-compose up -d
```

### 步骤 4: 验证修复

```bash
# 查看容器日志
docker-compose logs -f glowxq-oj

# 进入容器检查目录
docker-compose exec glowxq-oj ls -la /goj/testcase
docker-compose exec glowxq-oj ls -la /judge/testcase
```

## 一键修复脚本

### Linux/macOS

创建文件 `fix-dirs.sh`：

```bash
#!/bin/bash
echo "正在修复目录问题..."
docker-compose down
mkdir -p data/{testcase,file,log,run,spj,interactive}
chmod -R 777 data
docker-compose up -d
echo "修复完成！"
docker-compose logs -f glowxq-oj
```

运行：
```bash
chmod +x fix-dirs.sh
./fix-dirs.sh
```

### Windows PowerShell

创建文件 `fix-dirs.ps1`：

```powershell
Write-Host "正在修复目录问题..." -ForegroundColor Yellow
docker-compose down
New-Item -ItemType Directory -Force -Path data\testcase | Out-Null
New-Item -ItemType Directory -Force -Path data\file | Out-Null
New-Item -ItemType Directory -Force -Path data\log | Out-Null
New-Item -ItemType Directory -Force -Path data\run | Out-Null
New-Item -ItemType Directory -Force -Path data\spj | Out-Null
New-Item -ItemType Directory -Force -Path data\interactive | Out-Null
docker-compose up -d
Write-Host "修复完成！" -ForegroundColor Green
docker-compose logs -f glowxq-oj
```

运行：
```powershell
powershell -ExecutionPolicy Bypass -File fix-dirs.ps1
```

## 验证目录结构

修复后，应该看到以下目录结构：

```
init/
└── data/
    ├── testcase/      # 测试用例文件
    ├── file/          # 用户上传文件
    ├── log/           # 日志文件
    ├── run/           # 判题运行目录
    ├── spj/           # Special Judge
    └── interactive/   # 交互式判题
```

## 常见错误信息

### 错误 1: "No such file or directory"

```
Error: /goj/testcase: No such file or directory
```

**解决方案**：运行 `init-dirs.sh` 创建目录

### 错误 2: "Permission denied"

```
Error: /goj/testcase: Permission denied
```

**解决方案**：
```bash
chmod -R 777 ./data
```

### 错误 3: "Directory is empty"

```
Warning: /goj/testcase is empty
```

**说明**：这是正常的，测试用例会在上传题目时自动创建

## 预防措施

### 方法 1: 使用一键部署脚本

启动脚本已集成目录创建功能：

```bash
# Linux/macOS
./start.sh

# Windows
powershell -ExecutionPolicy Bypass -File start.ps1
```

### 方法 2: 添加到 .gitignore

确保 `init/.gitignore` 包含：

```
data/
```

这样数据目录不会被提交到 Git，但每次部署时需要重新创建。

### 方法 3: 使用 Docker 健康检查

Docker Compose 配置已包含健康检查，会自动检测服务状态。

## 进阶调试

### 查看容器内的实际挂载

```bash
docker-compose exec glowxq-oj mount | grep goj
```

### 查看目录权限

```bash
docker-compose exec glowxq-oj ls -la /goj
docker-compose exec glowxq-oj ls -la /judge
```

### 查看容器环境变量

```bash
docker-compose exec glowxq-oj env | grep -E "(GOJ|JUDGE)"
```

### 手动测试文件创建

```bash
# 在宿主机创建测试文件
echo "test" > data/testcase/test.txt

# 在容器内查看
docker-compose exec glowxq-oj cat /goj/testcase/test.txt
docker-compose exec glowxq-oj cat /judge/testcase/test.txt
```

## 相关文档

- [VOLUME_MAPPING_GUIDE.md](./VOLUME_MAPPING_GUIDE.md) - 详细的卷映射说明
- [README.md](./README.md) - 部署指南
- [init.md](./init.md) - 详细部署文档

## 需要帮助？

如果问题仍未解决，请：

1. 查看完整日志：`docker-compose logs glowxq-oj > oj.log`
2. 检查 Docker 版本：`docker --version && docker-compose --version`
3. 检查磁盘空间：`df -h`
4. 提供错误信息和环境信息以获取支持

