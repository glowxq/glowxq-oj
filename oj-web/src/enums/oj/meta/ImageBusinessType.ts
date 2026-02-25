import type { BaseEnum } from '@/enums/base';

// 图片业务类型枚举接口
interface ImageBusinessTypeEnum extends BaseEnum {
}

/**
 * 图片业务类型枚举
 * 用于标识不同的图片业务类型
 */
export const ImageBusinessType: { [key: string]: ImageBusinessTypeEnum } = {
  /**
   * 轮播图
   */
  CAROUSEL: {
    code: 'Carousel',
    name: '轮播图'
  },

  /**
   * 临时图片
   */
  TEMP: {
    code: 'Temp',
    name: '临时图片'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): ImageBusinessTypeEnum | null {
  return Object.values(ImageBusinessType).find(item => item.code === code) || null;
} 