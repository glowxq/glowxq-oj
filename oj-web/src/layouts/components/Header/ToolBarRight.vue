<template>
  <div class="tool-bar-ri">
    <div class="header-icon">
      <TenantSwitch id="tenantSwitch" class="tenant-switch-wrapper" />
      <MenuChange id="menuChange" v-show="isAdminOrCommon" />
      <AssemblySize id="assemblySize" v-show="isAdminOrCommon" />
      <Language id="language" />
      <SearchMenu id="searchMenu" />
      <ThemeSetting id="themeSetting" v-show="isAdminOrCommon" />
      <!-- <Message id="message" /> -->
      <GithubLink id="githubLink" />
      <Fullscreen id="fullscreen" />
      <span v-if="buildVersion && buildVersion !== 'dev'" class="build-version" :title="'构建版本: ' + buildVersion">
        v{{ buildVersion }}
      </span>
    </div>
    <div class="user-info">
      <div class="user-details">
        <span class="user-name">{{ name || nickname }}</span>
        <span class="user-id">{{ maskedUsername }}</span>
      </div>
    </div>
    <Avatar ref="avatarRef" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useUserStore } from '@/stores/modules/user';
import { useRole } from '@/hooks/useRole';
import AssemblySize from './components/AssemblySize.vue';
import Language from './components/Language.vue';
import SearchMenu from './components/SearchMenu.vue';
import ThemeSetting from './components/ThemeSetting.vue';
import Message from './components/Message.vue';
import Fullscreen from './components/Fullscreen.vue';
import Avatar from './components/Avatar.vue';
import MenuChange from './components/MenuChange.vue';
import TenantSwitch from './components/TenantSwitch.vue';
import GithubLink from './components/GithubLink.vue';

const { isAdminOrCommon } = useRole();
const userStore = useUserStore();
const buildVersion = import.meta.env.VITE_APP_BUILD_VERSION || '';
const username = computed(() => userStore.userInfo.username);
const name = computed(() => userStore.userInfo.name);
const nickname = computed(() => userStore.userInfo.nickname);
const avatarRef = ref(null);

// 处理用户名显示，只显示后4位
const maskedUsername = computed(() => {
  const user = username.value;
  if (!user || user.length <= 4) {
    return user;
  }
  return '***' + user.slice(-4);
});

// 图标间距统一
const iconGap = 'var(--oj-space-4)';
</script>

<style scoped lang="scss">
.tool-bar-ri {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 25px;

  .header-icon {
    display: flex;
    align-items: center;

    & > * {
      margin-left: var(--oj-space-4);
      color: var(--el-header-text-color);
    }

    & > *:first-child {
      margin-left: 0;
    }

    .build-version {
      font-size: 10px;
      font-weight: 500;
      color: var(--el-header-text-color);
      opacity: 0.5;
      font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, monospace;
      white-space: nowrap;
      cursor: default;
      user-select: all;
    }

    .tenant-switch-wrapper {
      margin-right: 8px;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        right: -14px;
        top: 50%;
        height: 20px;
        width: 1px;
        background-color: rgba(0, 0, 0, 0.1);
        transform: translateY(-50%);
      }

      &.highlight-pulse {
        animation: pulse-animation 1.5s ease-in-out infinite;
      }
    }
  }

  .user-info {
    margin: 0 16px;
    padding: 6px 12px;
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 12px;
    backdrop-filter: blur(10px);
    transition: all 0.2s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.12);
      border-color: rgba(255, 255, 255, 0.18);
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .user-details {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 1px;

      .user-name {
        font-size: 13px;
        font-weight: 600;
        color: var(--el-header-text-color);
        line-height: 1.2;
        white-space: nowrap;
        letter-spacing: -0.01em;
      }

      .user-id {
        font-size: 11px;
        font-weight: 400;
        color: var(--el-header-text-color);
        opacity: 0.7;
        line-height: 1.2;
        font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
        letter-spacing: 0.5px;
      }
    }
  }
}

@keyframes pulse-animation {
  0% {
    box-shadow: 0 0 0 0 rgba(var(--el-color-primary-rgb), 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(var(--el-color-primary-rgb), 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(var(--el-color-primary-rgb), 0);
  }
}

// 深色模式适配
[data-theme='dark'] .tool-bar-ri {
  .user-info {
    background: rgba(0, 0, 0, 0.15);
    border-color: rgba(255, 255, 255, 0.08);

    &:hover {
      background: rgba(0, 0, 0, 0.25);
      border-color: rgba(255, 255, 255, 0.12);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .tool-bar-ri {
    padding-right: 15px;

    .user-info {
      margin: 0 8px;
      padding: 4px 8px;

      .user-details {
        .user-name {
          font-size: 12px;
        }

        .user-id {
          font-size: 10px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .tool-bar-ri {
    .user-info {
      margin: 0 4px;

      .user-details {
        .user-name {
          max-width: 80px;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }
}
</style>
