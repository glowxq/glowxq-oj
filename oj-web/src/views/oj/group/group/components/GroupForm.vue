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
      <el-form-item label="班级名" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写班级名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="班级代码" prop="code">
        <div class="code-input-group">
          <el-input
            v-model="paramsProps.row.code"
            placeholder="请填写班级代码"
            maxlength="6"
            :disabled="!isAdd"
          ></el-input>
          <el-button
            v-if="isAdd"
            type="primary"
            @click="generateCode"
          >
            生成代码
          </el-button>
        </div>
      </el-form-item>
      <el-form-item label="负责人" prop="principalUserId">
        <user-select
          v-model="selectedUser"
          @change="handleUserChange"
          placeholder="请选择班级负责人"
        />
      </el-form-item>
      <el-form-item label="班级标签" prop="tagIds">
        <tag-select
          v-model="selectedTags"
          multiple
          placeholder="请选择班级标签"
          @change="handleTagsChange"
        />
      </el-form-item>
      <el-form-item label="班级描述" prop="description">
        <el-input
          v-model="paramsProps.row.description"
          placeholder="请填写班级描述"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="班级颜色" prop="color">
        <color-select
          v-model="paramsProps.row.color"
          preview-text="班级标签"
          :show-preview="true"
        />
      </el-form-item>
      <el-form-item label="字体颜色" prop="textColor">
        <color-select
          v-model="paramsProps.row.textColor"
          :show-preview="false"
        />
      </el-form-item>
      <el-form-item label="启用" prop="enable">
        <el-switch v-model="paramsProps.row.enable" ></el-switch>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="warning" @click="fillTestData" plain>🧪 一键测试</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import UserSelect from '@/components/Common/User/UserSelect.vue'
import ColorSelect from '@/components/Common/Color/ColorSelect.vue'
import TagSelect from '@/components/Common/Meta/Tag/TagSelect.vue'

// 定义默认参数类型
interface DefaultParams {
  title: string;
  row: Record<string, any>;
  api?: (params: any) => Promise<any>;
  getTableList?: () => void;
}

defineOptions({
    name: 'GroupForm'
})

const rules = reactive({
  name: [{ required: true, message: '请填写班级名' }],
  code: [{ required: true, message: '请填写班级代码' }],
  principalUserId: [{ required: true, message: '请选择负责人' }],
  description: [{ required: true, message: '请填写班级描述' }],
  color: [{ required: true, message: '请填写班级颜色' }],
  textColor: [{ required: true, message: '请填写字体颜色' }],
  enable: [{ required: true, message: '请填写启用' }],
  createTime: [{ required: true, message: '请填写创建时间' }],
  updateTime: [{ required: true, message: '请填写更新时间' }],
  delFlag: [{ required: true, message: '请填写是否删除' }],
})

const visible = ref(false)
const paramsProps = ref<DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 用户选择相关
const selectedUser = ref<number | undefined>(undefined)

// 标签选择相关
const selectedTags = ref<number[]>([])

// 监听表单用户ID变化
watch(() => paramsProps.value.row.principalUserId, (newVal) => {
  if (newVal) {
    selectedUser.value = Number(newVal)
  } else {
    selectedUser.value = undefined
  }
}, { immediate: true })

// 监听表单标签ID变化
watch(() => paramsProps.value.row.tagIds, (newVal) => {
  if (newVal && Array.isArray(newVal)) {
    selectedTags.value = newVal.map(id => Number(id))
  } else if (newVal) {
    // 处理可能的字符串形式的标签ID
    try {
      // 尝试解析为JSON字符串（可能是后端返回的数组格式）
      const parsedIds = JSON.parse(String(newVal))
      selectedTags.value = Array.isArray(parsedIds) ? parsedIds.map(id => Number(id)) : []
    } catch (e) {
      // 如果解析失败，可能是逗号分隔的ID字符串
      if (typeof newVal === 'string' && newVal.includes(',')) {
        selectedTags.value = newVal.split(',').map(id => Number(id.trim())).filter(id => !isNaN(id))
      } else {
        // 单个ID
        const id = Number(newVal)
        selectedTags.value = isNaN(id) ? [] : [id]
      }
    }
  } else {
    selectedTags.value = []
  }
}, { immediate: true })

// 处理用户选择变更
const handleUserChange = (userId: number, userInfo: any) => {
  if (userId) {
    paramsProps.value.row.principalUserId = userId

    // 使用接收到的用户信息
    if (userInfo) {
      paramsProps.value.row.principalName = userInfo.nickname || userInfo.username || '未知用户'
    }
  } else {
    paramsProps.value.row.principalUserId = undefined
    paramsProps.value.row.principalName = ''
  }
}

// 处理标签选择变更
const handleTagsChange = (tagIds: number[]) => {
  console.log('标签选择发生变化:', tagIds)
  paramsProps.value.row.tagIds = tagIds
}

// 是否为新增
const isAdd = ref(true)

// 生成6位随机代码
const generateCode = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  let code = ''
  for (let i = 0; i < 6; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  paramsProps.value.row.code = code
}

// 接收父组件传过来的参数
const acceptParams = (params: DefaultParams) => {
  console.log('接收到父组件参数:', params)
  paramsProps.value = params
  visible.value = true
  isAdd.value = params.title.includes('新增')

  // 初始化用户选择
  if (params.row && params.row.principalUserId) {
    selectedUser.value = Number(params.row.principalUserId)
  } else {
    selectedUser.value = undefined
  }

  // 打印完整的行数据，查看标签数据的原始形式
  console.log('完整的行数据:', params.row)

  // 初始化标签选择
  // 首先检查是否有tagList，这是服务器返回的完整标签列表对象
  if (params.row.tagList && Array.isArray(params.row.tagList) && params.row.tagList.length > 0) {
    console.log('从tagList提取标签ID')
    // 从tagList中提取id数组
    const tagIds = params.row.tagList.map(tag => tag.id).filter(Boolean)
    console.log('从tagList提取的标签ID:', tagIds)
    selectedTags.value = tagIds

    // 更新tagIds字段，确保提交时包含正确的标签ID
    paramsProps.value.row.tagIds = tagIds
  }
  // 如果没有tagList但有tagIds，则处理tagIds
  else if (params.row && params.row.tagIds) {
    console.log('原始标签数据:', params.row.tagIds, '类型:', typeof params.row.tagIds)

    // 尝试直接将各种格式转换为数组
    let tagIds: number[] = [];

    if (Array.isArray(params.row.tagIds)) {
      console.log('标签数据是数组')
      tagIds = params.row.tagIds.map(id => Number(id))
    } else if (typeof params.row.tagIds === 'string') {
      // 检查是否是JSON字符串
      if (params.row.tagIds.startsWith('[') && params.row.tagIds.endsWith(']')) {
        try {
          const parsedIds = JSON.parse(params.row.tagIds)
          console.log('解析JSON后:', parsedIds)
          tagIds = Array.isArray(parsedIds) ? parsedIds.map(id => Number(id)) : []
        } catch (e) {
          console.log('JSON解析失败:', e)
        }
      } else if (params.row.tagIds.includes(',')) {
        // 尝试解析逗号分隔的字符串
        console.log('解析逗号分隔的字符串')
        tagIds = params.row.tagIds.split(',').map(id => Number(id.trim())).filter(id => !isNaN(id))
      } else {
        // 尝试解析单个数字
        console.log('解析单个ID')
        const id = Number(params.row.tagIds)
        tagIds = isNaN(id) ? [] : [id]
      }
    } else if (typeof params.row.tagIds === 'number') {
      // 单个数字ID
      tagIds = [params.row.tagIds]
    }

    console.log('转换后的标签IDs:', tagIds)
    selectedTags.value = tagIds

    // 强制设置tagIds为转换后的数组，确保提交时格式一致
    paramsProps.value.row.tagIds = tagIds
  } else {
    console.log('没有标签数据')
    selectedTags.value = []
  }

  // 如果是新增，自动生成代码
  if (isAdd.value) {
    generateCode()
  }
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

// 生成随机测试数据
const generateTestData = () => {
  const randomId = Math.floor(Math.random() * 10000);
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let code = '';
  for (let i = 0; i < 6; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }

  return {
    name: `测试班级${randomId}`,
    code: code,
    description: `这是一个自动生成的测试班级${randomId}，用于测试系统功能。`,
    color: '#FF6B6B',
    textColor: '#FFFFFF',
    enable: true,
    principalUserId: 1,
    tagIds: [1, 2]
  };
};

// 一键填充测试数据
const fillTestData = () => {
  const testData = generateTestData();
  Object.assign(paramsProps.value.row, testData);

  // 更新选择器的值
  selectedUser.value = testData.principalUserId;
  selectedTags.value = testData.tagIds;

  ElMessage.success('测试数据已填充！');
};

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss">
.code-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

:deep(.el-button--warning.is-plain) {
  border-color: var(--el-color-warning);
  color: var(--el-color-warning);

  &:hover {
    background-color: var(--el-color-warning);
    border-color: var(--el-color-warning);
    color: white;
  }
}
</style>
