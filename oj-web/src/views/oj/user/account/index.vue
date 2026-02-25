<template>
  <div class="main-box">
    <el-tabs v-model="activeTab" class="apple-tabs" @tab-click="handleTabClick">
      <el-tab-pane :name="UserQueryType.GROUP.code" label="班级">
        <GroupTable ref="groupTableRef" v-model="initParam.groupIds" @change="handleGroupChange" />
      </el-tab-pane>
      <el-tab-pane :name="UserQueryType.TAG.code" label="标签">
        <TagTable ref="tagTableRef" v-model="initParam.tagIds" @change="handleTagChange" />
      </el-tab-pane>
      <el-tab-pane :name="UserQueryType.DEPT.code" label="部门">
        <DeptTree
          ref="deptTreeRef"
          :default-value="initParam.deptId"
          :request-api="getUserDeptTree"
          label="name"
          title="部门列表"
          @change="changeDeptTree"
        />
      </el-tab-pane>
    </el-tabs>
    <div class="table-box">
      <ProTable
        ref="proTableRef"
        :border="false"
        :columns="columns"
        :indent="20"
        :init-param="initParam"
        :loading-time="200"
        :request-api="getTableList"
        :search-col="4"
        :search-columns="searchColumns"
        class="apple-table"
        title="用户列表"
        @reset="handleTableReset"
      >
        <!-- 表格 header 按钮 -->
        <template #tableHeader="scope">
          <div class="table-header-buttons">
            <el-button
              v-auth="'sys.user.create_btn'"
              :icon="CirclePlus"
              class="apple-button"
              type="primary"
              @click="openUserAdd('新增用户')"
            >
              新增用户
            </el-button>
            <el-button
              v-auth="'sys.user.delete_btn'"
              :disabled="!scope.isSelected"
              :icon="Delete"
              class="apple-button"
              plain
              type="danger"
              @click="batchDelete(scope.selectedListIds)"
            >
              批量删除
            </el-button>
            <el-button :disabled="!scope.isSelected" class="apple-button" plain type="info" @click="settingDept(scope)">
              <el-icon class="el-icon--left">
                <SvgIcon name="org" />
              </el-icon>
              设置部门
            </el-button>
            <el-button :disabled="!scope.isSelected" class="apple-button" plain type="primary" @click="settingGroup(scope)">
              <el-icon class="el-icon--left">
                <SvgIcon name="group" />
              </el-icon>
              设置班级
            </el-button>
            <el-button
              :disabled="!scope.isSelected"
              :icon="Unlock"
              class="apple-button"
              plain
              type="info"
              @click="unlock(scope.selectedListIds)"
            >
              解锁
            </el-button>
          </div>
        </template>

        <template #username="{ row }">
          <el-button class="user-link" link type="primary">
            {{ row?.username }}
          </el-button>
        </template>

        <template #logo="{ row }">
          <el-image v-if="row.logo" :src="row.logo" class="user-avatar" />
          <div v-else class="user-avatar-placeholder">--</div>
        </template>

        <template #deptInfo="{ row }">
          <el-tag v-for="tag in formatInfo(row.deptInfo)" :key="tag.id" class="user-tag" type="info">
            {{ tag.name }}
          </el-tag>
        </template>

        <template #roleInfo="{ row }">
          <el-tag v-for="tag in formatInfo(row.roleInfo)" :key="tag.id" class="user-tag" type="info">
            {{ tag.name }}
          </el-tag>
        </template>

        <template #operation="{ row }">
          <div class="btn-group">
            <el-button v-auth="'sys.user.update_btn'" :icon="EditPen" link type="primary" @click="openUserEdit('编辑用户', row)">
              编辑
            </el-button>
            <el-button v-auth="'sys.user.unlock_btn'" :icon="Unlock" link type="primary" @click="unlock(row.id)">
              解锁
            </el-button>
            <el-button :icon="View" link type="primary" @click="showUserDetail(row.id)"> 详情 </el-button>
            <div
              v-auth="[
                {
                  type: 'or',
                  conditions: [
                    'sys.user.unlock_btn',
                    'sys.user.role_set_btn',
                    'sys.user.delete_btn',
                    'sys.user.dept_set_btn',
                    'sys.user.data_scope_btn'
                  ]
                }
              ]"
              class="el-dropdown"
            >
              <el-dropdown trigger="click">
                <el-button :icon="DArrowRight" link type="primary"> 更多</el-button>
                <template #dropdown>
                  <el-dropdown-menu class="apple-dropdown-menu">
                    <div v-auth="'sys.user.role_set_btn'">
                      <el-dropdown-item :icon="User" @click="openUserPermissions('设置角色', row)"> 设置角色 </el-dropdown-item>
                    </div>
                    <div v-auth="'sys.user.dept_set_btn'">
                      <el-dropdown-item type="primary" @click="settingDept(row)">
                        <el-icon>
                          <SvgIcon name="org" />
                        </el-icon>
                        设置部门
                      </el-dropdown-item>
                    </div>
                    <div v-auth="'sys.user.group_set_btn'">
                      <el-dropdown-item type="primary" @click="settingGroup(row)">
                        <el-icon>
                          <SvgIcon name="group" />
                        </el-icon>
                        设置班级
                      </el-dropdown-item>
                    </div>
                    <div v-auth="'sys.user.unlock_btn'">
                      <el-dropdown-item :icon="Refresh" link type="primary" @click="resetPwd(row)"> 重置密码 </el-dropdown-item>
                    </div>
                    <div v-auth="'sys.user.delete_btn'">
                      <el-dropdown-item v-if="row.id !== 1" :icon="Delete" @click="deleteInfo(row)"> 删除 </el-dropdown-item>
                    </div>
                    <div v-auth="'sys.user.data_role_set_btn'">
                      <el-dropdown-item v-if="row.id !== 1" type="primary" @click="openUserDataPermissions('设置数据角色', row)">
                        <el-icon class="el-icon--left">
                          <SvgIcon name="scope" />
                        </el-icon>
                        设置数据角色
                      </el-dropdown-item>
                    </div>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </template>
      </ProTable>
      <UserAdd ref="userAddRef" @submit="refreshDeptTree" />
      <UserEdit ref="userEditRef" />
      <UserPermissions ref="userPermissionsRef" />
      <UserDeptForm ref="userDeptFormRef" @submit="refreshDeptTree" />
      <UserGroupForm ref="userGroupFormRef" @submit="refreshDeptTree" />
      <UserDataPermissions ref="userDataPermissionsRef" />

      <!-- 用户详情对话框 -->
      <el-dialog
        v-model="userDetailVisible"
        :close-on-click-modal="false"
        center
        class="apple-style-dialog"
        destroy-on-close
        title="用户详细信息"
        width="480px"
      >
        <div class="user-detail-container">
          <user-cart v-if="currentUserDetail" :user="currentUserDetail" />

          <div v-if="currentUserBindInfo" class="user-bind-info">
            <div v-if="currentUserBindInfo.groups && currentUserBindInfo.groups.length > 0" class="bind-section">
              <h3 class="bind-title">所属班级</h3>
              <div class="bind-tags">
                <el-tag v-for="group in currentUserBindInfo.groups" :key="group.id" class="group-tag" type="success">
                  {{ group.name }}
                </el-tag>
              </div>
            </div>

            <div v-if="currentUserBindInfo.tags && currentUserBindInfo.tags.length > 0" class="bind-section">
              <h3 class="bind-title">用户标签</h3>
              <div class="bind-tags">
                <el-tag
                  v-for="tag in currentUserBindInfo.tags"
                  :key="tag.id"
                  :style="{
                    backgroundColor: tag.color || '#e9e9eb',
                    color: tag.textColor || '#333',
                    borderColor: tag.color || '#e9e9eb'
                  }"
                  class="tag-item"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { CirclePlus, DArrowRight, Delete, EditPen, Refresh, Unlock, User, View } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import DeptTree from '@/views/system/admin/accountManage/components/DeptTree.vue';
import { useHandleData } from '@/hooks/useHandleData';
import {
  addOJUser,
  deleteOJUser,
  editOJUser,
  getOJUserDetail,
  getOJUserList,
  getUserBindInfo,


} from '@/api/modules/oj/user/user';
import {
  getUserDeptTree,
  resetPassword,
  setUserDataRole,
  setUserRole,
  unlockUser
} from '@/api/modules/system/admin/user';
import { useOptionsStore } from '@/stores/modules/options';
import UserAdd from '@/views/oj/user/account/components/UserAdd.vue';
import UserEdit from '@/views/oj/user/account/components/UserEdit.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IOJUser } from '@/api/interface/oj/user/user';
import type { IRole } from '@/api/interface/system/admin/role';
import { reactive, ref } from 'vue';
import SvgIcon from '@/components/Common/SvgIcon/index.vue';
import UserGroupForm from '@/views/oj/user/account/components/UserGroupForm.vue';
import UserDeptForm from '@/views/system/admin/accountManage/components/UserDeptForm.vue';
import UserPermissions from '@/views/system/admin/accountManage/components/UserPermissions.vue';

import { ElMessage, ElMessageBox } from 'element-plus';
import UserDataPermissions from '@/views/system/admin/accountManage/components/UserDataPermissions.vue';

import { IS_PREVIEW } from '@/config';
import { UserQueryType } from '@/enums/oj/system/UserQueryType';
import GroupTable from '@/components/Oj/Group/GroupTable.vue';
import TagTable from '@/components/Common/Meta/Tag/TagTable.vue';
import UserCart from '@/components/Oj/User/UserCard.vue';

const optionsStore = useOptionsStore();

// 监听ProTable的reset事件，确保分页被重置
const handleTableReset = () => {
  // 1. 重置分页大小为10（默认值）
  if (proTableRef.value?.pageable) {
    proTableRef.value.pageable.pageSize = 10;
  }

  // 2. 根据当前激活的标签页，清空相应的选择
  switch (activeTab.value) {
    case UserQueryType.GROUP.code:
      // 清空班级选择
      initParam.groupIds = [];
      groupTableRef.value?.clearSelection();
      break;
    case UserQueryType.TAG.code:
      // 清空标签选择
      initParam.tagIds = [];
      tagTableRef.value?.clearSelection();
      break;
    case UserQueryType.DEPT.code:
      // 重置部门树选择
      initParam.deptId = -1;
      break;
  }

  console.log('表格搜索条件和分页已重置');
};

// 表格配置项
const columns: ColumnProps<IRole.Info>[] = [
  { type: 'selection', width: 55, selectable: row => row.id !== 1 },
  { prop: 'username', label: '账户', width: 150, align: 'left' },
  { prop: 'name', label: '真实姓名', width: 150, align: 'left' },
  { prop: 'nickname', label: '昵称', width: 150, align: 'left' },
  { prop: 'phone', label: '手机号', width: 120 },
  {
    prop: 'deptInfo',
    label: '部门'
  },
  {
    prop: 'roleInfo',
    label: '角色'
  },
  {
    prop: 'accountStatusCd',
    label: '状态',
    tag: true,
    enum: optionsStore.getDictOptions('account_status'),
    width: 80,
    fieldNames: { label: 'codeName', value: 'id', tagType: 'callbackShowStyle' }
  },
  { prop: 'createTime', label: '创建时间', width: 165 },
  { prop: 'operation', label: '操作', width: 260, fixed: 'right' }
];

const searchColumns: SearchProps[] = [
  { prop: 'username', label: '账户', el: 'input' },
  { prop: 'name', label: '真实姓名', el: 'input' },
  { prop: 'phone', label: '手机号', el: 'input' },
  {
    prop: 'isThisDeep',
    label: '直属部门',
    el: 'checkbox',
    defaultValue: true,
    tooltip: '直属部门只展示当前节点直接下级用户，不包含其后代'
  }
];

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTableRef = ref<ProTableInstance>();

// 获取table列表
const getTableList = (params: IOJUser.Query) => {
  // 确保userQueryType参数正确传递
  const queryParams = {
    ...params,
    userQueryType: String(initParam.userQueryType)
  };
  return getOJUserList(queryParams);
};

const userAddRef = ref<InstanceType<typeof UserAdd>>();
const openUserAdd = (title: string) => {
  const params: View.DefaultParams = {
    title,
    row: {},
    api: addOJUser,
    getTableList: proTableRef.value?.getTableList
  };
  userAddRef.value?.acceptParams(params);
};

const userEditRef = ref<InstanceType<typeof UserEdit>>();
const openUserEdit = async (title: string, row: any = {}) => {
  const { data } = await getOJUserDetail({ id: row.id });

  const params: View.DefaultParams = {
    title,
    row: data,
    api: editOJUser,
    getTableList: proTableRef.value?.getTableList
  };
  userEditRef.value?.acceptParams(params);
};
const userPermissionsRef = ref<InstanceType<typeof UserPermissions>>();
const openUserPermissions = (title: string, row = {}) => {
  const params = {
    title,
    row: row,
    api: setUserRole,
    getTableList: proTableRef.value?.getTableList
  };
  userPermissionsRef.value?.acceptParams(params);
  proTableRef.value?.getTableList();
};
const deptTreeRef = ref<InstanceType<typeof DeptTree>>();


// 删除信息
const deleteInfo = async (params: IOJUser.Info) => {
  if (IS_PREVIEW) {
    return ElMessage.warning({ message: '预览环境，禁止删除，请谅解！' });
  }
  await useHandleData(deleteOJUser, { ids: [params.id] }, `删除【${params.username}】用户`);
  proTableRef.value?.getTableList();
};

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(deleteOJUser, { ids }, '删除所选用户信息');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
  refreshDeptTree();
};

const userDeptFormRef = ref<InstanceType<typeof UserDeptForm>>();
const settingDept = (record: any) => {
  if (record.selectedListIds) {
    const params = {
      title: '批量设置部门',
      row: {},
      api: undefined,
      getTableList: proTableRef.value?.getTableList,
      selectedList: record.selectedList, //选中的行信息
      deptId: selectTreeId.value,
      selectIds: record.selectedListIds,
      isBatch: true
    };
    userDeptFormRef.value?.acceptParams(params);
  } else {
    const params = {
      title: '设置部门',
      row: {},
      api: undefined,
      getTableList: proTableRef.value?.getTableList,
      selectedList: [record], //选中的行信息
      deptId: record.deptIds.split(',').map(Number),
      selectIds: [record.id],
      isBatch: false
    };
    userDeptFormRef.value?.acceptParams(params);
  }
};

const unlock = async (id: (string | number)[]) => {
  if (Array.isArray(id)) {
    await unlockUser({ ids: id });
  } else {
    await unlockUser({ ids: [id] });
  }
  ElMessage.success(`解锁成功！`);
};

const resetPwd = async (row: any) => {
  const param = { id: row?.id };
  ElMessageBox.confirm('您确认要重置账户 [' + row.username + '] 密码?', '温馨提示', {
    type: 'warning'
  }).then(async () => {
    await resetPassword(param);
    proTableRef.value?.getTableList();
    ElMessage.success({
      message: `账户 [` + row.username + `] 重置密码成功！`,
      duration: 5000,
      showClose: true
    });
  });
};

const initParam = reactive({
  groupIds: [],
  tagIds: [],
  deptId: -1,
  userQueryType: UserQueryType.GROUP.code
});
const selectTreeId = ref<number[]>([]);
const changeDeptTree = (val: number) => {
  if (val) {
    selectTreeId.value = [val];
    initParam.deptId = val;
    initParam.userQueryType = UserQueryType.DEPT.code;
    proTableRef.value?.clearSelection();
  }
};

const formatInfo = (info: string): { id: string; name: string }[] => {
  if (info===null) {
    return [];
  }
  let departments: { id: string; name: string }[] = [];
  // 使用逗号分割字符串
  let departmentArray = info.split(',');
  // 遍历每个部门的键值对
  departmentArray.forEach(function (department: string) {
    // 使用冒号分割键值对
    let keyValue = department.split(':');
    // 构造部门对象
    let departmentObj = {
      id: keyValue[0],
      name: keyValue[1]
    };
    // 添加到数组
    departments.push(departmentObj);
  });
  return departments;
};

const refreshDeptTree = () => {
  deptTreeRef.value?.refresh();
};

// 设置数据角色
const userDataPermissionsRef = ref<InstanceType<typeof UserDataPermissions>>();
const openUserDataPermissions = (title: string, row = {}) => {
  const params = {
    title,
    row: row,
    api: setUserDataRole,
    getTableList: proTableRef.value?.getTableList
  };
  userDataPermissionsRef.value?.acceptParams(params);
  proTableRef.value?.getTableList();
};

// 新增：tab切换逻辑
const activeTab = ref(UserQueryType.GROUP.code);

const handleTabClick = (tab: any) => {
  const tabName = tab.name;
  // 避免重复查询
  if (activeTab.value === tabName) return;

  // 清空各个选项卡的选中状态
  switch (activeTab.value) {
    case UserQueryType.GROUP.code:
      // 清空班级选择
      initParam.groupIds = [];
      groupTableRef.value?.clearSelection();
      break;
    case UserQueryType.TAG.code:
      // 清空标签选择
      initParam.tagIds = [];
      tagTableRef.value?.clearSelection();
      break;
    case UserQueryType.DEPT.code:
      // 重置部门树选择
      initParam.deptId = -1;
      break;
  }

  // 然后设置新的查询类型
  switch (tabName) {
    case UserQueryType.DEPT.code:
      initParam.userQueryType = UserQueryType.DEPT.code;
      proTableRef.value?.getTableList();
      break;
    case UserQueryType.GROUP.code:
      initParam.userQueryType = UserQueryType.GROUP.code;
      proTableRef.value?.getTableList();
      break;
    case UserQueryType.TAG.code:
      initParam.userQueryType = UserQueryType.TAG.code;
      proTableRef.value?.getTableList();
      break;
  }
};

// 新增：处理班级和标签变化的函数
const handleGroupChange = (value: number[]) => {
  if (activeTab.value === UserQueryType.GROUP.code) {
    // 只在值实际变化时才设置userQueryType，避免触发额外的请求
    initParam.userQueryType = UserQueryType.GROUP.code;

    // 不手动触发查询，让ProTable的watch来处理
    // ProTable已经监听了initParam的变化，所以这里不需要手动调用getTableList
  }
};

const handleTagChange = (value: number[]) => {
  if (activeTab.value === UserQueryType.TAG.code) {
    // 只在值实际变化时才设置userQueryType，避免触发额外的请求
    initParam.userQueryType = UserQueryType.TAG.code;

    // 不手动触发查询，让ProTable的watch来处理
    // ProTable已经监听了initParam的变化，所以这里不需要手动调用getTableList
  }
};

// 新增：GroupTable和TagTable的引用
const groupTableRef = ref<InstanceType<typeof GroupTable>>();
const tagTableRef = ref<InstanceType<typeof TagTable>>();

// 设置班级
const userGroupFormRef = ref<InstanceType<typeof UserGroupForm>>();
const settingGroup = async (record: any) => {
  if (record.selectedListIds) {
    // 批量模式
    const params = {
      title: '批量设置班级',
      row: {},
      api: undefined,
      getTableList: proTableRef.value?.getTableList,
      selectedList: record.selectedList,
      selectIds: record.selectedListIds,
      isBatch: true
    };
    userGroupFormRef.value?.acceptParams(params);
  } else {
    // 单个用户模式
    try {
      // 获取用户当前班级信息
      const res = await getUserBindInfo<IOJUser.UserBindInfo>({ userId: record.id });
      let groupIds: number[] = [];

      if (res?.data && res.data.groups && res.data.groups.length > 0) {
        groupIds = res.data.groups.map((group: { id: number }) => group.id);
      }

      const params = {
        title: '设置班级',
        row: {},
        api: undefined,
        getTableList: proTableRef.value?.getTableList,
        selectedList: [record],
        groupIds: groupIds,
        selectIds: [record.id],
        isBatch: false
      };
      userGroupFormRef.value?.acceptParams(params);
    } catch (error) {
      console.error('获取用户班级信息失败:', error);
      ElMessage.error('获取用户班级信息失败');
    }
  }
};

// 用户详情
const userDetailVisible = ref(false);
const currentUserDetail = ref<IOJUser.Info>();
const currentUserBindInfo = ref<IOJUser.UserBindInfo>();

// 显示用户详情
const showUserDetail = async (userId: number) => {
  try {
    const res = await getUserBindInfo<IOJUser.UserBindInfo>({ userId });
    if (res?.data && res.data.ojUserInfo) {
      currentUserDetail.value = res.data.ojUserInfo;
      currentUserBindInfo.value = res.data;
      userDetailVisible.value = true;
    } else {
      ElMessage.warning('用户详情数据不完整');
    }
  } catch (error) {
    console.error('获取用户详情失败:', error);
    ElMessage.error('获取用户详情失败');
  }
};
</script>

<style lang="scss" scoped>
.main-box {
  background-color: var(--el-bg-color-page);
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

.apple-tabs {
  padding: 0 16px;
  margin-top: 16px;

  :deep(.el-tabs__header) {
    margin-bottom: 16px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background-color: var(--el-border-color-light);
  }

  :deep(.el-tabs__item) {
    font-size: 15px;
    font-weight: 500;
    height: 40px;
    line-height: 40px;
    transition: all 0.3s;

    &.is-active {
      font-weight: 600;
      color: var(--el-color-primary);
    }
  }

  :deep(.el-tabs__active-bar) {
    height: 3px;
    border-radius: 3px;
    background-color: var(--el-color-primary);
  }
}

.table-box {
  padding: 16px;

  .apple-table {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    :deep(.el-table) {
      &::before {
        display: none;
      }

      .el-table__header {
        th {
          background-color: var(--el-fill-color-light);
          font-weight: 600;
          color: var(--el-text-color-primary);
        }
      }

      .el-table__row {
        transition: all 0.2s;

        &:hover {
          background-color: var(--el-color-primary-light-9);
        }

        td {
          padding: 12px 0;
        }
      }
    }

    :deep(.pro-table-header) {
      padding: 16px 20px;
      border-bottom: 1px solid var(--el-border-color-light);
    }

    :deep(.search-form) {
      .el-form-item__label {
        font-weight: 500;
      }

      .el-input__wrapper {
        border-radius: 8px;
        box-shadow: 0 0 0 1px var(--el-border-color);

        &:hover,
        &:focus-within {
          box-shadow: 0 0 0 1px var(--el-color-primary);
        }
      }
    }
  }
}

.table-header-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.apple-button {
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s;

  &.el-button--primary:not(.is-plain) {
    background-color: var(--el-color-primary);
    border-color: var(--el-color-primary);

    &:hover {
      background-color: var(--el-color-primary-light-3);
      border-color: var(--el-color-primary-light-3);
    }
  }

  &.is-plain {
    &:hover {
      color: var(--el-color-primary);
      border-color: var(--el-color-primary-light-5);
      background-color: var(--el-color-primary-light-9);
    }
  }

  &.el-button--danger:not(.is-plain) {
    background-color: var(--el-color-danger);
    border-color: var(--el-color-danger);

    &:hover {
      background-color: var(--el-color-danger-light-3);
      border-color: var(--el-color-danger-light-3);
    }
  }
}

.user-tag {
  margin: 4px;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 12px;
}

.btn-group {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.el-button + .el-dropdown {
  margin-left: 0;
}

.apple-dropdown-menu {
  :deep(.el-dropdown-menu__item) {
    padding: 8px 16px;
    font-size: 14px;
    line-height: 1.5;

    &:hover {
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }
  }
}

.user-detail-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
}

.user-bind-info {
  margin-top: 24px;
  width: 100%;
  background-color: var(--el-fill-color-light);
  border-radius: 12px;
  padding: 16px;
}

.bind-section {
  margin-bottom: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}

.bind-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
  position: relative;
  padding-left: 12px;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 16px;
    background-color: var(--el-color-primary);
    border-radius: 3px;
  }
}

.bind-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.group-tag {
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 13px;
}

.tag-item {
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 13px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--el-border-color-lighter);
}

.user-avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--el-fill-color);
  color: var(--el-text-color-placeholder);
  font-size: 12px;
}

.user-link {
  font-weight: 500;

  &:hover {
    text-decoration: underline;
  }
}

.apple-style-dialog {
  :deep(.el-dialog__header) {
    margin: 0;
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }

  :deep(.el-dialog__title) {
    font-weight: 600;
    font-size: 16px;
  }

  :deep(.el-dialog__headerbtn) {
    top: 16px;
  }
}
</style>
