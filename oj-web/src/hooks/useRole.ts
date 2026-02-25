import { computed } from 'vue';
import { useAppStore } from '@/stores/modules/app';
import { MenuType } from '@/enums/oj/system/MenuType';

/**
 * 角色判断 Hook
 * 基于当前菜单类型判断用户端类型
 */
export function useRole() {
  const appStore = useAppStore();

  /** 是否为管理端 */
  const isAdmin = computed(() => appStore.menuTypeCd === MenuType.ADMIN.code);

  /** 是否为学生端 */
  const isClient = computed(() => appStore.menuTypeCd === MenuType.CLIENT.code);

  /** 是否为管理端或通用端（非纯学生端） */
  const isAdminOrCommon = computed(
    () => appStore.menuTypeCd === MenuType.ADMIN.code || appStore.menuTypeCd === MenuType.COMMON.code
  );

  return { isAdmin, isClient, isAdminOrCommon };
}
