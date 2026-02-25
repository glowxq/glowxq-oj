<template>
  <div class="register-form-container">
    <el-form
      ref="registerFormRef"
      :model="formModel"
      :rules="registerRules"
      label-position="left"
      size="default"
      @keyup.enter="handleRegister"
    >
      <!-- 折叠面板容器 -->
      <el-collapse v-model="activeCollapseNames" class="register-collapse">
        <!-- 基本信息区域 -->
        <el-collapse-item name="basic" class="collapse-item">
          <template #title>
            <div class="collapse-header">
              <span class="header-title">基本信息</span>
              <span class="required-tag">必填</span>
            </div>
          </template>
          
          <div class="form-section">
            <el-form-item prop="username" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>用户名</span>
                  <span class="required">*</span>
                </div>
              </template>
              <el-input
                v-model="formModel.username"
                placeholder="请输入用户名"
                clearable
                @input="syncPhoneWithUsername"
                class="uniform-input"
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><UserFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>密码</span>
                  <span class="required">*</span>
                </div>
              </template>
              <el-input
                v-model.trim="formModel.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
                class="uniform-input"
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="confirmPassword" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>确认密码</span>
                  <span class="required">*</span>
                </div>
              </template>
              <el-input
                v-model.trim="formModel.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                show-password
                clearable
                class="uniform-input"
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <!-- 租户选择区域 -->
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
            
            <el-form-item prop="tenantKey" class="tenant-form-item">
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
                    v-model="formModel.tenantKey"
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
                                'is-active': formModel.tenantKey === item.tenantId,
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
                                <el-tag v-if="tenantStore.tenantId === item.tenantId" 
                                       type="primary" size="small" effect="plain">当前</el-tag>
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
            </el-form-item>
          </div>
        </el-collapse-item>
        
        <!-- 个人信息区域 -->
        <el-collapse-item name="personal" class="collapse-item">
          <template #title>
            <div class="collapse-header">
              <span class="header-title">个人信息</span>
              <span class="optional-tag">选填</span>
            </div>
          </template>
          
          <div class="form-section">
            <el-form-item prop="phone" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>手机号</span>
                </div>
              </template>
              <el-input
                v-model="formModel.phone"
                placeholder="请输入手机号"
                clearable
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><Iphone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="email" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>邮箱</span>
                </div>
              </template>
              <el-input
                v-model="formModel.email"
                placeholder="请输入邮箱"
                clearable
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="name" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>真实姓名</span>
                </div>
              </template>
              <el-input
                v-model="formModel.name"
                placeholder="请输入真实姓名"
                clearable
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="nickname" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>昵称</span>
                </div>
              </template>
              <el-input
                v-model="formModel.nickname"
                placeholder="请输入昵称"
                clearable
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><ChatDotRound /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <div class="form-two-columns">
              <el-form-item prop="sex" class="form-item">
                <template #label>
                  <div class="form-label">
                    <span>性别</span>
                  </div>
                </template>
                <el-select v-model="formModel.sex" placeholder="请选择性别" class="full-width">
                  <el-option label="未知" :value="0" />
                  <el-option label="男" :value="1" />
                  <el-option label="女" :value="2" />
                </el-select>
              </el-form-item>
              
              <el-form-item prop="birthday" class="form-item">
                <template #label>
                  <div class="form-label">
                    <span>生日</span>
                  </div>
                </template>
                <el-date-picker
                  v-model="formModel.birthday"
                  type="date"
                  placeholder="请选择生日"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  class="full-width"
                />
              </el-form-item>
            </div>
          
            <el-form-item prop="address" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>地址</span>
                </div>
              </template>
              <el-input
                v-model="formModel.address"
                placeholder="请输入地址"
                clearable
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="logo" class="form-item">
              <template #label>
                <div class="form-label">
                  <span>头像</span>
                </div>
              </template>
              <div class="avatar-wrapper">
                <upload-img
                  v-model:imageUrl="formModel.logo"
                  :limit="1"
                  :file-size="2"
                  dir="avatar"
                  height="90px"
                  width="90px"
                >
                  <template #tip>
                    <span class="upload-tip">支持jpg, png格式，大小不超过2MB</span>
                  </template>
                </upload-img>
              </div>
            </el-form-item>
          </div>
        </el-collapse-item>
      </el-collapse>
      
      <div class="register-actions">
        <el-button
          type="primary"
          :loading="loading"
          class="register-button"
          @click="() => { console.log('点击了注册按钮'); handleRegister(); }"
        >
          注册
        </el-button>
      </div>
    </el-form>
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
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage, ElMessageBox } from 'element-plus';
import { registerApi } from '@/api/modules/system/admin/login';
import { getTenantListAllApi, getTenantByTenantKeyApi, getTenantEnableStatusApi } from '@/api/modules/system/tenant/tenant';
import type { ITenant } from '@/api/interface/system/tenant/tenant';
import type { ILogin } from '@/api/interface/system/admin/login';
import { 
  UserFilled, 
  Lock, 
  Iphone, 
  Message, 
  User, 
  ChatDotRound, 
  Location, 
  OfficeBuilding,
  Search,
  InfoFilled,
  WarningFilled
} from '@element-plus/icons-vue';
import UploadImg from '@/components/Common/Upload/Img.vue';
import { LOGIN_URL } from '@/config';
import { useTenantStore } from '@/stores/modules/tenant';

// 路由
const router = useRouter();
// 租户store
const tenantStore = useTenantStore();

// 表单引用
const registerFormRef = ref<FormInstance>();
const loading = ref(false);

// 折叠面板激活项
const activeCollapseNames = ref(['basic']);

// 选中的租户信息
const selectedTenant = ref<ITenant.Row | null>(null);

// 搜索相关
const searchLoading = ref(false);

// 密码验证相关
const passwordDialogVisible = ref(false);
const tempSelectedTenant = ref<ITenant.Row | null>(null);
const inputPassword = ref('');
const passwordError = ref('');
const verifyLoading = ref(false);

// 租户功能是否启用
const tenantEnabled = ref(true);

// 注册表单数据
const registerForm = reactive<ILogin.RegisterParams>({
  username: '',
  password: '',
  tenantKey: '',
  phone: '',
  email: '',
  name: '',
  nickname: '',
  sex: 0,
  birthday: '',
  logo: '',
  address: '',
});

// 将确认密码临时添加到表单模型 (不会提交到后端)
const formModel = reactive({
  ...registerForm,
  confirmPassword: ''
});

// 租户相关
const tenantItems = ref<ITenant.Row[]>([]);
const tenantLoading = ref(false);
const tenantSearchKey = ref('');

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

// 监听tenantKey变化，验证是否需要清空选择
watch(() => formModel.tenantKey, (newValue) => {
  if (selectedTenant.value) {
    const matchesTenantId = selectedTenant.value.tenantId === newValue;
    const matchesTenantCode = selectedTenant.value.tenantCode === newValue;
    
    if (!matchesTenantId && !matchesTenantCode) {
      selectedTenant.value = null;
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
      if (tenantStore.tenantId) {
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
  if (!formModel.tenantKey) {
    ElMessage.warning('请输入租户标识');
    return;
  }
  
  searchLoading.value = true;
  try {
    const res = await getTenantByTenantKeyApi(formModel.tenantKey);
    
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
  
  formModel.tenantKey = tenant.tenantId;
  selectedTenant.value = tenant;
  
  if (showMessage) {
    ElMessage.success(`已选择租户: ${tenant.tenantName}`);
  }
};

// 用户名输入时自动同步到手机号
const syncPhoneWithUsername = (value: string) => {
  formModel.phone = value;
};

// 表单验证规则
const validatePass2 = (rule: any, value: string, callback: any) => {
  // 确保值是字符串
  const inputValue = typeof value === 'string' ? value : '';
  const passwordValue = typeof formModel.password === 'string' ? formModel.password : '';
  
  if (inputValue === '') {
    callback(new Error('请再次输入密码'));
  } else if (inputValue.trim() !== passwordValue.trim()) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

// 监听密码变化，重新验证确认密码
watch(() => formModel.password, () => {
  if (formModel.confirmPassword) {
    registerFormRef.value?.validateField('confirmPassword');
  }
});

// 监听确认密码变化，确保与密码同步
watch(() => formModel.confirmPassword, (newVal) => {
  if (newVal) {
    registerFormRef.value?.validateField('confirmPassword');
  }
});

const registerRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  tenantKey: [
    { required: true, message: '请选择或输入租户', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
});

// 注册处理
const handleRegister = async () => {
  console.log('handleRegister 被调用');
  
  if (!registerFormRef.value) {
    console.error('表单引用为空');
    return;
  }
  
  // 防止重复点击
  if (loading.value) {
    console.log('正在提交中，请勿重复点击');
    return;
  }
  
  // 手动验证必填字段
  if (!formModel.username) {
    ElMessage.error('请输入用户名');
    return;
  }
  
  if (!formModel.password) {
    ElMessage.error('请输入密码');
    return;
  }
  
  if (!formModel.confirmPassword) {
    ElMessage.error('请确认密码');
    return;
  }
  
  if (!formModel.tenantKey) {
    ElMessage.error('请选择租户');
    return;
  }
  
  // 为了调试，先打印出表单数据
  console.log('注册表单数据:', formModel);
  console.log('验证前的registerForm:', registerForm);
  
  try {
    // 同步formModel到registerForm (除了confirmPassword)
    registerForm.username = formModel.username;
    registerForm.password = formModel.password;
    registerForm.tenantKey = formModel.tenantKey;
    registerForm.phone = formModel.phone;
    registerForm.email = formModel.email;
    registerForm.name = formModel.name;
    registerForm.nickname = formModel.nickname;
    registerForm.sex = formModel.sex;
    registerForm.birthday = formModel.birthday;
    registerForm.logo = formModel.logo;
    registerForm.address = formModel.address;
    
    console.log('同步后的registerForm:', registerForm);
    
    // 再次确认密码匹配
    const passwordValue = typeof formModel.password === 'string' ? formModel.password : '';
    const confirmValue = typeof formModel.confirmPassword === 'string' ? formModel.confirmPassword : '';
    
    if (passwordValue.trim() !== confirmValue.trim()) {
      ElMessage.error('两次输入密码不一致，请检查');
      return;
    }
    
    // 绕过表单验证，直接提交
    loading.value = true;
    try {
      console.log('提交的注册数据:', registerForm);
      await registerApi(registerForm);
      ElMessage.success('注册成功，即将跳转到登录页');
      // 跳转到登录页
      setTimeout(() => {
        router.push(LOGIN_URL);
      }, 1500);
    } catch (error: any) {
      console.error('注册失败:', error);
      ElMessage.error(error.message || '注册失败，请稍后重试');
    } finally {
      loading.value = false;
    }
  } catch (e) {
    console.error('处理注册过程中发生错误:', e);
    ElMessage.error('注册处理过程中出现异常');
  }
};

// 获取租户功能启用状态
const getTenantEnableStatus = async () => {
  try {
    const res = await getTenantEnableStatusApi();
    if (res.code === '0000' && res.data) {
      tenantEnabled.value = res.data.enable;
      console.log('租户功能启用状态:', tenantEnabled.value);
      
      // 如果租户功能被禁用，移除租户验证规则
      if (!tenantEnabled.value) {
        delete registerRules.tenantKey;
      }
    }
  } catch (error) {
    console.error('获取租户功能启用状态失败:', error);
  }
};

// 初始化
onMounted(() => {
  // 获取租户功能启用状态
  getTenantEnableStatus();
  
  // 页面加载时获取租户列表
  getTenantList();
  
  // 如果已有当前租户，默认填充到表单
  if (tenantStore.tenantId) {
    formModel.tenantKey = tenantStore.tenantId;
  }
});
</script>

<style scoped lang="scss">
.register-form-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.register-collapse {
  border: none;
  
  :deep(.el-collapse-item__header) {
    padding: 16px 20px;
    font-size: 16px;
    font-weight: 500;
    border: none;
    background-color: #f5f7fa;
    border-radius: 12px;
    margin-bottom: 16px;
    position: relative;
    transition: all 0.3s ease;
    
    &:hover {
      background-color: #f0f0f5;
    }
  }
  
  :deep(.el-collapse-item__content) {
    padding: 24px;
    background-color: #f9f9fb;
    border-radius: 12px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
  }
  
  :deep(.el-collapse-item__arrow) {
    font-size: 16px;
    transition: transform 0.3s ease;
  }
  
  :deep(.el-collapse-item.is-active .el-collapse-item__header) {
    background-color: #eef3fc;
    border-bottom-left-radius: 0;
    border-bottom-right-radius: 0;
    margin-bottom: 0;
    border-bottom: 1px solid rgba(var(--el-color-primary-rgb), 0.1);
  }
}

.collapse-header {
  display: flex;
  align-items: center;
  
  .header-title {
    font-size: 16px;
    font-weight: 600;
    color: #1d1d1f;
  }
  
  .required-tag, .optional-tag {
    margin-left: 10px;
    font-size: 12px;
    font-weight: normal;
    padding: 2px 8px;
    border-radius: 12px;
  }
  
  .required-tag {
    background-color: rgba(var(--el-color-danger-rgb), 0.08);
    color: var(--el-color-danger);
  }
  
  .optional-tag {
    background-color: rgba(var(--el-color-info-rgb), 0.08);
    color: var(--el-color-info);
  }
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  
  .form-item {
    margin-bottom: 0;
    
    :deep(.el-form-item__content) {
      display: flex;
      align-items: center;
    }
  }
  
  .uniform-input {
    width: 100%;
  }
}

.form-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
  margin-bottom: 8px;
  width: 80px;
  
  .required {
    color: var(--el-color-danger);
    margin-left: 4px;
  }
}

:deep(.el-form-item__label) {
  padding-right: 12px;
  font-weight: 500;
  color: #1d1d1f;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1) inset;
  transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
  height: 40px;
  
  &:hover {
    box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.2) inset;
  }
  
  &.is-focus {
    box-shadow: 0 0 0 2px var(--el-color-primary) inset !important;
  }
}

:deep(.el-input__inner) {
  font-size: 14px;
  height: 40px;
}

:deep(.el-input__icon) {
  color: #8e8e93;
}

// 租户选择特殊样式
.tenant-section {
  margin-top: 24px;
  
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
  
  .tenant-form-item {
    margin-bottom: 0;
    
    :deep(.el-form-item__content) {
      display: block;
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
  
  .tenant-label {
    margin: 0 0 8px;
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
  
  .tenant-tips {
    font-size: 12px;
    
    .current-tenant-tip {
      display: flex;
      align-items: center;
      background-color: rgba(var(--el-color-primary-rgb), 0.08);
      padding: 6px 10px;
      border-radius: 6px;
      
      .el-icon {
        color: var(--el-color-primary);
        margin-right: 6px;
        font-size: 14px;
      }
      
      span {
        color: var(--el-color-primary);
        
        strong {
          font-weight: 600;
        }
      }
    }
  }
}

// 租户选择样式
.tenant-popover-content {
  padding: 12px 0;
  
  .tenant-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    
    .tenant-title-wrapper {
      display: flex;
      align-items: center;
      
      .tenant-icon {
        font-size: 16px;
        color: var(--el-color-primary);
        margin-right: 8px;
      }
      
      .tenant-title {
        font-size: 15px;
        font-weight: 600;
        color: #1d1d1f;
      }
      
      .tenant-count-tag {
        margin-left: 8px;
        height: 20px;
        line-height: 18px;
        padding: 0 8px;
        font-size: 12px;
        background-color: rgba(0, 0, 0, 0.04);
        border-color: transparent;
        color: #8e8e93;
        border-radius: 10px;
      }
    }
    
    .current-tenant-name {
      font-size: 13px;
      font-weight: 500;
      color: var(--el-color-primary);
      background-color: rgba(var(--el-color-primary-rgb), 0.1);
      padding: 3px 8px;
      border-radius: 10px;
    }
  }
  
  .tenant-search {
    padding: 12px 16px;
    
    :deep(.el-input__wrapper) {
      background-color: #f5f7fa;
      border-radius: 10px;
    }
  }
  
  .tenant-list {
    max-height: 320px;
    overflow-y: auto;
    min-height: 120px;
    padding: 0 8px 8px;
    
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-thumb {
      background-color: rgba(0, 0, 0, 0.1);
      border-radius: 3px;
    }
    
    .tenant-grid {
      display: grid;
      grid-template-columns: repeat(1, 1fr);
      gap: 8px;
      padding: 8px;
    }
    
    .tenant-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 14px;
      cursor: pointer;
      border-radius: 10px;
      transition: all 0.3s ease;
      border: 1px solid transparent;
      background-color: #f9f9fb;
      
      &:hover {
        background-color: #f0f2f9;
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
      }
      
      &.is-active {
        background-color: rgba(var(--el-color-primary-rgb), 0.08);
        border-color: rgba(var(--el-color-primary-rgb), 0.2);
      }
      
      &.is-current {
        border-color: var(--el-color-primary);
        background-color: rgba(var(--el-color-primary-rgb), 0.05);
      }
      
      .tenant-item-left {
        display: flex;
        align-items: center;
        flex: 1;
        min-width: 0;
        
        .tenant-logo {
          width: 32px;
          height: 32px;
          border-radius: 6px;
          background-color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          overflow: hidden;
          margin-right: 10px;
          flex-shrink: 0;
          border: 1px solid var(--el-border-color-lighter);
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
          
          .el-icon {
            font-size: 18px;
            color: var(--el-color-primary);
          }
        }
        
        .tenant-info {
          display: flex;
          flex-direction: column;
          min-width: 0;
          
          .tenant-name-wrapper {
            margin-bottom: 4px;
            
            .tenant-name {
              font-size: 14px;
              font-weight: 600;
              color: #1d1d1f;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
              display: inline-block;
              max-width: 200px;
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
      
      .tenant-item-right {
        margin-left: 10px;
        
        .el-tag {
          border-radius: 10px;
          padding: 0 8px;
          height: 22px;
          line-height: 20px;
        }
      }
    }
  }
}

.avatar-wrapper {
  display: flex;
  align-items: center;
}

.upload-tip {
  font-size: 12px;
  color: #8e8e93;
  margin-top: 8px;
  display: block;
}

.register-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 32px;
  
  .register-button {
    width: 100%;
    max-width: 320px;
    height: 44px;
    font-size: 16px;
    font-weight: 500;
    border-radius: 22px;
    background-color: var(--el-color-primary);
    border: none;
    transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
    letter-spacing: 0.5px;
    
    &:hover {
      filter: brightness(1.05);
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(var(--el-color-primary-rgb), 0.2);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

.full-width {
  width: 100%;
}

// 移动端适配
@media (max-width: 767px) {
  .register-form-container {
    padding: 0 12px;
  }
  
  :deep(.el-form--label-left .el-form-item__label) {
    justify-content: flex-start;
    text-align: left;
  }
  
  .form-item {
    :deep(.el-form-item__label) {
      width: 100% !important;
      text-align: left;
      padding-bottom: 8px;
    }
    
    :deep(.el-form-item__content) {
      margin-left: 0 !important;
    }
  }
  
  .tenant-input-group {
    flex-direction: column;
    
    .tenant-buttons {
      display: flex;
      gap: 8px;
      
      .search-btn,
      .tenant-select-btn {
        flex: 1;
      }
    }
  }
  
  .current-tenant-tip {
    flex-direction: column;
    align-items: flex-start;
    
    .el-icon {
      margin-bottom: 6px;
    }
  }
  
  .register-actions .register-button {
    max-width: 100%;
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

.form-two-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
  
  @media (max-width: 767px) {
    grid-template-columns: 1fr;
  }
}
</style>
