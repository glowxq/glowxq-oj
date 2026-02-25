/**
 * 创建画笔工具
 * @param ctx 画布上下文
 * @param canvas 画布元素
 * @param brushCtx 画笔上下文（用于动画效果）
 * @param brushCanvas 画笔画布元素
 * @param getSpeed 获取速度的函数 - 不再使用，改为内部state.speed
 * @returns 画笔工具对象
 */
import { createShapeFunctions, type ShapeDrawingFunctions } from './shapes';

export const createPen = (
    ctx: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    brushCtx: CanvasRenderingContext2D,
    brushCanvas: HTMLCanvasElement,
    getSpeed: () => number, // 仍然保留参数以保持兼容性，但不再使用
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

    // 中心点坐标
    const center = {
        x: canvas.width / 2,
        y: canvas.height / 2
    };

    /**
     * 画笔状态接口 - 定义画笔的所有属性和状态
     * @interface PenState
     * @property {number} x - 画笔在画布上的X坐标
     * @property {number} y - 画笔在画布上的Y坐标
     * @property {number} angle - 画笔方向角度（弧度制），0表示向右，π/2表示向上
     * @property {string} color - 画笔颜色
     * @property {number} lineWidth - 线条宽度
     * @property {boolean} isPenDown - 画笔是否落下（是否在移动时绘制线条）
     * @property {number} speed - 动画速度（1-1000）
     * @property {string} background - 背景颜色
     * @property {boolean} finishMode - 是否启用瞬间完成模式（跳过动画）
     * @property {boolean} showState - 是否显示状态信息
     * @property {number} arrowSize - 指示器箭头长度
     * @property {number} arrowWidth - 指示器箭头宽度
     * @property {string} arrowUpColor - 画笔抬起时指示器颜色
     * @property {string} arrowDownColor - 画笔落下时指示器颜色
     * @property {number} arrowOpacity - 指示器透明度（0-1）
     */
    interface PenState {
        x: number;
        y: number;
        angle: number;
        color: string;
        lineWidth: number;
        isPenDown: boolean;
        speed: number;
        background: string;
        finishMode: boolean;
        showState: boolean; // 状态信息显示标志
        arrowSize: number;  // 指示器箭头长度
        arrowWidth: number; // 指示器箭头宽度
        arrowUpColor: string; // 画笔抬起时指示器颜色
        arrowDownColor: string; // 画笔落下时指示器颜色
        arrowOpacity: number; // 指示器透明度
    }

    // 初始化画笔状态
    const state: PenState = {
        x: canvas.width / 2,
        y: canvas.height / 2,
        angle: 90 * Math.PI / 180, // 初始角度为90度（垂直向上）
        color: 'black',
        lineWidth: 2,
        isPenDown: true,
        speed: 100,
        background: '#fff',
        finishMode: false,
        showState: true,  // 默认显示状态信息
        arrowSize: 20,    // 指示器箭头长度
        arrowWidth: 10,   // 指示器箭头宽度
        arrowUpColor: '#0000FF', // 画笔抬起时为蓝色
        arrowDownColor: '#FF0000', // 画笔落下时为红色
        arrowOpacity: 0.7 // 默认透明度
    };

    /**
     * 将角度转换为弧度
     * @param degrees 角度值
     * @returns 弧度值
     */
    const toRadians = (degrees: number): number => {
        return degrees * (Math.PI / 180);
    };

    /**
     * 将数字四舍五入到指定小数位
     * @param num 原始数字
     * @param places 小数位数
     * @returns 四舍五入后的数字
     */
    const roundTo = (num: number, places: number): number => parseFloat(num.toFixed(places));

    /**
     * 内部等待函数 - 受速度影响的延迟
     * @param ms 基准等待毫秒数
     * @returns Promise
     */
    const sleepWithSpeed = async (ms: number): Promise<void> => {
        // 瞬间完成模式下不等待
        if (state.finishMode) return Promise.resolve();

        // 如果速度为1000，几乎是瞬间完成
        if (state.speed >= 990) {
            return Promise.resolve();
        }

        // 使用对数关系而不是线性关系
        // 调整对数因子，使动画更流畅
        // 速度为1时，等待时间是原始ms的30倍（降低了极低速度的等待时间）
        // 速度为10时，等待时间是原始ms的3倍
        // 速度为100时，等待时间约等于原始ms的0.3倍
        const logFactor = 30 / Math.pow(state.speed, 0.7);
        const adjustedMs = ms * logFactor;

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

    /**
     * 用户直接调用的等待函数 - 不受速度影响
     * @param ms 等待毫秒数
     * @returns Promise
     */
    const sleep = async (ms: number): Promise<void> => {
        // 不做速度调整，直接等待指定的毫秒数
        return new Promise(resolve => setTimeout(resolve, ms));
    };

    /**
     * 清除画笔画布
     */
    const clearBrushCanvas = (): void => {
        brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    };

    /**
     * 设置绘图样式 - 同时设置主画布和画笔画布的样式
     */
    const setDrawStyle = (): void => {
        ctx.strokeStyle = state.color;
        ctx.fillStyle = state.color;
        ctx.lineWidth = state.lineWidth;
        ctx.lineJoin = 'round';
        ctx.lineCap = 'round';

        // 同时设置画笔画布样式
        brushCtx.strokeStyle = state.color;
        brushCtx.fillStyle = state.color;
        brushCtx.lineWidth = state.lineWidth;
        brushCtx.lineJoin = 'round';
        brushCtx.lineCap = 'round';
    };

    /**
     * 画笔指示器绘制函数 - 在指定位置绘制箭头指示器
     * @param x 可选的X坐标，默认为当前画笔位置
     * @param y 可选的Y坐标，默认为当前画笔位置
     * @returns Promise
     */
    const brushDraw = async (x: number = state.x, y: number = state.y): Promise<void> => {
        // 清除画笔画布
        brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);

        // 瞬间完成模式下可以不绘制指示器
        if (state.finishMode && state.speed >= 990) return;

        // 箭头配置 - 使用state中的设置值
        const ARROW_LENGTH = state.arrowSize; // 使用state中设置的箭头长度
        const ARROW_WIDTH = state.arrowWidth;  // 使用state中设置的箭头宽度

        // 计算箭头各点坐标 (箭头指向angle方向)
        const radians = state.angle;

        // 计算箭头主体尾部位置
        const end = {
            x: x - Math.cos(radians) * ARROW_LENGTH,
            y: y - Math.sin(radians) * ARROW_LENGTH
        };

        // 计算左侧翼点
        const left = {
            x: x - Math.cos(radians + Math.PI / 6) * ARROW_WIDTH,
            y: y - Math.sin(radians + Math.PI / 6) * ARROW_WIDTH
        };

        // 计算右侧翼点
        const right = {
            x: x - Math.cos(radians - Math.PI / 6) * ARROW_WIDTH,
            y: y - Math.sin(radians - Math.PI / 6) * ARROW_WIDTH
        };

        // 绘制箭头图形 - 使用透明色和填充效果让箭头更美观
        brushCtx.beginPath();

        // 箭头头部（三角形）
        brushCtx.moveTo(x, y); // 箭头尖
        brushCtx.lineTo(left.x, left.y);
        brushCtx.lineTo(right.x, right.y);
        brushCtx.closePath();

        // 设置透明填充样式 - 使用自定义颜色和透明度
        const fillColor = state.isPenDown
            ? `rgba(${hexToRgb(state.arrowDownColor)}, ${state.arrowOpacity})`
            : `rgba(${hexToRgb(state.arrowUpColor)}, ${state.arrowOpacity})`;
        brushCtx.fillStyle = fillColor;
        brushCtx.fill();

        // 绘制箭头主体（线条）
        brushCtx.beginPath();
        brushCtx.moveTo(end.x, end.y);
        brushCtx.lineTo(x, y);

        // 设置笔触颜色和样式 - 使用自定义颜色和透明度
        const strokeColor = state.isPenDown
            ? `rgba(${hexToRgb(state.arrowDownColor)}, ${state.arrowOpacity + 0.2})`
            : `rgba(${hexToRgb(state.arrowUpColor)}, ${state.arrowOpacity + 0.2})`;
        brushCtx.strokeStyle = strokeColor;
        brushCtx.lineWidth = 2.5;
        brushCtx.stroke();

        if (!state.finishMode) {
            await sleepWithSpeed(10);
        }
    };

    /**
     * 辅助函数：将十六进制颜色转换为RGB格式
     * @param hex 十六进制颜色字符串
     * @returns RGB格式字符串，例如："255, 0, 0"
     */
    const hexToRgb = (hex: string): string => {
        // 移除#前缀（如果有）
        hex = hex.replace(/^#/, '');

        // 处理简写形式（例如#FFF）
        if (hex.length === 3) {
            hex = hex.split('').map(char => char + char).join('');
        }

        // 转换为RGB
        const bigint = parseInt(hex, 16);
        const r = (bigint >> 16) & 255;
        const g = (bigint >> 8) & 255;
        const b = bigint & 255;

        return `${r}, ${g}, ${b}`;
    };

    /**
     * 绘制完整的状态显示（画笔+指示器）
     * @returns Promise
     */
    const drawFullState = async (): Promise<void> => {
        // 在主画布上绘制一个状态框
        if (state.showState) {
            ctx.save();

            // 在画布右下角绘制状态框
            const statusX = canvas.width - 120;
            const statusY = canvas.height - 70;

            // 绘制背景
            ctx.fillStyle = 'rgba(240, 240, 240, 0.8)';
            ctx.fillRect(statusX, statusY, 110, 60);
            ctx.strokeStyle = '#333';
            ctx.strokeRect(statusX, statusY, 110, 60);

            // 绘制文本信息
            ctx.fillStyle = '#000';
            ctx.font = '12px Arial';
            ctx.textAlign = 'left';
            ctx.fillText(`X: ${Math.round(state.x)}`, statusX + 10, statusY + 15);
            ctx.fillText(`Y: ${Math.round(state.y)}`, statusX + 10, statusY + 30);
            ctx.fillText(`角度: ${Math.round(state.angle)}°`, statusX + 10, statusY + 45);

            ctx.restore();
        }

        // 绘制画笔指示器
        await brushDraw();
    };

    // 画线函数 - 使用requestAnimationFrame优化绘制
    const drawLine = async (x1: number, y1: number, x2: number, y2: number): Promise<void> => {
        const dx = x2 - x1;
        const dy = y2 - y1;
        const distance = Math.sqrt(dx * dx + dy * dy);

        // 瞬间完成模式或线条太短时直接绘制
        if (state.finishMode || distance < 5 || state.speed >= 990) {
            ctx.beginPath();
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.stroke();

            // 更新画笔位置
            state.x = x2;
            state.y = y2;
            await brushDraw();
            return;
        }

        // 准备绘制线段
        ctx.beginPath();
        ctx.moveTo(x1, y1);

        // 根据距离和速度优化步数
        const steps = Math.min(Math.max(5, Math.floor(distance / 3)), 30);
        const stepX = dx / steps;
        const stepY = dy / steps;

        // 使用requestAnimationFrame优化绘制过程
        return new Promise<void>(resolve => {
            let step = 0;

            const animate = () => {
                // 批处理多个步骤，减少重绘频率
                const batchSize = Math.max(1, Math.min(5, Math.floor(state.speed / 100)));
                let currentStep = step;

                for (let b = 0; b < batchSize && currentStep < steps; b++, currentStep++) {
                    // 计算当前点
                    const t = (currentStep + 1) / steps;
                    const currentX = x1 + dx * t;
                    const currentY = y1 + dy * t;

                    // 更新画笔位置
                    state.x = currentX;
                    state.y = currentY;

                    // 绘制线段
                    ctx.lineTo(currentX, currentY);
                }

                // 一次性绘制批处理的所有线段
                ctx.stroke();

                // 更新画笔指示器
                brushDraw();

                // 更新步骤计数
                step = currentStep;

                // 继续动画或完成
                if (step < steps) {
                    requestAnimationFrame(animate);
                } else {
                    // 确保最后一段直线完全绘制到终点
                    if (Math.abs(state.x - x2) > 0.1 || Math.abs(state.y - y2) > 0.1) {
                        ctx.beginPath();
                        ctx.moveTo(state.x, state.y);
                        ctx.lineTo(x2, y2);
                        ctx.stroke();
                        state.x = x2;
                        state.y = y2;
                        brushDraw();
                    }

                    // 完成动画
                    resolve();
                }
            };

            // 启动动画
            requestAnimationFrame(animate);
        });
    };

    // 移动(不画线)函数 - 使用requestAnimationFrame优化移动
    const moveFd = async (distance: number): Promise<void> => {
        const radians = state.angle * Math.PI / 180;
        const startX = state.x; // 保存起始位置
        const startY = state.y;
        const endX = state.x + Math.cos(radians) * distance;
        const endY = state.y - Math.sin(radians) * distance;

        // 瞬间完成模式或速度很高，直接瞬移
        if (state.finishMode || state.speed >= 990) {
            state.x = endX;
            state.y = endY;
            await brushDraw();
            return;
        }

        // 优化步数
        const steps = Math.min(Math.max(5, Math.floor(distance / 5)), 20);

        // 使用requestAnimationFrame优化移动
        return new Promise<void>(resolve => {
            let step = 0;

            const animate = () => {
                // 批处理多个步骤
                const batchSize = Math.max(1, Math.min(3, Math.floor(state.speed / 200)));
                let currentStep = step;

                for (let b = 0; b < batchSize && currentStep < steps; b++, currentStep++) {
                    // 计算当前点
                    const t = (currentStep + 1) / steps;
                    state.x = startX + (endX - startX) * t;
                    state.y = startY + (endY - startY) * t;
                }

                // 更新画笔指示器
                brushDraw();

                // 更新步骤计数
                step = currentStep;

                // 继续动画或完成
                if (step < steps) {
                    requestAnimationFrame(animate);
                } else {
                    // 确保精确到达终点
                    state.x = endX;
                    state.y = endY;
                    brushDraw();
                    resolve();
                }
            };

            // 启动动画
            requestAnimationFrame(animate);
        });
    };

    // 通用的形状绘制函数
    interface DrawShapeOptions {
        fill?: boolean;
        steps?: number;
        sleepTime?: number;
        useTransform?: boolean;
    }

    // 动画信息接口
    interface AnimationInfo {
        currentStep: number;
        totalSteps: number;
    }

    /**
     * 通用形状绘制函数
     * @param draw 绘制函数，接收上下文对象和可选的动画信息
     * @param options 绘制选项
     */
    const drawShape = async (
        draw: (context: CanvasRenderingContext2D, animInfo?: AnimationInfo) => void,
        options: DrawShapeOptions = {}
    ): Promise<void> => {
        const {
            fill = false,
            steps = 1,
            sleepTime = 100,
            useTransform = true
        } = options;

        setDrawStyle();

        if (state.finishMode) {
            // 瞬间完成模式
            if (useTransform) ctx.save();
            if (useTransform) ctx.translate(state.x, state.y);
            if (useTransform) ctx.rotate(state.angle);

            draw(ctx);

            if (fill) {
                ctx.fill();
                if (!options.useTransform) ctx.stroke(); // 某些填充图形需要描边
            } else {
                ctx.stroke();
            }

            if (useTransform) ctx.restore();
            return;
        }

        // 带动画效果
        for (let i = 1; i <= steps; i++) {
            clearBrushCanvas();

            if (useTransform) brushCtx.save();
            if (useTransform) brushCtx.translate(state.x, state.y);
            if (useTransform) brushCtx.rotate(state.angle);

            // 为动画效果传递当前步骤
            const animationInfo: AnimationInfo = { currentStep: i, totalSteps: steps };
            draw(brushCtx, animationInfo);

            if (i === steps && fill) {
                brushCtx.fill();
            } else {
                brushCtx.stroke();
            }

            if (useTransform) brushCtx.restore();

            // 绘制画笔指示器
            await brushDraw();

            await sleepWithSpeed(sleepTime);
        }

        // 最终绘制到主画布
        if (useTransform) ctx.save();
        if (useTransform) ctx.translate(state.x, state.y);
        if (useTransform) ctx.rotate(state.angle);

        draw(ctx);

        if (fill) {
            ctx.fill();
            if (!options.useTransform) ctx.stroke(); // 某些填充图形需要描边
        } else {
            ctx.stroke();
        }

        if (useTransform) ctx.restore();
        clearBrushCanvas();
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
        } else {
            // 画笔抬起时只移动位置
            state.x = newX;
            state.y = newY;
            // 更新画笔指示器
            await brushDraw();
        }

        // 移除指令完成后的额外等待，减少卡顿感
    };

    // 向右转指定角度
    const rt = async (degrees: number = 90): Promise<void> => {
        // 瞬间完成模式直接更新角度
        if (state.finishMode || state.speed >= 990) {
            state.angle += toRadians(degrees);

            // 确保angle在0-2π之间
            state.angle = state.angle % (Math.PI * 2);
            if (state.angle < 0) {
                state.angle += Math.PI * 2;
            }

            await brushDraw();
            return;
        }

        // 动画参数计算
        const startAngle = state.angle;
        const finalAngle = startAngle + toRadians(degrees);
        const startTime = performance.now();

        // 根据速度调整动画持续时间
        // 速度越大，动画时间越短
        const speedFactor = Math.min(Math.max(state.speed / 100, 0.1), 10);
        const BASE_MS_PER_DEG = 5; // 基础时间（ms/度）
        const duration = (Math.abs(degrees) * (30 / speedFactor)) * (BASE_MS_PER_DEG / 10);

        // 使用Promise和requestAnimationFrame实现平滑动画
        return new Promise<void>((resolve) => {
            const animate = (currentTime: number) => {
                const elapsed = currentTime - startTime;
                let progress = duration > 0 ? Math.min(elapsed / duration, 1) : 1;

                // 使用缓动函数使动画更平滑
                const smoothProgress = 1 - Math.pow(1 - progress, 2);

                // 计算当前角度
                state.angle = startAngle + toRadians(degrees) * smoothProgress;

                // 确保angle在0-2π之间
                state.angle = state.angle % (Math.PI * 2);
                if (state.angle < 0) {
                    state.angle += Math.PI * 2;
                }

                // 更新画笔指示器
                brushDraw();

                // 动画继续或完成
                if (progress < 1) {
                    requestAnimationFrame(animate);
                } else {
                    // 确保最终角度精确
                    state.angle = finalAngle % (Math.PI * 2);
                    if (state.angle < 0) {
                        state.angle += Math.PI * 2;
                    }
                    resolve();
                }
            };

            // 启动动画
            requestAnimationFrame(animate);
        });
    };

    // 向左转指定角度
    const lt = async (degrees: number = 90): Promise<void> => {
        // 左转就是右转负角度，复用代码
        return rt(-degrees);
    };

    // 移动到指定坐标（使用0,0为中心点的坐标系）
    const move = async (x: number, y: number): Promise<void> => {
        // 转换坐标系：以画布中心为(0,0)，y轴向上为正方向
        const canvasX = center.x + x;
        const canvasY = center.y - y; // y轴方向相反

        // 无论是否抬笔都不绘制线条
        state.x = canvasX;
        state.y = canvasY;

        // 更新画笔指示器并确保等待完成
        await brushDraw();
        // 使用Promise确保等待完成
        return sleepWithSpeed(50); // 减少移动后的等待时间，但确保等待完成
    };

    // 抬起画笔
    const penUp = async (): Promise<void> => {
        state.isPenDown = false;
        return sleepWithSpeed(10);
    };

    // 落下画笔
    const penDown = async (): Promise<void> => {
        state.isPenDown = true;
        return sleepWithSpeed(10);
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
        return sleepWithSpeed(10);
    };

    // 设置线宽
    const lineWidth = async (width: number): Promise<void> => {
        state.lineWidth = width;
        setDrawStyle();
        return sleepWithSpeed(10);
    };

    // 清除画布
    const clear = async (): Promise<void> => {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        brushCtx.clearRect(0, 0, brushCanvas.width, brushCanvas.height);
    };

    // 设置背景颜色
    const background = async (color: string | number): Promise<void> => {
        // 处理数字颜色代码
        let bgColor = color;
        if (typeof color === 'number') {
            if (COLOR_MAP[color]) {
                bgColor = COLOR_MAP[color];
            } else {
                console.warn(`无效的颜色代码: ${color}`);
                // 默认使用白色
                bgColor = '#FFFFFF';
            }
        }

        // 保存当前样式
        ctx.save();

        // 设置填充颜色并绘制背景
        ctx.fillStyle = bgColor as string;
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        // 恢复样式
        ctx.restore();

        // 更新状态
        state.background = bgColor as string;

        // 重绘指示器
        await brushDraw();
    };

    // 模拟用户输入
    const getUserInput = (): number => {
        return parseInt(prompt('请输入值', '0') || '0');
    };

    // 设置速度
    const setSpeed = (newSpeed: number): void => {
        // 确保速度在1-1000范围内
        state.speed = Math.max(1, Math.min(1000, newSpeed));

        // 如果速度为1000，直接启用瞬间完成模式
        if (state.speed >= 1000) {
            state.finishMode = true;
            console.log('画笔已启用瞬间完成模式');
        } else {
            state.finishMode = false;
        }

        console.log('画笔速度已设置为:', state.speed);
    };

    // 立即完成所有动画
    const finish = (): void => {
        state.finishMode = true;
        console.log('已启用瞬间完成模式');
    };

    // 获取实际画布坐标（内部使用）
    const getCanvasCoordinates = (x: number, y: number): { x: number, y: number } => {
        return {
            x: center.x + x,
            y: center.y - y
        };
    };

    // 获取坐标系坐标（内部使用）
    const getCoordinateSystemPosition = (canvasX: number, canvasY: number): { x: number, y: number } => {
        // 将canvas坐标转换为内部坐标系
        return {
            x: canvasX - center.x,
            y: center.y - canvasY
        };
    };

    // 添加显示/隐藏箭头的方法
    const showArrow = (show: boolean) => {
        state.showState = show;
        return self;  // 返回自身以便链式调用
    };

    // 修改箭头大小的方法
    const setArrowSize = (size: number) => {
        state.arrowSize = size;
        return self;  // 返回自身以便链式调用
    };

    // 修改箭头宽度的方法
    const setArrowWidth = (width: number) => {
        state.arrowWidth = width;
        return self;  // 返回自身以便链式调用
    };

    /**
     * 设置指示器颜色
     * @param upColor 画笔抬起时的颜色
     * @param downColor 画笔落下时的颜色
     * @returns void
     */
    const setArrowColor = (upColor: string | number, downColor: string | number): void => {
        // 处理上箭头颜色
        if (typeof upColor === 'number') {
            if (COLOR_MAP[upColor]) {
                state.arrowUpColor = COLOR_MAP[upColor];
            } else {
                console.warn(`无效的颜色代码: ${upColor}`);
                // 默认使用蓝色
                state.arrowUpColor = '#0000FF';
            }
        } else {
            state.arrowUpColor = upColor;
        }

        // 处理下箭头颜色
        if (typeof downColor === 'number') {
            if (COLOR_MAP[downColor]) {
                state.arrowDownColor = COLOR_MAP[downColor];
            } else {
                console.warn(`无效的颜色代码: ${downColor}`);
                // 默认使用红色
                state.arrowDownColor = '#FF0000';
            }
        } else {
            state.arrowDownColor = downColor;
        }
    };

    /**
     * 设置指示器透明度
     * @param opacity 透明度值（0-1之间）
     * @returns void
     */
    const setArrowOpacity = (opacity: number): void => {
        // 确保透明度在有效范围内
        state.arrowOpacity = Math.max(0, Math.min(1, opacity));
    };

    /**
     * 获取当前X坐标
     * @returns number 当前X坐标
     */
    const getX = (): number => {
        return state.x;
    };

    /**
     * 获取当前Y坐标
     * @returns number 当前Y坐标
     */
    const getY = (): number => {
        return state.y;
    };

    // 创建几何图形绘制函数
    const shapes = createShapeFunctions(
        ctx,
        canvas,
        brushCtx,
        brushCanvas,
        () => state.speed,
        state,
        drawLine,
        sleepWithSpeed,
        setDrawStyle
    );

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
        sleep,  // 这是用户直接调用的sleep函数
        finish,

        // 位置获取函数
        getX,
        getY,

        // 图形绘制
        getCanvasCoordinates,
        getCoordinateSystemPosition,
        showArrow,
        setArrowSize,
        setArrowWidth,
        setArrowColor, // 新增：设置箭头颜色
        setArrowOpacity, // 新增：设置箭头透明度

        // 添加几何图形函数
        ...shapes
    };
};
