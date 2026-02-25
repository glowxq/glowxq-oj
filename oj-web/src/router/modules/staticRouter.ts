import { HOME_URL, LOGIN_URL, REGISTER_URL } from '@/config';
import type { RouteRecordRaw } from 'vue-router';

// 硬编码所有可能的首页组件路径
const homeViewMap: Record<string, () => Promise<any>> = {
  '/home/index': () => import('@/views/home/index.vue'),
  '/home/oj/index': () => import('@/views/home/oj/index.vue'),
};

// 使用一个函数来确定要使用的组件
function getHomeComponent() {
  // 如果homeViewMap中有对应的组件，则使用它
  if (HOME_URL && homeViewMap[HOME_URL]) {
    return homeViewMap[HOME_URL];
  }
  // 回退到默认组件
  return () => import('@/views/home/index.vue');
}

// 使用函数获取组件
const HomeView = getHomeComponent();

export const staticRouter: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: HOME_URL
  },
  {
    path: LOGIN_URL,
    name: 'login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: REGISTER_URL,
    name: 'register',
    component: () => import('@/views/register/index.vue'),
    meta: {
      title: '注册'
    }
  },
  {
    path: '/tenant/:tenantKey',
    name: 'tenantSwitch',
    component: () => import('@/views/tenant/TenantRedirect.vue'),
    meta: {
      title: '租户切换'
    }
  },
  {
    path: '/layout',
    name: 'layout',
    component: () => import('@/layouts/index.vue'),
    redirect: HOME_URL,
    children: [
      {
        path: HOME_URL,
        name: 'home',
        // component: () => import(`@/views${HOME_URL}.vue`),
        component:HomeView,
        meta: {
          title: '首页',
          icon: 'HomeFilled',
          isAffix: 'T',
          isFull: 'F',
          isHidden: 'T',
          isKeepAlive: 'T',
          isLink: ''
        }
      }
    ]
  }
];

/**
 * errorRouter (错误页面路由)
 */
export const errorRouter: RouteRecordRaw[] = [
  {
    path: '/403',
    name: '403',
    component: () => import('@/components/Common/ErrorMessage/403.vue'),
    meta: {
      title: '403页面'
    }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/components/Common/ErrorMessage/404.vue'),
    meta: {
      title: '404页面'
    }
  },
  {
    path: '/500',
    name: '500',
    component: () => import('@/components/Common/ErrorMessage/500.vue'),
    meta: {
      title: '500页面'
    }
  },
  // Resolve refresh page, route warnings
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/components/Common/ErrorMessage/404.vue')
  }
];
