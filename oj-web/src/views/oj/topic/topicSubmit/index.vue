<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="主题测评记录"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'topic.submit.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增主题测评记录')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'topic.submit.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'topic.submit.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'topic.submit.export'"
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
          v-auth="'topic.submit.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑主题测评记录', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'topic.submit.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <TopicSubmitForm ref="topicSubmitRef" />
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
  createTopicSubmitApi,
  removeTopicSubmitApi,
  updateTopicSubmitApi,
  getTopicSubmitListApi,
  getTopicSubmitDetailApi,
  importTopicSubmitExcelApi,
  exportTopicSubmitExcelApi,
} from '@/api/modules/oj/topic/topicSubmit';
import { useHandleData } from '@/hooks/useHandleData';
import TopicSubmitForm from '@/views/oj/topic/topicSubmit/components/TopicSubmitForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ITopicSubmit } from '@/api/interface/oj/topic/topicSubmit';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'TopicSubmitView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ITopicSubmit.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'topicId', label: '主题ID' },
  { prop: 'problemId', label: '题目ID' },
  { prop: 'problemKey', label: '题目Key' },
  { prop: 'problemTitle', label: '题目标题ID' },
  { prop: 'problemType', label: '题目类型' },
  { prop: 'topicJudgeType', label: '主题测评类型 ACM/OI' },
  { prop: 'userId', label: '用户ID' },
  { prop: 'name', label: '姓名' },
  { prop: 'nickName', label: '昵称' },
  { prop: 'judgeStatus', label: '测评状态' },
  { prop: 'score', label: '主题分数' },
  { prop: 'useTime', label: '做题用时(分)' },
  { prop: 'punishmentTime', label: '罚时(分)' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'topicId', label: '主题ID', el: 'input' },
  { prop: 'problemId', label: '题目ID', el: 'input' },
  { prop: 'problemKey', label: '题目Key', el: 'input' },
  { prop: 'problemTitle', label: '题目标题ID', el: 'input' },
  { prop: 'problemType', label: '题目类型', el: 'select' },
  { prop: 'topicJudgeType', label: '主题测评类型 ACM/OI', el: 'select' },
  { prop: 'userId', label: '用户ID', el: 'input' },
  { prop: 'name', label: '姓名', el: 'input' },
  { prop: 'nickName', label: '昵称', el: 'input' },
  { prop: 'judgeStatus', label: '测评状态', el: 'select' },
  { prop: 'score', label: '主题分数', el: 'input' },
  { prop: 'useTime',
    label: '做题用时(分)',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  { prop: 'punishmentTime',
    label: '罚时(分)',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
]
// 获取table列表
const getTableList = (params: ITopicSubmit.Query) => {
  let newParams = formatParams(params);
  return getTopicSubmitListApi(newParams);
};
const formatParams = (params: ITopicSubmit.Query) =>{
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
const topicSubmitRef = ref<InstanceType<typeof TopicSubmitForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getTopicSubmitDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createTopicSubmitApi : updateTopicSubmitApi,
    getTableList: proTableRef.value?.getTableList
  }
  topicSubmitRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ITopicSubmit.Row) => {
  await useHandleData(
    removeTopicSubmitApi,
    { ids: [params.id] },
    `删除【${params.id}】主题测评记录`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeTopicSubmitApi, { ids }, '删除所选主题测评记录')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '主题测评记录',
    templateName: '主题测评记录',
    tempApi: downloadTemplate,
    importApi: importTopicSubmitExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ITopicSubmit.Query);
  useDownload(exportTopicSubmitExcelApi, "主题测评记录", newParams);
};
</script>
