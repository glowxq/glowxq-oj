<template>
  <div class="judge-status-container">
    <el-tooltip
      :content="tooltipContent"
      placement="top"
      :effect="tooltipEffect"
    >
      <div
        class="judge-status-tag"
        :class="statusClass"
        :data-code="statusCode"
      >
        <div class="status-icon" v-if="showIcon">
          <i class="status-dot" :class="iconClass"></i>
          <i class="status-pulse" v-if="isProcessingStatus"></i>
        </div>
        <span class="status-name">{{ statusName }}</span>
      </div>
    </el-tooltip>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { matchCode } from '@/enums/oj/judge/JudgeStatus';

const props = defineProps({
  // 状态代码，使用数字
  code: {
    type: [Number, String], // 修改为允许字符串，内部会转换
    default: 15 // 默认无状态
  },
  // 是否显示状态图标
  showIcon: {
    type: Boolean,
    default: true
  },
  // tooltip主题
  tooltipEffect: {
    type: String,
    default: 'light'
  },
  // 自定义样式
  customClass: {
    type: String,
    default: ''
  },
  // 状态标签宽度
  fixedWidth: {
    type: String,
    default: '140px'
  }
});

// 将输入的code转换为数字
const numericCode = computed(() => {
  // 如果是数字则直接使用，否则尝试转换
  return typeof props.code === 'number' ? props.code : Number(props.code) || 15;
});

// 获取状态对象
const statusInfo = computed(() => {
  return matchCode(numericCode.value) || {
    code: 15,
    name: 'No Status',
    tooltip: '无状态',
    text: '未获取到评测状态'
  };
});

// 获取状态代码
const statusCode = computed(() => numericCode.value);

// 获取状态名称
const statusName = computed(() => statusInfo.value.name);

// 获取提示文本
const tooltipContent = computed(() => {
  const info = statusInfo.value;
  return `${info.tooltip || ''}\n${info.text || ''}`;
});

// 是否为处理中状态（需要动画效果）
const isProcessingStatus = computed(() => {
  return [5, 6, 7, 9].includes(numericCode.value);
});

// 获取图标类名
const iconClass = computed(() => {
  const code = numericCode.value;

  // 根据状态代码分配不同图标类名
  if (code === 0) { // Accepted
    return 'icon-check';
  } else if (code === 8) { // Partial Accepted
    return 'icon-partial';
  } else if ([5, 6, 7, 9].includes(code)) { // Pending, Compiling, Judging, Submitting
    return 'icon-pending';
  } else if ([1, 2, 3].includes(code)) { // TLE, MLE, RE
    return 'icon-warning';
  } else if ([-1, -2, -3, -4, 4, 10].includes(code)) { // WA, CE, PE, Cancel, SE, Submit Failed
    return 'icon-error';
  } else {
    return 'icon-unknown';
  }
});

// 获取状态类名
const statusClass = computed(() => {
  const code = numericCode.value;
  let colorClass = '';

  // 根据状态代码分配不同类名
  if (code === 0) { // Accepted
    colorClass = 'status-accepted';
  } else if (code === 8) { // Partial Accepted
    colorClass = 'status-partial';
  } else if ([5, 6, 7, 9].includes(code)) { // Pending, Compiling, Judging, Submitting
    colorClass = 'status-pending';
  } else if ([1, 2, 3].includes(code)) { // TLE, MLE, RE
    colorClass = 'status-warning';
  } else if ([-1, -2, -3, -4, 4, 10].includes(code)) { // WA, CE, PE, Cancel, SE, Submit Failed
    colorClass = 'status-error';
  } else {
    colorClass = 'status-unknown';
  }

  return [colorClass, props.customClass];
});
</script>

<style scoped lang="scss">
.judge-status-container {
  display: inline-block;
  perspective: 500px;
}

.judge-status-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.2;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
  cursor: default;
  gap: 8px;
  letter-spacing: -0.01em;
  position: relative;
  overflow: hidden;
  border: none;
  border-radius: 8px;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  width: v-bind(fixedWidth); // 使用动态宽度绑定
  height: 28px;

  &:hover {
    transform: translateY(-1px);
  }

  .status-icon {
    position: relative;
    width: 16px;
    height: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2;
    transition: transform 0.5s cubic-bezier(0.25, 0.1, 0.25, 1);
    flex-shrink: 0;

    .status-dot {
      width: 16px;
      height: 16px;
      display: block;
      transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
      position: relative;

      &::before,
      &::after {
        content: '';
        position: absolute;
        transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
      }

      &.icon-check {
        &::before {
          top: 2px;
          left: 2px;
          width: 12px;
          height: 12px;
          background: currentColor;
          mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
          -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
          mask-size: contain;
          -webkit-mask-size: contain;
          mask-repeat: no-repeat;
          -webkit-mask-repeat: no-repeat;
        }
      }

      &.icon-partial {
        &::before {
          top: 2px;
          left: 2px;
          width: 12px;
          height: 12px;
          background: currentColor;
          mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M22 11.08V12a10 10 0 1 1-5.93-9.14'%3E%3C/path%3E%3Cpolyline points='22 4 12 14.01 9 11.01'%3E%3C/polyline%3E%3C/svg%3E");
          -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M22 11.08V12a10 10 0 1 1-5.93-9.14'%3E%3C/path%3E%3Cpolyline points='22 4 12 14.01 9 11.01'%3E%3C/polyline%3E%3C/svg%3E");
          mask-size: contain;
          -webkit-mask-size: contain;
          mask-repeat: no-repeat;
          -webkit-mask-repeat: no-repeat;
        }
      }

      &.icon-pending {
        &::before {
          top: 2px;
          left: 2px;
          width: 12px;
          height: 12px;
          background: currentColor;
          mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='10'%3E%3C/circle%3E%3Cpolyline points='12 6 12 12 16 14'%3E%3C/polyline%3E%3C/svg%3E");
          -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='10'%3E%3C/circle%3E%3Cpolyline points='12 6 12 12 16 14'%3E%3C/polyline%3E%3C/svg%3E");
          mask-size: contain;
          -webkit-mask-size: contain;
          mask-repeat: no-repeat;
          -webkit-mask-repeat: no-repeat;
        }
      }

      &.icon-warning {
        &::before {
          top: 2px;
          left: 2px;
          width: 12px;
          height: 12px;
          background: currentColor;
          mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z'%3E%3C/path%3E%3Cline x1='12' y1='9' x2='12' y2='13'%3E%3C/line%3E%3Cline x1='12' y1='17' x2='12.01' y2='17'%3E%3C/line%3E%3C/svg%3E");
          -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z'%3E%3C/path%3E%3Cline x1='12' y1='9' x2='12' y2='13'%3E%3C/line%3E%3Cline x1='12' y1='17' x2='12.01' y2='17'%3E%3C/line%3E%3C/svg%3E");
          mask-size: contain;
          -webkit-mask-size: contain;
          mask-repeat: no-repeat;
          -webkit-mask-repeat: no-repeat;
        }
      }

      &.icon-error {
        &::before {
          top: 2px;
          left: 2px;
          width: 12px;
          height: 12px;
          background: currentColor;
          mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='10'%3E%3C/circle%3E%3Cline x1='15' y1='9' x2='9' y2='15'%3E%3C/line%3E%3Cline x1='9' y1='9' x2='15' y2='15'%3E%3C/line%3E%3C/svg%3E");
          -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='10'%3E%3C/circle%3E%3Cline x1='15' y1='9' x2='9' y2='15'%3E%3C/line%3E%3Cline x1='9' y1='9' x2='15' y2='15'%3E%3C/line%3E%3C/svg%3E");
          mask-size: contain;
          -webkit-mask-size: contain;
          mask-repeat: no-repeat;
          -webkit-mask-repeat: no-repeat;
        }
      }

      &.icon-unknown {
        &::before {
          top: 2px;
          left: 2px;
          width: 12px;
          height: 12px;
          background: currentColor;
          mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='10'%3E%3C/circle%3E%3Cpath d='M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3'%3E%3C/path%3E%3Cline x1='12' y1='17' x2='12.01' y2='17'%3E%3C/line%3E%3C/svg%3E");
          -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='10'%3E%3C/circle%3E%3Cpath d='M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3'%3E%3C/path%3E%3Cline x1='12' y1='17' x2='12.01' y2='17'%3E%3C/line%3E%3C/svg%3E");
          mask-size: contain;
          -webkit-mask-size: contain;
          mask-repeat: no-repeat;
          -webkit-mask-repeat: no-repeat;
        }
      }
    }

    .status-pulse {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: currentColor;
      opacity: 0.4;
      border-radius: 50%;
    }
  }

  .status-name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    position: relative;
    z-index: 2;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', 'Helvetica Neue', sans-serif;
    font-weight: 500;
    transition: letter-spacing 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
    text-align: center;
    flex-grow: 1;
  }

  // 已通过
  &.status-accepted {
    color: #34c759;
    background: rgba(52, 199, 89, 0.1);
    box-shadow: 0 2px 8px rgba(52, 199, 89, 0.15), inset 0 0 0 0.5px rgba(52, 199, 89, 0.2);

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: radial-gradient(circle at center, rgba(52, 199, 89, 0.2) 0%, transparent 70%);
      opacity: 0;
      transition: opacity 0.5s cubic-bezier(0.25, 0.1, 0.25, 1);
      z-index: 1;
    }

    &:hover {
      &::before {
        opacity: 1;
      }

      .status-name {
        letter-spacing: 0.2px;
      }

      .status-icon {
        transform: rotate(360deg);
      }
    }
  }

  // 部分通过
  &.status-partial {
    color: #bf5af2;
    background: rgba(191, 90, 242, 0.1);
    box-shadow: 0 2px 8px rgba(191, 90, 242, 0.15), inset 0 0 0 0.5px rgba(191, 90, 242, 0.2);

    &:hover {
      .status-icon {
        transform: scale(1.1);
      }
    }
  }

  // 进行中
  &.status-pending {
    color: #0a84ff;
    background: rgba(10, 132, 255, 0.1);
    box-shadow: 0 2px 8px rgba(10, 132, 255, 0.15), inset 0 0 0 0.5px rgba(10, 132, 255, 0.2);

    .status-icon {
      animation: apple-spin 1.5s infinite cubic-bezier(0.25, 0.1, 0.25, 1);
    }

    .status-pulse {
      animation: apple-pulse-wave 1.5s infinite cubic-bezier(0.25, 0.1, 0.25, 1);
      border-radius: 50%;
    }

    .status-name {
      animation: apple-pulse-text 2s infinite cubic-bezier(0.25, 0.1, 0.25, 1);
    }
  }

  // 警告
  &.status-warning {
    color: #ff9f0a;
    background: rgba(255, 159, 10, 0.1);
    box-shadow: 0 2px 8px rgba(255, 159, 10, 0.15), inset 0 0 0 0.5px rgba(255, 159, 10, 0.2);

    &:hover {
      animation: apple-scale 0.5s cubic-bezier(0.25, 0.1, 0.25, 1) both;

      .status-icon {
        animation: apple-warning-flash 0.8s cubic-bezier(0.25, 0.1, 0.25, 1);
      }
    }
  }

  // 错误
  &.status-error {
    color: #ff3b30;
    background: rgba(255, 59, 48, 0.1);
    box-shadow: 0 2px 8px rgba(255, 59, 48, 0.15), inset 0 0 0 0.5px rgba(255, 59, 48, 0.2);

    &:hover {
      .status-icon {
        transform: scale(1.1);
      }

      animation: apple-error-shake 0.5s cubic-bezier(0.25, 0.1, 0.25, 1) both;
    }
  }

  // 未知状态
  &.status-unknown {
    color: #8e8e93;
    background: rgba(142, 142, 147, 0.1);
    box-shadow: 0 2px 8px rgba(142, 142, 147, 0.15), inset 0 0 0 0.5px rgba(142, 142, 147, 0.2);

    &:hover {
      .status-icon {
        transform: scale(0.9);
        opacity: 0.8;
      }
    }
  }
}

// 关键帧动画
@keyframes apple-pulse-wave {
  0% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.5);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 0;
  }
}

@keyframes apple-pulse-text {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
  100% {
    opacity: 1;
  }
}

@keyframes apple-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes apple-scale {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.03);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes apple-warning-flash {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
    box-shadow: 0 0 10px rgba(255, 159, 10, 0.8);
  }
  100% {
    opacity: 1;
  }
}

@keyframes apple-error-shake {
  0%, 100% {
    transform: translateX(0);
  }
  20%, 60% {
    transform: translateX(-2px);
  }
  40%, 80% {
    transform: translateX(2px);
  }
}

// 响应式调整
@media (max-width: 768px) {
  .judge-status-tag {
    padding: 5px 10px;
    font-size: 11px;
    width: 120px;
    height: 24px;

    .status-icon {
      width: 14px;
      height: 14px;

      .status-dot {
        width: 14px;
        height: 14px;

        &::before {
          top: 1px;
          left: 1px;
          width: 12px;
          height: 12px;
        }
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .judge-status-tag {
    &.status-accepted {
      background: rgba(52, 199, 89, 0.15);
      box-shadow: 0 2px 8px rgba(52, 199, 89, 0.2), inset 0 0 0 0.5px rgba(52, 199, 89, 0.3);
    }

    &.status-partial {
      background: rgba(191, 90, 242, 0.15);
      box-shadow: 0 2px 8px rgba(191, 90, 242, 0.2), inset 0 0 0 0.5px rgba(191, 90, 242, 0.3);
    }

    &.status-pending {
      background: rgba(10, 132, 255, 0.15);
      box-shadow: 0 2px 8px rgba(10, 132, 255, 0.2), inset 0 0 0 0.5px rgba(10, 132, 255, 0.3);
    }

    &.status-warning {
      background: rgba(255, 159, 10, 0.15);
      box-shadow: 0 2px 8px rgba(255, 159, 10, 0.2), inset 0 0 0 0.5px rgba(255, 159, 10, 0.3);
    }

    &.status-error {
      background: rgba(255, 59, 48, 0.15);
      box-shadow: 0 2px 8px rgba(255, 59, 48, 0.2), inset 0 0 0 0.5px rgba(255, 59, 48, 0.3);
    }

    &.status-unknown {
      background: rgba(142, 142, 147, 0.15);
      box-shadow: 0 2px 8px rgba(142, 142, 147, 0.2), inset 0 0 0 0.5px rgba(142, 142, 147, 0.3);
    }
  }
}
</style>
