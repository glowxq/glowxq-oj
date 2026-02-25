import type { BaseEnum } from '@/enums/base';

// 主题类型枚举接口
interface TopicTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 主题类型枚举
 * 用于标识不同的主题类型
 */
export const TopicType: { [key: string]: TopicTypeEnum } = {
  /**
   * 练习
   */
  EXERCISE: {
    code: 'Exercise',
    name: '练习',
    tooltip: '默认设置 无限制的随意测试',
    text: '默认设置 无限制的随意测试'
  },

  /**
   * 作业
   */
  HOMEWORK: {
    code: 'Homework',
    name: '作业',
    tooltip: '默认设置 明确提交时间,过期可以补交但是扣分',
    text: '默认设置 明确提交时间,过期可以补交但是扣分'
  },

  /**
   * 比赛
   */
  CONTEST: {
    code: 'Contest',
    name: '比赛',
    tooltip: '默认设置 过期禁止提交',
    text: '默认设置 过期禁止提交'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): TopicTypeEnum | null {
  return Object.values(TopicType).find(item => item.code === code) || null;
} 