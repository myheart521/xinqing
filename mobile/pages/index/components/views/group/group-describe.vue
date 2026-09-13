<script setup>
	import {
		ref
	} from 'vue';
	const allUser = ref('2K+')
	const category = ref('19')
	const discuss = ref('2000')
	const allLike = ref('10000')
	const allComment = ref('1.29K')
	const allViews = ref('6.72K')
</script>

<template>
	<view class="tn-mt-sm tn-p-sm">
		<view class="tn-flex tn-flex-center-between tn-yellow_bg tn-round tn-pl tn-pr tn-pt-sm tn-pb-sm">
			<view class="tn-text-center tn-flex tn-flex-1" style="color: black;">
				<text class="tn-p-xs">共有<text class="tn-text-xl">{{allUser}}</text>圈友</text>
			</view>
			<view class="tn-text-right" style="color: black;">
				<view class="tn-text-bold">
					圈子<text class="tn-text-xl">{{ category }}</text>个/
					帖子<text class="tn-text-xl">{{ discuss }}</text>个
				</view>
				<view class="tn-text-xs tn-grey-dark_text tn-pt-xs">
					<text class="tn-text-xs">点赞</text><text style="font-size: 15px;">{{ allLike}}</text>
					评论<text style="font-size: 15px;">{{ allComment }}</text>
					阅读<text style="font-size: 15px">{{ allViews }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<style lang="scss">

</style>