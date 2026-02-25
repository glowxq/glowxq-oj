<template>
  <el-config-provider :locale="locale" :size="assemblySize" :button="buttonConfig">
    <RouterView />
    <teacher-helper />
  </el-config-provider>
</template>

<script setup lang="ts">
import { useTheme } from '@/hooks/useTheme';
import { useAppStore } from '@/stores/modules/app';
import { useI18n } from 'vue-i18n';
import { getBrowserLang } from '@/utils';
import en from 'element-plus/es/locale/lang/en';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import type { LanguageType } from '@/stores/interface/app';
import { computed, onMounted, reactive } from 'vue';
import TeacherHelper from '@/components/Oj/Teacher/TeacherHelper.vue'

const appStore = useAppStore();

// init theme
const { initTheme } = useTheme();
initTheme();

// init language
const i18n = useI18n();
onMounted(() => {
  const language = appStore.language ?? getBrowserLang();
  i18n.locale.value = language;
  appStore.changeLanguage(language as LanguageType);
});

// element language
const locale = computed(() => {
  if (appStore.language === 'zh') return zhCn;
  if (appStore.language === 'en') return en;
  return getBrowserLang() === 'zh' ? zhCn : en;
});

// element assemblySize
const assemblySize = computed(() => appStore.assemblySize);

// element button config
const buttonConfig = reactive({ autoInsertSpace: false });
</script>
