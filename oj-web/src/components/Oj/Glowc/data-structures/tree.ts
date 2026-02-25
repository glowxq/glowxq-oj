/**
 * 创建树可视化工具
 * @param ctx 画布上下文
 * @param canvas 画布元素
 * @param getSpeed 获取速度的函数
 * @returns 树可视化工具对象
 */
export const createTree = (
  ctx: CanvasRenderingContext2D,
  canvas: HTMLCanvasElement,
  getSpeed: () => number,
) => {
  // 树节点类型定义
  interface TreeNode {
    id: number;      // 节点唯一标识符
    value: number | string;  // 节点值
    children: TreeNode[];    // 子节点
    x: number;       // 绘制时的X坐标
    y: number;       // 绘制时的Y坐标
    highlighted: boolean;    // 是否高亮显示
    parent: TreeNode | null; // 父节点引用
  }

  // 树配置常量
  const TREE_CONFIG = {
    NODE_RADIUS: 25,                 // 节点半径
    LEVEL_HEIGHT: 80,                // 层级高度
    FONT_SIZE: 13,                   // 字体大小
    NODE_PADDING: 5,                 // 节点内边距
    FONT_FAMILY: 'Arial, sans-serif', // 字体
    TEXT_COLOR: '#000000',           // 文本颜色
    NODE_COLOR: '#FFFFFF',           // 节点颜色
    HIGHLIGHT_COLOR: '#FFD700',      // 高亮颜色
    LINE_COLOR: '#333333',           // 连线颜色
    ANIMATION_DURATION: 500,         // 动画持续时间(ms)
    HIGHLIGHT_DURATION: 2000,        // 高亮持续时间(ms)
  };

  // 树状态
  const state = {
    root: null as TreeNode | null,  // 根节点
    nodeCount: 0,                   // 节点计数器（用于生成唯一ID）
    speed: 1,                       // 速度控制因子，默认为1，范围1-1000
    finishMode: false,              // 瞬间完成模式
    width: canvas.width,            // 画布宽度
    height: canvas.height,          // 画布高度
    maxDepth: 0,                    // 树的最大深度
  };

  // 内部等待函数 - 受速度影响
  const sleepWithSpeed = async (ms: number): Promise<void> => {
    // 瞬间完成模式下不等待
    if (state.finishMode) return Promise.resolve();
    
    // 如果速度为1000，几乎是瞬间完成
    if (state.speed >= 1000) {
      return Promise.resolve();
    }
    
    // 使用对数关系而不是线性关系调整等待时间
    // 默认中速（500）对应1000ms基准等待时间
    const baseTime = 1000; // 中速基准等待时间
    const speedRatio = 500 / state.speed; // 中速(500)作为基准比率
    const adjustedMs = ms * speedRatio;
    
    // 使用requestAnimationFrame优化动画性能
    if (adjustedMs < 16.7) { // 如果等待时间小于一帧(约16.7ms@60fps)
      return new Promise(resolve => setTimeout(resolve, adjustedMs));
    } else {
      // 使用requestAnimationFrame处理较长的等待时间，可减少卡顿感
      const startTime = performance.now();
      return new Promise(resolve => {
        const step = (currentTime: number) => {
          const elapsedTime = currentTime - startTime;
          if (elapsedTime >= adjustedMs) {
            resolve();
          } else {
            requestAnimationFrame(step);
          }
        };
        requestAnimationFrame(step);
      });
    }
  };

  // 用户直接调用的等待函数 - 不受速度影响
  const sleep = async (ms: number): Promise<void> => {
    // 不做速度调整，直接等待指定的毫秒数
    return new Promise(resolve => setTimeout(resolve, ms));
  };

  // 设置速度
  const setSpeed = (newSpeed: number): void => {
    // 确保速度在1-1000范围内
    state.speed = Math.max(1, Math.min(1000, newSpeed));
    
    // 如果速度为1000，直接启用瞬间完成模式
    if (state.speed >= 1000) {
      state.finishMode = true;
      console.log('树结构已启用瞬间完成模式');
    } else {
      state.finishMode = false;
    }
    
    console.log('树结构速度已设置为:', state.speed);
  };

  // 更新初始速度
  setSpeed(getSpeed());

  // 清除画布
  const clear = (): void => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    state.root = null;
    state.nodeCount = 0;
    state.maxDepth = 0;
    console.log('树已清空');
  };

  // 计算树布局位置
  const calculateTreeLayout = (node: TreeNode, depth: number = 0, hPos: number = 0, totalWidth: number = 1): void => {
    state.maxDepth = Math.max(state.maxDepth, depth);
    
    // 更新节点位置
    node.y = depth * TREE_CONFIG.LEVEL_HEIGHT + 50;
    
    if (node.children.length === 0) {
      // 叶子节点
      node.x = hPos * (state.width / totalWidth) + (state.width / totalWidth) / 2;
      return;
    }
    
    // 计算子节点位置
    let currentPos = hPos;
    const childWidth = 1 / totalWidth / node.children.length;
    
    for (let i = 0; i < node.children.length; i++) {
      calculateTreeLayout(node.children[i], depth + 1, currentPos, totalWidth * node.children.length);
      currentPos += childWidth;
    }
    
    // 根据子节点位置计算当前节点X坐标
    if (node.children.length > 0) {
      const leftMost = node.children[0].x;
      const rightMost = node.children[node.children.length - 1].x;
      node.x = (leftMost + rightMost) / 2;
    }
  };

  // 绘制连线
  const drawLine = (fromNode: TreeNode, toNode: TreeNode): void => {
    ctx.beginPath();
    ctx.moveTo(fromNode.x, fromNode.y);
    ctx.lineTo(toNode.x, toNode.y);
    ctx.strokeStyle = TREE_CONFIG.LINE_COLOR;
    ctx.lineWidth = 2;
    ctx.stroke();
  };

  // 绘制节点
  const drawNode = (node: TreeNode): void => {
    // 绘制节点圆圈
    ctx.beginPath();
    ctx.arc(node.x, node.y, TREE_CONFIG.NODE_RADIUS, 0, Math.PI * 2);
    ctx.fillStyle = node.highlighted ? TREE_CONFIG.HIGHLIGHT_COLOR : TREE_CONFIG.NODE_COLOR;
    ctx.fill();
    ctx.strokeStyle = TREE_CONFIG.LINE_COLOR;
    ctx.lineWidth = 2;
    ctx.stroke();
    
    // 绘制节点文本
    ctx.fillStyle = TREE_CONFIG.TEXT_COLOR;
    // 使用NODE_RADIUS计算一致的字体大小
    const fontSize = Math.floor(TREE_CONFIG.NODE_RADIUS * 0.8);
    ctx.font = `${fontSize}px ${TREE_CONFIG.FONT_FAMILY}`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(String(node.value), node.x, node.y);
  };

  // 递归绘制树
  const drawTree = (node: TreeNode): void => {
    if (!node) return;
    
    // 先绘制到子节点的连线
    for (const child of node.children) {
      drawLine(node, child);
    }
    
    // 递归绘制子节点
    for (const child of node.children) {
      drawTree(child);
    }
    
    // 绘制当前节点
    drawNode(node);
  };

  // 渲染树
  const render = (): void => {
    if (!state.root) return;
    
    // 清空画布
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // 计算布局
    calculateTreeLayout(state.root);
    
    // 绘制树
    drawTree(state.root);
  };

  // 创建新节点
  const createNode = (value: number | string, parent: TreeNode | null = null): TreeNode => {
    const newNode: TreeNode = {
      id: state.nodeCount++,
      value,
      children: [],
      x: 0,
      y: 0,
      highlighted: false,
      parent
    };
    return newNode;
  };

  // 初始化树 - 创建根节点
  const init = async (rootValue: number | string): Promise<void> => {
    clear();
    rootValue = await Promise.resolve(rootValue);
    
    state.root = createNode(rootValue);
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(TREE_CONFIG.ANIMATION_DURATION);
    }
    
    console.log(`树已初始化，根节点值: ${rootValue}`);
    return Promise.resolve();
  };

  // 查找节点
  const findNode = (id: number): TreeNode | null => {
    if (!state.root) return null;
    
    // 广度优先搜索查找节点
    const queue: TreeNode[] = [state.root];
    
    while (queue.length > 0) {
      const current = queue.shift()!;
      
      if (current.id === id) {
        return current;
      }
      
      for (const child of current.children) {
        queue.push(child);
      }
    }
    
    return null;
  };

  // 添加子节点
  const addChild = async (parentId: number, value: number | string): Promise<number> => {
    parentId = Number(await Promise.resolve(parentId));
    value = await Promise.resolve(value);
    
    const parent = findNode(parentId);
    
    if (!parent) {
      console.error(`找不到ID为${parentId}的父节点`);
      return -1;
    }
    
    const newNode = createNode(value, parent);
    parent.children.push(newNode);
    
    render();
    
    // 高亮新添加的节点
    newNode.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(TREE_CONFIG.HIGHLIGHT_DURATION);
    }
    
    newNode.highlighted = false;
    render();
    
    console.log(`已添加子节点: ${value}，父节点: ${parent.value}`);
    return newNode.id;
  };

  // 移除节点
  const removeNode = async (id: number): Promise<boolean> => {
    id = Number(await Promise.resolve(id));
    
    if (!state.root) return false;
    
    // 处理根节点特殊情况
    if (state.root.id === id) {
      clear();
      return true;
    }
    
    // 查找要删除的节点及其父节点
    const nodeToRemove = findNode(id);
    
    if (!nodeToRemove || !nodeToRemove.parent) {
      console.error(`找不到ID为${id}的节点或其父节点`);
      return false;
    }
    
    // 高亮要删除的节点
    nodeToRemove.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(TREE_CONFIG.HIGHLIGHT_DURATION / 2);
    }
    
    // 从父节点的children数组中移除
    const parent = nodeToRemove.parent;
    parent.children = parent.children.filter(child => child.id !== id);
    
    render();
    
    console.log(`已移除节点: ${nodeToRemove.value}`);
    return true;
  };

  // 高亮显示节点
  const highlight = async (id: number): Promise<boolean> => {
    id = Number(await Promise.resolve(id));
    
    const node = findNode(id);
    
    if (!node) {
      console.error(`找不到ID为${id}的节点`);
      return false;
    }
    
    // 设置高亮状态
    node.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(TREE_CONFIG.HIGHLIGHT_DURATION);
    }
    
    // 恢复正常状态
    node.highlighted = false;
    render();
    
    return true;
  };

  // 查找节点值
  const getValue = async (id: number): Promise<number | string | null> => {
    id = Number(await Promise.resolve(id));
    
    const node = findNode(id);
    
    if (!node) {
      console.error(`找不到ID为${id}的节点`);
      return null;
    }
    
    // 高亮节点然后返回值
    await highlight(id);
    
    return node.value;
  };

  // 设置节点值
  const setValue = async (id: number, value: number | string): Promise<boolean> => {
    id = Number(await Promise.resolve(id));
    value = await Promise.resolve(value);
    
    const node = findNode(id);
    
    if (!node) {
      console.error(`找不到ID为${id}的节点`);
      return false;
    }
    
    // 高亮节点
    node.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(TREE_CONFIG.ANIMATION_DURATION);
    }
    
    // 更新值
    node.value = value;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(TREE_CONFIG.HIGHLIGHT_DURATION);
    }
    
    // 恢复正常状态
    node.highlighted = false;
    render();
    
    console.log(`节点${id}的值已更新为: ${value}`);
    return true;
  };

  // 前序遍历
  const preOrder = async (): Promise<(number | string)[]> => {
    if (!state.root) return [];
    
    const result: (number | string)[] = [];
    
    const traverse = async (node: TreeNode): Promise<void> => {
      // 访问当前节点
      result.push(node.value);
      
      // 高亮当前节点
      node.highlighted = true;
      render();
      
      if (!state.finishMode) {
        await sleepWithSpeed(TREE_CONFIG.ANIMATION_DURATION);
      }
      
      // 取消高亮
      node.highlighted = false;
      render();
      
      // 递归遍历子节点
      for (const child of node.children) {
        await traverse(child);
      }
    };
    
    await traverse(state.root);
    
    console.log('前序遍历结果:', result);
    return result;
  };

  // 后序遍历
  const postOrder = async (): Promise<(number | string)[]> => {
    if (!state.root) return [];
    
    const result: (number | string)[] = [];
    
    const traverse = async (node: TreeNode): Promise<void> => {
      // 递归遍历子节点
      for (const child of node.children) {
        await traverse(child);
      }
      
      // 访问当前节点
      result.push(node.value);
      
      // 高亮当前节点
      node.highlighted = true;
      render();
      
      if (!state.finishMode) {
        await sleepWithSpeed(TREE_CONFIG.ANIMATION_DURATION);
      }
      
      // 取消高亮
      node.highlighted = false;
      render();
    };
    
    await traverse(state.root);
    
    console.log('后序遍历结果:', result);
    return result;
  };

  // 层序遍历
  const levelOrder = async (): Promise<(number | string)[]> => {
    if (!state.root) return [];
    
    const result: (number | string)[] = [];
    const queue: TreeNode[] = [state.root];
    
    while (queue.length > 0) {
      const node = queue.shift()!;
      
      // 访问当前节点
      result.push(node.value);
      
      // 高亮当前节点
      node.highlighted = true;
      render();
      
      if (!state.finishMode) {
        await sleepWithSpeed(TREE_CONFIG.ANIMATION_DURATION);
      }
      
      // 取消高亮
      node.highlighted = false;
      render();
      
      // 将子节点加入队列
      for (const child of node.children) {
        queue.push(child);
      }
    }
    
    console.log('层序遍历结果:', result);
    return result;
  };

  // 获取根节点ID
  const getRootId = (): number => {
    return state.root ? state.root.id : -1;
  };

  // 立即完成所有动画
  const finish = (): void => {
    state.finishMode = true;
    console.log('树已启用瞬间完成模式');
  };

  // 在适当位置添加 setConfig 方法
  const setConfig = (config: any) => {
    if (!config) return;
    
    // 更新配置项
    if (config.nodeRadius) TREE_CONFIG.NODE_RADIUS = config.nodeRadius;
    if (config.levelHeight) TREE_CONFIG.LEVEL_HEIGHT = config.levelHeight;
    if (config.fontSize) TREE_CONFIG.FONT_SIZE = config.fontSize;
    if (config.nodePadding) TREE_CONFIG.NODE_PADDING = config.nodePadding;
    if (config.textColor) TREE_CONFIG.TEXT_COLOR = config.textColor;
    if (config.nodeColor) TREE_CONFIG.NODE_COLOR = config.nodeColor;
    if (config.highlightColor) TREE_CONFIG.HIGHLIGHT_COLOR = config.highlightColor;
    if (config.lineColor) TREE_CONFIG.LINE_COLOR = config.lineColor;
    if (config.animationDuration) TREE_CONFIG.ANIMATION_DURATION = config.animationDuration;
    if (config.highlightDuration) TREE_CONFIG.HIGHLIGHT_DURATION = config.highlightDuration;
    
    return self;
  };

  return {
    // 基本操作
    init,
    clear,
    
    // 节点操作
    addChild,
    removeNode,
    highlight,
    getValue,
    setValue,
    
    // 遍历方法
    preOrder,
    postOrder,
    levelOrder,
    
    // 获取根节点
    getRootId,
    
    // 工具函数
    sleep,
    setSpeed,
    finish,
    setConfig
  };
}; 