import type { IPageQuery } from '@/api/interface'

export namespace IJudgeServer {

  // 查询条件
  export interface Query extends IPageQuery {
    name?: string
    ip?: string
    port?: number
    url?: string
    cpuCore?: number
    freeMemory?: number
    taskNumber?: number
    maxTaskNumber?: number
    enable?: number
    remoteEnable?: number
    cfSubmittableEnable?: number
  }

  // 编辑form表单
  export interface Form {
    id?: number
    name?: string
    ip?: string
    port?: number
    url?: string
    cpuCore?: number
    freeMemory?: number
    taskNumber?: number
    maxTaskNumber?: number
    enable?: number
    remoteEnable?: number
    cfSubmittableEnable?: number
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    name?: string
    ip?: string
    port?: number
    url?: string
    cpuCore?: number
    freeMemory?: number
    taskNumber?: number
    maxTaskNumber?: number
    enable?: number
    remoteEnable?: number
    cfSubmittableEnable?: number
  }

}