import type { BaseEnum } from '@/enums/base';

// 主题状态枚举接口
interface TopicInfoStatusEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 主题状态枚举
 * 描述主题的当前状态
 */
export const TopicInfoStatus: { [key: string]: TopicInfoStatusEnum } = {
  /**
   * 未开始
   */
  NOT_STARTED: {
    code: 'NotStarted',
    name: '未开始',
    tagType: 'info'
  },

  /**
   * 进行中
   */
  IN_PROGRESS: {
    code: 'InProgress',
    name: '进行中',
    tagType: 'success'
  },

  /**
   * 已完成
   */
  COMPLETED: {
    code: 'Completed',
    name: '已完成',
    tagType: 'warning'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): TopicInfoStatusEnum | null {
  return Object.values(TopicInfoStatus).find(item => item.code === code) || null;
} 