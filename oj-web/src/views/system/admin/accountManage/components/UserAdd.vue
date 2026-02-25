<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="580px" draggable append-to-body>
    <el-form
        ref="ruleFormRef"
        label-width="80px"
        label-suffix=" :"
        :rules="rules"
        :model="paramsProps.row"
        @submit.enter.prevent="handleSubmit"
        class="form__label"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="账户" prop="username">
            <template #label>
              <el-space :size="2">
                <span>账户</span>
                <el-tooltip effect="dark" content="字母、数字和下划线，用于登录。" placement="top">
                  <i :class="'iconfont icon-yiwen'" />
                </el-tooltip>
              </el-space>
              <span>&nbsp;:</span>
            </template>
            <el-input v-model="paramsProps.row.username" placeholder="请填写账户" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="paramsProps.row.nickname" placeholder="请填写昵称" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="paramsProps.row.phone" placeholder="请填写手机号" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="paramsProps.row.email" placeholder="请填写邮箱地址" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="性别" prop="sex">
            <el-select v-model="paramsProps.row.sex" clearable placeholder="请选择性别">
              <el-option label="未知" :value="0" />
              <el-option label="男" :value="1" />
              <el-option label="女" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="生日" prop="birthday">
            <el-date-picker v-model="paramsProps.row.birthday" type="date" placeholder="选择生日" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="身份证" prop="idCard">
            <el-input v-model="paramsProps.row.idCard" placeholder="请填写身份证" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-form-item label="头像" prop="logo">
          <UploadImg
              v-model:image-url="paramsProps.row.logo"
              @change="fileChange"
              dir="user"
              width="135px"
              height="135px"
              border-radius="50%"
          >
            <template #empty>
              <el-icon><Avatar /></el-icon>
              <span>请上传头像</span>
            </template>
            <template #tip> 头像大小不能超过 3M </template>
          </UploadImg>
        </el-form-item>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import UploadImg from '@/components/Common/Upload/Img.vue';
import type { IUploadResult } from '@/api/interface/system/admin/upload';

defineOptions({
  name: 'UserAdd'
});
const rules = reactive({
  username: [
    { required: true, message: '用户名是必填项', trigger: 'blur' },
    { min: 3, max: 32, message: '用户名长度应在3到32个字符之间', trigger: 'blur' },
    { pattern: /^\w+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ]
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
};

const emit = defineEmits(['submit']);

// 提交数据（新增/编辑）
const ruleFormRef = ref();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid: boolean) => {
    if (!valid) return;
    try {
      await paramsProps.value.api!(paramsProps.value.row);
      ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      paramsProps.value.getTableList!();
      emit('submit');
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
};

// 获取文件变更事件
const fileChange = (data: IUploadResult) => {
  console.log(data);
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss"></style>
