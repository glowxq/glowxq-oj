<template>
  <div class="problem-header">
    <div class="problem-title-wrapper">
      <div class="problem-title">{{ problem?.title }}</div>
      <slot name="sceneType"></slot>
      <el-button type="text" @click="toggleInfo" class="info-toggle">
        {{ showInfo ? '收起' : '展开' }} <el-icon>
          <ArrowDown v-if="!showInfo" />
          <ArrowUp v-else />
        </el-icon>
      </el-button>
    </div>
    <div v-show="showInfo" class="problem-info">
      <!-- 难度等级标签 -->
      <EnumShow v-if="problem?.difficulty !== undefined"
        :enum="DifficultyLevel"
        :code="problem?.difficulty || 0"
        class="info-enum"
        :backgroundColor="getDifficultyTagColor(problem.difficulty)"
        :color="'#fff'"
      />

      <!-- 题目类型标签 -->
      <EnumShow
        :enum="ProblemType"
        :code="problem?.problemType || ''"
        class="info-enum"
        backgroundColor="#f0f2f5"
        color="#606266"
      />

      <!-- 编程类型标签 -->
      <EnumShow
        :enum="ProgramType"
        :code="problem?.programType || 0"
        class="info-enum"
        backgroundColor="#f0f2f5"
        color="#606266"
      />

      <!-- 显示标签列表 -->
      <slot name="tags"></slot>
      <span class="info-text" v-if="problem?.timeLimit">时间限制: {{ problem.timeLimit }}ms</span>
      <span class="info-text" v-if="problem?.memoryLimit">内存限制: {{ Math.floor((problem.memoryLimit || 0) / 1024)
        }}MB</span>
      <span class="info-text" v-if="problem?.author">作者: {{ problem.author }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import { ProblemType } from '@/enums/oj/problem';
import { ProgramType } from '@/enums/oj/problem/ProgramType';
import { DifficultyLevel } from '@/enums/oj/problem/DifficultyLevel';

defineOptions({
  name: 'ProblemHeader'
});

// 定义问题类型接口
interface Problem {
  id?: number;
  title?: string;
  problemType?: string;
  programType?: number;
  difficulty?: number;
  timeLimit?: number;
  memoryLimit?: number;
  author?: string;
  [key: string]: any;
}

const props = defineProps<{
  problem?: Problem;
  initialShowInfo?: boolean;
}>();

const showInfo = ref(props.initialShowInfo !== undefined ? props.initialShowInfo : true);

// 根据难度级别获取标签颜色
const getDifficultyTagColor = (difficulty: number) => {
  switch (difficulty) {
    case DifficultyLevel.ENTRY.code:
      return '#67c23a'; // 入门 - 绿色
    case DifficultyLevel.EASY.code:
      return '#67c23a'; // 简单 - 绿色
    case DifficultyLevel.MEDIUM.code:
      return '#e6a23c'; // 中等 - 黄色
    case DifficultyLevel.HARD.code:
      return '#f56c6c'; // 困难 - 红色
    case DifficultyLevel.EXPERT.code:
      return '#909399'; // 专家 - 灰色
    default:
      return '#909399'; // 默认灰色
  }
};

// 切换信息显示
const toggleInfo = () => {
  showInfo.value = !showInfo.value;
};
</script>

<style scoped>
.problem-header {
  border-bottom: 1px solid var(--el-border-color-light);
  padding-bottom: 5px;
  margin-bottom: 5px;
}

.problem-title-wrapper {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
  flex-wrap: nowrap;
}

.problem-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--el-color-primary);
  padding-left: 5px;
  border-left: 4px solid var(--el-color-primary);
  margin-right: auto;
}

.info-toggle {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.problem-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  padding: 8px 5px 0;
  transition: all 0.3s ease;
}

/* 移除info-tag样式，改用info-enum */
.info-enum {
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  display: inline-flex;
  align-items: center;
  height: 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  margin-right: 8px;
}

.info-text {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-right: 5px;
  background-color: var(--el-fill-color-light);
  padding: 2px 8px;
  border-radius: 4px;
}
</style>
