<template>
  <div v-if="isVisible" class="canvas-container" ref="canvasContainer">
    <div class="canvas-inner">
      <div class="canvas-overlay">
        <canvas ref="drawingCanvas" :key="`drawing-${canvasKey}`" :width="adjustedWidth" :height="adjustedHeight"></canvas>
        <canvas ref="brushCanvas" :key="`brush-${canvasKey}`" :width="adjustedWidth" :height="adjustedHeight"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick, computed, onBeforeMount, reactive } from 'vue';
import { createPen } from '../core/pen';
import { createArr } from '../data-structures/arr';
import { createGrid } from '../data-structures/grid';
import { createTree } from '../data-structures/tree';
import { createLinkedList } from '../data-structures/linkedlist';

// 定义组件属性
interface CodeRunnerProps {
  speed?: number;
  width?: number;
  height?: number;
  isVisible?: boolean;
  penSpeed?: number;
  arrSpeed?: number;
  gridSpeed?: number;
  treeSpeed?: number;
  listSpeed?: number;
  penConfig?: any;
  arrConfig?: any;
  gridConfig?: any;
  treeConfig?: any;
  listConfig?: any;
}

const props = withDefaults(defineProps<CodeRunnerProps>(), {
  speed: 1,
  width: 600,
  height: 600,
  isVisible: true,
  penSpeed: 100,
  arrSpeed: 100,
  gridSpeed: 100,
  treeSpeed: 100,
  listSpeed: 100,
  penConfig: () => ({}),
  arrConfig: () => ({}),
  gridConfig: () => ({}),
  treeConfig: () => ({}),
  listConfig: () => ({})
});

// 引用与状态
const brushCanvas = ref<HTMLCanvasElement | null>(null);
const drawingCanvas = ref<HTMLCanvasElement | null>(null);
const canvasContainer = ref<HTMLDivElement | null>(null);
const isComponentMounted = ref(false);
const windowWidth = ref(window.innerWidth);
const windowHeight = ref(window.innerHeight);

// 计算自适应的宽度和高度
const adjustedWidth = computed(() => {
  if (windowWidth.value <= 768) {
    // 在小屏幕上使用更小的固定宽度
    return 240;
  }
  if (windowWidth.value <= 1024) {
    // 中等屏幕使用屏幕宽度的40%或最大450px
    return Math.min(450, windowWidth.value * 0.4);
  }
  return props.width;
});

const adjustedHeight = computed(() => {
  if (windowWidth.value <= 768) {
    // 小屏幕上使用更小的固定高度
    return 220;
  }
  if (windowWidth.value <= 1024) {
    return Math.min(400, adjustedWidth.value * 0.9);
  }
  return props.height;
});

// 监听窗口大小变化
const handleResize = () => {
  windowWidth.value = window.innerWidth;
  windowHeight.value = window.innerHeight;
  updateCanvasSize();
};

// 更新画布大小
const updateCanvasSize = () => {
  if (brushCanvas.value && drawingCanvas.value && canvasContainer.value) {
    const newWidth = adjustedWidth.value;
    const newHeight = adjustedHeight.value;

    // 获取内部容器
    const canvasInner = canvasContainer.value.querySelector('.canvas-inner');
    if (canvasInner) {
      // 设置内部容器大小
      (canvasInner as HTMLElement).style.width = `${newWidth}px`;
      (canvasInner as HTMLElement).style.height = `${newHeight}px`;
    }

    // 设置画布尺寸
    brushCanvas.value.width = newWidth;
    brushCanvas.value.height = newHeight;
    drawingCanvas.value.width = newWidth;
    drawingCanvas.value.height = newHeight;

    // 设置overlay尺寸
    const canvasOverlay = canvasContainer.value.querySelector('.canvas-overlay');
    if (canvasOverlay) {
      (canvasOverlay as HTMLElement).style.width = `${newWidth}px`;
      (canvasOverlay as HTMLElement).style.height = `${newHeight}px`;
    }

    // 清空画布
    const brushCtx = brushCanvas.value.getContext('2d');
    const drawingCtx = drawingCanvas.value.getContext('2d');
    if (brushCtx && drawingCtx) {
      brushCtx.clearRect(0, 0, newWidth, newHeight);
      drawingCtx.clearRect(0, 0, newWidth, newHeight);
    }
  }
};

// 添加内部状态来跟踪最新的配置和速度
const currentSpeedValues = ref({
  pen: props.penSpeed,
  arr: props.arrSpeed,
  grid: props.gridSpeed,
  tree: props.treeSpeed,
  list: props.listSpeed
});

const currentConfigValues = ref({
  pen: props.penConfig,
  arr: props.arrConfig,
  grid: props.gridConfig,
  tree: props.treeConfig,
  list: props.listConfig
});

// 用于控制DOM重建的状态
const canvasKey = ref(0);

// 添加对工具实例的引用
interface PenInstance {
  setSpeed: (speed: number) => void;
  lineWidth: (width: number) => any;
  setArrowSize: (size: number) => any;
  setArrowWidth: (width: number) => any;
  setArrowColor: (upColor: string, downColor: string) => void;
  setArrowOpacity: (opacity: number) => void;
  showArrow: (show: boolean) => any;
  brushDraw: () => Promise<void>;
  [key: string]: any;
}

interface ToolInstance {
  setSpeed: (speed: number) => void;
  setConfig: (config: any) => any;
  [key: string]: any;
}

const penInstance = ref<PenInstance | null>(null);
const arrInstance = ref<ToolInstance | null>(null);
const gridInstance = ref<ToolInstance | null>(null);
const treeInstance = ref<ToolInstance | null>(null);
const listInstance = ref<ToolInstance | null>(null);

// C++代码转JavaScript
const translateCppToJs = (cppCode: string): string => {
  let jsCode = cppCode;
  // 移除include语句
  jsCode = jsCode.replace(/#include\s*<.*>/g, '');
  // 类型转换
  jsCode = jsCode.replace(/\bint\b/g, 'let');
  jsCode = jsCode.replace(/\bdouble\b/g, 'let');
  jsCode = jsCode.replace(/\bstring\b/g, 'let');
  jsCode = jsCode.replace(/\bchar\b/g, 'let');
  // 移除using namespace语句
  jsCode = jsCode.replace(/using\s+namespace\s+std\s*;/g, '');
  return jsCode.trim();
};

// 在关键词前插入await
const translateAwait = (code: string, key = 'arr.'): string => {
  // 转义关键字中的正则特殊字符（如 '.' 转义为 '\.'）
  const escapedKey = key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

  // 匹配关键字前的边界（包括变量名、运算符、括号等），且前面没有 await 的情况
  const regex = new RegExp(
    `(?<!\\bawait\\s+)(\\b|\\s)(\\s*)(${escapedKey})`,
    'g',
  );

  return code.replace(regex, (match, boundary, whitespace, keyPart) => {
    // 插入带有空格的 await
    return `${boundary}${whitespace}await ${keyPart}`;
  });
};

// 为多个关键字插入await
const translateAwaitCode = (code: string, ...keys: string[]): string => {
  for (const key of keys) {
    code = translateAwait(code, key);
  }
  return code;
};

// 处理网格(grid)相关函数调用的特殊处理
const processGridCalls = (code: string): string => {
  // 处理grid相关API的调用
  code = translateAwait(code, 'grid.');

  // 替换一些常见的二维数组操作为grid函数
  // 例如: grid[i][j] = value -> grid.setValueRC(i, j, value)
  code = code.replace(/grid\[(\d+|[a-zA-Z_][a-zA-Z0-9_]*)\]\[(\d+|[a-zA-Z_][a-zA-Z0-9_]*)\]\s*=\s*([^;]+);/g,
    'grid.setValueRC($1, $2, $3);');

  // 读取grid[i][j]值替换为grid.getValueRC(i, j)
  code = code.replace(/grid\[(\d+|[a-zA-Z_][a-zA-Z0-9_]*)\]\[(\d+|[a-zA-Z_][a-zA-Z0-9_]*)\]/g,
    'await grid.getValueRC($1, $2)');

  return code;
};

// 转换C++输入语句
const convertCppInput = (cppLine: string): string => {
  // 保留原始缩进
  const indent = cppLine.match(/^\s*/)?.[0] || '';

  // 全局匹配所有cin输入表达式
  const cinPattern = /\bcin(\s*>>\s*[a-zA-Z_]+\s*)+;?/g;

  return cppLine.replace(cinPattern, (match) => {
    // 检查当前cin语句是否以分号结尾
    const hasSemicolon = match.trim().endsWith(';');

    // 提取所有变量名
    const variables: string[] = [];
    const varExtractor = />>\s*([a-zA-Z_]\w*)/g;
    let varMatch;

    while ((varMatch = varExtractor.exec(match)) !== null) {
      variables.push(varMatch[1]);
    }

    // 生成带缩进的赋值语句
    return variables.map(varName =>
      `${indent}${varName} = pen.getUserInput()${hasSemicolon ? ';' : ''}`
    ).join('\n');
  });
};

// 重新加载组件
const reloadComponent = async () => {
  // 通过改变key值触发组件重新渲染
  canvasKey.value++;
  await nextTick();
  console.log('画板已重新创建，key值:', canvasKey.value);
};

// 运行代码方法（供父组件调用）
const runCode = async (inCode: string = '') => {
  // 先销毁并重建画板
  await reloadComponent();

  // 确保画布已经创建
  await nextTick();

  if (!brushCanvas.value || !drawingCanvas.value) return;

  // 清空画布
  const brushCtx = brushCanvas.value.getContext('2d');
  const drawingCtx = drawingCanvas.value.getContext('2d');

  if (!brushCtx || !drawingCtx) return;

  // 使用计算后的调整尺寸而不是props
  const width = adjustedWidth.value;
  const height = adjustedHeight.value;

  brushCtx.clearRect(0, 0, width, height);
  drawingCtx.clearRect(0, 0, width, height);

  // 处理代码
  let runCode = inCode?.trim() ? inCode : '';
  runCode = translateCppToJs(runCode);
  runCode = convertCppInput(runCode);
  runCode = translateAwaitCode(runCode, 'pen.', 'arr.', 'grid.', 'tree.', 'list.');

  // 识别并处理二维数组(grid)的函数调用
  runCode = processGridCalls(runCode);

  // 创建画笔工具实例，使用专用速度设置
  const pen = createPen(
    drawingCtx,
    drawingCanvas.value,
    brushCtx,
    brushCanvas.value,
    () => currentSpeedValues.value.pen || props.speed
  );

  // 保存实例引用
  penInstance.value = pen;

  // 确保设置正确的速度
  pen.setSpeed(currentSpeedValues.value.pen || props.speed);

  // 应用画笔配置
  if (currentConfigValues.value.pen) {
    if (currentConfigValues.value.pen.lineWidth) pen.lineWidth(currentConfigValues.value.pen.lineWidth);
    // 处理箭头指示器相关设置
    if (currentConfigValues.value.pen.arrowSize) pen.setArrowSize(currentConfigValues.value.pen.arrowSize);
    if (currentConfigValues.value.pen.arrowWidth) pen.setArrowWidth(currentConfigValues.value.pen.arrowWidth);
    if (currentConfigValues.value.pen.arrowUpColor && currentConfigValues.value.pen.arrowDownColor) {
      pen.setArrowColor(currentConfigValues.value.pen.arrowUpColor, currentConfigValues.value.pen.arrowDownColor);
    }
    if (currentConfigValues.value.pen.arrowOpacity !== undefined) {
      pen.setArrowOpacity(currentConfigValues.value.pen.arrowOpacity);
    }
    // 确保指示器显示
    if (currentConfigValues.value.pen.showArrow) {
      pen.showArrow(true);
    }
  }

  // 创建数组工具，使用专用速度设置
  const arr = createArr(
    drawingCtx,
    drawingCanvas.value,
    () => currentSpeedValues.value.arr || props.speed
  );

  // 保存实例引用
  arrInstance.value = arr;

  // 创建网格工具，使用专用速度设置
  const grid = createGrid(
    drawingCtx,
    drawingCanvas.value,
    () => currentSpeedValues.value.grid || props.speed
  );

  // 保存实例引用
  gridInstance.value = grid;

  // 创建树结构工具，使用专用速度设置
  const tree = createTree(
    drawingCtx,
    drawingCanvas.value,
    () => currentSpeedValues.value.tree || props.speed
  );

  // 保存实例引用
  treeInstance.value = tree;

  // 创建链表工具，使用专用速度设置
  const list = createLinkedList(
    drawingCtx,
    drawingCanvas.value,
    () => currentSpeedValues.value.list || props.speed
  );

  // 保存实例引用
  listInstance.value = list;

  // 确保设置正确的速度
  arr.setSpeed(currentSpeedValues.value.arr || props.speed);
  grid.setSpeed(currentSpeedValues.value.grid || props.speed);
  tree.setSpeed(currentSpeedValues.value.tree || props.speed);
  list.setSpeed(currentSpeedValues.value.list || props.speed);

  // 应用各种工具的配置
  if (currentConfigValues.value.arr) arr.setConfig(currentConfigValues.value.arr);
  if (currentConfigValues.value.grid) grid.setConfig(currentConfigValues.value.grid);
  if (currentConfigValues.value.tree) tree.setConfig(currentConfigValues.value.tree);
  if (currentConfigValues.value.list) list.setConfig(currentConfigValues.value.list);

  // 异步启动执行
  setTimeout(async () => {
    try {
      // 立即绘制画笔指示器
      await pen.brushDraw();

      // 创建异步函数包装器，添加错误处理
      const asyncWrapper = `
        return (async (pen, arr, grid, tree, list) => {
          try {
            ${runCode}
          } catch (e) {
            console.error('用户代码执行错误:', e);
          }
        })(pen, arr, grid, tree, list);
      `;

      // 创建并执行异步函数
      const asyncFn = new Function('pen, arr, grid, tree, list', asyncWrapper);
      await asyncFn(pen, arr, grid, tree, list); // 等待用户代码执行完成
    } catch (e) {
      console.error('代码执行错误:', e);
    }
  }, 0);
};

// 组件挂载时初始化
onMounted(() => {
  isComponentMounted.value = true;
  window.addEventListener('resize', handleResize);
  nextTick(() => {
    updateCanvasSize();
  });
});

// 组件卸载前移除事件监听
onBeforeMount(() => {
  window.removeEventListener('resize', handleResize);
});

// 监听可见性变化，重新初始化画布
watch(() => props.isVisible, async (newVal) => {
  if (newVal) {
    await nextTick();
    updateCanvasSize();
  }
});

// 监听宽高变化，在必要时重置画布
watch([() => props.width, () => props.height], () => {
  nextTick(() => {
    updateCanvasSize();
  });
});

// 暴露方法给父组件
defineExpose({
  runCode,
  updateConfigs(speedValues: { pen: number; arr: number; grid: number; tree: number; list: number },
               configValues: { pen: any; arr: any; grid: any; tree: any; list: any }) {
    // 更新内部状态中的速度值
    if (speedValues) {
      currentSpeedValues.value = {
        pen: speedValues.pen,
        arr: speedValues.arr,
        grid: speedValues.grid,
        tree: speedValues.tree,
        list: speedValues.list
      };

      // 立即更新当前工具的速度设置
      if (penInstance.value) penInstance.value.setSpeed(speedValues.pen);
      if (arrInstance.value) arrInstance.value.setSpeed(speedValues.arr);
      if (gridInstance.value) gridInstance.value.setSpeed(speedValues.grid);
      if (treeInstance.value) treeInstance.value.setSpeed(speedValues.tree);
      if (listInstance.value) listInstance.value.setSpeed(speedValues.list);
    }

    // 更新内部状态中的配置值
    if (configValues) {
      currentConfigValues.value = {
        pen: configValues.pen,
        arr: configValues.arr,
        grid: configValues.grid,
        tree: configValues.tree,
        list: configValues.list
      };

      // 立即更新当前画笔的配置
      if (penInstance.value && configValues.pen) {
        if (configValues.pen.lineWidth) {
          penInstance.value.lineWidth(configValues.pen.lineWidth);
        }
        if (configValues.pen.arrowSize) {
          penInstance.value.setArrowSize(configValues.pen.arrowSize);
        }
        if (configValues.pen.arrowWidth) {
          penInstance.value.setArrowWidth(configValues.pen.arrowWidth);
        }
        if (configValues.pen.arrowUpColor && configValues.pen.arrowDownColor) {
          penInstance.value.setArrowColor(configValues.pen.arrowUpColor, configValues.pen.arrowDownColor);
        }
        if (configValues.pen.arrowOpacity !== undefined) {
          penInstance.value.setArrowOpacity(configValues.pen.arrowOpacity);
        }
        if (configValues.pen.showArrow !== undefined) {
          penInstance.value.showArrow(configValues.pen.showArrow);
        }

        // 重新绘制画笔指示器，立即展示变化
        penInstance.value.brushDraw();
      }

      // 为其他工具应用配置
      if (arrInstance.value && configValues.arr) {
        arrInstance.value.setConfig(configValues.arr);
      }
      if (gridInstance.value && configValues.grid) {
        gridInstance.value.setConfig(configValues.grid);
      }
      if (treeInstance.value && configValues.tree) {
        treeInstance.value.setConfig(configValues.tree);
      }
      if (listInstance.value && configValues.list) {
        listInstance.value.setConfig(configValues.list);
      }
    }

    console.log('CodeRunner配置已更新:', {speedValues, configValues});
  }
});
</script>

<style scoped>
.canvas-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
  margin: 0;
}

.canvas-inner {
  position: relative;
}

.canvas-overlay {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  box-shadow: 
    inset 0 0 0 0.5px rgba(255, 255, 255, 0.15),
    0 10px 30px rgba(0, 0, 0, 0.1),
    0 1px 2px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.42, 0, 0.58, 1);
  transform: translateZ(0);
}

.canvas-overlay::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  padding: 1px;
  background: linear-gradient(
    135deg, 
    rgba(255, 255, 255, 0.3) 0%, 
    var(--el-color-primary-light-7) 25%, 
    var(--el-color-primary) 50%, 
    var(--el-color-primary-light-5) 75%, 
    rgba(255, 255, 255, 0.3) 100%
  );
  background-size: 200% 200%;
  -webkit-mask: 
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
  animation: borderGlow 8s infinite alternate cubic-bezier(0.455, 0.03, 0.515, 0.955);
  opacity: 0.85;
}

.canvas-overlay::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  background: 
    linear-gradient(
      to bottom,
      rgba(255, 255, 255, 0.12) 0%,
      rgba(255, 255, 255, 0.05) 40%,
      rgba(255, 255, 255, 0) 80%
    );
  pointer-events: none;
}

.canvas-overlay:hover {
  box-shadow: 
    inset 0 0 0 0.5px rgba(255, 255, 255, 0.25),
    0 12px 40px rgba(0, 0, 0, 0.14),
    0 2px 6px rgba(0, 0, 0, 0.08);
  transform: translateY(-3px) scale(1.01) translateZ(0);
}

.canvas-overlay:hover::before {
  animation-duration: 4s;
  opacity: 1;
}

/* 添加新的边框动画 */
@keyframes borderGlow {
  0% {
    background-position: 0% 50%;
    filter: brightness(0.9);
  }
  50% {
    background-position: 100% 50%;
    filter: brightness(1.1);
  }
  100% {
    background-position: 0% 50%;
    filter: brightness(0.9);
  }
}

canvas {
  position: absolute;
  top: 0;
  left: 0;
  display: block;
  background-color: transparent;
  box-sizing: border-box;
}

/* 确保drawing-canvas在底层，brush-canvas在上层 */
canvas:first-child {
  z-index: 1;
}

canvas:last-child {
  z-index: 2;
}

@media (max-width: 768px) {
  .canvas-container {
    margin: 0;
    padding: 0;
    height: auto;
  }

  .canvas-inner {
    transform: scale(0.95);
    transform-origin: center center;
    margin: 5px auto;
    padding-bottom: 5px;
  }

  .canvas-overlay {
    width: 240px;
    height: 220px;
    margin: 0 auto;
    border-radius: 12px;
    backdrop-filter: blur(15px) saturate(160%);
    -webkit-backdrop-filter: blur(15px) saturate(160%);
  }

  .canvas-overlay::before,
  .canvas-overlay::after {
    border-radius: 12px;
  }
  
  .canvas-overlay:hover {
    transform: translateY(-2px) scale(1.005) translateZ(0);
  }

  canvas {
    border-width: 1px;
    max-width: 240px;
    max-height: 220px;
  }
}

@media (max-width: 480px) {
  .canvas-inner {
    transform: scale(0.9);
  }

  .canvas-overlay {
    width: 220px;
    height: 200px;
    border-radius: 10px;
    backdrop-filter: blur(12px) saturate(150%);
    -webkit-backdrop-filter: blur(12px) saturate(150%);
  }

  .canvas-overlay::before,
  .canvas-overlay::after {
    border-radius: 10px;
  }
  
  .canvas-overlay:hover {
    transform: translateY(-1px) scale(1.002) translateZ(0);
  }

  canvas {
    max-width: 220px;
    max-height: 200px;
  }
}
</style>
