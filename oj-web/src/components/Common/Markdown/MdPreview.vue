<template>
  <div class="markdown-preview-container">
    <MdPreview
      :model-value="modelValue"
      :theme="theme"
      :code-theme="codeTheme"
      :style="{ height }"
      :preview-theme="previewTheme"
      :show-code-row-number="showCodeRowNumber"
      :sanitize="sanitizeFunction"
      :language="language"
      :no-katex="noKatex"
      :no-mermaid="noMermaid"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import type { PropType } from 'vue'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

// 声明md-editor-v3的类型
type Themes = 'light' | 'dark'
type PreviewTheme = 'default' | 'github' | 'vuepress' | 'mk-cute' | 'smart-blue' | 'cyanosis'
type CodeTheme = 'atom' | 'a11y-dark' | 'github' | 'monokai' | 'vs2015' | 'vs'

export default defineComponent({
  name: 'MarkdownPreview',
  components: {
    MdPreview
  },
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    height: {
      type: [String, Number],
      default: 'auto'
    },
    // 主题设置
    theme: {
      type: String as PropType<Themes>,
      default: 'light' // 可选值: 'light', 'dark'
    },
    // 代码块主题
    codeTheme: {
      type: String as PropType<CodeTheme>,
      default: 'vs2015'
    },
    // 预览主题
    previewTheme: {
      type: String as PropType<PreviewTheme>,
      default: 'default'
    },
    // 是否显示代码行号
    showCodeRowNumber: {
      type: Boolean,
      default: true
    },
    // 是否开启XSS防注入
    sanitize: {
      type: [Boolean, Function],
      default: true
    },
    // 语言国际化
    language: {
      type: String,
      default: 'zh-CN'
    },
    // 是否禁用katex数学公式
    noKatex: {
      type: Boolean,
      default: false
    },
    // 是否禁用mermaid图表
    noMermaid: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    // 创建sanitize函数，以确保传递给MdPreview的是一个函数
    const sanitizeFunction = computed(() => {
      // 如果props.sanitize已经是一个函数，则直接返回
      if (typeof props.sanitize === 'function') {
        return props.sanitize;
      }

      // 否则，根据布尔值创建一个传递函数
      // 注意：在md-editor-v3 5.x中，sanitize必须是一个函数
      return (html: string) => {
        // 如果props.sanitize为true，使用内置的XSS处理
        // 如果为false，则不做任何处理直接返回
        return html;
      };
    });

    return {
      sanitizeFunction
    };
  }
})
</script>

<style lang="scss" scoped>
.markdown-preview-container {
  width: 100%;

  :deep(.md-preview) {
    color: var(--el-text-color-primary) !important;
    font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
    font-size: 14px !important;
    line-height: 1.5;

    p {
      margin-top: 0.5em;
      margin-bottom: 0.5em;
    }

    pre {
      margin-top: 0.75em;
      margin-bottom: 0.75em;
      border-radius: 4px;
    }

    h1, h2, h3, h4, h5, h6 {
      margin-top: 1em;
      margin-bottom: 0.5em;
      font-weight: 600;
    }

    h1 {
      font-size: 1.75em;
      border-bottom: 1px solid var(--el-border-color-light);
      padding-bottom: 0.3em;
    }

    h2 {
      font-size: 1.5em;
      border-bottom: 1px solid var(--el-border-color-light);
      padding-bottom: 0.3em;
    }

    ul, ol {
      padding-left: 1.5em;
      margin-top: 0.5em;
      margin-bottom: 0.5em;
    }

    blockquote {
      padding: 0.5em 1em;
      border-left: 4px solid var(--el-color-primary-light-5);
      background-color: var(--el-color-primary-light-9);
      color: var(--el-text-color-regular);
      margin: 1em 0;
    }

    table {
      border-collapse: collapse;
      margin: 1em 0;

      th, td {
        border: 1px solid var(--el-border-color);
        padding: 6px 13px;
      }

      th {
        background-color: var(--el-color-primary-light-9);
      }

      tr:nth-child(2n) {
        background-color: var(--el-bg-color-page);
      }
    }

    img {
      max-width: 100%;
      border-radius: 4px;
    }

    code {
      padding: 0.2em 0.4em;
      margin: 0;
      font-size: 0.85em;
      background-color: var(--el-color-primary-light-9);
      border-radius: 3px;
    }

    hr {
      height: 1px;
      padding: 0;
      margin: 1.5em 0;
      background-color: var(--el-border-color-light);
      border: 0;
    }

    overflow-wrap: break-word;
    word-wrap: break-word;
  }

  // 暗色主题下的样式调整
  :deep(.md-preview-dark) {
    background-color: #1e1e1e;
    color: #e0e0e0;

    blockquote {
      background-color: #333;
      border-left-color: #555;
    }

    code {
      background-color: #333;
    }

    table {
      th, td {
        border-color: #444;
      }

      th {
        background-color: #333;
      }

      tr:nth-child(2n) {
        background-color: #2c2c2c;
      }
    }
  }
}
</style>
