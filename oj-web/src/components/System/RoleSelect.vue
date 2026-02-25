<template>
  <div class="role-select">
    <el-select
      v-model="selectedRoles"
      :multiple="multiple"
      :collapse-tags="false"
      :max-collapse-tags="maxRoleCount"
      :collapse-tags-tooltip="collapseTagsTooltip"
      filterable
      remote
      :remote-method="remoteSearch"
      :loading="loading"
      :placeholder="placeholder"
      :clearable="clearable"
      class="apple-select enhanced-role-select"
      popper-class="apple-select-dropdown custom-role-select-dropdown"
      @change="handleChange"
      @visible-change="handleDropdownVisibility"
    >
      <!-- 自定义角色标签渲染 -->
      <template #tag="{ closable, onClose }">
        <el-tag
          v-for="role in selectedRolesData"
          :key="role.id"
          :closable="closable"
          class="custom-role-tag"
          type="primary"
          effect="light"
          @close="onClose"
        >
          <el-icon class="role-icon"><User /></el-icon>
          {{ role.roleName }}
        </el-tag>
      </template>

      <!-- 空状态 -->
      <template #empty>
        <div class="empty-state">
          <el-empty description="暂无匹配角色" :image-size="60">
            <template #image>
              <el-icon class="empty-icon"><UserFilled /></el-icon>
            </template>
          </el-empty>
        </div>
      </template>

      <!-- 分组：常用角色 -->
      <el-option-group v-if="frequentRoles.length > 0" label="常用角色">
        <el-option
          v-for="item in frequentRoles"
          :key="item.id"
          :label="item.roleName"
          :value="item.id"
        >
          <div class="role-option">
            <div class="role-info">
              <div class="role-header">
                <el-icon class="role-icon-option"><User /></el-icon>
                <span class="role-name">{{ item.roleName }}</span>
                <el-tag size="small" type="success" class="frequent-tag">常用</el-tag>
              </div>
              <div class="role-permissions-container" v-if="item.permissions">
                <div class="permissions-tags">
                  <el-tag
                    size="small"
                    class="permission-tag"
                    type="info"
                    effect="plain"
                  >
                    {{ item.permissions }}
                  </el-tag>
                </div>
              </div>
            </div>
            <!--<span class="role-id">#{{ item.id }}</span>-->
          </div>
        </el-option>
      </el-option-group>

      <!-- 所有角色 -->
      <el-option-group v-if="roleOptions.length > 0" :label="frequentRoles.length ? '所有角色' : undefined">
        <el-option
          v-for="item in roleOptions"
          :key="item.id"
          :label="item.roleName"
          :value="item.id"
        >
          <div class="role-option">
            <div class="role-info">
              <div class="role-header">
                <el-icon class="role-icon-option"><User /></el-icon>

                <span class="role-name">{{ item.roleName }}</span>
                <span class="role-permissions"> - </span>
                <el-tag
                    size="small"
                    class="permission-tag"
                    effect="plain"
                  >
                    {{ item.permissions }}
                  </el-tag>
              </div>

            </div>
            <!--<span class="role-id">#{{ item.id }}</span>-->
          </div>
        </el-option>
      </el-option-group>

      <!-- 加载更多提示 -->
      <div
        v-if="hasMoreData"
        class="load-more-button"
        ref="loadMoreRef"
        @click="loadMoreData"
      >
        <div v-if="!loading" class="load-more-content">
          <el-icon><ArrowDown /></el-icon>
          <span>加载更多角色</span>
        </div>
        <div v-else class="load-more-content loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>正在加载...</span>
        </div>
      </div>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { User, UserFilled, ArrowDown, Loading } from '@element-plus/icons-vue'
import { getRoleList } from '@/api/modules/system/admin/role'
import type { IRole } from '@/api/interface/system/admin/role'

interface Props {
  modelValue?: number | number[]
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  collapseTags?: boolean
  collapseTagsTooltip?: boolean
  frequentPermissionKeys?: string[]
  maxRoleCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  placeholder: '请选择角色',
  clearable: true,
  collapseTags: false,
  collapseTagsTooltip: true,
  frequentPermissionKeys: () => [],
  modelValue: undefined,
  maxRoleCount: 10
})

const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const roleOptions = ref<IRole.Info[]>([])
const frequentRoles = ref<IRole.Info[]>([])
const selectedRoles = ref<number | number[]>(props.modelValue ?? (props.multiple ? [] : 0))
const selectedRolesData = ref<IRole.Info[]>([])
const loadMoreRef = ref<HTMLElement | null>(null)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const hasMoreData = ref(false)
const searchQuery = ref('')

// 检查角色是否为常用角色
const isFrequentRole = (role: IRole.Info): boolean => {
  if (!role.permissions || !props.frequentPermissionKeys.length) return false

  const permissions = role.permissions.toLowerCase()
  return props.frequentPermissionKeys.some(key =>
    permissions.includes(key.toLowerCase())
  )
}

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== undefined) {
    selectedRoles.value = newVal
  } else {
    selectedRoles.value = props.multiple ? [] : 0
  }
}, { immediate: true })

// 远程搜索
const remoteSearch = async (query: string) => {
  searchQuery.value = query
  currentPage.value = 1
  loading.value = true
  try {
    const res = await getRoleList({
      roleName: query || undefined,
      page: currentPage.value,
      limit: pageSize.value
    })
    roleOptions.value = res.data.rows || []
    total.value = res.data.total || 0

    // 筛选常用角色
    frequentRoles.value = roleOptions.value.filter(role => isFrequentRole(role))

    // 检查是否有更多数据
    hasMoreData.value = roleOptions.value.length < total.value

    console.log(`加载第${currentPage.value}页, 共${total.value}条, 已加载${roleOptions.value.length}条, 常用角色${frequentRoles.value.length}个`)
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载更多数据
const loadMoreData = async () => {
  if (loading.value || !hasMoreData.value) return

  loading.value = true
  currentPage.value++

  try {
    console.log(`开始加载第${currentPage.value}页数据...`)
    const res = await getRoleList({
      roleName: searchQuery.value || undefined,
      page: currentPage.value,
      limit: pageSize.value
    })

    if (res.data.rows && res.data.rows.length > 0) {
      roleOptions.value = [...roleOptions.value, ...res.data.rows]
      // 更新常用角色
      frequentRoles.value = roleOptions.value.filter(role => isFrequentRole(role))
      console.log(`成功加载${res.data.rows.length}条新数据`)
    } else {
      console.log('未加载到新数据')
    }

    total.value = res.data.total || 0
    // 更新是否有更多数据标志
    hasMoreData.value = roleOptions.value.length < total.value

    console.log(`当前共加载${roleOptions.value.length}/${total.value}条数据, 是否有更多: ${hasMoreData.value}`)
  } catch (error) {
    console.error('加载更多角色失败:', error)
    currentPage.value--
  } finally {
    loading.value = false
  }
}

// 监听下拉框滚动事件
const setupScrollListener = () => {
  nextTick(() => {
    const dropdown = document.querySelector('.el-select-dropdown.apple-select-dropdown')

    if (!dropdown) {
      console.error('找不到下拉框元素')
      return
    }

    dropdown.addEventListener('scroll', (e: Event) => {
      const target = e.target as HTMLElement
      const { scrollTop, scrollHeight, clientHeight } = target

      if (scrollHeight - scrollTop - clientHeight < 30 && !loading.value && hasMoreData.value) {
        console.log('触发滚动加载')
        loadMoreData()
      }
    })

    console.log('已添加滚动事件监听')
  })
}

// 选择变化
const handleChange = (value: number | number[]) => {
  updateSelectedRolesData(value)
  emit('update:modelValue', value)
  emit('change', value)
}

// 更新已选中角色的完整数据
const updateSelectedRolesData = (selectedIds: number | number[]) => {
  if (!selectedIds) {
    selectedRolesData.value = []
    return
  }

  const ids = Array.isArray(selectedIds) ? selectedIds : [selectedIds]
  const allRoles = [...roleOptions.value, ...frequentRoles.value]

  selectedRolesData.value = ids.map(id => {
    const role = allRoles.find(r => r.id === id)
    return role || {
      id,
      roleName: `角色${id}`,
      remark: '',
      delFlag: '',
      createTime: '',
      updateTime: ''
    }
  }).filter(Boolean)
}

// 处理下拉框可见性变化
const handleDropdownVisibility = (visible: boolean) => {
  if (visible) {
    remoteSearch('')

    setTimeout(() => {
      setupScrollListener()
    }, 300)
  }
}

// 初始化加载
onMounted(async () => {
  loading.value = true
  try {
    await remoteSearch('')
  } catch (error) {
    console.error('初始化角色选择器失败:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.role-select {
  width: 100%;
  position: relative;

  :deep(.el-select) {
    width: 100%;

    .el-input__wrapper {
      border-radius: 12px;
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.08);
      transition: all 0.25s cubic-bezier(0.25, 0.1, 0.25, 1);
      padding: 0 16px;
      min-height: 44px;
      height: auto;
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);

      &:hover {
        box-shadow: 0 0 0 1px rgba(0, 122, 255, 0.4);
        background: rgba(255, 255, 255, 1);
      }

      &.is-focus {
        box-shadow: 0 0 0 2px rgba(0, 122, 255, 0.2), 0 0 0 1px #007aff;
        background: rgba(255, 255, 255, 1);
      }

      .el-input__inner {
        font-size: 16px;
        color: #1d1d1f;
        font-weight: 400;
        line-height: 1.5;
      }
    }

    .el-select__tags {
      max-width: calc(100% - 40px);
      margin-top: 6px;
      margin-bottom: 6px;
      flex-wrap: wrap;
      gap: 8px;
    }
  }

  .enhanced-role-select {
    :deep(.el-input__wrapper) {
      min-height: 44px;
      height: auto;
      padding: 10px 16px;

      .el-select__tags {
        margin-top: 0;
        margin-bottom: 0;
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        align-items: center;
      }
    }
  }

  // 自定义角色标签样式
  :deep(.el-tag.custom-role-tag) {
    margin: 0;
    border-radius: 20px;
    padding: 6px 16px;
    font-size: 12px;
    font-weight: 500;
    height: 32px;
    line-height: 20px;
    display: flex;
    align-items: center;
    gap: 6px;
    background: linear-gradient(135deg, rgba(0, 122, 255, 0.1) 0%, rgba(0, 122, 255, 0.05) 100%);
    border: 1px solid rgba(0, 122, 255, 0.2);
    color: #007aff;
    box-shadow: 0 2px 8px rgba(0, 122, 255, 0.1);
    transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 122, 255, 0.2);
      background: linear-gradient(135deg, rgba(0, 122, 255, 0.15) 0%, rgba(0, 122, 255, 0.08) 100%);
      border-color: rgba(0, 122, 255, 0.3);
    }

    .role-icon {
      font-size: 14px;
    }

    .el-icon {
      margin-left: 8px;
      font-size: 14px;
      transition: all 0.2s ease;
      opacity: 0.8;

      &:hover {
        transform: scale(1.2);
        opacity: 1;
      }
    }
  }

  .role-option {
    display: flex;
    flex-direction: column;
    width: 100%;
    padding: 8px 0;
    gap: 8px;

    .role-info {
      display: flex;
      flex-direction: column;
      gap: 8px;
      flex: 1;
      min-width: 0;

      .role-header {
        display: flex;
        align-items: center;
        gap: 8px;
        justify-content: space-between;

        .role-icon-option {
          color: #007aff;
          font-size: 16px;
          flex-shrink: 0;
        }

        .role-name {
          font-weight: 600;
          color: #1d1d1f;
          font-size: 15px;
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .frequent-tag {
          flex-shrink: 0;
          font-size: 11px;
          padding: 2px 8px;
          height: 20px;
          line-height: 16px;
          border-radius: 10px;
          background: linear-gradient(135deg, #34c759 0%, #30d158 100%);
          border: none;
          color: white;
          font-weight: 600;
          box-shadow: 0 1px 3px rgba(52, 199, 89, 0.3);
        }
      }

      .role-permissions-container {
        margin-left: 24px;

        .permissions-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;

          .permission-tag {
            font-size: 11px;
            padding: 2px 8px;
            height: 20px;
            line-height: 16px;
            border-radius: 10px;
            background: rgba(142, 142, 147, 0.1);
            border: 1px solid rgba(142, 142, 147, 0.2);
            color: #8e8e93;
            font-weight: 500;
            transition: all 0.2s ease;

            &:hover {
              background: rgba(0, 122, 255, 0.1);
              border-color: rgba(0, 122, 255, 0.3);
              color: #007aff;
              transform: translateY(-1px);
            }
          }
        }
      }
    }

    .role-id {
      position: absolute;
      top: 8px;
      right: 0;
      font-size: 10px;
      color: #c7c7cc;
      font-weight: 500;
      background: rgba(199, 199, 204, 0.1);
      padding: 2px 6px;
      border-radius: 6px;
      flex-shrink: 0;
    }
  }

  .empty-state {
    padding: 32px 0;

    .empty-icon {
      font-size: 48px;
      color: #c7c7cc;
      margin-bottom: 12px;
    }

    :deep(.el-empty) {
      .el-empty__description {
        color: #8e8e93;
        font-size: 15px;
        font-weight: 400;
      }
    }
  }

  .load-more-button {
    position: relative;
    margin: 24px auto 16px !important;
    width: 90% !important;
    height: 44px !important;
    border-radius: 22px !important;
    background: linear-gradient(135deg, #f2f2f7 0%, #e5e5ea 100%) !important;
    overflow: hidden !important;
    cursor: pointer !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04) !important;
    transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1) !important;
    display: block !important;
    border: 1px solid rgba(0, 0, 0, 0.06) !important;

    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, transparent 0%, rgba(255, 255, 255, 0.6) 50%, transparent 100%);
      transform: translateX(-100%);
      transition: transform 0.8s ease;
    }

    .load-more-content {
      position: relative;
      z-index: 2;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      height: 100%;
      color: #007aff;
      font-size: 15px;
      font-weight: 600;

      .el-icon {
        font-size: 16px;
        transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      }

      &.loading {
        color: #8e8e93;

        .el-icon {
          font-size: 18px;
          animation: rotating 1.2s linear infinite;
        }
      }
    }

    &:hover {
      background: linear-gradient(135deg, #e5e5ea 0%, #d1d1d6 100%) !important;
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
      border-color: rgba(0, 122, 255, 0.2) !important;

      &::before {
        transform: translateX(100%);
      }

      .load-more-content:not(.loading) .el-icon {
        transform: translateY(2px);
      }
    }

    &:active {
      transform: translateY(0) scale(0.98);
      background: linear-gradient(135deg, #d1d1d6 0%, #c7c7cc 100%) !important;
    }
  }
}

:deep(.apple-select-dropdown) {
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08), 0 8px 16px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  transform-origin: center top;
  border: 1px solid rgba(0, 0, 0, 0.06);
  padding: 8px 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);

  .el-select-dropdown__item {
    height: auto;
    min-height: 56px;
    line-height: 1.4;
    padding: 12px 20px;
    font-size: 15px;
    position: relative;
    transition: all 0.2s cubic-bezier(0.25, 0.1, 0.25, 1);

    &.hover, &:hover {
      background: rgba(0, 122, 255, 0.06);
      backdrop-filter: blur(10px);
    }

    &.selected {
      background: rgba(0, 122, 255, 0.1);
      color: #007aff;
      font-weight: 600;

      &::after {
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 3px;
        background: linear-gradient(135deg, #007aff 0%, #0051d5 100%);
        border-radius: 0 2px 2px 0;
      }
    }
  }

  .el-select-group__title {
    padding: 16px 20px 8px;
    font-weight: 700;
    font-size: 13px;
    color: #8e8e93;
    background: rgba(0, 0, 0, 0.02);
    margin-top: 0;
    letter-spacing: 0.5px;
    text-transform: uppercase;
    border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  }

  .el-select-group__wrap {
    margin-bottom: 12px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>

<style lang="scss">
/* 全局样式，没有scoped，确保可以影响到下拉框 */
.custom-role-select-dropdown {
  .load-more-button {
    position: relative;
    margin: 24px auto 16px !important;
    width: 90% !important;
    height: 44px !important;
    border-radius: 22px !important;
    background: linear-gradient(135deg, #f2f2f7 0%, #e5e5ea 100%) !important;
    overflow: hidden !important;
    cursor: pointer !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04) !important;
    transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1) !important;
    display: block !important;
    border: 1px solid rgba(0, 0, 0, 0.06) !important;

    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, transparent 0%, rgba(255, 255, 255, 0.6) 50%, transparent 100%);
      transform: translateX(-100%);
      transition: transform 0.8s ease;
    }

    .load-more-content {
      position: relative;
      z-index: 2;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      height: 100%;
      color: #007aff;
      font-size: 15px;
      font-weight: 600;

      .el-icon {
        font-size: 16px;
        transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      }

      &.loading {
        color: #8e8e93;

        .el-icon {
          font-size: 18px;
          animation: rotating 1.2s linear infinite;
        }
      }
    }

    &:hover {
      background: linear-gradient(135deg, #e5e5ea 0%, #d1d1d6 100%) !important;
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
      border-color: rgba(0, 122, 255, 0.2) !important;

      &::before {
        transform: translateX(100%);
      }

      .load-more-content:not(.loading) .el-icon {
        transform: translateY(2px);
      }
    }

    &:active {
      transform: translateY(0) scale(0.98);
      background: linear-gradient(135deg, #d1d1d6 0%, #c7c7cc 100%) !important;
    }
  }

  @keyframes rotating {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
}
</style>
