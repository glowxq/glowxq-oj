//-------------------------------------------------------------
// ProblemForm 组件 - 问题表单
// 本组件用于创建和编辑题目，包含基本信息、题目内容、样例数据和测试数据等
//-------------------------------------------------------------
<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="100%"
    top="0"
    fullscreen
    draggable
    class="problem-dialog"
  >
    <!-- 表单区域 -->
    <el-form
      ref="ruleFormRef"
      label-width="140px"
      label-suffix=" :"
      :rules="rules"
      :model="problemData"
      @submit.enter.prevent="handleSubmit"
      class="problem-form"
    >
      <!-- 表单顶部栏：选项卡 + 填充测试按钮 -->
      <div class="form-header">
        <!-- 分组选项卡 -->
        <el-tabs v-model="activeTab" type="card" class="problem-tabs">
        <!-- 基本信息选项卡 -->
        <el-tab-pane label="基本信息" name="basic">
          <BasicInfoForm
            :modelValue="problemData"
            @update:modelValue="(val: ProblemData) => Object.assign(problemData, val)"
            :is-edit="isEdit"
            @upload-success="handleUploadSuccess"
            @upload-error="handleUploadError"
            @markdown-mounted="handleMarkdownMounted"
            @next-step="handleNextStep"
          />
        </el-tab-pane>

        <!-- 题目内容选项卡 - 只对编程题显示 -->
        <el-tab-pane
          v-if="checkTabVisible('content', problemData.problemBo.problemType)"
          label="题目内容"
          name="content"
        >
          <ProgrammingContent
            :modelValue="problemData"
            @update:modelValue="(val: ProblemData) => Object.assign(problemData, val)"
            @upload-success="handleUploadSuccess"
            @upload-error="handleUploadError"
          />
        </el-tab-pane>

        <!-- 选项&答案选项卡 - 只对客观题显示 -->
        <el-tab-pane
          v-if="checkTabVisible('options', problemData.problemBo.problemType)"
          label="选项&答案"
          name="options"
        >
          <ObjectiveForm
            :modelValue="problemData"
            @update:modelValue="(val: ProblemData) => Object.assign(problemData, val)"
            @markdown-mounted="handleMarkdownMounted"
          />
        </el-tab-pane>

        <!-- 测试数据选项卡 - 只对编程题显示 -->
        <el-tab-pane
          v-if="checkTabVisible('testcase', problemData.problemBo.problemType)"
          label="测试数据"
          name="testcase"
        >
          <!-- 编辑模式警告提示 -->
          <div v-if="isEdit" class="edit-mode-warning testcase-warning">
            <el-alert
              title="编辑模式下的测试用例处理提示"
              type="warning"
              :closable="false"
              show-icon
            >
              <p><strong>重要提示：</strong>编辑模式下，如果修改测试用例，以最后上传的用例数据为准（不会把原有题目的用例数据合并，如果有合并需求，请下载数据后手动合并）</p>
            </el-alert>
          </div>

          <TestCaseForm
            :modelValue="problemData"
            @update:modelValue="(val: ProblemData) => Object.assign(problemData, val)"
            :is-edit="isEdit"
          />
        </el-tab-pane>

        <!-- 高级设置选项卡 - 只对编程题显示 -->
        <el-tab-pane
          v-if="checkTabVisible('advanced', problemData.problemBo.problemType)"
          label="评测设置"
          name="advanced"
        >
          <JudgeSettingsForm
            :modelValue="problemData"
            @update:modelValue="(val: ProblemData) => Object.assign(problemData, val)"
            :is-edit="isEdit"
          />
        </el-tab-pane>

        <!-- 代码模板选项卡 - 只对编程题显示 -->
        <el-tab-pane
          v-if="checkTabVisible('codetpl', problemData.problemBo.problemType)"
          label="代码模板"
          name="codetpl"
        >
          <!-- 代码模板调试信息 -->
          <el-collapse v-if="isDebug" class="template-debug-panel">
            <el-collapse-item name="1" title="代码模板调试信息">
              <div class="template-debug-info">
                <p>当前选中的语言ID: {{ getSelectedLanguageIds().join(', ') || '无' }}</p>
                <p>自定义模板数量: {{ getCustomizedTemplatesCount() }} /
                  总模板数量: {{ problemData.codeTemplates?.length || 0 }}
                </p>
                <div v-if="problemData.codeTemplates && problemData.codeTemplates.length > 0">
                  <p>自定义模板详情:</p>
                  <ul>
                    <li v-for="(tpl, index) in problemData.codeTemplates" :key="index">
                      ID: {{ tpl.languageId || tpl.id }},
                      代码长度: {{ (tpl.code || tpl.codeTemplate || '')?.length || 0 }}字符,
                      是否自定义: {{ getTemplateCustomizedStatus(tpl) }}
                    </li>
                  </ul>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>

          <LanguageTamplateSelect
            :modelValue="problemData.codeTemplates"
            @update:modelValue="(val) => problemData.codeTemplates = val"
            :default-selected="getSelectedLanguageIds()"
            :custom-templates="problemData.codeTemplates"
            :prioritize-custom-code="true"
            @confirm="handleLanguageTemplatesFinish"
            @cancel="activeTab = 'advanced'"
          />
        </el-tab-pane>
      </el-tabs>
        <!-- 填充测试题目按钮 - 表单右上角（仅新建时显示） -->
        <div v-if="!isEdit" class="test-data-buttons">
          <el-dropdown trigger="click" @command="fillTestData">
            <el-button type="warning" text bg size="small">
              填充测试题目
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="programming">编程题 - A+B Problem</el-dropdown-item>
                <el-dropdown-item command="singleChoice">单选题 - 数据结构</el-dropdown-item>
                <el-dropdown-item command="multipleChoice">多选题 - 排序算法</el-dropdown-item>
                <el-dropdown-item command="trueFalse">判断题 - 计算机常识</el-dropdown-item>
                <el-dropdown-item command="fillBlank">填空题 - 算法分析</el-dropdown-item>
                <el-dropdown-item command="shortAnswer">简答题 - 设计模式</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-form>

    <!-- 操作按钮区域 -->
    <template #footer>
      <div class="dialog-footer">
        <div class="dialog-footer-actions">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'

// 引入子组件
import BasicInfoForm from './form/BasicInfoForm.vue'
import ProgrammingContent from './form/ProgrammingContent.vue'
import ObjectiveForm from './form/ObjectiveForm.vue'
import TestCaseForm from './form/TestCaseForm.vue'
import JudgeSettingsForm from './form/JudgeSettingsForm.vue'
import LanguageTamplateSelect from '@/components/Oj/Meta/LanguageTamplateSelect.vue'

// 引入枚举
import { ProblemType } from '@/enums/oj/problem'
import { ArrowDown } from '@element-plus/icons-vue'

// 引入组合函数
import { useMarkdownEditor } from './composables/useMarkdownEditor'

// 引入配置
import { isTabVisible as checkTabVisible, getNextTab } from './config/problemTypeConfig'

// 引入类型定义
import type { DefaultParams, ProblemData } from './types'
import { JudgeMode } from '@/enums/oj/judge/JudgeMode'
import { JudgeCaseMode } from '@/enums/oj/judge/JudgeCaseMode'

defineOptions({
  name: 'ProblemForm'
})

//-------------------------------------------------------------
// 状态定义
//-------------------------------------------------------------

const rules = reactive({
  'problemBo.problemKey': [{ required: false, message: '请填写题目的自定义ID' }],
  'problemBo.title': [{ required: true, message: '请填写题目标题' }],
  'problemBo.author': [{ required: false, message: '请填写作者' }],
  'problemBo.programType': [{ required: true, message: '请选择程序类型' }],
  'problemBo.problemType': [{ required: true, message: '请选择题目类型' }],
  'problemBo.sourceType': [{ required: false, message: '请选择来源类型' }],
  'problemBo.timeLimit': [{ required: true, message: '请填写时间限制' }],
  'problemBo.memoryLimit': [{ required: true, message: '请填写内存限制' }],
  'problemBo.stackLimit': [{ required: true, message: '请填写栈限制' }],
  'problemBo.auth': [{ required: true, message: '请选择权限类型' }],
  'problemBo.description': [{ required: true, message: '请填写题目描述' }],
})

const visible = ref(false)
const activeTab = ref('basic')
const paramsProps = ref<DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 判断是否为编辑模式
const isEdit = computed(() => {
  return !!paramsProps.value.row?.id
})

// 初始化问题数据结构
const problemData = reactive<ProblemData>({
  problemBo: {
    id: null,
    problemKey: '',
    title: '',
    author: '',
    programType: 0,  // 0为ACM,1为OI
    problemType: String(ProblemType.PROGRAMMING.code), // 默认为编程题
    sourceType: 'GlowOJ', // 默认为GlowOJ
    timeLimit: 1000,
    memoryLimit: 262144,
    stackLimit: 128,
    description: '',
    input: '',
    output: '',
    examples: '',
    difficulty: 0,
    hint: '',
    auth: 1, // 1公开|2为私有|3为比赛题目
    ioScore: 0,
    score: 0,
    source: '',
    judgeMode: String(JudgeMode.DEFAULT.code),
    judgeCaseMode: String(JudgeCaseMode.DEFAULT.code),
    userExtraFile: '',
    judgeExtraFile: '',
    spjCode: '',
    spjLanguage: '',
    remote: false,
    codeShare: true,
    removeEndBlank: false,
    openCaseResult: false,
    uploadCase: false, // 如果为true，表示使用上传的测试用例文件
    groupProblem: false,
    fileIo: false,
    requireImage: false,
    changeModeCode: false,
    changeJudgeCaseMode: false,
    caseVersion: '',
    modifiedUser: '',
    applyPublicProgress: null,
    ioReadFileName: '',
    ioWriteFileName: '',
    languageIds: [],
    tagIds: [],
    // 添加客观题所需字段
    answer: null, // 判断题答案
    referenceAnswer: '', // 简答题参考答案
    gradingCriteria: '', // 简答题评分标准
  },
  problemCaseDataList: [],
  codeTemplates: [],
  uploadTestcaseDir: '', // 测试用例目录存储路径
  options: [],
  blanks: []
})

// 使用Markdown编辑器组合函数
const {
  handleUploadSuccess,
  handleUploadError,
  handleMarkdownMounted,
  resetProcessedEvents
} = useMarkdownEditor()

// 监听题目类型变化
watch(
  () => problemData.problemBo.problemType,
  (newType) => {
    console.log('题目类型变更为:', newType)

    // 检查当前选中的tab在新题目类型下是否可见
    if (!checkTabVisible(activeTab.value, newType)) {
      // 如果当前tab在新题目类型下不可见，则切换到基本信息tab
      activeTab.value = 'basic'
    }

    // 初始化客观题选项
    if ((newType === ProblemType.SINGLE_CHOICE.code || newType === ProblemType.MULTIPLE_CHOICE.code)
        && (!problemData.options || problemData.options.length === 0)) {
      // 为选择题添加初始选项A、B、C、D
      problemData.options = [
        {
          optionKey: 'A',
          optionContent: '',
          score: newType === ProblemType.SINGLE_CHOICE.code ? 10 : 5,
          answer: false
        },
        {
          optionKey: 'B',
          optionContent: '',
          score: newType === ProblemType.SINGLE_CHOICE.code ? 10 : 5,
          answer: false
        },
        {
          optionKey: 'C',
          optionContent: '',
          score: newType === ProblemType.SINGLE_CHOICE.code ? 10 : 5,
          answer: false
        },
        {
          optionKey: 'D',
          optionContent: '',
          score: newType === ProblemType.SINGLE_CHOICE.code ? 10 : 5,
          answer: false
        }
      ]
    } else if (newType === ProblemType.FILL_BLANK.code && (!problemData.blanks || problemData.blanks.length === 0)) {
      // 为填空题添加初始空白
      problemData.blanks = [
        {
          optionKey: '填空1',
          optionContent: '',
          score: 5,
          answer: true
        }
      ]
    }
  }
)

// 监听代码模板数据变化，确保格式一致性
watch(
  () => problemData.codeTemplates,
  (templates) => {
    if (!templates || templates.length === 0) return;

    console.log('代码模板数据变化:', templates)

    // 转换模板数据格式，确保兼容性和一致性
    const formattedTemplates = templates.map(template => {
      // 确保languageId字段存在（可能需要从id字段转换）
      const languageId = template.languageId || (template.id ? Number(template.id) : undefined);

      // 判断是否为自定义模板
      const isCustomizedTemplate = (template as any).isCustomized === true;

      // 根据模板来源优先选择代码内容
      // 如果是自定义模板，优先保留其原有代码
      const code = isCustomizedTemplate ?
                   (template.code || template.codeTemplate || '') :
                   (template.code || template.codeTemplate || '');

      const codeTemplate = isCustomizedTemplate ?
                          (template.codeTemplate || template.code || '') :
                          (template.codeTemplate || template.code || '');

      console.log(`处理模板 ID:${template.id || languageId}, 是否自定义:${isCustomizedTemplate}, 代码长度:${code?.length || 0}`);

      // 创建规范化的模板对象，确保所有必要字段都存在
      const formattedTemplate = {
        id: template.id,
        name: template.name || '',
        description: template.description || '',
        languageId: languageId,
        code: code,
        codeTemplate: codeTemplate,
        compileCommand: template.compileCommand || '',
        contentType: template.contentType || '',
        oj: template.oj || '',
        template: template.template || '',
        spjEnable: template.spjEnable || 0,
        seq: template.seq || 0,
        // 保留自定义标记，确保后续处理时能识别
        isCustomized: (template as any).isCustomized || false
      };

      return formattedTemplate;
    });

    // 如果格式不一致，则更新（避免无限循环）
    if (JSON.stringify(formattedTemplates) !== JSON.stringify(templates)) {
      console.log('更新代码模板数据格式:')
      console.log('原格式:', JSON.stringify(templates))
      console.log('新格式:', JSON.stringify(formattedTemplates))
      problemData.codeTemplates = formattedTemplates;
    }
  },
  { deep: true }
)

// 调试模式开关
const isDebug = ref(true); // 开启调试信息输出，方便排查问题

//-------------------------------------------------------------
// 方法定义
//-------------------------------------------------------------

/**
 * 接收父组件传过来的参数
 * @param params 父组件传递的参数
 */
const acceptParams = (params: DefaultParams) => {
  paramsProps.value = params

  // 重置Markdown编辑器的事件处理状态
  resetProcessedEvents()

  // 重置表单为默认状态
  resetForm()

  // 如果是编辑模式，转换数据格式
  if (params.row && params.row.id) {
    console.log('编辑模式，填充数据:', params.row)

    try {
      // 填充数据（使用简化的方法）
      fillFormData(params.row)
      console.log('数据填充完成:', problemData)
    } catch (error) {
      console.error('处理编辑数据时出错:', error)
    }
  }

  visible.value = true
}

/**
 * 重置表单为默认值
 */
const resetForm = () => {
  problemData.problemBo = {
    id: null,
    problemKey: '',
    title: '',
    author: '',
    programType: 0, // 确保默认值为0 (ACM)
    problemType: String(ProblemType.PROGRAMMING.code),
    sourceType: 'GlowOJ', // 确保默认值为GlowOJ
    timeLimit: 1000,
    memoryLimit: 262144,
    stackLimit: 128,
    description: '', // 确保为空字符串而不是null
    input: '', // 确保为空字符串而不是null
    output: '', // 确保为空字符串而不是null
    examples: '', // 确保为空字符串而不是null
    difficulty: 0,
    hint: '', // 确保为空字符串而不是null
    auth: 1,
    ioScore: 0,
    score: 0,
    source: '',
    judgeMode: String(JudgeMode.DEFAULT.code),
    judgeCaseMode: String(JudgeCaseMode.DEFAULT.code),
    userExtraFile: '', // 确保为空字符串而不是null
    judgeExtraFile: '', // 确保为空字符串而不是null
    spjCode: '', // 确保为空字符串而不是null
    spjLanguage: '',
    remote: false,
    codeShare: true,
    removeEndBlank: false,
    openCaseResult: false,
    uploadCase: false,
    groupProblem: false,
    fileIo: false,
    requireImage: false,
    changeModeCode: false,
    changeJudgeCaseMode: false,
    caseVersion: '',
    modifiedUser: '',
    applyPublicProgress: null,
    ioReadFileName: '',
    ioWriteFileName: '',
    languageIds: [],
    tagIds: [],
    // 添加客观题所需字段
    answer: null, // 判断题答案
    referenceAnswer: '', // 简答题参考答案，确保为空字符串而不是null
    gradingCriteria: '', // 简答题评分标准，确保为空字符串而不是null
  }
  problemData.problemCaseDataList = []
  problemData.codeTemplates = []
  problemData.uploadTestcaseDir = ''

  // 清空客观题数据
  problemData.options = []
  problemData.blanks = []
}

/**
 * 填充表单数据
 * @param data 需要填充的数据
 */
const fillFormData = (data: Record<string, any>) => {
  // 定义需要确保为字符串类型的字段（Markdown编辑器相关字段）
  const stringFields = ['description', 'input', 'output', 'examples', 'hint', 'referenceAnswer', 'gradingCriteria', 'spjCode', 'userExtraFile', 'judgeExtraFile']

  // 处理problemBo数据
  if (data.problemBo) {
    // 处理嵌套的problemBo结构
    Object.keys(data.problemBo).forEach(key => {
      if (key in problemData.problemBo && data.problemBo[key] !== undefined) {
        // 确保字符串字段不为null或undefined
        if (stringFields.includes(key) && (data.problemBo[key] === null || data.problemBo[key] === undefined)) {
          problemData.problemBo[key] = ''
        } else {
          problemData.problemBo[key] = data.problemBo[key]
        }
      }
    })
  } else {
    // 处理扁平结构
    Object.keys(problemData.problemBo).forEach(key => {
      if (data[key] !== undefined) {
        // 确保字符串字段不为null或undefined
        if (stringFields.includes(key) && (data[key] === null || data[key] === undefined)) {
          problemData.problemBo[key] = ''
        } else {
          problemData.problemBo[key] = data[key]
        }
      }
    })
  }

  // 确保ID正确设置
  if (data.id) {
    problemData.problemBo.id = data.id
  }

  // 处理数组数据 - 使用类型安全的方式
  if (Array.isArray(data.samples)) {
    problemData.problemCaseDataList = [...data.samples]
  }

  if (Array.isArray(data.problemCaseDataList)) {
    problemData.problemCaseDataList = [...data.problemCaseDataList]
  }

  // 处理代码模板数据 - 确保正确处理从API返回的数据
  if (Array.isArray(data.codeTemplates)) {
    console.log('原始代码模板数据:', JSON.stringify(data.codeTemplates))

    // 处理并标准化代码模板数据，确保保留自定义代码
    problemData.codeTemplates = data.codeTemplates.map((template: any) => {
      // 确保languageId字段存在（API可能返回不同格式）
      const languageId = template.languageId || (template.id ? Number(template.id) : undefined);

      // 创建标准化的模板对象，特别注意保留用户自定义的代码内容
      const formattedTemplate = {
        id: template.id,
        name: template.name || '',
        description: template.description || '',
        languageId: languageId,
        // 确保保留API返回的代码内容，这些是用户之前自定义的
        code: template.code || template.codeTemplate || '',
        codeTemplate: template.codeTemplate || template.code || '',
        // 其他元数据
        compileCommand: template.compileCommand || '',
        contentType: template.contentType || '',
        oj: template.oj || '',
        template: template.template || '',
        spjEnable: template.spjEnable || 0,
        seq: template.seq || 0,
        // 添加标记，表示这是从API获取的自定义模板
        // 这个标记很重要，确保后续处理时优先使用这些代码
        isCustomized: true
      };

      console.log('处理后的模板数据(含自定义代码):', formattedTemplate)
      return formattedTemplate;
    });

    console.log('处理后的完整代码模板数据(含自定义代码):', problemData.codeTemplates)
  } else {
    console.log('未找到代码模板数据或格式不正确:', data.codeTemplates)
  }

  // 处理选项数据
  if (Array.isArray(data.options)) {
    problemData.options = [...data.options]
  }

  // 处理填空题数据 - 填空题数据应该存储在options字段
  if (Array.isArray(data.blanks) && data.blanks.length > 0) {
    // 如果是填空题类型，将blanks数据转移到options
    if (String(data.problemType) === String(ProblemType.FILL_BLANK.code) ||
        String(problemData.problemBo.problemType) === String(ProblemType.FILL_BLANK.code)) {
      // 如果options字段为空或不存在，使用blanks数据填充
      if (!problemData.options || problemData.options.length === 0) {
        problemData.options = [...data.blanks]
      }
      // 清空blanks数据，避免重复
      problemData.blanks = []
    } else {
      // 非填空题保留blanks数据
      problemData.blanks = [...data.blanks]
    }
  }

  // 处理测试用例目录
  if (data.uploadTestcaseDir) {
    problemData.uploadTestcaseDir = data.uploadTestcaseDir
  }

  // 处理布尔值字段
  const booleanFields = ['remote', 'codeShare', 'removeEndBlank', 'openCaseResult',
                        'uploadCase', 'groupProblem', 'fileIo', 'requireImage',
                        'changeModeCode', 'changeJudgeCaseMode']

  booleanFields.forEach(field => {
    if (data[field] !== undefined) {
      // 将各种格式转为布尔值
      const value = data[field]
      if (typeof value === 'string') {
        problemData.problemBo[field] = value.toLowerCase() === 'true'
      } else if (typeof value === 'number') {
        problemData.problemBo[field] = value !== 0
      } else if (typeof value === 'boolean') {
        problemData.problemBo[field] = value
      }
    }
  })

  // 处理标签和语言ID
  if (data.tagList && Array.isArray(data.tagList)) {
    // 如果存在tagList字段，从中提取标签ID
    problemData.problemBo.tagIds = data.tagList.map(tag => tag.id);
  } else if (data.tagIds) {
    problemData.problemBo.tagIds = Array.isArray(data.tagIds)
      ? [...data.tagIds]
      : (typeof data.tagIds === 'string' ? data.tagIds.split(',').map(id => Number(id)) : [])
  }

  if (data.languageIds) {
    problemData.problemBo.languageIds = Array.isArray(data.languageIds)
      ? [...data.languageIds]
      : (typeof data.languageIds === 'string' ? data.languageIds.split(',').map(id => Number(id)) : [])
  }

  // 调试输出
  console.log('填充后的数据:', {
    id: problemData.problemBo.id,
    problemType: problemData.problemBo.problemType,
    codeTemplates: problemData.codeTemplates.length,
    tagsCount: problemData.problemBo.tagIds?.length || 0,
    languagesCount: problemData.problemBo.languageIds?.length || 0
  })
}

/**
 * 提交表单数据（新增/编辑）
 */
const ruleFormRef = ref<InstanceType<typeof ElForm>>()
const handleSubmit = () => {
  ruleFormRef.value?.validate(async (valid) => {
    if (!valid) return

    try {
      // 确保在编辑模式下包含ID字段
      if (paramsProps.value.row && paramsProps.value.row.id) {
        // 直接设置problemBo中的id
        problemData.problemBo.id = paramsProps.value.row.id
      }

      // 确保填空题数据通过options字段提交
      if (String(problemData.problemBo.problemType) === String(ProblemType.FILL_BLANK.code)) {
        // 如果是填空题，确保blanks数据为空，避免数据重复
        problemData.blanks = []
      }

      // 将表单数据转换为适合API的格式
      const apiData = {
        id: problemData.problemBo.id, // 显式添加id字段
        ...problemData,
        // 确保tagIds在顶层也可以被访问
        tagIds: problemData.problemBo.tagIds || []
      }

      // 特殊处理samples字段，确保type字段只传递字符串
      if (apiData.problemCaseDataList && Array.isArray(apiData.problemCaseDataList)) {
        apiData.problemCaseDataList = apiData.problemCaseDataList.map(sample => {
          // 如果type是对象，则只取对象的code属性
          if (sample.type && typeof sample.type === 'object' && 'code' in sample.type) {
            return {
              ...sample,
              type: (sample.type as any).code as string
            }
          }
          return sample
        })
      }

      console.log('提交的数据:', apiData)
      // 显示tagIds日志，方便调试
      console.log('提交的标签IDs:', apiData.tagIds)
      console.log('提交的problemBo.tagIds:', problemData.problemBo.tagIds)

      await paramsProps.value.api!(apiData)
      ElMessage.success({ message: `${paramsProps.value.title}成功！` })
      paramsProps.value.getTableList!()
      visible.value = false
    } catch (error) {
      console.error('提交数据时出错:', error)
      ElMessage.error('提交失败，请检查数据格式')
    }
  })
}

/**
 * 处理下一步按钮点击
 * @param problemType 问题类型
 */
const handleNextStep = (problemType: string) => {
  // 使用配置获取下一步的tab
  activeTab.value = getNextTab(problemType)
  console.log(`题目类型 ${problemType} 的下一步跳转到: ${activeTab.value}`)
}

/**
 * 获取已选择的语言ID列表
 * 这个方法用于获取当前选中的语言ID，传递给LanguageTamplateSelect组件
 * 确保编辑时能正确选中已保存的语言模板
 */
const getSelectedLanguageIds = (): string[] => {
  if (!problemData.codeTemplates || problemData.codeTemplates.length === 0) {
    console.log('无代码模板数据，返回空数组')
    return []
  }

  console.log('从codeTemplates中提取语言ID:', JSON.stringify(problemData.codeTemplates))

  // 提取languageId，优先使用languageId字段，如果不存在则使用id字段
  const languageIds = problemData.codeTemplates
    .filter(template => template.languageId !== undefined || template.id !== undefined)
    .map(template => {
      const id = template.languageId || template.id;
      // 检查是否有自定义代码内容
      const hasCode = template.code && template.code.trim().length > 0;
      const hasCodeTemplate = template.codeTemplate && template.codeTemplate.trim().length > 0;
      const hasCustomCode = hasCode || hasCodeTemplate;

      // 记录详细信息，便于调试
      console.log(`模板 ${JSON.stringify({
        id,
        languageId: template.languageId,
        hasCode: hasCustomCode,
        isCustomized: (template as any).isCustomized
      })} 提取ID: ${id}`);

      return String(id); // 确保返回字符串类型，与组件要求一致
    });

  console.log('提取到的语言ID:', languageIds);

  // 如果没有任何ID，返回空数组并记录警告
  if (languageIds.length === 0) {
    console.warn('未能提取到任何有效的语言ID');
  }

  return languageIds;
}

/**
 * 处理语言模板选择完成
 * 在用户完成代码模板选择和编辑后调用
 */
const handleLanguageTemplatesFinish = () => {
  // 语言模板数据已通过v-model实时同步，这里只需处理UI操作
  console.log('语言模板设置已完成, 当前模板数量:', problemData.codeTemplates.length)

  // 检查模板中是否包含自定义代码
  if (problemData.codeTemplates && problemData.codeTemplates.length > 0) {
    const customizedCount = problemData.codeTemplates.filter(t => (t as any).isCustomized).length;
    const withCodeCount = problemData.codeTemplates.filter(t =>
      (t.code && t.code.trim().length > 0) ||
      (t.codeTemplate && t.codeTemplate.trim().length > 0)
    ).length;

    console.log(`已选择 ${problemData.codeTemplates.length} 个模板，其中 ${customizedCount} 个自定义模板，${withCodeCount} 个包含代码`);
  }

  // 切换到高级设置选项卡
  activeTab.value = 'advanced'

  // 显示成功提示
  ElMessage.success('代码模板设置成功')
}

/**
 * 获取自定义模板数量
 */
const getCustomizedTemplatesCount = () => {
  if (!problemData.codeTemplates) return 0
  return problemData.codeTemplates.filter(t => (t as any).isCustomized).length
}

/**
 * 获取模板自定义状态
 */
const getTemplateCustomizedStatus = (template: any) => {
  return (template as any).isCustomized ? '是' : '否'
}

/**
 * 填充测试题目数据
 * @param type 测试题目类型
 */
const fillTestData = (type: string) => {
  // 先重置表单
  resetForm()

  switch (type) {
    case 'programming':
      fillProgrammingTest()
      break
    case 'singleChoice':
      fillSingleChoiceTest()
      break
    case 'multipleChoice':
      fillMultipleChoiceTest()
      break
    case 'trueFalse':
      fillTrueFalseTest()
      break
    case 'fillBlank':
      fillFillBlankTest()
      break
    case 'shortAnswer':
      fillShortAnswerTest()
      break
  }

  activeTab.value = 'basic'
  ElMessage.success('测试题目数据已填充')
}

/** 编程题测试数据 - A+B Problem */
const fillProgrammingTest = () => {
  problemData.problemBo.problemType = String(ProblemType.PROGRAMMING.code)
  problemData.problemBo.title = 'A+B Problem'
  problemData.problemBo.author = '测试管理员'
  problemData.problemBo.programType = 0
  problemData.problemBo.difficulty = 0
  problemData.problemBo.sourceType = 'GlowOJ'
  problemData.problemBo.source = 'GlowOJ 测试题库'
  problemData.problemBo.auth = 1
  problemData.problemBo.timeLimit = 1000
  problemData.problemBo.memoryLimit = 262144
  problemData.problemBo.stackLimit = 128
  problemData.problemBo.description = '## 题目描述\n\n计算两个整数 $a$ 和 $b$ 的和。\n\n## 输入格式\n\n一行两个整数 $a$ 和 $b$，以空格分隔。\n\n## 输出格式\n\n一行一个整数，表示 $a + b$ 的值。\n\n## 数据范围\n\n$-10^9 \\leq a, b \\leq 10^9$'
  problemData.problemBo.input = '两个整数 a 和 b，以空格分隔'
  problemData.problemBo.output = '一个整数，表示 a + b 的值'
  problemData.problemBo.examples = '### 样例输入\n```\n1 2\n```\n### 样例输出\n```\n3\n```'
  problemData.problemBo.hint = '这是一道入门级别的题目，直接读入两个整数并输出它们的和即可。'
  problemData.problemCaseDataList = [
    { input: '1 2', output: '3', type: 'ManualEditing', score: 10, groupNum: '1' },
    { input: '0 0', output: '0', type: 'ManualEditing', score: 10, groupNum: '1' },
    { input: '-1 1', output: '0', type: 'ManualEditing', score: 10, groupNum: '1' },
    { input: '1000000000 1000000000', output: '2000000000', type: 'ManualEditing', score: 10, groupNum: '1' },
    { input: '-1000000000 -1000000000', output: '-2000000000', type: 'ManualEditing', score: 10, groupNum: '1' },
  ]
}

/** 单选题测试数据 */
const fillSingleChoiceTest = () => {
  problemData.problemBo.problemType = String(ProblemType.SINGLE_CHOICE.code)
  problemData.problemBo.title = '栈的基本特性'
  problemData.problemBo.author = '测试管理员'
  problemData.problemBo.difficulty = 1
  problemData.problemBo.auth = 1
  problemData.problemBo.score = 10
  problemData.problemBo.description = '以下关于栈（Stack）的描述中，正确的是？'
  problemData.options = [
    { optionKey: 'A', optionContent: '栈是一种先进先出（FIFO）的数据结构', score: 10, answer: false },
    { optionKey: 'B', optionContent: '栈是一种后进先出（LIFO）的数据结构', score: 10, answer: true },
    { optionKey: 'C', optionContent: '栈只能用数组实现，不能用链表实现', score: 10, answer: false },
    { optionKey: 'D', optionContent: '栈允许在任意位置插入和删除元素', score: 10, answer: false },
  ]
}

/** 多选题测试数据 */
const fillMultipleChoiceTest = () => {
  problemData.problemBo.problemType = String(ProblemType.MULTIPLE_CHOICE.code)
  problemData.problemBo.title = '稳定排序算法'
  problemData.problemBo.author = '测试管理员'
  problemData.problemBo.difficulty = 2
  problemData.problemBo.auth = 1
  problemData.problemBo.score = 10
  problemData.problemBo.description = '以下排序算法中，哪些是**稳定的**排序算法？（多选）'
  problemData.options = [
    { optionKey: 'A', optionContent: '冒泡排序（Bubble Sort）', score: 5, answer: true },
    { optionKey: 'B', optionContent: '快速排序（Quick Sort）', score: 5, answer: false },
    { optionKey: 'C', optionContent: '归并排序（Merge Sort）', score: 5, answer: true },
    { optionKey: 'D', optionContent: '插入排序（Insertion Sort）', score: 5, answer: true },
  ]
}

/** 判断题测试数据 */
const fillTrueFalseTest = () => {
  problemData.problemBo.problemType = String(ProblemType.TRUE_FALSE.code)
  problemData.problemBo.title = 'TCP 与 UDP 的区别'
  problemData.problemBo.author = '测试管理员'
  problemData.problemBo.difficulty = 1
  problemData.problemBo.auth = 1
  problemData.problemBo.score = 5
  problemData.problemBo.description = 'TCP 协议是面向连接的、可靠的传输层协议，而 UDP 协议是无连接的、不可靠的传输层协议。'
  problemData.problemBo.answer = true
}

/** 填空题测试数据 */
const fillFillBlankTest = () => {
  problemData.problemBo.problemType = String(ProblemType.FILL_BLANK.code)
  problemData.problemBo.title = '算法时间复杂度分析'
  problemData.problemBo.author = '测试管理员'
  problemData.problemBo.difficulty = 2
  problemData.problemBo.auth = 1
  problemData.problemBo.score = 15
  problemData.problemBo.description = '1. 冒泡排序的平均时间复杂度为 ___。\n\n2. 二分查找的时间复杂度为 ___。\n\n3. 归并排序的空间复杂度为 ___。'
  problemData.options = [
    { optionKey: '填空1', optionContent: 'O(n^2)', score: 5, answer: true },
    { optionKey: '填空2', optionContent: 'O(log n)', score: 5, answer: true },
    { optionKey: '填空3', optionContent: 'O(n)', score: 5, answer: true },
  ]
}

/** 简答题测试数据 */
const fillShortAnswerTest = () => {
  problemData.problemBo.problemType = String(ProblemType.SHORT_ANSWER.code)
  problemData.problemBo.title = '解释单例模式'
  problemData.problemBo.author = '测试管理员'
  problemData.problemBo.difficulty = 2
  problemData.problemBo.auth = 1
  problemData.problemBo.score = 20
  problemData.problemBo.description = '请简要描述设计模式中的**单例模式（Singleton Pattern）**，包括其定义、应用场景和一种常见的实现方式。'
  problemData.problemBo.referenceAnswer = '单例模式是一种创建型设计模式，确保一个类只有一个实例，并提供一个全局访问点。\n\n**应用场景：**\n- 数据库连接池\n- 日志管理器\n- 配置管理器\n- 线程池\n\n**实现方式（懒汉式）：**\n```java\npublic class Singleton {\n    private static volatile Singleton instance;\n    private Singleton() {}\n    public static Singleton getInstance() {\n        if (instance == null) {\n            synchronized (Singleton.class) {\n                if (instance == null) {\n                    instance = new Singleton();\n                }\n            }\n        }\n        return instance;\n    }\n}\n```'
  problemData.problemBo.gradingCriteria = '1. 正确描述单例模式的定义（5分）\n2. 列举至少2个应用场景（5分）\n3. 给出一种正确的实现方式（10分）'
}

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss">
.problem-dialog {
  display: flex;
  flex-direction: column;

  :deep(.el-dialog__body) {
    padding: 0 20px;
    overflow-y: auto;
    max-height: calc(100vh - 170px); /* 保留顶部和底部空间 */
  }

  :deep(.el-dialog__footer) {
    padding: 15px 20px;
    position: sticky;
    bottom: 0;
    background-color: white;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    z-index: 10;
  }
}

.problem-form {
  height: 100%;
}

.form-header {
  display: flex;
  align-items: flex-start;
  height: 100%;
}

.problem-tabs {
  flex: 1;
  height: 100%;
  min-width: 0;

  :deep(.el-tabs__content) {
    padding-bottom: 60px; /* 确保内容底部有足够空间，不会被footer遮挡 */
  }
}

.test-data-buttons {
  flex-shrink: 0;
  margin-left: 12px;
  margin-top: 2px; /* 与选项卡头部对齐 */
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.dialog-footer-actions {
  text-align: right;
}

.edit-mode-warning {
  margin-top: 15px;
  margin-bottom: 10px;

  p {
    margin: 8px 0;
    line-height: 1.5;
  }

  strong {
    font-weight: bold;
    color: #B33A00;
  }
}

.testcase-warning {
  margin: 0 0 20px 0;
  border: 2px solid #E6A23C;
  border-radius: 4px;
}

.template-debug-panel {
  border: 1px dashed #ccc;
  padding: 15px;
  margin: 10px 0;
  border-radius: 6px;
  background-color: #f9f9f9;
}

.template-debug-info {
  padding: 10px;

  h3 {
    margin-bottom: 10px;
  }

  p {
    margin: 5px 0;
  }

  ul {
    margin-left: 20px;
  }

  li {
    margin: 5px 0;
  }
}
</style>
