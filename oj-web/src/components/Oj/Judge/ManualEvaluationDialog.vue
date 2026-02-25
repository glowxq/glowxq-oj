<template>
  <el-dialog 
    v-model="visible" 
    destroy-on-close 
    title="人工测评" 
    width="500px"
    @close="handleClose"
  >
    <el-form 
      ref="formRef"
      :model="form" 
      :rules="rules"
      label-position="right" 
      label-width="120px"
    >
      <el-form-item label="测评ID">
        <span>{{ judgeData?.id }}</span>
      </el-form-item>
      <el-form-item label="题目ID">
        <span>{{ judgeData?.problemId }}</span>
      </el-form-item>
      <el-form-item label="题目标题">
        <span>{{ judgeData?.problemTitle || '未知题目' }}</span>
      </el-form-item>
      <el-form-item label="当前状态">
        <judge-status-show :code="Number(judgeData?.status)" />
      </el-form-item>
      <el-form-item label="评测状态" prop="judgeStatus" required>
        <el-select 
          v-model="form.judgeStatus" 
          placeholder="请选择评测状态" 
          style="width: 100%"
        >
          <el-option 
            v-for="(item, key) in JudgeStatus" 
            :key="key" 
            :label="item.text" 
            :value="(item as any).code"
          >
            <judge-status-show :code="(item as any).code" />
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="分数" prop="score" required>
        <el-input-number 
          v-model="form.score" 
          :max="100" 
          :min="0" 
          style="width: 100%" 
          placeholder="请输入分数"
        />
      </el-form-item>

    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { JudgeStatus } from '@/enums/oj/judge'
import JudgeStatusShow from './JudgeStatusShow.vue'
import { manualEvaluationApi } from '@/api/modules/oj/judge/judge'

defineOptions({
  name: 'ManualEvaluationDialog'
})

// Props和Events
interface Props {
  onSuccess?: (data: any) => void
}

const props = withDefaults(defineProps<Props>(), {})

const emit = defineEmits<{
  success: [data: any]
  close: []
}>()

// 组件状态
const visible = ref(false)
const loading = ref(false)
const formRef = ref<FormInstance>()
const judgeData = ref<any>(null)

// 表单数据
const form = reactive({
  id: '',
  judgeStatus: 0,
  score: 100
})

// 表单验证规则
const rules: FormRules = {
  judgeStatus: [
    { required: true, message: '请选择评测状态', trigger: 'change' }
  ],
  score: [
    { required: true, message: '请输入分数', trigger: 'blur' },
    { type: 'number', min: 0, max: 100, message: '分数必须在0-100之间', trigger: 'blur' }
  ]
}

// 打开弹窗
const openDialog = (row: any) => {
  judgeData.value = row
  
  // 重置表单数据
  form.id = row.id
  form.judgeStatus = Number(row.status) || 0
  form.score = row.score || 100
  
  visible.value = true
  
  // 等待DOM更新后清除验证状态
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  judgeData.value = null
  emit('close')
}

// 取消操作
const handleCancel = () => {
  handleClose()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 表单验证
    await formRef.value.validate()
    
    loading.value = true
    
    // 调用API
    await manualEvaluationApi({
      id: form.id,
      judgeStatus: form.judgeStatus,
      score: form.score
    })
    
    ElMessage.success('人工测评提交成功')
    
    // 执行成功回调
    const successData = {
      ...judgeData.value,
      status: form.judgeStatus,
      score: form.score,
      manualEvaluation: true
    }
    
    emit('success', successData)
    props.onSuccess?.(successData)
    
    handleClose()
  } catch (error) {
    console.error('人工测评提交失败', error)
    ElMessage.error('人工测评提交失败')
  } finally {
    loading.value = false
  }
}

// 对外暴露方法
defineExpose({
  openDialog
})
</script>

<style scoped lang="scss">
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-dialog__header) {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  
  .el-dialog__title {
    font-weight: 600;
    color: #303133;
  }
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #ebeef5;
  padding: 15px 20px;
  text-align: right;
}

.el-input-number {
  :deep(.el-input__inner) {
    text-align: left;
  }
}
</style> 