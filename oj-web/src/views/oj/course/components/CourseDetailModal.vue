<template>
  <el-dialog
    v-model="visible"
    :title="course?.name || '课程详情'"
    :destroy-on-close="true"
    width="780px"
    class="course-detail-dialog"
    draggable
  >
    <div class="course-detail-container">
      <!-- 课程标题和状态 -->
      <div class="course-header">
        <div class="course-status">
          <div class="status-badge" :class="getStatusClass">
            {{ getStatusText }}
          </div>
        </div>
      </div>

      <!-- 课程信息区域 -->
      <div class="course-info-section">
        <div class="info-row">
          <div class="info-icon-wrapper teacher-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="info-content">
            <div class="info-label">授课老师</div>
            <div class="info-value">{{ course?.teacherName || '暂无' }}</div>
          </div>
        </div>

        <div class="info-row" v-if="course?.teacherPhone">
          <div class="info-icon-wrapper phone-icon">
            <el-icon><Phone /></el-icon>
          </div>
          <div class="info-content">
            <div class="info-label">联系方式</div>
            <div class="info-value">
              {{ course?.teacherPhone }}
              <el-button
                link
                type="primary"
                size="small"
                @click="copyPhoneNumber"
                class="copy-btn"
              >
                <el-icon><CopyDocument /></el-icon>
                复制
              </el-button>
            </div>
          </div>
        </div>

        <div class="info-row">
          <div class="info-icon-wrapper time-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="info-content">
            <div class="info-label">课程时间</div>
            <div class="info-value time-value">
              <template v-if="course?.startTime && course?.endTime">
                <div class="time-item">
                  <span class="time-label">开始：</span>
                  <span class="time-text">{{ formatDateTime(course.startTime) }}</span>
                </div>
                <div class="time-item">
                  <span class="time-label">结束：</span>
                  <span class="time-text">{{ formatDateTime(course.endTime) }}</span>
                </div>
              </template>
              <template v-else>
                <div class="no-time">未设置课程时间</div>
              </template>
            </div>
          </div>
        </div>

        <div class="info-row" v-if="course?.url">
          <div class="info-icon-wrapper link-icon">
            <el-icon><Link /></el-icon>
          </div>
          <div class="info-content">
            <div class="info-label">课程链接</div>
            <div class="info-value">
              <a
                :href="course.url"
                target="_blank"
                rel="noopener noreferrer"
                class="course-link"
              >
                {{ course.url }}
                <el-icon class="external-icon"><Right /></el-icon>
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程内容 -->
      <div class="course-content-section">
        <div class="section-title">
          <el-icon><Notebook /></el-icon>
          <span>课程内容</span>
        </div>
        <div class="markdown-content">
          <md-preview v-if="course?.content" :modelValue="course.content" />
          <div v-else class="no-content">
            <el-empty description="暂无课程内容" :image-size="100" />
          </div>
        </div>
      </div>

      <!-- 班级信息 -->
      <div class="course-groups-section" v-if="course?.groups && course.groups.length > 0">
        <div class="section-title">
          <el-icon><OfficeBuilding /></el-icon>
          <span>所属班级</span>
        </div>
        <div class="groups-list">
          <el-tag
            v-for="group in course.groups"
            :key="group.id"
            class="group-tag"
            :style="{
              borderColor: group.color || '#e9e9eb',
              color: group.color || '#606266',
            }"
            effect="plain"
          >
            {{ group.name }}
          </el-tag>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
        <el-button
          type="primary"
          @click="startCourse"
          :disabled="!isCourseTime || !course?.url"
        >
          立即上课
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { User, Phone, Clock, Link, Notebook, OfficeBuilding, CopyDocument, Right } from '@element-plus/icons-vue';
import type { ICourse } from '@/api/interface/oj/course/course';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const props = defineProps<{
  modelValue: boolean;
  course: ICourse.Row | null;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'start-course'): void;
}>();

// 控制弹窗显示
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

// 获取课程状态
const getStatusClass = computed(() => {
  if (!props.course?.startTime || !props.course?.endTime) return 'status-unknown';

  const now = new Date().getTime();
  const startTime = new Date(props.course.startTime).getTime();
  const endTime = new Date(props.course.endTime).getTime();

  if (now < startTime) return 'status-upcoming';
  if (now > endTime) return 'status-ended';
  return 'status-ongoing';
});

const getStatusText = computed(() => {
  if (!props.course?.startTime || !props.course?.endTime) return '未设置时间';

  const now = new Date().getTime();
  const startTime = new Date(props.course.startTime).getTime();
  const endTime = new Date(props.course.endTime).getTime();

  if (now < startTime) return '即将开始';
  if (now > endTime) return '已结束';
  return '进行中';
});

// 是否在课程时间
const isCourseTime = computed(() => {
  if (!props.course?.startTime || !props.course?.endTime) return false;

  const now = new Date().getTime();
  const startTime = new Date(props.course.startTime).getTime();
  const endTime = new Date(props.course.endTime).getTime();

  // 课程开始前30分钟内，或者课程进行中
  return (now >= startTime - 30 * 60 * 1000 && now <= endTime);
});

// 格式化日期时间
const formatDateTime = (dateString: string) => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${year}年${month}月${day}日 ${hours}:${minutes}`;
};

// 复制手机号
const copyPhoneNumber = () => {
  if (props.course?.teacherPhone) {
    navigator.clipboard.writeText(props.course.teacherPhone)
      .then(() => {
        ElMessage.success('电话号码已复制到剪贴板');
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动复制');
      });
  }
};

// 开始上课
const startCourse = () => {
  if (!props.course?.url) {
    ElMessage.warning('该课程没有设置课程链接，无法开始上课');
    return;
  }

  window.open(props.course.url, '_blank');
  ElMessage.success(`已开始【${props.course.name}】课程`);
  emit('start-course');
};
</script>

<style scoped lang="scss">
.course-detail-dialog {
  :deep(.el-dialog) {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1), 0 6px 12px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(10px);
    
    .el-dialog__header {
      padding: 16px 24px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      background: linear-gradient(135deg, rgba(var(--el-color-primary-rgb), 0.03), rgba(var(--el-color-primary-rgb), 0.01));
      
      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: #1d1d1f;
        font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
      }
    }
    
    .el-dialog__body {
      padding: 0;
    }
    
    .el-dialog__footer {
      padding: 16px 24px;
      border-top: 1px solid rgba(0, 0, 0, 0.05);
    }
  }
}

.course-detail-container {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
  color: #1d1d1f;
  
  .course-header {
    display: flex;
    justify-content: flex-end;
    padding: 16px 24px 0;
    
    .course-status {
      .status-badge {
        display: inline-flex;
        align-items: center;
        padding: 4px 12px;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 600;
        
        &::before {
          content: '';
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          margin-right: 6px;
        }
        
        &.status-upcoming {
          background-color: rgba(255, 149, 0, 0.1);
          color: #ff9500;
          
          &::before {
            background-color: #ff9500;
            animation: pulse 1.5s infinite;
          }
        }
        
        &.status-ongoing {
          background-color: rgba(52, 199, 89, 0.1);
          color: #34c759;
          
          &::before {
            background-color: #34c759;
          }
        }
        
        &.status-ended, &.status-unknown {
          background-color: rgba(142, 142, 147, 0.1);
          color: #8e8e93;
          
          &::before {
            background-color: #8e8e93;
          }
        }
      }
    }
  }
  
  .course-info-section {
    padding: 12px 24px 20px;
    display: flex;
    flex-direction: column;
    gap: 14px;
    
    .info-row {
      display: flex;
      align-items: flex-start;
      
      .info-icon-wrapper {
        width: 36px;
        height: 36px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 14px;
        color: white;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
        
        &.teacher-icon {
          background: linear-gradient(135deg, #5856d6, #7773ff);
        }
        
        &.phone-icon {
          background: linear-gradient(135deg, #ff9500, #ffbd52);
        }
        
        &.time-icon {
          background: linear-gradient(135deg, #ff2d55, #ff617e);
        }
        
        &.link-icon {
          background: linear-gradient(135deg, #32d74b, #64e778);
        }
        
        .el-icon {
          font-size: 18px;
        }
      }
      
      .info-content {
        flex: 1;
        
        .info-label {
          font-size: 13px;
          color: #8e8e93;
          margin-bottom: 4px;
        }
        
        .info-value {
          font-size: 15px;
          color: #1d1d1f;
          
          &.time-value {
            display: flex;
            flex-direction: column;
            gap: 4px;
            
            .time-item {
              display: flex;
              align-items: center;
              
              .time-label {
                color: #8e8e93;
                width: 45px;
              }
              
              .time-text {
                color: #1d1d1f;
              }
            }
            
            .no-time {
              color: #8e8e93;
              font-style: italic;
            }
          }
          
          .copy-btn {
            margin-left: 8px;
            font-size: 12px;
            
            .el-icon {
              margin-right: 4px;
            }
          }
          
          .course-link {
            color: var(--el-color-primary);
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            transition: all 0.3s ease;
            
            &:hover {
              text-decoration: underline;
            }
            
            .external-icon {
              font-size: 12px;
              margin-left: 4px;
            }
          }
        }
      }
    }
  }
  
  .course-content-section, .course-groups-section {
    padding: 20px 24px;
    
    &:not(:last-child) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    }
    
    .section-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 16px;
      color: #1d1d1f;
      
      .el-icon {
        margin-right: 8px;
        color: var(--el-color-primary);
      }
    }
    
    .markdown-content {
      background-color: rgba(var(--el-color-primary-rgb), 0.02);
      border-radius: 12px;
      padding: 16px;
      min-height: 100px;
      max-height: 400px;
      overflow-y: auto;
      
      .no-content {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100px;
      }
    }
    
    .groups-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      .group-tag {
        padding: 6px 12px;
        border-radius: 8px;
        font-size: 13px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  .el-button {
    border-radius: 8px;
    font-weight: 500;
    
    &--primary {
      box-shadow: 0 2px 6px rgba(var(--el-color-primary-rgb), 0.2);
    }
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    opacity: 0.8;
  }
  70% {
    transform: scale(1);
    opacity: 0.3;
  }
  100% {
    transform: scale(0.95);
    opacity: 0.8;
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .course-detail-dialog {
    :deep(.el-dialog) {
      background-color: #1e1e1e;
      border: 1px solid rgba(255, 255, 255, 0.08);
      
      .el-dialog__header {
        background: linear-gradient(135deg, rgba(var(--el-color-primary-rgb), 0.1), rgba(var(--el-color-primary-rgb), 0.05));
        border-bottom-color: rgba(255, 255, 255, 0.08);
        
        .el-dialog__title {
          color: #f5f5f7;
        }
      }
      
      .el-dialog__footer {
        border-top-color: rgba(255, 255, 255, 0.08);
      }
    }
  }
  
  .course-detail-container {
    color: #f5f5f7;
    
    .course-info-section {
      .info-row {
        .info-content {
          .info-label {
            color: #8e8e93;
          }
          
          .info-value {
            color: #f5f5f7;
            
            .time-item {
              .time-text {
                color: #f5f5f7;
              }
            }
          }
        }
      }
    }
    
    .course-content-section, .course-groups-section {
      &:not(:last-child) {
        border-bottom-color: rgba(255, 255, 255, 0.08);
      }
      
      .section-title {
        color: #f5f5f7;
      }
      
      .markdown-content {
        background-color: rgba(255, 255, 255, 0.05);
      }
    }
  }
}
</style> 