<template>
  <div class="upload-box">
    <el-upload
      v-model:file-list="_fileList"
      action="#"
      list-type="picture-card"
      :class="['upload', self_disabled ? 'disabled' : '', drag ? 'no-border' : '']"
      :multiple="true"
      :disabled="self_disabled"
      :limit="limit"
      :http-request="handleHttpUpload"
      :before-upload="beforeUpload"
      :on-exceed="handleExceed"
      :on-success="uploadSuccess"
      :on-error="uploadError"
      :drag="drag"
      :accept="fileType.join(',')"
    >
      <div class="upload-empty">
        <slot name="empty">
          <el-icon><Plus /></el-icon>
        </slot>
      </div>
      <template #file="{ file }">
        <img :src="file.url" class="upload-image" alt="预览图片" />
        <div class="upload-handle" @click.stop>
          <div class="handle-icon" @click="handlePictureCardPreview(file)">
            <el-icon><ZoomIn /></el-icon>
            <span>查看</span>
          </div>
          <div v-if="!self_disabled" class="handle-icon" @click="handleRemove(file)">
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </div>
        </div>
      </template>
    </el-upload>
    <div class="el-upload__tip">
      <slot name="tip" />
    </div>
    <el-image-viewer v-if="imgViewVisible" :url-list="[viewImageUrl]" @close="imgViewVisible = false" />
  </div>
</template>

<script setup lang="ts" name="UploadImgs">
import { ref, computed, inject, watch } from 'vue';
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue';
import { uploadFile, uploadToOssWithPreSignedUrl } from '@/api/modules/system/admin/upload';
import type { UploadProps, UploadFile, UploadUserFile, UploadRequestOptions } from 'element-plus';
import { ElNotification, formContextKey, formItemContextKey } from 'element-plus';
import type { IUploadResult } from '@/api/interface/system/admin/upload';

interface UploadFileProps {
  fileList: UploadUserFile[];
  fileInfo?: IUploadResult; // 文件信息 ==> 非必传
  api?: (params: any) => Promise<any>; // 上传图片的 api 方法，一般项目上传都是同一个 api 方法，在组件里直接引入即可 ==> 非必传
  drag?: boolean; // 是否支持拖拽上传 ==> 非必传（默认为 true）
  disabled?: boolean; // 是否禁用上传组件 ==> 非必传（默认为 false）
  limit?: number; // 最大图片上传数 ==> 非必传（默认为 5张）
  fileSize?: number; // 图片大小限制 ==> 非必传（默认为 5M）
  fileType?: File.ImageMimeType[]; // 图片类型限制 ==> 非必传（默认为 ["image/jpeg", "image/png", "image/gif"]）
  height?: string; // 组件高度 ==> 非必传（默认为 150px）
  width?: string; // 组件宽度 ==> 非必传（默认为 150px）
  borderRadius?: string; // 组件边框圆角 ==> 非必传（默认为 8px）
  dir?: string; // 上传图片的目录 ==> 非必传（默认为 img）
  useOss?: boolean; // 是否使用OSS直传 ==> 非必传（默认为 false）
}

const props = withDefaults(defineProps<UploadFileProps>(), {
  fileList: () => [],
  fileInfo: undefined,
  drag: true,
  disabled: false,
  limit: 5,
  fileSize: 5,
  fileType: () => ['image/jpeg', 'image/png', 'image/gif'],
  height: '150px',
  width: '150px',
  borderRadius: '8px',
  dir: 'default',
  useOss: true
});

// 获取 el-form 组件上下文
const formContext = inject(formContextKey, void 0);
// 获取 el-form-item 组件上下文
const formItemContext = inject(formItemContextKey, void 0);
// 判断是否禁用上传和删除
const self_disabled = computed(() => {
  return props.disabled || formContext?.disabled;
});

const _fileList = ref<UploadUserFile[]>(props.fileList);

// 监听 props.fileList 列表默认值改变
watch(
  () => props.fileList,
  (n: UploadUserFile[]) => {
    _fileList.value = n;
  }
);

// 存储每个文件的上传进度
const uploadProgressMap = ref<Map<string, number>>(new Map());

// 定义本地错误类型
interface UploadErrorType {
  status: number;
  method: string;
  url: string;
  message: string;
}

/**
 * @description 文件上传之前判断
 * @param rawFile 选择的文件
 * */
const beforeUpload: UploadProps['beforeUpload'] = rawFile => {
  const imgSize = rawFile.size / 1024 / 1024 < props.fileSize;
  const imgType = props.fileType.includes(rawFile.type as File.ImageMimeType);
  if (!imgType)
    ElNotification({
      title: '温馨提示',
      message: '上传图片不符合所需的格式！',
      type: 'warning'
    });
  if (!imgSize)
    setTimeout(() => {
      ElNotification({
        title: '温馨提示',
        message: `上传图片大小不能超过 ${props.fileSize}M！`,
        type: 'warning'
      });
    }, 0);
  return imgType && imgSize;
};

/**
 * @description 图片上传
 * @param options upload 所有配置项
 * */
const handleHttpUpload = async (options: UploadRequestOptions) => {
  try {
    // 为每个上传生成唯一ID
    const uploadId = Date.now().toString() + Math.random().toString(36).substring(2, 9);
    uploadProgressMap.value.set(uploadId, 0);

    if (props.useOss) {
      // 使用OSS直传
      await uploadToOssWithPreSignedUrl(
        options.file as File,
        options.file.name,
        props.dir,
        (percent) => {
          // 更新上传进度
          uploadProgressMap.value.set(uploadId, percent);
          options.onProgress?.({ percent } as any);
        },
        (result) => {
          // 上传成功
          uploadProgressMap.value.delete(uploadId);
          options.onSuccess(result);
        },
        (error) => {
          // 上传失败
          uploadProgressMap.value.delete(uploadId);
          options.onError(error as any);
        }
      );
    } else {
      // 使用原有服务器上传
      const { data } = await uploadFile({ file: options.file, dirTag: props.dir });
      uploadProgressMap.value.delete(uploadId);
      options.onSuccess(data);
    }
  } catch (error) {
    options.onError(error as any);
  }
};

/**
 * @description 图片上传成功
 * @param response 上传响应结果
 * @param uploadFile 上传的文件
 * */
const emit = defineEmits<{
  'update:fileList': [value: UploadUserFile[]];
  change: [value: IUploadResult];
}>();
const uploadSuccess = (response: IUploadResult | undefined, uploadFile: UploadFile) => {
  if (!response) return;
  uploadFile.url = response.url;
  emit('update:fileList', _fileList.value);
  emit('change', response);
  // 调用 el-form 内部的校验方法（可自动校验）
  formItemContext?.prop && formContext?.validateField([formItemContext.prop as string]);
  ElNotification({
    title: '温馨提示',
    message: '图片上传成功！',
    type: 'success'
  });
};

/**
 * @description 删除图片
 * @param file 删除的文件
 * */
const handleRemove = (file: UploadFile) => {
  console.log('handleRemove', file);
  _fileList.value = _fileList.value.filter(item => item.url !== file.url);
  emit('update:fileList', _fileList.value);
  emit('change', undefined as any);
};

/**
 * @description 图片上传错误
 * */
const uploadError = () => {
  ElNotification({
    title: '温馨提示',
    message: '图片上传失败，请您重新上传！',
    type: 'error'
  });
};

/**
 * @description 文件数超出
 * */
const handleExceed = () => {
  ElNotification({
    title: '温馨提示',
    message: `当前最多只能上传 ${props.limit} 张图片，请移除后上传！`,
    type: 'warning'
  });
};

/**
 * @description 图片预览
 * @param file 预览的文件
 * */
const viewImageUrl = ref('');
const imgViewVisible = ref(false);
const handlePictureCardPreview: UploadProps['onPreview'] = file => {
  viewImageUrl.value = file.url!;
  imgViewVisible.value = true;
};
</script>

<style scoped lang="scss">
.is-error {
  .upload {
    :deep(.el-upload--picture-card),
    :deep(.el-upload-dragger) {
      border: 1px dashed var(--el-color-danger) !important;
      &:hover {
        border-color: var(--el-color-primary) !important;
      }
    }
  }
}
:deep(.disabled) {
  .el-upload--picture-card,
  .el-upload-dragger {
    cursor: not-allowed;
    background: var(--el-disabled-bg-color) !important;
    border: 1px dashed var(--el-border-color-darker);
    &:hover {
      border-color: var(--el-border-color-darker) !important;
    }
  }
}
.upload-box {
  .no-border {
    :deep(.el-upload--picture-card) {
      border: none !important;
    }
  }
  :deep(.upload) {
    .el-upload-dragger {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
      padding: 0;
      overflow: hidden;
      border: 1px dashed var(--el-border-color-darker);
      border-radius: v-bind(borderRadius);
      &:hover {
        border: 1px dashed var(--el-color-primary);
      }
    }
    .el-upload-dragger.is-dragover {
      background-color: var(--el-color-primary-light-9);
      border: 2px dashed var(--el-color-primary) !important;
    }
    .el-upload-list__item,
    .el-upload--picture-card {
      width: v-bind(width);
      height: v-bind(height);
      background-color: transparent;
      border-radius: v-bind(borderRadius);
    }
    .upload-image {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
    .upload-handle {
      position: absolute;
      top: 0;
      right: 0;
      box-sizing: border-box;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
      cursor: pointer;
      background: rgb(0 0 0 / 60%);
      opacity: 0;
      transition: var(--el-transition-duration-fast);
      .handle-icon {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 0 6%;
        color: aliceblue;
        .el-icon {
          margin-bottom: 15%;
          font-size: 140%;
        }
        span {
          font-size: 100%;
        }
      }
    }
    .el-upload-list__item {
      &:hover {
        .upload-handle {
          opacity: 1;
        }
      }
    }
    .upload-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      font-size: 12px;
      line-height: 30px;
      color: var(--el-color-info);
      .el-icon {
        font-size: 28px;
        color: var(--el-text-color-secondary);
      }
    }
  }
  .el-upload__tip {
    line-height: 15px;
    text-align: center;
  }
}
</style>
