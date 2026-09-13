<script setup>
	// import TnPhotoAlbum from '@tuniao/tnui-vue3-uniapp/components/photo-album/src/photo-album.vue'
	import {
		onMounted,
		ref
	} from 'vue';
	import TnScrollList from '@tuniao/tnui-vue3-uniapp/components/scroll-list/src/scroll-list.vue'
	import TnSwipeAction from '@tuniao/tnui-vue3-uniapp/components/swipe-action/src/swipe-action.vue'
	import TnSwipeActionItem from '@tuniao/tnui-vue3-uniapp/components/swipe-action/src/swipe-action-item.vue'

	import {
		useNavSize
	} from '@/utils/nav-height.js'
  import {ossAvatarUrl} from "@/utils/ossUrl";

  const avatar2 = ossAvatarUrl.avatar2
	const {
		status,
		navHeight
	} = useNavSize()
	onMounted(() => {
		console.log(status.value)
	})
	const icons = ref([{
			id: 0,
			name: 'comment',
			backgroundColor: '#c4dad8'
		},
		{
			id: 1,
			name: 'menu',
			backgroundColor: '#bbc5f5'
		},
		// {
		// 	id: 2,
		// 	name: 'add-circle',
		// 	backgroundColor: '#c96868'
		// }
	])
	const iconSize = ref('80rpx')
	const color = "black"
	const selectIndex = ref(0)
	//AI图片
	const imageData = ref([
    avatar2
	])
	const iconClick = (index) => {
		selectIndex.value = index
		emits('functionChange', index)
	}
	//为父组件传递选择的按钮值
	const emits = defineEmits(['functionChange'])

	// 添加控制头部显示的状态
	const showHeader = ref(true);
	const touchStartY = ref(0);
	const touchMoveY = ref(0);
	const threshold = 50; // 触发阈值

	// 处理触摸事件
	const handleTouchStart = (e) => {
		touchStartY.value = e.touches[0].clientY;
	}

	const handleTouchMove = (e) => {
		touchMoveY.value = e.touches[0].clientY;
		const deltaY = touchMoveY.value - touchStartY.value;
		
		// 下拉显示助手信息
		if (deltaY > threshold && !showHeader.value) {
			showHeader.value = true;
			uni.vibrateShort(); // 添加触感反馈
		}
		
		// 上滑隐藏助手信息
		if (deltaY < -threshold && showHeader.value) {
			showHeader.value = false;
			uni.vibrateShort(); // 添加触感反馈
		}
	}

	// 添加视觉提示器
	const showTabs = ref(true);
	const isScrolled = ref(false);

	// 滑动操作选项
	const swipeOptions = [
		{
			text: '切换',
			icon: 'refresh',
			bgColor: 'tn-blue'
		}
	]

	// 添加对外暴露方法
	defineExpose({
		updateSelectIndex
	});

	// 更新选中的索引
	function updateSelectIndex(index) {
		selectIndex.value = index;
	}
</script>

<template>
	<view class="counselor-header" :class="{'header-shadow': isScrolled}" :style="'padding-top:'+(status)+'rpx;'">
		<!-- 使用 TnSwipeAction 包装卡片内容 -->
		<TnSwipeAction>
			<TnSwipeActionItem :options="swipeOptions">
				<view class="counselor-card">
					<view class="avatar-wrapper">
						<tn-avatar 
							:url="imageData[0]" 
							size="120rpx" 
							border 
							border-color="rgba(255,255,255,0.6)"
							shadow
							shadow-color="rgba(0,0,0,0.15)"
						/>
						<view class="status-badge">
							<tn-icon name="circle" size="16rpx" color="#4CAF50"></tn-icon>
							<text>在线</text>
						</view>
					</view>
					
					<view class="info-container">
						<view class="name-row">
							<text class="name">晴晴</text>
							<view class="badge-group">
								<text class="badge">心理助手</text>
								<text class="badge badge--verified">
									<tn-icon name="check" size="24rpx"></tn-icon>
									NewBoy训练
								</text>
							</view>
						</view>
						<view class="specialty">
							<view class="specialty-tag">
								<tn-icon name="star-fill" size="24rpx" color="#FFB703"></tn-icon>
								<text>情绪管理</text>
							</view>
							<view class="specialty-tag">
								<tn-icon name="people" size="24rpx" color="#4361EE"></tn-icon>
								<text>人际关系</text>
							</view>
							<view class="specialty-tag">
								<tn-icon name="book" size="24rpx" color="#4CAF50"></tn-icon>
								<text>学业压力</text>
							</view>
						</view>
						<view class="from">
							<tn-icon name="home" size="24rpx" color="#b4c0d9"></tn-icon>
							<text>New Boy 心理团队</text>
<!--							<view class="rating">-->
<!--								<tn-icon name="star-fill" size="24rpx" color="#FFB703"></tn-icon>-->
<!--								<text>4.9</text>-->
<!--							</view>-->
						</view>
					</view>
				</view>
			</TnSwipeActionItem>
		</TnSwipeAction>
		
		<!-- 使用 TnScrollList 包装功能标签 -->
		<TnScrollList class="function-tabs-wrapper">
			<view class="function-tabs">
				<view 
					v-for="(item, index) in icons" 
					:key="item.id" 
					class="tab-item"
					:class="{'active': selectIndex === index}"
					@click="iconClick(index)"
				>
					<tn-icon 
						:name="item.name" 
						:color="selectIndex === index ? '#ffffff' : '#b4c0d9'" 
						size="48rpx"
					></tn-icon>
					<text>{{['聊天', '历史'][index]}}</text>
				</view>
			</view>
		</TnScrollList>

		<!-- 上滑指示器 -->
		<view class="swipe-indicator" v-if="showTabs">
			<view class="indicator-line"></view>
			<text>上滑隐藏</text>
		</view>
	</view>
</template>

<style lang="scss" scoped>
.counselor-header {
	width: 100%;
	background: rgba(255,255,255,0.08);
	backdrop-filter: blur(20rpx);
	border-radius: 0 0 30rpx 30rpx;
	transition: all 0.3s ease;
	position: relative;
	height: auto;
	min-height: 300rpx;
	max-height: 400rpx;
	
	&.header-shadow {
		box-shadow: 0 6rpx 16rpx rgba(0,0,0,0.1);
	}
	
	.counselor-card {
		padding: 30rpx;
		margin: 20rpx;
		background: rgba(255,255,255,0.9);
		border-radius: 24rpx;
		box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.08);
		display: flex;
		align-items: center;
		
		.avatar-wrapper {
			position: relative;
			margin-right: 30rpx;
			
			.status-badge {
				position: absolute;
				bottom: -6rpx;
				right: -6rpx;
				background: rgba(255,255,255,0.95);
				padding: 4rpx 12rpx;
				border-radius: 20rpx;
				display: flex;
				align-items: center;
				gap: 4rpx;
				box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
				
				text {
					font-size: 20rpx;
					color: #4CAF50;
					font-weight: 500;
				}
			}
		}
		
		.info-container {
			flex: 1;
			
			.name-row {
				display: flex;
				align-items: center;
				margin-bottom: 12rpx;
				
				.name {
					font-size: 36rpx;
					font-weight: 600;
					color: #2C3A4B;
					margin-right: 16rpx;
				}
				
				.badge-group {
					display: flex;
					gap: 8rpx;
					
					.badge {
						background: rgba(86,119,252,0.1);
						color: #5677fc;
						font-size: 22rpx;
						padding: 4rpx 12rpx;
						border-radius: 8rpx;
						font-weight: 500;
						
						&--verified {
							background: rgba(76,175,80,0.1);
							color: #4CAF50;
							display: flex;
							align-items: center;
							gap: 4rpx;
						}
					}
				}
			}
			
			.specialty {
				display: flex;
				gap: 12rpx;
				margin-bottom: 12rpx;
				flex-wrap: wrap;
				
				.specialty-tag {
					background: rgba(0,0,0,0.04);
					padding: 6rpx 16rpx;
					border-radius: 100rpx;
					display: flex;
					align-items: center;
					gap: 6rpx;
					
					text {
						font-size: 24rpx;
						color: #666;
					}
				}
			}
			
			.from {
				display: flex;
				align-items: center;
				gap: 8rpx;
				font-size: 22rpx;
				color: #999;
				
				.rating {
					margin-left: auto;
					display: flex;
					align-items: center;
					gap: 4rpx;
					background: rgba(255,183,3,0.1);
					padding: 4rpx 12rpx;
					border-radius: 100rpx;
					
					text {
						color: #FFB703;
						font-weight: 600;
					}
				}
			}
		}
	}
	
	.function-tabs-wrapper {
		border-top: 1px solid rgba(255,255,255,0.1);
		padding: 20rpx 0;
		
		.function-tabs {
			display: flex;
			justify-content: space-around;
			
			.tab-item {
				display: flex;
				flex-direction: column;
				align-items: center;
				padding: 16rpx 30rpx;
				border-radius: 16rpx;
				transition: all 0.3s;
				
				text {
					font-size: 24rpx;
					color: #b4c0d9;
					margin-top: 8rpx;
					transition: all 0.3s;
				}
				
				&.active {
					background: linear-gradient(135deg, #5677fc, #4361ee);
					box-shadow: 0 4rpx 12rpx rgba(67,97,238,0.3);
					
					text {
						color: #ffffff;
					}
				}
				
				&:active {
					transform: scale(0.95);
				}
			}
		}
	}
}

.swipe-indicator {
	position: absolute;
	bottom: 10rpx;
	left: 50%;
	transform: translateX(-50%);
	display: flex;
	flex-direction: column;
	align-items: center;
	opacity: 0.6;
	
	.indicator-line {
		width: 60rpx;
		height: 4rpx;
		background: rgba(255,255,255,0.8);
		border-radius: 2rpx;
		margin-bottom: 4rpx;
	}
	
	text {
		font-size: 20rpx;
		color: rgba(255,255,255,0.8);
	}
}
</style>