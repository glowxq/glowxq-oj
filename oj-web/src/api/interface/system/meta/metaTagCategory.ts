import type { IPageQuery } from '@/api/interface'

export namespace IMetaTagCategory {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    enable?: string
    tenantId?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    name?: string
    enable?: string
    remark?: string
    tenantId?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    enable?: string
    remark?: string
    tenantId?: string
  }

}