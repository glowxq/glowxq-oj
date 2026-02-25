<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="客户端管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="clientId"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary" v-auth="'sys.client.create'" :icon="CirclePlus" @click="openAddEdit('新增客户端管理')">
          新增
        </el-button>
        <el-button
          v-auth="'sys.client.remove'"
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
          v-auth="'sys.client.update'"
          v-if="row.isLock === 'F'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑客户端管理', row, false)"
        >
          编辑
        </el-button>
        <el-button
          v-auth="'sys.client.remove'"
          v-if="row.isLock === 'F'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <SysClientForm ref="sysClientRef" />
  </div>
</template>

<script setup lang="ts">
import { CirclePlus, Delete, EditPen } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import {
  createSysClientApi,
  removeSysClientApi,
  updateSysClientApi,
  getSysClientListApi,
  getSysClientDetailApi
} from '@/api/modules/system/admin/client';
import { useHandleData } from '@/hooks/useHandleData';
import SysClientForm from '@/views/system/admin/clientManage/components/SysClientForm.vue';
import { useOptionsStore } from '@/stores/modules/options';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ISysClient } from '@/api/interface/system/admin/client';
import { ref } from 'vue';
defineOptions({
  name: 'SysClientView'
});
const optionsStore = useOptionsStore();
const proTableRef = ref<ProTableInstance>();

// 表格配置项
const columns: ColumnProps<ISysClient.Row>[] = [
  { type: 'selection', width: 80, selectable: row => row.isLock !== 'T' },
  { prop: 'clientId', label: 'ClientId' },
  { prop: 'clientKey', label: '客户端名称' },
  { prop: 'clientSecret', label: '客户端秘钥' },
  {
    prop: 'grantTypeCd',
    label: '授权类型',
    tag: true,
    enum: optionsStore.getDictOptions('grant_type'),
    fieldNames: {
      label: 'codeName',
      value: 'alias',
      tagType: 'callbackShowStyle'
    }
  },
  {
    prop: 'deviceTypeCd',
    label: '设备类型',
    tag: true,
    enum: optionsStore.getDictOptions('device_type'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'activeTimeout', label: 'token活跃超时时间' },
  { prop: 'timeout', label: 'token固定超时' },
  {
    prop: 'clientStatusCd',
    label: '状态',
    tag: true,
    enum: optionsStore.getDictOptions('sys_client_status'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'remark', label: '备注' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'clientKey', label: '客户端名称', el: 'input' },
  { prop: 'clientSecret', label: '客户端秘钥', el: 'input' },
  {
    prop: 'grantTypeCd',
    label: '授权类型',
    el: 'select',
    fieldNames: {
      label: 'codeName',
      value: 'alias',
      tagType: 'callbackShowStyle'
    }
  },
  {
    prop: 'deviceTypeCd',
    label: '设备类型',
    el: 'select',
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  {
    prop: 'activeTimeout',
    label: 'token活跃超时时间',
    el: 'date-picker',
    span: 2,
    props: {
      type: 'datetimerange',
      valueFormat: 'YYYY-MM-DD HH:mm:ss'
    }
  }
];

// 获取table列表
const getTableList = (params: ISysClient.Query) => {
  let newParams = formatParams(params);
  return getSysClientListApi(newParams);
};

const formatParams = (params: ISysClient.Query) => {
  let newParams = JSON.parse(JSON.stringify(params));
  return newParams;
};

// 打开 drawer(新增、查看、编辑)
const sysClientRef = ref<InstanceType<typeof SysClientForm>>();
const openAddEdit = async (title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getSysClientDetailApi({ id: row?.clientId });
    row = record?.data;
  } else {
    row.activeTimeout = 86400;
    row.timeout = 604800;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createSysClientApi : updateSysClientApi,
    getTableList: proTableRef.value?.getTableList,
    isAdd: isAdd
  };
  sysClientRef.value?.acceptParams(params);
};

// 删除信息
const deleteInfo = async (params: ISysClient.Row) => {
  await useHandleData(removeSysClientApi, { ids: [params.clientId] }, `删除【${params.clientId}】客户端管理`);
  proTableRef.value?.getTableList();
};

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeSysClientApi, { ids }, '删除所选客户端管理');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
};
</script>
