<script setup>
	import functionSwiperVue from './views/function/function-swiper.vue';
	import toolsVue from './views/function/tools.vue';
</script>

<template>
	<view class="content">
		<!-- 地球轮播图 -->
		<functionSwiperVue></functionSwiperVue>
		<!-- 工具集合标题 -->
		<view>
			<view class="nav_title--wrap">
				<tn-icon color="tn-indigo" :transparent="false" name="star" custom-class="tn-pr-sm"></tn-icon>
				<view class="nav_title tn-gradient-bg__blue">

					其 / 他 / 工 / 具 / 集 / 合
				</view>
				<tn-icon color="tn-indigo" :transparent="false" name="star" custom-class="tn-pl-sm"></tn-icon>
			</view>
		</view>
		<!-- 其他工具卡片 -->
		<toolsVue></toolsVue>

	</view>
</template>

<style lang="scss">
	.content {
		background-color: rgb(229, 238, 255);
	}

	/* 标题 start */
	.nav_title {
		-webkit-background-clip: text;
		color: transparent;

		&--wrap {
			position: relative;
			display: flex;
			height: 120rpx;
			font-size: 42rpx;
			align-items: center;
			justify-content: center;
			font-weight: bold;
			background-image: url(http://localhost:8080/images/title_bg/title44.png);
			background-size: cover;
		}
	}

	/* 标题 end */
</style>