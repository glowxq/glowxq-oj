// vite.config.mts
import { resolve } from 'path';
import { fileURLToPath, URL } from 'node:url';
import { defineConfig, ConfigEnv, UserConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import vueDevTools from 'vite-plugin-vue-devtools'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons';
import viteCompression from 'vite-plugin-compression';

// 由于我们使用的是 ESM，不再需要 const path = require('node:path');
// 直接使用 import { resolve } from 'path'; 即可

export default defineConfig(({ mode, command }: ConfigEnv): UserConfig => {
  const root = process.cwd();
  // 根据命令判断是否为开发环境
  const isDev = command === 'serve';
  
  // 根据环境选择配置文件目录
  const envDir = isDev ? './env/dev' : './env';
  console.log(`🚀 使用${isDev ? '开发' : '生产'}环境配置: ${envDir}/.env.${mode}`);
  
  // 加载环境变量
  const env = loadEnv(mode, resolve(root, envDir));

  return {
    base: env.VITE_PUBLIC_PATH,
    root,
    envDir, // 动态设置环境目录
    plugins: [
      vue(),
      vueJsx(),
      vueDevTools(),
      // 使用 svg 图标
      createSvgIconsPlugin({
        iconDirs: [resolve(process.cwd(), 'src/assets/icons')],
        symbolId: 'icon-[dir]-[name]',
      }),
      // 配置 gzip 压缩插件
      viteCompression({
        algorithm: 'gzip', // 使用 gzip 压缩
        ext: '.gz', // 压缩文件扩展名
        threshold: 10240, // 只有大于 10 KB 的文件才会被压缩
        deleteOriginFile: false, // 不删除源文件
      }),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        'package.json': new URL('package.json', import.meta.url).pathname,
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler', // https://github.com/sass/dart-sass/issues/2395#issuecomment-988870897
          additionalData: `@use "@/styles/element/index.scss" as *;`,
        },
      },
    },
    build: {
      rollupOptions: {
        // 移除xlsx的外部依赖配置，允许打包到bundle中
        // external: ['xlsx']
      }
    },
    server: {
      host: '0.0.0.0',
      port: Number(env.VITE_PORT),
      open: env.VITE_OPEN === 'true',
      proxy: {
        '/api': {
          target: env.VITE_API_URL,
          changeOrigin: true,
          ws: true,
          rewrite: (path) => path.replace(new RegExp(`^/api`), ''),
          // https is require secure=false
          ...(/^https:\/\//.test(env.VITE_API_URL) ? { secure: false } : {}),
        },
      },
    },
  };
});
