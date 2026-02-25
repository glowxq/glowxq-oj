<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="图片"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.image.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增图片')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.image.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.image.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.image.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>

      <!-- 业务类型列 -->
      <template #businessType="{ row }">
        <EnumShow :enum="ImageBusinessType" :code="row.businessType" />
      </template>

      <!-- 图片URL预览 -->
      <template #url="{ row }">
        <el-image
          v-if="row.url"
          :src="row.url"
          style="width: 100px; height: 60px; object-fit: cover"
          :preview-src-list="[row.url]"
          fit="cover"
        >
          <template #error>
            <div class="image-slot">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        <span v-else>-</span>
      </template>

      <!-- 启用状态展示 -->
      <template #enable="{ row }">
        <el-tag :type="row.enable ? 'success' : 'danger'">
          {{ row.enable ? '启用' : '禁用' }}
        </el-tag>
      </template>

      <template #operation="{ row }">
        <el-button
          v-auth="'meta.image.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑图片', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.image.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaImageForm ref="metaImageRef" />
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
  Picture
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createMetaImageApi,
  removeMetaImageApi,
  updateMetaImageApi,
  getMetaImageListApi,
  getMetaImageDetailApi,
  importMetaImageExcelApi,
  exportMetaImageExcelApi,
} from '@/api/modules/system/meta/metaImage';
import { useHandleData } from '@/hooks/useHandleData';
import MetaImageForm from '@/views/system/meta/metaImage/components/MetaImageForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps, RenderScope } from '@/components/Common/ProTable/interface';
import type { IMetaImage } from '@/api/interface/system/meta/metaImage';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';
import { ImageBusinessType } from '@/enums/system/meta/ImageBusinessType';

defineOptions({
  name: 'MetaImageView'
})

const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IMetaImage.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: '图片名字' },
  { prop: 'imageKey', label: '图片key' },
  {
    prop: 'businessType',
    label: '业务类型',
    render: (scope: any) => scope.row.businessType || '-'
  },
  {
    prop: 'url',
    label: '图片预览',
    render: (scope: any) => scope.row.url || '-',
    width: 120
  },
  { prop: 'content', label: '图片介绍' },
  { prop: 'skipUrl', label: '跳转链接' },
  { prop: 'sort', label: '排序(降序)' },
  {
    prop: 'enable',
    label: '启用',
    render: (scope: any) => scope.row.enable ? '启用' : '禁用'
  },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '图片名字', el: 'input' },
  { prop: 'imageKey', label: '图片key', el: 'input' },
  {
    prop: 'businessType',
    label: '业务类型',
    render: (scope: any) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: ImageBusinessType,
        modelValue: scope.searchParam.businessType,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.businessType = val;
        }
      });
    }
  },
  { prop: 'content', label: '图片介绍', el: 'input' },
  { prop: 'skipUrl', label: '跳转链接', el: 'input' },
  { prop: 'sort', label: '排序(降序)', el: 'input' },
  { prop: 'enable', label: '启用', el: 'switch' },
]

// 获取table列表
const getTableList = (params: IMetaImage.Query) => {
  let newParams = formatParams(params);
  return getMetaImageListApi(newParams);
};

const formatParams = (params: IMetaImage.Query) =>{
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
const metaImageRef = ref<InstanceType<typeof MetaImageForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaImageDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaImageApi : updateMetaImageApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaImageRef.value?.acceptParams(params)
}

// 删除信息
const deleteInfo = async (params: IMetaImage.Row) => {
  await useHandleData(
    removeMetaImageApi,
    { ids: [params.id] },
    `删除【${params.id}】图片`
  )
  proTableRef.value?.getTableList()
}

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaImageApi, { ids }, '删除所选图片')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}

// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '图片',
    templateName: '图片',
    tempApi: downloadTemplate,
    importApi: importMetaImageExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}

// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaImage.Query);
  useDownload(exportMetaImageExcelApi, "图片", newParams);
};
</script>

<style scoped lang="scss">
.table-box {
  padding: 16px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 24px;
}
</style>
