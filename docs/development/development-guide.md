# 开发指南

本文档为开发者提供详细的开发环境搭建和开发规范指导。

## 🛠️ 开发环境搭建

### 必需软件

1. **JDK 21**
```bash
# 下载并安装 OpenJDK 21
# 设置环境变量
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

2. **Maven 3.8+**
```bash
# 下载并安装 Maven
# 配置阿里云镜像加速
# ~/.m2/settings.xml
<mirrors>
    <mirror>
        <id>aliyunmaven</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

3. **Node.js 16+**
```bash
# 安装 Node.js
# 配置npm镜像
npm config set registry https://registry.npmmirror.com
```

4. **MySQL 8.0**
```bash
# 安装MySQL
# 创建开发数据库
CREATE DATABASE glowxq_oj_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE glowxq_system_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

5. **Redis 6.0+**
```bash
# 安装Redis
redis-server --daemonize yes
```

### IDE配置

#### IntelliJ IDEA（推荐）

1. **必装插件**
   - Lombok Plugin
   - MyBatis Plugin
   - Docker Plugin
   - GitToolBox
   - Rainbow Brackets
   - Translation

2. **代码风格配置**
```xml
<!-- 导入 build/baeldung-style.xml -->
<!-- File -> Settings -> Editor -> Code Style -> Java -> Import Scheme -->
```

3. **运行配置**
```
# VM Options
-Dspring.profiles.active=dev
-Dfile.encoding=UTF-8
-Djava.awt.headless=true

# Environment Variables
SPRING_PROFILES_ACTIVE=dev
```

#### VS Code

1. **推荐插件**
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Docker
   - GitLens

2. **配置文件**
```json
// .vscode/settings.json
{
    "java.home": "/path/to/jdk-21",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/path/to/jdk-21"
        }
    ]
}
```

## 🏗️ 项目结构详解

```
glowxq-api/
├── app/                           # 应用启动模块
│   ├── app-oj/                   # OJ服务
│   │   ├── src/main/java/        # Java源码
│   │   ├── src/main/resources/   # 配置文件
│   │   └── pom.xml              # Maven配置
│   └── app-system/              # 系统管理服务
├── business/                     # 业务逻辑模块
│   ├── business-oj/             # OJ业务
│   │   ├── controller/          # 控制器层
│   │   ├── service/             # 服务层
│   │   ├── mapper/              # 数据访问层
│   │   └── entity/              # 实体类
│   └── business-system/         # 系统业务
├── common/                      # 公共模块
│   ├── common-core/            # 核心工具
│   ├── common-db-mysql/        # MySQL配置
│   ├── common-db-redis/        # Redis配置
│   ├── common-security/        # 安全认证
│   └── common-websocket/       # WebSocket
└── dependencies/               # 依赖管理
```

## 📝 开发规范

### 代码规范

#### 1. 命名规范

```java
// 类名：大驼峰命名
public class ProblemController {}

// 方法名：小驼峰命名
public void createProblem() {}

// 常量：全大写，下划线分隔
public static final String DEFAULT_LANGUAGE = "java";

// 包名：全小写，点分隔
package com.glowxq.oj.problem.controller;
```

#### 2. 注释规范

```java
/**
 * 题目管理控制器
 * 
 * @author glowxq
 * @since 2024-01-01
 */
@RestController
public class ProblemController {
    
    /**
     * 创建题目
     * 
     * @param dto 题目创建DTO
     * @return 创建结果
     */
    @PostMapping
    public ApiResult<Void> create(@RequestBody @Valid ProblemCreateDTO dto) {
        // 实现逻辑
        return ApiResult.success();
    }
}
```

#### 3. 异常处理

```java
// 业务异常
public class BusinessException extends RuntimeException {
    private final String code;
    private final String message;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}

// 全局异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        return ApiResult.error(e.getCode(), e.getMessage());
    }
}
```

### 数据库规范

#### 1. 表设计规范

```sql
-- 表名：小写，下划线分隔
CREATE TABLE problem_info (
    -- 主键：id，自增
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 字段名：小写，下划线分隔
    problem_title VARCHAR(255) NOT NULL COMMENT '题目标题',
    problem_content TEXT COMMENT '题目内容',
    
    -- 通用字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    del_flag ENUM('T', 'F') DEFAULT 'F' COMMENT '删除标识',
    tenant_id VARCHAR(64) DEFAULT 'GLOWXQ' COMMENT '租户ID'
) COMMENT '题目信息表';

-- 索引命名：idx_字段名
CREATE INDEX idx_tenant_id ON problem_info(tenant_id);
CREATE INDEX idx_create_time ON problem_info(create_time);
```

#### 2. SQL编写规范

```java
// 使用MyBatis-Flex
@Mapper
public interface ProblemMapper extends BaseMapper<Problem> {
    
    // 复杂查询使用QueryWrapper
    default List<Problem> selectByCondition(ProblemQueryDTO dto) {
        return selectListByQuery(
            QueryWrapper.create()
                .select()
                .from(PROBLEM)
                .where(PROBLEM.TITLE.like(dto.getTitle()))
                .and(PROBLEM.DIFFICULTY.eq(dto.getDifficulty()))
                .orderBy(PROBLEM.CREATE_TIME.desc())
        );
    }
}
```

### API设计规范

#### 1. RESTful API

```java
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {
    
    // GET /api/v1/problems - 获取题目列表
    @GetMapping
    public ApiResult<PageResult<Problem>> list(@Valid ProblemQueryDTO dto) {}
    
    // GET /api/v1/problems/{id} - 获取单个题目
    @GetMapping("/{id}")
    public ApiResult<Problem> get(@PathVariable Long id) {}
    
    // POST /api/v1/problems - 创建题目
    @PostMapping
    public ApiResult<Void> create(@RequestBody @Valid ProblemCreateDTO dto) {}
    
    // PUT /api/v1/problems/{id} - 更新题目
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody @Valid ProblemUpdateDTO dto) {}
    
    // DELETE /api/v1/problems/{id} - 删除题目
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {}
}
```

#### 2. 统一响应格式

```java
// 成功响应
{
    "code": "200",
    "message": "操作成功",
    "data": {...},
    "timestamp": "2024-01-01T12:00:00Z"
}

// 错误响应
{
    "code": "400",
    "message": "参数错误",
    "data": null,
    "timestamp": "2024-01-01T12:00:00Z"
}
```

## 🧪 测试规范

### 单元测试

```java
@SpringBootTest
@Transactional
@Rollback
class ProblemServiceTest {
    
    @Autowired
    private ProblemService problemService;
    
    @Test
    @DisplayName("创建题目-成功")
    void testCreateProblem_Success() {
        // Given
        ProblemCreateDTO dto = new ProblemCreateDTO();
        dto.setTitle("测试题目");
        dto.setContent("题目内容");
        
        // When
        problemService.create(dto);
        
        // Then
        // 验证结果
    }
    
    @Test
    @DisplayName("创建题目-标题为空")
    void testCreateProblem_EmptyTitle() {
        // Given
        ProblemCreateDTO dto = new ProblemCreateDTO();
        dto.setTitle("");
        
        // When & Then
        assertThrows(BusinessException.class, () -> {
            problemService.create(dto);
        });
    }
}
```

### 集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProblemControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateProblem() {
        // 准备测试数据
        ProblemCreateDTO dto = new ProblemCreateDTO();
        dto.setTitle("集成测试题目");
        
        // 发送请求
        ResponseEntity<ApiResult> response = restTemplate.postForEntity(
            "/api/v1/problems", dto, ApiResult.class);
        
        // 验证响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200", response.getBody().getCode());
    }
}
```

## 🔧 调试技巧

### 1. 日志配置

```yaml
# application-dev.yml
logging:
  level:
    com.glowxq: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    com.mybatisflex: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 2. 远程调试

```bash
# 启动参数
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
```

### 3. 性能分析

```java
// 使用@Timed注解监控方法执行时间
@Timed(name = "problem.create", description = "创建题目耗时")
public void create(ProblemCreateDTO dto) {
    // 方法实现
}
```

## 🚀 部署流程

### 开发环境

```bash
# 1. 启动依赖服务
docker-compose -f docker-compose.dev.yml up -d mysql redis

# 2. 启动后端服务
cd app/app-oj
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. 启动前端服务
cd ../glowxq-oj-web
npm run dev
```

### 测试环境

```bash
# 1. 构建镜像
mvn clean package -DskipTests
docker build -t glowxq/oj:test .

# 2. 部署到测试环境
docker-compose -f docker-compose.test.yml up -d
```

### 生产环境

```bash
# 1. 构建生产镜像
mvn clean package -Pprod
docker build -t glowxq/oj:prod .

# 2. 推送到镜像仓库
docker push glowxq/oj:prod

# 3. 部署到生产环境
kubectl apply -f k8s/
```

## 📋 开发流程

### Git工作流

```bash
# 1. 创建功能分支
git checkout -b feature/problem-management

# 2. 开发功能
# ... 编码 ...

# 3. 提交代码
git add .
git commit -m "feat: 添加题目管理功能"

# 4. 推送分支
git push origin feature/problem-management

# 5. 创建Pull Request
# 在GitHub/GitLab上创建PR

# 6. 代码审查
# 团队成员进行代码审查

# 7. 合并代码
git checkout main
git merge feature/problem-management
git push origin main
```

### 发版流程

```bash
# 1. 创建发版分支
git checkout -b release/v1.1.0

# 2. 更新版本号
# 修改pom.xml中的version

# 3. 构建测试
mvn clean package

# 4. 创建标签
git tag -a v1.1.0 -m "Release version 1.1.0"

# 5. 推送标签
git push origin v1.1.0
```
