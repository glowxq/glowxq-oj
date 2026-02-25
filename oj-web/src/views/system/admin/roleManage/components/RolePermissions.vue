<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}-${paramsProps.row?.roleName}`"
    :destroy-on-close="true"
    width="600px"
    @close="reset"
    append-to-body
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <el-form label-width="100px" label-suffix=" :" @submit.enter.prevent="handleSubmit">
      <el-form-item>
        <el-checkbox v-model="isExpand" @change="changeExpand"> 展开/折叠 </el-checkbox>
        <el-checkbox v-model="isNodeAll" @change="changeNodeAll"> 全选/全不选 </el-checkbox>
        <el-checkbox v-model="isCheckStrictly"> 父子联动 </el-checkbox>
        <el-checkbox v-model="isShowPermissions"> 权限标识 </el-checkbox>
      </el-form-item>
      <el-form-item label="权限">
        <el-tree
          :data="menuLists"
          show-checkbox
          ref="treeRef"
          node-key="id"
          :default-expand-all="isExpand"
          :check-strictly="!isCheckStrictly"
          :props="treeProps"
          empty-text="加载中，请稍候"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
              <el-tag v-if="data.permissions && isShowPermissions" style="margin-left: 10px" size="small">
                {{ data.permissions }}
              </el-tag>
            </span>
          </template>
        </el-tree>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { getRoleMenus } from '@/api/modules/system/admin/role';
import type { IRole } from '@/api/interface/system/admin/role';
import { nextTick, ref } from 'vue';
import { type CheckboxValueType, ElMessage } from 'element-plus';
import { IS_PREVIEW } from '@/config';

defineOptions({
  name: 'RolePermissions'
});
const treeProps = {
  children: 'children',
  label: 'title',
  value: 'id'
};
const isExpand = ref(true);
const isNodeAll = ref(false);
const isCheckStrictly = ref(true);
const isShowPermissions = ref(false);

const treeRef = ref();

/**
 * 全部展开/折叠
 * @param value
 */
const changeExpand = (value: CheckboxValueType) => {
  for (let i in treeRef.value!.store.nodesMap) {
    treeRef.value!.store.nodesMap[i].expanded = value;
  }
};

/**
 * 全选
 * @param value
 */
const changeNodeAll = (value: CheckboxValueType) => {
  if (value) {
    treeRef.value!.setCheckedNodes(menuLists.value as any);
  } else {
    treeRef.value!.setCheckedNodes([]);
  }
};

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined
});

const reset = () => {
  isExpand.value = true;
  isNodeAll.value = false;
  isCheckStrictly.value = true;
};

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
  visible.value = true;
  getInfo(params.row.id);
};

const menuLists = ref<IRole.MenuTree[]>([]);
const selectIds = ref<string[]>([]);

/**
 * 获取权限信息
 * @param roleId
 */
const getInfo = (roleId: number) => {
  getRoleMenus({ roleId }).then(res => {
    menuLists.value = res.data.menuLists;
    selectIds.value = res.data.selectIds;
    nextTick(() => {
      selectIds.value.forEach(item => {
        const node = treeRef.value!.getNode(item);
        if (node) {
          treeRef.value!.setChecked(node, true, false);
        }
      });
    });
  });
};

const handleSubmit = async () => {
  try {
    const checkedKeys = treeRef.value!.getCheckedKeys();
    const halfCheckedKeys = treeRef.value!.getHalfCheckedKeys();
    if (paramsProps.value.row.id === 1 && IS_PREVIEW) {
      return ElMessage.warning({ message: '预览环境，禁止修改超级管理员权限，请谅解！' });
    }
    await paramsProps.value.api!({
      roleId: paramsProps.value.row.id,
      menuIds: [...checkedKeys, ...halfCheckedKeys]
    });
    ElMessage.success({ message: `${paramsProps.value.title}成功！` });
    visible.value = false;
  } catch (error) {
    console.log(error);
  }
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss"></style>
