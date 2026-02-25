<template>
  <div class="code-monitor-component">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form
        :inline="true"
        :model="searchForm"
        class="search-form"
      >
        <el-form-item label="代码模式">
          <enum-select
            v-model="searchForm.codeMode"
            :enum-data="CodeMode"
            placeholder="请选择代码模式"
            clearable
          />
        </el-form-item>
        <el-form-item label="监控状态">
          <enum-select
            v-model="searchForm.monitorStatus"
            :enum-data="CodeMonitorStatus"
            placeholder="请选择监控状态"
            clearable
          />
        </el-form-item>
        <el-form-item label="刷新间隔">
          <el-select
            v-model="currentRefreshInterval"
            @change="handleRefreshIntervalChange"
            placeholder="选择刷新间隔"
            style="width: 140px"
          >
            <el-option
              v-for="option in refreshIntervalOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleSearch"
            class="search-button"
          >
            搜索
          </el-button>
          <el-button 
            @click="resetSearch"
            class="reset-button"
          >
            重置
          </el-button>
          <el-button
            @click="handleManualRefresh"
            :loading="isRefreshing"
            class="refresh-button"
          >
            <el-icon v-if="!isRefreshing" class="refresh-icon">
              <Refresh />
            </el-icon>
            {{ isRefreshing ? '刷新中...' : '立即刷新' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 刷新状态指示器 -->
      <div class="refresh-status" v-if="currentRefreshInterval > 0">
        <div class="status-indicator">
          <el-icon 
            class="status-icon"
            :class="{ 'refreshing': isRefreshing }"
          >
            <CircleCheckFilled v-if="!isRefreshing" />
            <Loading v-else />
          </el-icon>
          <span class="status-text">
            {{ isRefreshing ? '正在刷新数据...' : `每 ${formatRefreshInterval(currentRefreshInterval)} 自动刷新` }}
          </span>
          <div v-if="!isRefreshing && currentRefreshInterval > 0" class="countdown-bar">
            <div 
              class="countdown-progress"
              :style="{ 
                width: `${(countdownProgress * 100)}%`,
                animationDuration: `${currentRefreshInterval}ms`
              }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <el-empty
      v-if="filteredList.length === 0"
      :image-size="200"
      description="暂无代码监控记录"
    >
      <template #description>
        <div class="empty-description">
          <p>当前班级暂无代码监控记录</p>
          <p class="empty-tip">
            请确保班级成员已提交代码
          </p>
        </div>
      </template>
    </el-empty>
    
    <div v-else class="monitor-table-wrapper">
      <el-table
        :data="filteredList"
        border
        class="monitor-table"
      >
        <el-table-column
          prop="id"
          label="ID"
          width="80"
          align="center"
        />
        <el-table-column
          prop="monitorName"
          label="被监控人"
          width="120"
          align="center"

        />
        <el-table-column
          prop="monitorPhone"
          label="联系电话"
          width="120"
          align="center"

        />
        <el-table-column
          prop="overlayName"
          label="覆盖人"
          width="120"
          align="center"

        />
        <el-table-column
          label="代码"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <code-view-button
              :code="row.monitorCode"
              :language="row.language"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="代码模式"
          width="120"
          align="center"

        >
          <template #default="{ row }">
            <enum-show
              :enum="CodeMode"
              :code="row.codeMode"
              class="code-mode-tag"
              background-color="rgba(var(--el-color-primary-rgb), 0.1)"
              color="var(--el-color-primary)"
              border-color="var(--el-color-primary-light-5)"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="监控状态"
          width="120"
          align="center"

        >
          <template #default="{ row }">
            <enum-show
              :enum="CodeMonitorStatus"
              :code="row.monitorStatus"
              class="monitor-status-tag"
              background-color="rgba(var(--el-color-success-rgb), 0.1)"
              color="var(--el-color-success)"
              border-color="var(--el-color-success-light-5)"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="version"
          label="版本"
          width="80"
          align="center"

        />
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          align="center"

        >
          <template #default="{ row }">
            <span>{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="updateTime"
          label="更新时间"
          width="180"
          align="center"

        >
          <template #default="{ row }">
            <span>{{ formatTime(row.updateTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          fixed="right"
          align="center"
          width="140"
        >
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleViewDetail(row)"
              class="action-button"
            >
              <el-icon><Upload /></el-icon>推送代码
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { Upload, Refresh, CircleCheckFilled, Loading } from '@element-plus/icons-vue';
import { getCodeMonitorListByGroupApi } from '@/api/modules/oj/code/codeMonitor';
import { CodeMode, CodeMonitorStatus } from '@/enums/oj/code';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import CodeViewButton from '@/components/Oj/Judge/common/CodeViewButton.vue';

const props = defineProps({
  groupId: {
    type: Number,
    required: false,
    default: undefined
  },
  refreshInterval: {
    type: Number,
    default: 30000 // 默认30秒刷新一次
  }
});

const emit = defineEmits(['view-detail']);

// 代码监控列表数据
const monitorList = ref<any[]>([]);
let refreshTimer: number | null = null;

// 搜索表单
const searchForm = ref({
  codeMode: undefined as string | undefined,
  monitorStatus: undefined as string | undefined,
});

// 刷新相关状态
const isRefreshing = ref(false);
const currentRefreshInterval = ref(props.refreshInterval);
const countdownProgress = ref(0);
let countdownTimer: number | null = null;

// 刷新间隔选项
const refreshIntervalOptions = [
  { label: '关闭自动刷新', value: 0 },
  { label: '每 1 秒', value: 1000 },
  { label: '每 2 秒', value: 2000 },
  { label: '每 3 秒', value: 3000 },
  { label: '每 5 秒', value: 5000 },
  { label: '每 10 秒', value: 10000 },
  { label: '每 30 秒', value: 30000 },
  { label: '每 1 分钟', value: 60000 },
];

// 获取代码监控列表
const getCodeMonitorList = async () => {
  if (!props.groupId) return;
  
  isRefreshing.value = true;
  try {
    const res = await getCodeMonitorListByGroupApi({ groupId: props.groupId });
    if (res.data) {
      monitorList.value = res.data;
    }
  } catch (error) {
    console.error('获取代码监控列表失败:', error);
  } finally {
    isRefreshing.value = false;
  }
};

// 过滤后的代码监控列表
const filteredList = computed(() => {
  return monitorList.value.filter((item) => {
    const codeModeMatch =
      !searchForm.value.codeMode ||
      item.codeMode === searchForm.value.codeMode;
    const statusMatch =
      !searchForm.value.monitorStatus ||
      item.monitorStatus === searchForm.value.monitorStatus;

    return codeModeMatch && statusMatch;
  });
});

// 搜索处理函数
const handleSearch = () => {
  // 搜索逻辑已经在 computed 中实现
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    codeMode: undefined,
    monitorStatus: undefined,
  };
};

// 处理查看详情
const handleViewDetail = (row: any) => {
  emit('view-detail', row);
};

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-';
  const date = new Date(time);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  });
};

// 格式化刷新间隔显示
const formatRefreshInterval = (interval: number) => {
  if (interval < 1000) return `${interval}ms`;
  if (interval < 60000) return `${interval / 1000}秒`;
  return `${interval / 60000}分钟`;
};

// 手动刷新
const handleManualRefresh = () => {
  getCodeMonitorList();
};

// 处理刷新间隔变化
const handleRefreshIntervalChange = (newInterval: number) => {
  currentRefreshInterval.value = newInterval;
  startRefreshTimer();
};

// 启动倒计时进度条
const startCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
  
  if (currentRefreshInterval.value <= 0) return;
  
  countdownProgress.value = 0;
  const step = 100; // 更新频率：每100ms更新一次
  const totalSteps = currentRefreshInterval.value / step;
  let currentStep = 0;
  
  countdownTimer = window.setInterval(() => {
    currentStep++;
    countdownProgress.value = currentStep / totalSteps;
    
    if (currentStep >= totalSteps) {
      clearInterval(countdownTimer!);
      countdownProgress.value = 0;
    }
  }, step);
};

// 启动定时刷新
const startRefreshTimer = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
  
  // 初始加载数据
  getCodeMonitorList();
  
  // 设置定时刷新
  if (currentRefreshInterval.value > 0) {
    refreshTimer = window.setInterval(() => {
      getCodeMonitorList();
      startCountdown();
    }, currentRefreshInterval.value);
    
    // 启动倒计时
    startCountdown();
  }
};

// 停止定时刷新
const stopRefreshTimer = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
};

// 监听 groupId 变化
watch(() => props.groupId, (newVal) => {
  if (newVal) {
    startRefreshTimer();
  } else {
    stopRefreshTimer();
  }
});

// 组件挂载时初始化
onMounted(() => {
  if (props.groupId) {
    startRefreshTimer();
  }
});

// 组件卸载时清理
onUnmounted(() => {
  stopRefreshTimer();
});
</script>

<style scoped lang="scss">
.code-monitor-component {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif;
  
  .search-area {
    margin-bottom: 20px;
    padding: 18px;
    background-color: rgba(var(--el-color-primary-rgb), 0.03);
    border-radius: 12px;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(var(--el-color-primary-rgb), 0.05);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
    }

    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      align-items: center;

      :deep(.el-form-item) {
        margin-bottom: 0;
        
        .el-form-item__label {
          font-weight: 500;
          font-size: 14px;
          color: #1d1d1f;
        }
        
        .el-input__wrapper {
          border-radius: 8px;
          box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
          
          &:hover {
            box-shadow: 0 0 0 1px var(--el-color-primary-light-5);
          }
          
          &.is-focus {
            box-shadow: 0 0 0 1px var(--el-color-primary);
          }
        }
        
        .el-select {
          width: 160px;
        }
      }

      .search-button {
        background: var(--el-color-primary);
        border: none;
        border-radius: 8px;
        padding: 8px 16px;
        font-weight: 500;
        box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.2);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 10px rgba(var(--el-color-primary-rgb), 0.3);
        }
      }
      
      .reset-button {
        border-radius: 8px;
        border: 1px solid rgba(0, 0, 0, 0.1);
        padding: 8px 16px;
        font-weight: 500;
        transition: all 0.3s ease;
        
        &:hover {
          border-color: var(--el-color-primary-light-5);
          color: var(--el-color-primary);
          transform: translateY(-2px);
        }
      }
      
      .refresh-button {
        border-radius: 8px;
        border: 1px solid var(--el-color-success);
        padding: 8px 16px;
        font-weight: 500;
        color: var(--el-color-success);
        background-color: rgba(var(--el-color-success-rgb), 0.1);
        transition: all 0.3s ease;
        
        .refresh-icon {
          margin-right: 4px;
          animation: refreshSpin 1s linear infinite paused;
        }
        
        &:hover {
          background-color: var(--el-color-success);
          color: white;
          transform: translateY(-2px);
          box-shadow: 0 4px 8px rgba(var(--el-color-success-rgb), 0.3);
          
          .refresh-icon {
            animation-play-state: running;
          }
        }
        
        &.is-loading {
          .refresh-icon {
            animation-play-state: running;
          }
        }
      }
    }
    
    .refresh-status {
      margin-top: 12px;
      padding: 8px 12px;
      background-color: rgba(var(--el-color-success-rgb), 0.05);
      border-radius: 8px;
      border: 1px solid rgba(var(--el-color-success-rgb), 0.1);
      
      .status-indicator {
        display: flex;
        align-items: center;
        position: relative;
        
        .status-icon {
          margin-right: 8px;
          font-size: 16px;
          
          &.refreshing {
            color: var(--el-color-primary);
            animation: refreshSpin 1s linear infinite;
          }
          
          &:not(.refreshing) {
            color: var(--el-color-success);
          }
        }
        
        .status-text {
          font-size: 13px;
          color: #606266;
          font-weight: 500;
        }
        
        .countdown-bar {
          position: absolute;
          bottom: -4px;
          left: 0;
          right: 0;
          height: 2px;
          background-color: rgba(var(--el-color-success-rgb), 0.1);
          border-radius: 1px;
          overflow: hidden;
          
          .countdown-progress {
            height: 100%;
            background: linear-gradient(
              90deg,
              var(--el-color-success),
              var(--el-color-success-light-3)
            );
            border-radius: 1px;
            transition: width 0.1s linear;
            transform-origin: left;
            animation: countdownProgress linear;
          }
        }
      }
    }
  }

  .monitor-table-wrapper {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    
    .monitor-table {
      width: 100%;
      
      :deep(.el-table__header) {
        th {
          background: linear-gradient(
            to right,
            rgba(var(--el-color-primary-rgb), 0.05),
            rgba(var(--el-color-primary-rgb), 0.02)
          );
          font-weight: 600;
          color: #1d1d1f;
          padding: 12px 0;
          font-size: 14px;
        }
      }
      
      :deep(.el-table__row) {
        transition: all 0.2s ease;
        
        &:hover {
          background-color: rgba(var(--el-color-primary-rgb), 0.03);
        }
        
        td {
          padding: 10px 0;
          font-size: 14px;
        }
      }
    }
  }

  .code-view-btn {
    padding: 4px 12px;
    border-radius: 6px;
    font-size: 13px;
    background-color: var(--el-color-primary);
    color: white;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    cursor: pointer;
    font-weight: 500;
    
    &:hover {
      background-color: var(--el-color-primary-light-3);
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }
  }

  .code-mode-tag,
  .monitor-status-tag {
    padding: 4px 8px;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 500;
  }

  .action-button {
    color: var(--el-color-primary);
    font-weight: 500;
    font-size: 13px;
    transition: all 0.2s ease;
    
    .el-icon {
      margin-right: 4px;
    }
    
    &:hover {
      color: var(--el-color-primary-light-3);
      transform: translateY(-1px);
    }
  }

  .empty-description {
    text-align: center;

    p {
      margin: 8px 0;
      color: #606266;
    }

    .empty-tip {
      font-size: 14px;
      color: #909399;
    }
  }
}

// 动画定义
@keyframes refreshSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes countdownProgress {
  from {
    width: 0%;
  }
  to {
    width: 100%;
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .code-monitor-component {
    .search-area {
      background-color: rgba(255, 255, 255, 0.05);
      border-color: rgba(255, 255, 255, 0.05);
      
      .search-form {
        :deep(.el-form-item) {
          .el-form-item__label {
            color: #f5f5f7;
          }
          
          .el-input__wrapper {
            box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1);
            background-color: rgba(255, 255, 255, 0.05);
            
            .el-input__inner {
              color: #f5f5f7;
            }
          }
        }
        
        .reset-button {
          border-color: rgba(255, 255, 255, 0.1);
          color: #f5f5f7;
        }
      }
    }
    
    .monitor-table-wrapper {
      .monitor-table {
        background-color: transparent;
        
        :deep(.el-table__header) {
          th {
            background: linear-gradient(
              to right,
              rgba(var(--el-color-primary-rgb), 0.15),
              rgba(var(--el-color-primary-rgb), 0.1)
            );
            color: #f5f5f7;
            border-color: rgba(255, 255, 255, 0.05);
          }
        }
        
        :deep(.el-table__row) {
          background-color: rgba(255, 255, 255, 0.02);
          
          &:hover {
            background-color: rgba(255, 255, 255, 0.05);
          }
          
          td {
            color: #f5f5f7;
            border-color: rgba(255, 255, 255, 0.05);
          }
          
          &.el-table__row--striped {
            background-color: rgba(255, 255, 255, 0.01);
          }
        }
      }
    }
    
    .empty-description {
      p {
        color: #a1a1a6;
      }
      
      .empty-tip {
        color: #86868b;
      }
    }
    
    .refresh-status {
      background-color: rgba(255, 255, 255, 0.05);
      border-color: rgba(255, 255, 255, 0.1);
      
      .status-indicator {
        .status-text {
          color: #a1a1a6;
        }
        
        .countdown-bar {
          background-color: rgba(255, 255, 255, 0.1);
        }
      }
    }
  }
}
</style> 