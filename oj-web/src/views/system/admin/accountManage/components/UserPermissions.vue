<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}-${paramsProps.row?.username}`"
    :destroy-on-close="true"
    width="650px"
    draggable
    append-to-body
  >
    <el-transfer
      v-model="selectIds"
      filterable
      :filter-method="filterMethod"
      :titles="['未设置角色', '已设置角色']"
      filter-placeholder="筛选角色"
      :props="transferProps"
      :data="roleLists"
    />
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { getUserRole } from '@/api/modules/system/admin/user';
import type { IUser } from '@/api/interface/system/admin/user';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { IS_PREVIEW } from '@/config';

defineOptions({
  name: 'UserPermissions'
});

const transferProps = {
  key: 'id',
  label: 'roleName'
};

/**
 * 过滤角色
 * @param query
 * @param item
 * @returns {boolean}
 */
const filterMethod = (query: string, item: Record<string, any>) => {
  return item.roleName.toLowerCase().includes(query.toLowerCase());
};

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
  getInfo(params.row.id);
};

const roleLists = ref<IUser.RoleInfo[]>([]);
const selectIds = ref<number[]>([]);

/**
 * 获取权限信息
 * @param userId
 */
const getInfo = (userId: number) => {
  getUserRole({ userId }).then(res => {
    roleLists.value = res.data.roleInfoVOS;
    selectIds.value = res.data.selectIds;
  });
};

const handleSubmit = async () => {
  try {
    if (IS_PREVIEW && paramsProps.value.row.id === 1) {
      return ElMessage.warning({ message: '预览环境，禁止修改超级管理员角色，请谅解！' });
    }
    await paramsProps.value.api!({
      userId: paramsProps.value.row.id,
      roleIds: selectIds.value
    }).then(() => paramsProps.value.getTableList!());
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
