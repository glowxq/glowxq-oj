<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="操作日志"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'sys.operation.log.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增操作日志')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'sys.operation.log.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'sys.operation.log.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'sys.operation.log.export'"
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
          v-auth="'sys.operation.log.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑操作日志', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'sys.operation.log.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <SysOperationLogForm ref="sysOperationLogRef" />
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
  createSysOperationLogApi,
  removeSysOperationLogApi,
  updateSysOperationLogApi,
  getSysOperationLogListApi,
  getSysOperationLogDetailApi,
  importSysOperationLogExcelApi,
  exportSysOperationLogExcelApi,
} from '@/api/modules/system/admin/sysOperationLog';
import { useHandleData } from '@/hooks/useHandleData';
import SysOperationLogForm from '@/views/system/admin/sysOperationLog/components/SysOperationLogForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ISysOperationLog } from '@/api/interface/system/admin/sysOperationLog';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'SysOperationLogView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ISysOperationLog.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'userId', label: '用户id' },
  { prop: 'traceId', label: '链路追踪ID' },
  { prop: 'spanId', label: '业务模块跟踪ID' },
  { prop: 'username', label: '用户名称' },
  { prop: 'ip', label: 'ip' },
  { prop: 'method', label: '方法' },
  { prop: 'uri', label: '接口' },
  { prop: 'header', label: '请求头' },
  { prop: 'module', label: '模块名称' },
  { prop: 'description', label: '操作描述' },
  { prop: 'param', label: '参数' },
  { prop: 'request', label: '请求参数json' },
  { prop: 'response', label: '响应参数json' },
  { prop: 'error', label: '存在错误' },
  { prop: 'businessType', label: '操作类型' },
  { prop: 'errorMessage', label: '错误信息' },
  { prop: 'costTime', label: '操作耗时（ms）' },
  { prop: 'tenantId', label: '租户id' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'userId', label: '用户id', el: 'input' },
  { prop: 'traceId', label: '链路追踪ID', el: 'input' },
  { prop: 'spanId', label: '业务模块跟踪ID', el: 'input' },
  { prop: 'username', label: '用户名称', el: 'input' },
  { prop: 'ip', label: 'ip', el: 'input' },
  { prop: 'method', label: '方法', el: 'input' },
  { prop: 'uri', label: '接口', el: 'input' },
  { prop: 'header', label: '请求头', el: 'input' },
  { prop: 'module', label: '模块名称', el: 'input' },
  { prop: 'description', label: '操作描述', el: 'input' },
  { prop: 'param', label: '参数', el: 'input' },
  { prop: 'request', label: '请求参数json', el: 'input' },
  { prop: 'response', label: '响应参数json', el: 'input' },
  { prop: 'error', label: '存在错误', el: 'input' },
  { prop: 'businessType', label: '操作类型', el: 'select' },
  { prop: 'errorMessage', label: '错误信息', el: 'input' },
  { prop: 'costTime',
    label: '操作耗时（ms）',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  { prop: 'tenantId', label: '租户id', el: 'input' },
]
// 获取table列表
const getTableList = (params: ISysOperationLog.Query) => {
  let newParams = formatParams(params);
  return getSysOperationLogListApi(newParams);
};
const formatParams = (params: ISysOperationLog.Query) =>{
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
const sysOperationLogRef = ref<InstanceType<typeof SysOperationLogForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getSysOperationLogDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createSysOperationLogApi : updateSysOperationLogApi,
    getTableList: proTableRef.value?.getTableList
  }
  sysOperationLogRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ISysOperationLog.Row) => {
  await useHandleData(
    removeSysOperationLogApi,
    { ids: [params.id] },
    `删除【${params.id}】操作日志`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeSysOperationLogApi, { ids }, '删除所选操作日志')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '操作日志',
    templateName: '操作日志',
    tempApi: downloadTemplate,
    importApi: importSysOperationLogExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ISysOperationLog.Query);
  useDownload(exportSysOperationLogExcelApi, "操作日志", newParams);
};
</script>