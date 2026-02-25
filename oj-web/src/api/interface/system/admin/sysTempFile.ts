import type { IPageQuery } from '@/api/interface';

export namespace ISysTempFile {
  // 查询条件
  export interface Query extends IPageQuery {
    tempName?: string;
  }

  // 历史记录查询条件
  export interface HistoryQuery extends IPageQuery {
    sysTempFileId: number;
  }

  // 编辑form表单
  export interface Form {
    id?: number;
    sysFileId?: number;
    tempName?: string;
    url?: string;
    remark?: string;
  }

  // list或detail返回结构
  export interface Row {
    id?: number;
    sysFileId?: number;
    tempName?: string;
    url?: string;
    remark?: string;
    delFlag?: string;
    createId?: number;
    createTime?: string;
    updateId?: number;
    updateTime?: string;
  }

  // 历史记录
  export interface History {
    id?: number;
    sysTempFileId?: number;
    sysFileId?: number;
    tempName?: string;
    url?: string;
    remark?: string;
    delFlag?: string;
    createId?: number;
    createTime?: string;
  }
}
