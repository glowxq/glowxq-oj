<template>
  <!-- 基本信息表单 -->
  <div class="basic-info-form">
    <!-- 题目类型选择 - 已移除必填项限制 -->
    <div class="problem-type-section">
      <h3>选择题目类型</h3>
      <div class="problem-type-wrapper">
        <el-form-item label="题目类型" prop="problemBo.problemType">
          <div class="problem-type-wrapper">
            <enum-select
              v-model="modelValue.problemBo.problemType"
              :enum-data="ProblemType"
              :disabled="isEdit"
              placeholder="请选择题目类型"
            />
            <span v-if="isEdit" class="edit-disabled-text">
              <el-icon class="edit-disabled-icon"><InfoFilled /></el-icon>
              编辑模式下不允许修改题目类型
            </span>
          </div>
        </el-form-item>
      </div>
    </div>

    <el-divider content-position="left">基本信息</el-divider>

    <el-row :gutter="24">
      <el-col :span="12">
        <el-form-item label="题目ID" prop="problemBo.problemKey">
          <el-input
            v-model="modelValue.problemBo.problemKey"
            placeholder="请填写题目的自定义ID，例如(GOJ-1000),留空自动生成"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="题目标题" prop="problemBo.title" required>
          <el-input
            v-model="modelValue.problemBo.title"
            placeholder="请填写题目标题"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <el-col :span="8">
        <el-form-item label="作者" prop="problemBo.author" >
          <el-input
            v-model="modelValue.problemBo.author"
            placeholder="请填写作者，留空自动生成"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8" v-show="isProgramming">
        <el-form-item label="程序类型" prop="problemBo.programType" :required="isProgramming">
          <enum-select
            v-model="modelValue.problemBo.programType"
            :enum-data="ProgramType"
            type="select"
            placeholder="请选择程序类型"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="难度等级" prop="problemBo.difficulty">
          <enum-select
            v-model="modelValue.problemBo.difficulty"
            :enum-data="DifficultyLevel"
            type="select"
            placeholder="请选择难度等级"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <el-col :span="8" v-show="isProgramming">
        <el-form-item label="来源类型" prop="problemBo.sourceType">
          <enum-select
            v-model="modelValue.problemBo.sourceType"
            :enum-data="ProblemSourceType"
            type="select"
            placeholder="请选择来源类型"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8" v-show="isProgramming">
        <el-form-item label="题目来源" prop="problemBo.source">
          <el-input v-model="modelValue.problemBo.source" placeholder="请填写题目来源" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="权限类型" prop="problemBo.auth">
          <enum-select
            v-model="modelValue.problemBo.auth"
            :enum-data="AuthType"
            type="select"
            placeholder="请选择权限类型"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 资源限制 - 仅编程题显示 -->
    <div v-show="isProgramming">
      <el-divider content-position="left">资源限制</el-divider>

      <el-row :gutter="24">
        <el-col :span="8">
          <el-form-item label="时间限制(ms)" prop="problemBo.timeLimit">
            <el-input-number v-model="modelValue.problemBo.timeLimit" :min="0" :precision="0"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="内存限制(kb)" prop="problemBo.memoryLimit">
            <el-input-number v-model="modelValue.problemBo.memoryLimit" :min="0" :precision="0"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="栈限制(mb)" prop="problemBo.stackLimit">
            <el-input-number v-model="modelValue.problemBo.stackLimit" :min="0" :precision="0"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <!-- 分数设置 -->
    <div v-show="!isProgramming">
      <el-divider content-position="left">分数设置</el-divider>

      <el-row :gutter="24">
        <el-col :span="8">
          <el-form-item label="题目分数" prop="problemBo.score">
            <el-input-number v-model="modelValue.problemBo.score" :min="0" :precision="0"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <div v-show="isProgramming">
      <el-row :gutter="24">
        <el-col :span="8">
          <el-form-item label="OI题目分数" prop="problemBo.ioScore">
            <el-input-number v-model="modelValue.problemBo.ioScore" :min="0" :precision="0"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <el-divider content-position="left">标签与描述</el-divider>

    <el-row :gutter="24">
      <el-col :span="24">
        <el-form-item label="标签" prop="problemBo.tagIds">
          <tag-select
            v-model="modelValue.problemBo.tagIds"
            :multiple="true"
            placeholder="请选择题目标签"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-form-item label="题目描述" prop="problemBo.description" required>
      <div class="markdown-editor-wrapper">
        <MarkdownEditor
          v-model="modelValue.problemBo.description"
          placeholder="请填写题目描述"
          :height="300"
          :use-oss="true"
          img-dir="problem-description"
          key="problem-description-editor"
          :default-expand-toolbar="false"
          @upload-success="handleUploadSuccess"
          @upload-error="handleUploadError"
        />
      </div>
    </el-form-item>

    <!-- 新增下一步区域 -->
    <div class="next-step-actions">
      <el-button type="primary" @click="handleNextStep">下一步</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, computed, watch, onMounted } from 'vue'
import MarkdownEditor from '@/components/Common/Markdown/index.vue'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import TagSelect from '@/components/Common/Meta/Tag/TagSelect.vue'
import {
  ProblemType,
  ProgramType,
  AuthType,
  DifficultyLevel,
  ProblemSourceType
} from '@/enums/oj/problem'
import type { ProblemData } from '../types'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

defineOptions({
  name: 'BasicInfoForm'
})

const props = defineProps<{
  modelValue: ProblemData,
  isEdit?: boolean
}>()

const emit = defineEmits(['uploadSuccess', 'uploadError', 'next-step'])

// 是否为编程题类型
const isProgramming = computed(() => {
  return String(props.modelValue.problemBo.problemType) === String(ProblemType.PROGRAMMING.code)
})

/**
 * 处理文件上传成功回调
 * @param urls 上传成功的文件URL列表
 */
const handleUploadSuccess = (urls: string[]) => {
  emit('uploadSuccess', urls)
}

/**
 * 处理文件上传失败回调
 * @param error 上传失败的错误信息
 */
const handleUploadError = (error: any) => {
  emit('uploadError', error)
}

/**
 * 处理下一步按钮点击
 */
const handleNextStep = () => {
  // 基础必填项（所有题目类型都需要）
  const requiredFields = [
    { field: props.modelValue.problemBo.title, name: '题目标题' },
    { field: props.modelValue.problemBo.problemType, name: '题目类型' },
    { field: props.modelValue.problemBo.auth, name: '权限类型' },
    { field: props.modelValue.problemBo.description, name: '题目描述' }
  ]

  // 编程题额外必填项
  if (isProgramming.value) {
    requiredFields.push(
      { field: props.modelValue.problemBo.programType !== undefined, name: '程序类型' },
      { field: props.modelValue.problemBo.timeLimit, name: '时间限制' },
      { field: props.modelValue.problemBo.memoryLimit, name: '内存限制' },
      { field: props.modelValue.problemBo.stackLimit, name: '栈限制' },
    )
  }

  const emptyFields = requiredFields.filter(item => !item.field)

  if (emptyFields.length > 0) {
    ElMessage.warning({
      message: `请先填写必填项：${emptyFields.map(item => item.name).join('、')}`,
      duration: 3000
    })
    return
  }

  // 所有必填项已填写，发送下一步事件
  emit('next-step', props.modelValue.problemBo.problemType)
}

// 设置默认值
onMounted(() => {
  // 确保来源类型有默认值
  if (props.modelValue.problemBo.sourceType === '' || props.modelValue.problemBo.sourceType === undefined) {
    props.modelValue.problemBo.sourceType = 'GlowOJ'
  }

  // 确保程序类型有默认值
  if (props.modelValue.problemBo.programType === undefined) {
    props.modelValue.problemBo.programType = 0  // 默认为ACM
  }
})
</script>

<style scoped lang="scss">
.basic-info-form {
  padding: 16px;
}

.problem-type-section {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  border-left: 4px solid #409eff;

  h3 {
    margin-top: 0;
    margin-bottom: 15px;
    color: #409eff;
  }
}

.problem-type-wrapper {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.edit-disabled-icon {
  margin-right: 4px;
  color: #E6A23C;
  font-size: 16px;
  vertical-align: middle;
}

.edit-disabled-text {
  display: block;
  color: #E6A23C;
  font-size: 14px;
  margin-top: 5px;
  line-height: 1.5;
}

/* 调整表单项布局，使图标可以正确显示 */
:deep(.el-form-item) {
  position: relative;

  .el-form-item__content {
    display: flex;
    flex-direction: column;
  }
}

.el-divider {
  margin: 15px 0;

  :deep(.el-divider__text) {
    font-weight: bold;
    color: #606266;
  }
}

.markdown-editor-wrapper {
  width: 100%;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
  min-height: 200px;
  margin-bottom: 10px;

  :deep(.markdown-editor-container) {
    width: 100%;
    height: 100%;
    border: none;

    .markdown-editor {
      min-height: 200px;
    }
  }
}

:deep(.el-form-item.is-required:not(.is-no-asterisk) > .el-form-item__label:before) {
  color: #f56c6c;
}

:deep(.el-input-number) {
  width: 100%;
}

.next-step-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
