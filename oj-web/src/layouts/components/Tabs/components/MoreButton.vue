<template>
  <el-dropdown trigger="click" :teleported="false">
    <div class="more-button">
      <i :class="'iconfont icon-xiala'" />
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="refresh">
          <el-icon>
            <Refresh />
          </el-icon>
          {{ $t('tabs.refresh') }}
        </el-dropdown-item>
        <el-dropdown-item @click="maximize">
          <el-icon>
            <FullScreen />
          </el-icon>
          {{ $t('tabs.maximize') }}
        </el-dropdown-item>
        <el-dropdown-item divided @click="closeCurrentTab">
          <el-icon>
            <Remove />
          </el-icon>
          {{ $t('tabs.closeCurrent') }}
        </el-dropdown-item>
        <el-dropdown-item @click="tabStore.closeTabsOnSide(route.fullPath, 'left')">
          <el-icon>
            <DArrowLeft />
          </el-icon>
          {{ $t('tabs.closeLeft') }}
        </el-dropdown-item>
        <el-dropdown-item @click="tabStore.closeTabsOnSide(route.fullPath, 'right')">
          <el-icon>
            <DArrowRight />
          </el-icon>
          {{ $t('tabs.closeRight') }}
        </el-dropdown-item>
        <el-dropdown-item divided @click="closeOtherTab">
          <el-icon>
            <CircleClose />
          </el-icon>
          {{ $t('tabs.closeOther') }}
        </el-dropdown-item>
        <el-dropdown-item @click="closeAllTab">
          <el-icon>
            <FolderDelete />
          </el-icon>
          {{ $t('tabs.closeAll') }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { inject, nextTick } from 'vue';
import { HOME_URL } from '@/config';
import { useTabsStore } from '@/stores/modules/tabs';
import { useKeepAliveStore } from '@/stores/modules/keepAlive';
import { useRoute, useRouter } from 'vue-router';
import { useAppStore } from '@/stores/modules/app';
import { CircleClose, DArrowLeft, DArrowRight, FolderDelete, FullScreen, Remove } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const tabStore = useTabsStore();
const appStore = useAppStore();
const keepAliveStore = useKeepAliveStore();

// refresh current page
const refreshCurrentPage: Function = inject('refresh') as Function;
const refresh = () => {
  setTimeout(() => {
    keepAliveStore.removeKeepAliveName(route.name as string);
    refreshCurrentPage(false);
    nextTick(() => {
      keepAliveStore.addKeepAliveName(route.name as string);
      refreshCurrentPage(true);
    });
  }, 0);
};

// maximize current page
const maximize = () => {
  appStore.changeMaximize(true);
};

// Close Current
const closeCurrentTab = () => {
  if (route.meta.isAffix === 'T') return;
  tabStore.removeTabs(route.fullPath);
  keepAliveStore.removeKeepAliveName(route.name as string);
};

// Close Other
const closeOtherTab = () => {
  tabStore.closeMultipleTab(route.fullPath);
};

// Close All
const closeAllTab = () => {
  tabStore.closeMultipleTab();
  router.push(HOME_URL);
};
</script>

<style scoped lang="scss">
@import '../index.scss';
</style>
