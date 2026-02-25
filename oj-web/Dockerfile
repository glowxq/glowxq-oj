# 使用官方 Node.js 作为基础镜像
FROM node:20-alpine


# 在容器中创建目录
WORKDIR /app

# 将package.json和package-lock.json复制到工作目录
COPY package*.json ./

# 使用pnpm 安装项目依赖
RUN npm install -g pnpm

# pnpm 安装依赖
RUN pnpm install

# 将项目源代码复制到工作目录
COPY . .

# 设置应用名
ARG APP_NAME

# Docker 部署时覆盖 API 地址为相对路径（由 nginx 代理到本地 oj-api）
# 不修改 env/.env.oj 源文件，通过环境变量覆盖 Vite 的 env 配置
ARG VITE_API_URL=/api
ENV VITE_API_URL=${VITE_API_URL}
# WebSocket URL 留空，前端自动根据 window.location 推导
# ENV VITE_SOCKET_URL=

# 构建版本号
ARG APP_BUILD_VERSION=dev
ENV VITE_APP_BUILD_VERSION=${APP_BUILD_VERSION}

# 构建项目
RUN pnpm build-only --mode ${APP_NAME}


# 使用nginx作为服务
FROM nginx:stable-alpine as production-stage

# 将构建好的项目复制到nginx下
COPY --from=0 /app/dist /usr/share/nginx/html

# 将默认的nginx配置文件替换为我们自定义的nginx配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80
EXPOSE 443

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
