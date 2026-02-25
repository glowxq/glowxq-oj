<template>
  <div class="tabs-box">
    <div class="tabs-menu">
      <el-tabs v-model="tabsMenuValue" type="card" @tab-click="tabClick" @tab-remove="tabRemove">
        <el-tab-pane v-for="item in tabsMenuList" :key="item.path" :label="item.title" :name="item.path" :closable="item.close">
          <template #label>
            <el-icon v-show="item.icon && tabsIcon" class="tabs-icon">
              <SvgIcon v-if="item.icon.startsWith('svg-')" :name="item.icon.substring(4)" />
              <component v-else :is="item.icon" />
            </el-icon>
            {{ item.title }}
          </template>
        </el-tab-pane>
      </el-tabs>
      <MoreButton />
    </div>
  </div>
</template>

<script setup lang="ts">
import Sortable from 'sortablejs';
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTabsStore } from '@/stores/modules/tabs';
import { useAuthStore } from '@/stores/modules/auth';
import { useKeepAliveStore } from '@/stores/modules/keepAlive';
import MoreButton from './components/MoreButton.vue';
import { useAppStore } from '@/stores/modules/app';
import type { TabsMenuProps } from '@/stores/interface/tabs';
import type { TabPaneName, TabsPaneContext } from 'element-plus';
import SvgIcon from '@/components/Common/SvgIcon/index.vue';
defineOptions({
  name: 'Tabs'
});

const route = useRoute();
const router = useRouter();
const tabStore = useTabsStore();
const authStore = useAuthStore();
const appStore = useAppStore();
const keepAliveStore = useKeepAliveStore();

const tabsMenuValue = ref(route.fullPath);
const tabsMenuList = computed(() => tabStore.tabsMenuList);
const tabsIcon = computed(() => appStore.tabsIcon);

onMounted(() => {
  tabsDrop();
  initTabs();
});

// 监听路由的变化（防止浏览器后退/前进不变化 tabsMenuValue）
watch(
  () => route.fullPath,
  () => {
    if (route.meta.isFull === 'T') return;
    tabsMenuValue.value = route.fullPath;
    const tabsParams: TabsMenuProps = {
      icon: route.meta.icon as string,
      title: route.meta.title as string,
      path: route.fullPath,
      name: route.name as string,
      close: route.meta.isAffix !== 'T',
      isKeepAlive: route.meta.isKeepAlive === 'T'
    };
    tabStore.addTabs(tabsParams);
  },
  { immediate: true }
);

// tabs 拖拽排序
const tabsDrop = () => {
  Sortable.create(document.querySelector('.el-tabs__nav') as HTMLElement, {
    draggable: '.el-tabs__item',
    animation: 300,
    onEnd({ newIndex, oldIndex }) {
      const tabsList = [...tabStore.tabsMenuList];
      const currRow = tabsList.splice(oldIndex as number, 1)[0];
      tabsList.splice(newIndex as number, 0, currRow);
      tabStore.setTabs(tabsList);
    }
  });
};

// 初始化需要固定的 tabs
const initTabs = () => {
  authStore.flatMenuListGet.forEach(item => {
    if (item.meta.isAffix === 'T' && item.meta.isHidden === 'F' && item.meta.isFull === 'F') {
      const tabsParams = {
        icon: item.meta.icon,
        title: item.meta.title,
        path: item.path,
        name: item.name,
        close: item.meta.isAffix !== 'T',
        isKeepAlive: item.meta.isKeepAlive === 'T'
      };
      tabStore.addTabs(tabsParams);
    }
  });
};

// Tab Click
const tabClick = (tabItem: TabsPaneContext) => {
  const fullPath = tabItem.props.name as string;
  router.push(fullPath);
};

// Remove Tab
const tabRemove = (fullPath: TabPaneName) => {
  const name = tabStore.tabsMenuList.filter(item => item.path === fullPath)[0].name || '';
  keepAliveStore.removeKeepAliveName(name);
  tabStore.removeTabs(fullPath as string, fullPath === route.fullPath);
};
</script>

<style scoped lang="scss">
@import './index.scss';
</style>
