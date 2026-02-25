import type { BaseEnum } from '@/enums/base';

// 评测模式枚举接口
interface JudgeModeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

// 实现评测模式枚举对象
export const JudgeMode: { [key: string]: JudgeModeEnum } = {
  TEST: {
    code: 'test',
    name: '测试模式',
    tooltip: '测试代码运行情况',
    text: '仅测试代码运行结果，不进行实际评测'
  },
  DEFAULT: {
    code: 'default',
    name: '默认模式',
    tooltip: '标准输入输出评测',
    text: '使用标准输入输出方式评测提交的代码'
  },
  SPJ: {
    code: 'spj',
    name: 'SPJ模式',
    tooltip: '特殊评测模式',
    text: '使用特判程序评测提交代码的结果'
  },
  INTERACTIVE: {
    code: 'interactive',
    name: '交互式模式',
    tooltip: '交互式评测',
    text: '提交的代码需要与评测程序进行实时交互'
  }
};

// 添加 matchCode 方法
export function matchCode(code: string): JudgeModeEnum | null {
  return Object.values(JudgeMode).find(item => item.code === code) || null;
} 