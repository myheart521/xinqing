<script>
import { getPublicIntegrations } from "@/utils/integrations.js"
	export default {
		data() {
			return {
				digitalHumanUrl: getPublicIntegrations().digitalHumanUrl,
                wv: null,
				webViewRef: null,
				webviewContext: null
			}
		},
		methods: {
			// 发送消息到网页
			sendMessage(text) {
				// 向网页发送消息
				this.webviewContext.postMessage({
					data: text
				})
			},
		},
		onLoad() {
			// 获取webview上下文
			// this.webviewContext = uni.createWebviewContext('webview-components', this)
			// #ifdef APP-PLUS
			var currentWebview = this.$scope
				.$getAppWebview() //此对象相当于html5plus里的plus.webview.currentWebview()。在uni-app里vue页面直接使用plus.webview.currentWebview()无效
			setTimeout(function() {
				const wv = currentWebview.children()[0];
				wv.setStyle({
					top: 10, // 预留状态栏空间
					left: 20,
					height: 300,
					width: 270
				});
			}, 1000); //如果是页面初始化调用时，需要延时一下
			// #endif
			uni.$on('trigger-send-message', (data) => {
				this.sendMessage(data);
			});
		},
		//uni.$emit('trigger-send-message', { text: "Hello Webview!" });
		// onUnload() {
		//   uni.$off('trigger-send-message'); // 销毁监听
		// }
	};
</script>
<template>
	<view style="position: fixed; width: 270px;height: 300px;top:0">
		<web-view id="webview-components" :src="digitalHumanUrl"
			ref="webViewRef"></web-view>
	</view>
</template>
<style lang="scss" scoped>
</style>