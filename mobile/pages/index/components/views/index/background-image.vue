<script setup>
	// 移除图片导入和imageList相关代码
</script>

<template>
	<view class="mental-bg">
		<!-- 保留动态气泡 -->
		<view class="floating-bubbles">
			<view class="bubble"></view>
			<view class="bubble"></view>
		</view>
	</view>
</template>

<style lang="scss" scoped>
	.mental-bg {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100vh;
		z-index: -1;
		background: linear-gradient(
			135deg, 
			#2b5876 0%,
			#4e4376 100%
		);
		overflow: hidden;
		
		&::before {
			content: '';
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-image: 
				radial-gradient(circle at 25% 25%, rgba(255,255,255,0.05) 0%, transparent 50%),
				radial-gradient(circle at 75% 75%, rgba(255,255,255,0.03) 0%, transparent 40%);
			opacity: 0.6;
		}
		
		.floating-bubbles .bubble {
			background: rgba(255,255,255,0.08);
			box-shadow: 0 0 10rpx rgba(255,255,255,0.05);
			
			&:nth-child(1) {
				width: 60rpx;
				height: 60rpx;
				animation-duration: 18s;
			}
			
			&:nth-child(2) {
				width: 80rpx;
				height: 80rpx;
				animation-duration: 24s;
			}
		}
	}
	
	@keyframes float {
		0%, 100% { transform: translateY(0) translateX(0) scale(1); }
		25% { transform: translateY(-40rpx) translateX(20rpx) scale(0.95); }
		50% { transform: translateY(0) translateX(40rpx) scale(1); }
		75% { transform: translateY(40rpx) translateX(20rpx) scale(0.95); }
	}
</style>