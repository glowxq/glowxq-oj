<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="角色列表"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button v-auth="'sys.role.create_btn'" type="primary" :icon="CirclePlus" @click="openRoleForm('新增角色')">
          新增角色
        </el-button>
        <el-button
          v-auth="'sys.role.delete_btn'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除角色
        </el-button>
      </template>

      <template #operation="{ row }">
        <el-button
          v-auth="[{ type: 'and', conditions: ['sys.role.setting_btn', 'sys.role.update_btn'] }]"
          type="primary"
          link
          :icon="Lock"
          @click="openRolePermissions('设置权限', row)"
        >
          权限
        </el-button>
        <el-button
          v-auth="'sys.role.update_btn'"
          type="primary"
          link
          :icon="EditPen"
          @click="openRoleForm('编辑角色', row, false)"
        >
          编辑
        </el-button>
        <el-button v-auth="'sys.menu.delete_btn'" v-if="row.id !== 1" type="primary" link :icon="Delete" @click="deleteInfo(row)">
          删除
        </el-button>
      </template>
    </ProTable>
    <RoleForm ref="roleFormRef" />
    <RolePermissions ref="rolePermissionsRef" />
  </div>
</template>

<script setup lang="ts">
import { CirclePlus, Delete, EditPen, Lock } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import { useHandleData } from '@/hooks/useHandleData';
import { addRole, deleteRole, editRole, getRoleList, setRoleMenus } from '@/api/modules/system/admin/role';
import RoleForm from '@/views/system/admin/roleManage/components/RoleForm.vue';
import RolePermissions from '@/views/system/admin/roleManage/components/RolePermissions.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IRole } from '@/api/interface/system/admin/role';
import { ref } from 'vue';

defineOptions({
  name: 'RoleManage'
});

// 表格配置项
const columns: ColumnProps<IRole.Info>[] = [
  { type: 'selection', width: 80, selectable: row => row.isLock !== 'T' },
  { prop: 'id', label: '编号', width: 80 },
  { prop: 'roleName', label: '角色名称' },
  { prop: 'permissions', tag: true, label: '标识' },
  { prop: 'remark', label: '备注' },
  { prop: 'createTime', label: '创建时间' },
  { prop: 'updateTime', label: '修改时间' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];
// 表格配置项
const searchColumns: SearchProps[] = [
  { prop: 'roleName', label: '角色名称', el: 'input' },
  { prop: 'permissions', label: '标识', el: 'input' }
];

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTableRef = ref<ProTableInstance>();

// 获取table列表
const getTableList = (params: IRole.Query) => getRoleList(params);

const roleFormRef = ref<InstanceType<typeof RoleForm>>();
const openRoleForm = (title: string, row = {}, isAdd = true) => {
  const params: View.DefaultParams = {
    title,
    row: { ...row },
    api: isAdd ? addRole : editRole,
    getTableList: proTableRef.value?.getTableList
  };
  roleFormRef.value?.acceptParams(params);
};

const rolePermissionsRef = ref<InstanceType<typeof RolePermissions>>();
const openRolePermissions = (title: string, row = {}) => {
  const params: View.DefaultParams = {
    title,
    row: { ...row },
    api: setRoleMenus
  };
  rolePermissionsRef.value?.acceptParams(params);
};

// 删除信息
const deleteInfo = async (params: IRole.Info) => {
  await useHandleData(deleteRole, { ids: [params.id] }, `删除【${params.roleName}】角色`);
  proTableRef.value?.getTableList();
};

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(deleteRole, { ids }, '删除所选角色信息');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
};
</script>
