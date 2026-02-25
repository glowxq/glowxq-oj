import type { BaseEnum } from "@/enums/base";

/**
 * 性别枚举
 * 用于标识不同的性别类型
 */
export const Sex = {
  /**
   * 男性
   */
  Male: {
    code: "Male",
    name: "男",
    description: "男性"
  },

  /**
   * 女性
   */
  Female: {
    code: "Female",
    name: "女",
    description: "女性"
  }
} as const;

/**
 * 根据code获取枚举
 * @param code
 * @returns 枚举值或null
 */
export function matchCode(code: string): (typeof Sex)[keyof typeof Sex] | null {
  return (
    Object.values(Sex).find((item) => item.code === code) || null
  );
}

/**
 * 获取所有性别选项
 * @returns 性别数组
 */
export function getSexOptions(): (typeof Sex)[keyof typeof Sex][] {
  return Object.values(Sex);
}
