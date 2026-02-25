<template>
    <div class="markdown-editor-container">
      <div ref="editorRef" class="markdown-editor"></div>
    </div>
  </template>

  <script lang="ts">
  import { ref, onMounted, onBeforeUnmount, watch, nextTick, defineComponent } from 'vue'
  import Vditor from 'vditor'
  import 'vditor/dist/index.css'
  import { uploadFile, uploadToOssWithPreSignedUrl } from '@/api/modules/system/admin/upload'
  import { ElNotification } from 'element-plus'
  import type { IUploadResult } from '@/api/interface/system/admin/upload'

  export default defineComponent({
    name: 'MarkdownEditor',
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
        type: Array,
        default: () => [
          'emoji',
          'headings',
          'bold',
          'italic',
          'strike',
          'link',
          '|',
          'list',
          'ordered-list',
          'check',
          'outdent',
          'indent',
          '|',
          'quote',
          'line',
          'code',
          'inline-code',
          'insert-before',
          'insert-after',
          '|',
          'upload',
          'table',
          '|',
          'undo',
          'redo',
          '|',
          'fullscreen',
          'preview',
          'outline',
          'code-theme',
          'content-theme',
          'export'
        ]
      },
      readonly: {
        type: Boolean,
        default: false
      },
      mode: {
        type: String,
        default: 'ir' // 可选值: 'ir', 'wysiwyg', 'sv'
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
        type: Array,
        default: () => ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
      },
      fileTypes: {
        type: Array,
        default: () => ['.zip', '.rar', '.doc', '.docx', '.xls', '.xlsx', '.pdf', '.txt']
      }
    },
    emits: ['update:modelValue', 'save', 'change', 'upload-success', 'upload-error', 'mounted'],
    setup(props, { emit, expose }) {
      const editorRef = ref<HTMLElement | null>(null)
      const vditor = ref<any>(null)
      const editorContent = ref(props.modelValue)
      const instanceId = ref(`vditor-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`)

      // 上传进度
      const uploadProgress = ref(0)

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

      // 处理文件上传
      const handleFileUpload = async (files: File[], callback: (urls: string[]) => void) => {
        if (files.length === 0) return

        const uploadResults: string[] = []
        const promises: Promise<any>[] = []

        for (const file of files) {
          const isImage = props.imgFileTypes.includes(file.type)
          const dir = isImage ? props.imgDir : props.fileDir

          // 验证文件
          if (!validateFile(file, isImage)) continue

          // 根据上传模式选择上传方法
          const uploadPromise = props.useOss
            ? handleOssUpload(file, dir)
            : handleNormalUpload(file, dir)

          promises.push(uploadPromise.then(url => {
            if (url) uploadResults.push(url)
            return url
          }))
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
      const handleOssUpload = (file: File, dir: string): Promise<string> => {
        return new Promise((resolve, reject) => {
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

      // 初始化编辑器
      const initEditor = async () => {
        if (!editorRef.value) {
          console.error('Markdown编辑器DOM元素不存在')
          return
        }

        // 避免重复初始化
        if (vditor.value) {
          console.log('Markdown编辑器已初始化，直接发出mounted事件')
          emit('mounted')
          return
        }

        // 打印一些调试信息
        console.log(`初始化Markdown编辑器，ID: ${instanceId.value}`)

        // 确保使用本地资源而非CDN
        console.log('使用本地Vditor资源')

        await nextTick()

        const options = {
          height: typeof props.height === 'number' ? `${props.height}px` : props.height,
          placeholder: props.placeholder,
          toolbar: props.readonly ? [] : props.toolbar as any,
          mode: props.mode as 'ir' | 'wysiwyg' | 'sv',
          preview: {
            hljs: {
              lineNumber: true
            }
          },
          cache: {
            id: instanceId.value,
            enable: false // 禁用缓存，防止内容自动恢复导致的问题
          },
          cdn: '/vditor',
          theme: 'classic' as 'classic',
          upload: {
            accept: [...props.imgFileTypes, ...props.fileTypes].join(','),
            handler: (files: File[]) => {
              // 自定义上传处理
              handleFileUpload(files, (urls) => {
                // 上传成功后，将URL插入到编辑器中
                if (urls.length > 0) {
                  const uploadedText = urls.map(url => {
                    // 检查文件类型，决定插入图片还是文件链接
                    const file = files.find(f => url.includes(f.name))
                    if (file && props.imgFileTypes.includes(file.type)) {
                      return `![${file.name}](${url})`
                    } else {
                      return `[${file ? file.name : '文件'}](${url})`
                    }
                  }).join('\n')

                  // 插入内容到编辑器
                  vditor.value?.insertValue(uploadedText)
                }
              })

              return 'uploading...'
            }
          },
          after: () => {
            console.log('Vditor after回调执行')
            // 只有当modelValue与当前编辑器内容不一致时才设置值
            if (props.modelValue && props.modelValue !== vditor.value?.getValue()) {
              vditor.value?.setValue(props.modelValue)
            }
            if (props.readonly) {
              vditor.value?.disabled()
              // 确保工具栏隐藏
              const toolbar = editorRef.value?.querySelector('.vditor-toolbar')
              if (toolbar) {
                (toolbar as HTMLElement).style.display = 'none'
              }
              // 应用只读样式
              const container = editorRef.value?.querySelector('.vditor-content')
              if (container) {
                container.classList.add('readonly-content')
              }
            }

            // 确保DOM完全渲染后再发出事件
            nextTick(() => {
              console.log('Markdown编辑器初始化完成')
              emit('mounted')
            })
          },
          error: (err: any) => {
            console.error('Vditor初始化错误:', err)
            // 即使出错也尝试发出mounted事件，以便UI不会一直显示加载状态
            nextTick(() => emit('mounted'))
          },
          input: (value: string) => {
            // 防止无意义的更新
            if (value !== editorContent.value) {
              editorContent.value = value
              emit('update:modelValue', value)
              emit('change', value)
            }
          }
        };

        try {
          vditor.value = new Vditor(editorRef.value, options)

          // 确保样式正确加载
          const vditorStyle = document.querySelector('head style[data-vditor-style]')
          if (!vditorStyle) {
            console.warn('Vditor样式未正确加载，尝试手动注入样式')
          }
        } catch (err) {
          console.error('创建Vditor实例时发生错误 (使用本地资源):', err)
          // 出错时也发出mounted事件，以防止UI卡在加载状态
          emit('mounted')
        }
      }

      // 保存内容
      const saveContent = () => {
        if (vditor.value) {
          const content = vditor.value.getValue()
          emit('save', content)
        }
      }

      // 获取HTML内容
      const getHtml = () => {
        return vditor.value ? vditor.value.getHTML() : ''
      }

      // 监听modelValue的变化
      watch(
        () => props.modelValue,
        (newValue) => {
          if (vditor.value && newValue !== editorContent.value) {
            // 添加防抖，避免短时间内多次设置值
            const currentValue = vditor.value.getValue()
            if (newValue !== currentValue) {
              vditor.value.setValue(newValue)
            }
          }
        }
      )

      // 监听readonly的变化
      watch(
        () => props.readonly,
        (newValue) => {
          if (vditor.value) {
            if (newValue) {
              vditor.value.disabled()
              // 隐藏工具栏
              const toolbar = editorRef.value?.querySelector('.vditor-toolbar')
              if (toolbar) {
                (toolbar as HTMLElement).style.display = 'none'
              }
              // 添加只读模式的类名
              const container = editorRef.value?.querySelector('.vditor-content')
              if (container) {
                container.classList.add('readonly-content')
              }
            } else {
              vditor.value.enable()
              // 显示工具栏
              const toolbar = editorRef.value?.querySelector('.vditor-toolbar')
              if (toolbar) {
                (toolbar as HTMLElement).style.display = ''
              }
              // 移除只读模式的类名
              const container = editorRef.value?.querySelector('.vditor-content')
              if (container) {
                container.classList.remove('readonly-content')
              }
            }
          }
        }
      )

      onMounted(() => {
        console.log('Markdown编辑器组件挂载，准备初始化Vditor (本地资源)')
        initEditor()
      })

      onBeforeUnmount(() => {
        if (vditor.value) {
          console.log('销毁Vditor实例')
          vditor.value.destroy()
        }
      })

      // 暴露方法给父组件
      expose({
        saveContent,
        getHtml,
        getVditor: () => vditor.value
      })

      return {
        editorRef
      }
    }
  })
  </script>

  <style lang="scss" scoped>
  .markdown-editor-container {
    width: 100%;

    .markdown-editor {
      box-sizing: border-box;
      padding: 0;
      margin: 0;
      border-radius: 4px;
      overflow: hidden;
      min-height: 50px;
    }

    :deep(.readonly-content) {
      .vditor-ir, .vditor-reset, .vditor-wysiwyg {
        color: var(--el-text-color-primary) !important;
        opacity: 1 !important;
        font-size: inherit !important;
        line-height: 1.5;
        font-weight: 500 !important;
        font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
        font-size: 14px !important;
        height: auto !important;
         // 减少内边距，移除边框
        margin: 5px 5px !important;

        padding: 0px 5px !important;
        border: none !important;
        p {
          margin-top: 0.5em;
          margin-bottom: 0.5em;
        }

        // 减少空行
        pre {
          margin-top: 0.75em;
          margin-bottom: 0.75em;
        }

        h1, h2, h3, h4, h5, h6 {
          margin-top: 1em;
          margin-bottom: 0.5em;
        }

        ul, ol {
          padding-left: 1.5em;
          margin-top: 0.5em;
          margin-bottom: 0.5em;
        }

        // 内容区域的间距调整
        padding: 0.5em;

        // 确保内容不被截断
        overflow-wrap: break-word;
        word-wrap: break-word;
      }

      // 隐藏只读模式下不需要的元素
      .vditor-toolbar, .vditor-preview-hint {
        display: none !important;
      }


    }
  }
  </style>
