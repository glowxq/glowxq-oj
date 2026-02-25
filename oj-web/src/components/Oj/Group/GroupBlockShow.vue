<template>
  <div class="group-block-show">
    <template v-if="groups && groups.length > 0">
      <div class="group-list">
        <div class="group-count" v-if="showCount">共 {{ groups.length }} 个班级</div>
        <div class="group-tags-container">
          <el-tag
            v-for="group in groups"
            :key="group.id"
            :type="type"
            :effect="effect"
            class="group-name-tag"
            :style="getTagStyle(group)"
          >
            {{ group.name }}
          </el-tag>
        </div>
      </div>
    </template>
    <template v-else-if="groupIds && groupIds.length > 0">
      <el-tag
        :type="type"
        :effect="effect"
        class="group-tag"
      >
        已分配 {{ groupIds.length }} 个班级
      </el-tag>
    </template>
    <template v-else>
      <span class="no-group">暂无班级</span>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ElTag } from 'element-plus'
import { computed } from 'vue'

interface Group {
  id: number | string
  name: string
  color?: string
  textColor?: string
}

interface Props {
  groups?: Group[]
  groupIds?: (number | string)[]
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  effect?: 'plain' | 'light' | 'dark'
  showCount?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  effect: 'plain',
  showCount: true
})

// Apple主题颜色
const THEME_COLOR = '#007AFF' // Apple蓝色
// 默认文字颜色
const DEFAULT_TEXT_COLOR = '#1D1D1F' // Apple深灰色
// 默认背景颜色
const DEFAULT_BG_COLOR = '#F5F5F7' // Apple浅灰色背景

// 预设的文字颜色方案 - Apple风格
const TEXT_COLORS = {
  light: '#FFFFFF',      // 白色文字
  dark: '#1D1D1F',       // Apple深色文字
  primary: '#007AFF',    // Apple蓝色
  success: '#34C759',    // Apple绿色
  warning: '#FF9500',    // Apple橙色
  danger: '#FF3B30',     // Apple红色
  info: '#8E8E93'        // Apple灰色
}

// 将颜色转换为RGB
const hexToRgb = (hex: string) => {
  // 确保hex是6位十六进制颜色值
  hex = hex.replace('#', '')
  if (hex.length === 3) {
    hex = hex.split('').map(h => h + h).join('')
  }
  const result = /^([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result ? {
    r: parseInt(result[1], 16),
    g: parseInt(result[2], 16),
    b: parseInt(result[3], 16)
  } : null
}

// 计算颜色的亮度
const getBrightness = (color: string) => {
  const rgb = hexToRgb(color)
  if (!rgb) return 0
  return (rgb.r * 299 + rgb.g * 587 + rgb.b * 114) / 1000
}

// 判断颜色是否相似
const isColorSimilar = (color1: string, color2: string) => {
  const rgb1 = hexToRgb(color1)
  const rgb2 = hexToRgb(color2)

  if (!rgb1 || !rgb2) return false

  // 计算颜色差异
  const diff = Math.sqrt(
    Math.pow(rgb1.r - rgb2.r, 2) +
    Math.pow(rgb1.g - rgb2.g, 2) +
    Math.pow(rgb1.b - rgb2.b, 2)
  )

  // 如果差异小于阈值，认为颜色相似
  return diff < 50
}

// 获取标签样式
const getTagStyle = (group: Group) => {
  const bgColor = group.color || DEFAULT_BG_COLOR
  const textColor = group.textColor || DEFAULT_TEXT_COLOR

  // 如果背景色和文字颜色相似，使用主题色作为背景
  if (isColorSimilar(bgColor, textColor)) {
    const brightness = getBrightness(bgColor)
    // 根据背景色亮度选择文字颜色
    const newTextColor = brightness > 128 ? TEXT_COLORS.dark : TEXT_COLORS.light
    
    return {
      backgroundColor: THEME_COLOR,
      color: newTextColor,
      borderColor: 'transparent'
    }
  }

  // 如果背景色和文字颜色不相似，但对比度不够
  const bgBrightness = getBrightness(bgColor)
  const textBrightness = getBrightness(textColor)
  const contrastRatio = Math.abs(bgBrightness - textBrightness)

  if (contrastRatio < 100) {
    // 根据背景色亮度选择文字颜色
    const newTextColor = bgBrightness > 128 ? TEXT_COLORS.dark : TEXT_COLORS.light
    return {
      backgroundColor: bgColor,
      color: newTextColor,
      borderColor: 'transparent'
    }
  }

  return {
    backgroundColor: bgColor,
    color: textColor,
    borderColor: 'transparent'
  }
}
</script>

<style scoped lang="scss">
.group-block-show {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
  
  .group-list {
    .group-count {
      font-size: 13px;
      color: #86868B; // Apple次要文本颜色
      margin-bottom: 8px;
      font-weight: 500;
    }

    .group-tags-container {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .group-name-tag {
        margin: 0;
        font-size: 13px;
        border-radius: 8px;
        transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1); // Apple风格过渡
        padding: 6px 12px;
        font-weight: 500;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
        border: none;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
          opacity: 0.95;
        }
      }
    }
  }

  .group-tag {
    padding: 6px 12px;
    font-size: 13px;
    border-radius: 8px;
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
    font-weight: 500;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    border: none;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      opacity: 0.95;
    }
  }

  .no-group {
    color: #86868B; // Apple次要文本颜色
    font-size: 13px;
    display: inline-block;
    padding: 8px 0;
  }
}
</style>
