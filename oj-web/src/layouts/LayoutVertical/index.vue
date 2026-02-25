<!-- 纵向布局 -->
<template>
  <el-container class="layout">
    <el-aside>
      <div class="aside-box" :style="{ width: isCollapse ? '65px' : '210px' }">
        <div class="logo flx-center">
          <img class="logo-img" :src="logoUrl" alt="logo" />
          <span v-show="!isCollapse" class="logo-text">{{ tenantName }}</span>
        </div>
        <el-scrollbar>
          <el-menu
            :router="false"
            :default-active="activeMenu"
            :collapse="isCollapse"
            :unique-opened="accordion"
            :collapse-transition="false"
          >
            <el-menu-item v-if="homeRoute" :index="homeRoute.path" @click="handleClickHome">
              <el-icon>
                <component :is="homeRoute.meta?.icon" />
              </el-icon>
              <template #title>
                <span class="sle">{{ homeRoute.meta?.title || '' }}</span>
              </template>
            </el-menu-item>
            <SubMenu :menu-list="menuList" />
          </el-menu>
        </el-scrollbar>
      </div>
    </el-aside>
    <el-container>
      <el-header>
        <ToolBarLeft />
        <ToolBarRight />
      </el-header>
      <Main />
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/modules/auth';
import { useTenantStore } from '@/stores/modules/tenant';
import Main from '@/layouts/components/Main/index.vue';
import ToolBarLeft from '@/layouts/components/Header/ToolBarLeft.vue';
import ToolBarRight from '@/layouts/components/Header/ToolBarRight.vue';
import SubMenu from '@/layouts/components/Menu/SubMenu.vue';
import { useAppStore } from '@/stores/modules/app';
import logoDefault from '@/assets/images/logo.svg';

defineOptions({
  name: 'LayoutVertical'
});

const defaultTitle = import.meta.env.VITE_APP_TITLE;

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const appStore = useAppStore();
const tenantStore = useTenantStore();

// 获取租户名称和Logo
const tenantName = computed(() => {
  return tenantStore.tenantName || defaultTitle;
});

const logoUrl = computed(() => {
  // 只有当租户明确设置了有效的logo URL时才使用它
  if (tenantStore.logoUrl && tenantStore.logoUrl.trim() !== '') {
    // 验证URL格式是否有效
    try {
      new URL(tenantStore.logoUrl);
      return tenantStore.logoUrl;
    } catch (e) {
      // 如果URL格式无效，使用默认logo
      console.warn('租户Logo URL格式无效:', tenantStore.logoUrl);
      return logoDefault;
    }
  }
  // 否则使用默认logo
  return logoDefault;
});

const homeRoute = computed(() => {
  const routes = router.getRoutes();
  return routes.find(item => item.name === 'home');
});
const handleClickHome = () => {
  if (!homeRoute.value) return;
  if (homeRoute.value.meta.isLink) return window.open(homeRoute.value.meta.isLink as string, '_blank');
  router.push(homeRoute.value.path);
};
const accordion = computed(() => appStore.accordion);
const isCollapse = computed(() => appStore.isCollapse);
const menuList = computed(() => authStore.showMenuListGet);
const activeMenu = computed(() => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string);
</script>

<style scoped lang="scss">
@import './index.scss';
</style>
