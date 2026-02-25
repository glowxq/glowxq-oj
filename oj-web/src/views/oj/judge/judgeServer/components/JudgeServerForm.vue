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
      <el-form-item label="判题服务名字" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写判题服务名字"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="判题机ip" prop="ip">
        <el-input
          v-model="paramsProps.row.ip"
          placeholder="请填写判题机ip"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="判题机端口号" prop="port">
        <el-input-number
          v-model="paramsProps.row.port" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="ip:port" prop="url">
        <el-input
          v-model="paramsProps.row.url"
          placeholder="请填写ip:port"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="判题机所在服务器cpu核心数" prop="cpuCore">
        <el-input-number
          v-model="paramsProps.row.cpuCore" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="判题机所在服务器空闲内存" prop="freeMemory">
        <el-input-number
          v-model="paramsProps.row.freeMemory" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="当前判题数" prop="taskNumber">
        <el-input-number
          v-model="paramsProps.row.taskNumber" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="判题并发最大数" prop="maxTaskNumber">
        <el-input-number
          v-model="paramsProps.row.maxTaskNumber" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="0禁用|1启用" prop="enable">
        <el-input-number
          v-model="paramsProps.row.enable" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="是否开启远程判题vj" prop="remoteEnable">
        <el-input-number
          v-model="paramsProps.row.remoteEnable" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="是否可提交CF" prop="cfSubmittableEnable">
        <el-input-number
          v-model="paramsProps.row.cfSubmittableEnable" :precision="0" :min="1" :max="999999" />
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
    name: 'JudgeServerForm'
})

const rules = reactive({
  ip: [{ required: true, message: '请填写判题机ip' }],
  port: [{ required: true, message: '请填写判题机端口号' }],
  taskNumber: [{ required: true, message: '请填写当前判题数' }],
  maxTaskNumber: [{ required: true, message: '请填写判题并发最大数' }],
  createTime: [{ required: true, message: '请填写创建时间' }],
  updateTime: [{ required: true, message: '请填写更新时间' }],
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