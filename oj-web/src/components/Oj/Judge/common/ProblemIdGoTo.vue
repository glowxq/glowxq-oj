<template>
  <div
    v-if="problemId"
    class="problem-id-goto"
    @click="goToProblem"
    :title="title || '点击跳转到做题页面'"
  >
    <div class="goto-link">
      <el-icon class="link-icon"><Link /></el-icon>
      <span class="problem-id-number">{{ problemId }}</span>
      <span class="goto-text">{{ buttonText || '前往做题' }}</span>
    </div>
  </div>
  <span v-else>-</span>
</template>

<script setup lang="ts">
import { Link } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

interface Props {
  /** 题目ID */
  problemId?: number | string;
  /** 按钮文本 */
  buttonText?: string;
  /** 提示文本 */
  title?: string;
}

const props = defineProps<Props>()
const router = useRouter()

// 跳转到做题页面
const goToProblem = () => {
  if (!props.problemId) return
  router.push(`/oj/problem/submit/${props.problemId}`)
}
</script>

<style scoped lang="scss">
.problem-id-goto {
  display: inline-block;
  cursor: pointer;
  position: relative;

  .goto-link {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
    border: 1px solid var(--el-color-primary-light-7);
    border-radius: 4px;
    padding: 4px 8px;
    font-family: 'Roboto Mono', monospace;
    font-size: 13px;
    font-weight: 500;
    transition: all 0.25s ease;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

    &:hover {
      background-color: var(--el-color-primary-light-8);
      transform: translateY(-2px);
      box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
    }

    .link-icon {
      font-size: 14px;
      color: var(--el-color-primary);
    }

    .problem-id-number {
      font-weight: 600;
    }

    .goto-text {
      font-size: 12px;
      margin-left: 4px;
      background-color: var(--el-color-primary);
      color: white;
      padding: 2px 6px;
      border-radius: 10px;
      transition: all 0.3s;
    }

    &:hover .goto-text {
      background-color: var(--el-color-primary-dark-2);
    }
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -3px;
    left: 0;
    width: 100%;
    height: 2px;
    background-color: var(--el-color-primary);
    opacity: 0;
    transition: all 0.3s;
  }

  &:hover::after {
    opacity: 1;
    bottom: -1px;
  }
}
</style>
