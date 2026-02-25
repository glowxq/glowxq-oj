import type { BaseEnum } from '@/enums/base';

// 测试用例类型枚举接口
interface TestCaseTypeEnum extends BaseEnum {
  tooltip?: string;
  text?: string;
}

/**
 * 测试用例类型枚举
 * 用于标识不同的测试用例类型
 */
export const TestCaseType: { [key: string]: TestCaseTypeEnum } = {
  /**
   * 文本数据
   */
  ManualEditing: {
    code: 'ManualEditing',
    name: '手动编辑',
    tooltip: '手动编辑',
    text: '手动编辑测试数据并保存'
  },

  /**
   * 文件数据
   */
  FileUpload: {
    code: 'FileUpload',
    name: '压缩包上传',
    tooltip: '压缩包上传',
    text: '上传测试用例 (ZIP压缩包)，您可以上传包含所有测试用例的ZIP压缩包，系统会自动解析并提取测试用例'
  },


};

/**
 * 根据code获取枚举
 * @param code 
 * @returns 枚举值或null
 */
export function matchCode(code: string): TestCaseTypeEnum | null {
  return Object.values(TestCaseType).find(item => item.code === code) || null;
} 