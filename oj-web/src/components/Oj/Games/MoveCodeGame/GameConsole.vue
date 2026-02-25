<template>
  <div 
    class="game-console" 
    :class="{ 'collapsed': collapsed }"
    :style="{ 
      left: `${position.x}px`, 
      top: `${position.y}px` 
    }"
  >
    <div 
      class="console-header" 
      @mousedown="startDrag"
      @touchstart="startDrag"
    >
      <span class="console-title">📟 控制台</span>
      <div class="console-controls">
        <el-icon class="collapse-icon" :class="{ 'rotated': collapsed }" @click="toggleCollapse">
          <ArrowDown />
        </el-icon>
      </div>
    </div>
    
    <div class="console-content" v-show="!collapsed">
      <div class="console-output" ref="consoleOutput">
        <div
          v-for="(log, index) in logs"
          :key="index"
          class="console-log"
          :class="`log-${log.type}`"
        >
          <span class="log-time">{{ log.time }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
        <div v-if="logs.length === 0" class="console-empty">
          控制台输出将显示在这里...
        </div>
      </div>
      <div class="console-actions">
        <el-button @click="clearLogs" size="small" type="text">
          🗑️清空
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElButton, ElIcon } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

// 控制台日志接口
interface ConsoleLog {
  time: string
  message: string
  type: 'info' | 'warn' | 'error' | 'success'
}

// Props
interface GameConsoleProps {
  initialPosition?: { x: number; y: number }
  initialCollapsed?: boolean
}

const props = withDefaults(defineProps<GameConsoleProps>(), {
  initialPosition: () => ({ x: 20, y: 20 }),
  initialCollapsed: false
})

// Events
const emit = defineEmits<{
  positionChange: [position: { x: number; y: number }]
  collapseChange: [collapsed: boolean]
}>()

// 状态
const collapsed = ref(props.initialCollapsed)
const logs = ref<ConsoleLog[]>([])
const consoleOutput = ref<HTMLElement | null>(null)

// 拖拽相关状态
const isDragging = ref(false)
const position = reactive({ ...props.initialPosition })
const dragOffset = reactive({ x: 0, y: 0 })

// 切换折叠状态
const toggleCollapse = (): void => {
  collapsed.value = !collapsed.value
  emit('collapseChange', collapsed.value)
}

// 添加日志
const addLog = (message: string, type: 'info' | 'warn' | 'error' | 'success' = 'info'): void => {
  const now = new Date()
  const time = now.toLocaleTimeString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })

  logs.value.push({
    time,
    message,
    type
  })

  // 自动滚动到底部
  nextTick(() => {
    if (consoleOutput.value) {
      consoleOutput.value.scrollTop = consoleOutput.value.scrollHeight
    }
  })

  // 限制日志数量，避免内存泄漏
  if (logs.value.length > 100) {
    logs.value = logs.value.slice(-50)
  }
}

// 清空日志
const clearLogs = (): void => {
  logs.value = []
}

// 开始拖拽
const startDrag = (event: MouseEvent | TouchEvent): void => {
  event.preventDefault()
  event.stopPropagation()
  
  isDragging.value = true
  
  const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX
  const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY
  
  dragOffset.x = clientX - position.x
  dragOffset.y = clientY - position.y
  
  document.addEventListener('mousemove', handleDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchmove', handleDrag)
  document.addEventListener('touchend', stopDrag)
}

// 处理拖拽
const handleDrag = (event: MouseEvent | TouchEvent): void => {
  if (!isDragging.value) return
  
  event.preventDefault()
  
  const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX
  const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY
  
  const newX = clientX - dragOffset.x
  const newY = clientY - dragOffset.y
  
  // 限制拖拽范围，确保控制台不会完全移出视窗
  const consoleWidth = 300
  const consoleHeight = 250
  const minX = -consoleWidth + 50 // 允许部分移出屏幕
  const minY = 0
  const maxX = window.innerWidth - 50
  const maxY = window.innerHeight - 50
  
  position.x = Math.max(minX, Math.min(newX, maxX))
  position.y = Math.max(minY, Math.min(newY, maxY))
  
  emit('positionChange', { ...position })
}

// 停止拖拽
const stopDrag = (): void => {
  isDragging.value = false
  
  document.removeEventListener('mousemove', handleDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', handleDrag)
  document.removeEventListener('touchend', stopDrag)
}

// 暴露方法
defineExpose({
  addLog,
  clearLogs,
  toggleCollapse,
  setPosition: (newPosition: { x: number; y: number }) => {
    position.x = newPosition.x
    position.y = newPosition.y
  }
})

// 清理事件监听器
onBeforeUnmount(() => {
  document.removeEventListener('mousemove', handleDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', handleDrag)
  document.removeEventListener('touchend', stopDrag)
})
</script>

<style scoped>
.game-console {
  position: fixed;
  width: 300px;
  max-height: 250px;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.9);
  border-radius: 8px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: max-height 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.game-console.collapsed {
  max-height: 40px;
}

.console-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px 8px 0 0;
  cursor: move;
  user-select: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.console-header:hover {
  background: rgba(255, 255, 255, 0.15);
}

.console-title {
  color: #fff;
  font-size: 14px;
  font-weight: bold;
}

.console-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.collapse-icon {
  color: #fff;
  transition: transform 0.3s ease;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.collapse-icon:hover {
  background: rgba(255, 255, 255, 0.1);
}

.collapse-icon.rotated {
  transform: rotate(180deg);
}

.console-content {
  padding: 0;
  display: flex;
  flex-direction: column;
  height: 200px;
}

.console-output {
  flex: 1;
  padding: 8px 12px;
  overflow-y: auto;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.4;
  max-height: 160px;
}

.console-log {
  margin-bottom: 4px;
  word-wrap: break-word;
}

.log-time {
  color: #888;
  margin-right: 8px;
  font-size: 11px;
}

.log-message {
  color: #fff;
}

.log-info .log-message {
  color: #fff;
}

.log-success .log-message {
  color: #4CAF50;
}

.log-warn .log-message {
  color: #FF9800;
}

.log-error .log-message {
  color: #F44336;
}

.console-empty {
  color: #888;
  font-style: italic;
  text-align: center;
  padding: 20px;
}

.console-actions {
  padding: 8px 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
}

.console-actions .el-button {
  color: #fff;
  border: none;
  background: transparent;
  padding: 4px 8px;
  font-size: 12px;
}

.console-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 滚动条样式 */
.console-output::-webkit-scrollbar {
  width: 6px;
}

.console-output::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.console-output::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.console-output::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>
