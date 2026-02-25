<template>
  <div class="color-select">
    <el-color-picker
      v-model="colorValue"
      :predefine="predefineColors"
      show-alpha
      color-format="hex"
      @change="handleColorChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: string
  textColor?: string
  previewText?: string
  showPreview?: boolean
}>()

const emit = defineEmits(['update:modelValue', 'update:textColor'])

const colorValue = ref(props.modelValue)
const textColorValue = ref(props.textColor || '#FFFFFF')

// 预定义颜色列表
const predefineColors = [
  '#000000', // 黑色
  '#FFFFFF', // 白色
  '#FF4500', // 橙红色
  '#FF8C00', // 深橙色
  '#FFD700', // 金色
  '#90EE90', // 浅绿色
  '#00CED1', // 深青色
  '#1E90FF', // 道奇蓝
  '#C71585', // 中紫红色
  '#FF69B4', // 粉红色
  '#9370DB', // 中紫色
  '#8B4513',  // 马鞍棕色
  '#009688' // 青色
]

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  colorValue.value = newVal
})

watch(() => props.textColor, (newVal) => {
  textColorValue.value = newVal || '#FFFFFF'
})

// 处理颜色变化
const handleColorChange = (color: string) => {
  emit('update:modelValue', color)
  
  // 根据背景色自动调整文字颜色
  if (color && props.showPreview) {
    const r = parseInt(color.slice(1, 3), 16)
    const g = parseInt(color.slice(3, 5), 16)
    const b = parseInt(color.slice(5, 7), 16)
    // 计算亮度
    const brightness = (r * 299 + g * 587 + b * 114) / 1000
    // 根据亮度选择黑色或白色文字
    const textColor = brightness > 128 ? '#000000' : '#FFFFFF'
    textColorValue.value = textColor
    emit('update:textColor', textColor)
  }
}
</script>

<style scoped lang="scss">
.color-select {
  display: flex;
  align-items: center;
  gap: 16px;
}

.preview-box {
  padding: 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 40px;
  display: flex;
  align-items: center;
}
</style>
