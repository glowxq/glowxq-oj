import http from '@/api';
import type {GlobalNormalSubmitDTO, Judge} from '@/api/interface/oj/problem/ptoblemSubmit';
import {OJ_MODULE} from '@/api/helper/prefix';

/**
 * 普通测评提交接口
 * @param params
 * @returns {*}
 */
export const submitNormalApi = (params: GlobalNormalSubmitDTO) => {
  return http.post<Judge>(OJ_MODULE + `/judge/submit/submitNormal`, params);
};
