<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="菜单"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'meta.menu.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增菜单')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'meta.menu.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'meta.menu.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'meta.menu.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>
      <!-- 自定义图标类型列 -->
      <template #iconType="scope">
        <EnumShow :enum="MenuIconType" :code="scope.row.iconType" />
      </template>
      <!-- 自定义菜单类型列 -->
      <template #type="scope">
        <EnumShow :enum="MenuType" :code="scope.row.type" />
      </template>
      <!-- 自定义图标预览 -->
      <template #activeIcon="scope">
        <div v-if="scope.row.iconType === 'Image'">
          <el-image
            v-if="scope.row.activeIcon"
            :src="scope.row.activeIcon"
            style="width: 40px; height: 40px"
            :preview-src-list="[scope.row.activeIcon]"
          />
          <span v-else>-</span>
        </div>
        <span v-else>{{ scope.row.activeIcon }}</span>
      </template>
      <template #inactiveIcon="scope">
        <div v-if="scope.row.iconType === 'Image'">
          <el-image
            v-if="scope.row.inactiveIcon"
            :src="scope.row.inactiveIcon"
            style="width: 40px; height: 40px"
            :preview-src-list="[scope.row.inactiveIcon]"
          />
          <span v-else>-</span>
        </div>
        <span v-else>{{ scope.row.inactiveIcon }}</span>
      </template>
      <template #operation="{ row }">
        <el-button
          v-auth="'meta.menu.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑菜单', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'meta.menu.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <MetaMenuForm ref="metaMenuRef" />
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
  createMetaMenuApi,
  removeMetaMenuApi,
  updateMetaMenuApi,
  getMetaMenuListApi,
  getMetaMenuDetailApi,
  importMetaMenuExcelApi,
  exportMetaMenuExcelApi,
} from '@/api/modules/system/meta/metaMenu';
import { useHandleData } from '@/hooks/useHandleData';
import MetaMenuForm from '@/views/system/meta/metaMenu/components/MetaMenuForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps, RenderScope } from '@/components/Common/ProTable/interface';
import type { IMetaMenu } from '@/api/interface/system/meta/metaMenu';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import { MenuType } from '@/enums/system/meta/MenuType';
import { MenuIconType } from '@/enums/system/meta/MenuIconType';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';

defineOptions({
  name: 'MetaMenuView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<IMetaMenu.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'name', label: '底部菜单名称' },
  {
    prop: 'activeIcon',
    label: '选中图标',
    render: (scope: RenderScope<IMetaMenu.Row>) => {
      if (scope.row.iconType === 'Image') {
        if (scope.row.activeIcon) {
          return h('el-image', {
            src: scope.row.activeIcon,
            style: {
              width: '40px',
              height: '40px'
            },
            previewSrcList: [scope.row.activeIcon]
          });
        }
        return h('span', '-');
      }
      return h('span', scope.row.activeIcon);
    }
  },
  {
    prop: 'inactiveIcon',
    label: '未选中图标',
    render: (scope: RenderScope<IMetaMenu.Row>) => {
      if (scope.row.iconType === 'Image') {
        if (scope.row.inactiveIcon) {
          return h('el-image', {
            src: scope.row.inactiveIcon,
            style: {
              width: '40px',
              height: '40px'
            },
            previewSrcList: [scope.row.inactiveIcon]
          });
        }
        return h('span', '-');
      }
      return h('span', scope.row.inactiveIcon);
    }
  },
  {
    prop: 'iconType',
    label: '图标类型',
    render: (scope: RenderScope<IMetaMenu.Row>) => {
      return h(EnumShow, {
        enum: MenuIconType,
        code: scope.row.iconType || ''
      });
    }
  },
  {
    prop: 'type',
    label: '菜单类型',
    render: (scope: RenderScope<IMetaMenu.Row>) => {
      return h(EnumShow, {
        enum: MenuType,
        code: scope.row.type || ''
      });
    }
  },
  { prop: 'path', label: '菜单路径' },
  { prop: 'enable', label: '状态' },
  { prop: 'remark', label: '备注' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '底部菜单名称', el: 'input' },
  {
    prop: 'iconType',
    label: '图标类型',
    render: (scope: any) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: MenuIconType,
        modelValue: scope.searchParam.iconType,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.iconType = val;
        }
      });
    }
  },
  {
    prop: 'type',
    label: '菜单类型',
    render: (scope: any) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: MenuType,
        modelValue: scope.searchParam.type,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.type = val;
        }
      });
    }
  },
  { prop: 'path', label: '菜单路径', el: 'input' },
  { prop: 'enable', label: '状态', el: 'switch' },
  { prop: 'sort', label: '排序', el: 'switch' },
  { prop: 'hasChildren', label: '是否有子级', el: 'switch' },
  { prop: 'lock', label: '是否锁定', el: 'switch' },
]
// 获取table列表
const getTableList = (params: IMetaMenu.Query) => {
  let newParams = formatParams(params);
  return getMetaMenuListApi(newParams);
};
const formatParams = (params: IMetaMenu.Query) =>{
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
const metaMenuRef = ref<InstanceType<typeof MetaMenuForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getMetaMenuDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createMetaMenuApi : updateMetaMenuApi,
    getTableList: proTableRef.value?.getTableList
  }
  metaMenuRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: IMetaMenu.Row) => {
  await useHandleData(
    removeMetaMenuApi,
    { ids: [params.id] },
    `删除【${params.id}】菜单`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeMetaMenuApi, { ids }, '删除所选菜单')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '菜单',
    templateName: '菜单',
    tempApi: downloadTemplate,
    importApi: importMetaMenuExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IMetaMenu.Query);
  useDownload(exportMetaMenuExcelApi, "菜单", newParams);
};
</script>
