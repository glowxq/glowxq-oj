<template>
  <div class="problem-progress-bar">
    <div class="progress-title">{{ title || '答题进度:' }}</div>
    <el-progress 
      :percentage="percentage" 
      :format="(percentage: number) => `${percentage}%`"
      :status="status"
    />
    <div class="progress-detail" v-if="showDetail">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

defineOptions({
  name: 'ProgressBar'
});

const props = defineProps<{
  percentage: number;
  status?: string;
  title?: string;
  showDetail?: boolean;
}>();

// 计算进度状态，如果没有提供则根据百分比自动判断
const status = computed(() => {
  if (props.status) return props.status;
  
  if (props.percentage === 100) return 'success';
  if (props.percentage === 0) return 'exception';
  return 'warning';
});
</script>

<style scoped>
.problem-progress-bar {
  margin: 15px 0;
}

.progress-title {
  font-weight: 600;
  margin-bottom: 5px;
  color: var(--el-text-color-regular);
}

.progress-detail {
  margin-top: 10px;
}
</style> 