FROM ubuntu:18.04

ARG DEBIAN_FRONTEND=noninteractive

ENV TZ=Asia/Shanghai

# 安装需要的编译器和运行时依赖
# gcc 用于编译和运行 C C++ 代码，python3 用于运行 Python 代码，wget和tar用于安装Java
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    python3 \
    wget \
    tar \
    curl \
    ca-certificates \
    file \
    gzip \
    && rm -rf /var/lib/apt/lists/*

# 下载并安装OpenJDK 21
# 使用更简单可靠的方法，分步骤进行以便调试
RUN mkdir -p /usr/local/java

# 第一步：尝试下载OpenJDK
RUN echo "Downloading OpenJDK 21..." && \
    # 方法1：从GitHub Adoptium下载（最可靠）
    (wget --timeout=60 --tries=3 --progress=dot:giga \
        -O /tmp/openjdk-21.tar.gz \
        "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_linux_hotspot_21.0.2_13.tar.gz" || \
    # 方法2：从Eclipse Adoptium官方API下载
    wget --timeout=60 --tries=3 --progress=dot:giga \
        -O /tmp/openjdk-21.tar.gz \
        "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse" || \
    # 方法3：从华为云镜像下载
    wget --timeout=60 --tries=3 --progress=dot:giga \
        -O /tmp/openjdk-21.tar.gz \
        "https://repo.huaweicloud.com/openjdk/21.0.2/openjdk-21.0.2_linux-x64_bin.tar.gz" || \
    # 方法4：从阿里云镜像下载
    wget --timeout=60 --tries=3 --progress=dot:giga \
        -O /tmp/openjdk-21.tar.gz \
        "https://mirrors.aliyun.com/openjdk/21.0.2/openjdk-21.0.2_linux-x64_bin.tar.gz" || \
    # 如果所有方法都失败，退出
    (echo "ERROR: Failed to download OpenJDK from all sources" && exit 1))

# 第二步：验证下载的文件
RUN echo "Verifying downloaded file..." && \
    ls -la /tmp/openjdk-21.tar.gz && \
    # 检查文件大小（应该大于50MB）
    FILE_SIZE=$(stat -c%s /tmp/openjdk-21.tar.gz) && \
    echo "File size: $FILE_SIZE bytes" && \
    [ "$FILE_SIZE" -gt 52428800 ] && \
    # 验证文件类型
    file /tmp/openjdk-21.tar.gz && \
    # 验证gzip文件完整性
    echo "Testing gzip integrity..." && \
    gzip -t /tmp/openjdk-21.tar.gz && \
    echo "File verification passed"

# 第三步：解压并安装
RUN echo "Extracting OpenJDK..." && \
    tar -xzf /tmp/openjdk-21.tar.gz -C /usr/local/java --strip-components 1 && \
    # 清理临时文件
    rm -f /tmp/openjdk-21.tar.gz && \
    # 验证Java安装
    echo "Verifying Java installation..." && \
    /usr/local/java/bin/java -version

# 设置Java环境变量
ENV JAVA_HOME=/usr/local/java
ENV PATH=$JAVA_HOME/bin:$PATH

RUN mkdir -p /judge/testcase /judge/testcase /judge/run /judge/spj /judge/log


# 设置构建参数
ARG SPRING_PROFILES_ACTIVE

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY  ./app/app-oj/target/*.jar /judge/server/app.jar

COPY ./app/app-oj/run.sh /judge/server/run.sh

COPY ./app/app-oj/testlib.h /usr/include/testlib.h

ADD ./app/app-oj/Sandbox-amd64 /judge/server/Sandbox-amd64
ADD ./app/app-oj/Sandbox-arm64 /judge/server/Sandbox-arm64
	
WORKDIR /judge/server

ENTRYPOINT ["bash", "./run.sh"]

EXPOSE 7101

EXPOSE 5050