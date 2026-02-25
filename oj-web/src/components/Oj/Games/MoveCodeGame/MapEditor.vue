<template>
  <div class="map-editor">
    <div class="editor-header">
      <h2>🗺️ 关卡地图编辑器</h2>
      <div class="header-controls">
        <el-button @click="newLevel" type="primary">新建关卡</el-button>
        <el-button @click="loadLevel" type="success">加载关卡</el-button>
        <el-button @click="saveLevel" type="warning">保存关卡</el-button>
        <el-button @click="exportLevel" type="info">导出JSON</el-button>
        <el-button @click="previewLevel" type="default">预览关卡</el-button>
        <el-button @click="applyLevel" type="success" plain>应用关卡</el-button>
        <el-button @click="shareLevel" type="primary" plain>分享关卡</el-button>
      </div>
    </div>

    <div class="editor-content">
      <!-- 左侧工具栏 -->
      <div class="toolbar">
        <h3>🛠️ 工具栏</h3>

        <!-- 画笔工具 -->
        <div class="tool-section">
          <h4>画笔工具</h4>
          <div class="tool-buttons">
            <el-button
              v-for="tool in tools"
              :key="tool.type"
              :type="selectedTool === tool.type ? 'primary' : 'default'"
              @click="selectTool(tool.type)"
              size="small"
              class="tool-button"
            >
              {{ tool.icon }} {{ tool.name }}
            </el-button>
          </div>
        </div>

        <!-- 关卡信息 -->
        <div class="tool-section">
          <h4>关卡信息</h4>
          <el-form :model="levelData" label-width="80px" size="small">
            <el-form-item label="关卡ID">
              <el-input-number v-model="levelData.id" :min="1" :max="100" />
            </el-form-item>
            <el-form-item label="关卡名称">
              <el-input v-model="levelData.objective" placeholder="输入关卡目标" />
            </el-form-item>
            <el-form-item label="学习内容">
              <el-input v-model="levelData.learningContent" placeholder="输入学习内容" />
            </el-form-item>
            <el-form-item label="难度等级">
              <div class="difficulty-selector">
                <el-input-number
                  v-model="levelData.difficulty"
                  :min="1"
                  :max="10"
                  size="small"
                  style="width: 80px;"
                />
                <span class="difficulty-stars">{{ getDifficultyStars(levelData.difficulty) }}</span>
              </div>
            </el-form-item>
            <el-form-item label="成功消息">
              <el-input v-model="levelData.successMessage" placeholder="输入成功消息" />
            </el-form-item>
            <el-form-item label="代码提示">
              <el-input
                v-model="levelData.codePlaceholder"
                type="textarea"
                :rows="3"
                placeholder="输入代码提示"
              />
            </el-form-item>
            <el-form-item label="参考答案">
              <el-input
                v-model="levelData.answer"
                type="textarea"
                :rows="5"
                placeholder="输入参考答案代码"
              />
            </el-form-item>
          </el-form>
        </div>

        <!-- 地图管理 -->
        <div class="tool-section">
          <h4>地图管理</h4>
          <div class="map-management">
            <div class="current-map-info">
              <span>当前地图: {{ currentMapIndex + 1 }}/{{ levelData.maps.length }}</span>
              <el-input
                v-model="levelData.maps[currentMapIndex].name"
                placeholder="地图名称"
                size="small"
                style="margin-top: 5px;"
              />
            </div>
            <div class="map-controls" style="margin-top: 10px;">
              <el-button @click="addNewMap" type="primary" size="small">
                ➕ 新增地图
              </el-button>
              <el-button
                @click="deleteCurrentMap"
                type="danger"
                size="small"
                :disabled="levelData.maps.length <= 1"
              >
                🗑️ 删除地图
              </el-button>
            </div>
            <div class="map-navigation" style="margin-top: 10px;">
              <el-button
                @click="switchMap(currentMapIndex - 1)"
                :disabled="currentMapIndex <= 0"
                size="small"
              >
                ⬅️ 上一个
              </el-button>
              <el-button
                @click="switchMap(currentMapIndex + 1)"
                :disabled="currentMapIndex >= levelData.maps.length - 1"
                size="small"
              >
                ➡️ 下一个
              </el-button>
            </div>
          </div>
        </div>

        <!-- 可用指令 -->
        <div class="tool-section">
          <h4>可用指令</h4>
          <el-checkbox-group v-model="levelData.availableCommands">
            <el-checkbox
              v-for="command in allCommands"
              :key="command"
              :label="command"
              size="small"
            >
              {{ command }}
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </div>

      <!-- 中间地图编辑区域 -->
      <div class="map-area">
        <div class="map-controls">
          <span>地图尺寸：</span>
          <el-input-number v-model="mapWidth" :min="5" :max="20" size="small" />
          <span>×</span>
          <el-input-number v-model="mapHeight" :min="5" :max="15" size="small" />
          <el-button @click="resizeMap" size="small">调整大小</el-button>
          <el-button @click="clearMap" size="small" type="danger">清空地图</el-button>
        </div>

        <div class="map-grid" :style="gridStyle">
          <div
            v-for="(row, y) in mapGrid"
            :key="y"
            class="map-row"
          >
            <div
              v-for="(cell, x) in row"
              :key="x"
              class="map-cell"
              :class="{
                'has-hero': cell.hero,
                'has-treasure': cell.treasure,
                'has-obstacle': cell.obstacle,
                'has-monster': cell.monster,
                'has-weapon': cell.weapon
              }"
              @click="handleCellClick(x, y)"
              @mouseenter="handleCellHover(x, y)"
              @mouseleave="handleCellLeave(x, y)"
            >
              <span v-if="cell.hero" class="cell-content hero">🦸</span>
              <span v-if="cell.treasure" class="cell-content treasure">💰</span>
              <span v-if="cell.obstacle" class="cell-content obstacle">🧱</span>
              <span v-if="cell.monster" class="cell-content monster">
                {{ cell.monster.type === 'physical' ? '👹' : '🔮' }}
              </span>
              <span v-if="cell.weapon" class="cell-content weapon">
                {{ cell.weapon.type === 'sword' ? '⚔️' : '🪄' }}
              </span>
              <span class="cell-coords">{{ x }},{{ y }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧属性面板 -->
      <div class="properties-panel">
        <h3>🎯 属性面板</h3>

        <!-- 怪物属性 -->
        <div v-if="selectedTool === 'monster'" class="property-section">
          <h4>怪物属性</h4>
          <el-form size="small">
            <el-form-item label="类型">
              <el-select v-model="monsterType">
                <el-option label="物理系 👹" value="physical" />
                <el-option label="魔法系 🔮" value="magical" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>

        <!-- 武器属性 -->
        <div v-if="selectedTool === 'weapon'" class="property-section">
          <h4>武器属性</h4>
          <el-form size="small">
            <el-form-item label="类型">
              <el-select v-model="weaponType">
                <el-option label="剑 ⚔️" value="sword" />
                <el-option label="魔杖 🪄" value="staff" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>

        <!-- 统计信息 -->
        <div class="property-section">
          <h4>当前地图统计</h4>
          <div class="stats">
            <div class="stat-item">
              <span class="stat-label">🗺️ 地图总数：</span>
              <span class="stat-value">{{ levelData.maps.length }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">🦸 英雄：</span>
              <span class="stat-value">{{ stats.heroes }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">💰 宝箱：</span>
              <span class="stat-value">{{ stats.treasures }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">🧱 障碍物：</span>
              <span class="stat-value">{{ stats.obstacles }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">👹 怪物：</span>
              <span class="stat-value">{{ stats.monsters }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">⚔️ 武器：</span>
              <span class="stat-value">{{ stats.weapons }}</span>
            </div>
          </div>
        </div>

        <!-- 导出预览 -->
        <div class="property-section">
          <h4>JSON预览</h4>
          <el-input
            v-model="jsonPreview"
            type="textarea"
            :rows="10"
            readonly
            class="json-preview"
          />
        </div>
      </div>
    </div>

    <!-- 预览对话框 -->
    <el-dialog v-model="showPreview" title="关卡预览" width="80%" center>
      <div class="preview-content">
        <MoveCodeGame
          v-if="showPreview && previewLevelData"
          :level-data="previewLevelData"
          @level-complete="handlePreviewComplete"
        />
      </div>
    </el-dialog>

    <!-- 加载关卡对话框 -->
    <el-dialog v-model="showLoadDialog" title="加载关卡" width="50%" center>
      <el-input
        v-model="loadJsonText"
        type="textarea"
        :rows="15"
        placeholder="粘贴关卡JSON数据..."
      />
      <template #footer>
        <el-button @click="showLoadDialog = false">取消</el-button>
        <el-button @click="confirmLoadLevel" type="primary">加载</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MoveCodeGame from './MoveCodeGame.vue'

// 定义事件
const emit = defineEmits<{
  levelApply: [levelData: LevelData]
}>()

// 工具类型定义
interface Tool {
  type: string
  name: string
  icon: string
}

interface MapCell {
  hero?: boolean
  treasure?: boolean
  obstacle?: boolean
  monster?: { type: 'physical' | 'magical' }
  weapon?: { type: 'sword' | 'staff' }
}

interface MapData {
  id: number
  name: string
  heroStart: { x: number, y: number }
  treasures: Array<{ x: number, y: number }>
  obstacles: Array<{ x: number, y: number }>
  monsters?: Array<{ x: number, y: number, type: 'physical' | 'magical' }>
  weapons?: Array<{ x: number, y: number, type: 'sword' | 'staff' }>
}

interface LevelData {
  id: number
  objective: string
  learningContent: string
  difficulty: number
  availableCommands: string[]
  codePlaceholder: string
  successMessage: string
  maps: MapData[]
  answer: string
}

// 响应式数据
const mapWidth = ref(15)
const mapHeight = ref(10)
const selectedTool = ref('hero')
const monsterType = ref<'physical' | 'magical'>('physical')
const weaponType = ref<'sword' | 'staff'>('sword')
const showPreview = ref(false)
const showLoadDialog = ref(false)
const loadJsonText = ref('')
const previewLevelData = ref<LevelData | null>(null)
const currentMapIndex = ref(0) // 当前编辑的地图索引

// 工具定义
const tools: Tool[] = [
  { type: 'hero', name: '英雄', icon: '🦸' },
  { type: 'treasure', name: '宝箱', icon: '💰' },
  { type: 'obstacle', name: '障碍物', icon: '🧱' },
  { type: 'monster', name: '怪物', icon: '👹' },
  { type: 'weapon', name: '武器', icon: '⚔️' },
  { type: 'eraser', name: '橡皮擦', icon: '🧽' }
]

// 所有可用指令
const allCommands = [
  // 基础移动指令
  'right()', 'left()', 'up()', 'down()',
  // 高级移动指令
  'go()', 'dir()', 'turn()',
  // 位置获取指令
  'pos()', 'treasurePos()', 'weaponPos()', 'weapon()',
  // 方向检测指令
  'see()', 'seeTreasure()', 'seeWeapon()', 'seeMonster()',
  // 位置检测指令
  'hasObstacle()', 'hasTreasure()',
  // 控制结构
  'for循环', 'if-else',
  // 高级检测指令
  'thereObstacles()', 'isThereObstacles()', 'isReachBoundary()',
  // 战斗指令
  'hit()',
  // 旧指令（兼容性）
  'moveRight()', 'moveLeft()', 'moveUp()', 'moveDown()',
  'getHeroPosition()', 'fd()', 'rt()', 'direction()', 'attack()'
]

// 关卡数据
const levelData = reactive<LevelData>({
  id: 1,
  objective: '新关卡',
  learningContent: '学习内容',
  difficulty: 1,
  availableCommands: ['right()', 'left()', 'up()', 'down()'],
  codePlaceholder: '// 在这里编写代码',
  successMessage: '🎉 恭喜通关！',
  maps: [
    {
      id: 1,
      name: '地图1',
      heroStart: { x: 1, y: 1 },
      treasures: [],
      obstacles: [],
      monsters: [],
      weapons: []
    }
  ],
  answer: '// 参考答案'
})

// 地图网格
const mapGrid = ref<MapCell[][]>([])

// 初始化地图
const initializeMap = () => {
  mapGrid.value = Array(mapHeight.value).fill(null).map(() =>
    Array(mapWidth.value).fill(null).map(() => ({}))
  )
}

// 计算属性
const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${mapWidth.value}, 40px)`,
  gridTemplateRows: `repeat(${mapHeight.value}, 40px)`
}))

const stats = computed(() => {
  let heroes = 0, treasures = 0, obstacles = 0, monsters = 0, weapons = 0

  mapGrid.value.forEach(row => {
    row.forEach(cell => {
      if (cell.hero) heroes++
      if (cell.treasure) treasures++
      if (cell.obstacle) obstacles++
      if (cell.monster) monsters++
      if (cell.weapon) weapons++
    })
  })

  return { heroes, treasures, obstacles, monsters, weapons }
})

const jsonPreview = computed(() => {
  // 实时更新当前地图数据到关卡数据中
  updateCurrentMapFromGrid()
  return JSON.stringify(levelData, null, 2)
})

// 获取难度星级显示
const getDifficultyStars = (difficulty: number): string => {
  const stars = '⭐'.repeat(Math.min(Math.max(difficulty || 1, 1), 10))
  return stars
}

// 方法
const selectTool = (toolType: string) => {
  selectedTool.value = toolType
}

const handleCellClick = (x: number, y: number) => {
  const cell = mapGrid.value[y][x]

  if (selectedTool.value === 'eraser') {
    // 清除单元格内容
    Object.keys(cell).forEach(key => {
      delete cell[key as keyof MapCell]
    })
  } else if (selectedTool.value === 'hero') {
    // 清除其他英雄位置
    mapGrid.value.forEach(row => {
      row.forEach(c => delete c.hero)
    })
    cell.hero = true
  } else if (selectedTool.value === 'treasure') {
    cell.treasure = !cell.treasure
  } else if (selectedTool.value === 'obstacle') {
    cell.obstacle = !cell.obstacle
  } else if (selectedTool.value === 'monster') {
    if (cell.monster) {
      delete cell.monster
    } else {
      cell.monster = { type: monsterType.value }
    }
  } else if (selectedTool.value === 'weapon') {
    if (cell.weapon) {
      delete cell.weapon
    } else {
      cell.weapon = { type: weaponType.value }
    }
  }
}

const handleCellHover = (x: number, y: number) => {
  // 可以添加悬停效果
}

const handleCellLeave = (x: number, y: number) => {
  // 可以添加离开效果
}

const resizeMap = () => {
  initializeMap()
}

const clearMap = () => {
  ElMessageBox.confirm('确定要清空地图吗？', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    initializeMap()
    ElMessage.success('地图已清空')
  })
}

// 获取当前地图数据
const getCurrentMapData = (): MapData => {
  return levelData.maps[currentMapIndex.value]
}

// 更新当前地图数据从网格
const updateCurrentMapFromGrid = () => {
  const currentMap = getCurrentMapData()

  // 更新英雄起始位置
  for (let y = 0; y < mapHeight.value; y++) {
    for (let x = 0; x < mapWidth.value; x++) {
      if (mapGrid.value[y][x].hero) {
        currentMap.heroStart = { x, y }
        break
      }
    }
  }

  // 更新地图元素位置
  currentMap.treasures = []
  currentMap.obstacles = []
  currentMap.monsters = []
  currentMap.weapons = []

  for (let y = 0; y < mapHeight.value; y++) {
    for (let x = 0; x < mapWidth.value; x++) {
      const cell = mapGrid.value[y][x]
      if (cell.treasure) currentMap.treasures.push({ x, y })
      if (cell.obstacle) currentMap.obstacles.push({ x, y })
      if (cell.monster) currentMap.monsters!.push({ x, y, type: cell.monster.type })
      if (cell.weapon) currentMap.weapons!.push({ x, y, type: cell.weapon.type })
    }
  }
}

// 兼容旧方法名
const updateLevelDataFromMap = () => {
  updateCurrentMapFromGrid()
}

const newLevel = () => {
  ElMessageBox.confirm('确定要新建关卡吗？当前编辑内容将丢失。', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 重置关卡数据
    Object.assign(levelData, {
      id: 1,
      objective: '新关卡',
      learningContent: '学习内容',
      availableCommands: ['right()', 'left()', 'up()', 'down()'],
      codePlaceholder: '// 在这里编写代码',
      successMessage: '🎉 恭喜通关！',
      maps: [
        {
          id: 1,
          name: '地图1',
          heroStart: { x: 1, y: 1 },
          treasures: [],
          obstacles: [],
          monsters: [],
          weapons: []
        }
      ],
      answer: '// 参考答案'
    })
    currentMapIndex.value = 0
    initializeMap()
    ElMessage.success('新关卡已创建')
  })
}

const loadLevel = () => {
  showLoadDialog.value = true
}

const confirmLoadLevel = () => {
  try {
    const data = JSON.parse(loadJsonText.value)

    // 检查是否是新格式（包含maps数组）
    if (data.maps && Array.isArray(data.maps)) {
      // 新格式，直接加载
      Object.assign(levelData, data)
    } else {
      // 旧格式，转换为新格式
      const oldData = data
      Object.assign(levelData, {
        id: oldData.id,
        objective: oldData.objective,
        learningContent: oldData.learningContent,
        availableCommands: oldData.availableCommands,
        codePlaceholder: oldData.codePlaceholder,
        successMessage: oldData.successMessage,
        maps: [
          {
            id: 1,
            name: '地图1',
            heroStart: oldData.heroStart,
            treasures: oldData.treasures || [],
            obstacles: oldData.obstacles || [],
            monsters: oldData.monsters || [],
            weapons: oldData.weapons || []
          }
        ],
        answer: oldData.answer
      })
    }

    currentMapIndex.value = 0
    loadMapFromCurrentData()
    showLoadDialog.value = false
    ElMessage.success('关卡加载成功')
  } catch (error) {
    ElMessage.error('JSON格式错误，请检查数据格式')
  }
}

// 从当前地图数据加载到网格
const loadMapFromCurrentData = () => {
  initializeMap()

  const currentMap = getCurrentMapData()
  if (!currentMap) return

  // 设置英雄位置
  if (currentMap.heroStart) {
    const { x, y } = currentMap.heroStart
    if (y < mapHeight.value && x < mapWidth.value) {
      mapGrid.value[y][x].hero = true
    }
  }

  // 设置宝箱
  currentMap.treasures.forEach(({ x, y }) => {
    if (y < mapHeight.value && x < mapWidth.value) {
      mapGrid.value[y][x].treasure = true
    }
  })

  // 设置障碍物
  currentMap.obstacles.forEach(({ x, y }) => {
    if (y < mapHeight.value && x < mapWidth.value) {
      mapGrid.value[y][x].obstacle = true
    }
  })

  // 设置怪物
  currentMap.monsters?.forEach(({ x, y, type }) => {
    if (y < mapHeight.value && x < mapWidth.value) {
      mapGrid.value[y][x].monster = { type }
    }
  })

  // 设置武器
  currentMap.weapons?.forEach(({ x, y, type }) => {
    if (y < mapHeight.value && x < mapWidth.value) {
      mapGrid.value[y][x].weapon = { type }
    }
  })
}

// 兼容旧方法名
const loadMapFromLevelData = () => {
  loadMapFromCurrentData()
}

const saveLevel = () => {
  // 保存当前编辑的地图数据
  updateCurrentMapFromGrid()
  const jsonData = JSON.stringify(levelData, null, 2)

  // 创建下载链接
  const blob = new Blob([jsonData], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `level-${levelData.id}-multi-maps.json`
  a.click()
  URL.revokeObjectURL(url)

  ElMessage.success(`关卡已保存到本地 (包含${levelData.maps.length}个地图)`)
}

const exportLevel = () => {
  // 保存当前编辑的地图数据
  updateCurrentMapFromGrid()
  const jsonData = JSON.stringify(levelData, null, 2)

  // 复制到剪贴板
  navigator.clipboard.writeText(jsonData).then(() => {
    ElMessage.success(`JSON数据已复制到剪贴板 (包含${levelData.maps.length}个地图)`)
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

const previewLevel = () => {
  // 保存当前编辑的地图数据
  updateCurrentMapFromGrid()
  previewLevelData.value = { ...levelData }
  showPreview.value = true
}

const handlePreviewComplete = () => {
  ElMessage.success('关卡预览完成！')
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

// 应用关卡：将编辑好的关卡应用到游戏中
const applyLevel = (): void => {
  // 保存当前编辑的地图数据
  updateCurrentMapFromGrid()
  
  ElMessageBox.confirm(
    `确定要将关卡 ${levelData.id} 应用到游戏中吗？这将切换到游戏模式。`,
    '应用关卡',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    }
  ).then(() => {
    // 触发应用关卡事件
    emit('levelApply', { ...levelData })
    ElMessage.success('关卡已应用到游戏')
  }).catch(() => {
    ElMessage.info('已取消应用')
  })
}

// 分享关卡：分享编辑好的关卡，分享后打开进入游戏界面
const shareLevel = (): void => {
  // 保存当前编辑的地图数据
  updateCurrentMapFromGrid()
  
  try {
    // 将关卡数据转换为JSON字符串
    const levelJson = JSON.stringify(levelData, null, 2)
    
    // 进行 base64 编码
    const encodedLevel = btoa(encodeURIComponent(levelJson))
    
    // 构建分享链接，指向游戏模式
    const currentUrl = window.location.origin + window.location.pathname
    const shareUrl = `${currentUrl}?level=${encodedLevel}`
    
    // 复制到剪贴板
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(shareUrl).then(() => {
        ElMessage.success('关卡分享链接已复制到剪贴板！打开链接将进入游戏模式。')
      }).catch(() => {
        copyToClipboard(shareUrl)
      })
    } else {
      copyToClipboard(shareUrl)
    }
  } catch (error) {
    ElMessage.error('分享关卡失败，请重试')
    console.error('分享关卡失败:', error)
  }
}

// 地图管理方法
const addNewMap = () => {
  // 保存当前地图数据
  updateCurrentMapFromGrid()

  // 创建新地图
  const newMapId = levelData.maps.length + 1
  const newMap: MapData = {
    id: newMapId,
    name: `地图${newMapId}`,
    heroStart: { x: 1, y: 1 },
    treasures: [],
    obstacles: [],
    monsters: [],
    weapons: []
  }

  levelData.maps.push(newMap)
  currentMapIndex.value = levelData.maps.length - 1

  // 加载新地图到编辑器
  loadMapFromCurrentData()

  ElMessage.success(`已添加新地图: ${newMap.name}`)
}

const deleteCurrentMap = () => {
  if (levelData.maps.length <= 1) {
    ElMessage.warning('至少需要保留一个地图')
    return
  }

  ElMessageBox.confirm(
    `确定要删除地图 "${levelData.maps[currentMapIndex.value].name}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    levelData.maps.splice(currentMapIndex.value, 1)

    // 调整当前索引
    if (currentMapIndex.value >= levelData.maps.length) {
      currentMapIndex.value = levelData.maps.length - 1
    }

    // 重新编号地图ID
    levelData.maps.forEach((map, index) => {
      map.id = index + 1
      if (!map.name || map.name.startsWith('地图')) {
        map.name = `地图${index + 1}`
      }
    })

    // 加载当前地图
    loadMapFromCurrentData()

    ElMessage.success('地图已删除')
  })
}

const switchMap = (newIndex: number) => {
  if (newIndex < 0 || newIndex >= levelData.maps.length) return

  // 保存当前地图数据
  updateCurrentMapFromGrid()

  // 切换到新地图
  currentMapIndex.value = newIndex

  // 加载新地图数据
  loadMapFromCurrentData()

  ElMessage.info(`已切换到: ${levelData.maps[newIndex].name}`)
}

// 初始化
initializeMap()
</script>

<style scoped>
.map-editor {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #E0F7FA 0%, #B0E0E6 50%, #87CEEB 100%);
}

.editor-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.editor-header h2 {
  margin: 0;
  color: #1e88e5;
  font-size: 24px;
}

.header-controls {
  display: flex;
  gap: 10px;
}

.editor-content {
  flex: 1;
  display: flex;
  gap: 20px;
  padding: 20px 20px 20px 100px;
  overflow: hidden;
}

/* 左侧工具栏 */
.toolbar {
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.toolbar h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.tool-section {
  margin-bottom: 25px;
}

.tool-section h4 {
  margin: 0 0 15px 0;
  color: #34495e;
  font-size: 14px;
  font-weight: bold;
}

.tool-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tool-button {
  width: 100%;
  justify-content: flex-start;
  text-align: left;
}

/* 中间地图区域 */
.map-area {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.map-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.map-grid {
  display: grid;
  gap: 1px;
  background: #ddd;
  border: 2px solid #bbb;
  border-radius: 8px;
  padding: 10px;
  overflow: auto;
  flex: 1;
  justify-content: center;
  align-content: center;
}

.map-row {
  display: contents;
}

.map-cell {
  width: 40px;
  height: 40px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  transition: all 0.2s ease;
}

.map-cell:hover {
  background: #e9ecef;
  transform: scale(1.05);
  z-index: 1;
}

.map-cell.has-hero {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.map-cell.has-treasure {
  background: linear-gradient(135deg, #f39c12, #e67e22);
}

.map-cell.has-obstacle {
  background: linear-gradient(135deg, #95a5a6, #7f8c8d);
}

.map-cell.has-monster {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
}

.map-cell.has-weapon {
  background: linear-gradient(135deg, #9b59b6, #8e44ad);
}

.cell-content {
  font-size: 20px;
  z-index: 2;
}

.cell-coords {
  position: absolute;
  bottom: 1px;
  right: 2px;
  font-size: 8px;
  color: #666;
  background: rgba(255, 255, 255, 0.8);
  padding: 1px 2px;
  border-radius: 2px;
}

/* 右侧属性面板 */
.properties-panel {
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.properties-panel h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.property-section {
  margin-bottom: 25px;
}

.property-section h4 {
  margin: 0 0 15px 0;
  color: #34495e;
  font-size: 14px;
  font-weight: bold;
}

.stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.stat-label {
  font-weight: bold;
  color: #495057;
}

.stat-value {
  background: #007bff;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.json-preview {
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

/* 预览对话框 */
.preview-content {
  height: 600px;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .editor-content {
    flex-direction: column;
  }

  .toolbar,
  .properties-panel {
    width: 100%;
    max-height: 300px;
  }

  .map-area {
    min-height: 500px;
  }
}

@media (max-width: 768px) {
  .editor-header {
    flex-direction: column;
    gap: 15px;
  }

  .header-controls {
    flex-wrap: wrap;
    justify-content: center;
  }

  .map-cell {
    width: 30px;
    height: 30px;
  }

  .cell-content {
    font-size: 16px;
  }
}

/* 滚动条样式 */
.toolbar::-webkit-scrollbar,
.properties-panel::-webkit-scrollbar,
.map-grid::-webkit-scrollbar {
  width: 6px;
}

.toolbar::-webkit-scrollbar-track,
.properties-panel::-webkit-scrollbar-track,
.map-grid::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.toolbar::-webkit-scrollbar-thumb,
.properties-panel::-webkit-scrollbar-thumb,
.map-grid::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.toolbar::-webkit-scrollbar-thumb:hover,
.properties-panel::-webkit-scrollbar-thumb:hover,
.map-grid::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 地图管理样式 */
.map-management {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #e9ecef;
}

.current-map-info {
  font-weight: bold;
  color: #495057;
  margin-bottom: 10px;
}

.map-controls {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.map-navigation {
  display: flex;
  gap: 8px;
  justify-content: space-between;
}

.map-controls .el-button,
.map-navigation .el-button {
  flex: 1;
  min-width: 0;
}

/* 难度选择器样式 */
.difficulty-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}

.difficulty-stars {
  font-size: 16px;
  color: #ffc107;
  min-width: 120px;
}
</style>
