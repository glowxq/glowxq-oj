<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="580px" draggable append-to-body>
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="业务类型" prop="type">
        <el-select v-model="paramsProps.row.type" :disabled="!isAdd" clearable placeholder="请选择业务字典类型">
          <el-option v-for="item in dictBusinessType" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="字典名称" prop="typeName">
        <el-input v-model="paramsProps.row.typeName" placeholder="请填写字典名称" clearable />
      </el-form-item>
      <el-form-item label="字典类型" prop="typeCode">
        <el-input v-model="paramsProps.row.typeCode" placeholder="请填写字典类型" clearable />
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
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { dictBusinessType } from '@/config/consts';

defineOptions({
  name: 'DictTypeForm'
});

const rules = reactive({
  type: [{ required: true, message: '请选择业务字典类型' }],
  typeCode: [{ required: true, message: '请填写字典类型' }],
  typeName: [{ required: true, message: '请填写字典名称' }]
});
const isAdd = ref(true);
const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params;
  visible.value = true;
  if (params?.isAdd) isAdd.value = params?.isAdd;
};

// 提交数据（新增/编辑）
const ruleFormRef = ref();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid: boolean) => {
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

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss"></style>
