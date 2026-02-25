<template>
  <el-dropdown trigger="click" @command="setAssemblySize">
    <i :class="'iconfont icon-contentright'" class="toolBar-icon" />
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in assemblySizeList"
          :key="item.value"
          :command="item.value"
          :disabled="assemblySize === item.value"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useAppStore } from '@/stores/modules/app';
import type { AssemblySizeType } from '@/stores/interface/app';

const appStore = useAppStore();
const assemblySize = computed(() => appStore.assemblySize);

const assemblySizeList = [
  { label: '默认', value: 'default' },
  { label: '大型', value: 'large' },
  { label: '小型', value: 'small' }
];

const setAssemblySize = (item: string) => {
  if (assemblySize.value === item) return;
  appStore.changeAssemblySize(item as AssemblySizeType);
};
</script>
