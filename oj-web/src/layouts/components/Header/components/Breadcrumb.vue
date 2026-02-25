<template>
  <div :class="['breadcrumb-box mask-image', !appStore.breadcrumbIcon && 'no-icon']">
    <el-breadcrumb :separator-icon="ArrowRight">
      <transition-group name="breadcrumb">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.path">
          <div class="el-breadcrumb__inner is-link" @click="onBreadcrumbClick(item, index)">
            <el-icon v-if="item.meta.icon && appStore.breadcrumbIcon" class="breadcrumb-icon">
              <SvgIcon v-if="item.meta.icon.startsWith('svg-')" :name="item.meta.icon.substring(4)" />
              <component v-else :is="item.meta.icon" />
            </el-icon>
            <span class="breadcrumb-title">{{ item.meta.title }}</span>
          </div>
        </el-breadcrumb-item>
      </transition-group>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowRight } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/modules/auth';
import { HOME_URL } from '@/config';
import { useAppStore } from '@/stores/modules/app';
import SvgIcon from '@/components/Common/SvgIcon/index.vue';
import { MENU_DIR } from '@/config/consts';
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const appStore = useAppStore();

const breadcrumbList = computed(() => {
  const path = route.matched[route.matched.length - 1].path;
  let breadcrumbData = authStore.breadcrumbListGet[path] ?? [];
  if (path === HOME_URL) {
    breadcrumbData = [{ path: HOME_URL, meta: { icon: 'HomeFilled', title: '首页' } }, ...breadcrumbData];
  }
  // 🙅‍♀️不需要首页面包屑可删除以下判断
  if (breadcrumbData.length > 0 && breadcrumbData[0].path !== HOME_URL) {
    breadcrumbData = [{ path: HOME_URL, meta: { icon: 'HomeFilled', title: '首页' } }, ...breadcrumbData];
  }
  return breadcrumbData;
});

// Click Breadcrumb
const onBreadcrumbClick = (item: Menu.MenuOptions, index: number) => {
  if (index !== breadcrumbList.value.length - 1 && item.menuTypeCd !== MENU_DIR) router.push(item.path);
};
</script>

<style scoped lang="scss">
.breadcrumb-box {
  display: flex;
  align-items: center;
  overflow: hidden;

  .el-breadcrumb {
    white-space: nowrap;

    .el-breadcrumb__item {
      position: relative;
      display: inline-block;
      float: none;

      .el-breadcrumb__inner {
        display: inline-flex;

        &.is-link {
          color: var(--el-header-text-color);

          &:hover {
            color: var(--el-color-primary);
          }
        }

        .breadcrumb-icon {
          margin-top: 2px;
          margin-right: 6px;
          font-size: 16px;
        }

        .breadcrumb-title {
          margin-top: 3px;
        }
      }

      &:last-child .el-breadcrumb__inner,
      &:last-child .el-breadcrumb__inner:hover {
        color: var(--el-header-text-color-regular);
      }

      :deep(.el-breadcrumb__separator) {
        position: relative;
        top: -1px;
      }
    }
  }
}

.no-icon {
  .el-breadcrumb {
    .el-breadcrumb__item {
      top: -2px;

      :deep(.el-breadcrumb__separator) {
        top: 2px;
      }
    }
  }
}
</style>
