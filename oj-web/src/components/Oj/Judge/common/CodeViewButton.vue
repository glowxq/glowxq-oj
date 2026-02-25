<template>
  <div class="code-view-wrapper">
    <el-popover
      :width="800"
      trigger="click"
      popper-class="code-editor-popover"
    >
      <template #reference>
        <el-button type="primary" size="small" class="code-view-button">
          <el-icon><View /></el-icon>
          <span class="button-text">查看代码</span>
        </el-button>
      </template>
      <code-editor
        :model-value="code"
        :read-only-prop="true"
        :language="language || 'javascript'"
        style="height: 400px;"
      />
    </el-popover>
  </div>
</template>

<script setup lang="ts">
import { View } from '@element-plus/icons-vue'
import CodeEditor from '@/components/Common/CodeEditor/CodeEditor.vue'

interface Props {
  /** 代码内容 */
  code: string;
  /** 编程语言 */
  language?: string;
}

defineProps<Props>()
</script>

<style scoped lang="scss">
.code-view-wrapper {
  display: flex;
  justify-content: center;

  .button-text {
    margin-left: 4px;
  }

  :deep(.code-view-button) {
    color: white !important;
    background-color: var(--el-color-primary);
    border: none;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 13px;
    transition: all 0.3s;
    font-weight: 500;
    letter-spacing: 0.5px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    &:hover {
      background-color: var(--el-color-primary-light-3);
      transform: translateY(-1px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }
  }
}

:deep(.code-editor-popover) {
  max-width: 800px !important;
}
</style>
