<template>
  <div
    class="topic-card"
    :class="{ 'is-selected': isSelected }"
    :style="{
      background: bgGradient,
      boxShadow: `0 6px 16px ${boxShadowColor}`
    }"
    @click="toggleSelection"
  >
    <!-- 类型标签 - 左上角 -->
    <div class="topic-type-badge" v-if="topicData.topicType">
      <enum-show
        :enum="TopicType"
        :code="topicData.topicType"
        class="type-badge-text"
        :color="'#fff'"
        :background-color="themePrimary"
      />
    </div>

    <!-- 选中复选框 -->
    <div class="select-checkbox" v-if="isSelected">
      <span class="checkbox-icon">✓</span>
    </div>

    <!-- 卡片状态徽章 -->
    <div class="card-status-badge" :class="getStatusClass">
      {{ getTopicStatusText }}
      <el-tooltip v-if="!hasTopicType" :content="`主题可能是：${possibleTypesText}`" placement="top">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
      </el-tooltip>
    </div>

    <!-- 卡片标题 -->
    <div class="card-header">
      <h3 class="card-title" :style="{ color: themePrimary }">{{ topicData.name || '未命名主题' }}</h3>
      <div class="card-code" @click.stop="copyTopicCode">
        {{ topicData.code || '' }}
        <el-icon class="copy-icon"><CopyDocument /></el-icon>
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="card-content">
      <!-- 主题类型和判题模式 -->
      <div class="info-row type-judge-row" v-if="topicData.topicJudgeType">
        <div class="info-item">
          <div class="info-label">
            <div class="icon-wrapper">
              <el-icon><Share /></el-icon>
            </div>
            判题:
          </div>
          <div class="info-value">
            <enum-show
              :enum="TopicJudgeType"
              :code="topicData.topicJudgeType"
              class="enum-badge"
            />
          </div>
        </div>
      </div>

      <!-- 负责人信息 -->
      <div class="info-row" v-if="topicData.principalName">
        <div class="info-label">
          <div class="icon-wrapper">
            <img src="../../../../../assets/icons/teacher.svg" class="icon" />
          </div>
          负责人:
        </div>
        <div class="info-value">{{ topicData.principalName || '暂无' }}</div>
      </div>

      <!-- 时间信息 -->
      <div class="time-info-container">
        <div class="time-info-header">
          <div class="icon-wrapper">
            <el-icon><Timer /></el-icon>
          </div>
          时间范围
          <span class="time-strategy" v-if="topicData.timeRangeType">
            <enum-show
              :enum="TimeRangeType"
              :code="topicData.timeRangeType"
              class="enum-inline"
            />
          </span>
        </div>
        <div class="time-range">
          <div class="time-item" v-if="topicData.startTime">
            <span class="time-label">开始:</span>
            <span class="time-value">{{ formatDate(topicData.startTime) }}</span>
          </div>
          <div class="time-item" v-if="topicData.endTime">
            <span class="time-label">截止:</span>
            <span class="time-value">{{ formatDate(topicData.endTime) }}</span>
          </div>
          <div class="no-time-set" v-if="!topicData.startTime && !topicData.endTime">未设置时间范围</div>
        </div>
      </div>

      <!-- 描述信息 -->
      <div class="description-container" v-if="topicData.description">
        <div class="description-header">
          <div class="icon-wrapper">
            <el-icon><DocumentCopy /></el-icon>
          </div>
          描述信息
        </div>
        <div class="description-content">
          {{ topicData.description }}
        </div>
      </div>

      <!-- 公共/启用状态 和标签 -->
      <div class="status-row">
        <div class="status-tags">
          <div class="status-tag" :class="{ 'is-active': isCommon }">
            <span class="tag-text">公共</span>
          </div>
          <div class="status-tag" :class="{ 'is-active': isEnabled }">
            <span class="tag-text">启用</span>
          </div>
        </div>

        <div class="tags-container" v-if="topicData.tagList && topicData.tagList.length">
          <el-tag
            v-for="tag in topicData.tagList"
            :key="tag.id"
            :color="tag.color"
            :style="{ color: tag.textColor || '#ffffff' }"
            class="topic-tag"
            size="small"
          >
            {{ tag.name }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 操作按钮组 -->
    <div class="card-actions">
      <div class="action-buttons-row">
        <button
          class="action-button detail-button"
          @click.stop="handleDetail"
          title="查看详情"
        >
          <div class="button-content">
            <el-icon><InfoFilled /></el-icon>
            <span>查看详情</span>
          </div>
        </button>
        
        <button
          class="do-topic-button"
          @click.stop="handleDoTopic"
          v-if="isEnabled"
        >
          进入{{ topicTypeText }}
        </button>
      </div>

      <div class="other-actions">
        <button
          v-if="hasListener('edit')"
          v-auth="'topic.update'"
          class="action-icon edit-icon"
          @click.stop="handleEdit"
          title="编辑"
        >
          <el-icon><Edit /></el-icon>
        </button>

        <button
          v-if="hasListener('copy')"
          v-auth="'topic.create'"
          class="action-icon copy-icon"
          @click.stop="handleCopy"
          title="复制"
        >
          <el-icon><CopyDocument /></el-icon>
        </button>

        <button
          v-if="hasListener('delete')"
          v-auth="'topic.remove'"
          class="action-icon delete-icon"
          @click.stop="handleDelete"
          title="删除"
        >
          <el-icon><Delete /></el-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance } from 'vue';
import { Edit, Delete, CopyDocument, DocumentCopy, InfoFilled, Share, Timer } from '@element-plus/icons-vue';
import { useAppStore } from '@/stores/modules/app';
import { storeToRefs } from 'pinia';
import { getLightColor, getDarkColor } from '@/utils/color';
import { TopicInfoStatus } from '@/enums/oj/topic/TopicInfoStatus';
import { TopicType } from '@/enums/oj/topic/TopicType';
import { TopicJudgeType } from '@/enums/oj/topic/TopicJudgeType';
import { TimeRangeType } from '@/enums/oj/topic/TimeRangeType';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import type { ITopic } from '@/api/interface/oj/topic/topic';
import { ElMessage } from 'element-plus';

const props = defineProps({
  topic: {
    type: Object as () => ITopic.Row,
    required: true
  },
  isSelected: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['edit', 'copy', 'delete', 'doTopic', 'select', 'detail']);

// 获取应用主题色
const appStore = useAppStore();
const { primary: themePrimary } = storeToRefs(appStore);

// 计算衍生颜色
const primaryLighter = computed(() => getLightColor(themePrimary.value, 0.1));
const primaryDarker = computed(() => getDarkColor(themePrimary.value, 0.1));
const primaryGradient = computed(() => `linear-gradient(135deg, ${primaryLighter.value}, ${primaryDarker.value})`);
const bgGradient = computed(() => `linear-gradient(135deg, ${getLightColor(themePrimary.value, 0.95)}, ${getLightColor(themePrimary.value, 0.9)})`);
const boxShadowColor = computed(() => 'rgba(0, 0, 0, 0.1)');

// 处理主题数据
const topicData = computed(() => {
  if (typeof props.topic === 'string') {
    try {
      return JSON.parse(props.topic);
    } catch (e) {
      console.error('无法解析主题数据', e);
      return {
        name: '数据解析错误',
        content: props.topic
      };
    }
  }
  return props.topic;
});

// 判断是否为公共主题
const isCommon = computed(() => {
  return topicData.value.common === 'true' || topicData.value.common === true;
});

// 判断是否启用
const isEnabled = computed(() => {
  return topicData.value.enable === 'true' || topicData.value.enable === true;
});

// 获取状态样式类
const getStatusClass = computed(() => {
  const status = getTopicStatus.value;

  if (status === 'disabled') return 'status-unknown';
  if (status === TopicInfoStatus.NOT_STARTED.code) return 'status-upcoming';
  if (status === TopicInfoStatus.COMPLETED.code) return 'status-ended';
  return 'status-ongoing';
});

// 获取主题状态
const getTopicStatus = computed(() => {
  const now = new Date();
  const startTime = topicData.value.startTime ? new Date(topicData.value.startTime) : null;
  const endTime = topicData.value.endTime ? new Date(topicData.value.endTime) : null;

  if (!isEnabled.value) {
    return 'disabled';
  }

  if (startTime && now < startTime) {
    return TopicInfoStatus.NOT_STARTED.code;
  }

  if (endTime && now > endTime) {
    return TopicInfoStatus.COMPLETED.code;
  }

  return TopicInfoStatus.IN_PROGRESS.code;
});

// 获取主题状态文本
const getTopicStatusText = computed(() => {
  const status = getTopicStatus.value;

  if (status === 'disabled') {
    return '已禁用';
  }

  const statusMap = {
    [TopicInfoStatus.NOT_STARTED.code]: '即将开始',
    [TopicInfoStatus.IN_PROGRESS.code]: '进行中',
    [TopicInfoStatus.COMPLETED.code]: '已结束'
  };

  return statusMap[status] || '未知状态';
});

// 获取主题类型文本
const hasTopicType = computed(() => {
  return !!topicData.value.topicType;
});

const topicTypeText = computed(() => {
  if (!topicData.value.topicType) return '主题';

  const typeCode = topicData.value.topicType;
  const typeObj = Object.values(TopicType).find(item => item.code === typeCode);

  return typeObj ? typeObj.name : '主题';
});

// 可能的主题类型列表（用于tooltip）
const possibleTypesText = computed(() => {
  return Object.values(TopicType).map(item => item.name).join('、');
});

// 格式化日期
const formatDate = (dateStr: string): string => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const month = date.getMonth() + 1;
  const day = date.getDate();

  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${month}月${day}日 ${hours}:${minutes}`;
};

// 复制主题代码
const copyTopicCode = (event: MouseEvent): void => {
  event.stopPropagation();
  if (!topicData.value.code) return;

  // 使用clipboard API复制文本
  navigator.clipboard.writeText(topicData.value.code)
    .then(() => {
      ElMessage({
        message: '代码已复制到剪贴板',
        type: 'success',
        duration: 2000
      });
    })
    .catch(() => {
      ElMessage({
        message: '复制失败，请手动复制',
        type: 'error',
        duration: 2000
      });
    });
};

// 切换选中状态
const toggleSelection = () => {
  emit('select', topicData.value);
};

// 处理编辑
const handleEdit = () => {
  emit('edit', topicData.value);
};

// 处理复制
const handleCopy = () => {
  emit('copy', topicData.value);
};

// 处理删除
const handleDelete = () => {
  emit('delete', topicData.value);
};

// 处理进入主题
const handleDoTopic = () => {
  emit('doTopic', topicData.value);
};

// 处理查看详情
const handleDetail = () => {
  emit('detail', topicData.value);
};

// 使用 getCurrentInstance 判断事件监听是否存在
const instance = getCurrentInstance();
const hasListener = (event: string) => {
  return !!instance?.vnode.props?.[`on${event.charAt(0).toUpperCase() + event.slice(1)}`];
};
</script>

<style scoped lang="scss">
.topic-card {
  position: relative;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 32px;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background-clip: padding-box;
  min-height: 300px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  width: 100%;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  &.is-selected {
    border-color: v-bind('themePrimary');
    transform: translateY(-5px);

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      background: rgba(255, 255, 255, 0.1);
      z-index: 0;
    }
  }

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px v-bind('boxShadowColor');
    border-color: v-bind('primaryLighter');

    .card-title {
      transform: translateY(-2px);
    }
  }

  .select-checkbox {
    position: absolute;
    top: 12px;
    left: 12px;
    width: 18px;
    height: 18px;
    background: v-bind('themePrimary');
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    z-index: 10;
    animation: pop-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

    .checkbox-icon {
      font-size: 10px;
    }
  }

  .card-status-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    padding: 3px 8px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 500;
    z-index: 10;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    letter-spacing: 0.3px;
    display: flex;
    align-items: center;
    gap: 4px;

    .info-icon {
      font-size: 12px;
      opacity: 0.8;
      cursor: help;
      transition: all 0.2s ease;

      &:hover {
        opacity: 1;
        transform: scale(1.1);
      }
    }

    &.status-upcoming {
      background-color: rgba(230, 162, 60, 0.15);
      color: #E6A23C;
      border: 1px solid rgba(230, 162, 60, 0.3);

      &::before {
        content: '';
        display: inline-block;
        width: 4px;
        height: 4px;
        background-color: #E6A23C;
        border-radius: 50%;
        margin-right: 3px;
        animation: pulse 2s infinite;
      }
    }

    &.status-ongoing {
      background-color: rgba(103, 194, 58, 0.15);
      color: #67C23A;
      border: 1px solid rgba(103, 194, 58, 0.3);

      &::before {
        content: '';
        display: inline-block;
        width: 4px;
        height: 4px;
        background-color: #67C23A;
        border-radius: 50%;
        margin-right: 3px;
      }
    }

    &.status-ended, &.status-unknown {
      background-color: rgba(144, 147, 153, 0.15);
      color: #909399;
      border: 1px solid rgba(144, 147, 153, 0.3);

      &::before {
        content: '';
        display: inline-block;
        width: 4px;
        height: 4px;
        background-color: #909399;
        border-radius: 50%;
        margin-right: 3px;
      }
    }
  }

  .card-header {
    position: relative;
    margin-bottom: 16px;
    padding-top: 6px;

    .card-title {
      font-size: 18px;
      font-weight: 600;
      margin: 0;
      transition: all 0.3s ease;
      position: relative;
      z-index: 2;
      color: #333;
      padding: 2px 0;
    }

    .card-code {
      font-size: 12px;
      color: #909399;
      margin-top: 3px;
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
      letter-spacing: 0.3px;
      display: flex;
      align-items: center;
      gap: 4px;
      width: fit-content;
      padding: 2px 6px;
      border-radius: 4px;
      transition: all 0.2s ease;

      .copy-icon {
        font-size: 12px;
        opacity: 0;
        transition: opacity 0.2s ease;
      }

      &:hover {
        background-color: rgba(0, 0, 0, 0.05);

        .copy-icon {
          opacity: 0.8;
        }
      }

      &:active {
        background-color: rgba(0, 0, 0, 0.1);
      }
    }
  }

  .card-content {
    position: relative;
    z-index: 2;
    flex: 1;
    font-size: 14px;

    .icon-wrapper {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 14px;
      height: 14px;
      margin-right: 6px;

      .icon {
        width: 14px;
        height: 14px;
        object-fit: contain;
        opacity: 0.7;
      }
    }

    .info-row {
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      background-color: rgba(255, 255, 255, 0.5);
      padding: 8px 12px;
      border-radius: 10px;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.8);
      }

      .info-label {
        font-weight: 500;
        color: #555;
        margin-right: 6px;
        min-width: 70px;
        display: flex;
        align-items: center;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
      }

      .info-value {
        flex: 1;
        color: #303133;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
      }
    }

    .time-info-container {
      background-color: rgba(255, 255, 255, 0.5);
      padding: 10px 12px;
      border-radius: 10px;
      margin-bottom: 10px;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.8);
      }

      .time-info-header {
        font-weight: 500;
        color: #555;
        margin-bottom: 6px;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
        display: flex;
        align-items: center;
        font-size: 14px;

        .time-strategy {
          margin-left: 4px;
          font-size: 12px;
          color: #666;

          .enum-inline {
            display: inline-flex;
            font-size: 12px;
          }
        }
      }

      .time-range {
        display: flex;
        flex-direction: column;
        gap: 5px;

        .time-item {
          display: flex;
          align-items: center;

          .time-label {
            font-size: 13px;
            color: #666;
            width: 38px;
          }

          .time-value {
            font-size: 13px;
            color: #333;
            font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
          }
        }

        .no-time-set {
          color: #909399;
          font-style: italic;
          font-size: 13px;
          text-align: center;
          padding: 2px 0;
        }
      }
    }

    .description-container {
      background-color: rgba(255, 255, 255, 0.5);
      padding: 10px 12px;
      border-radius: 10px;
      margin-bottom: 10px;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.8);
      }

      .description-header {
        font-weight: 500;
        color: #555;
        margin-bottom: 6px;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
        display: flex;
        align-items: center;
        font-size: 14px;
      }

      .description-content {
        font-size: 13px;
        color: #333;
        line-height: 1.5;
      }
    }

    .status-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10px;

      .status-tags {
        display: flex;
        gap: 8px;

        .status-tag {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 4px 10px;
          border-radius: 8px;
          background-color: rgba(255, 255, 255, 0.5);
          transition: all 0.3s ease;
          opacity: 0.5;

          &.is-active {
            opacity: 1;
            background-color: rgba(255, 255, 255, 0.8);
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
          }

          .tag-text {
            font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
            font-size: 12px;
            font-weight: 500;
            color: #555;
          }
        }
      }

      .tags-container {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;

        .topic-tag {
          border-radius: 12px;
          padding: 2px 8px;
          height: 22px;
          line-height: 22px;
          font-size: 12px;
        }
      }
    }
  }

  .card-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-top: 12px;
    position: relative;
    z-index: 3;

    .action-buttons-row {
      display: flex;
      gap: 12px;
      width: 100%;
      
      .action-button {
        flex: 1;
        height: 40px;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 0.5px;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
        border: none;
        background: rgba(0, 0, 0, 0.03);
        color: #333;
        
        .button-content {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 6px;
        }
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
          background: rgba(0, 0, 0, 0.05);
        }
        
        &:active {
          transform: translateY(0);
        }
        
        &.detail-button {
          background: rgba(230, 162, 60, 0.1);
          color: #E6A23C;
          
          &:hover {
            background: rgba(230, 162, 60, 0.15);
          }
        }
      }

      .do-topic-button {
        flex: 1;
        height: 40px;
        background: v-bind('primaryGradient');
        color: white;
        border: none;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 600;
        letter-spacing: 0.5px;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
          filter: brightness(1.05);
        }

        &:active {
          transform: translateY(0);
        }
      }
    }

    .other-actions {
      display: flex;
      justify-content: center;
      gap: 14px;

      .action-icon {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: transparent;
        border: none;
        cursor: pointer;
        transition: all 0.3s ease;
        color: #666;

        &:hover {
          background-color: rgba(0, 0, 0, 0.05);
          transform: translateY(-2px);
        }

        &.edit-icon:hover {
          color: v-bind('themePrimary');
        }

        &.copy-icon:hover {
          color: #409EFF;
        }

        &.delete-icon:hover {
          color: #F56C6C;
        }

        .el-icon {
          font-size: 15px;
        }
      }
    }
  }
}

@keyframes pop-in {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  70% {
    transform: scale(1.1);
    opacity: 1;
  }
  100% {
    transform: scale(1);
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    opacity: 0.7;
  }
  70% {
    transform: scale(1);
    opacity: 0.3;
  }
  100% {
    transform: scale(0.95);
    opacity: 0.7;
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .topic-card {
    background-color: rgba(30, 30, 32, 0.7);
    border-color: rgba(255, 255, 255, 0.05);

    .card-header .card-title {
      color: #f5f5f7;
    }

    .card-content {
      .info-row, .time-info-container, .description-container, .status-tag {
        background-color: rgba(50, 50, 54, 0.5);

        &:hover {
          background-color: rgba(60, 60, 64, 0.7);
        }
      }

      .status-tags .status-tag.is-active {
        background-color: rgba(60, 60, 64, 0.7);
      }

      .info-label, .time-info-header, .description-header {
        color: #aaa;
      }

      .info-value, .time-value, .description-content {
        color: #f5f5f7;
      }

      .time-label {
        color: #aaa !important;
      }

      .icon-wrapper .icon {
        filter: brightness(0) invert(0.8);
      }

      .status-tags .status-tag .tag-text {
        color: #aaa;
      }
    }

    .card-actions {
      .action-buttons-row {
        .action-button {
          background: rgba(255, 255, 255, 0.1);
          color: #f5f5f7;
          
          &:hover {
            background: rgba(255, 255, 255, 0.15);
          }
          
          &.detail-button {
            background: rgba(230, 162, 60, 0.2);
            color: #ffc069;
            
            &:hover {
              background: rgba(230, 162, 60, 0.25);
            }
          }
        }
        
        .do-topic-button {
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
        }
      }

      .other-actions .action-icon {
        color: #aaa;

        &:hover {
          background-color: rgba(255, 255, 255, 0.1);
        }
      }
    }
  }
}

.type-judge-row {
  gap: 10px;
  justify-content: space-between;
}

.enum-badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

/* 类型标签样式 */
.topic-type-badge {
  position: absolute;
  top: 0;
  left: 20px;
  z-index: 20;

  .type-badge-text {
    padding: 4px 12px;
    border-radius: 0 0 8px 8px;
    font-size: 12px;
    font-weight: 600;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
    letter-spacing: 0.5px;
  }
}
</style>
