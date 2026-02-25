<!-- 时间范围展示组件 -->
<template>
  <div class="time-range">
    <div class="time-item start-time">
      <el-icon><Timer /></el-icon>
      <span>{{ startTime || '未设置' }}</span>
    </div>
    <div class="time-separator" v-if="startTime && endTime">→</div>
    <div class="time-item end-time">
      <el-icon><Timer /></el-icon>
      <span>{{ endTime || '未设置' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Timer } from '@element-plus/icons-vue';

defineProps({
  startTime: {
    type: String,
    default: ''
  },
  endTime: {
    type: String,
    default: ''
  }
});
</script>

<style scoped lang="scss">
.time-range {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  font-size: 14px;
  
  .time-item {
    padding: 8px 12px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    background-color: rgba(180, 180, 185, 0.12);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: none;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
    transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    min-width: 130px;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08);
    }
    
    .el-icon {
      margin-right: 8px;
      font-size: 16px;
      transition: all 0.3s ease;
    }
    
    span {
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', 'Helvetica Neue', sans-serif;
      font-weight: 500;
      letter-spacing: 0.3px;
    }
  }
  
  .start-time {
    color: #0A84FF;
    border-left: 3px solid #0A84FF;
    
    .el-icon {
      color: #0A84FF;
    }
  }
  
  .end-time {
    color: #30D158;
    border-left: 3px solid #30D158;
    
    .el-icon {
      color: #30D158;
    }
  }
  
  .time-separator {
    margin: 0 12px;
    color: #8E8E93;
    font-size: 16px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      width: 30px;
      height: 2px;
      background: linear-gradient(90deg, #0A84FF 0%, #30D158 100%);
      border-radius: 1px;
      bottom: -10px;
      opacity: 0.7;
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .time-range {
    .time-item {
      background-color: rgba(40, 40, 45, 0.75);
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
      
      &:hover {
        box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15);
      }
    }
    
    .start-time {
      color: #0A84FF;
      border-left: 3px solid #0A84FF;
      
      .el-icon {
        color: #0A84FF;
      }
    }
    
    .end-time {
      color: #30D158;
      border-left: 3px solid #30D158;
      
      .el-icon {
        color: #30D158;
      }
    }
    
    .time-separator {
      color: #98989D;
      
      &::after {
        opacity: 0.8;
      }
    }
  }
}
</style> 