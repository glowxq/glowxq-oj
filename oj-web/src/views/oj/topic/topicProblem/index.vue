<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="主题题目"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'topic.problem.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增主题题目')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'topic.problem.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'topic.problem.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'topic.problem.export'"
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
          v-auth="'topic.problem.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑主题题目', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'topic.problem.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <TopicProblemForm ref="topicProblemRef" />
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
  createTopicProblemApi,
  removeTopicProblemApi,
  updateTopicProblemApi,
  getTopicProblemListApi,
  getTopicProblemDetailApi,
  importTopicProblemExcelApi,
  exportTopicProblemExcelApi,
} from '@/api/modules/oj/topic/topicProblem';
import { useHandleData } from '@/hooks/useHandleData';
import TopicProblemForm from '@/views/oj/topic/topicProblem/components/TopicProblemForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ITopicProblem } from '@/api/interface/oj/topic/topicProblem';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'TopicProblemView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ITopicProblem.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'topicId', label: '主题ID' },
  { prop: 'problemId', label: '题目ID' },
  { prop: 'required', label: '必填' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'topicId', label: '主题ID', el: 'input' },
  { prop: 'problemId', label: '题目ID', el: 'input' },
  { prop: 'required', label: '必填', el: 'input' },
]
// 获取table列表
const getTableList = (params: ITopicProblem.Query) => {
  let newParams = formatParams(params);
  return getTopicProblemListApi(newParams);
};
const formatParams = (params: ITopicProblem.Query) =>{
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
const topicProblemRef = ref<InstanceType<typeof TopicProblemForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getTopicProblemDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createTopicProblemApi : updateTopicProblemApi,
    getTableList: proTableRef.value?.getTableList
  }
  topicProblemRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ITopicProblem.Row) => {
  await useHandleData(
    removeTopicProblemApi,
    { ids: [params.id] },
    `删除【${params.id}】主题题目`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeTopicProblemApi, { ids }, '删除所选主题题目')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '主题题目',
    templateName: '主题题目',
    tempApi: downloadTemplate,
    importApi: importTopicProblemExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ITopicProblem.Query);
  useDownload(exportTopicProblemExcelApi, "主题题目", newParams);
};
</script>
