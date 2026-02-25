// 注册组件配置
export interface RegisterComponentConfig {
  // 组件名称
  name: string;
  // 组件路径
  component: () => Promise<any>;
  // 是否显示租户控制区域
  showTenantControl: boolean;
  // 描述
  description: string;
}

// 注册组件配置映射
export const REGISTER_COMPONENT_CONFIG: Record<string, RegisterComponentConfig> = {
  // 默认系统注册组件
  default: {
    name: 'DefaultRegister',
    component: () => import('../components/DefaultRegister.vue'),
    showTenantControl: true,
    description: '默认系统注册组件，包含完整的用户注册和租户选择功能'
  },

  // OJ注册组件
  oj: {
    name: 'OjRegister',
    component: () => import('../components/OjRegister.vue'),
    showTenantControl: false, // OJ模式下不显示租户选择，但会显示当前租户信息
    description: 'OJ注册组件，适用于在线评测系统的用户注册'
  },
};

/**
 * 根据环境变量获取当前注册组件配置
 */
export function getCurrentRegisterConfig(): RegisterComponentConfig {
  // 根据环境变量判断当前模式
  const mode = import.meta.env.MODE;
  const appTitle = import.meta.env.VITE_APP_TITLE;
  
  // 优先根据 MODE 判断
  if (mode === 'oj' || appTitle === 'Glowxq-OJ') {
    return REGISTER_COMPONENT_CONFIG.oj;
  }

  // 默认使用系统注册组件
  return REGISTER_COMPONENT_CONFIG.default;
}

/**
 * 根据指定的配置key获取注册组件配置
 */
export function getRegisterConfigByKey(key: string): RegisterComponentConfig {
  return REGISTER_COMPONENT_CONFIG[key] || REGISTER_COMPONENT_CONFIG.default;
} 