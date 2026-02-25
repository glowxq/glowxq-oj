<template>
  <div class="login-container flx-center">
    <div class="login-box">
      <SwitchDark class="dark" />
      <div class="login-left">
        <img class="login-left-img" src="@/assets/images/login_left3.png" alt="login" />
      </div>
      <div class="login-form">
        <div class="login-logo">
          <img class="login-icon" src="@/assets/images/logo.svg" alt="" />
          <h3 class="logo-text">{{title}}</h3>
        </div>
        <div class="tenant-control">
          <div v-if="currentTenant.tenantName" class="current-tenant-info">
            <div class="tenant-badge">
              <el-icon><OfficeBuilding /></el-icon>
              <span>当前租户：{{ currentTenant.tenantName }}</span>
            </div>
          </div>
          <TenantSwitch class="tenant-switcher" />
        </div>
        
        <LoginForm />
        
        <div class="register-link">
          还没有账户？<el-link type="primary" @click="goToRegister">注册</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import SwitchDark from '@/components/Common/SwitchDark/index.vue';
import LoginForm from '@/views/login/components/LoginForm.vue';
import { useTenantStore } from '@/stores/modules/tenant';
import { OfficeBuilding } from '@element-plus/icons-vue';
import TenantSwitch from '@/layouts/components/Header/components/TenantSwitch.vue';
import { REGISTER_URL } from '@/config';

const router = useRouter();
const title = import.meta.env.VITE_APP_TITLE;
const tenantStore = useTenantStore();

// 当前租户信息
const currentTenant = computed(() => ({
  tenantId: tenantStore.tenantId,
  tenantName: tenantStore.tenantName,
  systemName: tenantStore.systemName || title
}));

// 跳转到注册页
const goToRegister = () => {
  router.push(REGISTER_URL);
};
</script>

<style scoped lang="scss">
@import './index.scss';

.register-link {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  display: flex;
  justify-content: center;
  align-items: center;
  
  .el-link {
    font-size: 14px;
    font-weight: 500;
    margin-left: 4px;
  }
}

.tenant-control {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.current-tenant-info {
  margin-bottom: 12px;
  text-align: center;
  
  .tenant-badge {
    display: inline-flex;
    align-items: center;
    background-color: rgba(var(--el-color-primary-rgb), 0.1);
    color: var(--el-color-primary);
    padding: 8px 16px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.2);
    
    .el-icon {
      margin-right: 6px;
      font-size: 16px;
    }
  }
}

.tenant-switcher {
  margin-top: 8px;
  
  :deep(.current-tenant) {
    .tenant-trigger {
      padding: 4px 10px;
      font-size: 13px;
      
      .tenant-label {
        max-width: 100px;
      }
    }
  }
}
</style>
