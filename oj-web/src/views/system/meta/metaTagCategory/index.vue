<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="标签分类"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.tag.category.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增标签分类')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.tag.category.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.tag.category.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.tag.category.export'"
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
          v-auth="'meta.tag.category.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑标签分类', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.tag.category.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaTagCategoryForm ref="metaTagCategoryRef" />
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
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createMetaTagCategoryApi,
  removeMetaTagCategoryApi,
  updateMetaTagCategoryApi,
  getMetaTagCategoryListApi,
  getMetaTagCategoryDetailApi,
  importMetaTagCategoryExcelApi,
  exportMetaTagCategoryExcelApi,
} from '@/api/modules/system/meta/metaTagCategory';
import { useHandleData } from '@/hooks/useHandleData';
import MetaTagCategoryForm from '@/views/system/meta/metaTagCategory/components/MetaTagCategoryForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IMetaTagCategory } from '@/api/interface/system/meta/metaTagCategory';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'MetaTagCategoryView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IMetaTagCategory.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: '分类名称' },
  { prop: 'enable', label: '启用' },
  { prop: 'remark', label: '备注' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '分类名称', el: 'input' },
  { prop: 'enable', label: '启用', el: 'input' },
]
// 获取table列表
const getTableList = (params: IMetaTagCategory.Query) => {
  let newParams = formatParams(params);
  return getMetaTagCategoryListApi(newParams);
};
const formatParams = (params: IMetaTagCategory.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}
// 打开 drawer(新增、查看、编辑)
const metaTagCategoryRef = ref<InstanceType<typeof MetaTagCategoryForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaTagCategoryDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaTagCategoryApi : updateMetaTagCategoryApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaTagCategoryRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IMetaTagCategory.Row) => {
  await useHandleData(
    removeMetaTagCategoryApi,
    { ids: [params.id] },
    `删除【${params.id}】标签分类`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaTagCategoryApi, { ids }, '删除所选标签分类')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '标签分类',
    templateName: '标签分类',
    tempApi: downloadTemplate,
    importApi: importMetaTagCategoryExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaTagCategory.Query);
  useDownload(exportMetaTagCategoryExcelApi, "标签分类", newParams);
};
</script>