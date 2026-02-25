import type { BaseEnum } from '@/enums/base'

// 文本业务类型枚举接口
interface TextBusinessTypeEnum extends BaseEnum {
  tooltip?: string
  text?: string
}

/**
 * 文本业务类型枚举
 * 用于标识不同的文本业务类型
 */
export const TextBusinessType: { [key: string]: TextBusinessTypeEnum } = {
  /**
   * 公告
   */
  ANNOUNCEMENT: {
    code: 'Announcement',
    name: '公告',
    tooltip: '系统公告信息',
    text: '系统公告信息 标题和具体内容都直接展示',
  },

  /**
   * 协议
   */
  AGREEMENT: {
    code: 'Agreement',
    name: '协议',
    tooltip: '协议',
    text: '协议 只展示标题，点击后弹窗显示具体内容',
  },

  /**
   * 信息
   */
  INFORMATION: {
    code: 'Information',
    name: '信息',
    tooltip: '一般信息',
    text: '一般信息直接展示信息',
  },
  /**
   * 信息
   */
  Cell: {
    code: 'Cell',
    name: '格子',
    tooltip: '格子',
    text: '显示一个好看的格子，点击后展示信息',
  },
}

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): TextBusinessTypeEnum | null {
  return Object.values(TextBusinessType).find(item => item.code === code) || null
}
