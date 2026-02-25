<!-- 💥 这里是一次性加载 LayoutComponents -->
<template>
  <component :is="LayoutComponents[layout]" />
  <ThemeDrawer />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import ThemeDrawer from '@/layouts/components/ThemeDrawer/index.vue';
import LayoutVertical from '@/layouts/LayoutVertical/index.vue';
import LayoutClassic from '@/layouts/LayoutClassic/index.vue';
import LayoutTransverse from '@/layouts/LayoutTransverse/index.vue';
import LayoutColumns from '@/layouts/LayoutColumns/index.vue';
import { useAppStore } from '@/stores/modules/app';
import { useSocketStore } from '@/stores/modules/socket';

defineOptions({
  name: 'Layout'
});

const LayoutComponents = {
  vertical: LayoutVertical,
  classic: LayoutClassic,
  transverse: LayoutTransverse,
  columns: LayoutColumns
};

const appStore = useAppStore();
const layout = computed(() => appStore.layout);

// 开启socket
const socketStore = useSocketStore();
socketStore.open();
</script>

<style scoped lang="scss">
.layout {
  min-width: 600px;
}
</style>
