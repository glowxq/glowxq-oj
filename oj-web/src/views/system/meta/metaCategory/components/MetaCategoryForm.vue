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
      <el-form-item label="上级分类" prop="pid">
        <el-tree-select
          v-model="paramsProps.row.pid"
          :data="parentCategories"
          check-strictly
          placeholder="请选择上级分类"
          :render-after-expand="false"
          clearable
          :default-expand-all="true"
          :props="treeProps"
        />
      </el-form-item>
      <el-form-item label="分类名称" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写分类名称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="paramsProps.row.sort" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="启用" prop="enable">
        <el-switch v-model="paramsProps.row.enable" ></el-switch>
      </el-form-item>
      <el-form-item label="是否锁定" prop="lock">
        <el-switch v-model="paramsProps.row.lock" ></el-switch>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="paramsProps.row.remark"
          placeholder="请填写备注"
          clearable
        ></el-input>
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
import { getMetaCategoryTreeApi } from '@/api/modules/system/meta/metaCategory';
import type { IMetaCategory } from '@/api/interface/system/meta/metaCategory';

defineOptions({
    name: 'MetaCategoryForm'
})

const parentCategories = ref<IMetaCategory.Tree[]>([]);
const treeProps = {
  label: 'name',
  value: 'id'
};

const rules = reactive({
  name: [{ required: true, message: '请填写分类名称' }],
  pid: [{ required: true, message: '请选择上级分类' }],
  enable: [{ required: true, message: '请选择是否启用' }],
  lock: [{ required: true, message: '请选择是否锁定' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 加载分类树
const loadCategoryTree = () => {
  getMetaCategoryTreeApi({ excludeNodeId: paramsProps.value.row?.id }).then(res => {
    parentCategories.value = res.data;
  });
};

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  visible.value = true
  // 如果是新增，默认启用
  if (paramsProps.value.title.includes('新增')) {
    paramsProps.value.row.enable = true;
    paramsProps.value.row.lock = false;
  }
  loadCategoryTree();
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