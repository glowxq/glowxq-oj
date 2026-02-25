<template>
  <div class="test-page">
    <!-- 左侧悬浮菜单 -->
    <div class="side-menu">
      <div class="menu-item" :class="{ 'active': currentView === 'game' }" @click="switchView('game')" title="游戏模式">
        <el-icon><VideoPlay /></el-icon>
        <span class="menu-label">游戏</span>
      </div>
      <div class="menu-item" :class="{ 'active': currentView === 'editor' }" @click="switchView('editor')" title="地图编辑器" v-show="isAdminOrCommon">
        <el-icon><MapLocation /></el-icon>
        <span class="menu-label">编辑器</span>
      </div>
      <div class="menu-item" @click="showCommandsReference" title="指令参考">
        <el-icon><Document /></el-icon>
        <span class="menu-label">指令</span>
      </div>
    </div>

    <!-- 右侧悬浮菜单 - 关卡选择 -->
    <div class="right-side-menu" v-if="currentView === 'game'">
      <div class="menu-item" @click="toggleLevelSelector" :class="{ 'active': showLevelSelector }" title="选择关卡">
        <el-icon><Trophy /></el-icon>
        <span class="menu-label">关卡</span>
      </div>
      
      <!-- 关卡选择面板 -->
      <transition name="slide-left">
        <div v-if="showLevelSelector" class="level-selector-panel">
          <div class="panel-header">
            <h4>🎯 选择关卡</h4>
            <el-icon class="close-icon" @click="toggleLevelSelector">
              <Close />
            </el-icon>
          </div>
          <div class="panel-content">
            <div class="levels-list">
              <div
                v-for="level in levels"
                :key="level.id"
                class="level-item"
                :class="{ 'active': selectedLevel === level.id }"
                @click="selectLevel(level.id)"
              >
                <span class="level-number">{{ level.id }}</span>
                <span class="level-title">{{ level.objective }}</span>
                <span class="level-difficulty">{{ getLevelDifficulty(level.difficulty) }}</span>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- 游戏模式 -->
    <div
      v-if="currentView === 'game'"
      class="game-mode"
    >
      <!-- 游戏组件 -->
      <div class="game-container" v-if="selectedLevelData">
        <MoveCodeGame
          :level-data="selectedLevelData"
          :key="`level-${selectedLevel}`"
          @game-start="handleGameStart"
          @game-end="handleGameEnd"
          @level-complete="handleLevelComplete"
          @level-import="handleLevelImport"
          @level-applied="handleLevelApplied"
          ref="codeGameRef"
        />
      </div>
    </div>

    <!-- 地图编辑器模式 -->
    <div
      v-if="currentView === 'editor'"
      class="editor-mode"
    >
      <MapEditor @level-apply="handleLevelApply" />
    </div>

    <!-- 指令参考弹窗 -->
    <el-dialog
      v-model="showCommandsDialog"
      title=""
      width="90%"
      :show-close="true"
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      class="commands-dialog"
      center
    >
      <CommandsReference />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from 'vue'
import { ElMessage, ElDialog, ElIcon } from 'element-plus'
import { VideoPlay, MapLocation, Document, Trophy, Close } from '@element-plus/icons-vue'
import MoveCodeGame from '@/components/Oj/Games/MoveCodeGame/MoveCodeGame.vue'
import MapEditor from '@/components/Oj/Games/MoveCodeGame/MapEditor.vue'
import CommandsReference from '@/components/Oj/Games/MoveCodeGame/CommandsReference.vue'
import levelsData from '@/components/Oj/Games/MoveCodeGame/levels.json'
import { useRole } from '@/hooks/useRole'

const { isAdminOrCommon } = useRole()

// 关卡数据类型定义
interface Level {
  id: number
  objective: string
  learningContent: string
  difficulty: number // 难度等级 1-10
  availableCommands: string[]
  codePlaceholder: string
  successMessage: string
  maps: any[]
  answer: string
}

// 当前视图模式
const currentView = ref<'game' | 'editor'>('game')

// 指令参考弹窗状态
const showCommandsDialog = ref(false)

// 游戏组件引用
const codeGameRef = ref<InstanceType<typeof MoveCodeGame> | null>(null)

// 关卡选择器显示状态
const showLevelSelector = ref(false)

// 切换关卡选择器显示状态
const toggleLevelSelector = (): void => {
  showLevelSelector.value = !showLevelSelector.value
}

// 关卡数据
const levels: Level[] = levelsData as Level[]

// 当前选择的关卡
const selectedLevel = ref(101)

// 当前选择的关卡数据
const selectedLevelData = computed(() => {
  return levels.find(level => level.id === selectedLevel.value) || null
})

// 切换视图
const switchView = (view: 'game' | 'editor') => {
  currentView.value = view
  if (view === 'game') {
    ElMessage.info('切换到游戏模式')
  } else {
    ElMessage.info('切换到地图编辑器模式')
  }
}

// 显示指令参考
const showCommandsReference = () => {
  showCommandsDialog.value = true
  ElMessage.info('打开指令参考手册')
}

// 选择关卡
const selectLevel = (levelId: number) => {
  selectedLevel.value = levelId
  const levelData = levels.find(level => level.id === levelId)
  if (levelData) {
    ElMessage.info(`选择关卡 ${levelId}: ${levelData.objective}`)
  }
}

// 获取关卡难度显示
const getLevelDifficulty = (difficulty: number): string => {
  const stars = '⭐'.repeat(Math.min(Math.max(difficulty, 1), 10))
  return stars
}

// 游戏统计数据
const gameStats = reactive({
  totalGames: 0,
  successCount: 0,
  failedCount: 0,
  maxLevel: 1
})

// 处理游戏开始事件
const handleGameStart = (): void => {
  console.log(`游戏开始 - 关卡 ${selectedLevel.value}`)
  gameStats.totalGames++
  ElMessage.info(`开始挑战关卡 ${selectedLevel.value}！`)
}

// 处理游戏结束事件
const handleGameEnd = (success: boolean): void => {
  console.log(`游戏结束 - 关卡 ${selectedLevel.value}, 成功: ${success}`)

  if (success) {
    gameStats.successCount++
    gameStats.maxLevel = Math.max(gameStats.maxLevel, selectedLevel.value)
    ElMessage.success(`恭喜通过关卡 ${selectedLevel.value}！`)
  } else {
    gameStats.failedCount++
    ElMessage.error(`关卡 ${selectedLevel.value} 挑战失败，再试一次吧！`)
  }
}

// 处理关卡完成事件
const handleLevelComplete = (): void => {
  console.log(`关卡完成 - 关卡 ${selectedLevel.value}`)

  if (selectedLevel.value === 107) {
    // 完成所有关卡的特殊处理
    setTimeout(() => {
      ElMessage({
        message: '🎉 恭喜你完成了所有关卡！你已经掌握了编程的基础概念！',
        type: 'success',
        duration: 5000,
        showClose: true
      })
    }, 1000)
  }
}

// 处理关卡导入事件
const handleLevelImport = (levelData: any): void => {
  try {
    // 检查关卡是否已存在
    const existingIndex = levels.findIndex(level => level.id === levelData.id)
    if (existingIndex >= 0) {
      // 更新现有关卡
      Object.assign(levels[existingIndex], levelData)
      ElMessage.success(`关卡 ${levelData.id} 已更新`)
    } else {
      // 添加新关卡
      levels.push(levelData)
      ElMessage.success(`关卡 ${levelData.id} 已添加`)
    }
    // 切换到游戏模式并选择该关卡
    currentView.value = 'game'
    selectedLevel.value = levelData.id
  } catch (error) {
    ElMessage.error('导入关卡失败')
    console.error('导入关卡失败:', error)
  }
}

// 处理关卡应用事件
const handleLevelApply = (levelData: any): void => {
  try {
    // 深拷贝关卡数据
    const clonedLevelData = JSON.parse(JSON.stringify(levelData))
    
    // 检查关卡是否已存在
    const existingIndex = levels.findIndex(level => level.id === clonedLevelData.id)
    if (existingIndex >= 0) {
      // 完全替换现有关卡数据，而不是合并
      levels[existingIndex] = clonedLevelData
      ElMessage.success(`关卡 ${clonedLevelData.id} 已更新`)
    } else {
      // 添加新关卡
      levels.push(clonedLevelData)
      ElMessage.success(`关卡 ${clonedLevelData.id} 已添加`)
    }
    
    // 先切换到游戏模式
    currentView.value = 'game'
    
    // 使用 nextTick 确保数据更新完成后再切换关卡
    nextTick(() => {
      // 如果当前选中的就是该关卡，需要强制更新
      if (selectedLevel.value === clonedLevelData.id) {
        // 临时设置为其他值，然后立即改回来，触发组件更新
        selectedLevel.value = -1
        nextTick(() => {
          selectedLevel.value = clonedLevelData.id
        })
      } else {
        // 直接切换关卡
        selectedLevel.value = clonedLevelData.id
      }
    })
  } catch (error) {
    ElMessage.error('应用关卡失败')
    console.error('应用关卡失败:', error)
  }
}

// 处理关卡应用完成事件（从MoveCodeGame组件发出）
const handleLevelApplied = (): void => {
  // 关卡应用完成，可以在这里做一些清理工作
}
</script>

<style scoped>
.test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #E0F7FA 0%, #B0E0E6 50%, #87CEEB 100%);
  padding: 0;
}

/* 左侧悬浮菜单 */
.side-menu {
  position: fixed;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 200;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  padding: 12px 8px;
  box-shadow: 0 8px 32px rgba(79, 195, 247, 0.4), 0 0 0 2px rgba(79, 195, 247, 0.3);
  border: 2px solid rgba(79, 195, 247, 0.4);
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #1e88e5;
  position: relative;
  background: rgba(79, 195, 247, 0.08);
  border: 1px solid rgba(79, 195, 247, 0.2);
}

.menu-item:hover {
  background: rgba(79, 195, 247, 0.2);
  color: #0288D1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 195, 247, 0.3);
  border-color: rgba(79, 195, 247, 0.4);
}

.menu-item.active {
  background: linear-gradient(135deg, #4FC3F7, #29B6F6);
  color: white;
  box-shadow: 0 4px 16px rgba(79, 195, 247, 0.5);
  border-color: rgba(79, 195, 247, 0.6);
}

.menu-item .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.menu-label {
  font-size: 11px;
  font-weight: 500;
  white-space: nowrap;
}

/* 右侧悬浮菜单 - 关卡选择 */
.right-side-menu {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 200;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}

.right-side-menu .menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #1e88e5;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 32px rgba(79, 195, 247, 0.4), 0 0 0 2px rgba(79, 195, 247, 0.3);
  border: 2px solid rgba(79, 195, 247, 0.4);
}

.right-side-menu .menu-item:hover {
  background: rgba(255, 255, 255, 1);
  color: #0288D1;
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(79, 195, 247, 0.5), 0 0 0 2px rgba(79, 195, 247, 0.4);
  border-color: rgba(79, 195, 247, 0.5);
}

.right-side-menu .menu-item.active {
  background: linear-gradient(135deg, #4FC3F7, #29B6F6);
  color: white;
  box-shadow: 0 4px 16px rgba(79, 195, 247, 0.5), 0 0 0 2px rgba(79, 195, 247, 0.6);
  border-color: rgba(79, 195, 247, 0.6);
}

.right-side-menu .menu-item .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.right-side-menu .menu-label {
  font-size: 11px;
  font-weight: 500;
  white-space: nowrap;
}

/* 关卡选择面板 */
.level-selector-panel {
  position: absolute;
  right: 70px;
  top: 0;
  width: 400px;
  max-height: 600px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(79, 195, 247, 0.25);
  border: 1px solid rgba(79, 195, 247, 0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(79, 195, 247, 0.05);
  border-bottom: 1px solid rgba(79, 195, 247, 0.1);
}

.panel-header h4 {
  margin: 0;
  color: #1e88e5;
  font-size: 18px;
  font-weight: 600;
}

.close-icon {
  color: #1e88e5;
  cursor: pointer;
  font-size: 20px;
  transition: all 0.2s ease;
}

.close-icon:hover {
  color: #4FC3F7;
  transform: rotate(90deg);
}

.panel-content {
  padding: 16px;
  overflow-y: auto;
  flex: 1;
}

/* 游戏模式和编辑器模式 */
.game-mode {
  padding: 20px 100px 20px 100px;
  max-width: 1800px;
  margin: 0 auto;
  width: 100%;
}

.levels-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.level-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid #B0E0E6;
  border-radius: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  width: 100%;
}

.level-item:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: #4FC3F7;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(79, 195, 247, 0.2);
}

.level-item.active {
  background: linear-gradient(135deg, #4FC3F7, #29B6F6);
  border-color: #29B6F6;
  color: white;
  box-shadow: 0 2px 8px rgba(79, 195, 247, 0.3);
}

.level-item.active .level-difficulty {
  color: #ffd700;
}

.level-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #4FC3F7;
  color: white;
  border-radius: 50%;
  font-weight: bold;
  font-size: 12px;
  flex-shrink: 0;
}

.level-item.active .level-number {
  background: rgba(255, 255, 255, 0.2);
}

.level-title {
  font-weight: 500;
  color: #1565c0;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.level-item.active .level-title {
  color: white;
}

.level-difficulty {
  font-size: 12px;
  color: #ffc107;
  flex-shrink: 0;
}

.editor-mode {
  padding: 0;
  height: calc(100vh - 70px);
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-content {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }

  .nav-tabs {
    width: 100%;
    justify-content: center;
  }

  .nav-tab {
    flex: 1;
    text-align: center;
  }

  .editor-mode {
    height: calc(100vh - 120px);
  }
}

/* 指令参考弹窗样式 */
.commands-dialog {
  border-radius: 12px;
}

.commands-dialog :deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

.commands-dialog :deep(.el-dialog__header) {
  display: none;
}

.commands-dialog :deep(.el-dialog__body) {
  padding: 0;
  height: 80vh;
  overflow: hidden;
}

/* 游戏容器 */
.game-container {
  margin: 0;
  height: calc(100vh - 40px);
}

/* 左侧滑入动画 */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.3s ease;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>

