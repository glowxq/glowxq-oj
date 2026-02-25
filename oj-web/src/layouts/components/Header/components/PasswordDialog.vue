<template>
  <el-dialog v-model="visible" append-to-body title="修改密码" width="500px" draggable>
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=" :"
      :rules="rules"
      :model="formData"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="原密码" prop="oldPwd">
        <el-input v-model="formData.oldPwd" type="password" placeholder="请填写原密码" show-password clearable />
      </el-form-item>
      <el-form-item label="新密码" prop="newPwd">
        <el-input v-model="formData.newPwd" type="password" placeholder="请填写新密码" show-password clearable />
      </el-form-item>
      <el-form-item label="确认新密码" prop="newPwdConfirm">
        <el-input v-model="formData.newPwdConfirm" type="password" placeholder="请填写确认新密码" show-password clearable />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { validatePasswordFormat } from '@/config/validator';
import { changePassword } from '@/api/modules/system/admin/user';
import { ElMessage } from 'element-plus';
import { IS_PREVIEW } from '@/config';

const validateOldNewPassword = (rule: any, value: any, callback: (error?: string | Error) => void) => {
  if (value === formData.value.oldPwd) {
    callback(new Error('旧密码和新密码不能相同'));
    return;
  }
  callback();
};

const validateConfirmPassword = (rule: any, value: any, callback: (error?: string | Error) => void) => {
  if (value !== formData.value.newPwd) {
    callback(new Error('新密码和确认密码不一致'));
    return;
  }
  callback();
};

const rules = reactive({
  oldPwd: [{ required: true, message: '请填写原密码' }],
  newPwd: [
    { required: true, message: '请填写新密码' },
    { validator: validatePasswordFormat, trigger: 'blur' },
    { validator: validateOldNewPassword, trigger: 'blur' }
  ],
  newPwdConfirm: [
    { required: true, message: '请填写确认新密码' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
});

const formData = ref({
  oldPwd: '',
  newPwd: '',
  newPwdConfirm: ''
});

const ruleFormRef = ref();
const handleSubmit = () => {
  ruleFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return;
    if (IS_PREVIEW) {
      return ElMessage.warning({ message: '预览环境，禁止修改密码，请谅解！' });
    }
    try {
      await changePassword(formData.value);
      ElMessage.success({ message: `修改密码成功！` });
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
};

const visible = ref(false);
const openDialog = () => {
  visible.value = true;
  formData.value = {
    oldPwd: '',
    newPwd: '',
    newPwdConfirm: ''
  };
};

defineExpose({ openDialog });
</script>
