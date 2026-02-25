<template>
  <!-- 评测设置表单 -->
  <div class="judge-settings-form">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="评测模式" prop="problemBo.judgeMode">
          <div class="judge-mode-wrapper">
            <enum-select
              v-model="modelValue.problemBo.judgeMode"
              :enum-data="JudgeMode"
              type="select"
              :disabled="isEdit"
              placeholder="请选择评测模式"
            />
            <span v-if="isEdit" class="edit-disabled-text">
              <el-icon class="edit-disabled-icon"><InfoFilled /></el-icon>
              编辑模式下不允许修改评测模式
            </span>
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="样例评测模式" prop="problemBo.judgeCaseMode">
          <enum-select
            v-model="modelValue.problemBo.judgeCaseMode"
            :enum-data="JudgeCaseMode"
            type="select"
            placeholder="请选择样例评测模式"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item
          v-if="modelValue.problemBo.judgeMode === 'SPJ' || modelValue.problemBo.judgeMode === 'INTERACTIVE'"
          label="特判程序代码"
          prop="problemBo.spjCode"
        >
          <el-input
            type="textarea"
            v-model="modelValue.problemBo.spjCode"
            placeholder="请填写特判程序或交互程序代码"
            :rows="6"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item
          v-if="modelValue.problemBo.judgeMode === 'SPJ' || modelValue.problemBo.judgeMode === 'INTERACTIVE'"
          label="特判程序语言"
          prop="problemBo.spjLanguage"
        >
          <el-input
            v-model="modelValue.problemBo.spjLanguage"
            placeholder="请填写特判程序语言"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="用户程序额外文件" prop="problemBo.userExtraFile">
          <el-input
            type="textarea"
            v-model="modelValue.problemBo.userExtraFile"
            placeholder="请填写JSON格式，key:name, value:content"
            :rows="4"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="评测程序额外文件" prop="problemBo.judgeExtraFile">
          <el-input
            type="textarea"
            v-model="modelValue.problemBo.judgeExtraFile"
            placeholder="请填写JSON格式，key:name, value:content"
            :rows="4"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20" v-if="modelValue.problemBo.fileIo">
      <el-col :span="12">
        <el-form-item label="输入文件名" prop="problemBo.ioReadFileName">
          <el-input
            v-model="modelValue.problemBo.ioReadFileName"
            placeholder="请填写输入文件名"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="输出文件名" prop="problemBo.ioWriteFileName">
          <el-input
            v-model="modelValue.problemBo.ioWriteFileName"
            placeholder="请填写输出文件名"
            clearable
          ></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-divider content-position="left">选项设置</el-divider>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-form-item label="远程VJ判题" prop="problemBo.remote">
          <el-switch v-model="modelValue.problemBo.remote"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="代码分享" prop="problemBo.codeShare">
          <el-switch v-model="modelValue.problemBo.codeShare"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="去除文末空格" prop="problemBo.removeEndBlank">
          <el-switch v-model="modelValue.problemBo.removeEndBlank"></el-switch>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-form-item label="开启样例结果查看" prop="problemBo.openCaseResult">
          <el-switch v-model="modelValue.problemBo.openCaseResult"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="上传文件测试数据" prop="problemBo.uploadCase">
          <el-switch v-model="modelValue.problemBo.uploadCase"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="队伍内题目" prop="problemBo.groupProblem">
          <el-switch v-model="modelValue.problemBo.groupProblem"></el-switch>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-form-item label="文件IO模式" prop="problemBo.fileIo">
          <el-switch v-model="modelValue.problemBo.fileIo"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="必须上传图片" prop="problemBo.requireImage">
          <el-switch v-model="modelValue.problemBo.requireImage"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="允许修改代码模式" prop="problemBo.changeModeCode">
          <el-switch v-model="modelValue.problemBo.changeModeCode"></el-switch>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import { JudgeMode, JudgeCaseMode } from '@/enums/oj/judge'
import type { ProblemData } from '../types'
import { InfoFilled } from '@element-plus/icons-vue'

defineOptions({
  name: 'JudgeSettingsForm'
})

const props = defineProps<{
  modelValue: ProblemData,
  isEdit?: boolean
}>()
</script>

<style scoped lang="scss">
.judge-settings-form {
  padding: 16px;
}

.judge-mode-wrapper {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.edit-disabled-icon {
  margin-right: 4px;
  color: #E6A23C;
  font-size: 16px;
  vertical-align: middle;
}

.edit-disabled-text {
  display: block;
  color: #E6A23C;
  font-size: 14px;
  margin-top: 5px;
  line-height: 1.5;
}

/* 调整表单项布局，使图标可以正确显示 */
:deep(.el-form-item) {
  position: relative;

  .el-form-item__content {
    display: flex;
    flex-direction: column;
  }
}
</style>
