<template>
  <div class="block-button-switch">
    <div 
      class="switch-block" 
      :class="[
        { 'is-active': modelValue },
        { 'is-unselected': isUnselected },
        { 'is-readonly': readonly },
        `size-${size}`
      ]"
      :style="{ 
        '--active-color': activeColor,
        '--inactive-color': inactiveColor,
        '--text-color': modelValue ? '#ffffff' : textColor,
        '--unselected-color': unselectedColor
      }"
      @click="!readonly && toggleSwitch()"
    >
      <i v-if="icon" :class="icon" class="switch-icon"></i>
      <span class="switch-text">{{ displayText }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: boolean | null | undefined;
  text?: string;
  inactiveText?: string;
  placeholderText?: string;
  icon?: string;
  activeColor?: string;
  inactiveColor?: string;
  unselectedColor?: string;
  textColor?: string;
  size?: 'small' | 'default' | 'large';
  allowUnselected?: boolean;
  readonly?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  text: '',
  inactiveText: '',
  placeholderText: '请选择',
  icon: '',
  activeColor: 'var(--el-color-primary)',
  inactiveColor: 'var(--el-fill-color)',
  unselectedColor: '#f5f7fa',
  textColor: 'var(--el-text-color-regular)',
  size: 'default',
  allowUnselected: false,
  readonly: false
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean | null]
}>()

// 计算是否为未选中状态
const isUnselected = computed(() => {
  return props.allowUnselected && (props.modelValue === null || props.modelValue === undefined)
})

// 计算显示文本
const displayText = computed(() => {
  if (isUnselected.value) {
    return props.placeholderText
  }
  
  if (props.modelValue) {
    return props.text || '已启用'
  } else {
    return props.inactiveText || '未启用'
  }
})

const toggleSwitch = () => {
  if (props.allowUnselected) {
    // 支持三态切换：null -> true -> false -> null
    if (props.modelValue === null || props.modelValue === undefined) {
      emit('update:modelValue', true)
    } else if (props.modelValue === true) {
      emit('update:modelValue', false)
    } else {
      emit('update:modelValue', null)
    }
  } else {
    // 二态切换：true <-> false
    emit('update:modelValue', !props.modelValue)
  }
}
</script>

<style scoped lang="scss">
.block-button-switch {
  display: inline-block;
  
  .switch-block {
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 64px;
    height: 28px;
    padding: 0 10px;
    font-size: 12px;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    background-color: var(--inactive-color);
    border: 1px solid var(--el-border-color-light);
    color: var(--text-color);
    user-select: none;
    position: relative;
    overflow: hidden;
    
    &.size-small {
      min-width: 40px;
      height: 24px;
      padding: 0 6px;
      font-size: 12px;
      border-radius: 4px;
      
      .switch-icon {
        font-size: 12px;
        margin-right: 3px;
      }
    }
    
    &.size-large {
      min-width: 80px;
      height: 32px;
      padding: 0 12px;
      font-size: 13px;
      
      .switch-icon {
        font-size: 14px;
      }
    }
    
    &:hover:not(.is-readonly) {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      
      &:not(.is-active):not(.is-unselected) {
        background-color: #f5f7fa;
      }
      
      &.is-unselected {
        background-color: #e8f4fd;
        border-color: var(--el-color-primary-light-7);
      }
    }
    
    &:active {
      transform: translateY(0);
    }
    
    &.is-unselected {
      background-color: var(--unselected-color);
      border: 1px dashed var(--el-border-color);
      color: var(--el-text-color-placeholder);
      font-style: italic;
      
      .switch-text {
        opacity: 0.8;
      }
      
      &::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 8px;
        width: 4px;
        height: 4px;
        background-color: var(--el-color-info);
        border-radius: 50%;
        transform: translateY(-50%);
        opacity: 0.6;
      }
    }
    
    &.is-readonly {
      cursor: default;
      user-select: none;
      opacity: 0.85;
      
      &.is-active {
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);
      }
      
      &:not(.is-active) {
        box-shadow: none;
      }
    }
    
    &.is-active {
      background-color: var(--active-color);
      border-color: var(--active-color);
      font-weight: 500;
      box-shadow: 0 2px 6px rgba(64, 158, 255, 0.25);
      
      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(180deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0) 100%);
        opacity: 0;
        transition: opacity 0.3s;
      }
      
      &:hover:not(.is-readonly)::after {
        opacity: 1;
      }
    }
    
    .switch-icon {
      margin-right: 4px;
      font-size: 12px;
    }
    
    .switch-text {
      font-weight: 500;
      transition: transform 0.15s;
      letter-spacing: 0.3px;
    }
  }
}
</style>
