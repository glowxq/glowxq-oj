/**
 * 创建数组可视化工具
 * @param ctx 画布上下文
 * @param canvas 画布元素
 * @param getSpeed 获取速度的函数
 * @returns 数组可视化工具对象
 */
export const createArr = (
  ctx: CanvasRenderingContext2D,
  canvas: HTMLCanvasElement,
  getSpeed: () => number,
) => {
  // 数组配置常量
  const ARRAY_CONFIG = {
    CELL_SIZE: 50,                   // 单元格大小
    PADDING: 10,                     // 内边距
    FONT_SIZE: 14,                   // 字体大小
    FONT_FAMILY: 'Arial, sans-serif', // 字体
    TEXT_COLOR: '#000000',           // 文本颜色
    HIGHLIGHT_COLOR: '#FFD700',     // 高亮颜色 - 使用金色更符合原版
    BORDER_COLOR: '#333333',         // 边框颜色
    BACKGROUND_COLOR: '#ffffff',     // 背景颜色
    HIGHLIGHT_DURATION: 2000,        // 高亮持续时间(ms)
    SET_DURATION: 1000               // 设置值的等待时间(ms)
  };

  // 数组状态
  const state = {
    grid: [] as (number | string)[][],  // 网格数据
    rows: 0,                           // 行数
    cols: 0,                           // 列数
    cells: [] as {x: number, y: number, value: number | string, highlighted: boolean}[], // 一维数组存储
    speed: 1,                          // 速度控制因子，默认为1，范围1-1000
    finishMode: false                  // 瞬间完成模式
  };

  // 几何计算工具
  const ArrayGeometry = {
    /**
     * 计算网格位置
     * @param index - 单元索引
     * @returns {{x: number, y: number}} 坐标对象
     */
    getGridPosition(index: number): {x: number, y: number} {
      const maxWidth = canvas.width - 2 * ARRAY_CONFIG.PADDING;
      const columnsPerRow = Math.floor(maxWidth / ARRAY_CONFIG.CELL_SIZE);
      
      const column = index % columnsPerRow;
      const row = Math.floor(index / columnsPerRow);
      
      return {
        x: column * ARRAY_CONFIG.CELL_SIZE + ARRAY_CONFIG.PADDING,
        y: row * ARRAY_CONFIG.CELL_SIZE + ARRAY_CONFIG.PADDING
      };
    }
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
      console.log('数组已启用瞬间完成模式');
    } else {
      state.finishMode = false;
    }
    
    console.log('数组速度已设置为:', state.speed);
  };

  // 更新初始速度
  setSpeed(getSpeed());

  // 绘制单个单元格
  const drawCell = (cell: {x: number, y: number, value: number | string, highlighted: boolean}): void => {
    const size = ARRAY_CONFIG.CELL_SIZE;
    const center = size / 2;

    // 清除旧内容
    ctx.clearRect(cell.x, cell.y, size, size);

    // 绘制背景
    ctx.fillStyle = cell.highlighted ? ARRAY_CONFIG.HIGHLIGHT_COLOR : ARRAY_CONFIG.BACKGROUND_COLOR;
    ctx.fillRect(cell.x, cell.y, size, size);

    // 绘制边框
    ctx.strokeStyle = ARRAY_CONFIG.BORDER_COLOR;
    ctx.strokeRect(cell.x, cell.y, size, size);

    // 绘制文本
    ctx.fillStyle = ARRAY_CONFIG.TEXT_COLOR;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    // 使用与build方法相同的字体大小计算
    ctx.font = `${Math.floor(ARRAY_CONFIG.CELL_SIZE * 0.6)}px ${ARRAY_CONFIG.FONT_FAMILY}`;
    ctx.fillText(
      String(cell.value), 
      cell.x + center, 
      cell.y + center
    );
  };

  // 清除画布
  const clear = async (): Promise<void> => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    state.cells = [];
    state.rows = 0;
    state.cols = 0;
    console.log('数组已清空');
  };

  // 构建数组 - 支持一维数组
  const build = async (size: number, initialValue?: number | string): Promise<void> => {
    // 确保参数是基本类型
    size = Number(await Promise.resolve(size));
    
    // 处理initialValue，保持字符串类型不变
    if (initialValue !== undefined) {
      initialValue = await Promise.resolve(initialValue);
      // 不再强制转换为数字
    }
    
    console.log('构建数组 build', size, initialValue, typeof initialValue);
    
    // 处理输入参数，限制数组大小
    size = Math.max(1, Math.min(100, size));
    
    // 清空画布和数据
    await clear();
    
    // 计算合适的单元格布局
    const maxWidth = canvas.width - 2 * ARRAY_CONFIG.PADDING;
    const columnsPerRow = Math.floor(maxWidth / ARRAY_CONFIG.CELL_SIZE);
    
    // 确定实际行数和列数
    if (size <= columnsPerRow) {
      state.rows = 1;
      state.cols = size;
    } else {
      state.cols = columnsPerRow;
      state.rows = Math.ceil(size / columnsPerRow);
    }
    
    // 初始化cells数组
    state.cells = [];
    
    // 设置canvas绘制属性
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.font = `${Math.floor(ARRAY_CONFIG.CELL_SIZE * 0.6)}px ${ARRAY_CONFIG.FONT_FAMILY}`;

    // 创建离屏画布以优化渲染
    const offscreenCanvas = document.createElement('canvas');
    offscreenCanvas.width = canvas.width;
    offscreenCanvas.height = canvas.height;
    const offscreenCtx = offscreenCanvas.getContext('2d');
    
    if (!offscreenCtx) {
      console.error('无法创建离屏画布');
      return;
    }
    
    // 配置离屏画布样式
    offscreenCtx.textAlign = 'center';
    offscreenCtx.textBaseline = 'middle';
    offscreenCtx.font = `${Math.floor(ARRAY_CONFIG.CELL_SIZE * 0.6)}px ${ARRAY_CONFIG.FONT_FAMILY}`;

    // 创建单元格并在离屏画布上绘制
    for (let i = 0; i < size; i++) {
      const position = ArrayGeometry.getGridPosition(i);
      
      let cellValue;
      if (initialValue !== undefined) {
        cellValue = initialValue; // 直接使用指定的初始值
      } else {
        cellValue = Math.floor(Math.random() * 100); // 随机值范围为0-99
      }
      
      const cell = { 
        x: position.x, 
        y: position.y, 
        value: cellValue,
        highlighted: false 
      };
      
      state.cells.push(cell);
      
      // 在离屏画布上绘制
      const cellSize = ARRAY_CONFIG.CELL_SIZE;
      const center = cellSize / 2;
      
      // 清除区域
      offscreenCtx.clearRect(cell.x, cell.y, cellSize, cellSize);
      
      // 绘制背景
      offscreenCtx.fillStyle = ARRAY_CONFIG.BACKGROUND_COLOR;
      offscreenCtx.fillRect(cell.x, cell.y, cellSize, cellSize);
      
      // 绘制边框
      offscreenCtx.strokeStyle = ARRAY_CONFIG.BORDER_COLOR;
      offscreenCtx.strokeRect(cell.x, cell.y, cellSize, cellSize);
      
      // 绘制文本
      offscreenCtx.fillStyle = ARRAY_CONFIG.TEXT_COLOR;
      offscreenCtx.fillText(
        String(cell.value), 
        cell.x + center, 
        cell.y + center
      );
    }
    
    // 将离屏画布内容一次性绘制到主画布
    ctx.drawImage(offscreenCanvas, 0, 0);
    
    // 添加适当的延迟
    if (!state.finishMode) {
      await sleepWithSpeed(200);
    }
    
    console.log('数组构建完成，大小:', size, '单元格数:', state.cells.length);
  };

  // 高亮单元格
  const bright = async (index: number): Promise<void> => {
    // 确保参数是基本类型
    index = Number(await Promise.resolve(index));
    
    console.log('高亮单元格 bright', index);
    
    if (index < 0 || index >= state.cells.length) {
      console.warn(`无效索引: ${index}`);
      return;
    }
    
    // 高亮切换函数
    const toggleHighlight = (highlighted: boolean) => {
      state.cells[index].highlighted = highlighted;
      drawCell(state.cells[index]);
    };
    
    // 设置高亮状态
    toggleHighlight(true);
    
    if (!state.finishMode) {
      // 等待高亮持续时间
      await sleepWithSpeed(ARRAY_CONFIG.HIGHLIGHT_DURATION);
    }
    
    // 恢复正常状态
    toggleHighlight(false);
  };

  // 设置单元格值
  const setValue = async (index: number, value: number | string): Promise<void> => {
    // 确保参数是基本类型
    index = Number(await Promise.resolve(index));
    value = await Promise.resolve(value);
    
    console.log('设置单元格值 setValue', index, value, typeof value);
    
    if (index < 0 || index >= state.cells.length) {
      console.warn(`无效索引: ${index}`);
      return;
    }
    
    if (!state.finishMode) {
      await sleepWithSpeed(ARRAY_CONFIG.SET_DURATION);
    }
    
    state.cells[index].value = value;
    drawCell(state.cells[index]);
  };

  // 获取单元格值
  const getValue = async (index: number): Promise<number | string> => {
    // 确保参数是基本类型
    index = Number(await Promise.resolve(index));
    
    console.log('获取单元格值 getValue', index);
    
    if (index < 0 || index >= state.cells.length) {
      console.warn(`无效索引: ${index}`);
      return 0;
    }

    // 先高亮显示，然后返回值
    await bright(index);
    return state.cells[index].value;
  };

  // 获取首个索引位置
  const begin = async (): Promise<number> => {
    console.log('获取首个索引位置 begin');
    return 0;
  };

  // 获取末尾索引位置
  const end = async (): Promise<number> => {
    console.log('获取末尾索引位置 end', state.cells.length);
    return state.cells.length;
  };

  // 立即完成所有动画
  const finish = (): void => {
    state.finishMode = true;
    console.log('arr已启用瞬间完成模式');
  };

  // 设置配置项
  const setConfig = (config: any) => {
    if (!config) return;
    
    // 更新配置项
    if (config.cellSize) ARRAY_CONFIG.CELL_SIZE = config.cellSize;
    if (config.padding) ARRAY_CONFIG.PADDING = config.padding;
    if (config.fontSize) ARRAY_CONFIG.FONT_SIZE = config.fontSize;
    if (config.textColor) ARRAY_CONFIG.TEXT_COLOR = config.textColor;
    if (config.highlightColor) ARRAY_CONFIG.HIGHLIGHT_COLOR = config.highlightColor;
    if (config.borderColor) ARRAY_CONFIG.BORDER_COLOR = config.borderColor;
    if (config.backgroundColor) ARRAY_CONFIG.BACKGROUND_COLOR = config.backgroundColor;
    if (config.highlightDuration) ARRAY_CONFIG.HIGHLIGHT_DURATION = config.highlightDuration;
    if (config.setDuration) ARRAY_CONFIG.SET_DURATION = config.setDuration;
    
    return self;
  };

  return {
    // 基本操作
    clear,
    build,
    
    // 数据操作
    setValue,
    getValue,
    bright,
    begin,
    end,
    
    // 工具函数
    sleep,
    setSpeed,
    finish,
    setConfig
  };
}; 