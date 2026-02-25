import type { BaseEnum } from '@/enums/base';

// 题目来源类型枚举接口
interface ProblemSourceTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 题目来源类型枚举
 * 用于标识不同的题目来源
 */
export const ProblemSourceType: { [key: string]: ProblemSourceTypeEnum } = {
  /**
   * GlowOJ
   */
  GlowOJ: {
    code: 'GlowOJ',
    name: 'GlowOJ',
    tooltip: 'GlowOJ自建题目',
    text: 'GlowOJ自建题目'
  },

  /**
   * Hoj
   */
  HOJ: {
    code: 'Hoj',
    name: 'Hoj',
    tooltip: 'Hoj',
    text: 'Hoj来源的题目'
  },

  /**
   * Hydro
   */
  HYDRO: {
    code: 'Hydro',
    name: 'Hydro',
    tooltip: 'Hydro',
    text: 'Hydro来源的题目'
  },

  /**
   * Other
   */
  OTHER: {
    code: 'Other',
    name: 'Other',
    tooltip: 'Other',
    text: '其他来源的题目'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): ProblemSourceTypeEnum | null {
  return Object.values(ProblemSourceType).find(item => item.code === code) || null;
}
