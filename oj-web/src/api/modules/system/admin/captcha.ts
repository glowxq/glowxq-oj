import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { ICaptcha } from '@/api/interface/system/admin/captcha';

// 获取验证码是否启用
export const getCaptchaStatus = () => {
  return http.get<boolean>(ADMIN_MODULE + `/captcha/status`, {}, { loading: false });
};
// 获取滑块拼图验证码
export const getImageCodeApi = () => {
  return http.post<ICaptcha.Info>(ADMIN_MODULE + `/captcha/get`, {}, { loading: false });
};
// 校验滑块拼图验证码
export const verifyImageCodeApi = (params: ICaptcha.VerifyImageParams) => {
  return http.post(ADMIN_MODULE + `/captcha/check`, params, { loading: false });
};
