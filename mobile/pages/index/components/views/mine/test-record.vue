<script setup>
	import TnCoolIcon from 'tnuiv3p-tn-cool-icon/index.vue'
  import { useUserStore } from '@/stores/user'

  const userStore = useUserStore()

  // 跳转到测评记录页面
  const goToTestRecord = () => {
    // 检查是否登录
    if (!userStore.checkLogin()) {
      return
    }

    // 跳转到测评记录页面
    uni.navigateTo({
      url: '/mine_pages/test-history'
    })
  }
  const tn = (router) => {
    uni.navigateTo({
      url: router
    })
  }
</script>

<template>
	<view class="tn-flex">
		<view class="tn-flex-1 tn-shadow-md tn-radius tn-white_bg tn-flex-center-center"
			style="margin: 30rpx 15rpx 0 0;padding: 30rpx 0;" @click="goToTestRecord">
			<view class="tn-flex tn-flex-column ">
				<view class="icon20__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur">
					<TnCoolIcon name="empty-history" type="circle" color="tn-white" bg-color='gradient-bg__orange'>
					</TnCoolIcon>
				</view>
				<view class="tn-text-center tn-mt-sm" style="font-size: 30rpx;">
					<view class="tn-text-ellipsis">测评记录</view>
				</view>
			</view>
		</view>
		<view class="tn-flex-1 tn-shadow-md tn-radius tn-white_bg tn-flex-center-center"
			style="margin: 30rpx 0 0 15rpx;padding: 30rpx 0;" @click="tn('/mine_pages/teacher-message')">
			<view class="tn-flex tn-flex-column">
				<view
					class="icon20__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-bg-purplered tn-color-white">
					<TnCoolIcon name="reply" type="circle" color="tn-white" bg-color='gradient-bg__teal'>
					</TnCoolIcon>
				</view>
				<view class="tn-text-center tn-mt-sm" style="font-size: 30rpx;">
					<view class="tn-text-ellipsis">聊天消息</view>
				</view>
			</view>
		</view>
	</view>

</template>

<style lang="scss">

</style>