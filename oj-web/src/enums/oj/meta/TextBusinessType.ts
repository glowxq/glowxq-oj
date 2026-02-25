import type { BaseEnum } from '@/enums/base';

// 文本业务类型枚举接口
interface TextBusinessTypeEnum extends BaseEnum {
}

/**
 * 文本业务类型枚举
 * 用于标识不同的文本业务类型
 */
export const TextBusinessType: { [key: string]: TextBusinessTypeEnum } = {
  /**
   * 公告
   */
  ANNOUNCEMENT: {
    code: 'Announcement',
    name: '公告'
  },

  /**
   * 临时
   */
  TEMP: {
    code: 'Temp',
    name: '临时'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): TextBusinessTypeEnum | null {
  return Object.values(TextBusinessType).find(item => item.code === code) || null;
} 