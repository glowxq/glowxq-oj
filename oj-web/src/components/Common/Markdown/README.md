# Markdown 组件

基于 [md-editor-v3](https://imzbf.github.io/md-editor-v3) 的 Markdown 编辑器和预览组件。

## 主要功能

- 支持编辑和预览 Markdown 文本
- 多种主题：明亮主题和暗黑主题
- 支持图片、文件上传（支持直接上传和OSS上传）
- 支持导出为 HTML、PDF、PNG 等格式
- 支持 LaTeX 数学公式（通过 KaTeX）
- 支持 Mermaid 图表
- 支持配置工具栏
- 支持自定义样式

## MarkdownEditor 编辑器组件

### 基本用法

```vue
<template>
  <MarkdownEditor 
    v-model="content" 
    height="400px"
    @save="handleSave"
  />
</template>

<script setup>
import { ref } from 'vue'
import { MarkdownEditor } from '@/components/Markdown'

const content = ref('# Hello Markdown')

const handleSave = (value) => {
  console.log('保存内容:', value)
}
</script>
```

### 属性

| 属性名        | 类型              | 默认值     | 说明                           |
| ------------- | ----------------- | ---------- | ------------------------------ |
| modelValue    | String            | ''         | 编辑器内容，支持 v-model       |
| height        | String/Number     | '400px'    | 编辑器高度                     |
| placeholder   | String            | '请输入内容...' | 占位文字                   |
| toolbar       | Array             | (见代码)   | 工具栏按钮配置                 |
| readonly      | Boolean           | false      | 是否只读                       |
| mode          | String            | 'ir'       | 编辑器模式，可选: 'ir', 'wysiwyg', 'sv' |
| theme         | String            | 'light'    | 主题，可选: 'light', 'dark'    |
| useOss        | Boolean           | false      | 是否使用OSS上传                |
| imgDir        | String            | 'markdown' | 图片上传目录                   |
| fileDir       | String            | 'markdown-files' | 文件上传目录              |
| fileSize      | Number            | 10         | 上传文件大小限制（MB）         |
| imgFileTypes  | Array             | ['image/jpeg',...] | 允许上传的图片类型      |
| fileTypes     | Array             | ['.zip',...] | 允许上传的文件类型           |

### 事件

| 事件名          | 参数              | 说明                 |
| --------------- | ----------------- | -------------------- |
| update:modelValue | (value: string) | 内容更新时触发       |
| save            | (value: string)   | 点击保存按钮时触发   |
| change          | (value: string)   | 内容变化时触发       |
| upload-success  | (urls: string[])  | 上传成功时触发       |
| upload-error    | (error: any)      | 上传失败时触发       |
| mounted         | -                 | 组件挂载完成时触发   |

### 方法

通过 ref 获取组件实例，可以调用以下方法：

- `saveContent()`: 保存内容，触发 save 事件
- `getHtml()`: 获取 HTML 内容
- `getEditor()`: 获取编辑器实例

## MarkdownPreview 预览组件

### 基本用法

```vue
<template>
  <MarkdownPreview 
    :model-value="content" 
    theme="light"
    code-theme="atom"
  />
</template>

<script setup>
import { ref } from 'vue'
import { MarkdownPreview } from '@/components/Markdown'

const content = ref('# Hello Markdown')
</script>
```

### 属性

| 属性名            | 类型              | 默认值     | 说明                         |
| ----------------- | ----------------- | ---------- | ---------------------------- |
| modelValue        | String            | ''         | Markdown 内容                |
| height            | String/Number     | 'auto'     | 预览区域高度                 |
| theme             | String            | 'light'    | 主题，可选: 'light', 'dark'  |
| codeTheme         | String            | 'atom'     | 代码块主题                   |
| previewTheme      | String            | 'default'  | 预览区域主题                 |
| showCodeRowNumber | Boolean           | true       | 是否显示代码行号             |
| sanitize          | Boolean           | true       | 是否开启XSS防注入            |
| language          | String            | 'zh-CN'    | 语言                         |
| noKatex           | Boolean           | false      | 是否禁用KaTeX数学公式        |
| noMermaid         | Boolean           | false      | 是否禁用Mermaid图表          |

## 注意事项

1. 使用前确保已安装vditor依赖
2. 普通上传模式需要配置好后端上传接口
3. OSS直传模式需要确保后端接口能正确返回预签名URL
4. 如需自定义更多配置，可以参考[Vditor官方文档](https://github.com/Vanessa219/vditor) 