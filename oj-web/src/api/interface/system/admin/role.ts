import type { IPageQuery } from '@/api/interface';

export namespace IRole {
  export interface Query extends IPageQuery {
    roleName?: string;
  }

  export interface Form {
    id?: number;
    roleName: string;
    remark: string;
  }

  export interface Info {
    id: number;
    roleName: string;
    remark: string;
    delFlag: string;
    createTime: string;
    updateTime: string;
    isLock?: string;
    permissions?: string;
  }

  export interface Menu {
    menuLists: MenuTree[];
    selectIds: string[];
  }

  export interface MenuTree {
    id: string;
    pid: string;
    title: string;
    children: MenuTree[];
  }

  export interface MenuForm {
    menuIds: string[];
    roleId: number;
  }
}
