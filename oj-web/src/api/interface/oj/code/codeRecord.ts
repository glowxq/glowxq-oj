import type { IPageQuery } from '@/api/interface'

export namespace ICodeRecord {

  // 查询条件
  export interface Query extends IPageQuery {
    userId?: number
    name?: string
    username?: string
    code?: string
    codeMode?: string
  }

  // 编辑form表单
  export interface Form {

    code?: string
    codeMode?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    userId?: number
    name?: string
    username?: string
    code?: string
    codeMode?: string
    createTime?: string
    updateTime?: string
  }

}
