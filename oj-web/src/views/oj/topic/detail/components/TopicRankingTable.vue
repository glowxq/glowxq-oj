<template>
  <div class="ranking-section">
    <div class="section-header">
      <div class="title">
        <el-icon><TrophyBase /></el-icon>
        <span>{{ title }}</span>
      </div>
      <div class="actions">
        <div class="judge-type-selector">
          <span class="selector-label">判题类型:</span>
          <el-radio-group v-model="selectedJudgeType" size="small" @change="handleJudgeTypeChange">
            <el-radio
              v-for="item in Object.values(TopicJudgeType)"
              :key="item.code"
              :label="item.code"
              class="judge-type-radio"
            >
              <enum-show
                :enum="TopicJudgeType"
                :code="item.code"
                class="judge-type-badge"
              />
            </el-radio>
          </el-radio-group>
        </div>
        <el-input
          v-if="rankList.length > 0"
          v-model="searchName"
          placeholder="搜索姓名/昵称"
          prefix-icon="Search"
          clearable
          @input="handleSearch"
          class="search-input"
        />
      </div>
    </div>

    <div class="ranking-content">
      <el-empty
        v-if="filteredRankList.length === 0"
        description="暂无排名数据"
        class="empty-data"
      />
      <div v-else class="ranking-table-wrapper">
        <el-table
          :data="filteredRankList"
          style="width: 100%"
          size="large"
          :header-cell-style="{ background: headerBgColor, color: '#ffffff' }"
          :row-class-name="tableRowClassName"
          border
          highlight-current-row
          class="ranking-table"
        >
          <el-table-column type="index" label="排名" width="80" align="center">
            <template #default="scope">
              <div class="rank">
                <div class="rank-badge" :class="getRankClass(scope.$index)">
                  {{ scope.$index + 1 }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="avatar" label="头像" width="100" align="center">
            <template #default="scope">
              <el-avatar
                :size="50"
                :src="scope.row.avatar"
                class="user-avatar"
              >
                {{ getNameInitial(scope.row.name || scope.row.nickName) }}
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="姓名" min-width="120">
            <template #default="scope">
              <div class="user-info">
                <span class="user-name">{{ scope.row.name || '未知' }}</span>
                <span class="user-nickname" v-if="scope.row.nickName">({{ scope.row.nickName }})</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="100" align="center" sortable>
            <template #default="scope">
              <span class="score">{{ scope.row.score || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="acNum" label="通过题数" width="100" align="center" sortable>
            <template #default="scope">
              <div class="stat-value">
                <span class="stat-number">{{ scope.row.acNum || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="submitNum" label="提交次数" width="100" align="center" sortable>
            <template #default="scope">
              <div class="stat-value">
                <span class="stat-number">{{ scope.row.submitNum || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="useTime" label="用时" width="120" align="center" sortable>
            <template #default="scope">
              <div class="time-value">
                {{ formatTime(scope.row.useTime) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="punishmentTime" label="罚时" width="120" align="center" sortable>
            <template #default="scope">
              <div class="time-value punishment">
                {{ formatTime(scope.row.punishmentTime) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" align="center">
            <template #default="scope">
              <div class="status-badge">
                <enum-show
                  :enum="TopicInfoStatus"
                  :code="scope.row.status"
                  v-if="scope.row.status"
                />
                <span v-else>-</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { Search, TrophyBase } from '@element-plus/icons-vue';
import { getTopicRankApi } from '@/api/modules/oj/topic/topicInfo';
import { TopicJudgeType } from '@/enums/oj/topic/TopicJudgeType';
import { TopicInfoStatus } from '@/enums/oj/topic/TopicInfoStatus';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import type { ITopicInfo } from '@/api/interface/oj/topic/topicInfo';

const props = defineProps({
  topicId: {
    type: Number,
    required: true
  },
  title: {
    type: String,
    default: '排行榜'
  },
  headerBgColor: {
    type: String,
    default: '#409EFF'
  },
  defaultJudgeType: {
    type: Number,
    default: 0 // 默认为ACM判题模式
  }
});

const emit = defineEmits(['update:loading', 'error']);

// 排行榜数据
const rankList = ref<ITopicInfo.Row[]>([]);
// 搜索关键词
const searchName = ref('');
// 选中的判题类型
const selectedJudgeType = ref(props.defaultJudgeType);
// 加载状态
const isLoading = ref(false);

// 表格行样式
const tableRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  if (rowIndex === 0) return 'first-row';
  if (rowIndex === 1) return 'second-row';
  if (rowIndex === 2) return 'third-row';
  return '';
};

// 获取排名样式
const getRankClass = (index: number): string => {
  if (index === 0) return 'first';
  if (index === 1) return 'second';
  if (index === 2) return 'third';
  return '';
};

// 过滤后的排行榜数据
const filteredRankList = computed(() => {
  if (!searchName.value) return rankList.value;

  const keyword = searchName.value.toLowerCase();
  return rankList.value.filter(item => {
    const name = (item.name || '').toLowerCase();
    const nickname = (item.nickName || '').toLowerCase();
    return name.includes(keyword) || nickname.includes(keyword);
  });
});

// 搜索处理
const handleSearch = () => {
  // 实时搜索，不需要额外处理，依赖计算属性即可
};

// 判题类型变更处理
const handleJudgeTypeChange = () => {
  fetchRankingData();
};

// 格式化时间（分钟）
const formatTime = (minutes?: number) => {
  if (!minutes) return '-';

  if (minutes < 60) {
    return `${minutes}分钟`;
  }

  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;

  if (mins === 0) {
    return `${hours}小时`;
  }

  return `${hours}小时${mins}分钟`;
};

// 获取姓名首字母（用于头像显示）
const getNameInitial = (name?: string) => {
  if (!name) return '?';
  return name.charAt(0).toUpperCase();
};

// 获取排行榜数据
const fetchRankingData = async () => {
  if (!props.topicId) return;

  isLoading.value = true;
  emit('update:loading', true);

  try {
    const params = {
      topicId: props.topicId,
      topicJudgeType: selectedJudgeType.value
    };

    const response = await getTopicRankApi(params);

    if (response.data) {
      rankList.value = response.data;
    } else {
      rankList.value = [];
    }
  } catch (error) {
    console.error('获取排行榜数据失败', error);
    emit('error', '获取排行榜数据失败，请稍后重试');
    rankList.value = [];
  } finally {
    isLoading.value = false;
    emit('update:loading', false);
  }
};

// 监听判题类型变化
watch(() => props.defaultJudgeType, (newVal) => {
  selectedJudgeType.value = newVal;
  // 不在这里调用fetchRankingData，避免初始加载时调用两次API
}, { immediate: true });

// 监听主题ID变化
watch(() => props.topicId, () => {
  fetchRankingData();
}, { immediate: true });
</script>

<style scoped lang="scss">
.ranking-section {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
    pointer-events: none;
    z-index: 0;
  }

  &:hover {
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    background: linear-gradient(135deg, v-bind('headerBgColor') 0%, rgba(64, 158, 255, 0.8) 100%);
    color: white;
    position: relative;
    z-index: 1;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, rgba(255, 255, 255, 0.08) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.08) 50%, rgba(255, 255, 255, 0.08) 75%, transparent 75%, transparent);
      background-size: 12px 12px;
      opacity: 0.6;
      z-index: -1;
    }

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
      z-index: 1;
    }

    .title {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 18px;
      font-weight: 700;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      position: relative;
      z-index: 2;

      .el-icon {
        font-size: 20px;
        filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
      }
    }

    .actions {
      display: flex;
      align-items: center;
      gap: 16px;

      .judge-type-selector {
        display: flex;
        align-items: center;
        gap: 8px;

        .selector-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.9);
        }

        .judge-type-radio {
          margin-right: 0;

          :deep(.el-radio__input) {
            display: none;
          }

          :deep(.el-radio__label) {
            padding-left: 0;
          }

          :deep(.judge-type-badge) {
            cursor: pointer;
            opacity: 0.8;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            padding: 6px 14px;
            border-radius: 16px;
            font-weight: 600;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.1) 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.2);
            text-transform: uppercase;
            letter-spacing: 0.8px;
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);

            &:hover {
              opacity: 1;
              background: linear-gradient(135deg, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0.2) 100%);
              transform: translateY(-2px) scale(1.05);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            }
          }

          &.is-checked {
            :deep(.judge-type-badge) {
              opacity: 1;
              background: linear-gradient(135deg, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.3) 100%);
              box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
              border-color: rgba(255, 255, 255, 0.4);
              transform: scale(1.02);
            }
          }
        }
      }

      .search-input {
        width: 220px;

        :deep(.el-input__wrapper) {
          border-radius: 24px;
          background: linear-gradient(135deg, rgba(255, 255, 255, 0.25) 0%, rgba(255, 255, 255, 0.15) 100%);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          padding: 8px 16px;
          border: 1px solid rgba(255, 255, 255, 0.2);
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

          &.is-focus {
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.35) 0%, rgba(255, 255, 255, 0.25) 100%);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            border-color: rgba(255, 255, 255, 0.3);
            transform: translateY(-1px);
          }

          .el-input__inner {
            color: white;
            font-weight: 500;

            &::placeholder {
              color: rgba(255, 255, 255, 0.8);
            }
          }

          .el-input__prefix {
            color: rgba(255, 255, 255, 0.9);
          }
        }
      }
    }
  }

  .ranking-content {
    padding: 0;

    .empty-data {
      padding: 60px 0;
    }

    .ranking-table-wrapper {
      overflow: hidden;

      .ranking-table {
        margin: 0;

        :deep(.el-table__header-wrapper) {
          th {
            font-weight: 600;
            font-size: 14px;
            padding: 12px 0;
          }
        }

        :deep(.el-table__body-wrapper) {
          td {
            padding: 16px 0;
            transition: all 0.2s ease;
          }

          .first-row {
            background-color: rgba(255, 215, 0, 0.1);
            td {
              border-color: rgba(255, 215, 0, 0.2);
            }
          }

          .second-row {
            background-color: rgba(192, 192, 192, 0.1);
            td {
              border-color: rgba(192, 192, 192, 0.2);
            }
          }

          .third-row {
            background-color: rgba(205, 127, 50, 0.1);
            td {
              border-color: rgba(205, 127, 50, 0.2);
            }
          }
        }

        .rank {
          display: flex;
          justify-content: center;
          align-items: center;

          .rank-badge {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            background: linear-gradient(135deg, #f5f5f5, #e0e0e0);
            color: #666;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            border: 2px solid rgba(255, 255, 255, 0.8);

            &:hover {
              transform: scale(1.1);
            }

            &.first {
              background: linear-gradient(135deg, #FFD700, #FFA000);
              color: white;
              box-shadow: 0 4px 16px rgba(255, 215, 0, 0.5);
              border-color: rgba(255, 255, 255, 0.9);
              animation: pulse-gold 2s infinite;
            }

            &.second {
              background: linear-gradient(135deg, #C0C0C0, #9E9E9E);
              color: white;
              box-shadow: 0 4px 16px rgba(192, 192, 192, 0.5);
              border-color: rgba(255, 255, 255, 0.9);
            }

            &.third {
              background: linear-gradient(135deg, #CD7F32, #A0522D);
              color: white;
              box-shadow: 0 4px 16px rgba(205, 127, 50, 0.5);
              border-color: rgba(255, 255, 255, 0.9);
            }
          }
        }

        .user-avatar {
          border: 3px solid white;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          font-weight: 700;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

          &:hover {
            transform: scale(1.15) rotate(5deg);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
            border-color: rgba(255, 255, 255, 0.9);
          }
        }

        .user-info {
          display: flex;
          flex-direction: column;

          .user-name {
            font-weight: 600;
            font-size: 14px;
            color: #333;
          }

          .user-nickname {
            font-size: 12px;
            color: #888;
            margin-top: 4px;
          }
        }

        .score {
          font-weight: 600;
          font-size: 16px;
          color: v-bind('headerBgColor');
        }

        .stat-value {
          .stat-number {
            font-weight: 500;
            font-size: 15px;
          }
        }

        .time-value {
          font-size: 13px;
          color: #666;

          &.punishment {
            color: #F56C6C;
          }
        }

        .status-badge {
          font-size: 13px;
        }
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .ranking-section {
    background: rgba(30, 30, 32, 0.7);
    border-color: rgba(255, 255, 255, 0.05);

    .section-header {
      .actions {
        .judge-type-selector {
          .judge-type-radio {
            :deep(.judge-type-badge) {
              background-color: rgba(255, 255, 255, 0.1);

              &:hover {
                background-color: rgba(255, 255, 255, 0.15);
              }
            }

            &.is-checked {
              :deep(.judge-type-badge) {
                background-color: rgba(255, 255, 255, 0.2);
              }
            }
          }
        }
      }
    }

    .ranking-content {
      .ranking-table-wrapper {
        .ranking-table {
          background-color: transparent;

          :deep(.el-table__body-wrapper) {
            background-color: transparent;

            .first-row {
              background-color: rgba(255, 215, 0, 0.15);
            }

            .second-row {
              background-color: rgba(192, 192, 192, 0.15);
            }

            .third-row {
              background-color: rgba(205, 127, 50, 0.15);
            }

            td {
              background-color: transparent;
              border-color: rgba(255, 255, 255, 0.1);
            }
          }

          .user-avatar {
            border-color: rgba(50, 50, 54, 1);
          }

          .user-info {
            .user-name {
              color: #f5f5f7;
            }

            .user-nickname {
              color: #aaa;
            }
          }

          .rank-badge {
            background-color: #444;
            color: #f5f5f7;

            &.first, &.second, &.third {
              color: #f5f5f7;
            }
          }
        }
      }
    }
  }
}

// 动画效果
@keyframes pulse-gold {
  0%, 100% {
    box-shadow: 0 4px 16px rgba(255, 215, 0, 0.5);
  }
  50% {
    box-shadow: 0 6px 24px rgba(255, 215, 0, 0.8);
    transform: scale(1.05);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>
