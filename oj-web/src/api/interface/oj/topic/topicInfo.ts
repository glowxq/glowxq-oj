import type { IPageQuery } from '@/api/interface'

export namespace ITopicInfo {

  // 查询条件
  export interface Query extends IPageQuery {
    topicId?: number
    userId?: number
    avatar?: string
    name?: string
    nickName?: string
    score?: number
    acNum?: number
    submitNum?: number
    useTime?: number
    punishmentTime?: number
    status?: string
    startTimeStart?: string
    startTimeEnd?: string
    endTimeStart?: string
    endTimeEnd?: string
    autoSubmit?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    topicId?: number
    userId?: number
    avatar?: string
    name?: string
    nickName?: string
    score?: number
    acNum?: number
    submitNum?: number
    useTime?: number
    punishmentTime?: number
    status?: string
    startTime?: string
    endTime?: string
    autoSubmit?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    topicId?: number
    userId?: number
    avatar?: string
    name?: string
    nickName?: string
    score?: number
    acNum?: number
    submitNum?: number
    useTime?: number
    punishmentTime?: number
    status?: string
    startTime?: string
    endTime?: string
    autoSubmit?: string
  }

}