import type { IPageQuery } from '@/api/interface'

// 基础查询接口
export interface BaseQuery extends IPageQuery {
  // 分页相关字段已在IPageQuery中定义
  createTime?: string[] | Date[];
  createTimeStart?: string | Date;
  createTimeEnd?: string | Date;
  updateTime?: string[] | Date[];
  updateTimeStart?: string | Date;
  updateTimeEnd?: string | Date;
  [key: string]: any;
}

// 基础表单接口
export interface BaseForm {
  id?: number;
  createTime?: string | Date;
  updateTime?: string | Date;
  createId?: number;
  updateId?: number;
  [key: string]: any;
}

// 基础数据行接口
export interface BaseRow {
  id?: number;
  createTime?: string | Date;
  updateTime?: string | Date;
  createId?: number;
  updateId?: number;
  [key: string]: any;
} 