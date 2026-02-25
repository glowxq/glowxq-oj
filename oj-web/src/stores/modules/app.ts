import { defineStore } from 'pinia';
import { DEFAULT_PRIMARY } from '@/config';
import type { AssemblySizeType, LanguageType, LayoutType } from '@/stores/interface/app';
import piniaPersistConfig from '@/stores/helper/persist';
import { ref } from 'vue';
import { useAuthStore } from '@/stores/modules/auth';
import { MenuType } from '@/enums/oj/system/MenuType';

export interface AppState {
  name: string;
  token: string;
  isCollapse: boolean;
  isDark: boolean;
  isWeak: boolean;
  isGrey: boolean;
  layout: LayoutType;
  primary: string;
  menuTypeCd: string;
  // 外链的外层是否需要layout
  disableLayout: boolean;
  // 是否去除Tab
  disableTabs: boolean;
  headerInverted: boolean;
  asideInverted: boolean;
  assemblySize: AssemblySizeType;
  language: string;
}

export const useAppStore = defineStore(
  'app',
  () => {
    // 布局模式 (纵向：vertical | 经典：classic | 横向：transverse | 分栏：columns)
    const layout = ref<LayoutType>('classic');
    // element 组件大小
    const assemblySize = ref<AssemblySizeType>('default');
    // 当前系统语言
    const language = ref<LanguageType>(null);
    // 当前页面是否全屏
    const maximize = ref(false);
    // 主题颜色
    const primary = ref(DEFAULT_PRIMARY);
    // 深色模式
    const isDark = ref(false);
    // 灰色模式
    const isGrey = ref(false);
    // 色弱模式
    const isWeak = ref(false);
    // 侧边栏反转
    const asideInverted = ref(false);
    // 头部反转
    const headerInverted = ref(false);
    // 折叠菜单
    const isCollapse = ref(false);
    // 菜单手风琴
    const accordion = ref(false);
    // 面包屑导航
    const breadcrumb = ref(true);
    // 面包屑导航图标
    const breadcrumbIcon = ref(true);
    // 标签页
    const tabs = ref(false);
    // 标签页图标
    const tabsIcon = ref(false);
    // 页脚
    const footer = ref(true);
    // 菜单类型
    const menuTypeCd = ref(MenuType.COMMON.code);
    // 是否禁用layout布局
    const disableLayout = ref(false);
    // 是否禁用Tabs标签页
    const disableTabs = ref(false);

    const changeLayout = (val: LayoutType) => (layout.value = val);
    const changeAssemblySize = (val: AssemblySizeType) => (assemblySize.value = val);
    const changeLanguage = (val: LanguageType) => (language.value = val);
    const changeMaximize = (val: boolean) => (maximize.value = val);
    const changePrimary = (val: string) => (primary.value = val);
    const changeIsDark = (val: boolean) => (isDark.value = val);
    const changeIsGrey = (val: boolean) => (isGrey.value = val);
    const changeIsWeak = (val: boolean) => (isWeak.value = val);
    const changeAsideInverted = (val: boolean) => (asideInverted.value = val);
    const changeHeaderInverted = (val: boolean) => (headerInverted.value = val);
    const changeIsCollapse = (val: boolean) => (isCollapse.value = val);
    const changeAccordion = (val: boolean) => (accordion.value = val);
    const changeBreadcrumb = (val: boolean) => (breadcrumb.value = val);
    const changeBreadcrumbIcon = (val: boolean) => (breadcrumbIcon.value = val);
    const changeTabs = (val: boolean) => (tabs.value = val);
    const changeTabsIcon = (val: boolean) => (tabsIcon.value = val);
    const changeFooter = (val: boolean) => (footer.value = val);
    const changeMenuType = (val: string) => (menuTypeCd.value = val);
    const changeDisableLayout = (val: boolean) => (disableLayout.value = val);
    const changeDisableTabs = (val: boolean) => (disableTabs.value = val);

    const reset = () => {
      const authStore = useAuthStore();
      // 清空auth
      authStore.clear();
      // 重置store
      isDark.value = false;
      isWeak.value = false;
      isGrey.value = false;
      layout.value = 'classic';
      primary.value = DEFAULT_PRIMARY;
      language.value = null;
      isCollapse.value = false;
      disableLayout.value = false;
      disableTabs.value = false;
      headerInverted.value = false;
      asideInverted.value = false;
      assemblySize.value = 'default';
      menuTypeCd.value = MenuType.COMMON.code;
      maximize.value = false;
      accordion.value = false;
      breadcrumb.value = true;
      breadcrumbIcon.value = true;
      tabs.value = false;
      tabsIcon.value = true;
      footer.value = true;
    };

    return {
      layout,
      assemblySize,
      language,
      maximize,
      primary,
      isDark,
      isGrey,
      isWeak,
      asideInverted,
      headerInverted,
      isCollapse,
      accordion,
      breadcrumb,
      breadcrumbIcon,
      tabs,
      tabsIcon,
      footer,
      menuTypeCd,
      disableLayout,
      disableTabs,
      changeLayout,
      changeAssemblySize,
      changeLanguage,
      changeMaximize,
      changePrimary,
      changeIsDark,
      changeIsGrey,
      changeIsWeak,
      changeAsideInverted,
      changeHeaderInverted,
      changeIsCollapse,
      changeAccordion,
      changeBreadcrumb,
      changeBreadcrumbIcon,
      changeTabs,
      changeTabsIcon,
      changeFooter,
      changeMenuType,
      changeDisableLayout,
      changeDisableTabs,
      reset
    };
  },
  {
    persist: piniaPersistConfig('app', [
      'isDark',
      'layout',
      'primary',
      'menuTypeCd',
      'language',
      'isGrey',
      'isWeak'
    ])
  }
);
