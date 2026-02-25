<template>
  <el-dialog
    v-model="visible"
    title="用户详情"
    width="700px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    class="apple-dialog user-detail-dialog"
    align-center
    :destroy-on-close="true"
  >
    <div v-if="loading" class="detail-loading">
      <el-loading :lock="true" text="加载中..." />
    </div>
    <div v-else class="user-detail-container">
      <!-- 用户头部信息 -->
      <div class="user-header" :style="headerStyle">
        <div class="header-content">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar
                :src="userDetail.avatar"
                :size="64"
                class="user-avatar"
              >
                <span v-if="!userDetail.avatar" class="avatar-text">
                  {{ userDetail.name?.charAt(0) || userDetail.nickName?.charAt(0) || 'U' }}
                </span>
              </el-avatar>
              <div v-if="userDetail.title" class="user-badge">
                <UserTitle
                  :title="userDetail.title"
                  :color="userDetail.color"
                  size="small"
                />
              </div>
            </div>
            <div class="user-info">
              <h3 class="user-name">{{ userDetail.name || userDetail.nickName || '-' }}</h3>
              <p class="user-subtitle" v-if="userDetail.nickName && userDetail.name !== userDetail.nickName">
                昵称：{{ userDetail.nickName }}
              </p>
            </div>
          </div>

          <!-- 统计卡片 -->
          <div class="stats-container">
            <div class="stats-grid">
              <div class="stat-card" v-for="(stat, index) in statsData" :key="stat.label" :style="{ animationDelay: `${index * 0.1}s` }">
                <div class="stat-icon">
                  <el-icon><component :is="stat.icon" /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ stat.value }}</div>
                  <div class="stat-label">{{ stat.label }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细信息内容 -->
      <div class="detail-content">
        <!-- 基本信息 -->
        <div class="detail-section" v-if="basicInfoData.length > 0">
          <div class="section-header">
            <el-icon class="section-icon"><User /></el-icon>
            <h4 class="section-title">基本信息</h4>
          </div>
          <div class="info-grid">
            <div
              class="info-item"
              v-for="(item, index) in basicInfoData"
              :key="item.label"
              :style="{ animationDelay: `${index * 0.05}s` }"
            >
              <div class="info-label">
                <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
              <div class="info-value">
                <EnumShow
                  v-if="item.key === 'sex' && userDetail.sex"
                  :enum="Sex"
                  :code="userDetail.sex"
                  :background-color="userDetail.sex === 'Male' ? '#e1f3ff' : '#fce8f3'"
                  :color="userDetail.sex === 'Male' ? '#409eff' : '#f56c6c'"
                  :border-color="userDetail.sex === 'Male' ? '#b3d8ff' : '#fdb9cc'"
                  font-size="11px"
                  padding="2px 6px"
                  border-radius="4px"
                />
                <span v-else>{{ item.value || '-' }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 角色信息 -->
        <div class="detail-section" v-if="userDetail.roles && userDetail.roles.length > 0">
          <div class="section-header">
            <el-icon class="section-icon"><UserFilled /></el-icon>
            <h4 class="section-title">角色信息</h4>
          </div>
          <div class="tags-container">
            <div
              class="role-tag"
              v-for="(role, index) in userDetail.roles"
              :key="role.id"
              :style="{ animationDelay: `${index * 0.05}s` }"
            >
              <div class="tag-content">
                <span class="tag-name">{{ role.roleName }}</span>
                <span v-if="role.remark" class="tag-remark">{{ role.remark }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 标签信息 -->
        <div class="detail-section" v-if="userDetail.tags && userDetail.tags.length > 0">
          <div class="section-header">
            <el-icon class="section-icon"><Collection /></el-icon>
            <h4 class="section-title">标签信息</h4>
          </div>
          <div class="tags-container">
            <div
              class="meta-tag"
              v-for="(tag, index) in userDetail.tags"
              :key="tag.id"
              :style="{
                backgroundColor: tag.backgroundColor || '#f0f0f0',
                color: tag.textColor || '#333',
                animationDelay: `${index * 0.05}s`
              }"
            >
              {{ tag.name }}
            </div>
          </div>
        </div>

        <!-- 班级信息 -->
        <div class="detail-section" v-if="userDetail.groups && userDetail.groups.length > 0">
          <div class="section-header">
            <el-icon class="section-icon"><School /></el-icon>
            <h4 class="section-title">班级信息</h4>
          </div>
          <div class="groups-container">
            <div
              class="group-card"
              v-for="(group, index) in userDetail.groups"
              :key="group.id"
              :style="{ animationDelay: `${index * 0.05}s` }"
            >
              <div
                class="group-header"
                :style="{
                  backgroundColor: group.color || '#409eff',
                  color: group.textColor || '#ffffff'
                }"
              >
                <div class="group-name">{{ group.name }}</div>
                <div v-if="group.code" class="group-code">{{ group.code }}</div>
              </div>
              <div class="group-body">
                <div v-if="group.principalName" class="group-info">
                  <span class="info-label">负责人：</span>
                  <span class="info-value">{{ group.principalName }}</span>
                </div>
                <div v-if="group.description" class="group-description">
                  {{ group.description }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 相关图片 -->
        <div class="detail-section" v-if="userDetail.image">
          <div class="section-header">
            <el-icon class="section-icon"><Picture /></el-icon>
            <h4 class="section-title">相关图片</h4>
          </div>
          <div class="image-section">
            <div class="image-wrapper">
              <el-image
                :src="userDetail.image"
                fit="cover"
                :preview-src-list="[userDetail.image]"
                preview-teleported
                class="detail-image"
              />
            </div>
          </div>
        </div>

        <!-- 备注信息 -->
        <div class="detail-section" v-if="userDetail.remark">
          <div class="section-header">
            <el-icon class="section-icon"><DocumentCopy /></el-icon>
            <h4 class="section-title">备注信息</h4>
          </div>
          <div class="remark-section">
            <div class="remark-content">
              {{ userDetail.remark }}
            </div>
          </div>
        </div>

        <!-- 系统信息 -->
        <div class="detail-section" v-if="systemInfoData.length > 0">
          <div class="section-header">
            <el-icon class="section-icon"><Setting /></el-icon>
            <h4 class="section-title">系统信息</h4>
          </div>
          <div class="info-grid">
            <div
              class="info-item"
              v-for="(item, index) in systemInfoData"
              :key="item.label"
              :style="{ animationDelay: `${index * 0.05}s` }"
            >
              <div class="info-label">
                <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
              <div class="info-value">{{ item.value || '-' }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="cancel-btn">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
        <el-button
          v-auth="'user.info.update'"
          type="primary"
          @click="handleEdit"
          class="edit-btn"
        >
          <el-icon><EditPen /></el-icon>
          编辑
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  User,
  Phone,
  Message,
  Calendar,
  Clock,
  Key,
  Setting,
  Picture,
  DocumentCopy,
  Close,
  EditPen,
  Trophy,
  Star,
  TrendCharts,
  DocumentChecked,
  UserFilled,
  Collection,
  School
} from '@element-plus/icons-vue'
import UserTitle from '@/components/Oj/User/UserTitle.vue'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import { Sex } from '@/enums/common/Sex'
import type { IUserInfo } from '@/api/interface/oj/user/userInfo'
import { getUserInfoDetailApi } from '@/api/modules/oj/user/userInfo'
import { ElMessage } from 'element-plus'

defineOptions({
  name: 'UserDetail'
})

const emit = defineEmits<{
  edit: [userInfo: IUserInfo.Row]
}>()

const visible = ref(false)
const loading = ref(false)
const userDetail = ref<IUserInfo.Row>({})

// 头部背景样式
const headerStyle = computed(() => {
  const baseStyle = 'linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%)'

  if (userDetail.value.color) {
    return {
      background: `linear-gradient(135deg, ${userDetail.value.color} 0%, ${adjustBrightness(userDetail.value.color, -20)} 100%)`
    }
  }

  return { background: baseStyle }
})

// 统计数据
const statsData = computed(() => [
  {
    label: 'AC题数',
    value: userDetail.value.acNum || 0,
    icon: Trophy
  },
  {
    label: '积分',
    value: userDetail.value.integral || 0,
    icon: Star
  },
  {
    label: '连续签到',
    value: userDetail.value.continuousSignDay || 0,
    icon: TrendCharts
  },
  {
    label: '提交数',
    value: userDetail.value.submitNum || 0,
    icon: DocumentChecked
  }
])

// 基本信息数据
const basicInfoData = computed(() => {
  const data = []

  if (userDetail.value.userId) {
    data.push({ label: '用户ID', value: userDetail.value.userId, icon: Key, key: 'userId' })
  }
  if (userDetail.value.phone) {
    data.push({ label: '手机号', value: userDetail.value.phone, icon: Phone, key: 'phone' })
  }
  if (userDetail.value.email) {
    data.push({ label: '邮箱', value: userDetail.value.email, icon: Message, key: 'email' })
  }
  if (userDetail.value.sex) {
    data.push({ label: '性别', value: userDetail.value.sex, icon: User, key: 'sex' })
  }
  if (userDetail.value.birthday) {
    data.push({ label: '生日', value: userDetail.value.birthday, icon: Calendar, key: 'birthday' })
  }
  if (userDetail.value.expirationTime) {
    data.push({ label: '过期时间', value: userDetail.value.expirationTime, icon: Clock, key: 'expirationTime' })
  }
  if (userDetail.value.lastLoginTime) {
    data.push({ label: '最后登录', value: userDetail.value.lastLoginTime, icon: Clock, key: 'lastLoginTime' })
  }
  if (userDetail.value.lastLoginIp) {
    data.push({ label: '最后登录IP', value: userDetail.value.lastLoginIp, icon: Setting, key: 'lastLoginIp' })
  }

  return data
})

// 系统信息数据
const systemInfoData = computed(() => {
  const data = []

  if (userDetail.value.createTime) {
    data.push({ label: '创建时间', value: userDetail.value.createTime, icon: Calendar })
  }
  if (userDetail.value.updateTime) {
    data.push({ label: '更新时间', value: userDetail.value.updateTime, icon: Clock })
  }
  if (userDetail.value.tenantId) {
    data.push({ label: '租户ID', value: userDetail.value.tenantId, icon: Key })
  }

  return data
})

// 调整颜色亮度
const adjustBrightness = (color: string, percent: number) => {
  const num = parseInt(color.replace('#', ''), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) + amt
  const G = (num >> 8 & 0x00FF) + amt
  const B = (num & 0x0000FF) + amt
  return '#' + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
    (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
    (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)
}

// 打开详情
const openDetail = async (row: IUserInfo.Row) => {
  visible.value = true
  loading.value = true
  try {
    if (!row.id) return;
    const record = await getUserInfoDetailApi({ id: row.id })
    userDetail.value = record?.data || {}
  } catch (error) {
    console.error('加载用户详情失败:', error)
    ElMessage.error('加载用户详情失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理编辑
const handleEdit = () => {
  visible.value = false
  emit('edit', userDetail.value)
}

defineExpose({
  openDetail
})
</script>

<style scoped lang="scss">
// 参考UserInfoForm的颜色风格
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

// 动画关键帧
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

:deep(.user-detail-dialog) {
  .el-dialog {
    border-radius: $border-radius;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1), 0 6px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;

    .el-dialog__header {
      padding: 0;
      margin: 0;
      border: none;
      background: transparent;

      .el-dialog__title {
        display: none;
      }

      .el-dialog__headerbtn {
        position: absolute;
        top: 16px;
        right: 16px;
        z-index: 10;

        .el-dialog__close {
          color: white;
          font-size: 16px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 50%;
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.3s ease;

          &:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: scale(1.1);
          }
        }
      }
    }

    .el-dialog__body {
      padding: 0;
      max-height: 65vh;
      overflow-y: auto;

      &::-webkit-scrollbar {
        width: 4px;
      }

      &::-webkit-scrollbar-track {
        background: $background;
      }

      &::-webkit-scrollbar-thumb {
        background: $border-color;
        border-radius: 2px;

        &:hover {
          background: $text-secondary;
        }
      }
    }

    .el-dialog__footer {
      padding: 16px 20px;
      border-top: 1px solid $border-color;
      background: $background;
    }
  }
}

.detail-loading {
  height: 200px;
  position: relative;
}

.user-detail-container {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
  animation: fadeInScale 0.4s ease-out;
}

.user-header {
  color: white;
  padding: 20px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
  }

  .header-content {
    position: relative;
    z-index: 1;
  }

  .avatar-section {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    .avatar-wrapper {
      position: relative;

      .user-avatar {
        border: 3px solid rgba(255, 255, 255, 0.3);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
        animation: pulse 2s infinite;

        .avatar-text {
          font-size: 24px;
          font-weight: 600;
          color: white;
        }
      }

      .user-badge {
        position: absolute;
        bottom: -6px;
        left: 50%;
        transform: translateX(-50%);
      }
    }

    .user-info {
      flex: 1;

      .user-name {
        font-size: 20px;
        font-weight: 700;
        margin: 0 0 4px 0;
        color: white;
        text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
      }

      .user-subtitle {
        font-size: 13px;
        margin: 0;
        color: rgba(255, 255, 255, 0.8);
        font-weight: 500;
      }
    }
  }

  .stats-container {
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 10px;

      .stat-card {
        background: rgba(255, 255, 255, 0.15);
        border-radius: $border-radius-small;
        padding: 12px 8px;
        text-align: center;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2);
        transition: all 0.3s ease;
        animation: slideInUp 0.6s ease-out both;

        &:hover {
          background: rgba(255, 255, 255, 0.2);
          transform: translateY(-2px);
        }

        .stat-icon {
          margin-bottom: 4px;

          .el-icon {
            font-size: 16px;
            color: rgba(255, 255, 255, 0.9);
          }
        }

        .stat-content {
          .stat-value {
            font-size: 16px;
            font-weight: 700;
            color: white;
            margin-bottom: 2px;
            line-height: 1;
          }

          .stat-label {
            font-size: 10px;
            color: rgba(255, 255, 255, 0.8);
            font-weight: 500;
          }
        }
      }
    }
  }
}

.detail-content {
  padding: 16px 20px;

  .detail-section {
    margin-bottom: 20px;
    animation: slideInUp 0.6s ease-out both;

    &:last-child {
      margin-bottom: 0;
    }

    .section-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 2px solid $primary-ultra-light;

      .section-icon {
        font-size: 14px;
        color: $primary-color;
      }

      .section-title {
        font-size: 14px;
        font-weight: 600;
        color: $text-primary;
        margin: 0;
      }
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 8px;

      .info-item {
        background: $background-elevated;
        border-radius: $border-radius-small;
        padding: 10px;
        border: 1px solid $border-color;
        transition: all 0.3s ease;
        animation: slideInUp 0.6s ease-out both;

        &:hover {
          background: $primary-ultra-light;
          border-color: $primary-light;
          transform: translateY(-1px);
          box-shadow: $shadow-light;
        }

        .info-label {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 11px;
          color: $text-secondary;
          margin-bottom: 4px;
          font-weight: 500;

          .el-icon {
            font-size: 12px;
            color: $primary-color;
          }
        }

        .info-value {
          font-size: 12px;
          color: $text-primary;
          font-weight: 500;
          word-break: break-all;
          line-height: 1.3;
        }
      }
    }

    .image-section {
      display: flex;
      justify-content: center;

      .image-wrapper {
        position: relative;
        border-radius: $border-radius;
        overflow: hidden;
        box-shadow: $shadow-medium;
        transition: all 0.3s ease;

        &:hover {
          transform: scale(1.02);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }

        .detail-image {
          width: 160px;
          height: 120px;
          border-radius: $border-radius;
        }
      }
    }

    .remark-section {
      .remark-content {
        background: $background-elevated;
        border-radius: $border-radius;
        padding: 12px;
        border: 1px solid $border-color;
        font-size: 12px;
        line-height: 1.5;
        color: $text-primary;
        border-left: 4px solid $primary-color;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;

  .cancel-btn {
    height: 32px;
    padding: 0 12px;
    border-radius: $border-radius-small;
    border: 1px solid $border-color;
    background: $background;
    color: $text-primary;
    font-weight: 500;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: all 0.3s ease;

    &:hover {
      background: $text-tertiary;
      border-color: $text-secondary;
      transform: translateY(-1px);
    }

    .el-icon {
      font-size: 12px;
    }
  }

  .edit-btn {
    height: 32px;
    padding: 0 12px;
    border-radius: $border-radius-small;
    background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
    border: none;
    color: white;
    font-weight: 600;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
    }

    &:active {
      transform: translateY(0);
    }

    .el-icon {
      font-size: 12px;
    }
  }
}

// 角色、标签和班级信息样式
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  .role-tag {
    background: $background-elevated;
    border: 1px solid $primary-light;
    border-radius: $border-radius-small;
    padding: 8px 12px;
    animation: slideInUp 0.6s ease-out both;
    transition: all 0.3s ease;

    &:hover {
      background: $primary-ultra-light;
      transform: translateY(-1px);
      box-shadow: $shadow-light;
    }

    .tag-content {
      display: flex;
      flex-direction: column;
      gap: 2px;

      .tag-name {
        font-size: 12px;
        font-weight: 600;
        color: $primary-color;
      }

      .tag-remark {
        font-size: 10px;
        color: $text-secondary;
        font-weight: 400;
      }
    }
  }

  .meta-tag {
    padding: 4px 8px;
    border-radius: $border-radius-small;
    font-size: 11px;
    font-weight: 500;
    animation: slideInUp 0.6s ease-out both;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: $shadow-light;
    }
  }
}

.groups-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;

  .group-card {
    background: $background-elevated;
    border-radius: $border-radius;
    overflow: hidden;
    border: 1px solid $border-color;
    transition: all 0.3s ease;
    animation: slideInUp 0.6s ease-out both;

    &:hover {
      transform: translateY(-2px);
      box-shadow: $shadow-medium;
    }

    .group-header {
      padding: 8px 12px;
      font-weight: 600;

      .group-name {
        font-size: 13px;
        margin-bottom: 2px;
      }

      .group-code {
        font-size: 10px;
        opacity: 0.8;
      }
    }

    .group-body {
      padding: 8px 12px;

      .group-info {
        margin-bottom: 4px;

        .info-label {
          font-size: 10px;
          color: $text-secondary;
          font-weight: 500;
        }

        .info-value {
          font-size: 11px;
          color: $text-primary;
          font-weight: 500;
        }
      }

      .group-description {
        font-size: 10px;
        color: $text-secondary;
        line-height: 1.4;
        margin-top: 4px;
        padding-top: 4px;
        border-top: 1px solid $border-color;
      }
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  :deep(.user-detail-dialog) {
    .el-dialog {
      width: 90% !important;
      margin: 5vh auto;
    }
  }

  .user-header {
    .stats-container .stats-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 8px;

      .stat-card {
        padding: 10px 6px;

        .stat-content {
          .stat-value {
            font-size: 14px;
          }

          .stat-label {
            font-size: 9px;
          }
        }
      }
    }
  }

  .detail-content {
    padding: 12px 16px;

    .detail-section {
      .info-grid {
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 6px;

        .info-item {
          padding: 8px;
        }
      }

      .image-section .image-wrapper .detail-image {
        width: 120px;
        height: 90px;
      }
    }
  }
}

@media (max-width: 768px) {
  :deep(.user-detail-dialog) {
    .el-dialog {
      width: 95% !important;
      margin: 2vh auto;
    }
  }

  .user-header {
    padding: 16px;

    .avatar-section {
      flex-direction: column;
      text-align: center;
      gap: 12px;

      .user-info {
        .user-name {
          font-size: 18px;
        }

        .user-subtitle {
          font-size: 12px;
        }
      }
    }

    .stats-container .stats-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 6px;

      .stat-card {
        padding: 8px 4px;

        .stat-icon .el-icon {
          font-size: 14px;
        }

        .stat-content {
          .stat-value {
            font-size: 12px;
          }

          .stat-label {
            font-size: 8px;
          }
        }
      }
    }
  }

  .detail-content {
    padding: 8px 12px;

    .detail-section {
      margin-bottom: 16px;

      .section-header {
        margin-bottom: 8px;

        .section-icon {
          font-size: 12px;
        }

        .section-title {
          font-size: 12px;
        }
      }

      .info-grid {
        grid-template-columns: 1fr;
        gap: 4px;

        .info-item {
          padding: 6px;

          .info-label {
            font-size: 10px;

            .el-icon {
              font-size: 10px;
            }
          }

          .info-value {
            font-size: 11px;
          }
        }
      }

      .image-section .image-wrapper .detail-image {
        width: 100px;
        height: 75px;
      }

      .remark-section .remark-content {
        padding: 8px;
        font-size: 11px;
      }
    }
  }

  .dialog-footer {
    flex-direction: column;
    gap: 6px;

    .cancel-btn,
    .edit-btn {
      width: 100%;
      height: 36px;
      justify-content: center;
    }
  }
}
</style>
