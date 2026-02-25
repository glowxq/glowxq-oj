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
          <el-form-item label="text名字" prop="name">
            <el-input
              v-model="paramsProps.row.name"
              placeholder="请填写text名字(选填)"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="文本key" prop="textKey">
            <el-input
              v-model="paramsProps.row.textKey"
              placeholder="请填写文本key或点击自动生成"
              clearable
            >
              <template #append>
                <el-button @click="generateTextKey">自动生成</el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="文本类型" prop="textType">
            <EnumSelect
              v-model="paramsProps.row.textType"
              :enum-data="TextType"
              type="tab"
              @change="handleTextTypeChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务类型" prop="businessType">
            <EnumSelect
              v-model="paramsProps.row.businessType"
              :enum-data="TextBusinessType"
              type="tab"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="图标" prop="icon">
            <UploadImg
              v-model:imageUrl="paramsProps.row.icon"
              height="100px"
              width="100px"
              :dir="'meta/icon'"
            >
              <template #tip>
                <div class="el-upload__tip">支持jpg、png格式，建议尺寸100*100px</div>
              </template>
            </UploadImg>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="文本标题" prop="title">
            <el-input
              v-model="paramsProps.row.title"
              placeholder="请填写文本标题"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="跳转URL" prop="skipUrl">
            <el-input
              v-model="paramsProps.row.skipUrl"
              placeholder="请填写跳转URL"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序(降序)" prop="sort">
            <el-input-number v-model="paramsProps.row.sort" :precision="0" :min="1" :max="999999" placeholder="选填，默认100" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="文本内容" prop="content">
            <!-- Markdown编辑器 -->
            <template v-if="isMarkdownType">
              <Markdown
                v-model="paramsProps.row.content"
                :height="400"
                :toolbars="markdownToolbars"
                :key="markdownKey"
              />
            </template>
            <!-- 富文本编辑器 -->
            <template v-else-if="isRichTextType">
              <RichText
                v-model="paramsProps.row.content"
                :height="400"
                placeholder="请输入富文本内容"
              />
            </template>
            <!-- 普通文本 -->
            <template v-else>
              <el-input
                v-model="paramsProps.row.content"
                type="textarea"
                placeholder="请填写文本内容"
                :rows="10"
              ></el-input>
            </template>
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { type ElForm, ElMessage } from 'element-plus'
import { useOptionsStore } from '@/stores/modules/options'
import EnumSelect from '@/components/Common/Enum/EnumSelect.vue'
import { TextType } from '@/enums/system/meta/TextType'
import { TextBusinessType } from '@/enums/system/meta/TextBusinessType'
import Markdown from '@/components/Common/Markdown/index.vue'
import RichText from '@/components/Common/RichText/index.vue'
import UploadImg from '@/components/Common/Upload/Img.vue'
import { generateUUID } from '@/utils'

defineOptions({
    name: 'MetaTextForm'
})

const optionsStore = useOptionsStore()
const rules = reactive({
  textKey: [{ required: true, message: '请填写文本key' }],
  textType: [{ required: true, message: '请选择文本类型' }],
  businessType: [{ required: true, message: '请选择业务类型' }],
  title: [{ required: true, message: '请填写文本标题' }],
  skipUrl: [{ required: true, message: '请填写跳转URL' }],
  content: [{ required: true, message: '请填写文本内容' }],
  enable: [{ required: true, message: '请选择是否启用' }],
})

// Markdown编辑器工具栏配置
const markdownToolbars = {
  bold: true,
  italic: true,
  header: true,
  underline: true,
  strikethrough: true,
  mark: true,
  superscript: true,
  subscript: true,
  quote: true,
  ol: true,
  ul: true,
  link: true,
  imagelink: true,
  code: true,
  table: true,
  fullscreen: true,
  readmodel: true,
  htmlcode: true,
  help: true,
  undo: true,
  redo: true,
  trash: true,
  save: true,
  navigation: true,
  alignleft: true,
  aligncenter: true,
  alignright: true,
  subfield: true,
  preview: true
}

const visible = ref(false)
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
})

// 用于强制刷新Markdown组件
const markdownKey = ref(0)

// 判断当前文本类型
const isMarkdownType = computed(() => {
  return paramsProps.value.row.textType === TextType.MARKDOWN.code;
})

const isRichTextType = computed(() => {
  return paramsProps.value.row.textType === TextType.RICH_TEXT.code;
})

// 文本类型变更处理
const handleTextTypeChange = () => {
  // 可以在这里添加文本类型变更时的处理逻辑
  console.log('文本类型已变更:', paramsProps.value.row.textType);
  if (paramsProps.value.row.textType === TextType.MARKDOWN.code) {
    // 如果切换为markdown类型，将content转换为markdown格式
    const content = paramsProps.value.row.content;
    if (content) {
      // 这里可以添加转换逻辑，例如将富文本转换为markdown
      // 示例：简单替换一些标签，实际项目中可能需要更复杂的转换
      const markdownContent = content.replace(/<[^>]*>/g, '');
      paramsProps.value.row.content = markdownContent;
    }
    // 强制刷新Markdown组件
    markdownKey.value++;
  }
}

// 添加对textType的监视，确保在textType变化时强制刷新Markdown组件
watch(() => paramsProps.value.row.textType, (newType, oldType) => {
  console.log('监听到textType变化:', newType, '原值:', oldType);
  
  // 如果变成了Markdown类型，需要转换内容格式
  if (newType === TextType.MARKDOWN.code && oldType !== TextType.MARKDOWN.code) {
    if (paramsProps.value.row.content) {
      // 在内容非空时执行转换
      const content = paramsProps.value.row.content;
      // 这里简单替换一些HTML标签，实际可能需要更复杂的转换
      // const markdownContent = content.replace(/<[^>]*>/g, '');
      // console.log('将内容转换为Markdown格式:', markdownContent);
      // 先修改内容，再增加key以强制刷新组件
      paramsProps.value.row.content = content;
    }
  }
  
  // 无论如何都刷新组件
  setTimeout(() => {
    markdownKey.value++;
    console.log('已更新markdownKey:', markdownKey.value);
  }, 0);
})

// 自动生成textKey
const generateTextKey = () => {
  const prefix = 'text_';
  const timestamp = new Date().getTime().toString().substring(5);
  const randomStr = generateUUID().substring(0, 8);
  paramsProps.value.row.textKey = `${prefix}${timestamp}_${randomStr}`;
}

// 接收父组件传过来的参数
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

const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params

  // 如果是新增，设置默认启用
  if (params.title.includes('新增')) {
    if (paramsProps.value.row.enable === undefined) {
      paramsProps.value.row.enable = true;
    }
    if (paramsProps.value.row.sort === undefined) {
      paramsProps.value.row.sort = 100;
    }
  }

  // 强制刷新Markdown组件
  markdownKey.value++;
  
  visible.value = true
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
