<template>
  <div class="problem-table-container">
    <ProTable
      ref="proTableRef"
      title="题目"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="id"
      class="problem-table"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-show="isAdminOrCommon"
          v-auth="'problem.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增题目')"
        >
          新增
        </el-button>
        <el-button
          v-show="isAdminOrCommon"
          v-auth="'problem.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-show="isAdminOrCommon"
          v-auth="'problem.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-show="isAdminOrCommon"
          v-auth="'problem.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importOssData"
        >
          编程题导入
        </el-button>
        <el-button
          v-show="isAdminOrCommon"
          v-auth="'problem.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importBatchOssData"
        >
          编程题批量导入
        </el-button>
        <el-button
          v-show="isAdminOrCommon"
          v-auth="'problem.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>


      <!-- 程序类型列 -->
      <template #programType="{ row }">
        <EnumShow :enum="ProgramType" :code="row.programType" />
      </template>
      <!-- 题目类型列 -->
      <template #problemType="{ row }">
        <EnumShow :enum="ProblemType" :code="row.problemType" />
      </template>
      <!-- 难度等级列 -->
      <template #difficulty="{ row }">
        <EnumShow
          :enum="DifficultyLevel"
          :code="row.difficulty"
          v-bind="getDifficultyStyle(row.difficulty)"
          class="difficulty-tag"
        />
      </template>
      <!-- 评测模式 -->
      <template #judgeMode="{ row }">
        <EnumShow
          :enum="JudgeMode"
          :code="row.judgeMode"
          backgroundColor="#f4f4f5"
          color="#909399"
          borderRadius="4px"
          fontWeight="bold"
          class="judge-mode-tag"
        />
      </template>

      <template #operation="{ row }">
        <el-button
          v-auth="'problem.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑题目', row, false)"
        >
          编辑
        </el-button>
        <el-button
          v-auth="'problem.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
        <el-button
          type="success"
          link
          :icon="Document"
          @click="handleGoToSubmit(row)"
          class="solve-button"
        >
          做题
        </el-button>
      </template>
    </ProTable>
    <ProblemForm ref="problemRef" />
    <ImportExcel ref="ImportExcelRef" />
    <ImportProgramDialog ref="ImportOssDialogRef" @success="handleImportSuccess" />
    <ImportBatchProgramDialog ref="ImportBatchOssDialogRef" @success="handleImportSuccess" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRole } from '@/hooks/useRole'
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  Document
} from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import { ProblemType } from '@/enums/oj/problem'
import { ProgramType } from '@/enums/oj/problem/ProgramType'
import { DifficultyLevel } from '@/enums/oj/problem/DifficultyLevel'
import {
  createProblemApi,
  removeProblemApi,
  updateProblemApi,
  getProblemListApi,
  getProblemDetailApi,
  importProblemExcelApi,
  exportProblemExcelApi,
} from '@/api/modules/oj/problem/problem';
import { useHandleData } from '@/hooks/useHandleData';
import ProblemForm from '@/views/oj/problem/problem/components/ProblemForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps, SearchRenderScope } from '@/components/Common/ProTable/interface';
import type { IProblem } from '@/api/interface/oj/problem/problem';
import ImportExcel from '@/components/Common/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/admin/common';
import { ElMessage, ElMessageBox } from "element-plus";
import { useDownload } from "@/hooks/useDownload";
import TagSelect from '@/components/Common/Meta/Tag/TagSelect.vue'
import { JudgeMode } from '@/enums/oj/judge'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import { ProblemSourceType } from '@/enums/oj/problem/ProblemSourceType'
import { h } from 'vue'
import { useRouter } from 'vue-router'
import ImportProgramDialog from './components/ImportProgramDialog.vue'
import ImportBatchProgramDialog from './components/ImportBatchProgramDialog.vue'

defineOptions({
  name: 'ProblemView'
})

const { isAdminOrCommon } = useRole()
const router = useRouter()
const proTableRef = ref<ProTableInstance>();
// 表格配置项
interface CustomColumnProps<T> extends ColumnProps<T> {
  slot?: string;
}

const columns: CustomColumnProps<IProblem.Row>[] = [
  { type: 'selection', width: 80 },
  { prop: 'problemKey', label: '题目KEY' ,width:100},
  { prop: 'title', label: '题目名称' ,width:200},
  { prop: 'author', label: '作者' },
  {
    prop: 'programType',
    label: '程序类型',
    slot: 'programType',
    width: 100
  },
  {
    prop: 'problemType',
    label: '题目类型',
    slot: 'problemType',
    width: 100
  },
  { prop: 'sourceType', label: '来源' },
  {
    prop: 'difficulty',
    label: '难度',
    slot: 'difficulty'
  },
  { prop: 'auth', label: '权限' },
  { prop: 'source', label: '来源' },
  { prop: 'judgeMode', label: '评测模式' ,width:120},
  { prop: 'requireImage', label: '图片' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
]
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'problemKey', label: '题目KEY', el: 'input' },
  { prop: 'title', label: '题目名称', el: 'input' },
  {
    prop: 'programType',
    label: '程序类型',
    render: (scope: SearchRenderScope) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: ProgramType,
        modelValue: scope.searchParam.programType,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.programType = val;
        }
      });
    }
  },
  {
    prop: 'problemType',
    label: '题目类型',
    render: (scope: SearchRenderScope) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: ProblemType,
        modelValue: scope.searchParam.problemType,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.problemType = val;
        }
      });
    }
  },
  {
    prop: 'sourceType',
    label: '来源类型',
    render: (scope: SearchRenderScope) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: ProblemSourceType,
        modelValue: scope.searchParam.sourceType,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.sourceType = val;
        }
      });
    }
  },
  {
    prop: 'tagIds',
    label: '标签',
    render: (scope: SearchRenderScope) => {
      return h(TagSelect, {
        multiple: true,
        modelValue: scope.searchParam.tagIds,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.tagIds = val;
        }
      });
    }
  },
  { prop: 'description', label: '描述', el: 'input' },
  {
    prop: 'judgeMode',
    label: '评测模式',
    render: (scope: SearchRenderScope) => {
      return h(EnumSelect, {
        type: 'select',
        enumData: JudgeMode,
        modelValue: scope.searchParam.judgeMode,
        'onUpdate:modelValue': (val: any) => {
          scope.searchParam.judgeMode = val;
        }
      });
    }
  },
  { prop: 'requireImage', label: '需要图片', el: 'switch' },
]
// 获取table列表
const getTableList = (params: IProblem.Query) => {
  let newParams = formatParams(params);
  return getProblemListApi(newParams);
};
const formatParams = (params: IProblem.Query) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  newParams.timeLimit && (newParams.timeLimitStart = newParams.timeLimit[0]);
  newParams.timeLimit && (newParams.timeLimitEnd = newParams.timeLimit[1]);
  delete newParams.timeLimit;
  newParams.memoryLimit && (newParams.memoryLimitStart = newParams.memoryLimit[0]);
  newParams.memoryLimit && (newParams.memoryLimitEnd = newParams.memoryLimit[1]);
  delete newParams.memoryLimit;
  newParams.createTime && (newParams.createTimeStart = newParams.createTime[0]);
  newParams.createTime && (newParams.createTimeEnd = newParams.createTime[1]);
  delete newParams.createTime;
  newParams.updateTime && (newParams.updateTimeStart = newParams.updateTime[0]);
  newParams.updateTime && (newParams.updateTimeEnd = newParams.updateTime[1]);
  delete newParams.updateTime;
  return newParams;
}
// 打开 drawer(新增、查看、编辑)
const problemRef = ref<InstanceType<typeof ProblemForm>>()
/**
 * 打开添加/编辑对话框
 * @param title 对话框标题
 * @param row 行数据（编辑模式下有值）
 * @param isAdd 是否为添加操作
 */
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    try {
      // 获取题目详情数据
      const res = await getProblemDetailApi({ id: row?.id })

      if (res?.data) {
        // 格式化数据，确保ID字段正确传递
        const problemData = {
          ...res.data,
          id: res.data.id || row.id  // 确保ID存在
        }

        console.log('编辑题目，获取的详情数据:', problemData)

        // 打印详细信息用于调试
        if (problemData.problemBo) {
          console.log('题目problemBo数据:', problemData.problemBo);
        }

        // 打印标签数据以便调试
        console.log('标签数据:', problemData.tagList || '无标签数据')

        // 打印测试用例数据
        console.log('测试用例数据:', problemData.problemCaseDataList || '无测试用例数据')

        // 处理代码模板数据 - 重点部分
        // 确保代码模板数据格式正确，尤其是自定义代码能正确回显
        if (problemData.codeTemplates && Array.isArray(problemData.codeTemplates)) {
          // 记录原始数据，便于排查问题
          console.log('代码模板原始数据:', JSON.stringify(problemData.codeTemplates))
          console.log('代码模板数量:', problemData.codeTemplates.length)

          // 标准化处理代码模板数据
          // 1. 确保每个模板都有languageId和code字段
          // 2. 标记为自定义模板，确保优先使用这些代码
          const formattedTemplates = problemData.codeTemplates.map(template => {
            // 创建标准化模板对象
            const formattedTemplate = {
              ...template,
              // 确保languageId字段存在（可能需要从id字段获取）
              languageId: template.languageId || template.id,
              // 确保code和codeTemplate字段存在（可能来自不同字段）
              code: template.code || template.codeTemplate || '',
              codeTemplate: template.codeTemplate || template.code || '',
              // 标记为自定义模板，这是关键标记，确保编辑时优先使用这些代码
              isCustomized: true
            };

            // 检查是否有实际内容，记录日志便于排查问题
            if (formattedTemplate.code && formattedTemplate.code.length > 0) {
              console.log(`模板 ${formattedTemplate.languageId} 包含自定义代码，长度: ${formattedTemplate.code.length}`);
            }

            // 打印每个模板的详细信息，便于排查问题
            console.log(`处理模板: ID=${formattedTemplate.languageId}, 代码: ${formattedTemplate.code?.substring(0, 50)}${formattedTemplate.code?.length > 50 ? '...' : ''}`);

            return formattedTemplate;
          });

          // 如果格式不一致，更新为标准化格式
          if (JSON.stringify(formattedTemplates) !== JSON.stringify(problemData.codeTemplates)) {
            console.log('更新代码模板格式，原格式数量:', problemData.codeTemplates.length)
            console.log('更新后格式数量:', formattedTemplates.length)
            problemData.codeTemplates = formattedTemplates as any;
          }

          // 添加额外的调试信息，确保模板数据正确处理
          console.log('最终格式化后的代码模板数据:', JSON.stringify(problemData.codeTemplates))
        } else {
          console.log('无代码模板数据')
        }

        // 调用表单组件的方法，传递数据
        problemRef.value?.acceptParams({
          title,
          row: problemData,
          api: updateProblemApi,
          getTableList: proTableRef.value?.getTableList
        })
      } else {
        ElMessage.error('获取题目详情失败')
      }
    } catch (error) {
      console.error('获取题目详情出错:', error)
      ElMessage.error('获取题目详情出错')
    }
  } else {
    // 新增模式
    problemRef.value?.acceptParams({
      title,
      row: { ...row },
      api: createProblemApi,
      getTableList: proTableRef.value?.getTableList
    })
  }
}
// 删除信息
const deleteInfo = async (params: IProblem.Row) => {
  await useHandleData(
    removeProblemApi,
    { ids: [params.id] },
    `删除【${params.id}】题目`
  )
  proTableRef.value?.getTableList()
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeProblemApi, { ids }, '删除所选题目')
  proTableRef.value?.clearSelection()
  proTableRef.value?.getTableList()
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>()
const ImportOssDialogRef = ref<InstanceType<typeof ImportProgramDialog>>()
const ImportBatchOssDialogRef = ref<InstanceType<typeof ImportBatchProgramDialog>>()
const importData = () => {
  const params = {
    title: '题目',
    templateName: '题目',
    tempApi: downloadTemplate,
    importApi: importProblemExcelApi,
    getTableList: proTableRef.value?.getTableList
  }
  ImportExcelRef.value?.acceptParams(params)
}
// OSS导入
const importOssData = () => {
  ImportOssDialogRef.value?.open()
}
// 批量OSS导入
const importBatchOssData = () => {
  ImportBatchOssDialogRef.value?.open()
}
// 导入成功回调
const handleImportSuccess = () => {
  proTableRef.value?.getTableList()
}
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as IProblem.Query);
  useDownload(exportProblemExcelApi, "题目", newParams);
};

// 获取难度等级对应的标签类型
const getDifficultyTagType = (difficulty: number) => {
  const enumValue = Object.values(DifficultyLevel).find(item => Number(item.code) === Number(difficulty))
  if (!enumValue) return 'info'

  switch (Number(difficulty)) {
    case Number(DifficultyLevel.ENTRY.code):
      return 'success'
    case Number(DifficultyLevel.EASY.code):
      return 'success'
    case Number(DifficultyLevel.MEDIUM.code):
      return 'warning'
    case Number(DifficultyLevel.HARD.code):
      return 'danger'
    case Number(DifficultyLevel.EXPERT.code):
      return 'info'
    default:
      return 'info'
  }
}

// 获取难度等级对应的样式
const getDifficultyStyle = (difficulty: number) => {
  const enumValue = Object.values(DifficultyLevel).find(item => Number(item.code) === Number(difficulty))
  if (!enumValue) return { backgroundColor: '#f4f4f5', color: '#909399' }

  switch (Number(difficulty)) {
    case Number(DifficultyLevel.ENTRY.code):
    case Number(DifficultyLevel.EASY.code):
      return {
        backgroundColor: '#f0f9eb',
        color: '#67c23a',
        borderRadius: '4px',
        fontWeight: 'bold'
      }
    case Number(DifficultyLevel.MEDIUM.code):
      return {
        backgroundColor: '#fdf6ec',
        color: '#e6a23c',
        borderRadius: '4px',
        fontWeight: 'bold'
      }
    case Number(DifficultyLevel.HARD.code):
      return {
        backgroundColor: '#fef0f0',
        color: '#f56c6c',
        borderRadius: '4px',
        fontWeight: 'bold'
      }
    case Number(DifficultyLevel.EXPERT.code):
      return {
        backgroundColor: '#f4f4f5',
        color: '#909399',
        borderRadius: '4px',
        fontWeight: 'bold'
      }
    default:
      return {
        backgroundColor: '#f4f4f5',
        color: '#909399',
        borderRadius: '4px',
        fontWeight: 'bold'
      }
  }
}

// 跳转到做题页面
const handleGoToSubmit = (row: IProblem.Row) => {
  router.push(`/oj/problem/submit/${row.id}`)
}
</script>

<style scoped>
.problem-table-container {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.problem-table {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.difficulty-tag {
  min-width: 60px;
  text-align: center;
  font-weight: bold;
}

.judge-mode-tag {
  min-width: 60px;
  text-align: center;
}

.solve-button {
  font-weight: bold;
}

:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.el-table__row:hover) {
  background-color: #ecf5ff;
}
</style>
