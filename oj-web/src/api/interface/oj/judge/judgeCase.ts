import type { IPageQuery } from '@/api/interface'

export namespace IJudgeCase {

  // 查询条件
  export interface Query extends IPageQuery {
    judgeId?: string
    judgeKey?: string
    userId?: string
    problemId?: string
    problemKey?: string
    caseId?: string
    status?: number
    time?: number
    memory?: number
    score?: number
    groupNum?: number
    seq?: number
    mode?: string
    inputData?: string
    outputData?: string
    userOutput?: string
  }

  // 编辑form表单
  export interface Form {
    id?: string
    judgeId?: string
    judgeKey?: string
    userId?: string
    problemId?: string
    problemKey?: string
    caseId?: string
    status?: number
    time?: number
    memory?: number
    score?: number
    groupNum?: number
    seq?: number
    mode?: string
    inputData?: string
    outputData?: string
    userOutput?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: string
    judgeId?: string
    judgeKey?: string
    userId?: string
    problemId?: string
    problemKey?: string
    caseId?: string
    status?: number
    time?: number
    memory?: number
    score?: number
    groupNum?: number
    seq?: number
    mode?: string
    inputData?: string
    outputData?: string
    userOutput?: string
  }

}