<script setup>
import {ref, onMounted, computed} from 'vue'

const props = defineProps({
  currentSong: {type: Object, required: true},
  isPlaying: {type: Boolean, required: true},
  currentTime: {type: Number, required: true, default: 0},
  duration: {type: Number, required: true, default: 0},
  audioContext: {type: Object, required: true, default: null},
  lyrics: {type: Array, required: true, default: []},
  currentLyricIndex: {type: Number, required: true, default: 0},
  scrollTop: {type: Number, required: true, default: 0},
  onSliderChange: {type: Function, required: true},
  onSliderChanging: {type: Function, required: true},
  sliderMax: {type: Number, required: true, default: 0},
})

const $emit = defineEmits(['close', 'play', 'prev', 'next'])
const currentIndex = ref(0)      // 用于 swiper 页面的切换（0：歌曲详情页，1：歌词页）
// 格式化时间（秒 → mm:ss）
const formatTime = (time) => {
  let minutes = Math.floor(time / 60)
  let seconds = Math.floor(time % 60)
  if (seconds < 10) seconds = '0' + seconds
  return `${minutes}:${seconds}`
}

// Swiper 切换事件（用于详情页和歌词页之间的切换）
const onSwiperChange = (e) => {
  currentIndex.value = e.detail.current
}

</script>

<template>
    <view class="container">
      <!-- 背景图片 -->
      <view class="slideshow">
        <view v-for="(item,index) in currentSong.coverBg" :key="index" class="slideshow-image"
              :style="'background-image:'+`url(${item})`"></view>
      </view>
      <!--毛玻璃效果-->
      <view class="slideshow blur_show"></view>
      
      <!-- 使用 Swiper 实现歌曲详情与歌词页切换 -->
      <swiper class="swiper" :current="currentIndex" @change="onSwiperChange">
        <!-- 歌曲详情页 -->
        <swiper-item>
          <view class="song-detail">
            <view class="tn-p-lg" style="width: 100%">
              <swiper class="album-swiper"
                      autoplay="true"
                      interval="4000"
                      circular="true"
                      indicator-dots="false">
                <swiper-item v-for="(item, index) in currentSong.coverBg" :key="index">
                  <image class="album-cover" :src="item" mode="aspectFill"></image>
                </swiper-item>
              </swiper>
              <!--音乐名字-->
              <view class="tn-mt-lg">
                <view class="tn-text-bold tn-text-xl tn-white_text">
                  <text>{{ currentSong.title }}</text>
                </view>
                <view class="tn-text-sm tn-white_text" style="color: rgba(255,255,255,0.7)">
                  <text>{{ currentSong.artist }}</text>
                </view>
              </view>
            </view>
            
            <!-- 加载状态提示 -->
            <view v-if="currentSong.isLoading" class="loading-container">
              <tn-loading type="flower" color="#ffffff"></tn-loading>
              <text class="loading-text">音频加载中，请稍候...</text>
            </view>
            
            <!-- 进度条（Slider 可拖拽），设置各个属性实现自定义样式 -->
            <slider class="progress-slider"
                    :value="currentTime"
                    :min="0"
                    :max="sliderMax"
                    @change="onSliderChange"
                    @changing="onSliderChanging"
                    activeColor="#ffffff"
                    backgroundColor="rgba(255,255,255,0.3)"
                    blockColor="#ffffff"
                    blockSize="16"
                    :disabled="currentSong.isLoading"
            ></slider>
            <!-- 当前时间 / 总时长显示 -->
            <view class="time-display">
              <view>
                <text>{{ formatTime(currentTime) }}</text>
              </view>
              <view>
                <text>{{ formatTime(duration) }}</text>
              </view>
            </view>
            <!-- 控制按钮：上一首 / 播放/暂停 / 下一首 -->
            <view class="tn-flex tn-flex-center-around" style="width: 100%">
              <!--返回-->
              <image @click="$emit('prev')" style="width: 60rpx;height: 60rpx"
                     src="/function_pages/static/music/music_public_forward.svg"></image>
              <!-- 暂停或者开始-->
              <view class="play-pause-button" @click="$emit('play')">
                <tn-loading v-if="currentSong.isLoading" type="flower" color="#ffffff" size="lg"></tn-loading>
                <image v-else style="width: 110rpx;height: 110rpx"
                       :src="isPlaying?'/function_pages/static/music/music_public_play.svg':'/function_pages/static/music/music_public_pause.svg'"></image>
              </view>
              <!--下一首-->
              <image @click="$emit('next')" style="width: 60rpx;height: 60rpx"
                     src="/function_pages/static/music/music_public_next.svg"></image>
            </view>
          </view>
        </swiper-item>
        <!--歌词页 -->
        <swiper-item>
          <scroll-view class="lyrics-box" scroll-y :scroll-top="scrollTop">
            <view v-if="lyrics.length === 0 || currentSong.isLoading" class="loading-lyrics">
              <tn-loading type="flower" color="#ffffff" size="sm"></tn-loading>
              <text class="tn-mt-xs">歌词加载中...</text>
            </view>
            <view v-else v-for="(line, index) in lyrics" :key="index"
                  :class="{ active: index === currentLyricIndex }"
                  class="lyric-line">
              {{ line.text }}
            </view>
          </scroll-view>
        </swiper-item>
      </swiper>
    </view>
</template>

<style scoped>
.player-header {
  margin-top: 40px;
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: black;
}

.back-btn {
  font-size: 16px;
  color: #007aff;
  background: none;
  border: none;
  padding: 5px 10px;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  margin-left: 10px;
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

.blur_show {
  background-color: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  z-index: 0;
  animation: blur_to_show 4s infinite normal;
}

@keyframes blur_to_show {
  0% {
    backdrop-filter: blur(6px); /* 初始模糊 */
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
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.swiper {
  flex: 1;
}

/* 歌曲详情页 */
.song-detail {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.album-cover {
  width: 100%;
  height: 300px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.album-swiper {
  width: 100%;
  height: 300px;
}


.progress-slider {
  width: 100%;
  margin: 40px 0 2px 0;
}

.time-display {
  width: 100%;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.controls {
  display: flex;
  justify-content: space-around;
  width: 100%;
}

.controls button {
  padding: 10px 20px;
  font-size: 16px;
}

/* 歌词页 */
.lyrics-box {
  padding: 20px;
  height: 100%;
}

.lyric-line {
  height: 40px; /* 与 LYRIC_LINE_HEIGHT 保持一致 */
  line-height: 40px;
  text-align: center;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

.lyric-line.active {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

/* 滑动过渡动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
}

.slide-up-enter-to,
.slide-up-leave-from {
  transform: translateY(0);
}

/* 添加加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 20px 0;
}

.loading-text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin-top: 10px;
}

.play-pause-button {
  width: 110rpx;
  height: 110rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-lyrics {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}
</style>
