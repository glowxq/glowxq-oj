<template>
  <div class="meta-category-select">
    <el-tree-select
      v-model="selectedCategories"
      :data="categoryTree"
      :multiple="multiple"
      :show-checkbox="multiple"
      :collapse-tags="multiple && collapseTags"
      :collapse-tags-tooltip="multiple && collapseTagsTooltip"
      :check-strictly="checkStrictly"
      filterable
      :placeholder="placeholder"
      :clearable="clearable"
      :render-after-expand="false"
      :default-expand-all="defaultExpandAll"
      :props="treeProps"
      class="apple-tree-select"
      v-loading="loading"
      @visible-change="handleDropdownVisibility"
    >
      <template #empty>
        <div class="empty-state">
          <el-empty description="暂无分类数据" :image-size="60">
            <template #image>
              <i class="el-icon-folder"></i>
            </template>
          </el-empty>
        </div>
      </template>
    </el-tree-select>
    
    <div v-if="debug" class="debug-info">
      <p>当前加载状态: {{ loading ? '加载中' : '加载完成' }}</p>
      <p>数据条数: {{ categoryTree.length }}</p>
      <p>选中值: {{ selectedCategories }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { getMetaCategoryTreeApi } from '@/api/modules/system/meta/metaCategory'
import type { IMetaCategory } from '@/api/interface/system/meta/metaCategory'
import { ElMessage } from 'element-plus'

// 组件选项
defineOptions({
  name: 'CategorySelect'
})

// 定义属性
const props = withDefaults(defineProps<{
  modelValue?: number | number[]
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  excludeNodeId?: number
  appendRoot?: boolean
  collapseTags?: boolean
  collapseTagsTooltip?: boolean
  defaultExpandAll?: boolean
  checkStrictly?: boolean
  debug?: boolean
}>(), {
  modelValue: () => [],
  multiple: false,
  placeholder: '请选择分类',
  clearable: true,
  excludeNodeId: undefined,
  appendRoot: true,
  collapseTags: true,
  collapseTagsTooltip: true,
  defaultExpandAll: true,
  checkStrictly: false,
  debug: false
})

// 定义事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: number | number[]): void
  (e: 'change', value: IMetaCategory.Tree | IMetaCategory.Tree[]): void
}>()

// 组件状态
const loading = ref(false)
const categoryTree = ref<IMetaCategory.Tree[]>([])

// 树形配置
const treeProps = {
  value: 'id',
  label: 'name',
  children: 'children'
}

// 计算选中值
const selectedCategories = computed({
  get: () => props.modelValue,
  set: (val) => {
    emit('update:modelValue', val)
    
    // 发出change事件，找出选中的分类对象
    let selectedItems: any;
    if (props.multiple && Array.isArray(val)) {
      // 多选模式 - 查找所有选中的分类对象
      selectedItems = val.map(id => findCategoryById(categoryTree.value, Number(id))).filter(Boolean)
    } else if (val !== undefined && val !== null) {
      // 单选模式 - 查找选中的分类对象
      selectedItems = findCategoryById(categoryTree.value, Number(val as number))
    }
    
    if (props.debug) {
      console.log('CategorySelect - 选中值变化:', val)
      console.log('CategorySelect - 选中项目:', selectedItems)
    }
    
    emit('change', selectedItems)
  }
})

// 递归查找分类对象
const findCategoryById = (tree: IMetaCategory.Tree[], id: number): IMetaCategory.Tree | undefined => {
  for (const node of tree) {
    if (node.id === id) {
      return node
    }
    if (node.children && node.children.length > 0) {
      const found = findCategoryById(node.children, id)
      if (found) return found
    }
  }
  return undefined
}

// 加载分类树数据
const loadCategoryTree = async () => {
  loading.value = true
  
  if (props.debug) {
    console.log('CategorySelect - 开始加载分类树')
  }
  
  try {
    const params = {
      excludeNodeId: props.excludeNodeId,
      appendRoot: props.appendRoot
    }
    
    if (props.debug) {
      console.log('CategorySelect - 请求参数:', params)
    }
    
    const res = await getMetaCategoryTreeApi(params)
    
    if (props.debug) {
      console.log('CategorySelect - API响应:', res)
    }
    
    if (typeof res.data === 'object' && res.data !== null) {
      if (Array.isArray(res.data)) {
        categoryTree.value = res.data
        
        if (props.debug) {
          console.log('CategorySelect - 加载成功, 数据条数:', res.data.length)
        }
        
        if (res.data.length === 0) {
          ElMessage.warning('暂无分类数据')
        }
      } else if (Object.keys(res.data).length === 0) {
        categoryTree.value = []
        
        if (props.debug) {
          console.log('CategorySelect - 返回空对象')
        }
        
        ElMessage.warning('暂无分类数据')
      } else {
        categoryTree.value = []
        
        if (props.debug) {
          console.log('CategorySelect - 数据格式不正确:', res.data)
        }
        
        ElMessage.error('分类数据格式错误')
      }
    } else {
      categoryTree.value = []
      
      if (props.debug) {
        console.log('CategorySelect - 无数据返回')
      }
      
      ElMessage.warning('暂无分类数据')
    }
  } catch (error) {
    console.error('加载分类树失败:', error)
    categoryTree.value = []
    ElMessage.error('加载分类数据失败')
  } finally {
    loading.value = false
    
    if (props.debug) {
      console.log('CategorySelect - 加载完成, 数据:', categoryTree.value)
    }
  }
}

// 下拉框可见性变化事件
const handleDropdownVisibility = (visible: boolean) => {
  if (visible) {
    // 当下拉框显示时，重新加载分类树
    loadCategoryTree()
  }
}

// 监听excludeNodeId变化
watch(() => props.excludeNodeId, () => {
  loadCategoryTree()
})

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  if (props.debug) {
    console.log('CategorySelect - modelValue变化:', newVal)
  }
}, { immediate: true })

// 组件挂载时加载数据
onMounted(() => {
  if (props.debug) {
    console.log('CategorySelect - 组件挂载')
  }
  loadCategoryTree()
})

// 暴露组件方法
defineExpose({
  loadCategoryTree,
  categoryTree
})
</script>

<style scoped lang="scss">
.meta-category-select {
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;

  :deep(.el-tree-select) {
    width: 100%;
  }

  :deep(.el-select-dropdown__item) {
    padding: 0;
  }

  :deep(.el-tree-node__content) {
    height: 36px;
    padding: 0 10px;
    border-radius: 6px;
    transition: all 0.2s ease;
    
    &:hover {
      background-color: rgba(0, 113, 227, 0.08);
    }
    
    .el-tree-node__label {
      font-size: 14px;
    }
  }

  :deep(.is-current) .el-tree-node__content {
    background-color: rgba(0, 113, 227, 0.15);
    color: #0071e3;
    font-weight: 500;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
    transition: all 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px #0071e3;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px #0071e3, 0 0 0 4px rgba(0, 113, 227, 0.1);
    }
  }

  :deep(.el-select__tags) {
    max-width: calc(100% - 30px);
    margin-top: 2px;
  }

  :deep(.el-select__tags-text) {
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  :deep(.el-tag) {
    background-color: rgba(0, 113, 227, 0.1);
    border-color: rgba(0, 113, 227, 0.2);
    color: #0071e3;
    border-radius: 4px;
    
    .el-tag__close {
      color: #0071e3;
      
      &:hover {
        background-color: #0071e3;
        color: white;
      }
    }
  }

  :deep(.el-tree-select__dropdown) {
    border-radius: 10px;
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transform-origin: center top;
    border: none;
    padding: 8px;
    
    .el-scrollbar__view {
      padding: 4px;
    }
    
    .el-tree {
      background: transparent;
    }
    
    .el-tree-node__expand-icon {
      font-size: 16px;
      color: #0071e3;
    }
  }
  
  .empty-state {
    padding: 20px 0;
    text-align: center;
  }
  
  .debug-info {
    margin-top: 8px;
    padding: 8px;
    background-color: #f5f7fa;
    border-radius: 4px;
    font-size: 12px;
    color: #606266;
    border: 1px dashed #dcdfe6;
  }
}
</style>
