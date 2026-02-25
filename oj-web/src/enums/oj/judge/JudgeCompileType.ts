import type { BaseEnum } from '@/enums/base';

// 评测编译类型枚举接口
interface JudgeCompileTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 评测编译类型枚举
 * 用于标识不同的编译模式
 */
export const JudgeCompileType: { [key: string]: JudgeCompileTypeEnum } = {
  /**
   * 编译特判程序
   */
  COMPILE_SPJ: {
    code: 'CompileSpj',
    name: '编译SPJ',
    tooltip: '编译特殊判题程序',
    text: '编译特殊判题程序，用于评测特殊判题题目'
  },

  /**
   * 编译交互程序
   */
  COMPILE_INTERACTIVE: {
    code: 'CompileInteractive',
    name: '编译交互式题目',
    tooltip: '编译交互式评测程序',
    text: '编译交互式评测程序，用于评测交互式题目'
  }
};

/**
 * 根据code获取枚举
 * @param code 
 * @returns 枚举值或null
 */
export function matchCode(code: string): JudgeCompileTypeEnum | null {
  return Object.values(JudgeCompileType).find(item => item.code === code) || null;
} 