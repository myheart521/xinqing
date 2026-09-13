import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';
import open from 'open';

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': '/src'
        }
    },
    server: {
        port: 5173,
        proxy: {
            '/dev-api': {
                target: 'http://localhost:8080',
                // target: 'http://localhost:8080',

                changeOrigin: true,
                secure: false,
                ws: true,
                rewrite: (path) => {
                    let replace = path.replace(/^\/dev-api/, '');
                    console.log('代理重写路径:', replace);
                    return replace
                }
            }
        },
        open: true, // 直接让 Vite 处理
        setup() {
            open('http://localhost:5173'); // 备用方式
        }
    }
});
