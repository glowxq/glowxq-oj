import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useTenantStore = defineStore('tenant', () => {
  // 从localStorage读取已存储的租户信息
  const tenantId = ref<string>(localStorage.getItem('tenantId') || '');
  const tenantName = ref<string>(localStorage.getItem('tenantName') || '');
  const tenantCode = ref<string>(localStorage.getItem('tenantCode') || '');
  const logoUrl = ref<string>(localStorage.getItem('logoUrl') || '');
  const systemName = ref<string>(localStorage.getItem('systemName') || '');
  const themeColor = ref<string>(localStorage.getItem('themeColor') || '');

  // 设置租户信息
  const setTenantInfo = (info: {
    tenantId: string;
    tenantName: string;
    tenantCode: string;
    logoUrl?: string;
    systemName?: string;
    themeColor?: string;
  }) => {
    console.log('Store - 设置租户信息:', info);
    
    tenantId.value = info.tenantId;
    tenantName.value = info.tenantName;
    tenantCode.value = info.tenantCode;
    
    // 设置logoUrl，如果未提供或为空字符串，则明确设置为空
    logoUrl.value = info.logoUrl || '';
    // 设置systemName，如果未提供或为空字符串，则明确设置为空
    systemName.value = info.systemName || '';
    // 设置themeColor，如果未提供或为空字符串，则明确设置为空
    themeColor.value = info.themeColor || '';
    
    // 同步保存到localStorage
    localStorage.setItem('tenantId', tenantId.value);
    localStorage.setItem('tenantName', tenantName.value);
    localStorage.setItem('tenantCode', tenantCode.value);
    localStorage.setItem('logoUrl', logoUrl.value);
    localStorage.setItem('systemName', systemName.value);
    localStorage.setItem('themeColor', themeColor.value);
    
    console.log('Store - 租户信息已保存，当前值:', {
      tenantId: tenantId.value,
      tenantName: tenantName.value,
      tenantCode: tenantCode.value,
      logoUrl: logoUrl.value,
      systemName: systemName.value,
      themeColor: themeColor.value
    });
  };

  // 清除租户信息
  const clearTenantInfo = () => {
    console.log('Store - 清除租户信息');
    
    tenantId.value = '';
    tenantName.value = '';
    tenantCode.value = '';
    logoUrl.value = '';
    systemName.value = '';
    themeColor.value = '';
    
    localStorage.removeItem('tenantId');
    localStorage.removeItem('tenantName');
    localStorage.removeItem('tenantCode');
    localStorage.removeItem('logoUrl');
    localStorage.removeItem('systemName');
    localStorage.removeItem('themeColor');
    
    console.log('Store - 租户信息已清除');
  };

  return {
    tenantId,
    tenantName,
    tenantCode,
    logoUrl,
    systemName,
    themeColor,
    setTenantInfo,
    clearTenantInfo
  };
}); 