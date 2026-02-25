<template>
  <div class="tuc-container">
    <!-- 左侧编码区 -->
    <div class="tuc-editor-panel" :style="{ width: `${leftPanelWidth}%` }">
      <!-- 独立的工具栏 -->
      <div class="toolbar-section">
        <div class="toolbar-buttons">
          <el-button
            plain
            type="success"
            size="default"
            @click="runCode"
          >
            <el-icon><VideoPlay /></el-icon>
            运行代码
          </el-button>

          <TemplateSelector
            button-text="示例"
            :replace-code="true"
            @select-template="handleTemplateSelect"
          />

          <el-button
            plain
            type="primary"
            size="default"
            @click="showCommandsDialog = true"
          >
            <el-icon><Document /></el-icon>
            说明
          </el-button>

          <el-button
            plain
            type="warning"
            size="default"
            @click="showSettingsDialog = true"
          >
            <el-icon><Setting /></el-icon>
            设置
          </el-button>

          <el-button
            plain
            type="info"
            size="default"
            @click="showCommandsPanel = !showCommandsPanel"
          >
            <el-icon><ArrowUp v-if="showCommandsPanel" /><ArrowDown v-else /></el-icon>
            {{ showCommandsPanel ? '隐藏' : '显示' }}帮助
          </el-button>

          <el-button
            plain
            type="primary"
            size="default"
            @click="shareCode"
          >
            <el-icon style="margin-right: 4px;"><Share /></el-icon>
            分享
          </el-button>
        </div>
      </div>

      <!-- 代码编辑器 -->
      <div class="code-editor-section">
        <CodeEditor
          ref="codeEditorRef"
          v-model="code"
          :default-code-mode="String(CodeMode.GlowC.code)"
          :language="currentLanguage.value"
          :theme="theme"
          :default-font-size="14"
          :default-tab-size="4"
          @change="onCodeChange"
          @language-change="onLanguageChange"
        />
      </div>

      <!-- 可折叠的命令内容展示区域 -->
      <CommandsPanel
        v-model="showCommandsPanel"
        :default-active-tab="commandsPanelTab"
        :code-editor-ref="codeEditorRef"
        @insert-code="insertTemplateContent"
      />
    </div>

    <!-- 拖拽分隔条 -->
    <div
      class="resize-handle"
      @mousedown="startResize"
      @touchstart="startResize"
    >
      <div class="resize-handle-line"></div>
    </div>

    <!-- 右侧画板区 -->
    <div class="tuc-canvas-panel" :style="{ width: `${100 - leftPanelWidth}%` }">
      <CodeRunner
        ref="codeRunnerRef"
        :speed="runSpeed"
        :width="adjustedCanvasWidth"
        :height="adjustedCanvasHeight"
        :is-visible="true"
        :pen-speed="speedValues.pen"
        :arr-speed="speedValues.arr"
        :grid-speed="speedValues.grid"
        :tree-speed="speedValues.tree"
        :list-speed="speedValues.list"
        :pen-config="configValues.pen"
        :arr-config="configValues.arr"
        :grid-config="configValues.grid"
        :tree-config="configValues.tree"
        :list-config="configValues.list"
      />
    </div>

    <!-- 使用抽离的指令说明组件 -->
    <CommandsDialog
      v-model:visible="showCommandsDialog"
      :default-active-tab="commandsActiveTab"
      :code-editor-ref="codeEditorRef"
      @insert-template="insertTemplateContent"
    />

    <!-- 使用抽离的设置面板组件 -->
    <SettingsPanel
      v-model:visible="showSettingsDialog"
      :default-active-tab="activeSettingsTab"
      :speed-values="speedValues"
      :config-values="configValues"
      :line-width-value="lineWidthValue"
      :color-map="colorMap"
      @speed-change="handleSpeedChange"
      @slider-change="handleSliderChange"
      @reset-speed="resetSpeed"
      @line-width-change="handleLineWidthChange"
      @config-change="handleConfigChange"
      @apply="applySettings"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, onBeforeMount } from 'vue';
import { VideoPlay, Brush, Grid, ArrowUp, ArrowDown, Document, Connection, Setting, Refresh, Share } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import CodeEditor from '@/components/Common/CodeEditor/CodeEditor.vue';
import CodeRunner from './components/CodeRunner.vue';
import CommandsDialog from './components/CommandsDialog.vue';
import TemplateSelector from './components/TemplateSelector.vue';
import SettingsPanel from './components/SettingsPanel.vue';
import testTemplates from './types/test_templates';
import {
  ComponentType,
  TemplateGroupType,
  COMMANDS_CONFIG,
  getTemplatesByGroup as getTemplatesByGroupFn
} from './config/commandsConfig';
import CommandsPanel from './components/CommandsPanel.vue';

import { CodeMode } from '@/enums/oj/code'

// 定义代码模板接口
interface CodeTemplate {
  label: string;
  content: string;
  group: string;
}

// 定义属性
interface TucProps {
  width?: number;
  height?: number;
  speed?: number;
}

const props = withDefaults(defineProps<TucProps>(), {
  width: 600,
  height: 600,
  speed: 500
});

// 引用组件实例
const codeEditorRef = ref<InstanceType<typeof CodeEditor> | null>(null);
const codeRunnerRef = ref<InstanceType<typeof CodeRunner> | null>(null);

// 状态变量 - 代码模板相关功能已集成到 CommandsDialog.vue 组件
const code = ref('// 请在此处编写代码\n');
const currentLanguage = ref({ value: 'cpp', label: 'C++' });
const theme = ref('light');
const runSpeed = ref(props.speed);
const canvasWidth = ref(props.width);
const canvasHeight = ref(props.height);
const showCommandsDialog = ref(false); // 控制指令说明弹窗的显示
const commandsActiveTab = ref('pen'); // 指令说明活动标签

// 底部命令面板状态
const showCommandsPanel = ref(false); // 默认折叠
const commandsPanelTab = ref('pen'); // 默认画笔标签

// 面板宽度控制状态 - 左侧编码区占40%，右侧画板区占60% (2:3比例)
const leftPanelWidth = ref(50);

// 拖拽相关状态
const isResizing = ref(false);
const startX = ref(0);
const startWidth = ref(0);

// 设置面板相关状态
const showSettingsDialog = ref(false); // 控制设置面板弹窗的显示
const lineWidthValue = ref(2); // 线条宽度
const activeSettingsTab = ref(ComponentType.PEN); // 设置面板活动标签

// 预设速度选项
const speedOptions = [
  { label: '极慢', value: 100 },
  { label: '慢速', value: 200 },
  { label: '中速', value: 500 },
  { label: '快速', value: 700 },
  { label: '极速', value: 900 },
  { label: '瞬间', value: 1000 }
];

// 各工具速度值
const speedValues = ref({
  pen: 500,   // 画笔速度 (默认中速)
  arr: 500,   // 数组操作速度 (默认中速)
  grid: 500,  // 二维数组操作速度 (默认中速)
  tree: 500,  // 树结构操作速度 (默认中速)
  list: 500   // 链表操作速度 (默认中速)
});

// 速度文字映射
const getSpeedLabel = (speed: number): string => {
  if (speed >= 1000) return '瞬间';
  if (speed >= 900) return '极速';
  if (speed >= 700) return '快速';
  if (speed >= 500) return '中速';
  if (speed >= 200) return '慢速';
  return '极慢';
};

// 各组件配置项
const configValues = ref({
  pen: {
    lineWidth: 2,         // 画笔线条粗细
    showArrow: true,      // 是否显示画笔方向指示箭头 - 始终显示，不提供开关
    arrowSize: 20,        // 方向指示箭头大小
    arrowWidth: 10,       // 方向指示箭头宽度
    arrowOpacity: 0.8,     // 箭头透明度
    arrowDownColor: '#FF0000', // 画笔落下颜色
    arrowUpColor: '#0000FF'  // 画笔抬起颜色
  },
  arr: {
    cellSize: 40,                // 单元格大小(像素) - 优化默认大小
    padding: 8,                  // 单元格内边距(像素) - 减小内边距
    fontSize: 14,                // 字体大小(像素) - 新增字体大小设置
    textColor: '#000000',        // 文本颜色
    highlightColor: '#FFD700',   // 高亮颜色
    borderColor: '#333333',      // 边框颜色
    backgroundColor: '#ffffff',  // 背景颜色
    highlightDuration: 2000,     // 高亮持续时间(ms)
    setDuration: 1000            // 设置值动画时间(ms)
  },
  grid: {
    cellSize: 35,                // 单元格大小(像素) - 优化默认大小
    padding: 6,                  // 网格内边距(像素) - 减小内边距
    fontSize: 12,                // 字体大小(像素) - 新增字体大小设置
    textColor: '#000000',        // 文本颜色
    highlightColor: '#FFD700',   // 高亮颜色
    borderColor: '#333333',      // 边框颜色
    backgroundColor: '#ffffff',  // 背景颜色
    highlightDuration: 2000,     // 高亮持续时间(ms)
    setDuration: 1000            // 设置值动画时间(ms)
  },
  tree: {
    nodeRadius: 22,              // 节点半径(像素) - 优化默认大小
    levelHeight: 70,             // 树层级间高度(像素) - 减小层级高度
    fontSize: 13,                // 字体大小(像素) - 新增字体大小设置
    nodePadding: 5,              // 节点内边距(像素) - 新增内边距设置
    textColor: '#000000',        // 文本颜色
    nodeColor: '#FFFFFF',        // 节点颜色
    highlightColor: '#FFD700',   // 高亮颜色
    lineColor: '#333333',        // 连线颜色
    animationDuration: 500,      // 动画持续时间(ms)
    highlightDuration: 2000      // 高亮持续时间(ms)
  },
  list: {
    nodeRadius: 25,              // 节点半径(像素) - 优化默认大小
    nodeSpacing: 80,             // 节点间距(像素) - 减小节点间距
    fontSize: 13,                // 字体大小(像素) - 新增字体大小设置
    nodePadding: 5,              // 节点内边距(像素) - 新增内边距设置
    textColor: '#000000',        // 文本颜色
    nodeColor: '#FFFFFF',        // 节点颜色
    highlightColor: '#FFD700',   // 高亮颜色
    lineColor: '#333333',        // 连线颜色
    animationDuration: 500,      // 动画持续时间(ms)
    highlightDuration: 2000,     // 高亮持续时间(ms)
    startX: 50,                  // 起始X坐标(像素)
    startY: 100,                 // 起始Y坐标(像素)
    arrowSize: 8                 // 箭头大小(像素) - 减小箭头大小
  }
});

// 颜色对照表
const colorMap = ref([
  { id: 1, name: '红色', value: '#FF0000' },
  { id: 2, name: '橙色', value: '#FFA500' },
  { id: 3, name: '黄色', value: '#FFFF00' },
  { id: 4, name: '绿色', value: '#00FF00' },
  { id: 5, name: '青色', value: '#00FFFF' },
  { id: 6, name: '蓝色', value: '#0000FF' },
  { id: 7, name: '紫色', value: '#800080' },
  { id: 8, name: '粉色', value: '#FFC0CB' },
  { id: 9, name: '浅粉色', value: '#FFB6C1' },
  { id: 10, name: '浅黄色', value: '#FFFF66' },
  { id: 11, name: '天蓝色', value: '#87CEEB' },
  { id: 12, name: '淡蓝色', value: '#B0C4DE' },
  { id: 13, name: '金色', value: '#FFD700' },
  { id: 14, name: '褐色', value: '#5C3317' },
  { id: 15, name: '灰色', value: '#808080' },
  { id: 16, name: '黑色', value: '#000000' }
]);

// 语言变更处理
const onLanguageChange = (lang: string) => {
  currentLanguage.value = {
    value: lang,
    label: lang === 'cpp' ? 'C++' :
           lang === 'javascript' ? 'JavaScript' :
           lang === 'java' ? 'Java' :
           lang === 'python' ? 'Python' : lang
  };
};

// 代码变更处理
const onCodeChange = (newCode: string) => {
  code.value = newCode;
};

// 处理模板内容插入事件
const insertTemplateContent = (content: string) => {
  if (codeEditorRef.value) {
    try {
      // 尝试获取编辑器视图
      const editorView = codeEditorRef.value.getEditorView?.();
      if (editorView) {
        const cursorPosition = editorView.state.selection.main.head;
        // 使用编辑器API在光标位置插入内容
        editorView.dispatch({
          changes: {
            from: cursorPosition,
            to: cursorPosition,
            insert: content
          }
        });
        return;
      }

      // 如果没有getEditorView方法，尝试使用setValue方法
      const currentCode = codeEditorRef.value.getValue?.() || code.value;
      codeEditorRef.value.setValue?.(currentCode + content);
    } catch (error) {
      console.error('插入代码模板时出错:', error);
      code.value += content;
    }
  } else {
    // 如果没有编辑器引用，直接追加到代码字符串
    code.value += content;
  }
};

// 处理模板选择
const handleTemplateSelect = (data: { type: any; content: string; replaceCode: boolean }) => {
  const { content, replaceCode } = data;

  if (replaceCode) {
    // 替换当前代码
    code.value = content;
  } else {
    // 在当前代码后追加
    code.value += content;
  }
};

// 运行代码
const runCode = async () => {
  if (codeRunnerRef.value) {
    // 直接运行代码，所有设置都通过props传递
    await codeRunnerRef.value.runCode(code.value);
  }
};

// 添加窗口大小状态
const windowWidth = ref(window.innerWidth);
const windowHeight = ref(window.innerHeight);

// 计算适应屏幕大小的画布尺寸
const adjustedCanvasWidth = computed(() => {
  if (windowWidth.value <= 768) {
    // 小屏幕上固定宽度为240px
    return 240;
  } else if (windowWidth.value <= 1024) {
    // 中等屏幕下根据右侧面板宽度计算
    const rightPanelWidth = (100 - leftPanelWidth.value) / 100;
    return Math.min(450, windowWidth.value * rightPanelWidth * 0.8);
  }
  // 大屏幕下根据右侧面板宽度动态计算
  const rightPanelWidth = (100 - leftPanelWidth.value) / 100;
  return Math.min(props.width, windowWidth.value * rightPanelWidth * 0.85);
});

const adjustedCanvasHeight = computed(() => {
  if (windowWidth.value <= 768) {
    // 在移动设备上保持固定宽高比，但高度稍小于宽度
    return 220;
  } else if (windowWidth.value <= 1024) {
    // 中等屏幕同样保持较小的高度
    return adjustedCanvasWidth.value * 0.9;
  }
  // 大屏幕下保持合适的宽高比
  return Math.min(props.height, adjustedCanvasWidth.value * 0.95);
});

// 监听窗口大小变化
const handleResize = () => {
  windowWidth.value = window.innerWidth;
  windowHeight.value = window.innerHeight;
};

// 分享功能
const shareCode = (): void => {
  try {
    const currentCode = code.value || ''
    if (!currentCode.trim()) {
      ElMessage.warning('代码为空，无法分享')
      return
    }
    
    // 将代码进行 base64 编码
    const encodedCode = btoa(encodeURIComponent(currentCode))
    
    // 构建分享链接
    const currentUrl = window.location.origin + window.location.pathname
    const shareUrl = `${currentUrl}?code=${encodedCode}`
    
    // 复制到剪贴板
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(shareUrl).then(() => {
        ElMessage.success('分享链接已复制到剪贴板！')
      }).catch(() => {
        // 降级方案
        copyToClipboard(shareUrl)
      })
    } else {
      copyToClipboard(shareUrl)
    }
  } catch (error) {
    ElMessage.error('分享失败，请重试')
    console.error('分享失败:', error)
  }
}

// 复制到剪贴板的降级方案
const copyToClipboard = (text: string): void => {
  const textArea = document.createElement('textarea')
  textArea.value = text
  textArea.style.position = 'fixed'
  textArea.style.left = '-999999px'
  textArea.style.top = '-999999px'
  document.body.appendChild(textArea)
  textArea.focus()
  textArea.select()
  try {
    document.execCommand('copy')
    ElMessage.success('分享链接已复制到剪贴板！')
  } catch (err) {
    ElMessage.error('复制失败，请手动复制')
  }
  document.body.removeChild(textArea)
}

// 从 URL 参数加载代码
const loadCodeFromUrl = (): boolean => {
  const urlParams = new URLSearchParams(window.location.search)
  const encodedCode = urlParams.get('code')
  
  if (encodedCode) {
    try {
      const decodedCode = decodeURIComponent(atob(encodedCode))
      code.value = decodedCode
      ElMessage.success('已加载分享的代码')
      return true // 表示已从URL加载代码
    } catch (error) {
      ElMessage.error('加载分享代码失败')
      console.error('解码失败:', error)
    }
  }
  return false // 表示未从URL加载代码
}

// 组件挂载时初始化
onMounted(() => {
  // 从 URL 加载分享的代码，如果没有则设置默认画笔示例
  const loadedFromUrl = loadCodeFromUrl()
  // 检查是否是默认占位符或空代码，如果是则设置默认画笔示例
  const isDefaultPlaceholder = code.value.trim() === '' || 
                                code.value.trim() === '// 请在此处编写代码' ||
                                code.value.trim() === '// 请在此处编写代码\n'
  if (!loadedFromUrl && isDefaultPlaceholder) {
    // 如果没有从URL加载代码且是默认占位符，则设置默认画笔示例
    code.value = testTemplates.pen
  }
  // 可以在这里添加初始化代码
  window.addEventListener('resize', handleResize);
});

// 组件卸载前移除事件监听
onBeforeMount(() => {
  window.removeEventListener('resize', handleResize);
  // 清理拖拽相关的事件监听
  document.removeEventListener('mousemove', handlePanelResize);
  document.removeEventListener('mouseup', stopResize);
  document.removeEventListener('touchmove', handlePanelResize);
  document.removeEventListener('touchend', stopResize);
});

// 监听设置面板的显示状态，打开时默认选中pen标签
watch(() => showSettingsDialog.value, (newVal) => {
  if (newVal) {
    // 打开设置面板时，默认选中画笔设置标签
    activeSettingsTab.value = ComponentType.PEN;
  }
});

// 监听props变化，更新对应的ref值
watch(() => props.speed, (newSpeed) => {
  runSpeed.value = newSpeed;
});

// 同时监听宽度和高度的变化
watch(() => props.width, (newWidth) => {
  canvasWidth.value = newWidth;
});

watch(() => props.height, (newHeight) => {
  canvasHeight.value = newHeight;
});

// 处理线宽变化
const handleLineWidthChange = (newWidth: number) => {
  lineWidthValue.value = newWidth;
  console.log(`线宽已更新为: ${newWidth}`);
};

// 处理配置变化
const handleConfigChange = (component: string, newConfig: any) => {
  if (component === 'pen') {
    configValues.value.pen = {...newConfig};
    // 确保lineWidth也同步更新
    if (newConfig.lineWidth) {
      lineWidthValue.value = newConfig.lineWidth;
    }
    console.log('画笔配置已更新:', newConfig);
  } else if (component === 'arr') {
    configValues.value.arr = {...newConfig};
  } else if (component === 'grid') {
    configValues.value.grid = {...newConfig};
  } else if (component === 'tree') {
    configValues.value.tree = {...newConfig};
  } else if (component === 'list') {
    configValues.value.list = {...newConfig};
  }
};

// 应用设置
const applySettings = () => {
  // 将lineWidth值同步到pen配置中
  configValues.value.pen.lineWidth = lineWidthValue.value;

  // 确保CodeRunner组件能够获取到最新的配置
  if (codeRunnerRef.value) {
    codeRunnerRef.value.updateConfigs(speedValues.value, configValues.value);
  }

  // 输出日志
  console.log('应用设置：', {
    lineWidth: lineWidthValue.value,  // 单独记录lineWidth方便调试
    speeds: speedValues.value,
    configs: configValues.value
  });
};

// 添加处理速度变化的方法
const handleSpeedChange = (component: 'pen' | 'arr' | 'grid' | 'tree' | 'list', value: number) => {
  // 更新速度值
  speedValues.value[component] = value;
  console.log(`${component}速度通过下拉框更新为:`, value, getSpeedLabel(value));

  // 如果是画笔速度变化，立即应用
  if (component === 'pen' && codeRunnerRef.value) {
    codeRunnerRef.value.updateConfigs(speedValues.value, configValues.value);
  }
};

// 为每个组件添加速度标签的计算属性
const speedLabels = computed(() => {
  return {
    pen: getSpeedLabel(speedValues.value.pen),
    arr: getSpeedLabel(speedValues.value.arr),
    grid: getSpeedLabel(speedValues.value.grid),
    tree: getSpeedLabel(speedValues.value.tree),
    list: getSpeedLabel(speedValues.value.list)
  };
});

const handleSliderChange = (component: 'pen' | 'arr' | 'grid' | 'tree' | 'list', value: number) => {
  // 更新速度值
  speedValues.value[component] = value;
  console.log(`${component}速度通过滑动条更新为:`, value, getSpeedLabel(value));

  // 可以在这里查找最接近的预设速度并更新标签（可选）
  const closestOption = findClosestSpeedOption(value);
  if (closestOption && Math.abs(closestOption.value - value) < 20) {
    // 如果滑动条的值非常接近某个预设值，就自动吸附到那个预设值
    speedValues.value[component] = closestOption.value;
  }

  // 如果是画笔速度变化，立即应用
  if (component === 'pen' && codeRunnerRef.value) {
    codeRunnerRef.value.updateConfigs(speedValues.value, configValues.value);
  }
};

// 查找最接近的速度预设选项
const findClosestSpeedOption = (value: number) => {
  return speedOptions.reduce((prev, curr) => {
    return Math.abs(curr.value - value) < Math.abs(prev.value - value) ? curr : prev;
  });
};

// 重置速度到中速的方法
const resetSpeed = (component: 'pen' | 'arr' | 'grid' | 'tree' | 'list') => {
  // 中速值为500
  speedValues.value[component] = 500;
  console.log(`${component}速度已重置为中速(500)`);
};

// 重置设置面板
const resetSettings = () => {
  // 重置设置面板为画笔设置
  activeSettingsTab.value = ComponentType.PEN;
  // ... existing code ...
}

// 拖拽处理方法
const startResize = (e: MouseEvent | TouchEvent): void => {
  isResizing.value = true;
  startX.value = 'touches' in e ? e.touches[0].clientX : e.clientX;
  startWidth.value = leftPanelWidth.value;

  // 添加全局事件监听
  document.addEventListener('mousemove', handlePanelResize);
  document.addEventListener('mouseup', stopResize);
  document.addEventListener('touchmove', handlePanelResize);
  document.addEventListener('touchend', stopResize);

  // 防止文本选择
  document.body.style.userSelect = 'none';
  e.preventDefault();
};

const handlePanelResize = (e: MouseEvent | TouchEvent): void => {
  if (!isResizing.value) return;

  const currentX = 'touches' in e ? e.touches[0].clientX : e.clientX;
  const deltaX = currentX - startX.value;
  const containerWidth = document.querySelector('.tuc-container')?.clientWidth || 1000;
  const deltaPercent = (deltaX / containerWidth) * 100;

  // 计算新的宽度百分比，限制在20%-80%之间
  let newWidth = startWidth.value + deltaPercent;
  newWidth = Math.max(20, Math.min(80, newWidth));

  leftPanelWidth.value = newWidth;
};

const stopResize = (): void => {
  isResizing.value = false;

  // 移除全局事件监听
  document.removeEventListener('mousemove', handlePanelResize);
  document.removeEventListener('mouseup', stopResize);
  document.removeEventListener('touchmove', handlePanelResize);
  document.removeEventListener('touchend', stopResize);

  // 恢复文本选择
  document.body.style.userSelect = '';
};
</script>

<style scoped>
.tuc-container {
  display: flex;
  flex-wrap: nowrap;
  gap: 0;
  width: 100%;
  height: 100%;
  padding: 0;
  background-color: #f5f5f7;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Helvetica Neue', sans-serif;
}

.tuc-editor-panel {
  min-width: 300px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-radius: 12px 0 0 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: width 0.1s ease;
}

/* 独立工具栏区域 */
.toolbar-section {
  flex-shrink: 0;
  background-color: #f8f8fa;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

/* 代码编辑器区域 */
.code-editor-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tuc-canvas-panel {
  min-width: 300px;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  border-radius: 0 12px 12px 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  padding: 16px;
  transition: width 0.1s ease;
}

/* 拖拽分隔条样式 */
.resize-handle {
  width: 8px;
  height: 100%;
  background-color: #f5f5f7;
  cursor: col-resize;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: background-color 0.2s ease;
  user-select: none;
  z-index: 10;
}

.resize-handle:hover {
  background-color: #e8e8ed;
}

.resize-handle:active {
  background-color: #d1d1d6;
}

.resize-handle-line {
  width: 2px;
  height: 40px;
  background-color: #c7c7cc;
  border-radius: 1px;
  transition: all 0.2s ease;
}

.resize-handle:hover .resize-handle-line {
  background-color: #0071e3;
  height: 60px;
}

.resize-handle:active .resize-handle-line {
  background-color: #005bb5;
  height: 80px;
}

.toolbar-buttons {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  gap: 8px;
  padding: 12px 16px;
  overflow-x: auto;
  white-space: nowrap;
  scrollbar-width: thin;
}

.toolbar-buttons::-webkit-scrollbar {
  height: 4px;
}

.toolbar-buttons::-webkit-scrollbar-track {
  background: transparent;
}

.toolbar-buttons::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

:deep(.el-button) {
  font-size: 13px;
  padding: 6px 10px;
  height: auto;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  letter-spacing: -0.01em;
  white-space: nowrap;
  flex-shrink: 0;
}

:deep(.el-button:not(.el-button--text)) {
  background-color: transparent;
  box-shadow: none;
}

:deep(.el-button.is-disabled) {
  opacity: 0.5;
  background-color: #f2f2f7;
  color: #8e8e93;
}

:deep(.el-button:hover:not(.is-disabled)) {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

:deep(.el-button.el-button--primary) {
  border: 1px solid var(--el-color-primary);
  color: var(--el-color-primary);
}

:deep(.el-button.el-button--primary:hover) {
  background-color: var(--el-color-primary);
  color: white;
}

:deep(.el-button.el-button--success) {
  border: 1px solid var(--el-color-success);
  color: var(--el-color-success);
}

:deep(.el-button.el-button--success:hover) {
  background-color: var(--el-color-success);
  color: white;
}

:deep(.el-button.el-button--warning) {
  border: 1px solid var(--el-color-warning);
  color: var(--el-color-warning);
}

:deep(.el-button.el-button--warning:hover) {
  background-color: var(--el-color-warning);
  color: white;
}

:deep(.el-button.el-button--danger) {
  border: 1px solid var(--el-color-danger);
  color: var(--el-color-danger);
}

:deep(.el-button.el-button--danger:hover) {
  background-color: var(--el-color-danger);
  color: white;
}

:deep(.el-button.el-button--info) {
  border: 1px solid var(--el-color-info);
  color: var(--el-color-info);
}

:deep(.el-button.el-button--info:hover) {
  background-color: var(--el-color-info);
  color: white;
}

:deep(.el-button-group) {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-button-group .el-button) {
  margin: 0;
  border-radius: 0;
  box-shadow: none;
}

:deep(.el-button-group .el-button:first-child) {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}

:deep(.el-button-group .el-button:last-child) {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}

:deep(.el-button-group .el-button:not(:last-child)) {
  margin-right: 1px;
}

:deep(.el-icon) {
  font-size: 14px;
  margin-right: 4px;
}

@media (max-width: 768px) {
  .tuc-container {
    padding: 5px;
    gap: 12px;
    flex-direction: column;
    height: auto;
    min-height: calc(100vh - 50px);
  }

  .tuc-editor-panel, .tuc-canvas-panel {
    flex: none;
    width: 100% !important;
    min-width: 0;
  }

  .tuc-editor-panel {
    height: 50vh;
    min-height: 300px;
    border-radius: 12px;
  }

  .tuc-canvas-panel {
    position: relative;
    padding: 10px 0 30px;
    min-height: 280px;
    height: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    overflow: visible;
    margin-bottom: 30px;
    border-radius: 12px;
  }

  /* 在移动端隐藏拖拽分隔条 */
  .resize-handle {
    display: none;
  }

  .toolbar-buttons {
    padding: 8px 12px;
    gap: 6px;
  }

  :deep(.el-button) {
    padding: 6px 8px;
    font-size: 12px;
  }

  :deep(.el-button .el-icon) {
    margin-right: 2px;
  }
}

@media (max-width: 480px) {
  .tuc-container {
    padding:5px;
    gap: 8px;
  }

  .tuc-canvas-panel {
    padding: 5px 0 40px;
    margin-bottom: 40px;
  }

  .toolbar-buttons {
    padding: 8px;
    gap: 6px;
  }

  :deep(.el-button) {
    padding: 4px 6px;
  }
}

/* 指令说明弹窗样式 */
.commands-content {
  padding: 16px;
  max-height: 60vh;
  overflow-y: auto;
}

.commands-content h3 {
  margin: 16px 0 12px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.01em;
}

:deep(.el-tabs__item) {
  padding: 0 16px;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  letter-spacing: -0.01em;
  color: #424245;
}

:deep(.el-tabs--card > .el-tabs__header .el-tabs__item.is-active) {
  background-color: #ffffff;
  font-weight: 600;
  color: #0071e3;
}

:deep(.el-tabs--card > .el-tabs__header) {
  border-bottom: none;
}

:deep(.el-tabs--card > .el-tabs__header .el-tabs__nav) {
  border: none;
  border-radius: 8px;
  background-color: #f5f5f7;
}

:deep(.el-tabs__item) {
  border: none !important;
}

/* 设置面板样式 */
.settings-content {
  padding: 24px;
}

.speed-slider, .line-width-slider, .config-item {
  margin: 16px 0;
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
}

.slider-label, .config-label {
  min-width: 130px;
  display: inline-block;
  margin-top: 8px;
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.01em;
}

.config-desc {
  color: #86868b;
  font-size: 12px;
  margin-top: 5px;
  padding-left: 130px;
  width: 100%;
  letter-spacing: -0.01em;
}

:deep(.el-slider__runway) {
  height: 2px;
  background-color: #d2d2d7;
}

:deep(.el-slider__bar) {
  height: 2px;
  background-color: #0071e3;
}

:deep(.el-slider__button) {
  width: 18px;
  height: 18px;
  border: none;
  background-color: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.15);
}

.line-preview {
  margin: 10px 0;
  background-color: #0071e3;
  width: 100%;
  border-radius: 4px;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.color-item {
  display: flex;
  align-items: center;
  border-radius: 10px;
  padding: 12px;
  background-color: #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.color-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
}

.color-box {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  margin-right: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.color-info {
  flex: 1;
}

.color-code {
  font-weight: 600;
  font-size: 15px;
  letter-spacing: -0.01em;
  color: #1d1d1f;
}

.color-name {
  color: #86868b;
  font-size: 12px;
  letter-spacing: -0.01em;
  margin-top: 2px;
}

.color-value {
  color: #86868b;
  font-size: 12px;
  letter-spacing: -0.01em;
}

.dialog-footer {
  margin-top: 24px;
  text-align: right;
}

.config-section {
  margin-bottom: 24px;
  padding: 20px;
  border-radius: 12px;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.config-section h3 {
  margin-top: 0;
  margin-bottom: 16px;
  color: #1d1d1f;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding-bottom: 12px;
  letter-spacing: -0.01em;
}

/* 颜色网格 - 大尺寸 */
.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

/* 颜色网格 - 小尺寸 (用于pen标签页) */
.color-grid-small {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
  margin-top: 16px;
}

.color-item-small {
  display: flex;
  align-items: center;
  border-radius: 8px;
  padding: 8px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.color-item-small:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.color-box-small {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  margin-right: 8px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}

.color-info-small {
  flex: 1;
  font-size: 12px;
}

.color-code-small {
  font-weight: 600;
  margin-right: 4px;
  letter-spacing: -0.01em;
  color: #1d1d1f;
}

.color-name-small {
  color: #86868b;
  letter-spacing: -0.01em;
}

.speed-slider-container {
  width: 200px;
  margin-left: 12px;
  margin-right: 12px;
}

.config-item {
  margin: 16px 0;
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
}

.slider-label, .config-label {
  min-width: 130px;
  display: inline-block;
  margin-top: 8px;
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

.config-desc {
  color: #86868b;
  font-size: 12px;
  margin-top: 5px;
  padding-left: 130px;
  width: 100%;
}

.speed-control-container {
  display: flex;
  align-items: center;
  flex: 1;
}

.speed-slider-container {
  flex: 1;
  margin-left: 12px;
  margin-right: 12px;
  max-width: 300px;
}

.speed-value-text {
  min-width: 40px;
  text-align: center;
  color: #0071e3;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.reset-button {
  margin-left: 8px;
  color: #86868b;
  transition: all 0.2s ease;
}

.reset-button:hover {
  color: #0071e3;
}

/* 可折叠的命令内容展示区域 */
.commands-panel {
  margin-top: 16px;
  border-radius: 12px;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.commands-header {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 12px 16px;
  background-color: #f5f5f7;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.01em;
  transition: all 0.2s ease;
}

.commands-header:hover {
  background-color: #ebebf0;
}

.commands-header .el-icon {
  margin-right: 8px;
  transition: transform 0.3s;
}

.commands-header .is-rotate {
  transform: rotate(180deg);
}

.commands-content-panel {
  max-height: 250px;
  overflow-y: auto;
  padding: 8px;
}

:deep(.commands-panel .el-tabs__content) {
  max-height: 200px;
  overflow-y: auto;
  padding-right: 5px;
  scrollbar-width: thin;
}

:deep(.commands-panel .el-tabs__content::-webkit-scrollbar) {
  width: 6px;
}

:deep(.commands-panel .el-tabs__content::-webkit-scrollbar-track) {
  background: #f2f3f5;
  border-radius: 3px;
}

:deep(.commands-panel .el-tabs__content::-webkit-scrollbar-thumb) {
  background: #d4d7de;
  border-radius: 3px;
}

:deep(.commands-panel .el-tabs__content::-webkit-scrollbar-thumb:hover) {
  background: #c3c6cf;
}

.commands-panel-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 10px 0;
  padding: 0 8px;
}

.cmd-item {
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  background-color: #f5f5f7;
  transition: all 0.2s ease;
  font-size: 13px;
  color: #0071e3;
  font-weight: 500;
  letter-spacing: -0.01em;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.cmd-item:hover {
  background-color: #e4e4e9;
  color: #0059b3;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.commands-panel-groups {
  padding: 8px;
}

.cmd-group-title {
  font-weight: 600;
  margin: 12px 0 8px 8px;
  color: #1d1d1f;
  font-size: 13px;
  letter-spacing: -0.01em;
}
</style>
