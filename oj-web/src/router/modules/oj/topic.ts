import type { RouteRecordRaw } from 'vue-router';

// 主题相关路由 - 作为布局路由的子路由
export const topicRouter: RouteRecordRaw = {
  path: '/layout',
  name: 'topicLayout',
  component: () => import('@/layouts/index.vue'),
  meta: {
    title: '主题管理',
  },
  children: [
    {
      path: '/oj/topic/contest',
      component: () => import('@/views/oj/topic/topic/index.vue'),
      name: 'TopicContest',
      meta: {
        icon: 'EditPen',
        title: '比赛',
        keepAlive: false,
        requiresAuth: true
      }
    },
    {
      path: '/oj/topic/other',
      component: () => import('@/views/oj/topic/topic/index.vue'),
      name: 'TopicOther',
      meta: {
        icon: 'EditPen',
        title: '作业/练习',
        keepAlive: false,
        requiresAuth: true
      }
    },
    {
      path: '/oj/topic/doTopic/:id',
      component: () => import('@/views/oj/topic/doTopic/index.vue'),
      name: 'DoTopic',
      meta: {
        icon: 'EditPen',
        title: '进入主题',
        keepAlive: false,
        requiresAuth: true
      }
    },
    {
      path: '/oj/topic/detail/:id',
      component: () => import('@/views/oj/topic/detail/index.vue'),
      name: 'TopicDetail',
      meta: {
        icon: 'InfoFilled',
        title: '主题详情',
        keepAlive: false,
        requiresAuth: true
      }
    }
  ]
};
