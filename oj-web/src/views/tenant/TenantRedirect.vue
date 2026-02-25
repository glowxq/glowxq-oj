<template>
  <div class="tenant-redirect-container">
    <div v-if="loading" class="tenant-card loading-wrapper">
      <div class="loading-spinner">
        <el-icon class="loading-icon is-loading"><Loading /></el-icon>
      </div>
      <h2 class="loading-title">租户切换中</h2>
      <p class="loading-message">正在处理您的请求，请稍候...</p>
    </div>
    <div v-else-if="error" class="tenant-card error-wrapper">
      <div class="error-icon">
        <el-icon><WarningFilled /></el-icon>
      </div>
      <h2 class="error-title">出现错误</h2>
      <p class="error-message">{{ error }}</p>
      <el-button type="primary" @click="goHome" class="apple-button">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回首页</span>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { WarningFilled, Loading, ArrowLeft } from '@element-plus/icons-vue';
import { getTenantByTenantKeyApi } from '@/api/modules/system/tenant/tenant';
import { useTenantStore } from '@/stores/modules/tenant';
import { useUserStore } from '@/stores/modules/user';
import { useAuthStore } from '@/stores/modules/auth';
import { useSocketStore } from '@/stores/modules/socket';
import { HOME_URL, LOGIN_URL } from '@/config';

const route = useRoute();
const router = useRouter();
const tenantStore = useTenantStore();
const userStore = useUserStore();
const authStore = useAuthStore();
const socketStore = useSocketStore();

const loading = ref(true);
const error = ref('');

// 根据tenantKey切换租户
const switchTenantByKey = async (tenantKey: string) => {
  try {
    console.log('开始执行switchTenantByKey, 参数:', tenantKey);
    loading.value = true;
    error.value = '';

    // 调用API获取租户信息
    console.log('正在调用API获取租户信息...');
    const res = await getTenantByTenantKeyApi(tenantKey);
    console.log('API返回结果:', res);
    
    if (res.code === '0000' && res.data) {
      const tenant = res.data;
      console.log('成功获取租户信息:', tenant);
      
      // 检查租户是否可用
      if (!tenant.enable) {
        console.warn('租户已被禁用:', tenant.tenantName);
        error.value = '该租户已被禁用，无法切换';
        return;
      }
      
      // 检查是否需要密码验证
      if (tenant.password) {
        console.log('租户需要密码验证:', tenant.tenantName);
        // 显示密码验证对话框
        await showPasswordVerification(tenant);
      } else {
        console.log('租户无需密码验证，直接切换:', tenant.tenantName);
        // 无需密码，直接切换
        switchTenant(tenant);
      }
    } else {
      console.error('获取租户信息失败:', res);
      error.value = res.message || `找不到租户: ${tenantKey}`;
    }
  } catch (err) {
    console.error('租户切换失败:', err);
    error.value = '租户切换失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};

// 密码验证对话框
const showPasswordVerification = (tenant: any) => {
  return new Promise((resolve) => {
    ElMessageBox.prompt(
      `租户 ${tenant.tenantName} 需要密码验证，请输入密码`,
      '租户密码验证',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputType: 'password',
        inputPlaceholder: '请输入租户密码',
        customClass: 'tenant-password-dialog'
      }
    ).then(({ value }) => {
      // 验证密码
      if (value === tenant.password) {
        // 密码正确，切换租户
        switchTenant(tenant);
        resolve(true);
      } else {
        // 密码错误
        ElMessage.error('密码错误，无法切换租户');
        error.value = '密码验证失败';
        resolve(false);
      }
    }).catch(() => {
      // 用户取消
      error.value = '已取消租户切换';
      resolve(false);
    });
  });
};

// 切换租户
const switchTenant = (tenant: any) => {
  console.log('开始切换租户，数据:', tenant);
  
  // 确保所有字段都有默认值，防止undefined
  const tenantInfo = {
    tenantId: tenant.tenantId || '',
    tenantName: tenant.tenantName || '',
    tenantCode: tenant.tenantCode || '',
    logoUrl: tenant.logoUrl || '',
    systemName: tenant.systemName || '',
    themeColor: tenant.themeColor || ''
  };
  
  console.log('处理后的租户信息:', tenantInfo);
  
  // 保存前先清空当前租户信息，避免数据混淆
  tenantStore.clearTenantInfo();
  
  // 保存新的租户信息
  tenantStore.setTenantInfo(tenantInfo);
  
  console.log('租户信息已保存到Store，验证结果:',  {
    'tenantId': tenantStore.tenantId,
    'tenantName': tenantStore.tenantName,
    'tenantCode': tenantStore.tenantCode,
    'logoUrl': tenantStore.logoUrl,
    'systemName': tenantStore.systemName,
    'themeColor': tenantStore.themeColor
  });
  
  ElMessage.success({
    message: `已切换到租户: ${tenant.tenantName}，即将跳转到登录页`,
    duration: 2000,
    customClass: 'tenant-success-message'
  });
  
  // 检查用户是否已登录
  if (userStore.token) {
    console.log('检测到用户已登录，需要清除登录状态');
    // 已登录状态，需要清除用户状态并重新登录
    setTimeout(() => {
      // 清除Token和状态
      console.log('正在清除用户状态和Token...');
      userStore.clear();
      authStore.clear();
      socketStore.close();
      
      // 直接跳转到登录页
      console.log('正在跳转到登录页面...');
      window.location.href = LOGIN_URL;
    }, 1500);
  } else {
    console.log('检测到用户未登录，直接跳转到登录页');
    // 未登录状态，直接跳转到登录页
    setTimeout(() => {
      console.log('未登录状态，直接跳转到登录页面...');
      router.push(LOGIN_URL);
    }, 1500);
  }
};

// 返回首页
const goHome = () => {
  router.push(HOME_URL);
};

onMounted(() => {
  // 从路由参数获取tenantKey
  const { tenantKey } = route.params;
  
  console.log('路由参数:', route.params);
  console.log('获取到的租户Key:', tenantKey);
  
  if (tenantKey && typeof tenantKey === 'string') {
    // 确保租户key统一大写处理
    const normalizedKey = tenantKey.toUpperCase();
    console.log('处理后的租户Key:', normalizedKey);
    switchTenantByKey(normalizedKey);
  } else {
    error.value = '无效的租户参数';
    loading.value = false;
  }
});
</script>

<style scoped>
.tenant-redirect-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  text-align: center;
  background: linear-gradient(135deg, #f7f9fc, #eef1f5);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Helvetica Neue', sans-serif;
}

.tenant-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  width: 100%;
  max-width: 420px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1), 0 1px 1px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(10px);
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 1px solid rgba(255, 255, 255, 0.5);
  animation: card-entrance 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.loading-wrapper {
  position: relative;
}

.loading-spinner {
  margin-bottom: 24px;
  height: 60px;
  width: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(var(--el-color-primary-rgb), 0.1);
  border-radius: 50%;
  box-shadow: 0 6px 16px rgba(var(--el-color-primary-rgb), 0.15);
  animation: pulse 2s infinite;
}

.loading-icon {
  font-size: 36px;
  color: var(--el-color-primary);
  animation: rotating 2s linear infinite;
}

.loading-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 16px;
  color: #333;
  letter-spacing: -0.5px;
}

.loading-message {
  font-size: 16px;
  color: #666;
  margin: 0;
  max-width: 300px;
  line-height: 1.5;
}

.error-wrapper {
  color: #f56c6c;
}

.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: rgba(245, 108, 108, 0.1);
  margin-bottom: 24px;
  box-shadow: 0 6px 16px rgba(245, 108, 108, 0.15);
}

.error-icon .el-icon {
  font-size: 40px;
  color: #f56c6c;
  animation: shake 1s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
  animation-delay: 0.2s;
}

.error-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 16px;
  color: #333;
  letter-spacing: -0.5px;
}

.error-message {
  font-size: 16px;
  color: #666;
  margin: 0 0 30px;
  max-width: 300px;
  line-height: 1.5;
}

.apple-button {
  min-width: 120px;
  height: 44px;
  border-radius: 12px;
  padding: 0 24px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(to right, var(--el-color-primary), rgba(var(--el-color-primary-rgb), 0.85));
  border: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.3);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.apple-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0));
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.apple-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(var(--el-color-primary-rgb), 0.4);
  filter: brightness(1.05);
}

.apple-button:hover::before {
  transform: translateX(100%);
}

.apple-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.2);
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes rotating {
  0% {
    transform: rotateZ(0deg);
  }
  100% {
    transform: rotateZ(360deg);
  }
}

@keyframes shake {
  10%, 90% {
    transform: translateX(-1px);
  }
  20%, 80% {
    transform: translateX(2px);
  }
  30%, 50%, 70% {
    transform: translateX(-2px);
  }
  40%, 60% {
    transform: translateX(2px);
  }
}

@keyframes card-entrance {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

<style>
.tenant-password-dialog {
  border-radius: 16px !important;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2) !important;
}

.tenant-password-dialog .el-message-box__header {
  padding: 20px 24px !important;
  border-bottom: 1px solid #ebeef5;
  background-color: #f9fafc;
}

.tenant-password-dialog .el-message-box__title {
  font-size: 18px !important;
  font-weight: 600 !important;
  letter-spacing: -0.3px;
}

.tenant-password-dialog .el-message-box__content {
  padding: 24px !important;
}

.tenant-password-dialog .el-message-box__input {
  padding: 16px 0 !important;
}

.tenant-password-dialog .el-message-box__input .el-input__wrapper {
  border-radius: 12px !important;
  padding: 0 16px !important;
  box-shadow: 0 0 0 1px #dcdfe6 inset !important;
  border: none !important;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
}

.tenant-password-dialog .el-message-box__input .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset, 0 4px 12px rgba(var(--el-color-primary-rgb), 0.1) !important;
  transform: translateY(-2px) !important;
}

.tenant-password-dialog .el-message-box__input .el-input__inner {
  height: 48px !important;
  font-size: 16px !important;
  border: none !important;
  background: transparent !important;
}

.tenant-password-dialog .el-message-box__btns {
  padding: 10px 24px 20px !important;
}

.tenant-password-dialog .el-button {
  border-radius: 12px !important;
  padding: 0 20px !important;
  height: 44px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
}

.tenant-password-dialog .el-button--default {
  border-color: #dcdfe6 !important;
  background-color: #f5f7fa !important;
}

.tenant-password-dialog .el-button--default:hover {
  border-color: #c0c4cc !important;
  background-color: #e9ecef !important;
  color: #606266 !important;
}

.tenant-password-dialog .el-button--primary {
  background: linear-gradient(to right, var(--el-color-primary), rgba(var(--el-color-primary-rgb), 0.85)) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.3) !important;
}

.tenant-password-dialog .el-button--primary:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 16px rgba(var(--el-color-primary-rgb), 0.4) !important;
  filter: brightness(1.05) !important;
}

.tenant-success-message {
  min-width: 240px !important;
  padding: 12px 16px !important;
  border-radius: 16px !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15) !important;
  backdrop-filter: blur(10px) !important;
  background-color: rgba(52, 199, 89, 0.9) !important;
  border: none !important;
}

.tenant-success-message .el-message__content {
  font-size: 15px !important;
  font-weight: 500 !important;
  color: white !important;
}

.tenant-success-message .el-message__icon {
  margin-right: 8px !important;
  font-size: 18px !important;
}

/* 确保按钮内的元素垂直居中 */
.apple-button .el-icon {
  vertical-align: middle !important;
}
</style> 