import type { IPageQuery } from '@/api/interface'

export namespace IMetaTagBind {

  // 查询条件
  export interface Query extends IPageQuery {
    tagId?: number
    businessId?: number
    type?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    tagId?: number
    businessId?: number
    type?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    tagId?: number
    businessId?: number
    type?: string
  }

}