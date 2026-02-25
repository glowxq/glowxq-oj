import type { IPageQuery } from '@/api/interface'

export namespace ICodeMonitor {

  // 查询条件
  export interface Query extends IPageQuery {
    monitorUserId?: number
    overlayUserId?: number
    monitorPhone?: string
    overlayPhone?: string
    monitorName?: string
    overlayName?: string
    monitorCode?: string
    overlayCode?: string
    codeMode?: string
    monitorStatus?: string
    version?: number
  }

  // 编辑form表单
  export interface Form {
    id?: number
    monitorUserId?: number
    overlayUserId?: number
    monitorPhone?: string
    overlayPhone?: string
    monitorName?: string
    overlayName?: string
    monitorCode?: string
    overlayCode?: string
    codeMode?: string
    monitorStatus?: string
    version?: number
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    monitorUserId?: number
    overlayUserId?: number
    monitorPhone?: string
    overlayPhone?: string
    monitorName?: string
    overlayName?: string
    monitorCode?: string
    overlayCode?: string
    codeMode?: string
    monitorStatus?: string
    version?: number
    language?: string
    createTime?: string
    updateTime?: string
  }

  // 搜索代码监控的查询参数
  export interface SearchMonitorCodeDTO extends IPageQuery {
    searchKey?: string
    monitorUserId?: number
  }

  // 老师推送覆盖代码的参数
  export interface CoveredCodeMonitorDTO {
    id: number
    overlayCode: string
  }

}