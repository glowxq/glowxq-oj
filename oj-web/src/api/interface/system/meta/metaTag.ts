import type { IPageQuery } from '@/api/interface'

export namespace IMetaTag {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    backgroundColor?: string
    textColor?: string
    plain?: string
    enable?: string
    categoryId?: number
  }

  // 编辑form表单
  export interface Form {
    id?: number
    name?: string
    backgroundColor?: string
    textColor?: string
    plain?: string
    enable?: string
    categoryId?: number
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    backgroundColor?: string
    textColor?: string
    plain?: string
    enable?: string
    categoryId?: number
  }

  // 分类统计返回结构
  export interface CategoryCount {
    count: number
    categoryId: number
  }

}