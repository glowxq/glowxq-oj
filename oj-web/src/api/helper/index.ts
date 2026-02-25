import { ElMessage } from 'element-plus';

/**
 * 成功状态
 * @type {string}
 */
export const CODE_SUCCESS: string = '0000';
/**
 * 无效Token
 * @type {string}
 */
export const CODE_TOKEN_FAIL: string = 'C105';

/**
 * @description: 校验网络请求状态码
 * @param {Number} status
 * @return void
 */
export const checkStatus = (status: number, message?: string) => {
  switch (status) {
    case 400:
      ElMessage.error(message || '请求失败！请您稍后重试');
      break;
    case 401:
      ElMessage.error(message || '登录失效！请您重新登录');
      break;
    case 403:
      ElMessage.error(message || '当前账号无权限访问！');
      break;
    case 404:
      ElMessage.error(message || '你所访问的资源不存在！');
      break;
    case 405:
      ElMessage.error(message || '请求方式错误！请您稍后重试');
      break;
    case 408:
      ElMessage.error(message || '请求超时！请您稍后重试');
      break;
    case 422:
      ElMessage.error(message || '请求参数异常！');
      break;
    case 500:
      ElMessage.error(message || '服务异常！');
      break;
    case 502:
      ElMessage.error(message || '网关错误！');
      break;
    case 503:
      ElMessage.error(message || '服务不可用！');
      break;
    case 504:
      ElMessage.error(message || '网关超时！');
      break;
    // 自定义响应码，处理 blob流异常时的返回信息
    case 1004:
      ElMessage.error(message || '模板文件不存在！');
      break;
    default:
      ElMessage.error(message || '请求失败！');
  }
};
