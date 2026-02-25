<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="主题数据"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'topic.info.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增主题数据')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'topic.info.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'topic.info.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'topic.info.export'"
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
          v-auth="'topic.info.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑主题数据', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'topic.info.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <TopicInfoForm ref="topicInfoRef" />
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
  createTopicInfoApi,
  removeTopicInfoApi,
  updateTopicInfoApi,
  getTopicInfoListApi,
  getTopicInfoDetailApi,
  importTopicInfoExcelApi,
  exportTopicInfoExcelApi,
} from '@/api/modules/oj/topic/topicInfo';
import { useHandleData } from '@/hooks/useHandleData';
import TopicInfoForm from '@/views/oj/topic/topicInfo/components/TopicInfoForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ITopicInfo } from '@/api/interface/oj/topic/topicInfo';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'TopicInfoView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ITopicInfo.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'topicId', label: '主题ID' },
  { prop: 'userId', label: '用户ID' },
  { prop: 'avatar', label: '头像' },
  { prop: 'name', label: '姓名' },
  { prop: 'nickName', label: '昵称' },
  { prop: 'score', label: '总得分' },
  { prop: 'acNum', label: '总AC数量' },
  { prop: 'submitNum', label: '提交次数' },
  { prop: 'useTime', label: '总做题用时(分)' },
  { prop: 'punishmentTime', label: '总罚时(分)' },
  { prop: 'status', label: '开始做题' },
  { prop: 'startTime', label: '开始时间' },
  { prop: 'endTime', label: '完成时间' },
  { prop: 'autoSubmit', label: '自动提交' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'topicId', label: '主题ID', el: 'input' },
  { prop: 'userId', label: '用户ID', el: 'input' },
  { prop: 'avatar', label: '头像', el: 'input' },
  { prop: 'name', label: '姓名', el: 'input' },
  { prop: 'nickName', label: '昵称', el: 'input' },
  { prop: 'score', label: '总得分', el: 'input' },
  { prop: 'acNum', label: '总AC数量', el: 'input' },
  { prop: 'submitNum', label: '提交次数', el: 'input' },
  { prop: 'useTime',
    label: '总做题用时(分)',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  { prop: 'punishmentTime',
    label: '总罚时(分)',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  { prop: 'status', label: '开始做题', el: 'select' },
  { prop: 'startTime',
    label: '开始时间',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  { prop: 'endTime',
    label: '完成时间',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  { prop: 'autoSubmit', label: '自动提交', el: 'input' },
]
// 获取table列表
const getTableList = (params: ITopicInfo.Query) => {
  let newParams = formatParams(params);
  return getTopicInfoListApi(newParams);
};
const formatParams = (params: ITopicInfo.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.startTime && (newParams.startTimeStart = newParams.startTime[0]);
  newParams.startTime && (newParams.startTimeEnd = newParams.startTime[1]);
  delete newParams.startTime;
  newParams.endTime && (newParams.endTimeStart = newParams.endTime[0]);
  newParams.endTime && (newParams.endTimeEnd = newParams.endTime[1]);
  delete newParams.endTime;
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}
// 打开 drawer(新增、查看、编辑)
const topicInfoRef = ref<InstanceType<typeof TopicInfoForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getTopicInfoDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createTopicInfoApi : updateTopicInfoApi,
    getTableList: proTableRef.value?.getTableList
  }
  topicInfoRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ITopicInfo.Row) => {
  await useHandleData(
    removeTopicInfoApi,
    { ids: [params.id] },
    `删除【${params.id}】主题数据`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeTopicInfoApi, { ids }, '删除所选主题数据')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '主题数据',
    templateName: '主题数据',
    tempApi: downloadTemplate,
    importApi: importTopicInfoExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ITopicInfo.Query);
  useDownload(exportTopicInfoExcelApi, "主题数据", newParams);
};
</script>
