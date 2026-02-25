<template>
  <div
    class="group-card"
    :class="{ 'is-selected': isSelected }"
    @click="handleCardClick"
  >
    <!-- 选中状态标记 - 只保留这一个选中标记 -->
    <div class="select-badge" v-if="isSelected">
      <el-icon><Check /></el-icon>
    </div>

    <!-- 状态标签 -->
    <div class="status-badge" :class="{ 'is-enabled': isEnabled }">
      {{ isEnabled ? '已启用' : '已禁用' }}
    </div>

    <!-- 卡片头部 -->
    <div class="card-header">
      <h3 class="card-title">{{ group.name || '未命名班级' }}</h3>
      <div class="card-code" v-if="group.code">{{ group.code }}</div>
    </div>

    <!-- 卡片内容 -->
    <div class="card-content">
      <!-- 负责人 -->
      <div class="info-item">
        <div class="info-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="info-content">
          <div class="info-label">负责人</div>
          <div class="info-value">{{ group.principalName || '未指定' }}</div>
        </div>
      </div>

      <!-- 代码 -->
      <div class="info-item">
        <div class="info-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="info-content">
          <div class="info-label">班级代码</div>
          <div class="info-value code">{{ group.code || '无' }}</div>
        </div>
      </div>

      <!-- 描述 -->
      <div class="info-item" v-if="group.description && group.description.trim()">
        <div class="info-icon">
          <el-icon><InfoFilled /></el-icon>
        </div>
        <div class="info-content">
          <div class="info-label">描述</div>
          <div class="info-value description">{{ group.description }}</div>
        </div>
      </div>

      <!-- 标签 -->
      <div class="info-item tags" v-if="group.tagList && group.tagList.length">
        <div class="info-icon">
          <el-icon><Collection /></el-icon>
        </div>
        <div class="info-content">
          <div class="info-label">标签</div>
          <div class="tag-list">
            <el-tag
              v-for="tag in group.tagList"
              :key="tag.id"
              size="small"
              effect="plain"
              class="group-tag"
            >
              {{ tag.name }}
            </el-tag>
            <div class="tag-more" v-if="group.tagList.length > 2">+{{ group.tagList.length - 2 }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 卡片底部 - 重新设计参考WorkCard -->
    <div class="card-actions">
      <div class="main-action">
        <button
          class="detail-button"
          @click.stop="$emit('detail', group.id)"
        >
          <el-icon><View /></el-icon>
          <span>查看班级</span>
        </button>
      </div>

      <div class="other-actions" v-show="isAdminOrCommon">
        <button
          v-auth="'group.update'"
          class="action-icon edit-icon"
          @click.stop="$emit('edit', group)"
          title="编辑"
        >
          <el-icon><EditPen /></el-icon>
        </button>

        <button
          v-auth="'group.remove'"
          class="action-icon delete-icon"
          @click.stop="$emit('delete', group)"
          title="删除"
        >
          <el-icon><Delete /></el-icon>
        </button>
      </div>
    </div>

    <!-- 选择交互区域 - 移到左上角 -->
    <div class="select-area" @click.stop="toggleSelect" v-if="!isSelected">
      <div class="select-icon">
        <el-icon><Check /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { EditPen, Delete, Document, User, InfoFilled, Check, CircleCheck, View, Collection } from '@element-plus/icons-vue';
import { useRole } from '@/hooks/useRole';
import type { IGroup } from '@/api/interface/oj/group/group';
import { useAppStore } from '@/stores/modules/app';
import { storeToRefs } from 'pinia';
import { getLightColor, getDarkColor } from '@/utils/color';

const props = defineProps({
  group: {
    type: Object as () => IGroup.Row,
    required: true
  },
  isSelected: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['select', 'edit', 'delete', 'detail']);

const { isAdminOrCommon } = useRole();

// 获取应用主题色
const appStore = useAppStore();
const { primary: themePrimary } = storeToRefs(appStore);

// 计算衍生颜色
const primaryLighter = computed(() => getLightColor(themePrimary.value, 0.1));
const primaryDarker = computed(() => getDarkColor(themePrimary.value, 0.1));
const primaryGradient = computed(() => `linear-gradient(135deg, ${primaryLighter.value}, ${primaryDarker.value})`);
const boxShadowColor = computed(() => `rgba(${parseInt(themePrimary.value.slice(1, 3), 16)}, ${parseInt(themePrimary.value.slice(3, 5), 16)}, ${parseInt(themePrimary.value.slice(5, 7), 16)}, 0.15)`);

// 启用状态
const isEnabled = computed(() => {
  const enableValue = props.group.enable;
  if (enableValue === undefined || enableValue === null) return false;

  // 处理不同类型的enable值
  if (typeof enableValue === 'boolean') return enableValue;
  if (typeof enableValue === 'string') {
    return enableValue === 'true' || enableValue === 'T';
  }
  return false;
});

// 处理卡片点击
const handleCardClick = () => {
  emit('detail', props.group.id);
};

// 选择/取消选择
const toggleSelect = () => {
  emit('select', props.group);
};
</script>

<style scoped lang="scss">
.group-card {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 380px;
  background-color: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px v-bind('boxShadowColor');
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  margin-bottom: 16px;

  // 顶部装饰条
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 6px;
    background-color: v-bind('themePrimary');
    z-index: 2;
  }

  // 悬浮效果
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--oj-shadow-hover);

    .select-area {
      opacity: 1;
    }

    .card-title {
      transform: translateY(-2px);
    }
  }

  // 选中状态
  &.is-selected {
    box-shadow: 0 0 0 2px v-bind('themePrimary'), 0 8px 20px v-bind('boxShadowColor');
  }

  // 选中状态标记
  .select-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    width: 22px;
    height: 22px;
    background-color: v-bind('themePrimary');
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 3;
    animation: pop-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);

    .el-icon {
      color: #FFFFFF;
      font-size: 14px;
    }
  }

  // 状态标签
  .status-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    padding: 3px 10px;
    border-radius: 12px;
    background-color: #f5f5f7;
    color: #86868b;
    font-size: 12px;
    font-weight: 600;
    z-index: 3;
    letter-spacing: 0.3px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    backdrop-filter: blur(8px);

    &.is-enabled {
      background-color: rgba(0, 200, 83, 0.1);
      color: #00c853;
    }

    &:not(.is-enabled) {
      background-color: rgba(244, 67, 54, 0.1);
      color: #f44336;
    }
  }

  // 卡片头部
  .card-header {
    padding: 40px 20px 20px;
    text-align: center;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60px;
      height: 3px;
      background-color: v-bind('themePrimary');
      opacity: 0.5;
      border-radius: 1.5px;
    }

    .card-title {
      font-size: 20px;
      font-weight: 600;
      color: v-bind('themePrimary');
      margin: 0 0 10px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
      transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1);
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
    }

    .card-code {
      font-size: 14px;
      color: #6e6e73;
      font-family: 'SF Mono', SFMono-Regular, ui-monospace, monospace;
      letter-spacing: 0.5px;
    }
  }

  // 卡片内容
  .card-content {
    flex: 1;
    padding: 20px 20px 0px 20px;
    overflow-y: auto;

    .info-item {
      margin-bottom: 5px;
      display: flex;
      align-items: flex-start;
      transition: all 0.3s ease;

      &:hover {
        transform: translateX(2px);

        .info-icon {
          transform: scale(1.05);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }
      }

      .info-icon {
        width: 32px;
        height: 32px;
        border-radius: 8px;
        background-color: v-bind('getLightColor(themePrimary, 0.9)');
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);

        .el-icon {
          font-size: 18px;
          color: v-bind('themePrimary');
        }
      }

      .info-content {
        flex: 1;
        min-width: 0;

        .info-label {
          font-size: 12px;
          color: #86868b;
          margin-bottom: 4px;
          font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
        }

        .info-value {
          font-size: 14px;
          color: #1d1d1f;
          font-weight: 500;
          word-break: break-word;
          font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;

          &.code {
            font-family: 'SF Mono', SFMono-Regular, ui-monospace, monospace;
            color: v-bind('themePrimary');
            letter-spacing: 0.5px;
            background-color: rgba(0, 0, 0, 0.02);
            padding: 2px 6px;
            border-radius: 4px;
            display: inline-block;
          }

          &.description {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            line-height: 1.4;
          }
        }

        .tag-list {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;

          .group-tag {
            background-color: v-bind('getLightColor(themePrimary, 0.9)');
            border-color: v-bind('getLightColor(themePrimary, 0.8)');
            color: v-bind('themePrimary');
            transition: all 0.2s ease;

            &:hover {
              transform: translateY(-1px);
              box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            }
          }

          .tag-more {
            font-size: 12px;
            color: #86868b;
            padding: 0 6px;
          }
        }
      }

      &.tags {
        .info-icon {
          background-color: v-bind('getLightColor(themePrimary, 0.9)');

          .el-icon {
            color: v-bind('themePrimary');
          }
        }
      }
    }
  }

  // 卡片操作 - 新设计参考WorkCard
  .card-actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 10px 16px 16px 16px;
    background-color: #f9f9fb;
    border-top: 1px solid rgba(0, 0, 0, 0.03);

    .main-action {
      width: 100%;

      .detail-button {
        width: 100%;
        height: 40px;
        background: v-bind('primaryGradient');
        color: white;
        border: none;
        border-radius: 20px;
        font-size: 15px;
        font-weight: 600;
        letter-spacing: 0.5px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
        box-shadow: 0 2px 6px v-bind('boxShadowColor');
        display: flex;
        align-items: center;
        justify-content: center;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;

        .el-icon {
          margin-right: 6px;
          font-size: 16px;
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 10px v-bind('boxShadowColor');
          filter: brightness(1.05);
        }

        &:active {
          transform: translateY(0);
          box-shadow: 0 1px 3px v-bind('boxShadowColor');
          filter: brightness(0.98);
        }
      }
    }

    .other-actions {
      display: flex;
      justify-content: center;
      gap: 14px;
      margin-top: 8px;

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
        transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
        color: #666;

        &:hover {
          background-color: rgba(0, 0, 0, 0.05);
          transform: translateY(-2px);
        }

        &:active {
          transform: translateY(0);
        }

        &.edit-icon:hover {
          color: v-bind('themePrimary');
        }

        &.delete-icon:hover {
          color: #F56C6C;
        }

        .el-icon {
          font-size: 16px;
        }
      }
    }
  }

  // 选择交互区域 - 移到左上角
  .select-area {
    position: absolute;
    top: 12px;
    left: 12px;
    width: 22px;
    height: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 4;
    opacity: 0;
    transition: opacity 0.2s ease, transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
    border-radius: 50%;
    border: 1px solid #d2d2d7;
    background-color: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(4px);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .select-icon {
      font-size: 14px;
      color: #86868b;

      .el-icon {
        transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);

        &:hover {
          transform: scale(1.1);
        }
      }
    }

    &:hover {
      background-color: #f5f5f7;
      border-color: v-bind('themePrimary');
      transform: scale(1.1);

      .select-icon {
        color: v-bind('themePrimary');
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
</style>
