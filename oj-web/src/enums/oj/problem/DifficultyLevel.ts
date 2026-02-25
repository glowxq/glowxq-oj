import type { BaseEnum } from '@/enums/base';

// 难度等级枚举接口
interface DifficultyLevelEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
  color?: string;
}

/**
 * 难度等级枚举
 * 用于标识不同的题目难度等级
 */
export const DifficultyLevel: { [key: string]: DifficultyLevelEnum } = {
  /**
   * 入门
   */
  ENTRY: {
    code: 0,
    name: '入门',
    tooltip: '入门级难度',
    text: '适合初学者的基础题目',
    color: '#19be6b'
  },

  /**
   * 简单
   */
  EASY: {
    code: 1,
    name: '简单',
    tooltip: '简单难度',
    text: '需要基本编程知识的简单题目',
    color: '#67c23a'
  },

  /**
   * 中等
   */
  MEDIUM: {
    code: 2,
    name: '中等',
    tooltip: '中等难度',
    text: '需要一定算法知识的中等难度题目',
    color: '#e6a23c'
  },

  /**
   * 困难
   */
  HARD: {
    code: 3,
    name: '困难',
    tooltip: '困难难度',
    text: '需要较复杂算法知识的困难题目',
    color: '#f56c6c'
  },

  /**
   * 专家
   */
  EXPERT: {
    code: 4,
    name: '专家',
    tooltip: '专家级难度',
    text: '需要深入的算法知识和技巧的高难度题目',
    color: '#909399'
  }
};

/**
 * 根据code获取枚举
 * @param code 
 * @returns 枚举值或null
 */
export function matchCode(code: number): DifficultyLevelEnum | null {
  return Object.values(DifficultyLevel).find(item => item.code === code) || null;
} 