<template>
  <div class="markdown-demo">
    <h1>Markdown编辑器示例</h1>
    
    <div class="config-section">
      <h2>配置选项</h2>
      <div class="config-items">
        <el-switch v-model="useOss" active-text="OSS直传" inactive-text="普通上传" />
        <el-select v-model="mode" placeholder="编辑器模式">
          <el-option label="即时渲染" value="ir" />
          <el-option label="所见即所得" value="wysiwyg" />
          <el-option label="分屏预览" value="sv" />
        </el-select>
      </div>
    </div>
    
    <div class="editor-container">
      <h2>编辑器</h2>
      <MarkdownEditor 
        v-model="content" 
        :height="400" 
        ref="markdownRef"
        :mode="mode"
        :use-oss="useOss"
        img-dir="md-images"
        file-dir="md-files"
        @upload-success="handleUploadSuccess"
        @upload-error="handleUploadError"
      />
    </div>
    
    <div class="buttons">
      <el-button type="primary" @click="saveContent">保存内容</el-button>
      <el-button @click="previewHtml">预览HTML</el-button>
    </div>
    
    <div v-if="uploadFiles.length > 0" class="uploaded-files">
      <h2>已上传文件</h2>
      <ul>
        <li v-for="(file, index) in uploadFiles" :key="index">
          <el-link :href="file" target="_blank" type="primary">{{ getFileName(file) }}</el-link>
        </li>
      </ul>
    </div>
    
    <div v-if="htmlPreview" class="preview-container">
      <h2>HTML预览</h2>
      <div class="html-preview" v-html="htmlPreview"></div>
    </div>
    
    <div class="content-display">
      <h2>Markdown内容</h2>
      <pre>{{ content }}</pre>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElButton, ElSelect, ElOption, ElSwitch, ElLink } from 'element-plus'
import MarkdownEditor from './index.vue'

const content = ref(`# 欢迎使用Markdown编辑器

这是一个基于**Vditor**的Markdown编辑器示例。

## 功能特点

- 支持图片和文件上传（普通上传和OSS直传）
- 支持实时预览
- 支持代码高亮
- 支持表格编辑
- 支持数学公式

> 点击工具栏中的上传按钮或拖拽文件到编辑器中进行上传。`)

const htmlPreview = ref('')
const markdownRef = ref()
const useOss = ref(false)
const mode = ref('ir')
const uploadFiles = ref<string[]>([])

const saveContent = () => {
  ElMessage.success('内容已保存：' + content.value.substring(0, 20) + '...')
}

const previewHtml = () => {
  htmlPreview.value = markdownRef.value.getHtml()
}

const handleUploadSuccess = (urls: string[]) => {
  ElMessage.success(`成功上传 ${urls.length} 个文件`)
  uploadFiles.value = [...uploadFiles.value, ...urls]
}

const handleUploadError = (error: any) => {
  ElMessage.error(`文件上传失败: ${error.message || '未知错误'}`)
}

const getFileName = (url: string) => {
  try {
    const urlObj = new URL(url)
    const pathParts = urlObj.pathname.split('/')
    return pathParts[pathParts.length - 1]
  } catch (e) {
    const parts = url.split('/')
    return parts[parts.length - 1]
  }
}
</script>

<style lang="scss" scoped>
.markdown-demo {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  
  h1 {
    margin-bottom: 20px;
    text-align: center;
  }
  
  h2 {
    margin-bottom: 10px;
    font-size: 18px;
  }
  
  .config-section {
    margin-bottom: 20px;
    padding: 15px;
    border: 1px solid #eee;
    border-radius: 4px;
    
    .config-items {
      display: flex;
      gap: 20px;
      align-items: center;
    }
  }
  
  .editor-container {
    margin-bottom: 20px;
  }
  
  .buttons {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
  }
  
  .uploaded-files {
    margin-bottom: 20px;
    border: 1px solid #eee;
    padding: 15px;
    border-radius: 4px;
    
    ul {
      list-style: none;
      padding: 0;
      
      li {
        margin-bottom: 8px;
      }
    }
  }
  
  .preview-container {
    margin-bottom: 20px;
    border: 1px solid #eee;
    padding: 15px;
    border-radius: 4px;
    
    .html-preview {
      background-color: #fafafa;
      padding: 15px;
      border-radius: 4px;
    }
  }
  
  .content-display {
    pre {
      background-color: #f5f5f5;
      padding: 15px;
      border-radius: 4px;
      overflow: auto;
      white-space: pre-wrap;
      word-break: break-all;
    }
  }
}
</style> 