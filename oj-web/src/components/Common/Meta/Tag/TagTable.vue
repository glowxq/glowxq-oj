<template>
  <div class="meta-tag-table">
    <!-- 顶部控制区 -->
    <div class="table-controls">
      <!-- 分类筛选下拉框 -->
      <el-select
        v-model="selectedCategoryId"
        clearable
        filterable
        placeholder="选择标签分类"
        class="category-select control-item"
        popper-class="apple-select-dropdown category-dropdown"
        @change="handleCategoryChange"
      >
        <template #prefix>
          <el-icon class="category-icon"><Collection /></el-icon>
        </template>
        <el-option 
          v-for="category in categories" 
          :key="category.id" 
          :label="category.name" 
          :value="category.id"
        >
          <div class="category-option">
            <span>{{ category.name }}</span>
            <el-tag size="small" type="info" class="count-tag" v-if="category.id !== undefined && categoryCount[category.id]">
              {{ categoryCount[category.id] }}
            </el-tag>
          </div>
        </el-option>
      </el-select>

      <!-- 搜索框 -->
      <el-input
        v-model="searchQuery"
        placeholder="搜索标签"
        clearable
        @input="handleSearch"
        class="search-input control-item"
      >
        <template #prefix>
          <el-icon class="search-icon"><Search /></el-icon>
        </template>
      </el-input>

      <!-- 清空按钮 -->
      <el-button 
        v-if="selectedTags.length > 0" 
        @click="clearSelection" 
        type="primary"
        size="default"
        class="clear-button control-item"
      >
        清空选择 ({{ selectedTags.length }})
      </el-button>
    </div>

    <!-- 已选标签区域 -->
    <div v-if="selectedTags.length > 0" class="selected-tags-container">
      <div class="selected-tags-header">
        <h4>已选标签</h4>
      </div>
      <div class="selected-tags-list">
        <el-tag
          v-for="tag in selectedTags"
          :key="`selected-${tag.id}`"
          :style="getTagStyle(tag)"
          closable
          @close="toggleSelection(tag)"
          class="selected-tag"
        >
          {{ tag.name }}
        </el-tag>
      </div>
    </div>

    <!-- 标签网格容器 -->
    <div class="tags-grid-container">
      <div 
        v-loading="loading"
        class="tags-grid"
        v-infinite-scroll="loadMore"
        :infinite-scroll-disabled="infiniteDisabled"
        :infinite-scroll-distance="10"
      >
        <!-- 标签卡片 -->
        <div 
          v-for="row in tagList" 
          :key="row.id"
          class="tag-card"
          :class="{ 'tag-selected': isSelected(row) }"
          @click="toggleSelection(row)"
        >
          <el-tag
            :style="getTagStyle(row)"
            class="tag-item"
          >
            {{ row.name }}
          </el-tag>
        </div>
        
        <!-- 加载中状态 -->
        <div v-if="loading" class="loading-more">
          <div class="loading-spinner">
            <el-icon class="loading-icon"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
        </div>
        
        <!-- 没有更多数据状态 -->
        <div v-if="!hasMore && tagList.length > 0" class="no-more">
          没有更多标签
        </div>
        
        <!-- 空状态 -->
        <div v-if="tagList.length === 0 && !loading" class="empty-state">
          <el-icon class="empty-icon"><DocumentRemove /></el-icon>
          <span>{{ searchQuery || selectedCategoryId ? '没有找到匹配的标签' : '暂无标签' }}</span>
          <el-button type="primary" size="small" plain @click="resetFilters">清除筛选</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Loading, DocumentRemove, Collection } from '@element-plus/icons-vue'
import { getMetaTagListApi } from '@/api/modules/system/meta/metaTag'
import { getMetaTagCategoryListApi } from '@/api/modules/system/meta/metaTagCategory'
import type { IMetaTag } from '@/api/interface/system/meta/metaTag'
import type { IMetaTagCategory } from '@/api/interface/system/meta/metaTagCategory'
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
const tagList = ref<IMetaTag.Row[]>([])
const selectedTags = ref<IMetaTag.Row[]>([])
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(24)
const hasMore = ref(true)
const apiErrorCount = ref(0)
const lastErrorTime = ref(0)
const maxRetries = 3
const retryDelay = 5000 // 5秒冷却时间

// 分类相关
const categories = ref<IMetaTagCategory.Row[]>([])
const selectedCategoryId = ref<number | undefined>(undefined)
const categoryCount = ref<Record<number, number>>({})

// 计算是否禁用无限滚动
const infiniteDisabled = computed(() => {
  return loading.value || !hasMore.value
})

// 获取标签样式
const getTagStyle = (tag: IMetaTag.Row) => {
  if (tag.plain === 'true') {
    return {
      color: tag.backgroundColor || '#0071e3',
      borderColor: tag.backgroundColor || '#0071e3',
      backgroundColor: 'transparent'
    }
  } else {
    return {
      color: tag.textColor || '#FFFFFF',
      backgroundColor: tag.backgroundColor || '#0071e3',
      borderColor: tag.backgroundColor || '#0071e3'
    }
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await getMetaTagCategoryListApi({
      enable: 'true',
      page: 1,
      limit: 100
    })
    categories.value = res.data.rows || []
  } catch (error) {
    console.error('获取标签分类失败:', error)
  }
}

// 加载分类标签数量
const loadCategoryCount = async () => {
  try {
    // 使用getCategoryListApi获取所有分类，然后针对每个分类查询标签数量
    const res = await getMetaTagListApi({
      page: 1,
      limit: 999,
      enable: 'true'
    })
    
    if (res.data.rows && res.data.rows.length) {
      // 计算每个分类的标签数量
      const countMap: Record<number, number> = {}
      res.data.rows.forEach((tag: IMetaTag.Row) => {
        if (tag.categoryId) {
          const catId = Number(tag.categoryId)
          countMap[catId] = (countMap[catId] || 0) + 1
        }
      })
      categoryCount.value = countMap
    }
  } catch (error) {
    console.error('获取分类标签数量失败:', error)
  }
}

// 处理分类变更
const handleCategoryChange = (value: number | undefined) => {
  selectedCategoryId.value = value
  currentPage.value = 1
  loadTags(true)
}

// 加载标签数据
const loadTags = async (isReset: boolean = false) => {
  if (isReset) {
    currentPage.value = 1
    tagList.value = []
    hasMore.value = true
    apiErrorCount.value = 0
  }

  if (!hasMore.value && !isReset) return
  
  // 检查API错误次数和冷却时间
  const now = Date.now()
  if (apiErrorCount.value >= maxRetries && now - lastErrorTime.value < retryDelay) {
    ElMessage.warning('请求失败次数过多，请稍后再试')
    return
  }

  loading.value = true
  try {
    const res = await getMetaTagListApi({
      name: searchQuery.value || undefined,
      categoryId: selectedCategoryId.value,
      enable: 'true',
      page: currentPage.value,
      limit: pageSize.value
    })

    if (res?.data) {
      const newRows = res.data.rows || []
      const totalCount = res.data.total || 0

      if (newRows.length < pageSize.value) {
        hasMore.value = false
      } else {
        hasMore.value = tagList.value.length + newRows.length < totalCount
      }

      tagList.value = isReset ? newRows : [...tagList.value, ...newRows]
      console.log(`标签加载成功: ${newRows.length}个, 总计${tagList.value.length}/${totalCount}个, 是否有更多: ${hasMore.value}`)

      // 如果重置数据且有选中值，尝试从新加载的数据中恢复选中状态
      if (isReset && props.modelValue.length > 0) {
        syncSelectedTags()
      }

      // 如果没有加载到数据，但是应该有数据
      if (tagList.value.length === 0 && totalCount > 0) {
        console.warn('数据加载异常: 应有数据但未获取到')
      }

      apiErrorCount.value = 0 // 成功后重置错误计数
    } else {
      console.error('获取标签列表返回数据格式异常:', res)
      hasMore.value = false
    }
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
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
  loadTags(true)
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
  loadTags()
}

// 检查标签是否被选中
const isSelected = (tag: IMetaTag.Row) => {
  return selectedTags.value.some(item => item.id === tag.id)
}

// 切换标签选择状态
const toggleSelection = (tag: IMetaTag.Row) => {
  const index = selectedTags.value.findIndex(item => item.id === tag.id)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }

  const selectedIds = selectedTags.value.map(item => item.id)
  // 只发出 change 事件，让父组件处理，避免重复事件
  emit('change', selectedIds)
}

// 清空所有选择
const clearSelection = () => {
  selectedTags.value = []
  // 只发出 change 事件，让父组件处理
  emit('change', [])
}

// 重置筛选条件
const resetFilters = () => {
  searchQuery.value = ''
  selectedCategoryId.value = undefined
  loadTags(true)
}

// 同步选中的标签
const syncSelectedTags = () => {
  if (props.modelValue.length === 0) {
    selectedTags.value = []
    return
  }

  // 保留已经在selectedTags中的标签
  const existingTags = selectedTags.value.filter(tag => 
    !tagList.value.some(t => t.id === tag.id)
  )

  // 添加从tagList中匹配到的标签
  const newSelectedTags = tagList.value.filter(tag => 
    props.modelValue.includes(tag.id as number)
  )

  // 合并去重
  selectedTags.value = [
    ...existingTags,
    ...newSelectedTags
  ]
}

// 同步外部传入的值
watch(() => props.modelValue, (newVal) => {
  if (newVal.length === 0) {
    selectedTags.value = []
  } else {
    syncSelectedTags()
    
    // 如果有选中的ID但没有在当前列表中找到，可能需要加载更多数据
    const missingIds = newVal.filter(id => 
      !tagList.value.some(tag => tag.id === id)
    )
    
    if (missingIds.length > 0 && tagList.value.length === 0) {
      loadTags(true)
    }
  }
}, { immediate: true, deep: true })

// 组件挂载时加载数据
onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      loadCategories(),
      loadCategoryCount()
    ])
    // 确保首次加载标签数据
    await loadTags(true) // 传入true表示重置加载
    console.log('TagTable组件初始化完成，加载了', tagList.value.length, '个标签')
  } catch (error) {
    console.error('TagTable初始化加载失败:', error)
  } finally {
    loading.value = false
  }
})

// 暴露方法给父组件使用
defineExpose({
  clearSelection,
  loadTags,
  resetFilters
})
</script>

<style scoped lang="scss">
.meta-tag-table {
  width: 220px;
  height: 100%;
  border-radius: 16px;
  overflow: hidden;
  background-color: #ffffff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;

  // 顶部控制区
  .table-controls {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .control-item {
      width: 100%;
    }
    
    .category-select, .search-input {
      .el-input__wrapper {
        border-radius: 10px;
        box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
        transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
        
        &:hover {
          box-shadow: 0 0 0 1px rgba(var(--el-color-primary-rgb, 0, 113, 227), 0.5);
        }
        
        &.is-focus {
          box-shadow: 0 0 0 2px rgba(var(--el-color-primary-rgb, 0, 113, 227), 0.3), 0 0 0 1px var(--el-color-primary, #0071e3);
        }
      }
    }
    
    .category-icon, .search-icon {
      color: #8e8e93;
      margin-right: 8px;
    }
    
    .category-option {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
      
      .count-tag {
        background-color: #f2f2f7;
        color: #8e8e93;
        font-size: 11px;
        padding: 0 6px;
        border-radius: 10px;
        border: none;
        font-weight: 500;
      }
    }
    
    .clear-button {
      border-radius: 10px;
      font-weight: 500;
      transition: all 0.2s cubic-bezier(0.25, 0.1, 0.25, 1.0);
      white-space: nowrap;
      background-color: var(--el-color-primary, #0071e3);
      border-color: var(--el-color-primary, #0071e3);
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
        opacity: 0.9;
      }
      
      &:active {
        transform: scale(0.98);
      }
    }
  }

  // 已选标签区域
  .selected-tags-container {
    background-color: #f5f7fa;
    border-radius: 12px;
    padding: 16px;
    
    .selected-tags-header {
      margin-bottom: 12px;
      
      h4 {
        margin: 0;
        font-size: 14px;
        color: #8e8e93;
        font-weight: 500;
      }
    }
    
    .selected-tags-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      .selected-tag {
        margin: 0;
        border-radius: 20px;
        padding: 4px 10px;
        font-size: 13px;
        font-weight: 500;
        transition: all 0.2s ease;
        
        &:hover {
          transform: translateY(-1px);
        }
      }
    }
  }

  // 标签网格
  .tags-grid-container {
    flex: 1;
    position: relative;
  }

  .tags-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 16px;
    min-height: 200px;
    max-height: 400px;
    overflow-y: auto;
    padding: 4px;
    position: relative;
    
    // 美化滚动条
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: transparent;
    }
    
    &::-webkit-scrollbar-thumb {
      background-color: rgba(0, 0, 0, 0.1);
      border-radius: 10px;
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.2);
      }
    }
    
    // 标签卡片
    .tag-card {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 12px;
      border-radius: 12px;
      background-color: #f5f7fa;
      transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1.0);
      cursor: pointer;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 8px 15px rgba(0, 0, 0, 0.08);
      }
      
      &:active {
        transform: scale(0.97);
      }
      
      &.tag-selected {
        background-color: rgba(0, 113, 227, 0.08);
        box-shadow: 0 6px 15px rgba(0, 113, 227, 0.15);
        
        &:hover {
          box-shadow: 0 8px 20px rgba(0, 113, 227, 0.2);
        }
      }
      
      .tag-item {
        width: 100%;
        text-align: center;
        margin: 0;
        padding: 6px 10px;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 500;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
        transition: all 0.2s ease;
      }
    }
    
    // 加载更多、无更多数据和空状态
    .loading-more, .no-more, .empty-state {
      grid-column: 1 / -1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 30px 20px;
      color: #8e8e93;
      gap: 12px;
      text-align: center;
    }
    
    .loading-spinner {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      
      .loading-icon {
        font-size: 24px;
        color: #0071e3;
        animation: spin 1.2s linear infinite;
      }
    }
    
    .empty-state {
      .empty-icon {
        font-size: 40px;
        margin-bottom: 10px;
        color: #c7c7cc;
      }
      
      .el-button {
        margin-top: 10px;
        border-radius: 20px;
        font-weight: 500;
      }
    }
  }
}

// 移动端样式已集成到主样式中，不需要额外适配

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>

<style>
/* 全局样式 */
.apple-select-dropdown.category-dropdown {
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.08);
  border: none;
  padding: 6px 0;
}

.apple-select-dropdown.category-dropdown .el-select-dropdown__item {
  height: 36px;
  line-height: 36px;
  padding: 0 16px;
}

.apple-select-dropdown.category-dropdown .el-select-dropdown__item.hover,
.apple-select-dropdown.category-dropdown .el-select-dropdown__item:hover {
  background-color: rgba(0, 113, 227, 0.08);
}

.apple-select-dropdown.category-dropdown .el-select-dropdown__item.selected {
  background-color: rgba(0, 113, 227, 0.15);
  color: #0071e3;
  font-weight: 500;
}
</style>
