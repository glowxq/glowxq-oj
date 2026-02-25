<template>
  <div class="tenant-select-container">
    <div class="tenant-section" v-if="tenantEnabled">
      <div class="section-title">
        <el-icon><OfficeBuilding /></el-icon>
        <span>选择租户</span>
        <span class="required">*</span>
        <el-tooltip
          content="租户标识是您账号的重要归属信息"
          placement="top"
          effect="light"
          :hide-after="0"
        >
          <el-icon class="help-icon"><InfoFilled /></el-icon>
        </el-tooltip>
      </div>
      
      <div class="tenant-card">
        <!-- 选中的租户信息展示 -->
        <div v-if="selectedTenant" class="selected-tenant">
          <div class="tenant-logo">
            <img v-if="selectedTenant.logoUrl" :src="selectedTenant.logoUrl" alt="logo" />
            <el-icon v-else><OfficeBuilding /></el-icon>
          </div>
          <div class="tenant-info">
            <div class="tenant-name-wrapper">
              <span class="tenant-name" :title="selectedTenant.tenantName">{{ selectedTenant.tenantName }}</span>
            </div>
            <div class="tenant-meta-tags">
              <span class="tenant-tag tenant-id">{{ selectedTenant.tenantId }}</span>
              <span class="tenant-tag tenant-code">{{ selectedTenant.tenantCode }}</span>
            </div>
          </div>
        </div>
        
        <div class="tenant-input-group">
          <el-input
            :model-value="modelValue"
            @update:model-value="updateValue"
            placeholder="请输入租户标识"
            clearable
            class="tenant-input"
            @keyup.enter="searchByTenantKey"
          >
            <template #prefix>
              <el-icon class="el-input__icon"><OfficeBuilding /></el-icon>
            </template>
          </el-input>
          
          <div class="tenant-buttons">
            <el-button @click="searchByTenantKey" :loading="searchLoading" type="primary" class="search-btn">
              <el-icon><Search /></el-icon>
            </el-button>
            
            <el-popover
              placement="bottom"
              trigger="click"
              :width="450"
              @show="getTenantList"
              popper-class="tenant-popover"
            >
              <template #reference>
                <el-button class="tenant-select-btn" type="primary">选择</el-button>
              </template>
              
              <div class="tenant-popover-content">
                <div class="tenant-header">
                  <div class="tenant-title-wrapper">
                    <el-icon class="tenant-icon"><OfficeBuilding /></el-icon>
                    <span class="tenant-title">选择租户</span>
                    <el-tag size="small" type="info" effect="plain" class="tenant-count-tag">
                      共 {{ tenantItems.length }} 个租户
                    </el-tag>
                  </div>
                  
                  <!-- 当前活跃租户标记 -->
                  <el-badge v-if="tenantStore.tenantName" :value="1" type="primary">
                    <span class="current-tenant-name">{{ tenantStore.tenantName }}</span>
                  </el-badge>
                </div>
                
                <!-- 搜索栏 -->
                <div class="tenant-search">
                  <el-input
                    v-model="tenantSearchKey"
                    placeholder="搜索租户"
                    clearable
                    class="tenant-key-input"
                  >
                    <template #prefix>
                      <el-icon class="search-prefix-icon"><Search /></el-icon>
                    </template>
                  </el-input>
                </div>
                
                <!-- 租户列表 -->
                <div v-loading="tenantLoading" class="tenant-list">
                  <div v-if="filteredTenants.length > 0" class="tenant-grid">
                    <div
                      v-for="item in filteredTenants"
                      :key="item.id"
                      class="tenant-item"
                      :class="{ 
                        'is-active': modelValue === item.tenantId,
                        'is-current': tenantStore.tenantId === item.tenantId
                      }"
                      @click="selectTenant(item)"
                    >
                      <div class="tenant-item-left">
                        <div class="tenant-logo">
                          <img v-if="item.logoUrl" :src="item.logoUrl" alt="logo" />
                          <el-icon v-else><OfficeBuilding /></el-icon>
                        </div>
                        <div class="tenant-info">
                          <div class="tenant-name-wrapper">
                            <span class="tenant-name" :title="item.tenantName">{{ item.tenantName }}</span>
                          </div>
                          <div class="tenant-meta-tags">
                            <span class="tenant-tag tenant-id">{{ item.tenantId }}</span>
                            <span class="tenant-tag tenant-code">{{ item.tenantCode }}</span>
                          </div>
                        </div>
                      </div>
                      <div class="tenant-item-right">
                        <el-tag 
                          v-if="tenantStore.tenantId === item.tenantId" 
                          type="primary" 
                          size="small" 
                          effect="plain"
                        >
                          当前
                        </el-tag>
                        <el-tag v-else-if="item.enable" type="success" size="small">正常</el-tag>
                        <el-tag v-else type="danger" size="small">禁用</el-tag>
                      </div>
                    </div>
                  </div>
                  
                  <!-- 空状态 -->
                  <div v-else class="tenant-empty">
                    <el-empty description="暂无租户信息" />
                  </div>
                </div>
              </div>
            </el-popover>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 密码验证对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="租户密码验证"
      width="380px"
      :close-on-click-modal="false"
      :append-to-body="true"
      destroy-on-close
    >
      <div class="password-dialog-content">
        <div class="tenant-info-display">
          <div class="tenant-logo-container">
            <img 
              v-if="tempSelectedTenant?.logoUrl" 
              :src="tempSelectedTenant.logoUrl" 
              alt="logo" 
              class="tenant-logo-large"
            />
            <el-icon v-else class="tenant-logo-placeholder"><OfficeBuilding /></el-icon>
          </div>
          <p class="tenant-name-display" :title="tempSelectedTenant?.tenantName">{{ tempSelectedTenant?.tenantName }}</p>
          <div class="tenant-meta-dialog">
            <span class="tenant-tag tenant-id">{{ tempSelectedTenant?.tenantId }}</span>
          </div>
        </div>
        <p class="password-tip">该租户需要密码验证</p>
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
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { getTenantListAllApi, getTenantByTenantKeyApi, getTenantEnableStatusApi } from '@/api/modules/system/tenant/tenant';
import type { ITenant } from '@/api/interface/system/tenant/tenant';
import { 
  OfficeBuilding,
  Search,
  InfoFilled,
  Lock,
  WarningFilled
} from '@element-plus/icons-vue';
import { useTenantStore } from '@/stores/modules/tenant';

// Props 和 Emits
interface Props {
  modelValue: string;
}

interface Emits {
  (e: 'update:modelValue', value: string): void;
  (e: 'change', tenant: ITenant.Row | null): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

// 租户store
const tenantStore = useTenantStore();

// 响应式数据
const tenantEnabled = ref(true);
const selectedTenant = ref<ITenant.Row | null>(null);
const searchLoading = ref(false);
const tenantItems = ref<ITenant.Row[]>([]);
const tenantLoading = ref(false);
const tenantSearchKey = ref('');

// 密码验证相关
const passwordDialogVisible = ref(false);
const tempSelectedTenant = ref<ITenant.Row | null>(null);
const inputPassword = ref('');
const passwordError = ref('');
const verifyLoading = ref(false);

// 过滤后的租户列表
const filteredTenants = computed(() => {
  if (!tenantSearchKey.value) return tenantItems.value;
  
  const keyword = tenantSearchKey.value.toLowerCase();
  return tenantItems.value.filter(item => 
    item.tenantName?.toLowerCase().includes(keyword) || 
    item.tenantCode?.toLowerCase().includes(keyword) ||
    item.tenantId?.toLowerCase().includes(keyword)
  );
});

// 更新值
const updateValue = (value: string) => {
  emit('update:modelValue', value);
};

// 监听modelValue变化，验证是否需要清空选择
watch(() => props.modelValue, (newValue) => {
  if (selectedTenant.value) {
    const matchesTenantId = selectedTenant.value.tenantId === newValue;
    const matchesTenantCode = selectedTenant.value.tenantCode === newValue;
    
    if (!matchesTenantId && !matchesTenantCode) {
      selectedTenant.value = null;
      emit('change', null);
    }
  }
});

// 获取租户列表
const getTenantList = async () => {
  tenantLoading.value = true;
  try {
    const res = await getTenantListAllApi();
    if (res.code === '0000' && res.data) {
      if (typeof res.data === 'object' && 'rows' in res.data && Array.isArray(res.data.rows)) {
        tenantItems.value = res.data.rows;
      } else if (Array.isArray(res.data)) {
        tenantItems.value = res.data;
      }
      
      // 如果当前有活跃的租户，则设置为默认选中
      if (tenantStore.tenantId && props.modelValue === tenantStore.tenantId) {
        const currentTenant = tenantItems.value.find(item => item.tenantId === tenantStore.tenantId);
        if (currentTenant) {
          selectTenant(currentTenant, false);
        }
      }
    }
  } catch (error) {
    console.error('获取租户列表失败:', error);
  } finally {
    tenantLoading.value = false;
  }
};

// 根据租户Key搜索
const searchByTenantKey = async () => {
  if (!props.modelValue) {
    ElMessage.warning('请输入租户标识');
    return;
  }
  
  searchLoading.value = true;
  try {
    const res = await getTenantByTenantKeyApi(props.modelValue);
    
    if (res.code === '0000' && res.data) {
      if (!res.data.enable) {
        ElMessage.warning('该租户已被禁用，无法选择');
        return;
      }
      
      // 检查是否有密码
      if (res.data.password) {
        // 显示密码验证对话框
        tempSelectedTenant.value = res.data;
        inputPassword.value = '';
        passwordError.value = '';
        passwordDialogVisible.value = true;
      } else {
        // 无需密码，直接选择租户
        selectTenant(res.data);
      }
    } else {
      ElMessage.error(res.message || '未找到匹配的租户');
    }
  } catch (error) {
    console.error('搜索租户失败:', error);
    ElMessage.error('搜索租户时发生错误');
  } finally {
    searchLoading.value = false;
  }
};

// 验证租户密码
const verifyPassword = () => {
  if (!tempSelectedTenant.value) return;
  
  if (!inputPassword.value) {
    passwordError.value = '请输入密码';
    return;
  }
  
  passwordError.value = '';
  verifyLoading.value = true;

  // 模拟网络请求验证密码
  setTimeout(() => {
    if (inputPassword.value === tempSelectedTenant.value!.password) {
      // 密码正确，选择租户
      passwordDialogVisible.value = false;
      selectTenant(tempSelectedTenant.value!);
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

// 选择租户
const selectTenant = (tenant: ITenant.Row, showMessage = true) => {
  if (!tenant.enable) {
    ElMessage.warning('该租户已被禁用，无法选择');
    return;
  }
  
  emit('update:modelValue', tenant.tenantId);
  selectedTenant.value = tenant;
  emit('change', tenant);
  
  if (showMessage) {
    ElMessage.success(`已选择租户: ${tenant.tenantName}`);
  }
};

// 获取租户功能启用状态
const getTenantEnableStatus = async () => {
  try {
    const res = await getTenantEnableStatusApi();
    if (res.code === '0000' && res.data) {
      tenantEnabled.value = res.data.enable;
    }
  } catch (error) {
    console.error('获取租户功能启用状态失败:', error);
  }
};

// 初始化
onMounted(() => {
  getTenantEnableStatus();
  getTenantList();
});
</script>

<style scoped lang="scss">
.tenant-select-container {
  width: 100%;
}

.tenant-section {
  .section-title {
    display: flex;
    align-items: center;
    font-size: 15px;
    font-weight: 500;
    color: #1d1d1f;
    margin-bottom: 16px;
    
    .el-icon {
      margin-right: 6px;
      color: var(--el-color-primary);
    }
    
    .required {
      color: var(--el-color-danger);
      margin-left: 4px;
    }
    
    .help-icon {
      margin-left: 8px;
      font-size: 14px;
      color: #8e8e93;
      cursor: pointer;
      transition: color 0.2s ease;
      
      &:hover {
        color: var(--el-color-primary);
      }
    }
  }
  
  .tenant-card {
    background-color: rgba(var(--el-color-primary-rgb), 0.03);
    border-radius: 12px;
    padding: 20px;
    border: 1px solid rgba(var(--el-color-primary-rgb), 0.1);
  }
  
  // 已选中的租户展示区域
  .selected-tenant {
    display: flex;
    align-items: center;
    background-color: white;
    border-radius: 8px;
    padding: 6px 10px;
    margin-bottom: 12px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
    
    .tenant-logo {
      width: 26px;
      height: 26px;
      border-radius: 5px;
      background-color: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      margin-right: 8px;
      flex-shrink: 0;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .el-icon {
        font-size: 14px;
        color: var(--el-color-primary);
      }
    }
    
    .tenant-info {
      flex: 1;
      min-width: 0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .tenant-name-wrapper {
        margin-right: 8px;
        min-width: 0;
        
        .tenant-name {
          font-size: 13px;
          font-weight: 600;
          color: #1d1d1f;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          display: inline-block;
          max-width: 150px;
        }
      }
      
      .tenant-meta-tags {
        display: flex;
        gap: 4px;
        
        .tenant-tag {
          font-size: 11px;
          padding: 1px 5px;
          border-radius: 3px;
          white-space: nowrap;
          line-height: 1.5;
        }
        
        .tenant-id {
          background-color: rgba(var(--el-color-primary-rgb), 0.08);
          color: var(--el-color-primary);
        }
        
        .tenant-code {
          background-color: rgba(var(--el-color-success-rgb), 0.08);
          color: var(--el-color-success);
        }
      }
    }
  }
  
  .tenant-input-group {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
    
    .tenant-input {
      flex: 1;
      
      :deep(.el-input__wrapper) {
        background-color: white;
      }
    }
    
    .tenant-buttons {
      display: flex;
      gap: 8px;
      
      .search-btn,
      .tenant-select-btn {
        border-radius: 8px;
        height: 40px;
        padding: 0 12px;
        font-weight: 500;
        border: none;
        background-color: var(--el-color-primary);
        transition: all 0.2s ease;
        
        &:hover {
          background-color: var(--el-color-primary-dark-2);
          transform: translateY(-2px);
        }
      }
      
      .search-btn {
        .el-icon {
          margin-right: 0;
        }
      }
    }
  }
}

/* 密码验证对话框样式 */
.password-dialog-content {
  padding: 0;
  
  .tenant-info-display {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;
    
    .tenant-logo-container {
      width: 54px;
      height: 54px;
      border-radius: 10px;
      overflow: hidden;
      background-color: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 10px;
      border: 1px solid var(--el-border-color-lighter);
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
      
      .tenant-logo-large {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .tenant-logo-placeholder {
        font-size: 26px;
        color: var(--el-text-color-secondary);
      }
    }
    
    .tenant-name-display {
      font-size: 16px;
      font-weight: 600;
      margin: 0 0 6px;
      text-align: center;
      color: var(--el-text-color-primary);
      max-width: 350px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .tenant-meta-dialog {
      display: flex;
      justify-content: center;
      
      .tenant-tag {
        font-size: 12px;
        padding: 2px 8px;
        border-radius: 4px;
        white-space: nowrap;
        line-height: 1.5;
        background-color: rgba(var(--el-color-primary-rgb), 0.08);
        color: var(--el-color-primary);
      }
    }
  }
  
  .password-tip {
    margin-bottom: 12px;
    color: var(--el-text-color-secondary);
    font-size: 14px;
    text-align: center;
  }
  
  .password-input {
    margin-bottom: 8px;
    
    &.shake-animation {
      animation: shake 0.5s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
    }
    
    :deep(.el-input__wrapper) {
      border-radius: 10px;
      padding: 0 12px;
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      
      &:hover {
        box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
      }
      
      &.is-focus {
        box-shadow: 0 0 0 2px var(--el-color-primary) inset;
      }
    }
    
    :deep(.el-input__inner) {
      height: 40px;
      font-size: 16px;
    }
  }
  
  .password-error {
    margin-top: 12px;
    color: var(--el-color-danger);
    display: flex;
    align-items: center;
    font-size: 13px;
    background-color: rgba(var(--el-color-danger-rgb), 0.1);
    padding: 8px 12px;
    border-radius: 8px;
    
    .el-icon {
      margin-right: 8px;
      font-size: 14px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  
  .cancel-btn, .confirm-btn {
    border-radius: 8px;
    height: 38px;
    padding: 0 16px;
    font-weight: 500;
    transition: all 0.2s ease;
    margin-left: 8px;
  }
  
  .confirm-btn {
    background: var(--el-color-primary);
    border: none;
    
    &:hover {
      background-color: var(--el-color-primary-dark-2);
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

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  
  .el-dialog__header {
    padding: 16px 20px;
    margin: 0;
    border-bottom: 1px solid var(--el-border-color-light);
    
    .el-dialog__title {
      font-size: 16px;
      font-weight: 600;
    }
  }
  
  .el-dialog__body {
    padding: 20px;
  }
  
  .el-dialog__footer {
    padding: 12px 20px 20px;
    border-top: none;
  }
}
</style>

<!-- 全局样式用于弹出框 -->
<style lang="scss">
.tenant-popover {
  border-radius: 16px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1), 0 4px 8px rgba(0, 0, 0, 0.05) !important;
  border: none !important;
  padding: 0 !important;
  overflow: hidden !important;
  
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
          margin-right: 8px;
        }
        
        .tenant-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          letter-spacing: -0.2px;
        }
        
        .tenant-count-tag {
          margin-left: 12px;
          height: 22px;
          line-height: 20px;
          padding: 0 8px;
          font-size: 12px;
          font-weight: 500;
          background-color: rgba(var(--el-color-primary-rgb), 0.08) !important;
          border-color: transparent !important;
          color: var(--el-color-primary) !important;
          border-radius: 12px;
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
        .el-input__wrapper {
          border-radius: 10px;
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
        
        .el-input__inner {
          height: 40px;
          font-size: 14px;
          color: var(--el-text-color-primary);
        }
        
        .search-prefix-icon {
          margin-right: 8px;
          color: var(--el-text-color-secondary);
        }
      }
    }
    
    .tenant-list {
      max-height: 400px;
      overflow-y: auto;
      min-height: 100px;
      padding: 0 8px;
      
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
          
          .tenant-item-left .tenant-logo {
            transform: scale(1.05);
          }
        }
        
        &.is-active {
          background-color: rgba(var(--el-color-primary-rgb), 0.08);
          border-color: rgba(var(--el-color-primary-rgb), 0.2);
          box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.1);
        }
        
        &.is-current {
          background-color: rgba(var(--el-color-primary-rgb), 0.08);
          border-color: rgba(var(--el-color-primary-rgb), 0.2);
          box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.1);
        }
        
        .tenant-item-left {
          display: flex;
          align-items: center;
          flex: 1;
          min-width: 0;
          
          .tenant-logo {
            width: 42px;
            height: 42px;
            border-radius: 10px;
            background-color: #f5f7fa;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            margin-right: 12px;
            border: 1px solid var(--el-border-color-lighter);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
            flex-shrink: 0;
            
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
            min-width: 0;
            flex: 1;
            
            .tenant-name-wrapper {
              margin-bottom: 4px;
              
              .tenant-name {
                font-size: 14px;
                font-weight: 600;
                color: var(--el-text-color-primary);
                letter-spacing: -0.2px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                max-width: 120px;
              }
            }
            
            .tenant-meta-tags {
              display: flex;
              gap: 6px;
              
              .tenant-tag {
                font-size: 11px;
                padding: 2px 6px;
                border-radius: 4px;
                white-space: nowrap;
                max-width: 80px;
                overflow: hidden;
                text-overflow: ellipsis;
              }
              
              .tenant-id {
                background-color: rgba(var(--el-color-primary-rgb), 0.08);
                color: var(--el-color-primary);
              }
              
              .tenant-code {
                background-color: rgba(var(--el-color-success-rgb), 0.08);
                color: var(--el-color-success);
              }
            }
          }
        }
        
        .tenant-item-right {
          display: flex;
          align-items: center;
          gap: 6px;
          flex-shrink: 0;
          
          .el-tag {
            border-radius: 8px;
            padding: 4px 8px;
            font-weight: 500;
            border: none;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            font-size: 11px;
            
            &.el-tag--primary {
              background: linear-gradient(to right, var(--el-color-primary), rgba(var(--el-color-primary-rgb), 0.8));
              color: white;
            }
            
            &.el-tag--success {
              background: linear-gradient(to right, #67c23a, #95d475);
              color: white;
            }
            
            &.el-tag--danger {
              background: linear-gradient(to right, #f56c6c, #f89898);
              color: white;
            }
          }
        }
      }
      
      .tenant-empty {
        padding: 30px 20px;
        text-align: center;
        
        .el-empty {
          .el-empty__description {
            color: var(--el-text-color-secondary);
            font-size: 14px;
          }
          
          .el-empty__image {
            width: 80px;
            height: 80px;
          }
        }
      }
    }
  }
}
</style> 