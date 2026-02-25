export namespace ITenant {

  // 查询条件
  export interface Query {
    /** 关键字 */
    keyword?: string
    /** 租户编码 */
    tenantCode?: string
    /** 是否显示 */
    show?: boolean
    /** 是否启用 */
    enable?: boolean
    /** 页码 */
    page: number
    /** 每页条数 */
    limit: number
  }

  // 编辑form表单
  export interface Form {
    /** ID */
    id?: number
    /** 租户ID */
    tenantId?: string
    /** 租户编码 */
    tenantCode: string
    /** 租户名称 */
    tenantName: string
    /** 联系人姓名 */
    contactName?: string
    /** 联系人手机号 */
    contactPhone?: string
    /** 联系人邮箱 */
    contactEmail?: string
    /** 是否显示 */
    show?: boolean
    /** 租户密码 */
    password?: string
    /** 是否启用 */
    enable?: boolean
    /** 到期时间 */
    expiredTime?: string
    /** 最大用户数 */
    maxUserNum?: number
    /** Logo地址 */
    logoUrl?: string
    /** 租户文本 */
    text?: string
    /** 系统名称 */
    systemName?: string
    /** 首页图片地址 */
    homeImageUrl?: string
    /** 主题色 */
    themeColor?: string
    /** 配置信息 */
    config?: string
    /** 自定义域名 */
    customDomain?: string
  }

  // list或detail返回结构
  export interface Row {
    /** 主键ID */
    id: number
    /** 租户ID */
    tenantId: string
    /** 租户编码 */
    tenantCode: string
    /** 租户名称 */
    tenantName: string
    /** 联系人姓名 */
    contactName?: string
    /** 联系人手机号 */
    contactPhone?: string
    /** 联系人邮箱 */
    contactEmail?: string
    /** 是否显示 */
    show?: boolean
    /** 租户密码 */
    password?: string
    /** 是否启用 */
    enable?: boolean
    /** 到期时间 */
    expiredTime?: string
    /** 最大用户数 */
    maxUserNum?: number
    /** 当前用户数 */
    currentUserNum?: number
    /** Logo地址 */
    logoUrl?: string
    /** 租户文本 */
    text?: string
    /** 系统名称 */
    systemName?: string
    /** 首页图片地址 */
    homeImageUrl?: string
    /** 主题色 */
    themeColor?: string
    /** 配置信息 */
    config?: string
    /** 自定义域名 */
    customDomain?: string
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

}