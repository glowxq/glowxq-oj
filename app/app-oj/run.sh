#!/bin/bash

# 解除系统栈大小限制（防止判题时栈溢出）
ulimit -s unlimited

# 为沙盒程序添加执行权限（安全加固）
chmod +777 Sandbox-amd64
chmod +777 Sandbox-arm64

# 自动检测 CPU 架构选择对应版本的沙盒程序
get_arch=`arch`  # 获取系统架构信息

# 默认使用 x86 版本
file="Sandbox-amd64"
ENV="prod"

# 根据架构选择执行文件
if [[ $get_arch =~ "x86_64" ]];then
    file="Sandbox-amd64"
elif [[ $get_arch =~ "aarch64" ]];then
    file="Sandbox-arm64"
else
    file="Sandbox-amd64"  # 非兼容架构尝试使用 x86 版本
    # 输出警告信息（需注意兼容性）
    echo "!!!Warning: the security sandbox [go-judge] of judgeserver only supports the linux systems of amd64 and arm64, but the current system architecture is $get_arch."
    echo "!!!Warning: So now the amd64 version is used by default, and may cause the security sandbox [go-judge] to fail to start normally!"
fi

# 根据环境变量配置沙盒并行任务数
if test -z "$PARALLEL_TASK";then  # 变量为空时使用默认参数
    nohup ./$file --silent=true --file-timeout=5m &
    echo -e "\033[42;34m ./$file --silent=true --file-timeout=5m \033[0m"
elif [ -z "$(echo $PARALLEL_TASK | sed 's#[0-9]##g')" ]; then  # 纯数字时添加并行参数
    nohup ./$file --silent=true --file-timeout=5m --parallelism=$PARALLEL_TASK &
    echo -e "\033[42;34m ./$file --silent=true --file-timeout=5m --parallelism=$PARALLEL_TASK \033[0m"
else  # 非法参数回退默认配置
    nohup ./$file --silent=true --file-timeout=5m &
    echo -e "\033[42;34m ./$file --silent=true --file-timeout=5m \033[0m"
fi

# 启动 Java 判题服务（JAR 文件）
if test -z "$JAVA_OPTS";then  # 无自定义 JVM 参数时
    # 使用 urandom 作为熵源加速启动
    java -Djava.security.egd=file:/dev/./urandom -jar ./app.jar --spring.profiles.active=$ENV
else  # 使用自定义内存参数
    java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar ./app.jar --spring.profiles.active=$ENV
fi