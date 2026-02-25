<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="580px" draggable append-to-body>
    <el-alert
      v-if="paramsProps?.isBatch"
      title="警告信息"
      type="warning"
      show-icon
      description="批量功能会忽略用户原部门，以最终提交数据为准！！"
      :closable="false"
      class="el-alert"
    />
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps"
      @submit.enter.prevent="handleSubmit"
      class="form__label"
      style="padding: 10px"
    >
      <el-form-item label="用户" prop="leaderIds">
        <el-tag class="user-item" v-for="tag in leaders" :key="tag.id" type="info">
          {{ tag.nickname }}
        </el-tag>
      </el-form-item>
      <el-form-item label="部门" prop="deptIds">
        <el-tree-select
          v-model="deptIds"
          :data="parentMenus"
          check-strictly
          multiple
          placeholder="请选择部门"
          :render-after-expand="false"
          clearable
          :default-expand-all="true"
          :props="treeProps"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { type ElForm, ElMessage } from 'element-plus';
import type { ISysDept } from '@/api/interface/system/admin/dept';
import { getMenuTree } from '@/api/modules/system/admin/dept';
import { bindUserDeptApi } from '@/api/modules/system/admin/user';
import { IS_PREVIEW } from '@/config';

defineOptions({
  name: 'UserDeptForm'
});
const parentMenus = ref<ISysDept.Tree[]>([]);
const treeProps = {
  label: 'name',
  value: 'id'
};
const leaders = ref<ISysDept.Leader[]>([]);

const selectIds = ref<number[]>([]);

const deptIds = ref<number[]>([]);

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

const loadParentMenus = () => {
  getMenuTree({ excludeNodeId: paramsProps.value.row?.id }).then(res => {
    parentMenus.value = res.data;
  });
};

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
  selectIds.value = params?.selectIds;
  leaders.value = params?.selectedList;
  if (Array.isArray(params?.deptId)) {
    // 过滤掉值为 0 -1 和 -2 的元素
    deptIds.value = params.deptId.filter(id => id !== 0 && id !== -1 && id !== -2);
  }
  visible.value = true;
  loadParentMenus();
};

const rules = reactive({
  id: [{ required: true, message: '请选择部门' }]
});

const emit = defineEmits(['submit']);

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>();
const handleSubmit = () => {
  const containsAny = [1, 2, 3, 4, 5, 6].some(id => paramsProps.value.selectIds.includes(id));
  if (IS_PREVIEW && containsAny) {
    return ElMessage.warning({ message: '预览环境，禁止修改，请谅解！' });
  }
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    try {
      const param = {
        userIds: selectIds.value,
        deptIds: deptIds.value
      };
      await bindUserDeptApi(param).then(() => paramsProps.value.getTableList!());
      ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      emit('submit');
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss">
.user-item {
  margin: 5px;
}

.el-alert {
  width: 90%;
  margin: 0 auto;
}
</style>
