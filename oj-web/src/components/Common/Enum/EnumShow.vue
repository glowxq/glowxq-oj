<template>
  <span 
    class="enum-show-value" 
    :class="$attrs.class" 
    :data-code="code"
    :style="customStyle"
  >{{ displayValue }}</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { BaseEnum } from '@/enums/base'

interface Props {
  enum: { [key: string]: BaseEnum }
  code: string | number
  backgroundColor?: string
  color?: string
  borderColor?: string
  fontSize?: string
  fontWeight?: string
  padding?: string
  borderRadius?: string
  boxShadow?: string
  textShadow?: string
  minWidth?: string
  textAlign?: string
  transform?: string
  border?: string
}

const props = withDefaults(defineProps<Props>(), {
  backgroundColor: '',
  color: '',
  borderColor: '',
  fontSize: '',
  fontWeight: '',
  padding: '',
  borderRadius: '',
  boxShadow: '',
  textShadow: '',
  minWidth: '',
  textAlign: '',
  transform: '',
  border: ''
})

const displayValue = computed(() => {
  const enumValue = Object.values(props.enum).find(item => item.code === props.code)
  return enumValue?.name || props.code
})

const customStyle = computed(() => {
  const style: Record<string, string> = {}
  
  if (props.backgroundColor) {
    style.backgroundColor = props.backgroundColor
  }
  
  if (props.color) {
    style.color = props.color
  }
  
  if (props.borderColor) {
    style.borderColor = props.borderColor
    style.borderWidth = '1px'
    style.borderStyle = 'solid'
  }

  if (props.fontSize) {
    style.fontSize = props.fontSize
  }

  if (props.fontWeight) {
    style.fontWeight = props.fontWeight
  }

  if (props.padding) {
    style.padding = props.padding
  }

  if (props.borderRadius) {
    style.borderRadius = props.borderRadius
  }

  if (props.boxShadow) {
    style.boxShadow = props.boxShadow
  }

  if (props.textShadow) {
    style.textShadow = props.textShadow
  }

  if (props.minWidth) {
    style.minWidth = props.minWidth
  }

  if (props.textAlign) {
    style.textAlign = props.textAlign
  }

  if (props.transform) {
    style.transform = props.transform
  }

  if (props.border) {
    style.border = props.border
  }
  
  return style
})
</script>

<style scoped>
.enum-show-value {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 2px 8px;
  border-radius: 3px;
  background-color: #f0f0f0;
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  transition: all 0.25s ease;
}
</style>
