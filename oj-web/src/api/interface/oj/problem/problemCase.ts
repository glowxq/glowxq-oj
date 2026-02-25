import type { IPageQuery } from '@/api/interface'

export namespace IProblemCase {

  // 查询条件
  export interface Query extends IPageQuery {
    problemId?: string
    problemKey?: string
    input?: string
    output?: string
    inputUrl?: string
    outputUrl?: string
    type?: string
    score?: number
    enable?: number
    groupNum?: number
  }

  // 编辑form表单
  export interface Form {
    id?: string
    problemId?: string
    problemKey?: string
    input?: string
    output?: string
    inputUrl?: string
    outputUrl?: string
    type?: string
    score?: number
    enable?: number
    groupNum?: number
 }

  // list或detail返回结构
  export interface Row {
    id?: string
    problemId?: string
    problemKey?: string
    input?: string
    output?: string
    inputUrl?: string
    outputUrl?: string
    type?: string
    score?: number
    enable?: number
    groupNum?: number
  }

}