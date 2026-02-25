import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IUploadResult } from '@/api/interface/system/admin/upload';
import type { AxiosRequestConfig } from 'axios';
import type { UploadRawFile } from 'element-plus/es/components/upload/src/upload';

/**
 * 自定义上传错误接口
 */
export interface UploadError {
  status: number;
  method: string;
  url: string;
  message: string;
}

// 定义IUpload命名空间，提供给其他地方使用
export namespace IUpload {
  export interface File {
    file: File | UploadRawFile;
    dirTag?: string;
  }
}

/**
 * 上传文件
 * @param params
 * @returns {*}
 */
export const uploadFile = (params: IUpload.File, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload<IUploadResult>(ADMIN_MODULE + `/sys-file/upload`, params, config);
};

/**
 * 上传模板文件
 * @param params
 * @param config
 * @returns {*}
 */
export const uploadTmpFile = (params: IUpload.File, config?: AxiosRequestConfig<{}> | undefined) => {
  return http.upload<IUploadResult>(ADMIN_MODULE + `/sys-temp-file/upload`, params, config);
};

/**
 * 获取OSS预签名上传URL
 * @param fileName 文件名
 * @returns 预签名URL
 */
export const generatePreSignedUrl = (fileName: string) => {
  return http.post<string>(ADMIN_MODULE + `/sys-file/generatePreUrl`, { name: fileName });
};

/**
 * 使用OSS预签名URL直接上传文件
 * @param file 要上传的文件
 * @param fileName 文件名
 * @param dir 目录标签
 * @param onProgress 进度回调
 * @param onSuccess 成功回调
 * @param onError 错误回调
 */
export const uploadToOssWithPreSignedUrl = async (
  file: File,
  fileName: string = file.name,
  dir: string = 'default',
  onProgress?: (percent: number) => void,
  onSuccess?: (result: IUploadResult) => void,
  onError?: (error: UploadError) => void
) => {
  try {
    // 1. 获取预签名URL
    const { data: preSignedUrl } = await generatePreSignedUrl(fileName);

    if (!preSignedUrl) {
      throw new Error('获取预签名URL失败');
    }

    // 2. 使用预签名URL上传文件
    const xhr = new XMLHttpRequest();
    xhr.open('PUT', preSignedUrl, true);

    // 3. 设置上传进度回调
    if (onProgress) {
      xhr.upload.addEventListener('progress', (event) => {
        if (event.lengthComputable) {
          const percent = Math.round((event.loaded / event.total) * 100);
          onProgress(percent);
        }
      });
    }

    // 4. 设置上传完成回调
    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        // 构造上传成功的响应数据，从URL中解析文件信息
        const baseUrl = preSignedUrl.split('?')[0];
        const path = new URL(baseUrl).pathname;

        // 构造成功响应
        const response: IUploadResult = {
          url: baseUrl,
          filename: fileName,
          fileName: fileName,
          eTag: xhr.getResponseHeader('ETag') || '',
          objectName: path,
          dirTag: dir,
          contextType: file.type,
          size: file.size,
          fileId: Date.now()
        };

        if (onSuccess) {
          onSuccess(response);
        }
      } else {
        const error: UploadError = {
          status: xhr.status,
          method: 'PUT',
          url: preSignedUrl,
          message: `上传失败: ${xhr.status}`
        };

        if (onError) {
          onError(error);
        }
      }
    };

    // 5. 设置上传错误回调
    xhr.onerror = () => {
      const error: UploadError = {
        status: 0,
        method: 'PUT',
        url: preSignedUrl,
        message: '网络错误，上传失败'
      };

      if (onError) {
        onError(error);
      }
    };

    // 6. 开始上传
    xhr.send(file);

    // 返回请求对象，可用于中止上传
    return xhr;

  } catch (error: any) {
    const ajaxError: UploadError = {
      status: 0,
      method: 'PUT',
      url: '',
      message: error.message || '上传失败'
    };

    if (onError) {
      onError(ajaxError);
    }

    throw ajaxError;
  }
};
