<script setup>
	const props = defineProps({
		name: {
			type: String,
			default: ""
		},
		containerWidth: {
			type: Number,
			default: 50
		},
		size: {
			type: String,
			default: "70rpx"
		},
		color: {
			type: String,
			default: "#7d8292"
		},
		text: {
			type: String,
			default: ""
		},
		bold: {
			type: Boolean,
			default: false
		}
	})

	const emits = defineEmits(['iconClick'])
	const iconClick = () => {
		emits('iconClick')
	}
</script>

<template>
	<view class="icon_container">
		<view @click="iconClick" class="tn-round tn-flex tn-flex-center-center"
			:style="'width:'+props.containerWidth+'rpx;height:'+props.containerWidth+'rpx;'">
			<tn-icon :offset-top="offsetTop" :name="name" :color="color" :size="size"></tn-icon>
		</view>
		<view class="icon_text">
			<text :style="'color:'+color" class="tn-text-sm" :class="{'tn-text-bold':bold}">{{text}}</text>
		</view>
	</view>
</template>

<style scoped lang="scss">
	.icon_container {
		display: flex;
		flex-direction: column;
		align-items: center;

		.icon_icon {
			background-color: #f7f6fa;
		}

		.icon_text {
			margin-top: 20rpx;

		}
	}
</style>