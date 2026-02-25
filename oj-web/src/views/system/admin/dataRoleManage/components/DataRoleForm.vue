<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    width="800px"
  >
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="paramsProps.row.roleName" placeholder="请填写角色名称" clearable :disabled="isLock" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="数据权限" prop="dataScopeCd">
            <el-select
              v-model="paramsProps.row.dataScopeCd"
              clearable
              placeholder="请选择数据权限"
              @change="changeDataScope"
              :disabled="isLock"
            >
              <el-option
                v-for="item in optionsStore.getDictOptions('data_scope')"
                :key="item.id"
                :label="item.codeName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label="用户维度" prop="userOptions" v-if="isCustom">
            <el-select v-model="paramsProps.row.userOptions" clearable multiple filterable placeholder="请选择用户">
              <el-option v-for="item in userOptions" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="isCustom">
        <el-col :span="11" style="margin-left: 30px">
          <el-card shadow="never">
            <template #header>
              <div class="card-header min-header">
                <el-space :size="4">
                  <span>菜单列表</span>
                  <el-tooltip effect="dark" content="展示[菜单管理]中属性[数据权限支持]选中状态的记录" placement="top">
                    <i :class="'iconfont icon-yiwen'" />
                  </el-tooltip>
                </el-space>
              </div>
            </template>
            <div class="tree-container">
              <el-tree
                :data="menuLists"
                show-checkbox
                ref="menuTreeRef"
                node-key="id"
                :props="menuTreeProps"
                empty-text="加载中，请稍候"
                :default-expand-all="isMenuExpand"
                :check-strictly="!isMenuCheckStrictly"
              />
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never" style="margin-left: 25px">
            <template #header>
              <div class="card-header min-header">
                <span>部门维度</span>
                <div>
                  <el-checkbox v-model="isDeptExpand" @change="changeDeptExpand"> 展开/折叠 </el-checkbox>
                  <el-checkbox v-model="isDeptCheckStrictly"> 父子联动 </el-checkbox>
                </div>
              </div>
            </template>
            <div class="tree-container">
              <div>
                <el-tree
                  :data="deptLists"
                  show-checkbox
                  ref="deptTreeRef"
                  node-key="id"
                  :props="deptTreeProps"
                  empty-text="加载中，请稍候"
                  :default-expand-all="isDeptExpand"
                  :check-strictly="!isDeptCheckStrictly"
                />
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row v-else>
        <el-col>
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <el-space :size="4">
                  <span>菜单列表</span>
                  <el-tooltip effect="dark" content="展示[菜单管理]中属性[数据权限支持]选中状态的记录" placement="top">
                    <i :class="'iconfont icon-yiwen'" />
                  </el-tooltip>
                </el-space>
              </div>
            </template>
            <div class="tree-container">
              <el-tree
                :data="menuLists"
                show-checkbox
                ref="menuTreeRef"
                node-key="id"
                :props="menuTreeProps"
                empty-text="加载中，请稍候"
                :default-expand-all="isMenuExpand"
                :check-strictly="!isMenuCheckStrictly"
              />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue';
import { type CheckboxValueType, ElForm, ElMessage } from 'element-plus';
import { useOptionsStore } from '@/stores/modules/options';
import type { IRole } from '@/api/interface/system/admin/role';
import type { ISysDept } from '@/api/interface/system/admin/dept';
import type { IUser } from '@/api/interface/system/admin/user';

defineOptions({
  name: 'SysDataRoleForm'
});
const optionsStore = useOptionsStore();
// 自定义数据权限
const isCustom = ref(false);
const isLock = ref(false);
const userOptions = ref<IUser.Options[]>([]);
// ------ 菜单树形
const menuLists = ref<IRole.MenuTree[]>([]);
const selectMenuIds = ref<string[]>([]);
const menuTreeRef = ref();
const menuTreeProps = {
  children: 'children',
  label: 'title',
  value: 'id'
};
const isMenuExpand = ref(true);
const isMenuCheckStrictly = ref(true);

// ------ 部门树形
const deptLists = ref<ISysDept.Tree[]>([]);
const selectDeptIds = ref<string[]>([]);
const deptTreeRef = ref();
const deptTreeProps = {
  label: 'name',
  value: 'id'
};
const isDeptExpand = ref(true);
const isDeptCheckStrictly = ref(true);

const changeDataScope = (value: number) => {
  isCustom.value = value == 1006005;
  paramsProps.value.row.userOptions = [];
};
const rules = reactive({
  roleName: [{ required: true, message: '请填写角色名称' }],
  dataScope: [{ required: true, message: '请选择数据权限' }]
});

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
  visible.value = true;
  menuLists.value = params.meta.menuLists;
  deptLists.value = params.meta.deptLists;
  userOptions.value = params.meta.userOptions;
  if (paramsProps.value.row.dataScopeCd) {
    isCustom.value = paramsProps.value.row.dataScopeCd == 1006005;
  } else {
    isCustom.value = false;
  }
  if (paramsProps.value.row.isLock) {
    isLock.value = paramsProps.value.row.isLock == 'T';
  } else {
    isLock.value = false;
  }
  if (paramsProps.value.row.selectMenuIds && paramsProps.value.row.selectMenuIds.length > 0) {
    selectMenuIds.value = paramsProps.value.row.selectMenuIds;
    nextTick(() => {
      selectMenuIds.value.forEach(item => {
        const node = menuTreeRef.value!.getNode(item);
        menuTreeRef.value!.setChecked(node, true, false);
      });
    });
  }

  if (isCustom.value && paramsProps.value.row.selectDeptIds && paramsProps.value.row.selectDeptIds.length > 0) {
    selectDeptIds.value = paramsProps.value.row.selectDeptIds;
    nextTick(() => {
      selectDeptIds.value.forEach(item => {
        const node = deptTreeRef.value!.getNode(item);
        deptTreeRef.value!.setChecked(node, true, false);
      });
    });
  }
};

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    if (isLock.value) {
      ElMessage.warning('当前表单已锁定，禁止修改！');
      return;
    }
    try {
      const selectMenuKeys = menuTreeRef.value!.getCheckedKeys();
      let selectDeptKeys = [];
      if (selectMenuKeys.length < 1) {
        ElMessage.error('请选择有效的数据。菜单列表不能为空');
        return;
      }
      if (isCustom.value) {
        selectDeptKeys = deptTreeRef.value!.getCheckedKeys();
        if (selectDeptKeys.length < 1 && paramsProps.value.row?.userOptions.length < 1) {
          ElMessage.error('请选择有效的数据。用户纬度和部门纬度至少有一个不能为空');
          return;
        }
      }
      paramsProps.value.row.selectMenuIds = selectMenuKeys;
      paramsProps.value.row.selectDeptIds = selectDeptKeys;
      await paramsProps.value.api!(paramsProps.value.row);
      ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      paramsProps.value.getTableList!();
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
};
const changeDeptExpand = (value: CheckboxValueType) => {
  for (let i in deptTreeRef.value!.store.nodesMap) {
    deptTreeRef.value!.store.nodesMap[i].expanded = value;
  }
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss">
.tree-container {
  height: 300px;
  overflow-y: scroll;
}

.min-header {
  min-height: 50px;
}
</style>
