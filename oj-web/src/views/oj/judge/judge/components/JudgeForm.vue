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
      <el-form-item label="测评Key" prop="judgeKey">
        <el-input
          v-model="paramsProps.row.judgeKey"
          placeholder="请填写测评Key"
          clearable
        ></el-input>
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
      <el-form-item label="错误提醒(编译错误|或者vj提醒)" prop="errorMessage">
        <el-input
          v-model="paramsProps.row.errorMessage"
          placeholder="请填写错误提醒(编译错误|或者vj提醒)"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="测评用户id" prop="userId">
        <UserSelect
          v-model="paramsProps.row.userId"
          placeholder="请选择测评用户"
          clearable
          @change="(val, user) => handleUserChange(val, user)"
        />
      </el-form-item>
      <el-form-item label="测评类型" prop="sceneType">
        <EnumSelect
          v-model="paramsProps.row.sceneType"
          :enumData="JudgeSceneType"
          placeholder="请选择测评类型"
          type="select"
          clearable
        />
      </el-form-item>
      <el-form-item label="关联业务" prop="businessId">
        <GroupSelect
          v-if="paramsProps.row.sceneType === 'Group'"
          v-model="paramsProps.row.businessId"
          placeholder="请选择班级"
          clearable
        />
        <el-input-number
          v-else
          v-model="paramsProps.row.businessId"
          :precision="0"
          :min="1"
          :max="999999"
          placeholder="请输入业务ID"
        />
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="paramsProps.row.username"
          placeholder="请填写用户名"
          clearable
          disabled
        ></el-input>
      </el-form-item>
      <el-form-item label="题目类型" prop="problemType">
        <EnumSelect
          v-model="paramsProps.row.problemType"
          :enumData="ProblemType"
          placeholder="请选择题目类型"
          type="select"
          clearable
        />
      </el-form-item>
      <el-form-item label="提交测评的时间" prop="submitTime">
        <el-date-picker clearable
          v-model="paramsProps.row.submitTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择提交测评的时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="开始测评的时间" prop="startTime">
        <el-date-picker clearable
          v-model="paramsProps.row.startTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择开始测评的时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束测评的时间" prop="endTime">
        <el-date-picker clearable
          v-model="paramsProps.row.endTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择结束测评的时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="评测状态" prop="status">
        <EnumSelect
          v-model="paramsProps.row.status"
          :enumData="JudgeStatus"
          placeholder="请选择评测状态"
          type="select"
          clearable
        />
      </el-form-item>
      <el-form-item label="运行时间(ms)" prop="time">
        <el-input-number v-model="paramsProps.row.time" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="运行内存(kb)" prop="memory">
        <el-input-number v-model="paramsProps.row.memory" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="IO判题则不为空" prop="score">
        <el-input-number v-model="paramsProps.row.score" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="代码长度" prop="length">
        <el-input-number v-model="paramsProps.row.length" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="流程图" prop="flowImage">
        <el-input
          v-model="paramsProps.row.flowImage"
          placeholder="请填写流程图"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="代码" prop="code">
        <el-input
          v-model="paramsProps.row.code"
          placeholder="请填写代码"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="客观题作答内容" prop="replyOptions">
        <el-input
          v-model="paramsProps.row.replyOptions"
          placeholder="请填写客观题作答内容"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="代码语言" prop="language">
        <el-input
          v-model="paramsProps.row.language"
          placeholder="请填写代码语言"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="是否为人工评测" prop="manualEvaluation">
        <el-switch
          v-model="paramsProps.row.manualEvaluation"
          active-text="人工评测"
          inactive-text="自动评测"
        ></el-switch>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { useOptionsStore } from '@/stores/modules/options'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import UserSelect from '@/components/Common/User/UserSelect.vue'
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue'
import { JudgeStatus, JudgeSceneType } from '@/enums/oj/judge'
import { ProblemType } from '@/enums/oj/problem'

defineOptions({
    name: 'JudgeForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  judgeKey: [{ required: true, message: '请填写测评Key' }],
  problemId: [{ required: true, message: '请填写题目ID' }],
  problemKey: [{ required: true, message: '请填写题目Key' }],
  userId: [{ required: true, message: '请填写测评用户id' }],
  businessId: [{ required: true, message: '请填写关联业务表ID' }],
  username: [{ required: true, message: '请填写用户名' }],
  sceneType: [{ required: true, message: '请填写测评类型' }],
  problemType: [{ required: true, message: '请填写题目类型' }],
  submitTime: [{ required: true, message: '请填写提交测评的时间' }],
  startTime: [{ required: true, message: '请填写开始测评的时间' }],
  endTime: [{ required: true, message: '请填写结束测评的时间' }],
  flowImage: [{ required: true, message: '请填写流程图' }],
  code: [{ required: true, message: '请填写代码' }],
  replyOptions: [{ required: true, message: '请填写客观题作答内容' }],
  version: [{ required: true, message: '请填写乐观锁' }],
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

// 监控测评类型变化
watch(
  () => paramsProps.value.row.sceneType,
  (newVal) => {
    // 当测评类型从班级变为其他类型时，清空业务ID
    if (newVal !== 'Group') {
      paramsProps.value.row.businessId = undefined;
    }
  }
)

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

// 处理用户选择变更
const handleUserChange = (val: number, user: any) => {
  if (user) {
    paramsProps.value.row.username = user.username || user.name;
    paramsProps.value.row.name = user.name;
  } else {
    paramsProps.value.row.username = undefined;
    paramsProps.value.row.name = undefined;
  }
}

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss"></style>
