import type { IPageQuery } from '@/api/interface'

export namespace IMetaText {

  // 查询参数
  export interface Query extends IPageQuery {
    key?: string
    title?: string
    textType?: string
    description?: string
  }

  // 表单数据
  export interface Form {
    id?: number
    key: string
    title?: string
    content?: string
    textType?: string
    description?: string
  }

  // 行数据
  export interface Row {
    id: number
    key: string
    title?: string
    content?: string
    textType?: string
    description?: string
    enable?: boolean
    createTime?: string
    updateTime?: string
  }

}
