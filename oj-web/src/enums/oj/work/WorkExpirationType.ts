import type { BaseEnum } from '@/enums/base';

// 过期处理策略枚举接口
interface WorkExpirationTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 过期处理策略枚举
 * 用于标识不同的过期处理策略
 */
export const WorkExpirationType: { [key: string]: WorkExpirationTypeEnum } = {
  // AllowedTime
  ALLOWED_TIME: {
    code: 'AllowedTime',
    name: '规定时间',
    tooltip: '必须在规定时间内才能打开，未到规定时间不能打开',
    text: '必须在规定时间内才能打开，未到规定时间不能打开'
  },

  /**
   * 禁止提交
   */
  BAN_COMMIT: {
    code: 'BanCommit',
    name: '禁止提交',
    tooltip: '过期后可以查看提详情，禁止点击题目进入做题界面',
    text: '过期后可以查看提详情，禁止点击题目进入做题界面'
  },

  /**
   * 禁止查看
   */
  BAN_VIEW: {
    code: 'BanView',
    name: '禁止查看',
    tooltip: '过期后禁止查看作业详情',
    text: '过期后禁止查看作业详情'
  },

  /**
   * 扣分10%
   */
  DEDUCT_10: {
    code: 'Deduct10',
    name: '扣分10%',
    tooltip: '过期后可以做题，但扣分10%',
    text: '过期后可以做题，但扣分10%'
  },

  /**
   * 无策略
   */
  NONE: {
    code: 'None',
    name: '无策略',
    tooltip: '过期后无任何处理',
    text: '过期后无任何处理'
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): WorkExpirationTypeEnum | null {
  return Object.values(WorkExpirationType).find(item => item.code === code) || null;
} 