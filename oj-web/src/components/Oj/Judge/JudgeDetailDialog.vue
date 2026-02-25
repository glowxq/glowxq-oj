<template>
  <el-dialog
    v-model="visible"
    title="测评详情"
    width="800px"
    destroy-on-close
    draggable
    append-to-body
  >
    <div v-if="judgeDetail" class="judge-detail-container">
      <!-- 基本信息部分 -->
      <div class="base-info-section">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="测评ID">
            <div class="key-info non-functional-key">{{ judgeDetail.id }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="测评Key">
            <div class="key-info non-functional-key">{{ judgeDetail.judgeKey }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="题目ID">
            <ProblemIdGoTo :problem-id="judgeDetail.problemId" button-text="前往做题" />
          </el-descriptions-item>
          <el-descriptions-item label="题目Key">
            <div class="key-info non-functional-key">{{ judgeDetail.problemKey }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <judge-status-show :code="Number(judgeDetail.status)" />
          </el-descriptions-item>
          <el-descriptions-item label="题目类型">
            <enum-show :enum="ProblemType" :code="judgeDetail.problemType || ''" />
            <span v-if="isProgrammingProblem && judgeDetail.programType !== undefined" class="program-type-tag">
              <enum-show :enum="ProgramType" :code="judgeDetail.programType" />
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatTime(judgeDetail.submitTime) }}</el-descriptions-item>
          <el-descriptions-item label="分数">
            <span :class="getScoreClass(judgeDetail.score)">{{ judgeDetail.score || 0 }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="运行时间">{{ judgeDetail.time || 0 }} ms</el-descriptions-item>
          <el-descriptions-item label="内存">{{ judgeDetail.memory || 0 }} KB</el-descriptions-item>
          <el-descriptions-item label="人工评测">
            <el-tag :type="judgeDetail.manualEvaluation ? 'success' : 'info'">
              {{ judgeDetail.manualEvaluation ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="语言">{{ judgeDetail.language || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 错误信息显示 -->
      <div v-if="judgeDetail.errorMessage" class="error-message-section">
        <el-alert
          title="错误信息"
          type="error"
          :closable="false"
          show-icon
        >
          <div class="error-content">{{ judgeDetail.errorMessage }}</div>
        </el-alert>
      </div>

      <!-- 代码显示部分 - 仅对编程题显示 -->
      <div v-if="isProgrammingProblem && judgeDetail.code" class="code-section">
        <h3 class="section-title">代码</h3>
        <code-editor
          :model-value="judgeDetail.code"
          :read-only-prop="true"
          :language="judgeDetail.language || 'javascript'"
          style="height: 300px; margin-top: 10px;"
        />
      </div>

      <!-- 作答内容 - 对于选择题/填空题等 -->
      <div v-if="!isProgrammingProblem && judgeDetail.replyOptions" class="reply-section">
        <h3 class="section-title">作答内容</h3>
        <div class="reply-content">
          <judge-reply-options-show-direct :reply-options="judgeDetail.replyOptions" />
        </div>
      </div>

      <!-- 测评用例 - 根据题型不同显示不同内容 -->
      <div v-if="judgeDetail.judgeCaseList && judgeDetail.judgeCaseList.length > 0" class="cases-section">
        <h3 class="section-title">测评用例 ({{ judgeDetail.judgeCaseList.length }})</h3>

        <!-- 编程题的测评用例显示 -->
        <template v-if="isProgrammingProblem">
          <el-table :data="judgeDetail.judgeCaseList" border stripe style="width: 100%" size="small">
            <el-table-column prop="seq" label="序号" width="60" />
            <el-table-column prop="status" label="状态" width="200">
              <template #default="scope">
                <judge-status-show :code="Number(scope.row.status)" />
              </template>
            </el-table-column>
            <el-table-column prop="time" label="时间" width="80">
              <template #default="scope">
                {{ scope.row.time || 0 }} ms
              </template>
            </el-table-column>
            <el-table-column prop="memory" label="内存" width="80">
              <template #default="scope">
                {{ scope.row.memory || 0 }} KB
              </template>
            </el-table-column>
            <el-table-column prop="score" label="分数" width="70">
              <template #default="scope">
                <span :class="getScoreClass(scope.row.score)">{{ scope.row.score || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="输入数据">
              <template #default="scope">
                <el-popover
                  placement="top"
                  width="300"
                  trigger="click"
                  v-if="scope.row.inputData"
                >
                  <template #reference>
                    <el-button type="primary" link>查看输入</el-button>
                  </template>
                  <div class="case-data-content">{{ scope.row.inputData }}</div>
                </el-popover>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="期望输出">
              <template #default="scope">
                <el-popover
                  placement="top"
                  width="300"
                  trigger="click"
                  v-if="scope.row.outputData"
                >
                  <template #reference>
                    <el-button type="primary" link>查看输出</el-button>
                  </template>
                  <div class="case-data-content">{{ scope.row.outputData }}</div>
                </el-popover>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="实际输出">
              <template #default="scope">
                <el-popover
                  placement="top"
                  width="300"
                  trigger="click"
                  v-if="scope.row.userOutput"
                >
                  <template #reference>
                    <el-button type="primary" link>查看输出</el-button>
                  </template>
                  <div class="case-data-content">{{ scope.row.userOutput }}</div>
                </el-popover>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </template>

        <!-- 非编程题的测评用例显示 -->
        <template v-else>
          <el-table :data="judgeDetail.judgeCaseList" border stripe style="width: 100%" size="small">
            <el-table-column prop="seq" label="序号" width="60" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <judge-status-show :code="Number(scope.row.status)" />
              </template>
            </el-table-column>
            <el-table-column prop="score" label="分数" width="70">
              <template #default="scope">
                <span :class="getScoreClass(scope.row.score)">{{ scope.row.score || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="说明">
              <template #default="scope">
                <el-popover
                  placement="top"
                  width="300"
                  trigger="click"
                  v-if="scope.row.outputData"
                >
                  <template #reference>
                    <el-button type="primary" link>查看详情</el-button>
                  </template>
                  <div class="case-data-content">{{ scope.row.outputData }}</div>
                </el-popover>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="用户作答">
              <template #default="scope">
                <el-popover
                  placement="top"
                  width="300"
                  trigger="click"
                  v-if="scope.row.userOutput"
                >
                  <template #reference>
                    <el-button type="primary" link>查看作答</el-button>
                  </template>
                  <div class="case-data-content">{{ scope.row.userOutput }}</div>
                </el-popover>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </div>

      <!-- 流程图展示 -->
      <div v-if="judgeDetail.flowImage" class="flow-image-section">
        <h3 class="section-title">流程图</h3>
        <div class="image-preview">
          <el-image
            :src="judgeDetail.flowImage"
            :preview-src-list="[judgeDetail.flowImage]"
            fit="contain"
            style="max-height: 300px; width: 100%"
          />
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="closeDialog">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElImage, ElTag } from 'element-plus'
import { Link, Check } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import CodeEditor from '@/components/Common/CodeEditor/CodeEditor.vue'
import JudgeStatusShow from '@/components/Oj/Judge/JudgeStatusShow.vue'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import { ProblemType, ProgramType } from '@/enums/oj/problem'
import type { IJudge } from '@/api/interface/oj/judge/judge'
import type { IJudgeCase } from '@/api/interface/oj/judge/judgeCase'
import JudgeReplyOptionsShowDirect from './common/JudgeReplyOptionsShowDirect.vue'
import ProblemIdGoTo from './common/ProblemIdGoTo.vue'

const router = useRouter()
const visible = ref(false)
const judgeDetail = ref<IJudge.Row & { judgeCaseList?: IJudgeCase.Row[] }>()

// 计算属性：是否为编程题
const isProgrammingProblem = computed(() => {
  if (!judgeDetail.value || !judgeDetail.value.problemType) return false
  return judgeDetail.value.problemType === 'Programmer'
})

// 打开弹窗
const openDialog = (detail: any) => {
  judgeDetail.value = detail
  visible.value = true

  // 打印数据以便调试
  console.log('测评详情数据:', detail)
}

// 关闭弹窗
const closeDialog = () => {
  visible.value = false
}

// 格式化时间
const formatTime = (time: string | undefined) => {
  if (!time) return '-'

  try {
    const date = new Date(time)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (e) {
    return time
  }
}

// 根据分数获取样式类
const getScoreClass = (score: number | undefined) => {
  if (score === undefined || score === null) return ''

  // 确保转换为数字并处理NaN情况
  let scoreNum = Number(score)
  if (isNaN(scoreNum)) scoreNum = 0

  if (scoreNum >= 80) {
    return 'high-score'
  } else if (scoreNum >= 60) {
    return 'medium-score'
  } else {
    return 'low-score'
  }
}

// 解析作答内容
const parseReplyOptions = (replyOptions: string | undefined) => {
  if (!replyOptions) return []

  try {
    const parsed = typeof replyOptions === 'string'
      ? JSON.parse(replyOptions)
      : replyOptions

    if (Array.isArray(parsed)) {
      return parsed
    } else if (parsed && typeof parsed === 'object') {
      return [parsed]
    } else {
      return []
    }
  } catch (e) {
    console.error('解析作答内容失败', e)
    return []
  }
}

defineExpose({
  openDialog
})
</script>

<style scoped lang="scss">
.judge-detail-container {
  padding: 10px;

  .section-title {
    margin-top: 20px;
    margin-bottom: 10px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    padding-left: 10px;
    border-left: 3px solid #409EFF;
  }

  .base-info-section {
    margin-bottom: 15px;

    :deep(.el-descriptions__label) {
      font-weight: 600;
      background-color: #f5f7fa;
    }

    .key-info {
      display: inline-block;
      padding: 4px 8px;
      border-radius: 4px;
      font-family: 'Roboto Mono', monospace;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.5px;
    }

    .non-functional-key {
      background-color: #f5f7fa;
      color: #606266;
      border: 1px solid #e0e6ed;
    }

    .code-format {
      background-color: #f0f9ff;
      color: #0078d4;
      border: 1px solid #d0e7ff;
    }

    .key-format {
      background-color: #f6ffed;
      color: #52c41a;
      border: 1px solid #d9f7be;
    }

    .id-format {
      background-color: #e6f7ff;
      color: #1890ff;
      border: 1px solid #bae7ff;
      cursor: pointer;
      transition: all 0.3s ease;
      display: inline-flex;
      align-items: center;
      gap: 4px;
      position: relative;
      overflow: hidden;

      // 添加点击波纹效果
      &:after {
        content: '';
        display: block;
        position: absolute;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        pointer-events: none;
        background-image: radial-gradient(circle, rgba(255, 255, 255, 0.7) 10%, transparent 10.01%);
        background-repeat: no-repeat;
        background-position: 50%;
        transform: scale(10, 10);
        opacity: 0;
        transition: transform 0.3s, opacity 0.5s;
      }

      &:active:after {
        transform: scale(0, 0);
        opacity: 0.3;
        transition: 0s;
      }

      &:hover {
        background-color: #bae7ff;
        transform: translateY(-2px);
        box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
        color: #096dd9;
      }

      .problem-id-number {
        font-weight: 600;
      }

      .goto-text {
        font-size: 12px;
        margin-left: 6px;
        background-color: #1890ff;
        color: white;
        padding: 2px 6px;
        border-radius: 10px;
        transition: all 0.3s;
      }

      &:hover .goto-text {
        background-color: #096dd9;
      }
    }

    .problem-key-format {
      background-color: #f9f0ff;
      color: #722ed1;
      border: 1px solid #efdbff;
    }

    .program-type-tag {
      margin-left: 8px;
      padding: 2px 6px;
      background-color: #e6f7ff;
      border: 1px solid #91d5ff;
      border-radius: 4px;
      font-size: 12px;
    }
  }

  .error-message-section {
    margin: 15px 0;

    .error-content {
      padding: 8px;
      white-space: pre-wrap;
      font-family: 'Courier New', monospace;
      font-size: 13px;
      background-color: #fdf2f0;
      border-radius: 4px;
      color: #d63031;
      margin-top: 5px;
      max-height: 150px;
      overflow: auto;
    }
  }

  .code-section {
    margin: 15px 0;
  }

  .reply-section {
    margin: 15px 0;

    .reply-content {
      border: 1px solid #ebeef5;
      border-radius: 4px;
      padding: 10px;

      .reply-item {
        padding: 12px;
        background-color: #f8f9fa;
        border-radius: 4px;
        margin-bottom: 12px;
        border-left: 3px solid #dcdfe6;
        transition: all 0.3s ease;

        &:last-child {
          margin-bottom: 0;
        }

        &.selected-option {
          background-color: #f0f9eb;
          border-left: 3px solid #67C23A;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        }

        .option-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .option-key {
            font-weight: 600;
            color: #303133;
            display: flex;
            align-items: center;

            .option-key-text {
              margin-right: 8px;
            }

            .answer-tag {
              font-size: 11px;
              padding: 0 5px;
              height: 20px;
              line-height: 18px;
            }
          }

          .selected-icon {
            color: #67C23A;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }

        .option-content {
          color: #606266;
          background-color: white;
          padding: 10px;
          border-radius: 4px;
          border: 1px solid #EBEEF5;
          white-space: pre-wrap;
          line-height: 1.5;
        }
      }
    }
  }

  .cases-section {
    margin: 15px 0;

    .case-data-content {
      white-space: pre-wrap;
      font-family: 'Courier New', monospace;
      font-size: 13px;
      max-height: 300px;
      overflow: auto;
    }
  }

  .flow-image-section {
    margin: 15px 0;

    .image-preview {
      border: 1px solid #ebeef5;
      border-radius: 4px;
      padding: 10px;
      text-align: center;
    }
  }

  // 分数样式
  .high-score {
    color: #fff;
    background-color: #67C23A;
    padding: 2px 6px;
    border-radius: 10px;
    font-weight: 600;
  }

  .medium-score {
    color: #fff;
    background-color: #E6A23C;
    padding: 2px 6px;
    border-radius: 10px;
    font-weight: 600;
  }

  .low-score {
    color: #fff;
    background-color: #F56C6C;
    padding: 2px 6px;
    border-radius: 10px;
    font-weight: 600;
  }
}
</style>
