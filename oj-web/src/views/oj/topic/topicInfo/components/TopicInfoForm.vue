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
      <el-form-item label="主题ID" prop="topicId">
        <el-input-number v-model="paramsProps.row.topicId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input-number v-model="paramsProps.row.userId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="头像" prop="avatar">
        <el-input
          v-model="paramsProps.row.avatar"
          placeholder="请填写头像"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写姓名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input
          v-model="paramsProps.row.nickName"
          placeholder="请填写昵称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="总得分" prop="score">
        <el-input-number v-model="paramsProps.row.score" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="总AC数量" prop="acNum">
        <el-input-number v-model="paramsProps.row.acNum" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="提交次数" prop="submitNum">
        <el-input-number v-model="paramsProps.row.submitNum" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="总做题用时(分)" prop="useTime">
        <el-input-number v-model="paramsProps.row.useTime" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="总罚时(分)" prop="punishmentTime">
        <el-input-number v-model="paramsProps.row.punishmentTime" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="开始做题" prop="status">
        <el-select v-model="paramsProps.row.status" clearable placeholder="请选择开始做题">
          <el-option
            v-for="item in optionsStore.getDictOptions('')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker clearable
          v-model="paramsProps.row.startTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间" prop="endTime">
        <el-date-picker clearable
          v-model="paramsProps.row.endTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="自动提交" prop="autoSubmit">
        <el-switch v-model="paramsProps.row.autoSubmit" ></el-switch>
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
    name: 'TopicInfoForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  topicId: [{ required: true, message: '请填写主题ID' }],
  userId: [{ required: true, message: '请填写用户ID' }],
  avatar: [{ required: true, message: '请填写头像' }],
  name: [{ required: true, message: '请填写姓名' }],
  nickName: [{ required: true, message: '请填写昵称' }],
  score: [{ required: true, message: '请填写总得分' }],
  acNum: [{ required: true, message: '请填写总AC数量' }],
  submitNum: [{ required: true, message: '请填写提交次数' }],
  useTime: [{ required: true, message: '请填写总做题用时(分)' }],
  punishmentTime: [{ required: true, message: '请填写总罚时(分)' }],
  status: [{ required: true, message: '请填写开始做题' }],
  startTime: [{ required: true, message: '请填写开始时间' }],
  endTime: [{ required: true, message: '请填写完成时间' }],
  autoSubmit: [{ required: true, message: '请填写自动提交' }],
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