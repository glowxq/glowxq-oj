import type { BaseEnum } from '@/enums/base';

// 比赛类型枚举接口
interface ContestTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

// 实现比赛类型枚举对象
export const ContestType: { [key: string]: ContestTypeEnum } = {
  // 比赛模式
  TYPE_ACM: {
    code: 0,
    name: 'ACM',
    tooltip: 'ACM竞赛模式',
    text: '国际大学生程序设计竞赛模式'
  },
  TYPE_OI: {
    code: 1,
    name: 'OI',
    tooltip: 'OI竞赛模式',
    text: '信息学奥赛模式'
  },

  // 比赛状态
  STATUS_SCHEDULED: {
    code: -1,
    name: 'Scheduled',
    tooltip: '未开始',
    text: '比赛已安排但尚未开始'
  },
  STATUS_RUNNING: {
    code: 0,
    name: 'Running',
    tooltip: '进行中',
    text: '比赛正在进行'
  },
  STATUS_ENDED: {
    code: 1,
    name: 'Ended',
    tooltip: '已结束',
    text: '比赛已结束'
  },

  // 比赛权限
  AUTH_PUBLIC: {
    code: 0,
    name: 'Public',
    tooltip: '公开比赛',
    text: '所有用户可参加'
  },
  AUTH_PRIVATE: {
    code: 1,
    name: 'Private',
    tooltip: '私有比赛',
    text: '仅特定用户可参加'
  },
  AUTH_PROTECT: {
    code: 2,
    name: 'Protect',
    tooltip: '受保护比赛',
    text: '需要密码才能参加'
  },

  // 比赛记录类型
  RECORD_NOT_AC_PENALTY: {
    code: -1,
    name: '未AC通过算罚时',
    tooltip: '未通过并计算罚时',
    text: '提交未通过并且计算罚时'
  },
  RECORD_NOT_AC_NOT_PENALTY: {
    code: 0,
    name: '未AC通过不罚时',
    tooltip: '未通过不计算罚时',
    text: '提交未通过但不计算罚时'
  },
  RECORD_AC: {
    code: 1,
    name: 'AC通过',
    tooltip: '通过',
    text: '提交通过'
  }
};

// 添加 matchCode 方法
export function matchCode(code: number): ContestTypeEnum | null {
  return Object.values(ContestType).find(item => item.code === code) || null;
} 