<script setup>
import {ossBgUrl} from "@/utils/ossUrl";

const image1 = ossBgUrl.music_picture1
const image2 = ossBgUrl.music_picture2
const image3 = ossBgUrl.music_picture3
const image4 = ossBgUrl.music_picture4
const imageList = [
  image1,
  image2,
  image3,
  image4
]

const fatherSwiperStyle = {
  height: '100%',
  width: '100%',
}

const sonSwiperStyle = {
  width: '100%',
  height: '600rpx'
}
</script>

<template>
  <view class="music-play-container">
    <!-- 背景图片 -->
    <view class="slideshow">
      <view v-for="(item,index) in imageList" :key="index" class="slideshow-image"
            :style="'background-image:'+`url(${item})`"></view>
    </view>
    <!--毛玻璃效果-->
    <view class="slideshow blur_show"></view>
    <!-- 自定义一个导航栏-->
    <tn-navbar :opacity="0">
      <template #back>
        <tn-icon name="down"></tn-icon>
      </template>
      <template #default>
        <!--swiper的指示点-->
        <view class="tn-radius tn-white_bg" style="width: 10rpx;height: 10rpx"></view>
        <view class="tn-round tn-white_bg" style="width: 10rpx;height: 10rpx"></view>
      </template>
    </tn-navbar>
    <!-- 播放页面和歌词页面，使用swiper组件-->
    <view>
      <z-swiper grabCursor effect="flip">
        <!--播放页面-->
        <z-swiper-item :custom-style="fatherSwiperStyle">
          <view class="tn-p-lg" style="position: relative">
            <!--海报-->
            <z-swiper grabCursor effect="cube" :custom-style="swiperStyle" :options="options"
                      @swiper="init">
              <z-swiper-item v-for="(item,index) in imageList" :key="index">
                <view class="tn-flex tn-flex-center tn-radius">
                  <tn-lazy-load width="100%" :src="item"></tn-lazy-load>
                </view>
              </z-swiper-item>
            </z-swiper>
            <!--音乐名字-->
            <view class="tn-mt-lg">
              <view class="tn-text-bold tn-text-xl tn-white_text">
                <text>起风了</text>
              </view>
              <view class="tn-text-sm tn-white_text">
                <text>买辣椒也用券</text>
              </view>
            </view>
            <!--下部控制按钮-->
            <view style="position: absolute;bottom: 20rpx;width: 100%" class="tn-flex tn-flex-column tn-flex-center">
              <!--一些操作-->
              <view class="tn-flex tn-flex-center-between">
                <image style="width: 50rpx;height: 50rpx"
                       src="/function_pages/static/music/music_public_list_cycled.svg"></image>
                <image style="width: 50rpx;height: 50rpx"
                       src="/function_pages/static/music/music_public_list_cycled.svg"></image>
                <image style="width: 50rpx;height: 50rpx"
                       src="/function_pages/static/music/music_public_list_cycled.svg"></image>
                <image style="width: 50rpx;height: 50rpx"
                       src="/function_pages/static/music/music_public_list_cycled.svg"></image>
              </view>
              <!--进度条-->
              <view class="tn-mt-lg tn-mb-lg" style="height: 10px">

              </view>
              <!--控制按钮-->
              <view class="tn-flex tn-flex-center-around">
                <!--返回-->
                <image style="width: 40rpx" src="/function_pages/static/music/music_public_forward.svg"></image>
                <!-- 暂停或者开始-->
                <image style="width: 40rpx" src="/function_pages/static/music/music_public_pause.svg"></image>
                <!--下一首-->
                <image style="width: 40rpx" src="/function_pages/static/music/music_public_next.svg"></image>
              </view>
            </view>
          </view>
        </z-swiper-item>
        <!--歌词页面-->

        <z-swiper-item>
          <view>
            歌词页面
          </view>
        </z-swiper-item>
      </z-swiper>
    </view>
  </view>
</template>

<style scoped lang="scss">
.music-play-container{
  height: 100vh;
}
/* 相册 start*/
.slideshow {
  top: 0;
  position: fixed;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  z-index: -1;
}
.blur_show{
  background-color: rgba(0,0,0,0.3);
  backdrop-filter: blur(10px);
  z-index: 0;
  animation: blur_to_show 4s infinite normal;
}
@keyframes blur_to_show {
  0% {
    backdrop-filter: blur(10px); /* 初始模糊 */
  }
  100% {
    backdrop-filter: blur(0px); /* 变清晰 */
  }
}


.slideshow-image {
  position: absolute;
  width: 100%;
  height: 100%;
  background: no-repeat 50% 50% rgba(0, 0, 0, 0.5);
  background-size: cover;
  -webkit-animation-name: kenburns;
  animation-name: kenburns;
  -webkit-animation-timing-function: linear;
  animation-timing-function: linear;
  -webkit-animation-iteration-count: infinite;
  animation-iteration-count: infinite;
  -webkit-animation-duration: 16s;
  animation-duration: 16s;
  opacity: 1;
  transform: scale(1.2);
}

.slideshow-image:nth-child(1) {
  -webkit-animation-name: kenburns-1;
  animation-name: kenburns-1;
  z-index: 3;
}

.slideshow-image:nth-child(2) {
  -webkit-animation-name: kenburns-2;
  animation-name: kenburns-2;
  z-index: 2;
}

.slideshow-image:nth-child(3) {
  -webkit-animation-name: kenburns-3;
  animation-name: kenburns-3;
  z-index: 1;
}

.slideshow-image:nth-child(4) {
  -webkit-animation-name: kenburns-4;
  animation-name: kenburns-4;
  z-index: 0;
}

@-webkit-keyframes kenburns-1 {
  0% {
    opacity: 1;
    transform: scale(1.2);
  }

  1.5625% {
    opacity: 1;
  }

  23.4375% {
    opacity: 1;
  }

  26.5625% {
    opacity: 0;
    transform: scale(1);
  }

  100% {
    opacity: 0;
    transform: scale(1.2);
  }

  98.4375% {
    opacity: 0;
    transform: scale(1.2117647059);
  }

  100% {
    opacity: 1;
  }
}

@keyframes kenburns-1 {
  0% {
    opacity: 1;
    transform: scale(1.2);
  }

  1.5625% {
    opacity: 1;
  }

  23.4375% {
    opacity: 1;
  }

  26.5625% {
    opacity: 0;
    transform: scale(1);
  }

  100% {
    opacity: 0;
    transform: scale(1.2);
  }

  98.4375% {
    opacity: 0;
    transform: scale(1.2117647059);
  }

  100% {
    opacity: 1;
  }
}

@-webkit-keyframes kenburns-2 {
  23.4375% {
    opacity: 1;
    transform: scale(1.2);
  }

  26.5625% {
    opacity: 1;
  }

  48.4375% {
    opacity: 1;
  }

  51.5625% {
    opacity: 0;
    transform: scale(1);
  }

  100% {
    opacity: 0;
    transform: scale(1.2);
  }
}

@keyframes kenburns-2 {
  23.4375% {
    opacity: 1;
    transform: scale(1.2);
  }

  26.5625% {
    opacity: 1;
  }

  48.4375% {
    opacity: 1;
  }

  51.5625% {
    opacity: 0;
    transform: scale(1);
  }

  100% {
    opacity: 0;
    transform: scale(1.2);
  }
}

@-webkit-keyframes kenburns-3 {
  48.4375% {
    opacity: 1;
    transform: scale(1.2);
  }

  51.5625% {
    opacity: 1;
  }

  73.4375% {
    opacity: 1;
  }

  76.5625% {
    opacity: 0;
    transform: scale(1);
  }

  100% {
    opacity: 0;
    transform: scale(1.2);
  }
}

@keyframes kenburns-3 {
  48.4375% {
    opacity: 1;
    transform: scale(1.2);
  }

  51.5625% {
    opacity: 1;
  }

  73.4375% {
    opacity: 1;
  }

  76.5625% {
    opacity: 0;
    transform: scale(1);
  }

  100% {
    opacity: 0;
    transform: scale(1.2);
  }
}

@-webkit-keyframes kenburns-4 {
  73.4375% {
    opacity: 1;
    transform: scale(1.2);
  }

  76.5625% {
    opacity: 1;
  }

  98.4375% {
    opacity: 1;
  }

  100% {
    opacity: 0;
    transform: scale(1);
  }
}

@keyframes kenburns-4 {
  73.4375% {
    opacity: 1;
    transform: scale(1.2);
  }

  76.5625% {
    opacity: 1;
  }

  98.4375% {
    opacity: 1;
  }

  100% {
    opacity: 0;
    transform: scale(1);
  }
}

/* 相册 end*/
</style>