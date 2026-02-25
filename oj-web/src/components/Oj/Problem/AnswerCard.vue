<template>
  <div class="objective-answer-card">
    <div class="card-header">
      <h3>答题卡</h3>
      <div class="timer-display" v-if="showTimer">
        <el-icon><Timer /></el-icon>
        <span>{{ formatTime(elapsedTime) }}</span>
      </div>
    </div>

    <!-- 单选多选答题卡 -->
    <template v-if="problemType === 'single' || problemType === 'multiple'">
      <div class="option-indicators">
        <div class="indicator-title">您的选择:</div>
        <div class="option-buttons">
          <el-tag
            v-for="option in options"
            :key="option.optionKey"
            :type="selectedOptions.includes(option.optionKey || '') ? 'primary' : 'info'"
            class="option-tag"
            @click="toggleOption(option.optionKey)"
          >
            {{ option.optionKey }}
          </el-tag>
        </div>
      </div>
      <!-- 选择题答题进度 -->
      <div class="answer-progress">
        <div class="progress-title">答题进度:</div>
        <el-progress
          :percentage="selectedOptions.length > 0 ? 100 : 0"
          :format="(percentage: number) => `${percentage}%`"
          :status="selectedOptions.length > 0 ? 'success' : 'exception'"
        />
      </div>
    </template>

    <!-- 判断题答题卡 -->
    <template v-else-if="problemType === 'truefalse'">
      <div class="option-indicators">
        <div class="indicator-title">您的选择:</div>
        <div class="tf-buttons">
          <el-button
            :type="selectedOptions.includes('A') ? 'success' : 'default'"
            @click="selectTrueFalseOption('A')"
            class="tf-button"
          >
            <el-icon><Check /></el-icon> 正确
          </el-button>
          <el-button
            :type="selectedOptions.includes('B') ? 'danger' : 'default'"
            @click="selectTrueFalseOption('B')"
            class="tf-button"
          >
            <el-icon><Close /></el-icon> 错误
          </el-button>
        </div>
      </div>
      <!-- 判断题答题进度 -->
      <div class="answer-progress">
        <div class="progress-title">答题进度:</div>
        <el-progress
          :percentage="selectedOptions.length > 0 ? 100 : 0"
          :format="(percentage: number) => `${percentage}%`"
          :status="selectedOptions.length > 0 ? 'success' : 'exception'"
        />
      </div>
    </template>

    <!-- 填空题答题状态 -->
    <template v-else-if="problemType === 'fillblank'">
      <div class="answer-status">
        <div class="indicator-title">答题状态:</div>
        <el-progress
          :percentage="blankProgress"
          :format="(percentage: number) => `${percentage}%`"
          :status="getProgressStatus(blankProgress)"
        />
        <div class="blank-summary" v-if="actualBlankAnswers.length > 0">
          <div v-for="(answer, index) in actualBlankAnswers" :key="index" class="blank-item-summary">
            <span class="blank-number">填空{{ index + 1 }}:</span>
            <el-tag size="small" :type="answer ? 'success' : 'info'">
              {{ answer ? '已填写' : '未填写' }}
            </el-tag>
          </div>
        </div>
      </div>
    </template>

    <!-- 简答题答题状态 -->
    <template v-else-if="problemType === 'shortanswer'">
      <div class="answer-status">
        <div class="indicator-title">答题状态:</div>
        <el-progress
          :percentage="shortAnswerProgress"
          :format="(percentage: number) => `${percentage}%`"
          :status="shortAnswer ? 'success' : 'exception'"
        />
        <div class="status-info">
          <div class="status-text">{{ shortAnswer ? '已作答' : '未作答' }}</div>
          <div class="word-count" v-if="shortAnswer">字数: {{ shortAnswer.length }}</div>
        </div>
      </div>
    </template>

    <!-- 题目统计信息 -->
    <div class="problem-stats" v-if="showStats">
      <h4>题目统计</h4>
      <div class="stats-row">
        <div class="stats-item">
          <div class="stats-icon"><el-icon><User /></el-icon></div>
          <div class="stats-info">
            <div class="stats-label">解答人数</div>
            <div class="stats-value">{{ formatNumber(actualStats.totalSubmissions) }}</div>
          </div>
        </div>
        <div class="stats-item">
          <div class="stats-icon"><el-icon><Check /></el-icon></div>
          <div class="stats-info">
            <div class="stats-label">通过率</div>
            <div class="stats-value">{{ actualStats.passRate }}%</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 提示和帮助 -->
    <div class="problem-tips">
      <h4>答题提示</h4>
      <ul class="tips-list">
        <li v-if="problemType === 'single'">
          <el-icon><InfoFilled /></el-icon> 单选题请选择一个最佳答案
        </li>
        <li v-if="problemType === 'multiple'">
          <el-icon><InfoFilled /></el-icon> 多选题可选择多个正确答案
        </li>
        <li v-if="problemType === 'truefalse'">
          <el-icon><InfoFilled /></el-icon> 判断题请选择"正确"或"错误"
        </li>
        <li v-if="problemType === 'fillblank'">
          <el-icon><InfoFilled /></el-icon> 填空题请在文本框中填写答案
        </li>
        <li v-if="problemType === 'shortanswer'">
          <el-icon><InfoFilled /></el-icon> 简答题请详细阐述您的答案
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { Timer, InfoFilled, User, Check, Close, Star } from '@element-plus/icons-vue';
import type { ProblemOption } from '@/api/interface/oj/problem/ptoblemSubmit';

defineOptions({
  name: 'AnswerCard'
});

const props = defineProps<{
  problemType: 'single' | 'multiple' | 'truefalse' | 'fillblank' | 'shortanswer'; // 题目类型
  options?: ProblemOption[]; // 选项数据
  selectedOptions: string[]; // 选中的选项
  blankAnswers?: string[]; // 填空题答案
  shortAnswer?: string; // 简答题答案
  elapsedTime: number; // 已用时间
  showTimer?: boolean; // 是否显示计时器
  showStats?: boolean; // 是否显示统计信息
  stats?: { // 统计数据
    totalSubmissions: number;
    passRate: number;
  };
}>();

const emits = defineEmits<{
  (e: 'update:selectedOptions', value: string[]): void;
}>();

// 提供默认值
const defaultStats = {
  totalSubmissions: 0,
  passRate: 0
};

// 计算属性
const actualStats = computed(() => props.stats || defaultStats);
const actualBlankAnswers = computed(() => props.blankAnswers || []);

// 填空题进度计算
const blankProgress = computed(() => {
  if (!props.blankAnswers || props.blankAnswers.length === 0) return 0;

  const filledCount = props.blankAnswers.filter(answer => answer && answer.trim()).length;
  return Math.round((filledCount / props.blankAnswers.length) * 100);
});

// 简答题进度计算
const shortAnswerProgress = computed(() => {
  if (!props.shortAnswer) return 0;
  return Math.min(Math.round((props.shortAnswer.length / 200) * 100), 100);
});

// 选择题选项切换
const toggleOption = (optionKey: string | undefined) => {
  if (!optionKey) return;

  const newSelected = [...props.selectedOptions];

  // 单选题逻辑
  if (props.problemType === 'single') {
    emits('update:selectedOptions', [optionKey]);
  }
  // 多选题逻辑
  else if (props.problemType === 'multiple') {
    const index = newSelected.indexOf(optionKey);
    if (index >= 0) {
      newSelected.splice(index, 1);
    } else {
      newSelected.push(optionKey);
    }
    emits('update:selectedOptions', newSelected);
  }
};

// 判断题选项选择
const selectTrueFalseOption = (value: string) => {
  emits('update:selectedOptions', [value]);
};

// 获取进度状态
const getProgressStatus = (percentage: number) => {
  if (percentage === 100) return 'success';
  if (percentage === 0) return 'exception';
  return 'warning';
};

// 格式化数字，添加千位分隔符
const formatNumber = (num: number): string => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

// 格式化时间
const formatTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};
</script>

<style scoped>
.objective-answer-card {
  padding: 20px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  color: var(--el-color-primary);
  font-size: 18px;
}

.timer-display {
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: var(--el-fill-color-light);
  padding: 4px 10px;
  border-radius: 16px;
}

.timer-display .el-icon {
  font-size: 16px;
  color: var(--el-color-primary);
}

.timer-display span {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.option-indicators {
  margin-bottom: 20px;
}

.indicator-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.option-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-tag {
  cursor: pointer;
  padding: 6px 14px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.option-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tf-buttons {
  display: flex;
  gap: 10px;
}

.tf-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.tf-button .el-icon {
  font-size: 16px;
}

.answer-progress,
.answer-status {
  margin-bottom: 20px;
}

.answer-progress .progress-title,
.answer-status .indicator-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.status-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.status-text {
  font-weight: 500;
}

.word-count {
  font-weight: 500;
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

.problem-stats {
  margin-bottom: 20px;
  padding: 15px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
}

.problem-stats h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: var(--el-color-primary);
  font-size: 16px;
}

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.stats-item {
  display: flex;
  align-items: center;
}

.stats-icon {
  margin-right: 10px;
  color: var(--el-color-primary);
}

.stats-info {
  display: flex;
  flex-direction: column;
}

.stats-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.stats-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.problem-tips {
  padding: 15px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
}

.problem-tips h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: var(--el-color-primary);
  font-size: 16px;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-regular);
}

.tips-list li .el-icon {
  color: var(--el-color-info);
}

.tips-list li:last-child {
  margin-bottom: 0;
}
</style>
