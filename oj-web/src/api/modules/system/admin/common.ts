import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { ICommon } from '@/api/interface/system/admin/common';

/**
 * 模版下载
 */
export const downloadTemplate = (params: ICommon.TemplateDown) => {
  return http.template(ADMIN_MODULE + `/common/download/templates`, params);
};
