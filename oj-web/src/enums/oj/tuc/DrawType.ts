import type { BaseEnum } from '@/enums/base';

// 绘图类型枚举接口
interface DrawTypeEnum extends BaseEnum {
  tooltip?: string;
  description?: string;
  icon?: string;
}

/**
 * 绘图类型枚举
 * 用于标识不同的绘图组件类型
 */
export const DrawType: { [key: string]: DrawTypeEnum } = {
  /**
   * 画笔组件
   */
  PEN: {
    code: 'pen',
    name: '画笔',
    tooltip: '画笔(pen)组件',
    description: '用于绘制图形和路径，控制画笔的颜色、粗细和动画速度等参数',
    icon: 'Brush'
  },

  /**
   * 一维数组组件
   */
  ARRAY: {
    code: 'arr',
    name: '数组',
    tooltip: '一维数组(arr)组件',
    description: '用于数组的创建、访问、修改和遍历，控制单元格大小、颜色和动画速度等参数',
    icon: 'List'
  },

  /**
   * 二维数组组件
   */
  GRID: {
    code: 'grid',
    name: '网格',
    tooltip: '二维数组(grid)组件',
    description: '用于二维数组的创建、访问、修改和遍历，控制单元格大小、颜色和动画速度等参数',
    icon: 'Grid'
  },

  /**
   * 树结构组件
   */
  TREE: {
    code: 'tree',
    name: '树结构',
    tooltip: '树结构(tree)组件',
    description: '用于树的创建、遍历、修改和删除，控制节点大小、颜色和动画速度等参数',
    icon: 'Operation'
  },

  /**
   * 链表组件
   */
  LIST: {
    code: 'list',
    name: '链表',
    tooltip: '链表(list)组件',
    description: '用于链表的创建、遍历、插入和删除，控制节点大小、颜色和动画速度等参数',
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
export function matchCode(code: string): DrawTypeEnum | null {
  return Object.values(DrawType).find(item => item.code === code) || null;
}

export default DrawType; 