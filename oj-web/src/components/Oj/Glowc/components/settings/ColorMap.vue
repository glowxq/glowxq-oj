<template>
  <div class="color-map-container">
    <h3>颜色对照表 (pen.color(数字))</h3>
    <div class="color-grid">
      <div 
        v-for="color in colorMap" 
        :key="color.id" 
        class="color-item"
      >
        <div class="color-box" :style="{ backgroundColor: color.value }"></div>
        <div class="color-info">
          <div class="color-code">{{ color.id }}</div>
          <div class="color-name">{{ color.name }}</div>
          <div class="color-value">{{ color.value }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';

// 定义颜色映射项接口
interface ColorMapItem {
  id: number;
  name: string;
  value: string;
}

const props = defineProps({
  // 颜色映射
  colorMap: {
    type: Array as () => ColorMapItem[],
    required: true
  }
});
</script>

<style scoped>
.color-map-container {
  padding: 15px;
}

.color-map-container h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #409EFF;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.color-item {
  display: flex;
  align-items: center;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 8px;
  background-color: #f9f9f9;
}

.color-box {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  margin-right: 10px;
  border: 1px solid #ddd;
}

.color-info {
  flex: 1;
}

.color-code {
  font-weight: bold;
  font-size: 16px;
}

.color-name {
  color: #666;
  font-size: 12px;
}

.color-value {
  color: #999;
  font-size: 12px;
}
</style> 