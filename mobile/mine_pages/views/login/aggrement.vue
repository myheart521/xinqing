<script setup>
	import {
		ref
	} from 'vue';
	import {
		getCurrentInstance
	} from 'vue';


	const topTitleText = ref('登录');
	//展示用户协议
	let isShowContract = ref(false);
	//同意用户协议
	const isContractAgreed = ref(false);
	//是否添加震动效果
	const isShock = ref(false)

	const onClickContract = () => {
		isShowContract.value = true;
	};
	//pinia
	// const userStore = new useUserStore()

	//选中按钮
	const checkClick = () => {
		isContractAgreed.value = !isContractAgreed.value
	}
	const closeContract = () => {
		isShowContract.value = false; //关闭协议弹窗
		isContractAgreed.value = true; // 用户同意后自动勾选
	};

	const onLogin = () => {
		if (!isContractAgreed.value) {
			uni.showToast({
				title: '请同意并遵守使用协议',
				icon: 'none'
			});
			shakeButton(); // 添加震动效果
			return;
		}
		// todo执行登录操作,并且判断手机号在后端存在不存在如果存在则直接登录成功获取手机号头像和名称，不执行下述的跳转
		uni.navigateTo({
			url: "/page_login/avatarAndName"
		})
	};

	const shakeButton = () => {
		isShock.value = true
		setTimeout(() => {
			isShock.value = false
		}, 500);
	};
</script>

<template>
	<view class="tn-shadow-md tn-p-xl tn-mt-xl tn-radius" style="background-color: rgba(255, 255, 255, 0.9);">
		<view class="center">
			<view class="text">
				<text>登录须知</text>
				<text>登录成功后需要设置您的头像和昵称</text>
			</view>
			<view>
				<view class="login_button">
					<button :class="isShock?'shake':''" id="login_button" @click="onLogin">一键授权登录</button>
				</view>

				<view class=" login_radio">
					<checkbox @click="checkClick" :checked="isContractAgreed" color="#ff9c24" borderColor="#ff9c24">
					</checkbox>

					<view class="text_left">
						<text>我已阅读，并同意遵守</text>
					</view>
					<view class="text_right" @click="onClickContract">
						<text>《心晴使用协议》</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 用户协议弹出层 -->
		<view v-if="isShowContract" class="modal" @click="isShowContract = false">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text>用户协议</text>
				</view>
				<scroll-view class="modal-body" scroll-y>
					<text>
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
						这里是用户协议的内容。您可以在这里添加详细的协议条款。
						此区域为可滑动的内容区域，用户可以上下滚动查看协议的全部内容。
					</text>
				</scroll-view>
				<view class="modal-footer">
					<button @click="closeContract">我已阅读并同意</button>
				</view>
			</view>
		</view>
	</view>
</template>

<style lang="scss">
	.shake {
		animation: shake 0.5s;
	}

	@keyframes shake {
		0% {
			transform: translateX(0);
		}

		25% {
			transform: translateX(-5px);
		}

		50% {
			transform: translateX(5px);
		}

		75% {
			transform: translateX(-5px);
		}

		100% {
			transform: translateX(0);
		}
	}

	.text {
		text {
			display: block;
			text-align: center;
			line-height: 100%;
		}

		text:nth-child(1) {
			font-size: 34rpx;
			font-weight: bolder;
			margin-top: 50rpx
		}

		text:nth-child(2) {
			font-size: 26rpx;
			margin-top: 20rpx;
			margin-bottom: 100rpx;
			color: #8d8d8d;
		}
	}

	.login_button {
		text-align: center;

		button {
			width: 85%;
			background: linear-gradient(90deg, rgba(255, 87, 51, 1) 0%, rgba(240, 131, 22, 1) 100%);
			font-weight: bolder;
			color: #ffffff;
			border-radius: 50rpx;
		}

		.login_phon {
			margin-top: 20rpx;
			font-size: 26rpx;
		}
	}

	.login_radio {
		margin-top: 120rpx;
		display: flex;
		justify-content: center;

		.text_left {
			text {
				font-size: 26rpx;
				color: #8d8d8d;
			}
		}

		.text_right {
			text {
				font-size: 26rpx;
				color: #ff9c24;
			}
		}
	}


	.modal {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}

	.modal-content {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		align-items: center;
		width: 90%;
		height: 70%;
		background-color: #f5f5f5;
		border-radius: 30rpx;
		padding: 20rpx;
	}

	.modal-header {
		padding: 20rpx;
		background-color: #f5f5f5;
		text-align: center;
		font-weight: bold;
	}

	.modal-body {
		border-radius: 50rpx;
		height: 80%;
		width: 90%;
		padding: 20rpx;
		background-color: #ffffff;

		// margin-right: 200rpx;


	}

	.modal-footer {
		// padding: 20rpx;
		text-align: center;
		background-color: #f5f5f5;
		width: 100%;
		border-radius: 50rpx;

		button {
			width: 100%;
			background: linear-gradient(90deg, rgba(255, 87, 51, 1) 0%, rgba(240, 131, 22, 1) 100%);
			color: #ffffff;
			border-radius: 40rpx;
			padding: 10rpx;
			font-size: 30rpx;
		}
	}
</style>