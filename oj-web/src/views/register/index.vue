<template>
  <div class="register-container flx-center">
    <div class="register-box">
      <SwitchDark class="dark" />
      <div class="register-left">
        <div class="left-content">
          <!-- 动态展示不同注册组件的文案 -->
          <div class="welcome-section">
            <h1 class="welcome-title">{{ leftContent.title }}</h1>
            <p class="welcome-subtitle">{{ leftContent.subtitle }}</p>
          </div>
          <div class="feature-list">
            <div 
              v-for="(feature, index) in leftContent.features" 
              :key="index"
              class="feature-item"
            >
              <div class="feature-icon">{{ feature.icon }}</div>
              <span>{{ feature.text }}</span>
            </div>
          </div>
        </div>
        <div class="decorative-pattern">
          <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="gradient1" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:var(--el-color-primary);stop-opacity:0.3" />
                <stop offset="100%" style="stop-color:var(--el-color-primary);stop-opacity:0.1" />
              </linearGradient>
            </defs>
            <circle cx="100" cy="100" r="80" fill="url(#gradient1)"/>
            <circle cx="60" cy="60" r="20" fill="rgba(255,255,255,0.2)"/>
            <circle cx="140" cy="140" r="15" fill="rgba(255,255,255,0.3)"/>
          </svg>
        </div>
      </div>
      
      <div class="register-form">
        <div class="register-logo">
          <img class="register-icon" src="@/assets/images/logo.svg" alt="logo" />
          <h3 class="logo-text">{{ title }}</h3>
        </div>
        
        <!-- 租户控制区域：根据配置决定是否显示 -->
        <div class="tenant-control" v-if="registerConfig.showTenantControl">
          <div v-if="currentTenant.tenantName" class="current-tenant-info">
            <div class="tenant-badge">
              <el-icon><OfficeBuilding /></el-icon>
              <span>当前租户：{{ currentTenant.tenantName }}</span>
            </div>
          </div>
        </div>
        
        <!-- 租户信息展示区域：在非租户控制模式下显示当前租户信息 -->
        <div class="tenant-info-display" v-else-if="currentTenant.tenantName">
          <div class="current-tenant-badge">
            <el-icon><OfficeBuilding /></el-icon>
            <span>{{ currentTenant.tenantName }}</span>
          </div>
        </div>
        
        <!-- 动态注册组件 -->
        <component 
          :is="currentRegisterComponent" 
          @updateLeftContent="handleUpdateLeftContent"
        />
        
        <div class="register-link">
          已有账户？<el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, ref } from 'vue';
import { useRouter } from 'vue-router';
import SwitchDark from '@/components/Common/SwitchDark/index.vue';
import { useTenantStore } from '@/stores/modules/tenant';
import { OfficeBuilding } from '@element-plus/icons-vue';
import { LOGIN_URL } from '@/config';
import { getCurrentRegisterConfig } from './config/registerConfig';

// 左侧文案接口
interface LeftContentType {
  title: string;
  subtitle: string;
  features: Array<{
    icon: string;
    text: string;
  }>;
}

const router = useRouter();
const title = import.meta.env.VITE_APP_TITLE;
const tenantStore = useTenantStore();

// 获取当前注册组件配置
const registerConfig = getCurrentRegisterConfig();

// 动态加载注册组件
const currentRegisterComponent = computed(() => {
  return defineAsyncComponent(registerConfig.component);
});

// 当前租户信息
const currentTenant = computed(() => ({
  tenantId: tenantStore.tenantId,
  tenantName: tenantStore.tenantName,
  systemName: tenantStore.systemName || title
}));

// 左侧内容，默认值
const leftContent = ref<LeftContentType>({
  title: '欢迎加入',
  subtitle: '开启您的学习之旅',
  features: [
    { icon: '🎯', text: '专业的学习平台' },
    { icon: '🚀', text: '高效的编程环境' },
    { icon: '🎓', text: '丰富的课程资源' }
  ]
});

// 处理子组件传来的左侧内容更新
const handleUpdateLeftContent = (content: LeftContentType) => {
  leftContent.value = content;
};

// 跳转到登录页
const goToLogin = () => {
  router.push(LOGIN_URL);
};
</script>

<style scoped lang="scss">
.register-container {
  height: 100%;
  min-height: 550px;
  background-color: #eeeeee;
  background-image: url('@/assets/images/login_bg.svg');
  background-size: cover;

  .register-box {
    position: relative;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-around;
    width: 96.5%;
    height: 94%;
    padding: 0 50px;
    background-color: rgb(255 255 255 / 80%);
    border-radius: 10px;

    .dark {
      position: absolute;
      top: 13px;
      right: 18px;
    }

    .register-left {
      width: 800px;
      margin-right: 10px;
      padding: 40px;
      border-radius: 10px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      position: relative;
      min-height: 400px;
      
      .left-content {
        z-index: 2;
        position: relative;
      }
      
      .welcome-section {
        text-align: center;
        margin-bottom: 40px;
        
        .welcome-title {
          font-size: 2.5rem;
          font-weight: 700;
          margin: 0 0 16px 0;
          color: var(--el-text-color-primary);
          animation: fadeInUp 1s ease-out 0.3s both;
        }
        
        .welcome-subtitle {
          font-size: 1.2rem;
          color: var(--el-text-color-regular);
          margin: 0;
          animation: fadeInUp 1s ease-out 0.5s both;
        }
      }
      
      .feature-list {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        
        .feature-item {
          display: flex;
          align-items: center;
          padding: 20px;
          background: white;
          border-radius: 12px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
          border: 1px solid var(--el-border-color-light);
          transition: all 0.3s ease;
          animation: fadeInUp 1s ease-out both;
          
          &:nth-child(1) { animation-delay: 0.7s; }
          &:nth-child(2) { animation-delay: 0.9s; }
          &:nth-child(3) { animation-delay: 1.1s; }
          &:nth-child(4) { animation-delay: 1.3s; }
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
            border-color: var(--el-color-primary-light-3);
          }
          
          .feature-icon {
            font-size: 1.5rem;
            margin-right: 12px;
            width: 40px;
            height: 40px;
            background: linear-gradient(135deg, var(--el-color-primary-light-7), var(--el-color-primary-light-5));
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
          }
          
          span {
            font-size: 14px;
            font-weight: 500;
            color: var(--el-text-color-primary);
          }
        }
      }
      
      .decorative-pattern {
        position: absolute;
        bottom: -50px;
        right: -50px;
        width: 200px;
        height: 200px;
        opacity: 0.1;
        z-index: 1;
      }
    }

    .register-form {
      width: 420px;
      padding: 50px 40px 45px;
      background-color: var(--el-bg-color);
      border-radius: 10px;
      box-shadow: rgb(0 0 0 / 10%) 0 2px 10px 2px;
      
      .register-logo {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 10px;
        
        .register-icon {
          width: 60px;
          height: 52px;
        }
        
        .logo-text {
          padding: 0 0 0 10px;
          margin: 0;
          font-size: 42px;
          font-weight: bold;
          color: #34495e;
          white-space: nowrap;
        }
      }

      .tenant-control {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 5px;
      }

      .current-tenant-info {
        margin-bottom: 2px;
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

      // 租户信息展示区域样式
      .tenant-info-display {
        display: flex;
        justify-content: center;
        margin-bottom: 15px;
        
        .current-tenant-badge {
          display: inline-flex;
          align-items: center;
          background-color: rgba(var(--el-color-primary-rgb), 0.1);
          color: var(--el-color-primary);
          padding: 8px 16px;
          border-radius: 20px;
          font-size: 14px;
          font-weight: 500;
          
          .el-icon {
            margin-right: 6px;
            font-size: 16px;
          }
        }
      }
      
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
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media screen and (width <= 1250px) {
  .register-left {
    display: none;
  }
}

@media screen and (width <= 600px) {
  .register-form {
    width: 97% !important;
  }
}
</style>

