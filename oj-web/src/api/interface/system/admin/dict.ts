import type { IPageQuery } from '@/api/interface';

export namespace IDict {
  // 获取所有字典信息
  export interface DictCustom {
    callbackShowStyle: string;
    codeName: string;
    id: string;
    isLock: string;
    isShow: string;
    sort: number;
    sysDictTypeId: number;
    alias?: string;
  }

  // 字典类别列表查询
  export interface DictTypeQuery extends IPageQuery {
    typeName: string;
    typeCode: string;
  }

  // 字典类别列表
  export interface DictType {
    id?: number;
    typeName: string;
    typeCode: string;
    isLock?: string;
    isShow?: string;
    delFlag?: string;
    remark: string;
    createTime?: string;
    updateTime?: string;
    isDynamic: boolean;
  }

  // 字典分类option
  export interface DictOption {
    value: string;
    label: string;
  }

  // 字典分类类型
  export interface DictCategory {
    label: string;
    options: DictOption[];
  }

  // 字典列表查询
  export interface DictQuery extends IPageQuery {
    sysDictTypeId: number;
    codeName: string;
  }

  export interface Dict {
    id?: number;
    sysDictTypeId?: number;
    codeName: string;
    alias?: string;
    sort?: number;
    callbackShowStyle: string;
    remark: string;
    isLock?: string;
    isShow?: string;
    delFlag?: string;
    createTime?: string;
    updateTime?: string;
  }
}
