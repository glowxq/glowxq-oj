import type { BaseEnum } from '@/enums/base';

// 编程语言枚举接口
interface ProgramLanguageEnum extends BaseEnum {
}

/**
 * 编程语言枚举
 * 用于标识不同的编程语言
 */
export const ProgramLanguage: { [key: string]: ProgramLanguageEnum } = {
  /**
   * C++
   */
  CPP: {
    code: 'C++',
    name: 'C++代码'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): ProgramLanguageEnum | null {
  return Object.values(ProgramLanguage).find(item => item.code === code) || null;
} 