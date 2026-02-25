<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="580px" draggable>
    <el-form
      ref="ruleFormRef"
      label-width="140px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="模板文件" prop="url">
        <UploadFiles
          v-model:file-list="fileList"
          multiple
          :file-size="3"
          width="300px"
          height="200px"
          @change="fileChange"
          :accept="'.xlsx,.xls,.doc,.docx'"
        >
          <template #tip> 文件大小不能超过 3M </template>
        </UploadFiles>
      </el-form-item>
      <el-form-item label="模版名" prop="tempName">
        <el-input v-model="paramsProps.row.tempName" placeholder="请填写模版名" clearable />
      </el-form-item>
      <el-form-item label="地址" prop="url">
        <el-input v-model="paramsProps.row.url" placeholder="请填写地址" clearable disabled />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="paramsProps.row.remark" placeholder="请填写备注" :rows="2" type="textarea" clearable />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消 </el-button>
      <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { type ElForm, ElMessage, type UploadUserFile } from 'element-plus';
import UploadFiles from '@/components/Common/Upload/file.vue';
import type { IUploadResult } from '@/api/interface/system/admin/upload';

defineOptions({
  name: 'SysTempFileForm'
});

const rules = reactive({});

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

const fileList = ref<UploadUserFile[]>();

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
  visible.value = true;
  fileList.value = params.row.url
    ? [
        {
          name: getFileNameFromUrl(params.row.url),
          url: params.row.url
        }
      ]
    : [];
};

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    try {
      await paramsProps.value.api!(paramsProps.value.row);
      ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      paramsProps.value.getTableList!();
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
};

const fileChange = (file: IUploadResult) => {
  if (paramsProps.value.isAdd) paramsProps.value.row.tempName = file?.filename; // 如果是新增，便利性带入文件名
  paramsProps.value.row.url = file?.url;
  paramsProps.value.row.sysFileId = file?.fileId;
};
const getFileNameFromUrl = (url: string) => {
  // 使用正则表达式提取文件名
  const regex = /\/([^/]+)$/;
  const match = url.match(regex);
  return match ? match[1] : '';
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss"></style>
