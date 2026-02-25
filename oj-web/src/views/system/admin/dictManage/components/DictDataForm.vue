<template>
  <el-dialog v-model="visible" append-to-body :title="`${paramsProps.title}`" :destroy-on-close="true" width="580px" draggable>
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="字典名称" prop="codeName">
        <el-input v-model="paramsProps.row.codeName" placeholder="请填写字典名称" clearable />
      </el-form-item>
      <el-form-item label="字典别名" prop="alias">
        <template #label>
          <span>
            <el-tooltip>
              <template #content>
                编号别名。编号是系统自动生成的标识Key。<br />一些特殊场景下可能需要使用自定义的字典key信息，可以通过别名来设置。<br />
                例如：编号(key)1003001，别名(key)：normal，代表属性(value)：正常。
              </template>
              <i class="el-icon-question">
                <el-icon>
                  <QuestionFilled />
                </el-icon>
              </i>
            </el-tooltip>
            <span>字典别名 :</span>
          </span>
        </template>
        <el-input v-model="paramsProps.row.alias" placeholder="请填写字典别名" clearable />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="paramsProps.row.sort" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="回显样式" prop="isFull">
        <el-radio-group v-model="paramsProps.row.callbackShowStyle" value="1">
          <el-radio :value="item.value" v-for="item in tagsTypeOptions" :key="item.label">
            <el-tag :type="item.value">
              {{ item.label }}
            </el-tag>
          </el-radio>
        </el-radio-group>
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
import { tagsTypeOptions } from '@/config/consts';
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { QuestionFilled } from '@element-plus/icons-vue';
import { IS_PREVIEW } from '@/config';
defineOptions({
  name: 'DictDataForm'
});

const rules = reactive({
  codeName: [{ required: true, message: '请填写字典名称' }],
  sort: [{ required: true, message: '请填写排序' }]
});

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
};

// 提交数据（新增/编辑）
const ruleFormRef = ref();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid: boolean) => {
    if (IS_PREVIEW) {
      return ElMessage.warning({ message: '预览环境，禁止修改字典请谅解！' });
    }
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

<style scoped lang="scss">
/* 样式用于调整提示信息的位置和样式 */
.el-form-item__label span {
  display: flex;
  align-items: center;
}

.el-icon-question {
  line-height: 0;
  margin-left: 4px; /* 根据需要调整问号图标与文本的间距 */
  cursor: pointer;
}
</style>
