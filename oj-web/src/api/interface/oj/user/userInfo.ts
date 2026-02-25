import type { IPageQuery } from '@/api/interface'
import type { IRole } from '@/api/interface/system/admin/role'
import type { IMetaTag } from '@/api/interface/system/meta/metaTag'
import type { IGroup } from '@/api/interface/oj/group/group'

export namespace IUserInfo {

  // 查询条件
  export interface Query extends IPageQuery {
    userId?: number
    deptId?: number
    name?: string
    nickName?: string
    phone?: string
    email?: string
    avatar?: string
    sex?: string
    birthdayStart?: string
    birthdayEnd?: string
    image?: string
    acNum?: number
    integral?: number
    continuousSignDay?: number
    submitNum?: number
    title?: string
    color?: string
    expirationTimeStart?: string
    expirationTimeEnd?: string
    lastLoginTimeStart?: string
    lastLoginTimeEnd?: string
    lastLoginIp?: string
    tenantId?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    userId?: number
    deptId?: number
    name?: string
    nickName?: string
    phone?: string
    email?: string
    avatar?: string
    sex?: string
    birthday?: string
    image?: string
    acNum?: number
    integral?: number
    continuousSignDay?: number
    submitNum?: number
    title?: string
    color?: string
    remark?: string
    expirationTime?: string
    lastLoginTime?: string
    lastLoginIp?: string
    tenantId?: string
    groupIds?: number[]
    roleIds?: number[]
  }

  // list或detail返回结构
  export interface Row {
    id?: number
    userId?: number
    deptId?: number
    name?: string
    nickName?: string
    phone?: string
    email?: string
    avatar?: string
    sex?: string
    birthday?: string
    image?: string
    acNum?: number
    integral?: number
    continuousSignDay?: number
    submitNum?: number
    title?: string
    color?: string
    remark?: string
    expirationTime?: string
    lastLoginTime?: string
    lastLoginIp?: string
    tenantId?: string
    groupIds?: number[]
    roleIds?: number[]
    createTime?: string
    updateTime?: string
    // 新增字段
    roles?: IRole.Info[]
    tags?: IMetaTag.Row[]
    groups?: IGroup.Row[]
  }

  // OJ用户注册接口
  export interface RegisterForm {
    name: string
    nickName?: string
    phone?: string
    email?: string
    avatar?: string
    sex?: string
    birthday?: string
    groupCode?: string
  }

} 