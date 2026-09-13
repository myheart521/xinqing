<script setup>
	import EasyTyper from 'easy-typer-js'
	import {
		onMounted,
		reactive,
		ref,
		watch
	} from 'vue';
	import {
		getCurrentInstance
	} from 'vue';
	const instance = getCurrentInstance();

	//元素的高度,单位px
	// const typeWriterHeight = ref()

	// const emits = defineEmits(['typeWriterChange'])
	const obj = reactive({
		output: "",
		isEnd: false,
		speed: 300,
		singleBack: false,
		sleep: 0,
		type: "rollback",
		backSpeed: 100,
		sentencePause: false,
	});

	// watch(() => obj.output, () => {
	// 	const query = uni.createSelectorQuery().in(instance.proxy);
	// 	query
	// 		.select("#typeWriter")
	// 		.boundingClientRect((data) => {
	// 			typeWriterHeight.value = data.height;
	// 			emits('typeWriterChange', typeWriterHeight.value)
	// 		})
	// 		.exec();

	// })
	onMounted(() => {
		fetchData();
	});

	let myYiYan = 'http://localhost:8080/'

	const fetchData = () => {
		uni.request({
			url: myYiYan,
			method: 'GET',
			success: (res) => {
				if (res.statusCode === 200) {
					const {
						hitokoto
					} = res.data;
					new EasyTyper(
						obj,
						hitokoto,
						() => {
							fetchData();
						},
						() => {}
					);
				} else {
					console.error('请求失败', res);
				}
			},
			fail: (error) => {
				console.error('请求出错', error);
			}
		});
	};
</script>

<template>
	<view class="content" id="typeWriter">
		<!-- 打字机 -->
		<view class="brand-text">
			<view class="title">
				<text>{{obj.output}}</text>
				<text class="easy-typed-cursor">|</text>
			</view>
		</view>
	</view>
</template>

<style lang="scss">
	.content {
		.brand-text {
			// 白色半透明背景
			background: rgba(255, 255, 255, 0.5);
			padding: 0.5em;
			border-radius: 0.5em;

			.title {
				letter-spacing: 0.1em;
				background: linear-gradient(90deg, #f79533, #f37055, #ef4e7b, #a166ab, #5073b8, #1098ad, #07b39b, #6fba82);
				-webkit-background-clip: text;
				-webkit-text-fill-color: transparent;
				font-weight: 700;
				font-size: 1.5rem;

				@media (max-width: 500px) {
					font-size: 1em;
				}

				.easy-typed-cursor {
					opacity: 0;
					-webkit-animation: blink 0.7s infinite;
					-moz-animation: blink 0.7s infinite;
					animation: blink 0.7s infinite;
					background: linear-gradient(90deg, #f79533, #f37055, #ef4e7b, #a166ab, #5073b8, #1098ad, #07b39b, #6fba82);
					-webkit-background-clip: text;
					-webkit-text-fill-color: transparent;

					@media (max-width: 500px) {
						font-size: 1em;
					}
				}
			}
		}
	}
</style>