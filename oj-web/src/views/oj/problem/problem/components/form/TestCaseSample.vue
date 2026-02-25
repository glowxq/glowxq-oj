<template>
  <div class="sample-item">
    <div class="sample-header">
      <span>测试用例 #{{ index + 1 }}</span>
      <el-button v-if="!readonly" type="danger" size="small" @click="removeSample">删除</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item :label="'输入'" :prop="`samples[${index}].input`">
          <el-input
            type="textarea"
            v-model="sample.input"
            placeholder="请填写测试用例输入"
            :autosize="{ minRows: 2, maxRows: 8 }"
            :disabled="readonly"
          ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item :label="'输出'" :prop="`samples[${index}].output`">
          <el-input
            type="textarea"
            v-model="sample.output"
            placeholder="请填写测试用例输出"
            :autosize="{ minRows: 2, maxRows: 8 }"
            :disabled="readonly"
          ></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :xs="12" :sm="8">
        <el-form-item :label="'评分'" :prop="`samples[${index}].score`">
          <el-input-number
            v-model="sample.score"
            :min="0"
            :precision="0"
            :controls-position="'right'"
            style="width: 100%;"
          ></el-input-number>
        </el-form-item>
      </el-col>
      <el-col :xs="12" :sm="8">
        <el-form-item :label="'分组号'" :prop="`samples[${index}].groupNum`">
          <el-input-number
            v-model="sample.groupNum"
            :min="0"
            :precision="0"
            :controls-position="'right'"
            style="width: 100%;"
          ></el-input-number>
        </el-form-item>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-form-item :label="'类型'" :prop="`samples[${index}].type`" style="display: none;">
          <enum-select
            v-model="sample.type"
            :enum-data="TestCaseType"
            type="select"
            placeholder="请选择类型"
          />
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import { TestCaseType } from '@/enums/oj/problem'
import type { ProblemCaseBoData } from '../types'

defineOptions({
  name: 'TestCaseSample'
})

const props = defineProps<{
  sample: ProblemCaseBoData
  index: number
  readonly?: boolean
}>()

const emit = defineEmits(['remove'])

const removeSample = () => {
  emit('remove', props.index)
}
</script>

<style scoped lang="scss">
.sample-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 16px;
  background-color: #fafafa;
}

.sample-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: bold;
}

@media screen and (max-width: 768px) {
  .sample-item {
    padding: 12px;
  }

  :deep(.el-form-item__label) {
    padding-bottom: 4px;
  }

  :deep(.el-form-item) {
    margin-bottom: 12px;
  }
}
</style>
