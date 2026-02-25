<template>
  <div class="applet-user-select">
    <el-select
      v-model="selectedUsers"
      :multiple="multiple"
      filterable
      :placeholder="placeholder"
      :clearable="clearable"
      class="apple-select"
      popper-class="apple-select-dropdown"
      :loading="loading"
      @change="handleChange"
      @visible-change="handleVisibleChange"
    >
      <!-- 搜索前缀图标 -->
      <template #prefix v-if="!loading">
        <i class="search-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16" style="fill: #007AFF;">
            <path d="M10,18c4.4,0,8-3.6,8-8s-3.6-8-8-8s-8,3.6-8,8S5.6,18,10,18z M10,4c3.3,0,6,2.7,6,6s-2.7,6-6,6s-6-2.7-6-6S6.7,4,10,4z"/>
            <path d="M21,21l-6-6l1.4-1.4l6,6L21,21z"/>
          </svg>
        </i>
      </template>

      <!-- 加载中状态 -->
      <template #empty v-if="loading">
        <div class="loading-state">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>正在加载用户数据...</span>
        </div>
      </template>

      <!-- 空状态 -->
      <template #empty v-else-if="filteredUsers.length === 0">
        <div class="empty-state">
          <el-empty description="暂无匹配用户" :image-size="60">
            <template #image>
              <el-icon class="empty-icon"><User /></el-icon>
            </template>
          </el-empty>
        </div>
      </template>

      <!-- 用户选项 -->
      <el-option
        v-for="user in filteredUsers"
        :key="user.id"
        :label="formatUserLabel(user)"
        :value="user.id"
      >
        <div class="user-option">
          <div class="user-info">
            <div class="user-main">
              <span class="user-id">#{{ user.id }}</span>
              <span class="user-id"> - </span>
              <span class="user-name">{{ user.nickname || user.name || '未知用户' }}</span>
            </div>
            <div class="user-secondary" v-if="user.openid || user.phone">
              <span class="user-openid" v-if="user.openid">{{ formatOpenId(user.openid) }}</span>
              <span class="user-phone" v-if="user.phone">{{ formatPhone(user.phone) }}</span>
            </div>
          </div>
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { Loading, User } from '@element-plus/icons-vue'
import { getAppletUserListApi } from '@/api/modules/system/applet/appletUser'
import type { IAppletUser } from '@/api/interface/system/applet/appletUser'

// 定义组件名称
defineOptions({
  name: 'AppletUserSelect'
})

// 定义Props接口
interface Props {
  // 绑定值
  modelValue?: number | number[]
  // 是否多选
  multiple?: boolean
  // 占位文本
  placeholder?: string
  // 是否可清空
  clearable?: boolean
  // 是否禁用
  disabled?: boolean
  // 每页加载数量
  pageSize?: number
}

// 设置Props默认值
const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  multiple: false,
  placeholder: '请选择小程序用户',
  clearable: true,
  disabled: false,
  pageSize: 20
})

// 定义组件事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: number | number[]): void
  (e: 'change', value: number | number[], users?: IAppletUser.Row | IAppletUser.Row[]): void
}>()

// 组件状态
const loading = ref(false)
const userList = ref<IAppletUser.Row[]>([])
const selectedUsers = computed({
  get: () => props.modelValue,
  set: (val) => {
    emit('update:modelValue', val as number | number[])
  }
})
const searchQuery = ref('')
const currentPage = ref(1)
const total = ref(0)

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  // 不需要设置selectedUsers，它是一个计算属性
})

// 筛选用户列表
const filteredUsers = computed(() => {
  if (!searchQuery.value) return userList.value

  const query = searchQuery.value.toLowerCase()
  return userList.value.filter(user => {
    return (
      (user.nickname?.toLowerCase().includes(query)) || 
      (user.name?.toLowerCase().includes(query)) || 
      (user.phone?.includes(query)) || 
      (user.openid?.toLowerCase().includes(query)) || 
      (user.id?.toString().includes(query))
    )
  })
})

// 格式化OpenID显示
const formatOpenId = (openid: string) => {
  if (!openid) return ''
  // 只显示前6位和后4位，中间用省略号替代
  if (openid.length > 10) {
    return `${openid.substring(0, 6)}...${openid.substring(openid.length - 4)}`
  }
  return openid
}

// 格式化手机号
const formatPhone = (phone: string) => {
  if (!phone) return ''
  // 格式化为 123-4567-8900 的形式
  if (phone.length === 11) {
    return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3')
  }
  return phone
}

// 格式化用户标签显示
const formatUserLabel = (user: IAppletUser.Row) => {
  const parts: string[] = []
  
  if (user.id) {
    parts.push(`#${user.id}`)
  }
  
  const displayName = user.nickname || user.name || '未知用户'
  parts.push(displayName)
  
  if (user.phone) {
    parts.push(formatPhone(user.phone))
  }
  
  return parts.join(' - ')
}

// 加载用户数据
const loadUsers = async (page = 1, limit = props.pageSize) => {
  loading.value = true
  try {
    const res = await getAppletUserListApi({
      page,
      limit,
      // 可以添加其他查询条件
      ...(searchQuery.value ? { nickname: searchQuery.value } : {})
    })
    
    if (page === 1) {
      // 重置列表
      userList.value = res.data.rows || []
    } else {
      // 追加数据
      userList.value = [...userList.value, ...(res.data.rows || [])]
    }
    
    total.value = res.data.total || 0
    currentPage.value = page
  } catch (error) {
    console.error('加载小程序用户数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索用户
const searchUsers = (query: string) => {
  searchQuery.value = query
  if (query) {
    // 有搜索词时重新加载第一页数据
    loadUsers(1)
  }
}

// 下拉框显示变化事件
const handleVisibleChange = (visible: boolean) => {
  if (visible && userList.value.length === 0) {
    // 首次打开下拉框且无数据时，加载数据
    loadUsers()
  }
}

// 选择变化事件
const handleChange = (value: number | number[]) => {
  emit('update:modelValue', value)
  
  // 找到选中的用户对象
  if (props.multiple) {
    // 多选模式
    const selectedItems = (value as number[]).map(id => 
      userList.value.find(user => user.id === id)
    ).filter(Boolean) as IAppletUser.Row[]
    
    emit('change', value, selectedItems)
  } else {
    // 单选模式
    const selectedItem = userList.value.find(user => user.id === value)
    emit('change', value, selectedItem)
  }
}

// 清空选中
const handleClear = () => {
  emit('update:modelValue', props.multiple ? [] : undefined as any)
  emit('change', props.multiple ? [] : undefined as any)
}

// 刷新数据
const refresh = () => {
  userList.value = []
  loadUsers()
}

// 组件挂载时初始化
onMounted(() => {
  // 不在挂载时立即加载数据，而是等到下拉框打开时再加载
  // 这样可以减少不必要的API调用
})

// 暴露组件方法
defineExpose({
  loadUsers,
  refresh,
  searchUsers,
  handleClear,
  userList
})
</script>

<style lang="scss" scoped>
.applet-user-select {
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;

  :deep(.el-select) {
    width: 100%;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);

    &:hover {
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.15);
    }

    &.is-focus {
      box-shadow: 0 0 0 2px #007AFF, 0 0 0 4px rgba(0, 122, 255, 0.1);
    }
  }

  :deep(.el-select__tags) {
    max-width: calc(100% - 30px);
    margin-top: 2px;
  }

  .search-icon {
    color: #007AFF;
    margin-right: 6px;
    display: flex;
    align-items: center;
  }

  .user-option {
    display: flex;
    align-items: center;
    padding: 8px 0;
    
    .avatar-container {
      margin-right: 12px;
      flex-shrink: 0;
    }
    
    .user-info {
      display: flex;
      flex-direction: column;
      flex: 1;
      min-width: 0;
      
      .user-main {
        display: flex;
        align-items: center;
        
        .user-id {
          font-weight: 500;
          color: #007AFF;
          margin-right: 8px;
          font-size: 13px;
        }
        
        .user-name {
          font-weight: 500;
          color: #1D1D1F;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
      
      .user-secondary {
        margin-top: 2px;
        font-size: 12px;
        color: #86868B;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        
        .user-openid {
          margin-right: 8px;
        }
      }
    }
  }
  
  .loading-state, .empty-state {
    padding: 20px;
    text-align: center;
    
    .is-loading {
      font-size: 20px;
      color: #007AFF;
      margin-right: 8px;
    }
    
    .empty-icon {
      font-size: 30px;
      color: #8E8E93;
    }
    
    span {
      font-size: 14px;
      color: #86868B;
    }
  }
}

:deep(.apple-select-dropdown) {
  border-radius: 10px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transform-origin: center top;
  border: none;
  
  .el-select-dropdown__item {
    &.hover, &:hover {
      background-color: rgba(0, 122, 255, 0.08);
    }
    
    &.selected {
      background-color: rgba(0, 122, 255, 0.15);
      color: #007AFF;
      font-weight: 500;
    }
  }
}
</style>
