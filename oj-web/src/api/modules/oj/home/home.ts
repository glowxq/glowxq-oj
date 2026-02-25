import http from '@/api'
import { OJ_MODULE } from '@/api/helper/prefix'
import type { IHome } from '@/api/interface/oj/home/home'
import type { IUserInfo } from '@/api/interface/oj/user/userInfo'

/**
 * 获取用户详情
 * @returns {*}
 */
export const getUserDetailApi = () => {
  return http.get<IHome.UserDetail>(OJ_MODULE + `/home/getUserDetail`)
}

/**
 * 全局排名
 * @returns {*}
 */
export const rankGlobalApi = () => {
  return http.get<IUserInfo.Row[]>(OJ_MODULE + `/home/rankGlobal`)
}

/**
 * 用户签到
 * @returns {*}
 */
export const signApi = () => {
  return http.post<IHome.CheckinResult>(OJ_MODULE + `/home/sign`)
}

/**
 * 获取全局统计数据
 * @returns {*}
 */
export const getGlobalDataCountApi = () => {
  return http.get<IHome.GlobalDataCount>(OJ_MODULE + `/home/data/getGlobalDataCount`)
}

/**
 * 获取题型统计数据
 * @returns {*}
 */
export const countProblemTypeDataApi = () => {
  return http.get<IHome.ProblemTypeCount[]>(OJ_MODULE + `/home/data/countProblemTypeData`)
}

/**
 * 获取首页展示的比赛/主题
 * @param num 数量
 * @returns {*}
 */
export const getHomeTopicApi = (num: number) => {
  return http.get<IHome.HomeTopic[]>(OJ_MODULE + `/home/homeTopic`, { num })
}
