<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="580px" draggable append-to-body>
    <el-alert
      v-if="paramsProps?.isBatch"
      title="警告信息"
      type="warning"
      show-icon
      description="批量功能会忽略用户原班级，以最终提交数据为准！！"
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
      <el-form-item label="用户" prop="userIds">
        <el-tag class="user-item" v-for="tag in users" :key="tag.id" type="info">
          {{ tag.nickname || tag.username }}
        </el-tag>
      </el-form-item>
      <el-form-item label="班级" prop="groupIds">
        <group-select
          v-model="groupIds"
          :multiple="true"
          placeholder="请选择班级"
          clearable
          @change="handleGroupChange"
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
import { bindUserGroupApi } from '@/api/modules/oj/user/user';
import { IS_PREVIEW } from '@/config';
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue';

defineOptions({
  name: 'UserGroupForm'
});

const users = ref<any[]>([]);
const selectIds = ref<number[]>([]);
const groupIds = ref<number[]>([]);

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
  selectIds.value = params?.selectIds;
  users.value = params?.selectedList;

  if (Array.isArray(params?.groupIds)) {
    groupIds.value = params.groupIds;
  } else {
    groupIds.value = [];
  }

  // 确保表单验证能够正确绑定到组件状态
  paramsProps.value.groupIds = groupIds.value;

  visible.value = true;
};

// 处理班级选择变化
const handleGroupChange = (value: number[]) => {
  // 将选择的值同步到paramsProps，确保表单验证正确工作
  paramsProps.value.groupIds = value;
};

const rules = reactive({
  groupIds: [{ required: true, message: '请选择班级' }]
});

const emit = defineEmits(['submit']);

// 提交数据
const ruleFormRef = ref<InstanceType<typeof ElForm>>();
const handleSubmit = () => {
  // 确保验证前将最新的groupIds值同步到paramsProps
  paramsProps.value.groupIds = groupIds.value;

  // 预览环境保护代码
  const containsAny = [1, 2, 3, 4, 5, 6].some(id => paramsProps.value.selectIds.includes(id));
  if (IS_PREVIEW && containsAny) {
    return ElMessage.warning({ message: '预览环境，禁止修改，请谅解！' });
  }

  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    try {
      const param = {
        userIds: selectIds.value,
        groupIds: groupIds.value
      };

      await bindUserGroupApi(param).then(() => {
        paramsProps.value.getTableList!();
        ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      });

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
