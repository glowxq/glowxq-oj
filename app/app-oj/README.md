# Glowxq-OJ Docker 构建说明

## 🚀 Dockerfile 优化

### 问题背景
原始Dockerfile包含194MB的OpenJDK本地文件，超过了GitHub的100MB文件大小限制，导致推送失败。

### 解决方案
1. **移除本地大文件**: 删除`openjdk-21.0.2_linux-x64_bin.tar.gz`
2. **网络下载**: 改为在构建时从网络下载OpenJDK
3. **多镜像源**: 配置多个下载源确保国内外都能访问

### 下载源优先级

#### 国内镜像源（优先）
1. **腾讯云镜像** - 最稳定，速度快
2. **华为云镜像** - 企业级稳定性
3. **阿里云镜像** - 覆盖面广
4. **清华大学镜像** - 教育网友好

#### 国外镜像源（备选）
5. **GitHub Adoptium** - 官方推荐，最可靠
6. **Oracle官方** - 原厂支持

### 构建特性
- ✅ **超时控制**: 每个源30-60秒超时
- ✅ **重试机制**: 自动重试2-3次
- ✅ **文件验证**: 验证下载文件完整性
- ✅ **安装验证**: 验证Java安装成功
- ✅ **自动清理**: 清理临时文件

## 🛠️ 构建命令

### 本地构建
```bash
# 在项目根目录执行
docker build -f app/app-oj/Dockerfile -t glowxq-oj:latest .
```

### 构建参数
```bash
# 指定Spring Profile
docker build -f app/app-oj/Dockerfile \
  --build-arg SPRING_PROFILES_ACTIVE=prod \
  -t glowxq-oj:prod .
```

## 🌐 网络要求

### 国内环境
- 优先使用腾讯云、华为云、阿里云镜像
- 通常在30秒内完成下载

### 国外环境
- 自动切换到GitHub Adoptium或Oracle官方源
- 可能需要60秒左右完成下载

### 网络受限环境
如果所有网络源都无法访问，可以：
1. 手动下载OpenJDK到本地
2. 临时修改Dockerfile使用本地文件
3. 构建完成后恢复网络下载版本

## 📦 包含的组件

### 编译器和运行时
- **GCC/G++**: C/C++代码编译和运行
- **Python3**: Python代码运行
- **OpenJDK 21**: Java代码编译和运行

### 判题环境
- **Sandbox**: 安全沙箱（支持AMD64和ARM64）
- **testlib.h**: 特殊判题库
- **判题目录**: `/judge/testcase`, `/judge/run`, `/judge/spj`, `/judge/log`

### 应用服务
- **Spring Boot应用**: 主要的OJ服务
- **端口**: 7101（Web服务）, 5050（调试端口）

## 🔧 故障排除

### 下载失败
如果所有镜像源都失败：
```bash
# 检查网络连接
curl -I https://github.com/adoptium/temurin21-binaries/releases/

# 手动下载并放置到构建上下文
wget https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_linux_hotspot_21.0.2_13.tar.gz
```

### 构建缓慢
- 国内环境建议使用代理或VPN
- 可以预先拉取基础镜像：`docker pull ubuntu:18.04`

### 验证安装
```bash
# 进入容器验证Java
docker run -it glowxq-oj:latest java -version
```

## 📝 更新日志

### v1.1.0 (2025-08-27)
- ✅ 移除194MB本地OpenJDK文件
- ✅ 实现网络下载多镜像源支持
- ✅ 添加下载验证和错误处理
- ✅ 优化构建速度和稳定性
- ✅ 解决GitHub文件大小限制问题

---

**维护团队**: GlowXQ Team  
**更新时间**: 2025-08-27
