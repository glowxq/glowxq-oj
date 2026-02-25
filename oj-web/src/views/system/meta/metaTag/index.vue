<template>
  <div class="main-box">
    <!-- 左侧分类树 -->
    <div class="category-tree-container">
      <div class="category-header">
        <h3>标签分类</h3>
        <el-button 
          class="reset-button" 
          type="primary" 
          text 
          size="small"
          @click="resetCategoryFilter"
        >
          <el-icon><RefreshRight /></el-icon>
          重置
        </el-button>
      </div>
      <div class="category-list">
        <div 
          v-for="item in categoryOptions" 
          :key="item.id" 
          class="category-item"
          :class="{ active: selectedCategoryId === Number(item.id) }"
          @click="handleCategorySelect(Number(item.id || 0))"
        >
          <div class="item-content">
            <el-icon><Folder /></el-icon>
            <span>{{ item.name }}</span>
          </div>
          <span class="item-count" v-if="item.id !== undefined && categoryCount[item.id]">{{ categoryCount[item.id] }}</span>
        </div>
      </div>
    </div>

    <div class="table-box">
      <ProTable
        ref="proTableRef"
        title="标签"
        :indent="20"
        :columns="columns"
        :search-columns="searchColumns"
        :request-api="getTableList"
        :init-param="initParam"
        row-key="id"
      >
        <!-- 表格 header 按钮 -->
        <template #tableHeader="scope">
          <el-button type="primary"
            v-auth="'meta.tag.create'"
            :icon="CirclePlus"
            @click="openAddEdit('新增标签')"
            class="primary-button"
          >
            新增
          </el-button>
          <el-button
            v-auth="'meta.tag.remove'"
            type="danger"
            :icon="Delete"
            plain
            :disabled="!scope.isSelected"
            @click="batchDelete(scope.selectedListIds)"
          >
            批量删除
          </el-button>
          <el-button
            v-auth="'meta.tag.import'"
            type="primary"
            :icon="Upload"
            plain
            @click="importData"
          >
            导入
          </el-button>
          <el-button
            v-auth="'meta.tag.export'"
            type="primary"
            :icon="Download"
            plain
            @click="downloadFile"
          >
            导出
          </el-button>
        </template>

        <template #operation="{ row }">
          <el-button
            v-auth="'meta.tag.update'"
            type="primary"
            link
            :icon="EditPen"
            @click="openAddEdit('编辑标签', row, false)"
          >
            编辑
          </el-button>
          <el-button
            v-auth="'meta.tag.remove'"
            type="primary"
            link
            :icon="Delete"
            @click="deleteInfo(row)"
          >
            删除
          </el-button>
        </template>
      </ProTable>
      <MetaTagForm ref="metaTagRef" />
      <ImportExcel ref="ImportExcelRef" />
    </div>
  </div>
</template>

<script setup lang="tsx">
import { ref, computed, onMounted, reactive, watch } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  Folder,
  FolderOpened,
  RefreshRight
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createMetaTagApi,
  removeMetaTagApi,
  updateMetaTagApi,
  getMetaTagListApi,
  getMetaTagDetailApi,
  importMetaTagExcelApi,
  exportMetaTagExcelApi,
  getMetaTagCategoryCountApi
} from '@/api/modules/system/meta/metaTag';
import { getMetaTagCategoryListApi } from '@/api/modules/system/meta/metaTagCategory';
import type { IMetaTagCategory } from '@/api/interface/system/meta/metaTagCategory';
import type { IMetaTag } from '@/api/interface/system/meta/metaTag';
import { useHandleData } from '@/hooks/useHandleData';
import MetaTagForm from '@/views/system/meta/metaTag/components/MetaTagForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps, RenderScope } from '@/components/Common/ProTable/interface';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessage } from "element-plus";
import { useDownload } from "@/hooks/useDownload";

defineOptions({
  name: 'MetaTagView'
})

const proTableRef = ref<ProTableInstance>();

// 初始化参数，用于分类过滤
const initParam = reactive<{ categoryId?: number }>({});
// 当前选中的分类ID
const selectedCategoryId = ref<number>(-1);

// 重置分类筛选
const resetCategoryFilter = () => {
  selectedCategoryId.value = -1;
  delete initParam.categoryId;
  
  // 重置表格的搜索参数
  if (proTableRef.value?.searchParam) {
    proTableRef.value.searchParam.categoryId = undefined;
  }
  
  // 强制刷新表格
  proTableRef.value?.reset();
  ElMessage.success('已重置分类筛选');
}

// 处理分类选择
const handleCategorySelect = (categoryId: number) => {
  selectedCategoryId.value = categoryId;
  
  // 如果是全部分类，清除categoryId参数
  if (categoryId === -1) {
    delete initParam.categoryId;
    
    // 同时也要清除表格的搜索参数中的categoryId
    if (proTableRef.value?.searchParam) {
      proTableRef.value.searchParam.categoryId = undefined;
    }
  } else {
    // 设置分类ID参数
    initParam.categoryId = categoryId;
    
    // 同时也要设置表格的搜索参数中的categoryId
    if (proTableRef.value?.searchParam) {
      proTableRef.value.searchParam.categoryId = categoryId;
    }
  }
  
  // 刷新表格
  proTableRef.value?.getTableList();
};

// 获取分类选项
const categoryOptions = ref<IMetaTagCategory.Row[]>([])

// 分类项数量统计
const categoryCount = ref<Record<string | number, number>>({})
const totalCount = ref<number>(0)

// 获取标签分类列表
const getCategoryOptions = async () => {
  try {
    const res = await getMetaTagCategoryListApi({ enable: "true", page: 1, limit: 100 })
    if (res?.data) {
      categoryOptions.value = res.data.rows || []
      // 获取分类统计数据
      getCategoryCount()
      
      // 再次确保更新分类下拉框
      const searchCol = searchColumns.find(col => col.prop === 'categoryId');
      if (searchCol && categoryOptions.value.length > 0) {
        searchCol.props = {
          options: [
            { label: '全部', value: '' },
            ...categoryOptions.value.map(item => ({ label: item.name, value: item.id }))
          ]
        };
      }
    }
  } catch (error) {
    console.error('获取标签分类失败', error)
  }
}

// 获取各分类的标签数量
const getCategoryCount = async () => {
  try {
    // 使用后端提供的分类统计接口
    const res = await getMetaTagCategoryCountApi();
    if (res?.data) {
      const countMap: Record<string | number, number> = {};
      let total = 0;
      
      // 处理接口返回的分类统计数据
      res.data.forEach(item => {
        if (item.categoryId !== undefined) {
          countMap[item.categoryId] = item.count;
          total += item.count;
        }
      });
      
      categoryCount.value = countMap;
      totalCount.value = total;
    }
  } catch (error) {
    console.error('获取标签统计失败', error);
  }
}

onMounted(() => {
  // 获取分类选项
  getCategoryOptions()
  
  // 确保初始化时选中的是全部分类
  selectedCategoryId.value = -1;
  delete initParam.categoryId;
  
  // 延迟一下，确保表格初始化完成
  setTimeout(() => {
    if (proTableRef.value?.searchParam) {
      // 确保表格的搜索参数中的categoryId也是清空的
      proTableRef.value.searchParam.categoryId = undefined;
    }
  }, 100);
})

// 获取标签样式
const getTagStyle = (row: IMetaTag.Row) => {
  if (row.plain) {
    return {
      color: row.backgroundColor || '#009688',
      borderColor: row.backgroundColor || '#009688',
      backgroundColor: 'transparent',
      border: `1px solid ${row.backgroundColor || '#009688'}`
    }
  } else {
    return {
      color: row.textColor || '#FFFFFF',
      backgroundColor: row.backgroundColor || '#009688',
      border: `1px solid ${row.backgroundColor || '#009688'}`
    }
  }
}

// 表格配置项
const columns: ColumnProps<IMetaTag.Row>[] = [
  { type: 'selection', width: 60 },
  {
    prop: 'name',
    label: '标签名字',
    minWidth: 120,
    render: (scope: RenderScope<IMetaTag.Row>) => {
      return (
        <div class="tag-display" style={getTagStyle(scope.row)}>
          {scope.row.name}
        </div>
      )
    }
  },
  {
    prop: 'categoryId',
    label: '所属分类',
    width: 120,
    render: (scope: RenderScope<IMetaTag.Row>) => {
      const category = categoryOptions.value.find(item => Number(item.id) === Number(scope.row.categoryId))
      return <span>{category ? category.name : '-'}</span>
    }
  },
  {
    prop: 'backgroundColor',
    label: '背景色',
    width: 120,
    render: (scope: RenderScope<IMetaTag.Row>) => {
      return (
        <div class="color-display">
          <div class="color-block" style={{ backgroundColor: scope.row.backgroundColor }}></div>
          <span>{scope.row.backgroundColor}</span>
        </div>
      )
    }
  },
  {
    prop: 'textColor',
    label: '字体颜色',
    width: 120,
    render: (scope: RenderScope<IMetaTag.Row>) => {
      return (
        <div class="color-display">
          <div class="color-block" style={{ backgroundColor: scope.row.textColor }}></div>
          <span>{scope.row.textColor}</span>
        </div>
      )
    }
  },
  {
    prop: 'plain',
    label: '镂空样式',
    width: 100,
    render: (scope: RenderScope<IMetaTag.Row>) => {
      return (
        <el-tag type="info" size="small" effect={scope.row.plain ? 'plain' : 'dark'}>
          {scope.row.plain ? '是' : '否'}
        </el-tag>
      )
    }
  },
  {
    prop: 'enable',
    label: '启用状态',
    width: 100,
    render: (scope: RenderScope<IMetaTag.Row>) => {
      return (
        <el-tag type={scope.row.enable ? 'success' : 'danger'} size="small">
          {scope.row.enable ? '已启用' : '已禁用'}
        </el-tag>
      )
    }
  },
  { prop: 'operation', label: '操作', width: 150, fixed: 'right' }
]

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '标签名字', el: 'input' },

  { prop: 'enable', label: '启用状态', el: 'select', props: { options: [
    { label: '全部', value: '' },
    { label: '已启用', value: true },
    { label: '已禁用', value: false }
  ] } },
  { prop: 'plain', label: '镂空样式', el: 'select', props: { options: [
    { label: '全部', value: '' },
    { label: '是', value: true },
    { label: '否', value: false }
  ] } }
]

// 监听分类数据变化，更新搜索条件下拉框
watch(() => categoryOptions.value, (newVal) => {
  if (newVal.length > 0) {
    // 找到分类ID对应的搜索列
    const searchCol = searchColumns.find(col => col.prop === 'categoryId');
    if (searchCol) {
      // 更新下拉选项
      searchCol.props = {
        options: [
          { label: '全部', value: '' },
          ...newVal.map(item => ({ label: item.name, value: item.id }))
        ]
      };
      
      // 强制刷新搜索表单
      if (proTableRef.value) {
        proTableRef.value.searchParam = { ...proTableRef.value.searchParam };
      }
    }
  }
}, { deep: true, immediate: true });

// 获取table列表
const getTableList = (params: IMetaTag.Query) => {
  let newParams = formatParams(params);
  return getMetaTagListApi(newParams);
};

// 格式化请求参数
const formatParams = (params: IMetaTag.Query) => {
  let newParams = JSON.parse(JSON.stringify(params));
  
  // 处理时间范围
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  
  // 处理分类ID
  if (newParams.categoryId === '' || newParams.categoryId === undefined) {
    delete newParams.categoryId;
  } else if (newParams.categoryId) {
    newParams.categoryId = Number(newParams.categoryId);
  }
  
  // 如果左侧选择了特定分类，则优先使用左侧选择的分类ID
  if (selectedCategoryId.value !== -1) {
    newParams.categoryId = selectedCategoryId.value;
  }
  
  return newParams;
}

// 打开 drawer(新增、查看、编辑)
const metaTagRef = ref<InstanceType<typeof MetaTagForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaTagDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaTagApi : updateMetaTagApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaTagRef.value?.acceptParams(params)
}

// 删除信息
const deleteInfo = async (params: IMetaTag.Row) => {
  await useHandleData(
    removeMetaTagApi,
    { ids: [params.id] },
    `删除【${params.name || params.id}】标签`
  )
  // 刷新表格
  proTableRef.value?.getTableList()
  // 更新分类统计
  getCategoryCount()
}

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaTagApi, { ids }, '删除所选标签')
  proTableRef.value?.clearSelection()
  // 刷新表格
  proTableRef.value?.getTableList()
  // 更新分类统计
  getCategoryCount()
}

// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '标签',
    templateName: '标签',
    tempApi: downloadTemplate,
    importApi: importMetaTagExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}

// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaTag.Query);
  useDownload(exportMetaTagExcelApi, "标签", newParams);
};

// 监听表格数据变化，更新分类统计
watch(() => proTableRef.value?.tableData, () => {
  // 当表格数据发生变化时，更新分类统计
  getCategoryCount();
}, { deep: true });

// 额外监听分页变化，确保在分页切换后也更新统计
watch(() => proTableRef.value?.pageable, () => {
  // 分页变化时也更新统计
  getCategoryCount();
}, { deep: true });
</script>

<style scoped lang="scss">
.main-box {
  display: flex;
  height: calc(100vh - 100px);
  padding: 20px;
  gap: 24px;
  background-color: #f5f5f7;
}

.category-tree-container {
  width: 250px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  flex-shrink: 0;
  
  .category-header {
    padding: 20px;
    border-bottom: 1px solid #e5e5e5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h3 {
      margin: 0;
      font-size: 17px;
      font-weight: 600;
      color: #1d1d1f;
      letter-spacing: -0.022em;
    }
    
    .reset-button {
      display: flex;
      align-items: center;
      gap: 4px;
      color: var(--el-color-primary);
      font-size: 13px;
      padding: 4px 8px;
      border-radius: 6px;
      transition: all 0.2s;
      
      .el-icon {
        font-size: 14px;
      }
      
      &:hover {
        background-color: rgba(var(--el-color-primary-rgb), 0.08);
      }
      
      &:active {
        transform: scale(0.96);
      }
    }
  }
  
  .category-list {
    padding: 8px 0;
    
    .category-item {
      padding: 12px 20px;
      cursor: pointer;
      transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: #1d1d1f;
      font-size: 14px;
      margin: 0 8px;
      border-radius: 10px;
      
      .item-content {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .el-icon {
          font-size: 16px;
          color: var(--el-color-primary);
        }
      }
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.03);
      }
      
      &.active {
        background-color: rgba(var(--el-color-primary-rgb), 0.08);
        color: var(--el-color-primary);
        font-weight: 500;
        
        .item-count {
          background-color: var(--el-color-primary);
          color: white;
        }
      }
      
      .item-count {
        background-color: #e4e4e4;
        color: #666;
        border-radius: 12px;
        padding: 2px 8px;
        font-size: 12px;
        min-width: 24px;
        text-align: center;
        transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
        font-weight: normal;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      }
    }
  }
}

.table-box {
  flex: 1;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  padding: 24px;
  overflow: hidden;
}

.primary-button {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  border-radius: 8px;
  padding: 9px 16px;
  font-weight: 500;
  letter-spacing: -0.01em;
  color: white;
  transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
  box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.25);

  &:hover {
    background-color: var(--el-color-primary-light-3);
    border-color: var(--el-color-primary-light-3);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.35);
  }
  
  // 按钮活跃/按下效果
  &:active {
    background-color: var(--el-color-primary-dark-2);
    border-color: var(--el-color-primary-dark-2);
    transform: translateY(0);
    box-shadow: 0 2px 4px rgba(var(--el-color-primary-rgb), 0.2);
  }
}

.tag-display {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.5;
  white-space: nowrap;
}

.color-display {
  display: flex;
  align-items: center;
  gap: 8px;

  .color-block {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
  }
}

// 表格部分优化
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
  
  .el-table__header th {
    background-color: #f8f8fb;
    font-weight: 500;
    color: #333;
    height: 50px;
  }
  
  .el-table__row td {
    padding: 14px 0;
  }
  
  .el-table--border, .el-table--group {
    border-color: #eaeaea;
  }
}

:deep(.el-button--primary) {
  &.is-link {
    color: var(--el-color-primary);
    font-weight: 500;
    
    &:hover {
      color: var(--el-color-primary-light-3);
    }
  }
}

:deep(.el-pagination) {
  margin-top: 24px;
  justify-content: center;
  
  .el-pagination__jump {
    margin-left: 20px;
  }
  
  .btn-prev, .btn-next, .el-pager li {
    border-radius: 8px;
    transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
  }
  
  .el-pager li.active {
    background-color: var(--el-color-primary);
    color: white;
    font-weight: 600;
  }
}
</style>
