import {
    defineConfig
} from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        uni(),
    ],
    build: {
        rollupOptions: {
            external: ['tnuiv3p-tn-cool-icon', 'tnuiv3p-tn-graphic-card'],
        },
    },
    devServer: {
        proxy: {
            '/api': { //axios要请求的，name
                target: 'http://localhost:8080', //target为目标地址
                changeOrigin: true, //开启跨域
                pathRewrite: { //重写路径
                    '^/api': '' //按照模板，name
                }
            }
        }
    }
})