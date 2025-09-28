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
   server: {
    host: 'localhost',
    port: 5173,
  },
  // 生产环境构建配置（可选）
  build: {
    outDir: 'dist', // 输出目录
    sourcemap: false, // 生产环境是否生成 sourcemap（建议关闭）
  },
})


