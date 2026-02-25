import type { BaseEnum } from '@/enums/base';

// 提交评测类型枚举接口
interface SubmitJudgeTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 提交评测类型枚举
 * 用于标识不同的评测方式
 */
export const SubmitType: { [key: string]: SubmitJudgeTypeEnum } = {
  /**
   * 普通评测
   */
  NORMAL_JUDGE: {
    code: 'NormalJudge',
    name: '普通评测',
    tooltip: '普通评测',
    text: '标准评测流程'
  },

  /**
   * 客观题评测
   */
  FIXED_JUDGE: {
    code: 'FixedJudge',
    name: '客观题评测',
    tooltip: '客观题评测',
    text: '针对选择、填空等客观题的评测'
  },

  /**
   * 远程评测
   */
  REMOTE_JUDGE: {
    code: 'RemoteJudge',
    name: '远程评测',
    tooltip: '远程评测',
    text: '使用远程评测系统进行评测'
  },

  /**
   * 在线调试
   */
  TEST_JUDGE: {
    code: 'TestJudge',
    name: '在线调试',
    tooltip: '在线调试',
    text: '用于测试和调试代码'
  },

  /**
   * 编译特判程序
   */
  COMPILE_SPJ: {
    code: 'CompileSpj',
    name: '编译特判程序',
    tooltip: '编译特判程序',
    text: '编译用于特殊判题的程序'
  },

  /**
   * 编译交互程序
   */
  COMPILE_INTERACTIVE: {
    code: 'CompileInteractive',
    name: '编译交互程序',
    tooltip: '编译交互程序',
    text: '编译用于交互式题目的程序'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): SubmitJudgeTypeEnum | null {
  return Object.values(SubmitType).find(item => item.code === code) || null;
}
