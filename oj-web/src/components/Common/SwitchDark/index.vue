<template>
  <el-switch v-model="globalStore.isDark" inline-prompt :active-icon="Sunny" :inactive-icon="Moon" @change="switchDark" />
</template>

<script setup lang="ts">
import { useTheme } from '@/hooks/useTheme';
import { Sunny, Moon } from '@element-plus/icons-vue';
import { useAppStore } from '@/stores/modules/app';

const { switchDark } = useTheme();
const globalStore = useAppStore();
</script>
