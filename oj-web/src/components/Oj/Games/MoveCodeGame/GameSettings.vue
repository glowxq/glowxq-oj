<template>
  <transition name="slide-down">
    <div v-if="visible" class="game-settings">
      <div class="settings-header">
        <h3>⚙️ 游戏设置</h3>
        <el-button @click="$emit('close')" type="text" size="small" class="close-btn">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <div class="settings-content">
        <!-- 动画速度设置 -->
        <div class="setting-group">
          <div class="setting-item">
            <label>移动速度</label>
            <div class="speed-control">
              <el-slider
                v-model="localSettings.animationSpeed"
                :min="0.1"
                :max="5"
                :step="0.1"
                @input="handleSpeedChange"
                style="width: 120px"
              />
              <span class="speed-value">{{ localSettings.animationSpeed.toFixed(1) }}x</span>
            </div>
          </div>
        </div>

        <!-- 角色外观设置 -->
        <div class="setting-group">
          <h4>角色外观</h4>

          <!-- 英雄设置 -->
          <div class="setting-item">
            <label>英雄头像</label>
            <div class="appearance-control">
              <div class="icon-selector">
                <el-button
                  v-for="icon in heroIcons"
                  :key="icon"
                  :type="localSettings.heroIcon === icon ? 'primary' : 'default'"
                  @click="selectIcon('hero', icon)"
                  class="icon-btn"
                  size="small"
                >
                  {{ icon }}
                </el-button>
              </div>
              <div class="image-input">
                <el-input
                  v-model="localSettings.heroImage"
                  placeholder="图片URL"
                  size="small"
                  @change="handleImageChange('hero')"
                >
                  <template #append>
                    <el-upload
                      :before-upload="(file) => handleImageUpload(file, 'hero')"
                      :show-file-list="false"
                      accept="image/*"
                    >
                      <el-button size="small">上传</el-button>
                    </el-upload>
                  </template>
                </el-input>
              </div>
            </div>
          </div>

          <!-- 宝箱设置 -->
          <div class="setting-item">
            <label>宝箱图标</label>
            <div class="appearance-control">
              <div class="icon-selector">
                <el-button
                  v-for="icon in treasureIcons"
                  :key="icon"
                  :type="localSettings.treasureIcon === icon ? 'primary' : 'default'"
                  @click="selectIcon('treasure', icon)"
                  class="icon-btn"
                  size="small"
                >
                  {{ icon }}
                </el-button>
              </div>
              <div class="image-input">
                <el-input
                  v-model="localSettings.treasureImage"
                  placeholder="图片URL"
                  size="small"
                  @change="handleImageChange('treasure')"
                >
                  <template #append>
                    <el-upload
                      :before-upload="(file) => handleImageUpload(file, 'treasure')"
                      :show-file-list="false"
                      accept="image/*"
                    >
                      <el-button size="small">上传</el-button>
                    </el-upload>
                  </template>
                </el-input>
              </div>
            </div>
          </div>

          <!-- 障碍物设置 -->
          <div class="setting-item">
            <label>障碍物图标</label>
            <div class="appearance-control">
              <div class="icon-selector">
                <el-button
                  v-for="icon in obstacleIcons"
                  :key="icon"
                  :type="localSettings.obstacleIcon === icon ? 'primary' : 'default'"
                  @click="selectIcon('obstacle', icon)"
                  class="icon-btn"
                  size="small"
                >
                  {{ icon }}
                </el-button>
              </div>
              <div class="image-input">
                <el-input
                  v-model="localSettings.obstacleImage"
                  placeholder="图片URL"
                  size="small"
                  @change="handleImageChange('obstacle')"
                >
                  <template #append>
                    <el-upload
                      :before-upload="(file) => handleImageUpload(file, 'obstacle')"
                      :show-file-list="false"
                      accept="image/*"
                    >
                      <el-button size="small">上传</el-button>
                    </el-upload>
                  </template>
                </el-input>
              </div>
            </div>
          </div>
        </div>

        <!-- 武器设置 -->
        <div class="setting-group">
          <h4>武器外观</h4>
          <div class="weapon-settings">
            <div class="weapon-item">
              <span class="weapon-label">剑：</span>
              <el-input
                v-model="localSettings.swordImage"
                placeholder="剑的图片URL"
                size="small"
                @change="handleImageChange('weapon')"
              >
                <template #append>
                  <el-upload
                    :before-upload="(file) => handleImageUpload(file, 'sword')"
                    :show-file-list="false"
                    accept="image/*"
                  >
                    <el-button size="small">上传</el-button>
                  </el-upload>
                </template>
              </el-input>
            </div>
            <div class="weapon-item">
              <span class="weapon-label">魔杖：</span>
              <el-input
                v-model="localSettings.staffImage"
                placeholder="魔杖的图片URL"
                size="small"
                @change="handleImageChange('weapon')"
              >
                <template #append>
                  <el-upload
                    :before-upload="(file) => handleImageUpload(file, 'staff')"
                    :show-file-list="false"
                    accept="image/*"
                  >
                    <el-button size="small">上传</el-button>
                  </el-upload>
                </template>
              </el-input>
            </div>
          </div>
        </div>

        <!-- 怪物设置 -->
        <div class="setting-group">
          <h4>怪物外观</h4>
          <div class="monster-settings">
            <div class="monster-item">
              <span class="monster-label">物理系：</span>
              <el-input
                v-model="localSettings.physicalMonsterImage"
                placeholder="物理系怪物图片URL"
                size="small"
                @change="handleImageChange('monster')"
              >
                <template #append>
                  <el-upload
                    :before-upload="(file) => handleImageUpload(file, 'physical')"
                    :show-file-list="false"
                    accept="image/*"
                  >
                    <el-button size="small">上传</el-button>
                  </el-upload>
                </template>
              </el-input>
            </div>
            <div class="monster-item">
              <span class="monster-label">魔法系：</span>
              <el-input
                v-model="localSettings.magicalMonsterImage"
                placeholder="魔法系怪物图片URL"
                size="small"
                @change="handleImageChange('monster')"
              >
                <template #append>
                  <el-upload
                    :before-upload="(file) => handleImageUpload(file, 'magical')"
                    :show-file-list="false"
                    accept="image/*"
                  >
                    <el-button size="small">上传</el-button>
                  </el-upload>
                </template>
              </el-input>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="setting-actions">
          <el-button @click="resetSettings" type="warning" size="small">
            🔄 重置设置
          </el-button>
          <el-button @click="$emit('close')" type="primary" size="small">
            ✅ 完成
          </el-button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElButton, ElSlider, ElInput, ElUpload, ElIcon, ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'

// 游戏设置接口
interface GameSettings {
  animationSpeed: number
  heroIcon: string
  treasureIcon: string
  obstacleIcon: string
  heroImage: string
  treasureImage: string
  obstacleImage: string
  swordImage: string
  staffImage: string
  physicalMonsterImage: string
  magicalMonsterImage: string
}

// Props
interface GameSettingsProps {
  visible: boolean
  settings: GameSettings
}

const props = defineProps<GameSettingsProps>()

// Events
const emit = defineEmits<{
  close: []
  updateSettings: [settings: GameSettings]
  speedChange: [speed: number]
  iconChange: [type: string, icon: string]
  imageChange: [type: string, image?: string]
}>()

// 本地设置状态
const localSettings = reactive<GameSettings>({ ...props.settings })

// 图标选项
const heroIcons = ['🦸', '👨‍💻', '🧙‍♂️', '🥷', '👨‍🚀', '🤖', '👨‍🔬', '🧑‍🎨']
const treasureIcons = ['💰', '💎', '🏆', '👑', '💍', '🎁', '⭐', '🌟']
const obstacleIcons = ['🧱', '🪨', '🌳', '🏔️', '🚧', '⛔', '🚪', '🔒']

// 监听外部设置变化
watch(() => props.settings, (newSettings) => {
  Object.assign(localSettings, newSettings)
}, { deep: true })

// 处理速度变化
const handleSpeedChange = (speed: number | number[]): void => {
  const actualSpeed = Array.isArray(speed) ? speed[0] : speed
  emit('speedChange', actualSpeed)
  emit('updateSettings', { ...localSettings })
}

// 选择图标
const selectIcon = (type: string, icon: string): void => {
  switch (type) {
    case 'hero':
      localSettings.heroIcon = icon
      break
    case 'treasure':
      localSettings.treasureIcon = icon
      break
    case 'obstacle':
      localSettings.obstacleIcon = icon
      break
  }
  emit('iconChange', type, icon)
  emit('updateSettings', { ...localSettings })
}

// 处理图片变化
const handleImageChange = (type: string): void => {
  emit('imageChange', type)
  emit('updateSettings', { ...localSettings })
}

// 处理图片上传
const handleImageUpload = (file: File, type: string): boolean => {
  const reader = new FileReader()
  reader.onload = (e) => {
    const result = e.target?.result as string
    switch (type) {
      case 'hero':
        localSettings.heroImage = result
        break
      case 'treasure':
        localSettings.treasureImage = result
        break
      case 'obstacle':
        localSettings.obstacleImage = result
        break
      case 'sword':
        localSettings.swordImage = result
        break
      case 'staff':
        localSettings.staffImage = result
        break
      case 'physical':
        localSettings.physicalMonsterImage = result
        break
      case 'magical':
        localSettings.magicalMonsterImage = result
        break
    }
    emit('imageChange', type === 'physical' || type === 'magical' ? 'monster' : type === 'sword' || type === 'staff' ? 'weapon' : type)
    emit('updateSettings', { ...localSettings })
  }
  reader.readAsDataURL(file)
  return false // 阻止自动上传
}

// 重置设置
const resetSettings = (): void => {
  const defaultSettings: GameSettings = {
    animationSpeed: 1,
    heroIcon: '🦸',
    treasureIcon: '💰',
    obstacleIcon: '🧱',
    heroImage: '',
    treasureImage: '',
    obstacleImage: '',
    swordImage: '',
    staffImage: '',
    physicalMonsterImage: '',
    magicalMonsterImage: ''
  }

  Object.assign(localSettings, defaultSettings)
  emit('updateSettings', { ...localSettings })
  emit('speedChange', 1)
  emit('iconChange', 'hero', '🦸')
  emit('iconChange', 'treasure', '💰')
  emit('iconChange', 'obstacle', '🧱')
  emit('imageChange', 'hero')
  emit('imageChange', 'treasure')
  emit('imageChange', 'obstacle')
  emit('imageChange', 'weapon')
  emit('imageChange', 'monster')

  ElMessage.success('设置已重置')
}
</script>

<style scoped>
.game-settings {
  position: absolute;
  top: 50px;
  right: 20px;
  width: 360px;
  max-height: 80vh;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  z-index: 1000;
  overflow: hidden;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.settings-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.close-btn {
  color: white !important;
  border: none !important;
  background: rgba(255, 255, 255, 0.1) !important;
  border-radius: 6px;
  padding: 6px;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

.settings-content {
  padding: 20px;
  max-height: calc(80vh - 60px);
  overflow-y: auto;
}

.setting-group {
  margin-bottom: 24px;
}

.setting-group h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 6px;
}

.setting-item {
  margin-bottom: 16px;
}

.setting-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #2c3e50;
  font-size: 13px;
}

.speed-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.speed-value {
  min-width: 40px;
  font-family: 'JetBrains Mono', monospace;
  color: #7f8c8d;
  font-size: 12px;
  font-weight: 600;
}

.appearance-control {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.icon-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.icon-btn {
  min-width: 36px;
  height: 36px;
  padding: 0;
  font-size: 16px;
  border-radius: 8px;
}

.image-input {
  width: 100%;
}

.weapon-settings,
.monster-settings {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.weapon-item,
.monster-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.weapon-label,
.monster-label {
  min-width: 60px;
  font-size: 12px;
  font-weight: 600;
  color: #2c3e50;
}

.setting-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

/* 滚动条样式 */
.settings-content::-webkit-scrollbar {
  width: 6px;
}

.settings-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.settings-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.settings-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 动画效果 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .game-settings {
    width: calc(100vw - 40px);
    right: 20px;
    max-height: 70vh;
  }

  .icon-selector {
    justify-content: center;
  }

  .weapon-item,
  .monster-item {
    flex-direction: column;
    align-items: stretch;
    gap: 4px;
  }

  .weapon-label,
  .monster-label {
    min-width: auto;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .game-settings {
    width: calc(100vw - 20px);
    right: 10px;
    top: 60px;
  }

  .settings-header {
    padding: 12px 16px;
  }

  .settings-content {
    padding: 16px;
  }

  .setting-actions {
    flex-direction: column;
  }
}
</style>
