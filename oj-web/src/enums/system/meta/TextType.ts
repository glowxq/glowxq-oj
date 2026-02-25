import type { BaseEnum } from '@/enums/base';

// 文本类型枚举接口
interface TextTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 文本类型枚举
 * 用于标识不同的文本类型
 */
export const TextType: { [key: string]: TextTypeEnum } = {
  /**
   * Markdown 文本
   */
  MARKDOWN: {
    code: 'Markdown',
    name: 'Markdown',
    tooltip: 'Markdown格式文本',
    text: 'Markdown格式文本'
  },

  /**
   * 富文本
   */
  RICH_TEXT: {
    code: 'RichText',
    name: '富文本',
    tooltip: '富文本格式',
    text: '富文本格式'
  },

  /**
   * 普通文本
   */
  NORMAL_TEXT: {
    code: 'NormalText',
    name: '普通文本',
    tooltip: '普通文本格式',
    text: '普通文本格式'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): TextTypeEnum | null {
  return Object.values(TextType).find(item => item.code === code) || null;
} 