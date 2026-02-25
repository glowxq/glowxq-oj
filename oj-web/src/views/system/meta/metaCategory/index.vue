<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="分类"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      :row-key="'id'"
      :pagination="false"
      :border="false"
      :default-expand-all="defaultExpandAllKey"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.category.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增分类')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.category.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.category.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.category.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
        <el-button type="info" :icon="Sort" @click="changeExpand"> 展开/折叠 </el-button>
      </template>
      <template #operation="{ row }">
        <el-button
          v-auth="'meta.category.create'"
          type="primary"
          link
          :icon="CirclePlus"
          @click="openAddEdit('新增分类', row)"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.category.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑分类', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.category.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaCategoryForm ref="metaCategoryRef" />
    <ImportExcel ref="ImportExcelRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  Sort
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createMetaCategoryApi,
  removeMetaCategoryApi,
  updateMetaCategoryApi,
  getMetaCategoryListApi,
  getMetaCategoryTreeApi,
  getMetaCategoryDetailApi,
  importMetaCategoryExcelApi,
  exportMetaCategoryExcelApi,
} from '@/api/modules/system/meta/metaCategory';
import { useHandleData } from '@/hooks/useHandleData';
import MetaCategoryForm from '@/views/system/meta/metaCategory/components/MetaCategoryForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IMetaCategory } from '@/api/interface/system/meta/metaCategory';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'MetaCategoryView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IMetaCategory.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: '分类名称', align: 'left' },
  { prop: 'sort', label: '排序', width: 60, align: 'left' },
  { prop: 'enable', label: '启用' },
  { prop: 'lock', label: '是否锁定' },
  { prop: 'remark', label: '备注' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '分类名称', el: 'input' },
  { prop: 'enable', label: '启用', el: 'select', enum: [
    { label: '是', value: true },
    { label: '否', value: false }
  ] },
  { prop: 'lock', label: '是否锁定', el: 'select', enum: [
    { label: '是', value: true },
    { label: '否', value: false }
  ] },
]

const defaultExpandAllKey = ref(true);

// 获取table列表
const getTableList = (params: IMetaCategory.Query) => {
  let newParams = formatParams(params);
  // 使用树形接口替代列表接口，并传递搜索参数
  return getMetaCategoryTreeApi({ ...newParams, appendRoot: false });
};
const formatParams = (params: IMetaCategory.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}

// 展开/折叠切换
const changeExpand = () => {
  defaultExpandAllKey.value = !defaultExpandAllKey.value;
  proTableRef.value?.refresh();
};

// sort的预计算
const presort = (row: any = {}, pid: number) => {
  let cnt;
  // 如果选择的是根节点
  if (pid == 0) {
    cnt = proTableRef.value?.tableData?.length || 0; // 根据分类列表的长度计算sort
  } else {
    cnt = row?.children?.length || 0; // 根据选中行的children长度计算sort
  }
  return (cnt + 1) * 100;
};

// 打开 drawer(新增、查看、编辑)
const metaCategoryRef = ref<InstanceType<typeof MetaCategoryForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  let formData = {};
  if (!isAdd) {
    const record = await getMetaCategoryDetailApi({ id: row?.id })
    formData = record?.data
  } else {
    let pid = row.id || 0;
    const sort = presort(row, pid);
    formData = {
      pid: pid,
      sort: sort
    };
  }
  const params = {
    title,
    row: { ...formData },
    api: isAdd ? createMetaCategoryApi : updateMetaCategoryApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaCategoryRef.value?.acceptParams(params)
}

// 删除信息
const deleteInfo = async (params: IMetaCategory.Row) => {
  await useHandleData(
    removeMetaCategoryApi,
    { ids: [params.id] },
    `删除【${params.name || params.id}】分类及其所有子分类（此操作不可逆请谨慎操作！）`,
    'error'
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaCategoryApi, { ids }, '删除所选分类及其所有子分类（此操作不可逆请谨慎操作！）', 'error')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '分类',
    templateName: '分类',
    tempApi: downloadTemplate,
    importApi: importMetaCategoryExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaCategory.Query);
  useDownload(exportMetaCategoryExcelApi, "分类", newParams);
};
</script>
