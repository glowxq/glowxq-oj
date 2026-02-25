import type { IPageQuery } from '@/api/interface'

export namespace IMetaMenu {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    activeIcon?: string
    inactiveIcon?: string
    iconType?: string
    type?: string
    path?: string
    enable?: string
    sort?: number
    hasChildren?: string
    lock?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    name?: string
    activeIcon?: string
    inactiveIcon?: string
    iconType?: string
    type?: string
    path?: string
    enable?: string
    sort?: number
    hasChildren?: string
    lock?: string
    remark?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    activeIcon?: string
    inactiveIcon?: string
    iconType?: string
    type?: string
    path?: string
    enable?: string
    sort?: number
    hasChildren?: string
    lock?: string
    remark?: string
  }

}
