import type { BaseEnum } from '@/enums/base';

interface CodeMonitorStatusEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

export const CodeMonitorStatus: { [key: string]: CodeMonitorStatusEnum } = {
  MONITOR_PUSH: {
    code: 'MonitorPush',
    name: '监控推送',
    tooltip: '监控端（学生）推送代码',
    text: '监控端（学生）推送代码'
  },
  COVERED_PUSH: {
    code: 'CoveredPush',
    name: '覆盖推送',
    tooltip: '覆盖端（老师）推送代码',
    text: '覆盖端（老师）推送代码'
  },
  MONITOR_PULL: {
    code: 'MonitorPull',
    name: '监控拉取',
    tooltip: '监控端（学生）拉取代码',
    text: '监控端（学生）拉取代码'
  },
  MONITOR_REJECT: {
    code: 'MonitorReject',
    name: '监控拒绝',
    tooltip: '监控端（学生）拒绝监控',
    text: '监控端（学生）拒绝监控'
  }
};

export function matchCode(code: string): CodeMonitorStatusEnum | null {
  return Object.values(CodeMonitorStatus).find(item => item.code === code) || null;
} 