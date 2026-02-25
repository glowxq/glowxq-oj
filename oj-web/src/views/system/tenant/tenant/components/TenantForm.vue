<template>
  <el-dialog
    v-model="visible"
    :title="paramsProps.title"
    :destroy-on-close="true"
    width="800px"
    draggable
  >
    <el-tabs v-model="activeTab" class="tenant-tabs">
      <el-tab-pane label="基本信息" name="basic">
        <el-form
          ref="ruleFormRef"
          label-width="140px"
          label-suffix=" :"
          :rules="paramsProps.readonly ? {} : rules"
          :model="paramsProps.row"
          :disabled="paramsProps.readonly"
          @submit.enter.prevent="handleSubmit"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="租户编码" prop="tenantCode">
                <el-input
                  v-model="paramsProps.row.tenantCode"
                  placeholder="请填写租户编码，唯一标识"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="租户名称" prop="tenantName">
                <el-input
                  v-model="paramsProps.row.tenantName"
                  placeholder="请填写租户名称"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人姓名" prop="contactName">
                <el-input
                  v-model="paramsProps.row.contactName"
                  placeholder="请填写联系人姓名"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系人手机号" prop="contactPhone">
                <el-input
                  v-model="paramsProps.row.contactPhone"
                  placeholder="请填写联系人手机号"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人邮箱" prop="contactEmail">
                <el-input
                  v-model="paramsProps.row.contactEmail"
                  placeholder="选填，请填写联系人邮箱"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="租户密码" prop="password">
                <el-input
                  v-model="paramsProps.row.password"
                  placeholder="请填写租户密码，不填则无需密码验证"
                  clearable
                  @click:append="generatePassword"
                >
                  <template #append>
                    <el-button @click="generatePassword">
                      <el-icon><RefreshRight /></el-icon>
                    </el-button>
                  </template>
                </el-input>
                <div class="password-tip">
                  <el-icon><InfoFilled /></el-icon>
                  <span>设置密码后，用户切换租户时需要输入密码验证</span>
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="套餐选择" prop="package">
                <el-select v-model="selectedPackage" placeholder="请选择套餐" @change="handlePackageChange">
                  <el-option
                    v-for="item in packageOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="到期时间" prop="expiredTime">
                <el-date-picker clearable
                  v-model="paramsProps.row.expiredTime"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="请选择到期时间">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="最大用户数" prop="maxUserNum">
                <el-input-number v-model="paramsProps.row.maxUserNum" :precision="0" :min="1" :max="999999" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="当前用户数" prop="currentUserNum">
                <el-input-number v-model="paramsProps.row.currentUserNum" :precision="0" :min="0" :max="999999" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="系统名称" prop="systemName">
                <el-input
                  v-model="paramsProps.row.systemName"
                  placeholder="请填写系统名称"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="自定义域名" prop="customDomain">
                <el-input
                  v-model="paramsProps.row.customDomain"
                  placeholder="请填写自定义域名"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="显示" prop="show">
                <el-switch v-model="paramsProps.row.show" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="enable">
                <el-switch v-model="paramsProps.row.enable" active-text="正常" inactive-text="禁用" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="外观设置" name="appearance">
        <el-form
          label-width="140px"
          label-suffix=" :"
          :model="paramsProps.row"
          :disabled="paramsProps.readonly"
        >
          <el-form-item label="Logo上传" prop="logoUrl">
            <upload-img
              v-model:imageUrl="paramsProps.row.logoUrl"
              :limit="1"
              :file-size="2"
              dir="logo"
              height="120px"
              width="120px"
              :disabled="paramsProps.readonly"
            >
              <template #tip>
                <span>推荐尺寸: 120x120px, 支持jpg, png格式</span>
              </template>
            </upload-img>
          </el-form-item>

          <el-form-item label="首页图片" prop="homeImageUrl">
            <upload-img
              v-model:imageUrl="paramsProps.row.homeImageUrl"
              :limit="1"
              :file-size="5"
              dir="banner"
              height="120px"
              width="240px"
              :disabled="paramsProps.readonly"
            >
              <template #tip>
                <span>推荐尺寸: 1920x400px, 支持jpg, png格式</span>
              </template>
            </upload-img>
          </el-form-item>

          <el-form-item label="主题颜色" prop="themeColor">
            <color-select
              v-model="paramsProps.row.themeColor"
              show-preview
              v-model:textColor="textColor"
              preview-text="预览效果"
              :disabled="paramsProps.readonly"
            />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="租户文本" name="content">
        <template v-if="paramsProps.readonly">
          <div class="markdown-preview-container">
            <markdown-preview v-model="paramsProps.row.text" :height="400" />
          </div>
        </template>
        <template v-else>
          <markdown-editor v-model="paramsProps.row.text" :height="400" />
        </template>
      </el-tab-pane>

      <el-tab-pane label="高级配置" name="advanced">
        <el-form
          label-width="140px"
          label-suffix=" :"
          :model="paramsProps.row"
          :disabled="paramsProps.readonly"
        >
          <el-form-item label="配置信息" prop="config">
            <el-input
              v-model="paramsProps.row.config"
              placeholder="请填写配置信息"
              type="textarea"
              :rows="10"
              clearable
              :disabled="paramsProps.readonly"
            ></el-input>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <el-button @click="visible = false">{{paramsProps.readonly ? '关闭' : '取消'}}</el-button>
      <el-button v-if="!paramsProps.readonly" type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { RefreshRight, InfoFilled } from '@element-plus/icons-vue'
import UploadImg from '@/components/Common/Upload/Img.vue'
import ColorSelect from '@/components/Common/Color/ColorSelect.vue'
import MarkdownEditor, { MarkdownPreview } from '@/components/Common/Markdown'

defineOptions({
  name: 'TenantForm'
})

// 表单规则
const rules = reactive({
  tenantCode: [{ required: true, message: '请填写租户编码，唯一标识' }],
  tenantName: [{ required: true, message: '请填写租户名称' }],
  contactName: [{ required: true, message: '请填写联系人姓名' }],
  contactPhone: [{ required: true, message: '请填写联系人手机号' }],
  contactEmail: [], // 移除必填验证
  show: [{ required: true, message: '请选择是否显示' }],
  password: [], // 移除必填验证
  enable: [{ required: true, message: '请选择状态' }],
  expiredTime: [{ required: true, message: '请选择到期时间' }],
  maxUserNum: [{ required: true, message: '请填写最大用户数' }],
})

// 对话框相关
const visible = ref(false)
const activeTab = ref('basic')
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined,
  readonly: false
})

// 文本颜色
const textColor = ref('#FFFFFF')

// 套餐选项
interface PackageOption {
  label: string;
  value: string;
  expiredTime?: number;
  maxUserNum?: number;
  price?: number;
}

const packageOptions: PackageOption[] = [
  { 
    label: '基础版（3个月）', 
    value: 'basic3', 
    expiredTime: 3, // 3个月
    maxUserNum: 10,
    price: 999
  },
  { 
    label: '基础版（1年）', 
    value: 'basic12', 
    expiredTime: 12, // 12个月
    maxUserNum: 10,
    price: 2999
  },
  { 
    label: '标准版（3个月）', 
    value: 'standard3', 
    expiredTime: 3, // 3个月
    maxUserNum: 50,
    price: 1999
  },
  { 
    label: '标准版（1年）', 
    value: 'standard12', 
    expiredTime: 12, // 12个月
    maxUserNum: 50,
    price: 5999
  },
  { 
    label: '企业版（3个月）', 
    value: 'enterprise3', 
    expiredTime: 3, // 3个月
    maxUserNum: 200,
    price: 3999
  },
  { 
    label: '企业版（1年）', 
    value: 'enterprise12', 
    expiredTime: 12, // 12个月
    maxUserNum: 200,
    price: 11999
  },
  { 
    label: '自定义', 
    value: 'custom'
  }
]

const selectedPackage = ref('')

// 套餐选择变更处理
const handlePackageChange = (value: string) => {
  const selectedOption = packageOptions.find(option => option.value === value)
  if (selectedOption && value !== 'custom') {
    // 计算到期时间
    const now = new Date()
    const expiredTime = new Date(now)
    expiredTime.setMonth(now.getMonth() + (selectedOption.expiredTime || 0))
    
    // 更新表单数据
    paramsProps.value.row.expiredTime = expiredTime.toISOString().slice(0, 19).replace('T', ' ')
    paramsProps.value.row.maxUserNum = selectedOption.maxUserNum
    paramsProps.value.row.currentUserNum = 0
  }
}

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  
  // 确保必要的字段都有默认值
  if (!paramsProps.value.row.themeColor) {
    paramsProps.value.row.themeColor = '#009688'
  }
  
  // 默认当前用户数为0
  if (paramsProps.value.row.currentUserNum === undefined) {
    paramsProps.value.row.currentUserNum = 0
  }
  
  // 默认启用状态
  if (paramsProps.value.row.enable === undefined) {
    paramsProps.value.row.enable = true
  }
  
  // 默认显示
  if (paramsProps.value.row.show === undefined) {
    paramsProps.value.row.show = true
  }
  
  // 确保text字段有默认值，防止Markdown编辑器报错
  if (paramsProps.value.row.text === undefined || paramsProps.value.row.text === null) {
    paramsProps.value.row.text = ''
  }
  
  visible.value = true
}

// 生成随机密码
const generatePassword = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*'
  let password = ''
  for (let i = 0; i < 12; i++) {
    password += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  paramsProps.value.row.password = password
}

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      await paramsProps.value.api!(paramsProps.value.row)
      ElMessage.success({ message: `${paramsProps.value.title}成功！` })
      paramsProps.value.getTableList!()
      visible.value = false
    } catch (error) {
      console.log(error)
    }
  })
}

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss">
.tenant-tabs {
  margin-bottom: 20px;
}

:deep(.el-tabs__nav) {
  padding-left: 20px;
}

:deep(.el-dialog__body) {
  padding-top: 10px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-select),
:deep(.el-date-editor) {
  width: 100%;
}

.password-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
  
  .el-icon {
    color: var(--el-color-primary);
    font-size: 14px;
  }
  
  span {
    line-height: 1.4;
  }
}
</style>