<template>
  <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
    <el-form-item prop="username">
      <el-input v-model="loginForm.username" placeholder="用户名： admin">
        <template #prefix>
          <el-icon class="el-input__icon">
            <user />
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password" style="margin-bottom: 0">
      <el-input
        v-model="loginForm.password"
        type="password"
        placeholder="密码： 123456"
        show-password
        autocomplete="new-password"
      >
        <template #prefix>
          <el-icon class="el-input__icon">
            <lock />
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
  </el-form>
  <div class="login-btn">
    <el-button :icon="CircleClose" round size="large" @click="resetForm"> 重置 </el-button>
    <el-button :icon="UserFilled" round size="large" type="primary" :loading="loading" @click="login"> 登录 </el-button>
  </div>
  <!-- 测试账号快速登录 -->
  <div class="test-accounts">
    <div class="test-accounts__title">
      <el-divider>
        <span class="divider-text">测试账号</span>
      </el-divider>
    </div>
    <div class="test-accounts__list">
      <el-button
        v-for="account in testAccounts"
        :key="account.phone"
        :type="account.type"
        text
        bg
        size="small"
        :loading="loginAccount === account.phone && loading"
        @click="quickLogin(account)"
      >
        {{ account.name }}
      </el-button>
    </div>
  </div>
  <div v-if="IS_PREVIEW" style="margin-top: 20px; color: var(--el-color-warning)">
    <span>如无法登陆请联系作者：glowxq@qq.com</span>
  </div>
  <SliderCaptcha ref="captchaRef" @success="onSliderSuccess" @close="onCaptchaClose" />
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { HOME_URL, IS_PREVIEW } from '@/config';
import { getTimeState } from '@/utils';
import { loginApi } from '@/api/modules/system/admin/login';
import { useUserStore } from '@/stores/modules/user';
import { useTabsStore } from '@/stores/modules/tabs';
import { useKeepAliveStore } from '@/stores/modules/keepAlive';
import { CircleClose, Lock, User, UserFilled } from '@element-plus/icons-vue';
import { initDynamicRouter } from '@/router/modules/dynamicRouter';

import { onMounted, reactive, ref } from 'vue';
import { ElNotification } from 'element-plus';
import SliderCaptcha from '@/components/Common/Captcha/SliderCaptcha.vue';
import { getCaptchaStatus } from '@/api/modules/system/admin/captcha';
import { useAppStore } from '@/stores/modules/app';
import { getTenantEnableStatusApi } from '@/api/modules/system/tenant/tenant';

const router = useRouter();
const userStore = useUserStore();
const tabsStore = useTabsStore();
const keepAliveStore = useKeepAliveStore();
const appStore = useAppStore();

// 租户功能是否启用
const tenantEnabled = ref(true);

const loginFormRef = ref();
const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
});

const loading = ref(false);
const loginAccount = ref('');
const loginForm = reactive({
  username: '13667753053',
  password: '123456',
  clientId: '',
  grantType: ''
});

// 测试账号列表
const testAccounts = [
  { name: '管理员', phone: '13667753053', type: 'danger' as const },
  { name: '校长', phone: '13667753054', type: 'warning' as const },
  { name: '学生', phone: '13667753055', type: 'primary' as const },
  { name: '老师', phone: '13667753056', type: 'success' as const },
];

// 快速登录
const quickLogin = (account: { name: string; phone: string }) => {
  loginForm.username = account.phone;
  loginForm.password = '123456';
  loginAccount.value = account.phone;
  login();
};

const onSliderSuccess = async () => {
  await performLogin();
};

const performLogin = async () => {
  loading.value = true;
  try {
    // 登录前先重置应用状态
    appStore.reset();

    const { data } = await loginApi({ ...loginForm });
    userStore.setToken(data.accessToken);
    userStore.setUserInfo(data.userInfo);

    // 保存并应用后端返回的菜单类型
    if (data.menuType) {
      appStore.changeMenuType(data.menuType);

      // 根据菜单类型自动设置布局
      if (data.menuType === 'Client') {
        // 客户端模式 - 横向布局
        appStore.changeLayout('transverse');
      } else if (data.menuType === 'Admin') {
        // 管理端模式 - 纵向布局
        appStore.changeLayout('vertical');
      } else if (data.menuType === 'Common') {
        // 通用模式 - 分栏布局
        appStore.changeLayout('columns');
      }
    }

    await initDynamicRouter();

    tabsStore.closeMultipleTab();
    keepAliveStore.setKeepAliveName();

    router.push(HOME_URL);
    ElNotification({
      title: getTimeState(),
      message: '欢迎登录 Glowxq-Nexus',
      type: 'success',
      duration: 3000
    });
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
    }
  } catch (error) {
    console.error('获取租户功能启用状态失败:', error);
  }
};

const captchaRef = ref<InstanceType<typeof SliderCaptcha>>();
const login = () => {
  if (!loginFormRef.value) {
    return;
  }
  loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      return;
    }
    try {
      loading.value = true;
      const { data } = await getCaptchaStatus(); // 获取验证码状态
      if (data) {
        captchaRef.value?.acceptParams(); // 打开验证码弹窗
      } else {
        await performLogin(); // 执行登录
      }
    } finally {
      loading.value = false;
    }
  });
};

const resetForm = () => {
  if (!loginFormRef.value) return;
  loginFormRef.value.resetFields();
};
const onCaptchaClose = () => {
  resetForm();
};

onMounted(() => {
  // 获取租户功能启用状态
  getTenantEnableStatus();

  document.onkeydown = (e: KeyboardEvent) => {
    e = (window.event as KeyboardEvent) || e;
    if (e.code === 'Enter' || e.code === 'enter' || e.code === 'NumpadEnter') {
      if (loading.value || captchaRef.value?.dialogVisible) return;
      login();
    }
  };
});
</script>

<style scoped lang="scss">
@import '../index.scss';

.test-accounts {
  margin-top: 20px;

  &__title {
    .divider-text {
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
  }

  &__list {
    display: flex;
    justify-content: center;
    gap: 10px;
    flex-wrap: wrap;
  }
}
</style>
