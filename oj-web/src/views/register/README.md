# 注册组件重构说明

## 概述

注册页面已经重构为可配置的组件系统，支持根据不同的app模式动态加载不同的注册组件。

## 文件结构

```
src/views/register/
├── index.vue                    # 主注册页面
├── config/
│   └── registerConfig.ts       # 注册组件配置文件
├── components/
│   ├── DefaultRegister.vue     # 默认系统注册组件
│   ├── OjRegister.vue         # OJ注册组件
│   └── ...                    # 其他注册组件（预留）
└── README.md                   # 本说明文档
```

## 配置说明

### 1. 组件配置接口

```typescript
interface RegisterComponentConfig {
  name: string;                    // 组件名称
  component: () => Promise<any>;   // 组件路径
  showTenantControl: boolean;      // 是否显示租户控制区域
  description: string;             // 描述
}
```

### 2. 当前可用配置

| 配置键 | 组件名称 | 租户控制 | 说明 |
|--------|----------|----------|------|
| `default` | DefaultRegister | ✅ | 默认系统注册，包含用户名密码和租户选择 |
| `oj` | OjRegister | ❌ | OJ注册，简化表单，显示当前租户信息 |
| `rescue` | RescueRegister | ✅ | 救援系统注册（预留，当前使用默认组件） |
| `wingman` | WingmanRegister | ✅ | 翼人系统注册（预留，当前使用默认组件） |

### 3. 如何添加新的注册组件

1. **创建新组件**
   ```bash
   # 在 components 目录下创建新组件
   src/views/register/components/CustomRegister.vue
   ```

2. **修改配置文件**
   ```typescript
   // src/views/register/config/registerConfig.ts
   export const REGISTER_COMPONENT_CONFIG: Record<string, RegisterComponentConfig> = {
     // ... 现有配置
     
     // 添加新配置
     custom: {
       name: 'CustomRegister',
       component: () => import('../components/CustomRegister.vue'),
       showTenantControl: true, // 根据需要设置
       description: '自定义注册组件'
     }
   };
   ```

3. **更新配置逻辑**
   ```typescript
   // 在 getCurrentRegisterConfig 函数中添加判断逻辑
   export function getCurrentRegisterConfig(): RegisterComponentConfig {
     const mode = import.meta.env.MODE;
     const appTitle = import.meta.env.VITE_APP_TITLE;
     
     if (mode === 'custom' || appTitle === 'Custom-App') {
       return REGISTER_COMPONENT_CONFIG.custom;
     }
     
     // ... 其他逻辑
   }
   ```

## 组件特性

### DefaultRegister.vue
- **完整系统注册功能**
- 用户名、密码、确认密码（必填）
- 租户选择功能（支持搜索和密码验证）
- 个人信息（手机、邮箱、姓名、昵称、性别、生日、地址、头像）
- 使用折叠面板组织表单

### OjRegister.vue  
- **简化OJ注册功能**
- 真实姓名（必填）
- 基本信息（昵称、手机、邮箱、性别、生日、班级代码、头像）
- 显示当前租户信息（只读）
- 直接调用OJ用户注册API

## 使用方式

### 自动模式（推荐）
系统会根据环境变量自动选择对应的注册组件：

```bash
# OJ模式
npm run dev:oj        # 加载 OjRegister 组件

# 其他模式  
npm run dev           # 加载 DefaultRegister 组件
npm run dev:system    # 加载 DefaultRegister 组件
npm run dev:rescue    # 加载 DefaultRegister 组件（预留）
npm run dev:wingman   # 加载 DefaultRegister 组件（预留）
```

### 手动指定模式
```typescript
// 可以通过配置函数手动指定
import { getRegisterConfigByKey } from '@/views/register/config/registerConfig';

const config = getRegisterConfigByKey('oj'); // 强制使用OJ注册组件
```

## API映射

| 组件 | API接口 | 后端DTO |
|------|---------|---------|
| DefaultRegister | `registerApi` → `/client/sys-user/auth/register` | `RegisterParams` |
| OjRegister | `registerUserInfoApi` → `/user-info/register` | `UserInfoRegisterDTO` |

## 环境变量

注册组件选择依赖以下环境变量：

- `VITE_APP_TITLE`: 应用标题，用于判断特定app
- `MODE`: Vite运行模式，对应package.json中的scripts

## 注意事项

1. **租户信息展示**: 
   - DefaultRegister: 提供租户选择功能
   - OjRegister: 只显示当前租户信息，不提供选择

2. **表单验证**: 
   - 每个组件都有独立的验证规则
   - 根据后端API要求设置必填字段

3. **样式统一**: 
   - 所有组件使用统一的设计语言
   - 支持响应式布局和移动端适配

4. **扩展性**: 
   - 新增注册组件只需修改配置文件
   - 支持不同组件有不同的展示逻辑 