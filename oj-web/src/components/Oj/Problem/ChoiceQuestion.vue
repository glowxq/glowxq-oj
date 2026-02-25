<template>
  <div class="choice-question">
    <div class="section-title options-title">
      <span class="title-text">选项</span>
      <span class="helper-text" v-if="isSingleChoice">
        (单选题，请选择一个选项)
      </span>
      <span class="helper-text" v-else>
        (多选题，可选择多个选项)
      </span>
    </div>
    <div class="options-container">
      <el-checkbox-group v-model="selectedValue" class="choice-group" @change="onSelectionChange">
        <div v-for="option in options" :key="option.id" class="option-item">
          <el-checkbox :label="option.optionKey">
            <div class="option-content-wrapper">
              <span class="option-key">{{ option.optionKey }}</span>
              <span class="option-content" v-html="option.optionContent"></span>
            </div>
          </el-checkbox>
        </div>
      </el-checkbox-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import type { ProblemOption } from '@/api/interface/oj/problem/ptoblemSubmit';

defineOptions({
  name: 'ChoiceQuestion'
});

const props = defineProps<{
  options: ProblemOption[];
  modelValue: string[];
  type: string; // 'single' 或 'multiple'
}>();

const emits = defineEmits<{
  (e: 'update:modelValue', value: string[]): void;
  (e: 'change', value: string[]): void;
}>();

// 计算属性判断是否为单选题
const isSingleChoice = computed(() => props.type === 'single');

// 本地选中值
const selectedValue = ref<string[]>(props.modelValue);

// 选项变更时处理
const onSelectionChange = (value: string[]) => {
  // 如果是单选题，确保只有一个选项被选中
  if (isSingleChoice.value && value.length > 1) {
    selectedValue.value = [value[value.length - 1]];
  } else {
    selectedValue.value = value;
  }

  // 向父组件发送更新事件
  emits('update:modelValue', selectedValue.value);
  emits('change', selectedValue.value);
};

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  selectedValue.value = newValue;
}, { deep: true });
</script>

<style scoped>
.choice-question {
  width: 100%;
}

.options-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.2rem;
  margin: 20px 0 10px;
  padding: 8px 10px;
  color: var(--el-color-primary);
  font-weight: 600;
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.options-title .title-text {
  font-weight: 600;
}

.options-title .helper-text {
  font-size: 0.9em;
  color: var(--el-text-color-secondary);
  font-weight: normal;
}

.options-container {
  margin: 15px 0;
  padding: 20px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.choice-group {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.option-item {
  margin-bottom: 12px;
  border-radius: 6px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.option-item:last-child {
  margin-bottom: 0;
}

.option-item .el-checkbox {
  width: 100%;
  margin-right: 0;
  padding: 14px 16px;
  border-radius: 6px;
  display: flex;
  align-items: flex-start;
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  transition: all 0.3s ease;
}

.option-item .el-checkbox:hover {
  background-color: var(--el-fill-color-light);
  border-color: var(--el-border-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.option-item .el-checkbox.is-checked {
  background-color: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.1);
}

.option-content-wrapper {
  display: flex;
  align-items: flex-start;
  width: 100%;
  margin-left: 8px;
}

.option-key {
  font-weight: 600;
  color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
  padding: 4px 0;
  border-radius: 4px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  min-width: 32px;
  height: 32px;
  margin-right: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.option-content {
  display: inline-block;
  flex: 1;
  line-height: 1.6;
  padding: 6px 0;
}

.option-item .el-checkbox__label {
  white-space: normal;
  color: var(--el-text-color-primary);
  width: 100%;
}

.option-item .el-checkbox.is-checked .el-checkbox__label {
  color: var(--el-color-primary);
  font-weight: 500;
}

.option-item .el-checkbox.is-checked .option-key {
  background-color: var(--el-color-primary);
  color: white;
  box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.25);
}
</style>
