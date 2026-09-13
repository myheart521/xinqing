import { loadConfiguredAvatarSdk } from './utils/vm-sdk/avatar-sdk-web_3.1.1.1011/index.js'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)


app.use(createPinia())
app.use(router)
app.use(ElementPlus)

loadConfiguredAvatarSdk()
  .catch((error: unknown) => console.error('数字人 SDK 加载失败', error))
  .finally(() => app.mount('#app'))
