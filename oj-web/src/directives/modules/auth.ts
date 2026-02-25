/**
 * v-auth
 * 按钮权限指令
 *
 * 使用方式：分为三种：1. 单一权限；2. 多权限and；3. 多权限or
 *
 *  1. v-auth="'sys.menu.create_btn'" 具有指定字符串''内的权限
 *  2. v-auth="[{ type: 'and', conditions: ['sys.role.setting_btn', 'sys.role.update_btn'] }]" 必须同时具有conditions数组包含的权限
 *  3. v-auth="[{ type: 'or',  conditions: ['sys.user.unlock_btn','sys.user.role_set_btn','sys.user.delete_btn' ] }]" 具有conditions数组内的任一权限
 *
 *
 */
import { useAuthStore } from '@/stores/modules/auth';
import type { Directive, DirectiveBinding } from 'vue';

const auth: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding;
    const authStore = useAuthStore();
    const currentBtnPermissions = authStore.authButtonListGet ?? [];
    const currentPageRoles: string[] = authStore.authRoleListGet ?? [];

    const ADMIN_ROLE = 'admin';
    const ADMIN_BYPASS = import.meta.env.VITE_ADMIN_BYPASS_PERMISSION || 'true';

    // 如果配置允许对admin用户放行，并且当前用户角色包含admin，则放行
    if (ADMIN_BYPASS === 'true' && currentPageRoles.includes(ADMIN_ROLE)) {
      return;
    }

    // 处理基础的单条件认证
    if (typeof value === 'string') {
      if (!currentBtnPermissions.includes(value)) {
        el.remove();
      }
      return;
    }
    // 处理 AND 条件的权限认证
    if (Array.isArray(value) && value.length === 1 && value[0].type === 'and' && Array.isArray(value[0].conditions)) {
      if (!value[0].conditions.every((item: string) => currentBtnPermissions.includes(item))) {
        el.remove();
      }
      return;
    }

    // 处理 OR 条件的权限认证
    if (Array.isArray(value) && value.length === 1 && value[0].type === 'or' && Array.isArray(value[0].conditions)) {
      if (!value[0].conditions.some((item: string) => currentBtnPermissions.includes(item))) {
        el.remove();
      }
      return;
    }
    // 如果传入的条件不符合以上任何一种格式，则移除元素
    el.remove();
  }
};

export default auth;
