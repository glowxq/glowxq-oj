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
      <el-form-item label="语言类型" prop="contentType">
        <el-select v-model="paramsProps.row.contentType" clearable placeholder="请选择语言类型">
          <el-option
            v-for="item in optionsStore.getDictOptions('')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="语言描述" prop="description">
        <el-input
          v-model="paramsProps.row.description"
          placeholder="请填写语言描述"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="语言名字" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写语言名字"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="编译指令" prop="compileCommand">
        <el-input
          v-model="paramsProps.row.compileCommand"
          placeholder="请填写编译指令"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="模板" prop="template">
        <el-input
          v-model="paramsProps.row.template"
          placeholder="请填写模板"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="语言默认代码模板" prop="codeTemplate">
        <el-input
          v-model="paramsProps.row.codeTemplate"
          placeholder="请填写语言默认代码模板"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="是否SPJ" prop="spjEnable">
        <el-switch v-model="paramsProps.row.spjEnable"  :active-value="1" :inactive-value="0"/>
      </el-form-item>
      <el-form-item label="OJ类型" prop="oj">
        <EnumSelect v-model="paramsProps.row.oj" :enum-data="ProblemSourceType" :clearable="true" :placeholder="'请选择OJ类型'"/>
      </el-form-item>
      <el-form-item label="语言排序" prop="seq">
        <el-input-number v-model="paramsProps.row.seq" :precision="0" :min="1" :max="999999"/>
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
import EnumSelect from "@/components/Common/Enum/EnumSelect.vue";
import {ProblemSourceType} from "@/enums/oj/problem";

defineOptions({
    name: 'MetaLanguageForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
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
