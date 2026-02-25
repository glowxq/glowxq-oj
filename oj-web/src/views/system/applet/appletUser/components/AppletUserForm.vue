<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="650px"
    draggable
    class="apple-style-dialog"
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
        <el-col :span="24">
          <el-form-item label="关联系统用户" prop="sysUserId">
            <UserSelect
              v-model="paramsProps.row.sysUserId"
              :multiple="false"
              placeholder="请选择关联的系统用户"
              @change="handleUserChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="OpenID" prop="openid">
            <el-input
              v-model="paramsProps.row.openid"
              placeholder="请填写小程序用户的唯一标识"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="UnionID" prop="unionid">
            <el-input
              v-model="paramsProps.row.unionid"
              placeholder="请填写公众号的唯一标识"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务Code" prop="code">
            <el-input
              v-model="paramsProps.row.code"
              placeholder="请填写业务Code"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="绑定Code" prop="bindCode">
            <el-input
              v-model="paramsProps.row.bindCode"
              placeholder="请填写绑定Code"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="昵称" prop="nickname">
            <el-input
              v-model="paramsProps.row.nickname"
              placeholder="请填写昵称"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="真实姓名" prop="name">
            <el-input
              v-model="paramsProps.row.name"
              placeholder="请填写真实姓名"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="paramsProps.row.phone"
              placeholder="请填写手机号"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="sex">
            <el-select v-model="paramsProps.row.sex" clearable placeholder="请选择性别">
              <el-option label="未知" :value="0" />
              <el-option label="男性" :value="1" />
              <el-option label="女性" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="地址" prop="address">
            <el-cascader
              v-model="selectedRegion"
              :options="regionOptions"
              placeholder="请选择所在地区"
              @change="handleRegionChange"
              style="width: 100%"
            />
            <el-input
              v-model="detailAddress"
              placeholder="请填写详细地址"
              clearable
              style="margin-top: 8px"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="头像" prop="avatar">
            <UploadImg
              v-model:imageUrl="paramsProps.row.avatar"
              :disabled="selfDisabled"
              dir="applet/avatar"
              height="120px"
              width="120px"
              :use-oss="true"
              @change="handleAvatarChange"
            >
              <template #tip>
                <span class="upload-tip">支持 jpg、png 格式，建议尺寸 200x200px，不超过2M</span>
              </template>
            </UploadImg>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="链接图片" prop="url">
            <UploadImg
              v-model:imageUrl="paramsProps.row.url"
              :disabled="selfDisabled"
              dir="applet/url"
              height="120px"
              width="120px"
              :use-oss="true"
              @change="handleUrlChange"
            >
              <template #tip>
                <span class="upload-tip">支持 jpg、png 格式，建议尺寸 200x200px，不超过2M</span>
              </template>
            </UploadImg>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订阅公众号" prop="subscribe">
            <el-switch
              v-model="paramsProps.row.subscribe"
              active-text="是"
              inactive-text="否"
              class="custom-switch"
            ></el-switch>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="enable">
            <el-switch
              v-model="paramsProps.row.enable"
              active-text="启用"
              inactive-text="禁用"
              class="custom-switch"
            ></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false" class="cancel-btn">取消</el-button>
      <el-button type="primary" @click="handleSubmit" class="confirm-btn">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { useOptionsStore } from '@/stores/modules/options'
import UserSelect from '@/components/Common/User/UserSelect.vue'
import UploadImg from '@/components/Common/Upload/Img.vue'
import type { IUploadResult } from '@/api/interface/system/admin/upload'

// 声明地区数据
const regionData = [
  {
    value: '北京市',
    label: '北京市',
    children: [
      {
        value: '北京市',
        label: '北京市',
        children: [
          { value: '东城区', label: '东城区' },
          { value: '西城区', label: '西城区' },
          { value: '朝阳区', label: '朝阳区' },
          { value: '海淀区', label: '海淀区' },
          { value: '丰台区', label: '丰台区' },
          { value: '石景山区', label: '石景山区' }
        ]
      }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      {
        value: '上海市',
        label: '上海市',
        children: [
          { value: '黄浦区', label: '黄浦区' },
          { value: '徐汇区', label: '徐汇区' },
          { value: '静安区', label: '静安区' },
          { value: '普陀区', label: '普陀区' },
        ]
      }
    ]
  },
  {
    value: '广东省',
    label: '广东省',
    children: [
      {
        value: '广州市',
        label: '广州市',
        children: [
          { value: '越秀区', label: '越秀区' },
          { value: '荔湾区', label: '荔湾区' },
          { value: '海珠区', label: '海珠区' },
          { value: '天河区', label: '天河区' },
        ]
      },
      {
        value: '深圳市',
        label: '深圳市',
        children: [
          { value: '福田区', label: '福田区' },
          { value: '罗湖区', label: '罗湖区' },
          { value: '南山区', label: '南山区' },
          { value: '宝安区', label: '宝安区' },
        ]
      }
    ]
  }
]

defineOptions({
    name: 'AppletUserForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  sysUserId: [{ required: true, message: '请选择关联的系统用户' }],
  openid: [{ required: true, message: '请填写小程序用户的唯一标识' }],
  address: [{ required: true, message: '请填写地址' }],
  subscribe: [{ required: true, message: '请选择是否订阅公众号' }],
  enable: [{ required: true, message: '请选择状态' }],
  code: [{ required: true, message: '请填写业务Code' }],
  bindCode: [{ required: true, message: '请填写绑定Code' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 是否禁用
const selfDisabled = computed(() => {
  return false; // 可根据需要调整逻辑
})

// 地址相关变量
const selectedRegion = ref([])
const detailAddress = ref('')
const regionOptions = ref(regionData)

// 处理地址变化
const handleRegionChange = (value: any) => {
  const regionText = value.map((item: any) => item).join(' ')
  const fullAddress = detailAddress.value ? `${regionText} ${detailAddress.value}` : regionText
  paramsProps.value.row.address = fullAddress
}

// 监听详细地址变化
watch(detailAddress, (newVal) => {
  if (selectedRegion.value && selectedRegion.value.length > 0) {
    const regionText = selectedRegion.value.map((item: any) => item).join(' ')
    paramsProps.value.row.address = `${regionText} ${newVal}`
  } else {
    paramsProps.value.row.address = newVal
  }
})

// 处理用户选择变化
const handleUserChange = (value: number, selectedUser: any) => {
  if (selectedUser) {
    // 可以自动填充用户相关信息
    paramsProps.value.row.name = selectedUser.name || paramsProps.value.row.name
    paramsProps.value.row.phone = selectedUser.phone || paramsProps.value.row.phone
  }
}

// 处理头像上传变化
const handleAvatarChange = (fileInfo: IUploadResult) => {
  // 头像上传成功后的处理逻辑
  if (fileInfo && fileInfo.url) {
    paramsProps.value.row.avatar = fileInfo.url
  }
}

// 处理链接图片上传变化
const handleUrlChange = (fileInfo: IUploadResult) => {
  // 链接图片上传成功后的处理逻辑
  if (fileInfo && fileInfo.url) {
    paramsProps.value.row.url = fileInfo.url
  }
}

// 解析已有地址
const parseAddress = (address: string) => {
  if (!address) return

  // 简单的地址解析逻辑，可以根据实际格式调整
  const parts = address.split(' ')
  if (parts.length > 1) {
    // 假设最后一部分是详细地址
    detailAddress.value = parts[parts.length - 1]
    // 前面的部分可能是地区
    // 这里需要根据实际数据结构匹配地区
  } else {
    detailAddress.value = address
  }
}

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  visible.value = true

  // 解析已有地址
  if (params.row.address) {
    parseAddress(params.row.address)
  } else {
    detailAddress.value = ''
    selectedRegion.value = []
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

defineExpose({
  acceptParams
})
</script>

<style scoped lang="scss">
.apple-style-dialog {
  :deep(.el-dialog__header) {
    padding: 20px;
    border-bottom: 1px solid #f0f0f0;
    margin: 0;
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }

  :deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 500;
    color: #333;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #333;
  }

  :deep(.el-input__inner) {
    border-radius: 8px;
  }

  :deep(.el-select) {
    width: 100%;
  }

  :deep(.el-cascader) {
    width: 100%;
  }

  .custom-switch {
    :deep(.el-switch__label) {
      font-size: 13px;
    }
  }

  .upload-tip {
    color: #909399;
    font-size: 12px;
  }

  .confirm-btn {
    background-color: #0071e3;
    border-color: #0071e3;
    border-radius: 8px;
    font-weight: 500;
    padding: 10px 20px;

    &:hover {
      background-color: #0077ed;
      border-color: #0077ed;
    }
  }

  .cancel-btn {
    border-radius: 8px;
    padding: 10px 20px;
  }
}
</style>
