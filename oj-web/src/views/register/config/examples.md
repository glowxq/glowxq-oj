# 注册组件配置示例

## 1. 快速切换组件示例

### 场景1：让所有模式都使用OJ注册组件

```typescript
// src/views/register/config/registerConfig.ts

export function getCurrentRegisterConfig(): RegisterComponentConfig {
  // 强制所有模式都使用OJ组件
  return REGISTER_COMPONENT_CONFIG.oj;
}
```

### 场景2：根据特定条件选择组件

```typescript
export function getCurrentRegisterConfig(): RegisterComponentConfig {
  const mode = import.meta.env.MODE;
  const appTitle = import.meta.env.VITE_APP_TITLE;
  
  // 如果是生产环境，强制使用默认组件
  if (import.meta.env.PROD) {
    return REGISTER_COMPONENT_CONFIG.default;
  }
  
  // 开发环境下根据模式选择
  if (mode === 'oj' || appTitle === 'Glowxq-OJ') {
    return REGISTER_COMPONENT_CONFIG.oj;
  }
  
  return REGISTER_COMPONENT_CONFIG.default;
}
```

### 场景3：A/B测试不同注册组件

```typescript
export function getCurrentRegisterConfig(): RegisterComponentConfig {
  // 随机选择组件（用于A/B测试）
  const shouldUseOj = Math.random() > 0.5;
  
  if (shouldUseOj) {
    return REGISTER_COMPONENT_CONFIG.oj;
  }
  
  return REGISTER_COMPONENT_CONFIG.default;
}
```

## 2. 添加新组件示例

### 添加简化版注册组件

1. **创建组件**：`src/views/register/components/SimpleRegister.vue`

```vue
<template>
  <div class="simple-register">
    <el-form ref="formRef" :model="form" :rules="rules">
      <el-form-item prop="name" label="姓名">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      
      <el-form-item prop="email" label="邮箱">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      
      <el-button type="primary" @click="handleSubmit">注册</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';

const form = reactive({ name: '', email: '' });
const rules = {
  name: [{ required: true, message: '请输入姓名' }],
  email: [{ required: true, type: 'email', message: '请输入正确的邮箱' }]
};

const handleSubmit = () => {
  console.log('简化注册:', form);
};
</script>
```

2. **修改配置**

```typescript
// src/views/register/config/registerConfig.ts

export const REGISTER_COMPONENT_CONFIG: Record<string, RegisterComponentConfig> = {
  // ... 现有配置

  // 新增简化组件
  simple: {
    name: 'SimpleRegister',
    component: () => import('../components/SimpleRegister.vue'),
    showTenantControl: false,
    description: '极简注册组件，只包含姓名和邮箱'
  }
};

export function getCurrentRegisterConfig(): RegisterComponentConfig {
  // 如果URL包含simple参数，使用简化组件
  if (window.location.search.includes('simple=true')) {
    return REGISTER_COMPONENT_CONFIG.simple;
  }
  
  // ... 其他逻辑
}
```

## 3. 组件功能切换示例

### 让OJ组件显示租户选择功能

```typescript
// 修改OJ配置，启用租户控制
export const REGISTER_COMPONENT_CONFIG: Record<string, RegisterComponentConfig> = {
  oj: {
    name: 'OjRegister',
    component: () => import('../components/OjRegister.vue'),
    showTenantControl: true, // 改为true
    description: 'OJ注册组件，包含租户选择功能'
  }
  // ...
};
```

### 让默认组件隐藏租户功能

```typescript
export const REGISTER_COMPONENT_CONFIG: Record<string, RegisterComponentConfig> = {
  default: {
    name: 'DefaultRegister',
    component: () => import('../components/DefaultRegister.vue'),
    showTenantControl: false, // 改为false，隐藏租户选择
    description: '默认注册组件，不包含租户功能'
  }
  // ...
};
```

## 4. 动态配置示例

### 根据用户权限选择组件

```typescript
export function getCurrentRegisterConfig(): RegisterComponentConfig {
  // 假设从localStorage获取用户权限
  const userRole = localStorage.getItem('userRole');
  
  if (userRole === 'admin') {
    // 管理员使用完整功能组件
    return REGISTER_COMPONENT_CONFIG.default;
  } else if (userRole === 'student') {
    // 学生使用OJ组件
    return REGISTER_COMPONENT_CONFIG.oj;
  }
  
  // 默认使用简化组件
  return REGISTER_COMPONENT_CONFIG.simple;
}
```

### 根据时间段选择组件

```typescript
export function getCurrentRegisterConfig(): RegisterComponentConfig {
  const hour = new Date().getHours();
  
  // 上班时间(9-17点)使用默认组件
  if (hour >= 9 && hour <= 17) {
    return REGISTER_COMPONENT_CONFIG.default;
  }
  
  // 其他时间使用简化组件
  return REGISTER_COMPONENT_CONFIG.simple;
}
```

## 5. 多租户配置示例

### 根据租户选择不同组件

```typescript
export function getCurrentRegisterConfig(): RegisterComponentConfig {
  const tenantId = localStorage.getItem('tenantId');
  
  // 教育机构租户使用OJ组件
  if (tenantId?.startsWith('edu_')) {
    return REGISTER_COMPONENT_CONFIG.oj;
  }
  
  // 企业租户使用默认组件
  if (tenantId?.startsWith('corp_')) {
    return REGISTER_COMPONENT_CONFIG.default;
  }
  
  // 其他情况使用简化组件
  return REGISTER_COMPONENT_CONFIG.simple;
}
```

## 6. 快速禁用/启用功能

### 临时禁用某个组件

```typescript
export const REGISTER_COMPONENT_CONFIG: Record<string, RegisterComponentConfig> = {
  oj: {
    name: 'OjRegister',
    // 临时禁用，回退到默认组件
    component: () => import('../components/DefaultRegister.vue'),
    showTenantControl: true,
    description: 'OJ注册组件（临时禁用，使用默认组件）'
  }
  // ...
};
```

### 功能开关配置

```typescript
// 添加功能开关配置
const FEATURE_FLAGS = {
  enableOjRegister: true,
  enableTenantControl: true,
  enableSimpleMode: false
};

export function getCurrentRegisterConfig(): RegisterComponentConfig {
  // 如果OJ注册被禁用，强制使用默认组件
  if (!FEATURE_FLAGS.enableOjRegister && import.meta.env.MODE === 'oj') {
    return REGISTER_COMPONENT_CONFIG.default;
  }
  
  // 如果启用简化模式
  if (FEATURE_FLAGS.enableSimpleMode) {
    return REGISTER_COMPONENT_CONFIG.simple;
  }
  
  // 正常逻辑
  const mode = import.meta.env.MODE;
  if (mode === 'oj') {
    return REGISTER_COMPONENT_CONFIG.oj;
  }
  
  return REGISTER_COMPONENT_CONFIG.default;
}
```

这样，你就可以通过简单的配置修改来实现：
- 快速切换注册组件
- 临时禁用某些功能
- 根据不同条件选择不同组件
- 添加新的注册组件

所有修改都只需要在配置文件中进行，无需修改主页面代码。 