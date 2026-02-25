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
      <el-form-item label="用户id" prop="userId">
        <el-input-number v-model="paramsProps.row.userId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写姓名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="paramsProps.row.username"
          placeholder="请填写用户名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="代码" prop="code">
        <el-input
          v-model="paramsProps.row.code"
          placeholder="请填写代码"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="代码模式" prop="codeMode">
        <el-input
          v-model="paramsProps.row.codeMode"
          placeholder="请填写代码模式"
          clearable
        ></el-input>
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

defineOptions({
    name: 'CodeRecordForm'
})

const rules = reactive({
  userId: [{ required: true, message: '请填写用户id' }],
  name: [{ required: true, message: '请填写姓名' }],
  username: [{ required: true, message: '请填写用户名' }],
  code: [{ required: true, message: '请填写代码' }],
  codeMode: [{ required: true, message: '请填写代码模式' }],
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