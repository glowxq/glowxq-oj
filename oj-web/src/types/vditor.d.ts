declare module 'vditor' {
  export default class Vditor {
    constructor(element: HTMLElement | string, options: any);
    getValue(): string;
    setValue(value: string): void;
    insertValue(value: string): void;
    getHTML(): string;
    disabled(): void;
    enable(): void;
    destroy(): void;
  }
}

declare module 'vditor/dist/index.css'; 