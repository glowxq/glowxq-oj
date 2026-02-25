#!/bin/bash

while :
    do
        # 访问nacos注册中心，获取http状态码
        CODE=`curl -I -m 10 -o /dev/null -s -w %{http_code}  http://$NACOS_URL/nacos/index.html`
        # 判断状态码为200
        if [[ $CODE -eq 200 ]]; then
            # 输出绿色文字，并跳出循环
            echo -e "\033[42;34m nacos is ok \033[0m"
            break
        else
            # 暂停1秒
            sleep 1
        fi
    done

# while结束时，执行容器中的run.sh。
bash ./run.sh
