<template>
  <div class="short-answer-question">
    <div class="section-title options-title">
      <span class="title-text">简答题</span>
      <span class="helper-text">
        (请在下方详细作答)
      </span>
    </div>
    <div class="options-container">
      <div class="short-answer-container">
        <div class="short-answer-label">请在下方作答：</div>
        <el-input 
          v-model="localAnswer" 
          type="textarea" 
          :rows="6"
          placeholder="请输入答案"
          class="short-answer-input"
          @input="onInputChange"
        ></el-input>
        
        <!-- 进度显示 -->
        <div class="answer-progress">
          <div class="progress-title">答题进度:</div>
          <el-progress 
            :percentage="calculateProgress()" 
            :format="(percentage: number) => `${percentage}%`"
            :status="getProgressStatus(calculateProgress())"
          />
          <div class="status-info">
            <div class="status-text">{{ localAnswer ? '已作答' : '未作答' }}</div>
            <div class="word-count" v-if="localAnswer">字数: {{ localAnswer.length }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';

defineOptions({
  name: 'ShortAnswerQuestion'
});

const props = defineProps<{
  modelValue: string;
  maxWords?: number; // 最大字数要求
}>();

const emits = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'change', value: string): void;
}>();

// 本地答案
const localAnswer = ref(props.modelValue || '');

// 最大字数限制，默认200
const maxLength = computed(() => props.maxWords || 200);

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  localAnswer.value = newValue || '';
});

// 输入改变事件
const onInputChange = () => {
  emits('update:modelValue', localAnswer.value);
  emits('change', localAnswer.value);
};

// 计算进度
const calculateProgress = () => {
  if (!localAnswer.value) return 0;
  
  // 计算进度比例，但最多100%
  return Math.min(Math.round((localAnswer.value.length / maxLength.value) * 100), 100);
};

// 获取进度状态
const getProgressStatus = (percentage: number) => {
  if (percentage === 0) return 'exception';
  if (percentage < 20) return 'warning';
  return 'success';
};
</script>

<style scoped>
.short-answer-question {
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

.short-answer-container {
  padding: 10px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
}

.short-answer-label {
  font-weight: 500;
  margin-bottom: 12px;
  color: var(--el-text-color-regular);
  padding-left: 8px;
  border-left: 3px solid var(--el-color-primary);
}

.short-answer-input .el-textarea__inner {
  border: 1px solid var(--el-border-color);
  transition: all 0.3s ease;
  background-color: var(--el-fill-color-blank);
  font-size: 14px;
  line-height: 1.6;
}

.short-answer-input .el-textarea__inner:focus {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.answer-progress {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px dashed var(--el-border-color-light);
}

.progress-title {
  font-weight: 600;
  margin-bottom: 5px;
  color: var(--el-text-color-regular);
}

.status-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.word-count {
  font-weight: 500;
}
</style> 