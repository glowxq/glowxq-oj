/**
 * 创建画笔工具
 * @param ctx 画布上下文
 * @param canvas 画布元素
 * @param brushCtx 画笔上下文（用于动画效果）
 * @param brushCanvas 画笔画布元素
 * @param getSpeed 获取速度的函数
 * @returns 画笔工具对象
 */
export const createPen = (
  ctx: CanvasRenderingContext2D,
  canvas: HTMLCanvasElement,
  brushCtx: CanvasRenderingContext2D,
  brushCanvas: HTMLCanvasElement,
  getSpeed: () => number,
) => {
  // 颜色映射表
  const COLOR_MAP: Record<number, string> = {
    1: '#FF0000',   // 红
    2: '#FFA500',   // 橙
    3: '#FFFF00',   // 黄
    4: '#00FF00',   // 绿
    5: '#00FFFF',   // 青
    6: '#0000FF',   // 蓝
    7: '#800080',   // 紫
    8: '#FFC0CB',   // 粉
    9: '#FFB6C1',   // 浅粉
    10: '#FFFF66',  // 浅黄
    11: '#87CEEB',  // 天蓝
    12: '#B0C4DE',  // 淡蓝
    13: '#FFD700',  // 金
    14: '#5C3317',  // 褐
    15: '#808080',  // 灰
    16: '#000000'   // 黑
  };

  // 画笔状态
  const state = {
    x: canvas.width / 2, // 当前x坐标
    y: canvas.height / 2, // 当前y坐标
    angle: 0, // 当前角度（弧度）
    color: '#000000', // 画笔颜色
    lineWidth: 2, // 线条宽度
    isPenDown: true, // 画笔是否落下
    speed: 1, // 绘制速度
    background: '#ffffff', // 背景颜色
    finishMode: false, // 是否瞬间完成（跳过动画）
  };

  // 工具函数
  const toRadians = (degrees: number): number => degrees * (Math.PI / 180);
  const roundTo = (num: number, places: number): number => parseFloat(num.toFixed(places));

  // 等待函数
  const sleep = async (ms: number): Promise<void> => {
    if (state.finishMode) return; // 瞬间完成模式下不等待
    return new Promise(resolve => setTimeout(resolve, ms / getSpeed()));
  };

  // 绘制线条（带动画）
  const drawLine = async (x1: number, y1: number, x2: number, y2: number): Promise<void> => {
    if (state.finishMode) {
      // 瞬间完成模式
      ctx.beginPath();
      ctx.moveTo(x1, y1);
      ctx.lineTo(x2, y2);
      ctx.stroke();
      return;
    }

    // 带动画的绘制
    const dx = x2 - x1;
    const dy = y2 - y1;
    const distance = Math.sqrt(dx * dx + dy * dy);
    const steps = Math.max(Math.floor(distance / 5), 1);
    const stepX = dx / steps;
    const stepY = dy / steps;

    for (let i = 0; i <= steps; i++) {
      const x = x1 + stepX * i;
      const y = y1 + stepY * i;

      // 绘制到临时画布
      brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
      brushCtx.beginPath();
      brushCtx.moveTo(x1, y1);
      brushCtx.lineTo(x, y);
      brushCtx.stroke();

      await sleep(10);
    }

    // 最终绘制到主画布
    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.stroke();
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };

  // 设置绘图样式
  const setDrawStyle = (): void => {
    ctx.strokeStyle = state.color;
    ctx.lineWidth = state.lineWidth;
    ctx.lineJoin = 'round';
    ctx.lineCap = 'round';

    // 同时设置画笔画布样式
    brushCtx.strokeStyle = state.color;
    brushCtx.lineWidth = state.lineWidth;
    brushCtx.lineJoin = 'round';
    brushCtx.lineCap = 'round';
  };

  // 初始化画笔绘制样式
  const brushDraw = (): void => {
    setDrawStyle();
  };

  // 前进指定距离
  const fd = async (distance: number = 50): Promise<void> => {
    setDrawStyle();
    const startX = state.x;
    const startY = state.y;
    
    // 计算新位置
    const newX = roundTo(startX + Math.cos(state.angle) * distance, 2);
    const newY = roundTo(startY + Math.sin(state.angle) * distance, 2);

    if (state.isPenDown) {
      // 画笔落下时绘制线条
      await drawLine(startX, startY, newX, newY);
    }

    // 更新位置
    state.x = newX;
    state.y = newY;
  };

  // 向右转指定角度
  const rt = async (degrees: number = 90): Promise<void> => {
    state.angle += toRadians(degrees);
    await sleep(100);
  };

  // 向左转指定角度
  const lt = async (degrees: number = 90): Promise<void> => {
    state.angle -= toRadians(degrees);
    await sleep(100);
  };

  // 移动到指定坐标
  const move = async (x: number, y: number): Promise<void> => {
    const startX = state.x;
    const startY = state.y;

    if (state.isPenDown) {
      // 画笔落下时绘制线条
      await drawLine(startX, startY, x, y);
    }

    state.x = x;
    state.y = y;
    await sleep(100);
  };

  // 向前移动（不留痕迹）
  const moveFd = async (distance: number = 50): Promise<void> => {
    const isPenDownOriginal = state.isPenDown;
    state.isPenDown = false;
    await fd(distance);
    state.isPenDown = isPenDownOriginal;
  };

  // 抬起画笔
  const penUp = async (): Promise<void> => {
    state.isPenDown = false;
    await sleep(10);
  };

  // 落下画笔
  const penDown = async (): Promise<void> => {
    state.isPenDown = true;
    await sleep(10);
  };

  // 设置颜色
  const color = async (newColor: string | number): Promise<void> => {
    // 处理数字颜色代码
    if (typeof newColor === 'number') {
      if (COLOR_MAP[newColor]) {
        state.color = COLOR_MAP[newColor];
      } else {
        console.warn(`无效的颜色代码: ${newColor}`);
        // 默认使用黑色
        state.color = '#000000';
      }
    } else {
      // 字符串颜色
      state.color = newColor;
    }
    
    setDrawStyle();
    await sleep(10);
  };

  // 设置线宽
  const lineWidth = async (width: number): Promise<void> => {
    state.lineWidth = width;
    setDrawStyle();
    await sleep(10);
  };

  // 清除画布
  const clear = async (): Promise<void> => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    await sleep(50);
  };

  // 设置背景颜色
  const background = async (color: string): Promise<void> => {
    state.background = color;
    ctx.fillStyle = color;
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    await sleep(50);
  };

  // 获取用户输入（模拟C++的cin>>）
  const getUserInput = (): number => {
    // 在实际应用中，可以使用prompt或自定义对话框
    const input = prompt('请输入一个值:');
    return input ? parseFloat(input) : 0;
  };

  // 设置绘制速度
  const setSpeed = (speed: number): void => {
    state.speed = Math.max(0.1, Math.min(10, speed));
  };

  // 开启瞬间完成模式
  const finish = (): void => {
    state.finishMode = true;
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
      
      await sleep(100);
    }

    // 最终绘制到主画布
    ctx.fillStyle = state.color;
    ctx.fillRect(state.x - half, state.y - half, size, size);
    ctx.strokeRect(state.x - half, state.y - half, size, size);
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };

  // 绘制空心三角形
  const triangle = async (length: number = 50): Promise<void> => {
    setDrawStyle();
    
    // 计算三角形高度（等边三角形）
    const height = length * (Math.sqrt(3) / 2);
    
    if (state.finishMode) {
      // 瞬间完成模式
      ctx.save();
      ctx.translate(state.x, state.y);
      ctx.rotate(state.angle);
      ctx.beginPath();
      ctx.moveTo(-length / 2, height / 3);
      ctx.lineTo(length / 2, height / 3);
      ctx.lineTo(0, -(2 * height) / 3);
      ctx.closePath();
      ctx.stroke();
      ctx.restore();
      return;
    }
    
    // 带动画效果
    const steps = 3;
    
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    
    // 第一条边
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.beginPath();
    brushCtx.moveTo(-length / 2, height / 3);
    brushCtx.lineTo(length / 2, height / 3);
    brushCtx.stroke();
    brushCtx.restore();
    await sleep(100);
    
    // 第二条边
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.beginPath();
    brushCtx.moveTo(-length / 2, height / 3);
    brushCtx.lineTo(length / 2, height / 3);
    brushCtx.lineTo(0, -(2 * height) / 3);
    brushCtx.stroke();
    brushCtx.restore();
    await sleep(100);
    
    // 完整三角形
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.beginPath();
    brushCtx.moveTo(-length / 2, height / 3);
    brushCtx.lineTo(length / 2, height / 3);
    brushCtx.lineTo(0, -(2 * height) / 3);
    brushCtx.closePath();
    brushCtx.stroke();
    brushCtx.restore();
    await sleep(100);
    
    // 最终绘制到主画布
    ctx.save();
    ctx.translate(state.x, state.y);
    ctx.rotate(state.angle);
    ctx.beginPath();
    ctx.moveTo(-length / 2, height / 3);
    ctx.lineTo(length / 2, height / 3);
    ctx.lineTo(0, -(2 * height) / 3);
    ctx.closePath();
    ctx.stroke();
    ctx.restore();
    
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };
  
  // 绘制实心三角形
  const triangleF = async (length: number = 50): Promise<void> => {
    setDrawStyle();
    
    // 计算三角形高度（等边三角形）
    const height = length * (Math.sqrt(3) / 2);
    
    if (state.finishMode) {
      // 瞬间完成模式
      ctx.save();
      ctx.translate(state.x, state.y);
      ctx.rotate(state.angle);
      ctx.fillStyle = state.color;
      ctx.beginPath();
      ctx.moveTo(-length / 2, height / 3);
      ctx.lineTo(length / 2, height / 3);
      ctx.lineTo(0, -(2 * height) / 3);
      ctx.closePath();
      ctx.fill();
      ctx.restore();
      return;
    }
    
    // 带动画效果
    const steps = 3;
    
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    
    // 第一条边
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.beginPath();
    brushCtx.moveTo(-length / 2, height / 3);
    brushCtx.lineTo(length / 2, height / 3);
    brushCtx.stroke();
    brushCtx.restore();
    await sleep(100);
    
    // 第二条边
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.beginPath();
    brushCtx.moveTo(-length / 2, height / 3);
    brushCtx.lineTo(length / 2, height / 3);
    brushCtx.lineTo(0, -(2 * height) / 3);
    brushCtx.stroke();
    brushCtx.restore();
    await sleep(100);
    
    // 完整三角形（实心）
    brushCtx.save();
    brushCtx.translate(state.x, state.y);
    brushCtx.rotate(state.angle);
    brushCtx.fillStyle = state.color;
    brushCtx.beginPath();
    brushCtx.moveTo(-length / 2, height / 3);
    brushCtx.lineTo(length / 2, height / 3);
    brushCtx.lineTo(0, -(2 * height) / 3);
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
    ctx.moveTo(-length / 2, height / 3);
    ctx.lineTo(length / 2, height / 3);
    ctx.lineTo(0, -(2 * height) / 3);
    ctx.closePath();
    ctx.fill();
    ctx.restore();
    
    brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
  };

  // 绘制空心椭圆
  const ellipse = async (radiusX: number = 50, radiusY: number = 30): Promise<void> => {
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
  const ellipseF = async (radiusX: number = 50, radiusY: number = 30): Promise<void> => {
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
  const orthogonal = async (width: number = 100, height: number = 50): Promise<void> => {
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
  const orthogonalF = async (width: number = 100, height: number = 50): Promise<void> => {
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
  
  // 绘制空心平行四边形
  const parallelogram = async (width: number = 100, height: number = 50): Promise<void> => {
    setDrawStyle();
    
    // 平行四边形的倾斜度
    const skew = width / 4;
    
    if (state.finishMode) {
      ctx.save();
      ctx.translate(state.x, state.y);
      ctx.rotate(state.angle);
      ctx.beginPath();
      ctx.moveTo(-width / 2 + skew / 2, height / 2);
      ctx.lineTo(-width / 2 - skew / 2, -height / 2);
      ctx.lineTo(width / 2 - skew / 2, -height / 2);
      ctx.lineTo(width / 2 + skew / 2, height / 2);
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
  const parallelogramF = async (width: number = 100, height: number = 50): Promise<void> => {
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
  const rhombus = async (length: number = 80): Promise<void> => {
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
  const rhombusF = async (length: number = 80): Promise<void> => {
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

  return {
    // 基本移动函数
    fd,
    rt,
    lt,
    move,
    moveFd,
    
    // 画笔控制
    penUp,
    penDown,
    color,
    lineWidth,
    clear,
    background,
    
    // 特殊控制
    brushDraw,
    getUserInput,
    setSpeed,
    sleep,
    finish,
    
    // 图形绘制
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
    rhombusF,
    
    // 读取当前状态
    getState: () => ({...state})
  };
}; 