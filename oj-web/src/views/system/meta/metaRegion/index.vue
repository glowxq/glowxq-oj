<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="区域地址"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      :pagination="false"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :load="loadChildren"
      lazy
      :expand-row-keys="expandedKeys"
      @expand-change="handleExpandChange"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.region.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增区域地址')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.region.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.region.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.region.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
        <el-button
          type="info"
          :icon="Refresh"
          plain
          @click="handleRefresh"
        >
          刷新
        </el-button>
        <el-button
          type="warning"
          plain
          @click="toggleExpandAll"
        >
          {{ isAllExpanded ? '全部折叠' : '全部展开' }}
        </el-button>
      </template>
      <!-- 地址名列添加层级标识 -->
      <template #name="{ row }">
        <span :style="{ paddingLeft: (row.level || 0) * 10 + 'px' }">
          {{ row.name }}
        </span>
        <el-tag v-if="row.level === 1" type="success" size="small" class="ml-2">省</el-tag>
        <el-tag v-else-if="row.level === 2" type="warning" size="small" class="ml-2">市</el-tag>
        <el-tag v-else-if="row.level === 3" type="info" size="small" class="ml-2">区</el-tag>
      </template>
      <template #operation="{ row }">
        <el-button
          v-auth="'meta.region.create'"
          type="primary"
          link
          :icon="CirclePlus"
          @click="openAddEdit('新增子区域', { parentId: row.id })"
        >
          新增子级
        </el-button>
        <el-button
          v-auth="'meta.region.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑区域地址', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.region.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaRegionForm ref="metaRegionRef" />
    <ImportExcel ref="ImportExcelRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  Refresh,
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createMetaRegionApi,
  removeMetaRegionApi,
  updateMetaRegionApi,
  getMetaRegionListAllApi,
  getMetaRegionDetailApi,
  importMetaRegionExcelApi,
  exportMetaRegionExcelApi,
} from '@/api/modules/system/meta/metaRegion';
import { useHandleData } from '@/hooks/useHandleData';
import MetaRegionForm from '@/views/system/meta/metaRegion/components/MetaRegionForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IMetaRegion } from '@/api/interface/system/meta/metaRegion';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { useDownload } from "@/hooks/useDownload";

defineOptions({
  name: 'MetaRegionView'
})

const proTableRef = ref<ProTableInstance>();

// 表格配置项
const columns: ColumnProps<IMetaRegion.TreeNode>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: '地址名', width: 400 ,align: 'left'},
  { prop: 'pinyin', label: '地址拼音', width: 150 },
  { prop: 'level', label: '地址等级', width: 100 },
  { prop: 'enable', label: '启用', width: 80 },
  { prop: 'operation', label: '操作', fixed: 'right' }
]

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '地址名', el: 'input' },
  { prop: 'pinyin', label: '地址拼音', el: 'input' },
  { prop: 'level', label: '地址等级', el: 'input' },
  { 
    prop: 'enable', 
    label: '启用', 
    el: 'select',
    fieldNames: { label: 'label', value: 'value' },
    enum: [
      { label: '启用', value: true },
      { label: '禁用', value: false }
    ]
  }
]

// 性能优化相关变量
const allRegionData = ref<IMetaRegion.Row[]>([]) // 缓存所有原始数据
const regionMap = ref<Map<number, IMetaRegion.Row>>(new Map()) // ID映射表，快速查找
const childrenMap = ref<Map<number, IMetaRegion.Row[]>>(new Map()) // 父子关系映射
const expandedKeys = ref<number[]>([]) // 展开的节点keys
const isAllExpanded = ref(false)
const loading = ref(false)

// 构建数据映射关系（性能优化：避免每次都遍历整个数组）
const buildDataMaps = (data: IMetaRegion.Row[]) => {
  const idMap = new Map<number, IMetaRegion.Row>()
  const childMap = new Map<number, IMetaRegion.Row[]>()
  
  // 建立ID映射
  data.forEach(item => {
    idMap.set(item.id, item)
  })
  
  // 建立父子关系映射
  data.forEach(item => {
    const parentId = item.parentId || 0
    if (!childMap.has(parentId)) {
      childMap.set(parentId, [])
    }
    childMap.get(parentId)!.push(item)
  })
  
  regionMap.value = idMap
  childrenMap.value = childMap
}

// 获取根节点（性能优化：只返回第一级节点）
const getRootNodes = (): IMetaRegion.TreeNode[] => {
  const rootChildren = childrenMap.value.get(0) || []
  return rootChildren.map(item => ({
    ...item,
    hasChildren: childrenMap.value.has(item.id),
    expanded: false,
    _originalData: item
  }))
}

// 懒加载子节点
const loadChildren = async (row: IMetaRegion.TreeNode, treeNode: any, resolve: Function) => {
  try {
    // 确保数据已加载
    if (allRegionData.value.length === 0) {
      await fetchAllRegionData()
    }
    
    // 从缓存中获取子节点
    const children = childrenMap.value.get(row.id) || []
    const childNodes: IMetaRegion.TreeNode[] = children.map(item => ({
      ...item,
      hasChildren: childrenMap.value.has(item.id),
      expanded: false,
      _originalData: item
    }))
    
    // 模拟异步加载（避免阻塞UI）
    await nextTick()
    resolve(childNodes)
  } catch (error) {
    console.error('加载子节点失败:', error)
    resolve([])
  }
}

// 处理节点展开/折叠
const handleExpandChange = (row: IMetaRegion.TreeNode, expanded: boolean) => {
  if (expanded) {
    if (!expandedKeys.value.includes(row.id)) {
      expandedKeys.value.push(row.id)
    }
  } else {
    const index = expandedKeys.value.indexOf(row.id)
    if (index > -1) {
      expandedKeys.value.splice(index, 1)
    }
  }
}

// 全部展开/折叠切换
const toggleExpandAll = async () => {
  if (isAllExpanded.value) {
    // 折叠全部
    expandedKeys.value = []
    isAllExpanded.value = false
  } else {
         // 展开全部（分批展开，避免卡顿）
     const allIds: number[] = []
     for (const [parentId] of childrenMap.value.entries()) {
       if (parentId !== 0) { // 排除根节点的key
         allIds.push(parentId)
       }
     }
    
    // 分批展开，避免一次性展开太多节点
    const batchSize = 50
    for (let i = 0; i < allIds.length; i += batchSize) {
      const batch = allIds.slice(i, i + batchSize)
      expandedKeys.value.push(...batch)
      await nextTick() // 让UI有时间渲染
    }
    isAllExpanded.value = true
  }
}

// 刷新数据
const handleRefresh = async () => {
  expandedKeys.value = []
  isAllExpanded.value = false
  await fetchAllRegionData()
  proTableRef.value?.getTableList()
}

// 获取全部区域数据并建立映射关系
const fetchAllRegionData = async () => {
  if (loading.value) return
  
  try {
    loading.value = true
    const response = await getMetaRegionListAllApi()
    allRegionData.value = response.data || []
    buildDataMaps(allRegionData.value)
  } catch (error) {
    console.error('获取区域数据失败:', error)
    allRegionData.value = []
  } finally {
    loading.value = false
  }
}

// 搜索过滤（性能优化：在内存中进行搜索）
const filterData = (searchParams: Partial<IMetaRegion.Query>): IMetaRegion.TreeNode[] => {
  if (!searchParams || Object.keys(searchParams).length === 0) {
    return getRootNodes()
  }
  
  const filtered = allRegionData.value.filter(item => {
    return Object.entries(searchParams).every(([key, value]) => {
      if (!value) return true
      const itemValue = item[key as keyof IMetaRegion.Row]
      if (typeof value === 'string') {
        return String(itemValue || '').toLowerCase().includes(value.toLowerCase())
      }
      return itemValue === value
    })
  })
  
  // 搜索结果转换为扁平的树节点（搜索时不需要层级结构）
  return filtered.map(item => ({
    ...item,
    hasChildren: false, // 搜索结果不显示子节点
    expanded: false,
    _originalData: item
  }))
}

// 获取table列表（性能优化版本）
const getTableList = async (params?: Partial<IMetaRegion.Query>) => {
  try {
    // 如果没有缓存数据，先获取全部数据
    if (allRegionData.value.length === 0) {
      await fetchAllRegionData()
    }
    
    // 等待数据加载完成
    if (loading.value) {
      await new Promise(resolve => {
        const checkLoading = () => {
          if (!loading.value) {
            resolve(true)
          } else {
            setTimeout(checkLoading, 100)
          }
        }
        checkLoading()
      })
    }
    
    let resultData: IMetaRegion.TreeNode[]
    
    // 检查是否有搜索条件
    const hasSearchParams = params && Object.values(params).some(v => v !== undefined && v !== null && v !== '')
    
    if (hasSearchParams) {
      // 有搜索条件时，返回过滤后的扁平数据
      resultData = filterData(params)
    } else {
      // 没有搜索条件时，返回树形根节点
      resultData = getRootNodes()
    }
    
    console.log('返回数据:', resultData.length, '条根节点')
    
    return {
      data: resultData,
      total: resultData.length
    }
  } catch (error) {
    console.error('获取区域数据失败:', error)
    return {
      data: [],
      total: 0
    }
  }
}

// 打开 drawer(新增、查看、编辑)
const metaRegionRef = ref<InstanceType<typeof MetaRegionForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaRegionDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaRegionApi : updateMetaRegionApi,
    getTableList: async () => {
      // 刷新缓存数据
      await handleRefresh()
      return proTableRef.value?.getTableList()
    }
  }
  metaRegionRef.value?.acceptParams(params)
}

// 删除信息
const deleteInfo = async (params: IMetaRegion.TreeNode) => {
  await useHandleData(
    removeMetaRegionApi,
    { ids: [params.id] },
    `删除【${params.name}】区域地址`
  )
  handleRefresh() // 删除后刷新数据
}

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaRegionApi, { ids }, '删除所选区域地址')
  proTableRef.value?.clearSelection()
  handleRefresh() // 删除后刷新数据
}

// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '区域地址',
    templateName: '区域地址',
    tempApi: downloadTemplate,
    importApi: importMetaRegionExcelApi,
    getTableList: () => {
      handleRefresh() // 导入后刷新数据
    }
  }
  ImportExcelRef.value?.acceptParams(params)
}

// 导出
const downloadFile = async () => {
  const params = proTableRef.value?.searchParam as IMetaRegion.Query || {}
  useDownload(exportMetaRegionExcelApi, "区域地址", params);
};

// 初始化时获取数据
fetchAllRegionData()
</script>

<style scoped lang="scss">
.ml-2 {
  margin-left: 8px;
}

// 性能优化：减少不必要的CSS计算
:deep(.el-table__body-wrapper) {
  // 启用硬件加速
  transform: translateZ(0);
  // 优化滚动性能
  -webkit-overflow-scrolling: touch;
}

// 优化大数据量时的滚动条样式
:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background-color: #d9d9d9;
  border-radius: 4px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background-color: #f5f5f5;
}
</style>