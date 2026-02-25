/**
 * 创建链表可视化工具
 * @param ctx 画布上下文
 * @param canvas 画布元素
 * @param getSpeed 获取速度的函数
 * @returns 链表可视化工具对象
 */
export const createLinkedList = (
  ctx: CanvasRenderingContext2D,
  canvas: HTMLCanvasElement,
  getSpeed: () => number,
) => {
  // 链表节点类型定义
  interface ListNode {
    id: number;               // 节点唯一标识符
    value: number | string;   // 节点值
    next: ListNode | null;    // 下一个节点
    prev: ListNode | null;    // 上一个节点（双向链表）
    x: number;                // 绘制时的X坐标
    y: number;                // 绘制时的Y坐标
    highlighted: boolean;     // 是否高亮显示
  }

  // 链表配置常量
  const LIST_CONFIG = {
    NODE_RADIUS: 30,                 // 节点半径
    NODE_SPACING: 100,               // 节点间距
    FONT_SIZE: 13,                   // 字体大小
    NODE_PADDING: 5,                 // 节点内边距
    FONT_FAMILY: 'Arial, sans-serif', // 字体
    TEXT_COLOR: '#000000',           // 文本颜色
    NODE_COLOR: '#FFFFFF',           // 节点颜色
    HIGHLIGHT_COLOR: '#FFD700',      // 高亮颜色
    LINE_COLOR: '#333333',           // 连线颜色
    ANIMATION_DURATION: 500,         // 动画持续时间(ms)
    HIGHLIGHT_DURATION: 2000,        // 高亮持续时间(ms)
    DOUBLE_LINKED: false,            // 是否为双向链表
    START_X: 50,                     // 起始X坐标
    START_Y: 100,                    // 起始Y坐标
    ARROW_SIZE: 10,                  // 箭头大小
  };

  // 链表状态
  const state = {
    head: null as ListNode | null,   // 链表头节点
    tail: null as ListNode | null,   // 链表尾节点
    nodeCount: 0,                    // 节点计数器（用于生成唯一ID）
    speed: 1,                        // 速度控制因子，默认为1，范围1-1000
    finishMode: false,               // 瞬间完成模式
    isDoubleLinked: false,           // 是否为双向链表
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
      console.log('链表已启用瞬间完成模式');
    } else {
      state.finishMode = false;
    }
    
    console.log('链表速度已设置为:', state.speed);
  };

  // 更新初始速度
  setSpeed(getSpeed());

  // 清除画布
  const clear = (): void => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    state.head = null;
    state.tail = null;
    state.nodeCount = 0;
    console.log('链表已清空');
  };

  // 计算链表布局
  const calculateLayout = (): void => {
    let current = state.head;
    let index = 0;
    let lastNodeX = 0;
    
    while (current) {
      // 处理链表换行绘制
      const maxNodesPerRow = Math.floor((canvas.width - LIST_CONFIG.START_X) / LIST_CONFIG.NODE_SPACING);
      const row = Math.floor(index / maxNodesPerRow);
      const col = index % maxNodesPerRow;
      
      current.x = LIST_CONFIG.START_X + col * LIST_CONFIG.NODE_SPACING;
      current.y = LIST_CONFIG.START_Y + row * LIST_CONFIG.NODE_SPACING * 1.5;
      
      lastNodeX = current.x;
      current = current.next;
      index++;
    }
  };

  // 绘制箭头
  const drawArrow = (fromX: number, fromY: number, toX: number, toY: number, isBackward = false): void => {
    const headLength = LIST_CONFIG.ARROW_SIZE;
    const dx = toX - fromX;
    const dy = toY - fromY;
    const angle = Math.atan2(dy, dx);
    
    // 绘制线段
    ctx.beginPath();
    ctx.moveTo(fromX, fromY);
    ctx.lineTo(toX, toY);
    ctx.strokeStyle = isBackward ? '#555555' : LIST_CONFIG.LINE_COLOR;
    ctx.lineWidth = isBackward ? 1 : 2;
    
    if (isBackward) {
      ctx.setLineDash([5, 3]);
    } else {
      ctx.setLineDash([]);
    }
    
    ctx.stroke();
    ctx.setLineDash([]);
    
    // 绘制箭头头部
    ctx.beginPath();
    ctx.moveTo(toX, toY);
    ctx.lineTo(
      toX - headLength * Math.cos(angle - Math.PI / 6),
      toY - headLength * Math.sin(angle - Math.PI / 6)
    );
    ctx.lineTo(
      toX - headLength * Math.cos(angle + Math.PI / 6),
      toY - headLength * Math.sin(angle + Math.PI / 6)
    );
    ctx.closePath();
    ctx.fillStyle = isBackward ? '#555555' : LIST_CONFIG.LINE_COLOR;
    ctx.fill();
  };

  // 绘制节点
  const drawNode = (node: ListNode): void => {
    // 绘制节点圆圈
    ctx.beginPath();
    ctx.arc(node.x, node.y, LIST_CONFIG.NODE_RADIUS, 0, Math.PI * 2);
    ctx.fillStyle = node.highlighted ? LIST_CONFIG.HIGHLIGHT_COLOR : LIST_CONFIG.NODE_COLOR;
    ctx.fill();
    ctx.strokeStyle = LIST_CONFIG.LINE_COLOR;
    ctx.lineWidth = 2;
    ctx.stroke();
    
    // 绘制节点文本
    ctx.fillStyle = LIST_CONFIG.TEXT_COLOR;
    // 使用NODE_RADIUS计算一致的字体大小
    const fontSize = Math.floor(LIST_CONFIG.NODE_RADIUS * 0.8);
    ctx.font = `${fontSize}px ${LIST_CONFIG.FONT_FAMILY}`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(String(node.value), node.x, node.y);
  };

  // 绘制链表
  const render = (): void => {
    // 清空画布
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // 计算布局
    calculateLayout();
    
    let current = state.head;
    
    // 先绘制所有连接线
    while (current && current.next) {
      // 计算箭头起点和终点
      const fromX = current.x + LIST_CONFIG.NODE_RADIUS;
      const fromY = current.y;
      const toX = current.next.x - LIST_CONFIG.NODE_RADIUS;
      const toY = current.next.y;
      
      drawArrow(fromX, fromY, toX, toY);
      
      // 如果是双向链表，绘制反向箭头
      if (state.isDoubleLinked && current.next) {
        drawArrow(toX, toY + 10, fromX, fromY + 10, true);
      }
      
      current = current.next;
    }
    
    // 再绘制所有节点（确保节点显示在箭头上层）
    current = state.head;
    while (current) {
      drawNode(current);
      current = current.next;
    }
    
    // 绘制NULL终止标记
    if (state.tail) {
      const nullX = state.tail.x + LIST_CONFIG.NODE_SPACING / 2;
      const nullY = state.tail.y;
      
      // 使用与节点绘制相同的字体大小计算方式
      const fontSize = Math.floor(LIST_CONFIG.NODE_RADIUS * 0.8);
      ctx.font = `${fontSize}px ${LIST_CONFIG.FONT_FAMILY}`;
      ctx.fillStyle = LIST_CONFIG.TEXT_COLOR;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText('NULL', nullX, nullY);
    }
  };

  // 创建新节点
  const createNode = (value: number | string): ListNode => {
    const newNode: ListNode = {
      id: state.nodeCount++,
      value,
      next: null,
      prev: null,
      x: 0,
      y: 0,
      highlighted: false
    };
    return newNode;
  };

  // 初始化链表
  const init = async (isDouble = false): Promise<void> => {
    clear();
    state.isDoubleLinked = isDouble;
    render();
    
    console.log(`已初始化${state.isDoubleLinked ? '双向' : '单向'}链表`);
    return Promise.resolve();
  };

  // 设置链表类型
  const setType = async (isDouble: boolean): Promise<void> => {
    state.isDoubleLinked = isDouble;
    render();
    
    console.log(`已设置为${state.isDoubleLinked ? '双向' : '单向'}链表`);
    return Promise.resolve();
  };

  // 在头部添加节点
  const addHead = async (value: number | string): Promise<number> => {
    value = await Promise.resolve(value);
    
    const newNode = createNode(value);
    
    if (!state.head) {
      state.head = newNode;
      state.tail = newNode;
    } else {
      newNode.next = state.head;
      if (state.isDoubleLinked) {
        state.head.prev = newNode;
      }
      state.head = newNode;
    }
    
    // 计算布局并渲染
    calculateLayout();
    
    // 高亮新添加的节点
    newNode.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.HIGHLIGHT_DURATION);
    }
    
    newNode.highlighted = false;
    render();
    
    console.log(`已在链表头部添加节点: ${value}`);
    return newNode.id;
  };

  // 在尾部添加节点
  const addTail = async (value: number | string): Promise<number> => {
    value = await Promise.resolve(value);
    
    const newNode = createNode(value);
    
    if (!state.head) {
      state.head = newNode;
      state.tail = newNode;
    } else {
      if (state.tail) {
        state.tail.next = newNode;
        if (state.isDoubleLinked) {
          newNode.prev = state.tail;
        }
        state.tail = newNode;
      }
    }
    
    // 计算布局并渲染
    calculateLayout();
    
    // 高亮新添加的节点
    newNode.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.HIGHLIGHT_DURATION);
    }
    
    newNode.highlighted = false;
    render();
    
    console.log(`已在链表尾部添加节点: ${value}`);
    return newNode.id;
  };

  // 在指定位置插入节点
  const insertAt = async (index: number, value: number | string): Promise<number> => {
    index = Number(await Promise.resolve(index));
    value = await Promise.resolve(value);
    
    if (index < 0) {
      console.error('索引不能为负数');
      return -1;
    }
    
    // 如果是空链表或索引为0，在头部添加
    if (!state.head || index === 0) {
      return addHead(value);
    }
    
    let current = state.head;
    let currentIndex = 0;
    
    // 找到插入位置的前一个节点
    while (current.next && currentIndex < index - 1) {
      current = current.next;
      currentIndex++;
    }
    
    // 创建新节点
    const newNode = createNode(value);
    
    // 如果要插入的位置超过当前链表长度，则在尾部添加
    if (!current.next && currentIndex === index - 1) {
      return addTail(value);
    }
    
    // 执行插入操作
    newNode.next = current.next;
    if (state.isDoubleLinked && current.next) {
      newNode.prev = current;
      current.next.prev = newNode;
    }
    current.next = newNode;
    
    // 更新尾节点（如果需要）
    if (!newNode.next) {
      state.tail = newNode;
    }
    
    // 计算布局并渲染
    calculateLayout();
    
    // 高亮新插入的节点
    newNode.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.HIGHLIGHT_DURATION);
    }
    
    newNode.highlighted = false;
    render();
    
    console.log(`已在位置${index}插入节点: ${value}`);
    return newNode.id;
  };

  // 查找节点
  const findNode = (id: number): ListNode | null => {
    let current = state.head;
    
    while (current) {
      if (current.id === id) {
        return current;
      }
      current = current.next;
    }
    
    return null;
  };

  // 根据值查找节点
  const findNodeByValue = (value: number | string): ListNode | null => {
    let current = state.head;
    
    while (current) {
      if (current.value === value) {
        return current;
      }
      current = current.next;
    }
    
    return null;
  };

  // 删除头节点
  const removeHead = async (): Promise<boolean> => {
    if (!state.head) {
      console.error('链表为空，无法删除头节点');
      return false;
    }
    
    // 高亮要删除的节点
    state.head.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.ANIMATION_DURATION);
    }
    
    const removedValue = state.head.value;
    
    // 删除头节点
    if (state.head === state.tail) {
      // 如果只有一个节点
      state.head = null;
      state.tail = null;
    } else {
      // 有多个节点
      const newHead = state.head.next;
      if (newHead && state.isDoubleLinked) {
        newHead.prev = null;
      }
      state.head = newHead;
    }
    
    render();
    
    console.log(`已删除头节点，值为: ${removedValue}`);
    return true;
  };

  // 删除尾节点
  const removeTail = async (): Promise<boolean> => {
    if (!state.head || !state.tail) {
      console.error('链表为空，无法删除尾节点');
      return false;
    }
    
    // 高亮要删除的节点
    state.tail.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.ANIMATION_DURATION);
    }
    
    const removedValue = state.tail.value;
    
    // 如果只有一个节点
    if (state.head === state.tail) {
      state.head = null;
      state.tail = null;
      render();
      console.log(`已删除尾节点，值为: ${removedValue}`);
      return true;
    }
    
    // 找到倒数第二个节点
    let current = state.head;
    while (current.next !== state.tail) {
      current = current.next!;
    }
    
    // 更新倒数第二个节点的next为null
    current.next = null;
    state.tail = current;
    
    render();
    
    console.log(`已删除尾节点，值为: ${removedValue}`);
    return true;
  };

  // 删除指定位置的节点
  const removeAt = async (index: number): Promise<boolean> => {
    index = Number(await Promise.resolve(index));
    
    if (!state.head) {
      console.error('链表为空，无法删除节点');
      return false;
    }
    
    if (index < 0) {
      console.error('索引不能为负数');
      return false;
    }
    
    // 如果要删除头节点
    if (index === 0) {
      return removeHead();
    }
    
    let current = state.head;
    let currentIndex = 0;
    
    // 找到要删除节点的前一个节点
    while (current.next && currentIndex < index - 1) {
      current = current.next;
      currentIndex++;
    }
    
    // 如果索引超出了范围
    if (!current.next) {
      console.error(`索引${index}超出了链表范围`);
      return false;
    }
    
    // 高亮要删除的节点
    current.next.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.ANIMATION_DURATION);
    }
    
    const removedValue = current.next.value;
    
    // 如果要删除的是尾节点
    if (current.next === state.tail) {
      state.tail = current;
    }
    
    // 处理双向链表的prev引用
    if (state.isDoubleLinked && current.next.next) {
      current.next.next.prev = current;
    }
    
    // 删除节点
    current.next = current.next.next;
    
    render();
    
    console.log(`已删除位置${index}的节点，值为: ${removedValue}`);
    return true;
  };

  // 根据值删除节点
  const removeByValue = async (value: number | string): Promise<boolean> => {
    value = await Promise.resolve(value);
    
    if (!state.head) {
      console.error('链表为空，无法删除节点');
      return false;
    }
    
    // 如果头节点就是要删除的节点
    if (state.head.value === value) {
      return removeHead();
    }
    
    let current = state.head;
    
    // 查找要删除的节点的前一个节点
    while (current.next && current.next.value !== value) {
      current = current.next;
    }
    
    // 如果没有找到要删除的节点
    if (!current.next) {
      console.error(`未找到值为${value}的节点`);
      return false;
    }
    
    // 高亮要删除的节点
    current.next.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.ANIMATION_DURATION);
    }
    
    // 如果要删除的是尾节点
    if (current.next === state.tail) {
      state.tail = current;
    }
    
    // 处理双向链表的prev引用
    if (state.isDoubleLinked && current.next.next) {
      current.next.next.prev = current;
    }
    
    // 删除节点
    current.next = current.next.next;
    
    render();
    
    console.log(`已删除值为${value}的节点`);
    return true;
  };

  // 查找节点值
  const getValue = async (index: number): Promise<number | string | null> => {
    index = Number(await Promise.resolve(index));
    
    if (!state.head) {
      console.error('链表为空');
      return null;
    }
    
    if (index < 0) {
      console.error('索引不能为负数');
      return null;
    }
    
    let current = state.head;
    let currentIndex = 0;
    
    // 查找指定位置的节点
    while (current && currentIndex < index) {
      current = current.next!;
      currentIndex++;
      
      if (!current) {
        console.error(`索引${index}超出了链表范围`);
        return null;
      }
    }
    
    // 高亮节点
    current.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.HIGHLIGHT_DURATION);
    }
    
    // 恢复正常状态
    current.highlighted = false;
    render();
    
    return current.value;
  };

  // 设置节点值
  const setValue = async (index: number, value: number | string): Promise<boolean> => {
    index = Number(await Promise.resolve(index));
    value = await Promise.resolve(value);
    
    if (!state.head) {
      console.error('链表为空');
      return false;
    }
    
    if (index < 0) {
      console.error('索引不能为负数');
      return false;
    }
    
    let current = state.head;
    let currentIndex = 0;
    
    // 查找指定位置的节点
    while (current && currentIndex < index) {
      current = current.next!;
      currentIndex++;
      
      if (!current) {
        console.error(`索引${index}超出了链表范围`);
        return false;
      }
    }
    
    // 高亮节点
    current.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.ANIMATION_DURATION);
    }
    
    // 更新值
    current.value = value;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.HIGHLIGHT_DURATION);
    }
    
    // 恢复正常状态
    current.highlighted = false;
    render();
    
    console.log(`节点${index}的值已更新为: ${value}`);
    return true;
  };

  // 高亮节点
  const highlight = async (index: number): Promise<boolean> => {
    index = Number(await Promise.resolve(index));
    
    if (!state.head) {
      console.error('链表为空');
      return false;
    }
    
    if (index < 0) {
      console.error('索引不能为负数');
      return false;
    }
    
    let current = state.head;
    let currentIndex = 0;
    
    // 查找指定位置的节点
    while (current && currentIndex < index) {
      current = current.next!;
      currentIndex++;
      
      if (!current) {
        console.error(`索引${index}超出了链表范围`);
        return false;
      }
    }
    
    // 高亮节点
    current.highlighted = true;
    render();
    
    if (!state.finishMode) {
      await sleepWithSpeed(LIST_CONFIG.HIGHLIGHT_DURATION);
    }
    
    // 恢复正常状态
    current.highlighted = false;
    render();
    
    return true;
  };

  // 获取链表长度
  const getLength = (): number => {
    let count = 0;
    let current = state.head;
    
    while (current) {
      count++;
      current = current.next;
    }
    
    return count;
  };

  // 获取链表遍历结果
  const traverse = async (): Promise<(number | string)[]> => {
    if (!state.head) {
      return [];
    }
    
    const result: (number | string)[] = [];
    let current = state.head;
    
    while (current) {
      // 高亮当前节点
      current.highlighted = true;
      render();
      
      result.push(current.value);
      
      if (!state.finishMode) {
        await sleepWithSpeed(LIST_CONFIG.ANIMATION_DURATION);
      }
      
      // 恢复正常状态
      current.highlighted = false;
      render();
      
      current = current.next!;
    }
    
    console.log('链表遍历结果:', result);
    return result;
  };

  // 反向遍历双向链表
  const reverseTraverse = async (): Promise<(number | string)[]> => {
    if (!state.tail || !state.isDoubleLinked) {
      console.error('链表为空或不是双向链表');
      return [];
    }
    
    const result: (number | string)[] = [];
    let current = state.tail;
    
    while (current) {
      // 高亮当前节点
      current.highlighted = true;
      render();
      
      result.push(current.value);
      
      if (!state.finishMode) {
        await sleepWithSpeed(LIST_CONFIG.ANIMATION_DURATION);
      }
      
      // 恢复正常状态
      current.highlighted = false;
      render();
      
      current = current.prev!;
    }
    
    console.log('链表反向遍历结果:', result);
    return result;
  };

  // 立即完成所有动画
  const finish = (): void => {
    state.finishMode = true;
    console.log('链表已启用瞬间完成模式');
  };

  // 设置配置项
  const setConfig = (config: any): void => {
    if (!config) return;
    
    // 更新配置项
    if (config.nodeRadius) LIST_CONFIG.NODE_RADIUS = config.nodeRadius;
    if (config.nodeSpacing) LIST_CONFIG.NODE_SPACING = config.nodeSpacing;
    if (config.fontSize) LIST_CONFIG.FONT_SIZE = config.fontSize;
    if (config.nodePadding) LIST_CONFIG.NODE_PADDING = config.nodePadding;
    if (config.textColor) LIST_CONFIG.TEXT_COLOR = config.textColor;
    if (config.nodeColor) LIST_CONFIG.NODE_COLOR = config.nodeColor;
    if (config.highlightColor) LIST_CONFIG.HIGHLIGHT_COLOR = config.highlightColor;
    if (config.lineColor) LIST_CONFIG.LINE_COLOR = config.lineColor;
    if (config.animationDuration) LIST_CONFIG.ANIMATION_DURATION = config.animationDuration;
    if (config.highlightDuration) LIST_CONFIG.HIGHLIGHT_DURATION = config.highlightDuration;
    if (config.startX) LIST_CONFIG.START_X = config.startX;
    if (config.startY) LIST_CONFIG.START_Y = config.startY;
    if (config.arrowSize) LIST_CONFIG.ARROW_SIZE = config.arrowSize;
    
    // 如果有节点，重新渲染以应用新配置
    if (state.head) {
      render();
    }
  };

  return {
    // 基本操作
    init,
    clear,
    setType,
    
    // 节点操作
    addHead,
    addTail,
    insertAt,
    removeHead,
    removeTail,
    removeAt,
    removeByValue,
    getValue,
    setValue,
    highlight,
    
    // 获取链表信息
    getLength,
    traverse,
    reverseTraverse,
    
    // 工具函数
    sleep,
    setSpeed,
    finish,
    setConfig
  };
}; 