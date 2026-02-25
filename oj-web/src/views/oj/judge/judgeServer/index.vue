<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="测评服务"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'judge.server.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增测评服务')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'judge.server.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'judge.server.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'judge.server.export'"
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
          v-auth="'judge.server.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑测评服务', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'judge.server.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <JudgeServerForm ref="judgeServerRef" />
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
  createJudgeServerApi,
  removeJudgeServerApi,
  updateJudgeServerApi,
  getJudgeServerListApi,
  getJudgeServerDetailApi,
  importJudgeServerExcelApi,
  exportJudgeServerExcelApi,
} from '@/api/modules/oj/judge/judgeServer';
import { useHandleData } from '@/hooks/useHandleData';
import JudgeServerForm from '@/views/oj/judge/judgeServer/components/JudgeServerForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IJudgeServer } from '@/api/interface/oj/judge/judgeServer';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'JudgeServerView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IJudgeServer.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: '判题服务名字' },
  { prop: 'ip', label: '判题机ip' },
  { prop: 'port', label: '判题机端口号' },
  { prop: 'url', label: 'ip:port' },
  { prop: 'cpuCore', label: '判题机所在服务器cpu核心数' },
  { prop: 'freeMemory', label: '判题机所在服务器空闲内存' },
  { prop: 'taskNumber', label: '当前判题数' },
  { prop: 'maxTaskNumber', label: '判题并发最大数' },
  { prop: 'enable', label: '0禁用|1启用' },
  { prop: 'remoteEnable', label: '是否开启远程判题vj' },
  { prop: 'cfSubmittableEnable', label: '是否可提交CF' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '判题服务名字', el: 'input' },
  { prop: 'ip', label: '判题机ip', el: 'input' },
  { prop: 'port', label: '判题机端口号', el: 'input' },
  { prop: 'url', label: 'ip:port', el: 'input' },
  { prop: 'cpuCore', label: '判题机所在服务器cpu核心数', el: 'input' },
  { prop: 'freeMemory', label: '判题机所在服务器空闲内存', el: 'input' },
  { prop: 'taskNumber', label: '当前判题数', el: 'input' },
  { prop: 'maxTaskNumber', label: '判题并发最大数', el: 'input' },
  { prop: 'enable', label: '0禁用|1启用', el: 'input' },
  { prop: 'remoteEnable', label: '是否开启远程判题vj', el: 'input' },
  { prop: 'cfSubmittableEnable', label: '是否可提交CF', el: 'input' },
]
// 获取table列表
const getTableList = (params: IJudgeServer.Query) => {
  let newParams = formatParams(params);
  return getJudgeServerListApi(newParams);
};
const formatParams = (params: IJudgeServer.Query) =>{
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
const judgeServerRef = ref<InstanceType<typeof JudgeServerForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getJudgeServerDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createJudgeServerApi : updateJudgeServerApi,
    getTableList: proTableRef.value?.getTableList
  }
  judgeServerRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IJudgeServer.Row) => {
  await useHandleData(
    removeJudgeServerApi,
    { ids: [params.id] },
    `删除【${params.id}】测评服务`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeJudgeServerApi, { ids }, '删除所选测评服务')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '测评服务',
    templateName: '测评服务',
    tempApi: downloadTemplate,
    importApi: importJudgeServerExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IJudgeServer.Query);
  useDownload(exportJudgeServerExcelApi, "测评服务", newParams);
};
</script>
