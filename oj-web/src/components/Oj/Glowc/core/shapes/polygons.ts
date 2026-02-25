/**
 * 多边形绘制函数
 * 包含平行四边形和菱形等形状
 */

// 绘制空心平行四边形
export const parallelogram = async (
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
  
  // 平行四边形的倾斜度
  const skew = width / 4;
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.beginPath();
    ctx.moveTo(-width / 2 + skew / 2, height / 2);
    ctx.lineTo(width / 2 + skew / 2, height / 2);
    ctx.lineTo(width / 2 - skew / 2, -height / 2);
    ctx.lineTo(-width / 2 - skew / 2, -height / 2);
    ctx.closePath();
    ctx.stroke();
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 4;
  
  // 第一步: 底边
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第二步: 添加右边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 - skew / 2, -height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第三步: 添加顶边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 - skew / 2, -height / 2);
  brushCtx.lineTo(-width / 2 - skew / 2, -height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第四步: 完整平行四边形
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 - skew / 2, -height / 2);
  brushCtx.lineTo(-width / 2 - skew / 2, -height / 2);
  brushCtx.closePath();
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.beginPath();
  ctx.moveTo(-width / 2 + skew / 2, height / 2);
  ctx.lineTo(width / 2 + skew / 2, height / 2);
  ctx.lineTo(width / 2 - skew / 2, -height / 2);
  ctx.lineTo(-width / 2 - skew / 2, -height / 2);
  ctx.closePath();
  ctx.stroke();
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
};

// 绘制填充平行四边形
export const parallelogramF = async (
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
  
  // 平行四边形的倾斜度
  const skew = width / 4;
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.fillStyle = state.color;
    ctx.beginPath();
    ctx.moveTo(-width / 2 + skew / 2, height / 2);
    ctx.lineTo(width / 2 + skew / 2, height / 2);
    ctx.lineTo(width / 2 - skew / 2, -height / 2);
    ctx.lineTo(-width / 2 - skew / 2, -height / 2);
    ctx.closePath();
    ctx.fill();
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 4;
  
  // 第一步: 底边
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第二步: 添加右边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 - skew / 2, -height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第三步: 添加顶边
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 - skew / 2, -height / 2);
  brushCtx.lineTo(-width / 2 - skew / 2, -height / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第四步: 完整平行四边形（填充）
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.fillStyle = state.color;
  brushCtx.beginPath();
  brushCtx.moveTo(-width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 + skew / 2, height / 2);
  brushCtx.lineTo(width / 2 - skew / 2, -height / 2);
  brushCtx.lineTo(-width / 2 - skew / 2, -height / 2);
  brushCtx.closePath();
  brushCtx.fill();
  brushCtx.restore();
  await sleep(100);
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.fillStyle = state.color;
  ctx.beginPath();
  ctx.moveTo(-width / 2 + skew / 2, height / 2);
  ctx.lineTo(width / 2 + skew / 2, height / 2);
  ctx.lineTo(width / 2 - skew / 2, -height / 2);
  ctx.lineTo(-width / 2 - skew / 2, -height / 2);
  ctx.closePath();
  ctx.fill();
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
};

// 绘制空心菱形
export const rhombus = async (
  ctx: CanvasRenderingContext2D,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  state: any,
  sleep: (ms: number) => Promise<void>,
  setDrawStyle: () => void,
  length: number = 80
): Promise<void> => {
  setDrawStyle();
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.beginPath();
    ctx.moveTo(0, -length / 2);
    ctx.lineTo(length / 2, 0);
    ctx.lineTo(0, length / 2);
    ctx.lineTo(-length / 2, 0);
    ctx.closePath();
    ctx.stroke();
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 4;
  
  // 第一步: 顶部到右侧
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第二步: 添加右侧到底部
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.lineTo(0, length / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第三步: 添加底部到左侧
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.lineTo(0, length / 2);
  brushCtx.lineTo(-length / 2, 0);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第四步: 完整菱形
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.lineTo(0, length / 2);
  brushCtx.lineTo(-length / 2, 0);
  brushCtx.closePath();
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.beginPath();
  ctx.moveTo(0, -length / 2);
  ctx.lineTo(length / 2, 0);
  ctx.lineTo(0, length / 2);
  ctx.lineTo(-length / 2, 0);
  ctx.closePath();
  ctx.stroke();
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
};

// 绘制填充菱形
export const rhombusF = async (
  ctx: CanvasRenderingContext2D,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  state: any,
  sleep: (ms: number) => Promise<void>,
  setDrawStyle: () => void,
  length: number = 80
): Promise<void> => {
  setDrawStyle();
  
  if (state.finishMode) {
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.fillStyle = state.color;
    ctx.beginPath();
    ctx.moveTo(0, -length / 2);
    ctx.lineTo(length / 2, 0);
    ctx.lineTo(0, length / 2);
    ctx.lineTo(-length / 2, 0);
    ctx.closePath();
    ctx.fill();
    ctx.restore();
    return;
  }
  
  // 带动画效果
  const steps = 4;
  
  // 第一步: 顶部到右侧
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第二步: 添加右侧到底部
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.lineTo(0, length / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第三步: 添加底部到左侧
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.lineTo(0, length / 2);
  brushCtx.stroke();
  brushCtx.restore();
  await sleep(100);
  
  // 第四步: 完整菱形（填充）
  brushCtx.save();
  brushCtx.translate(state.x, state.y);
  brushCtx.rotate(state.angle);
  brushCtx.fillStyle = state.color;
  brushCtx.beginPath();
  brushCtx.moveTo(0, -length / 2);
  brushCtx.lineTo(length / 2, 0);
  brushCtx.lineTo(0, length / 2);
  brushCtx.lineTo(-length / 2, 0);
  brushCtx.closePath();
  brushCtx.fill();
  brushCtx.restore();
  await sleep(100);
  
  // 最终绘制到主画布
  ctx.save();
  ctx.translate(state.x, state.y);
  ctx.rotate(state.angle);
  ctx.fillStyle = state.color;
  ctx.beginPath();
  ctx.moveTo(0, -length / 2);
  ctx.lineTo(length / 2, 0);
  ctx.lineTo(0, length / 2);
  ctx.lineTo(-length / 2, 0);
  ctx.closePath();
  ctx.fill();
  ctx.restore();
  
  brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
}; 