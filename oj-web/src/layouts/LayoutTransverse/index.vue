<!-- 横向布局 -->
<template>
  <el-container class="layout">
    <el-header>
      <div class="logo flx-center">
        <img class="logo-img" :src="logoUrl" alt="logo" />
        <span class="logo-text">{{ tenantName }}</span>
      </div>
      <el-menu mode="horizontal" :router="false" :default-active="activeMenu">
        <!-- 不能直接使用 SubMenu 组件，无法触发 el-menu 隐藏省略功能 -->
        <template v-for="subItem in menuList" :key="subItem.path">
          <el-sub-menu v-if="subItem.children?.length" :key="subItem.path" :index="subItem.path + 'el-sub-menu'">
            <template #title>
              <el-icon>
                <SvgIcon v-if="subItem.meta?.icon.startsWith('svg-')" :name="subItem.meta?.icon.substring(4)" />
                <component v-else :is="subItem.meta?.icon" />
              </el-icon>
              <span>{{ subItem.meta.title }}</span>
            </template>
            <SubMenu :menu-list="subItem.children" />
          </el-sub-menu>
          <el-menu-item v-else :key="subItem.path + 'el-menu-item'" :index="subItem.path" @click="handleClickMenu(subItem)">
            <el-icon>
              <SvgIcon v-if="subItem.meta?.icon.startsWith('svg-')" :name="subItem.meta?.icon.substring(4)" />
              <component v-else :is="subItem.meta?.icon" />
            </el-icon>
            <template #title>
              <span>{{ subItem.meta.title }}</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
      <ToolBarRight />
    </el-header>
    <Main />
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useAuthStore } from '@/stores/modules/auth';
import { useTenantStore } from '@/stores/modules/tenant';
import { useRoute, useRouter } from 'vue-router';
import Main from '@/layouts/components/Main/index.vue';
import ToolBarRight from '@/layouts/components/Header/ToolBarRight.vue';
import SubMenu from '@/layouts/components/Menu/SubMenu.vue';
import SvgIcon from '@/components/Common/SvgIcon/index.vue';
import logoDefault from '@/assets/images/logo.svg';

defineOptions({
  name: 'LayoutTransverse'
});

const defaultTitle = import.meta.env.VITE_APP_TITLE;

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
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

const menuList = computed(() => authStore.showMenuListGet);
const activeMenu = computed(() => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string);

const handleClickMenu = (subItem: Menu.MenuOptions) => {
  if (subItem.meta.isLink) return window.open(subItem.meta.isLink, '_blank');
  router.push(subItem.path);
};
</script>

<style scoped lang="scss">
@import './index.scss';
</style>
