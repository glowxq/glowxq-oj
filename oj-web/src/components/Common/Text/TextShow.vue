<template>
  <div class="text-show-container" v-if="textContent || isLoading">
    <div class="text-content" v-loading="isLoading">
      <div 
        v-if="textContent && textContent.content" 
        class="text-display"
        :class="{ 'markdown-content': isMarkdown, 'rich-text-content': isRichText }"
        v-html="displayContent"
      ></div>
      <div v-else-if="!isLoading && props.defaultContent" class="default-content">
        {{ props.defaultContent }}
      </div>
      <div v-else-if="!isLoading" class="no-content">
        <el-icon><Document /></el-icon>
        <span>暂无内容</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { Document } from '@element-plus/icons-vue';
import { getMetaTextByKeyApi } from '@/api/modules/system/meta/metaText';
import { TextType } from '@/enums/system/meta/TextType';

interface Props {
  textKey: string;
  defaultContent?: string;
  showTitle?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  defaultContent: '',
  showTitle: true
});

// 文本内容数据类型定义
interface MetaTextRow {
  id?: number;
  key?: string;
  title?: string;
  content?: string;
  textType?: string;
  description?: string;
  createTime?: string;
  updateTime?: string;
}

const textContent = ref<MetaTextRow | null>(null);
const isLoading = ref(false);

// 判断文本类型
const isMarkdown = computed(() => {
  return textContent.value?.textType === TextType.MARKDOWN.code;
});

const isRichText = computed(() => {
  return textContent.value?.textType === TextType.RICH_TEXT.code;
});

// 处理显示内容
const displayContent = computed(() => {
  if (!textContent.value?.content) {
    return props.defaultContent;
  }

  let content = textContent.value.content;

  // 如果是Markdown，进行简单的转换
  if (isMarkdown.value) {
    content = content
      .replace(/^### (.*$)/gim, '<h3>$1</h3>')
      .replace(/^## (.*$)/gim, '<h2>$1</h2>')
      .replace(/^# (.*$)/gim, '<h1>$1</h1>')
      .replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/gim, '<em>$1</em>')
      .replace(/`(.*?)`/gim, '<code>$1</code>')
      .replace(/\[([^\]]+)\]\(([^)]+)\)/gim, '<a href="$2" target="_blank">$1</a>')
      .replace(/\n\n/gim, '</p><p>')
      .replace(/\n/gim, '<br>');
    
    // 包装段落
    if (content && !content.startsWith('<h') && !content.startsWith('<p>')) {
      content = '<p>' + content + '</p>';
    }
  }

  return content;
});

// 获取文本内容
const fetchTextContent = async () => {
  if (!props.textKey) return;

  try {
    isLoading.value = true;
    const res = await getMetaTextByKeyApi(props.textKey);
    if (res.data) {
      textContent.value = res.data;
    } else {
      textContent.value = null;
    }
  } catch (error) {
    console.error('获取文本内容失败', error);
    textContent.value = null;
  } finally {
    isLoading.value = false;
  }
};

// 监听key变化
watch(() => props.textKey, () => {
  if (props.textKey) {
    fetchTextContent();
  }
}, { immediate: true });

onMounted(() => {
  if (props.textKey) {
    fetchTextContent();
  }
});
</script>

<style scoped lang="scss">
.text-show-container {
  width: 100%;
  
  .text-content {
    min-height: 40px;
    
    .text-display {
      line-height: 1.6;
      color: #333;
      font-size: 14px;
      word-wrap: break-word;
      
      &.markdown-content {
        :deep(h1) {
          font-size: 24px;
          font-weight: 700;
          margin: 16px 0 12px;
          color: #1d1d1f;
          line-height: 1.3;
        }
        
        :deep(h2) {
          font-size: 20px;
          font-weight: 600;
          margin: 14px 0 10px;
          color: #1d1d1f;
          line-height: 1.3;
        }
        
        :deep(h3) {
          font-size: 16px;
          font-weight: 600;
          margin: 12px 0 8px;
          color: #1d1d1f;
          line-height: 1.3;
        }
        
        :deep(p) {
          margin: 8px 0;
          line-height: 1.6;
        }
        
        :deep(strong) {
          font-weight: 600;
          color: #1d1d1f;
        }
        
        :deep(em) {
          font-style: italic;
          color: #666;
        }
        
        :deep(code) {
          background: rgba(0, 0, 0, 0.05);
          padding: 2px 4px;
          border-radius: 4px;
          font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
          font-size: 13px;
        }
        
        :deep(a) {
          color: #007AFF;
          text-decoration: none;
          
          &:hover {
            text-decoration: underline;
          }
        }
        
        :deep(br) {
          line-height: 1.6;
        }
      }
      
      &.rich-text-content {
        :deep(p) {
          margin-bottom: 12px;
          line-height: 1.6;
        }
        
        :deep(ul), :deep(ol) {
          margin: 12px 0;
          padding-left: 20px;
        }
        
        :deep(li) {
          margin-bottom: 6px;
          line-height: 1.5;
        }
        
        :deep(blockquote) {
          border-left: 4px solid #007AFF;
          padding-left: 16px;
          margin: 16px 0;
          color: #666;
          font-style: italic;
        }
      }
    }
    
    .default-content {
      color: #666;
      font-size: 14px;
      line-height: 1.6;
      padding: 12px 0;
    }
    
    .no-content {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      color: #909399;
      font-size: 14px;
      padding: 24px;
      
      .el-icon {
        font-size: 18px;
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .text-show-container {
    .text-content {
      .text-display {
        color: #f5f5f7;
        
        &.markdown-content {
          :deep(h1), :deep(h2), :deep(h3), :deep(strong) {
            color: #f5f5f7;
          }
          
          :deep(em) {
            color: #a1a1a6;
          }
          
          :deep(code) {
            background: rgba(255, 255, 255, 0.1);
            color: #f5f5f7;
          }
          
          :deep(a) {
            color: #0A84FF;
          }
        }
        
        &.rich-text-content {
          :deep(blockquote) {
            border-left-color: #0A84FF;
            color: #a1a1a6;
          }
        }
      }
      
      .default-content {
        color: #a1a1a6;
      }
      
      .no-content {
        color: #a1a1a6;
      }
    }
  }
}
</style> 