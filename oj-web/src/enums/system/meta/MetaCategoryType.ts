import type { BaseEnum } from '@/enums/base';

// 元数据分类类型枚举接口
interface MetaCategoryTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 元数据分类类型枚举
 * 用于标识不同的元数据分类类型
 */
export const MetaCategoryType: { [key: string]: MetaCategoryTypeEnum } = {
  /**
   * 系统分类
   */
  SYSTEM: {
    code: 'System',
    name: '系统分类',
    tooltip: '系统内置分类，不可修改',
    text: '系统内置分类，不可修改'
  },

  /**
   * 用户分类
   */
  USER: {
    code: 'User',
    name: '用户分类',
    tooltip: '用户自定义分类',
    text: '用户自定义分类'
  },

  /**
   * 业务分类
   */
  BUSINESS: {
    code: 'Business',
    name: '业务分类',
    tooltip: '与业务相关的分类',
    text: '与业务相关的分类'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): MetaCategoryTypeEnum | null {
  return Object.values(MetaCategoryType).find(item => item.code === code) || null;
} 