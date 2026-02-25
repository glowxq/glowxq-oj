<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="绑定标签权限"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.tag.bind.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增绑定标签权限')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.tag.bind.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.tag.bind.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.tag.bind.export'"
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
          v-auth="'meta.tag.bind.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑绑定标签权限', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.tag.bind.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaTagBindForm ref="metaTagBindRef" />
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
  createMetaTagBindApi,
  removeMetaTagBindApi,
  updateMetaTagBindApi,
  getMetaTagBindListApi,
  getMetaTagBindDetailApi,
  importMetaTagBindExcelApi,
  exportMetaTagBindExcelApi,
} from '@/api/modules/system/meta/metaTagBind';
import { useHandleData } from '@/hooks/useHandleData';
import MetaTagBindForm from '@/views/system/meta/metaTagBind/components/MetaTagBindForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IMetaTagBind } from '@/api/interface/system/meta/metaTagBind';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'MetaTagBindView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IMetaTagBind.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'tagId', label: '标签ID' },
  { prop: 'businessId', label: '标签绑定的业务ID' },
  { prop: 'type', label: '绑定类型' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'tagId', label: '标签ID', el: 'input' },
  { prop: 'businessId', label: '标签绑定的业务ID', el: 'input' },
  { prop: 'type', label: '绑定类型', el: 'select' },
]
// 获取table列表
const getTableList = (params: IMetaTagBind.Query) => {
  let newParams = formatParams(params);
  return getMetaTagBindListApi(newParams);
};
const formatParams = (params: IMetaTagBind.Query) =>{
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
const metaTagBindRef = ref<InstanceType<typeof MetaTagBindForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaTagBindDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaTagBindApi : updateMetaTagBindApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaTagBindRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IMetaTagBind.Row) => {
  await useHandleData(
    removeMetaTagBindApi,
    { ids: [params.id] },
    `删除【${params.id}】绑定标签权限`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaTagBindApi, { ids }, '删除所选绑定标签权限')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '绑定标签权限',
    templateName: '绑定标签权限',
    tempApi: downloadTemplate,
    importApi: importMetaTagBindExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaTagBind.Query);
  useDownload(exportMetaTagBindExcelApi, "绑定标签权限", newParams);
};
</script>
