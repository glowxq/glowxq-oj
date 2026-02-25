<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="小程序用户"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'applet.user.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增小程序用户')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'applet.user.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'applet.user.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'applet.user.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>
      <!-- 自定义头像列 -->
      <template #avatar="scope">
        <el-image
          v-if="scope.row.avatar"
          style="width: 50px; height: 50px; border-radius: 4px"
          :src="scope.row.avatar"
          fit="cover"
          :preview-src-list="[scope.row.avatar]"
          preview-teleported
        />
        <span v-else>-</span>
      </template>
      <!-- 自定义链接图片列 -->
      <template #url="scope">
        <el-image
          v-if="scope.row.url"
          style="width: 50px; height: 50px; border-radius: 4px"
          :src="scope.row.url"
          fit="cover"
          :preview-src-list="[scope.row.url]"
          preview-teleported
        />
        <span v-else>-</span>
      </template>
      <template #operation="{ row }">
        <el-button
          v-auth="'applet.user.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑小程序用户', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'applet.user.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <AppletUserForm ref="appletUserRef" />
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
  createAppletUserApi,
  removeAppletUserApi,
  updateAppletUserApi,
  getAppletUserListApi,
  getAppletUserDetailApi,
  importAppletUserExcelApi,
  exportAppletUserExcelApi,
} from '@/api/modules/system/applet/appletUser';
import { useHandleData } from '@/hooks/useHandleData';
import AppletUserForm from '@/views/system/applet/appletUser/components/AppletUserForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IAppletUser } from '@/api/interface/system/applet/appletUser';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
defineOptions({
  name: 'AppletUserView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IAppletUser.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'sysUserId', label: '系统用户ID' },
  { prop: 'openid', label: 'openid' },
  { prop: 'code', label: '业务Code' },
  { prop: 'bindCode', label: '绑定Code' },
  { prop: 'nickname', label: '昵称' },
  { prop: 'name', label: '姓名' },
  { prop: 'phone', label: '手机号' },
  { prop: 'address', label: '地址' },
  { prop: 'avatar', label: '头像' },
  { prop: 'url', label: '链接图片' },
  {
    prop: 'subscribe',
    label: '是否订阅',
    render: (scope) => {
      const value = Number(scope.row.subscribe);
      return value === 1 ? '是' : '否';
    }
  },
  { prop: 'enable', label: '状态' },
  {
    prop: 'sex',
    label: '性别',
    render: (scope) => {
      const sex = Number(scope.row.sex);
      if (sex === 0) return '未知';
      if (sex === 1) return '男';
      if (sex === 2) return '女';
      return '未知';
    }
  },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'openid', label: '小程序用户的唯一标识', el: 'input' },
  { prop: 'code', label: '业务Code', el: 'input' },
  { prop: 'bindCode', label: '绑定Code', el: 'input' },
  { prop: 'nickname', label: '昵称', el: 'input' },
  { prop: 'name', label: '真实姓名', el: 'input' },
  { prop: 'phone', label: '手机号', el: 'input' },
  { prop: 'address', label: '地址', el: 'input' },
  { prop: 'enable', label: '状态', el: 'input' },
]
// 获取table列表
const getTableList = (params: IAppletUser.Query) => {
  let newParams = formatParams(params);
  return getAppletUserListApi(newParams);
};
const formatParams = (params: IAppletUser.Query) =>{
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
const appletUserRef = ref<InstanceType<typeof AppletUserForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getAppletUserDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createAppletUserApi : updateAppletUserApi,
    getTableList: proTableRef.value?.getTableList
  }
  appletUserRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IAppletUser.Row) => {
  await useHandleData(
    removeAppletUserApi,
    { ids: [params.id] },
    `删除【${params.id}】小程序用户`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeAppletUserApi, { ids }, '删除所选小程序用户')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '小程序用户',
    templateName: '小程序用户',
    tempApi: downloadTemplate,
    importApi: importAppletUserExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IAppletUser.Query);
  useDownload(exportAppletUserExcelApi, "小程序用户", newParams);
};
</script>
