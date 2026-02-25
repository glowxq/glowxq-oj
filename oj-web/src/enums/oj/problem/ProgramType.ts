import type { BaseEnum } from '@/enums/base';

// 程序类型枚举接口
interface ProgramTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 程序类型枚举
 * 用于标识不同的程序类型
 */
export const ProgramType: { [key: string]: ProgramTypeEnum } = {

  ACM: {
    code: 0,
    name: 'ACM',
    tooltip: 'ACM',
    text: 'ACM程序'
  },

  OI: {
    code: 1,
    name: 'OI',
    tooltip: 'OI',
    text: 'OI程序'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: number): ProgramTypeEnum | null {
  return Object.values(ProgramType).find(item => item.code === code) || null;
}
