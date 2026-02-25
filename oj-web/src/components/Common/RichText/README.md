# RichText 富文本编辑器组件

基于 WangEditor V5 开发的富文本编辑器组件，UI设计参考Apple风格，功能齐全，使用简单。

## 功能特点

- 美观的UI界面，参考Apple设计风格
- 支持明/暗两种主题模式
- 完整的图片、视频上传功能
- 支持OSS和普通服务器上传
- 内容导出功能（HTML、JSON、纯文本）
- 预览模式和只读模式
- 响应式设计
- TypeScript支持

## 安装依赖

```bash
# 安装WangEditor V5
npm install @wangeditor/editor

# 安装file-saver（用于导出功能）
npm install file-saver
```

## 基本用法

```vue
<template>
  <RichText
    v-model="content"
    :height="500"
    @change="handleChange"
    @save="handleSave"
  />
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import RichText from '@/components/RichText'

const content = ref('<p>初始内容</p>')

const handleChange = (html: string) => {
  console.log('内容变化:', html)
}

const handleSave = (html: string) => {
  console.log('保存内容:', html)
}
</script>
```

## 组件属性

| 属性名 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| modelValue | String | '' | 编辑器内容，支持v-model双向绑定 |
| height | String/Number | '400px' | 编辑器高度 |
| minHeight | String/Number | '300px' | 编辑器最小高度 |
| placeholder | String | '请输入内容...' | 占位文本 |
| readonly | Boolean | false | 是否为只读模式 |
| theme | String | 'default' | 主题，可选值：'default'、'dark' |
| previewOnly | Boolean | false | 是否仅显示预览模式 |
| fileSize | Number | 10 | 图片上传大小限制（MB） |
| imgDir | String | 'editor' | 图片上传目录 |
| useOss | Boolean | true | 是否使用OSS上传 |
| autoFocus | Boolean | true | 是否自动聚焦 |

## 事件

| 事件名 | 参数 | 说明 |
| --- | --- | --- |
| update:modelValue | (html: string) | 内容更新时触发 |
| change | (html: string) | 内容变化时触发 |
| save | (html: string) | 保存内容时触发 |
| upload-success | (urls: string[]) | 文件上传成功时触发 |
| upload-error | (error: any) | 文件上传失败时触发 |
| mounted | (editor: IDomEditor) | 编辑器加载完成时触发 |
| update:previewOnly | (value: boolean) | 预览模式切换时触发 |

## 对外暴露的方法

| 方法名 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| getHtml | - | string | 获取HTML内容 |
| getText | - | string | 获取纯文本内容 |
| clear | - | void | 清空编辑器内容 |
| isEmpty | - | boolean | 判断编辑器是否为空 |
| insertHtml | (html: string) | void | 插入HTML内容 |
| undo | - | void | 撤销操作 |
| redo | - | void | 重做操作 |
| focus | - | void | 聚焦编辑器 |
| blur | - | void | 取消聚焦 |

## 图片上传

富文本编辑器支持两种上传模式：

1. 普通上传：通过服务端接口上传
2. OSS直传：通过OSS预签名URL上传到对象存储

默认使用OSS上传模式，可以通过`useOss`属性切换。

## 高级用法示例

```vue
<template>
  <div class="editor-container">
    <div class="toolbar">
      <el-button @click="toggleReadonly">{{ readonly ? '开启编辑' : '只读模式' }}</el-button>
      <el-button @click="togglePreviewOnly">{{ previewOnly ? '编辑模式' : '预览模式' }}</el-button>
      <el-button @click="insertContent">插入内容</el-button>
      <el-button @click="exportContent">导出内容</el-button>
    </div>
    
    <RichText
      v-model="content"
      :readonly="readonly"
      :preview-only="previewOnly"
      :height="500"
      theme="default"
      @change="handleChange"
      @save="handleSave"
      @upload-success="handleUploadSuccess"
      ref="richTextRef"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElButton, ElMessage } from 'element-plus'
import RichText from '@/components/RichText'

const content = ref('<p>初始内容</p>')
const readonly = ref(false)
const previewOnly = ref(false)
const richTextRef = ref(null)

// 切换只读状态
const toggleReadonly = () => {
  readonly.value = !readonly.value
}

// 切换预览模式
const togglePreviewOnly = () => {
  previewOnly.value = !previewOnly.value
}

// 插入内容
const insertContent = () => {
  if (richTextRef.value) {
    richTextRef.value.insertHtml('<p><strong>插入的内容</strong></p>')
  }
}

// 导出内容
const exportContent = () => {
  if (richTextRef.value) {
    const html = richTextRef.value.getHtml()
    const text = richTextRef.value.getText()
    console.log('HTML内容:', html)
    console.log('纯文本内容:', text)
    ElMessage.success('内容已导出到控制台')
  }
}

// 内容变化处理
const handleChange = (html) => {
  console.log('内容变化:', html)
}

// 保存处理
const handleSave = (html) => {
  console.log('保存内容:', html)
  ElMessage.success('内容已保存')
}

// 上传成功处理
const handleUploadSuccess = (urls) => {
  console.log('上传成功:', urls)
  ElMessage.success('文件上传成功')
}
</script>
```

## 注意事项

1. 需要先安装WangEditor V5和file-saver依赖
2. 上传接口需要自行实现，组件中的示例仅供参考
3. 暗黑模式需要配合Element Plus的暗黑模式使用
4. 图片上传大小限制默认为10MB，可通过fileSize属性调整
5. 视频上传大小限制默认为图片大小的5倍

## 效果预览

请查看 `demo.vue` 文件体验完整功能。 