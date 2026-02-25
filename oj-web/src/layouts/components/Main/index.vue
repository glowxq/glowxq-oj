<template>
  <Maximize v-if="maximize" />
  <Tabs v-if="tabs" />
  <el-main>
    <router-view v-slot="{ Component, route }">
      <transition appear name="fade-transform" mode="out-in">
        <keep-alive :include="keepAliveName">
          <component :is="Component" v-if="isRouterShow" :key="route.fullPath" />
        </keep-alive>
      </transition>
    </router-view>
  </el-main>
  <el-footer v-if="footer">
    <Footer />
  </el-footer>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, provide, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useDebounceFn } from '@vueuse/core';
import { useKeepAliveStore } from '@/stores/modules/keepAlive';
import { useAppStore } from '@/stores/modules/app';
import Maximize from './components/Maximize.vue';
import Tabs from '@/layouts/components/Tabs/index.vue';
import Footer from '@/layouts/components/Footer/index.vue';

const appStore = useAppStore();
const { maximize, isCollapse, layout, tabs, footer } = storeToRefs(appStore);

const keepAliveStore = useKeepAliveStore();
const { keepAliveName } = storeToRefs(keepAliveStore);

// 注入刷新页面方法
const isRouterShow = ref(true);
const refreshCurrentPage = (val: boolean) => (isRouterShow.value = val);
provide('refresh', refreshCurrentPage);

// 监听当前页面是否最大化，动态添加 class
watch(
  () => maximize.value,
  () => {
    const app = document.getElementById('app') as HTMLElement;
    if (maximize.value) app.classList.add('main-maximize');
    else app.classList.remove('main-maximize');
  },
  { immediate: true }
);

// 监听布局变化，在 body 上添加相对应的 layout class
watch(
  () => layout.value,
  () => {
    const body = document.body;
    body.setAttribute('class', layout.value);
  },
  { immediate: true }
);

// 监听窗口大小变化，折叠侧边栏
const screenWidth = ref(0);
const listeningWindow = useDebounceFn(() => {
  screenWidth.value = document.body.clientWidth;
  if (!isCollapse.value && screenWidth.value < 1200) appStore.changeIsCollapse(true);
  if (isCollapse.value && screenWidth.value > 1200) appStore.changeIsCollapse(false);
}, 100);
window.addEventListener('resize', listeningWindow, false);
onBeforeUnmount(() => {
  window.removeEventListener('resize', listeningWindow);
});
</script>

<style scoped lang="scss">
@import './index.scss';
</style>
