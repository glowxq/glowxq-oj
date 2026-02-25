<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="580px"
    draggable
  >
    <el-form
      ref="ruleFormRef"
      label-width="140px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="用户ID" prop="userId">
        <el-input-number v-model="paramsProps.row.userId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="题目ID" prop="problemId">
        <el-input-number v-model="paramsProps.row.problemId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="题目Key" prop="problemKey">
        <el-input
          v-model="paramsProps.row.problemKey"
          placeholder="请填写题目Key"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="题目标题ID" prop="problemTitle">
        <el-input
          v-model="paramsProps.row.problemTitle"
          placeholder="请填写题目标题ID"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="测评ID" prop="judgeId">
        <el-input-number v-model="paramsProps.row.judgeId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="测评状态" prop="judgeStatus">
        <el-select v-model="paramsProps.row.judgeStatus" clearable placeholder="请选择测评状态">
          <el-option
            v-for="item in optionsStore.getDictOptions('')"
            :key="item.id"
            :label="item.codeName"
            :value="Number(item.id)"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="作业分数" prop="score">
        <el-input-number v-model="paramsProps.row.score" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="AC的代码" prop="code">
        <el-input
          v-model="paramsProps.row.code"
          placeholder="请填写AC的代码"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="非编程题作答内容" prop="options">
        <el-input
          v-model="paramsProps.row.options"
          placeholder="请填写非编程题作答内容"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="流程图URL" prop="flowImage">
        <el-input
          v-model="paramsProps.row.flowImage"
          placeholder="请填写流程图URL"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="题目类型" prop="problemType">
        <el-select v-model="paramsProps.row.problemType" clearable placeholder="请选择题目类型">
          <el-option
            v-for="item in optionsStore.getDictOptions('')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { useOptionsStore } from '@/stores/modules/options'

defineOptions({
    name: 'UserProblemForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  userId: [{ required: true, message: '请填写用户ID' }],
  problemId: [{ required: true, message: '请填写题目ID' }],
  problemKey: [{ required: true, message: '请填写题目Key' }],
  problemTitle: [{ required: true, message: '请填写题目标题ID' }],
  judgeId: [{ required: true, message: '请填写测评ID' }],
  judgeStatus: [{ required: true, message: '请填写测评状态' }],
  score: [{ required: true, message: '请填写作业分数' }],
  code: [{ required: true, message: '请填写AC的代码' }],
  options: [{ required: true, message: '请填写非编程题作答内容' }],
  flowImage: [{ required: true, message: '请填写流程图URL' }],
  problemType: [{ required: true, message: '请填写题目类型' }],
  createTime: [{ required: true, message: '请填写创建时间' }],
  updateTime: [{ required: true, message: '请填写更新时间' }],
  delFlag: [{ required: true, message: '请填写是否删除' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  visible.value = true
}

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      await paramsProps.value.api!(paramsProps.value.row)
      ElMessage.success({ message: `${paramsProps.value.title}成功！` })
      paramsProps.value.getTableList!()
      visible.value = false
    } catch (error) {
      console.log(error)
    }
  })
}

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss"></style>