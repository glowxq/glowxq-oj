<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="用户题目数据"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'user.problem.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增用户题目数据')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'user.problem.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'user.problem.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'user.problem.export'"
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
          v-auth="'user.problem.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑用户题目数据', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'user.problem.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <UserProblemForm ref="userProblemRef" />
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
  createUserProblemApi,
  removeUserProblemApi,
  updateUserProblemApi,
  getUserProblemListApi,
  getUserProblemDetailApi,
  importUserProblemExcelApi,
  exportUserProblemExcelApi,
} from '@/api/modules/oj/user/userProblem';
import { useHandleData } from '@/hooks/useHandleData';
import UserProblemForm from '@/views/oj/user/userProblem/components/UserProblemForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IUserProblem } from '@/api/interface/oj/user/userProblem';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'UserProblemView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IUserProblem.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'userId', label: '用户ID' },
  { prop: 'problemId', label: '题目ID' },
  { prop: 'problemKey', label: '题目Key' },
  { prop: 'problemTitle', label: '题目标题ID' },
  { prop: 'judgeId', label: '测评ID' },
  { prop: 'judgeStatus', label: '测评状态' },
  { prop: 'score', label: '作业分数' },
  { prop: 'code', label: 'AC的代码' },
  { prop: 'options', label: '非编程题作答内容' },
  { prop: 'flowImage', label: '流程图URL' },
  { prop: 'problemType', label: '题目类型' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'userId', label: '用户ID', el: 'input' },
  { prop: 'problemId', label: '题目ID', el: 'input' },
  { prop: 'problemKey', label: '题目Key', el: 'input' },
  { prop: 'problemTitle', label: '题目标题ID', el: 'input' },
  { prop: 'judgeId', label: '测评ID', el: 'input' },
  { prop: 'judgeStatus', label: '测评状态', el: 'select' },
  { prop: 'score', label: '作业分数', el: 'input' },
  { prop: 'code', label: 'AC的代码', el: 'input' },
  { prop: 'options', label: '非编程题作答内容', el: 'input' },
  { prop: 'flowImage', label: '流程图URL', el: 'input' },
  { prop: 'problemType', label: '题目类型', el: 'select' },
]
// 获取table列表
const getTableList = (params: IUserProblem.Query) => {
  let newParams = formatParams(params);
  return getUserProblemListApi(newParams);
};
const formatParams = (params: IUserProblem.Query) =>{
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
const userProblemRef = ref<InstanceType<typeof UserProblemForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getUserProblemDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createUserProblemApi : updateUserProblemApi,
    getTableList: proTableRef.value?.getTableList
  }
  userProblemRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IUserProblem.Row) => {
  await useHandleData(
    removeUserProblemApi,
    { ids: [params.id] },
    `删除【${params.id}】用户题目数据`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeUserProblemApi, { ids }, '删除所选用户题目数据')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '用户题目数据',
    templateName: '用户题目数据',
    tempApi: downloadTemplate,
    importApi: importUserProblemExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IUserProblem.Query);
  useDownload(exportUserProblemExcelApi, "用户题目数据", newParams);
};
</script>
