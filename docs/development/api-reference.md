# API 接口文档

## 📖 概述

Glowxq-OJ 提供完整的 RESTful API 接口，支持第三方系统集成和自定义客户端开发。

## 🔐 认证方式

### Token 认证
系统使用 Sa-Token 进行身份认证，支持以下认证方式：

1. **Header 认证**（推荐）
```http
Authorization: Bearer {token}
```

2. **参数认证**
```http
GET /api/user/info?satoken={token}
```

3. **Cookie 认证**
```http
Cookie: satoken={token}
```

### 获取 Token
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "your_username",
    "password": "your_password"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "userInfo": {
            "id": 1,
            "username": "admin",
            "nickname": "管理员",
            "role": "admin"
        }
    }
}
```

## 📋 通用响应格式

所有 API 接口都遵循统一的响应格式：

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": 1640995200000
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 👤 用户相关接口

### 用户登录
```http
POST /api/auth/login
```

**请求参数**:
```json
{
    "username": "string",
    "password": "string",
    "captcha": "string",
    "captchaId": "string"
}
```

### 用户注册
```http
POST /api/auth/register
```

**请求参数**:
```json
{
    "username": "string",
    "password": "string",
    "email": "string",
    "nickname": "string"
}
```

### 获取用户信息
```http
GET /api/user/info
```

**响应示例**:
```json
{
    "code": 200,
    "data": {
        "id": 1,
        "username": "admin",
        "nickname": "管理员",
        "email": "admin@example.com",
        "avatar": "http://example.com/avatar.jpg",
        "role": "admin",
        "createTime": "2024-01-01T00:00:00"
    }
}
```

### 修改用户信息
```http
PUT /api/user/info
```

**请求参数**:
```json
{
    "nickname": "string",
    "email": "string",
    "avatar": "string"
}
```

## 📝 题目相关接口

### 获取题目列表
```http
GET /api/problem/list
```

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页大小（默认20）
- `keyword`: 搜索关键词
- `difficulty`: 难度筛选（1-简单，2-中等，3-困难）
- `tags`: 标签筛选（逗号分隔）
- `status`: 状态筛选（0-未尝试，1-已通过，2-尝试过）

**响应示例**:
```json
{
    "code": 200,
    "data": {
        "total": 100,
        "records": [
            {
                "id": 1,
                "title": "A+B Problem",
                "difficulty": 1,
                "tags": ["入门", "数学"],
                "acceptCount": 1000,
                "submitCount": 1200,
                "acceptRate": 83.33
            }
        ]
    }
}
```

### 获取题目详情
```http
GET /api/problem/{id}
```

**响应示例**:
```json
{
    "code": 200,
    "data": {
        "id": 1,
        "title": "A+B Problem",
        "description": "计算两个整数的和",
        "inputFormat": "两个整数 a 和 b",
        "outputFormat": "输出 a+b 的结果",
        "samples": [
            {
                "input": "1 2",
                "output": "3"
            }
        ],
        "timeLimit": 1000,
        "memoryLimit": 256,
        "difficulty": 1,
        "tags": ["入门", "数学"]
    }
}
```

### 创建题目
```http
POST /api/problem
```

**请求参数**:
```json
{
    "title": "string",
    "description": "string",
    "inputFormat": "string",
    "outputFormat": "string",
    "samples": [
        {
            "input": "string",
            "output": "string"
        }
    ],
    "timeLimit": 1000,
    "memoryLimit": 256,
    "difficulty": 1,
    "tags": ["string"]
}
```

## 🏆 提交相关接口

### 提交代码
```http
POST /api/submit
```

**请求参数**:
```json
{
    "problemId": 1,
    "language": "cpp",
    "code": "string",
    "contestId": 1
}
```

**响应示例**:
```json
{
    "code": 200,
    "data": {
        "submitId": 12345,
        "status": "Pending"
    }
}
```

### 获取提交记录
```http
GET /api/submit/list
```

**查询参数**:
- `page`: 页码
- `size`: 每页大小
- `problemId`: 题目ID
- `userId`: 用户ID
- `language`: 编程语言
- `status`: 判题状态

**响应示例**:
```json
{
    "code": 200,
    "data": {
        "total": 50,
        "records": [
            {
                "id": 12345,
                "problemId": 1,
                "problemTitle": "A+B Problem",
                "userId": 1,
                "username": "admin",
                "language": "cpp",
                "status": "Accepted",
                "time": 100,
                "memory": 1024,
                "submitTime": "2024-01-01T12:00:00"
            }
        ]
    }
}
```

### 获取提交详情
```http
GET /api/submit/{id}
```

**响应示例**:
```json
{
    "code": 200,
    "data": {
        "id": 12345,
        "problemId": 1,
        "userId": 1,
        "language": "cpp",
        "code": "#include <iostream>\nusing namespace std;\nint main() {\n    int a, b;\n    cin >> a >> b;\n    cout << a + b << endl;\n    return 0;\n}",
        "status": "Accepted",
        "time": 100,
        "memory": 1024,
        "score": 100,
        "compileInfo": "",
        "testCases": [
            {
                "status": "Accepted",
                "time": 10,
                "memory": 1024,
                "score": 100
            }
        ]
    }
}
```

## 🏆 竞赛相关接口

### 获取竞赛列表
```http
GET /api/contest/list
```

**查询参数**:
- `page`: 页码
- `size`: 每页大小
- `status`: 竞赛状态（upcoming, running, ended）

### 获取竞赛详情
```http
GET /api/contest/{id}
```

### 参加竞赛
```http
POST /api/contest/{id}/join
```

### 获取竞赛排行榜
```http
GET /api/contest/{id}/ranklist
```

## 📊 统计相关接口

### 获取用户统计
```http
GET /api/statistics/user/{userId}
```

**响应示例**:
```json
{
    "code": 200,
    "data": {
        "totalSubmit": 100,
        "totalAccept": 80,
        "acceptRate": 80.0,
        "solvedProblems": 50,
        "languageStats": {
            "cpp": 60,
            "java": 30,
            "python": 10
        },
        "difficultyStats": {
            "easy": 30,
            "medium": 15,
            "hard": 5
        }
    }
}
```

### 获取系统统计
```http
GET /api/statistics/system
```

## 🔧 管理员接口

### 用户管理
```http
GET /api/admin/user/list
POST /api/admin/user
PUT /api/admin/user/{id}
DELETE /api/admin/user/{id}
```

### 题目管理
```http
GET /api/admin/problem/list
POST /api/admin/problem
PUT /api/admin/problem/{id}
DELETE /api/admin/problem/{id}
```

### 系统配置
```http
GET /api/admin/config
PUT /api/admin/config
```

## 📡 WebSocket 接口

### 实时判题结果
```javascript
// 连接WebSocket
const ws = new WebSocket('ws://localhost:7101/ws/judge');

// 监听判题结果
ws.onmessage = function(event) {
    const result = JSON.parse(event.data);
    console.log('判题结果:', result);
};

// 订阅提交结果
ws.send(JSON.stringify({
    type: 'subscribe',
    submitId: 12345
}));
```

### 竞赛实时排名
```javascript
const ws = new WebSocket('ws://localhost:7101/ws/contest/1');

ws.onmessage = function(event) {
    const ranking = JSON.parse(event.data);
    console.log('实时排名:', ranking);
};
```

## 🔍 错误处理

### 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 10001 | 参数验证失败 | 检查请求参数格式 |
| 10002 | 用户不存在 | 确认用户ID正确 |
| 10003 | 密码错误 | 检查密码是否正确 |
| 10004 | 权限不足 | 确认用户权限 |
| 10005 | 题目不存在 | 确认题目ID正确 |
| 10006 | 竞赛不存在 | 确认竞赛ID正确 |

### 错误响应示例
```json
{
    "code": 10001,
    "message": "参数验证失败",
    "data": {
        "field": "username",
        "error": "用户名不能为空"
    },
    "timestamp": 1640995200000
}
```

## 📚 SDK 和示例

### Java SDK 示例
```java
// 初始化客户端
GlowxqOjClient client = new GlowxqOjClient("http://localhost:7101", "your_token");

// 获取题目列表
ProblemListResponse problems = client.getProblemList(1, 20);

// 提交代码
SubmitResponse submit = client.submitCode(1, "cpp", code);
```

### Python SDK 示例
```python
from glowxq_oj import GlowxqOjClient

# 初始化客户端
client = GlowxqOjClient("http://localhost:7101", "your_token")

# 获取题目列表
problems = client.get_problem_list(page=1, size=20)

# 提交代码
submit = client.submit_code(problem_id=1, language="python", code=code)
```

## 📞 技术支持

如需API技术支持，请联系：
- GitHub Issues: [提交问题](https://github.com/glowxq/glowxq-oj/issues)
- 技术交流群: 见README.md
- 邮箱: api-support@glowxq.com

---

**API版本**: v1.0  
**更新时间**: 2025-08-27  
**维护团队**: GlowXQ Team
