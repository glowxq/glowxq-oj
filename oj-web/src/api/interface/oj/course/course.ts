import type { IPageQuery } from '@/api/interface'
import type { IGroup } from '@/api/interface/oj/group/group'

export namespace ICourse {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    content?: string
    url?: string
    teacherUserId?: number
    teacherName?: string
    teacherPhone?: string
    status?: string
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
    content?: string
    url?: string
    teacherUserId?: number
    teacherName?: string
    teacherPhone?: string
    status?: string
    enable?: string
    startTime?: string
    endTime?: string
    groupIds?: number[]
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    content?: string
    url?: string
    teacherUserId?: number
    teacherName?: string
    teacherPhone?: string
    status?: string
    enable?: string
    startTime?: string
    endTime?: string
    groupIds?: number[]
    groups?: IGroup.Row[]
  }

}
