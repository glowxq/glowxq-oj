import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { ILogin } from '@/api/interface/system/admin/login';

// 用户登录
export const loginApi = (params: ILogin.LoginParams) => {
  params.grantType = 'password';
  params.clientId = import.meta.env.VITE_APP_CLIENT_ID;
  
  // 如果有租户信息，则添加到请求参数中
  const tenantId = localStorage.getItem('tenantId');
  if (tenantId) {
    params.tenantId = tenantId;
  }
  
  return http.post<ILogin.LoginInfo>(ADMIN_MODULE + `/auth/login`, params, { loading: false }); // 正常 post json 请求  ==>  application/json
  // return http.post(`/login`, params, { loading: false }); // 控制当前请求不显示 loading
  // return http.post(`/login`, {}, { params }); // post 请求携带 query 参数  ==>  ?username=admin&password=123456
  // return http.post(`/login`, qs.stringify(params)); // post 请求携带表单参数  ==>  application/x-www-form-urlencoded
  // return http.get(`/login?${qs.stringify(params, { arrayFormat: "repeat" })}`); // get 请求可以携带数组等复杂参数
};

// 用户注册
export const registerApi = (params: ILogin.RegisterParams) => {
  // 如果有租户信息，确保添加到请求参数中
  if (!params.tenantKey) {
    const tenantId = localStorage.getItem('tenantId');
    const tenantCode = localStorage.getItem('tenantCode');
    if (tenantId) {
      params.tenantKey = tenantId;
    } else if (tenantCode) {
      params.tenantKey = tenantCode;
    }
  }
  
  return http.post<void>(ADMIN_MODULE + `/client/sys-user/auth/register`, params);
};

// 获取菜单列表
export const getAuthMenuListApi = () => {
  return http.get<Menu.MenuOptions[]>(ADMIN_MODULE + `/sys-menu/menu`, {}, { loading: false });
};

// 获取按钮权限
export const getAuthButtonListApi = () => {
  return http.get<string[]>(ADMIN_MODULE + `/sys-menu/btn/permissions`, {}, { loading: false });
};

// 用户退出登录
export const logoutApi = () => {
  return http.post(ADMIN_MODULE + `/auth/logout`);
};

// 获取用户角色
export const getAuthRoleListApi = () => {
  return http.get<string[]>(ADMIN_MODULE + `/sys-menu/user/roles`, {}, { loading: false });
};
