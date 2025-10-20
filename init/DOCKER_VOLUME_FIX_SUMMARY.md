# Docker 卷映射问题修复总结

## 修复内容

本次修复解决了 Docker Compose 配置中目录映射导致程序无法读取 `/goj/testcase` 的问题。

## 主要变更

### 1. 修改 docker-compose.yml

**变更前（使用 Docker 命名卷）：**
```yaml
volumes:
  - oj-testcase:/goj/testcase
  - oj-testcase:/judge/testcase
  - oj-data:/goj/file
  - oj-logs:/goj/log

volumes:
  oj-testcase:
    driver: local
    name: glowxq-oj-testcase
  oj-data:
    driver: local
    name: glowxq-oj-data
  oj-logs:
    driver: local
    name: glowxq-oj-logs
```

**变更后（使用宿主机目录映射）：**
```yaml
volumes:
  - ./data/testcase:/goj/testcase
  - ./data/testcase:/judge/testcase
  - ./data/file:/goj/file
  - ./data/log:/goj/log
  - ./data/log:/judge/log
  - ./data/run:/judge/run
  - ./data/spj:/judge/spj
  - ./data/interactive:/judge/interactive
```

### 2. 新增文件

| 文件 | 用途 |
|------|------|
| `init-dirs.sh` | Linux/macOS 数据目录初始化脚本 |
| `init-dirs.ps1` | Windows 数据目录初始化脚本 |
| `.gitignore` | 忽略数据目录和敏感文件 |
| `VOLUME_MAPPING_GUIDE.md` | 详细的卷映射说明文档 |
| `QUICK_FIX.md` | 快速修复指南 |
| `DOCKER_VOLUME_FIX_SUMMARY.md` | 本文档 |

### 3. 更新文件

| 文件 | 变更内容 |
|------|---------|
| `start.sh` | 集成数据目录创建功能 |
| `start.ps1` | 集成数据目录创建功能 |
| `README.md` | 更新部署步骤和文件说明 |

## 为什么要修改？

### Docker 命名卷的问题

1. **不可见性**：数据存储在 Docker 管理的位置，难以直接访问
2. **初始化问题**：首次创建时是空的，程序读取会报错
3. **调试困难**：无法直接查看测试用例文件
4. **备份不便**：需要使用 Docker 命令备份

### 宿主机目录映射的优势

1. **可见性**：数据在 `init/data/` 目录下，可直接访问
2. **易于调试**：可以直接查看和修改文件
3. **便于备份**：直接复制 `data` 目录即可
4. **权限可控**：可以手动设置目录权限

## 使用方法

### 新部署（推荐）

直接使用一键部署脚本，已自动集成目录创建：

```bash
# Linux/macOS
cd init
./start.sh

# Windows
cd init
powershell -ExecutionPolicy Bypass -File start.ps1
```

### 已有部署（需要迁移）

如果已经使用 Docker 卷部署，需要迁移数据：

```bash
# 1. 停止服务
docker-compose down

# 2. 创建数据目录
./init-dirs.sh  # Linux/macOS
# 或
powershell -ExecutionPolicy Bypass -File init-dirs.ps1  # Windows

# 3. 从 Docker 卷复制数据（可选）
docker run --rm -v glowxq-oj-testcase:/source -v $(pwd)/data/testcase:/dest alpine cp -r /source/. /dest/
docker run --rm -v glowxq-oj-data:/source -v $(pwd)/data/file:/dest alpine cp -r /source/. /dest/
docker run --rm -v glowxq-oj-logs:/source -v $(pwd)/data/log:/dest alpine cp -r /source/. /dest/

# 4. 删除旧的 Docker 卷（可选）
docker volume rm glowxq-oj-testcase glowxq-oj-data glowxq-oj-logs

# 5. 重新启动服务
docker-compose up -d
```

### 手动部署

```bash
# 1. 初始化目录
cd init
./init-dirs.sh  # Linux/macOS
# 或
powershell -ExecutionPolicy Bypass -File init-dirs.ps1  # Windows

# 2. 启动服务
docker-compose up -d
```

## 目录结构

```
init/
├── data/                           # 数据根目录（新增）
│   ├── testcase/                  # 测试用例文件
│   ├── file/                      # 用户上传文件
│   ├── log/                       # 日志文件
│   ├── run/                       # 判题运行目录
│   ├── spj/                       # Special Judge
│   └── interactive/               # 交互式判题
├── docker-compose.yml             # Docker Compose 配置（已修改）
├── init-dirs.sh                   # 目录初始化脚本（新增）
├── init-dirs.ps1                  # 目录初始化脚本（新增）
├── start.sh                       # 启动脚本（已更新）
├── start.ps1                      # 启动脚本（已更新）
├── README.md                      # 部署指南（已更新）
├── .gitignore                     # Git 忽略配置（新增）
├── VOLUME_MAPPING_GUIDE.md        # 卷映射详细说明（新增）
├── QUICK_FIX.md                   # 快速修复指南（新增）
└── DOCKER_VOLUME_FIX_SUMMARY.md   # 本文档（新增）
```

## 验证修复

### 1. 检查目录是否创建

```bash
ls -la data/
```

应该看到：
```
drwxrwxrwx  testcase/
drwxrwxrwx  file/
drwxrwxrwx  log/
drwxrwxrwx  run/
drwxrwxrwx  spj/
drwxrwxrwx  interactive/
```

### 2. 检查容器内挂载

```bash
docker-compose exec glowxq-oj ls -la /goj/testcase
docker-compose exec glowxq-oj ls -la /judge/testcase
```

### 3. 测试文件读写

```bash
# 在宿主机创建测试文件
echo "test" > data/testcase/test.txt

# 在容器内读取
docker-compose exec glowxq-oj cat /goj/testcase/test.txt
```

## 常见问题

### Q1: 为什么选择目录映射而不是 Docker 卷？

A: 主要考虑：
- 便于调试和查看测试用例文件
- 便于备份和迁移数据
- 解决首次部署时目录为空的问题
- 更符合开发和测试环境的需求

### Q2: 生产环境也推荐使用目录映射吗？

A: 两种方式各有优劣：
- **开发/测试环境**：推荐目录映射（便于调试）
- **生产环境**：可以考虑 Docker 卷（更好的隔离性）

如需切换回 Docker 卷，参考 `VOLUME_MAPPING_GUIDE.md` 中的说明。

### Q3: 数据目录会被提交到 Git 吗？

A: 不会，已在 `.gitignore` 中忽略 `data/` 目录。

### Q4: 如何备份数据？

A: 直接备份 `data` 目录：
```bash
tar -czf backup-$(date +%Y%m%d).tar.gz data/
```

### Q5: 权限问题怎么解决？

A: Linux/macOS 下运行：
```bash
chmod -R 777 data/
```

## 技术细节

### volumes 配置解释

```yaml
volumes:
  - ./data/testcase:/goj/testcase
```

- `./data/testcase`：宿主机路径（相对于 docker-compose.yml）
- `/goj/testcase`：容器内路径
- 两个路径会同步，修改任一方都会反映到另一方

### 为什么同一个目录映射到两个路径？

```yaml
- ./data/testcase:/goj/testcase
- ./data/testcase:/judge/testcase
```

因为应用代码中有两个路径引用：
- `/goj/testcase`：应用层使用（FilePath.TESTCASE_BASE_FOLDER）
- `/judge/testcase`：判题层使用（JudgeDir.TEST_CASE_DIR）

映射到同一个宿主机目录可以节省空间并保持数据一致性。

## 回滚方案

如果需要回滚到 Docker 卷方式：

1. 编辑 `docker-compose.yml`
2. 恢复 volumes 部分的注释
3. 修改 glowxq-oj 服务的 volumes 配置
4. 运行：
```bash
docker-compose down
docker-compose up -d
```

## 相关文档

- [VOLUME_MAPPING_GUIDE.md](./VOLUME_MAPPING_GUIDE.md) - 详细技术说明
- [QUICK_FIX.md](./QUICK_FIX.md) - 快速修复指南
- [README.md](./README.md) - 部署指南
- [Docker Volumes 官方文档](https://docs.docker.com/storage/volumes/)

## 更新日志

- **2025-01-20**: 初始版本，修复目录映射问题
  - 将 Docker 命名卷改为宿主机目录映射
  - 新增目录初始化脚本
  - 更新启动脚本和文档

