import type { BaseEnum } from '@/enums/base';

interface CodeModeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

export const CodeMode: { [key: string]: CodeModeEnum } = {
  GlowC: {
    code: 'Glowc',
    name: 'Glowc模式',
    tooltip: 'Glowc代码模式',
    text: 'Glowc代码模式'
  },
  OJ: {
    code: 'OJ',
    name: 'OJ模式',
    tooltip: 'OJ代码模式',
    text: 'OJ代码模式'
  }
};

export function matchCode(code: string): CodeModeEnum | null {
  return Object.values(CodeMode).find(item => item.code === code) || null;
}
