import type { BaseEnum } from '@/enums/base';

// 评测目录枚举接口
interface JudgeDirEnum extends BaseEnum {
  content: string;
  tooltip?: string;
  text?: string;
}

// 实现评测目录枚举对象
export const JudgeDir: { [key: string]: JudgeDirEnum } = {
  RUN_WORKPLACE_DIR: {
    code: 'RUN_WORKPLACE_DIR',
    name: '运行工作目录',
    content: '/judge/run',
    tooltip: '运行代码的工作目录',
    text: '用于存放运行代码的工作目录'
  },
  TEST_CASE_DIR: {
    code: 'TEST_CASE_DIR',
    name: '测试用例目录',
    content: '/judge/testcase',
    tooltip: '存放测试用例的目录',
    text: '用于存放测试用例文件的目录'
  },
  SPJ_WORKPLACE_DIR: {
    code: 'SPJ_WORKPLACE_DIR',
    name: 'SPJ工作目录',
    content: '/judge/spj',
    tooltip: '特判程序工作目录',
    text: '用于存放特判程序的工作目录'
  },
  INTERACTIVE_WORKPLACE_DIR: {
    code: 'INTERACTIVE_WORKPLACE_DIR',
    name: '交互式工作目录',
    content: '/judge/interactive',
    tooltip: '交互式程序工作目录',
    text: '用于存放交互式程序的工作目录'
  },
  TMPFS_DIR: {
    code: 'TMPFS_DIR',
    name: '临时目录',
    content: '/w',
    tooltip: '临时文件存储目录',
    text: '用于存放临时文件的目录'
  }
};

// 添加 matchCode 方法
export function matchCode(code: string): JudgeDirEnum | null {
  return Object.values(JudgeDir).find(item => item.code === code) || null;
}

// 添加 getContentByCode 方法
export function getContentByCode(code: string): string | null {
  const item = matchCode(code);
  return item ? item.content : null;
} 