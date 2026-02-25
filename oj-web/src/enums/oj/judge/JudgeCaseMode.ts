import type { BaseEnum } from '@/enums/base';

// 评测用例模式枚举接口
interface JudgeCaseModeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

// 实现评测用例模式枚举对象
export const JudgeCaseMode: { [key: string]: JudgeCaseModeEnum } = {
  DEFAULT: {
    code: 'default',
    name: '默认模式',
    tooltip: '逐个评测所有测试用例',
    text: '按顺序逐个评测所有测试用例，总分为所有测试用例得分之和'
  },
  SUBTASK_LOWEST: {
    code: 'subtask_lowest',
    name: '子任务最低分模式',
    tooltip: '以子任务中最低分计算该子任务得分',
    text: '将测试用例划分为多个子任务，每个子任务的得分取该子任务中所有测试用例的最低分'
  },
  SUBTASK_AVERAGE: {
    code: 'subtask_average',
    name: '子任务平均分模式',
    tooltip: '以子任务中所有测试用例平均分计算该子任务得分',
    text: '将测试用例划分为多个子任务，每个子任务的得分取该子任务中所有测试用例的平均分'
  },
  ERGODIC_WITHOUT_ERROR: {
    code: 'ergodic_without_error',
    name: '遍历不包含错误',
    tooltip: '遍历所有测试用例但不统计错误',
    text: '遍历所有测试用例，但不将错误结果计入最终评分'
  }
};

// 添加 matchCode 方法
export function matchCode(code: string): JudgeCaseModeEnum | null {
  return Object.values(JudgeCaseMode).find(item => item.code === code) || null;
} 