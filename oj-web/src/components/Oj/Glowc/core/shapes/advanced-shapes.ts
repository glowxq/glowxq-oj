/**
 * 高级图形绘制函数
 * 包含椭圆、矩形、平行四边形和菱形等高级图形
 */

// 绘制空心椭圆
export const ellipse = async (
  ctx: CanvasRenderingContext2D,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  state: any,
  sleep: (ms: number) => Promise<void>,
  setDrawStyle: () => void,
  radiusX: number = 50, 
  radiusY: number = 30
): Promise<void> => {
  setDrawStyle();
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.beginPath();
    // 椭圆旋转90度使长轴朝上，与正方形方向一致
    ctx.ellipse(0, 0, radiusX, radiusY, Math.PI / 2, 0, Math.PI * 2);
    ctx.stroke();
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 36;
  const angleStep = (Math.PI * 2) / steps;
  
  for (let i = 0; i <= steps; i++) {
    const endAngle = i * angleStep;
    
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.beginPath();
    brushCtx.ellipse(0, 0, radiusX, radiusY, Math.PI / 2, 0, endAngle);
    brushCtx.stroke();
    brushCtx.restore();
    
    await sleep(20);
  }
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.beginPath();
  ctx.ellipse(0, 0, radiusX, radiusY, Math.PI / 2, 0, Math.PI * 2);
  ctx.stroke();
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
};

// 绘制填充椭圆
export const ellipseF = async (
  ctx: CanvasRenderingContext2D,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  state: any,
  sleep: (ms: number) => Promise<void>,
  setDrawStyle: () => void,
  radiusX: number = 50, 
  radiusY: number = 30
): Promise<void> => {
  setDrawStyle();
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.fillStyle = state.color;
    ctx.beginPath();
    ctx.ellipse(0, 0, radiusX, radiusY, Math.PI / 2, 0, Math.PI * 2);
    ctx.fill();
    ctx.stroke();
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 36;
  const angleStep = (Math.PI * 2) / steps;
  
  for (let i = 0; i <= steps; i++) {
    const endAngle = i * angleStep;
    
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.beginPath();
    brushCtx.ellipse(0, 0, radiusX, radiusY, Math.PI / 2, 0, endAngle);
    brushCtx.stroke();
    brushCtx.restore();
    
    await sleep(20);
  }
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.fillStyle = state.color;
  ctx.beginPath();
  ctx.ellipse(0, 0, radiusX, radiusY, Math.PI / 2, 0, Math.PI * 2);
  ctx.fill();
  ctx.stroke();
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
};

// 绘制空心矩形（正交方向）
export const orthogonal = async (
  ctx: CanvasRenderingContext2D,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  state: any,
  sleep: (ms: number) => Promise<void>,
  setDrawStyle: () => void,
  width: number = 100, 
  height: number = 50
): Promise<void> => {
  setDrawStyle();
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.beginPath();
    ctx.rect(-width / 2, -height / 2, width, height);
    ctx.stroke();
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 4;
  
  // 第一步: 横向绘制
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2, -height / 2);
  brushCtx.lineTo(width / 2, -height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第二步: 添加右边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2, -height / 2);
  brushCtx.lineTo(width / 2, -height / 2);
  brushCtx.lineTo(width / 2, height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第三步: 添加底边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2, -height / 2);
  brushCtx.lineTo(width / 2, -height / 2);
  brushCtx.lineTo(width / 2, height / 2);
  brushCtx.lineTo(-width / 2, height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第四步: 完整矩形
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.rect(-width / 2, -height / 2, width, height);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.beginPath();
  ctx.rect(-width / 2, -height / 2, width, height);
  ctx.stroke();
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
};

// 绘制填充矩形（正交方向）
export const orthogonalF = async (
  ctx: CanvasRenderingContext2D,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  state: any,
  sleep: (ms: number) => Promise<void>,
  setDrawStyle: () => void,
  width: number = 100, 
  height: number = 50
): Promise<void> => {
  setDrawStyle();
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.fillStyle = state.color;
    ctx.fillRect(-width / 2, -height / 2, width, height);
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 4;
  
  // 第一步: 横向绘制
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2, -height / 2);
  brushCtx.lineTo(width / 2, -height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第二步: 添加右边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2, -height / 2);
  brushCtx.lineTo(width / 2, -height / 2);
  brushCtx.lineTo(width / 2, height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第三步: 添加底边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2, -height / 2);
  brushCtx.lineTo(width / 2, -height / 2);
  brushCtx.lineTo(width / 2, height / 2);
  brushCtx.lineTo(-width / 2, height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第四步: 完整矩形（填充）
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.fillStyle = state.color;
  brushCtx.beginPath();
  brushCtx.rect(-width / 2, -height / 2, width, height);
  brushCtx.fill();
  brushCtx.restore();
  await sleep(100);
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.fillStyle = state.color;
  ctx.fillRect(-width / 2, -height / 2, width, height);
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
}; 