<template>
  <div class="default-register-container">
    <el-form
      ref="registerFormRef"
      :model="formModel"
      :rules="registerRules"
      label-width="100px"
      label-position="left"
      size="default"
      @keyup.enter="handleRegister"
      class="register-form"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h4 class="section-title">基本信息</h4>
        
        <!-- 用户名单独一行 -->
        <el-form-item prop="username" class="form-item">
          <template #label>用户名 <span class="required">*</span></template>
          <el-input
            v-model="formModel.username"
            placeholder="请输入用户名"
            clearable
            @input="syncPhoneWithUsername"
          >
            <template #prefix>
              <el-icon><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 密码单独一行 -->
        <el-form-item prop="password" class="form-item">
          <template #label>密码 <span class="required">*</span></template>
          <el-input
            v-model.trim="formModel.password"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 确认密码单独一行 -->
        <el-form-item prop="confirmPassword" class="form-item">
          <template #label>确认密码 <span class="required">*</span></template>
          <el-input
            v-model.trim="formModel.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 租户选择 -->
        <el-form-item prop="tenantKey" v-if="tenantEnabled" class="form-item">
          <template #label>租户选择 <span class="required">*</span></template>
          <TenantSelect
            v-model="formModel.tenantKey"
            @change="handleTenantChange"
          />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item prop="phone" class="form-item">
          <template #label>手机号</template>
          <el-input
            v-model="formModel.phone"
            placeholder="请输入手机号"
            clearable
          >
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <!-- 个人信息 - 折叠面板 -->
      <div class="form-section">
        <el-collapse v-model="personalInfoCollapse" class="personal-collapse">
          <el-collapse-item name="personal">
            <template #title>
              <div class="collapse-title">
                <h4 class="section-title-inline">个人信息</h4>
                <span class="optional-tag">选填</span>
              </div>
            </template>
            
            <div class="form-grid">
              <el-form-item prop="name" class="form-item">
                <template #label>真实姓名</template>
                <el-input
                  v-model="formModel.name"
                  placeholder="请输入真实姓名"
                  clearable
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item prop="nickname" class="form-item">
                <template #label>昵称</template>
                <el-input
                  v-model="formModel.nickname"
                  placeholder="请输入昵称"
                  clearable
                >
                  <template #prefix>
                    <el-icon><ChatDotRound /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>

            <div class="form-grid">
              <el-form-item prop="email" class="form-item">
                <template #label>邮箱</template>
                <el-input
                  v-model="formModel.email"
                  placeholder="请输入邮箱"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item prop="sex" class="form-item">
                <template #label>性别</template>
                <el-select v-model="formModel.sex" placeholder="请选择性别">
                  <el-option label="未知" :value="0" />
                  <el-option label="男" :value="1" />
                  <el-option label="女" :value="2" />
                </el-select>
              </el-form-item>
            </div>

            <div class="form-grid">
              <el-form-item prop="birthday" class="form-item">
                <template #label>生日</template>
                <el-date-picker
                  v-model="formModel.birthday"
                  type="date"
                  placeholder="请选择生日"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item prop="address" class="form-item">
                <template #label>地址</template>
                <el-input
                  v-model="formModel.address"
                  placeholder="请输入地址"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Location /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>

            <el-form-item prop="logo" class="form-item avatar-item">
              <template #label>头像</template>
              <div class="avatar-wrapper">
                <upload-img
                  v-model:image-url="formModel.logo"
                  :limit="1"
                  :file-size="2"
                  dir="avatar"
                  height="90px"
                  width="90px"
                >
                  <template #tip>
                    <span class="upload-tip">支持jpg, png格式，大小不超过2MB</span>
                  </template>
                </upload-img>
              </div>
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
      </div>
      
      <div class="register-actions">
        <el-button
          type="primary"
          :loading="loading"
          class="register-button"
          size="large"
          @click="handleRegister"
        >
          注册
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { registerApi } from '@/api/modules/system/admin/login';
import { getTenantEnableStatusApi } from '@/api/modules/system/tenant/tenant';
import type { ITenant } from '@/api/interface/system/tenant/tenant';
import type { ILogin } from '@/api/interface/system/admin/login';
import { 
  UserFilled, 
  Lock, 
  Iphone, 
  Message, 
  User, 
  ChatDotRound, 
  Location
} from '@element-plus/icons-vue';
import UploadImg from '@/components/Common/Upload/Img.vue';
import TenantSelect from '@/components/Common/TenantSelect/index.vue';
import { LOGIN_URL } from '@/config';
import { useTenantStore } from '@/stores/modules/tenant';

// 定义emits
const emit = defineEmits<{
  'updateLeftContent': [content: {
    title: string;
    subtitle: string;
    features: Array<{ icon: string; text: string }>;
  }]
}>();

// 路由
const router = useRouter();
// 租户store
const tenantStore = useTenantStore();

// 表单引用
const registerFormRef = ref<FormInstance>();
const loading = ref(false);

// 租户功能是否启用
const tenantEnabled = ref(true);

// 个人信息折叠状态（默认折叠，空数组表示不展开）
const personalInfoCollapse = ref<string[]>([]);

// 注册表单数据
const registerForm = reactive<ILogin.RegisterParams>({
  username: '',
  password: '',
  tenantKey: '',
  phone: '',
  email: '',
  name: '',
  nickname: '',
  sex: 0,
  birthday: '',
  logo: '',
  address: '',
});

// 将确认密码临时添加到表单模型 (不会提交到后端)
const formModel = reactive({
  ...registerForm,
  confirmPassword: ''
});

// 用户名输入时自动同步到手机号
const syncPhoneWithUsername = (value: string) => {
  formModel.phone = value;
};

// 处理租户变化
const handleTenantChange = (tenant: ITenant.Row | null) => {
  console.log('租户选择变化:', tenant);
};

// 表单验证规则
const validatePass2 = (rule: any, value: string, callback: any) => {
  // 确保值是字符串
  const inputValue = typeof value === 'string' ? value : '';
  const passwordValue = typeof formModel.password === 'string' ? formModel.password : '';
  
  if (inputValue === '') {
    callback(new Error('请再次输入密码'));
  } else if (inputValue.trim() !== passwordValue.trim()) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

// 监听密码变化，重新验证确认密码
watch(() => formModel.password, () => {
  if (formModel.confirmPassword) {
    registerFormRef.value?.validateField('confirmPassword');
  }
});

// 监听确认密码变化，确保与密码同步
watch(() => formModel.confirmPassword, (newVal) => {
  if (newVal) {
    registerFormRef.value?.validateField('confirmPassword');
  }
});

const registerRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  tenantKey: [
    { required: true, message: '请选择或输入租户', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
});

// 注册处理
const handleRegister = async () => {
  console.log('handleRegister 被调用');
  
  if (!registerFormRef.value) {
    console.error('表单引用为空');
    return;
  }
  
  // 防止重复点击
  if (loading.value) {
    console.log('正在提交中，请勿重复点击');
    return;
  }
  
  // 验证表单
  try {
    await registerFormRef.value.validate();
  } catch (error) {
    console.log('表单验证失败');
    return;
  }
  
  try {
    // 同步formModel到registerForm (除了confirmPassword)
    registerForm.username = formModel.username;
    registerForm.password = formModel.password;
    registerForm.tenantKey = formModel.tenantKey;
    registerForm.phone = formModel.phone;
    registerForm.email = formModel.email;
    registerForm.name = formModel.name;
    registerForm.nickname = formModel.nickname;
    registerForm.sex = formModel.sex;
    registerForm.birthday = formModel.birthday;
    registerForm.logo = formModel.logo;
    registerForm.address = formModel.address;
    
    loading.value = true;
    await registerApi(registerForm);
    ElMessage.success('注册成功，即将跳转到登录页');
    // 跳转到登录页
    setTimeout(() => {
      router.push(LOGIN_URL);
    }, 1500);
  } catch (error: any) {
    console.error('注册失败:', error);
    ElMessage.error(error.message || '注册失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 获取租户功能启用状态
const getTenantEnableStatus = async () => {
  try {
    const res = await getTenantEnableStatusApi();
    if (res.code === '0000' && res.data) {
      tenantEnabled.value = res.data.enable;
      console.log('租户功能启用状态:', tenantEnabled.value);
      
      // 如果租户功能被禁用，移除租户验证规则
      if (!tenantEnabled.value) {
        delete registerRules.tenantKey;
      }
    }
  } catch (error) {
    console.error('获取租户功能启用状态失败:', error);
  }
};

// 初始化
onMounted(() => {
  // 发送管理员注册专用的左侧文案
  emit('updateLeftContent', {
    title: '欢迎加入管理平台',
    subtitle: '构建强大的管理系统',
    features: [
      { icon: '🎯', text: '专业的管理平台' },
      { icon: '🛡️', text: '多租户管理系统' },
      { icon: '📊', text: '数据统计分析' },
      { icon: '⚙️', text: '灵活的权限控制' }
    ]
  });

  // 获取租户功能启用状态
  getTenantEnableStatus();
  
  // 如果已有当前租户，默认填充到表单
  if (tenantStore.tenantId) {
    formModel.tenantKey = tenantStore.tenantId;
  }
});
</script>

<style scoped lang="scss">
.default-register-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.register-form {
  .form-section {
    margin-bottom: 20px;
    
    .section-title {
      font-size: 15px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin-bottom: 16px;
      padding-bottom: 6px;
      border-bottom: 2px solid var(--el-border-color-light);
      display: flex;
      align-items: center;
      
      &::before {
        content: '';
        width: 4px;
        height: 14px;
        background-color: var(--el-color-primary);
        margin-right: 8px;
        border-radius: 2px;
      }
    }
  }
  
  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
    margin-bottom: 16px;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 12px;
    }
  }
  
  .form-item {
    margin-bottom: 14px;
    
    :deep(.el-form-item__label) {
      font-weight: 500;
      color: var(--el-text-color-regular);
      line-height: 32px;
      padding-right: 12px;
      
      .required {
        color: var(--el-color-danger);
        margin-left: 4px;
      }
    }
    
    :deep(.el-form-item__content) {
      min-height: 32px;
      line-height: 32px;
    }
    
    &.avatar-item {
      grid-column: 1 / -1;
      
      .avatar-wrapper {
        display: flex;
        align-items: center;
        
        .upload-tip {
          font-size: 12px;
          color: var(--el-text-color-placeholder);
          margin-left: 16px;
        }
      }
    }
  }
  
  // 必填字段标记
  :deep(.el-form-item.is-required .el-form-item__label::before) {
    content: '*';
    color: var(--el-color-danger);
    margin-right: 4px;
  }
}

// 个人信息折叠面板样式
.personal-collapse {
  border: none;
  
  :deep(.el-collapse-item) {
    border: none;
    
    .el-collapse-item__header {
      padding: 0;
      border: none;
      background: transparent;
      height: auto;
      line-height: normal;
      
      .collapse-title {
        display: flex;
        align-items: center;
        width: 100%;
        
        .section-title-inline {
          font-size: 15px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin: 0;
          padding-bottom: 6px;
          border-bottom: 2px solid var(--el-border-color-light);
          display: flex;
          align-items: center;
          flex: 1;
          
          &::before {
            content: '';
            width: 4px;
            height: 14px;
            background-color: var(--el-color-primary);
            margin-right: 8px;
            border-radius: 2px;
          }
        }
        
        .optional-tag {
          margin-left: 12px;
          font-size: 11px;
          font-weight: normal;
          padding: 2px 6px;
          border-radius: 10px;
          background-color: rgba(var(--el-color-info-rgb), 0.08);
          color: var(--el-color-info);
        }
      }
    }
    
    .el-collapse-item__arrow {
      margin-left: 8px;
      color: var(--el-color-primary);
      font-size: 12px;
      transition: transform 0.3s ease;
    }
    
    .el-collapse-item__content {
      padding: 16px 0 0;
      
      .el-collapse-item__wrap {
        border: none;
      }
    }
  }
}

// 现代化输入框样式 - 压缩高度
:deep(.el-input__wrapper) {
  border-radius: 6px;
  transition: all 0.2s ease;
  height: 38px;
  
  &:hover {
    box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
  }
  
  &.is-focus {
    box-shadow: 0 0 0 1px var(--el-color-primary) inset;
  }
}

:deep(.el-input__inner) {
  height: 38px;
  line-height: 38px;
  font-size: 14px;
}

:deep(.el-select) {
  width: 100%;
  
  .el-select__wrapper {
    border-radius: 6px;
    transition: all 0.2s ease;
    height: 38px;
    
    &:hover {
      box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 1px var(--el-color-primary) inset;
    }
  }
  
  .el-select__selected-item {
    height: 38px;
    line-height: 38px;
  }
}

:deep(.el-date-editor) {
  height: 38px;
  
  .el-input__wrapper {
    height: 38px;
  }
  
  .el-input__inner {
    height: 38px;
    line-height: 38px;
  }
}

.register-actions {
  text-align: center;
  margin-top: 24px;
  
  .register-button {
    width: 100%;
    max-width: 280px;
    height: 42px;
    font-size: 15px;
    font-weight: 600;
    border-radius: 6px;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(var(--el-color-primary-rgb), 0.3);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .default-register-container {
    padding: 0 12px;
  }
  
  .register-form {
    .form-section {
      margin-bottom: 16px;
      
      .section-title {
        font-size: 14px;
        margin-bottom: 12px;
      }
    }
    
    .form-grid {
      margin-bottom: 12px;
    }
    
    .form-item {
      margin-bottom: 12px;
    }
  }
  
  .register-actions {
    margin-top: 20px;
    
    .register-button {
      max-width: 100%;
      height: 40px;
      font-size: 14px;
    }
  }
}
</style>
