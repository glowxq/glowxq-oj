<template>
  <div class="group-table">
    <!-- 搜索框和清空按钮 -->
    <div class="table-header">
      <el-input
        v-model="searchQuery"
        placeholder="搜索班级"
        clearable
        @input="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button
        v-if="selectedGroups.length > 0"
        @click="clearSelection"
        type="danger"
        plain
        size="small"
        class="clear-button"
      >
        清空选择
      </el-button>
    </div>

    <!-- 班级展示区域 -->
    <div class="groups-container">
      <div
        v-loading="loading"
        class="groups-grid"
        v-infinite-scroll="loadMore"
        :infinite-scroll-disabled="infiniteDisabled"
        :infinite-scroll-distance="10"
      >
        <div
          v-for="row in groupList"
          :key="row.id"
          class="group-wrapper"
          :class="{ 'group-selected': isSelected(row) }"
          @click="toggleSelection(row)"
        >
          <div class="group-item">
            <el-tag
              :style="{
                backgroundColor: isSelected(row) ? row.color : 'transparent',
                color: isSelected(row) ? row.textColor : row.color,
                borderColor: row.color,
                cursor: 'pointer'
              }"
              class="tag-item"
            >
              {{ row.code ? `${row.code} - ` : '' }}{{ row.name }}
            </el-tag>
            <span v-if="row.principalName" class="principal-name">
              负责人: {{ row.principalName }}
            </span>
          </div>
        </div>
        <div v-if="loading" class="loading-more">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
        <div v-if="!hasMore && groupList.length > 0" class="no-more">
          没有更多班级
        </div>
        <div v-if="groupList.length === 0 && !loading" class="empty-state">
          <el-icon><School /></el-icon>
          <span>{{ searchQuery ? '没有找到匹配的班级' : '暂无班级' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Loading, School } from '@element-plus/icons-vue'
import { getGroupListApi } from '@/api/modules/oj/group/group'
import type { IGroup } from '@/api/interface/oj/group/group'
import { ElMessage } from 'element-plus'
import { debounce } from 'lodash-es'

interface Props {
  modelValue?: number[]
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => []
})

const emit = defineEmits(['update:modelValue', 'change'])

// 数据相关
const loading = ref(false)
const groupList = ref<IGroup.Row[]>([])
const selectedGroups = ref<IGroup.Row[]>([])
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(24) // 增加每页数量以适应网格布局
const hasMore = ref(true)
const tableRef = ref()
const apiErrorCount = ref(0)
const lastErrorTime = ref(0)
const maxRetries = 3
const retryDelay = 5000 // 5秒冷却时间

// 计算是否禁用无限滚动
const infiniteDisabled = computed(() => {
  return loading.value || !hasMore.value
})

// 加载班级数据
const loadGroups = async (isReset: boolean = false) => {
  if (isReset) {
    currentPage.value = 1
    groupList.value = []
    hasMore.value = true
    apiErrorCount.value = 0
  }

  if (!hasMore.value) return
  
  // 检查API错误次数和冷却时间
  const now = Date.now()
  if (apiErrorCount.value >= maxRetries && now - lastErrorTime.value < retryDelay) {
    ElMessage.warning('请求失败次数过多，请稍后再试')
    return
  }

  loading.value = true
  try {
    const res = await getGroupListApi({
      name: searchQuery.value || undefined,
      page: currentPage.value,
      limit: pageSize.value
    })

    const newRows = res.data.rows || []

    if (newRows.length < pageSize.value) {
      hasMore.value = false
    }

    groupList.value = isReset ? newRows : [...groupList.value, ...newRows]
    apiErrorCount.value = 0 // 成功后重置错误计数
  } catch (error) {
    console.error('获取班级列表失败:', error)
    ElMessage.error('获取班级列表失败')
    apiErrorCount.value++
    lastErrorTime.value = Date.now()
    hasMore.value = false // 出错时停止加载更多
  } finally {
    loading.value = false
  }
}

// 使用防抖处理搜索
const handleSearch = debounce(() => {
  if (apiErrorCount.value >= maxRetries) {
    const now = Date.now()
    if (now - lastErrorTime.value < retryDelay) {
      ElMessage.warning('请求失败次数过多，请稍后再试')
      return
    }
    apiErrorCount.value = 0 // 重置错误计数
  }
  loadGroups(true)
}, 300)

// 加载更多
const loadMore = () => {
  if (loading.value || !hasMore.value) return
  if (apiErrorCount.value >= maxRetries) {
    const now = Date.now()
    if (now - lastErrorTime.value < retryDelay) {
      return // 静默失败，不显示额外的消息以避免用户困扰
    }
    apiErrorCount.value = 0 // 重置错误计数
  }
  currentPage.value++
  loadGroups()
}

// 检查班级是否被选中
const isSelected = (group: IGroup.Row) => {
  return selectedGroups.value.some(item => item.id === group.id)
}

// 切换班级选择状态
const toggleSelection = (group: IGroup.Row) => {
  const index = selectedGroups.value.findIndex(item => item.id === group.id)
  if (index > -1) {
    selectedGroups.value.splice(index, 1)
  } else {
    selectedGroups.value.push(group)
  }

  const selectedIds = selectedGroups.value.map(item => item.id as number)
  emit('update:modelValue', selectedIds)

  // 传递选中的班级对象和ID给父组件
  emit('change', selectedIds, selectedGroups.value)
}

// 清空所有选择
const clearSelection = () => {
  selectedGroups.value = []
  emit('update:modelValue', [])
  emit('change', [], [])
}

// 同步外部传入的值
watch(() => props.modelValue, (newVal) => {
  if (newVal.length === 0) {
    selectedGroups.value = []
  } else if (groupList.value.length > 0) {
    selectedGroups.value = groupList.value.filter(group => newVal.includes(group.id as number))
  }
}, { immediate: true, deep: true })

// 组件挂载时加载数据
onMounted(() => {
  loadGroups()
})

// 暴露方法给父组件使用
defineExpose({
  clearSelection
})
</script>

<style scoped lang="scss">
.group-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--el-bg-color);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);

  .table-header {
    margin-bottom: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px 0;

    .search-input {
      width: calc(100% - 80px);
      border-radius: 6px;
      overflow: hidden;

      :deep(.el-input__wrapper) {
        border-radius: 6px;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
        padding: 2px 8px;
        height: 28px;
        transition: all 0.2s ease;

        &.is-focus {
          box-shadow: 0 0 0 1px var(--el-color-primary-light-5);
        }
      }

      :deep(.el-input__inner) {
        font-size: 13px;
      }
    }

    .clear-button {
      border-radius: 6px;
      font-size: 12px;
      height: 28px;
      padding: 0 8px;
      transition: all 0.2s ease;

      &:hover {
        transform: scale(1.02);
      }

      &:active {
        transform: scale(0.98);
      }
    }
  }

  .groups-container {
    padding: 0 12px 8px;
  }

  .groups-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 8px;
    padding: 4px 0;
    min-height: 120px;
    max-height: 300px;
    overflow-y: auto;
    position: relative;

    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(0, 0, 0, 0.1);
      border-radius: 2px;
    }
  }

  .group-wrapper {
    display: flex;
    justify-content: center;
    border-radius: 8px;
    background-color: var(--el-fill-color-lighter);
    transition: all 0.2s ease;
    overflow: hidden;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }

    &.group-selected {
      background-color: var(--el-color-primary-light-9);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
      }
    }
  }

  .group-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px;
    width: 100%;
    cursor: pointer;

    .tag-item {
      margin-bottom: 4px;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
      transition: all 0.2s ease;
      text-align: center;
      line-height: 1.2;

      &:hover {
        transform: scale(1.02);
      }
    }

    .principal-name {
      font-size: 10px;
      color: var(--el-text-color-secondary);
      margin-top: 2px;
      transition: all 0.2s;
      text-align: center;
      line-height: 1.2;
    }
  }

  .loading-more, .no-more, .empty-state {
    grid-column: 1 / -1;
    text-align: center;
    padding: 12px;
    color: var(--el-text-color-secondary);
    font-size: 12px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
  }

  .loading-icon {
    animation: rotating 2s linear infinite;
    font-size: 18px;
  }

  .empty-state {
    .el-icon {
      font-size: 24px;
      margin-bottom: 6px;
      opacity: 0.5;
    }
  }
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
