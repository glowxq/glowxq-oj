<template>
  <div class="judge-container">
    <ProTable
      ref="proTableRef"
      title="测评记录"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
      class="judge-table"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <div class="table-header-buttons">
          <el-button 
            v-auth="'judge.create'" 
            :icon="CirclePlus" 
            type="primary" 
            @click="handleAdd"
          >
            新增
          </el-button>
          <el-button
            v-auth="'judge.remove'"
            :disabled="!scope.isSelected"
            :icon="Delete"
            plain
            type="danger"
            @click="handleBatchDelete(scope.selectedListIds)"
          >
            批量删除
          </el-button>
          <el-button
            v-auth="'judge.import'"
            :icon="Upload"
            plain
            type="primary"
            @click="handleImport"
          >
            导入
          </el-button>
          <el-button
            v-auth="'judge.export'"
            :icon="Download"
            plain
            type="primary"
            @click="handleExport"
          >
            导出
          </el-button>
        </div>
      </template>

      <!-- 表格操作列 -->
      <template #operation="{ row }">
        <el-button
          v-auth="'judge.update'"
          :icon="Check"
          link
          type="primary"
          @click="handleManualEvaluation(row)"
        >
          人工测评
        </el-button>
        <el-button
          v-auth="'judge.remove'"
          :icon="Delete"
          link
          type="primary"
          @click="handleDelete(row)"
        >
          删除
        </el-button>
        <el-button 
          :icon="View" 
          link 
          type="primary" 
          @click="handleViewDetail(row)"
        >
          测评详情
        </el-button>
      </template>
    </ProTable>

    <!-- 引入表单组件 -->
    <JudgeForm ref="formRef" />

    <!-- 导入组件 -->
    <ImportExcel ref="importExcelRef" />

    <!-- 测评详情弹窗 -->
    <JudgeDetailDialog ref="judgeDetailRef" />

    <!-- 人工测评弹窗 -->
    <ManualEvaluationDialog ref="manualEvaluationRef" @success="refresh" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h } from 'vue'
import { 
  CirclePlus, 
  Delete, 
  Upload, 
  Download, 
  View, 
  Check 
} from '@element-plus/icons-vue'
import { ElMessage, ElPopover, ElTag } from 'element-plus'
import ProTable from '@/components/Common/ProTable/index.vue'
import { useHandleData } from '@/hooks/useHandleData'
import { useDownload } from '@/hooks/useDownload'
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface'
import ImportExcel from '@/components/Common/ImportExcel/index.vue'
import { downloadTemplate } from '@/api/modules/system/admin/common'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import BlockButtonSwitch from '@/components/Common/Base/BlockButtonSwitch.vue'
import { JudgeSceneType, JudgeStatus } from '@/enums/oj/judge'
import { ProblemType } from '@/enums/oj/problem'
import JudgeStatusShow from '@/components/Oj/Judge/JudgeStatusShow.vue'
import JudgeDetailDialog from '@/components/Oj/Judge/JudgeDetailDialog.vue'
import JudgeReplyOptionsShow from '@/components/Oj/Judge/common/JudgeReplyOptionsShow.vue'
import ProblemIdGoTo from '@/components/Oj/Judge/common/ProblemIdGoTo.vue'
import CodeViewButton from '@/components/Oj/Judge/common/CodeViewButton.vue'
import JudgeForm from './components/JudgeForm.vue'
import ManualEvaluationDialog from '@/components/Oj/Judge/ManualEvaluationDialog.vue'
import {
  createJudgeApi,
  removeJudgeApi,
  updateJudgeApi,
  getJudgeListApi,
  getJudgeDetailApi,
  importJudgeExcelApi,
  exportJudgeExcelApi,
} from '@/api/modules/oj/judge/judge'

defineOptions({
  name: 'JudgeView'
})

// 引用实例
const proTableRef = ref<ProTableInstance>()
const formRef = ref()
const importExcelRef = ref()
const judgeDetailRef = ref()
const manualEvaluationRef = ref()

// 表格配置项
const columns: ColumnProps<any>[] = [
  { type: 'selection', width: 50 },
  {
    prop: 'id',
    label: 'ID',
    width: 70,
    render: scope => {
      return h('div', { class: 'key-value non-functional-key' }, scope.row.id)
    }
  },
  {
    prop: 'problemId',
    label: '题目ID',
    width: 140,
    render: scope => {
      return h(ProblemIdGoTo, { problemId: scope.row.problemId })
    }
  },
  {
    prop: 'problemKey',
    label: '题目Key',
    width: 120,
    render: scope => {
      return h('div', { class: 'key-value non-functional-key' }, scope.row.problemKey)
    }
  },
  {
    prop: 'problemTitle',
    label: '题目标题',
    width: 250,
    render: scope => {
      return h('div', { class: 'key-value non-functional-key' }, scope.row.problemTitle)
    }
  },
  {
    prop: 'status',
    label: '状态',
    width: 200,
    render: scope => {
      return h('div', { class: 'enum-container' }, [
        h(JudgeStatusShow, {
          code: Number(scope.row.status),
          class: 'status-tag'
        })
      ])
    }
  },
  {
    prop: 'code',
    label: '代码',
    width: 100,
    render: scope => {
      if (!scope.row.code) {
        return h('span', { class: 'no-code' }, '无代码')
      }
      return h(CodeViewButton, {
        code: scope.row.code,
        language: scope.row.language || 'javascript'
      })
    }
  },
  {
    prop: 'errorMessage',
    label: '错误提醒',
    width: 180,
    showOverflowTooltip: true,
    render: scope => {
      if (!scope.row.errorMessage) return h('span', { class: 'no-error' }, '无错误')

      return h('div', { class: 'error-message-container' }, [
        h(
          ElPopover,
          {
            width: 400,
            trigger: 'hover',
            popperClass: 'error-message-popover'
          },
          {
            reference: () =>
              h('div', { class: 'error-summary' }, [
                h('i', { class: 'error-icon el-icon-error' }),
                h(
                  'span',
                  { class: 'error-text' },
                  scope.row.errorMessage.slice(0, 20) + (scope.row.errorMessage.length > 20 ? '...' : '')
                )
              ]),
            default: () => h('div', { class: 'error-message-content' }, scope.row.errorMessage)
          }
        )
      ])
    }
  },
  {
    prop: 'problemType',
    label: '题目类型',
    width: 100,
    render: scope => {
      return h('div', { class: 'enum-container' }, [
        h(EnumShow, {
          enum: ProblemType as any,
          code: scope.row.problemType || '',
          class: 'problem-type-enum enhanced-type-tag'
        })
      ])
    }
  },
  {
    prop: 'submitTime',
    label: '提交时间',
    width: 120,
    isShow: false,
    render: scope => {
      if (!scope.row.submitTime) return '-'

      try {
        const date = new Date(scope.row.submitTime)
        const month = (date.getMonth() + 1).toString().padStart(2, '0')
        const day = date.getDate().toString().padStart(2, '0')
        const hours = date.getHours().toString().padStart(2, '0')
        const minutes = date.getMinutes().toString().padStart(2, '0')

        return h('div', { class: 'time-display-wrapper' }, [
          h('span', { class: 'time-display' }, `${month}-${day} ${hours}:${minutes}`)
        ])
      } catch (e) {
        return scope.row.submitTime
      }
    }
  },
  {
    prop: 'time',
    label: '运行时间',
    width: 80,
    isShow: false,
    render: scope => {
      if (!scope.row.time && scope.row.time !== 0) return '-'

      let timeValue = scope.row.time
      let unit = 'ms'

      return h('div', { class: 'metric-display' }, [
        h('span', { class: 'metric-value' }, timeValue),
        h('span', { class: 'metric-unit' }, unit)
      ])
    }
  },
  {
    prop: 'memory',
    label: '内存',
    width: 80,
    isShow: false,
    render: scope => {
      if (!scope.row.memory && scope.row.memory !== 0) return '-'

      let memoryValue = scope.row.memory
      let unit = 'KB'

      return h('div', { class: 'metric-display' }, [
        h('span', { class: 'metric-value' }, memoryValue),
        h('span', { class: 'metric-unit' }, unit)
      ])
    }
  },
  {
    prop: 'sceneType',
    label: '测评场景',
    width: 100,
    render: scope => {
      return h('div', { class: 'enum-container' }, [
        h(EnumShow, { enum: JudgeSceneType as any, code: scope.row.sceneType, class: 'judge-scene-enum' })
      ])
    }
  },
  {
    prop: 'score',
    label: '分数',
    width: 80,
    isShow: true,
    render: scope => {
      if (scope.row.score === undefined || scope.row.score === null) return '-'

      let score = Number(scope.row.score)
      if (isNaN(score)) score = 0

      let scoreClass = ''
      if (score >= 80) {
        scoreClass = 'high-score'
      } else if (score >= 60) {
        scoreClass = 'medium-score'
      } else {
        scoreClass = 'low-score'
      }

      return h('div', { class: 'score-display' }, [h('span', { class: `score-value ${scoreClass}` }, scope.row.score)])
    }
  },
  {
    prop: 'flowImage',
    label: '流程图',
    width: 80,
    render: scope => {
      if (!scope.row.flowImage) return '无图片'

      return h('div', { class: 'image-container' }, [
        h(
          'a',
          {
            href: scope.row.flowImage,
            target: '_blank',
            class: 'flow-image-link'
          },
          [
            h(
              ElTag,
              {
                type: 'info',
                class: 'flow-image-tag',
                effect: 'plain'
              },
              ['查看图片', h(View, { class: 'view-icon', style: 'margin-left: 5px;' })]
            )
          ]
        )
      ])
    }
  },
  {
    prop: 'replyOptions',
    label: '客观题',
    width: 120,
    render: scope => {
      if (!scope.row.replyOptions) return h('span', { class: 'no-answer' }, '无作答')
      return h(JudgeReplyOptionsShow, { replyOptions: scope.row.replyOptions })
    }
  },
  {
    prop: 'language',
    label: '语言',
    width: 80
  },
  {
    prop: 'manualEvaluation',
    label: '人工评测',
    width: 100,
    render: scope => {
      const isManual = scope.row.manualEvaluation
      return h(
        ElTag,
        {
          type: isManual ? 'success' : 'info',
          effect: 'light',
          class: 'manual-tag'
        },
        () => (isManual ? '人工评测' : '自动评测')
      )
    }
  },
  {
    prop: 'operation',
    label: '操作',
    fixed: 'right',
    width: 300
  }
]

// 搜索条件项
const searchColumns: SearchProps[] = [
  {
    prop: 'status',
    label: '状态',
    render: (scope: any) => {
      return h(EnumSelect, {
        enumData: JudgeStatus as any,
        type: 'select',
        modelValue: typeof scope.searchParam.status === 'number' ? scope.searchParam.status : undefined,
        placeholder: '评测状态',
        clearable: true,
        'onUpdate:modelValue': (val: number) => {
          scope.searchParam.status = val
        }
      })
    }
  },
  {
    prop: 'problemType',
    label: '题型',
    render: (scope: any) => {
      return h(EnumSelect, {
        enumData: ProblemType as any,
        type: 'select',
        modelValue: scope.searchParam.problemType || '',
        placeholder: '题目类型',
        clearable: true,
        'onUpdate:modelValue': (val: string) => {
          scope.searchParam.problemType = val
        }
      })
    }
  },
  {
    prop: 'switchGroup',
    label: '筛选',
    render: (scope: any) => {
      return h('div', { class: 'filter-button-group' }, [
        h(BlockButtonSwitch, {
          modelValue: !!scope.searchParam.manualEvaluation,
          text: '人工测评',
          inactiveText: '筛选人工测评',
          'onUpdate:modelValue': (val: boolean | null) => {
            scope.searchParam.manualEvaluation = val
          }
        }),
        h(BlockButtonSwitch, {
          modelValue: !!scope.searchParam.myJudge,
          text: '我的测评',
          inactiveText: '筛选我的测评',
          'onUpdate:modelValue': (val: boolean | null) => {
            scope.searchParam.myJudge = val
          }
        })
      ])
    }
  },
  { prop: 'problemId', label: '题目ID', el: 'input', props: { placeholder: '题目ID' } },
  {
    prop: 'problemKey',
    label: '题目Key',
    el: 'input',
    props: { placeholder: '题目Key' }
  },
  {
    prop: 'problemTitle',
    label: '题目标题',
    el: 'input',
    props: {  placeholder: '题目标题' }
  },
  {
    prop: 'sceneType',
    label: '类型',
    render: (scope: any) => {
      return h(EnumSelect, {
        enumData: JudgeSceneType as any,
        type: 'select',
        modelValue: scope.searchParam.sceneType || '',
        placeholder: '测评类型',
        clearable: true,
        'onUpdate:modelValue': (val: string) => {
          scope.searchParam.sceneType = val
          scope.searchParam.businessId = undefined
        }
      })
    }
  }
]

// 处理数据请求
const getTableList = (params: Record<string, any>) => {
  const formattedParams = formatParams(params)
  return getJudgeListApi(formattedParams)
}

// 格式化参数
const formatParams = (params: Record<string, any>) => {
  let newParams = JSON.parse(JSON.stringify(params))
  const timeFields = ['submitTime', 'startTime', 'endTime', 'createTime', 'updateTime']

  timeFields.forEach(field => {
    if (newParams[field]) {
      newParams[`${field}Start`] = newParams[field][0]
      newParams[`${field}End`] = newParams[field][1]
      delete newParams[field]
    }
  })

  return newParams
}

// 刷新表格
const refresh = () => {
  proTableRef.value?.getTableList()
}

// 处理新增
const handleAdd = () => {
  if (formRef.value?.acceptParams) {
    const params = {
      title: '新增测评记录',
      row: {},
      api: createJudgeApi,
      getTableList: proTableRef.value?.getTableList
    }
    formRef.value.acceptParams(params)
  }
}

// 处理人工测评
const handleManualEvaluation = (row: any) => {
  if (manualEvaluationRef.value?.openDialog) {
    manualEvaluationRef.value.openDialog(row)
  }
}

// 处理删除
const handleDelete = async (row: any) => {
  await useHandleData(removeJudgeApi, { ids: [row.id] }, `删除【${row.id}】测评记录`)
  refresh()
}

// 处理批量删除
const handleBatchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeJudgeApi, { ids }, '删除所选测评记录')
  proTableRef.value?.clearSelection()
  refresh()
}

// 处理查看详情
const handleViewDetail = (row: any) => {
  if (judgeDetailRef.value?.openDialog) {
    getJudgeDetailApi({ id: row?.id })
      .then(res => {
        if (res?.data) {
          judgeDetailRef.value.openDialog(res.data)
        }
      })
      .catch(err => {
        console.error('获取测评详情失败', err)
      })
  }
}

// 处理导入
const handleImport = () => {
  if (importExcelRef.value?.acceptParams) {
    const params = {
      title: '测评记录',
      templateName: '测评记录',
      tempApi: downloadTemplate,
      importApi: importJudgeExcelApi,
      getTableList: proTableRef.value?.getTableList
    }
    importExcelRef.value.acceptParams(params)
  }
}

// 处理导出
const handleExport = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam || {})
  useDownload(exportJudgeExcelApi, '测评记录', newParams)
}
</script>

<style scoped lang="scss">
.judge-container {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.judge-table {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

/* 数据显示样式 */
.key-value {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Roboto Mono', monospace;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: center;
  
  &.non-functional-key {
    background-color: #f5f7fa;
    color: #606266;
    border: 1px solid #e0e6ed;
  }
}

.no-code,
.no-answer {
  color: #909399;
  font-style: italic;
  font-size: 13px;
}

.no-error {
  color: #67c23a;
  font-style: italic;
  font-size: 13px;
}

/* 错误信息和指标显示 */
.error-message-container {
  .error-summary {
    display: flex;
    align-items: center;
    cursor: pointer;

    .error-icon {
      color: #f56c6c;
      margin-right: 5px;
      font-size: 16px;
    }

    .error-text {
      color: #f56c6c;
      font-size: 12px;
    }
  }
}

.metric-display {
  display: flex;
  align-items: center;
  justify-content: flex-start;

  .metric-value {
    font-size: 13px;
    font-weight: 500;
    font-family: 'Roboto Mono', monospace;
    color: #606266;
  }

  .metric-unit {
    font-size: 11px;
    color: #909399;
    margin-left: 2px;
  }
}

/* 分数和时间显示 */
.score-display {
  display: flex;
  justify-content: center;

  .score-value {
    font-family: 'Roboto Mono', monospace;
    font-size: 14px;
    font-weight: 600;
    padding: 3px 10px;
    border-radius: 12px;
    display: inline-block;
    min-width: 40px;
    text-align: center;

    &.high-score {
      color: #fff;
      background-color: #67c23a;
      box-shadow: 0 2px 4px rgba(103, 194, 58, 0.3);
    }

    &.medium-score {
      color: #fff;
      background-color: #e6a23c;
      box-shadow: 0 2px 4px rgba(230, 162, 60, 0.3);
    }

    &.low-score {
      color: #fff;
      background-color: #f56c6c;
      box-shadow: 0 2px 4px rgba(245, 108, 108, 0.3);
    }
  }
}

.time-display-wrapper {
  .time-display {
    font-family: 'Roboto Mono', monospace;
    font-size: 13px;
    background-color: #f5f7fa;
    padding: 4px 8px;
    border-radius: 4px;
    color: #606266;
    display: inline-block;
    letter-spacing: 0.5px;
    border: 1px solid #ebeef5;
  }
}

/* 枚举和标签样式 */
.enum-container {
  display: inline-block;

  .scene-type-enum {
    background-color: rgba(25, 118, 210, 0.08);
    color: #1976d2;
    border-color: rgba(25, 118, 210, 0.5);
  }

  .problem-type-enum {
    background-color: rgba(46, 125, 50, 0.08);
    color: #2e7d32;
    border-color: rgba(46, 125, 50, 0.5);
  }

  .enhanced-type-tag {
    font-weight: 500;
    padding: 4px 12px;
    border-radius: 12px;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    font-size: 12px;
    display: inline-block;
    min-width: 70px;
    letter-spacing: 0.5px;
    transition: all 0.2s;
    border: 1px solid rgba(46, 125, 50, 0.2);

    &:hover {
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      transform: translateY(-1px);
    }
  }
}

.manual-tag {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
}

/* 通用工具类 */
.filter-button-group {
  display: flex;
  gap: 6px;
  align-items: center;
}

.ml-2 {
  margin-left: 6px;
}

/* 弹窗样式 */
:deep(.error-message-popover) {
  max-width: 500px !important;

  .error-message-content {
    white-space: pre-wrap;
    font-family: 'Roboto Mono', monospace;
    font-size: 12px;
    line-height: 1.5;
    background-color: #fef0f0;
    color: #f56c6c;
    padding: 8px;
    border-radius: 4px;
    max-height: 300px;
    overflow-y: auto;
  }
}

:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
  
  th {
    background-color: #f5f7fa;
    font-weight: bold;
  }
  
  .el-table__row:hover {
    background-color: #ecf5ff;
  }
}
</style>
