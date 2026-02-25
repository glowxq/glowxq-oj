<template>
  <div
    class="problem-submit-container"
    :class="{ 'devtools-open': isDevToolsOpen }"
  >
    <!-- 调试信息 -->
    <div
      class="debug-info"
      v-if="showDebug"
    >
      <h3>
        调试信息 <el-button
          size="small"
          @click="showDebug = false"
        >
          关闭
        </el-button>
      </h3>
      <div>
        <h4>控制台检测状态</h4>
        <div class="debug-section">
          <p><strong>控制台状态:</strong> {{ isDevToolsOpen ? '已打开' : '未打开' }}</p>
          <p><strong>检测方法:</strong> {{ devToolsDetectionResult?.method || 'none' }}</p>
          <p><strong>窗口尺寸:</strong> {{ windowWidth }}x{{ windowHeight }}</p>
          <p><strong>屏幕尺寸:</strong> {{ windowRef.screen.width }}x{{ windowRef.screen.height }}</p>
          <p><strong>高度差异:</strong> {{ devToolsDetectionResult?.windowInfo?.heightDiff || 0 }}px</p>
          <p><strong>宽度差异:</strong> {{ devToolsDetectionResult?.windowInfo?.widthDiff || 0 }}px</p>
          <p><strong>高度比例:</strong> {{ devToolsDetectionResult?.windowInfo?.heightRatio?.toFixed(3) || '0.000' }}</p>
        </div>

        <h4>题目数据</h4>
        <pre>{{ JSON.stringify(problemDetails, null, 2) }}</pre>

        <h4>ProblemType枚举</h4>
        <pre>{{ JSON.stringify(ProblemType, null, 2) }}</pre>
      </div>
    </div>

    <!-- 加载中状态 -->
    <div
      v-if="loading"
      class="loading-container"
    >
      <el-skeleton
        :rows="10"
        animated
      />
    </div>

    <!-- 数据加载失败 -->
    <el-empty
      v-else-if="loadFailed"
      description="题目数据加载失败，请重试"
      class="empty-container"
    >
      <el-button
        type="primary"
        @click="fetchProblemDetails"
      >
        重新加载
      </el-button>
      <el-button @click="showDebug = true">
        显示调试信息
      </el-button>
    </el-empty>

    <!-- 题目数据 -->
    <template v-else-if="problemDetails.problemBo?.id">
      <!-- 添加题目列表弹出抽屉 -->
      <el-drawer
        v-model="problemDrawerVisible"
        title="题目列表"
        size="300px"
        :show-close="true"
        direction="rtl"
      >
        <div class="problem-drawer-content">
          <div
            v-if="problemList.length > 0"
            class="problem-tabs"
          >
            <div
              v-for="(problem, index) in problemList"
              :key="problem.id"
              class="problem-tab-item"
              :class="{ 'active': index === currentProblemIndex }"
              @click="navigateToProblem(problem, index)"
            >
              <div class="tab-index">
                #{{ index + 1 }}
              </div>
              <div class="tab-title">
                {{ problem.title }}
              </div>
            </div>
          </div>
          <el-empty
            v-else
            description="无题目数据"
          />
        </div>
      </el-drawer>

      <div class="problem-submit-content">
        <!-- 左侧题目详情 -->
        <div class="problem-detail-panel">
          <!-- 题目标题和信息 -->
          <div class="problem-header">
            <ProblemHeader
              :problem="problemDetails.problemBo"
              v-model:initial-show-info="showProblemInfo"
            >
              <template #sceneType>
                <div class="scene-type-wrapper">
                  <enum-show
                    class="scene-type-tag"
                    :code="judgeSceneTypeFromQuery"
                    :enum="JudgeSceneType"
                  />
                </div>
              </template>

              <template #tags>
                <TagShow :tag-list="tagList" />
              </template>
            </ProblemHeader>
          </div>

          <!-- 题目内容 -->
          <div class="problem-content">
            <!-- 根据题目类型显示不同的内容 -->
            <div
              v-if="checkIsProgrammingProblem()"
              class="programming-problem"
            >
              <!-- 题目描述 -->
              <div class="content-section">
                <div class="section-header">
                  <div class="section-title">
                    题目描述
                  </div>
                  <el-button
                    type="text"
                    @click="copyContent('description')"
                    class="copy-btn"
                  >
                    <el-icon>
                      <DocumentCopy />
                    </el-icon> 复制
                  </el-button>
                </div>
                <div
                  class="markdown-content"
                  v-if="problemDetails.problemBo?.description"
                  ref="descriptionContent"
                >
                  <MdPreview
                    :model-value="problemDetails.problemBo.description"
                    :height="'auto'"
                  />
                </div>
                <el-empty
                  v-else
                  description="无题目描述"
                />
              </div>

              <!-- 输入和输出描述显示在同一行 -->
              <div
                class="io-description-row"
                v-if="problemDetails.problemBo?.input || problemDetails.problemBo?.output"
              >
                <div
                  class="io-description-col"
                  v-if="problemDetails.problemBo?.input"
                >
                  <div class="section-header">
                    <div class="section-title">
                      输入描述
                    </div>
                    <el-button
                      type="text"
                      @click="copyContent('input')"
                      class="copy-btn"
                    >
                      <el-icon>
                        <DocumentCopy />
                      </el-icon> 复制
                    </el-button>
                  </div>
                  <div
                    class="markdown-content"
                    ref="inputContent"
                  >
                    <MdPreview
                      :model-value="problemDetails.problemBo.input"
                      :height="'auto'"
                    />
                  </div>
                </div>

                <div
                  class="io-description-col"
                  v-if="problemDetails.problemBo?.output"
                >
                  <div class="section-header">
                    <div class="section-title">
                      输出描述
                    </div>
                    <el-button
                      type="text"
                      @click="copyContent('output')"
                      class="copy-btn"
                    >
                      <el-icon>
                        <DocumentCopy />
                      </el-icon> 复制
                    </el-button>
                  </div>
                  <div
                    class="markdown-content"
                    ref="outputContent"
                  >
                    <MdPreview
                      :model-value="problemDetails.problemBo.output"
                      :height="'auto'"
                    />
                  </div>
                </div>
              </div>

              <template v-if="problemDetails.problemBo?.examples">
                <div class="content-section">
                  <div class="section-header">
                    <div class="section-title">
                      示例
                    </div>
                    <el-button
                      type="text"
                      @click="copyContent('examples')"
                      class="copy-btn"
                    >
                      <el-icon>
                        <DocumentCopy />
                      </el-icon> 复制
                    </el-button>
                  </div>
                  <div
                    class="markdown-content example-content"
                    ref="examplesContent"
                  >
                    <MdPreview
                      :model-value="problemDetails.problemBo.examples"
                      :show-code-row-number="false"
                      :height="'auto'"
                    />
                  </div>
                </div>
              </template>

              <template v-if="problemDetails.problemBo?.hint">
                <div class="content-section">
                  <div class="section-header">
                    <div class="section-title">
                      提示
                    </div>
                    <el-button
                      type="text"
                      @click="copyContent('hint')"
                      class="copy-btn"
                    >
                      <el-icon>
                        <DocumentCopy />
                      </el-icon> 复制
                    </el-button>
                  </div>
                  <div
                    class="markdown-content"
                    ref="hintContent"
                  >
                    <MdPreview
                      :model-value="problemDetails.problemBo.hint"
                      :height="'auto'"
                    />
                  </div>
                </div>
              </template>
            </div>

            <!-- 客观题显示 -->
            <div
              v-else
              class="objective-problem"
            >
              <div class="content-section">
                <div class="section-header">
                  <div class="section-title">
                    题目描述
                  </div>
                  <el-button
                    type="text"
                    @click="copyContent('description')"
                    class="copy-btn"
                  >
                    <el-icon>
                      <DocumentCopy />
                    </el-icon> 复制
                  </el-button>
                </div>
                <div
                  class="markdown-content"
                  v-if="problemDetails.problemBo?.description"
                  ref="descriptionContent"
                >
                  <MdPreview
                    :model-value="problemDetails.problemBo.description"
                    :height="'auto'"
                  />
                </div>
                <el-empty
                  v-else
                  description="无题目描述"
                />
              </div>

              <div class="options-container">
                <template
                  v-if="[ProblemType.SINGLE_CHOICE.code, ProblemType.MULTIPLE_CHOICE.code].includes(problemDetails.problemBo?.problemType || '')"
                >
                  <ChoiceQuestion
                    :options="problemOptions"
                    v-model="selectedOptions"
                    :type="problemDetails.problemBo?.problemType === ProblemType.SINGLE_CHOICE.code ? 'single' : 'multiple'"
                  />
                </template>

                <template v-else-if="problemDetails.problemBo?.problemType === ProblemType.TRUE_FALSE.code">
                  <TrueFalseQuestion v-model="selectedOptions" />
                </template>

                <template v-else-if="problemDetails.problemBo?.problemType === ProblemType.FILL_BLANK.code">
                  <FillBlankQuestion
                    :options="problemOptions"
                    v-model="blankAnswers"
                    v-model:single-value="blankAnswer"
                    @change="handleBlankAnswerChange"
                  />
                </template>

                <template v-else-if="problemDetails.problemBo?.problemType === ProblemType.SHORT_ANSWER.code">
                  <ShortAnswerQuestion
                    v-model="shortAnswer"
                    :max-words="500"
                  />
                </template>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧代码编辑器或提交界面 -->
        <div class="code-submit-panel">
          <template v-if="checkIsProgrammingProblem()">
            <!-- 代码编辑器 -->
            <div class="code-editor-wrapper">
              <CodeEditor
                v-model="submitForm.code"
                :default-code-mode="CodeMode.OJ.code.toString()"
                :language="submitForm.language"
                :read-only-prop="false"
                :theme="'light'"
                :default-font-size="14"
                :default-tab-size="4"
                @change="onCodeChange"
                @language-change="onLanguageChange"
              >
                <!-- 通过插槽插入计时器 -->
                <template #custom-actions>
                  <div
                    class="problem-timer-info"
                    v-if="sceneType.value?.code !== JudgeSceneType.Normal.code"
                  >
                    <div class="problem-timer-item">
                      <el-icon><Timer /></el-icon>
                      <span class="timer-label">题目耗时</span>
                      <span class="timer-value">{{ formatTime(elapsedTime) }}</span>
                    </div>
                    <div class="problem-timer-item">
                      <el-icon><Stopwatch /></el-icon>
                      <span class="timer-label">总耗时</span>
                      <span class="timer-value">{{ formatTime(totalTime) }}</span>
                    </div>
                  </div>
                </template>
              </CodeEditor>
            </div>
          </template>

          <!-- 客观题答题卡和统计信息 -->
          <template v-else>
            <AnswerCard
              :problem-type="getProblemTypeForCard()"
              :options="problemOptions"
              v-model:selected-options="selectedOptions"
              :blank-answers="blankAnswers"
              :short-answer="shortAnswer"
              :elapsed-time="elapsedTime"
              :show-timer="showTimer"
              :show-stats="false"
              :stats="problemStats"
            />
          </template>

          <!-- 提交按钮 -->
          <div class="submit-actions">
            <!-- 导航按钮 -->
            <div class="submit-action-group">
              <!-- 将题目tabs按钮移到这里 -->
              <el-button
                class="problem-list-btn"
                type="info"
                plain
                size="small"
                v-if="problemList.length > 0"
                @click="problemDrawerVisible = true"
              >
                <el-icon><Menu /></el-icon>
                题目列表
              </el-button>

              <div
                class="tabs-mini-preview"
                v-if="problemList.length > 0"
              >
                <div
                  v-for="(problem, index) in problemList.slice(0, 5)"
                  :key="problem.id"
                  class="mini-tab-item"
                  :class="{ 'active': index === currentProblemIndex }"
                  @click="navigateToProblem(problem, index)"
                >
                  {{ index + 1 }}
                </div>
                <div
                  v-if="problemList.length > 5"
                  class="mini-tab-more"
                  @click="problemDrawerVisible = true"
                >
                  ...
                </div>
              </div>

              <div class="navigation-actions">
                <el-button
                  class="navigation-btn prev-btn"
                  :disabled="problemList.length === 0 || currentProblemIndex <= 0"
                  @click="goToPrevProblem"
                  :icon="ArrowLeft"
                  type="info"
                  plain
                >
                  上一题
                </el-button>

                <div
                  class="problem-position"
                  v-if="problemList.length > 0"
                >
                  <span>{{ currentProblemIndex + 1 }} / {{ problemList.length }}</span>
                </div>

                <el-button
                  class="navigation-btn next-btn"
                  :disabled="problemList.length === 0 || currentProblemIndex >= problemList.length - 1"
                  @click="goToNextProblem"
                  type="info"
                  plain
                >
                  下一题 <el-icon class="el-icon--right">
                    <ArrowRight />
                  </el-icon>
                </el-button>
              </div>
            </div>

            <!-- 提交按钮和评测状态放在同一行 -->
            <div class="submit-btn-status-group">
              <!-- 提交按钮 -->
              <el-button
                v-if="checkIsProgrammingProblem()"
                class="submit-btn"
                type="primary"
                size="large"
                :disabled="submitting"
                @click="submitProgram"
                :loading="submitting"
              >
                {{ submitting ? '正在提交...' : '提交代码' }}
              </el-button>

              <el-button
                v-else
                class="submit-btn"
                type="primary"
                size="large"
                :disabled="submitting"
                @click="submitObjective"
                :loading="submitting"
              >
                {{ submitting ? '正在提交...' : '提交答案' }}
              </el-button>

              <!-- 评测状态显示 -->
              <div
                class="judge-status-wrapper"
                v-if="judgeStatusCode !== JudgeStatus.STATUS_NULL.code"
              >
                <JudgeStatusShow :code="judgeStatusCode" />
              </div>

              <!-- 主动查询按钮 -->
              <el-button
                v-if="showManualQueryButton"
                class="manual-query-btn"
                type="warning"
                size="large"
                :loading="manualQuerying"
                @click="manualQueryJudgeResult"
              >
                {{ manualQuerying ? '查询中...' : '查询测评结果' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </template>
    <el-empty
      v-else
      description="无题目数据"
      class="empty-container"
    >
      <el-button
        type="primary"
        @click="fetchProblemDetails"
      >
        重新加载
      </el-button>
      <el-button @click="showDebug = true">
        显示调试信息
      </el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watchEffect, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading, ElNotification } from 'element-plus'
import { ArrowDown, ArrowUp, Check, Close, Timer, InfoFilled, User, Star, ArrowRight, ArrowLeft, DocumentCopy, Menu, Stopwatch } from '@element-plus/icons-vue'
import CodeEditor from '@/components/Common/CodeEditor/CodeEditor.vue'
import MdPreview from '@/components/Common/Markdown/MdPreview.vue'
import EnumShow from '@/components/Common/Enum/EnumShow.vue'
import TagShow from '@/components/Common/Meta/Tag/TagShow.vue'
import { ProblemType } from '@/enums/oj/problem'
import { ProgramType } from '@/enums/oj/problem/ProgramType'
import { DifficultyLevel } from '@/enums/oj/problem/DifficultyLevel'
import { JudgeSceneType, matchCode as matchJudgeSceneType } from '@/enums/oj/judge/JudgeSceneType'
import { SubmitType } from '@/enums/oj/problem/SubmitType'
import { getProblemDetailApi } from '@/api/modules/oj/problem/problem'
import { submitNormalApi } from '@/api/modules/oj/problem/ptoblemSubmit'
import { getJudgeResultApi } from '@/api/modules/oj/judge/judge'
import type { IProblem } from '@/api/interface/oj/problem/problem'
import type { GlobalNormalSubmitDTO, ProblemOption } from '@/api/interface/oj/problem/ptoblemSubmit'
import { ProblemHeader, ChoiceQuestion, TrueFalseQuestion, FillBlankQuestion, ShortAnswerQuestion, AnswerCard } from '@/components/Oj/Problem'
import mittBus from '@/utils/mittBus'
import { useSocketStore } from '@/stores/modules/socket'
import confetti from 'canvas-confetti'
import { WebsocketBusinessType, matchCode } from '@/enums/oj/websocket/BusinessType';
import JudgeStatusShow from '@/components/Oj/Judge/JudgeStatusShow.vue'
import { JudgeStatus } from '@/enums/oj/judge/JudgeStatus'
import { ProgramLanguage, normalizeProgramLanguage, areLanguagesCompatible } from '@/enums/oj/common/ProgramLanguage'
import { getTopicDetailApi } from '@/api/modules/oj/topic/topic'
import { TimeRangeType } from '@/enums/oj/topic/TimeRangeType'
import { CodeMode } from '@/enums/oj/code'
import { createDevToolsDetector, type DevToolsDetectionResult } from '@/utils/devToolsDetector'

defineOptions({
  name: 'ProblemSubmitView'
})

const route = useRoute()
const router = useRouter()
const problemId = computed(() => Number(route.params.id))
const topicId = computed(() => route.query.topicId ? Number(route.query.topicId) : 0)
const judgeSceneTypeFromQuery = computed(() => route.query.judgeSceneType as string || JudgeSceneType.Normal.code)

// 添加window对象引用
const windowRef = window

const sceneType = computed(() => matchJudgeSceneType( route.query.judgeSceneType as string ) || JudgeSceneType.Normal)

// 添加过期相关数据
const isExpired = computed(() => route.query.isExpired === '1')
const expirationType = computed(() => route.query.expirationType as string || '')

// 添加过期策略警告信息
const getExpirationWarning = () => {
  if (!isExpired.value) return null

  return null
}

const submitting = ref(false)
const loading = ref(true)
const loadFailed = ref(false)
const showDebug = ref(false)
const showProblemInfo = ref(true) // 控制题目信息的显示/隐藏
const tagList = ref<any[]>([]) // 存储tagList数据

// 添加作业题目列表数据
const topicDetail = ref<any>({})
const problemList = ref<any[]>([])
const currentProblemIndex = ref(-1)

// 添加判断题目是否已提交过的状态
const hasSubmitted = ref(false)
const submittedAnswer = ref<any>(null)

// 增加一个状态变量，跟踪语言切换
const languageChangedManually = ref(false);
// 防抖和防重处理相关的状态变量
const languageChangeTimeout = ref<any>(null);
const fillTemplateInProgress = ref(false);

// 题目详情数据类型扩展，添加 problemBo 字段
interface ProblemDetail extends IProblem.Form {
  problemBo?: {
    id?: number;
    problemKey?: string;
    title?: string;
    programType?: number;
    problemType?: string;
    sourceType?: string;
    timeLimit?: number;
    memoryLimit?: number;
    stackLimit?: number;
    description?: string;
    input?: string;
    output?: string;
    examples?: string;
    difficulty?: number;
    hint?: string;
    auth?: number;
    ioScore?: number;
    score?: number;
    source?: string;
    judgeMode?: string;
    judgeCaseMode?: string;
    userExtraFile?: string;
    judgeExtraFile?: string;
    spjCode?: string;
    spjLanguage?: string;
    remote?: boolean;
    codeShare?: boolean;
    removeEndBlank?: boolean;
    openCaseResult?: boolean;
    uploadCase?: boolean;
    groupProblem?: boolean;
    fileIo?: boolean;
    requireImage?: boolean;
    caseVersion?: string;
    modifiedUser?: string;
    applyPublicProgress?: number;
    ioReadFileName?: string;
    ioWriteFileName?: string;
    [key: string]: any;
  };
  uploadTestcaseDir?: string;
  options?: any[];
  problemCaseDataList?: Array<{
    input?: string;
    output?: string;
    type?: string;
    score?: number;
    groupNum?: string;
  }>;
  changeModeCode?: boolean;
  changeJudgeCaseMode?: boolean;
  languageIds?: number[];
  tagIds?: string;
  tagList?: Array<{
    id?: number;
    name?: string;
    color?: string;
    textColor?: string;
    enable?: boolean;
    createTime?: string;
    updateTime?: string;
    createId?: number;
    updateId?: number;
  }>;
  codeTemplates?: Array<{
    languageId?: number;
    name?: string;
    code?: string;
  }>;
  [key: string]: any;
}

// 调整题目详情数据类型
const problemDetails = ref<ProblemDetail>({})
const problemOptions = ref<ProblemOption[]>([])

// 添加代码模板数据
const codeTemplates = ref<Array<{
  languageId?: number;
  name?: string;
  code?: string;
}>>([])

// 选项答案
const selectedOptions = ref<string[]>([])
const blankAnswers = ref<string[]>([])
const blankAnswer = ref('') // 保留单个回答的情况
const shortAnswer = ref('')

// 题目统计数据
const problemStats = reactive({
  totalSubmissions: 1250,
  passRate: 65,
  averageTime: 5.2
})

// 计时器
const showTimer = ref(true)
const elapsedTime = ref(0)

// 添加WebSocket相关的状态
const waitingForJudgeResult = ref(false)
const judgeResultReceived = ref(false)
const judgeSubmitId = ref(0)
const judgeResultTimeout = ref<any>(null)
const continueWaitingDialogVisible = ref(false)
// 添加测评状态码
const judgeStatusCode = ref(JudgeStatus.STATUS_NULL.code) // 默认无状态

// 添加主动查询相关状态
const showManualQueryButton = ref(false) // 是否显示主动查询按钮
const manualQuerying = ref(false) // 是否正在主动查询

// 提交表单类型扩展，添加耗时字段
interface GlobalNormalSubmitDTOExtended extends GlobalNormalSubmitDTO {
  currentProblemCostTime?: number;
  totalCostTime?: number;
}

// 提交表单
const submitForm = reactive<GlobalNormalSubmitDTOExtended>({
  problemId: 0,
  language: ProgramLanguage.CPP, // 使用枚举作为默认语言
  code: '',
  judgeSceneType: String(JudgeSceneType.Normal.code),
  submitType: String(SubmitType.NORMAL_JUDGE.code),
  businessId: 0,
  isRemote: false,
  problemType: String(ProblemType.PROGRAMMING.code)
})

// 获取题目详情
const fetchProblemDetails = async () => {
  loading.value = true
  loadFailed.value = false

  try {
    console.log('获取题目详情，题目ID:', problemId.value)
    const res = await getProblemDetailApi({ id: problemId.value })

    console.log('获取到的题目详情:', res)

    if (res?.data) {
      // 详细输出题目数据，便于排查
      console.log('题目数据详情:', JSON.stringify(res.data))

      // 使用类型断言解决类型检查问题
      problemDetails.value = res.data as ProblemDetail

      // 验证problemBo数据是否存在
      if (!problemDetails.value.problemBo) {
        ElMessage.error('获取题目详情失败：题目数据格式不正确')
        loadFailed.value = true
        loading.value = false
        return
      }

      console.log('题目类型:', problemDetails.value.problemBo.problemType, '类型:', typeof problemDetails.value.problemBo.problemType)

      // 处理tagList数据
      if (res.data.tagList && Array.isArray(res.data.tagList)) {
        tagList.value = res.data.tagList
      }

      // 保存代码模板数据
      if (res.data.codeTemplates && Array.isArray(res.data.codeTemplates)) {
        codeTemplates.value = res.data.codeTemplates
        console.log('获取到代码模板:', codeTemplates.value)

        // 如果是编程题，并且有默认语言的代码模板，自动填充
        if (checkIsProgrammingProblem()) {
          fillCodeTemplate(submitForm.language)
        }
      }

      // 更新提交表单中的数据
      submitForm.problemId = problemDetails.value.problemBo?.id || 0
      submitForm.problemType = String(problemDetails.value.problemBo?.problemType || ProblemType.PROGRAMMING.code)

      // 根据场景类型设置业务ID
      if (sceneType.value !== JudgeSceneType.Normal) {
        // 作业或考试模式，businessId 应该是 topicId
        submitForm.businessId = topicId.value;
      } else {
        // 其他模式，businessId 是题目ID
        submitForm.businessId = problemDetails.value.problemBo?.id || 0;
      }

      // 根据题目类型设置提交类型
      const isProgramming = checkIsProgrammingProblem();
      submitForm.submitType = isProgramming ?
        String(SubmitType.NORMAL_JUDGE.code) :
        String(SubmitType.FIXED_JUDGE.code);
      console.log('设置提交类型:', submitForm.submitType, '是编程题:', isProgramming);

      // 检查当前题目是什么类型
      console.log('题目判断结果是否为编程题:', isProgramming);

      // 从选项数据加载选项
      if (res.data.options && Array.isArray(res.data.options)) {
        problemOptions.value = res.data.options || []
        console.log('从options获取选项数据:', problemOptions.value)
      }

      // 初始化填空题答案数组
      if (problemDetails.value.problemBo?.problemType === ProblemType.FILL_BLANK.code) {
        blankAnswers.value = new Array(problemOptions.value.length).fill('');
      }

      // 检查题目是否已提交过（仅对客观题）
      if (!isProgramming && (res.data.isSubmitted === true || res.data.hasSubmit === true)) {
        hasSubmitted.value = true;

        // 如果有提交过的答案数据
        if (res.data.submittedAnswer || res.data.lastSubmit) {
          submittedAnswer.value = res.data.submittedAnswer || res.data.lastSubmit;
          console.log('获取到之前提交的答案:', submittedAnswer.value);

          // 可以在这里恢复之前提交的答案到界面上
          restoreSubmittedAnswer();
        }
      }

      // 已加载完成
      loading.value = false
    } else {
      loadFailed.value = true
      ElMessage.error('获取题目详情失败：无数据')
      loading.value = false
    }
  } catch (error) {
    console.error('获取题目详情失败', error)
    ElMessage.error('获取题目详情失败：请求错误')
    loadFailed.value = true
    loading.value = false
  }
}

// 恢复之前提交的答案到界面
const restoreSubmittedAnswer = () => {
  if (!submittedAnswer.value) return;

  try {
    // 根据题目类型恢复答案
    const problemType = problemDetails.value.problemBo?.problemType;

    if ([ProblemType.SINGLE_CHOICE.code, ProblemType.MULTIPLE_CHOICE.code].includes(problemType || '')) {
      // 选择题
      if (submittedAnswer.value.selectedOptions) {
        selectedOptions.value = submittedAnswer.value.selectedOptions;
      } else if (submittedAnswer.value.replyOptions) {
        // 从回复选项中提取选中的选项
        selectedOptions.value = submittedAnswer.value.replyOptions
          .filter((opt: any) => opt.answer)
          .map((opt: any) => opt.optionKey);
      }
    } else if (problemType === ProblemType.TRUE_FALSE.code) {
      // 判断题
      if (submittedAnswer.value.selectedOptions) {
        selectedOptions.value = submittedAnswer.value.selectedOptions;
      } else if (submittedAnswer.value.replyOptions && submittedAnswer.value.replyOptions.length > 0) {
        selectedOptions.value = [submittedAnswer.value.replyOptions[0].optionKey];
      }
    } else if (problemType === ProblemType.FILL_BLANK.code) {
      // 填空题
      if (submittedAnswer.value.blankAnswers) {
        blankAnswers.value = submittedAnswer.value.blankAnswers;
      } else if (submittedAnswer.value.replyOptions) {
        blankAnswers.value = submittedAnswer.value.replyOptions.map((opt: any) => opt.optionContent || '');
      }

      // 处理单个填空的情况
      if (blankAnswers.value.length === 1) {
        blankAnswer.value = blankAnswers.value[0];
      }
    } else if (problemType === ProblemType.SHORT_ANSWER.code) {
      // 简答题
      if (submittedAnswer.value.shortAnswer) {
        shortAnswer.value = submittedAnswer.value.shortAnswer;
      } else if (submittedAnswer.value.replyOptions && submittedAnswer.value.replyOptions.length > 0) {
        shortAnswer.value = submittedAnswer.value.replyOptions[0].optionContent || '';
      }
    }

    console.log('已恢复之前提交的答案');
  } catch (error) {
    console.error('恢复之前提交的答案失败', error);
  }
}

// 检查是否为编程题的方法
const checkIsProgrammingProblem = () => {
  const currentProblemType = problemDetails.value.problemBo?.problemType;
  // console.log('当前题目类型:', currentProblemType, 'PROGRAMMING code:', ProblemType.PROGRAMMING.code);

  // 使用枚举进行比较
  return currentProblemType === ProblemType.PROGRAMMING.code;
}

// 判断是否为编程题
const isProgrammingProblem = computed(() => {
  return checkIsProgrammingProblem();
})

// 代码变更事件
const onCodeChange = (code: string) => {
  submitForm.code = code
}

// 语言变更事件
const onLanguageChange = (language: string) => {
  // 确保使用标准化的语言枚举
  const standardLang = normalizeProgramLanguage(language);
  submitForm.language = standardLang;
  console.log('语言已切换为:', standardLang)

  // 标记为手动切换语言
  languageChangedManually.value = true;

  // 添加防抖处理，避免短时间内多次调用
  if (languageChangeTimeout.value) {
    clearTimeout(languageChangeTimeout.value);
  }

  languageChangeTimeout.value = setTimeout(() => {
    // 尝试填充当前语言的代码模板
    fillCodeTemplate(standardLang);
    languageChangeTimeout.value = null;
  }, 50);
}

// 根据编辑器语言填充代码模板
const fillCodeTemplate = (editorLanguage: string) => {
  // 如果没有代码模板数据或不是编程题，则不处理
  if (!codeTemplates.value.length || !checkIsProgrammingProblem()) {
    return
  }

  // 防止重复调用
  if (fillTemplateInProgress.value) {
    console.log('模板填充操作正在进行中，跳过重复调用');
    return;
  }

  fillTemplateInProgress.value = true;

  // 标准化输入的语言为枚举值
  const normalizedLanguage = normalizeProgramLanguage(editorLanguage);
  console.log('寻找语言模板匹配:', normalizedLanguage)

  // 模板实际名称和调试信息
  console.log('可用模板列表:', codeTemplates.value.map(t => t.name))
  console.log('当前代码内容长度:', submitForm.code.length)

  // 严格匹配：确保模板语言名和当前语言完全匹配
  let template = codeTemplates.value.find(t => {
    if (!t.name) return false

    // 对于C和C++这样的特殊情况，确保完全匹配
    if ((normalizedLanguage === ProgramLanguage.CPP && t.name === 'C++') ||
      (normalizedLanguage === ProgramLanguage.CPP && t.name === 'C')) {
      // 严格匹配C++和C，不允许交叉
      return t.name === 'C++';
    }

    // 将模板名称也标准化为枚举
    const templateLang = normalizeProgramLanguage(t.name);

    // 严格匹配，只有完全相同的枚举值才通过
    const isMatch = templateLang === normalizedLanguage;
    if (isMatch) {
      console.log(`严格匹配成功: 模板"${t.name}"匹配语言${normalizedLanguage}`);
    }
    return isMatch;
  })

  // 如果严格匹配没找到，尝试完全相等的名称匹配
  if (!template) {
    template = codeTemplates.value.find(t => {
      if (!t.name) return false

      // 对于C++和C的特殊处理
      if (normalizedLanguage === ProgramLanguage.CPP) {
        // 如果是C++，只匹配名称为C++的模板，不匹配C
        return t.name === 'C++';
      }

      // 检查模板名是否等于或包含当前语言的名称
      const templateName = t.name.toLowerCase();
      const langString = normalizedLanguage.toLowerCase();

      // 完全相等匹配
      if (templateName === langString) {
        console.log(`完全相等匹配: 模板"${t.name}"匹配语言${normalizedLanguage}`);
        return true;
      }

      return false;
    })
  }

  // 如果仍然没找到，进行更宽松的兼容匹配
  if (!template) {
    template = codeTemplates.value.find(t => {
      if (!t.name) return false

      // 检查语言兼容性
      const isCompatible = areLanguagesCompatible(t.name, normalizedLanguage);
      if (isCompatible) {
        console.log(`兼容性匹配: 模板"${t.name}"兼容语言${normalizedLanguage}`);
        return true;
      }

      return false;
    })
  }

  if (template && template.code) {
    console.log('找到匹配的代码模板:', template.name)
    console.log('模板代码长度:', template.code.length)
    console.log('模板代码前20个字符:', template.code.substring(0, 20))

    // 直接更新代码内容，无条件应用模板
    const oldCode = submitForm.code
    submitForm.code = template.code
    console.log('已强制应用代码模板到编辑器，模板名称:', template.name)
    console.log('应用后代码长度:', submitForm.code.length)
    console.log('应用后代码前20个字符:', submitForm.code.substring(0, 20))

    // 判断代码是否成功更新
    if (oldCode === submitForm.code) {
      console.warn('代码没有变化，可能未成功应用模板')
    }

    // 使用nextTick确保DOM更新
    nextTick(() => {
      // 确保代码模板已经应用
      if (submitForm.code !== template!.code) {
        console.warn('代码模板可能未正确应用，再次尝试')
        submitForm.code = template!.code || '';
        console.log('二次应用后代码长度:', submitForm.code.length)
      } else {
        console.log('代码模板应用成功')
      }

      // 重置填充进行中标志
      fillTemplateInProgress.value = false;
    })
  } else {
    console.log('未找到匹配的代码模板，语言:', normalizedLanguage)
    // 重置填充进行中标志
    fillTemplateInProgress.value = false;
  }
}

// 处理评测结果的方法
const handleJudgeNotify = (data: any) => {
  console.log('收到评测结果:', data, judgeSubmitId.value)

  // 检查是否是当前提交的评测结果
  if (data.id !== judgeSubmitId.value) {
    console.log('非当前提交的评测结果，忽略')
    return
  }

  // 如果已经收到过结果，则忽略重复消息
  if (judgeResultReceived.value) {
    console.log('已处理过该评测结果，忽略重复消息')
    return
  }

  console.log('收到评测结果 ID:', data.id, judgeSubmitId.value)

  // 清除超时计时器
  if (judgeResultTimeout.value) {
    clearTimeout(judgeResultTimeout.value)
    judgeResultTimeout.value = null
  }

  // 更新状态
  waitingForJudgeResult.value = false
  judgeResultReceived.value = true
  submitting.value = false

  // 隐藏主动查询按钮
  showManualQueryButton.value = false

  // 更新测评状态码
  judgeStatusCode.value = data.status

  // 根据评测状态显示不同结果
  if (data.status === JudgeStatus.STATUS_ACCEPTED.code) { // AC
    // 显示成功消息
    ElMessage.success(`恭喜！${JudgeStatus.STATUS_ACCEPTED.tooltip}：${JudgeStatus.STATUS_ACCEPTED.text}`)

    // 撒花庆祝
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 }
    })
  } else {
    // 获取状态信息
    const statusInfo = Object.values(JudgeStatus).find(item => Number(item.code) === Number(data.status)) || JudgeStatus.STATUS_NULL

    // 显示评测失败信息
    const errorMsg = data.errorMessage || `${statusInfo.tooltip}：${statusInfo.text}`
    ElMessage.error(errorMsg)
  }
}

// 设置超时显示主动查询按钮和弹窗提示
const setupJudgeResultTimeout = () => {
  // 5秒后显示主动查询按钮
  setTimeout(() => {
    if (!judgeResultReceived.value && waitingForJudgeResult.value) {
      showManualQueryButton.value = true
      ElMessage.info('评测时间较长，您可以点击按钮主动查询结果')
    }
  }, 5000)

  // 10秒后开始弹窗提示，之后每10秒弹一次
  const showTimeoutDialog = () => {
    judgeResultTimeout.value = setTimeout(() => {
      if (!judgeResultReceived.value && waitingForJudgeResult.value) {
        ElMessageBox.confirm(
          '评测结果还未返回，您可以选择继续等待或点击下方的"查询测评结果"按钮主动查询。',
          '评测超时提示',
          {
            confirmButtonText: '继续等待',
            cancelButtonText: '取消等待',
            type: 'warning',
            showClose: false,
            closeOnClickModal: false,
            closeOnPressEscape: false
          }
        )
          .then(() => {
            // 用户选择继续等待，10秒后再次弹窗
            showTimeoutDialog()
          })
          .catch(() => {
            // 用户选择不再等待
            waitingForJudgeResult.value = false
            submitting.value = false
            showManualQueryButton.value = false
            ElMessage.info('已取消等待评测结果')
            // 设置状态为"未知"
            judgeStatusCode.value = JudgeStatus.STATUS_NULL.code
          })
      }
    }, 10000) // 10秒后触发
  }

  // 开始第一次10秒倒计时
  showTimeoutDialog()
}

// 主动查询测评结果
const manualQueryJudgeResult = async () => {
  if (manualQuerying.value || !judgeSubmitId.value) return

  manualQuerying.value = true

  try {
    console.log('主动查询测评结果，ID:', judgeSubmitId.value)
    const res = await getJudgeResultApi({ id: judgeSubmitId.value })
    console.log('查询到的测评结果:', res)

    if (res?.data) {
      // 模拟WebSocket消息格式，调用现有的处理方法
      const judgeData = {
        id: judgeSubmitId.value,
        status: res.data.status,
        errorMessage: res.data.errorMessage
      }

      // 调用现有的评测结果处理方法
      handleJudgeNotify(judgeData)

      // 隐藏主动查询按钮
      showManualQueryButton.value = false

      // 清除弹窗定时器
      if (judgeResultTimeout.value) {
        clearTimeout(judgeResultTimeout.value)
        judgeResultTimeout.value = null
      }
    } else {
      ElMessage.warning('未获取到测评结果，请稍后再试')
    }
  } catch (error) {
    console.error('查询测评结果失败:', error)
    ElMessage.error('查询测评结果失败，请稍后再试')
  } finally {
    manualQuerying.value = false
  }
}

// 修改提交题目函数，添加过期策略处理和已提交检查
const submitProblem = async () => {
  if (submitting.value) return

  // 验证提交内容
  if (isProgrammingProblem.value) {
    if (!submitForm.code.trim()) {
      ElMessage.warning('请编写代码后提交')
      return
    }
  } else {
    // 客观题验证
    let valid = false
    let replyOptions: ProblemOption[] = []

    if ([ProblemType.SINGLE_CHOICE.code, ProblemType.MULTIPLE_CHOICE.code].includes(problemDetails.value.problemBo?.problemType || '')) {
      if (selectedOptions.value.length === 0) {
        ElMessage.warning('请选择答案后提交')
        return
      }

      // 构建答案数据
      replyOptions = problemOptions.value.map(option => {
        return {
          id: option.id,
          optionKey: option.optionKey,
          optionContent: option.optionContent,
          answer: selectedOptions.value.includes(option.optionKey || '')
        }
      })
      valid = true
    } else if (problemDetails.value.problemBo?.problemType === ProblemType.TRUE_FALSE.code) {
      if (selectedOptions.value.length === 0) {
        ElMessage.warning('请选择正确或错误')
        return
      }

      replyOptions = [
        {
          optionKey: selectedOptions.value[0],
          answer: true
        }
      ]
      valid = true
    } else if (problemDetails.value.problemBo?.problemType === ProblemType.FILL_BLANK.code) {
      // 检查是否有选项
      if (problemOptions.value.length > 0) {
        // 检查是否所有填空都已填写
        const emptyAnswers = blankAnswers.value.filter(answer => !answer.trim());
        if (emptyAnswers.length > 0) {
          ElMessage.warning(`请填写所有答案 (还有${emptyAnswers.length}个填空未完成)`)
          return
        }

        // 构建答案选项
        replyOptions = blankAnswers.value.map((answer, index) => {
          return {
            id: problemOptions.value[index]?.id,
            optionKey: problemOptions.value[index]?.optionKey,
            optionContent: answer,
            answer: true
          }
        })
      } else {
        // 单个填空的情况
        if (!blankAnswer.value.trim()) {
          ElMessage.warning('请填写答案')
          return
        }

        replyOptions = [
          {
            optionContent: blankAnswer.value,
            answer: true
          }
        ]
      }
      valid = true
    } else if (problemDetails.value.problemBo?.problemType === ProblemType.SHORT_ANSWER.code) {
      if (!shortAnswer.value.trim()) {
        ElMessage.warning('请填写答案')
        return
      }

      replyOptions = [
        {
          optionContent: shortAnswer.value,
          answer: true
        }
      ]
      valid = true
    }

    if (valid) {
      submitForm.replyOptions = replyOptions
    }
  }

  // 更新提交表单信息
  submitForm.problemId = problemDetails.value.problemBo?.id || 0
  submitForm.problemType = String(problemDetails.value.problemBo?.problemType || ProblemType.PROGRAMMING.code)

  // 根据题目类型设置提交类型
  submitForm.submitType = isProgrammingProblem.value ?
    String(SubmitType.NORMAL_JUDGE.code) :
    String(SubmitType.FIXED_JUDGE.code);

  // 标准化语言处理
  submitForm.language = normalizeProgramLanguage(submitForm.language);

  // 如果不是普通做题模式，添加耗时信息
  if (sceneType.value?.code !== JudgeSceneType.Normal.code) {
    // 添加单体耗时和总耗时参数
    submitForm.currentProblemCostTime = elapsedTime.value;
    submitForm.totalCostTime = totalTime.value;
    console.log(`添加耗时信息: 单题耗时=${elapsedTime.value}秒, 总耗时=${totalTime.value}秒`);
  }

  // 开始提交
  submitting.value = true
  // 重置评测结果状态
  waitingForJudgeResult.value = true
  judgeResultReceived.value = false

  // 设置状态为"提交中"
  judgeStatusCode.value = JudgeStatus.STATUS_SUBMITTING.code

  try {
    console.log('提交评测，参数:', submitForm)
    const res = await submitNormalApi(submitForm)
    console.log('提交评测结果:', res)
    ElMessage.success(`${JudgeStatus.STATUS_SUBMITTING.tooltip}，${JudgeStatus.STATUS_JUDGING.tooltip}...`)

    // 保存提交ID用于匹配评测结果
    judgeSubmitId.value = res.data.id

    // 更新状态为"评测中"
    judgeStatusCode.value = JudgeStatus.STATUS_JUDGING.code

    // 确保Socket连接已打开
    const socketStore = useSocketStore()
    socketStore.open()

    // 设置超时检查
    setupJudgeResultTimeout()

    // 调用提交成功处理函数（仅当提交成功时重置当前题耗时）
    handleSubmitSuccess();

  } catch (error) {
    console.error('提交失败', error)
    ElMessage.error(`${JudgeStatus.STATUS_SUBMITTED_FAILED.tooltip}：${JudgeStatus.STATUS_SUBMITTED_FAILED.text}`)
    waitingForJudgeResult.value = false
    submitting.value = false

    // 更新状态为"提交失败"
    judgeStatusCode.value = JudgeStatus.STATUS_SUBMITTED_FAILED.code
  }
}

// 获取难度等级对应的标签类型
const getDifficultyTagType = (difficulty: number) => {
  switch (difficulty) {
    case DifficultyLevel.ENTRY.code:
      return 'success'
    case DifficultyLevel.EASY.code:
      return 'success'
    case DifficultyLevel.MEDIUM.code:
      return 'warning'
    case DifficultyLevel.HARD.code:
      return 'danger'
    case DifficultyLevel.EXPERT.code:
      return 'info'
    default:
      return 'info'
  }
}

// 重置评测状态
const resetJudgeStatus = () => {
  judgeStatusCode.value = JudgeStatus.STATUS_NULL.code; // 重置为默认无状态
  waitingForJudgeResult.value = false;
  judgeResultReceived.value = false;

  // 重置主动查询相关状态
  showManualQueryButton.value = false;
  manualQuerying.value = false;

  // 清除超时计时器
  if (judgeResultTimeout.value) {
    clearTimeout(judgeResultTimeout.value);
    judgeResultTimeout.value = null;
  }
};

// 添加获取URL中的题目索引和总题目数
const getProblemListFromQuery = () => {
  try {
    // 不再从URL解析problemList，而是使用totalProblems
    if (route.query.totalProblems) {
      const total = parseInt(route.query.totalProblems as string, 10);
      console.log('题目总数:', total);
    }

    if (route.query.problemIndex) {
      currentProblemIndex.value = parseInt(route.query.problemIndex as string, 10);
      console.log('当前题目索引:', currentProblemIndex.value);
    }
  } catch (error) {
    console.error('解析题目索引出错:', error);
    ElMessage.warning('题目索引数据格式错误');
  }
}

// 添加上一题按钮的逻辑
const goToPrevProblem = () => {
  if (problemList.value.length === 0 || currentProblemIndex.value <= 0) {
    ElMessage.info('已经是第一题');
    return;
  }

  const prevIndex = currentProblemIndex.value - 1;
  const prevProblem = problemList.value[prevIndex];

  if (!prevProblem || !prevProblem.id) {
    ElMessage.warning('上一题数据不完整');
    return;
  }

  // 导航到上一题
  navigateToProblem(prevProblem, prevIndex);
}

// 添加下一题按钮的逻辑
const goToNextProblem = () => {
  if (problemList.value.length === 0 || currentProblemIndex.value >= problemList.value.length - 1) {
    ElMessage.info('已经是最后一题');
    return;
  }

  const nextIndex = currentProblemIndex.value + 1;
  const nextProblem = problemList.value[nextIndex];

  if (!nextProblem || !nextProblem.id) {
    ElMessage.warning('下一题数据不完整');
    return;
  }

  // 导航到下一题
  navigateToProblem(nextProblem, nextIndex);
}

// 导航到指定题目
const navigateToProblem = (problem: any, index: number) => {
  // 在导航前保存当前计时数据
  saveTimerData();

  // 不再传递题目列表，只传递必要信息
  router.push({
    path: `/oj/problem/submit/${problem.id}`,
    query: {
      topicId: route.query.topicId,
      judgeSceneType: route.query.judgeSceneType,
      topicType: route.query.topicType,
      isExpired: route.query.isExpired,
      timeRangeType: route.query.timeRangeType,
      problemIndex: index.toString(),
      totalProblems: problemList.value.length.toString()
    }
  });
}

// 页面加载时获取题目详情
onMounted(() => {
  // 重置评测状态
  resetJudgeStatus();

  // 初始化控制台检测器
  devToolsDetectorCleanup = createDevToolsDetector(handleDevToolsChange, {
    threshold: 160,
    ratioThreshold: 0.75,
    debounceDelay: 100
  });

  if (problemId.value) {
    // 设置提交表单中的业务ID和场景类型
    if (topicId.value) {
      submitForm.businessId = topicId.value;
      submitForm.judgeSceneType = String(judgeSceneTypeFromQuery.value);
      console.log(`从主题页面跳转: 主题ID=${topicId.value}, 场景类型=${judgeSceneTypeFromQuery.value}`);

      // 加载持久化的计时数据
      loadTimerData();

      // 对所有页面都获取主题详情和题目列表
      fetchTopicDetail();
    } else {
      // 如果没有topicId，那么这是直接访问题目，不需要上一题/下一题功能
      console.log('直接访问题目，无主题ID');
      // 清空题目列表，禁用上一题/下一题功能
      problemList.value = [];
    }

    // 从URL中获取当前题目索引
    getProblemListFromQuery();

    // 获取题目详情
    fetchProblemDetails();

    // 初始化计时器
    startTimer();

    // 监听评测结果
    mittBus.on(`socket.${WebsocketBusinessType.JUDGE_NOTIFY.code}`, handleJudgeNotify);
  } else {
    ElMessage.error('题目ID无效');
    router.push('/oj/problem/problem');
  }
})

// 添加计时数据持久化相关方法
const TIMER_STORAGE_KEY = 'problem_timer_data';

// 保存计时数据到localStorage
const saveTimerData = () => {
  if (!topicId.value) return;

  const timerData = {
    topicId: topicId.value,
    problemId: problemId.value,
    elapsedTime: elapsedTime.value,
    totalTime: totalTime.value,
    timestamp: Date.now()
  };

  localStorage.setItem(TIMER_STORAGE_KEY, JSON.stringify(timerData));
};

// 从localStorage加载计时数据
const loadTimerData = () => {
  if (!topicId.value) return;

  try {
    const timerDataJson = localStorage.getItem(TIMER_STORAGE_KEY);
    if (!timerDataJson) return;

    const timerData = JSON.parse(timerDataJson);

    // 验证数据有效性
    if (timerData.topicId === topicId.value) {
      // 总耗时始终保留
      totalTime.value = timerData.totalTime || 0;

      // 单题耗时仅当做同一道题时保留
      if (timerData.problemId === problemId.value) {
        elapsedTime.value = timerData.elapsedTime || 0;
      } else {
        // 不同题目，重置单题耗时
        elapsedTime.value = 0;
      }

      console.log(`加载计时数据: 题目耗时=${elapsedTime.value}秒, 总耗时=${totalTime.value}秒`);
    }
  } catch (error) {
    console.error('加载计时数据失败:', error);
    // 出错时重置计时
    elapsedTime.value = 0;
    totalTime.value = 0;
  }
};

// 修改计时器
let timerInterval: number | null = null;

// 启动计时器
const startTimer = () => {
  if (timerInterval) clearInterval(timerInterval);

  timerInterval = window.setInterval(() => {
    elapsedTime.value++;
    // 如果不是普通做题模式，同时累加总耗时
    if (sceneType.value?.code !== JudgeSceneType.Normal.code) {
      totalTime.value++;
      // 每60秒保存一次计时数据
      if (totalTime.value % 60 === 0) {
        saveTimerData();
      }
    }
  }, 1000);
}

// 页面卸载时清除计时器
onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }

  // 保存计时数据
  saveTimerData();

  // 清理控制台检测器
  if (devToolsDetectorCleanup) {
    devToolsDetectorCleanup();
    devToolsDetectorCleanup = null;
  }

  // 清除评测结果监听
  mittBus.off(`socket.${WebsocketBusinessType.JUDGE_NOTIFY.code}`, handleJudgeNotify)

  // 清除超时计时器
  if (judgeResultTimeout.value) {
    clearTimeout(judgeResultTimeout.value)
    judgeResultTimeout.value = null
  }
})

// 提交后的处理
const handleSubmitSuccess = () => {
  // 仅重置当前题目耗时，总耗时继续累计
  elapsedTime.value = 0;
  // 保存计时数据
  saveTimerData();
}

// 监听单选题选项变化
watchEffect(() => {
  if (problemDetails.value?.problemBo?.problemType === ProblemType.SINGLE_CHOICE.code && selectedOptions.value.length > 1) {
    // 如果是单选题但选择了多个选项，只保留最新选择的选项
    selectedOptions.value = [selectedOptions.value[selectedOptions.value.length - 1]]
  }
})

// 处理判断题选项变化的逻辑
const handleTrueFalseChange = (value: string[]) => {
  if (value.length > 1) {
    // 如果选择了多个选项，只保留最新选择的选项
    selectedOptions.value = [value[value.length - 1]]
  }
}

// 格式化数字，添加千位分隔符
const formatNumber = (num: number): string => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 计算进度百分比
const calculateProgress = (value: string, maxLength: number): number => {
  if (!value) return 0;
  return Math.min(Math.round((value.length / maxLength) * 100), 100);
}

// 格式化时间
const formatTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}

// 选择题选项切换
const toggleOption = (optionKey: string | undefined) => {
  if (!optionKey) return;

  // 单选题逻辑
  if (problemDetails.value.problemBo?.problemType === ProblemType.SINGLE_CHOICE.code) {
    selectedOptions.value = [optionKey];
  }
  // 多选题逻辑
  else if (problemDetails.value.problemBo?.problemType === ProblemType.MULTIPLE_CHOICE.code) {
    const index = selectedOptions.value.indexOf(optionKey);
    if (index >= 0) {
      selectedOptions.value.splice(index, 1);
    } else {
      selectedOptions.value.push(optionKey);
    }
  }
}

// 判断题选项选择
const selectTrueFalseOption = (value: string) => {
  selectedOptions.value = [value];
}

// 获取难度等级的文字描述
const getDifficultyLabel = (difficulty: number): string => {
  switch (difficulty) {
    case DifficultyLevel.ENTRY.code:
      return '入门';
    case DifficultyLevel.EASY.code:
      return '简单';
    case DifficultyLevel.MEDIUM.code:
      return '中等';
    case DifficultyLevel.HARD.code:
      return '困难';
    case DifficultyLevel.EXPERT.code:
      return '专家';
    default:
      return '未知';
  }
}

// 计算填空题进度
const calculateBlankProgress = () => {
  const filledCount = blankAnswers.value.filter(Boolean).length;
  return Math.round((filledCount / problemOptions.value.length) * 100);
}

// 获取填空题进度状态
const getProgressStatus = (percentage: number) => {
  if (percentage === 100) return 'success';
  if (percentage === 0) return 'exception';
  return 'normal';
}

// 添加获取卡片组件类型的辅助方法
const getProblemTypeForCard = () => {
  const type = problemDetails.value.problemBo?.problemType;
  if (type === ProblemType.SINGLE_CHOICE.code) return 'single';
  if (type === ProblemType.MULTIPLE_CHOICE.code) return 'multiple';
  if (type === ProblemType.TRUE_FALSE.code) return 'truefalse';
  if (type === ProblemType.FILL_BLANK.code) return 'fillblank';
  if (type === ProblemType.SHORT_ANSWER.code) return 'shortanswer';
  return 'shortanswer'; // 默认返回简答题
};

// 添加填空题答案变更处理方法
const handleBlankAnswerChange = (values: string[]) => {
  if (values.length === 1 && !problemOptions.value.length) {
    blankAnswer.value = values[0];
  }
};

// 语言选择下拉框的选项定义
const languageOptions = Object.values(ProgramLanguage).map(lang => ({
  label: lang,
  value: lang
}));

// 获取作业详情和题目列表
const fetchTopicDetail = async () => {
  if (!topicId.value) return;

  try {
    const res = await getTopicDetailApi({ id: topicId.value });
    topicDetail.value = res.data;

    // 检查是否有题目列表
    if (res.data.problemList && res.data.problemList.length > 0) {
      problemList.value = res.data.problemList;
      console.log('获取到主题题目列表:', problemList.value.length, '题');

      // 如果当前题目索引超出范围，重置为0
      if (currentProblemIndex.value >= problemList.value.length) {
        currentProblemIndex.value = 0;
        console.warn('当前题目索引超出范围，重置为0');
      }

      // 查找当前题目在列表中的位置
      if (problemId.value) {
        const foundIndex = problemList.value.findIndex(p => p.id === Number(problemId.value));
        if (foundIndex !== -1 && foundIndex !== currentProblemIndex.value) {
          console.log(`找到当前题目在列表中的位置: ${foundIndex}，更新索引`);
          currentProblemIndex.value = foundIndex;
        }
      }
    } else {
      problemList.value = [];
      console.log('主题没有题目列表');
    }
  } catch (error) {
    console.error('获取主题详情失败:', error);
    ElMessage.error('获取主题详情失败');
  }
};

// 添加复制内容的方法
const copyContent = (section: string) => {
  let content = ''

  // 根据不同区域获取内容
  switch (section) {
    case 'description':
      content = problemDetails.value.problemBo?.description || ''
      break
    case 'input':
      content = problemDetails.value.problemBo?.input || ''
      break
    case 'output':
      content = problemDetails.value.problemBo?.output || ''
      break
    case 'examples':
      content = problemDetails.value.problemBo?.examples || ''
      break
    case 'hint':
      content = problemDetails.value.problemBo?.hint || ''
      break
    default:
      content = ''
  }

  if (!content.trim()) {
    ElMessage.warning('该部分内容为空')
    return
  }

  // 简单处理 Markdown 格式，转换为纯文本
  const cleanContent = content
    .replace(/```[\s\S]*?```/g, (match) => {
      // 保留代码块内容，去掉```标记
      return match.replace(/```[\w]*\n?/g, '').replace(/```$/g, '')
    })
    .replace(/`([^`]+)`/g, '$1') // 去掉行内代码标记
    .replace(/\*\*([^*]+)\*\*/g, '$1') // 去掉粗体标记
    .replace(/\*([^*]+)\*/g, '$1') // 去掉斜体标记
    .replace(/#{1,6}\s+/g, '') // 去掉标题标记
    .replace(/^\s*[-*+]\s+/gm, '') // 去掉列表标记
    .replace(/^\s*\d+\.\s+/gm, '') // 去掉有序列表标记
    .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1') // 去掉链接标记，保留文本
    .replace(/!\[([^\]]*)\]\([^)]+\)/g, '$1') // 去掉图片标记，保留alt文本
    .trim()

  // 尝试使用现代 Clipboard API
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard.writeText(cleanContent)
      .then(() => {
        ElMessage.success('内容已复制到剪贴板')
      })
      .catch(err => {
        console.warn('Clipboard API 复制失败，尝试降级方案', err)
        fallbackCopyTextToClipboard(cleanContent)
      })
  } else {
    // 降级方案：使用传统方法
    fallbackCopyTextToClipboard(cleanContent)
  }
}

// 降级复制方案（兼容旧浏览器和非HTTPS环境）
const fallbackCopyTextToClipboard = (text: string) => {
  const textArea = document.createElement('textarea')
  textArea.value = text

  // 避免在 iOS 上出现缩放
  textArea.style.fontSize = '16px'

  // 隐藏元素
  textArea.style.position = 'fixed'
  textArea.style.left = '-999999px'
  textArea.style.top = '-999999px'

  document.body.appendChild(textArea)

  // 针对移动设备的特殊处理
  if (navigator.userAgent.match(/ipad|ipod|iphone/i)) {
    // iOS 设备需要特殊处理
    const range = document.createRange()
    range.selectNodeContents(textArea)
    const selection = window.getSelection()
    if (selection) {
      selection.removeAllRanges()
      selection.addRange(range)
    }
    textArea.setSelectionRange(0, 999999)
  } else {
    textArea.select()
  }

  try {
    const successful = document.execCommand('copy')
    if (successful) {
      ElMessage.success('内容已复制到剪贴板')
    } else {
      ElMessage.error('复制失败，请手动复制')
    }
  } catch (err) {
    console.error('复制操作失败', err)
    ElMessage.error('复制失败，请手动复制')
  }

  document.body.removeChild(textArea)
}

// 解决提交方法问题
const submitProgram = () => {
  submitProblem()
}

const submitObjective = () => {
  submitProblem()
}

// 为canSubmit和canSubmitObjective添加计算属性
const canSubmit = computed(() => {
  return !!submitForm.code.trim()
})

const canSubmitObjective = computed(() => {
  const problemType = problemDetails.value.problemBo?.problemType

  if ([ProblemType.SINGLE_CHOICE.code, ProblemType.MULTIPLE_CHOICE.code, ProblemType.TRUE_FALSE.code].includes(problemType || '')) {
    return selectedOptions.value.length > 0
  } else if (problemType === ProblemType.FILL_BLANK.code) {
    if (problemOptions.value.length > 0) {
      return blankAnswers.value.every(answer => !!answer.trim())
    } else {
      return !!blankAnswer.value.trim()
    }
  } else if (problemType === ProblemType.SHORT_ANSWER.code) {
    return !!shortAnswer.value.trim()
  }

  return false
})

const problemDrawerVisible = ref(false) // 控制题目抽屉的显示/隐藏
const totalTime = ref(0) // 总耗时，秒为单位

// 添加控制台检测相关状态
const isDevToolsOpen = ref(false) // 控制台是否打开
const windowHeight = ref(window.innerHeight) // 窗口高度
const windowWidth = ref(window.innerWidth) // 窗口宽度
const devToolsDetectionResult = ref<DevToolsDetectionResult | null>(null) // 检测结果详情

// 控制台状态变化处理函数
const handleDevToolsChange = (result: DevToolsDetectionResult) => {
  const wasOpen = isDevToolsOpen.value
  isDevToolsOpen.value = result.isOpen
  devToolsDetectionResult.value = result

  // 更新窗口尺寸状态
  windowHeight.value = result.windowInfo.height
  windowWidth.value = result.windowInfo.width

  // 只在状态变化时输出日志
  if (wasOpen !== result.isOpen) {
    console.log('控制台状态变化:', {
      isOpen: result.isOpen,
      method: result.method,
      windowInfo: result.windowInfo
    })

    // 触发布局重新计算
    nextTick(() => {
      // 可以在这里添加额外的布局调整逻辑
    })
  }
}

// 控制台检测器清理函数
let devToolsDetectorCleanup: (() => void) | null = null
</script>

<style scoped>
.problem-submit-container {
  box-sizing: border-box;
  height: calc(100vh - 100px);
  min-height: 600px; /* 设置最小高度，确保内容可见 */
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden; /* 防止内容溢出 */
}

/* 添加过期警告样式 */
.expire-alert {
  margin-bottom: 15px;
  border-radius: 8px;
  /* 增加圆角 */
  overflow: hidden;
}

/* 重构布局，直接左右分栏 */
.problem-submit-content {
  display: flex;
  gap: 5px;
  flex: 1;
  min-height: 0; /* 修复Flex布局中的滚动问题 */
  overflow: hidden;
  height: 100%; /* 确保内容占满容器高度 */
}

.problem-detail-panel {
  flex: 1;
  border: none;
  border-radius: 12px;
  background-color: var(--el-bg-color);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: relative; /* 相对定位 */
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.problem-header {
  position: relative; /* 改回相对定位 */
  padding: 12px 16px;
  background-color: var(--el-bg-color);
  z-index: 5;
  flex-shrink: 0; /* 不压缩 */
}

.problem-title-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.problem-title {
  font-size: 1.3rem;
  /* 缩小标题 */
  font-weight: 600;
  color: var(--el-color-primary);
  padding-left: 8px;
  border-left: 4px solid var(--el-color-primary);
  flex: 1;
}

.info-toggle {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.problem-info {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  align-items: center;
  padding: 3px 3px 0;
  transition: all 0.3s ease;
}

.problem-content {
  flex: 1; /* 使用flex填充剩余空间 */
  overflow-y: auto; /* 内容可滚动 */
  scrollbar-width: thin; /* Firefox */
  padding: 0 16px 16px 16px;
}

/* 自定义滚动条样式 */
.problem-content::-webkit-scrollbar {
  width: 5px;
}

.problem-content::-webkit-scrollbar-track {
  background: transparent;
}

.problem-content::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.info-tag {
  margin-right: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

.info-text {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-right: 5px;
  background-color: var(--el-fill-color-light);
  padding: 2px 8px;
  border-radius: 20px;
}

.code-submit-panel {
  display: flex;
  flex-direction: column;
  width: 50%;
  min-width: 400px; /* 确保最小宽度 */
  border: none;
  border-radius: 12px;
  background-color: var(--el-bg-color);
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: relative; /* 为固定按钮做准备 */
}

.code-editor-wrapper {
  flex: 1; /* 使用flex布局而不是绝对定位 */
  min-height: 300px; /* 设置最小高度 */
  max-height: calc(100vh - 280px); /* 动态计算最大高度 */
  overflow: auto;
  margin-bottom: 140px; /* 为提交按钮留出空间 */
}

.markdown-content {
  line-height: 1.5;
  /* 减小行高 */
  margin-bottom: 10px;
  /* 减少底部间距 */
  font-size: 14px;
  /* 适当减小字体 */
}

.markdown-content :deep(pre) {
  background-color: var(--el-fill-color-light);
  padding: 5px;
  /* 减少内边距 */
  border-radius: 8px;
  /* 增加圆角 */
  overflow-x: auto;
  margin: 8px 0;
  /* 减少上下边距 */
  font-family: 'SF Mono', SFMono-Regular, ui-monospace, 'JetBrains Mono', monospace;
  /* Apple风格字体 */
}

.markdown-content :deep(code) {
  background-color: var(--el-fill-color);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: 'SF Mono', SFMono-Regular, ui-monospace, 'JetBrains Mono', monospace;
  /* Apple风格字体 */
}

.markdown-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 8px 0 10px;
  /* 减少上下边距 */
  border-radius: 8px;
  /* 增加圆角 */
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  /* 微妙阴影 */
}

.markdown-content :deep(th),
.markdown-content :deep(td) {
  border: 1px solid var(--el-border-color-light);
  padding: 8px 10px;
  /* 减少内边距 */
  text-align: left;
}

.markdown-content :deep(th) {
  background-color: var(--el-fill-color-light);
  font-weight: 500;
  /* Apple较少使用粗体 */
}

.markdown-content :deep(tr:nth-child(even)) {
  background-color: var(--el-fill-color-lighter);
}

/* 添加内容板块样式 */
.content-section {
  margin-bottom: 5px;
  /* 减少底部间距 */
  background-color: var(--el-bg-color);
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.content-section:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 添加标题和复制按钮布局 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  /* 减少标题与内容间距 */
}

/* 修改标题样式为更小的版本 */
.section-title {
  font-size: 0.95rem;
  /* 缩小标题 */
  margin: 0;
  padding: 6px 10px;
  /* 减少内边距 */
  color: var(--el-color-primary);
  font-weight: 700;
  /* 加粗标题 */
  background-color: var(--el-fill-color-lighter);
  border-radius: 6px;
  /* 圆角化 */
}

/* 复制按钮样式 */
.copy-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--el-color-primary);
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.copy-btn:hover {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.copy-btn .el-icon {
  font-size: 14px;
}

.options-container {
  margin: 12px 0;
  /* 减少上下边距 */
  padding: 16px;
  /* 减少内边距 */
  background-color: var(--el-fill-color-lighter);
  border-radius: 12px;
  /* 增加圆角 */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
  /* 轻微阴影 */
}

.option-item {
  margin-bottom: 5px;
  border-radius: 10px;
  /* 增加圆角 */
  overflow: hidden;
  transition: all 0.2s ease;
  /* 加快过渡效果 */
}

.option-item:last-child {
  margin-bottom: 0;
}

.option-item .el-radio,
.option-item .el-checkbox {
  width: 100%;
  margin-right: 0;
  padding: 14px 16px;
  border-radius: 10px;
  /* 增加圆角 */
  display: flex;
  align-items: flex-start;
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  transition: all 0.2s ease;
  /* Apple风格的敏捷过渡 */
}

.option-item .el-radio:hover,
.option-item .el-checkbox:hover {
  background-color: var(--el-fill-color-light);
  border-color: var(--el-border-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.06);
  /* 更轻微的阴影 */
}

.option-item .el-radio.is-checked,
.option-item .el-checkbox.is-checked {
  background-color: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.08);
}

/* 提交按钮区固定高度，确保始终可见 */
.submit-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  border-top: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: var(--el-fill-color-lighter);
  z-index: 10; /* 确保显示在上层 */
  height: 140px; /* 固定高度 */
}

.submit-action-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.problem-list-btn {
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.problem-list-btn:hover:not([disabled]) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.navigation-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.problem-timer-info {
  display: flex;
  gap: 12px;
}

.problem-timer-item {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: var(--el-fill-color-lighter);
  padding: 4px 10px;
  border-radius: 18px;
  min-width: 120px;
  transition: all 0.2s ease;
  border-left: 2px solid var(--el-color-primary);
}

.problem-timer-item:hover {
  background-color: var(--el-color-primary-light-9);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.timer-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.timer-value {
  font-weight: bold;
  color: var(--el-color-primary);
  font-family: monospace;
}

.tabs-mini-preview {
  display: flex;
  gap: 4px;
  align-items: center;
}

.mini-tab-item {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
  background-color: var(--el-fill-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.mini-tab-item:hover {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary-dark-2);
  transform: translateY(-2px);
}

.mini-tab-item.active {
  background-color: var(--el-color-primary);
  color: white;
}

.mini-tab-more {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 12px;
  background-color: var(--el-fill-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.mini-tab-more:hover {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary-dark-2);
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .submit-action-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .navigation-actions {
    width: 100%;
    margin-left: 0;
    justify-content: space-between;
  }

  .problem-timer-info {
    flex-direction: column;
    gap: 8px;
  }

  .problem-timer-item {
    min-width: 0;
    width: 100%;
  }
}

/* 调试按钮样式 */
.debug-btn {
  padding: 10px 16px;
  border-radius: 8px;
  opacity: 0.8;
}

.debug-btn:hover {
  opacity: 1;
}

/* 输入输出描述布局 */
.io-description-row {
  display: flex;
  gap: 12px;
  /* 减少列间距 */
  margin-bottom: 5px;
  /* 减少底部间距 */
}

.io-description-col {
  flex: 1;
  padding: 8px;
  /* 减少内边距 */
  background-color: var(--el-fill-color-lighter);
  border-radius: 10px;
  /* 增加圆角 */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
  /* 轻微阴影 */
}

/* 示例内容样式 */
.example-content {
  margin-bottom: 5px;
  /* 减少底部间距 */
  padding: 5px;
  /* 减少内边距 */
  background-color: var(--el-fill-color-lighter);
  border-radius: 10px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

/* 响应式设计 - 修改断点，避免控制台打开时布局切换 */
@media (max-width: 900px) {
  .problem-submit-content {
    flex-direction: column;
  }

  .problem-detail-panel,
  .code-submit-panel {
    width: 100%;
  }

  .code-editor-wrapper {
    min-height: 400px;
    position: relative; /* 改为相对定位 */
    bottom: auto;
  }

  .io-description-row {
    flex-direction: column;
    gap: 16px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .copy-btn {
    align-self: flex-end;
  }

  /* 在移动端调整提交按钮和状态为垂直布局 */
  .submit-btn-status-group {
    flex-direction: column;
    align-items: stretch;
  }

  /* 移动端下提交按钮区域调整 */
  .submit-actions {
    position: relative;
    height: auto;
    min-height: 120px;
  }
}

/* 针对控制台打开的情况优化 */
@media (max-width: 1200px) and (min-width: 901px) {
  .problem-submit-container {
    height: calc(100vh - 80px); /* 减少高度以适应控制台 */
  }

  .problem-submit-content {
    /* 保持左右布局，不切换为垂直布局 */
    flex-direction: row;
  }

  .problem-detail-panel {
    flex: 1;
    min-width: 45%;
  }

  .code-submit-panel {
    width: 50%;
    min-width: 400px; /* 确保编码区有最小宽度 */
  }

  .code-editor-wrapper {
    /* 确保编辑器在控制台打开时仍然可见 */
    min-height: 300px;
    max-height: calc(100vh - 300px);
  }
}

/* 已提交信息样式优化 */
.submitted-info {
  margin-top: 8px;

  .el-alert {
    padding: 10px 15px;
    border-radius: 8px;
  }
}

/* 加载和空状态容器 */
.loading-container,
.empty-container {
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background-color: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 修改场景类型标签的位置 - 放到题目标题旁边 */
.scene-type-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  margin-left: 10px;
  vertical-align: middle;
  z-index: 5;
  width: auto; /* 改为自动宽度 */
}

/* 修改为Apple风格的标签 */
:deep(.scene-type-tag) {
  position: relative !important;
  padding: 2px 8px !important; /* 缩小内边距 */
  border-radius: 4px !important; /* 四周都有圆角 */
  font-size: 12px !important;
  font-weight: 500 !important;
  letter-spacing: 0.02em !important;
  color: var(--el-color-primary-dark-2) !important;
  background: rgba(var(--el-color-primary-rgb), 0.08) !important;
  box-shadow: 0 1px 2px rgba(var(--el-color-primary-rgb), 0.05) !important;
  border: 1px solid rgba(var(--el-color-primary-rgb), 0.1) !important; /* 四周都有边框 */
  display: inline-flex !important; /* 改为内联弹性布局 */
  align-items: center !important;
  justify-content: center !important;
  width: auto !important;
  min-width: 60px !important; /* 设置最小宽度 */
  max-width: 180px !important;
  text-align: center !important; /* 居中对齐 */
  margin: 0 !important;
  z-index: 5 !important;
  transform: translateY(-1px) !important; /* 微调垂直位置 */

  /* 添加前缀图标 */
  &:before {
    content: '•' !important;
    display: inline-block !important;
    margin-right: 6px !important;
    color: var(--el-color-primary) !important;
  }
}

/* 调整代码编辑器滚动条 */
.code-editor-wrapper::-webkit-scrollbar {
  width: 5px;
}

.code-editor-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.code-editor-wrapper::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

/* 新增题目tabs按钮和计时信息 */
.problem-tabs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: var(--el-fill-color-lighter);
  border-bottom: 1px solid var(--el-border-color-light);
}

.tabs-nav-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.tabs-toggle-btn {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  transition: all 0.2s ease;
}

.tabs-toggle-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.tabs-mini-preview {
  display: flex;
  gap: 4px;
  align-items: center;
}

.mini-tab-item {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
  background-color: var(--el-fill-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.mini-tab-item:hover {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary-dark-2);
  transform: translateY(-2px);
}

.mini-tab-item.active {
  background-color: var(--el-color-primary);
  color: white;
}

.mini-tab-more {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 12px;
  background-color: var(--el-fill-color);
  cursor: pointer;
  transition: all 0.2s ease;
}

.mini-tab-more:hover {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary-dark-2);
  transform: translateY(-2px);
}

.timer-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.timer-item {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 4px 10px;
  border-radius: 16px;
  background-color: var(--el-fill-color);
  font-size: 12px;
  transition: all 0.2s ease;
}

.timer-item:hover {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .problem-tabs-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .timer-info {
    width: 100%;
    justify-content: space-between;
  }
}

/* 题目抽屉样式 */
.problem-drawer-content {
  padding: 10px;
  overflow-y: auto;
  max-height: 100%;
}

.problem-tabs {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.problem-tab-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  border-radius: 8px;
  background-color: var(--el-fill-color-light);
  cursor: pointer;
  transition: all 0.2s ease;
}

.problem-tab-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  background-color: var(--el-fill-color);
}

.problem-tab-item.active {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  border-left: 3px solid var(--el-color-primary);
}

.tab-index {
  font-weight: bold;
  margin-right: 10px;
  min-width: 24px;
  text-align: center;
  border-radius: 4px;
  background-color: var(--el-fill-color);
  padding: 2px 6px;
}

.tab-title {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 保留原始样式 */
.problem-position {
  font-size: 13px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  background-color: var(--el-fill-color);
  padding: 4px 10px;
  border-radius: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 导航按钮样式 */
.navigation-btn {
  padding: 4px 6px;
  border-radius: 6px;
  font-size: 10px;
  transition: all 0.2s ease;
}

.prev-btn:hover:not([disabled]) {
  transform: translateX(-2px);
}

.next-btn:hover:not([disabled]) {
  transform: translateX(2px);
}

/* 提交按钮样式 */
.submit-btn {
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.submit-btn:hover:not([disabled]) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.2);
}

/* 评测状态放在右边 */
.judge-status-wrapper {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

/* 提交按钮和状态 */
.submit-btn-status-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
}

/* 主动查询按钮样式 */
.manual-query-btn {
  min-width: 140px;
  height: 40px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.2s ease;
  animation: pulse 2s infinite;
}

.manual-query-btn:hover:not([disabled]) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--el-color-warning-rgb), 0.3);
}

/* 脉冲动画效果 */
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(var(--el-color-warning-rgb), 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(var(--el-color-warning-rgb), 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(var(--el-color-warning-rgb), 0);
  }
}

/* 控制台打开时的特殊样式 */
.problem-submit-container.devtools-open {
  height: calc(100vh - 120px); /* 减少更多高度以适应控制台 */
  min-height: 500px;
}

.problem-submit-container.devtools-open .problem-submit-content {
  /* 确保在控制台打开时保持左右布局 */
  flex-direction: row;
}

.problem-submit-container.devtools-open .code-editor-wrapper {
  max-height: calc(100vh - 320px); /* 进一步减少高度 */
  min-height: 250px;
}

.problem-submit-container.devtools-open .code-submit-panel {
  min-width: 350px; /* 在控制台打开时减少最小宽度 */
}

.problem-submit-container.devtools-open .problem-detail-panel {
  min-width: 40%; /* 减少左侧面板的最小宽度 */
}

/* 调试信息样式 */
.debug-info {
  position: fixed;
  top: 20px;
  right: 20px;
  width: 400px;
  max-height: 80vh;
  background: var(--el-bg-color);
  border: 2px solid var(--el-color-primary);
  border-radius: 8px;
  padding: 16px;
  z-index: 9999;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.debug-info h3 {
  margin: 0 0 16px 0;
  color: var(--el-color-primary);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.debug-info h4 {
  margin: 16px 0 8px 0;
  color: var(--el-text-color-primary);
  font-size: 14px;
}

.debug-section {
  background: var(--el-fill-color-lighter);
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.debug-section p {
  margin: 4px 0;
  font-size: 13px;
  font-family: monospace;
}

.debug-info pre {
  background: var(--el-fill-color-light);
  padding: 8px;
  border-radius: 4px;
  font-size: 11px;
  max-height: 200px;
  overflow-y: auto;
}
</style>
