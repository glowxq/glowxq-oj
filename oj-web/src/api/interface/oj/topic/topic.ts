import type { IPageQuery } from '@/api/interface'
import type { IGroup } from '@/api/interface/oj/group/group'
import type { IMetaTag } from '@/api/interface/system/meta/metaTag'
import type { IProblem } from '@/api/interface/oj/problem/problem'

export namespace ITopic {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    code?: string
    principalUserId?: number
    principalName?: string
    password?: string
    description?: string
    timeRangeType?: string
    topicType?: string
    topicJudgeType?: number
    sealedTime?: number
    timeLimit?: number
    punishmentTime?: number
    oiScoreType?: string
    color?: string
    common?: string
    enable?: string
    startTimeStart?: string
    startTimeEnd?: string
    endTimeStart?: string
    endTimeEnd?: string
    groupIds?: number[]
  }

  // 编辑form表单
  export interface Form {
    id?: number
    name?: string
    code?: string
    principalUserId?: number
    principalName?: string
    password?: string
    description?: string
    timeRangeType?: string
    topicType?: string
    sealedTime?: number
    timeLimit?: number
    punishmentTime?: number
    oiScoreType?: string
    color?: string
    common?: string
    enable?: string
    startTime?: string
    endTime?: string
    groupIds?: number[]
    tagIds?: number[]
    problemIds?: number[]
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    code?: string
    principalUserId?: number
    principalName?: string
    password?: string
    description?: string
    timeRangeType?: string
    topicType?: string
    topicJudgeType?: number
    sealedTime?: number
    timeLimit?: number
    punishmentTime?: number
    oiScoreType?: string
    color?: string
    common?: string
    enable?: string
    startTime?: string
    endTime?: string
    groupList?: IGroup.Row[]
    tagList?: IMetaTag.Row[]
    problemList?: IProblem.Row[]
  }

}
