<template>
  <div class="judge-list-container">
    <ProTable
      ref="proTableRef"
      :columns="mergedColumns"
      :indent="20"
      :request-api="requestFunc"
      :search-col="{ xs: 24, sm: 12, md: 12, lg: 8, xl: 8 }"
      :search-columns="mergedSearchColumns"
      :table-layout="'auto'"
      :title="title"
      class="judge-table"
      row-key="id"
      search-label-width="10px"
      size="small"
      v-bind="tableProps"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <slot name="beforeTableHeader"></slot>
        <el-button v-if="showAdd" v-auth="'judge.create'" :icon="CirclePlus" size="small" type="primary" @click="handleAdd">
          新增
        </el-button>
        <el-button
          v-if="showBatchDelete"
          v-auth="'judge.remove'"
          :disabled="!scope.isSelected"
          :icon="Delete"
          plain
          size="small"
          type="danger"
          @click="handleBatchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-if="showImport"
          v-auth="'judge.import'"
          :icon="Upload"
          plain
          size="small"
          type="primary"
          @click="handleImport"
        >
          导入
        </el-button>
        <el-button
          v-if="showExport"
          v-auth="'judge.export'"
          :icon="Download"
          plain
          size="small"
          type="primary"
          @click="handleExport"
        >
          导出
        </el-button>
        <slot name="afterTableHeader"></slot>
      </template>

      <!-- 表格操作列 -->
      <template #operation="{ row }">
        <slot :row="row" name="beforeOperation"></slot>
        <el-button
          v-if="showEdit"
          v-auth="'judge.update'"
          :icon="Check"
          link
          size="small"
          type="primary"
          @click="handleManualEvaluation(row)"
        >
          人工测评
        </el-button>
        <el-button
          v-if="showDelete"
          v-auth="'judge.remove'"
          :icon="Delete"
          link
          size="small"
          type="primary"
          @click="handleDelete(row)"
        >
          删除
        </el-button>
        <!-- 查看详情按钮 - 新增功能 -->
        <el-button v-if="showDetail" :icon="View" link size="small" type="primary" @click="handleViewDetail(row)">
          测评详情
        </el-button>
        <slot :row="row" name="afterOperation"></slot>
      </template>
    </ProTable>

    <!-- 引入表单组件 -->
    <component :is="formComponent" v-if="formComponent" ref="formRef" v-bind="formProps" />

    <!-- 导入组件 -->
    <ImportExcel ref="importExcelRef" />

    <!-- 测评详情弹窗 -->
    <judge-detail-dialog ref="judgeDetailRef" />

    <!-- 人工测评弹窗 -->
    <manual-evaluation-dialog ref="manualEvaluationRef" @success="refresh" />
  </div>
</template>

<script lang="ts" setup>
import { computed, defineEmits, defineExpose, h, reactive, ref } from 'vue';
import { Check, CirclePlus, Delete, Download, Upload, View } from '@element-plus/icons-vue';
import ProTable from '@/components/Common/ProTable/index.vue';
import { useHandleData } from '@/hooks/useHandleData';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { useDownload } from '@/hooks/useDownload';
import { ElMessage, ElPopover, ElTag } from 'element-plus';
import EnumShow from '@/components/Common/Enum/EnumShow.vue';
import { JudgeSceneType, JudgeStatus } from '@/enums/oj/judge';
import { ProblemType } from '@/enums/oj/problem';
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue';
import JudgeStatusShow from '@/components/Oj/Judge/JudgeStatusShow.vue';
import BlockButtonSwitch from '@/components/Common/Base/BlockButtonSwitch.vue';
import JudgeDetailDialog from './JudgeDetailDialog.vue';
import ManualEvaluationDialog from './ManualEvaluationDialog.vue';
import JudgeReplyOptionsShow from './common/JudgeReplyOptionsShow.vue';
import ProblemIdGoTo from './common/ProblemIdGoTo.vue';
import CodeViewButton from './common/CodeViewButton.vue';

// 定义组件属性
interface Props {
  // 基础配置
  title?: string;
  columns?: ColumnProps<any>[];
  searchColumns?: SearchProps[];

  // API配置
  requestApi?: (params: any) => Promise<any>;
  createApi?: (params: any) => Promise<any>;
  removeApi?: (params: any) => Promise<any>;
  updateApi?: (params: any) => Promise<any>;
  getDetailApi?: (params: any) => Promise<any>;
  importApi?: (params: any) => Promise<any>;
  exportApi?: (params: any) => Promise<any>;

  // 权限配置
  authConfig?: {
    create: string;
    update: string;
    remove: string;
    import: string;
    export: string;
  };

  // 功能控制
  showAdd?: boolean;
  showEdit?: boolean;
  showDelete?: boolean;
  showDetail?: boolean;
  showBatchDelete?: boolean;
  showImport?: boolean;
  showExport?: boolean;

  // 组件配置
  formComponent?: any;
  formProps?: Record<string, any>;
  tableProps?: Record<string, any>;

  // 自定义函数
  formatParams?: (params: Record<string, any>) => any;
  onSuccess?: (type: string, data?: any) => void;
}

// 默认属性
const props = withDefaults(defineProps<Props>(), {
  title: '测评记录',
  authConfig: () => ({
    create: 'judge.create',
    update: 'judge.update',
    remove: 'judge.remove',
    import: 'judge.import',
    export: 'judge.export'
  }),
  showAdd: true,
  showEdit: true,
  showDelete: true,
  showDetail: true,
  showBatchDelete: true,
  showImport: true,
  showExport: true,
  formatParams: (params: Record<string, any>) => params,
  tableProps: () => ({})
});

// 事件
const emit = defineEmits<{
  refresh: [];
  add: [row?: any];
  edit: [row: any];
  delete: [row: any];
  'batch-delete': [ids: (string | number)[]];
  view: [row: any];
  export: [];
  import: [];
}>();

// 引用实例
const proTableRef = ref<ProTableInstance>();
const formRef = ref();
const importExcelRef = ref();
const judgeDetailRef = ref();
const manualEvaluationRef = ref();

// 合并列配置（用户自定义 + 默认配置）
const mergedColumns = computed(() => {
  // 默认列配置
  const defaultColumns: ColumnProps<any>[] = [
    { type: 'selection', width: 50 },
    {
      prop: 'id',
      label: '测评ID',
      width: 70,
      render: scope => {
        return h('div', { class: 'key-value non-functional-key' }, scope.row.id);
      }
    },
    {
      prop: 'problemId',
      label: '题目ID',
      width: 140,
      render: scope => {
        return h(ProblemIdGoTo, { problemId: scope.row.problemId });
      }
    },
    {
      prop: 'problemKey',
      label: '题目Key',
      width: 80,
      render: scope => {
        return h('div', { class: 'key-value non-functional-key' }, scope.row.problemKey);
      }
    },
    {
      prop: 'problemTitle',
      label: '题目标题',
      width: 250,
      render: scope => {
        return h('div', { class: 'key-value non-functional-key' }, scope.row.problemTitle);
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
        ]);
      }
    },
    {
      prop: 'code',
      label: '代码',
      width: 100,
      render: scope => {
        if (!scope.row.code) {
          return h('span', { class: 'no-code' }, '无代码');
        }
        return h(CodeViewButton, {
          code: scope.row.code,
          language: scope.row.language || 'javascript'
        });
      }
    },
    {
      prop: 'errorMessage',
      label: '错误提醒',
      width: 180,
      showOverflowTooltip: true,
      render: scope => {
        if (!scope.row.errorMessage) return h('span', { class: 'no-error' }, '无错误');

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
        ]);
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
        ]);
      }
    },
    {
      prop: 'submitTime',
      label: '提交时间',
      width: 120,
      isShow: false,
      render: scope => {
        if (!scope.row.submitTime) return '-';

        try {
          const date = new Date(scope.row.submitTime);
          const month = (date.getMonth() + 1).toString().padStart(2, '0');
          const day = date.getDate().toString().padStart(2, '0');
          const hours = date.getHours().toString().padStart(2, '0');
          const minutes = date.getMinutes().toString().padStart(2, '0');

          return h('div', { class: 'time-display-wrapper' }, [
            h('span', { class: 'time-display' }, `${month}-${day} ${hours}:${minutes}`)
          ]);
        } catch (e) {
          return scope.row.submitTime;
        }
      }
    },
    {
      prop: 'time',
      label: '运行时间',
      width: 80,
      isShow: false,
      render: scope => {
        if (!scope.row.time && scope.row.time !== 0) return '-';

        let timeValue = scope.row.time;
        let unit = 'ms';

        return h('div', { class: 'metric-display' }, [
          h('span', { class: 'metric-value' }, timeValue),
          h('span', { class: 'metric-unit' }, unit)
        ]);
      }
    },
    {
      prop: 'memory',
      label: '内存',
      width: 80,
      isShow: false,
      render: scope => {
        if (!scope.row.memory && scope.row.memory !== 0) return '-';

        let memoryValue = scope.row.memory;
        let unit = 'KB';

        return h('div', { class: 'metric-display' }, [
          h('span', { class: 'metric-value' }, memoryValue),
          h('span', { class: 'metric-unit' }, unit)
        ]);
      }
    },
    {
      prop: 'sceneType',
      label: '测评场景',
      width: 100,
      render: scope => {
        return h('div', { class: 'enum-container' }, [
          h(EnumShow, { enum: JudgeSceneType as any, code: scope.row.sceneType, class: 'judge-scene-enum' })
        ]);
      }
    },
    {
      prop: 'score',
      label: '分数',
      width: 80,
      isShow: true,
      render: scope => {
        if (scope.row.score === undefined || scope.row.score === null) return '-';

        // 确保转换为数字并处理NaN情况
        let score = Number(scope.row.score);
        if (isNaN(score)) score = 0;

        let scoreClass = '';
        if (score >= 80) {
          scoreClass = 'high-score';
        } else if (score >= 60) {
          scoreClass = 'medium-score';
        } else {
          scoreClass = 'low-score';
        }

        return h('div', { class: 'score-display' }, [h('span', { class: `score-value ${scoreClass}` }, scope.row.score)]);
      }
    },
    {
      prop: 'flowImage',
      label: '流程图',
      width: 80,
      render: scope => {
        if (!scope.row.flowImage) return '无图片';

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
        ]);
      }
    },
    {
      prop: 'replyOptions',
      label: '客观题',
      width: 120,
      render: scope => {
        if (!scope.row.replyOptions) return h('span', { class: 'no-answer' }, '无作答');
        return h(JudgeReplyOptionsShow, { replyOptions: scope.row.replyOptions });
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
      width: 90,
      render: scope => {
        const isManual = scope.row.manualEvaluation;
        return h(
          ElTag,
          {
            type: isManual ? 'success' : 'info',
            effect: 'light',
            class: 'manual-tag'
          },
          () => (isManual ? '人工评测' : '自动评测')
        );
      }
    },
    {
      prop: 'operation',
      label: '操作',
      fixed: 'right',
      width: 300
    }
  ];

  // 如果用户提供了自定义列，则用自定义列替换默认列
  if (props.columns && props.columns.length > 0) {
    return props.columns;
  }

  return defaultColumns;
});

// 合并搜索条件（用户自定义 + 默认配置）
const mergedSearchColumns = computed(() => {
  // 默认搜索条件
  const defaultSearchColumns: SearchProps[] = [
    {
      prop: 'status',
      label: '状态',
      span: 1,
      render: (scope: any) => {
        return h(EnumSelect, {
          enumData: JudgeStatus as any,
          type: 'select',
          modelValue: typeof scope.searchParam.status === 'number' ? scope.searchParam.status : undefined,
          placeholder: '评测状态',
          clearable: true,
          style: { width: '110px' },
          'onUpdate:modelValue': (val: number) => {
            scope.searchParam.status = val;
          }
        });
      }
    },
    {
      prop: 'problemType',
      label: '题型',
      span: 1,
      render: (scope: any) => {
        return h(EnumSelect, {
          enumData: ProblemType as any,
          type: 'select',
          modelValue: scope.searchParam.problemType || '',
          placeholder: '题目类型',
          clearable: true,
          style: { width: '110px' },
          'onUpdate:modelValue': (val: string) => {
            scope.searchParam.problemType = val;
          }
        });
      }
    },
    {
      prop: 'switchGroup',
      label: '筛选',
      span: 1,
      render: (scope: any) => {
        return h('div', { class: 'filter-button-group' }, [
          h(BlockButtonSwitch, {
            modelValue: !!scope.searchParam.manualEvaluation,
            text: '人工',
            size: 'small',
            'onUpdate:modelValue': (val: boolean | null) => {
              scope.searchParam.manualEvaluation = val;
            }
          }),
          h(BlockButtonSwitch, {
            modelValue: !!scope.searchParam.myJudge,
            text: '我的',
            size: 'small',
            class: 'ml-2',
            'onUpdate:modelValue': (val: boolean | null) => {
              scope.searchParam.myJudge = val || false;
            }
          })
        ]);
      }
    },
    { prop: 'problemId', label: '题目ID', el: 'input', span: 1, props: { style: { width: '110px' }, placeholder: '题目ID' } },
    {
      prop: 'problemKey',
      label: '题目Key',
      el: 'input',
      span: 1,
      props: { style: { width: '110px' }, placeholder: '题目Key' }
    },
    {
      prop: 'problemTitle',
      label: '题目标题',
      el: 'input',
      span: 1,
      props: { style: { width: '110px' }, placeholder: '题目标题' }
    },
    {
      prop: 'sceneType',
      label: '类型',
      span: 1,
      render: (scope: any) => {
        return h(EnumSelect, {
          enumData: JudgeSceneType as any,
          type: 'select',
          modelValue: scope.searchParam.sceneType || '',
          placeholder: '测评类型',
          clearable: true,
          style: { width: '110px' },
          'onUpdate:modelValue': (val: string) => {
            scope.searchParam.sceneType = val;
            // 当测评类型变化时，清空业务ID
            scope.searchParam.businessId = undefined;
          }
        });
      }
    }
  ];

  // 如果用户提供了自定义搜索条件，则用自定义搜索条件替换默认搜索条件
  if (props.searchColumns && props.searchColumns.length > 0) {
    return props.searchColumns;
  }

  return defaultSearchColumns;
});

// 处理数据请求
const requestFunc = async (params: Record<string, any>) => {
  // 格式化参数
  const formattedParams = formatSearchParams(params);

  // 如果提供了自定义请求API，则使用自定义请求API
  if (props.requestApi) {
    return props.requestApi(formattedParams);
  }

  // 否则返回空数据
  return {
    code: 200,
    data: {
      list: [],
      total: 0
    }
  };
};

// 格式化搜索参数
const formatSearchParams = (params: Record<string, any>) => {
  // 如果有自定义的格式化函数，使用自定义的格式化函数
  if (typeof props.formatParams === 'function') {
    return props.formatParams(params);
  }

  return params;
};

// 刷新表格
const refresh = () => {
  proTableRef.value?.getTableList();
  emit('refresh');
};

// 处理新增
const handleAdd = () => {
  emit('add');

  if (formRef.value?.acceptParams) {
    const params = {
      title: '新增测评记录',
      row: {},
      api: props.createApi,
      getTableList: proTableRef.value?.getTableList
    };
    formRef.value.acceptParams(params);
  }
};

// 处理编辑
const handleEdit = async (row: any) => {
  emit('edit', row);

  if (formRef.value?.acceptParams) {
    let editRow = { ...row };

    // 如果提供了获取详情API，则先获取详情
    if (props.getDetailApi) {
      try {
        const record = await props.getDetailApi({ id: row?.id });
        editRow = record?.data;
      } catch (err) {
        console.error('获取详情失败', err);
      }
    }

    const params = {
      title: '编辑测评记录',
      row: editRow,
      api: props.updateApi,
      getTableList: proTableRef.value?.getTableList
    };
    formRef.value.acceptParams(params);
  }
};

// 处理人工测评
const handleManualEvaluation = (row: any) => {
  if (manualEvaluationRef.value?.openDialog) {
    manualEvaluationRef.value.openDialog(row);
  }
};

// 处理删除
const handleDelete = async (row: any) => {
  emit('delete', row);

  if (props.removeApi) {
    await useHandleData(props.removeApi, { ids: [row.id] }, `删除【${row.id}】测评记录`);
    refresh();

    // 执行成功回调
    props.onSuccess && props.onSuccess('delete', row);
  }
};

// 处理批量删除
const handleBatchDelete = async (ids: (string | number)[]) => {
  emit('batch-delete', ids);

  if (props.removeApi) {
    await useHandleData(props.removeApi, { ids }, '删除所选测评记录');
    proTableRef.value?.clearSelection();
    refresh();

    // 执行成功回调
    props.onSuccess && props.onSuccess('batchDelete', ids);
  }
};

// 处理查看详情 - 新增功能
const handleViewDetail = (row: any) => {
  emit('view', row);

  if (judgeDetailRef.value?.openDialog) {
    // 如果提供了获取详情API，则先获取详情
    if (props.getDetailApi) {
      props
        .getDetailApi({ id: row?.id })
        .then(res => {
          if (res?.data) {
            judgeDetailRef.value.openDialog(res.data);
          }
        })
        .catch(err => {
          console.error('获取测评详情失败', err);
        });
    } else {
      // 没有详情API时直接使用当前行数据
      judgeDetailRef.value.openDialog(row);
    }
  }
};

// 处理导入
const handleImport = () => {
  emit('import');

  if (importExcelRef.value?.acceptParams) {
    const params = {
      title: '测评记录',
      templateName: '测评记录',
      tempApi: downloadTemplate,
      importApi: props.importApi,
      getTableList: proTableRef.value?.getTableList
    };
    importExcelRef.value.acceptParams(params);
  }
};

// 处理导出
const handleExport = async () => {
  emit('export');

  if (props.exportApi) {
    let newParams = formatSearchParams(proTableRef.value?.searchParam || {});
    useDownload(props.exportApi, '测评记录', newParams);
  }
};

// 对外暴露的方法
defineExpose({
  refresh,
  getTableList: () => proTableRef.value?.getTableList(),
  clearSelection: () => proTableRef.value?.clearSelection(),
  getSelectedRows: () => {
    // 安全地获取选中行
    return (proTableRef.value as any)?.getSelectionRows?.() || [];
  },
  getSearchParams: () => proTableRef.value?.searchParam,
  tableRef: proTableRef,
  formRef
});
</script>

<style lang="scss" scoped>
.problem-table {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.judge-list-container {
  // 优化搜索区域样式，使其更紧凑
  :deep(.search-form) {
    padding: 10px 10px 0;
    background-color: #fafbfc;
    border-radius: 8px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
    margin-bottom: 8px;
    display: flex;
    flex-wrap: wrap;

    .el-form-item {
      margin-bottom: 10px;
      margin-right: 6px;

      .el-form-item__label {
        padding-right: 3px;
        font-size: 12px;
        color: var(--el-text-color-secondary);
        font-weight: 500;
      }

      .el-form-item__content {
        .el-input,
        .el-select,
        .el-date-editor,
        .user-select,
        .el-switch {
          width: 110px; // 设置更紧凑的宽度
        }

        // 开关按钮不需要左边距
        .el-switch {
          margin-left: 0;
        }

        .el-select {
          .el-input {
            width: 100%;
          }
        }

        .filter-button-group {
          display: flex;
          gap: 6px;
          align-items: center;
        }
      }
    }

    .operation {
      margin-bottom: 10px;

      .el-button {
        padding: 5px 10px;
        height: 26px;
        font-size: 12px;

        & + .el-button {
          margin-left: 5px;
        }
      }
    }

    // 优化搜索按钮
    .search-operation {
      .search-operation-item {
        .el-button {
          padding: 5px 10px;
          height: 26px;
          font-size: 12px;
        }
      }
    }
  }

  // 表格操作栏按钮样式优化
  :deep(.table-header) {
    padding: 12px 16px;

    .header-button-lf {
      .table-title {
        font-size: 18px;
        font-weight: 700;
        color: #222222;
        margin-right: 15px;
        position: relative;
        padding-left: 12px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 18px;
          background-color: #409eff;
          border-radius: 2px;
        }
      }

      .el-button {
        padding: 7px 14px;

        & + .el-button {
          margin-left: 8px;
        }
      }
    }
  }

  // Key值样式优化
  .key-value {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 4px;
    font-family: 'Roboto Mono', monospace;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.5px;
    text-align: center;
  }

  .non-functional-key {
    background-color: #f5f7fa;
    color: #606266;
    border: 1px solid #e0e6ed;
  }

  // 题目ID点击样式
  .clickable-problem-id {
    display: inline-block;
    cursor: pointer;
    position: relative;

    .problem-id-link {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      color: #1890ff;
      background-color: #e6f7ff;
      border: 1px solid #bae7ff;
      border-radius: 4px;
      padding: 4px 8px;
      font-family: 'Roboto Mono', monospace;
      font-size: 13px;
      font-weight: 500;
      transition: all 0.25s ease;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

      &:hover {
        background-color: #bae7ff;
        transform: translateY(-2px);
        box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
      }

      .link-icon {
        font-size: 14px;
        color: #1890ff;
      }

      .problem-id-number {
        font-weight: 600;
      }

      .goto-text {
        font-size: 12px;
        margin-left: 4px;
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

    &::after {
      content: '';
      position: absolute;
      bottom: -3px;
      left: 0;
      width: 100%;
      height: 2px;
      background-color: #1890ff;
      opacity: 0;
      transition: all 0.3s;
    }

    &:hover::after {
      opacity: 1;
      bottom: -1px;
    }
  }

  .no-code {
    color: #909399;
    font-style: italic;
    font-size: 13px;
  }

  .code-preview-wrapper {
    display: flex;
    justify-content: center;
  }

  .code-preview-button {
    cursor: pointer;
    background-color: #409eff;
    color: white;
    border-radius: 4px;
    padding: 4px 10px;
    font-size: 12px;
    transition: all 0.3s;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    &:hover {
      background-color: #337ecc;
      transform: translateY(-1px);
      box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
    }

    .view-text {
      font-size: 12px;
      font-weight: 500;
    }

    .view-icon {
      font-size: 14px;
    }
  }

  .user-info {
    .user-id {
      color: #909399;
      font-size: 12px;
      margin-top: 3px;
    }
  }

  .business-info {
    .business-type {
      font-weight: 500;
      color: #409eff;
      font-size: 13px;
    }

    .business-id {
      color: #606266;
      font-size: 12px;
      margin-top: 3px;
    }

    .business-name {
      color: #909399;
      font-size: 12px;
      margin-top: 3px;
      font-style: italic;
    }

    &.group-business {
      .business-type {
        color: #67c23a;
      }
    }
  }

  .manual-tag {
    padding: 4px 8px;
    font-size: 12px;
    border-radius: 4px;
  }

  .image-container {
    cursor: pointer;
    display: inline-block;
  }

  .flow-image-link {
    text-decoration: none;
    display: inline-block;

    .flow-image-tag {
      padding: 4px 8px;
      display: flex;
      align-items: center;
      transition: all 0.3s;

      &:hover {
        color: #409eff;
        border-color: #409eff;
      }

      .view-icon {
        margin-left: 5px;
      }
    }
  }

  .json-preview {
    cursor: pointer;

    .json-tag {
      padding: 4px 10px;
      font-size: 12px;
      border-radius: 4px;
      transition: all 0.3s;
      display: inline-flex;
      align-items: center;
      gap: 4px;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }
    }
  }

  // 操作按钮样式
  :deep(.el-button--small) {
    font-size: 12px;
    padding: 5px 12px;
    height: 28px;
  }

  :deep(.el-button--text) {
    padding: 5px 8px;
  }

  :deep(.el-form-item) {
    .el-form-item__content {
      .el-date-editor {
        .el-input__wrapper {
          padding: 0 8px;
        }

        .el-input__prefix-inner {
          .el-input__icon {
            height: 28px;
            width: 20px;
          }
        }
      }

      // 输入框样式优化
      .el-input {
        .el-input__wrapper {
          padding: 0 10px;
        }
      }

      // 精简下拉框样式
      .el-select {
        .select-trigger {
          .el-input__wrapper {
            padding: 0 10px;
          }
        }
      }

      // 开关按钮布局
      .el-switch {
        margin-left: 12px;
      }
    }
  }
}

.no-answer,
.error-parsing {
  color: #909399;
  font-style: italic;
  font-size: 13px;
}

.error-parsing {
  color: #f56c6c;
}

.option-content-wrapper {
  padding: 8px;
  max-height: 350px;
  overflow-y: auto;

  .empty-data {
    color: #909399;
    text-align: center;
    padding: 10px;
    font-style: italic;
  }

  .option-item {
    background: #f8f9fa;
    border-radius: 4px;
    padding: 12px;
    margin-bottom: 10px;
    border-left: 3px solid #dcdfe6;
    transition: all 0.3s;

    &:last-child {
      margin-bottom: 0;
    }

    &.selected-option {
      background-color: #f0f9eb;
      border-left: 3px solid #67c23a;
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
        gap: 6px;
        font-size: 13px;

        .answer-tag {
          font-size: 11px;
          padding: 0 5px;
          height: 20px;
          line-height: 18px;
        }
      }

      .selected-icon {
        color: #67c23a;
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
      border: 1px solid #ebeef5;
      white-space: pre-wrap;
      line-height: 1.5;
      font-size: 13px;
    }
  }
}

.json-preview {
  .json-tag {
    padding: 4px 10px;
    font-size: 12px;
    border-radius: 4px;
    transition: all 0.3s;
    display: inline-flex;
    align-items: center;
    gap: 4px;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }
}

// 美化表头样式 - 直接针对ProTable内部的表头元素
.judge-list-container :deep(.el-table) {
  .el-table__header {
    th {
      background-color: #f5f7fa;

      .cell {
        font-size: 16px !important;
        font-weight: 700 !important;
        color: #222222 !important;
        padding: 8px 0;
        position: relative;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          width: 100%;
          height: 2px;
          background-color: #e0e6ed;
        }
      }
    }
  }

  // 行高调整
  .el-table__row {
    td {
      padding: 8px 0;
    }
  }
}

// 错误信息样式
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

.no-error {
  color: #67c23a;
  font-size: 12px;
}

// 运行时间、内存显示
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

// 分数显示
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

// 优化时间显示
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

// 增强枚举组件样式
:deep(.enum-show-value) {
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-flex;
  line-height: 1.2;
  font-size: 13px;

  &.status-tag {
    // 状态特殊样式
    font-weight: bold;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  &.type-tag {
    // 类型特殊样式
    border-width: 1px;
    border-style: solid;
  }
}

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

  .status-tag {
    min-width: 100px;
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

// 弹窗样式
:deep(.code-editor-popover) {
  max-width: 800px !important;
}

:deep(.json-content-popover) {
  max-width: 500px !important;
}

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

// 自定义组件样式优化
:deep(.user-select, .el-date-editor.el-input) {
  width: 110px !important;

  .el-input__wrapper {
    padding-right: 6px;
  }
}

:deep(.select-trigger) {
  max-width: 110px;
}

// BlockButtonSwitch样式定制
:deep(.block-button-switch) {
  .switch-block {
    min-width: 46px;

    &.size-small {
      min-width: 42px;
      height: 26px;
      padding: 0 6px;
      border-radius: 4px;
    }

    .switch-text {
      font-size: 12px;
    }
  }
}

.ml-2 {
  margin-left: 6px;
}
</style>

<!-- 添加全局样式，确保覆盖表头样式 -->
<style lang="scss">
/* 直接对el-table表头进行样式覆盖 */
.judge-list-container .el-table thead th .cell {
  font-size: 16px !important;
  font-weight: 700 !important;
  color: #666 !important;
}

.judge-list-container .el-table thead.is-group th.el-table__cell {
  background-color: #f5f7fa;
}

/* 确保样式优先级足够高 */
.el-table--small .el-table__cell.is-leaf {
  .cell {
    font-size: 16px !important;
    font-weight: 700 !important;
    color: #666 !important;
  }
}
</style>
