import type { IPageQuery } from '@/api/interface'

export namespace ITopicProblem {

  // 查询条件
  export interface Query extends IPageQuery {
    topicId?: number
    problemId?: number
    required?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    topicId?: number
    problemId?: number
    required?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    topicId?: number
    problemId?: number
    required?: string
  }

}