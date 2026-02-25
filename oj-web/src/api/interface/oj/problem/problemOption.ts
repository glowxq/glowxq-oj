import type { IPageQuery } from '@/api/interface'

export namespace IProblemOption {

  // 查询条件
  export interface Query extends IPageQuery {
    problemId?: string
    problemKey?: string
    optionKey?: string
    problemType?: string
    score?: number
    optionContent?: string
    answer?: number
  }

  // 编辑form表单
  export interface Form {
    id?: string
    problemId?: string
    problemKey?: string
    optionKey?: string
    problemType?: string
    score?: number
    optionContent?: string
    answer?: number
 }

  // list或detail返回结构
  export interface Row {
    id?: string
    problemId?: string
    problemKey?: string
    optionKey?: string
    problemType?: string
    score?: number
    optionContent?: string
    answer?: number
  }

}