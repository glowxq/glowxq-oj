<template>
  <el-dialog
    v-model="visible"
    title="批量导入编程题"
    width="500px"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      label-position="right"
    >
      <!-- 文件上传 -->
      <el-form-item label="选择文件" prop="batchFileUrl">
        <UploadFile
          v-model="formData.batchFileUrl"
          accept=".zip"
          :limit="1"
          :file-size="1000"
          :use-oss="true"
          dir="problem/batch-import"
          :file-list="fileList"
          @update:fileList="handleFileListUpdate"
          @change="handleFileChange"
        />
        <div class="upload-tip">
          支持.zip格式的批量编程题压缩包（包含多个题目压缩包的压缩包）
        </div>
      </el-form-item>

      <!-- 题目来源 -->
      <el-form-item label="题目来源" prop="problemSourceType">
        <EnumSelect
          :enumData="ProblemSourceType"
          type="select"
          v-model="formData.problemSourceType"
        />
      </el-form-item>

      <!-- 是否需要插入不存在的标签 -->
      <el-form-item label="标签处理" prop="needInsertUnExistTag">
        <el-switch
          v-model="formData.needInsertUnExistTag"
          active-text="自动创建不存在的标签"
          inactive-text="忽略不存在的标签"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确认导入
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type UploadUserFile } from 'element-plus'
import { ProblemSourceType } from '@/enums/oj/problem/ProblemSourceType'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import UploadFile from '@/components/Common/Upload/file.vue'
import { batchImportProblemProgramApi } from '@/api/modules/oj/problem/problem'

defineOptions({
  name: 'ImportBatchProgramDialog'
})

// 对话框可见状态
const visible = ref(false)
// 表单引用
const formRef = ref<FormInstance>()
// 加载状态
const loading = ref(false)
// 文件列表
const fileList = ref<UploadUserFile[]>([])

// 表单数据
const formData = reactive({
  batchFileUrl: '',
  problemSourceType: ProblemSourceType.HOJ.code,
  needInsertUnExistTag: false
})

// 表单验证规则
const rules = {
  batchFileUrl: [
    { required: true, message: '请上传批量题目压缩包', trigger: 'change' }
  ],
  problemSourceType: [
    { required: true, message: '请选择题目来源', trigger: 'change' }
  ]
}

// 更新文件列表
const handleFileListUpdate = (list: UploadUserFile[]) => {
  fileList.value = list
}

// 文件上传变化处理
const handleFileChange = (info: any) => {
  if (info && info.url) {
    formData.batchFileUrl = info.url
  } else {
    formData.batchFileUrl = ''
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const params = {
          batchFileUrl: formData.batchFileUrl,
          problemSourceType: String(formData.problemSourceType),
          needInsertUnExistTag: formData.needInsertUnExistTag
        }
        const res = await batchImportProblemProgramApi(params)
        if (res.code === '0000') {
          ElMessage.success('批量导入编程题成功')
          handleSuccess()
        } else {
          ElMessage.error(res.message || '导入失败')
        }
      } catch (error) {
        console.error('批量导入失败:', error)
        ElMessage.error('批量导入失败，请检查文件格式是否正确')
      } finally {
        loading.value = false
      }
    }
  })
}

// 成功后处理
const handleSuccess = () => {
  visible.value = false
  emit('success')
}

const emit = defineEmits(['success'])

// 打开对话框方法
const open = () => {
  visible.value = true
  // 重置表单
  formData.batchFileUrl = ''
  formData.problemSourceType = ProblemSourceType.HOJ.code
  formData.needInsertUnExistTag = false
  fileList.value = []
}

// 暴露方法给父组件
defineExpose({
  open
})
</script>

<style scoped lang="scss">
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.dialog-footer {
  text-align: right;
}
</style>
