<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="参数列表"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary" :icon="CirclePlus" @click="openAddEdit('新增参数')"> 新增参数 </el-button>
        <el-button type="danger" :icon="Delete" plain :disabled="!scope.isSelected" @click="batchDelete(scope.selectedListIds)">
          批量删除参数
        </el-button>
      </template>

      <template #operation="{ row }">
        <el-button type="primary" link :icon="EditPen" @click="openAddEdit('编辑参数', row, false)"> 编辑 </el-button>
        <el-button v-if="row.isLock === 'F'" type="primary" link :icon="Delete" @click="deleteInfo(row)"> 删除 </el-button>
      </template>
    </ProTable>
    <ConfigEditForm ref="dictTypeRef" />
  </div>
</template>

<script setup lang="ts">
import { CirclePlus, Delete, EditPen } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import { addConfig, deleteConfig, editConfig, getConfigList } from '@/api/modules/system/admin/config';
import { useHandleData } from '@/hooks/useHandleData';
import ConfigEditForm from '@/views/system/admin/configManage/components/ConfigEditForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IConfig } from '@/api/interface/system/admin/config';
import { ref } from 'vue';

defineOptions({
  name: 'DictManage'
});

// 表格配置项
const columns: ColumnProps<IConfig.Info>[] = [
  { type: 'selection', width: 80, selectable: row => row.isLock !== 'T' },
  { prop: 'configName', label: '参数名称' },
  { prop: 'configKey', label: 'Key', tag: true, width: 200 },
  { prop: 'configValue', label: 'Value' },
  { prop: 'remark', label: '备注' },
  { prop: 'createTime', label: '创建时间' },
  { prop: 'updateTime', label: '修改时间' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];

const searchColumns: SearchProps[] = [
  { prop: 'configName', label: '参数名称', el: 'input' },
  { prop: 'configKey', label: 'Key', el: 'input' }
];

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTableRef = ref<ProTableInstance>();

// 获取table列表
const getTableList = (params: IConfig.Query) => getConfigList(params);

// 打开 drawer(新增、查看、编辑)
const dictTypeRef = ref<InstanceType<typeof ConfigEditForm>>();
const openAddEdit = (title: string, row = {}, isAdd = true) => {
  const params = {
    title,
    row: { ...row },
    api: isAdd ? addConfig : editConfig,
    getTableList: proTableRef.value?.getTableList
  };
  dictTypeRef.value?.acceptParams(params);
};

// 删除信息
const deleteInfo = async (params: IConfig.Info) => {
  await useHandleData(deleteConfig, { ids: [params.id] }, `删除【${params.configName}】参数`);
  proTableRef.value?.getTableList();
};

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(deleteConfig, { ids }, '删除所选参数');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
};
</script>
