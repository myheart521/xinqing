import { loadConfiguredAvatarSdk } from './utils/vm-sdk/avatar-sdk-web_3.1.1.1011/index.js'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as echarts from 'echarts'

const app = createApp(App)
const pinia = createPinia()

app.use(router)
app.use(pinia)
app.use(ElementPlus)
app.config.globalProperties.$echarts = echarts
loadConfiguredAvatarSdk()
  .catch((error: unknown) => console.error('数字人 SDK 加载失败', error))
  .finally(() => app.mount('#app'))
