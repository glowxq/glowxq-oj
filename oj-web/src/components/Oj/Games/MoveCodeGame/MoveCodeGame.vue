<template>
  <div class="move-code-game-container">
    <!-- 游戏控制栏 -->
    <div class="game-header">
      <!-- 游戏状态 -->
      <div class="game-status">
        <el-tag :type="gameStatus === 'playing' ? 'primary' : gameStatus === 'success' ? 'success' : 'info'" size="small">
          {{ getStatusText() }}
        </el-tag>
        <span class="level-number">关卡 {{ currentLevel?.id || 0 }}</span>
        <span class="map-info" v-if="getCurrentMapData()">
          🗺️ {{ getCurrentMapData()?.name }}
        </span>
      </div>

      <!-- 游戏控制按钮 -->
      <div class="game-controls">
        <el-button @click="runUserCode" :loading="isRunning" type="primary">
          <el-icon style="margin-right: 4px;"><VideoPlay /></el-icon>
          运行代码
        </el-button>
        <el-button @click="resetAndRun" :loading="isRunning" type="success">
          <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
          重新运行
        </el-button>
        <el-button @click="importLevel" type="info" size="default">
          <el-icon style="margin-right: 4px;"><Upload /></el-icon>
          导入关卡
        </el-button>
        <el-button @click="clearCode" type="default">
          <el-icon style="margin-right: 4px;"><Delete /></el-icon>
          清空
        </el-button>
        <el-button @click="toggleSettings" type="default">
          <el-icon style="margin-right: 4px;"><Setting /></el-icon>
          设置
        </el-button>
        <el-button @click="showAnswer" type="warning">
          <el-icon style="margin-right: 4px;"><Sunny /></el-icon>
          查看答案
        </el-button>
        <el-button @click="shareCode" type="primary" plain>
          <el-icon style="margin-right: 4px;"><Share /></el-icon>
          分享
        </el-button>
        <el-button @click="resetGame" type="default">
          <el-icon style="margin-right: 4px;"><RefreshLeft /></el-icon>
          重置
        </el-button>
      </div>
    </div>

    <!-- 游戏主体区域 -->
    <div class="game-content">
      <!-- 左侧游戏世界 -->
      <div class="game-world-container" :style="{ width: `${leftPanelWidth}%` }">
        <!-- 悬浮球 - 关卡信息 -->
        <div class="floating-ball level-info-ball" @click="toggleLevelInfo">
          <el-icon><InfoFilled /></el-icon>
        </div>

        <!-- 关卡信息面板 -->
        <transition name="slide-down">
          <div v-if="showLevelInfo" class="level-info-panel">
            <div class="panel-header">
              <h3>🎯 关卡 {{ currentLevel?.id || 0 }}</h3>
              <el-button @click="toggleLevelInfo" type="text" size="small">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
            <div class="panel-content">
              <div class="info-section">
                <h4>目标</h4>
                <p>{{ currentLevel?.objective || '未知目标' }}</p>
              </div>
              <div class="info-section">
                <h4>学习内容</h4>
                <p>{{ currentLevel?.learningContent || '暂无学习内容' }}</p>
              </div>
              <div class="info-section">
                <h4>可用命令</h4>
                <div class="commands-list">
                  <el-tag
                    v-for="command in (currentLevel?.availableCommands || [])"
                    :key="command"
                    size="small"
                    class="command-tag"
                  >
                    {{ command }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </transition>

        <!-- 游戏画布 -->
        <div class="game-world">
          <canvas
            ref="gameCanvas"
            :width="canvasWidth"
            :height="canvasHeight"
            class="game-canvas"
          ></canvas>

          <!-- 武器框 - 悬浮在游戏画布内 -->
          <div class="floating-weapon-bar">
            <div class="weapon-slot">
              <div
                class="weapon-display"
                v-if="heroWeapon"
                :style="{ backgroundColor: getWeaponColor(heroWeapon.weaponType) }"
              >
                <img
                  v-if="heroWeapon.image"
                  :src="heroWeapon.image"
                  :alt="getWeaponName(heroWeapon.weaponType)"
                  class="weapon-image"
                />
                <span v-else class="weapon-icon">{{ getWeaponIcon(heroWeapon.weaponType) }}</span>
              </div>
              <div class="empty-weapon-slot" v-else>
                <span class="empty-text">武器</span>
              </div>
            </div>
          </div>

          <!-- 游戏信息覆盖层 -->
          <div class="game-overlay" v-if="showOverlay">
            <div class="overlay-content">
              <div v-if="gameStatus === 'success'" class="success-message">
                <div class="message-header">
                  <h3>🎉 恭喜通关！</h3>
                  <el-button @click="closeSuccessMessage" type="text" size="small" class="close-button">
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
                <p>{{ currentLevel?.successMessage || '恭喜完成关卡！' }}</p>
                <div class="message-actions">
                  <el-button @click="closeSuccessMessage" type="default" size="small">
                    关闭
                  </el-button>
                </div>
              </div>
              <div v-else-if="gameStatus === 'failed'" class="failed-message">
                <div class="message-header">
                  <h3>💥 游戏失败</h3>
                  <el-button @click="closeFailureMessage" type="text" size="small" class="close-button">
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
                <p>{{ failureReason }}</p>
                <div class="message-actions">
                  <el-button @click="resetGame" type="primary" size="small">
                    🔄 重新开始
                  </el-button>
                  <el-button @click="closeFailureMessage" type="default" size="small">
                    关闭
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 拖拽分隔条 -->
      <div
        class="resize-handle"
        @mousedown="startResize"
        @touchstart="startResize"
      >
        <div class="resize-handle-line"></div>
      </div>

      <!-- 右侧代码编辑区 -->
      <div class="code-editor-container" :style="{ width: `${100 - leftPanelWidth}%` }">


        <!-- 游戏设置组件 -->
        <GameSettings
          :visible="showSettings"
          :settings="gameSettings"
          @close="toggleSettings"
          @update-settings="handleSettingsUpdate"
          @speed-change="handleSpeedChange"
          @icon-change="handleIconChange"
          @image-change="handleImageChange"
        />

        <!-- 代码编辑器 -->
        <div class="code-editor-wrapper">
          <CodeEditor
            ref="codeEditorRef"
            v-model="userCode"
            :language="'javascript'"
            :theme="'light'"
            :default-font-size="14"
            :read-only-prop="false"
            @change="handleCodeChange"
          >
            <template #custom-actions>
              <el-button @click="clearCode" type="default" size="small">
                🗑️ 清空
              </el-button>
            </template>
          </CodeEditor>
        </div>

        <!-- 控制台 -->
        <div class="console-wrapper">
          <GameConsole
            ref="gameConsoleRef"
            :initial-position="{ x: 0, y: 0 }"
            :initial-collapsed="false"
            @position-change="handleConsolePositionChange"
            @collapse-change="handleConsoleCollapseChange"
          />
        </div>

      </div>

      <!-- 运行信息模块 -->
      <div class="runtime-info" v-show="runtimeLogs.length > 0">
        <div class="runtime-header">
          <span class="runtime-title">📋 运行信息</span>
          <el-button @click="clearRuntimeLogs" type="text" size="small" class="clear-btn">
            🗑️ 清空
          </el-button>
        </div>
        <div class="runtime-content">
          <div class="runtime-logs" ref="runtimeLogsRef">
            <div
              v-for="(log, index) in runtimeLogs"
              :key="index"
              class="runtime-log"
            >
              <span class="log-time">{{ log.time }}</span>
              <span class="log-message">{{ log.message }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 导入关卡对话框 -->
    <el-dialog
      v-model="showImportDialog"
      title="导入关卡"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="import-dialog-content">
        <el-upload
          class="upload-demo"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          :file-list="fileList"
          accept=".json"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将JSON文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传JSON文件
            </div>
          </template>
        </el-upload>
        <div class="import-text-area" style="margin-top: 20px;">
          <el-input
            v-model="importJsonText"
            type="textarea"
            :rows="10"
            placeholder="或者直接粘贴关卡JSON数据..."
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button @click="confirmImportLevel" type="primary" :disabled="!importJsonText.trim()">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, reactive, computed, onBeforeUnmount, watch } from 'vue'
import { ElButton, ElTag, ElMessage, ElIcon, ElMessageBox, ElDialog, ElInput, ElUpload } from 'element-plus'
import { InfoFilled, Close, Upload, Download, UploadFilled, VideoPlay, Refresh, Delete, Setting, Sunny, RefreshLeft, Share } from '@element-plus/icons-vue'
import CodeEditor from '@/components/Common/CodeEditor/CodeEditor.vue'
import GameSettings from './GameSettings.vue'
import GameConsole from './GameConsole.vue'

// 游戏配置常量
const GAME_CONFIG = {
  WORLD_WIDTH: 600,
  WORLD_HEIGHT: 400,
  GRID_SIZE: 40,
  HERO_COLOR: '#4A90E2',
  TREASURE_COLOR: '#FFD700',
  OBSTACLE_COLOR: '#E74C3C',
  GRID_COLOR: '#E8E8E8',
  BACKGROUND_COLOR: '#F8F9FA',
  ANIMATION_SPEED: 300, // 动画持续时间(ms)
  MOVE_STEPS: 20 // 移动动画步数
}

// 游戏状态类型定义
type GameStatus = 'idle' | 'playing' | 'success' | 'failed'
type Position = { x: number; y: number }

// 游戏元素基础接口
interface GameElement {
  position: Position
  type: 'hero' | 'treasure' | 'obstacle' | 'monster' | 'weapon'
  id: string
  symbol?: string // 默认符号
  image?: string // 自定义图片URL
  color?: string // 自定义颜色
  size?: number // 自定义大小比例 (0-1)
  animation?: {
    isAnimating: boolean
    startPos: Position
    endPos: Position
    startTime: number
    duration: number
  }
}

// 英雄元素接口
interface HeroElement extends GameElement {
  type: 'hero'
  direction?: 'up' | 'down' | 'left' | 'right' // 朝向
}

// 宝箱元素接口
interface TreasureElement extends GameElement {
  type: 'treasure'
  collected?: boolean
  sparkle?: boolean // 闪烁效果
}

// 障碍物元素接口
interface ObstacleElement extends GameElement {
  type: 'obstacle'
  destructible?: boolean // 是否可破坏
}

// 怪物元素接口
interface MonsterElement extends GameElement {
  type: 'monster'
  monsterType: 'physical' | 'magical' // 物理系或魔法系
  alive: boolean // 是否存活
}

// 武器元素接口
interface WeaponElement extends GameElement {
  type: 'weapon'
  weaponType: 'sword' | 'staff' // 剑或魔杖
  collected: boolean // 是否已收集
}

// 地图配置接口
interface MapData {
  id: number
  name: string
  heroStart: Position
  treasures: Position[]
  obstacles: Position[]
  monsters?: Array<{x: number, y: number, type: 'physical' | 'magical'}>
  weapons?: Array<{x: number, y: number, type: 'sword' | 'staff'}>
}

// 关卡配置接口
interface Level {
  id: number
  objective: string
  learningContent: string
  difficulty: number // 难度等级 1-10
  availableCommands: string[]
  codePlaceholder: string
  successMessage: string
  maps: MapData[] // 支持多个地图
  answer: string // 关卡答案代码
}

// 组件属性定义
interface MoveCodeGameProps {
  levelData?: Level
  customLevel?: Level | null
}

const props = defineProps<MoveCodeGameProps>()

// 组件事件定义
const emit = defineEmits<{
  gameStart: []
  gameEnd: [success: boolean]
  levelComplete: []
  levelImport: [levelData: any]
  levelApplied: []
}>()



// 响应式状态
const gameCanvas = ref<HTMLCanvasElement | null>(null)
const codeEditorRef = ref<InstanceType<typeof CodeEditor> | null>(null)
const gameStatus = ref<GameStatus>('idle')
const currentMapIndex = ref(0) // 当前使用的地图索引
const userCode = ref('')
const isRunning = ref(false)
const showOverlay = ref(false)
const failureReason = ref('')

// 控制台组件引用
const gameConsoleRef = ref<InstanceType<typeof GameConsole> | null>(null)

// 运行信息状态
interface RuntimeLog {
  time: string
  message: string
}

const runtimeLogs = ref<RuntimeLog[]>([])
const runtimeLogsRef = ref<HTMLElement | null>(null)

// 防止应用关卡后被 watch 覆盖的标志
const lastLevelId = ref<number | null>(null)


// 分享功能：分享代码和关卡数据
const shareCode = (): void => {
  try {
    const code = userCode.value || ''
    const currentLevelData = currentLevel.value
    
    if (!code.trim() && !currentLevelData) {
      ElMessage.warning('代码和关卡数据都为空，无法分享')
      return
    }
    
    const currentUrl = window.location.origin + window.location.pathname
    const params = new URLSearchParams()
    
    // 如果有代码，添加代码参数
    if (code.trim()) {
      const encodedCode = btoa(encodeURIComponent(code))
      params.append('code', encodedCode)
    }
    
    // 如果有关卡数据，添加关卡参数
    if (currentLevelData) {
      const levelJson = JSON.stringify(currentLevelData, null, 2)
      const encodedLevel = btoa(encodeURIComponent(levelJson))
      params.append('level', encodedLevel)
    }
    
    const shareUrl = `${currentUrl}?${params.toString()}`
    
    // 复制到剪贴板
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(shareUrl).then(() => {
        ElMessage.success('分享链接已复制到剪贴板！')
      }).catch(() => {
        // 降级方案
        copyToClipboard(shareUrl)
      })
    } else {
      copyToClipboard(shareUrl)
    }
  } catch (error) {
    ElMessage.error('分享失败，请重试')
    console.error('分享失败:', error)
  }
}

// 复制到剪贴板的降级方案
const copyToClipboard = (text: string): void => {
  const textArea = document.createElement('textarea')
  textArea.value = text
  textArea.style.position = 'fixed'
  textArea.style.left = '-999999px'
  textArea.style.top = '-999999px'
  document.body.appendChild(textArea)
  textArea.focus()
  textArea.select()
  try {
    document.execCommand('copy')
    ElMessage.success('分享链接已复制到剪贴板！')
  } catch (err) {
    ElMessage.error('复制失败，请手动复制')
  }
  document.body.removeChild(textArea)
}

// 从 URL 参数加载代码或关卡
const loadCodeFromUrl = (): void => {
  const urlParams = new URLSearchParams(window.location.search)
  const encodedCode = urlParams.get('code')
  const encodedLevel = urlParams.get('level')
  
  if (encodedCode) {
    try {
      const decodedCode = decodeURIComponent(atob(encodedCode))
      userCode.value = decodedCode
      ElMessage.success('已加载分享的代码')
    } catch (error) {
      ElMessage.error('加载分享代码失败')
      console.error('解码失败:', error)
    }
  }
  
  if (encodedLevel) {
    try {
      const decodedLevel = decodeURIComponent(atob(encodedLevel))
      const levelData = JSON.parse(decodedLevel)
      // 触发导入关卡事件
      emit('levelImport', levelData)
      ElMessage.success('已加载分享的关卡')
    } catch (error) {
      ElMessage.error('加载分享关卡失败')
      console.error('解码失败:', error)
    }
  }
}


// 导入关卡：打开导入对话框
const importLevel = (): void => {
  showImportDialog.value = true
  importJsonText.value = ''
  fileList.value = []
}

// 处理文件上传
const handleFileChange = (file: any): void => {
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const content = e.target?.result as string
      importJsonText.value = content
    } catch (error) {
      ElMessage.error('读取文件失败')
      console.error('读取文件失败:', error)
    }
  }
  reader.readAsText(file.raw)
}

// 确认导入关卡
const confirmImportLevel = (): void => {
  if (!importJsonText.value.trim()) {
    ElMessage.warning('请输入或上传关卡JSON数据')
    return
  }

  try {
    const levelData = JSON.parse(importJsonText.value)
    
    // 验证关卡数据格式
    if (!levelData.id || !levelData.maps || !Array.isArray(levelData.maps)) {
      ElMessage.error('关卡数据格式不正确，请检查JSON格式')
      return
    }

    ElMessageBox.confirm(
      `确定要导入关卡 ${levelData.id} 吗？这将替换当前关卡数据。`,
      '导入关卡',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }
    ).then(() => {
      // 触发导入关卡事件，由父组件处理
      emit('levelImport', levelData)
      showImportDialog.value = false
      importJsonText.value = ''
      fileList.value = []
      ElMessage.success('关卡导入成功')
    }).catch(() => {
      ElMessage.info('已取消导入')
    })
  } catch (error) {
    ElMessage.error('JSON格式错误，请检查数据格式')
    console.error('导入关卡失败:', error)
  }
}

// 界面控制状态
const showLevelInfo = ref(false)
const showSettings = ref(false)
const showImportDialog = ref(false)
const leftPanelWidth = ref(55) // 左侧面板宽度百分比，增加游戏空间

// 导入关卡相关状态
const importJsonText = ref('')
const fileList = ref<any[]>([])

// 游戏设置状态
const gameSettings = reactive({
  animationSpeed: 1, // 移动速度 (0.1-5)
  heroIcon: '🦸', // 英雄头像
  treasureIcon: '💰', // 宝箱图标
  obstacleIcon: '🧱', // 障碍物图标
  theme: 'default', // 主题
  // 图片URL设置
  heroImage: '',
  treasureImage: '',
  obstacleImage: '',
  swordImage: '',
  staffImage: '',
  physicalMonsterImage: '',
  magicalMonsterImage: ''
})
const animationSpeed = ref(1)

// 拖拽相关状态
const isResizing = ref(false)
const startX = ref(0)
const startWidth = ref(0)



// 画布尺寸计算
const canvasWidth = computed(() => {
  // 固定画布宽度，避免拖拽时消失
  return GAME_CONFIG.WORLD_WIDTH
})

const canvasHeight = computed(() => {
  // 固定画布高度
  return GAME_CONFIG.WORLD_HEIGHT
})

// 游戏世界状态
const gameWorld = reactive({
  hero: null as HeroElement | null,
  treasures: [] as TreasureElement[],
  obstacles: [] as ObstacleElement[],
  monsters: [] as MonsterElement[],
  weapons: [] as WeaponElement[],
  collectedTreasures: new Set<string>(),
  animationSpeed: 1 // 动画速度倍率 (0.1-10)
})

// 英雄武器状态
const heroWeapon = ref<WeaponElement | null>(null)

// 辅助函数：检查指定位置是否有障碍物
const hasObstacle = (x: number, y: number): boolean => {
  return gameWorld.obstacles.some(obstacle =>
    obstacle.position.x === x && obstacle.position.y === y
  )
}

// 辅助函数：检查指定位置是否有宝箱
const hasTreasure = (x: number, y: number): boolean => {
  return gameWorld.treasures.some(treasure =>
    treasure.position.x === x && treasure.position.y === y && !treasure.collected
  )
}

// 检查指定方向是否有障碍物（返回数组）
const thereObstacles = (): number[] => {
  if (!gameWorld.hero) return []

  const { x, y } = gameWorld.hero.position
  const obstacles = []

  // 1=上, 2=右, 3=下, 4=左
  if (hasObstacle(x, y - 1)) obstacles.push(1) // 上
  if (hasObstacle(x + 1, y)) obstacles.push(2) // 右
  if (hasObstacle(x, y + 1)) obstacles.push(3) // 下
  if (hasObstacle(x - 1, y)) obstacles.push(4) // 左
  addConsoleLog(`thereObstacles() 返回: [${obstacles.join(', ')}]`, 'info')
  return obstacles
}

// 检查指定方向是否到达边界（返回数组）
const reachBoundary = (): number[] => {
  if (!gameWorld.hero) return []

  const { x, y } = gameWorld.hero.position
  const boundaries = []

  // 1=上, 2=右, 3=下, 4=左
  if (y <= 1) boundaries.push(1) // 上边界
  if (x >= GAME_CONFIG.WORLD_WIDTH) boundaries.push(2) // 右边界
  if (y >= GAME_CONFIG.WORLD_HEIGHT) boundaries.push(3) // 下边界
  if (x <= 1) boundaries.push(4) // 左边界

  return boundaries
}

// 检查指定方向是否有障碍物（返回布尔值）
const isThereObstacles = (direction: number): boolean => {
  if (!gameWorld.hero) return false

  const { x, y } = gameWorld.hero.position

  switch (direction) {
    case 1: return hasObstacle(x, y - 1) // 上
    case 2: return hasObstacle(x + 1, y) // 右
    case 3: return hasObstacle(x, y + 1) // 下
    case 4: return hasObstacle(x - 1, y) // 左
    default: return false
  }
}

// 检查指定方向是否到达边界（返回布尔值）
const isReachBoundary = (direction: number): boolean => {
  if (!gameWorld.hero) return false

  const { x, y } = gameWorld.hero.position

  switch (direction) {
    case 1: return y <= 1 // 上边界
    case 2: return x >= GAME_CONFIG.WORLD_WIDTH // 右边界
    case 3: return y >= GAME_CONFIG.WORLD_HEIGHT // 下边界
    case 4: return x <= 1 // 左边界
    default: return false
  }
}

// 命令执行队列
let commandQueue: (() => Promise<void>)[] = []
let isExecutingQueue = false

// 执行命令队列
const executeCommandQueue = async (): Promise<void> => {
  if (isExecutingQueue || commandQueue.length === 0) return

  isExecutingQueue = true

  while (commandQueue.length > 0) {
    const command = commandQueue.shift()
    if (command) {
      await command()
    }
  }

  isExecutingQueue = false
}

// 前进指定格子数（根据当前方向）
const fd = (steps: number = 1): Promise<void> => {
  return new Promise<void>((resolve) => {
    const command = async () => {
      if (!gameWorld.hero) {
        resolve()
        return
      }
      addConsoleLog(`fd(${steps}) 执行: 向前移动 ${steps} 步`, 'info')
      // 确保步数为正整数
      const actualSteps = Math.max(1, Math.floor(Math.abs(steps)))
      const direction = gameWorld.hero.direction || 'right'

      // 根据方向确定移动向量
      let dx = 0, dy = 0
      switch (direction) {
        case 'up':
          dx = 0; dy = -1
          break
        case 'right':
          dx = 1; dy = 0
          break
        case 'down':
          dx = 0; dy = 1
          break
        case 'left':
          dx = -1; dy = 0
          break
      }

      // 连续移动指定步数
      for (let i = 0; i < actualSteps; i++) {
        await moveHero(dx, dy)
      }

      resolve()
    }

    commandQueue.push(command)
    executeCommandQueue()
  })
}

// 旋转方向（rt指令）- 保留向后兼容
const rt = (times: number = 1): Promise<void> => {
  return new Promise<void>((resolve) => {
    const command = async () => {
      if (!gameWorld.hero) {
        resolve()
        return
      }

      const directions = ['up', 'right', 'down', 'left'] as const
      const currentIndex = directions.indexOf(gameWorld.hero.direction || 'right')

      // 计算新的方向索引（顺时针旋转）
      const newIndex = (currentIndex + times) % 4
      gameWorld.hero.direction = directions[newIndex]

      // 更新显示
      await renderGame()
      resolve()
    }

    commandQueue.push(command)
    executeCommandQueue()
  })
}

// 直接设置方向（direction指令）
const direction = (dir: number): Promise<void> => {
  return new Promise<void>((resolve) => {
    const command = async () => {
      if (!gameWorld.hero) {
        resolve()
        return
      }

      // 1=上, 2=右, 3=下, 4=左
      const directions = ['up', 'right', 'down', 'left'] as const

      if (dir >= 1 && dir <= 4) {
        gameWorld.hero.direction = directions[dir - 1]  // 数组索引从0开始，所以减1

        // 更新显示
        await renderGame()
        resolve()
      } else {
        console.warn('direction()参数必须是1-4之间的数字: 1=上, 2=右, 3=下, 4=左')
        resolve()
      }
    }

    commandQueue.push(command)
    executeCommandQueue()
  })
}

// 攻击（attack指令）
const attack = (): Promise<void> => {
  return new Promise<void>((resolve) => {
    const command = async () => {
      if (!gameWorld.hero || !heroWeapon.value) {
        ElMessage.warning('没有装备武器，无法攻击')
        resolve()
        return
      }
      addConsoleLog("attack() 执行: 攻击", 'info')

      const weapon = heroWeapon.value
      const heroPos = gameWorld.hero.position
      const direction = gameWorld.hero.direction || 'right'

      // 计算攻击目标位置
      let targetX = heroPos.x
      let targetY = heroPos.y

      switch (direction) {
        case 'up':
          targetY--
          break
        case 'right':
          targetX++
          break
        case 'down':
          targetY++
          break
        case 'left':
          targetX--
          break
      }

      // 查找目标位置的怪物
      const targetMonster = gameWorld.monsters.find(monster =>
        monster.alive && monster.position.x === targetX && monster.position.y === targetY
      )

      if (targetMonster) {
        // 检查武器类型是否匹配怪物类型
        const canKill = (weapon.weaponType === 'sword' && targetMonster.monsterType === 'physical') ||
                       (weapon.weaponType === 'staff' && targetMonster.monsterType === 'magical')

        if (canKill) {
          targetMonster.alive = false
          targetMonster.symbol = '💀'
          targetMonster.color = '#666666'

          const weaponName = getWeaponName(weapon.weaponType)
          const monsterIcon = getMonsterIcon(targetMonster.monsterType)
          ElMessage.success(`${weaponName}击败了${monsterIcon}怪物！`)
        } else {
          const weaponName = getWeaponName(weapon.weaponType)
          const monsterIcon = getMonsterIcon(targetMonster.monsterType)
          ElMessage.warning(`${weaponName}对${monsterIcon}怪物无效！`)
        }
      } else {
        const weaponName = getWeaponName(weapon.weaponType)
        ElMessage.info(`${weaponName}攻击了空气`)
      }

      await renderGame()
      resolve()
    }

    commandQueue.push(command)
    executeCommandQueue()
  })
}

// 新的简化指令 - 基础移动
const right = (): Promise<void> => moveHero(1, 0)
const left = (): Promise<void> => moveHero(-1, 0)
const up = (): Promise<void> => moveHero(0, -1)
const down = (): Promise<void> => moveHero(0, 1)

// 新的简化指令 - 高级移动
const dir = direction // 别名
const go = fd // 别名
const turn = rt // 别名
const hit = attack // 别名

// 获取英雄横坐标
const posX = (): number => {
  const x = gameWorld.hero ? gameWorld.hero.position.x : 0
    addConsoleLog(`posX() 返回: ${x}`, 'info')
    addRuntimeLog(`posX() 返回: ${x}`)
  return x
}

// 获取英雄纵坐标
const posY = (): number => {
  const y = gameWorld.hero ? gameWorld.hero.position.y : 0
    addConsoleLog(`posY() 返回: ${y}`, 'info')
    addRuntimeLog(`posY() 返回: ${y}`)
  return y
}

// 获取宝箱横坐标（只有一个宝箱时可用）
const treasureX = (): number => {
  const uncollectedTreasures = gameWorld.treasures.filter(treasure => !treasure.collected)
  if (uncollectedTreasures.length === 0) {
    addConsoleLog('treasureX() 错误: 地图上没有宝箱！', 'error')
    throw new Error('地图上没有宝箱！')
  }
  if (uncollectedTreasures.length > 1) {
    addConsoleLog('treasureX() 错误: 地图上有多个宝箱，无法确定位置！', 'error')
    throw new Error('地图上有多个宝箱，无法确定位置！请使用hasTreasure()检查具体位置。')
  }
  const x = uncollectedTreasures[0].position.x
  addConsoleLog(`treasureX() 返回: ${x}`, 'info')
  addRuntimeLog(`treasureX() 返回: ${x}`)
  return x
}

// 获取宝箱纵坐标（只有一个宝箱时可用）
const treasureY = (): number => {
  const uncollectedTreasures = gameWorld.treasures.filter(treasure => !treasure.collected)
  if (uncollectedTreasures.length === 0) {
    addConsoleLog('treasureY() 错误: 地图上没有宝箱！', 'error')
    throw new Error('地图上没有宝箱！')
  }
  if (uncollectedTreasures.length > 1) {
    addConsoleLog('treasureY() 错误: 地图上有多个宝箱，无法确定位置！', 'error')
    throw new Error('地图上有多个宝箱，无法确定位置！请使用hasTreasure()检查具体位置。')
  }
  const y = uncollectedTreasures[0].position.y
  addConsoleLog(`treasureY() 返回: ${y}`, 'info')
  addRuntimeLog(`treasureY() 返回: ${y}`)
  return y
}

// 获取武器横坐标
const weaponX = (weaponType: number): number => {
  const typeMap = { 1: 'sword', 2: 'staff' } as const
  const type = typeMap[weaponType as keyof typeof typeMap]

  if (!type) {
    addConsoleLog('weaponX() 错误: 武器类型错误！请使用 1=剑, 2=魔杖', 'error')
    throw new Error('武器类型错误！请使用 1=剑, 2=魔杖')
  }

  const weapons = gameWorld.weapons.filter(w => w.weaponType === type && !w.collected)
  if (weapons.length === 0) {
    const weaponName = type === 'sword' ? '剑' : '魔杖'
    addConsoleLog(`weaponX() 错误: 地图上没有${weaponName}！`, 'error')
    throw new Error(`地图上没有${weaponName}！`)
  }
  if (weapons.length > 1) {
    const weaponName = type === 'sword' ? '剑' : '魔杖'
    addConsoleLog(`weaponX() 错误: 地图上有多个${weaponName}，无法确定位置！`, 'error')
    throw new Error(`地图上有多个${weaponName}，无法确定位置！`)
  }

  const x = weapons[0].position.x
  const weaponName = type === 'sword' ? '剑' : '魔杖'
  addConsoleLog(`weaponX(${weaponType}) 返回: ${weaponName}横坐标 ${x}`, 'info')
  addRuntimeLog(`weaponX(${weaponType}) 返回: ${weaponName}横坐标 ${x}`)
  return x
}

// 获取武器纵坐标
const weaponY = (weaponType: number): number => {
  const typeMap = { 1: 'sword', 2: 'staff' } as const
  const type = typeMap[weaponType as keyof typeof typeMap]

  if (!type) {
    addConsoleLog('weaponY() 错误: 武器类型错误！请使用 1=剑, 2=魔杖', 'error')
    throw new Error('武器类型错误！请使用 1=剑, 2=魔杖')
  }

  const weapons = gameWorld.weapons.filter(weapon => weapon.weaponType === type && !weapon.collected)
  if (weapons.length === 0) {
    const weaponName = type === 'sword' ? '剑' : '魔杖'
    addConsoleLog(`weaponY() 错误: 地图上没有${weaponName}！`, 'error')
    throw new Error(`地图上没有${weaponName}！`)
  }
  if (weapons.length > 1) {
    const weaponName = type === 'sword' ? '剑' : '魔杖'
    addConsoleLog(`weaponY() 错误: 地图上有多个${weaponName}，无法确定位置！`, 'error')
    throw new Error(`地图上有多个${weaponName}，无法确定位置！`)
  }

  const y = weapons[0].position.y
  const weaponName = type === 'sword' ? '剑' : '魔杖'
  addConsoleLog(`weaponY(${weaponType}) 返回: ${weaponName}纵坐标 ${y}`, 'info')
  addRuntimeLog(`weaponY(${weaponType}) 返回: ${weaponName}纵坐标 ${y}`)
  return y
}

// 检测当前持有的武器类型
const weapon = (): number => {
  const weaponType = !heroWeapon.value ? 0 : (heroWeapon.value.weaponType === 'sword' ? 1 : 2)
  const weaponName = weaponType === 0 ? '无武器' : (weaponType === 1 ? '剑' : '魔杖')
  addConsoleLog(`weapon() 返回: ${weaponType} (${weaponName})`, 'info')
  addRuntimeLog(`weapon() 返回: ${weaponType} (${weaponName})`)
  return weaponType
}

// 获取英雄当前朝向对应的方向数字
const getHeroDirection = (): number => {
  if (!gameWorld.hero) return 2 // 默认向右
  const dirMap = { 'up': 1, 'right': 2, 'down': 3, 'left': 4 }
  return dirMap[gameWorld.hero.direction || 'right']
}

// 根据方向获取目标位置
const getTargetPosition = (direction?: number): Position => {
  if (!gameWorld.hero) return { x: 0, y: 0 }

  const dir = direction || getHeroDirection()
  const { x, y } = gameWorld.hero.position

  switch (dir) {
    case 1: return { x, y: y - 1 } // 上
    case 2: return { x: x + 1, y } // 右
    case 3: return { x, y: y + 1 } // 下
    case 4: return { x: x - 1, y } // 左
    default: return { x, y }
  }
}

// 检测指定方向是否有障碍物（不传方向则检测前方）
const see = (direction?: number): boolean => {
  const target = getTargetPosition(direction)
  const result = hasObstacle(target.x, target.y)
  const dirName = direction ? ['', '上方', '右方', '下方', '左方'][direction] : '前方'
  addConsoleLog(`see(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}障碍物)`, 'info')
  addRuntimeLog(`see(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}障碍物)`)
  return result
}

// 检测指定方向是否有宝箱
const seeTreasure = (direction?: number): boolean => {
  const target = getTargetPosition(direction)
  const result = hasTreasure(target.x, target.y)
  const dirName = direction ? ['', '上方', '右方', '下方', '左方'][direction] : '前方'
  addConsoleLog(`seeTreasure(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}宝箱)`, 'info')
  addRuntimeLog(`seeTreasure(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}宝箱)`)
  return result
}

// 检测指定方向是否有武器
const seeWeapon = (direction?: number): boolean => {
  const target = getTargetPosition(direction)
  const result = gameWorld.weapons.some(weapon =>
    !weapon.collected && weapon.position.x === target.x && weapon.position.y === target.y
  )
  const dirName = direction ? ['', '上方', '右方', '下方', '左方'][direction] : '前方'
  addConsoleLog(`seeWeapon(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}武器)`, 'info')
  addRuntimeLog(`seeWeapon(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}武器)`)
  return result
}

// 检测指定方向是否有怪物
const seeMonster = (direction?: number): boolean => {
  const target = getTargetPosition(direction)
  const result = gameWorld.monsters.some(monster =>
    monster.alive && monster.position.x === target.x && monster.position.y === target.y
  )
  const dirName = direction ? ['', '上方', '右方', '下方', '左方'][direction] : '前方'
  addConsoleLog(`seeMonster(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}怪物)`, 'info')
  addRuntimeLog(`seeMonster(${direction || '前方'}) 返回: ${result} (${dirName}${result ? '有' : '无'}怪物)`)
  return result
}

// 将辅助函数添加到全局作用域，供用户代码使用
if (typeof window !== 'undefined') {
  // 旧指令（保持兼容性）
  (window as any).hasObstacle = hasObstacle;
  (window as any).hasTreasure = hasTreasure;
  (window as any).thereObstacles = thereObstacles;
  (window as any).reachBoundary = reachBoundary;
  (window as any).isThereObstacles = isThereObstacles;
  (window as any).isReachBoundary = isReachBoundary;
  (window as any).fd = fd;
  (window as any).rt = rt;
  (window as any).direction = direction;
  (window as any).attack = attack;
  (window as any).moveRight = () => moveHero(1, 0);
  (window as any).moveLeft = () => moveHero(-1, 0);
  (window as any).moveUp = () => moveHero(0, -1);
  (window as any).moveDown = () => moveHero(0, 1);
  (window as any).getHeroPosition = () => ({ x: posX(), y: posY() });

  // 新的简化指令
  (window as any).right = right;
  (window as any).left = left;
  (window as any).up = up;
  (window as any).down = down;
  (window as any).dir = dir;
  (window as any).go = go;
  (window as any).turn = turn;
  (window as any).hit = hit;
  (window as any).posX = posX;
  (window as any).posY = posY;
  (window as any).treasureX = treasureX;
  (window as any).treasureY = treasureY;
  (window as any).weaponX = weaponX;
  (window as any).weaponY = weaponY;
  (window as any).weapon = weapon;
  (window as any).see = see;
  (window as any).seeTreasure = seeTreasure;
  (window as any).seeWeapon = seeWeapon;
  (window as any).seeMonster = seeMonster;
}



// 获取武器图标
const getWeaponIcon = (weaponType: string): string => {
  const icons = {
    sword: '⚔️',
    staff: '🪄'
  }
  return icons[weaponType as keyof typeof icons] || '⚔️'
}

// 获取武器名称
const getWeaponName = (weaponType: string): string => {
  const names = {
    sword: '剑',
    staff: '魔杖'
  }
  return names[weaponType as keyof typeof names] || '未知武器'
}

// 获取怪物图标和颜色
const getMonsterIcon = (monsterType: string): string => {
  const icons = {
    physical: '👹', // 物理系怪物 - 红色
    magical: '🔮'   // 魔法系怪物 - 蓝色
  }
  return icons[monsterType as keyof typeof icons] || '👹'
}

const getMonsterColor = (monsterType: string): string => {
  const colors = {
    physical: '#DC143C', // 深红色 - 物理系
    magical: '#4169E1'   // 蓝色 - 魔法系
  }
  return colors[monsterType as keyof typeof colors] || '#DC143C'
}

// 获取武器颜色（与对应怪物类型相同）
const getWeaponColor = (weaponType: string): string => {
  const colors = {
    sword: '#DC143C',  // 深红色 - 对应物理系怪物
    staff: '#4169E1'   // 蓝色 - 对应魔法系怪物
  }
  return colors[weaponType as keyof typeof colors] || '#FFD700'
}

// 当前关卡数据
const currentLevel = computed(() => props.customLevel || props.levelData)



// 动画工具函数
const sleepWithSpeed = async (ms: number): Promise<void> => {
  // 根据动画速度调整等待时间
  const adjustedMs = ms / gameWorld.animationSpeed

  if (adjustedMs < 16.7) { // 小于一帧时间
    return new Promise(resolve => setTimeout(resolve, adjustedMs))
  } else {
    // 使用requestAnimationFrame优化长时间等待
    const startTime = performance.now()
    return new Promise(resolve => {
      const step = (currentTime: number) => {
        const elapsedTime = currentTime - startTime
        if (elapsedTime >= adjustedMs) {
          resolve()
        } else {
          requestAnimationFrame(step)
        }
      }
      requestAnimationFrame(step)
    })
  }
}

// 缓动函数 - 提供平滑的动画效果
const easeInOutQuad = (t: number): number => {
  return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t
}

// 线性插值函数
const lerp = (start: number, end: number, t: number): number => {
  return start + (end - start) * t
}

// 获取状态文本
const getStatusText = (): string => {
  switch (gameStatus.value) {
    case 'idle': return '准备开始'
    case 'playing': return '游戏进行中'
    case 'success': return '通关成功'
    case 'failed': return '游戏失败'
    default: return '未知状态'
  }
}


// 获取关卡难度星级
const getLevelDifficultyStars = (difficulty: number): string => {
  return '⭐'.repeat(Math.min(Math.max(difficulty || 1, 1), 10))
}

// 控制台相关方法
const addConsoleLog = (message: string, type: 'info' | 'warn' | 'error' | 'success' = 'info'): void => {
  if (gameConsoleRef.value) {
    gameConsoleRef.value.addLog(message, type)
  }
}

const clearConsole = (): void => {
  if (gameConsoleRef.value) {
    gameConsoleRef.value.clearLogs()
  }
}

// 控制台事件处理
const handleConsolePositionChange = (position: { x: number; y: number }): void => {
  // 可以在这里保存位置到本地存储等
}

const handleConsoleCollapseChange = (collapsed: boolean): void => {
  // 可以在这里保存折叠状态到本地存储等
}

// 运行信息管理
const addRuntimeLog = (message: string): void => {
  const now = new Date()
  const time = now.toLocaleTimeString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })

  runtimeLogs.value.push({
    time,
    message
  })

  // 自动滚动到底部
  nextTick(() => {
    if (runtimeLogsRef.value) {
      runtimeLogsRef.value.scrollTop = runtimeLogsRef.value.scrollHeight
    }
  })

  // 限制日志数量，避免内存泄漏
  if (runtimeLogs.value.length > 100) {
    runtimeLogs.value = runtimeLogs.value.slice(-50)
  }
}

const clearRuntimeLogs = (): void => {
  runtimeLogs.value = []
}

// printf 指令 - 输出信息到控制台
const printf = (...args: any[]): void => {
  const message = args.map(arg => {
    if (typeof arg === 'string') {
      return arg
    } else if (typeof arg === 'number') {
      return arg.toString()
    } else if (typeof arg === 'boolean') {
      return arg.toString()
    } else if (arg === null) {
      return 'null'
    } else if (arg === undefined) {
      return 'undefined'
    } else {
      return JSON.stringify(arg)
    }
  }).join(' ')

  addConsoleLog(message, 'info')
  addRuntimeLog(message)
}

// 初始化游戏画布
const initCanvas = (): CanvasRenderingContext2D | null => {
  if (!gameCanvas.value) return null

  const ctx = gameCanvas.value.getContext('2d')
  if (!ctx) return null

  // 设置画布样式
  ctx.imageSmoothingEnabled = false
  return ctx
}

// 绘制网格背景
const drawGrid = (ctx: CanvasRenderingContext2D): void => {
  const { WORLD_WIDTH, WORLD_HEIGHT, GRID_SIZE, GRID_COLOR } = GAME_CONFIG

  ctx.strokeStyle = GRID_COLOR
  ctx.lineWidth = 1

  // 绘制垂直线
  for (let x = 0; x <= WORLD_WIDTH; x += GRID_SIZE) {
    ctx.beginPath()
    ctx.moveTo(x, 0)
    ctx.lineTo(x, WORLD_HEIGHT)
    ctx.stroke()
  }

  // 绘制水平线
  for (let y = 0; y <= WORLD_HEIGHT; y += GRID_SIZE) {
    ctx.beginPath()
    ctx.moveTo(0, y)
    ctx.lineTo(WORLD_WIDTH, y)
    ctx.stroke()
  }
}

// 图片缓存
const imageCache = new Map<string, HTMLImageElement>()

// 加载图片的异步函数
const loadImage = async (src: string): Promise<HTMLImageElement> => {
  if (imageCache.has(src)) {
    return imageCache.get(src)!
  }

  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      imageCache.set(src, img)
      resolve(img)
    }
    img.onerror = reject
    img.src = src
  })
}

// 绘制游戏元素 - 支持自定义图片和动画
const drawElement = async (ctx: CanvasRenderingContext2D, element: GameElement): Promise<void> => {
  const { GRID_SIZE } = GAME_CONFIG

  // 获取当前位置（考虑动画）
  let currentPos = element.position
  if (element.animation?.isAnimating) {
    const elapsed = Date.now() - element.animation.startTime
    const progress = Math.min(elapsed / element.animation.duration, 1)
    const easedProgress = easeInOutQuad(progress)

    currentPos = {
      x: lerp(element.animation.startPos.x, element.animation.endPos.x, easedProgress),
      y: lerp(element.animation.startPos.y, element.animation.endPos.y, easedProgress)
    }

    // 动画完成时更新位置
    if (progress >= 1) {
      element.position = element.animation.endPos
      element.animation.isAnimating = false
    }
  }

  const pixelX = currentPos.x * GRID_SIZE
  const pixelY = currentPos.y * GRID_SIZE
  const size = GRID_SIZE * (element.size || 1)
  const offset = (GRID_SIZE - size) / 2

  // 如果有自定义图片，尝试绘制图片
  if (element.image) {
    try {
      const img = await loadImage(element.image)
      ctx.drawImage(img, pixelX + offset, pixelY + offset, size, size)
      return
    } catch (error) {
      console.warn(`Failed to load image: ${element.image}`, error)
      // 图片加载失败时回退到默认绘制
    }
  }

  // 默认绘制方式
  const color = element.color || getDefaultColor(element.type)
  const symbol = element.symbol || getDefaultSymbol(element.type)

  // 绘制背景
  ctx.fillStyle = color
  ctx.fillRect(pixelX + 2, pixelY + 2, GRID_SIZE - 4, GRID_SIZE - 4)

  // 绘制符号
  ctx.fillStyle = '#FFFFFF'
  ctx.font = `${GRID_SIZE * 0.6}px Arial`
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(symbol, pixelX + GRID_SIZE / 2, pixelY + GRID_SIZE / 2)

  // 为英雄添加方向指示
  if (element.type === 'hero') {
    const hero = element as HeroElement
    const direction = hero.direction || 'right'

    // 绘制方向箭头
    ctx.fillStyle = '#FFD700' // 金色箭头
    ctx.font = `${GRID_SIZE * 0.3}px Arial`

    let arrow = ''
    let arrowX = pixelX + GRID_SIZE / 2
    let arrowY = pixelY + GRID_SIZE / 2

    switch (direction) {
      case 'up':
        arrow = '↑'
        arrowY = pixelY + GRID_SIZE * 0.2
        break
      case 'right':
        arrow = '→'
        arrowX = pixelX + GRID_SIZE * 0.8
        break
      case 'down':
        arrow = '↓'
        arrowY = pixelY + GRID_SIZE * 0.8
        break
      case 'left':
        arrow = '←'
        arrowX = pixelX + GRID_SIZE * 0.2
        break
    }

    ctx.fillText(arrow, arrowX, arrowY)
  }

  // 特殊效果
  if (element.type === 'treasure' && (element as TreasureElement).sparkle) {
    drawSparkleEffect(ctx, pixelX + GRID_SIZE / 2, pixelY + GRID_SIZE / 2)
  }
}

// 获取默认颜色
const getDefaultColor = (type: GameElement['type']): string => {
  switch (type) {
    case 'hero': return GAME_CONFIG.HERO_COLOR
    case 'treasure': return GAME_CONFIG.TREASURE_COLOR
    case 'obstacle': return GAME_CONFIG.OBSTACLE_COLOR
    case 'monster': return '#DC143C'
    case 'weapon': return '#DC143C' // 武器默认使用物理系颜色
    default: return '#CCCCCC'
  }
}

// 获取默认符号
const getDefaultSymbol = (type: GameElement['type']): string => {
  switch (type) {
    case 'hero': return '🦸'
    case 'treasure': return '💰'
    case 'obstacle': return '🧱'
    case 'monster': return '👹'
    case 'weapon': return '⚔️'
    default: return '❓'
  }
}

// 绘制闪烁效果
const drawSparkleEffect = (ctx: CanvasRenderingContext2D, centerX: number, centerY: number): void => {
  const time = Date.now() * 0.005
  const sparkles = [
    { x: centerX - 15, y: centerY - 15, phase: 0 },
    { x: centerX + 15, y: centerY - 15, phase: Math.PI / 2 },
    { x: centerX - 15, y: centerY + 15, phase: Math.PI },
    { x: centerX + 15, y: centerY + 15, phase: Math.PI * 1.5 }
  ]

  sparkles.forEach(sparkle => {
    const alpha = (Math.sin(time + sparkle.phase) + 1) / 2
    ctx.fillStyle = `rgba(255, 255, 0, ${alpha * 0.8})`
    ctx.fillRect(sparkle.x - 2, sparkle.y - 2, 4, 4)
  })
}

// 渲染游戏世界
const renderGame = async (): Promise<void> => {
  const ctx = initCanvas()
  if (!ctx) return

  // 清空画布
  ctx.fillStyle = GAME_CONFIG.BACKGROUND_COLOR
  ctx.fillRect(0, 0, GAME_CONFIG.WORLD_WIDTH, GAME_CONFIG.WORLD_HEIGHT)

  // 绘制网格
  drawGrid(ctx)

  // 绘制障碍物
  for (const obstacle of gameWorld.obstacles) {
    await drawElement(ctx, obstacle)
  }

  // 绘制武器（未收集的）
  for (const weapon of gameWorld.weapons) {
    if (!weapon.collected) {
      await drawElement(ctx, weapon)
    }
  }

  // 绘制宝箱（未收集的）
  for (const treasure of gameWorld.treasures) {
    const treasureKey = `${treasure.position.x}-${treasure.position.y}`
    if (!gameWorld.collectedTreasures.has(treasureKey) && !treasure.collected) {
      await drawElement(ctx, treasure)
    }
  }

  // 绘制怪物
  for (const monster of gameWorld.monsters) {
    await drawElement(ctx, monster)
  }

  // 绘制英雄
  if (gameWorld.hero) {
    await drawElement(ctx, gameWorld.hero)
  }
}

// 获取当前关卡的当前地图数据
const getCurrentMapData = (): MapData | null => {
  const level = currentLevel.value
  if (!level) return null

  // 兼容旧格式：如果没有maps数组，从旧字段构造MapData
  if (!level.maps || level.maps.length === 0) {
    // 旧格式兼容
    const oldLevel = level as any
    if (oldLevel.heroStart) {
      return {
        id: 1,
        name: '默认地图',
        heroStart: oldLevel.heroStart,
        treasures: oldLevel.treasures || [],
        obstacles: oldLevel.obstacles || [],
        monsters: oldLevel.monsters || [],
        weapons: oldLevel.weapons || []
      }
    }
    return null
  }

  // 确保索引在有效范围内
  const mapIndex = Math.min(currentMapIndex.value, level.maps.length - 1)
  return level.maps[mapIndex]
}

// 随机选择地图
const selectRandomMap = (): void => {
  const level = currentLevel.value
  if (!level || !level.maps || level.maps.length === 0) return

  // 随机选择一个地图索引
  currentMapIndex.value = Math.floor(Math.random() * level.maps.length)
  console.log(`随机选择地图: ${currentMapIndex.value + 1}/${level.maps.length} - ${level.maps[currentMapIndex.value].name}`)
}

// 初始化关卡
const initLevel = async (useRandomMap: boolean = false): Promise<void> => {
  const level = currentLevel.value
  if (!level) return

  // 如果需要随机选择地图，则随机选择；否则使用第一个地图
  if (useRandomMap) {
    selectRandomMap()
  } else {
    currentMapIndex.value = 0 // 默认使用第一个地图
  }

  const mapData = getCurrentMapData()
  if (!mapData) return

  // 重置游戏世界
  gameWorld.hero = {
    id: 'hero',
    type: 'hero',
    position: { ...mapData.heroStart },
    direction: 'right',
    symbol: gameSettings.heroIcon,
    image: gameSettings.heroImage,
    color: GAME_CONFIG.HERO_COLOR
  } as HeroElement

  gameWorld.treasures = mapData.treasures.map((pos, index) => ({
    id: `treasure-${index}`,
    type: 'treasure',
    position: { ...pos },
    symbol: gameSettings.treasureIcon,
    image: gameSettings.treasureImage,
    color: GAME_CONFIG.TREASURE_COLOR,
    collected: false,
    sparkle: true
  } as TreasureElement))

  gameWorld.obstacles = mapData.obstacles.map((pos, index) => ({
    id: `obstacle-${index}`,
    type: 'obstacle',
    position: { ...pos },
    symbol: gameSettings.obstacleIcon,
    image: gameSettings.obstacleImage,
    color: GAME_CONFIG.OBSTACLE_COLOR,
    destructible: false
  } as ObstacleElement))

  // 初始化怪物
  gameWorld.monsters = (mapData.monsters || []).map((monster, index) => ({
    id: `monster-${index}`,
    type: 'monster',
    position: { x: monster.x, y: monster.y },
    symbol: getMonsterIcon(monster.type),
    image: monster.type === 'physical' ? gameSettings.physicalMonsterImage : gameSettings.magicalMonsterImage,
    color: getMonsterColor(monster.type),
    monsterType: monster.type,
    alive: true
  } as MonsterElement))

  // 初始化武器
  gameWorld.weapons = (mapData.weapons || []).map((weapon, index) => ({
    id: `weapon-${index}`,
    type: 'weapon',
    position: { x: weapon.x, y: weapon.y },
    symbol: getWeaponIcon(weapon.type),
    image: weapon.type === 'sword' ? gameSettings.swordImage : gameSettings.staffImage,
    color: getWeaponColor(weapon.type),
    weaponType: weapon.type,
    collected: false
  } as WeaponElement))

  gameWorld.collectedTreasures.clear()

  // 重置英雄武器
  heroWeapon.value = null

  // 重置游戏状态
  gameStatus.value = 'idle'
  showOverlay.value = false
  failureReason.value = ''

  // 设置代码编辑器的初始代码
  // 如果是第一关（id=101），默认填充答案；否则填充占位符
  // 注意：只有在代码为空时才设置，避免覆盖用户已输入的代码
  if (!userCode.value || userCode.value.trim() === '') {
    if (level.id === 101 && level.answer) {
      userCode.value = level.answer
    } else {
      userCode.value = level.codePlaceholder || ''
    }
  }

  // 渲染游戏世界
  await renderGame()
}

// 检查位置是否有效（不越界且不碰撞障碍物）
const isValidPosition = (pos: Position): boolean => {
  const { WORLD_WIDTH, WORLD_HEIGHT, GRID_SIZE } = GAME_CONFIG
  const maxX = Math.floor(WORLD_WIDTH / GRID_SIZE) - 1
  const maxY = Math.floor(WORLD_HEIGHT / GRID_SIZE) - 1

  // 检查边界
  if (pos.x < 0 || pos.x > maxX || pos.y < 0 || pos.y > maxY) {
    return false
  }

  // 检查障碍物碰撞
  return !gameWorld.obstacles.some(obstacle =>
    obstacle.position.x === pos.x && obstacle.position.y === pos.y
  )
}

// 检查是否收集到宝箱
const checkTreasureCollection = (): void => {
  if (!gameWorld.hero) return

  gameWorld.treasures.forEach(treasure => {
    const treasureKey = `${treasure.position.x}-${treasure.position.y}`
    if (treasure.position.x === gameWorld.hero!.position.x &&
        treasure.position.y === gameWorld.hero!.position.y &&
        !gameWorld.collectedTreasures.has(treasureKey) &&
        !treasure.collected) {
      gameWorld.collectedTreasures.add(treasureKey)
      treasure.collected = true
      treasure.sparkle = false
      ElMessage.success('🎉 收集到宝箱！')
    }
  })
}

// 检查武器收集
const checkWeaponCollection = (): void => {
  if (!gameWorld.hero) return

  const heroPos = gameWorld.hero.position

  gameWorld.weapons.forEach(weapon => {
    if (!weapon.collected && weapon.position.x === heroPos.x && weapon.position.y === heroPos.y) {
      weapon.collected = true

      // 如果已有武器，替换掉
      if (heroWeapon.value) {
        const oldWeaponName = getWeaponName(heroWeapon.value.weaponType)
        const newWeaponName = getWeaponName(weapon.weaponType)
        ElMessage.info(`替换了武器：${oldWeaponName} → ${newWeaponName}`)
      } else {
        const weaponName = getWeaponName(weapon.weaponType)
        ElMessage.success(`装备了${weaponName}`)
      }

      heroWeapon.value = weapon
    }
  })
}

// 检查怪物碰撞
const checkMonsterCollision = (): void => {
  if (!gameWorld.hero) return

  const heroPos = gameWorld.hero.position

  // 检查是否与存活的怪物碰撞
  const collidedMonster = gameWorld.monsters.find(monster =>
    monster.alive && monster.position.x === heroPos.x && monster.position.y === heroPos.y
  )

  if (collidedMonster) {
    // 英雄死亡
    gameStatus.value = 'failed'
    showOverlay.value = true
    const monsterIcon = getMonsterIcon(collidedMonster.monsterType)
    failureReason.value = `被${monsterIcon}怪物击败了！英雄需要先用正确的武器击败怪物才能通过。`
    ElMessage.error('游戏失败！英雄被怪物击败了！')
  }
}

// 检查游戏胜利条件
const checkWinCondition = (): boolean => {
  return gameWorld.collectedTreasures.size === gameWorld.treasures.length
}

// 移动英雄的通用方法 - 带动画效果
const moveHero = async (deltaX: number, deltaY: number): Promise<void> => {
  if (gameStatus.value !== 'playing' || !gameWorld.hero) return

  const newPos = {
    x: gameWorld.hero.position.x + deltaX,
    y: gameWorld.hero.position.y + deltaY
  }

  if (isValidPosition(newPos)) {
    // 设置英雄朝向
    if (deltaX > 0) gameWorld.hero.direction = 'right'
    else if (deltaX < 0) gameWorld.hero.direction = 'left'
    else if (deltaY > 0) gameWorld.hero.direction = 'down'
    else if (deltaY < 0) gameWorld.hero.direction = 'up'

    // 开始移动动画
    gameWorld.hero.animation = {
      isAnimating: true,
      startPos: { ...gameWorld.hero.position },
      endPos: newPos,
      startTime: Date.now(),
      duration: GAME_CONFIG.ANIMATION_SPEED / gameWorld.animationSpeed
    }

    // 动画循环
    return new Promise<void>((resolve) => {
      const animateMove = async () => {
        await renderGame()

        if (gameWorld.hero?.animation?.isAnimating) {
          requestAnimationFrame(animateMove)
        } else {
          // 动画完成，检查游戏状态
          checkTreasureCollection()
          checkWeaponCollection()
          checkMonsterCollision()

          // 检查胜利条件
          if (checkWinCondition()) {
            gameStatus.value = 'success'
            showOverlay.value = true
            emit('levelComplete')
            ElMessage.success('🎉 关卡完成！')
          }

          resolve()
        }
      }

      requestAnimationFrame(animateMove)
    })
  } else {
    // 碰撞处理
    gameStatus.value = 'failed'
    failureReason.value = '撞到障碍物或越界了！请重新尝试。'
    showOverlay.value = true
    emit('gameEnd', false)
    ElMessage.error('💥 移动失败！')
  }
}

// 游戏API - 提供给用户代码调用的移动方法
const createGameAPI = () => {
  return {
    // 旧的移动指令（保持兼容性）
    moveRight: async (): Promise<void> => {
      await moveHero(1, 0)
    },
    moveLeft: async (): Promise<void> => {
      await moveHero(-1, 0)
    },
    moveUp: async (): Promise<void> => {
      await moveHero(0, -1)
    },
    moveDown: async (): Promise<void> => {
      await moveHero(0, 1)
    },
    getHeroPosition: (): Position => {
      return gameWorld.hero ? { ...gameWorld.hero.position } : { x: 0, y: 0 }
    },

    // 新的简化移动指令
    right: right,
    left: left,
    up: up,
    down: down,

    // 输出指令
    printf: printf,

    // 重写 console.log 输出到运行信息模块
    console: {
      log: (...args: any[]) => {
        const message = args.map(arg => {
          if (typeof arg === 'string') {
            return arg
          } else if (typeof arg === 'number') {
            return arg.toString()
          } else if (typeof arg === 'boolean') {
            return arg.toString()
          } else if (arg === null) {
            return 'null'
          } else if (arg === undefined) {
            return 'undefined'
          } else {
            return JSON.stringify(arg)
          }
        }).join(' ')

        console.log(message)
      }
    },

    // 高级移动指令
    dir: dir,
    go: go,
    turn: turn,

    // 位置获取指令
    posX: posX,
    posY: posY,
    treasureX: treasureX,
    treasureY: treasureY,
    weaponX: weaponX,
    weaponY: weaponY,
    weapon: weapon,

    // 方向检测指令
    see: see,
    seeTreasure: seeTreasure,
    seeWeapon: seeWeapon,
    seeMonster: seeMonster,

    // 位置检测指令
    hasObstacle: (x: number, y: number): boolean => {
      const result = gameWorld.obstacles.some(obstacle =>
        obstacle.position.x === x && obstacle.position.y === y
      )
      addConsoleLog(`hasObstacle(${x}, ${y}) 返回: ${result} (位置${result ? '有' : '无'}障碍物)`, 'info')
      addRuntimeLog(`hasObstacle(${x}, ${y}) 返回: ${result} (位置${result ? '有' : '无'}障碍物)`)
      return result
    },
    hasTreasure: (x: number, y: number): boolean => {
      const treasureKey = `${x}-${y}`
      const result = gameWorld.treasures.some(treasure =>
        treasure.position.x === x &&
        treasure.position.y === y &&
        !gameWorld.collectedTreasures.has(treasureKey) &&
        !treasure.collected
      )
      addConsoleLog(`hasTreasure(${x}, ${y}) 返回: ${result} (位置${result ? '有' : '无'}宝箱)`, 'info')
      addRuntimeLog(`hasTreasure(${x}, ${y}) 返回: ${result} (位置${result ? '有' : '无'}宝箱)`)
      return result
    },

    // 设置动画速度
    setAnimationSpeed: (speed: number): void => {
      gameWorld.animationSpeed = Math.max(0.1, Math.min(10, speed))
    },

    // 高级检测指令
    fd: fd,
    rt: rt,
    direction: direction,
    thereObstacles: thereObstacles,
    reachBoundary: reachBoundary,
    isThereObstacles: isThereObstacles,
    isReachBoundary: isReachBoundary,

    // 战斗指令
    attack: attack,
    hit: hit
  }
}

// 在关键词前插入await - 参考CodeRunner实现
const translateAwait = (code: string, key: string): string => {
  // 转义关键字中的正则特殊字符
  const escapedKey = key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')

  // 匹配关键字前的边界，且前面没有 await 的情况
  const regex = new RegExp(
    `(?<!\\bawait\\s+)(\\b|\\s)(\\s*)(${escapedKey})`,
    'g'
  )

  return code.replace(regex, (match, boundary, whitespace, keyPart) => {
    // 插入带有空格的 await
    return `${boundary}${whitespace}await ${keyPart}`
  })
}

// 为多个关键字插入await
const translateAwaitCode = (code: string, ...keys: string[]): string => {
  for (const key of keys) {
    code = translateAwait(code, key)
  }
  return code
}

// 执行用户代码
const runUserCode = async (): Promise<void> => {
  if (!userCode.value.trim()) {
    ElMessage.warning('请先编写代码！')
    return
  }

  // 开始执行代码
  clearConsole()
  clearRuntimeLogs()
  addConsoleLog('🚀 开始执行代码...', 'info')

  // 清空命令队列
  commandQueue = []
  isExecutingQueue = false

  // 运行代码时随机选择地图
  await initLevel(true)

  // 自动开始游戏
  if (gameStatus.value !== 'playing') {
    gameStatus.value = 'playing'
    emit('gameStart')
  }

  isRunning.value = true

  try {
    // 创建游戏API
    const gameAPI = createGameAPI()

    // 处理用户代码 - 自动添加await
    let processedCode = userCode.value.trim()
    processedCode = translateAwaitCode(
      processedCode,
      // 旧指令
      'moveRight()', 'moveLeft()', 'moveUp()', 'moveDown()',
      'getHeroPosition()', 'hasObstacle(', 'hasTreasure(', 'setAnimationSpeed(',
      'fd()', 'rt(', 'direction(', 'thereObstacles()', 'reachBoundary()', 'isThereObstacles(', 'isReachBoundary(',
      'attack()',
      // 新的简化指令
      'right()', 'left()', 'up()', 'down()',
      'dir(', 'go(', 'turn()', 'hit()',
      'posX()', 'posY()', 'treasureX()', 'treasureY()', 'weaponX(', 'weaponY(', 'weapon()',
      'printf(',
      'see(', 'seeTreasure(', 'seeWeapon(', 'seeMonster('
    )

    // 创建异步函数包装器
    const asyncWrapper = `
      return (async (gameAPI) => {
        const {
          moveRight, moveLeft, moveUp, moveDown, getHeroPosition, hasObstacle, hasTreasure, setAnimationSpeed,
          right, left, up, down, dir, go, turn, hit, posX, posY, treasureX, treasureY, weaponX, weaponY, weapon, printf,
          see, seeTreasure, seeWeapon, seeMonster
        } = gameAPI;
        try {
          ${processedCode}
        } catch (e) {
          console.error('用户代码执行错误:', e);
          throw e;
        }
      })(gameAPI);
    `

    // 创建并执行异步函数
    const asyncFn = new Function('gameAPI', asyncWrapper)
    await asyncFn(gameAPI) // 等待用户代码执行完成

  } catch (error) {
    console.error('代码执行错误:', error)
    addConsoleLog(`❌ 代码执行错误: ${error instanceof Error ? error.message : '未知错误'}`, 'error')
    gameStatus.value = 'failed'
    failureReason.value = `代码执行出错：${error instanceof Error ? error.message : '未知错误'}`
    showOverlay.value = true
    emit('gameEnd', false)
    ElMessage.error('代码执行失败！请检查语法。')
  } finally {
    isRunning.value = false
    addConsoleLog('✅ 代码执行完成', 'success')
  }
}

// 开始游戏
const startGame = (): void => {
  gameStatus.value = 'playing'
  showOverlay.value = false
  emit('gameStart')
  ElMessage.info('游戏开始！请运行你的代码。')
}

// 重置游戏
const resetGame = async (): Promise<void> => {
  // 清空命令队列
  commandQueue = []
  isExecutingQueue = false
  clearRuntimeLogs()

  await initLevel()
  // 不清空代码，只重置游戏状态
  ElMessage.info('游戏已重置')
}

// 关闭失败消息
const closeFailureMessage = (): void => {
  gameStatus.value = 'idle'
  failureReason.value = ''
  showOverlay.value = false
}

// 关闭成功消息
const closeSuccessMessage = (): void => {
  gameStatus.value = 'idle'
  showOverlay.value = false
}



// 清空代码
const clearCode = (): void => {
  userCode.value = ''
  if (codeEditorRef.value && typeof codeEditorRef.value.setValue === 'function') {
    codeEditorRef.value.setValue('')
  }
}

// 显示答案
const showAnswer = (): void => {
  const currentLevelData = currentLevel.value
  if (!currentLevelData) {
    ElMessage.error('当前关卡数据不存在')
    return
  }

  // 确认对话框
  ElMessageBox.confirm(
    `确定要查看关卡 ${currentLevelData.id} 的答案吗？这将替换当前编辑器中的代码。`,
    '查看答案',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    }
  ).then(() => {
    // 用户确认后填充答案
    userCode.value = currentLevelData.answer
    // 使用 nextTick 确保 userCode 更新后再同步到编辑器
    nextTick(() => {
      if (codeEditorRef.value && typeof codeEditorRef.value.setValue === 'function') {
        codeEditorRef.value.setValue(currentLevelData.answer)
      }
    })
    ElMessage.success('答案已填充到编辑器中')
  }).catch(() => {
    // 用户取消
    ElMessage.info('已取消查看答案')
  })
}

// 重新运行（重置后运行）
const resetAndRun = async (): Promise<void> => {
  // 延迟后运行代码（runUserCode会自动重置并随机选择地图）
  setTimeout(() => {
    runUserCode()
  }, 100)
}

// 界面控制方法
const toggleLevelInfo = (): void => {
  showLevelInfo.value = !showLevelInfo.value
  if (showLevelInfo.value) {
    showSettings.value = false
  }
}

const toggleSettings = (): void => {
  showSettings.value = !showSettings.value
  if (showSettings.value) {
    showLevelInfo.value = false
  }
}

// 设置组件事件处理
const handleSettingsUpdate = (settings: any): void => {
  Object.assign(gameSettings, settings)
}

const handleSpeedChange = (speed: number): void => {
  gameWorld.animationSpeed = speed
}

const handleIconChange = (type: string, icon: string): void => {
  switch (type) {
    case 'hero':
      if (gameWorld.hero) {
        gameWorld.hero.symbol = icon
        renderGame()
      }
      break
    case 'treasure':
      gameWorld.treasures.forEach(treasure => {
        treasure.symbol = icon
      })
      renderGame()
      break
    case 'obstacle':
      gameWorld.obstacles.forEach(obstacle => {
        obstacle.symbol = icon
      })
      renderGame()
      break
  }
}

const handleImageChange = (type: string): void => {
  switch (type) {
    case 'hero':
      if (gameWorld.hero) {
        gameWorld.hero.image = gameSettings.heroImage
        renderGame()
      }
      break
    case 'treasure':
      gameWorld.treasures.forEach(treasure => {
        treasure.image = gameSettings.treasureImage
      })
      renderGame()
      break
    case 'obstacle':
      gameWorld.obstacles.forEach(obstacle => {
        obstacle.image = gameSettings.obstacleImage
      })
      renderGame()
      break
    case 'weapon':
      gameWorld.weapons.forEach(weapon => {
        if (weapon.weaponType === 'sword') {
          weapon.image = gameSettings.swordImage
        } else if (weapon.weaponType === 'staff') {
          weapon.image = gameSettings.staffImage
        }
      })
      if (heroWeapon.value) {
        if (heroWeapon.value.weaponType === 'sword') {
          heroWeapon.value.image = gameSettings.swordImage
        } else if (heroWeapon.value.weaponType === 'staff') {
          heroWeapon.value.image = gameSettings.staffImage
        }
      }
      renderGame()
      break
    case 'monster':
      gameWorld.monsters.forEach(monster => {
        if (monster.monsterType === 'physical') {
          monster.image = gameSettings.physicalMonsterImage
        } else if (monster.monsterType === 'magical') {
          monster.image = gameSettings.magicalMonsterImage
        }
      })
      renderGame()
      break
  }
}







// 代码变化处理
const handleCodeChange = (code: string): void => {
  userCode.value = code
}

// 拖拽调整面板大小
const startResize = (e: MouseEvent | TouchEvent): void => {
  isResizing.value = true

  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  startX.value = clientX
  startWidth.value = leftPanelWidth.value

  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
  document.addEventListener('touchmove', handleResize)
  document.addEventListener('touchend', stopResize)

  e.preventDefault()
}

const handleResize = (e: MouseEvent | TouchEvent): void => {
  if (!isResizing.value) return

  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  const deltaX = clientX - startX.value
  const containerWidth = window.innerWidth
  const deltaPercent = (deltaX / containerWidth) * 100

  const newWidth = startWidth.value + deltaPercent
  // 限制拖拽范围，确保两侧都有足够空间显示内容，增加游戏空间
  leftPanelWidth.value = Math.max(40, Math.min(70, newWidth))

  e.preventDefault()
}

const stopResize = (): void => {
  isResizing.value = false

  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  document.removeEventListener('touchmove', handleResize)
  document.removeEventListener('touchend', stopResize)
}

// 窗口大小变化处理
const handleWindowResize = (): void => {
  // 重新渲染游戏以适应新的画布尺寸
  nextTick(async () => {
    await renderGame()
  })
}

// 监听关卡数据变化
watch(() => [props.levelData, props.customLevel], async ([newLevelData, newCustomLevel], [oldLevelData, oldCustomLevel]) => {
  const newLevel = newLevelData || newCustomLevel
  const oldLevel = oldLevelData || oldCustomLevel
  const newLevelId = newLevel?.id
  const oldLevelId = oldLevel?.id
  
  // 如果关卡ID没有变化，说明是应用关卡操作（数据更新但ID相同），只重新渲染不重新初始化
  if (newLevelId === oldLevelId && newLevelId === lastLevelId.value && newLevel) {
    // 这是应用关卡操作，不重新初始化，但需要重新渲染游戏
    await renderGame()
    emit('levelApplied')
    return
  }
  
  // 关卡ID变化了，需要重新初始化
  if (newLevel && newLevelId !== lastLevelId.value) {
    lastLevelId.value = newLevelId
    await initLevel()
  }
}, { immediate: false })

// 组件挂载时初始化
onMounted(async () => {
  // 从 URL 加载分享的代码
  loadCodeFromUrl()
  nextTick(async () => {
    const level = currentLevel.value
    if (level) {
      lastLevelId.value = level.id
    }
    await initLevel()

    // 监听窗口大小变化
    window.addEventListener('resize', handleWindowResize)

    // 启动动画循环（用于闪烁效果等）
    const animationLoop = async () => {
      if (gameStatus.value === 'playing') {
        await renderGame()
      }
      requestAnimationFrame(animationLoop)
    }
    requestAnimationFrame(animationLoop)
  })
})

// 组件卸载前清理
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleWindowResize)

  // 清理拖拽事件监听器
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  document.removeEventListener('touchmove', handleResize)
  document.removeEventListener('touchend', stopResize)
})

// 暴露组件方法
defineExpose({
  resetGame,
  runUserCode,
  resetAndRun,
  clearCode,
  showAnswer,
  // 获取当前游戏状态
  getGameStatus: () => gameStatus.value,
  getCurrentLevel: () => currentLevel.value?.id || 0,

  // 设置动画速度
  setAnimationSpeed: (speed: number) => {
    animationSpeed.value = speed
    gameWorld.animationSpeed = Math.max(0.1, Math.min(10, speed))
  },

  // 界面控制
  toggleLevelInfo,
  toggleSettings,

  // 面板宽度控制
  setLeftPanelWidth: (width: number) => {
    leftPanelWidth.value = Math.max(30, Math.min(70, width))
  },

  // 添加自定义游戏元素
  addCustomElement: (element: GameElement) => {
    switch (element.type) {
      case 'hero':
        gameWorld.hero = element as HeroElement
        break
      case 'treasure':
        gameWorld.treasures.push(element as TreasureElement)
        break
      case 'obstacle':
        gameWorld.obstacles.push(element as ObstacleElement)
        break
    }
    renderGame()
  },

  // 移除游戏元素
  removeElement: (id: string) => {
    if (gameWorld.hero?.id === id) {
      gameWorld.hero = null
    }
    gameWorld.treasures = gameWorld.treasures.filter(t => t.id !== id)
    gameWorld.obstacles = gameWorld.obstacles.filter(o => o.id !== id)
    renderGame()
  },



  // 消息控制
  closeFailureMessage,

  // 获取设置
  getGameSettings: () => gameSettings
})
</script>

<style scoped>
.move-code-game-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: linear-gradient(135deg, #E0F7FA 0%, #B0E0E6 50%, #87CEEB 100%);
  overflow: hidden;
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 8px 16px;
  box-shadow: 0 2px 10px rgba(79, 195, 247, 0.1);
  z-index: 10;
  flex-shrink: 0;
  border-bottom: 1px solid rgba(79, 195, 247, 0.2);
  min-height: 48px;
}


.game-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.level-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 15px;
  gap: 4px;
}

.level-number {
  color: #1e88e5;
  font-size: 16px;
  font-weight: 600;
}

.level-title {
  color: #1565c0;
  font-size: 13px;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.level-difficulty {
  font-size: 12px;
}

.map-info {
  color: #27ae60;
  font-size: 12px;
  background: rgba(39, 174, 96, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 8px;
}

.game-controls {
  display: flex;
  gap: 12px;
}

.game-content {
  display: flex;
  flex: 1;
  overflow: hidden;
  position: relative;
}

/* 左侧游戏世界容器 */
.game-world-container {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
  min-width: 0; /* 确保flex子元素可以收缩 */
}

/* 悬浮球样式 */
.floating-ball {
  position: absolute;
  top: 20px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 20;
}

.floating-ball:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 1);
}

.level-info-ball {
  right: 80px;
  color: #4A90E2;
}



/* 信息面板样式 */
.level-info-panel {
  position: absolute;
  top: 80px;
  right: 20px;
  width: 320px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  z-index: 15;
  overflow: hidden;
  border: 1px solid rgba(79, 195, 247, 0.2);
}

.panel-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(79, 195, 247, 0.05);
  border-bottom: 1px solid rgba(79, 195, 247, 0.1);
}

.panel-header h3 {
  margin: 0;
  color: #1e88e5;
  font-size: 18px;
  font-weight: 600;
}

.panel-content {
  padding: 16px 20px;
  max-height: 500px;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 20px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.info-section h4 {
  margin: 0 0 8px 0;
  color: #1565c0;
  font-size: 14px;
  font-weight: 600;
}

.info-section p {
  margin: 0;
  color: #424242;
  font-size: 13px;
  line-height: 1.6;
}

.commands-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.command-tag {
  margin: 0;
  font-size: 12px;
  font-family: 'Courier New', monospace;
  background: rgba(79, 195, 247, 0.1);
  color: #1e88e5;
  border: 1px solid rgba(79, 195, 247, 0.3);
}

/* 导入对话框样式 */
.import-dialog-content {
  padding: 10px 0;
}

.import-text-area {
  margin-top: 20px;
}





.game-world {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  position: relative;
  min-height: 0; /* 确保flex子元素可以收缩 */
  overflow: auto; /* 如果内容超出则显示滚动条 */
}

/* 武器框样式 */
.weapon-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
  padding: 8px;
}

/* 内联武器框样式 */
.weapon-bar-inline {
  display: flex;
  align-items: center;
  margin: 0 8px;
}

/* 悬浮武器框样式 */
.floating-weapon-bar {
  position: absolute;
  top: 20px;
  right: 140px; /* 位于关卡信息按钮的左侧（80px + 48px + 12px间距） */
  z-index: 10;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.weapon-slot {
  display: flex;
  align-items: center;
  justify-content: center;
}

.weapon-display {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 背景色通过内联样式动态设置 */
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: 2px solid rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s ease;
}

.weapon-image {
  width: 36px;
  height: 36px;
  object-fit: contain;
  border-radius: 4px;
}

.weapon-icon {
  font-size: 24px;
  color: white;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.empty-weapon-slot {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed #bdc3c7;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(5px);
}

.empty-text {
  font-size: 10px;
  color: #7f8c8d;
  font-weight: bold;
}





/* 拖拽分隔条 */
.resize-handle {
  width: 8px;
  background: rgba(255, 255, 255, 0.3);
  cursor: col-resize;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.resize-handle:hover {
  background: rgba(255, 255, 255, 0.5);
}

.resize-handle-line {
  width: 2px;
  height: 40px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 1px;
  transition: all 0.2s ease;
}

.resize-handle:hover .resize-handle-line {
  background: rgba(255, 255, 255, 1);
  height: 60px;
}

/* 右侧代码编辑器容器 */
.code-editor-container {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.code-editor-wrapper {
  flex: 1;
  padding: 20px;
  overflow: hidden;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.editor-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(79, 195, 247, 0.2);
}

.console-wrapper {
  flex-shrink: 0;
  height: 250px;
  border-top: 1px solid rgba(79, 195, 247, 0.2);
  background: rgba(255, 255, 255, 0.95);
  position: relative;
  overflow: hidden;
}

.console-wrapper :deep(.game-console) {
  position: relative;
  width: 100%;
  height: 100%;
  left: 0 !important;
  top: 0 !important;
  box-shadow: none;
  border-radius: 0;
}

.console-wrapper :deep(.console-header) {
  cursor: default;
  background: rgba(79, 195, 247, 0.05);
  border-bottom: 1px solid rgba(79, 195, 247, 0.1);
}

.console-wrapper :deep(.console-content) {
  height: calc(100% - 40px);
  flex: 1;
}

.game-canvas {
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.game-canvas:hover {
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.15);
}

.game-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  backdrop-filter: blur(5px);
}

.overlay-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  text-align: center;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.success-message h3 {
  color: #27ae60;
  margin-bottom: 10px;
  font-size: 20px;
}

.failed-message h3 {
  color: #e74c3c;
  margin-bottom: 10px;
  font-size: 20px;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.message-header h3 {
  margin: 0;
}

.close-button {
  color: #999;
  padding: 4px;
  min-width: auto;
}

.close-button:hover {
  color: #e74c3c;
  background-color: rgba(231, 76, 60, 0.1);
}

.message-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 20px;
}

.message-actions .el-button {
  min-width: 80px;
}

/* 面板滑动动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
  transform-origin: top;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}

.slide-down-enter-to,
.slide-down-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .game-content {
    flex-direction: column;
  }

  .game-world-container,
  .code-editor-container {
    width: 100% !important;
  }

  .resize-handle {
    display: none;
  }

  .level-info-panel {
    width: 280px;
    right: 10px;
  }
}

@media (max-width: 768px) {
  .move-code-game-container {
    padding: 0;
  }

  .game-header {
    padding: 12px 16px;
    flex-direction: column;
    gap: 12px;
  }


  .game-controls {
    justify-content: center;
    flex-wrap: wrap;
    gap: 8px;
  }

  .floating-ball {
    width: 40px;
    height: 40px;
    top: 15px;
    right: 15px;
  }

  .level-info-panel {
    width: calc(100vw - 30px);
    right: 15px;
    top: 70px;
  }

  .game-world,
  .code-editor-wrapper {
    padding: 15px;
  }

  .panel-content {
    max-height: 300px;
  }
}

@media (max-width: 480px) {
  .game-header {
    padding: 10px 12px;
  }


  .game-controls {
    gap: 6px;
  }

  .floating-ball {
    width: 36px;
    height: 36px;
    top: 12px;
    right: 12px;
  }

  .level-info-panel {
    width: calc(100vw - 24px);
    right: 12px;
    top: 60px;
  }

  .game-world,
  .code-editor-wrapper {
    padding: 12px;
  }
}

/* 按钮样式增强 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 标签样式增强 */
.el-tag {
  border-radius: 6px;
  font-weight: 500;
  border: none;
}

/* 滑块样式 */
:deep(.el-slider) {
  .el-slider__runway {
    background-color: rgba(255, 255, 255, 0.3);
  }

  .el-slider__bar {
    background-color: #4A90E2;
  }

  .el-slider__button {
    border: 2px solid #4A90E2;
    background-color: white;
  }
}

/* 运行信息模块样式 */
.runtime-info {
  background: linear-gradient(135deg, #f0f9ff 0%, #e8f5e9 100%);
  border-radius: 8px;
  margin-top: 12px;
  border: 1px solid rgba(76, 175, 80, 0.2);
}

.runtime-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: rgba(76, 175, 80, 0.08);
  border-radius: 8px 8px 0 0;
}

.runtime-title {
  font-size: 13px;
  font-weight: 600;
  color: #2e7d32;
}

.clear-btn {
  color: rgba(0, 0, 0, 0.45) !important;
  font-size: 12px !important;
  padding: 2px 8px !important;
  border-radius: 4px !important;
  transition: all 0.2s ease;
}

.clear-btn:hover {
  background: rgba(255, 255, 255, 0.25) !important;
}

.runtime-content {
  padding: 0;
}

.runtime-logs {
  max-height: 120px;
  overflow-y: auto;
  padding: 12px 16px;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.4;
}

.runtime-log {
  margin-bottom: 4px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.runtime-log .log-time {
  color: #888;
  font-size: 11px;
  white-space: nowrap;
  min-width: 60px;
}

.runtime-log .log-message {
  color: #2c3e50;
  word-break: break-word;
  flex: 1;
}

/* 运行信息滚动条样式 */
.runtime-logs::-webkit-scrollbar {
  width: 6px;
}

.runtime-logs::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.runtime-logs::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.runtime-logs::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

</style>
