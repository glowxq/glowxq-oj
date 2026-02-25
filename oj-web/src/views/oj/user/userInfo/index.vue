<template>
  <div class="main-box">
    <el-tabs v-model="activeTab" class="apple-tabs" @tab-click="handleTabClick">
      <el-tab-pane name="group" label="按班级筛选">
        <GroupTable ref="groupTableRef" v-model="initParam.groupIds" @change="handleGroupChange" />
      </el-tab-pane>
      <el-tab-pane name="dept" label="按部门筛选">
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
        title="用户信息"
        :indent="20"
        :columns="columns"
        :search-columns="searchColumns"
        :request-api="getTableList"
        :init-param="initParam"
        row-key="id"
        @reset="handleTableReset"
      >
        <!-- 表格 header 按钮 -->
        <template #tableHeader="scope">
          <el-button type="primary"
            v-auth="'user.info.create'"
            :icon="CirclePlus"
            @click="openAddEdit('新增用户信息')"
          >
            新增
          </el-button>
          <el-button
            v-auth="'user.info.remove'"
            type="danger"
            :icon="Delete"
            plain
            :disabled="!scope.isSelected"
            @click="batchDelete(scope.selectedListIds)"
          >
            批量删除
          </el-button>
          <el-button
            v-auth="'user.info.unlock'"
            type="info"
            :icon="Unlock"
            plain
            :disabled="!scope.isSelected"
            @click="batchUnlock(scope.selectedListIds)"
          >
            批量解锁
          </el-button>
          <el-button
            v-auth="'user.info.dept.setting'"
            type="warning"
            :icon="OfficeBuilding"
            plain
            :disabled="!scope.isSelected"
            @click="settingDept(scope)"
          >
            设置部门
          </el-button>
          <el-button
            v-auth="'user.info.group.bind'"
            type="success"
            :icon="Avatar"
            plain
            :disabled="!scope.isSelected"
            @click="settingGroup(scope)"
          >
            绑定班级
          </el-button>
          <el-button
            v-auth="'user.info.import'"
            type="primary"
            :icon="Upload"
            plain
            @click="importData"
          >
            导入
          </el-button>
          <el-button
            v-auth="'user.info.export'"
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
            v-auth="'user.info.detail'"
            type="primary"
            link
            :icon="View"
            @click="openDetail(row)"
          >
            详情
          </el-button>
          <el-button
            v-auth="'user.info.update'"
            type="primary"
            link
            :icon="EditPen"
            @click="openAddEdit('编辑用户信息', row, false)"
          >
            编辑
          </el-button>
          <el-button
            v-auth="'user.info.unlock'"
            type="primary"
            :icon="Unlock"
            link
            @click="row.userId && unlock([row.userId])"
          >
            解锁
          </el-button>
          <div class="el-dropdown">
            <el-dropdown trigger="click">
              <el-button type="primary" link :icon="DArrowRight"> 更多 </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <div v-auth="'user.info.reset.password'">
                    <el-dropdown-item :icon="Refresh" @click="resetPwd(row)"> 重置密码 </el-dropdown-item>
                  </div>
                  <div v-auth="'user.info.dept.setting'">
                    <el-dropdown-item :icon="OfficeBuilding" @click="settingDept(row)"> 设置部门 </el-dropdown-item>
                  </div>
                  <div v-auth="'user.info.group.bind'">
                    <el-dropdown-item :icon="Avatar" @click="settingGroup(row)"> 绑定班级 </el-dropdown-item>
                  </div>
                  <div v-auth="'user.info.remove'">
                    <el-dropdown-item :icon="Delete" @click="deleteInfo(row)"> 删除 </el-dropdown-item>
                  </div>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>

        <!-- 头像列 -->
        <template #avatar="{ row }">
          <el-image
            v-if="row.avatar"
            :src="row.avatar"
            style="width: 50px; height: 50px; border-radius: 8px"
            fit="cover"
            :preview-src-list="[row.avatar]"
            preview-teleported
            lazy
          />
          <span v-else class="no-avatar">-</span>
        </template>

        <!-- 相关图片列 -->
        <template #image="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            style="width: 60px; height: 40px; border-radius: 6px"
            fit="cover"
            :preview-src-list="[row.image]"
            preview-teleported
            lazy
          />
          <span v-else class="no-image">-</span>
        </template>

        <!-- 性别列 -->
        <template #sex="{ row }">
          <EnumShow
            :enum="Sex"
            :code="row.sex"
            :background-color="row.sex === 'Male' ? '#e1f3ff' : '#fce8f3'"
            :color="row.sex === 'Male' ? '#409eff' : '#f56c6c'"
            :border-color="row.sex === 'Male' ? '#b3d8ff' : '#fdb9cc'"
            font-size="12px"
            padding="2px 6px"
            border-radius="4px"
          />
        </template>

        <!-- 称号和颜色列 -->
        <template #titleColor="{ row }">
          <UserTitle :title="row.title" :color="row.color" size="small" />
        </template>
      </ProTable>
      <UserInfoForm ref="userInfoRef" />
      <ImportExcel ref="ImportExcelRef" />
      <UserDeptForm ref="userDeptFormRef" @submit="refreshData" />
      <UserInfoGroupForm ref="userInfoGroupFormRef" @submit="refreshData" />
      <UserDetail ref="userDetailRef" @edit="handleEditFromDetail" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  Unlock,
  Refresh,
  DArrowRight,
  OfficeBuilding,
  Avatar,
  View,
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createUserInfoApi,
  removeUserInfoApi,
  updateUserInfoApi,
  getUserInfoListApi,
  getUserInfoDetailApi,
  importUserInfoExcelApi,
  exportUserInfoExcelApi,
} from '@/api/modules/oj/user/userInfo';
import { useHandleData } from '@/hooks/useHandleData';
import UserInfoForm from '@/views/oj/user/userInfo/components/UserInfoForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { IUserInfo } from '@/api/interface/oj/user/userInfo';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessage, ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
import GroupTable from '@/components/Oj/Group/GroupTable.vue';
import DeptTree from '@/views/system/admin/accountManage/components/DeptTree.vue';
import { getUserDeptTree, resetPassword, unlockUser } from '@/api/modules/system/admin/user';
import UserDeptForm from '@/views/system/admin/accountManage/components/UserDeptForm.vue';
import UserInfoGroupForm from '@/views/oj/user/userInfo/components/UserInfoGroupForm.vue';
import UserDetail from '@/views/oj/user/userInfo/components/UserDetail.vue';
import UserTitle from '@/components/Oj/User/UserTitle.vue';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import { Sex } from '@/enums/common/Sex';

defineOptions({
  name: 'UserInfoView'
})

const proTableRef = ref<ProTableInstance>();
const groupTableRef = ref<InstanceType<typeof GroupTable>>();
const deptTreeRef = ref<InstanceType<typeof DeptTree>>();
const userDeptFormRef = ref<InstanceType<typeof UserDeptForm>>();
const userInfoGroupFormRef = ref<InstanceType<typeof UserInfoGroupForm>>();
const userDetailRef = ref<InstanceType<typeof UserDetail>>();

// 筛选参数
const initParam = reactive({
  groupIds: [] as number[],
  deptId: undefined as number | undefined
})

// 当前激活的标签页
const activeTab = ref('group')

// 处理标签页切换
const handleTabClick = (tab: any) => {
  const tabName = tab.name;

  // 避免重复处理
  if (activeTab.value === tabName) return;

  // 切换标签页时清空其他筛选条件
  switch (activeTab.value) {
    case 'group':
      initParam.groupIds = [];
      groupTableRef.value?.clearSelection();
      break;
    case 'dept':
      initParam.deptId = undefined;
      break;
  }

  activeTab.value = tabName;
}

// 处理表格重置
const handleTableReset = () => {
  // 重置分页
  if (proTableRef.value?.pageable) {
    proTableRef.value.pageable.pageSize = 10;
  }

  // 根据当前标签页清空相应选择
  switch (activeTab.value) {
    case 'group':
      initParam.groupIds = [];
      groupTableRef.value?.clearSelection();
      break;
    case 'dept':
      initParam.deptId = undefined;
      break;
  }
}

// 处理班级选择变化
const handleGroupChange = (value: number[]) => {
  if (activeTab.value === 'group') {
    console.log('班级选择变化:', value);
  }
}

// 处理部门树选择变化
const changeDeptTree = (val: number) => {
  if (val) {
    initParam.deptId = val;
    proTableRef.value?.clearSelection();
  }
}

// 表格配置项
const columns: ColumnProps<IUserInfo.Row>[] = [
  { type: 'selection', width: 60 },
  { prop: 'userId', label: 'ID' ,width: 60},
  { prop: 'name', label: '姓名' },
  { prop: 'nickName', label: '昵称' },
  { prop: 'phone', label: '手机号' ,width: 120},
  { prop: 'avatar', label: '头像', width: 80, render: (scope: any) => scope.row.avatar || '-' },
  { prop: 'sex', label: '性别', width: 60, render: (scope: any) => scope.row.sex || '-' },
  { prop: 'birthday', label: '生日' ,width: 120},
  { prop: 'image', label: '相关图片', width: 100, render: (scope: any) => scope.row.image || '-' },
  { prop: 'acNum', label: 'AC' ,width: 60},
  { prop: 'integral', label: '积分' ,width: 60},
  { prop: 'continuousSignDay', label: '连续签到' ,width: 100},
  { prop: 'submitNum', label: '提交题目' ,width: 100},
  { prop: 'titleColor', label: '称号', width: 120, render: (scope: any) => scope.row.titleColor || '-' },
  { prop: 'expirationTime', label: '过期时间', width: 120},
  { prop: 'lastLoginTime', label: '最后登录', width: 120},
  { prop: 'remark', label: '备注' ,width: 120},
  { prop: 'operation', label: '操作', width: 200, fixed: 'right' }
]

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'name', label: '姓名', el: 'input' },
  { prop: 'nickName', label: '昵称', el: 'input' },
  { prop: 'phone', label: '手机号', el: 'input' },
  { prop: 'expirationTime',
    label: '帐号过期时间',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
  { prop: 'lastLoginTime',
    label: '最后登录时间',
    el: 'date-picker',
    span: 2,
    props: {
      type: "datetimerange",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
  },
]

// 获取table列表
const getTableList = (params: IUserInfo.Query) => {
  let newParams = formatParams(params);

  // 根据当前标签页决定传递哪个筛选参数
  if (activeTab.value === 'group' && initParam.groupIds.length > 0) {
    newParams.groupIds = initParam.groupIds;
  } else if (activeTab.value === 'dept' && initParam.deptId) {
    newParams.deptId = initParam.deptId;
  }

  return getUserInfoListApi(newParams);
};

const formatParams = (params: IUserInfo.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.birthday && (newParams.birthdayStart = newParams.birthday[0]);
  newParams.birthday && (newParams.birthdayEnd = newParams.birthday[1]);
  delete newParams.birthday;
  newParams.expirationTime && (newParams.expirationTimeStart = newParams.expirationTime[0]);
  newParams.expirationTime && (newParams.expirationTimeEnd = newParams.expirationTime[1]);
  delete newParams.expirationTime;
  newParams.lastLoginTime && (newParams.lastLoginTimeStart = newParams.lastLoginTime[0]);
  newParams.lastLoginTime && (newParams.lastLoginTimeEnd = newParams.lastLoginTime[1]);
  delete newParams.lastLoginTime;
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}

// 打开 drawer(新增、查看、编辑)
const userInfoRef = ref<InstanceType<typeof UserInfoForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getUserInfoDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createUserInfoApi : updateUserInfoApi,
    getTableList: proTableRef.value?.getTableList
  }
  userInfoRef.value?.acceptParams(params)
}

// 删除信息
const deleteInfo = async (params: IUserInfo.Row) => {
  await useHandleData(
    removeUserInfoApi,
    { ids: [params.id] },
    `删除【${params.id}】用户信息`
  )
  proTableRef.value?.getTableList()
}

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeUserInfoApi, { ids }, '删除所选用户信息')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}

// 解锁用户（单个或批量）
const unlock = async (ids: (string | number)[]) => {
  try {
    await unlockUser({ ids });
    ElMessage.success(`解锁成功！`);
    proTableRef.value?.getTableList();
  } catch (error) {
    console.error('解锁失败:', error);
    ElMessage.error('解锁失败，请重试');
  }
}

// 批量解锁
const batchUnlock = async (ids: (string | number)[]) => {
  await unlock(ids);
  proTableRef.value?.clearSelection();
}

// 重置密码
const resetPwd = async (row: IUserInfo.Row) => {
  if (!row.userId) {
    ElMessage.warning('用户ID不存在，无法重置密码');
    return;
  }

  const param = { id: row.userId };
  ElMessageBox.confirm('您确认要重置用户 [' + (row.name || row.nickName) + '] 的密码?', '温馨提示', {
    type: 'warning'
  }).then(async () => {
    await resetPassword(param);
    proTableRef.value?.getTableList();
    ElMessage.success({
      message: `用户 [` + (row.name || row.nickName) + `] 重置密码成功！`,
      duration: 5000,
      showClose: true
    });
  });
};

// 批量设置部门
const settingDept = (record: any) => {
  if (record.selectedListIds) {
    // 批量设置
    const params = {
      title: '批量设置部门',
      row: {},
      api: undefined,
      getTableList: proTableRef.value?.getTableList,
      selectedList: record.selectedList,
      deptId: [],
      selectIds: record.selectedListIds,
      isBatch: true
    };
    userDeptFormRef.value?.acceptParams(params);
  } else {
    // 单个设置
    const params = {
      title: '设置部门',
      row: {},
      api: undefined,
      getTableList: proTableRef.value?.getTableList,
      selectedList: [record],
      deptId: record.deptId ? [record.deptId] : [],
      selectIds: [record.id],
      isBatch: false
    };
    userDeptFormRef.value?.acceptParams(params);
  }
};

// 批量绑定班级
const settingGroup = (record: any) => {
  if (record.selectedListIds) {
    // 批量绑定
    const params = {
      title: '批量绑定班级',
      selectedUsers: record.selectedList || [],
      getTableList: proTableRef.value?.getTableList
    };
    userInfoGroupFormRef.value?.acceptParams(params);
  } else {
    // 单个绑定
    const params = {
      title: '绑定班级',
      selectedUsers: [record],
      getTableList: proTableRef.value?.getTableList
    };
    userInfoGroupFormRef.value?.acceptParams(params);
  }
};

// 刷新数据
const refreshData = () => {
  proTableRef.value?.getTableList();
  proTableRef.value?.clearSelection();
};

// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '用户信息',
    templateName: '用户信息',
    tempApi: downloadTemplate,
    importApi: importUserInfoExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}

// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IUserInfo.Query);

  // 导出时也要考虑筛选条件
  if (activeTab.value === 'group' && initParam.groupIds.length > 0) {
    newParams.groupIds = initParam.groupIds;
  } else if (activeTab.value === 'dept' && initParam.deptId) {
    newParams.deptId = initParam.deptId;
  }

  useDownload(exportUserInfoExcelApi, "用户信息", newParams);
};

// 打开用户详情
const openDetail = (row: IUserInfo.Row) => {
  userDetailRef.value?.openDetail(row);
};

// 从详情页编辑
const handleEditFromDetail = (userInfo: IUserInfo.Row) => {
  openAddEdit('编辑用户信息', userInfo, false);
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

  :deep(.pro-table) {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    .el-table {
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

    .pro-table-header {
      padding: 16px 20px;
      border-bottom: 1px solid var(--el-border-color-light);
    }

    .search-form {
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

// 新增组件样式
.no-avatar,
.no-image {
  color: #c0c4cc;
  font-size: 12px;
  font-style: italic;
}

:deep(.el-image) {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15);
  }

  img {
    object-fit: cover;
  }
}



// 响应式设计 - 平板设备
@media (max-width: 1024px) {
  .apple-tabs {
    padding: 0 12px;
    margin-top: 12px;

    :deep(.el-tabs__item) {
      font-size: 14px;
      height: 36px;
      line-height: 36px;
      padding: 0 12px;
    }
  }

  .table-box {
    padding: 12px;

    :deep(.pro-table) {
      .pro-table-header {
        padding: 12px 16px;
      }

      // 搜索表单优化
      .search-form {
        :deep(.el-form) {
          .el-form-item {
            margin-bottom: 12px;
          }
        }
      }

      // 表格操作按钮优化
      .table-header-buttons {
        flex-wrap: wrap;
        gap: 8px;

        .el-button {
          font-size: 13px;
          padding: 6px 12px;
        }
      }
    }
  }


}

// 响应式设计 - 手机设备
@media (max-width: 768px) {
  .main-box {
    border-radius: 8px;
    margin: 8px;
  }

  .apple-tabs {
    padding: 0 8px;
    margin-top: 8px;

    :deep(.el-tabs__header) {
      margin-bottom: 8px;
    }

    :deep(.el-tabs__item) {
      font-size: 13px;
      height: 32px;
      line-height: 32px;
      padding: 0 8px;
    }

    :deep(.el-tabs__nav-scroll) {
      padding: 0;
    }
  }

  .table-box {
    padding: 8px;

    :deep(.pro-table) {
      border-radius: 8px;

      .pro-table-header {
        padding: 8px 12px;

        .table-title {
          font-size: 16px;
        }
      }

      // 搜索表单移动端优化
      .search-form {
        :deep(.el-form) {
          .el-row {
            margin: 0 !important;
          }

          .el-col {
            padding: 0 4px !important;
            margin-bottom: 8px;

            &.search-operation {
              margin-top: 8px;
            }
          }

          .el-form-item {
            margin-bottom: 8px;

            .el-form-item__label {
              font-size: 13px;
              line-height: 1.2;
              margin-bottom: 4px;
            }

            .el-form-item__content {
              .el-input,
              .el-select,
              .el-date-picker {
                width: 100% !important;

                :deep(.el-input__wrapper) {
                  height: 32px;
                  font-size: 13px;
                }
              }

              .el-date-picker {
                :deep(.el-input__wrapper) {
                  .el-input__inner {
                    font-size: 12px;
                  }
                }
              }
            }
          }

          // 搜索操作按钮
          .search-operation {
            .el-button {
              width: 100%;
              margin-bottom: 6px;
              height: 32px;
              font-size: 13px;

              &:last-child {
                margin-bottom: 0;
              }
            }
          }
        }
      }

      // 表格头部按钮优化
      .table-header-buttons {
        flex-direction: column;
        align-items: stretch;
        gap: 6px;

        .el-button {
          width: 100%;
          height: 32px;
          font-size: 13px;
          padding: 0 12px;
          justify-content: center;
        }
      }

      // 表格内容优化
      .el-table {
        font-size: 12px;

        .el-table__header {
          th {
            padding: 8px 4px;
            font-size: 12px;
          }
        }

        .el-table__row {
          td {
            padding: 8px 4px;
            font-size: 12px;

            // 操作列按钮优化
            &.el-table-fixed-column--right {
              .btn-group {
                flex-direction: column;
                gap: 2px;

                .el-button {
                  font-size: 11px;
                  height: 24px;
                  padding: 0 6px;
                }
              }
            }
          }
        }

        // 隐藏部分列在小屏幕
        .el-table__column--selection {
          width: 40px !important;
        }

        // 优化较宽的列
        .el-table__cell {
          word-break: break-all;
          line-height: 1.2;
        }
      }
    }
  }
}

// 响应式设计 - 小手机设备
@media (max-width: 480px) {
  .main-box {
    margin: 4px;
    border-radius: 6px;
  }

  .apple-tabs {
    padding: 0 6px;
    margin-top: 6px;

    :deep(.el-tabs__item) {
      font-size: 12px;
      height: 28px;
      line-height: 28px;
      padding: 0 6px;
    }
  }

  .table-box {
    padding: 6px;

    :deep(.pro-table) {
      .pro-table-header {
        padding: 6px 8px;
      }

      // 进一步优化搜索表单
      .search-form {
        :deep(.el-form) {
          .el-col {
            padding: 0 2px !important;
          }

          .el-form-item {
            .el-form-item__label {
              font-size: 12px;
            }

            .el-form-item__content {
              .el-input,
              .el-select,
              .el-date-picker {
                :deep(.el-input__wrapper) {
                  height: 28px;
                  font-size: 12px;
                  padding: 0 8px;
                }
              }
            }
          }

          .search-operation {
            .el-button {
              height: 28px;
              font-size: 12px;
            }
          }
        }
      }

      // 表格进一步优化
      .el-table {
        font-size: 11px;

        .el-table__header {
          th {
            padding: 6px 2px;
            font-size: 11px;
          }
        }

        .el-table__row {
          td {
            padding: 6px 2px;
            font-size: 11px;
          }
        }
      }
    }
  }


}

// 横屏模式优化
@media (max-height: 600px) and (orientation: landscape) {
  .table-box {
    :deep(.pro-table) {
      .search-form {
        :deep(.el-form) {
          .el-form-item {
            margin-bottom: 6px;
          }
        }
      }
    }
  }
}
</style>
