<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="模版文件管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary" v-auth="'sys.temp.file.create'" :icon="CirclePlus" @click="openAddEdit('新增模版文件管理')">
          新增
        </el-button>
        <el-button
          v-auth="'sys.temp.file.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
      </template>

      <template #url="{ row }">
        <a href="#" target="_blank" @click="downloadFile(row?.url)">下载</a>
      </template>
      <template #history="{ row }">
        <span>
          <el-button type="primary" link @click="openFileHistory(row)"> 文件历史 </el-button>
        </span>
      </template>

      <template #operation="{ row }">
        <el-button
          v-auth="'sys.temp.file.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑模版文件管理', row, false)"
        >
          编辑
        </el-button>
        <el-button v-auth="'sys.temp.file.remove'" type="primary" link :icon="Delete" @click="deleteInfo(row)"> 删除 </el-button>
      </template>
    </ProTable>
    <SysTempFileForm ref="sysTempFileRef" />
    <SysTemplateHistoryList ref="sysTempFileHistoryListRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { CirclePlus, Delete, EditPen } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import {
  createSysTempFileApi,
  removeSysTempFileApi,
  updateSysTempFileApi,
  getSysTempFileListApi,
  getSysTempFileDetailApi
} from '@/api/modules/system/admin/sysTempFile';
import { useHandleData } from '@/hooks/useHandleData';
import SysTempFileForm from '@/views/system/admin/sysTempFile/components/SysTempFileForm.vue';
import { useOptionsStore } from '@/stores/modules/options';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ISysTempFile } from '@/api/interface/system/admin/sysTempFile';
import SysTemplateHistoryList from '@/views/system/admin/sysTempFile/components/HistoryList.vue';
defineOptions({
  name: 'SysTempFileView'
});
const optionsStore = useOptionsStore();
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ISysTempFile.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'id', label: '模板标识', width: 120 },
  { prop: 'tempName', label: '模版名' },
  { prop: 'url', label: '文件', width: 120 },
  { prop: 'remark', label: '备注' },
  { prop: 'history', label: '历史' },
  {
    prop: 'createId',
    label: '创建人',
    tag: true,
    enum: optionsStore.getDictOptions('dynamic_user_options'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'createTime', label: '创建时间' },
  {
    prop: 'updateId',
    label: '更新人',
    tag: true,
    enum: optionsStore.getDictOptions('dynamic_user_options'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'updateTime', label: '更新时间' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];
// 搜索条件项
const searchColumns: SearchProps[] = [{ prop: 'tempName', label: '模版名', el: 'input' }];
// 获取table列表
const getTableList = (params: ISysTempFile.Query) => {
  let newParams = formatParams(params);
  return getSysTempFileListApi(newParams);
};
const formatParams = (params: ISysTempFile.Query) => {
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
};
// 打开 drawer(新增、查看、编辑)
const sysTempFileRef = ref<InstanceType<typeof SysTempFileForm>>();
const openAddEdit = async (title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getSysTempFileDetailApi({ id: row?.id });
    row = record?.data;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createSysTempFileApi : updateSysTempFileApi,
    getTableList: proTableRef.value?.getTableList,
    isAdd: isAdd
  };
  sysTempFileRef.value?.acceptParams(params);
};

const sysTempFileHistoryListRef = ref<InstanceType<typeof SysTemplateHistoryList>>();

// 删除信息
const deleteInfo = async (params: ISysTempFile.Row) => {
  await useHandleData(removeSysTempFileApi, { ids: [params.id] }, `删除【${params.id}】模版文件管理`);
  proTableRef.value?.getTableList();
};
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeSysTempFileApi, { ids }, '删除所选模版文件管理');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
};

const downloadFile = (url: string) => {
  const link = document.createElement('a'); // 创建一个 a 标签用来模拟点击事件
  link.style.display = 'none';
  link.href = url;
  const fileName = getFileNameFromUrl(url);
  link.setAttribute('download', fileName);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

const getFileNameFromUrl = (url: string) => {
  // 使用正则表达式提取文件名
  const regex = /\/([^/]+)$/;
  const match = url.match(regex);
  return match ? match[1] : '';
};

// 打开历史记录页
const openFileHistory = (row: ISysTempFile.Row) => {
  sysTempFileHistoryListRef.value?.show(row);
};
</script>
