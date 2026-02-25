<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="题目黑名单"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'problem.black.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增题目黑名单')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'problem.black.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'problem.black.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'problem.black.export'"
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
          v-auth="'problem.black.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑题目黑名单', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'problem.black.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <ProblemBlackForm ref="problemBlackRef" />
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
  createProblemBlackApi,
  removeProblemBlackApi,
  updateProblemBlackApi,
  getProblemBlackListApi,
  getProblemBlackDetailApi,
  importProblemBlackExcelApi,
  exportProblemBlackExcelApi,
} from '@/api/modules/oj/problem/problemBlack';
import { useHandleData } from '@/hooks/useHandleData';
import ProblemBlackForm from '@/views/oj/problem/problemBlack/components/ProblemBlackForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IProblemBlack } from '@/api/interface/oj/problem/problemBlack';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'ProblemBlackView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IProblemBlack.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'problemId', label: '题目ID' },
  { prop: 'businessId', label: '拉黑对象ID' },
  { prop: 'businessName', label: '拉黑对象名' },
  { prop: 'type', label: '拉黑对象类型' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'problemId', label: '题目ID', el: 'input' },
  { prop: 'businessId', label: '拉黑对象ID', el: 'input' },
  { prop: 'businessName', label: '拉黑对象名', el: 'input' },
  { prop: 'type', label: '拉黑对象类型', el: 'select' },
]
// 获取table列表
const getTableList = (params: IProblemBlack.Query) => {
  let newParams = formatParams(params);
  return getProblemBlackListApi(newParams);
};
const formatParams = (params: IProblemBlack.Query) =>{
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
const problemBlackRef = ref<InstanceType<typeof ProblemBlackForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getProblemBlackDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createProblemBlackApi : updateProblemBlackApi,
    getTableList: proTableRef.value?.getTableList
  }
  problemBlackRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IProblemBlack.Row) => {
  await useHandleData(
    removeProblemBlackApi,
    { ids: [params.id] },
    `删除【${params.id}】题目黑名单`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeProblemBlackApi, { ids }, '删除所选题目黑名单')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '题目黑名单',
    templateName: '题目黑名单',
    tempApi: downloadTemplate,
    importApi: importProblemBlackExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IProblemBlack.Query);
  useDownload(exportProblemBlackExcelApi, "题目黑名单", newParams);
};
</script>
