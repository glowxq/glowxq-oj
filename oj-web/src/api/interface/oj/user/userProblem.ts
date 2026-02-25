import type { IPageQuery } from '@/api/interface'

export namespace IUserProblem {

  // 查询条件
  export interface Query extends IPageQuery {
    userId?: number
    problemId?: number
    problemKey?: string
    problemTitle?: string
    judgeId?: number
    judgeStatus?: number
    score?: number
    code?: string
    options?: string
    flowImage?: string
    problemType?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    userId?: number
    problemId?: number
    problemKey?: string
    problemTitle?: string
    judgeId?: number
    judgeStatus?: number
    score?: number
    code?: string
    options?: string
    flowImage?: string
    problemType?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    userId?: number
    problemId?: number
    problemKey?: string
    problemTitle?: string
    judgeId?: number
    judgeStatus?: number
    score?: number
    code?: string
    options?: string
    flowImage?: string
    problemType?: string
  }

}