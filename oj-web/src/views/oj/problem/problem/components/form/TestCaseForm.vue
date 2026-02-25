<template>
  <!-- 测试数据表单 -->
  <div class="testcase-container">
    <!-- 类型选择区域 -->
    <div class="type-selection-section">
      <h3 class="section-title">测试用例类型</h3>
      <div class="type-selector">
        <enum-select
          v-model="testCaseTypeSelected"
          :enum-data="TestCaseType"
          type="radio"
          placeholder="请选择测试用例类型"
          @change="handleTestCaseTypeChange"
        />
      </div>
      <div class="type-description" v-if="testCaseTypeSelected === 'ManualEditing'">
        {{ getTestCaseTypeInfo('ManualEditing', 'text') }}
      </div>
      <div class="type-description" v-else-if="testCaseTypeSelected === 'FileUpload'">
        {{ getTestCaseTypeInfo('FileUpload', 'text') }}
      </div>
    </div>

    <!-- 手动编辑模式 -->
    <div v-if="testCaseTypeSelected === 'ManualEditing'">
      <el-divider>{{ getTestCaseTypeInfo('ManualEditing', 'name') }}</el-divider>

      <!-- 手动编辑区域 -->
      <div class="manual-edit-section">
        <div class="section-title">
          <h3>测试用例列表</h3>
          <el-button type="primary" @click="addSample">添加测试用例</el-button>
        </div>

        <!-- 测试用例列表区域，设置最大高度和滚动条 -->
        <div class="samples-container manual-mode">
          <TestCaseSample
            v-for="(sample, index) in modelValue.problemCaseDataList"
            :key="index"
            :sample="sample"
            :index="index"
            @remove="removeSample"
          />
        </div>
      </div>
    </div>

    <!-- 文件上传模式 -->
    <div v-else-if="testCaseTypeSelected === 'FileUpload'">
      <el-divider>{{ getTestCaseTypeInfo('FileUpload', 'name') }}</el-divider>

      <!-- 上传区域 -->
      <div class="upload-section">
        <h3 class="section-title">上传测试用例 (ZIP压缩包)</h3>

        <!-- 上传组件和信息区域 - 使用卡片包装 -->
        <el-card class="upload-card" shadow="hover">
          <div class="upload-card-content">
            <!-- 左侧上传说明 -->
            <div class="upload-info-area">
              <p class="upload-description">
                您可以上传包含所有测试用例的ZIP压缩包，系统会自动解析并提取测试用例
              </p>

              <!-- 上传组件区域 -->
              <div class="upload-area">
                <UploadFile
                  :use-oss="true"
                  :dir="'testcase'"
                  :accept="'.zip,.rar'"
                  :file-size="50"
                  :before-upload="beforeUploadZip"
                  @change="handleZipUploadCallback"
                >
                  <template #tip>
                    <div class="el-upload__tip">
                      请上传ZIP格式的压缩文件，文件大小不超过50MB
                    </div>
                  </template>
                </UploadFile>
              </div>
            </div>

            <!-- 右侧上传成功信息 -->
            <div v-if="uploadTestcaseDir" class="upload-success-info">
              <el-alert
                title="上传成功"
                type="success"
                :closable="false"
                show-icon
              >
                <template #default>
                  <div class="upload-path-info">
                    <p class="path-label">文件存储路径:</p>
                    <p class="path-value">{{ uploadTestcaseDir }}</p>
                  </div>
                </template>
              </el-alert>
            </div>
          </div>
        </el-card>

        <!-- 上传后的测试用例数据展示 -->
        <div v-if="modelValue.problemCaseDataList && modelValue.problemCaseDataList.length > 0" class="uploaded-samples-section">
          <div class="section-header">
            <h3 class="section-title">上传的测试用例数据 <span class="sample-count">(共 {{
                modelValue.problemCaseDataList.length
              }} 个)</span></h3>
            <el-button
              type="primary"
              link
              size="small"
              @click="toggleSamplesExpand"
            >
              {{ samplesExpanded ? '收起' : '展开' }}
              <el-icon><component :is="samplesExpanded ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
            </el-button>
          </div>

          <div class="samples-container upload-mode" v-show="samplesExpanded">
            <TestCaseSample
              v-for="(sample, index) in modelValue.problemCaseDataList"
              :key="index"
              :sample="sample"
              :index="index"
              :readonly="true"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch } from 'vue'
import { ElMessage } from 'element-plus'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import UploadFile from '@/components/Common/Upload/file.vue'
import TestCaseSample from './TestCaseSample.vue'
import { TestCaseType } from '@/enums/oj/problem'
import { uploadTestcaseZipUrlApi } from '@/api/modules/oj/problem/problemCase'
import type { IUploadResult } from '@/api/interface/system/admin/upload'
import type { ProblemData } from '../types'
// 图标

// 扩展的测试用例类型接口，包含提示和文本
defineOptions({
  name: 'TestCaseForm'
})

const props = defineProps<{
  modelValue: ProblemData
  isEdit?: boolean
}>()

const emit = defineEmits(['update:modelValue'])

// 选择的测试用例类型
const testCaseTypeSelected = ref('ManualEditing')

// 测试用例文件列表
const caseFileList = ref<Array<{input: string, output: string, groupNum: number}>>([])
// 测试用例文件存储目录
const uploadTestcaseDir = ref(props.modelValue.uploadTestcaseDir || '')
// 测试用例列表是否展开
const samplesExpanded = ref(true)

/**
 * 获取测试用例类型的属性
 * @param code 类型代码
 * @param prop 要获取的属性名称
 * @returns 属性值
 */
const getTestCaseTypeInfo = (code: string, prop: string): string => {
  // 安全地获取指定代码的枚举值
  const enumValue = TestCaseType[code as keyof typeof TestCaseType] as any
  if (enumValue && prop in enumValue) {
    return String(enumValue[prop] || '')
  }
  return ''
}

/**
 * 处理测试用例类型变更
 */
const handleTestCaseTypeChange = (value: string) => {
  // 确保value是字符串，并更新所有样例的类型
  const typeValue = String(value)

  props.modelValue.problemCaseDataList.forEach(sample => {
    sample.type = typeValue
  })

  // 如果选择的是文件上传类型，设置上传标记
  if (typeValue === 'FileUpload') {
    props.modelValue.problemBo.uploadCase = true
  } else {
    props.modelValue.problemBo.uploadCase = false
  }

  emit('update:modelValue', props.modelValue)
}

/**
 * 添加样例数据
 */
const addSample = () => {
  props.modelValue.problemCaseDataList.push({
    input: '',
    output: '',
    type: String(testCaseTypeSelected.value),
    score: 0,
    groupNum: 0
  })
  emit('update:modelValue', props.modelValue)
}

/**
 * 删除样例数据
 * @param index 要删除的样例索引
 */
const removeSample = (index: number) => {
  props.modelValue.problemCaseDataList.splice(index, 1)
  emit('update:modelValue', props.modelValue)
}

/**
 * 验证ZIP文件上传前的处理
 * @param file 待上传的文件对象
 * @returns 是否允许上传
 */
const beforeUploadZip = (file: File) => {
  // 检查文件类型
  const isZip = file.type === 'application/zip' ||
                file.type === 'application/x-zip-compressed' ||
                file.name.endsWith('.zip');

  // 检查文件大小（不超过50MB）
  const isLt50M = file.size / 1024 / 1024 < 50;

  if (!isZip) {
    ElMessage.error('上传文件必须是ZIP格式!');
    return false;
  }

  if (!isLt50M) {
    ElMessage.error('上传文件大小不能超过50MB!');
    return false;
  }

  // 设置上传标记
  props.modelValue.problemBo.uploadCase = true;
  emit('update:modelValue', props.modelValue)

  return true;
}

/**
 * 处理ZIP文件上传成功后的逻辑
 * @param response 上传成功的响应数据
 */
const handleZipUploadCallback = (response: IUploadResult | null) => {
  if (!response) return;

  try {
    // 使用新的后端接口处理ZIP文件URL
    processUploadedZipFile(response.url);
  } catch (error) {
    console.error('处理ZIP文件出错:', error);
    ElMessage.error('处理ZIP文件失败');
  }
};

/**
 * 处理上传的ZIP文件
 * @param fileUrl 上传后的文件URL
 */
const processUploadedZipFile = async (fileUrl: string) => {
  try {
    // 调用新的后端接口处理ZIP文件URL
    const response = await uploadTestcaseZipUrlApi(fileUrl);

    if (response.data) {
      // 处理成功响应
      handleZipUploadSuccess(response.data);
    } else {
      // 处理失败响应
      ElMessage.error('处理ZIP文件失败: ' + (response.message || '未知错误'));
    }
  } catch (error: any) {
    console.error('处理ZIP文件出错:', error);
    ElMessage.error('处理ZIP文件失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 处理ZIP文件上传成功后的逻辑
 * @param response 上传成功的响应数据
 */
const handleZipUploadSuccess = (response: any) => {
  // 如果有测试用例文件列表，则显示并自动填充到表单中
  if (response.fileList && Array.isArray(response.fileList)) {
    caseFileList.value = response.fileList;

    // 将上传的测试用例数据自动填充到手动编辑区域
    const newSamples = response.fileList.map((item: any) => ({
      input: item.input || '',
      output: item.output || '',
      type: String(testCaseTypeSelected.value), // 确保type是字符串
      score: item.score || 0,
      groupNum: item.groupNum || 0
    }));

    props.modelValue.problemCaseDataList = newSamples;
  }

  // 保存测试用例文件存储目录
  if (response.fileListDir) {
    uploadTestcaseDir.value = response.fileListDir;
    props.modelValue.uploadTestcaseDir = response.fileListDir;
  }

  emit('update:modelValue', props.modelValue)
  ElMessage.success('测试用例上传成功并已自动填充!');
};

/**
 * 展开/收起测试用例列表
 */
const toggleSamplesExpand = () => {
  samplesExpanded.value = !samplesExpanded.value
}

// 监听props变化，初始化testCaseTypeSelected
watch(() => props.modelValue, () => {
  // 如果有样例数据，根据第一个样例的类型设置全局类型
  if (props.modelValue.problemCaseDataList && props.modelValue.problemCaseDataList.length > 0) {
    testCaseTypeSelected.value = props.modelValue.problemCaseDataList[0].type
  } else if (props.modelValue.problemBo.uploadCase) {
    // 如果设置了uploadCase为true，则选择FileUpload类型
    testCaseTypeSelected.value = 'FileUpload'
  }
}, { immediate: true })
</script>

<style scoped lang="scss">
/* 测试数据选项卡样式 */
.testcase-container {
  padding: 16px;
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

    .sample-count {
      margin-left: 8px;
      font-size: 14px;
      color: #909399;
      font-weight: normal;
    }
  }
}

.type-selection-section {
  margin-bottom: 12px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.type-selector {
  margin-top: 12px;
}

.type-description {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

/* 样例列表容器，添加滚动条 */
.samples-container {
  max-height: 600px;
  overflow-y: auto;
  padding-right: 8px;
  margin-bottom: 20px;

  &.manual-mode {
    border-left: 3px solid #409eff;
    padding-left: 8px;
  }

  &.upload-mode {
    border-left: 3px solid #67c23a;
    padding-left: 8px;
  }
}

.manual-edit-section {
  margin-bottom: 24px;
}

.upload-section {
  margin-bottom: 24px;
}

.uploaded-samples-section {
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px dashed #dcdfe6;
}

.upload-description {
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.upload-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.upload-card-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.upload-info-area {
  flex: 1;
  min-width: 280px;
}

.upload-success-info {
  flex: 1;
  min-width: 280px;
  display: flex;
  align-items: center;
}

.upload-path-info {
  margin-top: 5px;
}

.path-label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #67c23a;
}

.path-value {
  word-break: break-all;
  font-family: monospace;
  background-color: #f8f8f8;
  padding: 5px 8px;
  border-radius: 4px;
  border-left: 3px solid #67c23a;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.upload-area {
  margin-bottom: 10px;
}

/* 响应式样式 */
@media screen and (max-width: 768px) {
  .testcase-container {
    padding: 12px 8px;
  }

  .samples-container {
    max-height: 500px;
    padding-right: 4px;

    &.manual-mode,
    &.upload-mode {
      padding-left: 4px;
    }
  }

  .section-title {
    flex-direction: column;
    align-items: flex-start;

    h3 {
      margin-bottom: 12px;
    }
  }

  .type-selection-section {
    padding: 12px;
  }
}
</style>
