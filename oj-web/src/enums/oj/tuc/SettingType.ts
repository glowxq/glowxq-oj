import type { BaseEnum } from '@/enums/base';

// 设置类型枚举接口
interface SettingTypeEnum extends BaseEnum {
  tooltip?: string;
  description?: string;
  icon?: string;
}

/**
 * 设置面板类型枚举
 * 用于标识不同的设置面板类型
 */
export const SettingType: { [key: string]: SettingTypeEnum } = {
  /**
   * 画笔设置
   */
  PEN: {
    code: 'pen',
    name: '画笔设置',
    tooltip: '画笔(pen)设置',
    description: '控制画笔的颜色、粗细和动画速度等参数',
    icon: 'Brush'
  },

  /**
   * 一维数组设置
   */
  ARRAY: {
    code: 'arr',
    name: '数组设置',
    tooltip: '一维数组(arr)设置',
    description: '控制一维数组的单元格大小、颜色和动画速度等参数',
    icon: 'List'
  },

  /**
   * 二维数组设置
   */
  GRID: {
    code: 'grid',
    name: '网格设置',
    tooltip: '二维数组(grid)设置',
    description: '控制二维数组的单元格大小、颜色和动画速度等参数',
    icon: 'Grid'
  },

  /**
   * 树结构设置
   */
  TREE: {
    code: 'tree',
    name: '树结构设置',
    tooltip: '树结构(tree)设置',
    description: '控制树节点的大小、颜色和动画速度等参数',
    icon: 'Operation'
  },

  /**
   * 链表设置
   */
  LIST: {
    code: 'list',
    name: '链表设置',
    tooltip: '链表(list)设置',
    description: '控制链表节点的大小、颜色和动画速度等参数',
    icon: 'Connection'
  },

  /**
   * 颜色对照表
   */
  COLORS: {
    code: 'colors',
    name: '颜色对照表',
    tooltip: '颜色对照表',
    description: '展示所有可用的颜色及其对应的编号',
    icon: 'ColorPicker'
  }
};

/**
 * 根据code获取枚举
 * @param code 
 * @returns 枚举值或null
 */
export function matchCode(code: string): SettingTypeEnum | null {
  return Object.values(SettingType).find(item => item.code === code) || null;
}

export default SettingType; 