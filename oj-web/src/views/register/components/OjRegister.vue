<template>
  <div class="oj-register-container">

    <div class="form-card">
      <div class="form-header">
        <h3 class="form-title">学员信息注册</h3>
      </div>

      <el-form
        ref="registerFormRef"
        :model="formModel"
        :rules="registerRules"
        label-position="top"
        size="default"
        @keyup.enter="handleRegister"
        class="register-form"
      >
        <!-- 基础信息 -->
        <div class="form-row">
          <el-form-item prop="name" class="form-item">
            <template #label>真实姓名</template>
            <el-input
              v-model="formModel.name"
              placeholder="请输入真实姓名"
              clearable
              class="compact-input"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="nickName" class="form-item">
            <template #label>昵称</template>
            <el-input
              v-model="formModel.nickName"
              placeholder="请输入昵称"
              clearable
              class="compact-input"
            >
              <template #prefix>
                <el-icon><ChatDotRound /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <!-- 联系信息 -->
        <div class="form-row">
          <el-form-item prop="phone" class="form-item">
            <template #label>手机号</template>
            <el-input
              v-model="formModel.phone"
              placeholder="请输入手机号"
              clearable
              class="compact-input"
            >
              <template #prefix>
                <el-icon><Iphone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email" class="form-item">
            <template #label>邮箱</template>
            <el-input
              v-model="formModel.email"
              placeholder="请输入邮箱"
              clearable
              class="compact-input"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <!-- 个人详情 -->
        <div class="form-row">
          <el-form-item prop="sex" class="form-item">
            <template #label>
              <span class="form-label">性别</span>
            </template>
            <el-select v-model="formModel.sex" placeholder="请选择性别" class="compact-select">
              <el-option label="未知" value="0" />
              <el-option label="男" value="1" />
              <el-option label="女" value="2" />
            </el-select>
          </el-form-item>

          <el-form-item prop="birthday" class="form-item">
            <template #label>
              <span class="form-label">生日</span>
            </template>
            <el-date-picker
              v-model="formModel.birthday"
              type="date"
              placeholder="请选择生日"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="compact-date-picker"
            />
          </el-form-item>
        </div>

        <!-- 班级和头像 -->
        <div class="form-row">
          <el-form-item prop="groupCode" class="form-item">
            <template #label>
              <span class="form-label">班级代码</span>
            </template>
            <el-input
              v-model="formModel.groupCode"
              placeholder="请输入班级代码"
              clearable
              class="compact-input"
            >
              <template #prefix>
                <el-icon><School /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="avatar" class="form-item avatar-item">
            <template #label>
              <span class="form-label">头像</span>
            </template>
            <div class="avatar-upload-wrapper">
              <upload-img
                v-model:image-url="formModel.avatar"
                :limit="1"
                :file-size="2"
                dir="avatar"
                height="60px"
                width="60px"
                class="avatar-uploader"
              />
            </div>
          </el-form-item>
        </div>

        <div class="register-actions">
          <el-button
            type="primary"
            :loading="loading"
            class="register-button"
            size="large"
            @click="handleRegister"
          >
            <el-icon class="button-icon" v-if="!loading"><Check /></el-icon>
            完成注册
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { registerUserInfoApi } from '@/api/modules/oj/user/userInfo';
import type { IUserInfo } from '@/api/interface/oj/user/userInfo';
import { 
  User, 
  ChatDotRound, 
  Iphone, 
  Message,
  School,
  OfficeBuilding,
  Check
} from '@element-plus/icons-vue';
import UploadImg from '@/components/Common/Upload/Img.vue';
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

// 当前租户信息
const currentTenant = computed(() => ({
  tenantId: tenantStore.tenantId,
  tenantName: tenantStore.tenantName,
  systemName: tenantStore.systemName
}));

// 注册表单数据
const formModel = reactive<IUserInfo.RegisterForm>({
  name: '',
  nickName: '',
  phone: '',
  email: '',
  avatar: '',
  sex: '0',
  birthday: '',
  groupCode: ''
});

// 表单验证规则
const registerRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应在 2 到 20 个字符之间', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 15, message: '昵称长度应在 2 到 15 个字符之间', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
});

// 注册处理
const handleRegister = async () => {
  console.log('OJ注册被调用');
  
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
  
  // 手动验证必填字段
  if (!formModel.name) {
    ElMessage.error('请输入真实姓名');
    return;
  }
  
  console.log('OJ注册表单数据:', formModel);
  
  loading.value = true;
  try {
    await registerUserInfoApi(formModel);
    ElMessage.success('注册成功，即将跳转到登录页');
    // 跳转到登录页
    setTimeout(() => {
      router.push(LOGIN_URL);
    }, 1500);
  } catch (error: any) {
    console.error('OJ注册失败:', error);
    ElMessage.error(error.message || '注册失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 组件挂载时发送左侧内容
onMounted(() => {
  // 发送OJ注册专用的左侧文案
  emit('updateLeftContent', {
    title: '欢迎加入编程学习',
    subtitle: '开启你的代码之旅',
    features: [
      { icon: '💻', text: '在线编程练习' },
      { icon: '🏆', text: '算法竞赛系统' },
      { icon: '📚', text: '丰富题库资源' },
      { icon: '👥', text: '学习小组协作' }
    ]
  });
});
</script>

<style scoped lang="scss">
.oj-register-container {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: 0;
}


// 表单卡片 - 更清爽的设计
.form-card {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--el-border-color-light);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 6px 30px rgba(0, 0, 0, 0.12);
  }
}

.form-header {
  text-align: center;
  margin-bottom: 28px;
  
  .form-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0;
    position: relative;
    display: inline-block;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 50%;
      transform: translateX(-50%);
      width: 40px;
      height: 3px;
      background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
      border-radius: 2px;
    }
  }
}

// 表单行布局
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

// 表单项
.form-item {
  margin-bottom: 0;
  
  &.required-item {
    .required-mark {
      color: var(--el-color-danger);
      margin-left: 2px;
    }
  }
  
  &.avatar-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
}

.form-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
  font-weight: 500;
  margin-bottom: 6px;
  display: block;
}

// 现代化输入框样式 - 与登录页面保持一致
:deep(.compact-input) {
  .el-input__wrapper {
    box-shadow: 0 0 0 1px var(--el-border-color) inset;
    border-radius: 6px;
    height: 40px;
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px var(--el-color-primary-light-3) inset;
    }
  }
  
  .el-input__inner {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
  }
  
  .el-input__prefix {
    .el-icon {
      color: var(--el-text-color-placeholder);
      font-size: 16px;
      transition: color 0.3s ease;
    }
  }
  
  &.is-focus .el-input__prefix .el-icon {
    color: var(--el-color-primary);
  }
}

:deep(.compact-select) {
  .el-select__wrapper {
    box-shadow: 0 0 0 1px var(--el-border-color) inset;
    border-radius: 6px;
    height: 40px;
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px var(--el-color-primary-light-3) inset;
    }
  }
  
  .el-select__selected-item {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
  }
  
  .el-select__placeholder {
    font-size: 14px;
  }
}

:deep(.compact-date-picker) {
  width: 100%;
  
  .el-input__wrapper {
    box-shadow: 0 0 0 1px var(--el-border-color) inset;
    border-radius: 6px;
    height: 40px;
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px var(--el-color-primary-light-3) inset;
    }
  }
  
  .el-input__inner {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
  }
}

// 头像上传样式 - 简化
.avatar-upload-wrapper {
  :deep(.avatar-uploader) {
    border-radius: 12px;
    border: 2px dashed var(--el-border-color);
    transition: all 0.3s ease;
    overflow: hidden;
    
    &:hover {
      border-color: var(--el-color-primary);
      transform: scale(1.05);
    }
  }
}

// 注册按钮 - 与登录页面风格一致
.register-actions {
  margin-top: 32px;
  
  .register-button {
    width: 100%;
    border-radius: 6px;
    font-weight: 600;
    height: 48px;
    font-size: 16px;
    background: var(--el-color-primary);
    border: none;
    transition: all 0.3s ease;
    
    &:hover {
      background: var(--el-color-primary-dark-2);
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(var(--el-color-primary-rgb), 0.3);
    }
    
    &:active {
      transform: translateY(0);
    }
    
    .button-icon {
      margin-right: 6px;
      font-size: 16px;
    }
  }
}

// Form item 标签样式调整
:deep(.el-form-item__label) {
  line-height: 22px;
  padding-bottom: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-regular);
}

:deep(.el-form-item__content) {
  line-height: 40px;
}

// 必填字段样式
:deep(.el-form-item.is-required .el-form-item__label::before) {
  content: '*';
  color: var(--el-color-danger);
  margin-right: 4px;
}

// 响应式设计
@media (max-width: 768px) {
  .oj-register-container {
    padding: 0;
  }
  
  .form-card {
    padding: 24px 20px;
    border-radius: 8px;
  }
  
  .form-header {
    margin-bottom: 24px;
    
    .form-title {
      font-size: 18px;
    }
  }
  
  .form-row {
    margin-bottom: 16px;
  }
  
  .register-actions {
    margin-top: 28px;
    
    .register-button {
      height: 44px;
      font-size: 15px;
    }
  }
  
  .tenant-info-section {
    margin-bottom: 16px;
    
    .tenant-display-card {
      padding: 12px 16px;
      
      .tenant-name {
        font-size: 13px;
      }
    }
  }
}

@media (max-width: 480px) {
  .form-card {
    padding: 20px 16px;
  }
  
  .form-row {
    gap: 12px;
    margin-bottom: 12px;
  }
  
  .form-header {
    margin-bottom: 20px;
    
    .form-title {
      font-size: 17px;
    }
  }
}

// 添加一些微动画
.form-item {
  animation: fadeInUp 0.6s ease-out both;
  
  &:nth-child(1) { animation-delay: 0.1s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.3s; }
  &:nth-child(4) { animation-delay: 0.4s; }
}

.register-actions {
  animation: fadeInUp 0.6s ease-out 0.5s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 