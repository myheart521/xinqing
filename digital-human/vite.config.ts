import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5174,
    proxy: {
      '/dev-api': {
        target: 'http://localhost:8080',
        // target: 'http://localhost:8080',

        changeOrigin: true,
        secure: false,
        ws: true,
        rewrite: (path) => {
          const replace = path.replace(/^\/dev-api/, '')
          console.log('代理重写路径:', replace)
          return replace
        },
      },
    },
    open: true, // 直接让 Vite 处理
  },
})
