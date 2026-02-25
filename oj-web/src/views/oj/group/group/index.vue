<template>
  <div class="group-view">
    <!-- 操作按钮区域 - 使用更简洁的布局 -->
    <div class="action-bar">
      <div class="primary-actions">
        <el-button
          v-show="isAdminOrCommon"
          type="primary"
          v-auth="'group.create'"
          class="action-button create-button"
          @click="openAddEdit('新增班级')"
        >
          <el-icon><CirclePlus /></el-icon>
          <span>新增班级</span>
        </el-button>

        <el-button
          v-show="isAdminOrCommon"
          v-auth="'group.remove'"
          type="danger"
          class="action-button delete-button"
          :disabled="!selectedIds.length"
          @click="batchDelete(selectedIds)"
        >
          <el-icon><Delete /></el-icon>
          <span>批量删除</span>
        </el-button>

        <el-button
          type="default"
          class="action-button select-button"
          :class="{ 'is-selected': checkAll }"
          @click="handleCheckAllChange(!checkAll)"
        >
          <el-icon><Select /></el-icon>
          <span>{{ checkAll ? '取消全选' : '全选' }}</span>
        </el-button>

        <div class="search-toggle" @click="toggleSearch">
          <el-button type="default" class="toggle-button" :class="{ 'is-active': !isSearchCollapsed }">
            <el-icon><Search /></el-icon>
            <span>{{ isSearchCollapsed ? '展开筛选' : '收起筛选' }}</span>
          </el-button>
        </div>
      </div>

      <div class="secondary-actions">
        <el-tooltip content="导入班级数据" placement="top" :show-after="300">
          <el-button
            v-auth="'group.import'"
            type="default"
            class="action-button icon-button import-button"
            @click="importData"
          >
            <el-icon><Upload /></el-icon>
          </el-button>
        </el-tooltip>

        <el-tooltip content="导出班级数据" placement="top" :show-after="300">
          <el-button
            v-auth="'group.export'"
            type="default"
            class="action-button icon-button export-button"
            @click="downloadFile"
          >
            <el-icon><Download /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </div>

    <!-- 搜索区域 - 改进动画效果 -->
    <div class="search-area" :class="{ 'is-collapsed': isSearchCollapsed }">
      <div class="search-header" @click="toggleSearch">
        <div class="search-title">
          <el-icon class="search-icon"><Search /></el-icon>
          <span>筛选班级</span>
        </div>
        <el-icon :class="{ 'is-rotate': !isSearchCollapsed }"><ArrowDown /></el-icon>
      </div>

      <div class="search-content" v-show="!isSearchCollapsed">
        <el-form :model="searchForm" @keyup.enter="searchGroups">
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item>
                <template #label>
                  <div class="form-label">
                    <el-icon><School /></el-icon>
                    <span>班级名</span>
                  </div>
                </template>
                <el-input
                  v-model="searchForm.name"
                  placeholder="请输入班级名"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item>
                <template #label>
                  <div class="form-label">
                    <el-icon><Document /></el-icon>
                    <span>班级代码</span>
                  </div>
                </template>
                <el-input
                  v-model="searchForm.code"
                  placeholder="请输入班级代码"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item>
                <template #label>
                  <div class="form-label">
                    <el-icon><User /></el-icon>
                    <span>负责人</span>
                  </div>
                </template>
                <user-select
                  v-model="searchForm.principalUserId"
                  placeholder="请选择负责人"
                  clearable
                  ref="principalUserSelect"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <div class="form-actions">
            <el-button type="primary" class="search-button" @click="searchGroups">
              <el-icon><Search /></el-icon>
              <span>搜索</span>
            </el-button>
            <el-button type="default" class="reset-button" @click="resetSearch">
              <el-icon><RefreshRight /></el-icon>
              <span>重置</span>
            </el-button>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 群组数量信息 -->
    <div class="group-info" v-if="total > 0">
      <span>共 <em>{{ total }}</em> 个班级</span>
      <span v-if="selectedIds.length">已选择 <em>{{ selectedIds.length }}</em> 项</span>
    </div>

    <!-- 群组卡片区域 - 增加卡片阴影和过渡效果 -->
    <div class="group-grid">
      <div v-if="groupList.length === 0" class="empty-data">
        <el-empty description="暂无班级数据">
          <template #image>
            <div class="empty-icon">
              <el-icon><School /></el-icon>
            </div>
          </template>
        </el-empty>
      </div>

      <el-row :gutter="24" v-else>
        <el-col
          v-for="item in groupList"
          :key="item.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          :xl="4"
        >
          <group-card
            :group="item"
            :is-selected="item.id ? selectedIds.includes(item.id) : false"
            @select="toggleGroupSelection"
            @edit="openAddEdit('编辑班级', item, false)"
            @delete="deleteInfo"
            @detail="goToDetail(item.id)"
          />
        </el-col>
      </el-row>
    </div>

    <!-- 分页控件 - 添加Apple风格 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 16, 24, 32]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <GroupForm ref="groupRef" />
    <ImportExcel ref="ImportExcelRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useRole } from '@/hooks/useRole'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  Document,
  User,
  Switch,
  School,
  InfoFilled,
  Select,
  Search,
  RefreshRight,
  ArrowDown,
  ArrowUp
} from '@element-plus/icons-vue'
import {
  createGroupApi,
  removeGroupApi,
  updateGroupApi,
  getGroupListApi,
  getGroupDetailApi,
  importGroupExcelApi,
  exportGroupExcelApi,
} from '@/api/modules/oj/group/group';
import { useHandleData } from '@/hooks/useHandleData';
import GroupForm from '@/views/oj/group/group/components/GroupForm.vue';
import GroupCard from '@/views/oj/group/group/components/GroupCard.vue';
import type { IGroup } from '@/api/interface/oj/group/group';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { useDownload } from "@/hooks/useDownload";
import UserSelect from '@/components/Common/User/UserSelect.vue';
import { useAppStore } from '@/stores/modules/app';
import { storeToRefs } from 'pinia';
import { getLightColor, getDarkColor } from '@/utils/color';

defineOptions({
  name: 'GroupView'
})

const { isAdminOrCommon } = useRole()

// 获取应用主题色
const appStore = useAppStore();
const { primary: themePrimary } = storeToRefs(appStore);

// 计算衍生颜色
const primaryLighter = computed(() => getLightColor(themePrimary.value, 0.1));
const primaryDarker = computed(() => getDarkColor(themePrimary.value, 0.1));
const primaryLightColor = computed(() => getLightColor(themePrimary.value, 0.9));
const primaryBorderColor = computed(() => getLightColor(themePrimary.value, 0.8));
const boxShadowColor = computed(() => `rgba(${parseInt(themePrimary.value.slice(1, 3), 16)}, ${parseInt(themePrimary.value.slice(3, 5), 16)}, ${parseInt(themePrimary.value.slice(5, 7), 16)}, 0.15)`);

// 路由
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  principalUserId: undefined
})

// 搜索区域折叠状态
const isSearchCollapsed = ref(true)
const principalUserSelect = ref()

// 切换搜索区域折叠状态
const toggleSearch = () => {
  isSearchCollapsed.value = !isSearchCollapsed.value
}

// 分页相关
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const selectedIds = ref<number[]>([])
const checkAll = ref(false)
const isIndeterminate = ref(false)

// 班级列表
const groupList = ref<IGroup.Row[]>([])

// 获取班级列表
const getGroups = async () => {
  const params = {
    current: currentPage.value,
    size: pageSize.value,
    ...searchForm
  }
  try {
    const res = await getGroupListApi(formatParams(params))
    groupList.value = res.data?.rows || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取班级列表失败:', error)
  }
}

// 搜索
const searchGroups = () => {
  currentPage.value = 1
  getGroups()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.code = ''
  searchForm.principalUserId = undefined

  if (principalUserSelect.value) {
    principalUserSelect.value.handleClear()
  }

  searchGroups()
}

// 格式化查询参数
const formatParams = (params: any) => {
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}

// 分页相关
const handleSizeChange = (val: number) => {
  pageSize.value = val
  getGroups()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getGroups()
}

// 跳转到班级详情
const goToDetail = (id: number | undefined) => {
  if (id) {
    router.push(`/oj/group/detail/${id}`)
  }
}

// 打开 drawer(新增、查看、编辑)
const groupRef = ref<InstanceType<typeof GroupForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getGroupDetailApi({ id: row?.id })
    row = record?.data
  } else {
    row.color = '#0071e3'  // Apple 蓝色
    row.textColor = '#FFFFFF'  // 白色文字
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createGroupApi : updateGroupApi,
    getTableList: getGroups
  }
  groupRef.value?.acceptParams(params)
}

// 删除信息
const deleteInfo = async (params: IGroup.Row) => {
  await useHandleData(
    removeGroupApi,
    { ids: [params.id] },
    `删除【${params.name}】班级`
  )
  getGroups()
}

// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeGroupApi, { ids }, '删除所选班级')
  selectedIds.value = []
  getGroups()
}

// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '班级表',
    templateName: '班级表',
    tempApi: downloadTemplate,
    importApi: importGroupExcelApi,
    getTableList: getGroups
  }
  ImportExcelRef.value?.acceptParams(params)
}

// 导出
const downloadFile = async () => {
  let newParams = formatParams({...searchForm});
  useDownload(exportGroupExcelApi, "班级表", newParams);
};

// 全选处理
const handleCheckAllChange = (val: boolean) => {
  if (val) {
    selectedIds.value = groupList.value.map(item => item.id!).filter(id => id !== undefined) as number[]
  } else {
    selectedIds.value = []
  }
  checkAll.value = val
  isIndeterminate.value = false
}

// 处理群组选择
const toggleGroupSelection = (group: IGroup.Row) => {
  // 确保id存在
  if (typeof group.id === 'undefined') return;

  const index = selectedIds.value.indexOf(group.id)
  if (index === -1) {
    selectedIds.value.push(group.id)
  } else {
    selectedIds.value.splice(index, 1)
  }

  // 更新全选状态
  const checkedCount = selectedIds.value.length
  const totalCount = groupList.value.length
  checkAll.value = checkedCount === totalCount && totalCount > 0
  isIndeterminate.value = checkedCount > 0 && checkedCount < totalCount
}

// 处理部分选择
const handleSelectionChange = (value: number[]) => {
  const checkedCount = value.length
  const totalCount = groupList.value.length

  checkAll.value = checkedCount === totalCount && totalCount > 0
  isIndeterminate.value = checkedCount > 0 && checkedCount < totalCount
}

// 初始加载
onMounted(() => {
  getGroups()
})
</script>

<style scoped lang="scss">
.group-view {
  // 全局主题颜色变量（使用动态主题色）
  --theme-color: v-bind('themePrimary');
  --theme-hover-color: v-bind('primaryDarker');
  --theme-light-color: v-bind('primaryLightColor');
  --theme-border-color: v-bind('primaryBorderColor');
  --apple-background: #f5f7fa;
  --apple-card-bg: #ffffff;
  --apple-text-primary: #1d1d1f;
  --apple-text-secondary: #6e6e73;
  --apple-text-tertiary: #86868b;
  --apple-border-color: #d2d2d7;
  --apple-highlight-bg: #f5f5f7;

  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: var(--apple-background);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;

  // ACTION BAR
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .primary-actions, .secondary-actions {
      display: flex;
      gap: 10px;

      .action-button {
        height: 36px;
        border-radius: 18px;
        padding: 0 16px;
        font-size: 14px;
        font-weight: 500;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

        .el-icon {
          margin-right: 6px;
          font-size: 16px;
        }

        &.icon-button {
          width: 36px;
          padding: 0;

          .el-icon {
            margin-right: 0;
          }
        }

        &.create-button {
          background-color: var(--theme-color);
          border-color: var(--theme-color);
          color: #fff;

          &:hover {
            background-color: var(--theme-hover-color);
            transform: translateY(-2px);
            box-shadow: 0 2px 8px v-bind('boxShadowColor');
          }
        }

        &.delete-button:not([disabled]) {
          background-color: #F56C6C;
          border-color: #F56C6C;
          color: #fff;

          &:hover {
            background-color: #ff7875;
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
          }
        }

        &.select-button {
          border-color: var(--apple-border-color);
          color: var(--apple-text-primary);

          &:hover, &.is-selected {
            background-color: var(--apple-highlight-bg);
            transform: translateY(-2px);
          }

          &.is-selected {
            border-color: var(--theme-color);
            color: var(--theme-color);
          }
        }

        &.import-button, &.export-button {
          border-color: var(--apple-border-color);
          color: var(--apple-text-primary);

          &:hover {
            background-color: var(--apple-highlight-bg);
            transform: translateY(-2px);
            border-color: var(--theme-color);
            color: var(--theme-color);
          }
        }
      }

      .search-toggle {
        .toggle-button {
          height: 36px;
          border-radius: 18px;
          padding: 0 16px;
          min-width: 115px;
          font-size: 14px;
          font-weight: 500;
          display: flex;
          align-items: center;
          justify-content: center;
          border-color: var(--apple-border-color);
          color: var(--apple-text-primary);
          transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
          white-space: nowrap;

          .el-icon {
            margin-right: 6px;
            font-size: 16px;
          }

          &:hover, &.is-active {
            background-color: var(--apple-highlight-bg);
            transform: translateY(-2px);
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
          }

          &.is-active {
            border-color: var(--theme-color);
            color: var(--theme-color);
          }
        }
      }
    }
  }

  // SEARCH AREA
  .search-area {
    background-color: var(--apple-card-bg);
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    margin-bottom: 24px;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transform-origin: top;
    border: 1px solid rgba(0, 0, 0, 0.05);

    &.is-collapsed {
      .search-content {
        max-height: 0;
        padding: 0;
        opacity: 0;
      }
    }

    .search-header {
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      cursor: pointer;
      transition: background-color 0.2s ease;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);

      &:hover {
        background-color: rgba(0, 0, 0, 0.02);
      }

      .search-title {
        display: flex;
        align-items: center;
        gap: 10px;
        color: var(--apple-text-primary);
        font-size: 15px;
        font-weight: 500;

        .search-icon {
          font-size: 16px;
          color: var(--theme-color);
        }
      }

      .el-icon {
        font-size: 14px;
        transition: transform 0.3s ease;
        color: var(--apple-text-tertiary);

        &.is-rotate {
          transform: rotate(180deg);
        }
      }
    }

    .search-content {
      padding: 20px;
      max-height: 1000px;
      opacity: 1;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

      .form-label {
        display: flex;
        align-items: center;
        gap: 6px;
        color: var(--apple-text-secondary);
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 8px;

        .el-icon {
          font-size: 16px;
          color: var(--theme-color);
        }
      }

      .el-input, :deep(.el-select) {
        .el-input__wrapper {
          border-radius: 10px;
          padding: 0 12px;
          height: 40px;
          box-shadow: 0 0 0 1px var(--apple-border-color) inset;

          &:hover {
            box-shadow: 0 0 0 1px var(--apple-text-tertiary) inset;
          }

          &.is-focus {
            box-shadow: 0 0 0 2px var(--theme-color) inset !important;
          }
        }
      }

      .form-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        margin-top: 24px;

        .search-button, .reset-button {
          height: 38px;
          border-radius: 19px;
          padding: 0 20px;
          font-size: 14px;
          font-weight: 500;

          .el-icon {
            margin-right: 6px;
          }
        }

        .search-button {
          background-color: var(--theme-color);
          border-color: var(--theme-color);

          &:hover {
            background-color: var(--theme-hover-color);
            transform: translateY(-2px);
            box-shadow: 0 2px 8px v-bind('boxShadowColor');
          }
        }

        .reset-button {
          border-color: var(--apple-border-color);
          color: var(--apple-text-primary);

          &:hover {
            background-color: var(--apple-highlight-bg);
            transform: translateY(-2px);
          }
        }
      }
    }
  }

  // GROUP INFO
  .group-info {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;
    color: var(--apple-text-secondary);
    font-size: 14px;
    letter-spacing: 0.3px;

    em {
      font-style: normal;
      font-weight: 600;
      color: var(--apple-text-primary);
    }
  }

  // GROUP GRID
  .group-grid {
    margin-bottom: 30px;
    min-height: 200px;

    .empty-data {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 300px;

      .empty-icon {
        width: 80px;
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--apple-highlight-bg);
        border-radius: 50%;

        .el-icon {
          font-size: 36px;
          color: var(--apple-text-tertiary);
        }
      }
    }

    .el-col {
      margin-bottom: 24px;
    }
  }

  // PAGINATION
  .pagination-container {
    display: flex;
    justify-content: center;
    padding: 20px 0;

    :deep(.el-pagination) {
      .el-pagination__jump {
        margin-left: 16px;
      }

      .el-pagination__sizes {
        margin-right: 16px;
      }

      .el-input__wrapper {
        border-radius: 10px;
      }

      .el-pager li {
        border-radius: 50%;
        font-weight: 500;
        transition: all 0.2s ease;

        &.is-active {
          background-color: var(--theme-color) !important;
          color: #fff !important;
        }

        &:hover:not(.is-active) {
          color: var(--theme-color);
        }
      }

      .btn-prev, .btn-next {
        border-radius: 50%;

        &:hover {
          color: var(--theme-color);
        }
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .group-view {
    --apple-background: #1a1a1a;
    --apple-card-bg: #2c2c2c;
    --apple-text-primary: #f5f5f7;
    --apple-text-secondary: #ababab;
    --apple-text-tertiary: #8e8e93;
    --apple-border-color: #3a3a3c;
    --apple-highlight-bg: #3a3a3c;

    .search-area {
      border-color: rgba(255, 255, 255, 0.08);
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);

      .search-header {
        border-color: rgba(255, 255, 255, 0.08);

        &:hover {
          background-color: rgba(255, 255, 255, 0.05);
        }
      }
    }
  }
}
</style>
