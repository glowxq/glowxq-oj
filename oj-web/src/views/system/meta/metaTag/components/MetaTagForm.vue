<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="680px"
    draggable
    class="tag-form-dialog"
  >
    <div class="form-preview-container">
      <el-form
        ref="ruleFormRef"
        label-width="120px"
        label-suffix=" :"
        :rules="rules"
        :model="paramsProps.row"
        @submit.enter.prevent="handleSubmit"
        class="tag-form"
      >
        <div class="form-section">
          <div class="section-header">
            <h3 class="section-title">基本信息</h3>
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
          <el-form-item label="标签名字" prop="name">
            <el-input
              v-model="paramsProps.row.name"
              placeholder="请填写标签名字"
              clearable
              @input="updatePreview"
            ></el-input>
          </el-form-item>

          <el-form-item label="所属分类" prop="categoryId">
            <el-select
              v-model="paramsProps.row.categoryId"
              placeholder="请选择所属分类"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="item in categoryOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>

          <h3 class="section-title">样式设置</h3>
          <el-form-item label="背景色" prop="backgroundColor">
            <ColorSelect
              v-model="paramsProps.row.backgroundColor"
              @update:modelValue="handleBackgroundColorChange"
            />
          </el-form-item>

          <el-form-item label="文字颜色" prop="textColor">
            <ColorSelect
              v-model="paramsProps.row.textColor"
              @update:modelValue="updatePreview"
            />
            <div class="color-tip">
              <el-tooltip content="自动根据背景色计算文字颜色" placement="top">
                <el-button
                  type="primary"
                  link
                  size="small"
                  @click="calculateTextColor"
                >自动计算</el-button>
              </el-tooltip>
            </div>
          </el-form-item>

          <el-form-item label="镂空样式" prop="plain">
            <el-switch
              v-model="paramsProps.row.plain"
              @change="updatePreview"
              active-color="#13ce66"
              inactive-color="#dcdfe6"
            ></el-switch>
          </el-form-item>

          <el-form-item label="启用" prop="enable">
            <el-switch
              v-model="paramsProps.row.enable"
              active-color="#13ce66"
              inactive-color="#dcdfe6"
            ></el-switch>
          </el-form-item>
        </div>
      </el-form>

      <div class="preview-container">
        <h3 class="preview-title">标签预览</h3>
        <div class="preview-content">
          <div class="tag-preview-wrapper">
            <div
              class="tag-preview"
              :style="tagStyle"
            >
              {{ previewText || '标签预览' }}
            </div>
          </div>

          <div class="preview-examples">
            <p>在不同背景下的效果：</p>
            <div class="preview-background light">
              <div
                class="tag-preview"
                :style="tagStyle"
              >
                {{ previewText || '标签预览' }}
              </div>
            </div>
            <div class="preview-background dark">
              <div
                class="tag-preview"
                :style="tagStyle"
              >
                {{ previewText || '标签预览' }}
              </div>
            </div>
          </div>

          <div class="color-contrast-info" v-if="contrastRatio">
            <p>对比度: {{ contrastRatio.toFixed(2) }}
              <span :class="{
                'pass': contrastRatio >= 4.5,
                'warning': contrastRatio >= 3 && contrastRatio < 4.5,
                'fail': contrastRatio < 3
              }">
                {{ contrastRatio >= 4.5 ? '(优秀)' : contrastRatio >= 3 ? '(一般)' : '(较差)' }}
              </span>
            </p>
            <p class="contrast-tip">建议对比度大于4.5以确保可读性</p>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false" class="cancel-btn"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit" class="submit-btn"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import ColorSelect from '@/components/Common/Color/ColorSelect.vue'
import { getMetaTagCategoryListApi } from '@/api/modules/system/meta/metaTagCategory'
import type { IMetaTagCategory } from '@/api/interface/system/meta/metaTagCategory'

defineOptions({
    name: 'MetaTagForm'
})

const rules = reactive({
  name: [{ required: true, message: '请填写标签名字' }],
  backgroundColor: [{ required: true, message: '请填写背景' }],
  textColor: [{ required: true, message: '请填写字体颜色' }],
  plain: [{ required: true, message: '请选择是否镂空' }],
  enable: [{ required: true, message: '请选择是否启用' }],
  categoryId: [{ required: true, message: '请选择所属分类' }],
})

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {
    backgroundColor: '#009688',
    textColor: '#FFFFFF',
    plain: true,
    enable: true
  },
  api: undefined,
  getTableList: undefined
})

const previewText = ref('')
const contrastRatio = ref(0)

// 获取分类选项
const categoryOptions = ref<IMetaTagCategory.Row[]>([])

// 获取标签分类列表
const getCategoryOptions = async () => {
  try {
    const res = await getMetaTagCategoryListApi({ enable: "true", page: 1, limit: 100 })
    if (res?.data) {
      categoryOptions.value = res.data.rows || []
    }
  } catch (error) {
    console.error('获取标签分类失败', error)
  }
}

// 计算标签样式
const tagStyle = computed(() => {
  const { backgroundColor, textColor, plain } = paramsProps.value.row

  if (plain) {
    return {
      color: backgroundColor || '#009688',
      borderColor: backgroundColor || '#009688',
      backgroundColor: 'transparent',
      border: `1px solid ${backgroundColor || '#009688'}`
    }
  } else {
    return {
      color: textColor || '#FFFFFF',
      backgroundColor: backgroundColor || '#009688',
      border: `1px solid ${backgroundColor || '#009688'}`
    }
  }
})

// 转换颜色为RGB值
const hexToRgb = (hex: string) => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result ? {
    r: parseInt(result[1], 16),
    g: parseInt(result[2], 16),
    b: parseInt(result[3], 16)
  } : { r: 0, g: 0, b: 0 }
}

// 计算亮度
const getLuminance = (color: string) => {
  const rgb = hexToRgb(color)
  // 相对亮度公式: https://www.w3.org/TR/WCAG20/#relativeluminancedef
  const r = rgb.r / 255
  const g = rgb.g / 255
  const b = rgb.b / 255

  const R = r <= 0.03928 ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4)
  const G = g <= 0.03928 ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4)
  const B = b <= 0.03928 ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4)

  return 0.2126 * R + 0.7152 * G + 0.0722 * B
}

// 计算对比度
const calculateContrastRatio = () => {
  if (!paramsProps.value.row.backgroundColor || !paramsProps.value.row.textColor) return

  const bgLuminance = getLuminance(paramsProps.value.row.backgroundColor)
  const textLuminance = getLuminance(paramsProps.value.row.textColor)

  // 对比度公式: (L1 + 0.05) / (L2 + 0.05)，其中 L1 是较亮的颜色，L2 是较暗的颜色
  const ratio = bgLuminance > textLuminance
    ? (bgLuminance + 0.05) / (textLuminance + 0.05)
    : (textLuminance + 0.05) / (bgLuminance + 0.05)

  contrastRatio.value = ratio
}

// 根据背景颜色自动计算文字颜色
const calculateTextColor = () => {
  if (!paramsProps.value.row.backgroundColor) return

  const bgLuminance = getLuminance(paramsProps.value.row.backgroundColor)
  // 根据背景亮度选择黑色或白色文字
  paramsProps.value.row.textColor = bgLuminance > 0.5 ? '#000000' : '#FFFFFF'
  calculateContrastRatio()
}

// 处理背景颜色变化
const handleBackgroundColorChange = (color: string) => {
  updatePreview()
  calculateContrastRatio()
}

// 监听颜色变化更新对比度
watch(() => [paramsProps.value.row.backgroundColor, paramsProps.value.row.textColor], () => {
  calculateContrastRatio()
}, { deep: true })

// 更新预览文本
const updatePreview = () => {
  previewText.value = paramsProps.value.row.name || '标签预览'
}

// 一键填充测试数据
const fillTestData = () => {
  const testNames = ['重要', '紧急', '高优先级', '新功能', 'Bug修复', '测试标签', '临时', '待处理']
  const testColors = ['#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#9B59B6', '#1ABC9C', '#E67E22', '#34495E']
  
  const randomName = testNames[Math.floor(Math.random() * testNames.length)]
  const randomBgColor = testColors[Math.floor(Math.random() * testColors.length)]
  
  // 随机选择一个分类（如果有的话）
  const randomCategoryId = categoryOptions.value.length > 0 
    ? categoryOptions.value[Math.floor(Math.random() * categoryOptions.value.length)].id 
    : undefined
  
  paramsProps.value.row = {
    ...paramsProps.value.row,
    name: randomName,
    backgroundColor: randomBgColor,
    categoryId: randomCategoryId,
    plain: Math.random() > 0.5, // 随机镂空样式
    enable: true
  }
  
  // 自动计算文字颜色
  calculateTextColor()
  updatePreview()
  
  ElMessage.success('已自动填充测试数据')
}

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params

  // 确保属性具有默认值
  if (!paramsProps.value.row.backgroundColor) {
    paramsProps.value.row.backgroundColor = '#009688'
  }

  if (!paramsProps.value.row.textColor) {
    paramsProps.value.row.textColor = '#FFFFFF'
  }

  if (paramsProps.value.row.plain === undefined || paramsProps.value.row.plain === null) {
    paramsProps.value.row.plain = true
  }

  if (paramsProps.value.row.enable === undefined || paramsProps.value.row.enable === null) {
    paramsProps.value.row.enable = true
  }

  updatePreview()
  calculateContrastRatio()
  getCategoryOptions() // 获取分类选项
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
.tag-form-dialog {
  :deep(.el-dialog__header) {
    border-bottom: 1px solid #f0f0f0;
    padding: 20px 24px;
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }

  :deep(.el-dialog__footer) {
    border-top: 1px solid #f0f0f0;
    padding: 16px 24px;
  }
}

.form-preview-container {
  display: flex;
  gap: 24px;
}

.tag-form {
  flex: 1;

  .form-section {
    background-color: #f9f9f9;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 16px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .section-title {
    font-size: 15px;
    color: #333;
    margin: 0;
    font-weight: 500;
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
}

.color-tip {
  margin-top: 8px;
  display: flex;
  align-items: center;
}

.preview-container {
  width: 220px;
  border-radius: 12px;
  background-color: #f9f9f9;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .preview-title {
    font-size: 15px;
    color: #333;
    margin: 0 0 16px 0;
    font-weight: 500;
  }

  .preview-content {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .tag-preview-wrapper {
    display: flex;
    justify-content: center;
    margin-bottom: 16px;
  }

  .tag-preview {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 4px;
    font-size: 12px;
    white-space: nowrap;
    line-height: 1.5;
  }

  .preview-examples {
    p {
      font-size: 13px;
      color: #666;
      margin-bottom: 8px;
    }

    .preview-background {
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 8px;
      display: flex;
      justify-content: center;

      &.light {
        background-color: #ffffff;
        border: 1px solid #eaeaea;
      }

      &.dark {
        background-color: #2c2c2c;
      }
    }
  }

  .color-contrast-info {
    margin-top: 8px;
    padding: 8px;
    border-radius: 8px;
    background-color: #f5f5f5;

    p {
      margin: 0 0 4px 0;
      font-size: 13px;

      .pass {
        color: #67c23a;
      }

      .warning {
        color: #e6a23c;
      }

      .fail {
        color: #f56c6c;
      }
    }

    .contrast-tip {
      font-size: 12px;
      color: #909399;
    }
  }
}

.cancel-btn, .submit-btn {
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
}

.submit-btn {
  background: #0071e3;
  border-color: #0071e3;

  &:hover {
    background: #0077ed;
    border-color: #0077ed;
  }
}
</style>
