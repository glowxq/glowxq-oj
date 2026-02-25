import type { IPageQuery } from '@/api/interface'

export namespace IMetaImage {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    imageKey?: string
    businessType?: string
    url?: string
    content?: string
    skipUrl?: string
    sort?: number
    enable?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    name?: string
    imageKey?: string
    businessType?: string
    url?: string
    content?: string
    skipUrl?: string
    sort?: number
    enable?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    imageKey?: string
    businessType?: string
    url?: string
    content?: string
    skipUrl?: string
    sort?: number
    enable?: string
  }

}