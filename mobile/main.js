import App from './App'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import { createPinia } from 'pinia'
// 引入全局uView
// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App
})
app.$mount()
// #endif

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
const pinia = createPinia()
export function createApp() {
	const app = createSSRApp(App)
	app.component('TnIcon', TnIcon)
	app.use(pinia)
	return {
		app
	}
}
// #endif