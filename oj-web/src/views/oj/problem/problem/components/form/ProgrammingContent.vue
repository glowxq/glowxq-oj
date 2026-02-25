<template>
  <!-- 编程题内容表单 -->
  <div class="programming-content-form">
    <!-- 输入描述 -->
    <el-form-item label="输入描述" prop="problemBo.input">
      <div class="markdown-editor-wrapper">
        <MarkdownEditor
          v-model="modelValue.problemBo.input"
          placeholder="请填写输入描述"
          :height="250"
          :use-oss="true"
          img-dir="problem-input"
          key="problem-input-editor"
          :default-expand-toolbar="false"
          @upload-success="handleUploadSuccess"
          @upload-error="handleUploadError"
        />
      </div>
    </el-form-item>

    <!-- 输出描述 -->
    <el-form-item label="输出描述" prop="problemBo.output">
      <div class="markdown-editor-wrapper">
        <MarkdownEditor
          v-model="modelValue.problemBo.output"
          placeholder="请填写输出描述"
          :height="250"
          :use-oss="true"
          img-dir="problem-output"
          key="problem-output-editor"
          :default-expand-toolbar="false"
          @upload-success="handleUploadSuccess"
          @upload-error="handleUploadError"
        />
      </div>
    </el-form-item>

    <el-form-item label="题面样例" prop="problemBo.examples">
      <div class="markdown-editor-wrapper">
        <MarkdownEditor
          v-model="modelValue.problemBo.examples"
          placeholder="请填写题面样例"
          :height="250"
          :use-oss="true"
          img-dir="problem-examples"
          key="problem-examples-editor"
          :default-expand-toolbar="false"
          @upload-success="handleUploadSuccess"
          @upload-error="handleUploadError"
        />
      </div>
    </el-form-item>

    <el-form-item label="提示/备注" prop="problemBo.hint">
      <div class="markdown-editor-wrapper">
        <MarkdownEditor
          v-model="modelValue.problemBo.hint"
          placeholder="请填写提示或备注"
          :height="250"
          :use-oss="true"
          img-dir="problem-hint"
          key="problem-hint-editor"
          :default-expand-toolbar="false"
          @upload-success="handleUploadSuccess"
          @upload-error="handleUploadError"
        />
      </div>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import MarkdownEditor from '@/components/Common/Markdown/index.vue'
import type { ProblemData } from '../types'

defineOptions({
  name: 'ProgrammingContent'
})

const props = defineProps<{
  modelValue: ProblemData
}>()

const emit = defineEmits(['uploadSuccess', 'uploadError'])

/**
 * 处理文件上传成功回调
 * @param urls 上传成功的文件URL列表
 */
const handleUploadSuccess = (urls: string[]) => {
  emit('uploadSuccess', urls)
}

/**
 * 处理文件上传失败回调
 * @param error 上传失败的错误信息
 */
const handleUploadError = (error: any) => {
  emit('uploadError', error)
}
</script>

<style scoped lang="scss">
.programming-content-form {
  padding: 16px;
}

.markdown-editor-wrapper {
  width: 100%;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  overflow: hidden;
  min-height: 250px;
  height: auto;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 0;

  &:hover {
    border-color: var(--el-color-primary);
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
  }

  :deep(.markdown-editor-container) {
    width: 100%;
    height: 100%;
    border: none;

    .markdown-editor {
      min-height: 250px;
    }

    .md-editor {
      min-height: 250px;
    }
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}
</style>
