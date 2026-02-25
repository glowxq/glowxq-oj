/**
 * 基础枚举类型接口
 * 所有枚举类型都应继承此接口
 */
export interface BaseEnum {
  /**
   * 枚举代码值 - 支持字符串和数字类型
   */
  code: string | number;
  
  /**
   * 枚举显示名称
   */
  name: string;
  
  /**
   * 可选的枚举描述
   */
  description?: string;
  
  /**
   * 可选的悬停提示文本
   */
  tooltip?: string;
  
  /**
   * 其他自定义属性
   */
  [key: string]: any;
}

// BaseEnum接口已在上面定义并导出，不需要重复导出


