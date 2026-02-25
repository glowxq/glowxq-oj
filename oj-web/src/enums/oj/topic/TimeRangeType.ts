import type { BaseEnum } from '@/enums/base';

// 时间范围类型枚举接口
interface TimeRangeTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 时间范围类型枚举
 * 描述时间范围外的提交和访问规则
 */
export const TimeRangeType: { [key: string]: TimeRangeTypeEnum } = {
  /**
   * 严格查看时间策略
   */
  STRICT_TIME_VIEW: {
    code: 'StrictTimeView',
    name: '严格时间-限制查看',
    tooltip: '必须在规定时间内才能打开主题详情',
    text: '必须在规定时间内才能打开主题详情'
  },

  /**
   * 严格提交时间策略
   */
  STRICT_TIME_SUBMIT: {
    code: 'StrictTimeSubmit',
    name: '严格时间-限制做题',
    tooltip: '非规定时间内能打开主题详情，但是不能做题提交',
    text: '非规定时间内能打开主题详情，但是不能做题提交'
  },

  /**
   * 固定比例扣分
   */
  PENALTY_FIXED: {
    code: 'PenaltyFixed',
    name: '超时固定扣分',
    tooltip: '任意时间都可以进入主题，但是超时扣 x%的分数（需配合扣分率参数使用）',
    text: '任意时间都可以进入主题，但是超时扣 x%的分数（需配合扣分率参数使用）'
  },

  /**
   * 线性递减扣分
   */
  PENALTY_GRADUAL: {
    code: 'PenaltyGradual',
    name: '超时递减扣分',
    tooltip: '任意时间都可以进入主题，但是超时后按小时线性扣分，每小时扣x% （需配合扣分率参数使用）',
    text: '任意时间都可以进入主题，但是超时后按小时线性扣分，每小时扣x% （需配合扣分率参数使用）'
  },

  /**
   * 无限制策略
   */
  UNRESTRICTED: {
    code: 'Unrestricted',
    name: '无限制策略',
    tooltip: '永久开放提交和查看',
    text: '永久开放提交和查看'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): TimeRangeTypeEnum | null {
  return Object.values(TimeRangeType).find(item => item.code === code) || null;
} 