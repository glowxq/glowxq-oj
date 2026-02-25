<!-- 时间状态展示组件 -->
<template>
  <div class="time-status">
    <div :class="['status-indicator', `status-${status.type}`]">
      <span class="status-dot"></span>
      <span class="status-text">{{ status.text }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps } from 'vue';

const props = defineProps({
  startTime: {
    type: String,
    default: ''
  },
  endTime: {
    type: String,
    default: ''
  },
  tagEffect: {
    type: String,
    default: 'light'
  }
});

// 计算当前状态
const status = computed(() => {
  const now = new Date().getTime();
  
  // 如果没有设置开始时间或结束时间
  if (!props.startTime && !props.endTime) {
    return { type: 'info', text: '未设置' };
  }
  
  // 如果设置了开始时间但没设置结束时间
  if (props.startTime && !props.endTime) {
    const startTime = new Date(props.startTime).getTime();
    return now < startTime 
      ? { type: 'info', text: '未开始' } 
      : { type: 'success', text: '进行中' };
  }
  
  // 如果设置了结束时间但没设置开始时间
  if (!props.startTime && props.endTime) {
    const endTime = new Date(props.endTime).getTime();
    return now > endTime 
      ? { type: 'danger', text: '已结束' } 
      : { type: 'success', text: '进行中' };
  }
  
  // 如果开始时间和结束时间都设置了
  if (props.startTime && props.endTime) {
    const startTime = new Date(props.startTime).getTime();
    const endTime = new Date(props.endTime).getTime();
    
    if (now < startTime) return { type: 'info', text: '未开始' };
    if (now > endTime) return { type: 'danger', text: '已结束' };
    return { type: 'success', text: '进行中' };
  }
  
  // 默认返回
  return { type: 'info', text: '未设置' };
});
</script>

<style scoped lang="scss">
.time-status {
  display: inline-flex;
  
  .status-indicator {
    display: inline-flex;
    align-items: center;
    font-size: 13px;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', 'Helvetica Neue', sans-serif;
    font-weight: 500;
    padding: 5px 10px;
    border-radius: 15px;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    transition: all 0.3s ease;
    
    .status-dot {
      display: inline-block;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      margin-right: 6px;
      transition: all 0.3s ease;
    }
    
    .status-text {
      letter-spacing: 0.3px;
    }
    
    &.status-info {
      color: #8E8E93;
      background-color: rgba(142, 142, 147, 0.12);
      
      .status-dot {
        background-color: #8E8E93;
        box-shadow: 0 0 0 2px rgba(142, 142, 147, 0.2);
      }
    }
    
    &.status-success {
      color: #34C759;
      background-color: rgba(52, 199, 89, 0.12);
      
      .status-dot {
        background-color: #34C759;
        box-shadow: 0 0 0 2px rgba(52, 199, 89, 0.2);
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          width: 16px;
          height: 16px;
          background-color: rgba(52, 199, 89, 0.2);
          border-radius: 50%;
          top: -4px;
          left: -4px;
          animation: pulse 2s infinite cubic-bezier(0.25, 0.46, 0.45, 0.94);
        }
      }
    }
    
    &.status-danger {
      color: #FF3B30;
      background-color: rgba(255, 59, 48, 0.12);
      
      .status-dot {
        background-color: #FF3B30;
        box-shadow: 0 0 0 2px rgba(255, 59, 48, 0.2);
      }
    }
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.85);
    opacity: 0.8;
  }
  70% {
    transform: scale(1.2);
    opacity: 0.2;
  }
  100% {
    transform: scale(0.85);
    opacity: 0.8;
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .time-status {
    .status-indicator {
      &.status-info {
        color: #98989D;
        background-color: rgba(152, 152, 157, 0.15);
        
        .status-dot {
          background-color: #98989D;
          box-shadow: 0 0 0 2px rgba(152, 152, 157, 0.25);
        }
      }
      
      &.status-success {
        color: #30D158;
        background-color: rgba(48, 209, 88, 0.15);
        
        .status-dot {
          background-color: #30D158;
          box-shadow: 0 0 0 2px rgba(48, 209, 88, 0.25);
          
          &::after {
            background-color: rgba(48, 209, 88, 0.25);
          }
        }
      }
      
      &.status-danger {
        color: #FF453A;
        background-color: rgba(255, 69, 58, 0.15);
        
        .status-dot {
          background-color: #FF453A;
          box-shadow: 0 0 0 2px rgba(255, 69, 58, 0.25);
        }
      }
    }
  }
}
</style>
