<template>
  <div class="user-title" v-if="title || color" :style="titleStyle">
    <span class="title-text">{{ title || '无称号' }}</span>
    <div v-if="color" class="color-indicator" :style="{ backgroundColor: color }"></div>
  </div>
  <span v-else class="no-title">-</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  title?: string
  color?: string
  size?: 'small' | 'default' | 'large'
  showColorIndicator?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  showColorIndicator: true
})

// 根据尺寸计算样式
const titleStyle = computed(() => {
  const baseStyle: Record<string, string> = {
    color: props.color || '#606266',
    borderColor: props.color || '#dcdfe6'
  }

  // 根据尺寸调整样式
  switch (props.size) {
    case 'small':
      baseStyle.fontSize = '12px'
      baseStyle.padding = '2px 6px'
      break
    case 'large':
      baseStyle.fontSize = '16px'
      baseStyle.padding = '6px 12px'
      break
    default:
      baseStyle.fontSize = '14px'
      baseStyle.padding = '4px 8px'
      break
  }

  return baseStyle
})
</script>

<style scoped lang="scss">
.user-title {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  font-weight: 500;
  line-height: 1.2;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), transparent);
    pointer-events: none;
  }

  .title-text {
    position: relative;
    z-index: 1;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 100px;
  }

  .color-indicator {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    flex-shrink: 0;
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
    position: relative;
    z-index: 1;
  }

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

.no-title {
  color: #c0c4cc;
  font-size: 12px;
}

// 响应式设计
@media (max-width: 768px) {
  .user-title {
    .title-text {
      max-width: 60px;
    }
    
    .color-indicator {
      width: 10px;
      height: 10px;
    }
  }
}
</style>
