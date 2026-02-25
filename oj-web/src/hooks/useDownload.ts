import { ElNotification } from 'element-plus';

/**
 * @description 接收数据流生成 blob，创建链接，下载文件
 * @param {Function} api 导出表格的api方法 (必传)
 * @param {String} tempName 导出的文件名 (必传)
 * @param {Object} params 导出的参数 (默认{})
 * @param {Boolean} isNotify 是否有导出消息提示 (默认为 true)
 * @param {String} fileType 导出的文件格式 (默认为.xlsx)
 * */
export const useDownload = async (
  api: (param: any) => Promise<any>,
  tempName: string,
  params: any = {},
  isNotify: boolean = true
) => {
  if (isNotify) {
    ElNotification({
      title: '温馨提示',
      message: '如果数据庞大会导致下载缓慢哦，请您耐心等待！',
      type: 'info',
      duration: 3000
    });
  }
  try {
    // 如果params为空，构建请求参数
    if (!params) {
      params = {
        templateName: tempName
      };
    }
    const res = await api(params);

    // 提取文件名
    const contentDisposition = res.headers['content-disposition'];
    const filename = decodeURIComponent(
      contentDisposition?.match(/filename\*?=(?:UTF-8'')?([^;]+)?/)?.[1]?.replace(/['"]/g, '') || '下载文件'
    );

    // 下载文件
    const blob = new Blob([res.data], { type: res.headers['content-type'] });
    const blobUrl = window.URL.createObjectURL(blob);

    // 兼容 edge 不支持 createObjectURL 方法
    if ('msSaveOrOpenBlob' in navigator) return window.navigator.msSaveOrOpenBlob(blob, filename);
    const exportFile = document.createElement('a');
    exportFile.style.display = 'none';
    exportFile.download = filename;
    exportFile.href = blobUrl;
    document.body.appendChild(exportFile);
    exportFile.click();
    // 去除下载对 url 的影响
    document.body.removeChild(exportFile);
    window.URL.revokeObjectURL(blobUrl);
  } catch (error) {
    console.log(error);
  }
};
