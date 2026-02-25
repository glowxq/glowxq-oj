import type { BaseEnum } from '@/enums/base';

// 作业类型枚举接口
interface WorkTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 作业类型枚举
 * 用于标识不同的作业类型
 */
export const WorkType: { [key: string]: WorkTypeEnum } = {
  /**
   * 普通作业
   */
  NORMAL: {
    code: 'Normal',
    name: '普通作业',
    tooltip: '明确提交时间',
    text: '明确提交时间'
  },

  /**
   * 考试
   */
  EXAM: {
    code: 'Exam',
    name: '考试',
    tooltip: '过期禁止提交',
    text: '过期禁止提交'
  },

  /**
   * 测试
   */
  TEST: {
    code: 'Test',
    name: '测试',
    tooltip: '无限制的随意测试',
    text: '无限制的随意测试'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): WorkTypeEnum | null {
  return Object.values(WorkType).find(item => item.code === code) || null;
} 