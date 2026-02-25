<!-- 分栏布局 -->
<template>
  <el-container class="layout">
    <div class="aside-split">
      <div class="logo flx-center">
        <img class="logo-img" :src="logoUrl" alt="logo" />
      </div>
      <el-scrollbar>
        <div class="split-list">
          <div
            v-for="item in menuList"
            :key="item.path"
            class="split-item"
            :class="{
              'split-active': splitActive === item.path || `/${splitActive.split('/')[1]}` === item.path
            }"
            @click="changeSubMenu(item)"
          >
            <el-icon>
              <SvgIcon v-if="item.meta.icon.startsWith('svg-')" :name="item.meta.icon.substring(4)" />
              <component v-else :is="item.meta.icon" />
            </el-icon>
            <span class="title">{{ item.meta.title }}</span>
          </div>
        </div>
      </el-scrollbar>
    </div>
    <el-aside :class="{ 'not-aside': !subMenuList.length }" :style="{ width: isCollapse ? '65px' : '210px' }">
      <div class="logo flx-center">
        <span v-show="subMenuList.length" class="logo-text">{{ isCollapse ? 'G' : tenantName }}</span>
      </div>
      <el-scrollbar>
        <el-menu
          :router="false"
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="accordion"
          :collapse-transition="false"
        >
          <SubMenu :menu-list="subMenuList" />
        </el-menu>
      </el-scrollbar>
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
import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/modules/auth';
import { useTenantStore } from '@/stores/modules/tenant';
import Main from '@/layouts/components/Main/index.vue';
import ToolBarLeft from '@/layouts/components/Header/ToolBarLeft.vue';
import ToolBarRight from '@/layouts/components/Header/ToolBarRight.vue';
import SubMenu from '@/layouts/components/Menu/SubMenu.vue';
import { useAppStore } from '@/stores/modules/app';
import SvgIcon from '@/components/Common/SvgIcon/index.vue';
import logoDefault from '@/assets/images/logo.svg';

defineOptions({
  name: 'LayoutColumns'
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

const accordion = computed(() => appStore.accordion);
const isCollapse = computed(() => appStore.isCollapse);
const menuList = computed(() => authStore.showMenuListGet);
const activeMenu = computed(() => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string);

const subMenuList = ref<Menu.MenuOptions[]>([]);
const splitActive = ref('');
watch(
  () => [menuList, route],
  () => {
    // 当前菜单没有数据直接 return
    if (!menuList.value.length) return;
    splitActive.value = route.path;
    const menuItem = menuList.value.filter(item => {
      return route.path === item.path || `/${route.path.split('/')[1]}` === item.path;
    });
    if (menuItem[0] && menuItem[0].children?.length) {
      subMenuList.value = menuItem[0].children;
    } else {
      subMenuList.value = [];
    }
  },
  {
    deep: true,
    immediate: true
  }
);

// change SubMenu
const changeSubMenu = (item: Menu.MenuOptions) => {
  splitActive.value = item.path;
  if (item.children?.length) {
    subMenuList.value = item.children;
  } else {
    subMenuList.value = [];
  }
  router.push(item.path);
};
</script>

<style scoped lang="scss">
@import './index.scss';
</style>
