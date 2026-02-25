<template>
  <div class="markdown-editor-container">
    <div v-if="!readonly" class="md-editor-custom-toolbar" :class="{ 'collapsed': !showToolbar }">
      <div class="toolbar-toggle" @click="toggleToolbar">
        <el-icon :size="12">
          <component :is="showToolbar ? 'ArrowUp' : 'ArrowDown'" />
        </el-icon>
        <span>{{ showToolbar ? '收起' : '展开' }}</span>
      </div>
      <div v-show="showToolbar" class="toolbar-content">
        <el-button-group>
          <el-button size="small" @click="exportMarkdown">导出 Markdown</el-button>
          <el-button size="small" @click="exportHtml">导出 HTML</el-button>
          <el-button size="small" @click="exportPdf">导出 PDF</el-button>
          <el-button size="small" @click="exportPng">导出 PNG</el-button>
        </el-button-group>
        <el-divider direction="vertical" />
        <el-dropdown @command="handleThemeChange">
          <el-button size="small">
            主题 <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="light">浅色主题</el-dropdown-item>
              <el-dropdown-item command="dark">深色主题</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-dropdown @command="handleCodeThemeChange">
          <el-button size="small">
            代码主题 <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="atom">Atom</el-dropdown-item>
              <el-dropdown-item command="a11y-dark">A11y Dark</el-dropdown-item>
              <el-dropdown-item command="github">GitHub</el-dropdown-item>
              <el-dropdown-item command="monokai">Monokai</el-dropdown-item>
              <el-dropdown-item command="vs2015">VS2015</el-dropdown-item>
              <el-dropdown-item command="vs">VS</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-dropdown @command="handlePreviewThemeChange">
          <el-button size="small">
            预览主题 <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="default">默认</el-dropdown-item>
              <el-dropdown-item command="github">GitHub</el-dropdown-item>
              <el-dropdown-item command="vuepress">VuePress</el-dropdown-item>
              <el-dropdown-item command="mk-cute">MK-Cute</el-dropdown-item>
              <el-dropdown-item command="smart-blue">Smart Blue</el-dropdown-item>
              <el-dropdown-item command="cyanosis">Cyanosis</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-divider direction="vertical" />
        <el-button
          size="small"
          :type="previewOnly ? 'primary' : 'default'"
          @click="togglePreviewOnly"
        >
          {{ previewOnly ? '退出预览' : '仅预览模式' }}
        </el-button>
      </div>
    </div>
    <MdEditor
      v-if="!previewOnly && !readonly"
      v-model="editorContent"
      :style="{ height: typeof props.height === 'number' ? `${props.height}px` : props.height }"
      :placeholder="placeholder"
      :toolbars="readonly ? [] : toolbars"
      :theme="theme"
      :code-theme="codeTheme"
      :preview-theme="previewTheme"
      :show-code-row-number="showCodeRowNumber"
      :tab-width="tabWidth"
      :footers="['markdownTotal', 'scrollSwitch']"
      @save="handleSave"
      @change="handleChange"
      @upload-img="handleImageUpload"
      @after-upload-img="handleUploadSuccess"
      @upload-img-fail="handleUploadError"
      @mounted="handleMounted"
      @html-preview="handleHtmlPreview"
    />
    <MdPreview
      v-if="readonly || previewOnly"
      :modelValue="editorContent"
      :theme="theme"
      :code-theme="codeTheme"
      :preview-theme="previewTheme"
      :sanitize="(html: string) => html"
      :style="{
        height: typeof props.height === 'number' ? `${props.height}px` : props.height,
        overflow: 'auto'
      }"
    />
  </div>
</template>

<script lang="ts">
import { ref, watch, defineComponent, computed, onMounted } from 'vue'
import type { PropType, DefineComponent } from 'vue'
// 导入md-editor-v3组件
import { MdEditor, MdCatalog, MdPreview } from 'md-editor-v3'
// 从md-editor-v3导入或自定义必要的类型
import 'md-editor-v3/lib/style.css'
import { uploadFile, uploadToOssWithPreSignedUrl } from '@/api/modules/system/admin/upload'
import { ElNotification, ElButton, ElButtonGroup, ElDropdown, ElDropdownMenu, ElDropdownItem, ElDivider, ElIcon } from 'element-plus'
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'
// @ts-ignore 忽略类型声明错误
import html2canvas from 'html2canvas'
// @ts-ignore 忽略类型声明错误
import { saveAs } from 'file-saver'

// 声明md-editor-v3的相关类型
type Themes = 'light' | 'dark'
type PreviewTheme = 'default' | 'github' | 'vuepress' | 'mk-cute' | 'smart-blue' | 'cyanosis'
type CodeTheme = 'atom' | 'a11y-dark' | 'github' | 'monokai' | 'vs2015' | 'vs'
// 定义工具栏项的类型
type ToolbarNames = 'bold' | 'underline' | 'italic' | 'strikeThrough' | 'title' | 'sub' | 'sup' | 'quote'
  | 'unorderedList' | 'orderedList' | 'task' | 'codeRow' | 'code' | 'link' | 'image' | 'table' | 'mermaid'
  | 'katex' | 'save' | 'pageFullscreen' | 'fullscreen' | 'preview' | 'htmlPreview' | 'catalog' | 'github'
  | 'export' | 'revoke' | 'next' | 'indent' | 'outdent' | '-';

// 分组工具栏的类型定义
type ToolbarGroup = {
  name: string;
  tools: ToolbarNames[];
}

// 导出PDF的相关工具函数
function exportToPdf(content: string, title: string = 'document') {
  try {
    // 创建一个隐藏的iframe用于转换
    const iframe = document.createElement('iframe');
    iframe.style.visibility = 'hidden';
    iframe.style.position = 'fixed';
    iframe.style.right = '0';
    iframe.style.bottom = '0';

    document.body.appendChild(iframe);

    // 获取iframe内容
    const iframedoc = iframe.contentDocument || iframe.contentWindow?.document;

    if (iframedoc) {
      // 使用基本的HTML渲染，不依赖组件实例
      const markdownHtml = `
        <div class="md-preview md-preview-theme-default">
          <div class="md-preview-wrapper">
            ${generateMarkdownHTML(content)}
          </div>
        </div>
      `;

      iframedoc.body.innerHTML = markdownHtml;

      // 添加样式
      const style = iframedoc.createElement('style');
      style.textContent = `
        body {
          font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
          padding: 20px;
          line-height: 1.5;
        }
        pre {
          background-color: #f6f8fa;
          border-radius: 4px;
          padding: 12px;
          overflow-x: auto;
        }
        code {
          font-family: 'Courier New', Courier, monospace;
          background-color: #f6f8fa;
          padding: 2px 4px;
          border-radius: 3px;
        }
        img {
          max-width: 100%;
        }
        table {
          border-collapse: collapse;
          width: 100%;
        }
        table, th, td {
          border: 1px solid #ddd;
        }
        th, td {
          padding: 8px;
          text-align: left;
        }
        th {
          background-color: #f2f2f2;
        }
      `;
      iframedoc.head.appendChild(style);

      // 等待图片加载完成
      setTimeout(() => {
        // 使用浏览器的打印功能导出PDF
        iframe.contentWindow?.print();

        // 清理
        setTimeout(() => {
          document.body.removeChild(iframe);
        }, 100);
      }, 500);
    }
  } catch (error) {
    console.error('导出PDF失败:', error);
    ElNotification({
      title: '导出失败',
      message: '导出PDF文件时发生错误，请重试!',
      type: 'error'
    });
  }
}

// 简单的Markdown到HTML转换函数
function generateMarkdownHTML(markdown: string): string {
  try {
    // 基本的Markdown转HTML处理
    let html = markdown
      // 处理标题
      .replace(/^# (.*$)/gm, '<h1>$1</h1>')
      .replace(/^## (.*$)/gm, '<h2>$1</h2>')
      .replace(/^### (.*$)/gm, '<h3>$1</h3>')
      .replace(/^#### (.*$)/gm, '<h4>$1</h4>')
      .replace(/^##### (.*$)/gm, '<h5>$1</h5>')
      .replace(/^###### (.*$)/gm, '<h6>$1</h6>')
      // 处理粗体和斜体
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      // 处理链接
      .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>')
      // 处理图片
      .replace(/!\[([^\]]+)\]\(([^)]+)\)/g, '<img src="$2" alt="$1">')
      // 处理代码块
      .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
      // 处理行内代码
      .replace(/`([^`]+)`/g, '<code>$1</code>')
      // 处理列表
      .replace(/^\* (.*$)/gm, '<ul><li>$1</li></ul>')
      .replace(/^- (.*$)/gm, '<ul><li>$1</li></ul>')
      .replace(/^\d\. (.*$)/gm, '<ol><li>$1</li></ol>')
      // 处理引用
      .replace(/^> (.*$)/gm, '<blockquote>$1</blockquote>')
      // 处理段落
      .replace(/\n\n/g, '</p><p>');

    return `<p>${html}</p>`;
  } catch (e) {
    console.error('Markdown渲染错误', e);
    return `<p>${markdown}</p>`;
  }
}

// 定义组件
export default defineComponent({
  name: 'MarkdownEditor',
  components: {
    MdEditor,
    MdPreview,
    MdCatalog,
    ElButton,
    ElButtonGroup,
    ElDropdown,
    ElDropdownMenu,
    ElDropdownItem,
    ElDivider,
    ElIcon,
    ArrowDown,
    ArrowUp
  },
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    height: {
      type: [String, Number],
      default: '400px'
    },
    placeholder: {
      type: String,
      default: '请输入内容...'
    },
    toolbar: {
      type: Array as PropType<ToolbarNames[]>,
      default: () => [] as ToolbarNames[]
    },
    readonly: {
      type: Boolean,
      default: false
    },
    mode: {
      type: String,
      default: 'ir' // 可选值: 'ir', 'wysiwyg', 'sv'
    },
    // 是否仅预览模式
    previewOnly: {
      type: Boolean,
      default: false
    },
    // 主题设置
    theme: {
      type: String as PropType<Themes>,
      default: 'light' // 可选值: 'light', 'dark'
    },
    // 代码块主题
    codeTheme: {
      type: String as PropType<CodeTheme>,
      default: 'atom' // 可用的代码主题
    },
    // 预览主题
    previewTheme: {
      type: String as PropType<PreviewTheme>,
      default: 'default' // 预览区域主题
    },
    // 是否显示代码行号
    showCodeRowNumber: {
      type: Boolean,
      default: true
    },
    // Tab键宽度
    tabWidth: {
      type: Number,
      default: 2
    },
    // 上传相关属性
    useOss: {
      type: Boolean,
      default: false
    },
    imgDir: {
      type: String,
      default: 'markdown'
    },
    fileDir: {
      type: String,
      default: 'markdown-files'
    },
    fileSize: {
      type: Number,
      default: 10 // 默认10MB
    },
    imgFileTypes: {
      type: Array as PropType<string[]>,
      default: () => ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
    },
    fileTypes: {
      type: Array as PropType<string[]>,
      default: () => ['.zip', '.rar', '.doc', '.docx', '.xls', '.xlsx', '.pdf', '.txt']
    },
    // 工具栏默认展开状态
    defaultExpandToolbar: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue', 'save', 'change', 'upload-success', 'upload-error', 'mounted', 'update:previewOnly'],
  setup(props, { emit, expose }) {
    const editorContent = ref(props.modelValue)
    const editorInstance = ref<any>(null)
    const mdEditor = ref<any>(null)

    // 主题状态
    const theme = ref<Themes>(props.theme)
    const codeTheme = ref<CodeTheme>(props.codeTheme)
    const previewTheme = ref<PreviewTheme>(props.previewTheme)

    // 仅预览模式状态
    const previewOnly = ref(props.previewOnly)

    // 工具栏展开状态
    const showToolbar = ref(props.defaultExpandToolbar)

    // 上传进度
    const uploadProgress = ref(0)

    // 保存的HTML预览内容
    const previewHtml = ref('')

    // 工具栏分组配置
    const toolbarGroups = computed<ToolbarGroup[]>(() => [
      {
        name: '历史',
        tools: ['revoke', 'next']
      },
      {
        name: '格式',
        tools: ['bold', 'underline', 'italic', 'strikeThrough', 'title', 'sub', 'sup']
      },
      {
        name: '列表',
        tools: ['quote', 'unorderedList', 'orderedList', 'task', 'indent', 'outdent']
      },
      {
        name: '插入',
        tools: ['link', 'image', 'table', 'mermaid', 'katex']
      },
      {
        name: '代码',
        tools: ['code', 'codeRow']
      },
      {
        name: '视图',
        tools: ['save', 'preview', 'htmlPreview', 'pageFullscreen', 'fullscreen', 'catalog']
      },
      {
        name: '扩展',
        tools: ['export']
      }
    ])

    // 计算属性：转换工具栏配置
    const toolbars = computed<ToolbarNames[]>(() => {
      if (props.readonly) return [];

      if (props.toolbar && props.toolbar.length > 0) {
        return props.toolbar;
      }

      // 使用分组定义组合工具栏
      const result: ToolbarNames[] = [];

      toolbarGroups.value.forEach((group, index) => {
        // 添加组内工具
        result.push(...group.tools);

        // 在分组之间添加分隔符（最后一组后不添加）
        if (index < toolbarGroups.value.length - 1) {
          result.push('-');
        }
      });

      return result;
    })

    // 处理HTML预览事件
    const handleHtmlPreview = (html: string) => {
      previewHtml.value = html
    }

    // 处理上一步操作
    const handleUndo = () => {
      if (editorInstance.value) {
        editorInstance.value.triggerCommand('undo')
      }
    }

    // 处理下一步操作
    const handleRedo = () => {
      if (editorInstance.value) {
        editorInstance.value.triggerCommand('redo')
      }
    }

    // 导出为Markdown
    const handleExportMarkdown = () => {
      const blob = new Blob([editorContent.value], { type: 'text/markdown;charset=utf-8' })
      saveAs(blob, 'markdown-document.md')
      ElNotification({
        title: '导出成功',
        message: '已导出为Markdown文件',
        type: 'success'
      })
    }

    // 导出为HTML
    const handleExportHtml = () => {
      // 使用预览HTML或者创建一个临时div来渲染markdown内容
      let html = previewHtml.value;

      if (!html) {
        // 获取预览内容
        const previewElement = document.querySelector('.md-editor-preview');
        if (previewElement) {
          html = previewElement.innerHTML;
        } else {
          // 如果没有预览元素，尝试先预览再获取内容
          editorInstance.value?.triggerCommand('preview');
          setTimeout(() => {
            const el = document.querySelector('.md-editor-preview');
            html = el?.innerHTML || '';

            // 导出HTML
            exportHtmlContent(html);
          }, 100);
          return;
        }
      }

      exportHtmlContent(html);
    }

    // 辅助函数 - 导出HTML内容
    const exportHtmlContent = (html: string) => {
      const fullHtml = `
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <title>Markdown文档</title>
          <style>
            body {
              font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
              padding: 20px;
              line-height: 1.5;
              max-width: 900px;
              margin: 0 auto;
            }
            pre {
              background-color: #f6f8fa;
              border-radius: 4px;
              padding: 12px;
              overflow-x: auto;
            }
            code {
              font-family: 'Courier New', Courier, monospace;
              background-color: #f6f8fa;
              padding: 2px 4px;
              border-radius: 3px;
            }
            img {
              max-width: 100%;
            }
            table {
              border-collapse: collapse;
              width: 100%;
            }
            table, th, td {
              border: 1px solid #ddd;
            }
            th, td {
              padding: 8px;
              text-align: left;
            }
            th {
              background-color: #f2f2f2;
            }
          </style>
        </head>
        <body>
          ${html}
        </body>
        </html>
      `;

      const blob = new Blob([fullHtml], { type: 'text/html;charset=utf-8' });
      saveAs(blob, 'markdown-document.html');
      ElNotification({
        title: '导出成功',
        message: '已导出为HTML文件',
        type: 'success'
      });
    }

    // 处理导出PDF
    const handleExportPdf = () => {
      exportToPdf(editorContent.value, 'markdown-document')
      ElNotification({
        title: '导出成功',
        message: '正在导出为PDF文件',
        type: 'success'
      })
    }

    // 导出为PNG
    const handleExportPng = async () => {
      try {
        // 获取预览容器
        const previewContainer = document.querySelector('.md-editor-preview') as HTMLElement
        if (!previewContainer) {
          // 如果找不到预览容器，先切换到预览模式
          editorInstance.value?.triggerCommand('preview')
          // 等待DOM更新
          setTimeout(async () => {
            const container = document.querySelector('.md-editor-preview') as HTMLElement
            if (container) {
              await exportToImage(container)
            } else {
              throw new Error('找不到预览容器')
            }
          }, 300)
        } else {
          await exportToImage(previewContainer)
        }
      } catch (error) {
        console.error('导出PNG失败:', error)
        ElNotification({
          title: '导出失败',
          message: '导出PNG文件时发生错误，请重试!',
          type: 'error'
        })
      }
    }

    // 导出为图片的辅助函数
    const exportToImage = async (element: HTMLElement) => {
      const canvas = await html2canvas(element, {
        useCORS: true,
        scale: 2,
        logging: false,
        backgroundColor: props.theme === 'dark' ? '#1e1e1e' : '#ffffff'
      })

      canvas.toBlob((blob: Blob | null) => {
        if (blob) {
          saveAs(blob, 'markdown-document.png')
          ElNotification({
            title: '导出成功',
            message: '已导出为PNG图片',
            type: 'success'
          })
        }
      })
    }

    // 检查文件是否符合要求
    const validateFile = (file: File, isImage = true) => {
      // 检查文件大小
      const fileSize = file.size / 1024 / 1024 < props.fileSize

      // 检查文件类型
      let fileType = false
      if (isImage) {
        fileType = props.imgFileTypes.includes(file.type)
      } else {
        // 对于非图片文件，检查文件扩展名
        const extension = '.' + file.name.split('.').pop()?.toLowerCase()
        fileType = props.fileTypes.includes(extension)
      }

      if (!fileType) {
        ElNotification({
          title: '温馨提示',
          message: `上传的${isImage ? '图片' : '文件'}格式不正确！`,
          type: 'warning'
        })
      }

      if (!fileSize) {
        ElNotification({
          title: '温馨提示',
          message: `上传的${isImage ? '图片' : '文件'}大小不能超过 ${props.fileSize}MB！`,
          type: 'warning'
        })
      }

      return fileType && fileSize
    }

    // 处理图片上传
    const handleImageUpload = async (files: File[], callback: (urls: string[]) => void) => {
      if (files.length === 0) return

      const uploadResults: string[] = []
      const promises: Promise<any>[] = []

      for (const file of files) {
        // 验证文件
        if (!validateFile(file, true)) continue

        // 根据上传模式选择上传方法
        const uploadPromise = props.useOss
          ? handleOssUpload(file, props.imgDir)
          : handleNormalUpload(file, props.imgDir)

        promises.push(
          uploadPromise.then(url => {
            if (url) uploadResults.push(url)
            return url
          })
        )
      }

      // 等待所有上传完成
      Promise.all(promises)
        .then(() => {
          if (uploadResults.length > 0) {
            callback(uploadResults)
            emit('upload-success', uploadResults)
          }
        })
        .catch(error => {
          console.error('上传失败', error)
          emit('upload-error', error)
        })
    }

    // 处理上传成功
    const handleUploadSuccess = (urls: string[]) => {
      emit('upload-success', urls)
    }

    // 处理上传失败
    const handleUploadError = (error: any) => {
      console.error('上传失败', error)
      emit('upload-error', error)
    }

    // 普通上传方式
    const handleNormalUpload = async (file: File, dir: string): Promise<string> => {
      try {
        // 转换为UploadRawFile类型
        const uploadRawFile = file as any;
        uploadRawFile.uid = Date.now().toString();

        const { data } = await uploadFile({ file: uploadRawFile, dirTag: dir })
        return data?.url || ''
      } catch (error) {
        console.error('上传失败', error)
        ElNotification({
          title: '温馨提示',
          message: '上传失败，请重试！',
          type: 'error'
        })
        return ''
      }
    }

    // OSS直传方式
    const handleOssUpload = async (file: File, dir: string): Promise<string> => {
      return new Promise<string>((resolve, reject) => {
        uploadToOssWithPreSignedUrl(
          file,
          file.name,
          dir,
          (percent) => {
            uploadProgress.value = percent
            // 可以在这里添加上传进度显示逻辑
          },
          (result) => {
            if (result) {
              resolve(result.url)
              ElNotification({
                title: '温馨提示',
                message: '上传成功！',
                type: 'success'
              })
            } else {
              resolve('')
            }
          },
          (error) => {
            console.error('上传失败', error)
            ElNotification({
              title: '温馨提示',
              message: '上传失败，请重试！',
              type: 'error'
            })
            reject(error)
          }
        )
      })
    }

    // 处理内容变化
    const handleChange = (content: string) => {
      editorContent.value = content
      emit('update:modelValue', content)
      emit('change', content)
    }

    // 处理保存
    const handleSave = (content: string) => {
      emit('save', content)
    }

    // 获取HTML内容
    const getHtml = () => {
      return previewHtml.value || MdEditor.MarkdownPreview(editorContent.value)
    }

    // 保存内容
    const saveContent = () => {
      emit('save', editorContent.value)
    }

    // 切换仅预览模式
    const togglePreviewOnly = () => {
      previewOnly.value = !previewOnly.value
      emit('update:previewOnly', previewOnly.value)

      ElNotification({
        title: '模式切换',
        message: previewOnly.value ? '已切换至仅预览模式' : '已退出仅预览模式',
        type: 'success'
      })
    }

    // 切换工具栏显示状态
    const toggleToolbar = () => {
      showToolbar.value = !showToolbar.value
    }

    // 监听modelValue的变化
    watch(
      () => props.modelValue,
      (newValue) => {
        if (newValue !== editorContent.value) {
          editorContent.value = newValue
        }
      }
    )

    // 监听previewOnly的变化
    watch(
      () => props.previewOnly,
      (newValue) => {
        if (newValue !== previewOnly.value) {
          previewOnly.value = newValue
        }
      }
    )

    // 导出相关方法
    const exportMarkdown = () => {
      handleExportMarkdown()
    }

    const exportHtml = () => {
      handleExportHtml()
    }

    const exportPdf = () => {
      handleExportPdf()
    }

    const exportPng = () => {
      handleExportPng()
    }

    // 主题切换处理函数
    const handleThemeChange = (command: Themes) => {
      theme.value = command
      ElNotification({
        title: '主题更新',
        message: '编辑器主题已更新',
        type: 'success'
      })
    }

    const handleCodeThemeChange = (command: CodeTheme) => {
      codeTheme.value = command
      ElNotification({
        title: '主题更新',
        message: '代码主题已更新',
        type: 'success'
      })
    }

    const handlePreviewThemeChange = (command: PreviewTheme) => {
      previewTheme.value = command
      ElNotification({
        title: '主题更新',
        message: '预览主题已更新',
        type: 'success'
      })
    }

    // 组件挂载完成后
    const handleMounted = (editor: any) => {
      // 保存编辑器实例
      editorInstance.value = editor

      // 添加自定义导出菜单
      if (editorInstance.value) {
        setTimeout(() => {
          const exportBtn = document.querySelector('.md-editor-toolbar-item[data-name="export"]') as HTMLElement;
          if (exportBtn) {
            const exportMenu = document.createElement('div');
            exportMenu.className = 'md-editor-export-menu';
            exportMenu.style.display = 'none';
            exportMenu.innerHTML = `
              <div class="md-editor-export-item" data-type="markdown">Markdown</div>
              <div class="md-editor-export-item" data-type="html">HTML</div>
              <div class="md-editor-export-item" data-type="pdf">PDF</div>
              <div class="md-editor-export-item" data-type="png">PNG</div>
            `;

            exportBtn.appendChild(exportMenu);

            // 点击导出按钮时显示菜单
            exportBtn.addEventListener('click', (e: Event) => {
              e.preventDefault();
              e.stopPropagation();
              const menu = exportBtn.querySelector('.md-editor-export-menu') as HTMLElement;
              if (menu) {
                menu.style.display = menu.style.display === 'none' ? 'block' : 'none';
              }
            });

            // 点击菜单项时触发导出功能
            const menuItems = exportMenu.querySelectorAll('.md-editor-export-item');
            menuItems.forEach(item => {
              item.addEventListener('click', (e: Event) => {
                e.preventDefault();
                e.stopPropagation();
                const type = (e.currentTarget as HTMLElement).getAttribute('data-type');
                if (type) {
                  exportMarkdown();
                }
                exportMenu.style.display = 'none';
              });
            });

            // 点击页面其他地方时隐藏菜单
            document.addEventListener('click', () => {
              exportMenu.style.display = 'none';
            });
          }
        }, 100);
      }

      emit('mounted');
    }

    // 暴露方法给父组件
    expose({
      saveContent,
      getHtml,
      getEditor: () => editorInstance.value,
      triggerCommand: (command: string) => {
        editorInstance.value?.triggerCommand(command)
      },
      exportMarkdown: handleExportMarkdown,
      exportHtml: handleExportHtml,
      exportPdf: handleExportPdf,
      exportPng: handleExportPng,
      undo: handleUndo,
      redo: handleRedo,
      setTheme: (newTheme: Themes) => {
        theme.value = newTheme
      },
      setCodeTheme: (newTheme: CodeTheme) => {
        codeTheme.value = newTheme
      },
      setPreviewTheme: (newTheme: PreviewTheme) => {
        previewTheme.value = newTheme
      },
      setPreviewOnly: (value: boolean) => {
        previewOnly.value = value
        emit('update:previewOnly', value)
      },
      togglePreviewOnly,
      toggleToolbar,
      setShowToolbar: (value: boolean) => {
        showToolbar.value = value
      },
    })

    return {
      editorContent,
      toolbars,
      mdEditor,
      theme,
      codeTheme,
      previewTheme,
      previewOnly,
      showToolbar,
      handleChange,
      handleSave,
      handleImageUpload,
      handleUploadSuccess,
      handleUploadError,
      handleUndo,
      handleRedo,
      handleExportMarkdown,
      handleExportHtml,
      handleExportPdf,
      handleExportPng,
      handleHtmlPreview,
      handleMounted,
      handleThemeChange,
      handleCodeThemeChange,
      handlePreviewThemeChange,
      exportMarkdown,
      exportHtml,
      exportPdf,
      exportPng,
      togglePreviewOnly,
      toggleToolbar,
      props,
    }
  }
}) as DefineComponent<any, any, any>
</script>

<style lang="scss" scoped>
.markdown-editor-container {
  width: 100%;
  overflow: hidden;
  border-radius: 4px;

  // 添加自定义工具栏样式
  .md-editor-custom-toolbar {
    display: flex;
    flex-direction: column;
    padding: 0;
    background-color: var(--el-bg-color-page);
    border-bottom: 1px solid var(--el-border-color-light);
    z-index: 10;

    .toolbar-toggle {
      display: flex;
      align-items: center;
      padding: 2px 8px;
      cursor: pointer;
      color: var(--el-text-color-secondary);
      transition: all 0.3s;
      font-size: 12px;
      line-height: 1;
      height: 22px;

      &:hover {
        color: var(--el-color-primary);
        background-color: var(--el-color-primary-light-9);
      }

      .el-icon {
        margin-right: 3px;
      }

      span {
        font-size: 11px;
      }
    }

    &.collapsed {
      border-bottom-color: transparent;
    }

    .toolbar-content {
      display: flex;
      align-items: center;
      padding: 8px 12px;

      .el-button-group {
        margin-right: 10px;
      }

      .el-dropdown {
        margin-left: 10px;
      }
    }
  }

  :deep(.md-editor) {
    box-sizing: border-box;
    width: 100%;
    min-height: 50px;

    // 自定义工具栏按钮
    .custom-toolbar-btn-wrapper {
      display: flex;
      align-items: center;
      padding: 0 8px;

      .md-toolbar-btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 6px 8px;
        margin: 0 4px;
        background: none;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        color: var(--el-text-color-regular);
        transition: all 0.2s;

        svg {
          fill: currentColor;
          margin-right: 4px;
        }

        &:hover {
          background-color: var(--el-color-primary-light-8);
          color: var(--el-color-primary);
        }
      }

      .md-toolbar-divider {
        width: 1px;
        height: 20px;
        background-color: var(--el-border-color-light);
        margin: 0 8px;
      }
    }

    // 美化工具栏
    .md-toolbar {
      padding: 0.5rem;
      background-color: var(--el-bg-color-page);
      border-bottom: 1px solid var(--el-border-color-light);

      button {
        margin: 0 2px;
        border-radius: 4px;

        &:hover {
          background-color: var(--el-color-primary-light-8);
        }
      }
    }

    // 美化编辑区域
    .md-editor-input-area {
      padding: 0.5rem;
      font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
      font-size: 14px !important;
      line-height: 1.6;
    }

    // 自定义导出菜单
    .md-editor-export-menu {
      position: absolute;
      z-index: 100;
      background-color: var(--el-bg-color);
      border-radius: 4px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      padding: 8px 0;
      min-width: 120px;

      .md-editor-export-item {
        padding: 8px 16px;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
          background-color: var(--el-color-primary-light-9);
          color: var(--el-color-primary);
        }
      }
    }
  }

  :deep(.md-editor-preview-wrapper), :deep(.md-preview) {
    color: var(--el-text-color-primary) !important;
    font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
    font-size: 14px !important;
    line-height: 1.5;
    padding: 16px;
    box-sizing: border-box;
    height: 100%;

    p {
      margin-top: 0.5em;
      margin-bottom: 0.5em;
    }

    pre {
      margin-top: 0.75em;
      margin-bottom: 0.75em;
      border-radius: 4px;
    }

    h1, h2, h3, h4, h5, h6 {
      margin-top: 1em;
      margin-bottom: 0.5em;
      font-weight: 600;
    }

    h1 {
      font-size: 1.75em;
      border-bottom: 1px solid var(--el-border-color-light);
      padding-bottom: 0.3em;
    }

    h2 {
      font-size: 1.5em;
      border-bottom: 1px solid var(--el-border-color-light);
      padding-bottom: 0.3em;
    }

    ul, ol {
      padding-left: 1.5em;
      margin-top: 0.5em;
      margin-bottom: 0.5em;
    }

    blockquote {
      padding: 0.5em 1em;
      border-left: 4px solid var(--el-color-primary-light-5);
      background-color: var(--el-color-primary-light-9);
      color: var(--el-text-color-regular);
      margin: 1em 0;
    }

    table {
      border-collapse: collapse;
      margin: 1em 0;

      th, td {
        border: 1px solid var(--el-border-color);
        padding: 6px 13px;
      }

      th {
        background-color: var(--el-color-primary-light-9);
      }

      tr:nth-child(2n) {
        background-color: var(--el-bg-color-page);
      }
    }

    img {
      max-width: 100%;
      border-radius: 4px;
    }

    code {
      padding: 0.2em 0.4em;
      margin: 0;
      font-size: 0.85em;
      background-color: var(--el-color-primary-light-9);
      border-radius: 3px;
    }

    hr {
      height: 1px;
      padding: 0;
      margin: 1.5em 0;
      background-color: var(--el-border-color-light);
      border: 0;
    }

    overflow-wrap: break-word;
    word-wrap: break-word;
  }

  // 暗色主题下的样式调整
  :deep(.md-editor-dark) {
    .md-toolbar {
      background-color: #252525;
      border-color: #333;
    }

    .custom-toolbar-btn-wrapper {
      .md-toolbar-btn {
        color: #e0e0e0;

        &:hover {
          background-color: #333;
          color: #42a5f5;
        }
      }

      .md-toolbar-divider {
        background-color: #444;
      }
    }

    .md-editor-export-menu {
      background-color: #252525;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.5);

      .md-editor-export-item {
        color: #e0e0e0;

        &:hover {
          background-color: #333;
          color: #42a5f5;
        }
      }
    }

    .md-editor-input-area {
      background-color: #1e1e1e;
      color: #e0e0e0;
    }

    .md-editor-preview {
      background-color: #1e1e1e;
      color: #e0e0e0;
    }

    .md-editor-footer {
      background-color: #252525;
      border-color: #333;
      color: #e0e0e0;
    }

    blockquote {
      background-color: #333;
      border-left-color: #555;
    }

    code {
      background-color: #333;
    }

    table {
      th, td {
        border-color: #444;
      }

      th {
        background-color: #333;
      }

      tr:nth-child(2n) {
        background-color: #2c2c2c;
      }
    }

    & + .md-editor-custom-toolbar {
      background-color: #252525;
      border-color: #333;

      .toolbar-toggle {
        color: #999;

        &:hover {
          color: #42a5f5;
          background-color: #333;
        }
      }

      .el-button {
        background-color: #333;
        border-color: #444;
        color: #e0e0e0;

        &:hover {
          background-color: #444;
          border-color: #555;
        }
      }

      .el-divider {
        background-color: #444;
      }
    }
  }
}
</style>
