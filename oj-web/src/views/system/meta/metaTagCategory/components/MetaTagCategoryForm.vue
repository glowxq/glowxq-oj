<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="580px"
    draggable
  >
    <div class="form-container">
      <div class="form-header">
        <h3 class="form-title">分类信息</h3>
        <el-button 
          type="primary" 
          link 
          size="small" 
          @click="fillTestData"
          class="test-button"
        >
          <el-icon><MagicStick /></el-icon>
          一键测试
        </el-button>
      </div>
      
      <el-form
        ref="ruleFormRef"
        label-width="140px"
        label-suffix=" :"
        :rules="rules"
        :model="paramsProps.row"
        @submit.enter.prevent="handleSubmit"
        class="tag-category-form"
      >
        <el-form-item label="分类名称" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写分类名称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="启用" prop="enable">
        <el-switch v-model="paramsProps.row.enable" ></el-switch>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="paramsProps.row.remark"
          placeholder="请填写备注"
          clearable
        ></el-input>
      </el-form-item>

      </el-form>
    </div>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'

defineOptions({
    name: 'MetaTagCategoryForm'
})

const rules = reactive({
  name: [{ required: true, message: '请填写分类名称' }],
  enable: [{ required: true, message: '请填写启用' }],
  delFlag: [{ required: true, message: '请填写删除标识' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {
    enable: true
  },
  api: undefined,
  getTableList: undefined
})

// 一键填充测试数据
const fillTestData = () => {
  const testNames = ['技术标签', '业务标签', '状态标签', '优先级标签', '类型标签', '测试分类']
  const testRemarks = ['用于技术相关的标签分类', '业务流程相关标签', '状态管理标签', '优先级管理', '类型分类管理', '测试用分类备注']
  
  const randomName = testNames[Math.floor(Math.random() * testNames.length)]
  const randomRemark = testRemarks[Math.floor(Math.random() * testRemarks.length)]
  
  paramsProps.value.row = {
    ...paramsProps.value.row,
    name: randomName,
    remark: randomRemark,
    enable: true
  }
  
  ElMessage.success('已自动填充测试数据')
}

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  
  // 确保属性具有默认值
  if (paramsProps.value.row.enable === undefined || paramsProps.value.row.enable === null) {
    paramsProps.value.row.enable = true
  }
  
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

<style scoped lang="scss">
.form-container {
  padding: 8px 0;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.form-title {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
  letter-spacing: -0.022em;
}

.test-button {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    background-color: rgba(var(--el-color-primary-rgb), 0.1);
    transform: translateY(-1px);
  }
  
  .el-icon {
    font-size: 14px;
  }
}

.tag-category-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #1d1d1f;
    font-size: 13px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    border-radius: 6px;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.2);
    }

    &.is-focus {
      box-shadow: 0 0 0 2px var(--el-color-primary);
    }
  }
}
</style>