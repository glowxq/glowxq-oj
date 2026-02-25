import type { IPageQuery } from '@/api/interface'

export namespace IGroupBind {

  // 查询条件
  export interface Query extends IPageQuery {
    groupId?: number
    businessId?: number
    type?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    groupId?: number
    businessId?: number
    type?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    groupId?: number
    businessId?: number
    type?: string
  }

}