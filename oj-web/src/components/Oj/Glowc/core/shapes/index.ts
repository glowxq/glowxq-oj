/**
 * 图形绘制函数模块
 * 导出所有图形绘制相关的函数
 */
import { ellipse as ellipseFunc, ellipseF as ellipseFFunc, orthogonal as orthogonalFunc, orthogonalF as orthogonalFFunc } from './advanced-shapes';
import { parallelogram as parallelogramFunc, parallelogramF as parallelogramFFunc, rhombus as rhombusFunc, rhombusF as rhombusFFunc } from './polygons';

// 图形绘制函数接口
export interface ShapeDrawingFunctions {
  // 基本图形
  circle: (radius?: number) => Promise<void>;
  circleF: (radius?: number) => Promise<void>;
  square: (size?: number) => Promise<void>;
  squareF: (size?: number) => Promise<void>;
  triangle: (length?: number) => Promise<void>;
  triangleF: (length?: number) => Promise<void>;
  
  // 更多形状
  ellipse: (radiusX?: number, radiusY?: number) => Promise<void>;
  ellipseF: (radiusX?: number, radiusY?: number) => Promise<void>;
  orthogonal: (width?: number, height?: number) => Promise<void>;
  orthogonalF: (width?: number, height?: number) => Promise<void>;
  parallelogram: (width?: number, height?: number) => Promise<void>;
  parallelogramF: (width?: number, height?: number) => Promise<void>;
  rhombus: (length?: number) => Promise<void>;
  rhombusF: (length?: number) => Promise<void>;
}

// 创建图形绘制函数
export const createShapeFunctions = (
  ctx: CanvasRenderingContext2D,
  canvas: HTMLCanvasElement,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  getSpeed: () => number,
  state: any,
  drawLine: (x1: number, y1: number, x2: number, y2: number) => Promise<void>,
  sleep: (ms: number) => Promise<void>,
  setDrawStyle: () => void,
): ShapeDrawingFunctions => {
  
  // 前进函数和旋转函数（用于square实现）
  const fd = async (distance: number = 50): Promise<void> => {
    const startX = state.x;
    const startY = state.y;
    
    // 计算新位置
    const newX = parseFloat((startX + Math.cos(state.angle) * distance).toFixed(2));
    const newY = parseFloat((startY + Math.sin(state.angle) * distance).toFixed(2));

    if (state.isPenDown) {
      await drawLine(startX, startY, newX, newY);
    }

    // 更新位置
    state.x = newX;
    state.y = newY;
  };

  const rt = async (degrees: number = 90): Promise<void> => {
    state.angle += degrees * (Math.PI / 180);
    await sleep(100);
  };

  // 移动到指定坐标
  const move = async (x: number, y: number): Promise<void> => {
    const startX = state.x;
    const startY = state.y;

    if (state.isPenDown) {
      await drawLine(startX, startY, x, y);
    }

    state.x = x;
    state.y = y;
    await sleep(100);
  };
  
  // 绘制圆形（不填充）
  const circle = async (radius: number = 50): Promise<void> => {
    setDrawStyle();
    
    if (state.finishMode) {
      ctx.beginPath();
      ctx.arc(state.x, state.y, radius, 0, Math.PI * 2);
      ctx.stroke();
      return;
    }

    // 带动画效果
    const steps = 36;
    const angleStep = (Math.PI * 2) / steps;

    for (let i = 0; i <= steps; i++) {
      const angle = i * angleStep;
      const endAngle = angle + angleStep;
      
      brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
      brushCtx.beginPath();
      brushCtx.arc(state.x, state.y, radius, 0, endAngle);
      brushCtx.stroke();
      
      await sleep(20);
    }

    // 最终绘制到主画布
    ctx.beginPath();
    ctx.arc(state.x, state.y, radius, 0, Math.PI * 2);
    ctx.stroke();
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };

  // 绘制填充圆形
  const circleF = async (radius: number = 50): Promise<void> => {
    setDrawStyle();
    
    if (state.finishMode) {
      ctx.beginPath();
      ctx.arc(state.x, state.y, radius, 0, Math.PI * 2);
      ctx.fill();
      ctx.stroke();
      return;
    }

    // 带动画效果
    const steps = 36;
    const angleStep = (Math.PI * 2) / steps;

    for (let i = 0; i <= steps; i++) {
      const angle = i * angleStep;
      const endAngle = angle + angleStep;
      
      brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
      brushCtx.beginPath();
      brushCtx.arc(state.x, state.y, radius, 0, endAngle);
      brushCtx.stroke();
      
      await sleep(20);
    }

    // 最终绘制到主画布
    ctx.beginPath();
    ctx.arc(state.x, state.y, radius, 0, Math.PI * 2);
    ctx.fillStyle = state.color;
    ctx.fill();
    ctx.stroke();
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };

  // 绘制正方形
  const square = async (size: number = 100): Promise<void> => {
    const half = size / 2;
    const originalX = state.x;
    const originalY = state.y;
    const originalAngle = state.angle;

    // 移动到正方形左上角
    state.isPenDown = false;
    await move(originalX - half, originalY - half);
    state.isPenDown = true;
    state.angle = 0;

    // 绘制四条边
    await fd(size);
    await rt(90);
    await fd(size);
    await rt(90);
    await fd(size);
    await rt(90);
    await fd(size);

    // 恢复原始位置和角度
    state.isPenDown = false;
    state.x = originalX;
    state.y = originalY;
    state.angle = originalAngle;
  };

  // 绘制填充正方形
  const squareF = async (size: number = 100): Promise<void> => {
    const half = size / 2;
    setDrawStyle();

    if (state.finishMode) {
      ctx.fillStyle = state.color;
      ctx.fillRect(state.x - half, state.y - half, size, size);
      ctx.strokeRect(state.x - half, state.y - half, size, size);
      return;
    }

    // 带动画效果
    const steps = 4;
    const step = size / steps;

    for (let i = 0; i <= steps; i++) {
      const currentSize = i * step;
      
      brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
      brushCtx.strokeRect(state.x - currentSize/2, state.y - currentSize/2, currentSize, currentSize);
      
      await sleep(50);
    }

    // 最终绘制到主画布
    ctx.fillStyle = state.color;
    ctx.fillRect(state.x - half, state.y - half, size, size);
    ctx.strokeRect(state.x - half, state.y - half, size, size);
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };

  // 绘制三角形
  const triangle = async (length: number = 50): Promise<void> => {
    const originalX = state.x;
    const originalY = state.y;
    const originalAngle = state.angle;
    
    // 绘制三角形
    await fd(length);
    await rt(120);
    await fd(length);
    await rt(120);
    await fd(length);
    
    // 恢复原始位置和角度
    state.isPenDown = false;
    state.x = originalX;
    state.y = originalY;
    state.angle = originalAngle;
    state.isPenDown = true;
  };

  // 绘制填充三角形
  const triangleF = async (length: number = 50): Promise<void> => {
    setDrawStyle();
    const height = length * Math.sqrt(3) / 2;
    const half = length / 2;

    if (state.finishMode) {
      ctx.beginPath();
      ctx.moveTo(state.x, state.y - height * 2/3);
      ctx.lineTo(state.x - half, state.y + height / 3);
      ctx.lineTo(state.x + half, state.y + height / 3);
      ctx.closePath();
      ctx.fill();
      ctx.stroke();
      return;
    }

    // 带动画效果
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    brushCtx.beginPath();
    brushCtx.moveTo(state.x, state.y - height * 2/3);
    brushCtx.lineTo(state.x - half, state.y + height / 3);
    await sleep(100);
    
    brushCtx.lineTo(state.x + half, state.y + height / 3);
    await sleep(100);
    
    brushCtx.closePath();
    brushCtx.stroke();
    await sleep(100);
    
    // 最终绘制到主画布
    ctx.beginPath();
    ctx.moveTo(state.x, state.y - height * 2/3);
    ctx.lineTo(state.x - half, state.y + height / 3);
    ctx.lineTo(state.x + half, state.y + height / 3);
    ctx.closePath();
    ctx.fillStyle = state.color;
    ctx.fill();
    ctx.stroke();
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };

  // 绘制椭圆
  const ellipse = async (radiusX: number = 50, radiusY: number = 30): Promise<void> => {
    await ellipseFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, radiusX, radiusY);
  };

  // 绘制填充椭圆
  const ellipseF = async (radiusX: number = 50, radiusY: number = 30): Promise<void> => {
    await ellipseFFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, radiusX, radiusY);
  };

  // 绘制矩形
  const orthogonal = async (width: number = 100, height: number = 50): Promise<void> => {
    await orthogonalFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, width, height);
  };

  // 绘制填充矩形
  const orthogonalF = async (width: number = 100, height: number = 50): Promise<void> => {
    await orthogonalFFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, width, height);
  };

  // 绘制平行四边形
  const parallelogram = async (width: number = 100, height: number = 50): Promise<void> => {
    await parallelogramFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, width, height);
  };

  // 绘制填充平行四边形
  const parallelogramF = async (width: number = 100, height: number = 50): Promise<void> => {
    await parallelogramFFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, width, height);
  };

  // 绘制菱形
  const rhombus = async (length: number = 80): Promise<void> => {
    await rhombusFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, length);
  };

  // 绘制填充菱形
  const rhombusF = async (length: number = 80): Promise<void> => {
    await rhombusFFunc(ctx, brushCtx, brushCanvas, state, sleep, setDrawStyle, length);
  };
  
  return {
    circle,
    circleF,
    square,
    squareF,
    triangle,
    triangleF,
    ellipse,
    ellipseF,
    orthogonal,
    orthogonalF,
    parallelogram,
    parallelogramF,
    rhombus,
    rhombusF
  };
}; 