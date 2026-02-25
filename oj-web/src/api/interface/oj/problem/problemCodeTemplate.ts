import type { IPageQuery } from '@/api/interface'

export namespace IProblemCodeTemplate {

  // 查询条件
  export interface Query extends IPageQuery {
    problemId?: number
    languageId?: number
    name?: string
    code?: string
    enable?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    problemId?: number
    languageId?: number
    name?: string
    code?: string
    enable?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    problemId?: number
    languageId?: number
    name?: string
    code?: string
    enable?: string
  }

}