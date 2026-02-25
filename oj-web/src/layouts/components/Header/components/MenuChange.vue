<template>
  <el-dropdown trigger="click" @command="handleMenuTypeChange">
    <div class="menu-change-button">
      <el-icon class="toolBar-icon">
        <Menu />
      </el-icon>
      <span class="current-selected">{{ currentMenuTypeName }}</span>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in Object.values(MenuType)"
          :key="item.code"
          :command="item.code"
          :disabled="selectedMenuType === item.code"
        >
          <div class="menu-item">
            <span class="menu-name">{{ item.name }}</span>
            <span class="menu-desc">{{ item.text }}</span>
          </div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useAppStore } from '@/stores/modules/app';
import { useAuthStore } from '@/stores/modules/auth';
import { MenuType, matchCode } from '@/enums/oj/system/MenuType';
import { Menu } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 获取app store和auth store
const appStore = useAppStore();
const authStore = useAuthStore();

// 选中的菜单类型
const selectedMenuType = ref(appStore.menuTypeCd);

// 当前菜单类型名称
const currentMenuTypeName = computed(() => {
  const menuTypeInfo = matchCode(String(selectedMenuType.value));
  return menuTypeInfo ? menuTypeInfo.name : '通用';
});

// 处理菜单类型变更
const handleMenuTypeChange = async (type: string) => {
  if (selectedMenuType.value === type) return;

  // 更新本地选中值
  selectedMenuType.value = type;

  // 更新store
  appStore.changeMenuType(type);

  // 根据菜单类型自动切换布局
  if (type === MenuType.CLIENT.code) {
    // 客户端模式 - 横向布局
    appStore.changeLayout('transverse');
  } else if (type === MenuType.ADMIN.code) {
    // 管理端模式 - 纵向布局
    appStore.changeLayout('vertical');
  } else if (type === MenuType.COMMON.code) {
    // 通用模式 - 分栏布局
    appStore.changeLayout('columns');
  }

  // 直接刷新整个页面而不是只刷新菜单
  ElMessage.info('正在切换菜单类型，页面即将刷新...');

  // 使用setTimeout确保消息能够显示，并且store数据能保存
  setTimeout(() => {
    // 使用replace而不是reload，将当前URL替换为根路径
    window.location.href = '/';
  }, 100);
};

// 组件挂载时，确保本地状态与store同步
onMounted(() => {
  // 清除本地存储中可能的旧值
  localStorage.removeItem('menuType');

  // 强制使用store中的menuType，这个可能是后端返回并已保存的值
  selectedMenuType.value = appStore.menuTypeCd;

  // 如果没有值，设置默认值为Common
  if (!selectedMenuType.value) {
    selectedMenuType.value = MenuType.COMMON.code;
    appStore.changeMenuType(String(MenuType.COMMON.code));

    // 同步更新布局
    appStore.changeLayout('columns');
  }
});
</script>

<style lang="scss" scoped>
.menu-change-button {
  display: flex;
  align-items: center;
  padding: 5px 7px;
  border-radius: 4px;
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    background-color: var(--el-fill-color-light);
  }

  .current-selected {
    margin-left: 6px;
    font-size: 14px;
    color: var(--el-header-text-color);
  }
}

.menu-item {
  display: flex;
  flex-direction: column;
  padding: 4px 0;

  .menu-name {
    font-size: 14px;
    font-weight: bold;
    color: var(--el-text-color-primary);
  }

  .menu-desc {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-top: 4px;
  }
}

:deep(.el-dropdown-menu__item:not(.is-disabled):hover) {
  background-color: var(--el-color-primary-light-9);
}

:deep(.el-dropdown-menu) {
  padding: 5px 0;
}

:deep(.el-dropdown-menu__item) {
  line-height: 1.5;
  padding: 8px 20px;
}

.toolBar-icon {
  font-size: 18px;
  color: var(--el-header-text-color);
}
</style>
