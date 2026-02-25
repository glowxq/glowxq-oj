<template>
  <el-dialog 
    v-model="visible" 
    :title="`${paramsProps.title}`" 
    :destroy-on-close="true" 
    width="700px" 
    draggable 
    append-to-body
    class="apple-style-dialog"
  >
    <el-form
      ref="ruleFormRef"
      label-width="90px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
      class="form__label apple-form"
    >
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        <el-row :gutter="20">
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="name">
              <el-input v-model="paramsProps.row.name" placeholder="请填写真实姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="paramsProps.row.phone" placeholder="请填写手机号" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="paramsProps.row.email" placeholder="请填写邮箱地址" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-select v-model="paramsProps.row.sex" clearable placeholder="请选择性别" class="w-full">
                <el-option label="未知" :value="0" />
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker 
                v-model="paramsProps.row.birthday" 
                type="date" 
                placeholder="选择生日" 
                value-format="YYYY-MM-DD"
                class="w-full" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证" prop="idCard">
              <el-input v-model="paramsProps.row.idCard" placeholder="请填写身份证" clearable />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="form-section">
        <h3 class="section-title">数据统计</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="AC数量" prop="acNum">
              <el-input-number v-model="paramsProps.row.acNum" :min="0" placeholder="AC数量" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户积分" prop="integral">
              <el-input-number v-model="paramsProps.row.integral" :min="0" placeholder="用户积分" class="w-full" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="连续签到" prop="continuousSignDay">
              <el-input-number v-model="paramsProps.row.continuousSignDay" :min="0" placeholder="连续签到天数" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提交数量" prop="submitNum">
              <el-input-number v-model="paramsProps.row.submitNum" :min="0" placeholder="提交题目数量" class="w-full" />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="form-section">
        <h3 class="section-title">个性化设置</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="称号" prop="title">
              <el-input v-model="paramsProps.row.title" placeholder="请填写称号" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="颜色" prop="color">
              <ColorSelect 
                v-model="paramsProps.row.color" 
                :showPreview="true" 
                :previewText="paramsProps.row.title || '称号预览'" 
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="form-section">
        <h3 class="section-title">分组与标签</h3>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="标签" prop="tagIds">
              <meta-tag
                v-model="paramsProps.row.tagIds"
                :multiple="true"
                placeholder="请选择用户标签"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="班级" prop="groupIds">
              <group-select
                v-model="paramsProps.row.groupIds"
                :multiple="true"
                placeholder="请选择用户所属班级"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="form-section">
        <h3 class="section-title">头像上传</h3>
        <el-row>
          <el-form-item label="头像" prop="logo">
            <UploadImg
              v-model:image-url="paramsProps.row.logo"
              @change="fileChange"
              dir="user"
              :use-oss="true"
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
      </div>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="cancel-btn"> 取消 </el-button>
        <el-button type="warning" @click="fillTestData" class="test-btn" plain> 🧪 一键测试 </el-button>
        <el-button type="primary" @click="handleSubmit" class="submit-btn"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import UploadImg from '@/components/Common/Upload/Img.vue';
import type { IUploadResult } from '@/api/interface/system/admin/upload';
import MetaTag from '@/components/Common/Meta/Tag/TagSelect.vue';
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue';
import ColorSelect from '@/components/Common/Color/ColorSelect.vue';
import type { IOJUser } from '@/api/interface/oj/user/user';

defineOptions({
  name: 'UserAdd'
});
const rules = reactive({
  username: [
    { required: true, message: '用户名是必填项', trigger: 'blur' },
    { min: 3, max: 32, message: '用户名长度应在3到32个字符之间', trigger: 'blur' },
    { pattern: /^\w+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '真实姓名是必填项', trigger: 'blur' },
    { min: 2, max: 20, message: '真实姓名长度应在2到20个字符之间', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
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
  // 初始化表单数据
  paramsProps.value = {
    title: '',
    row: {
      tagIds: [],
      groupIds: []
    },
    api: undefined,
    getTableList: undefined
  };
  
  // 设置新的参数值
  paramsProps.value = { ...params };
  
  // 确保标签和班级字段被初始化
  if (!paramsProps.value.row.tagIds) {
    paramsProps.value.row.tagIds = [];
  }
  if (!paramsProps.value.row.groupIds) {
    paramsProps.value.row.groupIds = [];
  }
  
  visible.value = true;
};

const emit = defineEmits(['submit']);

// 提交数据（新增/编辑）
const ruleFormRef = ref();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid: boolean) => {
    if (!valid) return;
    try {
      // 直接提交表单数据，使用IOJUser.Form格式
      const formData: IOJUser.Form = { ...paramsProps.value.row };
      
      await paramsProps.value.api!(formData);
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

// 生成随机测试数据
const generateTestData = () => {
  const randomId = Math.floor(Math.random() * 10000);
  const genders = [0, 1, 2];
  const randomGender = genders[Math.floor(Math.random() * genders.length)];
  
  // 生成随机头像
  const avatarUrls = [
    'https://api.dicebear.com/7.x/avataaars/svg?seed=test1',
    'https://api.dicebear.com/7.x/avataaars/svg?seed=test2',
    'https://api.dicebear.com/7.x/avataaars/svg?seed=test3',
    'https://api.dicebear.com/7.x/personas/svg?seed=test4',
    'https://api.dicebear.com/7.x/personas/svg?seed=test5'
  ];
  
  return {
    username: `testuser${randomId}`,
    nickname: `测试用户${randomId}`,
    name: `张测试${randomId}`,
    phone: `138${String(Math.floor(Math.random() * 100000000)).padStart(8, '0')}`,
    email: `test${randomId}@example.com`,
    sex: randomGender,
    birthday: '1995-06-15',
    idCard: `${Math.floor(Math.random() * 900000) + 100000}199506151234`,
    acNum: Math.floor(Math.random() * 100),
    integral: Math.floor(Math.random() * 5000),
    continuousSignDay: Math.floor(Math.random() * 30),
    submitNum: Math.floor(Math.random() * 200),
    title: '测试称号',
    color: '#FF6B6B',
    tagIds: [1, 2], // 默认选择一些标签ID
    groupIds: [1], // 默认选择一个班级ID
    logo: avatarUrls[Math.floor(Math.random() * avatarUrls.length)]
  };
};

// 一键填充测试数据
const fillTestData = () => {
  const testData = generateTestData();
  Object.assign(paramsProps.value.row, testData);
  ElMessage.success('测试数据已填充！');
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss">
.apple-style-dialog {
  :deep(.el-dialog__header) {
    margin: 0;
    padding: 20px 24px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }
  
  :deep(.el-dialog__body) {
    padding: 24px;
  }
  
  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

.apple-form {
  .el-form-item {
    margin-bottom: 16px;
  }
  
  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper),
  :deep(.el-input-number__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px var(--el-border-color);
    transition: all 0.2s;
    
    &:hover, &:focus-within {
      box-shadow: 0 0 0 1px var(--el-color-primary);
    }
  }
  
  :deep(.el-input-number) {
    width: 100%;
  }
  
  :deep(.el-date-editor) {
    width: 100%;
  }
  
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background-color: var(--el-color-primary);
    box-shadow: -1px 0 0 0 var(--el-color-primary);
  }
}

.form-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  
  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin: 0 0 16px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.w-full {
  width: 100%;
}

.cancel-btn {
  border-radius: 8px;
}

.test-btn {
  border-radius: 8px;
  border-color: var(--el-color-warning);
  color: var(--el-color-warning);
  
  &:hover {
    background-color: var(--el-color-warning);
    color: white;
  }
}

.submit-btn {
  border-radius: 8px;
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  
  &:hover {
    background-color: var(--el-color-primary-light-3);
    border-color: var(--el-color-primary-light-3);
  }
}
</style>
