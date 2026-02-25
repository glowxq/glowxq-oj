import type { IPageQuery } from '@/api/interface'

export namespace ISysOperationLog {

  // 查询条件
  export interface Query extends IPageQuery {
    userId?: number
    traceId?: string
    spanId?: string
    username?: string
    ip?: string
    method?: string
    uri?: string
    header?: string
    module?: string
    description?: string
    param?: string
    request?: string
    response?: string
    error?: string
    businessType?: string
    errorMessage?: string
    costTime?: number
    tenantId?: string
  }

  // 编辑form表单
  export interface Form {
    id?: number
    userId?: number
    traceId?: string
    spanId?: string
    username?: string
    ip?: string
    method?: string
    uri?: string
    header?: string
    module?: string
    description?: string
    param?: string
    request?: string
    response?: string
    error?: string
    businessType?: string
    errorMessage?: string
    costTime?: number
    tenantId?: string
 }

  // list或detail返回结构
  export interface Row {
    id?: number
    userId?: number
    traceId?: string
    spanId?: string
    username?: string
    ip?: string
    method?: string
    uri?: string
    header?: string
    module?: string
    description?: string
    param?: string
    request?: string
    response?: string
    error?: string
    businessType?: string
    errorMessage?: string
    costTime?: number
    tenantId?: string
  }

}