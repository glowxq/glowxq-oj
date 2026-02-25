import type { IPageQuery } from '@/api/interface';

export namespace ISysFile {
  // 查询条件
  export interface Query extends IPageQuery {
    filename?: string;
    dirTag?: string;
    objectName?: string;
  }

  // 编辑form表单
  export interface Form {}

  // list或detail返回结构
  export interface Row {
    id?: number;
    filename?: string;
    dirTag?: string;
    size?: number;
    url?: string;
    objectName?: string;
    contextType?: string;
    eTag?: string;
    fileId?: number;
  }
}
