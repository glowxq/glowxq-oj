import type { BaseEnum } from '@/enums/base';

// WebSocket业务类型枚举接口
interface WebsocketBusinessTypeEnum extends BaseEnum {
  /**
   * 方向 
   * 0:全双工 
   * 1:S->C 
   * -1:C->S
   */
  direction: number;
}

/**
 * WebSocket业务类型枚举
 * 用于标识不同的WebSocket业务类型
 */
export const WebsocketBusinessType: { [key: string]: WebsocketBusinessTypeEnum } = {

  /**
   * 未知
   */
  UNKNOWN: {
    code: 'Unknown',
    name: '未知',
    direction: 0
  },
  
  /**
   * 代码监控推送
   */
  CODE_MONITOR: {
    code: 'CodeMonitor',
    name: '代码监控推送',
    direction: -1
  },

  /**
   * 测评结果
   */
  JUDGE_NOTIFY: {
    code: 'JudgeNotify',
    name: '测评结果',
    direction: 1
  },

  /**
   * 上课通知
   */
  BEGIN_COURSE: {
    code: 'BeginCourse',
    name: '上课通知',
    direction: 1
  },

  /**
   * 推送覆盖代码
   */
  PUSH_COVERED_CODE: {
    code: 'PushCoveredCode',
    name: '推送覆盖代码',
    direction: 1
  }
};

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): WebsocketBusinessTypeEnum | null {
  return Object.values(WebsocketBusinessType).find(item => item.code === code) || null;
}


