import type { IPageQuery } from '@/api/interface'

export namespace ITopicSubmit {

  // 分页返回结构
  export interface Page {
    list: Row[]
    total: number
  }

  // 查询条件
  export interface Query extends IPageQuery {
    topicId?: number
    problemId?: number
    problemKey?: string
    problemTitle?: string
    problemType?: string
    topicJudgeType?: number
    userId?: number
    name?: string
    nickName?: string
    judgeStatus?: number
    score?: number
    useTime?: number
    punishmentTime?: number
  }

  // 编辑form表单
  export interface Form {
    id?: number
    topicId?: number
    problemId?: number
    problemKey?: string
    problemTitle?: string
    problemType?: string
    topicJudgeType?: number
    userId?: number
    name?: string
    nickName?: string
    judgeStatus?: number
    score?: number
    useTime?: number
    punishmentTime?: number
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    topicId?: number
    problemId?: number
    problemKey?: string
    problemTitle?: string
    problemType?: string
    topicJudgeType?: number
    userId?: number
    name?: string
    nickName?: string
    judgeStatus?: number
    score?: number
    useTime?: number
    punishmentTime?: number
  }
  
  // 主题做题情况接口
  export interface SubmitInfo {
    // 用户信息
    users: {
      userId: number
      name: string
      nickName?: string
      avatar?: string
      totalScore?: number
      totalAcNum?: number
      totalSubmitNum?: number
    }[]
    // 题目信息
    problems: {
      problemId: number
      problemKey: string
      problemTitle: string
      problemType: string
      maxScore: number
    }[]
    // 提交记录信息（以用户ID和题目ID为键）
    submissions: {
      [userId: number]: {
        [problemId: number]: {
          judgeStatus: number
          score: number
          submitNum: number
          useTime?: number
          lastSubmitTime?: string
        }
      }
    }
  }
}