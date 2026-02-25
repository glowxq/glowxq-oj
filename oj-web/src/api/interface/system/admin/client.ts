import type { IPageQuery } from '@/api/interface';

export namespace ISysClient {
  // 查询条件
  export interface Query extends IPageQuery {
    clientKey?: string;
    clientSecret?: string;
    grantTypeCd?: string;
    deviceTypeCd?: string;
    activeTimeout?: number;
    version?: number;
  }

  // 编辑form表单
  export interface Form {
    clientKey?: string;
    clientSecret?: string;
    grantTypeCdList?: string[];
    grantTypeCd?: string;
    deviceTypeCd?: string;
    activeTimeout?: number;
    timeout?: number;
    clientStatusCd?: string;
    version?: number;
    remark?: string;
  }

  // list或detail返回结构
  export interface Row {
    clientId?: string;
    clientKey?: string;
    clientSecret?: string;
    grantTypeCd?: string;
    grantTypeCdList?: string[];
    deviceTypeCd?: string;
    activeTimeout?: number;
    timeout?: number;
    clientStatusCd?: string;
    version?: number;
    remark?: string;
    isLock?: string;
  }
}
