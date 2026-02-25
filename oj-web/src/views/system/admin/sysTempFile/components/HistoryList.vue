<template>
  <div class="dialog-table-box">
    <el-dialog
      v-model="visible"
      :title="`模板: ${info?.tempName}`"
      width="90%"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      append-to-body
    >
      <ProTable
        ref="proTableRef"
        title="历史记录"
        :indent="20"
        :columns="columns"
        :request-api="getTableList"
        :init-param="initParam"
      >
        <template #url="{ row }">
          <a href="#" target="_blank" @click="downloadFile(row?.url)">下载</a>
        </template>
      </ProTable>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import ProTable from '@/components/Common/ProTable/index.vue';
import type { IDict } from '@/api/interface/system/admin/dict';
import type { ColumnProps, ProTableInstance } from '@/components/Common/ProTable/interface';
import { reactive, ref } from 'vue';
import { getSysTempFileHistoryListApi } from '@/api/modules/system/admin/sysTempFile';
import type { ISysTempFile } from '@/api/interface/system/admin/sysTempFile';
import { useOptionsStore } from '@/stores/modules/options';

defineOptions({
  name: 'SysTemplateHistoryList'
});

const visible = ref(false);
const info = ref<ISysTempFile.History>();

// 接收父组件传过来的参数
const show = (params: ISysTempFile.Row) => {
  initParam.sysTempFileId = params.id as number;
  info.value = params;
  visible.value = true;
};

defineExpose({
  show
});

const optionsStore = useOptionsStore();
// 表格配置项
const columns: ColumnProps<IDict.Dict>[] = [
  { prop: 'sysTempFileId', label: '模板标识', width: 120 },
  { prop: 'sysFileId', label: '文件标识', width: 120 },
  { prop: 'tempName', label: '模版名', tag: true },
  { prop: 'url', label: '文件', width: 120 },
  { prop: 'remark', label: '备注' },
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
  { prop: 'createTime', label: '创建时间' }
];

// 如果表格需要初始化请求参数，直接定义传给 ProTable(之后每次请求都会自动带上该参数，此参数更改之后也会一直带上，改变此参数会自动刷新表格数据)
const initParam = reactive({ sysTempFileId: 0 });

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTableRef = ref<ProTableInstance>();

// 获取table列表
const getTableList = (params: ISysTempFile.HistoryQuery) => getSysTempFileHistoryListApi(params);

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
</script>

<style scoped lang="scss"></style>
