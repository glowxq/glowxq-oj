<template>
  <el-dropdown trigger="click" @command="changeLanguage">
    <i :class="'iconfont icon-zhongyingwen'" class="toolBar-icon" />
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in languageList"
          :key="item.value"
          :command="item.value"
          :disabled="language === item.value"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
import { useAppStore } from '@/stores/modules/app';
import type { LanguageType } from '@/stores/interface/app';

const i18n = useI18n();
const appStore = useAppStore();
const language = computed(() => appStore.language);

const languageList = [
  { label: '简体中文', value: 'zh' },
  { label: 'English', value: 'en' }
];

const changeLanguage = (lang: string) => {
  i18n.locale.value = lang;
  appStore.changeLanguage(lang as LanguageType);
};
</script>
