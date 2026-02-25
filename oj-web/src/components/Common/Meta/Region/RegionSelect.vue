<template>
  <el-tree-select
    v-model="selectedValue"
    :data="regionOptions"
    :props="treeProps"
    check-strictly
    :render-after-expand="false"
    placeholder="请选择区域"
    filterable
    clearable
    :style="{ width: width }"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { getMetaRegionListAllApi } from '@/api/modules/system/meta/metaRegion'
import type { IMetaRegion } from '@/api/interface/system/meta/metaRegion'

interface Props {
  modelValue?: string | number | null
  width?: string
  placeholder?: string
  disabled?: boolean
  level?: number // 限制选择的层级
  excludeIds?: (string | number)[] // 排除的节点ID
}

interface Emits {
  (e: 'update:modelValue', value: string | number | null): void
  (e: 'change', value: string | number | null, node?: any): void
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  placeholder: '请选择区域',
  disabled: false
})

const emit = defineEmits<Emits>()

const selectedValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value || null)
})

const regionList = ref<IMetaRegion.Row[]>([])
const loading = ref(false)

const treeProps = {
  value: 'id',
  label: 'name',
  children: 'children'
}

// 将扁平数据转换为树形结构
const buildTree = (data: IMetaRegion.Row[]) => {
  const map = new Map<string | number, any>()
  const roots: any[] = []
  
  // 先创建所有节点的映射
  data.forEach(item => {
    // 确保必要字段存在
    if (!item.id || item.name === undefined) {
      return
    }
    
    // 过滤掉被排除的节点
    if (props.excludeIds?.includes(item.id)) {
      return
    }
    
    // 如果限制了层级，过滤超过层级的节点
    if (props.level && item.level !== undefined && item.level > props.level) {
      return
    }
    
    map.set(item.id, {
      id: item.id,
      name: item.name,
      level: item.level || 0,
      parentId: item.parentId || 0,
      children: []
    })
  })
  
  // 构建树形结构
  data.forEach(item => {
    // 确保必要字段存在
    if (!item.id || item.name === undefined) {
      return
    }
    
    if (props.excludeIds?.includes(item.id)) {
      return
    }
    
    if (props.level && item.level !== undefined && item.level > props.level) {
      return
    }
    
    const node = map.get(item.id)
    if (!node) return
    
    const parentId = item.parentId || 0
    if (parentId === 0 || !map.has(parentId)) {
      // 根节点
      roots.push(node)
    } else {
      // 子节点
      const parent = map.get(parentId)
      if (parent) {
        parent.children = parent.children || []
        parent.children.push(node)
      }
    }
  })
  
  return roots
}

const regionOptions = computed(() => {
  return buildTree(regionList.value)
})

// 获取区域数据
const fetchRegionData = async () => {
  try {
    loading.value = true
    const response = await getMetaRegionListAllApi()
    regionList.value = response.data || []
  } catch (error) {
    console.error('获取区域数据失败:', error)
    regionList.value = []
  } finally {
    loading.value = false
  }
}

const handleChange = (value: string | number | null) => {
  emit('change', value)
}

// 刷新数据
const refresh = () => {
  fetchRegionData()
}

onMounted(() => {
  fetchRegionData()
})

// 监听excludeIds变化，重新构建树
watch(() => props.excludeIds, () => {
  // 树会自动重新计算
}, { deep: true })

defineExpose({
  refresh
})
</script>

<style scoped>
:deep(.el-tree-select) {
  width: 100%;
}
</style>
