<script setup>
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/stores/user.js'
import { onMounted } from 'vue'

// 获取用户store
const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore)

// 初始化
onMounted(() => {
	userStore.initUserInfo()
})

// 跳转
const tn = (e) => {
	// 判断是否登录
	if (!userStore.isLogin) {
		uni.navigateTo({
			url: '/mine_pages/login'
		})
		return
	}
	uni.navigateTo({
		url: e,
	});
}
</script>

<template>
	<view class="avatar-card">
		<view class="avatar-container">
			<view class="avatar-wrapper">
        <view class="logo-pic tn-shadow">
          <view class="logo-image tn-radius">
            <!--  <view class="tn-shadow-blur"
              style="background-image:url('https://assets.example.invalid/placeholder.png');width: 110rpx;height: 110rpx;background-size: cover;">
            </view> -->
            <tn-lazy-load
                :src="userInfo.userAvatar || '/static/avatar/default.png'"
                width='110rpx'
                height='110rpx'
            ></tn-lazy-load>
          </view>
        </view>
			</view>
			<view class="user-info">
				<view class="username">{{userInfo.userName || '未登录'}}</view>
				<view class="user-bio">{{userInfo.userProfile || '开启你的心灵成长之旅'}}</view>
			</view>
		</view>
		<view class="action-buttons">
			<view class="action-btn" @click="tn('/mine_pages/user-detail')">
				<tn-icon name="my-lack" color="#8364e8" size="44rpx"></tn-icon>
				<text>个人中心</text>
			</view>
			<view class="action-btn" @click="tn('/mine_pages/settings')">
				<tn-icon name="set" color="#5677fc" size="44rpx"></tn-icon>
				<text>账号设置</text>
			</view>
		</view>
	</view>
</template>

<style lang="scss" scoped>
.avatar-card {
	background: #ffffff;
	border-radius: 24rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 10rpx 30rpx rgba(131, 100, 232, 0.1);
	
	.avatar-container {
		display: flex;
		align-items: center;
		
		.avatar-wrapper {
			position: relative;
			margin-right: 30rpx;
			
			.avatar-badge {
				position: absolute;
				bottom: -5rpx;
				right: -5rpx;
				background: linear-gradient(135deg, #8364e8, #5677fc);
				color: #ffffff;
				font-size: 20rpx;
				padding: 4rpx 12rpx;
				border-radius: 20rpx;
				box-shadow: 0 4rpx 8rpx rgba(86, 119, 252, 0.3);
			}
		}
		
		.user-info {
			.username {
				font-size: 36rpx;
				font-weight: bold;
				color: #333333;
				margin-bottom: 8rpx;
			}
			
			.user-bio {
				font-size: 24rpx;
				color: #8D93A1;
			}
		}
	}
	
	.action-buttons {
		display: flex;
		margin-top: 30rpx;
		border-top: 1px solid #f5f7fa;
		padding-top: 30rpx;
		
		.action-btn {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			
			text {
				font-size: 24rpx;
				color: #666;
				margin-top: 10rpx;
			}
		}
	}
}
</style>