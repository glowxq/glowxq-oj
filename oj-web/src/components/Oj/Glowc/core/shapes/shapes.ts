/**
 * 图形绘制函数集
 * 包含各种基本图形的绘制功能
 */

// 绘制圆形（不填充）
export const circle = async (
  ctx: CanvasRenderingContext2D,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  state: any,
  sleep: (ms: number) => Promise<void>
): Promise<void> => {
  const radius = 50;
  
  // 设置绘图样式
  ctx.strokeStyle = state.color;
  ctx.lineWidth = state.lineWidth;
  ctx.lineJoin = 'round';
  ctx.lineCap = 'round';
  brushCtx.strokeStyle = state.color;
  brushCtx.lineWidth = state.lineWidth;
  brushCtx.lineJoin = 'round';
  brushCtx.lineCap = 'round';
  
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