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
      <el-form-item label="链路追踪ID" prop="traceId">
        <el-input
          v-model="paramsProps.row.traceId"
          placeholder="请填写链路追踪ID"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="业务模块跟踪ID" prop="spanId">
        <el-input
          v-model="paramsProps.row.spanId"
          placeholder="请填写业务模块跟踪ID"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="用户名称" prop="username">
        <el-input
          v-model="paramsProps.row.username"
          placeholder="请填写用户名称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="ip" prop="ip">
        <el-input
          v-model="paramsProps.row.ip"
          placeholder="请填写ip"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="方法" prop="method">
        <el-input
          v-model="paramsProps.row.method"
          placeholder="请填写方法"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="接口" prop="uri">
        <el-input
          v-model="paramsProps.row.uri"
          placeholder="请填写接口"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="请求头" prop="header">
        <el-input
          v-model="paramsProps.row.header"
          placeholder="请填写请求头"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="模块名称" prop="module">
        <el-input
          v-model="paramsProps.row.module"
          placeholder="请填写模块名称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="操作描述" prop="description">
        <el-input
          v-model="paramsProps.row.description"
          placeholder="请填写操作描述"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="参数" prop="param">
        <el-input
          v-model="paramsProps.row.param"
          placeholder="请填写参数"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="请求参数json" prop="request">
        <el-input
          v-model="paramsProps.row.request"
          placeholder="请填写请求参数json"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="响应参数json" prop="response">
        <el-input
          v-model="paramsProps.row.response"
          placeholder="请填写响应参数json"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="存在错误" prop="error">
        <el-switch v-model="paramsProps.row.error" ></el-switch>
      </el-form-item>
      <el-form-item label="操作类型" prop="businessType">
        <el-select v-model="paramsProps.row.businessType" clearable placeholder="请选择操作类型">
          <el-option
            v-for="item in optionsStore.getDictOptions('')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="错误信息" prop="errorMessage">
        <el-input
          v-model="paramsProps.row.errorMessage"
          placeholder="请填写错误信息"
          :rows="2"
          type="textarea"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="操作耗时（ms）" prop="costTime">
        <el-input-number v-model="paramsProps.row.costTime" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="租户id" prop="tenantId">
        <el-input
          v-model="paramsProps.row.tenantId"
          placeholder="请填写租户id"
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
import { useOptionsStore } from '@/stores/modules/options'

defineOptions({
    name: 'SysOperationLogForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  userId: [{ required: true, message: '请填写用户id' }],
  traceId: [{ required: true, message: '请填写链路追踪ID' }],
  spanId: [{ required: true, message: '请填写业务模块跟踪ID' }],
  username: [{ required: true, message: '请填写用户名称' }],
  ip: [{ required: true, message: '请填写ip' }],
  method: [{ required: true, message: '请填写方法' }],
  uri: [{ required: true, message: '请填写接口' }],
  module: [{ required: true, message: '请填写模块名称' }],
  description: [{ required: true, message: '请填写操作描述' }],
  param: [{ required: true, message: '请填写参数' }],
  request: [{ required: true, message: '请填写请求参数json' }],
  response: [{ required: true, message: '请填写响应参数json' }],
  error: [{ required: true, message: '请填写存在错误' }],
  businessType: [{ required: true, message: '请填写操作类型' }],
  errorMessage: [{ required: true, message: '请填写错误信息' }],
  costTime: [{ required: true, message: '请填写操作耗时（ms）' }],
  createTime: [{ required: true, message: '请填写创建时间' }],
  updateTime: [{ required: true, message: '请填写更新时间' }],
  delFlag: [{ required: true, message: '请填写是否删除' }],
  tenantId: [{ required: true, message: '请填写租户id' }],
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