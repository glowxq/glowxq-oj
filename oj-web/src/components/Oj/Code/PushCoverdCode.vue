<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="80%"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    destroy-on-close
  >
    <div class="push-code-container">
      <!-- 顶部信息 -->
      <div class="header-info">
        <div class="user-info">
          <el-avatar :size="32" :style="{ backgroundColor: '#409EFF' }">
            {{ row.monitorName?.charAt(0) || '?' }}
          </el-avatar>
          <div class="user-detail">
            <div class="user-name">{{ row.monitorName }}</div>
            <div class="user-phone">{{ row.monitorPhone }}</div>
          </div>
        </div>
        <div class="code-info">
          <el-tag size="small" type="info">
            <el-icon><Document /></el-icon>
            代码模式：{{ row.codeMode }}
          </el-tag>
          <el-tag size="small" type="info">
            <el-icon><Monitor /></el-icon>
            监控状态：{{ row.monitorStatus }}
          </el-tag>
        </div>
      </div>

      <!-- 代码对比区域 -->
      <div class="code-comparison">
        <!-- 现有代码 -->
        <div class="code-section existing-code">
          <div class="section-header">
            <h3>现有代码</h3>
          </div>
          <div class="code-content">
            <code-editor
              v-model="row.monitorCode"
              :language="row.language"
              :read-only-prop="true"
              :default-code-mode="row.codeMode"
              style="width: 100%; height: 100%;"
            />
          </div>
        </div>

        <!-- 代码编辑器 -->
        <div class="code-section push-code">
          <div class="section-header">
            <h3>推送代码</h3>
          </div>
          <div class="code-content">
            <code-editor
              v-model="pushCode"
              :language="row.language"
              :default-code-mode="row.codeMode"
              style="width: 100%; height: 100%;"
            />
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePush" :loading="loading" class="push-button">
          立即推送
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Document, Monitor } from '@element-plus/icons-vue'
import type { ICodeMonitor } from '@/api/interface/oj/code/codeMonitor'
import { coveredPushApi } from '@/api/modules/oj/code/codeMonitor'
import CodeEditor from '@/components/Common/CodeEditor/CodeEditor.vue'

const dialogVisible = ref(false)
const loading = ref(false)
const title = ref('推送覆盖代码')
const row = ref<ICodeMonitor.Row>({})
const pushCode = ref('')

// 打开弹窗
const open = (data: ICodeMonitor.Row) => {
  row.value = data
  pushCode.value = data.monitorCode || ''
  dialogVisible.value = true
}

// 推送代码
const handlePush = async () => {
  if (!row.value.id) {
    ElMessage.error('推送失败：缺少必要参数')
    return
  }

  if (!pushCode.value) {
    ElMessage.warning('请输入要推送的代码')
    return
  }

  try {
    loading.value = true
    await coveredPushApi({
      id: row.value.id,
      overlayCode: pushCode.value
    })
    ElMessage.success('代码推送成功')
    dialogVisible.value = false
  } catch (error) {
    console.error('推送失败:', error)
    ElMessage.error('代码推送失败')
  } finally {
    loading.value = false
  }
}

defineExpose({
  open
})
</script>

<style scoped lang="scss">
.push-code-container {
  display: flex;
  flex-direction: column;
  height: 70vh;
  gap: 16px;
}

.header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);

  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-detail {
      .user-name {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin-bottom: 4px;
      }

      .user-phone {
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .code-info {
    display: flex;
    gap: 8px;

    .el-tag {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      padding: 6px 10px;
      border-radius: 6px;

      .el-icon {
        font-size: 14px;
      }
    }
  }
}

.code-comparison {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.code-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
  overflow: hidden;

  .section-header {
    padding: 12px 16px;
    border-bottom: 1px solid var(--el-border-color-light);
    background-color: var(--el-fill-color-light);

    h3 {
      margin: 0;
      font-size: 14px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .code-content {
    flex: 1;
    min-height: 0;
    overflow: hidden;
  }

  &.existing-code {
    border-color: var(--el-border-color);
    
    .section-header {
      background-color: #f5f7fa;
      border-bottom: 1px solid var(--el-border-color);
      
      h3 {
        color: var(--el-text-color-secondary);
        &::before {
          content: '';
          display: inline-block;
          width: 4px;
          height: 14px;
          background-color: var(--el-color-info);
          margin-right: 8px;
          vertical-align: middle;
          border-radius: 2px;
        }
      }
    }
  }
  
  &.push-code {
    border-color: var(--el-color-primary-light-5);
    box-shadow: 0 0 10px rgba(var(--el-color-primary-rgb), 0.1);
    
    .section-header {
      background: linear-gradient(to right, rgba(var(--el-color-primary-rgb), 0.05), transparent);
      border-bottom: 1px solid var(--el-color-primary-light-5);
      
      h3 {
        color: var(--el-color-primary);
        font-weight: 600;
        &::before {
          content: '';
          display: inline-block;
          width: 4px;
          height: 14px;
          background-color: var(--el-color-primary);
          margin-right: 8px;
          vertical-align: middle;
          border-radius: 2px;
        }
      }
    }
  }
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 16px 20px;
  border-top: 1px solid var(--el-border-color-light);
}

/* 推送按钮样式 */
.push-button {
  padding: 10px 24px;
  font-weight: 600;
  font-size: 15px;
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.3);
  border-radius: 6px;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(var(--el-color-primary-rgb), 0.4);
  }
}
</style>
