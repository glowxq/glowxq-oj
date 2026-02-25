import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios';

import router from '@/router';
import { LOGIN_URL } from '@/config';
import { checkStatus, CODE_SUCCESS, CODE_TOKEN_FAIL } from '@/api/helper';
import type { IResultData } from '@/api/interface';
import { useUserStore } from '@/stores/modules/user';
import { useAuthStore } from '@/stores/modules/auth';
import { useSocketStore } from '@/stores/modules/socket';
import { useAppStore } from '@/stores/modules/app';
import { useTenantStore } from '@/stores/modules/tenant';
import { ElMessage } from 'element-plus';

export interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  loading?: boolean;
  cancel?: boolean;
}

const config = {
  // 默认地址请求地址，可在 .env.** 文件中修改
  baseURL: import.meta.env.VITE_API_URL as string,
  // 设置超时时间，默认超时时间60s
  timeout: import.meta.env.VITE_APP_HTTP_TIMEOUT || 60000
  // 跨域时候允许携带凭证
  // withCredentials: true
};

class RequestHttp {
  service: AxiosInstance;

  constructor(config: AxiosRequestConfig) {
    // instantiation
    this.service = axios.create(config);

    /**
     * @description 请求拦截器
     * 客户端发送请求 -> [请求拦截器] -> 服务器
     * token校验(JWT) : 接受服务器返回的 token,存储到 vuex/pinia/本地储存当中
     */
    this.service.interceptors.request.use(
      (config: CustomAxiosRequestConfig) => {
        const userStore = useUserStore();
        const appStore = useAppStore();
        const tenantStore = useTenantStore();
        // 当前请求不需要显示 loading，在 api 服务中通过指定的第三个参数: { loading: false } 来控制
        // config.loading !== false && showFullScreenLoading()
        if (config.headers && typeof config.headers.set === 'function') {
          config.headers.set('Authorization', 'Bearer ' + userStore.token);
          // 添加MenuType请求头
          config.headers.set('MenuType', appStore.menuTypeCd);
          // 添加租户ID请求头
          if (tenantStore.tenantId) {
            config.headers.set('Tenant-Id', tenantStore.tenantId);
          }
        }
        return config;
      },
      error => {
        return Promise.reject(error);
      }
    );

    /**
     * @description 响应拦截器
     *  服务器换返回信息 -> [拦截统一处理] -> 客户端JS获取到信息
     */
    this.service.interceptors.response.use(
      response => {
        const { data } = response;
        const userStore = useUserStore();
        const socketStore = useSocketStore();
        const authStore = useAuthStore();
        // 如果是文件流，直接返回整个响应对象
        if (response.config.responseType === 'blob') {
          return response;
        }

        //tryHideFullScreenLoading()
        // 登陆失效
        if (data.code === CODE_TOKEN_FAIL) {
          userStore.clear();
          authStore.clear();
          // 关闭socket
          socketStore.close();

          router.replace(LOGIN_URL);
          ElMessage.error(data.message);
          return Promise.reject(data);
        }
        // 全局错误信息拦截（防止下载文件的时候返回数据流，没有 code 直接报错）
        if (data.code && data.code !== CODE_SUCCESS) {
          ElMessage.error({ message: data.message, dangerouslyUseHTMLString: true });
          return Promise.reject(data);
        }
        // 成功请求（在页面上除非特殊情况，否则不用处理失败逻辑）
        return data;
      },
      async error => {
        const { response } = error;
        // tryHideFullScreenLoading()
        // 请求超时 && 网络错误单独判断，没有 response
        if (error.message.indexOf('timeout') !== -1) {
          ElMessage.error('请求超时！请您稍后重试');
        }
        if (error.message.indexOf('Network Error') !== -1) {
          ElMessage.error('网络错误！请您稍后重试');
        }
        // 根据服务器响应的错误状态码，做不同的处理
        if (response) {
          checkStatus(response?.status, response?.data?.message);
        }
        // 服务器结果都没有返回(可能服务器错误可能客户端断网)，断网处理:可以跳转到断网页面
        if (!window.navigator.onLine) {
          router.replace('/500');
        }
        return Promise.reject(error);
      }
    );
  }

  /**
   * @description 常用请求方法封装
   */
  get<T>(url: string, params: object = {}, _object = {}): Promise<IResultData<T>> {
    return this.service.get(url, { params, ..._object });
  }

  post<T>(url: string, params: object = {}, _object = {}): Promise<IResultData<T>> {
    return this.service.post(url, params, _object);
  }

  put<T>(url: string, params: object = {}, _object = {}): Promise<IResultData<T>> {
    return this.service.put(url, params, _object);
  }

  delete<T>(url: string, data: object = {}, _object = {}): Promise<IResultData<T>> {
    return this.service.delete(url, { data, ..._object });
  }

  download(url: string, params = {}, _object = {}): Promise<BlobPart> {
    return this.service.post(url, params, { ..._object, responseType: 'blob' });
  }

  template(url: string, params = {}, _object = {}): Promise<BlobPart> {
    return this.service.get(url, { params, ..._object, responseType: 'blob' });
  }

  upload<T>(url: string, params: any = {}, _object: AxiosRequestConfig<{}> | undefined): Promise<IResultData<T>> {
    return this.service.post(url, params, {
      ..._object,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
}

export default new RequestHttp(config);
