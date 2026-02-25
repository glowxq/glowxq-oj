<template>
  <div class="tenant-switch" v-if="tenantEnabled">
    <el-popover 
      placement="bottom" 
      :width="700" 
      trigger="click"
      @show="getTenantList"
    >
      <template #reference>
        <div class="current-tenant">
          <el-badge :value="tenantStore.tenantName ? 'active' : 'click'" type="primary">
            <el-tooltip
              :content="tenantStore.tenantName ? `当前租户: ${tenantStore.tenantName}` : '点击切换租户'"
              placement="bottom"
            >
              <div class="tenant-trigger">
                <el-icon class="toolBar-icon"><OfficeBuilding /></el-icon>
                <span class="tenant-label">{{ tenantStore.tenantName || '切换租户' }}</span>
              </div>
            </el-tooltip>
          </el-badge>
        </div>
      </template>
      
      <div class="tenant-popover-content">
        <div class="tenant-header">
          <div class="tenant-title-wrapper">
            <el-icon class="tenant-icon"><OfficeBuilding /></el-icon>
            <span class="tenant-title">租户切换</span>
            <el-tag size="small" type="info" effect="plain" class="tenant-count-tag">
              共 {{ tenantItems.length }} 个租户
            </el-tag>
          </div>
          <el-badge v-if="tenantStore.tenantName" :value="1" type="primary">
            <span class="current-tenant-name">{{ tenantStore.tenantName }}</span>
          </el-badge>
        </div>

        <!-- 搜索栏 -->
        <div class="tenant-search">
          <el-input
            v-model="tenantKey"
            placeholder="输入租户Key搜索"
            clearable
            class="tenant-key-input"
            @keyup.enter="searchByTenantKey"
          >
            <template #prefix>
              <el-icon class="search-prefix-icon"><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="searchByTenantKey" type="primary" class="search-btn">
                <el-icon><ArrowRight /></el-icon>
                <span>切换</span>
              </el-button>
            </template>
          </el-input>
        </div>
        
        <!-- 租户列表 -->
        <div v-loading="loading" class="tenant-list">
          <div v-if="tenantItems.length > 0" class="tenant-grid">
            <div
              v-for="item in tenantItems"
              :key="item.id"
              class="tenant-item"
              :class="{ 'is-active': tenantStore.tenantId === item.tenantId }"
              @click="handleTenantClick(item)"
            >
              <div class="tenant-item-left">
                <div class="tenant-logo">
                  <img v-if="item.logoUrl" :src="item.logoUrl" alt="logo" />
                  <el-icon v-else><OfficeBuilding /></el-icon>
                </div>
                <div class="tenant-info">
                  <span class="tenant-name">{{ item.tenantName }}</span>
                  <span class="tenant-code">{{ item.tenantCode }}</span>
                </div>
              </div>
              <div class="tenant-item-right">
                <el-tag v-if="item.enable" type="success" size="small">正常</el-tag>
                <el-tag v-else type="danger" size="small">禁用</el-tag>
                
                <div v-if="item.password" class="lock-wrapper" title="需要密码验证">
                  <el-icon class="lock-icon"><Lock /></el-icon>
                </div>
                
                <!-- 添加直达链接按钮 -->
                <div class="share-dropdown-wrapper" @click.stop title="复制租户链接">
                  <el-dropdown trigger="click" class="share-dropdown">
                    <el-icon class="share-icon"><Share /></el-icon>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click.stop="copyDirectLink(item.tenantId)">
                          <el-icon><Link /></el-icon>
                          <span>复制租户直达链接</span>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-else class="tenant-empty">
            <el-empty description="暂无租户信息" />
            <div class="empty-tip">
              <p>可能原因：</p>
              <ul>
                <li>系统中尚未配置任何租户</li>
                <li>您没有访问租户的权限</li>
                <li>网络连接问题导致获取失败</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </el-popover>

    <!-- 密码验证对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="租户密码验证"
      width="420px"
      :close-on-click-modal="false"
      :append-to-body="true"
      destroy-on-close
    >
      <div class="password-dialog-content">
        <div class="tenant-info-display">
          <div class="tenant-logo-container">
            <img 
              v-if="selectedTenant?.logoUrl" 
              :src="selectedTenant.logoUrl" 
              alt="logo" 
              class="tenant-logo-large"
            />
            <el-icon v-else class="tenant-logo-placeholder"><OfficeBuilding /></el-icon>
          </div>
          <p class="tenant-name-display">{{ selectedTenant?.tenantName }}</p>
          <p class="tenant-id-display">{{ selectedTenant?.tenantId }}</p>
        </div>
        <p class="password-tip">该租户需要密码验证，请输入密码</p>
        <el-input
          v-model="inputPassword"
          type="password"
          placeholder="请输入租户密码"
          show-password
          @keyup.enter="verifyPassword"
          class="password-input"
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
        <div v-if="passwordError" class="password-error">
          <el-icon><WarningFilled /></el-icon>
          <span>{{ passwordError }}</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="passwordDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="verifyPassword" :loading="verifyLoading" class="confirm-btn">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, inject } from 'vue';
import { useTenantStore } from '@/stores/modules/tenant';
import { useUserStore } from '@/stores/modules/user';
import { useAuthStore } from '@/stores/modules/auth';
import { useSocketStore } from '@/stores/modules/socket';
import { getTenantListAllApi, getTenantByTenantKeyApi, getTenantEnableStatusApi } from '@/api/modules/system/tenant/tenant';
import type { ITenant } from '@/api/interface/system/tenant/tenant';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, OfficeBuilding, Lock, WarningFilled, Share, Link, ArrowRight } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { LOGIN_URL } from '@/config';

const tenantStore = useTenantStore();
const tenantItems = ref<ITenant.Row[]>([]);
const tenantKey = ref('');
const loading = ref(false);
const router = useRouter();

// 租户功能是否启用
const tenantEnabled = ref(true);

// 密码验证相关
const passwordDialogVisible = ref(false);
const selectedTenant = ref<ITenant.Row | null>(null);
const inputPassword = ref('');
const passwordError = ref('');
const verifyLoading = ref(false);

// 使用示例数据
const useMockData = () => {
  const mockData = [
    {
      "id": 1,
      "tenantId": "GLOWXQ0000",
      "tenantCode": "GLOWXQ0000",
      "tenantName": "Glowxq-OJ",
      "contactName": "吴一一",
      "contactPhone": "19323030408",
      "contactEmail": "glowxq@qq.com",
      "show": true,
      "password": "123456",
      "enable": true,
      "expiredTime": "2099-06-06 16:36:35",
      "maxUserNum": 100000,
      "currentUserNum": 1,
      "logoUrl": "",
      "text": "",
      "systemName": "",
      "homeImageUrl": "",
      "themeColor": "#1890ff",
      "config": "",
      "customDomain": ""
    }
  ];
  
  return mockData;
};

// 获取租户列表
const getTenantList = async () => {
  loading.value = true;
  try {
    // 真实API调用
    const res = await getTenantListAllApi();
    if (res.code === '0000' && res.data) {
      // 检查返回数据格式
      if (typeof res.data === 'object' && 'rows' in res.data && Array.isArray(res.data.rows)) {
        // 如果是分页数据结构
        tenantItems.value = res.data.rows;
      } else if (Array.isArray(res.data)) {
        // 如果直接返回数组
        tenantItems.value = res.data;
      } else {
        console.warn('返回的租户数据格式无法识别，使用模拟数据:', res.data);
        tenantItems.value = useMockData();
      }
    } else {
      console.warn('获取租户列表失败，使用模拟数据:', res.message);
      tenantItems.value = useMockData();
    }
  } catch (error) {
    console.error('获取租户列表异常，使用模拟数据:', error);
    tenantItems.value = useMockData();
  } finally {
    loading.value = false;
  }
};

// 点击租户时的处理
const handleTenantClick = (tenant: ITenant.Row) => {
  if (!tenant.enable) {
    ElMessage.warning('该租户已被禁用，无法切换');
    return;
  }

  // 检查是否需要密码验证
  if (tenant.password) {
    // 显示密码验证对话框
    selectedTenant.value = tenant;
    inputPassword.value = '';
    passwordError.value = '';
    passwordDialogVisible.value = true;
  } else {
    // 无需密码，直接切换
    switchTenant(tenant);
  }
};

// 验证租户密码
const verifyPassword = () => {
  if (!selectedTenant.value) return;
  
  if (!inputPassword.value) {
    passwordError.value = '请输入密码';
    return;
  }
  
  passwordError.value = '';
  verifyLoading.value = true;

  // 延迟一下模拟网络请求
  setTimeout(() => {
    if (inputPassword.value === selectedTenant.value!.password) {
      // 密码正确，切换租户
      passwordDialogVisible.value = false;
      switchTenant(selectedTenant.value!);
    } else {
      // 密码错误
      passwordError.value = '密码错误，请重新输入';
      // 轻微震动输入框
      const passwordInput = document.querySelector('.password-input');
      if (passwordInput) {
        passwordInput.classList.add('shake-animation');
        setTimeout(() => {
          passwordInput.classList.remove('shake-animation');
        }, 500);
      }
    }
    verifyLoading.value = false;
  }, 500);
};

// 根据租户Key搜索
const searchByTenantKey = async () => {
  if (!tenantKey.value) {
    ElMessage.warning('请输入租户Key');
    return;
  }
  
  loading.value = true;
  try {
    const res = await getTenantByTenantKeyApi(tenantKey.value);
    if (res.code === '0000' && res.data) {
      // 检查是否有密码
      if (res.data.password) {
        // 显示密码验证对话框
        selectedTenant.value = res.data;
        inputPassword.value = '';
        passwordError.value = '';
        passwordDialogVisible.value = true;
      } else {
        // 无需密码，弹出确认对话框
        ElMessageBox.confirm(
          `是否切换到租户: ${res.data.tenantName}?`,
          '租户切换确认',
          {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          switchTenant(res.data);
        }).catch(() => {
          // 用户取消操作
        });
      }
    } else {
      ElMessage.error(res.message || '未找到匹配的租户');
    }
  } catch (error) {
    console.error('搜索租户失败:', error);
    ElMessage.error('搜索租户时发生错误');
  } finally {
    loading.value = false;
  }
};

// 切换租户
const switchTenant = (tenant: ITenant.Row) => {
  // 保存租户信息到store
  tenantStore.setTenantInfo({
    tenantId: tenant.tenantId || '',
    tenantName: tenant.tenantName || '',
    tenantCode: tenant.tenantCode || '',
    logoUrl: tenant.logoUrl || '',
    systemName: tenant.systemName || '',
    themeColor: tenant.themeColor || ''
  });
  
  ElMessage.success({
    message: `已切换到租户: ${tenant.tenantName}，即将跳转到登录页`,
    duration: 1500
  });
  
  const userStore = useUserStore();
  const authStore = useAuthStore();
  const socketStore = useSocketStore();
  
  // 检查用户是否已登录
  if (userStore.token) {
    // 已登录状态，需要清除用户状态并重新登录
    setTimeout(() => {
      // 清除Token和状态
      userStore.clear();
      authStore.clear();
      socketStore.close();
      
      // 直接跳转到登录页
      window.location.href = LOGIN_URL;
    }, 1500);
  } else {
    // 未登录状态，直接跳转到登录页
    setTimeout(() => {
      router.push(LOGIN_URL);
    }, 1500);
  }
};

// 复制租户直达链接
const copyDirectLink = (tenantKey: string) => {
  if (!tenantKey) {
    ElMessage.warning('租户Key不能为空');
    return;
  }
  
  // 生成租户直达链接
  const currentOrigin = window.location.origin;
  const directLink = `${currentOrigin}/tenant/${tenantKey}`;
  
  // 尝试使用现代 Clipboard API
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard.writeText(directLink)
      .then(() => {
        ElMessage.success('租户直达链接已复制到剪贴板');
      })
      .catch((err) => {
        console.warn('Clipboard API 复制失败，尝试降级方案', err);
        fallbackCopyDirectLink(directLink);
      });
  } else {
    // 降级方案：使用传统方法
    fallbackCopyDirectLink(directLink);
  }
};

// 降级复制方案（兼容旧浏览器和非HTTPS环境）
const fallbackCopyDirectLink = (text: string) => {
  const textArea = document.createElement('textarea');
  textArea.value = text;
  
  // 避免在 iOS 上出现缩放
  textArea.style.fontSize = '16px';
  
  // 隐藏元素
  textArea.style.position = 'fixed';
  textArea.style.left = '-999999px';
  textArea.style.top = '-999999px';
  textArea.style.opacity = '0';
  textArea.style.pointerEvents = 'none';
  
  document.body.appendChild(textArea);
  
  // 针对移动设备的特殊处理
  if (navigator.userAgent.match(/ipad|ipod|iphone/i)) {
    // iOS 设备需要特殊处理
    const range = document.createRange();
    range.selectNodeContents(textArea);
    const selection = window.getSelection();
    if (selection) {
      selection.removeAllRanges();
      selection.addRange(range);
    }
    textArea.setSelectionRange(0, 999999);
  } else {
    textArea.select();
    textArea.focus();
  }
  
  try {
    const successful = document.execCommand('copy');
    if (successful) {
      ElMessage.success('租户直达链接已复制到剪贴板');
    } else {
      ElMessage.error('复制失败，请手动复制链接：' + text);
    }
  } catch (err) {
    console.error('复制操作失败', err);
    // 显示链接让用户手动复制
    ElMessage({
      message: `复制失败，请手动复制此链接：${text}`,
      type: 'warning',
      duration: 10000,
      showClose: true
    });
  }
  
  document.body.removeChild(textArea);
};

// 监听主题色变化
watch(() => tenantStore.themeColor, (newThemeColor) => {
  if (newThemeColor) {
    // 设置主题色
    document.documentElement.style.setProperty('--el-color-primary', newThemeColor);
  }
}, { immediate: true });

// 获取租户功能启用状态
const getTenantEnableStatus = async () => {
  try {
    const res = await getTenantEnableStatusApi();
    if (res.code === '0000' && res.data) {
      tenantEnabled.value = res.data.enable;

      // 如果租户功能被禁用，清空租户信息
      if (!tenantEnabled.value) {
        tenantStore.clearTenantInfo();
      }
    }
  } catch (error) {
    console.error('获取租户功能启用状态失败:', error);
  }
};

onMounted(() => {
  // 初始加载
  getTenantEnableStatus();
  getTenantList();
});
</script>

<style scoped lang="scss">
.tenant-switch {
  cursor: pointer;
  
  .current-tenant {
    display: flex;
    align-items: center;
    justify-content: center;
    
    .tenant-trigger {
      display: flex;
      align-items: center;
      background-color: rgba(var(--el-color-primary-rgb), 0.1);
      padding: 5px 12px;
      border-radius: 20px;
      border: 1px solid rgba(var(--el-color-primary-rgb), 0.2);
      transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.1);
      
      &:hover {
        background-color: rgba(var(--el-color-primary-rgb), 0.15);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.2);
      }
      
      .toolBar-icon {
        font-size: 18px;
        color: var(--el-color-primary);
        margin-right: 6px;
      }
      
      .tenant-label {
        font-size: 14px;
        font-weight: 500;
        color: var(--el-color-primary);
        max-width: 120px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

.tenant-popover-content {
  padding: 16px 0;
  
  .tenant-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px 16px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    
    .tenant-title-wrapper {
      display: flex;
      align-items: center;
      
      .tenant-icon {
        font-size: 18px;
        color: var(--el-color-primary);
        margin-right: 6px;
      }
      
      .tenant-title {
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        letter-spacing: -0.2px;
      }
      
      .tenant-count-tag {
        margin-left: 8px;
        height: 20px;
        line-height: 18px;
        padding: 0 6px;
        font-size: 12px;
        font-weight: 500;
        background-color: rgba(0, 0, 0, 0.04);
        border-color: transparent;
        color: var(--el-text-color-secondary);
      }
    }
    
    .current-tenant-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--el-color-primary);
      background-color: rgba(var(--el-color-primary-rgb), 0.1);
      padding: 4px 10px;
      border-radius: 12px;
    }
  }
  
  .tenant-search {
    padding: 16px 20px;
    max-width: 400px;
    margin: 0 auto;
    
    .tenant-key-input {
      :deep(.el-input__wrapper) {
        border-top-right-radius: 0;
        border-bottom-right-radius: 0;
        transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        box-shadow: 0 0 0 1px var(--el-border-color-lighter) inset;
        padding: 0 12px;
        
        &:hover {
          box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
        }
        
        &.is-focus {
          transform: translateY(-1px);
          box-shadow: 0 0 0 1px var(--el-color-primary) inset, 0 2px 6px rgba(0, 0, 0, 0.1);
        }
      }
      
      :deep(.el-input__inner) {
        height: 40px;
        font-size: 14px;
        color: var(--el-text-color-primary);
      }
      
      .search-prefix-icon {
        margin-right: 8px;
        color: var(--el-text-color-secondary);
      }
    }
    
    .search-btn {
      padding: 0 20px;
      height: 100%;
      border: none;
      transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      background: linear-gradient(to right, var(--el-color-primary), rgba(var(--el-color-primary-rgb), 0.8));
      display: flex;
      align-items: center;
      gap: 6px;
      
      &:hover {
        filter: brightness(1.1);
        transform: translateY(-1px);
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
      }
      
      span {
        font-size: 14px;
        font-weight: 500;
      }
      
      .el-icon {
        font-size: 16px;
      }
    }
  }
  
  .tenant-list {
    max-height: 500px;
    overflow-y: auto;
    min-height: 100px;
    padding: 0 4px;
    
    &::-webkit-scrollbar {
      width: 4px;
    }
    
    &::-webkit-scrollbar-track {
      background-color: rgba(0, 0, 0, 0.02);
      border-radius: 2px;
    }
    
    &::-webkit-scrollbar-thumb {
      background-color: rgba(0, 0, 0, 0.1);
      border-radius: 2px;
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.2);
      }
    }
    
    .tenant-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      padding: 16px;
    }
    
    .tenant-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px;
      cursor: pointer;
      border-radius: 12px;
      transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      border: 1px solid transparent;
      background-color: rgba(0, 0, 0, 0.02);
      
      &:hover {
        background-color: rgba(var(--el-color-primary-rgb), 0.05);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        border-color: rgba(var(--el-color-primary-rgb), 0.1);
      }
      
      &.is-active {
        background-color: rgba(var(--el-color-primary-rgb), 0.08);
        border-color: rgba(var(--el-color-primary-rgb), 0.2);
        box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.1);
      }
      
      .tenant-item-left {
        display: flex;
        align-items: center;
        
        .tenant-logo {
          width: 42px;
          height: 42px;
          border-radius: 10px;
          background-color: #f5f7fa;
          display: flex;
          align-items: center;
          justify-content: center;
          overflow: hidden;
          margin-right: 16px;
          border: 1px solid var(--el-border-color-lighter);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
          
          .el-icon {
            font-size: 22px;
            color: var(--el-text-color-secondary);
          }
        }
        
        .tenant-info {
          display: flex;
          flex-direction: column;
          
          .tenant-name {
            font-size: 14px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            margin-bottom: 4px;
            letter-spacing: -0.2px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            max-width: 150px;
          }
          
          .tenant-code {
            font-size: 12px;
            color: var(--el-text-color-secondary);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            max-width: 150px;
          }
        }
      }

      .tenant-item-right {
        display: flex;
        align-items: center;
        gap: 8px;

        .el-tag {
          border-radius: 10px;
          padding: 4px 8px;
          font-weight: 500;
          border: none;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          
          &--success {
            background: linear-gradient(to right, #67c23a, #95d475);
            color: white;
          }
          
          &--danger {
            background: linear-gradient(to right, #f56c6c, #f89898);
            color: white;
          }
        }

        .lock-wrapper {
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 24px;
          height: 24px;
          border-radius: 50%;
          background-color: rgba(var(--el-color-warning-rgb), 0.1);
          transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
          
          &:hover {
            transform: scale(1.1);
            background-color: rgba(var(--el-color-warning-rgb), 0.2);
          }
          
          .lock-icon {
            font-size: 14px;
            color: var(--el-color-warning);
            filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.05));
          }
        }

        .share-dropdown-wrapper {
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 24px;
          height: 24px;
          border-radius: 50%;
          background-color: rgba(var(--el-color-primary-rgb), 0.1);
          transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
          
          &:hover {
            transform: scale(1.1);
            background-color: rgba(var(--el-color-primary-rgb), 0.2);
          }
          
          .share-icon {
            font-size: 14px;
            color: var(--el-color-primary);
          }
        }
      }
    }
  }
  
  .tenant-empty {
    padding: 30px 20px;
    text-align: center;
    
    .empty-tip {
      margin-top: 20px;
      color: var(--el-text-color-secondary);
      font-size: 13px;
      text-align: left;
      
      p {
        font-weight: 500;
        margin: 0 0 8px;
      }
      
      ul {
        margin: 0;
        padding-left: 20px;
        
        li {
          margin-bottom: 4px;
        }
      }
    }
  }
}

// 密码验证对话框样式
.password-dialog-content {
  padding: 16px 0;
  
  .tenant-info-display {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 24px;
    
    .tenant-logo-container {
      width: 80px;
      height: 80px;
      border-radius: 16px;
      overflow: hidden;
      background-color: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 16px;
      border: 1px solid var(--el-border-color-lighter);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
      transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      
      &:hover {
        transform: scale(1.05) translateY(-5px);
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
      }
      
      .tenant-logo-large {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .tenant-logo-placeholder {
        font-size: 40px;
        color: var(--el-text-color-secondary);
      }
    }
    
    .tenant-name-display {
      font-size: 20px;
      font-weight: 600;
      margin: 0 0 6px;
      text-align: center;
      color: var(--el-text-color-primary);
      letter-spacing: -0.3px;
    }
    
    .tenant-id-display {
      font-size: 14px;
      margin: 0;
      color: var(--el-text-color-secondary);
      background-color: rgba(0, 0, 0, 0.03);
      padding: 4px 10px;
      border-radius: 10px;
    }
  }
  
  .password-tip {
    margin-bottom: 20px;
    color: var(--el-text-color-secondary);
    font-size: 15px;
    text-align: center;
  }
  
  .password-input {
    margin-bottom: 8px;
    
    &.shake-animation {
      animation: shake 0.5s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
    }
    
    :deep(.el-input__wrapper) {
      border-radius: 12px;
      padding: 0 12px;
      transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      border: none;
      
      &:hover {
        box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
      }
      
      &.is-focus {
        transform: translateY(-2px);
        box-shadow: 0 0 0 1px var(--el-color-primary) inset, 0 4px 12px rgba(var(--el-color-primary-rgb), 0.1);
      }
    }
    
    :deep(.el-input__inner) {
      height: 44px;
      font-size: 16px;
      border: none;
      background: transparent;
    }
  }
  
  .password-error {
    margin-top: 12px;
    color: var(--el-color-danger);
    display: flex;
    align-items: center;
    font-size: 14px;
    background-color: rgba(var(--el-color-danger-rgb), 0.1);
    padding: 8px 12px;
    border-radius: 10px;
    animation: fadeIn 0.3s ease;
    
    .el-icon {
      margin-right: 8px;
      font-size: 16px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  
  .cancel-btn {
    margin-right: 12px;
    border-radius: 10px;
    font-weight: 500;
    height: 40px;
    transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    
    &:hover {
      background-color: #f5f7fa;
      transform: translateY(-2px);
    }
  }
  
  .confirm-btn {
    font-weight: 500;
    border-radius: 10px;
    height: 40px;
    padding: 0 20px;
    background: linear-gradient(to right, var(--el-color-primary), rgba(var(--el-color-primary-rgb), 0.8));
    transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    border: none;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.2);
      filter: brightness(1.05);
    }
    
    &:active {
      transform: translateY(0);
      box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.1);
    }
  }
}

// 动画效果
@keyframes shake {
  10%, 90% { transform: translateX(-1px); }
  20%, 80% { transform: translateX(2px); }
  30%, 50%, 70% { transform: translateX(-2px); }
  40%, 60% { transform: translateX(2px); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

// Apple风格美化
:deep(.el-popover) {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1), 0 4px 8px rgba(0, 0, 0, 0.05);
  border: none;
  padding: 0;
  overflow: hidden;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px var(--el-border-color-lighter) inset;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  
  &:hover {
    box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
  }
}

:deep(.el-input-group__append) {
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  
  .el-button {
    border: none;
    color: white;
    background: transparent;
    height: 40px;
    
    &:hover {
      background-color: var(--el-color-primary-dark-2);
    }
    
    .el-icon {
      color: white;
    }
  }
}

:deep(.el-tag) {
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

:deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.15);
  
  .el-dialog__header {
    margin: 0;
    padding: 20px 24px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    
    .el-dialog__title {
      font-weight: 600;
      font-size: 18px;
      letter-spacing: -0.2px;
    }
    
    .el-dialog__headerbtn {
      .el-dialog__close {
        font-size: 18px;
        transition: transform 0.3s ease;
        
        &:hover {
          transform: rotate(90deg);
        }
      }
    }
  }
  
  .el-dialog__body {
    padding: 24px 28px;
  }
  
  .el-dialog__footer {
    border-top: 1px solid var(--el-border-color-lighter);
    padding: 16px 24px;
  }
}

:deep(.el-dropdown-menu) {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1), 0 4px 8px rgba(0, 0, 0, 0.05);
  padding: 6px;
  
  .el-dropdown-menu__item {
    border-radius: 8px;
    margin: 4px;
    padding: 10px 14px;
    font-size: 14px;
    
    &:hover {
      background-color: rgba(var(--el-color-primary-rgb), 0.08);
    }
    
    .el-icon {
      margin-right: 8px;
      color: var(--el-color-primary);
    }
  }
}

:deep(.el-badge) {
  .el-badge__content {
    height: auto;
    padding: 2px 6px;
    border: none;
    font-size: 12px;
    font-weight: 600;
    border-radius: 10px;
    
    &.is-fixed {
      top: -2px;
      right: -2px;
      transform: translate(50%, -50%);
      z-index: 10;
    }
  }
}
</style> 