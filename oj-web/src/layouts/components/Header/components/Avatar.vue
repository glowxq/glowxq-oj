<template>
  <el-dropdown trigger="click">
    <div class="avatar">
      <img :src="avatarSrc || ''" alt="avatar" />
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="openDialog('infoRef')">
          <el-icon>
            <User />
          </el-icon>
          {{ $t('header.personalData') }}
        </el-dropdown-item>
        <el-dropdown-item @click="openDialog('passwordRef')">
          <el-icon>
            <Edit />
          </el-icon>
          {{ $t('header.changePassword') }}
        </el-dropdown-item>
        <el-dropdown-item divided @click="logout">
          <el-icon>
            <SwitchButton />
          </el-icon>
          {{ $t('header.logout') }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <!-- infoDialog -->
  <InfoDialog ref="infoRef" />
  <!-- passwordDialog -->
  <PasswordDialog ref="passwordRef" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { LOGIN_URL } from '@/config';
import { useRouter } from 'vue-router';
import { logoutApi } from '@/api/modules/system/admin/login';
import { useUserStore } from '@/stores/modules/user';
import { useAuthStore } from '@/stores/modules/auth';
import InfoDialog from './InfoDialog.vue';
import PasswordDialog from './PasswordDialog.vue';
import { Edit, SwitchButton, User } from '@element-plus/icons-vue';
import { useSocketStore } from '@/stores/modules/socket';
import { ElMessage, ElMessageBox } from 'element-plus';
import defaultAvatar from '@/assets/images/avatar.gif';

const router = useRouter();
const userStore = useUserStore();
const socketStore = useSocketStore();
const authStore = useAuthStore();
const avatarSrc = ref<string | null>(null);
/**
 * 预加载图片，判断图片是否可以正常加载
 * @param url
 */
const preloadImage = (url: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve();
    img.onerror = () => reject();
    img.src = url;
  });
};

const getLogo = async () => {
  const logo = userStore.userInfo.logo || defaultAvatar;
  try {
    await preloadImage(logo);
    avatarSrc.value = logo;
  } catch (error) {
    console.error(`Error loading image: ${logo}`);
    avatarSrc.value = defaultAvatar; // 默认头像地址
  }
};

// 退出登录
const logout = () => {
  ElMessageBox.confirm('您是否确认退出登录?', '温馨提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 1.执行退出登录接口
      await logoutApi();
    } catch (error) {
      console.error('退出登录API调用失败:', error);
    } finally {
      // 清除状态并重定向到登录页，不管API是否成功
      handleLogout();
    }
  });
};

// 处理登出逻辑，可以被其他组件调用
const handleLogout = () => {
  // 清除 Token 和状态
  userStore.clear();
  authStore.clear();
  // 关闭socket
  socketStore.close();

  // 重定向到登陆页
  router.replace(LOGIN_URL);
  ElMessage.success('退出登录成功！');
};

// 打开修改密码和个人信息弹窗
const infoRef = ref<InstanceType<typeof InfoDialog>>();
const passwordRef = ref<InstanceType<typeof PasswordDialog>>();
const openDialog = (ref: string) => {
  if (ref === 'infoRef') infoRef.value?.openDialog();
  if (ref === 'passwordRef') passwordRef.value?.openDialog();
};
getLogo();

// 将退出登录逻辑导出，供其他组件使用
defineExpose({
  handleLogout
});
</script>

<style scoped lang="scss">
.avatar {
  width: 40px;
  height: 40px;
  overflow: hidden;
  cursor: pointer;
  border-radius: 50%;

  img {
    width: 100%;
    height: 100%;
  }
}
</style>
