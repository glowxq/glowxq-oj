<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="80%"
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="图片名字" prop="name">
            <el-input
              v-model="paramsProps.row.name"
              placeholder="请填写图片名字(选填)"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="图片key" prop="imageKey">
            <el-input
              v-model="paramsProps.row.imageKey"
              placeholder="请填写图片key或点击自动生成"
              clearable
            >
              <template #append>
                <el-button @click="generateImageKey">自动生成</el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="业务类型" prop="businessType">
            <EnumSelect
              v-model="paramsProps.row.businessType"
              :enum-data="ImageBusinessType"
              type="tab"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序(降序)" prop="sort">
            <el-input-number
              v-model="paramsProps.row.sort"
              :precision="0"
              :min="1"
              :max="999999"
              placeholder="选填，默认100"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="图片URL" prop="url">
            <Img
              v-model:fileList="imageUrlList"
              dir="meta/image"
              :limit="5"
              :useOss="true"
              @change="handleImageChange"
            >
              <template #tip>
                <div class="el-upload__tip">支持jpg、png格式图片上传，建议尺寸1920*1080px或等比例，最多5张，每张不超过5MB</div>
              </template>
            </Img>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="图片介绍" prop="content">
            <el-input
              v-model="paramsProps.row.content"
              type="textarea"
              :rows="4"
              placeholder="请填写图片介绍"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="跳转链接" prop="skipUrl">
            <el-input
              v-model="paramsProps.row.skipUrl"
              placeholder="请填写跳转链接"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="启用" prop="enable">
            <el-switch v-model="paramsProps.row.enable"></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { useOptionsStore } from '@/stores/modules/options'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import { ImageBusinessType } from '@/enums/system/meta/ImageBusinessType'
import Img from '@/components/Common/Upload/Img.vue'
import { generateUUID } from '@/utils'
import type { UploadUserFile } from 'element-plus'

defineOptions({
    name: 'MetaImageForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  imageKey: [{ required: true, message: '请填写图片key' }],
  businessType: [{ required: true, message: '请选择业务类型' }],
  url: [{ required: true, message: '请上传图片或填写图片URL' }],
  enable: [{ required: true, message: '请选择是否启用' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 处理图片列表
const imageUrlList = ref<UploadUserFile[]>([])

// 监听url变化，更新图片文件列表
watch(() => paramsProps.value.row.url, (newValue) => {
  if (newValue && typeof newValue === 'string') {
    // 将字符串转换为文件列表
    const urls = newValue.split(',').filter(Boolean)
    imageUrlList.value = urls.map(url => ({
      name: url.split('/').pop() || 'image.jpg',
      url
    }))
  } else {
    imageUrlList.value = []
  }
}, { immediate: true })

// 图片变化处理
const handleImageChange = () => {
  // 将文件列表转换为逗号分隔的URL字符串
  paramsProps.value.row.url = imageUrlList.value
    .map(file => file.url)
    .join(',')
}

// 自动生成imageKey
const generateImageKey = () => {
  const prefix = 'img_';
  const timestamp = new Date().getTime().toString().substring(5);
  const randomStr = generateUUID().substring(0, 8);
  paramsProps.value.row.imageKey = `${prefix}${timestamp}_${randomStr}`;
}

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params

  // 如果是新增，设置默认启用和排序
  if (params.title.includes('新增')) {
    if (paramsProps.value.row.enable === undefined) {
      paramsProps.value.row.enable = true;
    }
    if (paramsProps.value.row.sort === undefined) {
      paramsProps.value.row.sort = 100;
    }
  }

  visible.value = true
}

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      // 提交前再次确保图片格式正确
      handleImageChange()
      
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

.el-row {
  margin-bottom: 10px;
}
</style>
