<template>
  <div class="meta-tag-select">
    <!-- 分类筛选下拉框 -->
    <el-select
      v-if="showCategoryFilter"
      v-model="selectedCategoryId"
      clearable
      filterable
      placeholder="选择标签分类"
      class="category-select"
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
          <el-tag size="small" type="info" class="count-tag" v-if="category.id !== undefined && categoryCount[category.id as number]">
            {{ categoryCount[category.id as number] }}
          </el-tag>
        </div>
      </el-option>
    </el-select>

    <el-select
        v-model="selectedTags"
        :multiple="multiple"
        :collapse-tags="false"
        :max-collapse-tags="maxTagCount"
        :collapse-tags-tooltip="collapseTagsTooltip"
        filterable
        remote
        :remote-method="remoteSearch"
        :loading="loading"
        :placeholder="placeholder"
        :clearable="clearable"
        class="apple-select enhanced-tag-select"
        popper-class="apple-select-dropdown custom-tag-select-dropdown"
        @change="handleChange"
        @visible-change="handleDropdownVisibility"
    >
      <!-- 自定义标签渲染 -->
      <template #tag="{ closable, onClose }">
        <el-tag
          v-for="tag in selectedTagsData"
          :key="tag.id"
          :closable="closable"
          :style="getTagStyle(tag)"
          :effect="tag.plain === 'true' ? 'plain' : 'dark'"
          class="custom-tag"
          @close="onClose"
        >
          {{ tag.name }}
        </el-tag>
      </template>
      <!-- 快速操作区域 -->
      <template #prefix v-if="showCreateButton">
        <el-tooltip content="创建新标签" placement="top">
          <el-button
              class="create-tag-btn"
              :icon="Plus"
              circle
              type="primary"
              @click.stop="handleCreateTag"
          ></el-button>
        </el-tooltip>
      </template>

      <!-- 空状态 -->
      <template #empty>
        <div class="empty-state">
          <el-empty description="暂无匹配标签" :image-size="60">
            <template #image>
              <el-icon class="empty-icon"><Document /></el-icon>
            </template>
            <el-button
                v-if="showCreateButton"
                type="primary"
                size="small"
                class="apple-button"
                @click="handleCreateTag"
            >
              创建新标签
            </el-button>
          </el-empty>
        </div>
      </template>

      <!-- 分组：常用标签 -->
      <el-option-group v-if="frequentTags.length > 0" label="常用标签">
        <el-option
            v-for="item in frequentTags"
            :key="item.id"
            :label="item.name"
            :value="item.id"
        >
          <div class="tag-option">
            <el-tag
                :style="getTagStyle(item)"
                effect="plain"
                size="small"
                class="apple-tag"
            >
              {{ item.name }}
            </el-tag>
          </div>
        </el-option>
      </el-option-group>

      <!-- 所有标签 -->
      <el-option-group v-if="tagOptions.length > 0" :label="frequentTags.length ? '所有标签' : undefined">
        <el-option
            v-for="item in tagOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
        >
          <div class="tag-option">
            <el-tag
                :style="getTagStyle(item)"
                :effect="item.plain ? 'plain' : 'dark'"
                size="small"
                class="apple-tag"
            >
              {{ item.name }}
            </el-tag>
          </div>
        </el-option>
      </el-option-group>

      <!-- 加载更多提示 -->
      <div 
        v-if="hasMoreData" 
        class="load-more-button" 
        ref="loadMoreRef"
        @click="loadMoreData"
      >
        <div v-if="!loading" class="load-more-content">
          <el-icon><ArrowDown /></el-icon>
          <span>加载更多标签</span>
        </div>
        <div v-else class="load-more-content loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>正在加载...</span>
        </div>
      </div>
    </el-select>

    <!-- 创建标签对话框 -->
    <el-dialog
        v-model="createTagDialogVisible"
        title="创建新标签"
        width="420px"
        class="create-tag-dialog"
        append-to-body
        destroy-on-close
    >
      <el-form ref="tagFormRef" :model="newTag" :rules="rules" label-width="80px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="newTag.name" placeholder="请输入标签名称" class="apple-input" />
        </el-form-item>
        <el-form-item label="标签分类" prop="categoryId">
          <el-select 
            v-model="newTag.categoryId" 
            placeholder="请选择标签分类" 
            filterable 
            clearable 
            class="apple-select"
            popper-class="apple-select-dropdown"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="背景颜色" prop="backgroundColor">
          <el-color-picker v-model="newTag.backgroundColor" :predefine="predefineColors" @change="calculateTextColor" />
        </el-form-item>
        <el-form-item label="文字颜色" prop="textColor">
          <el-color-picker v-model="newTag.textColor" :predefine="predefineColors" />
        </el-form-item>
        <el-form-item label="镂空效果" prop="plain">
          <el-switch v-model="newTag.plain" class="apple-switch" />
        </el-form-item>
        <el-form-item label="预览">
          <div class="tag-preview">
            <el-tag
                :style="getTagStyle(newTag)"
                :effect="newTag.plain ? 'plain' : 'dark'"
                size="small"
                class="apple-tag"
            >
              {{ newTag.name || '标签预览' }}
            </el-tag>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="apple-button cancel" @click="createTagDialogVisible = false">取消</el-button>
          <el-button type="primary" class="apple-button create" @click="submitNewTag" :loading="submitting">创建</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Document, Plus, Collection, ArrowDown, Loading } from '@element-plus/icons-vue'
import { getMetaTagListApi, createMetaTagApi, getMetaTagCategoryCountApi } from '@/api/modules/system/meta/metaTag'
import { getMetaTagCategoryListApi } from '@/api/modules/system/meta/metaTagCategory'
import type { IMetaTag } from '@/api/interface/system/meta/metaTag'
import type { IMetaTagCategory } from '@/api/interface/system/meta/metaTagCategory'

interface Props {
  modelValue?: number | number[]
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  collapseTags?: boolean
  collapseTagsTooltip?: boolean
  showCreateButton?: boolean
  showCategoryFilter?: boolean
  frequentTagIds?: number[]
  maxTagCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  placeholder: '请选择标签',
  clearable: true,
  collapseTags: false,
  collapseTagsTooltip: true,
  showCreateButton: true,
  showCategoryFilter: true,
  frequentTagIds: () => [],
  modelValue: undefined,
  maxTagCount: 10
})

const emit = defineEmits(['update:modelValue', 'change', 'create'])

const loading = ref(false)
const tagOptions = ref<IMetaTag.Row[]>([])
const frequentTags = ref<IMetaTag.Row[]>([])
const selectedTags = ref<number | number[]>(props.modelValue ?? (props.multiple ? [] : 0))
const selectedTagsData = ref<IMetaTag.Row[]>([])
const createTagDialogVisible = ref(false)
const submitting = ref(false)
const tagFormRef = ref<FormInstance>()
const loadMoreRef = ref<HTMLElement | null>(null)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const hasMoreData = ref(false)
const searchQuery = ref('')
const categories = ref<IMetaTagCategory.Row[]>([])
const selectedCategoryId = ref<number | undefined>(undefined)
const categoryCount = ref<Record<number, number>>({})

// 预定义颜色
const predefineColors = [
  '#0071e3', // Apple蓝
  '#000000', // 黑色
  '#FFFFFF', // 白色
  '#34c759', // 绿色
  '#ff3b30', // 红色
  '#ffcc00', // 黄色
  '#5856d6', // 紫色
  '#ff9500'  // 橙色
]

// 新标签表单
const newTag = ref<IMetaTag.Form>({
  name: '',
  backgroundColor: '#0071e3',
  textColor: '#FFFFFF',
  plain: 'false',
  enable: 'true',
  categoryId: undefined
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
  backgroundColor: [{ required: true, message: '请选择背景颜色', trigger: 'change' }],
  textColor: [{ required: true, message: '请选择文字颜色', trigger: 'change' }]
}

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== undefined) {
    selectedTags.value = newVal
  } else {
    // 当modelValue被设置为undefined时（例如重置操作），将选择项重置为默认值
    selectedTags.value = props.multiple ? [] : 0
  }
}, { immediate: true })

// 获取标签样式
const getTagStyle = (tag: any) => {
  if (tag.plain) {
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

// 远程搜索
const remoteSearch = async (query: string) => {
  searchQuery.value = query
  currentPage.value = 1
  loading.value = true
  try {
    const res = await getMetaTagListApi({
      name: query || undefined,
      enable: 'true',
      page: currentPage.value,
      limit: pageSize.value,
      categoryId: selectedCategoryId.value
    })
    tagOptions.value = res.data.rows || []
    total.value = res.data.total || 0
    
    // 检查是否有更多数据
    hasMoreData.value = tagOptions.value.length < total.value
    
    console.log(`加载第${currentPage.value}页, 共${total.value}条, 已加载${tagOptions.value.length}条, 是否有更多: ${hasMoreData.value}`)
  } catch (error) {
    console.error('获取标签列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载更多数据
const loadMoreData = async () => {
  if (loading.value || !hasMoreData.value) return
  
  loading.value = true
  currentPage.value++
  
  try {
    console.log(`开始加载第${currentPage.value}页数据...`)
    const res = await getMetaTagListApi({
      name: searchQuery.value || undefined,
      enable: 'true',
      page: currentPage.value,
      limit: pageSize.value,
      categoryId: selectedCategoryId.value
    })
    
    if (res.data.rows && res.data.rows.length > 0) {
      tagOptions.value = [...tagOptions.value, ...res.data.rows]
      console.log(`成功加载${res.data.rows.length}条新数据`)
    } else {
      console.log('未加载到新数据')
    }
    
    total.value = res.data.total || 0
    // 更新是否有更多数据标志
    hasMoreData.value = tagOptions.value.length < total.value
    
    console.log(`当前共加载${tagOptions.value.length}/${total.value}条数据, 是否有更多: ${hasMoreData.value}`)
  } catch (error) {
    console.error('加载更多标签失败:', error)
    currentPage.value--
  } finally {
    loading.value = false
  }
}

// 监听下拉框滚动事件
const setupScrollListener = () => {
  nextTick(() => {
    // 尝试获取下拉框的DOM元素
    const dropdown = document.querySelector('.el-select-dropdown.apple-select-dropdown')
    
    if (!dropdown) {
      console.error('找不到下拉框元素')
      return
    }
    
    // 添加滚动事件监听
    dropdown.addEventListener('scroll', (e: Event) => {
      const target = e.target as HTMLElement
      const { scrollTop, scrollHeight, clientHeight } = target
      
      // 当滚动到距离底部30px以内时加载更多
      if (scrollHeight - scrollTop - clientHeight < 30 && !loading.value && hasMoreData.value) {
        console.log('触发滚动加载')
        loadMoreData()
      }
    })
    
    console.log('已添加滚动事件监听')
  })
}

// 选择变化
const handleChange = (value: number | number[]) => {
  // 更新选中的标签数据
  updateSelectedTagsData(value)
  emit('update:modelValue', value)
  emit('change', value)
}

// 更新已选中标签的完整数据
const updateSelectedTagsData = (selectedIds: number | number[]) => {
  if (!selectedIds) {
    selectedTagsData.value = []
    return
  }
  
  const ids = Array.isArray(selectedIds) ? selectedIds : [selectedIds]
  const allTags = [...tagOptions.value, ...frequentTags.value]
  
  selectedTagsData.value = ids.map(id => {
    const tag = allTags.find(t => t.id === id)
    return tag || { id, name: `标签${id}`, backgroundColor: '#0071e3', textColor: '#FFFFFF', plain: 'false' }
  }).filter(Boolean)
}

// 处理下拉框可见性变化
const handleDropdownVisibility = (visible: boolean) => {
  if (visible) {
    // 当下拉框打开时加载数据
    remoteSearch('')
    
    // 延迟设置滚动监听，确保下拉框已渲染
    setTimeout(() => {
      setupScrollListener()
    }, 300)
  }
}

// 处理分类变更
const handleCategoryChange = (value: number | undefined) => {
  selectedCategoryId.value = value
  currentPage.value = 1
  remoteSearch(searchQuery.value)
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
    const res = await getMetaTagCategoryCountApi()
    if (res.data && res.data.length) {
      const countMap: Record<number, number> = {}
      res.data.forEach(item => {
        countMap[item.categoryId] = item.count
      })
      categoryCount.value = countMap
    }
  } catch (error) {
    console.error('获取分类标签数量失败:', error)
  }
}

// 打开创建标签对话框
const handleCreateTag = (e: Event) => {
  e.stopPropagation()
  createTagDialogVisible.value = true
}

// 根据背景颜色自动计算文字颜色
const calculateTextColor = () => {
  if (!newTag.value.backgroundColor) return

  // 计算亮度
  const hex = newTag.value.backgroundColor
  const r = parseInt(hex.slice(1, 3), 16)
  const g = parseInt(hex.slice(3, 5), 16)
  const b = parseInt(hex.slice(5, 7), 16)
  const brightness = (r * 299 + g * 587 + b * 114) / 1000

  // 根据亮度选择黑色或白色文字
  newTag.value.textColor = brightness > 128 ? '#000000' : '#FFFFFF'
}

// 提交新标签
const submitNewTag = async () => {
  if (!tagFormRef.value) return

  await tagFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      // 转换布尔类型为字符串类型 (API需要)
      const tagData = {
        ...newTag.value,
        plain: newTag.value.plain ? 'true' : 'false'
      }

      await createMetaTagApi(tagData)
      ElMessage.success('标签创建成功')
      createTagDialogVisible.value = false

      // 重新加载标签列表
      remoteSearch('')
      
      // 如果创建了新的分类标签，刷新分类数量统计
      loadCategoryCount()

      // 重置表单
      newTag.value = {
        name: '',
        backgroundColor: '#0071e3',
        textColor: '#FFFFFF',
        plain: 'false',
        enable: 'true',
        categoryId: undefined
      }

      // 发送创建完成事件
      emit('create')
    } catch (error) {
      console.error('创建标签失败:', error)
      ElMessage.error('创建标签失败')
    } finally {
      submitting.value = false
    }
  })
}

// 加载常用标签
const loadFrequentTags = async () => {
  if (!props.frequentTagIds || props.frequentTagIds.length === 0) return

  try {
    // 这里简化处理，实际应该有一个API来获取多个标签信息
    const res = await getMetaTagListApi({
      page: 1,
      limit: 100,
      enable: 'true'
    })

    if (res.data.rows?.length) {
      frequentTags.value = res.data.rows.filter(tag =>
          props.frequentTagIds.includes(Number(tag.id))
      )
    }
  } catch (error) {
    console.error('获取常用标签失败:', error)
  }
}

// 初始化加载
onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      loadCategories(),
      loadCategoryCount(),
      remoteSearch(''),
      loadFrequentTags()
    ])
  } catch (error) {
    console.error('初始化标签选择器失败:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.meta-tag-select {
  width: 100%;
  position: relative;

  .category-select {
    width: 100%;
    margin-bottom: 16px;
    
    .category-icon {
      margin-right: 8px;
      color: #8e8e93;
    }
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

  :deep(.el-select) {
    width: 100%;

    .el-input__wrapper {
      border-radius: 10px;
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
      transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
      padding: 0 16px;
      min-height: 40px;
      height: auto;

      &:hover {
        box-shadow: 0 0 0 1px rgba(0, 113, 227, 0.5);
      }
      
      &.is-focus {
        box-shadow: 0 0 0 2px rgba(0, 113, 227, 0.3), 0 0 0 1px #0071e3;
      }
      
      .el-input__inner {
        font-size: 14px;
      }
    }

    .el-select__tags {
      max-width: calc(100% - 30px);
      margin-top: 4px;
      margin-bottom: 4px;
      flex-wrap: wrap;
      gap: 4px;
    }

    .el-tag {
      margin-right: 4px;
      margin-bottom: 4px;
      border-radius: 8px;
      font-weight: 500;
      transition: all 0.2s ease;

      &.el-tag--small {
        height: 24px;
        padding: 0 10px;
        font-size: 12px;
      }
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }
    }
  }
  
  .enhanced-tag-select {
    :deep(.el-input__wrapper) {
      min-height: 40px;
      height: auto;
      padding: 8px 16px;
      
      .el-select__tags {
        margin-top: 0;
        margin-bottom: 0;
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
        align-items: center;
      }
    }
  }
  
  // 自定义标签样式 - 使用标签自身的颜色配置
  :deep(.el-tag.custom-tag) {
    margin: 0;
    border: none;
    border-radius: 12px;
    padding: 4px 12px;
    font-size: 12px;
    font-weight: 500;
    height: 24px;
    line-height: 16px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
    transition: all 0.2s cubic-bezier(0.25, 0.1, 0.25, 1.0);
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
    }
    
    .el-icon {
      margin-left: 6px;
      font-size: 12px;
      transition: all 0.2s ease;
      
      &:hover {
        transform: scale(1.2);
      }
    }
  }

  .create-tag-btn {
    width: 24px;
    height: 24px;
    font-size: 14px;
    padding: 0;
    color: #0071e3;
    margin-right: 6px;
    background-color: transparent;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      background-color: rgba(0, 113, 227, 0.1);
    }
    
    &:active {
      background-color: rgba(0, 113, 227, 0.2);
    }
  }

  .tag-option {
    display: inline-flex;
    align-items: center;
    margin-right: 8px;

    &:last-child {
      margin-right: 0;
    }
  }

  .apple-tag {
    border-radius: 4px;
    font-weight: 500;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    transition: all 0.2s ease;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }
  }

  .empty-state {
    padding: 20px 0;
    
    .empty-icon {
      font-size: 32px;
      color: #8e8e93;
      margin-bottom: 8px;
    }
  }

  .tag-preview {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 48px;
    padding: 12px;
    background: rgba(246, 246, 246, 0.6);
    border-radius: 10px;
    border: 1px solid rgba(0, 0, 0, 0.05);
  }

  .load-more-button {
    position: relative;
    margin: 20px auto 10px !important;
    width: 85% !important;
    height: 36px !important;
    border-radius: 18px !important;
    background: #f5f5f7 !important;
    overflow: hidden !important;
    cursor: pointer !important;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05) !important;
    transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1) !important;
    display: block !important;
    
    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.8), transparent);
      transform: translateX(-100%);
      transition: transform 0.8s ease;
    }
    
    .load-more-content {
      position: relative;
      z-index: 2;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      height: 100%;
      color: #0071e3;
      font-size: 14px;
      font-weight: 500;
      
      .el-icon {
        font-size: 14px;
        transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      }
      
      &.loading {
        color: #8e8e93;
        
        .el-icon {
          font-size: 16px;
          animation: rotating 1.2s linear infinite;
        }
      }
    }
    
    &:hover {
      background: #eaeaeb !important;
      transform: translateY(-1px);
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08) !important;
      
      &::before {
        transform: translateX(100%);
      }
      
      .load-more-content:not(.loading) .el-icon {
        transform: translateY(2px);
      }
    }
    
    &:active {
      transform: scale(0.97);
      background: #e1e1e3 !important;
    }
  }
}

:deep(.create-tag-dialog) {
  .el-dialog__header {
    margin: 0;
    padding: 16px 20px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    font-weight: 600;
  }

  .el-dialog__body {
    padding: 24px 20px;
  }

  .el-dialog__footer {
    padding: 12px 20px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
  }
  
  .el-form-item {
    margin-bottom: 22px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .el-form-item__label {
    font-weight: 500;
    color: #1c1c1e;
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
  
  .apple-input {
    .el-input__wrapper {
      border-radius: 8px;
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
      transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
      
      &:hover {
        box-shadow: 0 0 0 1px rgba(0, 113, 227, 0.5);
      }
      
      &.is-focus {
        box-shadow: 0 0 0 2px rgba(0, 113, 227, 0.3), 0 0 0 1px #0071e3;
      }
    }
  }
  
  .apple-switch {
    .el-switch__core {
      border-radius: 20px;
    }
  }
  
  .apple-button {
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
    
    &.cancel {
      background-color: #f5f5f5;
      border-color: #f5f5f5;
      color: #1c1c1e;
      
      &:hover {
        background-color: #e5e5ea;
        border-color: #e5e5ea;
      }
      
      &:active {
        background-color: #d1d1d6;
        border-color: #d1d1d6;
      }
    }
    
    &.create {
      background-color: #0071e3;
      border-color: #0071e3;
      
      &:hover {
        background-color: #0077ed;
        border-color: #0077ed;
        box-shadow: 0 2px 5px rgba(0, 113, 227, 0.3);
      }
      
      &:active {
        background-color: #0068d1;
        border-color: #0068d1;
        transform: scale(0.98);
      }
    }
  }
}

:deep(.apple-select-dropdown) {
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transform-origin: center top;
  border: none;
  padding: 6px 0;

  .el-select-dropdown__item {
    height: 36px;
    line-height: 36px;
    padding: 0 16px;
    font-size: 14px;

    &.hover, &:hover {
      background-color: rgba(0, 113, 227, 0.08);
    }

    &.selected {
      background-color: rgba(0, 113, 227, 0.15);
      color: #0071e3;
      font-weight: 500;
    }
  }

  .el-select-group__title {
    padding: 10px 16px;
    font-weight: 600;
    font-size: 12px;
    color: #8e8e93;
    background-color: rgba(0, 0, 0, 0.02);
    margin-top: 4px;
  }

  .el-select-group__wrap {
    margin-bottom: 8px;

    &:last-child {
      margin-bottom: 0;
    }
  }
  
  &.category-dropdown {
    .el-select-dropdown__item {
      padding: 0 12px;
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

<style lang="scss">
/* 全局样式，没有scoped，确保可以影响到下拉框 */
.custom-tag-select-dropdown {
  .load-more-button {
    position: relative;
    margin: 20px auto 10px !important;
    width: 85% !important;
    height: 36px !important;
    border-radius: 18px !important;
    background: #f5f5f7 !important;
    overflow: hidden !important;
    cursor: pointer !important;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05) !important;
    transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1) !important;
    display: block !important;
    
    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.8), transparent);
      transform: translateX(-100%);
      transition: transform 0.8s ease;
    }
    
    .load-more-content {
      position: relative;
      z-index: 2;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      height: 100%;
      color: #0071e3;
      font-size: 14px;
      font-weight: 500;
      
      .el-icon {
        font-size: 14px;
        transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      }
      
      &.loading {
        color: #8e8e93;
        
        .el-icon {
          font-size: 16px;
          animation: rotating 1.2s linear infinite;
        }
      }
    }
    
    &:hover {
      background: #eaeaeb !important;
      transform: translateY(-1px);
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08) !important;
      
      &::before {
        transform: translateX(100%);
      }
      
      .load-more-content:not(.loading) .el-icon {
        transform: translateY(2px);
      }
    }
    
    &:active {
      transform: scale(0.97);
      background: #e1e1e3 !important;
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
}
</style>
