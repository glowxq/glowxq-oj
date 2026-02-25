import type { IPageQuery } from '@/api/interface'

export namespace IProblemBlack {

  // 查询条件
  export interface Query extends IPageQuery {
    problemId?: number
    businessId?: number
    businessName?: string
    type?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    problemId?: number
    businessId?: number
    businessName?: string
    type?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    problemId?: number
    businessId?: number
    businessName?: string
    type?: string
  }

}