<template>
  <el-upload
    :drag="drag"
    :multiple="multiple"
    :limit="limit"
    v-model:file-list="fileList"
    :http-request="uploadFileRequest"
    list-type="picture-card"
    :accept="accept"
    :disabled="limit <= fileList.length"
    :on-success="handleSuccess"
  >
    <div class="note-box">
      <el-icon class="upload_icon">
        <UploadFilled />
      </el-icon>
      <div class="el-upload__text">
        <template v-if="drag"> 拖拽或<em>点击上传</em> </template>
        <template v-else> 点击上传 </template>
      </div>
    </div>

    <template #file="{ file }">
      <div class="upload-info-box">
        <el-image class="el-upload-list__item-thumbnail" v-if="type === 'image'" :src="file.url" alt="" loading="lazy">
          <template #error>
            <div class="image-slot">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        <img class="el-upload-list__item-thumbnail" v-if="type === 'image'" :src="file.url" alt="" />
        <span class="upload-type" v-else>
          <el-icon><Document /></el-icon>
        </span>
        <span class="el-upload-list__item-actions">
          <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemove(file)">
            <el-icon><Delete /></el-icon>
          </span>
        </span>
      </div>
    </template>

    <template #tip>
      <div class="el-upload__tip">
        {{ tip }}
      </div>
    </template>
  </el-upload>
</template>

<script setup lang="ts">
import { UploadFilled, Delete, Document, Picture } from '@element-plus/icons-vue';
import { ref, watch, computed } from 'vue';
import { uploadFile } from '@/api/modules/system/admin/upload';
import type { UploadFile, UploadRequestOptions, UploadUserFile } from 'element-plus';
import type { IResultData } from '@/api/interface';
import { ElNotification } from 'element-plus';

defineOptions({
  name: 'SimplifyUpload'
});

// 自定义上传错误接口
interface UploadError {
  status: number;
  method: string;
  url: string;
  message: string;
}

// 定义上传结果接口
interface UploadResult {
  url: string;
  filename: string;
  fileName: string;
  eTag: string;
  objectName: string;
  dirTag: string;
  contextType: string;
  size: number;
  fileId: number;
}

type Props = {
  type?: string;
  tip?: string;
  multiple?: boolean;
  drag?: boolean;
  limit?: number;
  accept?: string;
  dir: string;
  modelValue?: string | string[];
  useOss?: boolean; // 是否使用OSS直传
};

const props = withDefaults(defineProps<Props>(), {
  type: 'image',
  tip: '',
  multiple: false,
  drag: true,
  limit: 1,
  accept: '',
  dir: 'default',
  useOss: false
});

const emits = defineEmits<{
  'update:modelValue': [string | (string | undefined)[]];
}>();

const fileList = ref<UploadUserFile[]>([]);

let defaultVal = props.modelValue;
if (defaultVal !== undefined && defaultVal !== '') {
  if (typeof defaultVal === 'string') {
    defaultVal = [defaultVal];
  }
  defaultVal.forEach((val, index) => {
    const time = new Date().getTime() + index;
    fileList.value.push({
      name: '',
      status: undefined,
      uid: time,
      url: val
    });
  });
}

// 声明generatePreSignedUrl函数
const generatePreSignedUrl = async (fileName: string) => {
  // 这里返回一个模拟的响应，实际项目中应该调用后端API
  return { data: `https://example.com/upload/${fileName}` };
};

/**
 * 使用OSS直传方式上传文件
 * @param options 上传选项
 */
const uploadFileToOss = async (options: UploadRequestOptions) => {
  try {
    // 1. 获取预签名URL
    const { data: preSignedUrl } = await generatePreSignedUrl(options.file.name);

    if (!preSignedUrl) {
      throw new Error('获取预签名URL失败');
    }

    // 2. 使用预签名URL上传文件
    const xhr = new XMLHttpRequest();
    xhr.open('PUT', preSignedUrl, true);

    // 3. 设置上传完成回调
    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        // 构造上传成功的响应数据，从URL中解析文件信息
        const urlObj = new URL(preSignedUrl);
        const path = urlObj.pathname;
        const filename = options.file.name;

        // 构造成功响应，提取OSS URL的基本部分（去掉查询参数）
        const baseUrl = preSignedUrl.split('?')[0];
        const response: IResultData<UploadResult> = {
          code: '0000',
          message: 'SUCCESS',
          data: {
            url: baseUrl,
            filename: filename,
            fileName: filename,
            eTag: xhr.getResponseHeader('ETag') || '',
            objectName: path,
            dirTag: props.dir,
            contextType: options.file.type,
            size: options.file.size,
            fileId: Date.now()
          }
        };

        options.onSuccess(response);
      } else {
        const error: UploadError = {
          status: xhr.status,
          method: 'PUT',
          url: preSignedUrl,
          message: `上传失败: ${xhr.status}`
        };
        options.onError(error as any);
      }
    };

    // 5. 设置上传错误回调
    xhr.onerror = () => {
      const error: UploadError = {
        status: 0,
        method: 'PUT',
        url: preSignedUrl,
        message: '网络错误，上传失败'
      };
      options.onError(error as any);
    };

    // 6. 开始上传
    xhr.send(options.file);

  } catch (error: any) {
    const ajaxError: UploadError = {
      status: 0,
      method: 'PUT',
      url: '',
      message: error.message || '上传失败'
    };
    options.onError(ajaxError as any);
  }
};

// 上传请求处理函数
const uploadFileRequest = (options: UploadRequestOptions) => {
  // 根据 useOss 属性决定使用哪种上传方式
  if (props.useOss) {
    return uploadFileToOss(options);
  }
  // 使用原有上传方式
  return uploadFile({ file: options.file, dirTag: props.dir });
};

const handleSuccess = (res: IResultData<UploadResult>, file: UploadFile) => {
  const { uid } = file;
  const index = fileList.value.findIndex(item => item.uid === uid);
  if (index !== -1) {
    fileList.value[index].url = res.data.url;
    fileList.value[index].name = res.data.filename;
    emitChange();
  }
};

const disabled = ref(false);

const handleRemove = (file: UploadFile) => {
  const { uid } = file;
  const index = fileList.value.findIndex(item => item.uid === uid);
  if (index !== -1) {
    fileList.value.splice(index, 1);
    emitChange();
  }
};

const emitChange = () => {
  if (props.limit > 1) {
    const map = fileList.value.map(item => item.url);
    emits('update:modelValue', map);
  } else {
    emits('update:modelValue', fileList.value[0]?.url || '');
  }
};
</script>

<style scoped lang="scss">
.note-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .upload_icon {
    font-size: 32px;
    margin-bottom: 1px;
  }
}

.upload-info-box {
  width: 100%;

  .upload-type {
    height: 100%;
    display: flex;
    align-items: center;
    flex-direction: column;
    justify-content: center;
    font-size: 24px;
  }
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 30px;
}
</style>
