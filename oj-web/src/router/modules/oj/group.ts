import type { RouteRecordRaw } from 'vue-router';

// 班级相关路由 - 作为布局路由的子路由
export const groupRouter: RouteRecordRaw = {
  path: '/layout',
  name: 'groupLayout',
  component: () => import('@/layouts/index.vue'),
  meta: {
    title: '班级管理',
  },
  children: [
    {
      path: '/oj/group/group',
      component: () => import('@/views/oj/group/group/index.vue'),
      name: 'Group',
      meta: {
        icon: 'School',
        roles: ['admin', 'teacher'],
        title: '班级列表',
        keepAlive: true
      }
    },
    {
      path: '/oj/group/detail/:id',
      component: () => import('@/views/oj/group/group/detail.vue'),
      name: 'GroupDetail',
      meta: {
        icon: 'InfoFilled',
        roles: ['admin', 'teacher'],
        title: '班级详情',
        keepAlive: false,
        requiresAuth: true
      }
    }
  ]
};
