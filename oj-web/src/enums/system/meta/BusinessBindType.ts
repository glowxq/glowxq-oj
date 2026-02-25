import type { BaseEnum } from '@/enums/base';

// 业务绑定类型枚举接口
interface BusinessBindTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 业务绑定类型枚举
 * 用于标识不同的业务绑定类型
 */
export const BusinessBindType: { [key: string]: BusinessBindTypeEnum } = {
  /**
   * 用户
   */
  USER: {
    code: 'User',
    name: '用户',
    tooltip: '用户相关业务',
    text: '用户相关业务'
  },

  /**
   * 商品
   */
  PRODUCT: {
    code: 'Product',
    name: '商品',
    tooltip: '商品相关业务',
    text: '商品相关业务'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): BusinessBindTypeEnum | null {
  return Object.values(BusinessBindType).find(item => item.code === code) || null;
} 