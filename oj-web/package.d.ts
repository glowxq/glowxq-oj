// 在 package.d.ts 文件中
declare module 'package.json' {
  const content: {
    version: string;
  };
  export = content;
}
