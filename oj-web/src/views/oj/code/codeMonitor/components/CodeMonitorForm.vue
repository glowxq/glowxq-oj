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
      <el-form-item label="被用户id" prop="monitorUserId">
        <el-input-number v-model="paramsProps.row.monitorUserId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="覆盖用户id" prop="overlayUserId">
        <el-input-number v-model="paramsProps.row.overlayUserId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="被监控人电话" prop="monitorPhone">
        <el-input
          v-model="paramsProps.row.monitorPhone"
          placeholder="请填写被监控人电话"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="覆盖人电话" prop="overlayPhone">
        <el-input
          v-model="paramsProps.row.overlayPhone"
          placeholder="请填写覆盖人电话"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="被监控人" prop="monitorName">
        <el-input
          v-model="paramsProps.row.monitorName"
          placeholder="请填写被监控人"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="覆盖人" prop="overlayName">
        <el-input
          v-model="paramsProps.row.overlayName"
          placeholder="请填写覆盖人"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="被监控代码" prop="monitorCode">
        <el-input
          v-model="paramsProps.row.monitorCode"
          placeholder="请填写被监控代码"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="覆盖代码" prop="overlayCode">
        <el-input
          v-model="paramsProps.row.overlayCode"
          placeholder="请填写覆盖代码"
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
      <el-form-item label="监控状态" prop="monitorStatus">
        <el-select v-model="paramsProps.row.monitorStatus" clearable placeholder="请选择监控状态">
          <el-option
            v-for="item in optionsStore.getDictOptions('')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="版本" prop="version">
        <el-input-number v-model="paramsProps.row.version" :precision="0" :min="1" :max="999999" />
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
    name: 'CodeMonitorForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  monitorUserId: [{ required: true, message: '请填写被用户id' }],
  overlayUserId: [{ required: true, message: '请填写覆盖用户id' }],
  monitorPhone: [{ required: true, message: '请填写被监控人电话' }],
  overlayPhone: [{ required: true, message: '请填写覆盖人电话' }],
  monitorName: [{ required: true, message: '请填写被监控人' }],
  overlayName: [{ required: true, message: '请填写覆盖人' }],
  codeMode: [{ required: true, message: '请填写代码模式' }],
  monitorStatus: [{ required: true, message: '请填写监控状态' }],
  version: [{ required: true, message: '请填写版本' }],
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