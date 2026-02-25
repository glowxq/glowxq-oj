import type { IPageQuery } from '@/api/interface'

export namespace IMetaCategory {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    pid?: number
    deep?: number
    sort?: number
    enable?: string
    hasChildren?: string
    lock?: boolean
  }

  // 编辑form表单
  export interface Form {
    id?: number
    name?: string
    pid?: number
    deep?: number
    sort?: number
    enable?: string
    hasChildren?: string
    lock?: boolean
    remark?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    pid?: number
    deep?: number
    sort?: number
    enable?: string
    hasChildren?: string
    lock?: boolean
    remark?: string
  }

  export interface Tree {
    id: number;
    pid: number;
    name: string;
    deep?: number;
    sort?: number;
    children: Tree[];
    enable?: string;
    lock?: boolean;
    remark?: string;
    hasChildren?: boolean;
  }
}
