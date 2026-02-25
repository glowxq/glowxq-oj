// 登录模块
import type { IPageQuery } from '@/api/interface';

export namespace IUser {
  export interface Query extends IPageQuery {
    username?: string;
    phone?: string;
    accountStatusCd?: string;
    startDate?: string;
    endDate?: string;
    nickname?: string;
    deptId?: number;
    isThisDeep?: boolean;
  }

  // 新增：搜索用户DTO
  export interface SearchUserDTO extends IPageQuery {
    searchKey?: string;
  }

  // 新增：搜索用户VO
  export interface SearchUserVO {
    id: number;
    username: string;
    phone: string;
    name: string;
    nickname: string;
    email: string;
  }

  // 新增：分页结果
  export interface PageResult<T> {
    current: number;
    limit: number;
    total: number;
    totalPage: number;
    rows: T[];
  }

  export interface Form {
    id?: number;
    username?: string;
    pwd?: string;
    phone: string;
    nickname: string;
    logo: string;
    age: number;
    sex: number;
    idCard: string;
    email: string;
    accountStatusCd: string;
    userTagCd: string;
    birthday: string;
  }

  export interface Info {
    id?: number;
    username: string;
    phone: string;
    nickname: string;
    name: string;
    logo: string;
    age: number;
    sex: number;
    idCard: string;
    email: string;
    accountStatusCd: string;
    userTagCd: string;
    lastLoginTime: string;
    createTime: string;
    updateTime: string;
    delFlag: string;
    birthday: string;
  }

  export interface RoleForm {
    roleIds: number[];
    userId: number;
  }

  export interface RoleData {
    selectIds: number[];
    roleInfoVOS: RoleInfo[];
  }

  export interface RoleInfo {
    id: number;
    roleName: string;
  }

  export interface PasswordForm {
    oldPwd: string;
    newPwd: string;
  }

  export interface Options {
    id: number;
    username: string;
    nickname: string;
  }
}
