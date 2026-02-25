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
      <el-form-item label="底部菜单名称" prop="name">
        <el-input
          v-model="paramsProps.row.name"
          placeholder="请填写底部菜单名称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="图标类型" prop="iconType">
        <EnumSelect
          v-model="paramsProps.row.iconType"
          :enum-data="MenuIconType"
          type="tab"
          @change="handleIconTypeChange"
        />
      </el-form-item>
      <el-form-item label="选中图标" prop="activeIcon">
        <Img
          v-if="isImageIconType"
          v-model:imageUrl="paramsProps.row.activeIcon"
          height="100px"
          width="100px"
          dir="menu"
          accept="image/*"
        />
        <el-input
          v-else
          v-model="paramsProps.row.activeIcon"
          placeholder="请填写选中图标"
          clearable
        />
      </el-form-item>
      <el-form-item label="未选中图标" prop="inactiveIcon">
        <Img
          v-if="isImageIconType"
          v-model:imageUrl="paramsProps.row.inactiveIcon"
          height="100px"
          width="100px"
          dir="menu"
          accept="image/*"
        />
        <el-input
          v-else
          v-model="paramsProps.row.inactiveIcon"
          placeholder="请填写未选中图标"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="菜单类型" prop="type">
        <EnumSelect
          v-model="paramsProps.row.type"
          :enum-data="MenuType"
          type="tab"
        />
      </el-form-item>
      <el-form-item label="菜单路径" prop="path">
        <el-input
          v-model="paramsProps.row.path"
          placeholder="请填写菜单路径"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="enable">
        <el-switch v-model="paramsProps.row.enable" ></el-switch>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="paramsProps.row.sort" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="是否有子级" prop="hasChildren">
        <el-switch
          v-model="paramsProps.row.hasChildren"
          placeholder="请填写是否有子级"
          clearable
        ></el-switch>
      </el-form-item>
      <el-form-item label="是否锁定" prop="lock">
        <el-switch
          v-model="paramsProps.row.lock"
          placeholder="请填写是否锁定"
          clearable
        ></el-switch>
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
import { ref, reactive, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { useOptionsStore } from '@/stores/modules/options'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import Img from '@/components/Common/Upload/Img.vue'
import { MenuType } from '@/enums/system/meta/MenuType'
import { MenuIconType } from '@/enums/system/meta/MenuIconType'

defineOptions({
    name: 'MetaMenuForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  name: [{ required: true, message: '请填写底部菜单名称' }],
  activeIcon: [{ required: true, message: '请填写选中图标' }],
  inactiveIcon: [{ required: true, message: '请填写未选中图标' }],
  iconType: [{ required: true, message: '请选择图标类型' }],
  type: [{ required: true, message: '请选择菜单类型' }],
  path: [{ required: true, message: '请填写菜单路径' }],
  lock: [{ required: true, message: '请填写是否锁定' }],
  delFlag: [{ required: true, message: '请填写删除标识' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 判断是否为图片类型图标
const isImageIconType = computed(() => {
  return paramsProps.value.row.iconType === 'Image'
})

// 图标类型变更时处理
const handleIconTypeChange = () => {
  // 当图标类型变更时，清空已选图标值
  paramsProps.value.row.activeIcon = ''
  paramsProps.value.row.inactiveIcon = ''
}

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
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
.el-form-item {
  margin-bottom: 20px;
}
</style>
