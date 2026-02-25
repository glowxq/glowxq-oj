<template>
  <div class="upload-box">
    <div class="upload-list" v-if="fileList.length > 0">
      <div v-for="(item, index) in fileList" :key="index" class="upload-item">
        <img :src="item.url || ''" class="upload-image" alt="图片预览" />
        <div class="upload-handle" @click.stop>
          <div v-if="!self_disabled" class="handle-icon" @click="deleteImg(index)">
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </div>
          <div class="handle-icon" @click="previewImg(item.url || '')">
            <el-icon><ZoomIn /></el-icon>
            <span>查看</span>
          </div>
        </div>
      </div>
    </div>
    <el-upload
      v-if="fileList.length < limit || limit === 0"
      :id="uuid"
      action="#"
      :class="['upload', self_disabled ? 'disabled' : '', drag ? 'no-border' : '']"
      :multiple="multiple"
      :disabled="self_disabled"
      :show-file-list="false"
      :http-request="handleHttpUpload"
      :before-upload="beforeAvatarUpload"
      :on-success="uploadSuccess"
      :on-error="uploadError"
      :drag="drag"
      :accept="accept"
    >
      <div class="upload-empty">
        <slot name="empty">
          <el-icon><Plus /></el-icon>
          <span>点击上传</span>
        </slot>
      </div>
    </el-upload>
    <div class="el-upload__tip">
      <slot name="tip" />
    </div>
    <el-image-viewer v-if="imgViewVisible" :url-list="[previewUrl]" @close="imgViewVisible = false" />
  </div>
</template>

<script setup lang="ts" name="UploadImg">
import { ref, computed, inject, watch } from 'vue';
import { generateUUID } from '@/utils';
import { uploadFile, uploadToOssWithPreSignedUrl } from '@/api/modules/system/admin/upload';
import { ElNotification, formContextKey, formItemContextKey } from 'element-plus';
import type { UploadProps, UploadRequestOptions, UploadUserFile } from 'element-plus';
import type { IUploadResult } from '@/api/interface/system/admin/upload';
import { Plus, Delete, ZoomIn, Edit } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

// 定义本地错误类型
interface UploadErrorType {
  status: number;
  method: string;
  url: string;
  message: string;
}

interface UploadFileProps {
  imageUrl?: string; // 单图片地址 (兼容旧版本) ==> 非必传
  fileList?: UploadUserFile[]; // 图片列表 ==> 非必传
  fileInfo?: IUploadResult; // 文件信息 ==> 非必传
  api?: (params: any) => Promise<any>; // 上传图片的 api 方法，一般项目上传都是同一个 api 方法，在组件里直接引入即可 ==> 非必传
  drag?: boolean; // 是否支持拖拽上传 ==> 非必传（默认为 true）
  disabled?: boolean; // 是否禁用上传组件 ==> 非必传（默认为 false）
  fileSize?: number; // 图片大小限制 ==> 非必传（默认为 5M）
  height?: string; // 组件高度 ==> 非必传（默认为 150px）
  width?: string; // 组件宽度 ==> 非必传（默认为 150px）
  borderRadius?: string; // 组件边框圆角 ==> 非必传（默认为 8px）
  dir?: string; // 上传图片的目录 ==> 非必传（默认为 img）
  useOss?: boolean; // 是否使用OSS直传 ==> 非必传（默认为 true）
  draggable?: boolean;
  limit?: number; // 最大上传数量 ==> 非必传（默认为 1，0表示不限制）
  fixedSize?: boolean;
  title?: string;
  accept?: string;
  multiple?: boolean; // 是否支持多选文件 ==> 非必传（默认为 false）
  beforeUpload?: UploadProps['beforeUpload'];
  preView?: boolean;
}

const props = withDefaults(defineProps<UploadFileProps>(), {
  imageUrl: '',
  fileList: () => [],
  draggable: true,
  disabled: false,
  height: '150px',
  width: '150px',
  limit: 1,
  fileSize: 5,
  fixedSize: false,
  title: '上传图片',
  accept: 'image/*',
  multiple: true,
  beforeUpload: () => true,
  preView: true,
  fileInfo: undefined,
  dir: 'default',
  useOss: true
});

// 生成组件唯一id
const uuid = ref('id-' + generateUUID());

// 内部维护的文件列表
const innerFileList = ref<UploadUserFile[]>([]);

// 计算fileList，同时兼容单图片和多图片模式
const fileList = computed<UploadUserFile[]>({
  get: () => {
    // 优先使用props.fileList
    if (props.fileList && props.fileList.length > 0) {
      return props.fileList;
    }
    // 兼容旧版单图片模式
    if (props.imageUrl) {
      return [{
        name: props.imageUrl.split('/').pop() || 'image.jpg',
        url: props.imageUrl
      }];
    }
    return innerFileList.value;
  },
  set: (val) => {
    innerFileList.value = val;
    emit('update:fileList', val);
    // 兼容旧版单图片模式
    if (val.length > 0) {
      emit('update:imageUrl', val[0].url || '');
    } else {
      emit('update:imageUrl', '');
    }
  }
});

// 查看图片
const imgViewVisible = ref(false);
const previewUrl = ref('');

// 预览图片
const previewImg = (url: string) => {
  previewUrl.value = url;
  imgViewVisible.value = true;
};

// 获取 el-form 组件上下文
const formContext = inject(formContextKey, void 0);
// 获取 el-form-item 组件上下文
const formItemContext = inject(formItemContextKey, void 0);
// 判断是否禁用上传和删除
const self_disabled = computed(() => {
  return props.disabled || formContext?.disabled;
});

/**
 * @description 图片上传
 * @param options upload 所有配置项
 * */
const emit = defineEmits<{
  'update:imageUrl': [value: string];
  'update:fileList': [value: UploadUserFile[]];
  change: [value: IUploadResult];
}>();

// 上传进度
const uploadProgress = ref(0);

const handleHttpUpload = async (options: UploadRequestOptions) => {
  try {
    if (props.useOss) {
      // 使用OSS直传
      await uploadToOssWithPreSignedUrl(
        options.file as File,
        options.file.name,
        props.dir,
        (percent) => {
          uploadProgress.value = percent;
          // 这里可以添加上传进度显示逻辑
          options.onProgress?.({ percent } as any);
        },
        (result) => {
          // 添加到文件列表
          const newFile = {
            name: options.file.name,
            url: result?.url || ''
          };
          const newFileList = [...fileList.value, newFile];
          fileList.value = newFileList;

          // 触发change事件
          emit('change', result);
          // 调用 el-form 内部的校验方法
          formItemContext?.prop && formContext?.validateField([formItemContext.prop as string]);
          options.onSuccess(result);
        },
        (error) => {
          options.onError(error as any);
        }
      );
    } else {
      // 使用原有服务器上传
      const { data } = await uploadFile({ file: options.file, dirTag: props.dir });

      // 添加到文件列表
      const newFile = {
        name: options.file.name,
        url: data?.url || ''
      };
      const newFileList = [...fileList.value, newFile];
      fileList.value = newFileList;

      emit('change', data);
      // 调用 el-form 内部的校验方法（可自动校验）
      formItemContext?.prop && formContext?.validateField([formItemContext.prop as string]);
      options.onSuccess(data);
    }
  } catch (error) {
    options.onError(error as any);
  }
};

/**
 * @description 删除图片
 * */
const deleteImg = (index: number) => {
  ElMessageBox.confirm('确定要删除这张图片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const newFileList = [...fileList.value];
    newFileList.splice(index, 1);
    fileList.value = newFileList;
    ElNotification({
      title: '温馨提示',
      message: '图片已删除！',
      type: 'success'
    });
  }).catch(() => {});
};

// 文件类型判断
const isImage = (file: File) => {
  return file.type.startsWith('image/');
};

// 文件上传前钩子
const beforeAvatarUpload = (file: File) => {
  const isCorrectType = isImage(file);
  const isLt5M = file.size / 1024 / 1024 < props.fileSize;

  if (!isCorrectType) {
    ElNotification({
      title: '温馨提示',
      message: '上传图片只能是图片格式！',
      type: 'warning'
    });
    return false;
  }
  if (!isLt5M) {
    ElNotification({
      title: '温馨提示',
      message: `上传图片大小不能超过 ${props.fileSize}M！`,
      type: 'warning'
    });
    return false;
  }

  // 检查上传数量限制
  if (props.limit > 0 && fileList.value.length >= props.limit) {
    ElNotification({
      title: '温馨提示',
      message: `最多只能上传 ${props.limit} 张图片！`,
      type: 'warning'
    });
    return false;
  }

  // 如果有自定义的beforeUpload，也调用它
  if (props.beforeUpload && typeof props.beforeUpload === 'function') {
    // 注意: 这里可能需要将File转换为UploadRawFile，实际使用时可能需要调整
    return props.beforeUpload(file as any);
  }

  return true;
};

/**
 * @description 图片上传成功
 * */
const uploadSuccess = (response: IUploadResult | undefined) => {
  // 重置上传进度
  uploadProgress.value = 0;

  if (response) {
    ElNotification({
      title: '温馨提示',
      message: '图片上传成功！',
      type: 'success'
    });
  }
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
</script>

<style scoped lang="scss">
.is-error {
  .upload {
    :deep(.el-upload),
    :deep(.el-upload-dragger) {
      border: 1px dashed var(--el-color-danger) !important;
      &:hover {
        border-color: var(--el-color-primary) !important;
      }
    }
  }
}
:deep(.disabled) {
  .el-upload,
  .el-upload-dragger {
    cursor: not-allowed !important;
    background: var(--el-disabled-bg-color);
    border: 1px dashed var(--el-border-color-darker) !important;
    &:hover {
      border: 1px dashed var(--el-border-color-darker) !important;
    }
  }
}
.upload-box {
  .upload-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 10px;

    .upload-item {
      position: relative;
      width: v-bind(width);
      height: v-bind(height);
      border: 1px solid var(--el-border-color);
      border-radius: v-bind(borderRadius);
      overflow: hidden;

      &:hover .upload-handle {
        opacity: 1;
      }

      .upload-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
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
            margin-bottom: 40%;
            font-size: 130%;
            line-height: 130%;
          }

          span {
            font-size: 85%;
            line-height: 85%;
          }
        }
      }
    }
  }

  .no-border {
    :deep(.el-upload) {
      border: none !important;
    }
  }
  :deep(.upload) {
    .el-upload {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      width: v-bind(width);
      height: v-bind(height);
      overflow: hidden;
      border: 1px dashed var(--el-border-color-darker);
      border-radius: v-bind(borderRadius);
      transition: var(--el-transition-duration-fast);
      &:hover {
        border-color: var(--el-color-primary);
      }
      .el-upload-dragger {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        padding: 0;
        overflow: hidden;
        background-color: transparent;
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
      .upload-empty {
        position: relative;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        line-height: 30px;
        color: var(--el-color-info);
        .el-icon {
          font-size: 28px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }
  .el-upload__tip {
    line-height: 18px;
    text-align: center;
  }
}
</style>
