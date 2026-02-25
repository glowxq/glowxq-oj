import type { BaseEnum } from '@/enums/base';

// 菜单图标类型枚举接口
interface MenuIconTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 菜单图标类型枚举
 * 用于标识不同的菜单图标类型
 */
export const MenuIconType: { [key: string]: MenuIconTypeEnum } = {
  /**
   * 图标
   */
  ICON: {
    code: 'Icon',
    name: 'Icon',
    tooltip: '图标类型',
    text: '图标类型'
  },

  /**
   * 图片
   */
  IMAGE: {
    code: 'Image',
    name: '图片',
    tooltip: '图片类型',
    text: '图片类型'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): MenuIconTypeEnum | null {
  return Object.values(MenuIconType).find(item => item.code === code) || null;
} 