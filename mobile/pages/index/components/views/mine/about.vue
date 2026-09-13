<script setup>
	import TnListItem from '@tuniao/tnui-vue3-uniapp/components/list/src/list-item.vue'
	import TnCoolIcon from 'tnuiv3p-tn-cool-icon/index.vue'

	// 跳转到关于心晴页面
	const goToAboutMe = () => {
		uni.navigateTo({
			url: '/mine_pages/about-me'
		})
	}
	
	// 跳转到使用协议页面
	const goToUseProtocol = () => {
		uni.navigateTo({
			url: '/mine_pages/use-protocol'
		})
	}
	
	// 跳转到未来规划页面
	const goToFuturePlan = () => {
		uni.navigateTo({
			url: '/mine_pages/future-plan'
		})
	}
	
	// 跳转到问题反馈页面
const goToFeedback = () => {
    uni.navigateTo({
        url: '/mine_pages/question-feedback'
    })
}
</script>

<template>
	<view>
		<view class="tn-mt-xl tn-radius tn-shadow-md">
			<TnListItem right-icon="creative" right-icon-color='tn-red-dark' @click="goToAboutMe">
				<view class="tn-flex">
					<tn-lazy-load src="/static/logo.png" width="50rpx" height="50rpx">
					</tn-lazy-load>
					<text class="tn-ml-sm">关于心晴</text>
				</view>
			</TnListItem>

			<TnListItem right-icon="bookmark" right-icon-color='tn-purplered-dark' @click="goToUseProtocol">
				<view class="tn-flex">
					<tn-lazy-load src="/static/mine-image/agreement.png" width="50rpx" height="50rpx">
					</tn-lazy-load>
					<text class="tn-ml-sm">使用协议</text>
				</view>
			</TnListItem>
			<TnListItem right-icon="check" right-icon-color='tn-purple-dark' @click="goToFuturePlan">
				<view class="tn-flex">
					<tn-lazy-load src="/static/mine-image/furture.png" width="50rpx" height="50rpx">
					</tn-lazy-load>
					<text class="tn-ml-sm">未来规划</text>
				</view>
			</TnListItem>

		</view>
		<view class="tn-mt-xl tn-shadow-md">
			<TnListItem right-icon="emoji-sad" radius right-icon-color='tn-yellow-dark' @click="goToFeedback">
				<TnCoolIcon name="comment" size='50rpx' type="circle" color="tn-white" bg-color='gradient-bg__orange'>
				</TnCoolIcon>
				<text class="tn-ml-sm">问题反馈</text>
			</TnListItem>
		</view>
	</view>
</template>

<style lang="scss">

</style>