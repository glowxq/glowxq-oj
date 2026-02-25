# ============================================================
# GlowXQ OJ Backend - 多阶段构建
# Stage 1: Maven 编译（容器内编译，不依赖宿主机 Java）
# Stage 2: 运行时镜像（JDK 21 + go-judge + 编译器）
# ============================================================

# ==================== Stage 1: Maven 编译 ====================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# 先复制所有 pom.xml，利用 Docker 层缓存加速依赖下载
# 根 pom
COPY pom.xml .
COPY build/pom.xml build/pom.xml

# dependencies 模块
COPY dependencies/pom.xml dependencies/pom.xml

# common 模块
COPY common/pom.xml common/pom.xml
COPY common/common-core/pom.xml common/common-core/pom.xml
COPY common/common-business/pom.xml common/common-business/pom.xml
COPY common/common-db-mysql/pom.xml common/common-db-mysql/pom.xml
COPY common/common-db-redis/pom.xml common/common-db-redis/pom.xml
COPY common/common-db-mongodb/pom.xml common/common-db-mongodb/pom.xml
COPY common/common-excel/pom.xml common/common-excel/pom.xml
COPY common/common-generator/pom.xml common/common-generator/pom.xml
COPY common/common-log/pom.xml common/common-log/pom.xml
COPY common/common-mq/pom.xml common/common-mq/pom.xml
COPY common/common-oss/pom.xml common/common-oss/pom.xml
COPY common/common-security/pom.xml common/common-security/pom.xml
COPY common/common-tenant/pom.xml common/common-tenant/pom.xml
COPY common/common-websocket/pom.xml common/common-websocket/pom.xml
COPY common/common-wechat/pom.xml common/common-wechat/pom.xml

# business 模块
COPY business/pom.xml business/pom.xml
COPY business/business-oj/pom.xml business/business-oj/pom.xml
COPY business/business-system/pom.xml business/business-system/pom.xml

# app 模块
COPY app/pom.xml app/pom.xml
COPY app/app-oj/pom.xml app/app-oj/pom.xml

# 下载依赖（此层会被缓存，只要 pom 不变就不会重新下载）
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn dependency:go-offline -DskipTests -q 2>/dev/null || true

# 复制全部源码并编译
COPY . .

RUN --mount=type=cache,target=/root/.m2/repository \
    mvn clean package -DskipTests -q

# ==================== Stage 2: 运行时镜像 ====================
FROM eclipse-temurin:21-jdk-jammy

ARG DEBIAN_FRONTEND=noninteractive

ENV TZ=Asia/Shanghai

# 安装需要的编译器和运行时依赖
# gcc 用于编译和运行 C/C++ 代码，python3 用于运行 Python 代码
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    python3 \
    && rm -rf /var/lib/apt/lists/*

# 创建 judge 沙盒目录
RUN mkdir -p /judge/testcase /judge/run /judge/spj /judge/log

# 创建 goj 应用数据目录（FilePath 枚举定义的所有路径）
RUN mkdir -p /goj/testcase \
    /goj/file/avatar/group \
    /goj/file/carousel \
    /goj/file/md \
    /goj/file/problem/upload/tmp \
    /goj/file/zip/download \
    /goj/file/zip/contest_ac \
    /goj/file/contest_print

# 设置默认 profile 为 prod
ENV SPRING_PROFILES_ACTIVE=prod

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 从 builder 阶段复制编译产物
COPY --from=builder /build/app/app-oj/target/*.jar /judge/server/app.jar

COPY ./app/app-oj/run.sh /judge/server/run.sh

COPY ./app/app-oj/testlib.h /usr/include/testlib.h

ADD ./app/app-oj/Sandbox-amd64 /judge/server/Sandbox-amd64
ADD ./app/app-oj/Sandbox-arm64 /judge/server/Sandbox-arm64

WORKDIR /judge/server

ENTRYPOINT ["bash", "./run.sh"]

EXPOSE 7101

EXPOSE 5050
