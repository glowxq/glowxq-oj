import type { RouteRecordRaw } from 'vue-router';

// 问题相关路由 - 作为布局路由的子路由
export const problemRouter: RouteRecordRaw = {
  path: '/layout',
  name: 'problemLayout',
  component: () => import('@/layouts/index.vue'),
  meta: {
    title: '题目管理',
  },
  children: [
    {
      path: '/oj/problem/problem',
      component: () => import('@/views/oj/problem/problem/index.vue'),
      name: 'Problem',
      meta: {
        icon: 'Question',
        roles: ['admin', 'student'],
        title: '题目列表',
        keepAlive: true
      }
    },
    {
      path: '/oj/problem/submit/:id',
      component: () => import('@/views/oj/problem/submit/index.vue'),
      name: 'ProblemSubmit',
      meta: {
        icon: 'Edit',
        roles: ['admin', 'student'],
        title: '做题页面',
        keepAlive: false,
        requiresAuth: true
      }
    }
  ]
};
