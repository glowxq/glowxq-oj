<template>
  <div
    class="course-card"
    :class="{ 'is-selected': isSelected }"
    :style="{
      background: bgGradient,
      boxShadow: `0 6px 16px ${boxShadowColor}`
    }"
    @click="handleCardClick"
  >
    <div class="select-checkbox" v-if="isSelected">
      <span class="checkbox-icon">✓</span>
    </div>

    <div class="card-status-badge" :class="getStatusClass">
      {{ getStatusText }}
    </div>

    <div class="card-header">
      <h3 class="card-title" :style="{ color: themePrimary }">{{ courseData.name || '未命名课程' }}</h3>
      <div class="card-code" v-if="courseData.code">{{ courseData.code }}</div>
    </div>

    <div class="card-content">
      <div class="info-row">
        <div class="info-label">
          <div class="icon-wrapper">
            <img src="../../../../assets/icons/teacher.svg" class="icon" />
          </div>
          授课老师:
        </div>
        <div class="info-value">{{ courseData.teacherName || '暂无' }}</div>
      </div>

      <div class="info-row" v-if="courseData.teacherPhone">
        <div class="info-label">
          <div class="icon-wrapper">
            <img src="../../../../assets/icons/phone.svg" class="icon" />
          </div>
          联系电话:
        </div>
        <div class="info-value">
          <span class="phone-number">{{ courseData.teacherPhone }}</span>
          <button class="copy-phone-btn" @click.stop="copyPhoneNumber" title="复制电话">
            <el-icon><DocumentCopy /></el-icon>
          </button>
        </div>
      </div>

      <div class="time-info-container">
        <div class="time-info-header">
          <div class="icon-wrapper">
            <img src="../../../../assets/icons/time.svg" class="icon" />
          </div>
          课程时间
        </div>
        <div class="time-range">
          <div class="time-item" v-if="courseData.startTime">
            <span class="time-label">开始:</span>
            <span class="time-value">{{ formatDate(courseData.startTime) }}</span>
          </div>
          <div class="time-item" v-if="courseData.endTime">
            <span class="time-label">结束:</span>
            <span class="time-value">{{ formatDate(courseData.endTime) }}</span>
          </div>
          <div class="no-time-set" v-if="!courseData.startTime && !courseData.endTime">未设置课程时间</div>
        </div>
      </div>

      <div class="description-container" v-if="courseData.content">
        <div class="description-header">
          <div class="icon-wrapper">
            <img src="../../../../assets/icons/note.svg" class="icon" />
          </div>
          课程内容
        </div>
        <div class="description-wrapper">
          <p class="description-text" :title="courseData.content">{{ courseData.content }}</p>
        </div>
      </div>

      <div class="groups-container" v-if="courseData.groups && courseData.groups.length > 0">
        <div class="groups-header">
          <div class="icon-wrapper">
            <img src="../../../../assets/icons/class.svg" class="icon" />
          </div>
          班级
        </div>
        <div class="groups-tags" ref="groupsTagsRef" :class="{ 'overflow': isGroupsOverflow }">
          <el-tag
            v-for="group in courseData.groups"
            :key="group.id"
            class="group-tag"
            size="small"
            type="info"
            effect="plain"
            :style="{
              borderColor: group.color || '#e9e9eb',
              color: group.color || '#606266',
            }"
          >
            {{ group.name }}
          </el-tag>
        </div>
      </div>
    </div>

    <div class="card-actions">
      <div class="actions-container">
        <button
          class="action-button view-details-button"
          @click.stop="viewDetails"
        >
          查看详情
        </button>
        <button
          class="action-button start-course-button"
          :class="{ 'disabled': !isCourseTime || !courseData.url }"
          @click.stop="startCourse"
        >
          立即上课
        </button>
      </div>

      <div class="other-actions" v-show="isAdminOrCommon">
        <button
          v-if="hasListener('edit')"
          v-auth="'course.update'"
          class="action-icon edit-icon"
          @click.stop="$emit('edit', courseData)"
          title="编辑"
        >
          <el-icon><EditPen /></el-icon>
        </button>

        <button
          v-if="hasListener('copy')"
          class="action-icon copy-icon"
          v-auth="'course.update'"
          @click.stop="$emit('copy', courseData)"
          title="复制"
        >
          <el-icon><DocumentCopy /></el-icon>
        </button>

        <button
          v-if="hasListener('delete')"
          v-auth="'course.remove'"
          class="action-icon delete-icon"
          @click.stop="$emit('delete', courseData)"
          title="删除"
        >
          <el-icon><Delete /></el-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, getCurrentInstance } from 'vue';
import { EditPen, Delete, DocumentCopy } from '@element-plus/icons-vue';
import { useRole } from '@/hooks/useRole';
import { useAppStore } from '@/stores/modules/app';
import { ElMessage } from 'element-plus';
import { getLightColor, getDarkColor } from '@/utils/color';
import type { ICourse } from '@/api/interface/oj/course/course';

const props = defineProps<{
  course: ICourse.Row;
  isSelected: boolean;
}>();

const emit = defineEmits<{
  (e: 'edit', course: ICourse.Row): void;
  (e: 'copy', course: ICourse.Row): void;
  (e: 'delete', course: ICourse.Row): void;
  (e: 'select', course: ICourse.Row): void;
  (e: 'view-details', course: ICourse.Row): void;
}>();

const { isAdminOrCommon } = useRole();

// 获取应用主题色
const appStore = useAppStore();
const themePrimary = computed(() => appStore.primary);

// 计算衍生颜色
const primaryLighter = computed(() => getLightColor(themePrimary.value, 0.1));
const primaryDarker = computed(() => getDarkColor(themePrimary.value, 0.1));
const primaryGradient = computed(() => `linear-gradient(135deg, ${getLightColor(themePrimary.value, 0.15)}, ${getDarkColor(themePrimary.value, 0.05)})`);
const bgGradient = computed(() => `linear-gradient(135deg, ${getLightColor(themePrimary.value, 0.98)}, ${getLightColor(themePrimary.value, 0.95)})`);
const boxShadowColor = computed(() => 'rgba(0, 0, 0, 0.08)');

// 处理可能的字符串格式课程数据
const courseData = computed(() => {
  // 如果课程数据是字符串，尝试解析JSON
  if (typeof props.course === 'string') {
    try {
      return JSON.parse(props.course);
    } catch (e) {
      console.error('无法解析课程数据', e);
      return {
        name: '数据解析错误',
        content: props.course
      };
    }
  }
  return props.course;
});

// 获取课程状态
const getStatusClass = computed(() => {
  if (!courseData.value.startTime || !courseData.value.endTime) return 'status-unknown';

  const now = new Date().getTime();
  const startTime = new Date(courseData.value.startTime).getTime();
  const endTime = new Date(courseData.value.endTime).getTime();

  if (now < startTime) return 'status-upcoming';
  if (now > endTime) return 'status-ended';
  return 'status-ongoing';
});

const getStatusText = computed(() => {
  if (!courseData.value.startTime || !courseData.value.endTime) return '未设置时间';

  const now = new Date().getTime();
  const startTime = new Date(courseData.value.startTime).getTime();
  const endTime = new Date(courseData.value.endTime).getTime();

  if (now < startTime) return '即将开始';
  if (now > endTime) return '已结束';
  return '进行中';
});

// 是否处于上课时间（课程开始前30分钟内，或者课程进行中）
const isCourseTime = computed(() => {
  if (!courseData.value.startTime || !courseData.value.endTime) return false;

  const now = new Date().getTime();
  const startTime = new Date(courseData.value.startTime).getTime();
  const endTime = new Date(courseData.value.endTime).getTime();

  // 课程开始前30分钟内，或者课程进行中
  return (now >= startTime - 30 * 60 * 1000 && now <= endTime);
});

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const month = date.getMonth() + 1;
  const day = date.getDate();

  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${month}月${day}日 ${hours}:${minutes}`;
};

// 开始上课
const startCourse = () => {
  if (!courseData.value.url) {
    ElMessage.warning('该课程没有设置课程链接，无法开始上课');
    return;
  }

  window.open(courseData.value.url, '_blank');
  ElMessage.success(`已开始【${courseData.value.name}】课程`);
};

// 复制电话号码
const copyPhoneNumber = () => {
  if (courseData.value.teacherPhone) {
    navigator.clipboard.writeText(courseData.value.teacherPhone).then(() => {
      ElMessage.success('电话号码已复制到剪贴板');
    }).catch(() => {
      ElMessage.error('复制失败，请手动复制');
    });
  }
};

// 处理卡片点击
const handleCardClick = () => {
  emit('select', courseData.value);
};

// 检测班级标签是否溢出
const groupsTagsRef = ref<HTMLElement | null>(null);
const isGroupsOverflow = ref(false);

onMounted(() => {
  checkGroupsOverflow();
  window.addEventListener('resize', checkGroupsOverflow);
});

onUnmounted(() => {
  window.removeEventListener('resize', checkGroupsOverflow);
});

const checkGroupsOverflow = () => {
  if (!groupsTagsRef.value) return;

  const el = groupsTagsRef.value;
  isGroupsOverflow.value = el.scrollHeight > el.clientHeight;
};

// 使用 getCurrentInstance 判断事件监听是否存在
const instance = getCurrentInstance();
const hasListener = (event: string) => {
  return !!instance?.vnode.props?.[`on${event.charAt(0).toUpperCase() + event.slice(1)}`];
};

// 查看详情
const viewDetails = () => {
  emit('view-details', courseData.value);
};
</script>

<style scoped lang="scss">
.course-card {
  position: relative;
  padding: 24px;
  min-height: 300px;
  border-radius: 18px;
  background: linear-gradient(140deg, rgba(255, 255, 255, 0.95) 0%, rgba(250, 250, 255, 0.95) 100%);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  border: 1px solid rgba(0, 0, 0, 0.03);
  margin-bottom: 32px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  width: 100%;

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--oj-shadow-hover);

    .card-actions {
      opacity: 1;
    }
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: v-bind(primaryGradient);
    background-size: 200% 200%;
    animation: gradient-shimmer 8s ease infinite;
  }

  &.is-selected {
    border-color: v-bind(themePrimary) !important;
    transform: translateY(-2px);
    box-shadow: var(--oj-shadow-lg);

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      background: rgba(255, 255, 255, 0.1);
      z-index: 0;
    }
  }
}

.select-checkbox {
  position: absolute;
  top: 12px;
  left: 12px;
  width: 18px;
  height: 18px;
  background: v-bind(themePrimary);
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
  z-index: 1;

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
      font-size: 14px;
      display: flex;
      align-items: center;

      .phone-number {
        font-size: 13px;
        color: #606266;
      }

      .copy-phone-btn {
        margin-left: 8px;
        background: transparent;
        border: none;
        padding: 2px;
        border-radius: 4px;
        cursor: pointer;
        color: #909399;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;

        &:hover {
          background-color: rgba(64, 158, 255, 0.1);
          color: #409EFF;
        }
      }
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

    .description-wrapper {
      position: relative;
    }

    .description-text {
      margin: 0;
      font-size: 13px;
      color: #606266;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      min-height: 39px; /* 固定高度为2行 */
    }
  }

  .groups-container {
    background-color: rgba(255, 255, 255, 0.5);
    padding: 10px 12px;
    border-radius: 10px;
    margin-bottom: 10px;
    transition: all 0.3s ease;

    &:hover {
      background-color: rgba(255, 255, 255, 0.8);
    }

    .groups-header {
      font-weight: 500;
      color: #555;
      margin-bottom: 6px;
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
      display: flex;
      align-items: center;
      font-size: 14px;
    }

    .groups-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      max-height: 52px;
      overflow: hidden;
      position: relative;

      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        right: 0;
        height: 20px;
        width: 40px;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8));
        display: none;
      }

      &.overflow::after {
        display: block;
      }

      .group-tag {
        font-size: 12px;
        padding: 2px 8px;
        border-radius: 4px;
        margin-bottom: 4px;
        white-space: nowrap;
      }
    }
  }
}

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
  position: relative;
  z-index: 3;

  .actions-container {
    display: flex;
    gap: 10px;
    width: 100%;

    .action-button {
      flex: 1;
      height: 38px;
      border-radius: 19px;
      font-size: 14px;
      font-weight: 600;
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
      
      &:hover {
        transform: translateY(-2px);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
    
    .view-details-button {
      background-color: rgba(var(--el-color-primary-rgb), 0.1);
      color: var(--el-color-primary);
      border: 1px solid rgba(var(--el-color-primary-rgb), 0.2);
      
      &:hover {
        background-color: rgba(var(--el-color-primary-rgb), 0.15);
        box-shadow: 0 4px 8px rgba(var(--el-color-primary-rgb), 0.15);
      }
    }
    
    .start-course-button {
      background: v-bind(primaryGradient);
      color: white;
      border: none;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);

      &.disabled {
        background: #c0c0c0;
        cursor: pointer;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
      }

      &:hover {
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
        filter: brightness(1.05);

        &.disabled {
          filter: brightness(1.05);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }

  .other-actions {
    display: flex;
    justify-content: center;
    gap: 14px;
    margin-top: 4px;

    .action-icon {
      width: 28px;
      height: 28px;
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
        color: v-bind(themePrimary);
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

@keyframes gradient-shimmer {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

// 深色模式适配 (使用 html.dark 而非 prefers-color-scheme)
:global(html.dark) .course-card {
    background-color: rgba(30, 30, 32, 0.7);
    border-color: rgba(255, 255, 255, 0.05);

    .card-header .card-title {
      color: #f5f5f7;
    }

    .card-content {
      .info-row, .time-info-container, .description-container, .groups-container {
        background-color: rgba(50, 50, 54, 0.5);

        &:hover {
          background-color: rgba(60, 60, 64, 0.7);
        }
      }

      .info-label, .time-info-header, .description-header, .groups-header {
        color: #aaa;
      }

      .info-value, .time-value, .description-text {
        color: #f5f5f7;
      }

      .time-label {
        color: #aaa !important;
      }

      .icon-wrapper .icon {
        filter: brightness(0) invert(0.8);
      }

      .groups-tags::after {
        background: linear-gradient(90deg, transparent, rgba(60, 60, 64, 0.7));
      }
    }

    .card-actions {
      .actions-container {
        .view-details-button {
          background-color: rgba(var(--el-color-primary-rgb), 0.15);
          border-color: rgba(var(--el-color-primary-rgb), 0.3);
          
          &:hover {
            background-color: rgba(var(--el-color-primary-rgb), 0.25);
          }
        }
        
        .start-course-button {
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
          
          &.disabled {
            background-color: #444;
          }
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
</style>
