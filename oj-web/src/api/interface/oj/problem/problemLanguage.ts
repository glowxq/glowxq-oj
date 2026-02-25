import type { IPageQuery } from '@/api/interface'

export namespace IProblemLanguage {

  // 查询条件
  export interface Query extends IPageQuery {
    problemId?: string
    problemKey?: string
    problemLanguage?: string
    languageId?: string
  }

  // 编辑form表单
  export interface Form {
    id?: string
    problemId?: string
    problemKey?: string
    problemLanguage?: string
    languageId?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: string
    problemId?: string
    problemKey?: string
    problemLanguage?: string
    languageId?: string
  }

}