<template>
  <el-icon class="collapse-icon" @click="changeCollapse">
    <component :is="appStore.isCollapse ? 'expand' : 'fold'" />
  </el-icon>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/modules/app';

const appStore = useAppStore();
const changeCollapse = () => appStore.changeIsCollapse(!appStore.isCollapse);
</script>

<style scoped lang="scss">
.collapse-icon {
  margin-right: 20px;
  font-size: 22px;
  color: var(--el-header-text-color);
  cursor: pointer;
}
</style>
