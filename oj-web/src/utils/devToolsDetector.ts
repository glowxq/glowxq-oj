/**
 * 开发者工具检测器
 * 用于检测浏览器控制台是否打开，并提供相应的布局调整建议
 */

export interface DevToolsDetectionResult {
  /** 控制台是否打开 */
  isOpen: boolean
  /** 检测方法 */
  method: 'size' | 'ratio' | 'none'
  /** 窗口信息 */
  windowInfo: {
    width: number
    height: number
    screenWidth: number
    screenHeight: number
    heightDiff: number
    widthDiff: number
    heightRatio: number
  }
}

/**
 * 检测开发者工具是否打开
 * @param threshold 尺寸差异阈值，默认160px
 * @param ratioThreshold 高度比例阈值，默认0.75
 * @returns 检测结果
 */
export function detectDevTools(
  threshold: number = 160,
  ratioThreshold: number = 0.75
): DevToolsDetectionResult {
  const windowInfo = {
    width: window.innerWidth,
    height: window.innerHeight,
    screenWidth: screen.width,
    screenHeight: screen.height,
    heightDiff: screen.height - window.innerHeight,
    widthDiff: screen.width - window.innerWidth,
    heightRatio: window.innerHeight / screen.height
  }

  // 方法1: 检查窗口尺寸变化
  const sizeDetection = windowInfo.heightDiff > threshold || windowInfo.widthDiff > threshold

  // 方法2: 检查可用高度与屏幕高度的比例
  const ratioDetection = windowInfo.heightRatio < ratioThreshold

  let isOpen = false
  let method: 'size' | 'ratio' | 'none' = 'none'

  if (sizeDetection) {
    isOpen = true
    method = 'size'
  } else if (ratioDetection) {
    isOpen = true
    method = 'ratio'
  }

  return {
    isOpen,
    method,
    windowInfo
  }
}

/**
 * 创建开发者工具检测器实例
 * @param callback 状态变化回调函数
 * @param options 配置选项
 */
export function createDevToolsDetector(
  callback: (result: DevToolsDetectionResult) => void,
  options: {
    threshold?: number
    ratioThreshold?: number
    debounceDelay?: number
  } = {}
) {
  const {
    threshold = 160,
    ratioThreshold = 0.75,
    debounceDelay = 100
  } = options

  let timeoutId: number | null = null
  let lastResult: DevToolsDetectionResult | null = null

  const detect = () => {
    const result = detectDevTools(threshold, ratioThreshold)
    
    // 只在状态变化时触发回调
    if (!lastResult || lastResult.isOpen !== result.isOpen) {
      callback(result)
      lastResult = result
    }
  }

  const debouncedDetect = () => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    timeoutId = window.setTimeout(detect, debounceDelay)
  }

  // 初始检测
  detect()

  // 监听窗口大小变化
  window.addEventListener('resize', debouncedDetect)

  // 返回清理函数
  return () => {
    window.removeEventListener('resize', debouncedDetect)
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
  }
}

/**
 * 获取布局调整建议
 * @param result 检测结果
 * @returns CSS类名和样式建议
 */
export function getLayoutAdjustments(result: DevToolsDetectionResult) {
  const suggestions = {
    cssClass: result.isOpen ? 'devtools-open' : '',
    containerHeight: result.isOpen ? 'calc(100vh - 120px)' : 'calc(100vh - 100px)',
    editorMaxHeight: result.isOpen ? 'calc(100vh - 320px)' : 'calc(100vh - 280px)',
    minHeight: result.isOpen ? '500px' : '600px'
  }

  return suggestions
}
