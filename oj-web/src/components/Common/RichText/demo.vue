<template>
  <div class="rich-text-demo">
    <h2>富文本编辑器演示</h2>
    
    <div class="control-panel">
      <el-button @click="toggleReadonly">{{ readonly ? '开启编辑' : '只读模式' }}</el-button>
      <el-button @click="togglePreviewOnly">{{ previewOnly ? '编辑模式' : '预览模式' }}</el-button>
      <el-button @click="insertHtml">插入HTML</el-button>
      <el-button @click="clearContent">清空内容</el-button>
      <el-button @click="saveContent">保存内容</el-button>
    </div>
    
    <div class="editor-wrapper">
      <RichText
        v-model="content"
        :readonly="readonly"
        :preview-only="previewOnly"
        :height="500"
        :file-size="5"
        img-dir="rich-text-demo"
        :use-oss="true"
        @change="handleChange"
        @save="handleSave"
        @upload-success="handleUploadSuccess"
        @upload-error="handleUploadError"
        ref="richTextRef"
      />
    </div>
    
    <div class="content-preview">
      <h3>编辑器内容：</h3>
      <div class="html-preview">
        <pre>{{ content }}</pre>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElButton, ElMessage } from 'element-plus'
import RichText from './index.vue'

// 定义状态变量
const content = ref('<p>这是一个基于WangEditor V5的富文本编辑器演示</p><p>你可以试试它的各种功能！</p>')
const readonly = ref(false)
const previewOnly = ref(false)
const richTextRef = ref<InstanceType<typeof RichText> | null>(null)

// 切换只读状态
const toggleReadonly = () => {
  readonly.value = !readonly.value
  ElMessage.success(readonly.value ? '已切换到只读模式' : '已切换到编辑模式')
}

// 切换预览模式
const togglePreviewOnly = () => {
  previewOnly.value = !previewOnly.value
  ElMessage.success(previewOnly.value ? '已切换到预览模式' : '已切换到编辑模式')
}

// 插入HTML内容
const insertHtml = () => {
  if (richTextRef.value) {
    richTextRef.value.insertHtml('<p style="color: red;">这是插入的<strong>HTML</strong>内容</p>')
    ElMessage.success('已插入HTML内容')
  }
}

// 清空内容
const clearContent = () => {
  if (richTextRef.value) {
    richTextRef.value.clear()
    ElMessage.success('已清空编辑器内容')
  }
}

// 保存内容
const saveContent = () => {
  if (richTextRef.value) {
    const html = richTextRef.value.getHtml()
    const text = richTextRef.value.getText()
    console.log('保存HTML:', html)
    console.log('保存纯文本:', text)
    ElMessage.success('内容已保存到控制台')
  }
}

// 编辑器内容变化事件
const handleChange = (html: string) => {
  console.log('内容变化:', html)
}

// 编辑器保存事件
const handleSave = (html: string) => {
  console.log('保存内容:', html)
  ElMessage.success('内容已保存')
}

// 上传成功事件
const handleUploadSuccess = (urls: string[]) => {
  console.log('上传成功:', urls)
  ElMessage.success(`成功上传了 ${urls.length} 个文件`)
}

// 上传失败事件
const handleUploadError = (error: any) => {
  console.error('上传失败:', error)
  ElMessage.error('文件上传失败')
}
</script>

<style lang="scss" scoped>
.rich-text-demo {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  
  h2 {
    margin-bottom: 20px;
    text-align: center;
    font-weight: 500;
  }
  
  .control-panel {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }
  
  .editor-wrapper {
    margin-bottom: 30px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  }
  
  .content-preview {
    margin-top: 30px;
    
    h3 {
      margin-bottom: 10px;
      font-weight: 500;
    }
    
    .html-preview {
      background-color: #f7f7f7;
      border-radius: 8px;
      padding: 15px;
      overflow: auto;
      max-height: 300px;
      
      pre {
        white-space: pre-wrap;
        word-break: break-all;
        font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
        font-size: 14px;
        line-height: 1.5;
      }
    }
  }
}

// 暗黑模式适配
html.dark {
  .rich-text-demo {
    .content-preview {
      .html-preview {
        background-color: #333;
        
        pre {
          color: #ddd;
        }
      }
    }
  }
}
</style> 