import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/interface';
import type { ISysFile } from '@/api/interface/system/admin/file';

/**
 * 查询列表
 * @param params
 * @returns {*}
 */
export const getSysFileListApi = (params: ISysFile.Query) => {
  return http.get<IPage<ISysFile.Row>>(ADMIN_MODULE + `/sys-file`, params);
};
