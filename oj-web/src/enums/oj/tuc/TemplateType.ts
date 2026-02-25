import type { BaseEnum } from '@/enums/base';

// 模板类型枚举接口
interface TemplateTypeEnum extends BaseEnum {
  tooltip?: string;
  description?: string;
  icon?: string;
}

/**
 * 测试模板类型枚举
 * 用于标识不同的测试模板类型
 */
export const TemplateType: { [key: string]: TemplateTypeEnum } = {
  /**
   * 画笔测试
   */
  PEN: {
    code: 'pen',
    name: '画笔测试',
    tooltip: '绘制图形和路径',
    description: '测试基本绘图功能、条件语句和循环结构',
    icon: 'Brush'
  },

  /**
   * 一维数组测试
   */
  ARRAY: {
    code: 'arr',
    name: '数组测试',
    tooltip: '一维数组操作',
    description: '测试数组的创建、访问、修改和遍历',
    icon: 'List'
  },

  /**
   * 二维数组测试
   */
  GRID: {
    code: 'grid',
    name: '网格测试',
    tooltip: '二维数组操作',
    description: '测试二维数组的创建、访问、修改和遍历',
    icon: 'Grid'
  },

  /**
   * 树结构测试
   */
  TREE: {
    code: 'tree',
    name: '树结构测试',
    tooltip: '树结构操作',
    description: '测试树的创建、遍历、修改和删除',
    icon: 'Operation'
  },

  /**
   * 链表测试
   */
  LIST: {
    code: 'list',
    name: '链表测试',
    tooltip: '链表结构操作',
    description: '测试链表的创建、遍历、插入和删除',
    icon: 'Connection'
  }
};

/**
 * 根据code获取枚举
 * @param code 
 * @returns 枚举值或null
 */
export function matchCode(code: string): TemplateTypeEnum | null {
  return Object.values(TemplateType).find(item => item.code === code) || null;
}

export default TemplateType; 