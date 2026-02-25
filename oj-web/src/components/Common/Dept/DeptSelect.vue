<template>
  <el-tree-select
    :model-value="internalValue"
    :data="deptTree"
    :multiple="multiple"
    :check-strictly="checkStrictly"
    :placeholder="placeholder"
    :clearable="clearable"
    :disabled="disabled"
    :render-after-expand="false"
    :default-expand-all="defaultExpandAll"
    :props="treeProps"
    :filterable="filterable"
    :filter-node-method="filterNode"
    @update:model-value="handleChange"
    class="dept-select"
  >
    <template #default="{ node, data }">
      <span class="custom-tree-node">
        <el-icon class="dept-icon">
          <OfficeBuilding />
        </el-icon>
        <span class="dept-name">{{ data.name }}</span>
      </span>
    </template>
  </el-tree-select>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { OfficeBuilding } from '@element-plus/icons-vue'
import { getMenuTree } from '@/api/modules/system/admin/dept'
import type { ISysDept } from '@/api/interface/system/admin/dept'

defineOptions({
  name: 'DeptSelect'
})

interface Props {
  modelValue?: number | number[] | null
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  disabled?: boolean
  checkStrictly?: boolean
  defaultExpandAll?: boolean
  filterable?: boolean
  excludeNodeId?: number
  filterSystemDepts?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  placeholder: '请选择部门',
  clearable: true,
  disabled: false,
  checkStrictly: true,
  defaultExpandAll: true,
  filterable: true,
  excludeNodeId: undefined,
  filterSystemDepts: true
})

const emit = defineEmits<{
  'update:modelValue': [value: number | number[] | null]
  'change': [value: number | number[] | null, data?: ISysDept.Tree | ISysDept.Tree[]]
}>()

const deptTree = ref<ISysDept.Tree[]>([])
const loading = ref(false)

// 树形组件属性配置
const treeProps = {
  label: 'name',
  value: 'id',
  children: 'children'
}

// 内部值管理
const internalValue = computed({
  get() {
    if (props.multiple) {
      const value = props.modelValue as number[]
      if (!Array.isArray(value)) return []
      
      // 过滤系统部门ID
      if (props.filterSystemDepts) {
        return value.filter(id => id !== 0 && id !== -1 && id !== -2)
      }
      return value
    } else {
      const value = props.modelValue as number
      // 过滤系统部门ID
      if (props.filterSystemDepts && (value === 0 || value === -1 || value === -2)) {
        return null
      }
      return value
    }
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

// 处理值变化
const handleChange = (value: number | number[]) => {
  let finalValue: number | number[] | null = value
  
  if (props.multiple) {
    finalValue = Array.isArray(value) ? value : []
  } else {
    finalValue = typeof value === 'number' ? value : null
  }
  
  emit('update:modelValue', finalValue)
  
  // 查找对应的数据对象
  const findNodeData = (tree: ISysDept.Tree[], targetId: number): ISysDept.Tree | null => {
    for (const node of tree) {
      if (node.id === targetId) return node
      if (node.children) {
        const found = findNodeData(node.children, targetId)
        if (found) return found
      }
    }
    return null
  }
  
  let selectedData: ISysDept.Tree | ISysDept.Tree[] | undefined
  
  if (props.multiple && Array.isArray(finalValue)) {
    selectedData = finalValue.map(id => findNodeData(deptTree.value, id)).filter(Boolean) as ISysDept.Tree[]
  } else if (typeof finalValue === 'number') {
    selectedData = findNodeData(deptTree.value, finalValue) || undefined
  }
  
  emit('change', finalValue, selectedData)
}

// 节点过滤方法
const filterNode = (value: string, data: ISysDept.Tree) => {
  if (!value) return true
  return data.name.includes(value)
}

// 加载部门树数据
const loadDeptTree = async () => {
  if (loading.value) return
  
  loading.value = true
  try {
    const params: any = {}
    if (props.excludeNodeId !== undefined) {
      params.excludeNodeId = props.excludeNodeId
    }
    
    const response = await getMenuTree(params)
    deptTree.value = response.data || []
  } catch (error) {
    console.error('加载部门树失败:', error)
    deptTree.value = []
  } finally {
    loading.value = false
  }
}

// 监听excludeNodeId变化，重新加载数据
watch(() => props.excludeNodeId, () => {
  loadDeptTree()
}, { immediate: false })

// 组件挂载时加载数据
onMounted(() => {
  loadDeptTree()
})

// 暴露方法
defineExpose({
  loadDeptTree,
  deptTree: computed(() => deptTree.value)
})
</script>

<style scoped lang="scss">
.dept-select {
  width: 100%;
  
  :deep(.el-tree-select__wrapper) {
    max-height: 300px;
  }
  
  :deep(.el-tree) {
    .el-tree-node__content {
      height: 32px;
      
      &:hover {
        background-color: var(--el-color-primary-light-9);
      }
    }
    
    .el-tree-node.is-current > .el-tree-node__content {
      background-color: var(--el-color-primary-light-8);
      color: var(--el-color-primary);
    }
  }
}

.custom-tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
  font-size: 14px;
  
  .dept-icon {
    color: var(--el-color-primary);
    font-size: 16px;
    flex-shrink: 0;
  }
  
  .dept-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

// 多选模式下的标签样式
:deep(.el-tree-select__tags) {
  .el-tag {
    background-color: var(--el-color-primary-light-9);
    border-color: var(--el-color-primary-light-7);
    color: var(--el-color-primary);
    
    .el-tag__close {
      color: var(--el-color-primary);
      
      &:hover {
        background-color: var(--el-color-primary);
        color: #fff;
      }
    }
  }
}

// 禁用状态样式
:deep(.is-disabled) {
  .custom-tree-node {
    color: var(--el-text-color-disabled);
    
    .dept-icon {
      color: var(--el-text-color-disabled);
    }
  }
}
</style>
