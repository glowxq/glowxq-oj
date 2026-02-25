// OJ用户模块接口定义
import type { IPageQuery } from '@/api/interface';

export namespace IOJUser {
  // 查询参数
  export interface Query extends IPageQuery {
    username?: string;
    nickname?: string;
    name?: string;
    phone?: string;
    email?: string;
    sex?: number;
    accountStatusCd?: string;
    startDate?: string;
    endDate?: string;
    groupIds?: number[];
    tagIds?: number[];
    minAcNum?: number;
    maxAcNum?: number;
    minIntegral?: number;
    maxIntegral?: number;
    title?: string;
    userQueryType?: string;
  }

  // OJ用户表单数据
  export interface Form {
    id?: number;
    username?: string;
    nickname?: string;
    name?: string;
    phone?: string;
    email?: string;
    sex?: number;
    birthday?: string;
    idCard?: string;
    logo?: string;
    // OJ特有字段
    acNum?: number;
    integral?: number;
    continuousSignDay?: number;
    submitNum?: number;
    title?: string;
    color?: string;
    // 关联数据
    tagIds?: number[];
    groupIds?: number[];
  }

  // OJ用户信息
  export interface Info {
    id: number;
    username: string;
    nickname: string;
    name: string;
    phone?: string;
    email?: string;
    sex?: number;
    birthday?: string;
    idCard?: string;
    logo?: string;
    accountStatusCd?: string;
    // OJ特有字段
    acNum: number;
    integral: number;
    continuousSignDay: number;
    submitNum: number;
    title?: string;
    color?: string;
    // 时间字段
    createTime: string;
    updateTime: string;
    lastLoginTime?: string;
    // 关联数据
    tagIds?: number[];
    groupIds?: number[];
    tagList?: TagInfo[];
    groupList?: GroupInfo[];
    // 统计数据
    ranking?: number;
    solvedProblems?: number;
    attemptedProblems?: number;
  }

  // 标签信息
  export interface TagInfo {
    id: number;
    name: string;
    color?: string;
    textColor?: string;
  }

  // 班级信息
  export interface GroupInfo {
    id: number;
    name: string;
    code?: string;
    color?: string;
    textColor?: string;
    principalName?: string;
  }

  // 用户绑定信息
  export interface UserBindInfo {
    tags: TagInfo[];
    groups: GroupInfo[];
    ojUserInfo?: Info;
  }

  // 用户统计信息
  export interface UserStats {
    totalUsers: number;
    activeUsers: number;
    todaySignIn: number;
    avgAcNum: number;
    avgIntegral: number;
    topPerformers: Info[];
  }

  // 用户选项（用于选择器）
  export interface Options {
    id: number;
    username: string;
    nickname: string;
    name: string;
    logo?: string;
  }

  // 密码修改表单
  export interface PasswordForm {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
  }

  // 批量操作参数
  export interface BatchOperation {
    userIds: number[];
    operation: 'delete' | 'enable' | 'disable' | 'resetPassword';
    data?: any;
  }

  // 导入用户数据
  export interface ImportData {
    username: string;
    nickname?: string;
    name: string;
    phone?: string;
    email?: string;
    sex?: number;
    birthday?: string;
    groupCodes?: string[];
    tagNames?: string[];
  }

  // 导出用户数据
  export interface ExportData extends Info {
    groupNames?: string;
    tagNames?: string;
  }
}
