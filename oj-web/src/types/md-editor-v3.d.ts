declare module 'md-editor-v3' {
  import { Component } from 'vue';

  export const MdEditor: Component & {
    /** 将Markdown转换为HTML */
    MarkdownPreview: (markdown: string) => string;
  };

  export const MdPreview: Component;
  export const MdCatalog: Component;

  export const config: {
    editorConfig: any;
    markdownItConfig: any;
    codeMirrorConfig: any;
  };
  
  export type ToolbarNames = 'bold' | 'underline' | 'italic' | 'strikeThrough' | 'title' | 'sub' | 'sup' | 'quote' 
    | 'unorderedList' | 'orderedList' | 'task' | 'codeRow' | 'code' | 'link' | 'image' | 'table' | 'mermaid' 
    | 'katex' | 'save' | 'pageFullscreen' | 'fullscreen' | 'preview' | 'htmlPreview' | 'catalog' | 'github' 
    | 'export' | 'revoke' | 'next' | 'indent' | 'outdent' | 'theme' | '-';
} 
