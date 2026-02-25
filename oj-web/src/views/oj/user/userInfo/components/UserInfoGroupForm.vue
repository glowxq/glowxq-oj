<template>
  <el-dialog 
    v-model="visible" 
    :title="`${paramsProps.title}`" 
    :destroy-on-close="true" 
    width="600px" 
    draggable 
    append-to-body
    class="user-group-dialog"
  >
    <el-alert
      title="批量绑定提醒"
      type="info"
      show-icon
      description="批量功能会将选中用户绑定到指定的班级，请谨慎操作！"
      :closable="false"
      class="alert-notice"
    />
    
    <el-form
      ref="ruleFormRef"
      label-width="100px"
      label-suffix=""
      :rules="rules"
      :model="formData"
      @submit.enter.prevent="handleSubmit"
      class="group-form"
    >
      <el-form-item label="选中用户" prop="userIds">
        <div class="user-tags-container">
          <el-tag 
            class="user-tag" 
            v-for="user in selectedUsers" 
            :key="user.id" 
            type="primary"
            effect="light"
          >
            <el-icon class="user-icon"><User /></el-icon>
            {{ user.name || user.nickName || `用户${user.id}` }}
          </el-tag>
          <el-empty 
            v-if="selectedUsers.length === 0" 
            description="暂无选中用户" 
            :image-size="60"
          />
        </div>
      </el-form-item>
      
      <el-form-item label="目标班级" prop="groupIds">
        <GroupSelect
          v-model="formData.groupIds"
          :multiple="true"
          placeholder="请选择要绑定的班级"
          :clearable="true"
          class="group-selector"
        />
        <div class="form-tip">
          <el-icon class="tip-icon"><InfoFilled /></el-icon>
          <span>可以选择多个班级，用户将同时归属于所选的所有班级</span>
        </div>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="cancel-btn">
          取消
        </el-button>
        <el-button 
          type="primary" 
          @click="handleSubmit" 
          :loading="loading"
          class="submit-btn"
        >
          <el-icon v-if="!loading"><Check /></el-icon>
          {{ loading ? '绑定中...' : '确定绑定' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { User, InfoFilled, Check } from '@element-plus/icons-vue'
import { bindUserInfoGroupApi } from '@/api/modules/oj/user/userInfo'
import GroupSelect from '@/components/Oj/Group/GroupSelect.vue'
import type { IUserInfo } from '@/api/interface/oj/user/userInfo'

defineOptions({
  name: 'UserInfoGroupForm'
})

interface Props {
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '批量绑定班级'
})

const emit = defineEmits<{
  'submit': []
}>()

const visible = ref(false)
const loading = ref(false)
const ruleFormRef = ref<InstanceType<typeof ElForm>>()

const paramsProps = ref<{
  title: string
  selectedUsers: IUserInfo.Row[]
  getTableList?: () => void
}>({
  title: '',
  selectedUsers: [],
  getTableList: undefined
})

const formData = reactive({
  userIds: [] as number[],
  groupIds: [] as number[]
})

const selectedUsers = computed(() => paramsProps.value.selectedUsers || [])

// 表单验证规则
const rules = reactive({
  groupIds: [
    { 
      required: true, 
      message: '请选择至少一个班级', 
      type: 'array', 
      min: 1 
    }
  ]
})

// 接收父组件传过来的参数
const acceptParams = (params: {
  title?: string
  selectedUsers: IUserInfo.Row[]
  getTableList?: () => void
}) => {
  paramsProps.value = {
    title: params.title || props.title,
    selectedUsers: params.selectedUsers || [],
    getTableList: params.getTableList
  }
  
  // 设置用户ID列表
  formData.userIds = params.selectedUsers.map(user => user.id!).filter(Boolean)
  formData.groupIds = []
  
  visible.value = true
}

// 提交数据
const handleSubmit = () => {
  if (!ruleFormRef.value) return
  
  ruleFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    if (formData.userIds.length === 0) {
      ElMessage.warning('请选择要绑定的用户')
      return
    }
    
    if (formData.groupIds.length === 0) {
      ElMessage.warning('请选择目标班级')
      return
    }
    
    loading.value = true
    try {
      await bindUserInfoGroupApi({
        userIds: formData.userIds,
        groupIds: formData.groupIds
      })
      
      ElMessage.success(`成功为 ${formData.userIds.length} 个用户绑定班级！`)
      
      // 刷新表格数据
      if (paramsProps.value.getTableList) {
        paramsProps.value.getTableList()
      }
      
      emit('submit')
      visible.value = false
    } catch (error) {
      console.error('批量绑定班级失败:', error)
      ElMessage.error('绑定失败，请重试')
    } finally {
      loading.value = false
    }
  })
}

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss">
.user-group-dialog {
  :deep(.el-dialog__body) {
    padding: 20px 24px;
  }

  :deep(.el-dialog__header) {
    padding: 20px 24px 12px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.04);

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #1d1d1f;
    }
  }
}

.alert-notice {
  margin-bottom: 20px;
  border-radius: 8px;
  
  :deep(.el-alert__content) {
    .el-alert__title {
      font-weight: 600;
    }
    
    .el-alert__description {
      margin-top: 6px;
      font-size: 13px;
      line-height: 1.4;
    }
  }
}

.group-form {
  .user-tags-container {
    min-height: 60px;
    padding: 12px;
    border: 1px solid var(--el-border-color-light);
    border-radius: 8px;
    background-color: var(--el-fill-color-extra-light);
    
    .user-tag {
      margin: 4px 8px 4px 0;
      padding: 6px 12px;
      border-radius: 16px;
      font-size: 13px;
      
      .user-icon {
        margin-right: 4px;
        font-size: 14px;
      }
    }
  }
  
  .group-selector {
    width: 100%;
  }
  
  .form-tip {
    display: flex;
    align-items: center;
    margin-top: 8px;
    padding: 8px 12px;
    background-color: var(--el-color-primary-light-9);
    border-radius: 6px;
    font-size: 12px;
    color: var(--el-color-primary);
    
    .tip-icon {
      margin-right: 6px;
      font-size: 14px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  .cancel-btn {
    padding: 8px 20px;
    border-radius: 8px;
  }
  
  .submit-btn {
    padding: 8px 20px;
    border-radius: 8px;
    font-weight: 500;
    
    .el-icon {
      margin-right: 4px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-group-dialog {
    :deep(.el-dialog) {
      width: 90% !important;
      margin: 5vh auto;
    }
  }
  
  .group-form {
    .user-tags-container {
      min-height: 80px;
      
      .user-tag {
        font-size: 12px;
        padding: 4px 8px;
      }
    }
  }
  
  .dialog-footer {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style> 