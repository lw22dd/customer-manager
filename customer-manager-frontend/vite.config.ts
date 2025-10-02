import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';//注意这里报错时通过快速修复可以安装对应依赖
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),

  ],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'), // 将 @ 指向 src 目录
      //
    },
  },
  base: './',
  server: {
    //host: 'localhost',
    port: 5173,
    //使用IP能访问
    host: "0.0.0.0",
    // 热更新
    hmr: true,
    //设为 true 时若端口已被占用则会直接退出，而不是尝试下一个可用端口
    strictPort: false,
    proxy: {
      "/api": {
        target: "http://localhost:8080",// 目标服务器地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
  // 生产环境构建配置
  build: {
    outDir: '../customer-manager-backend/src/main/resources/static', // 生产环境构建输出目录
    sourcemap: false, // 生产环境是否生成 sourcemap（建议关闭）
    minify: 'terser', // 生产环境使用terser进行代码压缩
    terserOptions: {
      compress: {
        drop_console: true, // 生产环境移除console
        drop_debugger: true, // 生产环境移除debugger
      },
    },
    rollupOptions: {
      output: {
        // 静态资源分类打包
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: '[ext]/[name]-[hash].[ext]',
        // 代码分割
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return 'vendor';
          }
        },
      },
    },
  },
})


