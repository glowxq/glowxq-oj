<template>
  <div v-if="showHelper" class="teacher-helper" :style="{ left: position.x + 'px', top: position.y + 'px' }">
    <!-- 浮动按钮 -->
    <div
      class="floating-button"
      :class="{ 'is-active': isMenuOpen }"
      @mousedown="startDrag"
    >
      <div class="button-inner" @click.stop="toggleMenu">
        <el-icon class="button-icon">
          <component :is="currentIcon" />
        </el-icon>
      </div>
    </div>

    <!-- 功能菜单 -->
    <transition name="menu-fade">
      <div class="menu-container" v-if="isMenuOpen">
        <div class="menu-content">
          <!-- 代码监控菜单 -->
          <div class="menu-item" @click="openCodeMonitor">
            <el-icon><Monitor /></el-icon>
            <span>代码监控</span>
          </div>
          <!-- 更换图标菜单 -->
          <div class="menu-item" @click="toggleIconSelector">
            <el-icon><Brush /></el-icon>
            <span>更换图标</span>
          </div>
          <!-- 其他菜单项可以在这里添加 -->
        </div>
      </div>
    </transition>

    <!-- 图标选择器 -->
    <transition name="fade">
      <div class="icon-selector" v-if="showIconSelector">
        <div class="icon-selector-header">
          <span>选择图标</span>
          <el-icon class="close-icon" @click="showIconSelector = false"><Close /></el-icon>
        </div>
        <div class="icon-grid">
          <div
            v-for="icon in availableIcons"
            :key="icon.name"
            class="icon-item"
            :class="{ 'is-selected': currentIconName === icon.name }"
            @click="selectIcon(icon.name)"
          >
            <el-icon><component :is="icon.component" /></el-icon>
          </div>
        </div>
      </div>
    </transition>

    <!-- 代码监控弹窗 -->
    <el-dialog
      v-model="codeMonitorVisible"
      title="代码监控"
      width="80%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
      :append-to-body="true"
      :modal-class="'teacher-helper-modal'"
    >
      <div class="code-monitor-container">
        <!-- 搜索条件 -->
        <div class="search-conditions">
          <el-form :inline="true" :model="searchForm">
            <el-form-item label="搜索方式">
              <el-radio-group v-model="searchType">
                <el-radio label="keyword">关键词搜索</el-radio>
                <el-radio label="user">用户搜索</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item v-if="searchType === 'keyword'" label="关键词">
              <el-input
                v-model="searchForm.searchKey"
                placeholder="请输入搜索关键词"
                clearable
              />
            </el-form-item>

            <el-form-item v-if="searchType === 'user'" label="用户">
              <user-select
                v-model="searchForm.monitorUserId"
                :multiple="false"
                placeholder="请选择用户"
                style="width: 240px;"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 监控列表 -->
        <el-table
          v-loading="loading"
          :data="monitorList"
          border
          style="width: 100%"
          height="calc(70vh - 200px)"
          fit
          class="apple-style-table"
          :header-cell-style="{
            backgroundColor: '#f5f7fa',
            color: '#606266',
            fontWeight: 600,
            textAlign: 'center'
          }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" sortable />
          <el-table-column prop="monitorName" label="被监控人" width="120" align="center" filterable />
          <el-table-column prop="monitorPhone" label="联系电话" width="120" align="center" />
          <el-table-column prop="overlayName" label="覆盖人" width="120" align="center" filterable />
          <el-table-column label="代码" width="150" align="center">
            <template #default="{ row }">
              <code-view-button
                :code="row.monitorCode"
                :language="row.language"
                class="code-view-btn"
                :show-background="true"
                type="info"
                size="small"
                text
              >
                <template #default>
                  <el-icon><Document /></el-icon>
                  <span>查看</span>
                </template>
              </code-view-button>
            </template>
          </el-table-column>
          <el-table-column label="代码模式" width="120" align="center" filterable>
            <template #default="{ row }">
              <enum-show
                :enum="CodeMode"
                :code="row.codeMode"
                class="code-mode-tag"
              />
            </template>
          </el-table-column>
          <el-table-column label="监控状态" width="120" align="center" filterable>
            <template #default="{ row }">
              <enum-show
                :enum="CodeMonitorStatus"
                :code="row.monitorStatus"
                class="monitor-status-tag"
              />
            </template>
          </el-table-column>
          <el-table-column prop="version" label="版本" width="80" align="center" sortable />
          <el-table-column prop="createTime" label="创建时间" width="180" align="center" sortable>
            <template #default="{ row }">
              <span>{{ formatTime(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作"  fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" @click="handlePushCode(row)" size="small" class="push-btn">
                <el-icon><Upload /></el-icon>推送代码
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="searchForm.page"
            v-model:page-size="searchForm.limit"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-dialog>

    <!-- 推送代码组件 -->
    <push-coverd-code ref="pushCoverdCodeRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import {
  Monitor,
  Upload,
  Brush,
  Close,
  StarFilled,
  MagicStick,
  Cpu,
  DataLine,
  HelpFilled,
  Key,
  Mug,
  Apple,
  Document
} from '@element-plus/icons-vue'
import { searchMonitorCodeListApi } from '@/api/modules/oj/code/codeMonitor'
import type { ICodeMonitor } from '@/api/interface/oj/code/codeMonitor'
import UserSelect from '@/components/Common/User/UserSelect.vue'
import CodeViewButton from '@/components/Oj/Judge/common/CodeViewButton.vue'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import PushCoverdCode from '@/components/Oj/Code/PushCoverdCode.vue'
import { CodeMode, CodeMonitorStatus } from '@/enums/oj/code'

// 控制是否显示助手
const showHelper = computed(() => {
  return import.meta.env.VITE_APP_TITLE === 'Glowxq-OJ'
})

// 菜单状态
const isMenuOpen = ref(false)
const codeMonitorVisible = ref(false)
const loading = ref(false)
const total = ref(0)

// 可用图标列表
const availableIcons = [
  { name: 'Monitor', component: Monitor },
  { name: 'StarFilled', component: StarFilled },
  { name: 'MagicStick', component: MagicStick },
  { name: 'Cpu', component: Cpu },
  { name: 'DataLine', component: DataLine },
  { name: 'HelpFilled', component: HelpFilled },
  { name: 'Key', component: Key },
  { name: 'Mug', component: Mug },
  { name: 'Apple', component: Apple }
]

// 图标选择器
const showIconSelector = ref(false)
const currentIconName = ref('MagicStick')
const currentIcon = computed(() => {
  return availableIcons.find(icon => icon.name === currentIconName.value)?.component || Monitor
})

// 拖拽相关
const isDragging = ref(false)
const position = ref({ x: window.innerWidth - 80, y: window.innerHeight - 80 })
const dragOffset = ref({ x: 0, y: 0 })

// 搜索表单
const searchType = ref<'keyword' | 'user'>('keyword')
const searchForm = reactive<ICodeMonitor.SearchMonitorCodeDTO>({
  page: 1,
  limit: 10,
  searchKey: '',
  monitorUserId: undefined
})

// 监控列表数据
const monitorList = ref<ICodeMonitor.Row[]>([])
const pushCoverdCodeRef = ref<InstanceType<typeof PushCoverdCode>>()

// 开始拖拽
const startDrag = (event: MouseEvent) => {
  // 判断是否是左键点击
  if (event.button !== 0) return

  event.preventDefault()
  event.stopPropagation()

  isDragging.value = true
  dragOffset.value = {
    x: event.clientX - position.value.x,
    y: event.clientY - position.value.y
  }

  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

// 拖拽中
const onDrag = (event: MouseEvent) => {
  if (!isDragging.value) return

  // 计算新位置
  let newX = event.clientX - dragOffset.value.x
  let newY = event.clientY - dragOffset.value.y

  // 边界处理，避免拖出视口
  const buttonSize = 44 // 按钮大小
  newX = Math.max(0, Math.min(window.innerWidth - buttonSize, newX))
  newY = Math.max(0, Math.min(window.innerHeight - buttonSize, newY))

  position.value = { x: newX, y: newY }
}

// 停止拖拽
const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// 切换菜单
const toggleMenu = (event: MouseEvent) => {
  event.stopPropagation()
  isMenuOpen.value = !isMenuOpen.value

  // 关闭图标选择器
  if (isMenuOpen.value) {
    showIconSelector.value = false
  }
}

// 切换图标选择器
const toggleIconSelector = () => {
  showIconSelector.value = !showIconSelector.value
  isMenuOpen.value = false
}

// 选择图标
const selectIcon = (iconName: string) => {
  currentIconName.value = iconName
  showIconSelector.value = false
}

// 关闭菜单
const closeMenu = () => {
  isMenuOpen.value = false
}

// 打开代码监控
const openCodeMonitor = () => {
  codeMonitorVisible.value = true
  closeMenu()
  getMonitorList()
}

// 获取监控列表
const getMonitorList = async () => {
  loading.value = true
  try {
    const res = await searchMonitorCodeListApi(searchForm)
    if (res.code === '0000' && res.data) {
      // 直接使用返回的数组数据
      monitorList.value = Array.isArray(res.data) ? res.data : []
      total.value = monitorList.value.length
      console.log('监控列表数据:', monitorList.value)
    }
  } catch (error) {
    console.error('获取监控列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  searchForm.page = 1
  getMonitorList()
}

// 重置搜索
const resetSearch = () => {
  searchType.value = 'keyword'
  searchForm.searchKey = ''
  searchForm.monitorUserId = undefined
  handleSearch()
}

// 分页处理
const handleSizeChange = (val: number) => {
  searchForm.limit = val
  getMonitorList()
}

const handleCurrentChange = (val: number) => {
  searchForm.page = val
  getMonitorList()
}

// 推送代码
const handlePushCode = (row: ICodeMonitor.Row) => {
  pushCoverdCodeRef.value?.open(row)
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString()
}

// 监听窗口大小变化
const handleResize = () => {
  // 如果当前位置超出了新的窗口大小，重新调整位置
  const buttonSize = 44
  if (position.value.x > window.innerWidth - buttonSize) {
    position.value.x = window.innerWidth - buttonSize
  }
  if (position.value.y > window.innerHeight - buttonSize) {
    position.value.y = window.innerHeight - buttonSize
  }
}

// 全局点击事件处理
const handleGlobalClick = (event: MouseEvent) => {
  // 检查点击事件是否发生在菜单之外
  const helperEl = document.querySelector('.teacher-helper')
  if (helperEl && !helperEl.contains(event.target as Node)) {
    isMenuOpen.value = false
    showIconSelector.value = false
  }
}

// 挂载时添加监听
onMounted(() => {
  if (showHelper.value) {
    window.addEventListener('resize', handleResize)
    document.addEventListener('click', handleGlobalClick)
  }
})

// 卸载前移除监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('click', handleGlobalClick)
  // 确保移除拖拽相关的监听器
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
})
</script>

<style scoped lang="scss">
.teacher-helper {
  position: fixed;
  z-index: 9999;
  transition: none; /* 拖拽时不需要过渡 */
}

.floating-button {
  width: 44px;
  height: 44px;
  border-radius: 22px;
  background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15), 0 0 0 4px rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: move; /* 指示可拖动 */
  position: relative;
  transition: transform 0.3s, box-shadow 0.3s, background 0.3s;
  z-index: 1000;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2), 0 0 0 6px rgba(255, 255, 255, 0.25);
  }

  &.is-active {
    transform: rotate(45deg);
    background: linear-gradient(135deg, var(--el-color-danger), var(--el-color-danger-light-3));
  }

  .button-inner {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    cursor: pointer;

    &:active {
      transform: scale(0.95);
    }
  }

  .button-icon {
    font-size: 20px;
    color: white;
    filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.1));
  }

  &::before {
    content: '';
    position: absolute;
    top: -4px;
    left: -4px;
    right: -4px;
    bottom: -4px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    z-index: -1;
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover::before {
    opacity: 1;
  }
}

.menu-container {
  position: absolute;
  right: 52px;
  top: 0;
  z-index: 999;
}

.menu-content {
  background: white;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  padding: 6px;
  min-width: 160px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    right: -8px;
    top: 16px;
    border-width: 8px 0 8px 8px;
    border-style: solid;
    border-color: transparent transparent transparent white;
  }
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  color: var(--el-text-color-primary);
  font-size: 14px;

  &:hover {
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    transform: translateX(2px);
  }

  .el-icon {
    margin-right: 8px;
    font-size: 16px;
  }
}

.icon-selector {
  position: absolute;
  left: 52px;
  top: 0;
  background: white;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  padding: 12px;
  width: 200px;
  z-index: 999;

  &::after {
    content: '';
    position: absolute;
    right: -8px;
    top: 16px;
    border-width: 8px 0 8px 8px;
    border-style: solid;
    border-color: transparent transparent transparent white;
  }
}

.icon-selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--el-border-color-light);

  span {
    font-weight: 600;
    color: var(--el-text-color-primary);
    font-size: 14px;
  }

  .close-icon {
    cursor: pointer;
    color: var(--el-text-color-secondary);

    &:hover {
      color: var(--el-text-color-primary);
    }
  }
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.icon-item {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40px;
  background: var(--el-fill-color-light);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;

  .el-icon {
    font-size: 20px;
    color: var(--el-text-color-primary);
  }

  &:hover {
    background: var(--el-color-primary-light-9);
    transform: translateY(-2px);

    .el-icon {
      color: var(--el-color-primary);
    }
  }

  &.is-selected {
    background: var(--el-color-primary-light-5);
    box-shadow: 0 2px 6px var(--el-color-primary-light-7);

    .el-icon {
      color: var(--el-color-primary);
    }
  }
}

.code-monitor-container {
  .search-conditions {
    margin-bottom: 20px;
    padding: 16px;
    background-color: var(--el-bg-color-page);
    border-radius: 8px;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .el-table {
    width: 100% !important;
    overflow-x: hidden;

    &::before {
      height: 0;
    }
  }

  :deep(.el-table__inner-wrapper) {
    overflow-x: auto;
  }

  .code-view-btn {
    opacity: 0.75;
    color: #606266;
    padding: 4px 8px;
    border-radius: 4px;
    transition: all 0.2s ease;
    font-size: 12px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background-color: #f4f4f5;
    border: 1px solid #ebeef5;

    &:hover {
      opacity: 1;
      background-color: #f0f2f5;
      color: var(--el-color-primary);
      border-color: #e4e7ed;
    }

    .el-icon {
      font-size: 14px;
      margin-right: 3px;
    }

    span {
      font-weight: normal;
    }
  }
}

/* 动画 */
.menu-fade-enter-active,
.menu-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.menu-fade-enter-from,
.menu-fade-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 确保弹窗不被蒙层覆盖 */
:deep(.teacher-helper-modal) {
  backdrop-filter: blur(4px);
}

:deep(.el-dialog__wrapper) {
  z-index: 10000 !important;
}

/* Apple风格表格样式 */
.apple-style-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: none;

  :deep(.el-table__header) {
    th {
      background-color: #f8f8f8 !important;
      border-bottom: 1px solid #eaeaea;
      transition: background-color 0.2s;
      position: relative;

      &:not(:last-child)::after {
        content: '';
        position: absolute;
        right: 0;
        top: 25%;
        height: 50%;
        width: 1px;
        background-color: #e0e0e0;
      }

      .cell {
        padding: 12px 8px;
        font-size: 13px;
        font-weight: 600;
        letter-spacing: -0.01em;
      }
    }
  }

  :deep(.el-table__body) {
    td {
      padding: 8px 0;
      border-bottom: 1px solid #f0f0f0;
      transition: background-color 0.2s;

      .cell {
        padding: 8px;
        font-size: 13px;
        color: #333;
      }
    }

    tr {
      &:hover > td {
        background-color: rgba(var(--el-color-primary-rgb), 0.05) !important;
      }

      &:last-child td {
        border-bottom: none;
      }
    }
  }

  /* 过滤器和排序图标样式 */
  :deep(.el-table__column-filter-trigger) {
    margin-left: 4px;
    color: #909399;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  :deep(.caret-wrapper) {
    margin-left: 4px;

    .sort-caret {
      border-width: 4px;

      &.ascending {
        border-bottom-color: #c0c4cc;
      }

      &.descending {
        border-top-color: #c0c4cc;
      }
    }
  }

  :deep(.ascending .sort-caret.ascending) {
    border-bottom-color: var(--el-color-primary);
  }

  :deep(.descending .sort-caret.descending) {
    border-top-color: var(--el-color-primary);
  }
}

/* 推送按钮样式强调 */
.push-btn {
  font-weight: 600;
  padding: 6px 12px;
  box-shadow: 0 2px 5px rgba(var(--el-color-primary-rgb), 0.3);
  border-radius: 4px;
  transition: all 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(var(--el-color-primary-rgb), 0.4);
  }

  .el-icon {
    margin-right: 4px;
  }
}
</style>
