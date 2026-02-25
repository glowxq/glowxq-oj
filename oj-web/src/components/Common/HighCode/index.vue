<template>
  <div class="hljs-container">
    <div class="hljs-header" :codetype="title">
      <div class="hljs-copy" @click="handleCopy">
        <el-button type="primary" link @click="handleCopy" :icon="DocumentCopy" class="hljs-copy">
          {{ copyButtonText }}
        </el-button>
      </div>
    </div>
    <div class="hljs-wrapper" v-code>
      <highlightjs :language="language" :autodetect="false" :code="code" />
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { DocumentCopy } from '@element-plus/icons-vue';
import vCode from './line';

defineOptions({
  name: 'HighCode'
});
const props = withDefaults(defineProps<HighCodeProps>(), {
  language: 'java',
  code: '',
  title: ''
});
const copyButtonText = ref<string>('复制');

export interface HighCodeProps {
  language: string; // 语言
  code: string; // 要填充的代码块
  title: string; // 要展示的title
}

// 复制到剪贴板方法
const handleCopy = async () => {
  try {
    if (navigator.clipboard) {
      // 安全域
      await navigator.clipboard.writeText(props.code);
    } else {
      // 非安全域，回退到 document.execCommand
      const tempTextarea = document.createElement('textarea');
      tempTextarea.value = props.code;
      document.body.appendChild(tempTextarea);
      tempTextarea.select();
      document.execCommand('copy');
      document.body.removeChild(tempTextarea);
    }
    copyButtonText.value = '复制成功！';
    setTimeout(() => {
      copyButtonText.value = '复制';
    }, 2000); // 2000 毫秒后还原为 '复制'
  } catch (err) {
    console.error('复制操作不被支持或失败: ', err);
  }
};
</script>

<style lang="scss" scoped></style>
