import type { BaseEnum } from '@/enums/base';

// 题目类型枚举接口
interface ProblemTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 题目类型枚举
 * 用于标识不同的题目类型
 */
export const ProblemType: { [key: string]: ProblemTypeEnum } = {
  /**
   * 编程题
   */
  PROGRAMMING: {
    code: 'Programmer',
    name: '编程题',
    tooltip: '编程题',
    text: '需要编写代码解决的题目'
  },

  /**
   * 单选题
   */
  SINGLE_CHOICE: {
    code: 'SingleChoice',
    name: '单选题',
    tooltip: '单选题',
    text: '只有一个正确选项的选择题'
  },

  /**
   * 多选题
   */
  MULTIPLE_CHOICE: {
    code: 'MultipleChoice',
    name: '多选题',
    tooltip: '多选题',
    text: '有多个正确选项的选择题'
  },

  /**
   * 判断题
   */
  TRUE_FALSE: {
    code: 'TrueFalse',
    name: '判断题',
    tooltip: '判断题',
    text: '判断题目陈述是否正确'
  },

  /**
   * 填空题
   */
  FILL_BLANK: {
    code: 'FillBlank',
    name: '填空题',
    tooltip: '填空题',
    text: '在题目空白处填写内容'
  },

  /**
   * 简答题
   */
  SHORT_ANSWER: {
    code: 'ShortAnswer',
    name: '简答题',
    tooltip: '简答题',
    text: '需要简单文字回答的题目'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): ProblemTypeEnum | null {
  return Object.values(ProblemType).find(item => item.code === code) || null;
}
