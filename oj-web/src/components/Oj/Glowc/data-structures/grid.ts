/**
 * 创建二维网格可视化工具
 * @param ctx 画布上下文
 * @param canvas 画布元素
 * @param getSpeed 获取速度的函数
 * @returns 二维网格可视化工具对象
 */
export const createGrid = (
  ctx: CanvasRenderingContext2D,
  canvas: HTMLCanvasElement,
  getSpeed: () => number,
) => {
  // 网格配置常量
  const GRID_CONFIG = {
    CELL_SIZE: 50,                   // 单元格大小
    PADDING: 10,                     // 内边距
    FONT_SIZE: 12,                   // 字体大小
    FONT_FAMILY: 'Arial, sans-serif', // 字体
    TEXT_COLOR: '#000000',           // 文本颜色
    HIGHLIGHT_COLOR: '#FFD700',     // 高亮颜色 - 使用金色更符合原版
    BORDER_COLOR: '#333333',         // 边框颜色
    BACKGROUND_COLOR: '#ffffff',     // 背景颜色
    HIGHLIGHT_DURATION: 2000,        // 高亮持续时间(ms)
    SET_DURATION: 1000               // 设置值的等待时间(ms)
  };

  // 网格状态
  const state = {
    grid: [] as (number | string)[][],  // 网格数据
    rows: 0,                         // 行数
    cols: 0,                         // 列数
    cells: [] as {x: number, y: number, row: number, col: number, value: number | string, highlighted: boolean}[], // 单元格存储
    speed: 1,                        // 速度控制因子，默认为1，范围1-1000
    finishMode: false                // 瞬间完成模式
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
      console.log('网格已启用瞬间完成模式');
    } else {
      state.finishMode = false;
    }
    
    console.log('网格速度已设置为:', state.speed);
  };

  // 更新初始速度
  setSpeed(getSpeed());

  // 绘制单个单元格
  const drawCell = (cell: {x: number, y: number, value: number | string, highlighted: boolean}): void => {
    const size = GRID_CONFIG.CELL_SIZE;
    const center = size / 2;

    // 清除旧内容
    ctx.clearRect(cell.x, cell.y, size, size);

    // 绘制背景
    ctx.fillStyle = cell.highlighted ? GRID_CONFIG.HIGHLIGHT_COLOR : GRID_CONFIG.BACKGROUND_COLOR;
    ctx.fillRect(cell.x, cell.y, size, size);

    // 绘制边框
    ctx.strokeStyle = GRID_CONFIG.BORDER_COLOR;
    ctx.strokeRect(cell.x, cell.y, size, size);

    // 绘制文本
    ctx.fillStyle = GRID_CONFIG.TEXT_COLOR;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.font = `${Math.floor(size * 0.6)}px ${GRID_CONFIG.FONT_FAMILY}`;
    ctx.fillText(
      String(cell.value), 
      cell.x + center, 
      cell.y + center
    );
  };

  // 清除画布
  const clear = async (): Promise<void> => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    state.grid = [];
    state.cells = [];
    state.rows = 0;
    state.cols = 0;
    console.log('网格已清空');
  };

  // 构建二维网格
  const build = async (rows: number, cols: number, initialValue?: number | string): Promise<void> => {
    // 确保参数是基本类型
    rows = Number(await Promise.resolve(rows));
    cols = Number(await Promise.resolve(cols));
    
    // 处理initialValue，保持字符串类型不变
    if (initialValue !== undefined) {
      initialValue = await Promise.resolve(initialValue);
      // 不再强制转换为数字
    }
    
    console.log('构建网格 build', rows, cols, initialValue, typeof initialValue);
    
    // 处理输入参数，限制网格大小
    rows = Math.max(1, Math.min(20, rows));
    cols = Math.max(1, Math.min(20, cols));
    
    // 计算单元格大小，确保适配画布
    const maxWidth = canvas.width - 2 * GRID_CONFIG.PADDING;
    const maxHeight = canvas.height - 2 * GRID_CONFIG.PADDING;
    
    // 如果网格太大，自动调整单元格大小
    const calculatedCellSize = Math.min(
      Math.floor(maxWidth / cols),
      Math.floor(maxHeight / rows),
      GRID_CONFIG.CELL_SIZE
    );
    
    // 清空画布和数据
    await clear();
    
    state.rows = rows;
    state.cols = cols;
    state.grid = Array(rows).fill(0).map(() => Array(cols).fill(0));
    
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
    offscreenCtx.font = `${Math.floor(calculatedCellSize * 0.6)}px ${GRID_CONFIG.FONT_FAMILY}`;
    
    // 创建单元格并在离屏画布上绘制
    for (let r = 0; r < rows; r++) {
      for (let c = 0; c < cols; c++) {
        const x = c * calculatedCellSize + GRID_CONFIG.PADDING;
        const y = r * calculatedCellSize + GRID_CONFIG.PADDING;
        
        let cellValue;
        if (initialValue !== undefined) {
          cellValue = initialValue; // 直接使用指定的初始值
        } else {
          cellValue = Math.floor(Math.random() * 100); // 随机值范围为0-99
        }
        
        // 保存到网格数据
        state.grid[r][c] = cellValue;
        
        // 创建单元格对象
        const cell = {
          x, 
          y,
          row: r,
          col: c,
          value: cellValue,
          highlighted: false
        };
        
        state.cells.push(cell);
        
        // 在离屏画布上绘制
        const center = calculatedCellSize / 2;
        
        // 清除区域
        offscreenCtx.clearRect(x, y, calculatedCellSize, calculatedCellSize);
        
        // 绘制背景
        offscreenCtx.fillStyle = GRID_CONFIG.BACKGROUND_COLOR;
        offscreenCtx.fillRect(x, y, calculatedCellSize, calculatedCellSize);
        
        // 绘制边框
        offscreenCtx.strokeStyle = GRID_CONFIG.BORDER_COLOR;
        offscreenCtx.strokeRect(x, y, calculatedCellSize, calculatedCellSize);
        
        // 绘制文本
        offscreenCtx.fillStyle = GRID_CONFIG.TEXT_COLOR;
        offscreenCtx.fillText(
          String(cellValue), 
          x + center, 
          y + center
        );
      }
    }
    
    // 将离屏画布内容一次性绘制到主画布
    ctx.drawImage(offscreenCanvas, 0, 0);
    
    // 添加适当的延迟
    if (!state.finishMode) {
      await sleepWithSpeed(200);
    }
    
    console.log('网格构建完成，行数:', rows, '列数:', cols, '单元格数:', state.cells.length);
  };

  // 通过行列定位单元格索引
  const getCellIndexByRowCol = (row: number, col: number): number => {
    if (row < 0 || row >= state.rows || col < 0 || col >= state.cols) {
      return -1;
    }
    return row * state.cols + col;
  };

  // 高亮单元格（通过行列）
  const brightRC = async (row: number, col: number): Promise<void> => {
    // 确保参数是基本类型
    row = Number(await Promise.resolve(row));
    col = Number(await Promise.resolve(col));
    
    console.log('高亮单元格 brightRC', row, col);
    
    const index = getCellIndexByRowCol(row, col);
    if (index === -1) {
      console.warn(`无效行列: [${row}, ${col}]`);
      return;
    }
    
    await brightIdx(index);
  };

  // 高亮单元格（通过索引）
  const brightIdx = async (index: number): Promise<void> => {
    // 确保参数是基本类型
    index = Number(await Promise.resolve(index));
    
    console.log('高亮单元格 brightIdx', index);
    
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
      await sleepWithSpeed(GRID_CONFIG.HIGHLIGHT_DURATION);
    }
    
    // 恢复正常状态
    toggleHighlight(false);
  };

  // 设置单元格值（通过行列）
  const setValueRC = async (row: number, col: number, value: number | string): Promise<void> => {
    // 确保参数是基本类型
    row = Number(await Promise.resolve(row));
    col = Number(await Promise.resolve(col));
    value = await Promise.resolve(value);
    
    console.log('设置单元格值 setValueRC', row, col, value);
    
    const index = getCellIndexByRowCol(row, col);
    if (index === -1) {
      console.warn(`无效行列: [${row}, ${col}]`);
      return;
    }
    
    // 保存值到网格数据
    state.grid[row][col] = value;
    
    await setValueIdx(index, value);
  };

  // 设置单元格值（通过索引）
  const setValueIdx = async (index: number, value: number | string): Promise<void> => {
    // 确保参数是基本类型
    index = Number(await Promise.resolve(index));
    value = await Promise.resolve(value);
    
    console.log('设置单元格值 setValueIdx', index, value);
    
    if (index < 0 || index >= state.cells.length) {
      console.warn(`无效索引: ${index}`);
      return;
    }
    
    if (!state.finishMode) {
      await sleepWithSpeed(GRID_CONFIG.SET_DURATION);
    }
    
    // 更新单元格显示
    state.cells[index].value = value;
    
    // 更新网格数据
    const cell = state.cells[index];
    state.grid[cell.row][cell.col] = value;
    
    drawCell(state.cells[index]);
  };

  // 获取单元格值（通过行列）
  const getValueRC = async (row: number, col: number): Promise<number | string> => {
    // 确保参数是基本类型
    row = Number(await Promise.resolve(row));
    col = Number(await Promise.resolve(col));
    
    console.log('获取单元格值 getValueRC', row, col);
    
    const index = getCellIndexByRowCol(row, col);
    if (index === -1) {
      console.warn(`无效行列: [${row}, ${col}]`);
      return 0;
    }
    
    return getValueIdx(index);
  };

  // 获取单元格值（通过索引）
  const getValueIdx = async (index: number): Promise<number | string> => {
    // 确保参数是基本类型
    index = Number(await Promise.resolve(index));
    
    console.log('获取单元格值 getValueIdx', index);
    
    if (index < 0 || index >= state.cells.length) {
      console.warn(`无效索引: ${index}`);
      return 0;
    }

    // 先高亮显示，然后返回值
    await brightIdx(index);
    return state.cells[index].value;
  };

  // 获取行数
  const getRows = async (): Promise<number> => {
    console.log('获取行数 getRows:', state.rows);
    return state.rows;
  };

  // 获取列数
  const getCols = async (): Promise<number> => {
    console.log('获取列数 getCols:', state.cols);
    return state.cols;
  };

  // 获取特定行的起始索引
  const getRowStart = async (row: number): Promise<number> => {
    // 确保参数是基本类型
    row = Number(await Promise.resolve(row));
    
    console.log('获取行起始索引 getRowStart', row);
    
    if (row < 0 || row >= state.rows) {
      console.warn(`无效行号: ${row}`);
      return 0;
    }
    
    return row * state.cols;
  };

  // 获取特定行的结束索引（不包含）
  const getRowEnd = async (row: number): Promise<number> => {
    // 确保参数是基本类型
    row = Number(await Promise.resolve(row));
    
    console.log('获取行结束索引 getRowEnd', row);
    
    if (row < 0 || row >= state.rows) {
      console.warn(`无效行号: ${row}`);
      return 0;
    }
    
    return (row + 1) * state.cols;
  };

  // 立即完成所有动画
  const finish = (): void => {
    state.finishMode = true;
    console.log('grid已启用瞬间完成模式');
  };

  // 在适当位置添加 setConfig 方法
  const setConfig = (config: any) => {
    if (!config) return;
    
    // 更新配置项
    if (config.cellSize) GRID_CONFIG.CELL_SIZE = config.cellSize;
    if (config.padding) GRID_CONFIG.PADDING = config.padding;
    if (config.fontSize) GRID_CONFIG.FONT_SIZE = config.fontSize;
    if (config.textColor) GRID_CONFIG.TEXT_COLOR = config.textColor;
    if (config.highlightColor) GRID_CONFIG.HIGHLIGHT_COLOR = config.highlightColor;
    if (config.borderColor) GRID_CONFIG.BORDER_COLOR = config.borderColor;
    if (config.backgroundColor) GRID_CONFIG.BACKGROUND_COLOR = config.backgroundColor;
    if (config.highlightDuration) GRID_CONFIG.HIGHLIGHT_DURATION = config.highlightDuration;
    if (config.setDuration) GRID_CONFIG.SET_DURATION = config.setDuration;
    
    return self;
  };

  return {
    // 基本操作
    clear,
    build,
    
    // 数据操作
    brightRC,
    brightIdx,
    setValueRC,
    setValueIdx,
    getValueRC,
    getValueIdx,
    getRows,
    getCols,
    getRowStart,
    getRowEnd,
    
    // 工具函数
    sleep,
    setSpeed,
    finish,
    setConfig
  };
}; 