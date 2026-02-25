// 全局默认配置项

// const homeUrl = import.meta.env.HOME_URL;

// 首页地址（默认）
export const HOME_URL: string = import.meta.env.VITE_HOME_URL;

// 登录页地址（默认）
export const LOGIN_URL: string = '/login';

// 注册页地址（默认）
export const REGISTER_URL: string = '/register';

// 默认主题颜色
export const DEFAULT_PRIMARY: string = '#009688';

// 路由白名单地址（必须是本地存在的路由 staticRouter.ts 中）
export const ROUTER_WHITE_LIST: string[] = ['/500', '/tenant', '/register'];

// 是否是预览环境
export const IS_PREVIEW: boolean = import.meta.env.VITE_PREVIEW === 'true';
