import type { IPageQuery } from '@/api/interface';
import type { IMetaTag } from '@/api/interface/system/meta/metaTag';
import type { ICourse } from '@/api/interface/oj/course/course';
import type { ITopic } from '@/api/interface/oj/topic/topic';
import type { IProblem } from '@/api/interface/oj/problem/problem';
import type { IUser } from '@/api/interface/system/admin/user';

export namespace IGroup {
  // 查询条件
  export interface Query extends IPageQuery {
    name?: string;
    code?: string;
    principalUserId?: string;
    principalName?: string;
    description?: string;
    color?: string;
    textColor?: string;
    enable?: string;
  }

  // 编辑form表单
  export interface Form {
    id?: number;
    name?: string;
    code?: string;
    principalUserId?: string;
    principalName?: string;
    description?: string;
    color?: string;
    textColor?: string;
    enable?: string;
  }

  // list或detail返回结构
  export interface Row {
    id?: number;
    name?: string;
    code?: string;
    principalUserId?: string;
    principalName?: string;
    description?: string;
    color?: string;
    textColor?: string;
    enable?: string;
    // 班级详情页面新增字段
    tagList?: IMetaTag.Row[];
    courseList?: ICourse.Row[];
    topicList?: ITopic.Row[];
    problemList?: IProblem.Row[];
    userList?: IUser.Info[];
  }
}
