<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="租户"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'tenant.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增租户')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'tenant.remove'"
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
          v-auth="'tenant.detail'"
          type="primary"
          link
          :icon="View"
          @click="openDetail(row)"
        >
          查看
        </el-button>
        <el-button
          v-auth="'tenant.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑租户', row, false)"
        >
          编辑
        </el-button>
        <el-button
          type="primary"
          link
          :icon="Link"
          @click="copyTenantLink(row)"
        >
          复制链接
        </el-button>
        <el-button
          v-auth="'tenant.create'"
          type="primary"
          link
          :icon="CopyDocument"
          @click="cloneTenant(row)"
        >
          复制租户
        </el-button>
        <el-button
          v-auth="'tenant.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <TenantForm ref="tenantRef" />
  </div>
</template>

<script setup lang="tsx">
import { ref } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  View,
  Link,
  CopyDocument
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createTenantApi,
  removeTenantApi,
  updateTenantApi,
  getTenantListApi,
  getTenantDetailApi,
} from '@/api/modules/system/tenant/tenant';
import { useHandleData } from '@/hooks/useHandleData';
import TenantForm from '@/views/system/tenant/tenant/components/TenantForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps, RenderScope } from '@/components/Common/ProTable/interface';
import type { ITenant } from '@/api/interface/system/tenant/tenant';
import { ElMessage } from 'element-plus';

defineOptions({
  name: 'TenantView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ITenant.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'tenantCode', label: '租户编码' },
  { prop: 'tenantName', label: '租户名称' },
  { prop: 'contactName', label: '联系人' },
  { 
    prop: 'enable', 
    label: '状态', 
    enum: [
      { label: '禁用', value: false },
      { label: '正常', value: true }
    ],
    fieldNames: { label: 'label', value: 'value' },
    width: 90
  },
  { prop: 'expiredTime', label: '到期时间' },
  { 
    prop: 'maxUserNum', 
    label: '最大用户数',
    width: 100
  },
  { 
    prop: 'currentUserNum', 
    label: '当前用户数',
    width: 100
  },
  { 
    prop: 'logoUrl', 
    label: 'Logo',
    render: (scope: RenderScope<ITenant.Row>) => {
      return scope.row.logoUrl ? (
        <img src={scope.row.logoUrl} style={{ width: '40px', height: '40px', objectFit: 'contain' }} />
      ) : '-'
    }
  },
  { 
    prop: 'themeColor', 
    label: '主题色',
    width: 80,
    render: (scope: RenderScope<ITenant.Row>) => {
      const color = scope.row.themeColor || '#009688';
      return (
        <div 
          class="color-block" 
          style={{ 
            backgroundColor: color, 
            width: '36px', 
            height: '36px', 
            borderRadius: '4px', 
            border: '1px solid #dcdfe6',
            margin: '0 auto'
          }}
        ></div>
      )
    }
  },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'tenantCode', label: '租户编码', el: 'input' },
  { prop: 'tenantName', label: '租户名称', el: 'input' },
  { prop: 'contactName', label: '联系人姓名', el: 'input' },
  { 
    prop: 'enable', 
    label: '状态', 
    el: 'select',
    props: {
      options: [
        { label: '禁用', value: false },
        { label: '正常', value: true }
      ]
    }
  },
  { prop: 'expiredTime',
    label: '到期时间',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
]
// 获取table列表
const getTableList = (params: ITenant.Query) => {
  let newParams = formatParams(params);
  return getTenantListApi(newParams);
};
const formatParams = (params: ITenant.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.expiredTime && (newParams.expiredTimeStart = newParams.expiredTime[0]);
  newParams.expiredTime && (newParams.expiredTimeEnd = newParams.expiredTime[1]);
  delete newParams.expiredTime;
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}
// 打开 drawer(新增、查看、编辑)
const tenantRef = ref<InstanceType<typeof TenantForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getTenantDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createTenantApi : updateTenantApi,
    getTableList: proTableRef.value?.getTableList
  }
  tenantRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ITenant.Row) => {
  await useHandleData(
    removeTenantApi,
    { ids: [params.id] },
    `删除【${params.tenantName}】租户`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeTenantApi, { ids }, '删除所选租户')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 查看详情
const openDetail = async (row: ITenant.Row) => {
  if (!row?.id) {
    ElMessage.error('租户ID不存在');
    return;
  }
  const record = await getTenantDetailApi({ id: row.id })
  const params = {
    title: '租户详情',
    row: { ...record?.data },
    api: undefined,
    readonly: true, // 添加只读标志
    getTableList: undefined
  }
  tenantRef.value?.acceptParams(params)
}

// 复制租户
const cloneTenant = async (row: ITenant.Row) => {
  if (!row?.id) {
    ElMessage.error('租户ID不存在');
    return;
  }
  
  // 获取租户详情
  const record = await getTenantDetailApi({ id: row.id });
  if (!record?.data) {
    ElMessage.error('获取租户详情失败');
    return;
  }
  
  // 创建新租户对象，复制原租户的大部分属性
  const newTenant: any = { ...record.data };
  
  // 清除不应该复制的字段
  newTenant.id = null;
  newTenant.tenantId = '';
  newTenant.createTime = null;
  newTenant.updateTime = null;
  newTenant.currentUserNum = 0;
  
  // 修改需要更改的字段
  newTenant.tenantCode = `${newTenant.tenantCode}_COPY`;
  newTenant.tenantName = `${newTenant.tenantName} (复制)`;
  
  // 打开新建租户表单
  const params = {
    title: '新增租户 (复制)',
    row: newTenant,
    api: createTenantApi,
    getTableList: proTableRef.value?.getTableList
  }
  tenantRef.value?.acceptParams(params);
  
  ElMessage.success('已复制租户信息，请修改后保存');
}

// 复制租户链接
const copyTenantLink = (row: ITenant.Row) => {
  if (!row?.tenantId) {
    ElMessage.error('租户ID不存在');
    return;
  }
  
  // 生成租户直达链接
  const currentOrigin = window.location.origin;
  const directLink = `${currentOrigin}/tenant/${row.tenantId}`;
  
  // 复制到剪贴板
  navigator.clipboard.writeText(directLink)
    .then(() => {
      ElMessage.success(`租户【${row.tenantName}】直达链接已复制到剪贴板`);
    })
    .catch(() => {
      // 复制失败，使用fallback方法
      const textarea = document.createElement('textarea');
      textarea.value = directLink;
      textarea.style.position = 'fixed';
      document.body.appendChild(textarea);
      textarea.select();
      
      try {
        document.execCommand('copy');
        ElMessage.success(`租户【${row.tenantName}】直达链接已复制到剪贴板`);
      } catch (err) {
        ElMessage.error('复制失败，请手动复制链接');
        console.error('复制失败:', err);
      }
      
      document.body.removeChild(textarea);
    });
};
</script>

<style scoped lang="scss">
.table-box {
  margin: 20px;
  padding: 24px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.color-display {
  display: flex;
  align-items: center;
  gap: 8px;

  .color-block {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
  }
}
</style>