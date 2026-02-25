import type { BaseEnum } from '@/enums/base';

// 主题判题类型枚举接口
interface TopicJudgeTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 主题判题类型枚举
 * 用于标识不同的判题类型
 */
export const TopicJudgeType: { [key: string]: TopicJudgeTypeEnum } = {
  /**
   * ACM 判题模式
   */
  ACM: {
    code: 0,
    name: 'acm',
    tooltip: 'ACM 判题模式',
    text: 'ACM 判题模式'
  },

  /**
   * OI 判题模式
   */
  OI: {
    code: 1,
    name: 'oi',
    tooltip: 'OI 判题模式',
    text: 'OI 判题模式'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: number): TopicJudgeTypeEnum | null {
  return Object.values(TopicJudgeType).find(item => item.code === code) || null;
} 