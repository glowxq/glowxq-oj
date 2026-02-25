<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="文本"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.text.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增文本')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.text.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.text.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.text.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>

      <!-- 文本类型列 -->
      <template #textType="{ row }">
        <EnumShow :enum="TextType" :code="row.textType" />
      </template>

      <!-- 业务类型列 -->
      <template #businessType="{ row }">
        <EnumShow :enum="TextBusinessType" :code="row.businessType" />
      </template>

      <!-- 文本内容预览 -->
      <template #content="{ row }">
        <el-tooltip
          :content="row.content"
          placement="top-start"
          :hide-after="2000"
          :max-width="500"
          :show-after="500"
        >
          <span>{{ row.content ? (row.content.length > 20 ? row.content.slice(0, 20) + '...' : row.content) : '-' }}</span>
        </el-tooltip>
      </template>

      <template #operation="{ row }">
        <el-button
          v-auth="'meta.text.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑文本', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.text.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaTextForm ref="metaTextRef" />
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
  createMetaTextApi,
  removeMetaTextApi,
  updateMetaTextApi,
  getMetaTextListApi,
  getMetaTextDetailApi,
  importMetaTextExcelApi,
  exportMetaTextExcelApi,
} from '@/api/modules/system/meta/metaText';
import { useHandleData } from '@/hooks/useHandleData';
import MetaTextForm from '@/views/system/meta/metaText/components/MetaTextForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps, RenderScope } from '@/components/Common/ProTable/interface';
import type { IMetaText } from '@/api/interface/system/meta/metaText';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';
import { TextType } from '@/enums/system/meta/TextType';
import { TextBusinessType } from '@/enums/system/meta/TextBusinessType';

// 扩展列接口以支持slot属性
interface CustomColumnProps<T> extends ColumnProps<T> {
  slot?: string;
}

defineOptions({
  name: 'MetaTextView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: CustomColumnProps<IMetaText.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: 'text名字' },
  { prop: 'textKey', label: '文本key' },
  {
    prop: 'textType',
    label: '文本类型',
    render: (scope: any) => scope.row.textType || '-'
  },
  { prop: 'icon', label: '图标' },
  {
    prop: 'businessType',
    label: '业务类型',
    render: (scope: any) => scope.row.businessType || '-'
  },
  { prop: 'title', label: '文本标题' },
  { prop: 'skipUrl', label: '跳转URL' },
  {
    prop: 'content',
    label: '文本内容',
    render: (scope: any) => scope.row.content || '-',
    width: 200
  },
  { prop: 'sort', label: '排序(降序)' },
  {
    prop: 'enable',
    label: '启用',
    render: (scope: RenderScope<IMetaText.Row>) => {
      return h('el-tag', {
        type: scope.row.enable ? 'success' : 'danger'
      }, scope.row.enable ? '启用' : '禁用');
    }
  },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: 'text名字', el: 'input' },
  { prop: 'textKey', label: '文本key', el: 'input' },
  {
    prop: 'textType',
    label: '文本类型',
    render: (scope: any) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: TextType,
        modelValue: scope.searchParam.textType,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.textType = val;
        }
      });
    }
  },
  { prop: 'icon', label: '图标', el: 'input' },
  {
    prop: 'businessType',
    label: '业务类型',
    render: (scope: any) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: TextBusinessType,
        modelValue: scope.searchParam.businessType,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.businessType = val;
        }
      });
    }
  },
  { prop: 'title', label: '文本标题', el: 'input' },
  { prop: 'skipUrl', label: '跳转URL', el: 'input' },
  { prop: 'content', label: '文本内容', el: 'input' },
  { prop: 'sort', label: '排序(降序)', el: 'input' },
  { prop: 'enable', label: '启用', el: 'switch' },
]
// 获取table列表
const getTableList = (params: IMetaText.Query) => {
  let newParams = formatParams(params);
  return getMetaTextListApi(newParams);
};
const formatParams = (params: IMetaText.Query) =>{
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
const metaTextRef = ref<InstanceType<typeof MetaTextForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaTextDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaTextApi : updateMetaTextApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaTextRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IMetaText.Row) => {
  await useHandleData(
    removeMetaTextApi,
    { ids: [params.id] },
    `删除【${params.id}】文本`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaTextApi, { ids }, '删除所选文本')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '文本',
    templateName: '文本',
    tempApi: downloadTemplate,
    importApi: importMetaTextExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaText.Query);
  useDownload(exportMetaTextExcelApi, "文本", newParams);
};
</script>

<style scoped>
.table-box {
  padding: 16px;
}
</style>
