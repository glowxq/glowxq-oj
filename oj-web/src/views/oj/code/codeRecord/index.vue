<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="用户代码"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'code.record.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增用户代码')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'code.record.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'code.record.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'code.record.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>

      <!-- 代码列自定义内容 -->
      <template #code="scope">
        <div class="code-column">
          <span class="code-preview">{{ scope.row.code ? scope.row.code.substring(0, 20) + '...' : '' }}</span>
          <el-button
            type="primary"
            link
            size="small"
            @click.stop="handleViewCode(scope.row)"
          >
            查看代码
          </el-button>
        </div>
      </template>

      <template #operation="{ row }">
        <el-button
          v-auth="'code.record.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑用户代码', row, false)"
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
          v-auth="'code.record.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <CodeRecordForm ref="codeRecordRef" />
    <ImportExcel ref="ImportExcelRef" />

    <!-- 代码查看对话框 -->
    <el-dialog
      v-model="codeDialogVisible"
      title="代码查看"
      width="80%"
      destroy-on-close
      class="code-dialog"
    >
      <div class="code-container">
        <div class="code-info">
          <div class="info-item">
            <span class="info-label">用户：</span>
            <span class="info-value">{{ currentCodeUser?.name || currentCodeUser?.username || '未知用户' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">代码模式：</span>
            <span class="info-value">{{ currentCodeUser?.codeMode || '未知' }}</span>
          </div>
        </div>
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
      title="用户代码详情"
      width="650px"
      destroy-on-close
      class="detail-dialog"
    >
      <div v-if="currentDetail" class="detail-container">
        <div class="detail-section">
          <h3 class="section-title">用户信息</h3>
          <div class="detail-row">
            <div class="detail-label">用户ID：</div>
            <div class="detail-value">{{ currentDetail.userId || '暂无' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">姓名：</div>
            <div class="detail-value">{{ currentDetail.name || '暂无' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">用户名：</div>
            <div class="detail-value">{{ currentDetail.username || '暂无' }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">代码信息</h3>
          <div class="detail-row">
            <div class="detail-label">代码模式：</div>
            <div class="detail-value">{{ currentDetail.codeMode || '暂无' }}</div>
          </div>
          <div class="detail-code">
            <div class="code-header">
              <span>代码内容</span>
              <el-button
                type="primary"
                link
                size="small"
                @click="handleViewCode(currentDetail)"
              >
                查看完整代码
              </el-button>
            </div>
            <pre class="code-preview">{{ currentDetail.code ? currentDetail.code.substring(0, 200) + (currentDetail.code.length > 200 ? '...' : '') : '暂无' }}</pre>
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
          @click="openAddEdit('编辑用户代码', currentDetail, false)"
        >
          编辑
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
  createCodeRecordApi,
  removeCodeRecordApi,
  updateCodeRecordApi,
  getCodeRecordListApi,
  getCodeRecordDetailApi,
  importCodeRecordExcelApi,
  exportCodeRecordExcelApi,
} from '@/api/modules/oj/code/codeRecord';
import { useHandleData } from '@/hooks/useHandleData';
import CodeRecordForm from '@/views/oj/code/codeRecord/components/CodeRecordForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/Common/ProTable/interface';
import type { ICodeRecord } from '@/api/interface/oj/code/codeRecord';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessageBox, ElMessage } from "element-plus";
import { useDownload } from "@/hooks/useDownload";

defineOptions({
  name: 'CodeRecordView'
})

// 代码查看对话框
const codeDialogVisible = ref(false);
const currentCode = ref('');
const currentCodeUser = ref<ICodeRecord.Row | null>(null);

// 处理查看代码
const handleViewCode = (row: ICodeRecord.Row) => {
  currentCode.value = row.code || '';
  currentCodeUser.value = row;
  codeDialogVisible.value = true;
};

// 复制代码功能
const handleCopyCode = () => {
  navigator.clipboard.writeText(currentCode.value)
    .then(() => {
      ElMessage.success('代码已复制到剪贴板');
    })
    .catch(() => {
      ElMessage.error('复制失败，请手动复制');
    });
};

// 详情对话框
const detailDialogVisible = ref(false);
const currentDetail = ref<ICodeRecord.Row | null>(null);

// 打开详情对话框
const openDetail = (row: ICodeRecord.Row) => {
  if (row.id) {
    getCodeRecordDetailApi({ id: row.id }).then(res => {
      if (res?.data) {
        currentDetail.value = res.data;
        detailDialogVisible.value = true;
      }
    });
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

const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ICodeRecord.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'userId', label: '用户id' },
  { prop: 'name', label: '姓名' },
  { prop: 'username', label: '用户名' },
  {
    prop: 'code',
    label: '代码',
    render: (scope: any) => scope.row.code || '-'
  },
  { prop: 'codeMode', label: '代码模式' },
  { prop: 'operation', label: '操作', width: 330, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'userId', label: '用户id', el: 'input' },
  { prop: 'name', label: '姓名', el: 'input' },
  { prop: 'username', label: '用户名', el: 'input' },
  { prop: 'code', label: '代码', el: 'input' },
  { prop: 'codeMode', label: '代码模式', el: 'input' },
]
// 获取table列表
const getTableList = (params: ICodeRecord.Query) => {
  let newParams = formatParams(params);
  return getCodeRecordListApi(newParams);
};
const formatParams = (params: ICodeRecord.Query) =>{
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
const codeRecordRef = ref<InstanceType<typeof CodeRecordForm>>()
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getCodeRecordDetailApi({ id: row?.id })
    row = record?.data
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createCodeRecordApi : updateCodeRecordApi,
    getTableList: proTableRef.value?.getTableList
  }
  codeRecordRef.value?.acceptParams(params)
}
// 删除信息
const deleteInfo = async (params: ICodeRecord.Row) => {
  await useHandleData(
    removeCodeRecordApi,
    { ids: [params.id] },
    `删除【${params.id}】用户代码`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeCodeRecordApi, { ids }, '删除所选用户代码')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const importData = () => {
  const params = {
    title: '用户代码',
    templateName: '用户代码',
    tempApi: downloadTemplate,
    importApi: importCodeRecordExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ICodeRecord.Query);
  useDownload(exportCodeRecordExcelApi, "用户代码", newParams);
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

      .code-info {
        display: flex;
        padding: 16px 20px;
        background-color: #f8f9fa;
        border-bottom: 1px solid #ebeef5;
        flex-wrap: wrap;
        gap: 16px;

        .info-item {
          display: flex;
          align-items: center;

          .info-label {
            font-weight: 600;
            margin-right: 8px;
            color: #606266;
          }

          .info-value {
            color: #303133;
          }
        }
      }

      .code-actions {
        position: absolute;
        top: 68px;
        right: 10px;
        z-index: 10;
      }

      .code-block {
        background-color: #f5f7fa;
        padding: 20px;
        border-radius: 0 0 8px 8px;
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
            flex: 0 0 100px;
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
    .code-dialog {
      .code-container {
        .code-info {
          background-color: #2b2b2b;
          border-bottom-color: #3e3e3e;

          .info-item {
            .info-label {
              color: #a0a0a0;
            }

            .info-value {
              color: #e6e6e6;
            }
          }
        }

        .code-block {
          background-color: #2b2b2b;
          color: #e6e6e6;
        }
      }
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

      .detail-code .code-preview {
        background-color: #2b2b2b;
        color: #e6e6e6;
      }
    }
  }
}
</style>
