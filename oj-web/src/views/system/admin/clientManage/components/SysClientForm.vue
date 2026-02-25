<template>
  <el-dialog v-model="visible" :title="`${paramsProps.title}`" :destroy-on-close="true" width="500px" draggable append-to-body>
    <el-form
      ref="ruleFormRef"
      label-width="180px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="ClientId" prop="clientId" v-if="!paramsProps.isAdd">
        <template #label>
          <el-space :size="4">
            <span>clientId</span>
            <el-tooltip
              effect="dark"
              content="唯一标识串，用于token的生成。针对不同端生成不同类型的token。多端尽量不要使用相同token"
              placement="top"
            >
              <i :class="'iconfont icon-yiwen'" />
            </el-tooltip>
          </el-space>
          <span>&nbsp;:</span>
        </template>
        <el-input disabled v-model="paramsProps.row.clientId" clearable />
      </el-form-item>
      <el-form-item label="客户端名称" prop="clientKey">
        <el-input :disabled="!paramsProps.isAdd" v-model="paramsProps.row.clientKey" placeholder="请填写客户端名称" clearable />
      </el-form-item>
      <el-form-item label="secret秘钥" prop="clientSecret" v-if="!paramsProps.isAdd">
        <template #label>
          <el-space :size="4">
            <span>secret秘钥</span>
            <el-tooltip effect="dark" content="加密串，后续实现" placement="top">
              <i :class="'iconfont icon-yiwen'" />
            </el-tooltip>
          </el-space>
          <span>&nbsp;:</span>
        </template>
        <el-input disabled v-model="paramsProps.row.clientSecret" clearable />
      </el-form-item>
      <el-form-item label="授权类型" prop="grantTypeCd">
        <template #label>
          <el-space :size="4">
            <span>授权类型</span>
            <el-tooltip effect="dark" content="选择认证策略strategy，如PasswordStrategy。多选支持后续完善" placement="top">
              <i :class="'iconfont icon-yiwen'" />
            </el-tooltip>
          </el-space>
          <span>&nbsp;:</span>
        </template>
        <el-select v-model="paramsProps.row.grantTypeCdList" multiple clearable placeholder="请选择授权类型">
          <el-option
            v-for="item in optionsStore.getDictOptions('grant_type')"
            :key="item.alias"
            :label="item.codeName"
            :value="item.alias"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型" prop="deviceTypeCd">
        <el-select v-model="paramsProps.row.deviceTypeCd" clearable placeholder="请选择设备类型">
          <el-option
            v-for="item in optionsStore.getDictOptions('device_type')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="token活跃超时时间" prop="activeTimeout">
        <template #label>
          <el-space :size="4">
            <span>token活跃超时时间</span>
            <el-tooltip
              effect="dark"
              content="指定此次登录 token 最低活跃频率，单位：秒。如果用户在活跃时间内未作任何操作将踢出登录。对应sa-token activeTimeout。"
              placement="top"
            >
              <i :class="'iconfont icon-yiwen'" />
            </el-tooltip>
          </el-space>
          <span>&nbsp;:</span>
        </template>
        <el-input-number v-model="paramsProps.row.activeTimeout" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="token固定超时" prop="timeout">
        <template #label>
          <el-space :size="4">
            <span>token固定超时</span>
            <el-tooltip
              effect="dark"
              content="指定此次登录 token 有效期，单位：秒。token的最大有效期。对应sa-token timeout"
              placement="top"
            >
              <i :class="'iconfont icon-yiwen'" />
            </el-tooltip>
          </el-space>
          <span>&nbsp;:</span>
        </template>
        <el-input-number v-model="paramsProps.row.timeout" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="状态" prop="clientStatusCd">
        <el-select v-model="paramsProps.row.clientStatusCd" clearable placeholder="请选择状态">
          <el-option
            v-for="item in optionsStore.getDictOptions('sys_client_status')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
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
import { useOptionsStore } from '@/stores/modules/options';
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

defineOptions({
  name: 'SysClientForm'
});

const optionsStore = useOptionsStore();

const rules = reactive({
  clientKey: [{ required: true, message: '请填写客户端名称' }],
  deviceTypeCd: [{ required: true, message: '请填写设备类型' }],
  clientStatusCd: [{ required: true, message: '请设置状态' }]
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
