<template>
  <el-dialog
    v-model="visible"
    :title="paramsProps.title"
    :destroy-on-close="true"
    width="650px"
    draggable
    class="apple-dialog"
  >
    <div class="user-form-container">
      <!-- 测试数据生成按钮 -->
      <div class="test-actions">
        <el-button
          type="primary"
          size="small"
          @click="generateTestData"
          class="test-btn"
        >
          <el-icon><DataAnalysis /></el-icon>
          生成测试数据
        </el-button>
      </div>

      <el-form
        ref="ruleFormRef"
        label-width="90px"
        label-suffix=""
        :rules="rules"
        :model="paramsProps.row"
        @submit.enter.prevent="handleSubmit"
        class="apple-form"
      >
        <!-- 基础信息卡片 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><User /></el-icon>
            <span>基础信息</span>
          </div>

          <div class="form-grid">
            <!-- 头像 -->
            <el-form-item label="头像" prop="avatar" class="avatar-item">
              <UploadImg
                v-model:image-url="paramsProps.row.avatar"
                :width="'60px'"
                :height="'60px'"
                :limit="1"
                :file-size="2"
                dir="avatar"
                accept="image/*"
                placeholder="上传头像"
              />
            </el-form-item>

            <!-- 姓名 -->
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="paramsProps.row.name"
                placeholder="请填写姓名"
                clearable
                class="apple-input"
              />
            </el-form-item>

            <!-- 昵称 -->
            <el-form-item label="昵称" prop="nickName">
              <el-input
                v-model="paramsProps.row.nickName"
                placeholder="请填写昵称"
                clearable
                class="apple-input"
              />
            </el-form-item>

            <!-- 手机号 -->
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="paramsProps.row.phone"
                placeholder="请填写手机号"
                clearable
                class="apple-input"
              />
            </el-form-item>

            <!-- 邮箱 -->
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="paramsProps.row.email"
                placeholder="请填写邮箱"
                clearable
                class="apple-input"
              />
            </el-form-item>

            <!-- 性别 -->
            <el-form-item label="性别" prop="sex">
              <EnumSelect
                v-model="paramsProps.row.sex"
                :enum-data="Sex"
                type="select"
                placeholder="请选择性别"
                class="apple-enum-select"
              />
            </el-form-item>

            <!-- 生日 -->
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="paramsProps.row.birthday"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择生日"
                clearable
                class="apple-date-picker"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 组织信息卡片 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><OfficeBuilding /></el-icon>
            <span>组织信息</span>
          </div>

          <div class="form-grid">
            <!-- 部门 -->
            <el-form-item label="部门" prop="deptId" class="full-width">
              <DeptSelect
                v-model="paramsProps.row.deptId"
                placeholder="请选择部门"
                clearable
              />
            </el-form-item>

            <!-- 班级 -->
            <el-form-item label="班级" prop="groupIds" class="full-width">
              <GroupSelect
                v-model="paramsProps.row.groupIds"
                :multiple="true"
                placeholder="请选择班级"
                clearable
              />
            </el-form-item>

            <!-- 角色 -->
            <el-form-item label="角色" prop="roleIds" class="full-width">
              <div class="role-select-container">
                <RoleSelect
                  v-model="paramsProps.row.roleIds"
                  :multiple="true"
                  :disabled="!hasRoleUpdatePermission"
                  placeholder="请选择角色"
                  clearable
                />
                <div v-if="!hasRoleUpdatePermission" class="permission-hint">
                  <el-text type="info" size="small">
                    <el-icon><Lock /></el-icon>
                    您没有修改用户角色的权限
                  </el-text>
                </div>
              </div>
            </el-form-item>
          </div>
        </div>

        <!-- 个性化设置卡片 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><Star /></el-icon>
            <span>个性化设置</span>
          </div>

          <div class="form-grid">
            <!-- 称号 -->
            <el-form-item label="称号" prop="title">
              <el-input
                v-model="paramsProps.row.title"
                placeholder="请填写称号"
                clearable
                class="apple-input"
              />
            </el-form-item>

            <!-- 颜色 -->
            <el-form-item label="主题颜色" prop="color">
              <ColorSelect
                v-model="colorValue"
                :show-preview="true"
                :preview-text="paramsProps.row.title || '预览文字'"
              />
            </el-form-item>

            <!-- 相关图片 -->
            <el-form-item label="背景图片" prop="image" class="full-width">
              <UploadImg
                v-model:image-url="paramsProps.row.image"
                :width="'90px'"
                :height="'50px'"
                :limit="1"
                :file-size="5"
                dir="user-bg"
                accept="image/*"
                placeholder="上传背景图片"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 统计信息卡片 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><TrendCharts /></el-icon>
            <span>统计信息</span>
          </div>

          <div class="form-grid stats-grid">
            <!-- AC数量 -->
            <el-form-item label="AC数量" prop="acNum">
              <el-input-number
                v-model="paramsProps.row.acNum"
                :precision="0"
                :min="0"
                :max="999999"
                controls-position="right"
                class="apple-number-input"
              />
            </el-form-item>

            <!-- 积分 -->
            <el-form-item label="积分" prop="integral">
              <el-input-number
                v-model="paramsProps.row.integral"
                :precision="0"
                :min="0"
                :max="999999"
                controls-position="right"
                class="apple-number-input"
              />
            </el-form-item>

            <!-- 连续签到时间 -->
            <el-form-item label="连续签到" prop="continuousSignDay">
              <el-input-number
                v-model="paramsProps.row.continuousSignDay"
                :precision="0"
                :min="0"
                :max="999999"
                controls-position="right"
                class="apple-number-input"
              />
            </el-form-item>

            <!-- 提交题目数量 -->
            <el-form-item label="提交数量" prop="submitNum">
              <el-input-number
                v-model="paramsProps.row.submitNum"
                :precision="0"
                :min="0"
                :max="999999"
                controls-position="right"
                class="apple-number-input"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 系统设置卡片 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </div>

          <div class="form-grid">
            <!-- 帐号过期时间 -->
            <el-form-item label="过期时间" prop="expirationTime">
              <el-date-picker
                v-model="paramsProps.row.expirationTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择帐号过期时间"
                clearable
                class="apple-date-picker"
              />
            </el-form-item>

            <!-- 备注 -->
            <el-form-item label="备注" prop="remark" class="full-width">
              <el-input
                v-model="paramsProps.row.remark"
                type="textarea"
                :rows="2"
                placeholder="请填写备注信息"
                class="apple-textarea"
              />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="cancel-btn">
          取消
        </el-button>
        <el-button type="primary" @click="handleSubmit" class="submit-btn">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import {
  User,
  OfficeBuilding,
  Star,
  TrendCharts,
  Setting,
  Check,
  DataAnalysis,
  Lock
} from '@element-plus/icons-vue'
import UploadImg from '@/components/Common/Upload/Img.vue'
import ColorSelect from '@/components/Common/Color/ColorSelect.vue'
import DeptSelect from '@/components/Common/Dept/DeptSelect.vue'
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue'
import RoleSelect from '@/components/System/RoleSelect.vue'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import { Sex } from '@/enums/common/Sex'
import type { IUserInfo } from '@/api/interface/oj/user/userInfo'
import { useAuthStore } from '@/stores/modules/auth'

defineOptions({
  name: 'UserInfoForm'
})

// 定义组件Props类型
interface Props {
  title: string
  row: IUserInfo.Form
  api?: (data: IUserInfo.Form) => Promise<any>
  getTableList?: () => void
}

// 计算属性
const colorValue = computed({
  get: () => paramsProps.value.row.color || '',
  set: (value: string) => {
    paramsProps.value.row.color = value
  }
})

// 权限检查
const authStore = useAuthStore()
const hasRoleUpdatePermission = computed(() => {
  const currentBtnPermissions = authStore.authButtonListGet ?? []
  const currentPageRoles: string[] = authStore.authRoleListGet ?? []

  const ADMIN_ROLE = 'admin'
  const ADMIN_BYPASS = import.meta.env.VITE_ADMIN_BYPASS_PERMISSION || 'true'

  // 如果配置允许对admin用户放行，并且当前用户角色包含admin，则放行
  if (ADMIN_BYPASS === 'true' && currentPageRoles.includes(ADMIN_ROLE)) {
    return true
  }

  // 检查是否有角色更新权限
  return currentBtnPermissions.includes('sys.role.update_btn')
})

// 表单验证规则 - 移除部门必选
const rules = reactive({
  name: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请填写手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请填写邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
})

const visible = ref(false)
const paramsProps = ref<Props>({
  title: '',
  row: {
    groupIds: [], // 新增班级字段
    roleIds: []   // 新增角色字段
  },
  api: undefined,
  getTableList: undefined
})

// 随机测试数据生成器
const generateTestData = () => {
  const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
  const nickNames = ['小张', '小李', '小王', '小赵', '小钱', '小孙', '小周', '小吴']
  const titles = ['算法达人', '编程高手', '代码艺术家', '系统架构师', '全栈开发者', '数据科学家', '机器学习专家', '前端大师']
  const colors = ['#007AFF', '#FF3B30', '#FF9500', '#FFCC00', '#34C759', '#5AC8FA', '#AF52DE', '#FF2D92']
  const domains = ['example.com', 'test.com', 'demo.org', 'sample.net', 'mock.io']
  const sexOptions = ['Male', 'Female']

  const randomName = names[Math.floor(Math.random() * names.length)]
  const randomNickName = nickNames[Math.floor(Math.random() * nickNames.length)]
  const randomTitle = titles[Math.floor(Math.random() * titles.length)]
  const randomColor = colors[Math.floor(Math.random() * colors.length)]
  const randomDomain = domains[Math.floor(Math.random() * domains.length)]
  const randomSex = sexOptions[Math.floor(Math.random() * sexOptions.length)]

  // 生成随机手机号 - 修复位数问题
  // 第一位：1，第二位：3-9，后面9位：随机数字
  const secondDigit = Math.floor(Math.random() * 7) + 3 // 3-9
  const lastNineDigits = Math.floor(Math.random() * 1000000000).toString().padStart(9, '0')
  const randomPhone = '1' + secondDigit + lastNineDigits

  // 生成随机邮箱
  const randomEmail = `${randomName.toLowerCase()}${Math.floor(Math.random() * 999)}@${randomDomain}`

  // 生成随机生日（1990-2005年之间）
  const randomYear = Math.floor(Math.random() * 15) + 1990
  const randomMonth = Math.floor(Math.random() * 12) + 1
  const randomDay = Math.floor(Math.random() * 28) + 1
  const randomBirthday = `${randomYear}-${randomMonth.toString().padStart(2, '0')}-${randomDay.toString().padStart(2, '0')}`

  // 生成随机过期时间（2025-2030年之间）
  const expYear = Math.floor(Math.random() * 5) + 2025
  const expMonth = Math.floor(Math.random() * 12) + 1
  const expDay = Math.floor(Math.random() * 28) + 1
  const randomExpiration = `${expYear}-${expMonth.toString().padStart(2, '0')}-${expDay.toString().padStart(2, '0')}`

  const testData: IUserInfo.Form = {
    name: randomName,
    nickName: randomNickName,
    phone: randomPhone,
    email: randomEmail,
    sex: randomSex,
    birthday: randomBirthday,
    avatar: `https://i.pravatar.cc/150?u=${Math.random()}`,
    image: `https://picsum.photos/seed/${Math.random()}/300/200`,
    title: randomTitle,
    color: randomColor,
    acNum: Math.floor(Math.random() * 500) + 1,
    integral: Math.floor(Math.random() * 5000) + 100,
    continuousSignDay: Math.floor(Math.random() * 100) + 1,
    submitNum: Math.floor(Math.random() * 1000) + 10,
    expirationTime: randomExpiration,
    remark: `这是${randomName}的测试账号，生成时间：${new Date().toLocaleString()}`,
    deptId: Math.floor(Math.random() * 10) + 1,
    groupIds: [],
    roleIds: []
  }

  // 合并测试数据到当前表单
  Object.assign(paramsProps.value.row, testData)

  ElMessage.success('随机测试数据生成成功！')
}

// 接收父组件传过来的参数
const acceptParams = (params: Props) => {
  paramsProps.value = params

  // 确保新字段有默认值
  if (!paramsProps.value.row.groupIds) {
    paramsProps.value.row.groupIds = []
  }
  if (!paramsProps.value.row.roleIds) {
    paramsProps.value.row.roleIds = []
  }

  // 处理从UserDetail传来的数据结构转换
  // 如果数据中包含完整的角色/班级对象，需要转换为ID数组
  const row = paramsProps.value.row as any

  // 转换角色数据：从roles对象数组转换为roleIds数组
  if (row.roles && Array.isArray(row.roles) && row.roles.length > 0) {
    paramsProps.value.row.roleIds = row.roles.map((role: any) => role.id).filter(Boolean)
    // 清理原始的roles数据，避免混淆
    delete row.roles
  }

  // 转换班级数据：从groups对象数组转换为groupIds数组
  if (row.groups && Array.isArray(row.groups) && row.groups.length > 0) {
    paramsProps.value.row.groupIds = row.groups.map((group: any) => group.id).filter(Boolean)
    // 清理原始的groups数据，避免混淆
    delete row.groups
  }

  visible.value = true
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
// 参考CourseForm的颜色风格
$primary-color: var(--el-color-primary);
$primary-light: var(--el-color-primary-light-3);
$primary-ultra-light: var(--el-color-primary-light-9);
$success-color: var(--el-color-success);
$warning-color: var(--el-color-warning);
$danger-color: var(--el-color-danger);
$text-primary: #1d1d1f;
$text-secondary: var(--el-text-color-regular);
$text-tertiary: var(--el-text-color-disabled);
$background: var(--el-bg-color-page);
$background-elevated: var(--el-bg-color);
$border-color: var(--el-border-color-light);
$border-radius: 12px;
$border-radius-small: 8px;
$shadow-light: 0 1px 4px rgba(0, 0, 0, 0.08);
$shadow-medium: 0 6px 12px rgba(0, 0, 0, 0.1);

:deep(.apple-dialog) {
  .el-dialog {
    border-radius: $border-radius;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1), 0 6px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;

    .el-dialog__header {
      padding: 20px 24px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      margin-right: 0;

      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: $text-primary;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
      }
    }

    .el-dialog__body {
      padding: 0;
    }

    .el-dialog__footer {
      padding: 16px 24px;
      border-top: 1px solid rgba(0, 0, 0, 0.05);
    }
  }
}

.user-form-container {
  max-height: 60vh;
  overflow-y: auto;
  padding: 12px 20px;

  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 3px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: $text-tertiary;
    border-radius: 2px;

    &:hover {
      background: $text-secondary;
    }
  }
}

.test-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;

  .test-btn {
    height: 28px;
    padding: 0 12px;
    border-radius: $border-radius-small;
    background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
    border: none;
    color: white;
    font-weight: 500;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 6px rgba(0, 122, 255, 0.2);
    }

    &:active {
      transform: translateY(0);
    }

    .el-icon {
      font-size: 12px;
    }
  }
}

.apple-form {
  .form-section {
    background: $background-elevated;
    border-radius: $border-radius;
    padding: 12px;
    margin-bottom: 8px;
    box-shadow: $shadow-light;
    border: 1px solid $border-color;
    transition: all 0.2s ease;

    &:hover {
      box-shadow: $shadow-medium;
      transform: translateY(-1px);
    }

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid $border-color;

      .el-icon {
        font-size: 16px;
        color: $primary-color;
      }

      span {
        font-size: 14px;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 12px;
      align-items: start;

      &.stats-grid {
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
      }

      .full-width {
        grid-column: 1 / -1;
      }

      .avatar-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        grid-column: 1 / -1;

        :deep(.el-form-item__content) {
          justify-content: center;
        }
      }
    }
  }

  // 表单项样式
  :deep(.el-form-item) {
    margin-bottom: 0;

    .el-form-item__label {
      font-weight: 500;
      color: $text-primary;
      font-size: 12px;
      padding-bottom: 4px;
    }

    .el-form-item__content {
      line-height: 1;
    }

    .el-form-item__error {
      font-size: 10px;
      color: $danger-color;
      padding-top: 2px;
    }
  }

  // 输入框样式
  .apple-input {
    :deep(.el-input__wrapper) {
      border: 1px solid $border-color;
      border-radius: $border-radius-small;
      padding: 0 10px;
      height: 32px;
      transition: all 0.2s ease;
      box-shadow: none;

      &:hover {
        border-color: $primary-light;
      }

      &.is-focus {
        border-color: $primary-color;
        box-shadow: 0 0 0 2px $primary-ultra-light;
      }

      .el-input__inner {
        color: $text-primary;
        font-size: 12px;

        &::placeholder {
          color: $text-secondary;
        }
      }
    }
  }

  // 枚举选择器样式
  .apple-enum-select {
    width: 100%;

    :deep(.el-select) {
      width: 100%;

      .el-input__wrapper {
        background: $background;
        border: 1px solid $border-color;
        border-radius: $border-radius-small;
        padding: 0 10px;
        height: 32px;
        transition: all 0.2s ease;

        &:hover {
          border-color: $primary-light;
          background: $background-elevated;
        }

        &.is-focus {
          border-color: $primary-color;
          background: $background-elevated;
          box-shadow: 0 0 0 2px $primary-ultra-light;
        }

        .el-input__inner {
          color: $text-primary;
          font-size: 12px;

          &::placeholder {
            color: $text-secondary;
          }
        }
      }
    }
  }

  // 日期选择器样式
  .apple-date-picker {
    width: 100%;

    :deep(.el-input__wrapper) {
      background: $background;
      border: 1px solid $border-color;
      border-radius: $border-radius-small;
      padding: 0 10px;
      height: 32px;
      transition: all 0.2s ease;

      &:hover {
        border-color: $primary-light;
        background: $background-elevated;
      }

      &.is-focus {
        border-color: $primary-color;
        background: $background-elevated;
        box-shadow: 0 0 0 2px $primary-ultra-light;
      }

      .el-input__inner {
        color: $text-primary;
        font-size: 12px;

        &::placeholder {
          color: $text-secondary;
        }
      }
    }
  }

  // 数字输入框样式
  .apple-number-input {
    width: 100%;

    :deep(.el-input__wrapper) {
      background: $background;
      border: 1px solid $border-color;
      border-radius: $border-radius-small;
      padding: 0 10px;
      height: 32px;
      transition: all 0.2s ease;

      &:hover {
        border-color: $primary-light;
        background: $background-elevated;
      }

      &.is-focus {
        border-color: $primary-color;
        background: $background-elevated;
        box-shadow: 0 0 0 2px $primary-ultra-light;
      }

      .el-input__inner {
        color: $text-primary;
        font-size: 12px;
      }
    }

    :deep(.el-input-number__increase),
    :deep(.el-input-number__decrease) {
      border: none;
      background: transparent;
      color: $primary-color;
      width: 20px;

      &:hover {
        background: $primary-ultra-light;
        color: $primary-color;
      }
    }
  }

  // 文本域样式
  .apple-textarea {
    :deep(.el-textarea__inner) {
      background: $background;
      border: 1px solid $border-color;
      border-radius: $border-radius-small;
      padding: 8px;
      font-size: 12px;
      color: $text-primary;
      transition: all 0.2s ease;
      resize: vertical;
      min-height: 50px;

      &:hover {
        border-color: $primary-light;
        background: $background-elevated;
      }

      &:focus {
        border-color: $primary-color;
        background: $background-elevated;
        box-shadow: 0 0 0 2px $primary-ultra-light;
      }

      &::placeholder {
        color: $text-secondary;
      }
    }
  }
}

// 角色选择器容器样式
.role-select-container {
  width: 100%;

  .permission-hint {
    margin-top: 6px;
    padding: 4px 8px;
    background-color: var(--el-color-info-light-9);
    border: 1px solid var(--el-color-info-light-7);
    border-radius: $border-radius-small;

    .el-text {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 11px;
      color: var(--el-color-info);

      .el-icon {
        font-size: 12px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;

  .cancel-btn {
    height: 32px;
    padding: 0 16px;
    border-radius: $border-radius-small;
    border: 1px solid $border-color;
    background: $background;
    color: $text-primary;
    font-weight: 500;
    font-size: 12px;
    transition: all 0.2s ease;

    &:hover {
      background: $text-tertiary;
      border-color: $text-secondary;
    }
  }

  .submit-btn {
    height: 32px;
    padding: 0 16px;
    border-radius: $border-radius-small;
    background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
    border: none;
    color: white;
    font-weight: 600;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 122, 255, 0.25);
    }

    &:active {
      transform: translateY(0);
    }

    .el-icon {
      font-size: 12px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-form-container {
    padding: 8px 12px;
  }

  .apple-form {
    .form-section {
      padding: 8px;
      margin-bottom: 6px;

      .form-grid {
        grid-template-columns: 1fr;
        gap: 8px;

        .full-width {
          grid-column: 1;
        }
      }
    }
  }

  :deep(.apple-dialog) {
    .el-dialog {
      margin: 10px;
      width: calc(100% - 20px) !important;
    }
  }
}
</style>
