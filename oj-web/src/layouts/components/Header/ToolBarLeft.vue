<template>
  <div class="tool-bar-lf">
    <CollapseIcon id="collapseIcon" />
    <Breadcrumb v-if="appStore.breadcrumb" id="breadcrumb" />
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/modules/app';
import CollapseIcon from './components/CollapseIcon.vue';
import Breadcrumb from './components/Breadcrumb.vue';

const appStore = useAppStore();
</script>

<style scoped lang="scss">
.tool-bar-lf {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  white-space: nowrap;
}
</style>
