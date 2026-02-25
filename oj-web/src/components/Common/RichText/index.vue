<template>
  <div class="rich-text-editor-container" :style="containerStyle">
    <div class="editor-toolbar" v-if="!readonly">
      <div class="toolbar-top">
        <div class="toolbar-left">
          <el-button-group class="editor-actions">
            <el-button size="small" @click="exportHtml">导出 HTML</el-button>
            <el-button size="small" @click="exportJson">导出 JSON</el-button>
            <el-button size="small" @click="exportText">导出 文本</el-button>
          </el-button-group>

          <el-divider direction="vertical" />

          <el-dropdown @command="handleThemeChange">
            <el-button size="small">
              主题 <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="default">默认主题</el-dropdown-item>
                <el-dropdown-item command="dark">暗黑主题</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="toolbar-right">
          <el-button
            size="small"
            :type="previewMode ? 'primary' : 'default'"
            @click="togglePreviewMode"
          >
            {{ previewMode ? '退出预览' : '预览模式' }}
          </el-button>
        </div>
      </div>
    </div>

    <div class="editor-main" :class="{ 'preview-mode': previewMode || readonly }">
      <div v-show="!previewMode && !readonly">
        <Toolbar
          :editor="editorRef"
          :defaultConfig="toolbarConfig"
          :mode="mode"
          style="border-bottom: 1px solid #ccc"
        />
        <Editor
          v-model="valueHtml"
          :defaultConfig="editorConfig"
          :mode="mode"
          @onCreated="handleCreated"
          style="height: 380px"
        />
      </div>
      <div v-if="previewMode || readonly" class="editor-preview" v-html="valueHtml"></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onBeforeUnmount, watch, nextTick, shallowRef } from 'vue'
import { ElButton, ElButtonGroup, ElDropdown, ElDropdownMenu, ElDropdownItem, ElDivider, ElIcon, ElNotification, ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
// @ts-ignore
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile, uploadToOssWithPreSignedUrl } from '@/api/modules/system/admin/upload'
import { saveAs } from 'file-saver'

// 导入样式
import '@wangeditor/editor/dist/css/style.css'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: [String, Number],
    default: '400px'
  },
  minHeight: {
    type: [String, Number],
    default: '300px'
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  readonly: {
    type: Boolean,
    default: false
  },
  theme: {
    type: String,
    default: 'default'
  },
  previewOnly: {
    type: Boolean,
    default: false
  },
  fileSize: {
    type: Number,
    default: 10 // 默认10MB
  },
  imgDir: {
    type: String,
    default: 'editor'
  },
  useOss: {
    type: Boolean,
    default: true
  },
  autoFocus: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits([
  'update:modelValue',
  'change',
  'save',
  'upload-success',
  'upload-error',
  'mounted',
  'update:previewOnly'
])

// 编辑器实例
const editorRef = shallowRef()
const mode = ref('default')
const valueHtml = ref(props.modelValue)
const previewHtml = ref<string>('')
const previewMode = ref<boolean>(props.previewOnly)
const editorTheme = ref<string>(props.theme)
const uploadProgress = ref<number>(0)

// 容器样式
const containerStyle = computed(() => {
  const heightValue = typeof props.height === 'number' ? `${props.height}px` : props.height
  return {
    height: heightValue
  }
})

// 工具栏配置
const toolbarConfig = ref({})

// 编辑器配置
const editorConfig = ref({
  placeholder: props.placeholder,
  readOnly: props.readonly,
  autoFocus: props.autoFocus,
  MENU_CONF: {} // 先定义空对象，后面再添加配置
})

// 验证文件
const validateFile = (file: File, isImage: boolean = true): boolean => {
  const maxSize = isImage ? props.fileSize : props.fileSize * 5

  // 验证文件类型
  let fileType = true
  if (isImage) {
    fileType = /image\/\w+/.test(file.type)
  } else {
    fileType = /video\/\w+/.test(file.type)
  }

  // 验证文件大小
  const fileSize = file.size / (1024 * 1024) < maxSize

  if (!fileType) {
    ElNotification({
      title: '温馨提示',
      message: isImage ? '请上传图片文件！' : '请上传视频文件！',
      type: 'warning'
    })
  }

  if (!fileSize) {
    ElNotification({
      title: '温馨提示',
      message: `上传的${isImage ? '图片' : '视频'}大小不能超过 ${maxSize}MB！`,
      type: 'warning'
    })
  }

  return fileType && fileSize
}

// 普通上传方式
const handleNormalUpload = async (file: File, dir: string): Promise<string> => {
  try {
    // 转换为UploadRawFile类型
    const uploadRawFile = file as any;
    uploadRawFile.uid = Date.now().toString();

    const { data } = await uploadFile({ file: uploadRawFile, dirTag: dir })

    ElNotification({
      title: '温馨提示',
      message: '上传成功！',
      type: 'success'
    })

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

// 处理图片上传
const handleImageUpload = async (file: File, insertFn: (url: string, alt?: string, href?: string) => void) => {
  try {
    // 验证文件
    if (!validateFile(file, true)) return

    // 根据上传模式选择上传方法
    const url = props.useOss
      ? await handleOssUpload(file, props.imgDir)
      : await handleNormalUpload(file, props.imgDir)

    if (url) {
      // 插入到编辑器
      insertFn(url, file.name)
      emit('upload-success', [url])
    }
  } catch (error) {
    console.error('图片上传失败', error)
    emit('upload-error', error)
  }
}

// 处理视频上传
const handleVideoUpload = async (file: File, insertFn: (url: string, poster?: string) => void) => {
  try {
    // 验证文件
    if (!validateFile(file, false)) return

    // 根据上传模式选择上传方法
    const url = props.useOss
      ? await handleOssUpload(file, props.imgDir + '/video')
      : await handleNormalUpload(file, props.imgDir + '/video')

    if (url) {
      // 插入到编辑器
      insertFn(url)
      emit('upload-success', [url])
    }
  } catch (error) {
    console.error('视频上传失败', error)
    emit('upload-error', error)
  }
}

// 添加编辑器配置，放在函数定义后面
editorConfig.value.MENU_CONF = {
  uploadImage: {
    server: '/api/upload',
    fieldName: 'file',
    maxFileSize: props.fileSize * 1024 * 1024,
    maxNumberOfFiles: 10,
    allowedFileTypes: ['image/*'],
    meta: { dirTag: props.imgDir },
    timeout: 10 * 1000,
    customUpload: handleImageUpload,
    onBeforeUpload: (file: File) => {
      return validateFile(file, true)
    },
    onSuccess: (file: File, res: any) => {
      emit('upload-success', res)
    },
    onFailed: (file: File, res: any) => {
      emit('upload-error', res)
      ElMessage.error('图片上传失败')
    },
    onError: (file: File, err: any, res: any) => {
      emit('upload-error', err)
      ElMessage.error('图片上传错误: ' + err.message)
    }
  },
  uploadVideo: {
    server: '/api/upload',
    fieldName: 'file',
    maxFileSize: props.fileSize * 5 * 1024 * 1024, // 视频大小限制更大
    maxNumberOfFiles: 5,
    allowedFileTypes: ['video/*'],
    meta: { dirTag: props.imgDir + '/video' },
    timeout: 60 * 1000,
    customUpload: handleVideoUpload,
    onBeforeUpload: (file: File) => {
      return validateFile(file, false)
    },
    onSuccess: (file: File, res: any) => {
      emit('upload-success', res)
    },
    onFailed: (file: File, res: any) => {
      emit('upload-error', res)
      ElMessage.error('视频上传失败')
    },
    onError: (file: File, err: any, res: any) => {
      emit('upload-error', err)
      ElMessage.error('视频上传错误: ' + err.message)
    }
  }
}

// 切换预览模式
const togglePreviewMode = () => {
  previewMode.value = !previewMode.value
  emit('update:previewOnly', previewMode.value)

  ElNotification({
    title: '模式切换',
    message: previewMode.value ? '已切换至预览模式' : '已退出预览模式',
    type: 'success'
  })
}

// 处理主题切换
const handleThemeChange = (command: string) => {
  editorTheme.value = command

  // 设置编辑器的主题
  if (editorRef.value) {
    // 通过修改CSS变量方式改变主题，而不是调用setTheme方法
    if (command === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  ElNotification({
    title: '主题更新',
    message: '编辑器主题已更新',
    type: 'success'
  })
}

// 创建编辑器后的回调
const handleCreated = (editor: any) => {
  editorRef.value = editor
  emit('mounted', editor)

  // 监听内容变化
  editor.on('change', () => {
    valueHtml.value = editor.getHtml()
    emit('update:modelValue', valueHtml.value)
    emit('change', valueHtml.value)
  })
}

// 导出HTML内容
const exportHtml = () => {
  if (!editorRef.value) return

  const html = editorRef.value.getHtml()
  const blob = new Blob([html], { type: 'text/html;charset=utf-8' })
  saveAs(blob, `rich-text-${Date.now()}.html`)
}

// 导出JSON内容
const exportJson = () => {
  if (!editorRef.value) return

  const json = JSON.stringify(editorRef.value.children, null, 2)
  const blob = new Blob([json], { type: 'application/json;charset=utf-8' })
  saveAs(blob, `rich-text-${Date.now()}.json`)
}

// 导出纯文本内容
const exportText = () => {
  if (!editorRef.value) return

  const text = editorRef.value.getText()
  const blob = new Blob([text], { type: 'text/plain;charset=utf-8' })
  saveAs(blob, `rich-text-${Date.now()}.txt`)
}

// 监听props变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue !== valueHtml.value) {
      valueHtml.value = newValue
    }
  }
)

watch(
  () => valueHtml.value,
  (newValue) => {
    emit('update:modelValue', newValue)
  }
)

watch(
  () => props.previewOnly,
  (newValue) => {
    previewMode.value = newValue
  }
)

watch(
  () => props.readonly,
  (newValue) => {
    if (editorConfig.value) {
      editorConfig.value.readOnly = newValue
    }
  }
)

// 生命周期钩子
onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy()
  }
})

// 对外暴露方法
defineExpose({
  editor: editorRef,
  getHtml: () => editorRef.value?.getHtml() || '',
  getText: () => editorRef.value?.getText() || '',
  clear: () => editorRef.value?.clear(),
  isEmpty: () => editorRef.value?.isEmpty() || true,
  focus: () => editorRef.value?.focus(),
  blur: () => editorRef.value?.blur(),
  insertHtml: (html: string) => editorRef.value?.dangerouslyInsertHtml(html),
})
</script>

<style lang="scss" scoped>
.rich-text-editor-container {
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  background-color: var(--el-bg-color);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.03);

  &.dark {
    border-color: #333;
    background-color: #333;
  }

  .editor-toolbar {
    border-bottom: 1px solid #e4e7ed;
    background-color: var(--el-bg-color-overlay);
    padding: 8px 12px;

    .toolbar-top {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .toolbar-left, .toolbar-right {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }
  }

  .editor-main {
    flex: 1;
    overflow: hidden;
    position: relative;

    &.preview-mode .editor-preview {
      padding: 16px;
      overflow: auto;
      height: 100%;
    }
  }
}

// 暗黑模式适配
html.dark {
  .rich-text-editor-container {
    border-color: #444;

    .editor-toolbar {
      border-color: #444;
      background-color: #333;
    }
  }
}
</style>
