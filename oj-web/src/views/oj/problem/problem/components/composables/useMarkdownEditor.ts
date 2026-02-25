import { ref } from 'vue'
import { ElNotification } from 'element-plus'
import type { MarkdownLoadedState } from '../types'

/**
 * Markdown编辑器相关功能的组合函数
 * @param initialState 初始状态
 * @returns Markdown编辑器相关的状态和方法
 */
export function useMarkdownEditor(initialState: Partial<MarkdownLoadedState> = {}) {
  // Markdown编辑器加载状态
  const markdownLoaded = ref<MarkdownLoadedState>({
    description: false,
    input: false,
    output: false,
    hint: false,
    ...initialState
  })

  // 跟踪已处理的事件，防止重复处理
  const processedEvents = ref<Record<string, boolean>>({})

  /**
   * 处理文件上传成功回调
   * @param urls 上传成功的文件URL列表
   */
  const handleUploadSuccess = (urls: string[]) => {
    ElNotification({
      title: '上传成功',
      message: `成功上传 ${urls.length} 个文件`,
      type: 'success'
    })
    return urls
  }

  /**
   * 处理文件上传失败回调
   * @param error 上传失败的错误信息
   */
  const handleUploadError = (error: any) => {
    ElNotification({
      title: '上传失败',
      message: error.message || '上传文件失败，请重试',
      type: 'error'
    })
    return error
  }

  /**
   * 处理Markdown编辑器加载完成
   * @param type 加载完成的编辑器类型
   */
  const handleMarkdownMounted = (type: string) => {
    // 防止重复处理同一编辑器的挂载事件
    const eventKey = `mounted-${type}`
    if (processedEvents.value[eventKey]) {
      console.log(`编辑器 ${type} 已经处理过挂载事件，跳过`)
      return type
    }
    
    // 标记为已处理
    processedEvents.value[eventKey] = true
    
    // 更新状态
    if (type in markdownLoaded.value) {
      markdownLoaded.value[type as keyof typeof markdownLoaded.value] = true
    }
    
    return type
  }

  // 清除已处理的事件记录（仅用于重置状态）
  const resetProcessedEvents = () => {
    processedEvents.value = {}
  }

  return {
    markdownLoaded,
    handleUploadSuccess,
    handleUploadError,
    handleMarkdownMounted,
    resetProcessedEvents
  }
} 
