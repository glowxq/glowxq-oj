<template>
  <div class="true-false-question">
    <div class="section-title options-title">
      <span class="title-text">选项</span>
      <span class="helper-text">
        (判断题，请选择正确或错误)
      </span>
    </div>
    <div class="options-container">
      <div class="true-false-container">
        <el-checkbox-group v-model="selectedValue" class="choice-group" @change="handleChange">
          <div class="option-item">
            <el-checkbox label="A">
              <div class="option-content-wrapper">
                <span class="option-key">A</span>
                <span class="option-content">
                  <el-icon class="true-false-icon"><Check /></el-icon>
                  正确
                </span>
              </div>
            </el-checkbox>
          </div>
          <div class="option-item">
            <el-checkbox label="B">
              <div class="option-content-wrapper">
                <span class="option-key">B</span>
                <span class="option-content">
                  <el-icon class="true-false-icon"><Close /></el-icon>
                  错误
                </span>
              </div>
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { Check, Close } from '@element-plus/icons-vue';

defineOptions({
  name: 'TrueFalseQuestion'
});

const props = defineProps<{
  modelValue: string[];
}>();

const emits = defineEmits<{
  (e: 'update:modelValue', value: string[]): void;
  (e: 'change', value: string[]): void;
}>();

// 本地选中值
const selectedValue = ref<string[]>(props.modelValue);

// 处理选项变更
const handleChange = (value: string[]) => {
  // 判断题只能选一个选项
  if (value.length > 1) {
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
.true-false-question {
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

.true-false-container {
  margin-top: 10px;
}

.true-false-container .choice-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.true-false-icon {
  margin-right: 5px;
  font-size: 16px;
  vertical-align: middle;
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

.option-item:first-child .true-false-icon {
  color: var(--el-color-success);
}

.option-item:last-child .true-false-icon {
  color: var(--el-color-danger);
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