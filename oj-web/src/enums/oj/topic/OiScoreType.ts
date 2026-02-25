import type { BaseEnum } from '@/enums/base';

// OI 得分类型枚举接口
interface OiScoreTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * OI 得分类型枚举
 * 用于标识不同的得分类型
 */
export const OiScoreType: { [key: string]: OiScoreTypeEnum } = {
  /**
   * 最近得分
   */
  RECENT: {
    code: 'Recent',
    name: '最近得分',
    tooltip: '使用最近一次提交的得分',
    text: '使用最近一次提交的得分'
  },

  /**
   * 最高得分
   */
  HIGHEST: {
    code: 'Highest',
    name: '最高得分',
    tooltip: '使用历史提交中的最高得分',
    text: '使用历史提交中的最高得分'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): OiScoreTypeEnum | null {
  return Object.values(OiScoreType).find(item => item.code === code) || null;
} 