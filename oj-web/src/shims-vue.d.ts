/* Vue文件声明 */
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

/* Tuc模块声明 */
// CodeRunner组件
declare module '@/components/Oj/Glowc/components/CodeRunner.vue' {
  import { DefineComponent } from 'vue'
  interface CodeRunnerExpose {
    runCode: (code: string) => Promise<void>;
  }
  const component: DefineComponent<{
    speed?: number;
    width?: number;
    height?: number;
    isVisible?: boolean;
  }, {}, any, {}, CodeRunnerExpose>;
  export default component
}

// 自定义模块声明
declare module '@/components/Oj/Glowc/core/pen' {
  export const createPen: (
    ctx: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    brushCtx: CanvasRenderingContext2D,
    brushCanvas: HTMLCanvasElement,
    getSpeed: () => number
  ) => any;
}

declare module '@/components/Oj/Glowc/data-structures/arr' {
  export const createArr: (
    ctx: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    getSpeed: () => number
  ) => any;
}
