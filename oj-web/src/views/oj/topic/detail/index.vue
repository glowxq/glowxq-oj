<template>
  <div class="topic-detail-container">
    <!-- 顶部导航栏 -->
    <div class="topic-nav">
      <div class="back-button" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>
      <div class="topic-title">
        <h1>{{ topicDetail.name || getTypeTextWithDefault('详情') }}</h1>
        <div class="topic-code" v-if="topicDetail.code">{{ topicDetail.code }}</div>
      </div>
      <div class="topic-actions">
        <el-button
          v-if="isEnabled"
          type="primary"
          @click="goToDoTopic"
          :icon="Right"
          class="action-button"
        >
          进入{{ topicTypeText }}
        </el-button>
      </div>
    </div>

    <!-- 主题信息卡片 -->
    <div class="info-section">
      <el-collapse v-model="activeCollapse" accordion class="custom-collapse">
        <el-collapse-item name="info">
          <template #title>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>{{ getTypeTextWithDefault('基本信息') }}</span>
            </div>
          </template>
          <div class="info-content">
            <div class="info-row">
              <div class="info-label">负责人</div>
              <div class="info-value">{{ topicDetail.principalName || '未设置' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">{{ getTypeTextWithDefault('类型') }}</div>
              <div class="info-value">
                <enum-show
                  :enum="TopicType"
                  :code="topicDetail.topicType"
                  v-if="topicDetail.topicType"
                />
                <span v-else>未设置</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-label">判题模式</div>
              <div class="info-value">
                <enum-show
                  :enum="TopicJudgeType"
                  :code="topicDetail.topicJudgeType"
                  v-if="topicDetail.topicJudgeType"
                />
                <span v-else>未设置</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-label">时间策略</div>
              <div class="info-value">
                <enum-show
                  :enum="TimeRangeType"
                  :code="topicDetail.timeRangeType"
                  v-if="topicDetail.timeRangeType"
                />
                <span v-else>未设置</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-label">开始时间</div>
              <div class="info-value">{{ formatDateTime(topicDetail.startTime) || '未设置' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">结束时间</div>
              <div class="info-value">{{ formatDateTime(topicDetail.endTime) || '未设置' }}</div>
            </div>
            <div class="info-row" v-if="topicDetail.description">
              <div class="info-label">描述</div>
              <div class="info-value description">{{ topicDetail.description }}</div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 做题情况表格 -->
    <topic-submit-table
      :topic-id="topicId"
      :title="getTypeTextWithDefault('做题情况')"
      :topic-judge-type="topicDetail.topicJudgeType || 0"
      :topic-name="topicDetail.name"
      :header-bg-color="themePrimary"
      v-model:loading="isSubmitTableLoading"
      @error="handleError"
    />

    <!-- 排行榜组件 -->
    <topic-ranking-table
      :topic-id="topicId"
      :title="getTypeTextWithDefault('排行榜')"
      :header-bg-color="themePrimary"
      :default-judge-type="topicDetail.topicJudgeType || 0"
      v-model:loading="isRankLoading"
      @error="handleError"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, InfoFilled, Right } from '@element-plus/icons-vue';
import { getTopicDetailApi } from '@/api/modules/oj/topic/topic';
import { TopicType } from '@/enums/oj/topic/TopicType';
import { TopicJudgeType } from '@/enums/oj/topic/TopicJudgeType';
import { TimeRangeType } from '@/enums/oj/topic/TimeRangeType';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import { useAppStore } from '@/stores/modules/app';
import { storeToRefs } from 'pinia';
import type { ITopic } from '@/api/interface/oj/topic/topic';
import TopicRankingTable from './components/TopicRankingTable.vue';
import TopicSubmitTable from './components/TopicSubmitTable.vue';

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const { primary: themePrimary } = storeToRefs(appStore);

// 主题详情
const topicDetail = ref<ITopic.Row>({});
// 主题ID
const topicId = computed(() => Number(route.params.id));
// 控制信息折叠面板的激活状态（默认折叠）
const activeCollapse = ref<string[]>([]);
// 排行榜加载状态
const isRankLoading = ref(false);
const isSubmitTableLoading = ref(false);
// 错误计数器，防止重复显示错误
const errorCount = ref(0);

// 是否启用
const isEnabled = computed(() => {
  const enable = topicDetail.value.enable;
  if (typeof enable === 'boolean') return enable;
  if (typeof enable === 'string') return enable === 'true';
  if (typeof enable === 'number') return enable === 1;
  return false;
});

// 获取主题类型文本
const topicTypeText = computed(() => {
  if (!topicDetail.value.topicType) return '主题';

  const typeCode = topicDetail.value.topicType;
  const typeObj = Object.values(TopicType).find(item => (item as any).code === typeCode);

  return typeObj ? (typeObj as any).name : '主题';
});

// 根据主题类型替换文本中的"主题"
const getTypeTextWithDefault = (suffix: string): string => {
  return `${topicTypeText.value}${suffix}`;
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 进入主题
const goToDoTopic = () => {
  if (!topicDetail.value.id) return;

  router.push({
    path: `/oj/topic/doTopic/${topicDetail.value.id}`,
    query: {
      topicName: topicDetail.value.name,
      topicCode: topicDetail.value.code
    }
  });
};

// 格式化日期时间
const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '';

  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 处理错误，避免重复显示
const handleError = (message: string) => {
  // 防止短时间内多次重复显示相同错误
  errorCount.value++;
  const currentCount = errorCount.value;

  // 设置一个延迟，如果在这段时间内有新的错误覆盖，则不显示旧的错误
  setTimeout(() => {
    if (currentCount === errorCount.value) {
      ElMessage.error(message);
    }
  }, 300);
};

// 初始化数据
const fetchData = async () => {
  if (!topicId.value) {
    ElMessage.error('主题ID无效');
    router.back();
    return;
  }

  try {
    // 获取主题详情
    const topicRes = await getTopicDetailApi({ id: topicId.value });
    if (topicRes.data) {
      topicDetail.value = topicRes.data;
    }
  } catch (error) {
    console.error('获取数据失败', error);
    ElMessage.error('获取数据失败，请稍后重试');
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
.topic-detail-container {
  position: relative;
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif;
  display: flex;
  flex-direction: column;
  gap: 24px;

  // 顶部导航栏
  .topic-nav {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    padding: 16px 24px;
    border-radius: 16px;
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    border: 1px solid rgba(0, 0, 0, 0.03);

    &:hover {
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }

    .back-button {
      display: flex;
      align-items: center;
      gap: 6px;
      cursor: pointer;
      color: #666;
      transition: all 0.2s ease;
      padding: 8px 12px;
      border-radius: 10px;

      &:hover {
        background-color: rgba(0, 0, 0, 0.05);
        color: v-bind('themePrimary');
        transform: translateX(-2px);
      }

      &:active {
        transform: translateX(0);
      }
    }

    .topic-title {
      flex: 1;
      text-align: center;

      h1 {
        margin: 0;
        font-size: 24px;
        font-weight: 600;
        color: #333;
        letter-spacing: -0.5px;
      }

      .topic-code {
        font-size: 14px;
        color: #888;
        margin-top: 4px;
        font-family: 'SF Mono', SFMono-Regular, ui-monospace, monospace;
      }
    }

    .topic-actions {
      .action-button {
        border-radius: 10px;
        transition: all 0.2s ease;
        font-weight: 500;
        letter-spacing: 0.2px;
        padding: 10px 18px;
        border: none;
        background: v-bind('themePrimary');

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          filter: brightness(1.05);
        }

        &:active {
          transform: translateY(0);
        }
      }
    }
  }

  // 信息部分 - 使用折叠面板
  .info-section {
    .custom-collapse {
      border-radius: 16px;
      overflow: hidden;
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
      border: 1px solid rgba(0, 0, 0, 0.03);
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      }

      :deep(.el-collapse-item__header) {
        background-color: v-bind('themePrimary');
        color: white;
        padding: 16px 20px;
        border: none;
        font-size: 16px;

        .el-collapse-item__arrow {
          color: white;
          margin-right: 0;
        }
      }

      :deep(.el-collapse-item__wrap) {
        border: none;
        background-color: transparent;
      }

      :deep(.el-collapse-item__content) {
        padding: 0;
      }

      .card-header {
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 16px;
        font-weight: 600;

        .el-icon {
          font-size: 18px;
        }
      }

      .info-content {
        padding: 20px;

        .info-row {
          display: flex;
          margin-bottom: 16px;
          padding-bottom: 16px;
          border-bottom: 1px solid rgba(0, 0, 0, 0.05);

          &:last-child {
            margin-bottom: 0;
            padding-bottom: 0;
            border-bottom: none;
          }

          .info-label {
            flex: 0 0 100px;
            font-weight: 500;
            color: #666;
            font-size: 14px;
          }

          .info-value {
            flex: 1;
            color: #333;
            font-size: 14px;

            &.description {
              white-space: pre-line;
              line-height: 1.6;
            }
          }
        }
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .topic-detail-container {
    .topic-nav {
      background: rgba(30, 30, 32, 0.7);
      border-color: rgba(255, 255, 255, 0.05);

      .back-button {
        color: #aaa;

        &:hover {
          background-color: rgba(255, 255, 255, 0.1);
        }
      }

      .topic-title {
        h1 {
          color: #f5f5f7;
        }

        .topic-code {
          color: #aaa;
        }
      }
    }

    .info-section {
      .custom-collapse {
        background: rgba(30, 30, 32, 0.7);
        border-color: rgba(255, 255, 255, 0.05);

        .info-content {
          .info-row {
            border-bottom-color: rgba(255, 255, 255, 0.1);

            .info-label {
              color: #aaa;
            }

            .info-value {
              color: #f5f5f7;
            }
          }
        }
      }
    }
  }
}
</style>
