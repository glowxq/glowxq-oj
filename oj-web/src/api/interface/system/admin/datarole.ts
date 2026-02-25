import type { IPageQuery } from '@/api/interface';

export namespace ISysDataRole {
  // 查询条件
  export interface Query extends IPageQuery {
    roleName?: string;
    isLock?: string;
  }

  // 编辑form表单
  export interface Form {
    id?: number;
    roleName?: string;
    remark?: string;
    isLock?: string;
  }

  // list或detail返回结构
  export interface Row {
    id?: number;
    roleName?: string;
    remark?: string;
    isLock?: string;
  }

  // 数据角色Form初始化数据：菜单树、部门树、用户列等
  export interface Meta {
    menuLists: MenuTree[];
  }

  export interface MenuTree {
    id: string;
    pid: string;
    title: string;
    children: MenuTree[];
  }
}
