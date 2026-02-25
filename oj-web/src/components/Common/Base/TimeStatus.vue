<template>
  <div v-if="targetTime" class="time-status" :class="statusClass">
    <div class="status-indicator">
      <el-icon v-if="showIcon" class="status-icon" :size="iconSize">
        <component :is="currentIcon" />
      </el-icon>
      
      <div class="time-content">
        <div class="time-main">
          <span class="time-value">{{ formatTime(targetTime) }}</span>
          <span v-if="showTimeDistance" class="time-distance">
            {{ timeDistanceText }}
          </span>
        </div>
        
        <div v-if="showOverdueTime && isOverdue" class="overdue-text">
          已超期 {{ Math.abs(daysDiff) }} 天
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type Component } from 'vue'
import { 
  Clock, 
  Warning, 
  SuccessFilled, 
  InfoFilled,
  CircleClose
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'

interface Props {
  /** 目标时间 */
  targetTime: string | Date | null | undefined
  /** 时间类型：warning表示时间越近越坏，info表示时间越近越好 */
  type?: 'warning' | 'info'
  /** 是否显示图标 */
  showIcon?: boolean
  /** 是否显示距今时间 */
  showTimeDistance?: boolean
  /** 是否显示超期时间 */
  showOverdueTime?: boolean
  /** 自定义图标 */
  icon?: Component
  /** 告警阈值，单位：天 [二级, 三级, 四级] */
  thresholds?: [number, number, number]
  /** 图标大小 */
  iconSize?: number
}

const props = withDefaults(defineProps<Props>(), {
  type: 'warning',
  showIcon: false,
  showTimeDistance: true,
  showOverdueTime: true,
  thresholds: () => [7, 60, 365],
  iconSize: 16
})

// 计算距离当前时间的天数
const daysDiff = computed(() => {
  if (!props.targetTime) return 0
  return dayjs(props.targetTime).diff(dayjs(), 'day')
})

// 是否已过期
const isOverdue = computed(() => daysDiff.value < 0)

// 获取告警级别 (1-5，1最严重)
const alertLevel = computed(() => {
  const days = Math.abs(daysDiff.value)
  const [threshold1, threshold2, threshold3] = props.thresholds
  
  if (props.type === 'warning') {
    // 对于警告类型，时间越近越危险
    if (isOverdue.value) return 1 // 已过期
    if (days <= threshold1) return 2 // 7天内
    if (days <= threshold2) return 3 // 60天内
    if (days <= threshold3) return 4 // 365天内
    return 5 // 365天以上
  } else {
    // 对于信息类型，时间越近越好
    if (days <= threshold1) return 5 // 7天内，最好
    if (days <= threshold2) return 4 // 60天内
    if (days <= threshold3) return 3 // 365天内
    if (days <= threshold3 * 2) return 2 // 2年内
    return 1 // 2年以上，最差
  }
})

// 状态样式类名
const statusClass = computed(() => {
  const level = alertLevel.value
  return `status-level-${level} status-type-${props.type}`
})

// 当前图标
const currentIcon = computed(() => {
  if (props.icon) return props.icon
  
  const level = alertLevel.value
  if (props.type === 'warning') {
    switch (level) {
      case 1: return CircleClose
      case 2: return Warning
      case 3: return Warning
      case 4: return InfoFilled
      case 5: return SuccessFilled
      default: return Clock
    }
  } else {
    switch (level) {
      case 1: return Warning
      case 2: return InfoFilled
      case 3: return InfoFilled
      case 4: return SuccessFilled
      case 5: return SuccessFilled
      default: return Clock
    }
  }
})

// 时间距离文本
const timeDistanceText = computed(() => {
  const days = daysDiff.value
  
  if (days === 0) return '今天'
  if (days === 1) return '明天'
  if (days === -1) return '昨天'
  if (days > 0) return `${days}天后`
  return `${Math.abs(days)}天前`
})

// 格式化时间显示
const formatTime = (time: string | Date | null | undefined) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD')
}
</script>

<style scoped lang="scss">
.time-status {
  display: inline-flex;
  align-items: center;
  
  .status-indicator {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px;
    border-radius: 8px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: 1px solid transparent;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      border-radius: inherit;
      padding: 1px;
      background: linear-gradient(135deg, transparent 0%, rgba(255,255,255,0.1) 50%, transparent 100%);
      mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      mask-composite: exclude;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
    
    &:hover::before {
      opacity: 1;
    }
  }
  
  .status-icon {
    flex-shrink: 0;
    transition: transform 0.2s ease;
  }
  
  .time-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
  }
  
  .time-main {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
  }
  
  .time-value {
    font-weight: 600;
    font-size: 14px;
    line-height: 1.2;
  }
  
  .time-distance {
    font-size: 12px;
    font-weight: 500;
    opacity: 0.8;
    padding: 2px 6px;
    border-radius: 4px;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(4px);
  }
  
  .overdue-text {
    font-size: 11px;
    font-weight: 600;
    opacity: 0.9;
    animation: pulse 2s infinite;
  }
  
  // 一级告警（最严重）
  &.status-level-1 {
    .status-indicator {
      background: linear-gradient(135deg, #ff4757 0%, #ff3838 100%);
      color: white;
      box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3);
      border-color: rgba(255, 255, 255, 0.2);
    }
    
    .status-icon {
      animation: shake 0.5s ease-in-out infinite alternate;
    }
  }
  
  // 二级告警
  &.status-level-2 {
    .status-indicator {
      background: linear-gradient(135deg, #ff6b35 0%, #ff5722 100%);
      color: white;
      box-shadow: 0 3px 10px rgba(255, 107, 53, 0.25);
      border-color: rgba(255, 255, 255, 0.15);
    }
  }
  
  // 三级告警
  &.status-level-3 {
    .status-indicator {
      background: linear-gradient(135deg, #ffa726 0%, #ff9800 100%);
      color: white;
      box-shadow: 0 2px 8px rgba(255, 167, 38, 0.2);
    }
  }
  
  // 四级告警
  &.status-level-4 {
    .status-indicator {
      background: linear-gradient(135deg, #ffeb3b 0%, #ffc107 100%);
      color: #333;
      box-shadow: 0 2px 6px rgba(255, 235, 59, 0.15);
    }
  }
  
  // 五级告警（最轻）
  &.status-level-5 {
    .status-indicator {
      background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
      color: white;
      box-shadow: 0 2px 6px rgba(76, 175, 80, 0.15);
    }
  }
  
  // 信息类型的特殊处理
  &.status-type-info {
    &.status-level-1 {
      .status-indicator {
        background: linear-gradient(135deg, #9e9e9e 0%, #757575 100%);
        box-shadow: 0 2px 6px rgba(158, 158, 158, 0.15);
      }
    }
  }
  
  // 悬停效果
  &:hover {
    .status-indicator {
      transform: translateY(-1px);
      
      &.status-level-1 {
        box-shadow: 0 6px 16px rgba(255, 71, 87, 0.4);
      }
      &.status-level-2 {
        box-shadow: 0 5px 14px rgba(255, 107, 53, 0.35);
      }
      &.status-level-3 {
        box-shadow: 0 4px 12px rgba(255, 167, 38, 0.3);
      }
      &.status-level-4 {
        box-shadow: 0 4px 10px rgba(255, 235, 59, 0.25);
      }
      &.status-level-5 {
        box-shadow: 0 4px 10px rgba(76, 175, 80, 0.25);
      }
    }
    
    .status-icon {
      transform: scale(1.1);
    }
  }
}

@keyframes shake {
  0% { transform: translateX(0); }
  100% { transform: translateX(2px); }
}

@keyframes pulse {
  0%, 100% { opacity: 0.9; }
  50% { opacity: 0.6; }
}

// 暗色主题适配
:deep(.dark) {
  .time-status {
    .status-indicator {
      &::before {
        background: linear-gradient(135deg, transparent 0%, rgba(255,255,255,0.05) 50%, transparent 100%);
      }
    }
    
    .time-distance {
      background: rgba(255, 255, 255, 0.08);
    }
    
    &.status-level-4 {
      .status-indicator {
        color: #1a1a1a;
      }
    }
  }
}
</style>
