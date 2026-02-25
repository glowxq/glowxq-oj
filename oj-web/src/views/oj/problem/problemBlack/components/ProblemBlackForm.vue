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
      <el-form-item label="题目ID" prop="problemId">
        <el-input-number v-model="paramsProps.row.problemId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="拉黑对象ID" prop="businessId">
        <el-input-number v-model="paramsProps.row.businessId" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="拉黑对象名" prop="businessName">
        <el-input
          v-model="paramsProps.row.businessName"
          placeholder="请填写拉黑对象名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="拉黑对象类型" prop="type">
        <el-select v-model="paramsProps.row.type" clearable placeholder="请选择拉黑对象类型">
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
    name: 'ProblemBlackForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  problemId: [{ required: true, message: '请填写题目ID' }],
  businessId: [{ required: true, message: '请填写拉黑对象ID' }],
  businessName: [{ required: true, message: '请填写拉黑对象名' }],
  type: [{ required: true, message: '请填写拉黑对象类型' }],
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