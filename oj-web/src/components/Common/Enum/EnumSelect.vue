<template>
  <div class="enum-select">
    <!-- 下拉框模式 -->
    <el-select
      v-if="type === 'select'"
      v-model="selectedValue"
      :multiple="multiple"
      :placeholder="placeholder"
      :size="size"
      :disabled="disabled"
      @change="handleChange"
    >
      <el-option
        v-for="item in options"
        :key="item.code"
        :label="item.name"
        :value="item.code"
        :disabled="disabledValues.includes(item.code)"
      />
    </el-select>

    <!-- 单选按钮模式 -->
    <el-radio-group
      v-else-if="type === 'radio'"
      v-model="selectedValue"
      :size="size"
      :disabled="disabled"
      @change="handleChange"
    >
      <el-radio
        v-for="item in options"
        :key="item.code"
        :label="item.code"
        :disabled="disabledValues.includes(item.code)"
      >
        {{ item.name }}
      </el-radio>
    </el-radio-group>

    <!-- Tab风格模式 -->
    <div v-else-if="type === 'tab'" class="enum-tabs">
      <el-tooltip
        v-for="item in options"
        :key="item.code"
        :content="item.tooltip || item.name"
        placement="top"
        :disabled="!item.tooltip"
      >
        <div
          class="enum-tab-item"
          :class="{ 
            'enum-tab-active': isItemSelected(item.code),
            'enum-tab-disabled': disabledValues.includes(item.code) 
          }"
          :disabled="disabledValues.includes(item.code)"
          @click="!disabledValues.includes(item.code) && handleTabClick(item.code)"
        >
          {{ item.name }}
        </div>
      </el-tooltip>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import type { PropType } from 'vue'
import type { BaseEnum } from '@/enums/base'

const props = defineProps({
  // 枚举对象或枚举数组
  enumData: {
    type: Object as PropType<Record<string, BaseEnum>>,
    required: true
  },
  // 选择器类型：select-下拉框，radio-单选按钮，tab-标签风格
  type: {
    type: String as PropType<'select' | 'radio' | 'tab'>,
    default: 'tab'
  },
  // 是否多选
  multiple: {
    type: Boolean,
    default: false
  },
  // 默认值
  modelValue: {
    type: [String, Number, Array] as PropType<string | number | (string | number)[]>,
    default: ''
  },
  // 禁用的值
  disabledValues: {
    type: Array as PropType<(string | number)[]>,
    default: () => []
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择'
  },
  // 尺寸
  size: {
    type: String as PropType<'large' | 'default' | 'small'>,
    default: 'default'
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 选中值
const selectedValue = ref(props.modelValue)

// 选项列表
const options = computed(() => {
  return Object.values(props.enumData)
})

// 判断项目是否被选中
const isItemSelected = (code: string | number) => {
  if (props.multiple && Array.isArray(selectedValue.value)) {
    return selectedValue.value.includes(code)
  }
  return selectedValue.value === code
}

// Tab点击处理
const handleTabClick = (code: string | number) => {
  if (props.disabled) return
  
  if (props.multiple && Array.isArray(selectedValue.value)) {
    // 多选模式
    const newValue = [...selectedValue.value]
    const index = newValue.indexOf(code)
    
    if (index > -1) {
      newValue.splice(index, 1)
    } else {
      newValue.push(code)
    }
    
    selectedValue.value = newValue
    handleChange(newValue)
  } else {
    // 单选模式
    selectedValue.value = code
    handleChange(code)
  }
}

// 值变化处理
const handleChange = (val: any) => {
  emit('update:modelValue', val)
  // 返回完整的枚举对象
  const selectedEnum = props.multiple
    ? options.value.filter(item => val.includes(item.code))
    : options.value.find(item => item.code === val)
  emit('change', selectedEnum)
}

// 监听外部值变化
watch(
  () => props.modelValue,
  (newVal) => {
    selectedValue.value = newVal
  }
)
</script>

<style lang="scss" scoped>
.enum-select {
  width: 100%;
  
  .enum-tabs {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    
    .enum-tab-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 6px 14px;
      font-size: 14px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
      background-color: var(--el-fill-color-lighter);
      color: var(--el-text-color-regular);
      user-select: none;
      
      &:hover:not(.enum-tab-disabled) {
        background-color: var(--el-color-primary-light-9);
        color: var(--el-color-primary);
      }
      
      &.enum-tab-active {
        background-color: var(--el-color-primary);
        color: white;
      }
      
      &.enum-tab-disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}
</style>
