<template>
	<view class="user-list-item" @click="replayClick">
		<image class="avatar" :src="user.avatar" alt="User Avatar"></image>
		<view class="user-info">
			<text class="name">{{ user.name }}</text>
			<text class="title tn-ml-sm tn-text-sm tn-gray-dark_text">{{ user.title }}</text>
			<view class="tn-mt-sm">
				<text class="signature">{{ user.signature }}</text>
			</view>
			<view class="message-count tn-flex tn-flex-center-between">
				<text class="count tn-orangered-light_bg tn-orangered_text">{{ user.messageCount }}</text>
				<text class="time tn-mr-sm ">{{ user.time }}</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue';
	const props = defineProps({
		user: {
			type: Object,
			default: {}
		}
	})

	const emits = defineEmits(['replayClick'])

	const replayClick = () => {
		emits('replayClick')
	}
</script>

<style scoped>
	.user-list-item {
		display: flex;
		align-items: center;
		padding: 10px;
		background-color: #fff;
		border-bottom: 1px solid #ebebeb;
	}

	.avatar {
		width: 40px;
		height: 40px;
		border-radius: 50%;
		margin-right: 10px;
	}

	.user-info {
		flex: 1;
	}

	.name {
		font-size: 16px;
		font-weight: bold;
		color: #333;
	}

	.title {
		display: inline-block;
		font-size: 14px;
		margin-top: 5px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		width: 300rpx;
	}

	.signature {
		font-size: 14px;
		color: #999;
		margin-top: 5px;

	}

	.message-count {
		display: flex;
		align-items: center;
		margin-top: 5px;
	}

	.count {

		color: #a94442;
		padding: 3px 5px;
		font-size: 12px;
		border-radius: 15px;
		margin-right: 10px;
	}

	.time {
		font-size: 12px;
		color: #999;
	}
</style>