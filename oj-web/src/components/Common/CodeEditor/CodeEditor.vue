<template>
  <div class="code-editor-container">
    <!-- 工具栏 -->
    <div class="editor-toolbar">
      <div class="toolbar-main-section">
        <el-button
          class="collapse-button"
          type="text"
          size="small"
          @click="isToolbarExpanded = !isToolbarExpanded"
        >
          <el-icon>
            <component :is="isToolbarExpanded ? 'ArrowUp' : 'ArrowDown'" />
          </el-icon>
          {{ isToolbarExpanded ? "收起选项" : "展开选项" }}
        </el-button>

        <div class="toolbar-group action-group">
          <slot name="custom-actions"></slot>

          <!-- 添加计时器 -->
          <div class="timer-container">
            <div class="timer-display">{{ formattedTime }}</div>
            <el-button class="timer-button" plain @click="resetTimer">
              <el-icon class="timer-icon"><Timer /></el-icon>
            </el-button>
          </div>

          <el-button-group class="action-buttons">
            <el-button plain size="small" type="primary" @click="saveCode">
              <el-icon><Upload /></el-icon>
              保存
            </el-button>

            <el-button 
              plain 
              size="small" 
              @click="copyCode"
              :class="{
                'copy-button-success': copyStatus === 'success',
                'copy-button-error': copyStatus === 'error',
                'copy-button-copying': copyStatus === 'copying'
              }"
              :disabled="copyStatus === 'copying'"
            >
              <el-icon v-if="copyStatus === 'copying'"><Loading /></el-icon>
              <el-icon v-else-if="copyStatus === 'success'"><CircleCheck /></el-icon>
              <el-icon v-else-if="copyStatus === 'error'"><CircleClose /></el-icon>
              <el-icon v-else><CopyDocument /></el-icon>
              <span v-if="copyStatus === 'copying'">复制中</span>
              <span v-else-if="copyStatus === 'success'">已复制</span>
              <span v-else-if="copyStatus === 'error'">复制失败</span>
              <span v-else>复制</span>
            </el-button>

            <el-button plain size="small" type="danger" @click="clearEditor">
              <el-icon><Delete /></el-icon>
              清空
            </el-button>
          </el-button-group>

          <!-- 额外选项 -->
          <el-dropdown trigger="click" size="small">
            <el-button plain size="small">
              <el-icon class="el-icon--left"><MoreFilled /></el-icon>
              更多
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="toggleReadOnly">
                  <el-icon><component :is="readOnly ? 'Lock' : 'Unlock'" /></el-icon>
                  {{ readOnly ? "允许编辑" : "只读模式" }}
                </el-dropdown-item>
                <el-dropdown-item @click="toggleLineNumbers">
                  <el-icon><component :is="showLineNumbers ? 'Hide' : 'View'" /></el-icon>
                  {{ showLineNumbers ? "隐藏行号" : "显示行号" }}
                </el-dropdown-item>
                <el-dropdown-item @click="toggleLineWrapping">
                  <el-icon
                    ><component :is="lineWrapping ? 'ScaleToOriginal' : 'Operation'"
                  /></el-icon>
                  {{ lineWrapping ? "禁用自动换行" : "启用自动换行" }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <transition name="fade">
        <div v-if="isToolbarExpanded" class="collapsible-section">
          <div class="toolbar-group language-group">
            <span class="group-label">语言</span>
            <el-select
              v-model="currentLanguage"
              placeholder="选择语言"
              @change="changeLanguage"
              size="small"
              popper-class="code-editor-select"
            >
              <el-option
                v-for="lang in supportedLanguages"
                :key="lang.value"
                :label="lang.label"
                :value="lang.value"
              />
            </el-select>

            <span class="group-label">主题</span>
            <el-select
              v-model="currentTheme"
              placeholder="选择主题"
              @change="changeTheme"
              size="small"
              popper-class="code-editor-select"
            >
              <el-option label="明亮模式" value="light" />
              <el-option label="暗黑模式" value="dark" />
            </el-select>
          </div>

          <el-divider direction="vertical" class="divider" />

          <div class="toolbar-group format-group">
            <span class="group-label">格式</span>
            <el-tooltip content="调整字体大小">
              <div class="font-size-control">
                <span class="control-label">字体</span>
                <el-button-group>
                  <el-button
                    size="small"
                    @click="decreaseFontSize"
                    :disabled="fontSize <= 12"
                  >
                    <el-icon><Minus /></el-icon>
                  </el-button>
                  <el-button
                    size="small"
                    @click="increaseFontSize"
                    :disabled="fontSize >= 24"
                  >
                    <el-icon><Plus /></el-icon>
                  </el-button>
                </el-button-group>
                <span class="font-size-display">{{ fontSize }}px</span>
              </div>
            </el-tooltip>

            <el-tooltip content="设置Tab缩进宽度">
              <div class="tab-size-control">
                <span class="control-label">Tab宽度</span>
                <el-select
                  v-model="tabSize"
                  placeholder="Tab宽度"
                  @change="changeTabSize"
                  size="small"
                  popper-class="code-editor-select"
                >
                  <el-option
                    v-for="size in [2, 4, 8]"
                    :key="size"
                    :label="`${size} 空格`"
                    :value="size"
                  />
                </el-select>
              </div>
            </el-tooltip>
          </div>

          <!-- 高亮行输入 -->
          <div class="highlight-line-control">
            <el-input
              v-model="highlightLineInput"
              placeholder="行号高亮 (例如：1,3-5)"
              size="small"
              @change="applyHighlightLines"
            >
              <template #prepend><span class="highlight-label">高亮</span></template>
              <template #append>
                <el-button type="primary" size="small" @click="applyHighlightLines"
                  >应用</el-button
                >
              </template>
            </el-input>
          </div>
        </div>
      </transition>
    </div>

    <!-- 编辑器主体 -->
    <div
      ref="editorRef"
      class="editor-body"
      :style="{ '--editor-font-size': fontSize + 'px' }"
    ></div>

    <!-- 添加代码覆盖弹窗 - 使用Diff视图 -->
    <el-dialog
      v-model="showCodePushDialog"
      width="70%"
      top="5vh"
      class="code-push-dialog"
      destroy-on-close
    >
      <template #header>
        <div class="code-push-dialog-header">
          <el-icon class="dialog-icon"><Message /></el-icon>
          <span>代码推送通知</span>
        </div>
      </template>

      <div class="code-push-container">
        <div class="code-push-message">
          <el-alert
            type="info"
            :closable="false"
            show-icon
          >
            <template #title>
              <span class="push-message">{{ pushMessage }}</span>
            </template>
          </el-alert>
        </div>

        <div class="code-diff-container">
          <div class="diff-header">
            <div class="diff-title left">当前代码</div>
            <div class="diff-title right">推送代码</div>
          </div>
          <div class="diff-content" v-html="diffResult"></div>
        </div>
      </div>

      <template #footer>
        <div class="code-push-dialog-footer">
          <el-button @click="showCodePushDialog = false">取消</el-button>
          <el-button type="primary" @click="applyPushedCode">接受覆盖</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick, computed, onBeforeUnmount } from "vue";
import { EditorState } from "@codemirror/state";
import type { Extension } from "@codemirror/state";
import { StateEffect, Compartment } from "@codemirror/state";
import {
  EditorView,
  keymap,
  lineNumbers,
  highlightActiveLine,
  highlightSpecialChars,
  Decoration,
} from "@codemirror/view";
import type { DecorationSet } from "@codemirror/view";
import {
  defaultKeymap,
  indentWithTab,
  history,
  historyKeymap,
  indentSelection,
} from "@codemirror/commands";
import {
  indentOnInput,
  syntaxHighlighting,
  defaultHighlightStyle,
  bracketMatching,
  foldGutter,
  indentUnit,
} from "@codemirror/language";
import { oneDark } from "@codemirror/theme-one-dark";
import {
  autocompletion,
  completionKeymap,
  type CompletionContext,
  type CompletionResult,
  type Completion
} from "@codemirror/autocomplete";
import { lintKeymap } from "@codemirror/lint";
import { searchKeymap } from "@codemirror/search";
import { ElMessage, ElMessageBox, ElDialog, ElTabs, ElTabPane, ElAlert } from "element-plus";
import { useSocketStore } from "@/stores/modules/socket";
import { WebsocketBusinessType } from "@/enums/oj/websocket/BusinessType";
import { createCodeRecordApi } from "@/api/modules/oj/code/codeRecord";
import { Message } from '@element-plus/icons-vue';
import mittBus from '@/utils/mittBus';
import * as Diff from 'diff';

// 引入语言支持
import { javascript } from "@codemirror/lang-javascript";
import { python } from "@codemirror/lang-python";
import { java } from "@codemirror/lang-java";
import { cpp } from "@codemirror/lang-cpp";
import { rust } from "@codemirror/lang-rust";
import { LanguageSupport, StreamLanguage } from "@codemirror/language";
import { go } from "@codemirror/legacy-modes/mode/go";

// 引入语言枚举
import { ProgramLanguage, getSupportedLanguages, getEditorLanguage, normalizeProgramLanguage } from "@/enums/oj/common/ProgramLanguage";

// 引入Element Plus图标
import {
  Minus,
  Plus,
  Delete,
  ArrowDown,
  ArrowUp,
  Hide,
  View,
  Lock,
  Unlock,
  ScaleToOriginal,
  Operation,
  MoreFilled,
  Timer,
  CopyDocument,
  Upload,
  Loading,
  CircleCheck,
  CircleClose,
} from "@element-plus/icons-vue";

// 定义组件属性
const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  language: {
    type: String,
    default: ProgramLanguage.JAVASCRIPT,
  },
  readOnlyProp: {
    type: Boolean,
    default: false,
  },
  theme: {
    type: String,
    default: "light",
  },
  defaultFontSize: {
    type: Number,
    default: 14,
  },
  defaultTabSize: {
    type: Number,
    default: 2,
  },
  defaultCodeMode: {
    type: String ,
    default: null,
  },
});

// 定义事件
const emit = defineEmits([
  "update:modelValue",
  "change",
  "language-change",
  "theme-change",
]);

// 编辑器相关状态
const editorRef = ref<HTMLElement | null>(null);
const currentLanguage = ref(ProgramLanguage.JAVASCRIPT);
const currentTheme = ref(props.theme);
const readOnly = ref(props.readOnlyProp);
const showLineNumbers = ref(true);
const lineWrapping = ref(true);
const fontSize = ref(props.defaultFontSize);
const tabSize = ref(props.defaultTabSize);
// 高亮行输入
const highlightLineInput = ref("");
const highlightedLines = ref<number[]>([]);
const isToolbarExpanded = ref(false);

// 复制状态
type CopyStatus = 'idle' | 'copying' | 'success' | 'error';
const copyStatus = ref<CopyStatus>('idle');
const copyStatusTimer = ref<number | null>(null);

// 计时器相关状态
const timerStartTime = ref<number>(Date.now());
const timerRunning = ref<boolean>(false);
const timerElapsed = ref<number>(0);
const timerInterval = ref<number | null>(null);

// 格式化时间显示 (mm:ss)
const formattedTime = computed(() => {
  const totalSeconds = Math.floor(timerElapsed.value / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
});

// 支持的语言列表
const supportedLanguages = getSupportedLanguages();

// CodeMirror编辑器实例
let view: EditorView | null = null;

// 创建Compartments用于可热替换的配置
const languageConf = new Compartment();
const themeConf = new Compartment();
const tabSizeConf = new Compartment();
const readOnlyConf = new Compartment();
const lineNumbersConf = new Compartment();
const lineWrappingConf = new Compartment();
const highlightLinesConf = new Compartment();

// 为C/C++添加代码片段补全
const cppSnippets: Completion[] = [
  { label: "for", type: "keyword", detail: "循环", apply: "for (int i = 0; i < n; i++) {\n\t\n}" },
  { label: "while", type: "keyword", detail: "循环", apply: "while (condition) {\n\t\n}" },
  { label: "if", type: "keyword", detail: "条件", apply: "if (condition) {\n\t\n}" },
  { label: "else", type: "keyword", detail: "条件", apply: "else {\n\t\n}" },
  { label: "else if", type: "keyword", detail: "条件", apply: "else if (condition) {\n\t\n}" },
  { label: "switch", type: "keyword", detail: "条件", apply: "switch (expression) {\n\tcase value:\n\t\t\n\t\tbreak;\n\tdefault:\n\t\t\n\t\tbreak;\n}" },
  { label: "struct", type: "keyword", detail: "类型", apply: "struct Name {\n\t\n};" },
  { label: "class", type: "keyword", detail: "类型", apply: "class Name {\nprivate:\n\t\npublic:\n\t\n};" },
  { label: "include", type: "keyword", detail: "预处理", apply: "#include <>" },
  { label: "define", type: "keyword", detail: "预处理", apply: "#define " },
  { label: "main", type: "function", detail: "主函数", apply: "int main(int argc, char *argv[]) {\n\t\n\treturn 0;\n}" },
  { label: "cout", type: "function", detail: "输出", apply: "std::cout << ${}; << std::endl" },
  { label: "cin", type: "function", detail: "输入", apply: "std::cin >> ${};" },
  { label: "vector", type: "type", detail: "容器", apply: "std::vector<${type}> ${name};" },
  { label: "map", type: "type", detail: "容器", apply: "std::map<${keyType}, ${valueType}> ${name};" },
];

// 为Python添加代码片段补全
const pythonSnippets: Completion[] = [
  { label: "for", type: "keyword", detail: "循环", apply: "for i in range():\n\t" },
  { label: "while", type: "keyword", detail: "循环", apply: "while condition:\n\t" },
  { label: "if", type: "keyword", detail: "条件", apply: "if condition:\n\t" },
  { label: "elif", type: "keyword", detail: "条件", apply: "elif condition:\n\t" },
  { label: "else", type: "keyword", detail: "条件", apply: "else:\n\t" },
  { label: "def", type: "keyword", detail: "函数", apply: "def function_name():\n\t" },
  { label: "class", type: "keyword", detail: "类", apply: "class ClassName:\n\tdef __init__(self):\n\t\t" },
  { label: "import", type: "keyword", detail: "导入", apply: "import " },
  { label: "from", type: "keyword", detail: "导入", apply: "from module import name" },
  { label: "print", type: "function", detail: "输出", apply: "print(${value})" },
  { label: "input", type: "function", detail: "输入", apply: "input(${prompt})" },
  { label: "list", type: "type", detail: "容器", apply: "list(${iterable})" },
  { label: "dict", type: "type", detail: "容器", apply: "dict(${items})" },
  { label: "set", type: "type", detail: "容器", apply: "set(${items})" },
  { label: "tuple", type: "type", detail: "容器", apply: "tuple(${items})" },
  { label: "with", type: "keyword", detail: "上下文", apply: "with open(${file}, '${mode}') as f:\n\t" },
  { label: "try", type: "keyword", detail: "异常", apply: "try:\n\t\n\nexcept Exception as e:\n\t" },
];

// 创建自定义补全提供程序
const createLanguageCompletions = (snippets: Completion[]) => {
  return (context: CompletionContext): CompletionResult | null => {
    const word = context.matchBefore(/\w*/);
    if (!word || word.from === word.to && !context.explicit) return null;

    return {
      from: word.from,
      options: snippets
    };
  };
};

// 创建自定义主题
const createTheme = (dark: boolean): Extension => {
  return EditorView.theme(
    {
      "&": {
        height: "100%",
        fontSize: "var(--editor-font-size)",
      },
      ".cm-content": {
        fontFamily: '"JetBrains Mono", "Fira Code", Consolas, monospace',
        padding: "8px 0",
      },
      ".cm-gutters": {
        backgroundColor: dark ? "#21252b" : "#f5f5f5",
        color: dark ? "#676f7d" : "#999",
        border: "none",
      },
      ".cm-activeLineGutter": {
        backgroundColor: dark ? "#2c313a" : "#e8f2ff",
      },
      ".cm-highlighted-line": {
        backgroundColor: dark ? "rgba(220, 38, 38, 0.15)" : "rgba(220, 38, 38, 0.12)",
        borderLeft: "2px solid rgba(220, 38, 38, 0.6)",
      },
      ".cm-foldPlaceholder": {
        backgroundColor: dark ? "#374151" : "#e5e7eb",
        color: dark ? "#d1d5db" : "#6b7280",
      },
    },
    { dark }
  );
};

// 自定义高亮行扩展
const highlightedLineDecoration = Decoration.line({
  attributes: { class: "cm-highlighted-line" },
});

const highlightLines = (view: EditorView, lines: number[]): DecorationSet => {
  const decorations = [];
  const doc = view.state.doc;

  for (const line of lines) {
    if (line <= 0 || line > doc.lines) continue;
    const pos = doc.line(line);
    decorations.push(highlightedLineDecoration.range(pos.from));
  }

  return Decoration.set(decorations);
};

// 自定义高亮行扩展
const createHighlightExtension = (lines: number[]): Extension => {
  return EditorView.decorations.of((view) => {
    return highlightLines(view, lines);
  });
};

// 解析高亮行输入（支持1,3-5这样的格式）
const parseHighlightLines = (input: string): number[] => {
  if (!input.trim()) return [];

  const lines: number[] = [];
  const parts = input.split(",");

  for (const part of parts) {
    if (part.includes("-")) {
      const [start, end] = part.split("-").map((n) => parseInt(n.trim()));
      if (!isNaN(start) && !isNaN(end)) {
        for (let i = start; i <= end; i++) {
          lines.push(i);
        }
      }
    } else {
      const line = parseInt(part.trim());
      if (!isNaN(line)) {
        lines.push(line);
      }
    }
  }

  return [...new Set(lines)]; // 去重
};

// 应用高亮行
const applyHighlightLines = () => {
  highlightedLines.value = parseHighlightLines(highlightLineInput.value);
  if (view) {
    view.dispatch({
      effects: highlightLinesConf.reconfigure(
        createHighlightExtension(highlightedLines.value)
      ),
    });
  }
};

// 获取语言扩展，添加语言特定的补全
const getLanguageExtension = (): Extension => {
  switch (currentLanguage.value) {
    case ProgramLanguage.JAVASCRIPT:
      return javascript();
    case ProgramLanguage.TYPESCRIPT:
      return javascript({ typescript: true });
    case ProgramLanguage.PYTHON:
      return [
        python(),
        autocompletion({ override: [createLanguageCompletions(pythonSnippets)] })
      ];
    case ProgramLanguage.JAVA:
      return java();
    case ProgramLanguage.CPP:
      return [
        cpp(),
        autocompletion({ override: [createLanguageCompletions(cppSnippets)] })
      ];
    case ProgramLanguage.RUST:
      return rust();
    case ProgramLanguage.GOLANG:
      return new LanguageSupport(StreamLanguage.define(go));
    default:
      return javascript();
  }
};

// 获取基本扩展
const getBaseExtensions = (): Extension[] => {
  const extensions: Extension[] = [
    history(),
    highlightSpecialChars(),
    indentOnInput(),
    bracketMatching(),
    autocompletion(),
    highlightActiveLine(),
    foldGutter(),
    languageConf.of(getLanguageExtension()),
    tabSizeConf.of(indentUnit.of(" ".repeat(tabSize.value))),
    readOnlyConf.of(EditorState.readOnly.of(readOnly.value)),
    lineNumbersConf.of(showLineNumbers.value ? lineNumbers() : []),
    lineWrappingConf.of(lineWrapping.value ? EditorView.lineWrapping : []),
    highlightLinesConf.of(createHighlightExtension(highlightedLines.value)),
    themeConf.of(
      currentTheme.value === "dark" ? [oneDark, createTheme(true)] : createTheme(false)
    ),
    syntaxHighlighting(defaultHighlightStyle),
    EditorState.allowMultipleSelections.of(true),
    EditorView.updateListener.of((update) => {
      if (update.changes) {
        const value = update.state.doc.toString();
        emit("update:modelValue", value);
        emit("change", value);
        // 当代码变动时检查是否需要发送监控数据
        sendCodeMonitorData();
      }
    }),
    keymap.of([
      ...defaultKeymap,
      ...historyKeymap,
      ...completionKeymap,
      ...lintKeymap,
      ...searchKeymap,
      indentWithTab,
    ]),
  ];

  return extensions;
};

// WebSocket代码监控相关变量
const socketStore = useSocketStore();
let codeMonitorInterval = ref<number | null>(null);
let lastSentCode = ref<string>("");

// 发送代码监控数据
const sendCodeMonitorData = () => {
  if (!view) return;

  const currentCode = view.state.doc.toString();

  // 只有代码发生变化且长度超过10个字符且codeMode不为空时才发送
  if (currentCode !== lastSentCode.value && currentCode.length > 10 && props.defaultCodeMode) {
    socketStore.sendMessage({
      data: {
        monitorCode: currentCode,
        codeMode: props.defaultCodeMode
      },
      businessType: WebsocketBusinessType.CODE_MONITOR.code,
      channel: "DEFAULTS",
      scope: "SOCKET_SERVER"
    });

    // 更新已发送的代码
    lastSentCode.value = currentCode;
  }
};

// 添加代码覆盖相关状态
const showCodePushDialog = ref(false);
const pushedCode = ref('');
const currentPreviewCode = ref('');
const pushMessage = ref('');
const pushData = ref<any>(null);
const diffResult = ref('');

// 生成diff视图HTML
const generateDiffHTML = (oldStr: string, newStr: string): string => {
  // 使用Diff库比较两段代码
  const diffParts = Diff.diffLines(oldStr, newStr);

  // 构建HTML结果
  let html = '<div class="diff-view">';
  let lineNumberOld = 1;
  let lineNumberNew = 1;

  diffParts.forEach((part: Diff.Change) => {
    // 分割成行
    const lines = part.value.split('\n');
    // 如果最后一行是空字符串，则移除（通常是因为分割导致的）
    if (lines[lines.length - 1] === '') {
      lines.pop();
    }

    lines.forEach((line: string) => {
      if (part.added) {
        // 添加的行 - 右侧高亮绿色
        html += `<div class="diff-line added">
          <div class="line-number old"></div>
          <div class="line-content old"></div>
          <div class="line-number new">${lineNumberNew++}</div>
          <div class="line-content new">${escapeHtml(line) || '&nbsp;'}</div>
        </div>`;
      } else if (part.removed) {
        // 删除的行 - 左侧高亮红色
        html += `<div class="diff-line removed">
          <div class="line-number old">${lineNumberOld++}</div>
          <div class="line-content old">${escapeHtml(line) || '&nbsp;'}</div>
          <div class="line-number new"></div>
          <div class="line-content new"></div>
        </div>`;
      } else {
        // 相同的行 - 两侧都显示
        html += `<div class="diff-line">
          <div class="line-number old">${lineNumberOld++}</div>
          <div class="line-content old">${escapeHtml(line) || '&nbsp;'}</div>
          <div class="line-number new">${lineNumberNew++}</div>
          <div class="line-content new">${escapeHtml(line) || '&nbsp;'}</div>
        </div>`;
      }
    });
  });

  html += '</div>';
  return html;
};

// 转义HTML字符
const escapeHtml = (unsafe: string): string => {
  return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
};

// 监听代码推送事件
onMounted(() => {
  // 标准化初始语言
  if (props.language) {
    currentLanguage.value = normalizeProgramLanguage(props.language);
  }

  if (!editorRef.value) return;

  const startState = EditorState.create({
    doc: props.modelValue,
    extensions: getBaseExtensions(),
  });

  view = new EditorView({
    state: startState,
    parent: editorRef.value,
  });

  // 开始计时
  startTimer();

  // 初始化WebSocket连接
  socketStore.open();

  // 添加WebSocket推送代码事件监听
  mittBus.on(`socket.${WebsocketBusinessType.PUSH_COVERED_CODE.code}`, handlePushCoveredCode);
});

// 切换主题
const changeTheme = (theme: string) => {
  currentTheme.value = theme;
  emit("theme-change", theme);
  if (view) {
    view.dispatch({
      effects: themeConf.reconfigure(
        theme === "dark" ? [oneDark, createTheme(true)] : createTheme(false)
      ),
    });
  }
};

// 切换语言
const changeLanguage = (lang: string) => {
  // 标准化语言值
  const normalizedLang = normalizeProgramLanguage(lang);
  currentLanguage.value = normalizedLang;
  emit("language-change", normalizedLang);
  if (view) {
    view.dispatch({
      effects: languageConf.reconfigure(getLanguageExtension()),
    });
  }
};

// 切换只读状态
const toggleReadOnly = () => {
  readOnly.value = !readOnly.value;
  if (view) {
    view.dispatch({
      effects: readOnlyConf.reconfigure(EditorState.readOnly.of(readOnly.value)),
    });
  }
};

// 切换行号显示
const toggleLineNumbers = () => {
  showLineNumbers.value = !showLineNumbers.value;
  if (view) {
    view.dispatch({
      effects: lineNumbersConf.reconfigure(showLineNumbers.value ? lineNumbers() : []),
    });
  }
};

// 切换自动换行
const toggleLineWrapping = () => {
  lineWrapping.value = !lineWrapping.value;
  if (view) {
    view.dispatch({
      effects: lineWrappingConf.reconfigure(
        lineWrapping.value ? EditorView.lineWrapping : []
      ),
    });
  }
};

// 修改Tab宽度
const changeTabSize = (size: number) => {
  tabSize.value = size;
  if (view) {
    view.dispatch({
      effects: tabSizeConf.reconfigure(indentUnit.of(" ".repeat(size))),
    });
  }
};

// 增加字体大小
const increaseFontSize = () => {
  if (fontSize.value < 24) {
    fontSize.value += 1;
    updateFontSize();
  }
};

// 减小字体大小
const decreaseFontSize = () => {
  if (fontSize.value > 12) {
    fontSize.value -= 1;
    updateFontSize();
  }
};

// 更新字体大小
const updateFontSize = () => {
  if (view) {
    // 使用CSS变量更新字体大小，无需重新配置编辑器
    nextTick(() => {
      if (editorRef.value) {
        editorRef.value.style.setProperty("--editor-font-size", `${fontSize.value}px`);
      }
    });
  }
};

// 清空编辑器
const clearEditor = () => {
  if (!view || readOnly.value) return;

  view.dispatch({
    changes: {
      from: 0,
      to: view.state.doc.length,
      insert: "",
    },
  });
};

// 监听props.modelValue的变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (view && view.state.doc.toString() !== newValue) {
      console.log('CodeEditor: 检测到传入的modelValue变化，更新编辑器内容');
      view.dispatch({
        changes: { from: 0, to: view.state.doc.length, insert: newValue }
      });
    }
  },
  { immediate: true }
);

// 监听传入的只读属性变化
watch(
  () => props.readOnlyProp,
  (newValue) => {
    readOnly.value = newValue;
    if (view) {
      view.dispatch({
        effects: readOnlyConf.reconfigure(EditorState.readOnly.of(newValue)),
      });
    }
  }
);

// 监听传入的语言变化
watch(
  () => props.language,
  (newValue) => {
    if (newValue) {
      const normalizedLang = normalizeProgramLanguage(newValue);
      currentLanguage.value = normalizedLang;
      changeLanguage(normalizedLang);
    }
  }
);

// 监听传入的主题变化
watch(
  () => props.theme,
  (newValue) => {
    currentTheme.value = newValue;
    changeTheme(newValue);
  }
);

// 开始计时器
const startTimer = () => {
  if (timerRunning.value) return;

  timerRunning.value = true;
  timerStartTime.value = Date.now() - timerElapsed.value;

  timerInterval.value = window.setInterval(() => {
    timerElapsed.value = Date.now() - timerStartTime.value;
  }, 1000);
};

// 重置计时器
const resetTimer = () => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value);
    timerInterval.value = null;
  }

  timerElapsed.value = 0;
  timerRunning.value = false;
  timerStartTime.value = Date.now();

  // 重置后自动开始计时
  startTimer();
};

// 组件卸载前清除计时器和代码监控定时器
onBeforeUnmount(() => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value);
    timerInterval.value = null;
  }

  // 清除代码监控定时器（这里不需要了，但保留代码结构一致性）
  if (codeMonitorInterval.value) {
    clearInterval(codeMonitorInterval.value);
    codeMonitorInterval.value = null;
  }

  // 清除复制状态定时器
  if (copyStatusTimer.value) {
    clearTimeout(copyStatusTimer.value);
    copyStatusTimer.value = null;
  }

  // 移除WebSocket推送代码事件监听
  mittBus.off(`socket.${WebsocketBusinessType.PUSH_COVERED_CODE.code}`, handlePushCoveredCode);
});

// 重置复制状态
const resetCopyStatus = () => {
  if (copyStatusTimer.value) {
    clearTimeout(copyStatusTimer.value);
    copyStatusTimer.value = null;
  }
  copyStatus.value = 'idle';
};

// 设置复制状态并自动重置
const setCopyStatusWithTimeout = (status: CopyStatus, duration: number = 2000) => {
  copyStatus.value = status;
  
  if (copyStatusTimer.value) {
    clearTimeout(copyStatusTimer.value);
  }
  
  copyStatusTimer.value = window.setTimeout(() => {
    resetCopyStatus();
  }, duration);
};

// 使用 Clipboard API 复制
const copyWithClipboardAPI = async (text: string): Promise<boolean> => {
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      // 检查是否在安全上下文中（HTTPS 或 localhost）
      if (window.isSecureContext) {
        await navigator.clipboard.writeText(text);
        return true;
      }
      // 非安全上下文但支持 clipboard API（某些浏览器可能允许）
      try {
        await navigator.clipboard.writeText(text);
        return true;
      } catch {
        return false;
      }
    }
    return false;
  } catch {
    return false;
  }
};

// 使用传统 execCommand 复制（兼容旧浏览器）
const copyWithExecCommand = (text: string): boolean => {
  try {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    
    // 样式设置确保不影响页面布局
    textArea.style.position = 'fixed';
    textArea.style.left = '-9999px';
    textArea.style.top = '-9999px';
    textArea.style.width = '1px';
    textArea.style.height = '1px';
    textArea.style.opacity = '0';
    textArea.style.pointerEvents = 'none';
    textArea.style.zIndex = '-1';
    textArea.readOnly = true;
    
    document.body.appendChild(textArea);
    
    // 选择文本
    textArea.focus();
    textArea.select();
    textArea.setSelectionRange(0, textArea.value.length);
    
    // 执行复制
    const result = document.execCommand('copy');
    
    // 清理
    document.body.removeChild(textArea);
    
    return result;
  } catch {
    return false;
  }
};

// 使用 selection API 作为备选方案
const copyWithSelection = (text: string): boolean => {
  try {
    // 创建一个临时元素
    const tempDiv = document.createElement('div');
    tempDiv.textContent = text;
    
    // 样式设置
    tempDiv.style.position = 'fixed';
    tempDiv.style.left = '-9999px';
    tempDiv.style.top = '-9999px';
    tempDiv.style.opacity = '0';
    tempDiv.style.pointerEvents = 'none';
    tempDiv.style.zIndex = '-1';
    tempDiv.style.whiteSpace = 'pre-wrap';
    tempDiv.style.userSelect = 'text';
    
    document.body.appendChild(tempDiv);
    
    // 创建选区
    const selection = window.getSelection();
    const range = document.createRange();
    range.selectNodeContents(tempDiv);
    
    // 清除现有选区并添加新选区
    if (selection) {
      selection.removeAllRanges();
      selection.addRange(range);
    }
    
    // 执行复制
    const result = document.execCommand('copy');
    
    // 清理
    if (selection) {
      selection.removeAllRanges();
    }
    document.body.removeChild(tempDiv);
    
    return result;
  } catch {
    return false;
  }
};

// 复制代码
const copyCode = async () => {
  if (!view) return;

  const code = view.state.doc.toString();
  
  // 如果代码为空，提示用户
  if (!code || code.trim() === '') {
    ElMessage.warning('代码为空，无需复制');
    return;
  }
  
  // 设置复制中状态
  copyStatus.value = 'copying';
  
  try {
    // 1. 优先使用 Clipboard API（现代浏览器推荐）
    let copied = await copyWithClipboardAPI(code);
    
    // 2. 如果 Clipboard API 失败，使用 execCommand
    if (!copied) {
      copied = copyWithExecCommand(code);
    }
    
    // 3. 如果 execCommand 失败，使用 Selection API 作为备选
    if (!copied) {
      copied = copyWithSelection(code);
    }
    
    if (copied) {
      // 复制成功
      setCopyStatusWithTimeout('success', 2500);
      ElMessage({
        message: '代码已复制到剪贴板',
        type: 'success',
        duration: 2000,
        showClose: true
      });
    } else {
      // 所有方法都失败
      setCopyStatusWithTimeout('error', 3000);
      
      // 显示更详细的错误信息和解决方案
      ElMessage({
        message: '复制失败，请使用快捷键 Ctrl+C (Windows) 或 Cmd+C (Mac) 手动复制',
        type: 'error',
        duration: 5000,
        showClose: true
      });
      
      // 尝试聚焦编辑器，方便用户手动复制
      view.focus();
      
      // 如果有选中文本，保持选中；否则全选
      if (view.state.selection.main.empty) {
        view.dispatch({
          selection: { anchor: 0, head: view.state.doc.length }
        });
      }
    }
  } catch (err) {
    // 捕获意外错误
    setCopyStatusWithTimeout('error', 3000);
    
    const errorMessage = err instanceof Error ? err.message : String(err);
    console.error('复制代码时发生错误:', errorMessage);
    
    ElMessage({
      message: `复制失败：${errorMessage}，请尝试手动复制`,
      type: 'error',
      duration: 5000,
      showClose: true
    });
  }
};

// 保存代码
const saveCode = async () => {
  if (!view) return;

  try {
    const code = view.state.doc.toString();
    await createCodeRecordApi({
      code,
      codeMode: props.defaultCodeMode
    });
    ElMessage.success('代码保存成功');
  } catch (err) {
    ElMessage.error('代码保存失败');
    console.error('保存失败:', err);
  }
};

// 处理推送代码事件
const handlePushCoveredCode = (data: any) => {
  console.log('收到推送代码事件:', data);
  if (!data || !data.overlayCode) return;

  // 保存推送数据
  pushData.value = data;

  // 设置消息内容
  pushMessage.value = `${data.overlayName || '教师'} 向您推送了代码`;

  // 设置预览内容
  currentPreviewCode.value = view ? view.state.doc.toString() : '';
  pushedCode.value = data.overlayCode;

  // 生成diff视图
  diffResult.value = generateDiffHTML(currentPreviewCode.value, pushedCode.value);

  // 显示对话框
  showCodePushDialog.value = true;
};

// 应用推送的代码
const applyPushedCode = () => {
  if (!view || !pushedCode.value) return;

  // 替换编辑器内容
  view.dispatch({
    changes: {
      from: 0,
      to: view.state.doc.length,
      insert: pushedCode.value
    }
  });

  // 关闭对话框
  showCodePushDialog.value = false;

  // 提示用户
  ElMessage.success('已成功应用推送的代码');
};

// 暴露组件属性和方法给父组件
defineExpose({
  getEditorView: () => view,
  saveCode,
  clearEditor,
  copyCode,
  setValue: (value: string) => {
    if (view) {
      view.dispatch({
        changes: {
          from: 0,
          to: view.state.doc.length,
          insert: value,
        },
      });
    }
  },
  getValue: () => view?.state.doc.toString() || "",
  focus: () => view?.focus(),
  refresh: () => {
    if (view) {
      view.dispatch({
        effects: StateEffect.reconfigure.of(getBaseExtensions()),
      });
    }
  },
  applyHighlightLines,
  highlightLines: highlightedLines,
  setHighlightLines: (lines: string) => {
    highlightLineInput.value = lines;
    applyHighlightLines();
  },
  clearHighlightLines: () => {
    highlightLineInput.value = "";
    highlightedLines.value = [];
    if (view) {
      view.dispatch({
        effects: highlightLinesConf.reconfigure(createHighlightExtension([])),
      });
    }
  },
});
</script>

<style scoped>
.code-editor-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
  border-bottom: 1px solid var(--el-border-color);
}

.toolbar-main-section {
  display: flex;
  align-items: center;
  padding: 6px 10px;
  gap: 10px;
  flex-wrap: wrap;
}

.collapsible-section {
  display: flex;
  align-items: center;
  padding: 6px 10px;
  gap: 10px;
  flex-wrap: wrap;
  border-top: 1px dashed var(--el-border-color-light);
  background-color: var(--el-fill-color-light);
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 6px;
  min-height: 32px;
}

.language-group {
  background-color: rgba(var(--el-color-primary-rgb), 0.05);
  border-left: 3px solid var(--el-color-primary);
  padding: 4px 8px;
  border-radius: 4px;
}

.format-group {
  background-color: rgba(var(--el-color-success-rgb), 0.05);
  border-left: 3px solid var(--el-color-success);
  padding: 4px 8px;
  border-radius: 4px;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  justify-content: flex-end;
  flex-wrap: nowrap;
  overflow-x: auto;
  padding-right: 8px;
}

.timer-container {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 8px;
  background-color: rgba(var(--el-color-primary-rgb), 0.05);
  border-radius: 6px;
  padding: 0 8px;
  border-left: 2px solid var(--el-color-primary);
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.timer-container:hover {
  background-color: rgba(var(--el-color-primary-rgb), 0.1);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.timer-display {
  font-family: monospace;
  font-weight: bold;
  font-size: 14px;
  color: var(--el-color-primary);
  min-width: 50px;
  text-align: center;
}

.timer-button {
  padding: 4px !important;
  min-height: 24px;
  height: 24px;
  width: 24px;
  border-radius: 4px;
  box-shadow: none;
  display: flex;
  justify-content: center;
  align-items: center;
}

.timer-button :deep(.el-icon) {
  margin: 0 !important;
  display: flex;
  justify-content: center;
  align-items: center;
}

.timer-icon {
  animation: pulse 1.5s infinite ease-in-out;
  margin: 0;
  padding: 0;
  font-size: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 0.8;
  }
}

.group-label {
  font-weight: bold;
  color: var(--el-text-color-primary);
  min-width: 30px;
  font-size: 12px;
}

.control-label {
  color: var(--el-text-color-secondary);
  margin-right: 4px;
  font-size: 12px;
}

.select-current-value {
  color: var(--el-text-color-primary);
  font-weight: 500;
  margin-right: 8px;
}

/* 调整下拉框样式 */
:deep(.el-input__wrapper) {
  padding: 0 8px;
}

:deep(.el-select) {
  min-width: 100px;
}

:deep(.el-input__suffix) {
  margin-left: 0;
}

.divider {
  height: 32px;
  margin: 0 8px;
}

.font-size-control,
.tab-size-control {
  display: flex;
  align-items: center;
  padding: 0 4px;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
  height: 32px;
}

.font-size-display {
  margin-left: 4px;
  min-width: 30px;
  text-align: center;
  font-weight: 500;
  font-size: 12px;
}

.highlight-line-control {
  padding: 0 4px;
  flex: 1;
  max-width: 300px;
}

.highlight-label {
  font-weight: 500;
  color: var(--el-text-color-primary);
  padding: 0 4px;
  font-size: 12px;
}

.editor-body {
  flex: 1;
  overflow: auto;
  position: relative;
}

:deep(.cm-highlighted-line) {
  background-color: rgba(220, 38, 38, 0.12) !important;
  border-left: 2px solid rgba(220, 38, 38, 0.6) !important;
}

:deep(.cm-focused) {
  outline: none !important;
}

.collapse-button {
  margin-right: auto;
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--el-color-primary);
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 4px;
  flex-wrap: nowrap;
  flex-shrink: 0;
}

/* 切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, max-height 0.3s;
  max-height: 200px;
  overflow: hidden;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  max-height: 0;
}

/* 响应式布局调整 */
@media (max-width: 768px) {
  .toolbar-main-section {
    padding: 6px 8px;
    gap: 6px;
  }

  .collapsible-section {
    flex-direction: column;
    align-items: stretch;
    padding: 6px 8px;
  }

  .toolbar-group {
    width: 100%;
    justify-content: space-between;
    margin-bottom: 4px;
  }

  .language-group, .format-group {
    width: calc(100% - 16px);
  }

  .action-group {
    justify-content: flex-start;
    margin-top: 4px;
    gap: 8px;
  }

  .timer-container {
    margin-right: 0;
  }

  .highlight-line-control {
    max-width: 100%;
    width: 100%;
    margin-top: 4px;
  }

  .divider {
    display: none;
  }
}

@media (max-width: 480px) {
  :deep(.el-button) {
    padding: 4px 6px;
    font-size: 12px;
  }

  .font-size-control, .tab-size-control {
    height: 28px;
  }

  .timer-container {
    flex: 1;
    justify-content: flex-end;
  }

  .action-buttons {
    width: 100%;
    margin-top: 6px;
  }

  :deep(.el-select) {
    min-width: 80px;
  }

  .group-label {
    min-width: 24px;
    font-size: 11px;
  }
}

/* 代码推送弹窗样式 */
.code-push-dialog :deep(.el-dialog__header) {
  margin: 0;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.code-push-dialog-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #1d1d1f;
}

.dialog-icon {
  color: #0071e3;
  margin-right: 8px;
  font-size: 20px;
}

.code-push-container {
  padding: 20px;
}

.code-push-message {
  margin-bottom: 20px;
}

.push-message {
  font-weight: 500;
  font-size: 14px;
}

/* Diff视图样式 */
.code-diff-container {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
  background-color: #fafafa;
}

.diff-header {
  display: flex;
  background-color: #f0f0f0;
  border-bottom: 1px solid #ddd;
}

.diff-title {
  flex: 1;
  padding: 10px 15px;
  font-weight: 500;
  font-size: 14px;
  text-align: center;
}

.diff-title.left {
  border-right: 1px solid #ddd;
  color: #d32f2f;
}

.diff-title.right {
  color: #388e3c;
}

.diff-content {
  max-height: 600px;
  overflow: auto;
}

.diff-content :deep(.diff-view) {
  display: flex;
  flex-direction: column;
  font-size: 13px;
  line-height: 1.5;
}

.diff-content :deep(.diff-line) {
  display: flex;
  width: 100%;
}

.diff-content :deep(.line-number) {
  width: 40px;
  text-align: right;
  padding: 0 8px;
  color: #999;
  background-color: #f5f5f5;
  user-select: none;
  border-right: 1px solid #eee;
}

.diff-content :deep(.line-content) {
  flex: 1;
  padding: 0 8px;
  white-space: pre-wrap;
  word-break: break-all;
}

.diff-content :deep(.diff-line.added .line-content.new) {
  background-color: #e6ffed;
}

.diff-content :deep(.diff-line.added .line-number.new) {
  background-color: #cdffd8;
  color: #22863a;
}

.diff-content :deep(.diff-line.removed .line-content.old) {
  background-color: #ffeef0;
}

.diff-content :deep(.diff-line.removed .line-number.old) {
  background-color: #ffdce0;
  color: #d73a49;
}

.code-push-dialog-footer {
  padding: 16px 20px;
  text-align: right;
}

/* 符合Apple风格的按钮样式增强 */
.code-push-dialog-footer :deep(.el-button) {
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.code-push-dialog-footer :deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.code-push-dialog-footer :deep(.el-button--primary) {
  background-color: #0071e3;
  border-color: #0071e3;
}

.code-push-dialog-footer :deep(.el-button--primary:hover) {
  background-color: #0077ed;
  border-color: #0077ed;
}

@media (max-width: 768px) {
  .code-push-dialog {
    width: 95% !important;
  }

  .diff-content {
    max-height: 400px;
  }

  .diff-content :deep(.line-number) {
    width: 30px;
    padding: 0 4px;
  }

  .diff-content :deep(.line-content) {
    padding: 0 4px;
    font-size: 12px;
  }
}
</style>

<style>
/* 全局样式，应用于弹出选择框 */
.code-editor-select.el-select__popper .el-select-dropdown__item {
  padding: 0 8px;
  height: 28px;
  line-height: 28px;
  font-size: 12px;
}

:deep(.el-button) {
  font-size: 13px;
  padding: 6px 10px;
  height: auto;
  border-radius: 6px;
  border: none;
  font-weight: 500;
  transition: all 0.2s ease;
  letter-spacing: -0.01em;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

:deep(.el-button:hover:not(.is-disabled)) {
  transform: translateY(-1px);
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
}

:deep(.el-button.el-button--primary) {
  background-color: #0071e3;
  color: white;
}

:deep(.el-button.el-button--success) {
  background-color: #34c759;
  color: white;
}

:deep(.el-button.el-button--warning) {
  background-color: #ff9500;
  color: white;
}

:deep(.el-button.el-button--danger) {
  background-color: #ff3b30;
  color: white;
}

:deep(.el-button.el-button--info) {
  background-color: #8e8e93;
  color: white;
}

/* 添加按钮组效果 */
:deep(.el-button-group) {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border-radius: 6px;
  overflow: hidden;
}

:deep(.el-button-group .el-button) {
  margin: 0;
  border-radius: 0;
  box-shadow: none;
}

:deep(.el-button-group .el-button:first-child) {
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
}

:deep(.el-button-group .el-button:last-child) {
  border-top-right-radius: 6px;
  border-bottom-right-radius: 6px;
}

:deep(.el-button-group .el-button:not(:last-child)) {
  margin-right: 1px;
}

/* 设置下拉菜单不会被压缩 */
:deep(.el-dropdown) {
  flex-shrink: 0;
}

/* 复制按钮状态样式 */
.copy-button-success {
  background-color: #34c759 !important;
  color: white !important;
  border-color: #34c759 !important;
  animation: success-pulse 0.3s ease-out;
}

.copy-button-success:hover {
  background-color: #32d760 !important;
  color: white !important;
}

.copy-button-error {
  background-color: #ff3b30 !important;
  color: white !important;
  border-color: #ff3b30 !important;
  animation: error-shake 0.5s ease-out;
}

.copy-button-error:hover {
  background-color: #ff453a !important;
  color: white !important;
}

.copy-button-copying {
  background-color: #0071e3 !important;
  color: white !important;
  border-color: #0071e3 !important;
}

/* 成功提示动画 */
@keyframes success-pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

/* 错误提示动画 */
@keyframes error-shake {
  0%, 100% {
    transform: translateX(0);
  }
  10%, 30%, 50%, 70%, 90% {
    transform: translateX(-3px);
  }
  20%, 40%, 60%, 80% {
    transform: translateX(3px);
  }
}

/* Loading 图标旋转动画 */
.copy-button-copying :deep(.el-icon) {
  animation: loading-spin 1s linear infinite;
}

@keyframes loading-spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
