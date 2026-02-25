<template>
  <!-- 客观题选项表单 -->
  <div class="options-container">
    <!-- 左侧浮动菜单 -->
    <div class="floating-menu" :class="{ 'menu-collapsed': !menuVisible }">
      <div v-if="menuVisible" class="menu-content">
        <div class="menu-title">
          <span>选项导航</span>
          <el-icon class="menu-close" @click="toggleMenu"><Close /></el-icon>
        </div>
        <ul class="menu-list">
          <li v-if="modelValue.problemBo.problemType === ProblemType.SINGLE_CHOICE.code || modelValue.problemBo.problemType === ProblemType.MULTIPLE_CHOICE.code"
              v-for="(option, index) in problemOptions"
              :key="`option-${index}`"
              @click="scrollToItem(`option-${index}`)">
            选项 {{ getOptionKey(index) }}
          </li>
          <li v-if="modelValue.problemBo.problemType === ProblemType.FILL_BLANK.code"
              v-for="(blank, index) in problemBlanks"
              :key="`blank-${index}`"
              @click="scrollToItem(`blank-${index}`)">
            填空 #{{ index + 1 }}
          </li>
        </ul>
      </div>
      <div v-if="!menuVisible" class="menu-toggle" @click="toggleMenu">
        <el-icon><Menu /></el-icon>
      </div>
    </div>

    <!-- 显示题目描述，方便参考 -->
    <el-card class="problem-description-card">
      <template #header>
        <div class="card-header">
          <span>题目描述</span>
        </div>
      </template>
      <div class="markdown-preview">
        <MarkdownEditor
          v-model="modelValue.problemBo.description"
          :readonly="true"
          :height="'auto'"
          placeholder="暂无题目描述"
          @mounted="handleMarkdownMounted('preview')"
        />
      </div>
    </el-card>

    <!-- 单选题/多选题选项 -->
    <template v-if="modelValue.problemBo.problemType === ProblemType.SINGLE_CHOICE.code || modelValue.problemBo.problemType === ProblemType.MULTIPLE_CHOICE.code">
      <div class="section-title">
        <h3>选项设置</h3>
        <el-button type="primary" @click="addOption">添加选项</el-button>
      </div>

      <div v-for="(option, index) in problemOptions" :key="index" class="option-item" :id="`option-${index}`">
        <div class="option-header" @click="toggleCollapse(`option-${index}`)">
          <div class="option-header-left">
            <el-tag size="small" effect="plain">{{ getOptionKey(index) }}</el-tag>
            <span class="option-title">{{ option.optionContent ? (option.optionContent.length > 20 ? option.optionContent.substring(0, 20) + '...' : option.optionContent) : '未设置内容' }}</span>
            <el-tag v-if="option.answer" type="success" size="small">正确答案</el-tag>
          </div>
          <div class="option-header-right">
            <el-icon :class="collapseState[`option-${index}`] ? 'el-icon-arrow-down' : 'el-icon-arrow-right'">
              <component :is="collapseState[`option-${index}`] ? 'ArrowDown' : 'ArrowRight'" />
            </el-icon>
            <el-button type="danger" size="small" @click.stop="removeOption(index)">删除</el-button>
          </div>
        </div>

        <div v-show="collapseState[`option-${index}`]" class="option-content">
          <div class="option-form-row">
            <el-form-item :label="`选项标识`" :prop="`options[${index}].optionKey`" class="form-item-compact">
              <el-input
                v-model="option.optionKey"
                placeholder="请填写选项标识，如A/B/C/D"
                :disabled="true"
              ></el-input>
            </el-form-item>
            <el-form-item :label="`选项分值`" :prop="`options[${index}].score`" class="form-item-compact">
              <el-input-number v-model="option.score" :min="0" :precision="0" controls-position="right" :controls="false" style="width: 120px"></el-input-number>
            </el-form-item>
            <el-form-item :label="`是否正确`" :prop="`options[${index}].answer`" class="form-item-compact">
              <el-switch v-model="option.answer"
                @change="(val: boolean) => onOptionAnswerChange(index, val)"></el-switch>
            </el-form-item>
          </div>

          <el-form-item :label="`选项内容`" :prop="`options[${index}].optionContent`">
            <el-input
              type="textarea"
              v-model="option.optionContent"
              placeholder="请填写选项内容"
              :rows="2"
            ></el-input>
          </el-form-item>
        </div>
      </div>
      <div v-if="modelValue.problemBo.problemType === ProblemType.SINGLE_CHOICE.code && hasMultipleCorrectOptions" class="option-warning">
        <el-alert
          title="单选题只能有一个正确答案"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
    </template>

    <!-- 判断题选项 -->
    <template v-if="modelValue.problemBo.problemType === ProblemType.TRUE_FALSE.code">
      <div class="true-false-container">
        <h3 class="section-title-text">判断题答案设置</h3>

        <el-form-item label="正确答案" prop="problemBo.answer" class="true-false-form-item">
          <div class="true-false-options">
            <div
              class="true-false-option"
              :class="{ 'selected': modelValue.problemBo.answer === true }"
              @click="selectTrueFalseOption(true)"
            >
              <div class="option-icon true-icon">
                <el-icon><Check /></el-icon>
              </div>
              <div class="option-text">正确</div>
            </div>

            <div
              class="true-false-option"
              :class="{ 'selected': modelValue.problemBo.answer === false }"
              @click="selectTrueFalseOption(false)"
            >
              <div class="option-icon false-icon">
                <el-icon><Close /></el-icon>
              </div>
              <div class="option-text">错误</div>
            </div>
          </div>
        </el-form-item>

        <div class="form-tip">
          <el-alert
            type="info"
            :closable="false"
            show-icon
          >
            <template #title>
              <span>判断题提示</span>
            </template>
            <p>请选择正确的答案选项。判断题的题干应该是一个陈述句，学生需要判断该陈述是正确还是错误。</p>
          </el-alert>
        </div>
      </div>
    </template>

    <!-- 填空题答案 -->
    <template v-if="modelValue.problemBo.problemType === ProblemType.FILL_BLANK.code">
      <div class="section-title">
        <h3>填空答案设置</h3>
        <div class="fill-blank-actions">
          <el-tooltip content="系统会自动从题目描述中识别连续的三个下划线作为填空，也可以手动添加" placement="top">
            <el-button type="primary" @click="addBlank">添加填空</el-button>
          </el-tooltip>
          <el-button type="success" @click="parseBlankFromDescription">从题目描述自动识别</el-button>
        </div>
      </div>

      <el-alert
        type="info"
        :closable="false"
        show-icon
        class="fill-blank-rules"
      >
        <template #title>
          <span>填空规则说明：</span>
        </template>
        <p>1. 在题目描述中使用连续三个或更多下划线<code>___</code>表示填空位置</p>
        <p>2. 系统会自动识别这些下划线并创建对应数量的填空项</p>
        <p>3. 您也可以手动添加填空项</p>
      </el-alert>

      <div v-for="(blank, index) in problemBlanks" :key="index" class="blank-item" :id="`blank-${index}`">
        <div class="option-header" @click="toggleCollapse(`blank-${index}`)">
          <div class="option-header-left">
            <el-tag size="small" effect="plain">填空 #{{ index + 1 }}</el-tag>
            <span class="option-title">{{ blank.optionContent ? (blank.optionContent.length > 20 ? blank.optionContent.substring(0, 20) + '...' : blank.optionContent) : '未设置内容' }}</span>
          </div>
          <div class="option-header-right">
            <el-icon :class="collapseState[`blank-${index}`] ? 'el-icon-arrow-down' : 'el-icon-arrow-right'">
              <component :is="collapseState[`blank-${index}`] ? 'ArrowDown' : 'ArrowRight'" />
            </el-icon>
            <el-button type="danger" size="small" @click.stop="removeBlank(index)">删除</el-button>
          </div>
        </div>

        <div v-show="collapseState[`blank-${index}`]" class="option-content">
          <div class="option-form-row">
            <el-form-item :label="`选项标识`" :prop="`blanks[${index}].optionKey`" class="form-item-compact">
              <el-input
                v-model="blank.optionKey"
                placeholder="请填写选项标识"
                :disabled="true"
                style="width: 120px"
              ></el-input>
            </el-form-item>
            <el-form-item :label="`分值`" :prop="`blanks[${index}].score`" class="form-item-compact">
              <el-input-number v-model="blank.score" :min="0" :precision="0" controls-position="right" :controls="false" style="width: 120px"></el-input-number>
            </el-form-item>
            <el-form-item :label="`是否答案`" :prop="`blanks[${index}].answer`" class="form-item-compact">
              <el-switch v-model="blank.answer" :disabled="true"></el-switch>
            </el-form-item>
          </div>

          <el-form-item :label="`答案内容`" :prop="`blanks[${index}].answer`">
            <el-input
              v-model="blank.optionContent"
              placeholder="请填写答案内容"
            ></el-input>
          </el-form-item>
        </div>
      </div>
    </template>

    <!-- 简答题答案 -->
    <template v-if="modelValue.problemBo.problemType === ProblemType.SHORT_ANSWER.code">
      <el-form-item label="参考答案" prop="problemBo.referenceAnswer">
        <el-input
          type="textarea"
          v-model="modelValue.problemBo.referenceAnswer"
          placeholder="请填写参考答案"
          :rows="6"
        ></el-input>
      </el-form-item>
      <el-form-item label="评分标准" prop="problemBo.gradingCriteria">
        <el-input
          type="textarea"
          v-model="modelValue.problemBo.gradingCriteria"
          placeholder="请填写评分标准"
          :rows="4"
        ></el-input>
      </el-form-item>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, defineProps, defineEmits, defineComponent, watch } from 'vue'
import { ElMessage } from 'element-plus'
import MarkdownEditor from '@/components/Common/Markdown/index.vue'
import { ProblemType } from '@/enums/oj/problem'
import type { ProblemData, ProblemOption, MarkdownLoadedState } from '../types'
import { ArrowDown, ArrowRight, Close, Menu, Check } from '@element-plus/icons-vue'

defineOptions({
  name: 'ObjectiveForm'
})

const props = defineProps<{
  modelValue: ProblemData
}>()

const emit = defineEmits(['update:modelValue', 'markdownMounted'])

// 客观题选项
const problemOptions = reactive<ProblemOption[]>(props.modelValue.options || [])

// 填空题空白 - 仅用于临时存储UI显示，最终需要合并到options
const problemBlanks = reactive<ProblemOption[]>([])

// Markdown编辑器加载状态
const markdownLoaded = ref<MarkdownLoadedState>({
  description: false,
  input: false,
  output: false,
  hint: false
})

// 折叠状态管理
const collapseState = reactive<Record<string, boolean>>({})

// 添加检查单选题是否有多个正确选项的计算属性
const hasMultipleCorrectOptions = computed(() => {
  if (props.modelValue.problemBo.problemType !== ProblemType.SINGLE_CHOICE.code) return false
  return problemOptions.filter(option => option.answer).length > 1
})

// 菜单显示状态
const menuVisible = ref(false)

// 组件挂载时，如果是填空题且options中有数据，则初始化problemBlanks
const initializeBlanks = () => {
  if (props.modelValue.problemBo.problemType === ProblemType.FILL_BLANK.code &&
      props.modelValue.options && props.modelValue.options.length > 0) {
    // 清空当前blanks
    problemBlanks.splice(0, problemBlanks.length)
    // 从options复制数据到blanks用于UI显示
    props.modelValue.options.forEach(option => {
      problemBlanks.push({...option})
    })
  }
}

// 初始调用一次
initializeBlanks()

// 监听题目类型变化，当切换到填空题时初始化blanks
watch(() => props.modelValue.problemBo.problemType, (newType) => {
  if (newType === ProblemType.FILL_BLANK.code) {
    initializeBlanks()
  }
})

/**
 * 处理Markdown编辑器加载完成
 * @param type 加载完成的编辑器类型
 */
const handleMarkdownMounted = (type: string) => {
  markdownLoaded.value[type as keyof typeof markdownLoaded.value] = true
  emit('markdownMounted', type)
}

/**
 * 切换选项折叠状态
 * @param id 选项ID
 */
const toggleCollapse = (id: string) => {
  collapseState[id] = !collapseState[id]
}

/**
 * 滚动到指定选项
 * @param id 选项ID
 */
const scrollToItem = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'center' })
    // 展开被点击的选项
    collapseState[id] = true
  }
}

/**
 * 添加选项（针对单选和多选题）
 */
const addOption = () => {
  const newIndex = problemOptions.length
  const newId = `option-${newIndex}`

  problemOptions.push({
    optionKey: getOptionKey(newIndex),
    optionContent: '',
    score: props.modelValue.problemBo.problemType === ProblemType.SINGLE_CHOICE.code ? 10 : 5,
    answer: false
  })

  // 设置新添加的选项为展开状态
  collapseState[newId] = true

  // 更新父组件的选项数据
  updateOptionsData()

  // 滚动到新添加的选项
  setTimeout(() => {
    scrollToItem(newId)
  }, 100)
}

/**
 * 获取选项标识（A/B/C/D...）
 * @param index 选项索引
 * @returns 选项标识
 */
const getOptionKey = (index: number): string => {
  return String.fromCharCode(65 + index)
}

/**
 * 处理选项答案变更的事件处理函数
 * @param index 变更的选项索引
 * @param value 是否为正确答案
 */
const onOptionAnswerChange = (index: number, value: boolean) => {
  handleOptionAnswerChange(index, value)

  // 更新父组件的选项数据
  updateOptionsData()
}

/**
 * 处理选项答案变更
 * @param index 变更的选项索引
 * @param value 是否为正确答案
 */
const handleOptionAnswerChange = (index: number, value: boolean = false): void => {
  // 单选题只能有一个正确答案
  if (props.modelValue.problemBo.problemType === ProblemType.SINGLE_CHOICE.code && value) {
    // 将其他选项设为非正确答案
    problemOptions.forEach((option, i) => {
      if (i !== index) {
        option.answer = false
      }
    })
  }
}

/**
 * 删除选项
 * @param index 要删除的选项索引
 */
const removeOption = (index: number) => {
  problemOptions.splice(index, 1)
  // 更新剩余选项的标识
  problemOptions.forEach((option, i) => {
    option.optionKey = getOptionKey(i)
  })

  // 更新父组件的选项数据
  updateOptionsData()
}

/**
 * 添加填空（针对填空题）
 */
const addBlank = () => {
  const newIndex = problemBlanks.length
  const newId = `blank-${newIndex}`

  problemBlanks.push({
    optionKey: `填空${newIndex + 1}`,
    optionContent: '',
    score: 5,
    answer: true // 填空题的选项默认都是答案
  })

  // 设置新添加的填空为展开状态
  collapseState[newId] = true

  // 更新父组件的选项数据 - 填空题要保存到options字段
  updateFillBlanksToOptions()

  // 滚动到新添加的填空
  setTimeout(() => {
    scrollToItem(newId)
  }, 100)
}

/**
 * 从题目描述中解析填空
 */
const parseBlankFromDescription = () => {
  const description = props.modelValue.problemBo.description || ''

  // 使用正则表达式匹配三个连续的下划线
  const matches = description.match(/_{3,}/g)

  if (!matches || matches.length === 0) {
    ElMessage.warning('未在题目描述中找到填空符号（连续的三个下划线"___"）')
    return
  }

  // 清空现有填空
  problemBlanks.splice(0, problemBlanks.length)

  // 为每个匹配添加填空
  matches.forEach((_, index) => {
    const newId = `blank-${index}`
    problemBlanks.push({
      optionKey: `填空${index + 1}`,
      optionContent: '',
      score: 5,
      answer: true
    })
    // 设置填空为展开状态
    collapseState[newId] = true
  })

  // 更新父组件的填空数据 - 填空题要保存到options字段
  updateFillBlanksToOptions()

  ElMessage.success(`从题目描述中找到 ${matches.length} 个填空，已自动添加`)
}

/**
 * 删除填空
 * @param index 要删除的填空索引
 */
const removeBlank = (index: number) => {
  problemBlanks.splice(index, 1)
  // 更新剩余填空的标识
  problemBlanks.forEach((blank, i) => {
    blank.optionKey = `填空${i + 1}`
  })

  // 更新父组件的填空数据 - 填空题要保存到options字段
  updateFillBlanksToOptions()
}

/**
 * 更新父组件的选项数据
 */
const updateOptionsData = () => {
  props.modelValue.options = [...problemOptions]
  emit('update:modelValue', props.modelValue)
}

/**
 * 更新填空题数据到options字段
 */
const updateFillBlanksToOptions = () => {
  if (props.modelValue.problemBo.problemType === ProblemType.FILL_BLANK.code) {
    // 将填空题数据存储到options字段
    props.modelValue.options = [...problemBlanks]
    // 清空blanks字段，确保不会重复存储
    props.modelValue.blanks = []
  } else {
    // 非填空题保持原来的数据结构
    updateBlanksData()
  }
  emit('update:modelValue', props.modelValue)
}

/**
 * 更新父组件的填空数据 - 仅用于非填空题
 */
const updateBlanksData = () => {
  if (props.modelValue.problemBo.problemType !== ProblemType.FILL_BLANK.code) {
    props.modelValue.blanks = [...problemBlanks]
  }
  emit('update:modelValue', props.modelValue)
}

/**
 * 切换菜单显示状态
 */
const toggleMenu = () => {
  menuVisible.value = !menuVisible.value
}

/**
 * 选择判断题选项
 * @param value 选项值（true/false）
 */
const selectTrueFalseOption = (value: boolean) => {
  props.modelValue.problemBo.answer = value
  emit('update:modelValue', props.modelValue)
}
</script>

<style scoped lang="scss">
/* 选项&答案样式 */
.options-container {
  padding: 16px;
  position: relative;
}

.option-item, .blank-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 12px;
  background-color: #fafafa;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

.option-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 4px 0;

  .option-header-left {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    min-width: 0;

    .option-title {
      font-size: 14px;
      color: #606266;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }

  .option-header-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.option-content {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #ebeef5;
}

.option-form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;

  .form-item-compact {
    margin-bottom: 0;
    min-width: 120px;
  }
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
  }
}

/* 题目描述卡片样式 */
.problem-description-card {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    color: #303133;
  }

  .markdown-preview {
    padding: 10px 0;

    :deep(.vditor) {
      border: none;
    }
  }
}

/* 填空题操作区域 */
.fill-blank-actions {
  display: flex;
  gap: 10px;
}

/* 填空规则提示样式 */
.fill-blank-rules {
  margin-bottom: 16px;

  p {
    margin: 4px 0;
  }

  code {
    background-color: #f0f0f0;
    padding: 2px 4px;
    border-radius: 2px;
    color: #c7254e;
  }
}

/* 选项警告样式 */
.option-warning {
  margin: 10px 0;
}

/* 浮动菜单样式 */
.floating-menu {
  position: fixed;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  background-color: #fff;
  border-radius: 0 4px 4px 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 100;
  transition: all 0.3s ease;

  &.menu-collapsed {
    width: auto;
  }

  .menu-content {
    width: 100px;
    max-height: 70vh;
    overflow-y: auto;
  }

  .menu-title {
    padding: 6px 10px;
    font-weight: bold;
    border-bottom: 1px solid #ebeef5;
    font-size: 13px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #303133;

    .menu-close {
      cursor: pointer;
      font-size: 12px;
      &:hover {
        color: #f56c6c;
      }
    }
  }

  .menu-toggle {
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    padding: 5px;
    color: #606266;

    &:hover {
      color: #409EFF;
    }
  }

  .menu-list {
    padding: 0;
    margin: 0;
    list-style: none;

    li {
      padding: 6px 10px;
      cursor: pointer;
      font-size: 12px;
      color: #606266;
      border-bottom: 1px solid #f0f0f0;

      &:hover {
        background-color: #f5f7fa;
        color: #409eff;
      }

      &:last-child {
        border-bottom: none;
      }
    }
  }
}

/* 判断题样式 */
.true-false-container {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.section-title-text {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.true-false-form-item {
  margin-bottom: 30px;
}

.true-false-options {
  display: flex;
  gap: 30px;
  margin-top: 10px;
}

.true-false-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
  padding: 15px;

  &:hover {
    border-color: #c0c4cc;
    background-color: #f5f7fa;
  }

  &.selected {
    border-color: #409EFF;
    background-color: #ecf5ff;

    .option-text {
      color: #409EFF;
      font-weight: 600;
    }

    &:hover {
      background-color: #ecf5ff;
    }
  }

  .option-icon {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 15px;
    font-size: 24px;

    &.true-icon {
      background-color: #f0f9eb;
      color: #67c23a;
    }

    &.false-icon {
      background-color: #fef0f0;
      color: #f56c6c;
    }
  }

  .option-text {
    font-size: 16px;
    color: #606266;
  }
}

.form-tip {
  margin-top: 20px;

  p {
    margin: 8px 0;
    line-height: 1.5;
  }
}
</style>
