<template>
  <div class="dept-select-demo">
    <h2>DeptSelect 组件使用示例</h2>
    
    <el-card class="demo-card" header="单选模式">
      <el-form label-width="120px">
        <el-form-item label="选择部门:">
          <DeptSelect 
            v-model="singleDeptId"
            placeholder="请选择一个部门"
            @change="handleSingleChange"
          />
        </el-form-item>
        <el-form-item label="选中的部门ID:">
          <el-tag v-if="singleDeptId" type="primary">{{ singleDeptId }}</el-tag>
          <span v-else class="text-placeholder">未选择</span>
        </el-form-item>
        <el-form-item label="选中的部门信息:">
          <pre v-if="singleDeptData">{{ JSON.stringify(singleDeptData, null, 2) }}</pre>
          <span v-else class="text-placeholder">未选择</span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="demo-card" header="多选模式">
      <el-form label-width="120px">
        <el-form-item label="选择部门:">
          <DeptSelect 
            v-model="multipleDeptIds"
            :multiple="true"
            placeholder="请选择多个部门"
            @change="handleMultipleChange"
          />
        </el-form-item>
        <el-form-item label="选中的部门IDs:">
          <el-tag 
            v-for="id in multipleDeptIds" 
            :key="id" 
            type="primary" 
            class="dept-tag"
          >
            {{ id }}
          </el-tag>
          <span v-if="!multipleDeptIds.length" class="text-placeholder">未选择</span>
        </el-form-item>
        <el-form-item label="选中的部门信息:">
          <pre v-if="multipleDeptData && multipleDeptData.length">{{ JSON.stringify(multipleDeptData, null, 2) }}</pre>
          <span v-else class="text-placeholder">未选择</span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="demo-card" header="高级配置">
      <el-form label-width="120px">
        <el-form-item label="禁用状态:">
          <DeptSelect 
            v-model="advancedDeptId"
            :disabled="isDisabled"
            placeholder="禁用状态的部门选择"
          />
        </el-form-item>
        <el-form-item label="控制选项:">
          <el-switch 
            v-model="isDisabled" 
            active-text="禁用" 
            inactive-text="启用"
          />
        </el-form-item>
        <el-form-item label="不可清空:">
          <DeptSelect 
            v-model="noClearDeptId"
            :clearable="false"
            placeholder="不可清空的部门选择"
          />
        </el-form-item>
        <el-form-item label="不展开全部:">
          <DeptSelect 
            v-model="noExpandDeptId"
            :default-expand-all="false"
            placeholder="默认不展开全部节点"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="demo-card" header="表单验证示例">
      <el-form 
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="所属部门:" prop="deptId">
          <DeptSelect 
            v-model="formData.deptId"
            placeholder="请选择所属部门"
          />
        </el-form-item>
        <el-form-item label="管理部门:" prop="manageDeptIds">
          <DeptSelect 
            v-model="formData.manageDeptIds"
            :multiple="true"
            placeholder="请选择管理的部门"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="validateForm">验证表单</el-button>
          <el-button @click="resetForm">重置表单</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import DeptSelect from './DeptSelect.vue'
import type { ISysDept } from '@/api/interface/system/admin/dept'

defineOptions({
  name: 'DeptSelectExample'
})

// 单选示例
const singleDeptId = ref<number | null>(null)
const singleDeptData = ref<ISysDept.Tree | undefined>()

const handleSingleChange = (value: number | number[] | null, data?: ISysDept.Tree | ISysDept.Tree[]) => {
  console.log('单选部门变化:', value, data)
  singleDeptData.value = Array.isArray(data) ? data[0] : data
}

// 多选示例
const multipleDeptIds = ref<number[]>([])
const multipleDeptData = ref<ISysDept.Tree[]>([])

const handleMultipleChange = (value: number | number[] | null, data?: ISysDept.Tree | ISysDept.Tree[]) => {
  console.log('多选部门变化:', value, data)
  multipleDeptData.value = Array.isArray(data) ? data : []
}

// 高级配置示例
const advancedDeptId = ref<number | null>(null)
const noClearDeptId = ref<number | null>(null)
const noExpandDeptId = ref<number | null>(null)
const isDisabled = ref(false)

// 表单验证示例
const formRef = ref<FormInstance>()
const formData = reactive({
  deptId: null as number | null,
  manageDeptIds: [] as number[]
})

const formRules = {
  deptId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  manageDeptIds: [
    { 
      required: true, 
      message: '请至少选择一个管理部门', 
      trigger: 'change',
      validator: (rule: any, value: number[], callback: Function) => {
        if (!Array.isArray(value) || value.length === 0) {
          callback(new Error('请至少选择一个管理部门'))
        } else {
          callback()
        }
      }
    }
  ]
}

const validateForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      ElMessage.success('表单验证通过！')
      console.log('表单数据:', formData)
    } else {
      ElMessage.error('表单验证失败！')
    }
  })
}

const resetForm = () => {
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.dept-select-demo {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  h2 {
    color: #1d1d1f;
    font-weight: 600;
    margin-bottom: 24px;
    text-align: center;
  }
}

.demo-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  :deep(.el-card__header) {
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    border-bottom: 1px solid rgba(0, 0, 0, 0.08);
    font-weight: 600;
    color: #1d1d1f;
  }

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.dept-tag {
  margin-right: 8px;
  margin-bottom: 4px;
}

.text-placeholder {
  color: #909399;
  font-style: italic;
}

pre {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  font-size: 12px;
  color: #2c3e50;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
}

.el-form {
  .el-form-item {
    margin-bottom: 18px;
  }
}
</style> 