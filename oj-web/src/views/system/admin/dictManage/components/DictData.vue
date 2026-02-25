<template>
  <div class="dialog-table-box">
    <el-dialog
      v-model="visible"
      :title="`字典: ${info?.typeName}(${info?.typeCode})`"
      width="90%"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      append-to-body
    >
      <ProTable
        ref="proTableRef"
        title="字典列表"
        :indent="20"
        :columns="columns"
        :search-columns="searchColumns"
        :request-api="getTableList"
        :init-param="initParam"
      >
        <!-- 表格 header 按钮 -->
        <template #tableHeader="scope">
          <el-button
            type="primary"
            :icon="CirclePlus"
            @click="openAddEdit('新增字典', { sysDictTypeId: info?.id, sort: 1, callbackShowStyle: 'primary' }, true)"
          >
            新增字典
          </el-button>
          <el-button type="danger" :icon="Delete" plain :disabled="!scope.isSelected" @click="batchDelete(scope.selectedListIds)">
            批量删除字典
          </el-button>
        </template>

        <template #isShow="{ row }">
          <el-tag :type="row.isShow === 'T' ? 'success' : 'danger'">
            {{ yesNoOptionsLabel(row?.isShow) }}
          </el-tag>
        </template>

        <template #callbackShowStyle="{ row }">
          <el-tag :type="row.callbackShowStyle">
            {{ tagsTypeOptionsLabel(row?.callbackShowStyle) }}
          </el-tag>
        </template>

        <template #operation="{ row }">
          <el-button
            type="primary"
            link
            :disabled="row.isLock === 'T'"
            :icon="EditPen"
            @click="openAddEdit('编辑字典', row, false)"
          >
            编辑
          </el-button>
          <el-button type="primary" link :disabled="row.isLock === 'T'" :icon="Delete" @click="deleteInfo(row)"> 删除 </el-button>
        </template>
      </ProTable>
      <DictDataForm ref="dictDataFormRef" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { CirclePlus, Delete, EditPen } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import { tagsTypeOptionsLabel, yesNoOptions, yesNoOptionsLabel } from '@/config/consts';
import { addDictData, deleteDictData, editDictData, getDictData } from '@/api/modules/system/admin/dict';
import DictDataForm from '@/views/system/admin/dictManage/components/DictDataForm.vue';
import { useHandleData } from '@/hooks/useHandleData';
import type { IDict } from '@/api/interface/system/admin/dict';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { IS_PREVIEW } from '@/config';

defineOptions({
  name: 'DictData'
});

const visible = ref(false);
const info = ref<IDict.DictType>();

// 接收父组件传过来的参数
const show = (params: IDict.DictType) => {
  initParam.sysDictTypeId = params.id as number;
  info.value = params;
  visible.value = true;
};

defineExpose({
  show
});

// 表格配置项
const columns: ColumnProps<IDict.Dict>[] = [
  { type: 'selection', width: 80, selectable: row => row.isLock !== 'T' },
  { prop: 'id', label: '编号', width: 120 },
  { prop: 'alias', label: '别名' },
  { prop: 'codeName', label: '名称' },
  { prop: 'isShow', label: '显示', enum: yesNoOptions, width: 80 },
  { prop: 'callbackShowStyle', label: '回显样式', width: 100 },
  { prop: 'sort', label: '排序', width: 100 },
  { prop: 'remark', label: '备注' },
  { prop: 'createTime', label: '创建时间' },
  { prop: 'updateTime', label: '修改时间' },
  { prop: 'operation', label: '操作', width: 150, fixed: 'right' }
];

const searchColumns: SearchProps[] = [{ prop: 'codeName', label: '名称', el: 'input' }];

// 如果表格需要初始化请求参数，直接定义传给 ProTable(之后每次请求都会自动带上该参数，此参数更改之后也会一直带上，改变此参数会自动刷新表格数据)
const initParam = reactive({ sysDictTypeId: 0 });

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTableRef = ref<ProTableInstance>();

// 获取table列表
const getTableList = (params: IDict.DictQuery) => getDictData(params);

// 打开 drawer(新增、查看、编辑)
const dictDataFormRef = ref<InstanceType<typeof DictDataForm>>();
const openAddEdit = (title: string, row = {}, isAdd = true) => {
  const params: View.DefaultParams = {
    title,
    row: { ...row },
    api: isAdd ? addDictData : editDictData,
    getTableList: proTableRef.value?.getTableList
  };
  dictDataFormRef.value?.acceptParams(params);
};

// 删除信息
const deleteInfo = async (params: IDict.Dict) => {
  if (IS_PREVIEW) {
    return ElMessage.warning({ message: '预览环境，禁止删除所选字典，请谅解！' });
  }
  await useHandleData(deleteDictData, { ids: [params.id] }, `删除【${params.codeName}】字典`);
  proTableRef.value?.getTableList();
};

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  if (IS_PREVIEW) {
    return ElMessage.warning({ message: '预览环境，禁止删除所选字典，请谅解！' });
  }
  await useHandleData(deleteDictData, { ids }, '删除所选字典信息');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
};
</script>

<style scoped lang="scss"></style>
