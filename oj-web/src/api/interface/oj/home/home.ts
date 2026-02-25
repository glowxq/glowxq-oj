import type { IUserInfo } from '../user/userInfo'

export namespace IHome {
  // 用户签到结果
  export interface CheckinResult {
    // 本次获得积分
    signDayIntegral: number
    // 总积分
    totalIntegral: number
    // 连续签到天数
    continuousSignDay: number
    // 今天是否已经签到
    todayChecked: boolean
  }

  // 用户详情信息
  export interface UserDetail {
    id?: number
    username?: string
    phone?: string
    nickname?: string
    name?: string
    sex?: number
    birthday?: string
    logo?: string
    age?: number
    idCard?: string
    email?: string
    accountStatusCd?: string
    userTagCd?: string
    lastLoginTime?: string
    acNum?: number
    integral?: number
    continuousSignDay?: number
    submitNum?: number
    title?: string
    color?: string
    createTime?: string
    updateTime?: string
  }

  // 全局数据统计
  export interface GlobalDataCount {
    // 用户数量
    userCount: number
    // 作业比赛题集数量
    topicCount: number
    // 题目数量
    problemCount: number
    // AC数量
    acCount: number
    // 测评数量
    judgeCount: number
    // 班级数量
    groupCount: number
    // 课程数量
    courseCount: number
  }

  // 题型统计数据
  export interface ProblemTypeCount {
    // 题型
    problemType: string
    // 数量
    number: number
  }

  // 首页比赛/主题数据
  export interface HomeTopic {
    id: number
    name: string
    code: string
    description?: string
    topicType?: string
    startTime?: string
    endTime?: string
    color?: string
    textColor?: string
    principalName?: string
    homeShow?: boolean
    enable?: boolean
    common?: boolean
  }
}
