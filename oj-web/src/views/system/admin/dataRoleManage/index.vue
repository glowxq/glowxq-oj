<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="数据权限管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary" v-auth="'sys.data.role.create'" :icon="CirclePlus" @click="openAddEdit('新增数据权限角色')">
          新增
        </el-button>
        <el-button
          v-auth="'sys.data.role.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
      </template>
      <template #operation="{ row }">
        <el-button
          v-auth="'sys.data.role.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑数据权限角色', row, false)"
        >
          编辑
        </el-button>
        <el-button
          v-auth="'sys.data.role.remove'"
          v-if="row.isLock !== 'T'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <SysDataRoleForm ref="sysDataRoleRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { CirclePlus, Delete, EditPen } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import {
  createSysDataRoleApi,
  removeSysDataRoleApi,
  updateSysDataRoleApi,
  getSysDataRoleListApi,
  getSysDataRoleDetailApi,
  getSysDataRoleMenuApi
} from '@/api/modules/system/admin/datarole';
import { useHandleData } from '@/hooks/useHandleData';
import SysDataRoleForm from '@/views/system/admin/dataRoleManage/components/DataRoleForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ISysDataRole } from '@/api/interface/system/admin/datarole';
defineOptions({
  name: 'SysDataRoleView'
});
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ISysDataRole.Row>[] = [
  { type: 'selection', width: 80, selectable: row => row.isLock !== 'T' },
  { prop: 'id', label: '编号', width: 150 },
  { prop: 'roleName', label: '角色名称' },
  { prop: 'createTime', label: '创建时间' },
  { prop: 'updateTime', label: '修改时间' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];
// 搜索条件项
const searchColumns: SearchProps[] = [{ prop: 'roleName', label: '角色名称', el: 'input' }];
// 获取table列表
const getTableList = (params: ISysDataRole.Query) => {
  let newParams = formatParams(params);
  return getSysDataRoleListApi(newParams);
};
const formatParams = (params: ISysDataRole.Query) => {
  let newParams = JSON.parse(JSON.stringify(params));
  return newParams;
};
// 打开 drawer(新增、查看、编辑)
const sysDataRoleRef = ref<InstanceType<typeof SysDataRoleForm>>();
const openAddEdit = async (title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getSysDataRoleDetailApi({ id: row?.id });
    row = record?.data;
  }
  const menuRecord = await getSysDataRoleMenuApi();
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createSysDataRoleApi : updateSysDataRoleApi,
    getTableList: proTableRef.value?.getTableList,
    meta: menuRecord.data
  };
  sysDataRoleRef.value?.acceptParams(params);
};
// 删除信息
const deleteInfo = async (params: ISysDataRole.Row) => {
  await useHandleData(removeSysDataRoleApi, { ids: [params.id] }, `删除【${params.id}】数据权限`);
  proTableRef.value?.getTableList();
};
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeSysDataRoleApi, { ids }, '删除所选数据权限');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
};
</script>
