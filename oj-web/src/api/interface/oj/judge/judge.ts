import type { IPageQuery } from '@/api/interface'

export namespace IJudge {

  // 查询条件
  export interface Query extends IPageQuery {
    judgeKey?: string
    problemId?: number
    problemKey?: string
    errorMessage?: string
    userId?: number
    businessId?: number
    name?: string
    sceneType?: string
    problemType?: string
    submitTimeStart?: string
    submitTimeEnd?: string
    startTimeStart?: string
    startTimeEnd?: string
    endTimeStart?: string
    endTimeEnd?: string
    status?: number
    time?: number
    memory?: number
    score?: number
    length?: number
    flowImage?: string
    code?: string
    replyOptions?: string
    language?: string
    manualEvaluation?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    judgeKey?: string
    problemId?: number
    problemKey?: string
    errorMessage?: string
    userId?: number
    businessId?: number
    name?: string
    sceneType?: string
    problemType?: string
    submitTime?: string
    startTime?: string
    endTime?: string
    status?: number
    time?: number
    memory?: number
    score?: number
    length?: number
    flowImage?: string
    code?: string
    replyOptions?: string
    language?: string
    manualEvaluation?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    judgeKey?: string
    problemId?: number
    problemKey?: string
    problemTitle?: string
    errorMessage?: string
    userId?: number
    businessId?: number
    name?: string
    sceneType?: string
    problemType?: string
    programType?: string | number
    submitTime?: string
    startTime?: string
    endTime?: string
    status?: number
    time?: number
    memory?: number
    score?: number
    length?: number
    flowImage?: string
    code?: string
    replyOptions?: string
    language?: string
    manualEvaluation?: string
  }

}
