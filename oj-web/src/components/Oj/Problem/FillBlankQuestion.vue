<template>
  <div class="fill-blank-question">
    <div class="section-title options-title">
      <span class="title-text">填空题</span>
      <span class="helper-text">
        (请在下方填入您的答案)
      </span>
    </div>
    <div class="options-container">
      <div class="fill-blank-container">
        <div class="fill-blank-label">请在下方填入您的答案：</div>

        <!-- 多个填空项的情况 -->
        <div v-if="hasMultipleOptions">
          <div v-for="(option, index) in options" :key="index" class="fill-blank-item">
            <div class="fill-blank-header">
              <div class="fill-blank-title">填空 #{{ index + 1 }}</div>
              <div class="fill-blank-status">
                <el-tag :type="answers[index] ? 'success' : 'info'" size="small">
                  {{ answers[index] ? '已填写' : '未填写' }}
                </el-tag>
              </div>
            </div>
            <el-input
              v-model="answers[index]"
              :placeholder="`请输入第${index + 1}空答案`"
              class="fill-blank-input"
              @input="onInputChange"
            ></el-input>
          </div>
        </div>

        <!-- 没有选项数据时的单个填空框 -->
        <template v-else>
          <el-input
            v-model="singleAnswer"
            type="textarea"
            :rows="3"
            placeholder="请输入答案"
            class="fill-blank-input"
            @input="onSingleInputChange"
          ></el-input>
        </template>

        <!-- 填空题的总体进度 -->
        <div class="answer-progress">
          <div class="progress-title">答题进度:</div>
          <el-progress
            :percentage="calculateProgress()"
            :format="(percentage: number) => `${percentage}%`"
            :status="getProgressStatus(calculateProgress())"
          />

          <!-- 填空项摘要 -->
          <div v-if="hasMultipleOptions" class="blank-summary">
            <div v-for="(answer, index) in answers" :key="index" class="blank-item-summary">
              <span class="blank-number">填空{{ index + 1 }}:</span>
              <el-tag size="small" :type="answer ? 'success' : 'info'">
                {{ answer ? '已填写' : '未填写' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import type { ProblemOption } from '@/api/interface/oj/problem/ptoblemSubmit';

defineOptions({
  name: 'FillBlankQuestion'
});

const props = defineProps<{
  options: ProblemOption[];
  modelValue: string[];
  singleValue?: string;
}>();

const emits = defineEmits<{
  (e: 'update:modelValue', value: string[]): void;
  (e: 'update:singleValue', value: string): void;
  (e: 'change', value: string[]): void;
}>();

// 判断是否有多个填空选项
const hasMultipleOptions = computed(() => props.options && props.options.length > 0);

// 本地答案数组
const answers = ref<string[]>(props.modelValue || []);

// 单个答案
const singleAnswer = ref(props.singleValue || '');

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  answers.value = newValue || [];
}, { deep: true });

watch(() => props.singleValue, (newValue) => {
  singleAnswer.value = newValue || '';
});

// 输入改变事件
const onInputChange = () => {
  emits('update:modelValue', answers.value);
  emits('change', answers.value);
};

// 单个输入框变更
const onSingleInputChange = () => {
  emits('update:singleValue', singleAnswer.value);
  emits('change', [singleAnswer.value]);
};

// 计算进度
const calculateProgress = () => {
  if (!hasMultipleOptions.value) {
    return singleAnswer.value ? 100 : 0;
  }

  const filledCount = answers.value.filter(answer => answer && answer.trim()).length;
  return Math.round((filledCount / props.options.length) * 100);
};

// 获取进度状态
const getProgressStatus = (percentage: number) => {
  if (percentage === 100) return 'success';
  if (percentage === 0) return 'exception';
  return 'warning';
};
</script>

<style scoped>
.fill-blank-question {
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

.fill-blank-container {
  padding: 10px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
}

.fill-blank-label {
  font-weight: 500;
  margin-bottom: 12px;
  color: var(--el-text-color-regular);
  padding-left: 8px;
  border-left: 3px solid var(--el-color-primary);
}

.fill-blank-item {
  margin-bottom: 15px;
  padding: 12px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  background-color: var(--el-bg-color);
}

.fill-blank-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.fill-blank-title {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.fill-blank-status {
  display: flex;
  align-items: center;
}

.fill-blank-input .el-textarea__inner {
  border: 1px solid var(--el-border-color);
  transition: all 0.3s ease;
  background-color: var(--el-fill-color-blank);
}

.fill-blank-input .el-textarea__inner:focus {
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

.blank-summary {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.blank-item-summary {
  padding: 5px 8px;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.blank-number {
  font-weight: 600;
  margin-right: 5px;
  color: var(--el-text-color-regular);
}
</style>
