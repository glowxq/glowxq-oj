<template>
  <div class="user-select">
    <el-select
      v-model="selectedUsers"
      :multiple="multiple"
      :collapse-tags="collapseTags"
      :max-collapse-tags="maxUserCount"
      :collapse-tags-tooltip="collapseTagsTooltip"
      remote
      :remote-method="remoteSearch"
      :loading="loading"
      :placeholder="placeholder"
      :clearable="clearable"
      class="apple-select"
      popper-class="apple-select-dropdown"
      @change="handleChange"
      @visible-change="handleDropdownVisibility"
    >
      <!-- 搜索前缀图标 -->
      <template #prefix>
        <el-icon class="search-prefix-icon"><User /></el-icon>
      </template>

      <!-- 空状态 -->
      <template #empty>
        <div class="empty-state">
          <div class="empty-icon-wrapper">
            <el-icon class="empty-icon"><UserFilled /></el-icon>
          </div>
          <p class="empty-text">暂无匹配用户</p>
          <p class="empty-hint">输入关键词搜索用户</p>
        </div>
      </template>

      <!-- 用户选项 -->
      <el-option
        v-for="item in userOptions"
        :key="item.id"
        :label="getUserDisplayName(item)"
        :value="item.id"
        class="user-option"
      >
        <div class="user-item">
          <div class="user-avatar">
            <el-icon><User /></el-icon>
          </div>
          <div class="user-info">
            <div class="user-name">{{ getUserDisplayName(item) }}</div>
            <div v-if="item.phone" class="user-phone">{{ item.phone }}</div>
          </div>
        </div>
      </el-option>

      <!-- 加载更多按钮 -->
      <div 
        v-if="hasMoreData" 
        class="load-more-wrapper"
      >
        <button 
          class="load-more-button"
          :class="{ 'loading': loadingMore }"
          @click="loadMoreData"
          :disabled="loadingMore"
        >
          <div class="load-more-content">
            <el-icon v-if="!loadingMore" class="load-more-icon"><ArrowDown /></el-icon>
            <el-icon v-else class="loading-icon"><Loading /></el-icon>
            <span class="load-more-text">
              {{ loadingMore ? '正在加载...' : '加载更多' }}
            </span>
          </div>
        </button>
      </div>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, UserFilled, ArrowDown, Loading } from '@element-plus/icons-vue'
import { searchUserList } from '@/api/modules/system/admin/user'
import type { IUser } from '@/api/interface/system/admin/user'

// 用户对象类型定义 - 使用 API 接口定义
type UserInfo = IUser.SearchUserVO

// 搜索参数类型 - 使用 API 接口定义
type SearchUserDTO = IUser.SearchUserDTO

defineOptions({
  name: 'UserSelect'
})

interface Props {
  modelValue?: number | number[]
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  collapseTags?: boolean
  collapseTagsTooltip?: boolean
  maxUserCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  placeholder: '请选择用户',
  clearable: true,
  modelValue: undefined,
  collapseTags: false,
  collapseTagsTooltip: true,
  maxUserCount: 10
})

const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const loadingMore = ref(false)
const userOptions = ref<UserInfo[]>([])
const selectedUsers = ref<number | number[] | undefined>(props.modelValue)
const searchQuery = ref('')
const currentPage = ref(1)
const total = ref(0)
const hasMoreData = ref(false)
const pageLimit = 10

// 获取用户显示名称
const getUserDisplayName = (user: UserInfo) => {
  console.log('获取用户显示名称:', user)
  return user.name || user.nickname || user.username || `用户${user.id}`
}

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== undefined) {
    selectedUsers.value = newVal
  } else {
    selectedUsers.value = props.multiple ? [] : undefined
  }
}, { immediate: true })

// 远程搜索
const remoteSearch = async (query: string) => {
  console.log('开始远程搜索:', query)
  searchQuery.value = query
  currentPage.value = 1
  loading.value = true
  
  try {
    const params: SearchUserDTO = {
      searchKey: query,
      page: currentPage.value,
      limit: pageLimit
    }
    
    const res = await searchUserList(params)
    console.log('搜索结果:', res)
    
    if (res.data) {
      userOptions.value = res.data.rows || []
      total.value = res.data.total || 0
      hasMoreData.value = userOptions.value.length < total.value
      
      console.log(`搜索用户成功，加载${userOptions.value.length}条数据，总计${total.value}条`)
      console.log('用户列表:', userOptions.value)
    } else {
      userOptions.value = []
      hasMoreData.value = false
    }
  } catch (error) {
    console.error('搜索用户失败:', error)
    ElMessage.error('搜索用户失败')
    userOptions.value = []
    hasMoreData.value = false
  } finally {
    loading.value = false
  }
}

// 加载更多数据
const loadMoreData = async () => {
  if (loadingMore.value || !hasMoreData.value) return
  
  console.log('开始加载更多数据')
  loadingMore.value = true
  currentPage.value++
  
  try {
    const params: SearchUserDTO = {
      searchKey: searchQuery.value,
      page: currentPage.value,
      limit: pageLimit
    }
    
    const res = await searchUserList(params)
    
    if (res.data && res.data.rows && res.data.rows.length > 0) {
      userOptions.value = [...userOptions.value, ...res.data.rows]
      hasMoreData.value = userOptions.value.length < total.value
      
      console.log(`加载更多用户成功，新增${res.data.rows.length}条数据`)
    } else {
      hasMoreData.value = false
      console.log('没有更多用户数据')
    }
  } catch (error) {
    console.error('加载更多用户失败:', error)
    currentPage.value--
    ElMessage.error('加载更多用户失败')
  } finally {
    loadingMore.value = false
  }
}

// 选择变化
const handleChange = (value: number | number[]) => {
  console.log('选择变化:', value)
  emit('update:modelValue', value)

  // 找到对应的用户对象并传递给父组件
  if (props.multiple) {
    // 多选模式
    const selectedUserObjects = (value as number[]).map(id =>
      userOptions.value.find(user => user.id === id)
    ).filter(Boolean);
    emit('change', value, selectedUserObjects)
  } else {
    // 单选模式
    const selectedUserObject = userOptions.value.find(user => user.id === value);
    emit('change', value, selectedUserObject)
  }
}

// 处理下拉框可见性变化
const handleDropdownVisibility = (visible: boolean) => {
  console.log('下拉框可见性变化:', visible)
  if (visible) {
    // 如果没有搜索过，则初始化搜索
    if (userOptions.value.length === 0) {
      remoteSearch('')
    }
  }
}

// 清空选择器的方法
const handleClear = () => {
  selectedUsers.value = props.multiple ? [] : undefined
  emit('update:modelValue', selectedUsers.value)
}

// 初始加载
onMounted(() => {
  console.log('UserSelect 组件已挂载')
  // 不需要初始加载，等用户打开下拉框或输入搜索内容时再加载
})

defineExpose({
  userOptions, // 暴露用户选项数据
  handleClear, // 暴露清空方法
  selectedUsers, // 暴露选中值，方便外部直接修改
  refresh: remoteSearch // 暴露刷新方法
})
</script>

<style scoped lang="scss">
.user-select {
  width: 100%;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;

  .apple-select {
    width: 100%;

    :deep(.el-input__wrapper) {
      border-radius: 12px;
      border: 1px solid rgba(0, 0, 0, 0.06);
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
      background: #ffffff;
      min-height: 44px;
      padding: 0 16px;
      
      &:hover {
        border-color: rgba(0, 0, 0, 0.12);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }
      
      &.is-focus {
        border-color: var(--el-color-primary);
        box-shadow: 0 0 0 3px rgba(var(--el-color-primary-rgb), 0.2), 0 2px 8px rgba(0, 0, 0, 0.08);
      }

      .el-input__inner {
        font-size: 16px;
        font-weight: 400;
        color: #1d1d1f;
        
        &::placeholder {
          color: #86868b;
          font-weight: 400;
        }
      }
    }

    :deep(.el-select__tags) {
      max-width: calc(100% - 30px);
      flex-wrap: wrap;
      gap: 6px;
      padding: 4px 0;
    }

    :deep(.el-tag) {
      margin: 0;
      border-radius: 8px;
      border: none;
      background: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
      font-size: 14px;
      font-weight: 500;
      padding: 4px 10px;
      height: auto;
      transition: all 0.2s ease;

      &:hover {
        background: var(--el-color-primary-light-8);
        transform: translateY(-1px);
      }

      .el-tag__close {
        color: rgba(var(--el-color-primary-rgb), 0.8);
        font-size: 12px;
        
        &:hover {
          background: rgba(0, 0, 0, 0.1);
          color: var(--el-color-primary);
        }
      }
    }
  }

  .search-prefix-icon {
    color: #86868b;
    font-size: 16px;
    margin-right: 4px;
  }

  .empty-state {
    padding: 40px 20px;
    text-align: center;
    
    .empty-icon-wrapper {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: #f2f2f7;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 16px;
      
      .empty-icon {
        font-size: 24px;
        color: #86868b;
      }
    }
    
    .empty-text {
      font-size: 17px;
      font-weight: 600;
      color: #1d1d1f;
      margin: 0 0 4px;
    }
    
    .empty-hint {
      font-size: 14px;
      color: #86868b;
      margin: 0;
    }
  }

  .load-more-wrapper {
    padding: 8px 0;
    
    .load-more-button {
      position: relative;
      margin: 12px auto 8px !important;
      width: 85% !important;
      height: 36px !important;
      border-radius: 18px !important;
      background: var(--el-color-primary-light-9) !important;
      border: 1px solid var(--el-color-primary-light-7) !important;
      overflow: hidden !important;
      cursor: pointer !important;
      box-shadow: 0 1px 3px rgba(var(--el-color-primary-rgb), 0.08) !important;
      transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1) !important;
      display: block !important;
      font-family: inherit !important;
      
      &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.6), transparent);
        transform: translateX(-100%);
        transition: transform 0.8s ease;
      }
      
      .load-more-content {
        position: relative;
        z-index: 2;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        height: 100%;
        color: var(--el-color-primary);
        font-size: 13px;
        font-weight: 500;
        
        .load-more-icon, .loading-icon {
          font-size: 14px;
          transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        }
        
        .loading-icon {
          animation: rotate 1.2s linear infinite;
        }
        
        .load-more-text {
          font-size: 13px;
          font-weight: 500;
          letter-spacing: -0.01em;
        }
      }
      
      &:hover:not(.loading) {
        background: var(--el-color-primary-light-8) !important;
        border-color: var(--el-color-primary-light-6) !important;
        transform: translateY(-1px);
        box-shadow: 0 3px 8px rgba(var(--el-color-primary-rgb), 0.15) !important;
        
        &::before {
          transform: translateX(100%);
        }
        
        .load-more-content {
          color: var(--el-color-primary-dark-2);
          
          .load-more-icon {
            transform: translateY(2px);
          }
        }
      }
      
      &:active:not(.loading) {
        transform: scale(0.97);
        background: var(--el-color-primary-light-7) !important;
        box-shadow: 0 2px 4px rgba(var(--el-color-primary-rgb), 0.1) !important;
      }
      
      &.loading {
        background: #f2f2f7 !important;
        border-color: #d1d1d6 !important;
        cursor: not-allowed;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05) !important;
        
        .load-more-content {
          color: #86868b;
          
          .loading-icon {
            font-size: 14px;
          }
        }
      }
      
      &:disabled {
        cursor: not-allowed;
      }
    }
  }
}
</style>

<style lang="scss">
/* 全局样式，影响下拉框 */
.apple-select-dropdown {
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12), 0 2px 6px rgba(0, 0, 0, 0.08) !important;
  overflow: hidden;
  background: #ffffff;
  padding: 8px 0;
  margin-top: 8px;

  .el-select-dropdown__item {
    height: auto !important;
    line-height: normal !important;
    padding: 0 !important;
    margin: 0 8px 4px;
    border-radius: 10px;
    transition: all 0.2s ease;

    &:last-child {
      margin-bottom: 0;
    }

    &.hover, &:hover {
      background-color: #f2f2f7 !important;
    }

    &.selected {
      background-color: rgba(var(--el-color-primary-rgb), 0.1) !important;
      
      .user-item {
        .user-name {
          color: var(--el-color-primary) !important;
          font-weight: 600;
        }
      }
    }

    .user-item {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      width: 100%;
      
      .user-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        flex-shrink: 0;
        
        .el-icon {
          font-size: 18px;
          color: #ffffff;
        }
      }
      
      .user-info {
        flex: 1;
        min-width: 0;
        
        .user-name {
          font-size: 15px;
          font-weight: 500;
          color: #1d1d1f;
          margin-bottom: 2px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .user-phone {
          font-size: 13px;
          color: #86868b;
          font-weight: 400;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }

  .load-more-wrapper {
    margin: 0;
    border-radius: 0;
    padding: 8px 0;
    
    .load-more-button {
      position: relative;
      margin: 12px auto 8px !important;
      width: 85% !important;
      height: 36px !important;
      border-radius: 18px !important;
      background: var(--el-color-primary-light-9) !important;
      border: 1px solid var(--el-color-primary-light-7) !important;
      overflow: hidden !important;
      cursor: pointer !important;
      box-shadow: 0 1px 3px rgba(var(--el-color-primary-rgb), 0.08) !important;
      transition: all 0.3s cubic-bezier(0.42, 0, 0.58, 1) !important;
      display: block !important;
      font-family: inherit !important;
      
      &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.6), transparent);
        transform: translateX(-100%);
        transition: transform 0.8s ease;
      }
      
      .load-more-content {
        position: relative;
        z-index: 2;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        height: 100%;
        color: var(--el-color-primary);
        font-size: 13px;
        font-weight: 500;
        
        .load-more-icon, .loading-icon {
          font-size: 14px;
          transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        }
        
        .loading-icon {
          animation: rotate 1.2s linear infinite;
        }
        
        .load-more-text {
          font-size: 13px;
          font-weight: 500;
          letter-spacing: -0.01em;
        }
      }
      
      &:hover:not(.loading) {
        background: var(--el-color-primary-light-8) !important;
        border-color: var(--el-color-primary-light-6) !important;
        transform: translateY(-1px);
        box-shadow: 0 3px 8px rgba(var(--el-color-primary-rgb), 0.15) !important;
        
        &::before {
          transform: translateX(100%);
        }
        
        .load-more-content {
          color: var(--el-color-primary-dark-2);
          
          .load-more-icon {
            transform: translateY(2px);
          }
        }
      }
      
      &:active:not(.loading) {
        transform: scale(0.97);
        background: var(--el-color-primary-light-7) !important;
        box-shadow: 0 2px 4px rgba(var(--el-color-primary-rgb), 0.1) !important;
      }
      
      &.loading {
        background: #f2f2f7 !important;
        border-color: #d1d1d6 !important;
        cursor: not-allowed;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05) !important;
        
        .load-more-content {
          color: #86868b;
          
          .loading-icon {
            font-size: 14px;
          }
        }
      }
      
      &:disabled {
        cursor: not-allowed;
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-select {
    .apple-select {
      :deep(.el-input__wrapper) {
        min-height: 40px;
        
        .el-input__inner {
          font-size: 16px; // 防止iOS缩放
        }
      }
    }
  }
  
  .apple-select-dropdown {
    .el-select-dropdown__item {
      .user-item {
        padding: 10px 12px;
        
        .user-avatar {
          width: 32px;
          height: 32px;
          margin-right: 10px;
          
          .el-icon {
            font-size: 16px;
          }
        }
        
        .user-info {
          .user-name {
            font-size: 14px;
          }
          
          .user-phone {
            font-size: 12px;
          }
        }
      }
    }
  }
}
</style>
