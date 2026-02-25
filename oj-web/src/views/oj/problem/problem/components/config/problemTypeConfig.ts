/**
 * 题目类型配置
 * 用于管理不同题目类型的配置信息
 */

import { ProblemType } from '@/enums/oj/problem'

/**
 * Tab显示配置类型定义
 */
export interface TabConfig {
  /** 该题目类型下可见的选项卡数组 */
  availableTabs: string[];
  /** 点击"下一步"按钮后默认跳转的选项卡 */
  defaultNextTab: string;
}

/**
 * 题目类型与Tab显示的配置规则
 *
 * 每种题目类型对应一个配置项，包含两个属性：
 * - availableTabs: 该题目类型下可见的选项卡数组
 * - defaultNextTab: 点击"下一步"按钮后默认跳转的选项卡
 *
 * Tab标识说明：
 * - basic: 基本信息选项卡（所有题型都显示）
 * - content: 题目内容选项卡（只对编程题显示）
 * - options: 选项&答案选项卡（只对客观题显示）
 * - testcase: 测试数据选项卡（只对编程题显示）
 * - advanced: 评测设置选项卡（只对编程题显示）
 * - codetpl: 代码模板选项卡（只对编程题显示）
 *
 * 题型对应的Tab规则：
 * 1. 编程题：基本信息、题目内容、测试数据、评测设置、代码模板
 * 2. 客观题（单选题、多选题、判断题、填空题、简答题）：基本信息、选项&答案
 */
export const problemTypeTabConfig: Record<string, TabConfig> = {
  // 编程题
  [ProblemType.PROGRAMMING.code]: {
    availableTabs: ['basic', 'content', 'testcase', 'advanced', 'codetpl'],
    defaultNextTab: 'content'
  },
  // 单选题
  [ProblemType.SINGLE_CHOICE.code]: {
    availableTabs: ['basic', 'options'],
    defaultNextTab: 'options'
  },
  // 多选题
  [ProblemType.MULTIPLE_CHOICE.code]: {
    availableTabs: ['basic', 'options'],
    defaultNextTab: 'options'
  },
  // 判断题
  [ProblemType.TRUE_FALSE.code]: {
    availableTabs: ['basic', 'options'],
    defaultNextTab: 'options'
  },
  // 填空题
  [ProblemType.FILL_BLANK.code]: {
    availableTabs: ['basic', 'options'],
    defaultNextTab: 'options'
  },
  // 简答题
  [ProblemType.SHORT_ANSWER.code]: {
    availableTabs: ['basic', 'options'],
    defaultNextTab: 'options'
  }
}

/**
 * 根据题目类型判断指定的tab是否可见
 * @param tabName 选项卡名称
 * @param problemType 题目类型
 * @returns 是否可见
 */
export const isTabVisible = (tabName: string, problemType: string): boolean => {
  // 基本信息tab所有题目类型都显示
  if (tabName === 'basic') return true

  // 如果找不到配置，则默认不显示
  if (!problemTypeTabConfig[problemType]) return false

  // 根据配置决定是否显示
  return problemTypeTabConfig[problemType].availableTabs.includes(tabName)
}

/**
 * 获取题目类型的下一步Tab
 * @param problemType 题目类型
 * @returns 下一步Tab名称
 */
export const getNextTab = (problemType: string): string => {
  if (problemTypeTabConfig[problemType]) {
    return problemTypeTabConfig[problemType].defaultNextTab
  }
  // 未找到配置，返回基本信息tab
  return 'basic'
}
