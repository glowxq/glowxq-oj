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
      <el-form-item label="父区域" prop="parentId">
        <RegionSelect 
          v-model="paramsProps.row.parentId" 
          placeholder="请选择父区域，不选择则为顶级区域"
          :exclude-ids="excludeIds"
          @change="handleParentChange"
        />
      </el-form-item>
      <el-form-item label="地址名" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写地址名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="地址拼音" prop="pinyin">
        <el-input
          v-model="paramsProps.row.pinyin"
          placeholder="请填写地址拼音"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="拼音前缀" prop="pinyinPrefix">
        <el-input
          v-model="paramsProps.row.pinyinPrefix"
          placeholder="请填写拼音前缀"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="地址等级" prop="level">
        <el-input-number 
          v-model="paramsProps.row.level" 
          :precision="0" 
          :min="0" 
          :max="5"
          placeholder="系统自动计算"
          disabled
        />
        <div class="form-tip">系统根据父区域自动计算等级</div>
      </el-form-item>
      <el-form-item label="启用" prop="enable">
        <el-switch v-model="paramsProps.row.enable" ></el-switch>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import RegionSelect from '@/components/Common/Meta/Region/RegionSelect.vue'
import { getMetaRegionDetailApi } from '@/api/modules/system/meta/metaRegion'

defineOptions({
    name: 'MetaRegionForm'
})

const rules = reactive({
  name: [{ required: true, message: '请填写地址名' }],
  pinyin: [{ required: true, message: '请填写地址拼音' }],
  pinyinPrefix: [{ required: true, message: '请填写拼音前缀' }],
  enable: [{ required: true, message: '请选择是否启用' }],
  tenantId: [{ required: true, message: '请填写租户ID' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 计算排除的ID列表（编辑时排除当前节点及其子节点）
const excludeIds = computed(() => {
  if (!paramsProps.value.row?.id) {
    return []
  }
  // 这里应该包含当前节点及其所有子节点的ID
  // 简化处理，只排除当前节点
  return [paramsProps.value.row.id]
})

// 处理父区域变化
const handleParentChange = async (parentId: string | number | null) => {
  if (!parentId) {
    // 如果没有选择父区域，则为顶级区域
    paramsProps.value.row.parentId = 0
    paramsProps.value.row.level = 1
    paramsProps.value.row.ancestors = '0'
  } else {
    try {
      // 获取父区域详情来计算层级和祖先路径
      const parentDetail = await getMetaRegionDetailApi({ id: Number(parentId) })
      const parent = parentDetail.data
      if (parent) {
        paramsProps.value.row.parentId = parentId
        paramsProps.value.row.level = (parent.level || 0) + 1
        // 构建祖先路径
        const parentAncestors = parent.ancestors || '0'
        paramsProps.value.row.ancestors = parentAncestors + ',' + parentId
      }
    } catch (error) {
      console.error('获取父区域详情失败:', error)
      ElMessage.warning('获取父区域信息失败，请重新选择')
    }
  }
}

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  
  // 初始化默认值
  if (!paramsProps.value.row.parentId && paramsProps.value.row.parentId !== 0) {
    paramsProps.value.row.parentId = 0
  }
  if (!paramsProps.value.row.level) {
    paramsProps.value.row.level = 1
  }
  if (!paramsProps.value.row.ancestors) {
    paramsProps.value.row.ancestors = '0'
  }
  if (paramsProps.value.row.enable === undefined) {
    paramsProps.value.row.enable = true
  }
  if (!paramsProps.value.row.tenantId) {
    paramsProps.value.row.tenantId = 'GLOWXQ'
  }
  
  visible.value = true
}

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      // 确保parentId为数字类型
      if (paramsProps.value.row.parentId && typeof paramsProps.value.row.parentId === 'string') {
        paramsProps.value.row.parentId = Number(paramsProps.value.row.parentId)
      }
      
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
.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>