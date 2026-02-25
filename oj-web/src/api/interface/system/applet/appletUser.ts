import type { IPageQuery } from '@/api/interface'

export namespace IAppletUser {

  // 查询条件
  export interface Query extends IPageQuery {
    sysUserId?: number
    openid?: string
    unionid?: string
    nickname?: string
    name?: string
    phone?: string
    address?: string
    avatar?: string
    subscribe?: string
    enable?: string
    sex?: string
    code?: string
    bindCode?: string
    url?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    sysUserId?: number
    openid?: string
    unionid?: string
    nickname?: string
    name?: string
    phone?: string
    address?: string
    avatar?: string
    subscribe?: string
    enable?: string
    sex?: string
    code?: string
    bindCode?: string
    url?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    sysUserId?: number
    openid?: string
    unionid?: string
    nickname?: string
    name?: string
    phone?: string
    address?: string
    avatar?: string
    subscribe?: string
    enable?: string
    sex?: string
    code?: string
    bindCode?: string
    url?: string
  }

}