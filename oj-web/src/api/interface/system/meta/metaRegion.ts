import type { IPageQuery } from '@/api/interface'

export namespace IMetaRegion {

  // 查询条件
  export interface Query extends IPageQuery {
    parentId?: number
    ancestors?: string
    name?: string
    pinyin?: string
    pinyinPrefix?: string
    level?: number
    enable?: string
    tenantId?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    parentId?: number
    ancestors?: string
    name?: string
    pinyin?: string
    pinyinPrefix?: string
    level?: number
    enable?: string
    tenantId?: string
 }

  // list或detail返回结构
  export interface Row {
    id: number
    parentId?: number
    ancestors?: string
    name?: string
    pinyin?: string
    pinyinPrefix?: string
    level?: number
    enable?: string
    tenantId?: string
  }

  // 树形结构节点（用于前端渲染）
  export interface TreeNode extends Row {
    children?: TreeNode[]
    hasChildren?: boolean
    loading?: boolean
    expanded?: boolean
    _originalData?: Row
  }

}