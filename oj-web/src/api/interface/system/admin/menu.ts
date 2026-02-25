export namespace IMenu {
  export interface Query {
    isShowButton: boolean;
  }

  export interface Form {
    id?: string;
    title?: string;
    pid?: string;
    path?: string;
    name?: string;
    icon?: string;
    component?: string;
    redirect?: string;
    sort?: number;
    deep?: number;
    menuTypeCd?: string;
    permissions?: string;
    isHidden?: string;
    hasChildren?: string;
    isLink?: string;
    isFull?: string;
    isAffix?: string;
    isKeepAlive?: string;
  }

  export interface Tree {
    id: string;
    pid: string;
    title: string;
    children: Tree[];
  }

  export interface PermissionQuery {
    id: string;
    permissions: string;
  }
}
