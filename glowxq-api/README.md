# Glowxq-OJ - 在线编程测评系统

<div align="center">

**面向信奥赛、少儿编程教学的在线测评平台**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)](https://www.docker.com/)

[在线体验](https://oj.glowxq.com) | [问题反馈](https://github.com/glowxq/glowxq-oj/issues)

</div>

## 项目简介

Glowxq-OJ 是一个专为 **信息学奥林匹克竞赛（信奥赛）** 和 **少儿编程教学** 设计的在线编程测评系统。支持多语言判题、安全沙箱隔离、班级管理、竞赛系统等完整功能，开箱即用。

### 在线体验

- **体验地址**: [https://oj.glowxq.com](https://oj.glowxq.com)

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | 13667753053 | 123456 |
| 学生 | 13667753055 | 123456 |
| 老师 | 13667753056 | 123456 |

> 生产环境请务必修改默认密码。

---

## 快速开始

### 环境要求

- Docker 20.x+
- Docker Compose v2+
- Maven 3.8+（用于编译）
- JDK 21+（用于编译）

### 一键部署（推荐）

整个项目（MySQL、Redis、MinIO、OJ API）通过一条命令即可启动：

```bash
# 1. 克隆项目
git clone https://github.com/glowxq/glowxq-oj.git
cd glowxq-oj/glowxq-api

# 2. 一键部署
bash script/deploy.sh
```

`deploy.sh` 会自动完成以下步骤：
1. 检查 Docker / Docker Compose 环境
2. 从 `.env.example` 生成 `.env` 配置文件（自动生成随机 JWT 密钥）
3. Maven 编译项目
4. `docker compose up -d --build` 启动所有服务
5. 等待服务就绪并打印访问地址

部署完成后：

| 服务 | 地址 |
|------|------|
| API 接口 | http://localhost:7101 |
| Swagger 文档 | http://localhost:7101/doc.html |
| MinIO 控制台 | http://localhost:9001 |

### 服务管理

```bash
cd script

# 停止所有服务
docker compose down

# 查看服务日志
docker compose logs -f oj-api

# 重启单个服务
docker compose restart oj-api

# 查看服务状态
docker compose ps
```

### 自定义配置

所有配置通过环境变量管理，编辑 `script/.env` 即可：

```bash
# 数据库
MYSQL_ROOT_PASSWORD=your_password
MYSQL_DATABASE=glowxq_oj

# Redis
REDIS_PASSWORD=your_password

# MinIO 对象存储
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin

# JWT 密钥（deploy.sh 已自动生成）
JWT_SECRET_KEY=your_random_key

# JVM 参数
JAVA_OPTS=-Xms256m -Xmx512m

# 飞书通知（可选）
# FEISHU_INTERNAL_WEBHOOK=https://open.feishu.cn/...
```

完整配置项参见 [`script/.env.example`](script/.env.example)。

---

## 本地开发

如果需要在本地 IDE 中开发调试，手动准备 MySQL 和 Redis 即可：

```bash
# 1. 准备数据库
#    创建 MySQL 数据库 glowxq_oj，导入 script/init.sql
#    启动 Redis 服务

# 2. 修改配置
#    编辑 app/app-oj/src/main/resources/config/dev/ 下的配置文件
#    设置正确的数据库和 Redis 连接信息

# 3. 编译项目
mvn clean install -DskipTests

# 4. 启动服务
mvn -pl app/app-oj spring-boot:run -Dspring-boot.run.profiles=dev
```

API 文档: http://localhost:7101/doc.html

---

## 技术架构

### 后端

| 技术 | 说明 |
|------|------|
| Java 21 | 基础语言，启用虚拟线程 |
| Spring Boot 3.x | 核心框架 |
| MyBatis-Flex | ORM 框架 |
| Sa-Token | 权限认证（JWT 模式） |
| Knife4j | API 文档 |
| MySQL 8.0 | 主数据库 |
| Redis 7 | 缓存 & 会话 |
| MinIO | 对象存储（兼容 S3 协议） |
| Docker | 容器化部署 |

### 判题系统

- **沙箱隔离**: go-judge 安全沙箱，限制系统调用和资源
- **支持语言**: C/C++、Java、Python
- **判题模式**: 普通判题、特殊判题（SPJ）、交互判题、文件 IO
- **并发处理**: 虚拟线程 + 异步队列

### 项目结构

```
glowxq-api/
├── app/                        # 应用启动模块
│   └── app-oj/                 # OJ 服务
│       ├── Dockerfile          # 容器构建
│       ├── run.sh              # 启动脚本
│       └── src/main/resources/
│           └── config/         # 多环境配置
│               ├── dev/        # 开发环境
│               ├── local/      # 本地环境
│               ├── docker/     # Docker 环境
│               ├── preview/    # 预发布环境
│               └── prod/       # 生产环境
├── business/                   # 业务逻辑模块
│   └── business-oj/            # OJ 核心业务
├── common/                     # 公共模块
│   ├── common-core/            # 核心工具 & 基础实体
│   ├── common-db-mysql/        # MySQL 配置
│   ├── common-db-redis/        # Redis 配置
│   ├── common-security/        # 认证授权（Sa-Token）
│   ├── common-oss/             # 对象存储（MinIO/阿里云）
│   ├── common-wechat/          # 微信集成
│   ├── common-excel/           # Excel 导入导出
│   └── common-log/             # 日志模块
├── script/                     # 部署脚本
│   ├── docker-compose.yml      # Docker Compose 编排
│   ├── deploy.sh               # 一键部署脚本
│   ├── .env.example            # 环境变量模板
│   └── init.sql                # 数据库初始化
└── dependencies/               # 依赖版本管理
```

---

## 核心功能

### 题目管理
- 题目类型：算法题、编程题、选择题、填空题
- 难度分级：入门、普及、提高、省选、NOI
- 标签系统、批量导入（兼容 HOJ / Codeforces 格式）
- 富文本编辑器，支持 LaTeX 数学公式
- 可视化测试用例管理

### 在线测评
- 多语言支持：C/C++、Java、Python
- 实时异步判题，支持高并发
- 判题模式：标准对比、SPJ、交互判题、文件 IO
- 安全沙箱隔离，限制系统调用和资源
- 代码查重检测

### 竞赛系统
- ACM/ICPC 模式（实时排名 + 罚时）
- OI 模式（最终测试 + 按分排名）
- IOI 模式（部分分）
- 实时榜单、封榜功能

### 用户 & 权限
- 多角色：超级管理员、管理员、校长、教师、学生
- 基于 RBAC 的细粒度权限控制
- 个人中心：提交记录、错题本、学习统计

### 教学管理
- 班级管理、作业布置与批改
- 学习路径规划、知识点管理
- 数据统计与分析报告

---

## 贡献指南

欢迎参与贡献！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/your-feature`)
3. 提交更改 (`git commit -m 'feat: add your feature'`)
4. 推送分支 (`git push origin feature/your-feature`)
5. 创建 Pull Request

---

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。

## 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Flex](https://mybatis-flex.com/)
- [Sa-Token](https://sa-token.cc/)
- [sz-admin](https://github.com/fanhuibin/sz-admin)

---

<div align="center">

**如果这个项目对你有帮助，请给我们一个 Star！**

Made with love by GlowXQ Team

</div>
