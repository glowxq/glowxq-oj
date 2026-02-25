import router from '@/router';
import { defineStore } from 'pinia';
import { useKeepAliveStore } from '@/stores/modules/keepAlive';
import piniaPersistConfig from '@/stores/helper/persist';
import type { TabsMenuProps } from '@/stores/interface/tabs';
import { ref } from 'vue';

const keepAliveStore = useKeepAliveStore();

export const useTabsStore = defineStore(
  'tabs',
  () => {
    const tabsMenuList = ref<TabsMenuProps[]>([]);

    // Add Tabs
    async function addTabs(tabItem: TabsMenuProps) {
      const index = tabsMenuList.value.findIndex(item => item.path === tabItem.path);
      if (index === -1) {
        tabsMenuList.value.push(tabItem);
      } else {
        tabsMenuList.value.splice(index, 1, tabItem);
      }
      if (!keepAliveStore.keepAliveName.includes(tabItem.name) && tabItem.isKeepAlive) {
        keepAliveStore.addKeepAliveName(tabItem.name);
      }
    }

    // Remove Tabs
    async function removeTabs(tabPath: string, isCurrent = true) {
      const tabsMenus = tabsMenuList.value;
      if (isCurrent) {
        tabsMenus.forEach((item, index) => {
          if (item.path !== tabPath) return;
          const nextTab = tabsMenus[index + 1] || tabsMenus[index - 1];
          if (!nextTab) return;
          router.push(nextTab.path);
        });
      }
      tabsMenuList.value = tabsMenus.filter(item => item.path !== tabPath);
    }

    // Close Tabs On Side
    async function closeTabsOnSide(path: string, type: string) {
      const currentIndex = tabsMenuList.value.findIndex(item => item.path === path);
      if (currentIndex !== -1) {
        const range = type === 'left' ? [0, currentIndex] : [currentIndex + 1, tabsMenuList.value.length];
        tabsMenuList.value = tabsMenuList.value.filter((item, index) => {
          return index < range[0] || index >= range[1] || !item.close;
        });
      }
      keepAliveStore.setKeepAliveName(tabsMenuList.value.map(item => item.name));
    }

    // Close MultipleTab
    async function closeMultipleTab(tabsMenuValue?: string) {
      tabsMenuList.value = tabsMenuList.value.filter(item => {
        return item.path === tabsMenuValue || !item.close;
      });
      keepAliveStore.setKeepAliveName(tabsMenuList.value.map(item => item.name));
    }

    // Set Tabs
    async function setTabs(menus: TabsMenuProps[]) {
      tabsMenuList.value = menus;
    }

    // Set Tabs Title
    async function setTabsTitle(title: string) {
      const nowFullPath = location.hash.substring(1);
      tabsMenuList.value.forEach(item => {
        if (item.path === nowFullPath) item.title = title;
      });
    }

    return {
      tabsMenuList,
      addTabs,
      removeTabs,
      closeTabsOnSide,
      closeMultipleTab,
      setTabs,
      setTabsTitle
    };
  },
  {
    persist: piniaPersistConfig('tabs')
  }
);
