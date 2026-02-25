<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="代码监控"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'code.monitor.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增代码监控')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'code.monitor.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'code.monitor.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'code.monitor.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>

      <!-- 被监控代码列 -->
      <template #monitorCode="scope">
        <div class="code-column">
          <span class="code-preview">{{ scope.row.monitorCode ? scope.row.monitorCode.substring(0, 20) + '...' : '' }}</span>
          <el-button
            type="primary"
            link
            size="small"
            @click.stop="handleViewCode(scope.row, 'monitor')"
          >
            查看代码
          </el-button>
        </div>
      </template>

      <!-- 覆盖代码列 -->
      <template #overlayCode="scope">
        <div class="code-column">
          <span class="code-preview">{{ scope.row.overlayCode ? scope.row.overlayCode.substring(0, 20) + '...' : '' }}</span>
          <el-button
            type="primary"
            link
            size="small"
            @click.stop="handleViewCode(scope.row, 'overlay')"
          >
            查看代码
          </el-button>
        </div>
      </template>

      <template #operation="{ row }">
        <el-button
          v-auth="'code.monitor.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑代码监控', row, false)"
        >
          编辑
        </el-button>
        <el-button
          type="primary"
          link
          :icon="View"
          @click="openDetail(row)"
        >
          查看详情
        </el-button>
        <el-button
          v-auth="'code.monitor.push'"
          type="primary"
          link
          :icon="Upload"
          @click="handlePushCode(row)"
        >
          推送代码
        </el-button>
        <el-button
          v-auth="'code.monitor.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <CodeMonitorForm ref="codeMonitorRef" />
    <ImportExcel ref="ImportExcelRef" />
    <PushCoverdCode ref="pushCoverdCodeRef" />

    <!-- 代码查看对话框 -->
    <el-dialog
      v-model="codeDialogVisible"
      :title="codeDialogTitle"
      width="80%"
      destroy-on-close
      class="code-dialog"
    >
      <div class="code-container">
        <div class="code-actions">
          <el-button
            type="primary"
            size="small"
            @click="handleCopyCode"
          >
            复制代码
          </el-button>
        </div>
        <pre class="code-block"><code>{{ currentCode }}</code></pre>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="代码监控详情"
      width="650px"
      destroy-on-close
      class="detail-dialog"
    >
      <div v-if="currentDetail" class="detail-container">
        <div class="detail-section">
          <h3 class="section-title">基本信息</h3>
          <div class="detail-row">
            <div class="detail-label">被监控人：</div>
            <div class="detail-value">{{ currentDetail.monitorName || '暂无' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">被监控人电话：</div>
            <div class="detail-value">{{ currentDetail.monitorPhone || '暂无' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">覆盖人：</div>
            <div class="detail-value">{{ currentDetail.overlayName || '暂无' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">覆盖人电话：</div>
            <div class="detail-value">{{ currentDetail.overlayPhone || '暂无' }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">代码信息</h3>
          <div class="detail-row">
            <div class="detail-label">代码模式：</div>
            <div class="detail-value">{{ currentDetail.codeMode || '暂无' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">监控状态：</div>
            <div class="detail-value">{{ currentDetail.monitorStatus || '暂无' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">版本：</div>
            <div class="detail-value">{{ currentDetail.version || '暂无' }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">代码预览</h3>
          <div class="detail-code">
            <div class="code-header">
              <span>被监控代码</span>
              <el-button
                type="primary"
                link
                size="small"
                @click="handleViewCode(currentDetail, 'monitor')"
              >
                查看完整代码
              </el-button>
            </div>
            <pre class="code-preview">{{ currentDetail.monitorCode ? currentDetail.monitorCode.substring(0, 200) + (currentDetail.monitorCode.length > 200 ? '...' : '') : '暂无' }}</pre>
          </div>
          <div class="detail-code">
            <div class="code-header">
              <span>覆盖代码</span>
              <el-button
                type="primary"
                link
                size="small"
                @click="handleViewCode(currentDetail, 'overlay')"
              >
                查看完整代码
              </el-button>
            </div>
            <pre class="code-preview">{{ currentDetail.overlayCode ? currentDetail.overlayCode.substring(0, 200) + (currentDetail.overlayCode.length > 200 ? '...' : '') : '暂无' }}</pre>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">时间信息</h3>
          <div class="detail-row">
            <div class="detail-label">创建时间：</div>
            <div class="detail-value">{{ formatDateTime(currentDetail.createTime) }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">更新时间：</div>
            <div class="detail-value">{{ formatDateTime(currentDetail.updateTime) }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          type="primary"
          @click="handlePushCode(currentDetail as ICodeMonitor.Row)"
        >
          推送代码
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  View,
  Document
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import {
  createCodeMonitorApi,
  removeCodeMonitorApi,
  updateCodeMonitorApi,
  getCodeMonitorListApi,
  getCodeMonitorDetailApi,
  importCodeMonitorExcelApi,
  exportCodeMonitorExcelApi,
} from '@/api/modules/oj/code/codeMonitor';
import { useHandleData } from '@/hooks/useHandleData';
import CodeMonitorForm from '@/views/oj/code/codeMonitor/components/CodeMonitorForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ICodeMonitor } from '@/api/interface/oj/code/codeMonitor';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox, ElMessage } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
import PushCoverdCode from '@/components/Oj/Code/PushCoverdCode.vue';

defineOptions({
  name: 'CodeMonitorView'
})
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ICodeMonitor.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'monitorUserId', label: '被用户id' },
  { prop: 'overlayUserId', label: '覆盖用户id' },
  { prop: 'monitorPhone', label: '被监控人电话' },
  { prop: 'overlayPhone', label: '覆盖人电话' },
  { prop: 'monitorName', label: '被监控人' },
  { prop: 'overlayName', label: '覆盖人' },
  {
    prop: 'monitorCode',
    label: '被监控代码',
    render: (scope) => {
      return h('div', { class: 'code-column' }, [
        h('span', { class: 'code-preview' },
          scope.row.monitorCode ? scope.row.monitorCode.substring(0, 20) + '...' : ''
        ),
        h('el-button', {
          type: 'primary',
          link: true,
          size: 'small',
          onClick: (e: Event) => {
            e.stopPropagation();
            handleViewCode(scope.row, 'monitor');
          }
        }, '查看代码')
      ]);
    }
  },
  {
    prop: 'overlayCode',
    label: '覆盖代码',
    render: (scope) => {
      return h('div', { class: 'code-column' }, [
        h('span', { class: 'code-preview' },
          scope.row.overlayCode ? scope.row.overlayCode.substring(0, 20) + '...' : ''
        ),
        h('el-button', {
          type: 'primary',
          link: true,
          size: 'small',
          onClick: (e: Event) => {
            e.stopPropagation();
            handleViewCode(scope.row, 'overlay');
          }
        }, '查看代码')
      ]);
    }
  },
  { prop: 'codeMode', label: '代码模式' },
  { prop: 'monitorStatus', label: '监控状态' },
  { prop: 'version', label: '版本' },
  { prop: 'operation', label: '操作', width: 330, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'monitorUserId', label: '被用户id', el: 'input' },
  { prop: 'overlayUserId', label: '覆盖用户id', el: 'input' },
  { prop: 'monitorPhone', label: '被监控人电话', el: 'input' },
  { prop: 'overlayPhone', label: '覆盖人电话', el: 'input' },
  { prop: 'monitorName', label: '被监控人', el: 'input' },
  { prop: 'overlayName', label: '覆盖人', el: 'input' },
  { prop: 'monitorCode', label: '被监控代码', el: 'input' },
  { prop: 'overlayCode', label: '覆盖代码', el: 'input' },
  { prop: 'codeMode', label: '代码模式', el: 'input' },
  { prop: 'monitorStatus', label: '监控状态', el: 'select' },
  { prop: 'version', label: '版本', el: 'input' },
]
// 获取table列表
const getTableList = (params: ICodeMonitor.Query) => {
  let newParams = formatParams(params);
  return getCodeMonitorListApi(newParams);
};
const formatParams = (params: ICodeMonitor.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}
// 打开 drawer(新增、查看、编辑)
const codeMonitorRef = ref<InstanceType<typeof CodeMonitorForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getCodeMonitorDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createCodeMonitorApi : updateCodeMonitorApi,
    getTableList: proTableRef.value?.getTableList
  }
  codeMonitorRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ICodeMonitor.Row) => {
  await useHandleData(
    removeCodeMonitorApi,
    { ids: [params.id] },
    `删除【${params.id}】代码监控`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeCodeMonitorApi, { ids }, '删除所选代码监控')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '代码监控',
    templateName: '代码监控',
    tempApi: downloadTemplate,
    importApi: importCodeMonitorExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ICodeMonitor.Query);
  useDownload(exportCodeMonitorExcelApi, "代码监控", newParams);
};

// 处理推送代码
const pushCoverdCodeRef = ref<InstanceType<typeof PushCoverdCode>>()
const handlePushCode = (row: ICodeMonitor.Row) => {
  pushCoverdCodeRef.value?.open(row)
}

// 代码查看对话框
const codeDialogVisible = ref(false);
const currentCode = ref('');
const codeDialogTitle = ref('');
const codeLanguage = ref('javascript');

// 处理查看代码
const handleViewCode = (row: ICodeMonitor.Row, type: 'monitor' | 'overlay') => {
  codeDialogTitle.value = type === 'monitor' ? '被监控代码' : '覆盖代码';
  currentCode.value = type === 'monitor' ? (row.monitorCode || '') : (row.overlayCode || '');
  // 根据代码模式设置语言
  codeLanguage.value = getCodeLanguage(row.codeMode || '');
  codeDialogVisible.value = true;
};

// 根据代码模式获取代码语言类型
const getCodeLanguage = (codeMode: string): string => {
  const modeLangMap: Record<string, string> = {
    'java': 'java',
    'javascript': 'javascript',
    'python': 'python',
    'c': 'c',
    'cpp': 'cpp',
    'csharp': 'csharp',
    'go': 'go'
  };
  return modeLangMap[codeMode.toLowerCase()] || 'javascript';
};

// 详情对话框
const detailDialogVisible = ref(false);
const currentDetail = ref<ICodeMonitor.Row | null>(null);

// 打开详情对话框
const openDetail = (row: ICodeMonitor.Row) => {
  if (row.id) {
    getCodeMonitorDetailApi({ id: row.id }).then(res => {
      if (res?.data) {
        currentDetail.value = res.data;
        detailDialogVisible.value = true;
      }
    });
  }
};

// 添加复制代码功能和日期格式化函数
const handleCopyCode = async () => {
  try {
    // 优先使用新的 Clipboard API
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(currentCode.value);
    } else {
      // 降级使用传统的复制方法
      const textArea = document.createElement('textarea');
      textArea.value = currentCode.value;
      textArea.style.position = 'fixed';
      textArea.style.left = '-999999px';
      textArea.style.top = '-999999px';
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      document.execCommand('copy');
      textArea.remove();
    }
    ElMessage.success('代码已复制到剪贴板');
  } catch (err) {
    ElMessage.error('复制失败，请手动复制');
    console.error('复制失败:', err);
  }
};

// 日期格式化函数
const formatDateTime = (dateTime?: string) => {
  if (!dateTime) return '暂无';

  try {
    const date = new Date(dateTime);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    });
  } catch (error) {
    return dateTime;
  }
};
</script>

<style scoped lang="scss">
.table-box {
  :deep(.code-column) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;

    .code-preview {
      font-family: monospace;
      color: #666;
      max-width: 150px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .code-dialog {
    :deep(.el-dialog__body) {
      padding: 0;
    }

    .code-container {
      position: relative;

      .code-actions {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 10;
      }

      .code-block {
        background-color: #f5f7fa;
        padding: 20px;
        border-radius: 8px;
        margin: 0;
        max-height: 70vh;
        overflow-y: auto;
        font-family: 'Courier New', Courier, monospace;
        white-space: pre-wrap;
        word-break: break-all;
      }
    }
  }

  .detail-dialog {
    :deep(.el-dialog__body) {
      padding: 20px;
    }

    .detail-container {
      .detail-section {
        margin-bottom: 20px;

        .section-title {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 12px;
          padding-bottom: 8px;
          border-bottom: 1px solid #eee;
          color: var(--el-color-primary);
        }

        .detail-row {
          display: flex;
          margin-bottom: 8px;

          .detail-label {
            flex: 0 0 120px;
            font-weight: 500;
            color: #606266;
          }

          .detail-value {
            flex: 1;
            color: #303133;
          }
        }

        .detail-code {
          margin-bottom: 16px;

          .code-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            span {
              font-weight: 500;
            }
          }

          .code-preview {
            background-color: #f5f7fa;
            padding: 12px;
            border-radius: 6px;
            font-family: 'Courier New', Courier, monospace;
            font-size: 13px;
            white-space: pre-wrap;
            word-break: break-all;
            max-height: 150px;
            overflow-y: auto;
          }
        }
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .table-box {
    .code-dialog .code-block,
    .detail-dialog .detail-code .code-preview {
      background-color: #2b2b2b;
      color: #e6e6e6;
    }

    .detail-dialog .detail-container .detail-section {
      .section-title {
        border-bottom-color: #3e3e3e;
      }

      .detail-row {
        .detail-label {
          color: #a0a0a0;
        }

        .detail-value {
          color: #e6e6e6;
        }
      }
    }
  }
}
</style>
