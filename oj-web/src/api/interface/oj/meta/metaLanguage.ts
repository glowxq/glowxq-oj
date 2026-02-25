import type { IPageQuery } from '@/api/interface'

export namespace IMetaLanguage {

  // 查询条件
  export interface Query extends IPageQuery {
    contentType?: string
    description?: string
    name?: string
    compileCommand?: string
    template?: string
    codeTemplate?: string
    spjEnable?: number
    oj?: string
    seq?: number
  }

  // 编辑form表单
  export interface Form {
    id?: string
    contentType?: string
    description?: string
    name?: string
    compileCommand?: string
    template?: string
    codeTemplate?: string
    spjEnable?: number
    oj?: string
    seq?: number
 }

  // list或detail返回结构
  export interface Row {
    id?: string
    contentType?: string
    description?: string
    name?: string
    compileCommand?: string
    template?: string
    codeTemplate?: string
    spjEnable?: number
    oj?: string
    seq?: number
  }

}