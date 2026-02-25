<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="语言"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.language.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增语言')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.language.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.language.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.language.export'"
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
          v-auth="'meta.language.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑语言', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.language.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaLanguageForm ref="metaLanguageRef" />
    <ImportExcel ref="ImportExcelRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createMetaLanguageApi,
  removeMetaLanguageApi,
  updateMetaLanguageApi,
  getMetaLanguageListApi,
  getMetaLanguageDetailApi,
  importMetaLanguageExcelApi,
  exportMetaLanguageExcelApi,
} from '@/api/modules/oj/meta/metaLanguage';
import { useHandleData } from '@/hooks/useHandleData';
import MetaLanguageForm from '@/views/oj/meta/metaLanguage/components/MetaLanguageForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IMetaLanguage } from '@/api/interface/oj/meta/metaLanguage';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
// 引入枚举和枚举组件
import { ProblemSourceType } from '@/enums/oj/problem/ProblemSourceType';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';

defineOptions({
  name: 'MetaLanguageView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IMetaLanguage.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'contentType', label: '语言类型' },
  { prop: 'description', label: '语言描述' },
  { prop: 'name', label: '语言名字' },
  { prop: 'compileCommand', label: '编译指令' },
  { prop: 'template', label: '模板' },
  { prop: 'codeTemplate', label: '默认代码模板' },
  { prop: 'spjEnable', label: '是否SPJ' },
  {
    prop: 'oj',
    label: 'OJ类型',
    // 使用render方法替代slot
    render: (scope) => {
      return h(EnumShow, {
        enum: ProblemSourceType,
        code: scope.row.oj || ''
      });
    }
  },
  { prop: 'seq', label: '语言排序' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'contentType', label: '语言类型', el: 'select' },
  { prop: 'description', label: '语言描述', el: 'input' },
  { prop: 'name', label: '语言名字', el: 'input' },
  {
    prop: 'oj',
    label: 'OJ类型',
    // 使用render函数替代custom和slot
    render: (scope) => {
      return h(EnumSelect, {
        modelValue: scope.searchParam.oj,
        'onUpdate:modelValue': (val: string) => {
          scope.searchParam.oj = val;
        },
        enumData: ProblemSourceType,
        type: 'select',
        placeholder: '请选择OJ类型'
      });
    }
  },
  { prop: 'codeTemplate', label: '语言默认代码模板', el: 'input' },
]
// 获取table列表
const getTableList = (params: IMetaLanguage.Query) => {
  let newParams = formatParams(params);
  return getMetaLanguageListApi(newParams);
};
const formatParams = (params: IMetaLanguage.Query) =>{
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
const metaLanguageRef = ref<InstanceType<typeof MetaLanguageForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaLanguageDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaLanguageApi : updateMetaLanguageApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaLanguageRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IMetaLanguage.Row) => {
  await useHandleData(
    removeMetaLanguageApi,
    { ids: [params.id] },
    `删除【${params.id}】语言`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaLanguageApi, { ids }, '删除所选语言')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '语言',
    templateName: '语言',
    tempApi: downloadTemplate,
    importApi: importMetaLanguageExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaLanguage.Query);
  useDownload(exportMetaLanguageExcelApi, "语言", newParams);
};
</script>
