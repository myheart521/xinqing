<template>
	<view class="navbar_container">
		<view class="navbar">
			<!-- 固定在顶部 -->
			<tn-navbar :opacity="opacity" :placeholder="placeholder" :frosted="true" fixed height="80px" :back-icon="back" :home-icon="home">
				<text class=" tn-text-bold">{{title}}</text>
			</tn-navbar>
		</view>
	</view>
</template>

<script setup>
	import {
		useNavSize
	} from '@/utils/nav-height.js'
	const {
		marginTop
	} = useNavSize()
	const props = defineProps({
		back: {
			type: String,
			default: 'left'
		},
		home: {
			type: String,
			default: 'home'
		},
		title: {
			type: String,
			default: ""
		},
    placeholder:{
      type:Boolean,
      default:true
    },
    opacity:{
      type:Number,
      default:1
    }
	})
</script>

<style lang="scss">

</style>