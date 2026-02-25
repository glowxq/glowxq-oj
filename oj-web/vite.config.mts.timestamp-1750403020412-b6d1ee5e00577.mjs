// vite.config.mts
import { resolve } from "path";
import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "file:///Users/glowxq/Documents/code/glowxq-nexus/glowxq-web/node_modules/.pnpm/vite@5.4.9_@types+node@18.19.56_sass@1.79.5/node_modules/vite/dist/node/index.js";
import vue from "file:///Users/glowxq/Documents/code/glowxq-nexus/glowxq-web/node_modules/.pnpm/@vitejs+plugin-vue@4.6.2_vite@5.4.9_@types+node@18.19.56_sass@1.79.5__vue@3.5.12_typescript@5.2.2_/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import vueJsx from "file:///Users/glowxq/Documents/code/glowxq-nexus/glowxq-web/node_modules/.pnpm/@vitejs+plugin-vue-jsx@3.1.0_vite@5.4.9_@types+node@18.19.56_sass@1.79.5__vue@3.5.12_typescript@5.2.2_/node_modules/@vitejs/plugin-vue-jsx/dist/index.mjs";
import vueDevTools from "file:///Users/glowxq/Documents/code/glowxq-nexus/glowxq-web/node_modules/.pnpm/vite-plugin-vue-devtools@7.6.4_rollup@4.24.0_vite@5.4.9_@types+node@18.19.56_sass@1.79.5__vue@3.5.12_typescript@5.2.2_/node_modules/vite-plugin-vue-devtools/dist/vite.mjs";
import { createSvgIconsPlugin } from "file:///Users/glowxq/Documents/code/glowxq-nexus/glowxq-web/node_modules/.pnpm/vite-plugin-svg-icons@2.0.1_vite@5.4.9_@types+node@18.19.56_sass@1.79.5_/node_modules/vite-plugin-svg-icons/dist/index.mjs";
import viteCompression from "file:///Users/glowxq/Documents/code/glowxq-nexus/glowxq-web/node_modules/.pnpm/vite-plugin-compression@0.5.1_vite@5.4.9_@types+node@18.19.56_sass@1.79.5_/node_modules/vite-plugin-compression/dist/index.mjs";
var __vite_injected_original_import_meta_url = "file:///Users/glowxq/Documents/code/glowxq-nexus/glowxq-web/vite.config.mts";
var vite_config_default = defineConfig(({ mode, command }) => {
  const root = process.cwd();
  const isDev = command === "serve";
  const envDir = isDev ? "./env/dev" : "./env";
  console.log(`\u{1F680} \u4F7F\u7528${isDev ? "\u5F00\u53D1" : "\u751F\u4EA7"}\u73AF\u5883\u914D\u7F6E: ${envDir}/.env.${mode}`);
  const env = loadEnv(mode, resolve(root, envDir));
  return {
    base: env.VITE_PUBLIC_PATH,
    root,
    envDir,
    // 动态设置环境目录
    plugins: [
      vue(),
      vueJsx(),
      vueDevTools(),
      // 使用 svg 图标
      createSvgIconsPlugin({
        iconDirs: [resolve(process.cwd(), "src/assets/icons")],
        symbolId: "icon-[dir]-[name]"
      }),
      // 配置 gzip 压缩插件
      viteCompression({
        algorithm: "gzip",
        // 使用 gzip 压缩
        ext: ".gz",
        // 压缩文件扩展名
        threshold: 10240,
        // 只有大于 10 KB 的文件才会被压缩
        deleteOriginFile: false
        // 不删除源文件
      })
    ],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", __vite_injected_original_import_meta_url)),
        "package.json": new URL("package.json", __vite_injected_original_import_meta_url).pathname
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: "modern-compiler",
          // https://github.com/sass/dart-sass/issues/2395#issuecomment-988870897
          additionalData: `@use "@/styles/element/index.scss" as *;`
        }
      }
    },
    build: {
      rollupOptions: {
        external: ["xlsx"]
      }
    },
    server: {
      host: "0.0.0.0",
      port: Number(env.VITE_PORT),
      open: env.VITE_OPEN === "true",
      proxy: {
        "/api": {
          target: env.VITE_API_URL,
          changeOrigin: true,
          ws: true,
          rewrite: (path) => path.replace(new RegExp(`^/api`), ""),
          // https is require secure=false
          .../^https:\/\//.test(env.VITE_API_URL) ? { secure: false } : {}
        }
      }
    }
  };
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcubXRzIl0sCiAgInNvdXJjZXNDb250ZW50IjogWyJjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZGlybmFtZSA9IFwiL1VzZXJzL2dsb3d4cS9Eb2N1bWVudHMvY29kZS9nbG93eHEtbmV4dXMvZ2xvd3hxLXdlYlwiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9maWxlbmFtZSA9IFwiL1VzZXJzL2dsb3d4cS9Eb2N1bWVudHMvY29kZS9nbG93eHEtbmV4dXMvZ2xvd3hxLXdlYi92aXRlLmNvbmZpZy5tdHNcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfaW1wb3J0X21ldGFfdXJsID0gXCJmaWxlOi8vL1VzZXJzL2dsb3d4cS9Eb2N1bWVudHMvY29kZS9nbG93eHEtbmV4dXMvZ2xvd3hxLXdlYi92aXRlLmNvbmZpZy5tdHNcIjsvLyB2aXRlLmNvbmZpZy5tdHNcbmltcG9ydCB7IHJlc29sdmUgfSBmcm9tICdwYXRoJztcbmltcG9ydCB7IGZpbGVVUkxUb1BhdGgsIFVSTCB9IGZyb20gJ25vZGU6dXJsJztcbmltcG9ydCB7IGRlZmluZUNvbmZpZywgQ29uZmlnRW52LCBVc2VyQ29uZmlnLCBsb2FkRW52IH0gZnJvbSAndml0ZSc7XG5pbXBvcnQgdnVlIGZyb20gJ0B2aXRlanMvcGx1Z2luLXZ1ZSc7XG5pbXBvcnQgdnVlSnN4IGZyb20gJ0B2aXRlanMvcGx1Z2luLXZ1ZS1qc3gnO1xuaW1wb3J0IHZ1ZURldlRvb2xzIGZyb20gJ3ZpdGUtcGx1Z2luLXZ1ZS1kZXZ0b29scydcbmltcG9ydCB7IGNyZWF0ZVN2Z0ljb25zUGx1Z2luIH0gZnJvbSAndml0ZS1wbHVnaW4tc3ZnLWljb25zJztcbmltcG9ydCB2aXRlQ29tcHJlc3Npb24gZnJvbSAndml0ZS1wbHVnaW4tY29tcHJlc3Npb24nO1xuXG4vLyBcdTc1MzFcdTRFOEVcdTYyMTFcdTRFRUNcdTRGN0ZcdTc1MjhcdTc2ODRcdTY2MkYgRVNNXHVGRjBDXHU0RTBEXHU1MThEXHU5NzAwXHU4OTgxIGNvbnN0IHBhdGggPSByZXF1aXJlKCdub2RlOnBhdGgnKTtcbi8vIFx1NzZGNFx1NjNBNVx1NEY3Rlx1NzUyOCBpbXBvcnQgeyByZXNvbHZlIH0gZnJvbSAncGF0aCc7IFx1NTM3M1x1NTNFRlxuXG5leHBvcnQgZGVmYXVsdCBkZWZpbmVDb25maWcoKHsgbW9kZSwgY29tbWFuZCB9OiBDb25maWdFbnYpOiBVc2VyQ29uZmlnID0+IHtcbiAgY29uc3Qgcm9vdCA9IHByb2Nlc3MuY3dkKCk7XG4gIC8vIFx1NjgzOVx1NjM2RVx1NTQ3RFx1NEVFNFx1NTIyNFx1NjVBRFx1NjYyRlx1NTQyNlx1NEUzQVx1NUYwMFx1NTNEMVx1NzNBRlx1NTg4M1xuICBjb25zdCBpc0RldiA9IGNvbW1hbmQgPT09ICdzZXJ2ZSc7XG4gIFxuICAvLyBcdTY4MzlcdTYzNkVcdTczQUZcdTU4ODNcdTkwMDlcdTYyRTlcdTkxNERcdTdGNkVcdTY1ODdcdTRFRjZcdTc2RUVcdTVGNTVcbiAgY29uc3QgZW52RGlyID0gaXNEZXYgPyAnLi9lbnYvZGV2JyA6ICcuL2Vudic7XG4gIGNvbnNvbGUubG9nKGBcdUQ4M0RcdURFODAgXHU0RjdGXHU3NTI4JHtpc0RldiA/ICdcdTVGMDBcdTUzRDEnIDogJ1x1NzUxRlx1NEVBNyd9XHU3M0FGXHU1ODgzXHU5MTREXHU3RjZFOiAke2VudkRpcn0vLmVudi4ke21vZGV9YCk7XG4gIFxuICAvLyBcdTUyQTBcdThGN0RcdTczQUZcdTU4ODNcdTUzRDhcdTkxQ0ZcbiAgY29uc3QgZW52ID0gbG9hZEVudihtb2RlLCByZXNvbHZlKHJvb3QsIGVudkRpcikpO1xuXG4gIHJldHVybiB7XG4gICAgYmFzZTogZW52LlZJVEVfUFVCTElDX1BBVEgsXG4gICAgcm9vdCxcbiAgICBlbnZEaXIsIC8vIFx1NTJBOFx1NjAwMVx1OEJCRVx1N0Y2RVx1NzNBRlx1NTg4M1x1NzZFRVx1NUY1NVxuICAgIHBsdWdpbnM6IFtcbiAgICAgIHZ1ZSgpLFxuICAgICAgdnVlSnN4KCksXG4gICAgICB2dWVEZXZUb29scygpLFxuICAgICAgLy8gXHU0RjdGXHU3NTI4IHN2ZyBcdTU2RkVcdTY4MDdcbiAgICAgIGNyZWF0ZVN2Z0ljb25zUGx1Z2luKHtcbiAgICAgICAgaWNvbkRpcnM6IFtyZXNvbHZlKHByb2Nlc3MuY3dkKCksICdzcmMvYXNzZXRzL2ljb25zJyldLFxuICAgICAgICBzeW1ib2xJZDogJ2ljb24tW2Rpcl0tW25hbWVdJyxcbiAgICAgIH0pLFxuICAgICAgLy8gXHU5MTREXHU3RjZFIGd6aXAgXHU1MzhCXHU3RjI5XHU2M0QyXHU0RUY2XG4gICAgICB2aXRlQ29tcHJlc3Npb24oe1xuICAgICAgICBhbGdvcml0aG06ICdnemlwJywgLy8gXHU0RjdGXHU3NTI4IGd6aXAgXHU1MzhCXHU3RjI5XG4gICAgICAgIGV4dDogJy5neicsIC8vIFx1NTM4Qlx1N0YyOVx1NjU4N1x1NEVGNlx1NjI2OVx1NUM1NVx1NTQwRFxuICAgICAgICB0aHJlc2hvbGQ6IDEwMjQwLCAvLyBcdTUzRUFcdTY3MDlcdTU5MjdcdTRFOEUgMTAgS0IgXHU3Njg0XHU2NTg3XHU0RUY2XHU2MjREXHU0RjFBXHU4OEFCXHU1MzhCXHU3RjI5XG4gICAgICAgIGRlbGV0ZU9yaWdpbkZpbGU6IGZhbHNlLCAvLyBcdTRFMERcdTUyMjBcdTk2NjRcdTZFOTBcdTY1ODdcdTRFRjZcbiAgICAgIH0pLFxuICAgIF0sXG4gICAgcmVzb2x2ZToge1xuICAgICAgYWxpYXM6IHtcbiAgICAgICAgJ0AnOiBmaWxlVVJMVG9QYXRoKG5ldyBVUkwoJy4vc3JjJywgaW1wb3J0Lm1ldGEudXJsKSksXG4gICAgICAgICdwYWNrYWdlLmpzb24nOiBuZXcgVVJMKCdwYWNrYWdlLmpzb24nLCBpbXBvcnQubWV0YS51cmwpLnBhdGhuYW1lLFxuICAgICAgfSxcbiAgICB9LFxuICAgIGNzczoge1xuICAgICAgcHJlcHJvY2Vzc29yT3B0aW9uczoge1xuICAgICAgICBzY3NzOiB7XG4gICAgICAgICAgYXBpOiAnbW9kZXJuLWNvbXBpbGVyJywgLy8gaHR0cHM6Ly9naXRodWIuY29tL3Nhc3MvZGFydC1zYXNzL2lzc3Vlcy8yMzk1I2lzc3VlY29tbWVudC05ODg4NzA4OTdcbiAgICAgICAgICBhZGRpdGlvbmFsRGF0YTogYEB1c2UgXCJAL3N0eWxlcy9lbGVtZW50L2luZGV4LnNjc3NcIiBhcyAqO2AsXG4gICAgICAgIH0sXG4gICAgICB9LFxuICAgIH0sXG4gICAgYnVpbGQ6IHtcbiAgICAgIHJvbGx1cE9wdGlvbnM6IHtcbiAgICAgICAgZXh0ZXJuYWw6IFsneGxzeCddXG4gICAgICB9XG4gICAgfSxcbiAgICBzZXJ2ZXI6IHtcbiAgICAgIGhvc3Q6ICcwLjAuMC4wJyxcbiAgICAgIHBvcnQ6IE51bWJlcihlbnYuVklURV9QT1JUKSxcbiAgICAgIG9wZW46IGVudi5WSVRFX09QRU4gPT09ICd0cnVlJyxcbiAgICAgIHByb3h5OiB7XG4gICAgICAgICcvYXBpJzoge1xuICAgICAgICAgIHRhcmdldDogZW52LlZJVEVfQVBJX1VSTCxcbiAgICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsXG4gICAgICAgICAgd3M6IHRydWUsXG4gICAgICAgICAgcmV3cml0ZTogKHBhdGgpID0+IHBhdGgucmVwbGFjZShuZXcgUmVnRXhwKGBeL2FwaWApLCAnJyksXG4gICAgICAgICAgLy8gaHR0cHMgaXMgcmVxdWlyZSBzZWN1cmU9ZmFsc2VcbiAgICAgICAgICAuLi4oL15odHRwczpcXC9cXC8vLnRlc3QoZW52LlZJVEVfQVBJX1VSTCkgPyB7IHNlY3VyZTogZmFsc2UgfSA6IHt9KSxcbiAgICAgICAgfSxcbiAgICAgIH0sXG4gICAgfSxcbiAgfTtcbn0pO1xuIl0sCiAgIm1hcHBpbmdzIjogIjtBQUNBLFNBQVMsZUFBZTtBQUN4QixTQUFTLGVBQWUsV0FBVztBQUNuQyxTQUFTLGNBQXFDLGVBQWU7QUFDN0QsT0FBTyxTQUFTO0FBQ2hCLE9BQU8sWUFBWTtBQUNuQixPQUFPLGlCQUFpQjtBQUN4QixTQUFTLDRCQUE0QjtBQUNyQyxPQUFPLHFCQUFxQjtBQVJxTCxJQUFNLDJDQUEyQztBQWFsUSxJQUFPLHNCQUFRLGFBQWEsQ0FBQyxFQUFFLE1BQU0sUUFBUSxNQUE2QjtBQUN4RSxRQUFNLE9BQU8sUUFBUSxJQUFJO0FBRXpCLFFBQU0sUUFBUSxZQUFZO0FBRzFCLFFBQU0sU0FBUyxRQUFRLGNBQWM7QUFDckMsVUFBUSxJQUFJLHlCQUFRLFFBQVEsaUJBQU8sY0FBSSw2QkFBUyxNQUFNLFNBQVMsSUFBSSxFQUFFO0FBR3JFLFFBQU0sTUFBTSxRQUFRLE1BQU0sUUFBUSxNQUFNLE1BQU0sQ0FBQztBQUUvQyxTQUFPO0FBQUEsSUFDTCxNQUFNLElBQUk7QUFBQSxJQUNWO0FBQUEsSUFDQTtBQUFBO0FBQUEsSUFDQSxTQUFTO0FBQUEsTUFDUCxJQUFJO0FBQUEsTUFDSixPQUFPO0FBQUEsTUFDUCxZQUFZO0FBQUE7QUFBQSxNQUVaLHFCQUFxQjtBQUFBLFFBQ25CLFVBQVUsQ0FBQyxRQUFRLFFBQVEsSUFBSSxHQUFHLGtCQUFrQixDQUFDO0FBQUEsUUFDckQsVUFBVTtBQUFBLE1BQ1osQ0FBQztBQUFBO0FBQUEsTUFFRCxnQkFBZ0I7QUFBQSxRQUNkLFdBQVc7QUFBQTtBQUFBLFFBQ1gsS0FBSztBQUFBO0FBQUEsUUFDTCxXQUFXO0FBQUE7QUFBQSxRQUNYLGtCQUFrQjtBQUFBO0FBQUEsTUFDcEIsQ0FBQztBQUFBLElBQ0g7QUFBQSxJQUNBLFNBQVM7QUFBQSxNQUNQLE9BQU87QUFBQSxRQUNMLEtBQUssY0FBYyxJQUFJLElBQUksU0FBUyx3Q0FBZSxDQUFDO0FBQUEsUUFDcEQsZ0JBQWdCLElBQUksSUFBSSxnQkFBZ0Isd0NBQWUsRUFBRTtBQUFBLE1BQzNEO0FBQUEsSUFDRjtBQUFBLElBQ0EsS0FBSztBQUFBLE1BQ0gscUJBQXFCO0FBQUEsUUFDbkIsTUFBTTtBQUFBLFVBQ0osS0FBSztBQUFBO0FBQUEsVUFDTCxnQkFBZ0I7QUFBQSxRQUNsQjtBQUFBLE1BQ0Y7QUFBQSxJQUNGO0FBQUEsSUFDQSxPQUFPO0FBQUEsTUFDTCxlQUFlO0FBQUEsUUFDYixVQUFVLENBQUMsTUFBTTtBQUFBLE1BQ25CO0FBQUEsSUFDRjtBQUFBLElBQ0EsUUFBUTtBQUFBLE1BQ04sTUFBTTtBQUFBLE1BQ04sTUFBTSxPQUFPLElBQUksU0FBUztBQUFBLE1BQzFCLE1BQU0sSUFBSSxjQUFjO0FBQUEsTUFDeEIsT0FBTztBQUFBLFFBQ0wsUUFBUTtBQUFBLFVBQ04sUUFBUSxJQUFJO0FBQUEsVUFDWixjQUFjO0FBQUEsVUFDZCxJQUFJO0FBQUEsVUFDSixTQUFTLENBQUMsU0FBUyxLQUFLLFFBQVEsSUFBSSxPQUFPLE9BQU8sR0FBRyxFQUFFO0FBQUE7QUFBQSxVQUV2RCxHQUFJLGNBQWMsS0FBSyxJQUFJLFlBQVksSUFBSSxFQUFFLFFBQVEsTUFBTSxJQUFJLENBQUM7QUFBQSxRQUNsRTtBQUFBLE1BQ0Y7QUFBQSxJQUNGO0FBQUEsRUFDRjtBQUNGLENBQUM7IiwKICAibmFtZXMiOiBbXQp9Cg==
