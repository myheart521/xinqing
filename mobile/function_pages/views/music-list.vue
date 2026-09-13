<template>
  <view class="music-list">
    <tn-navbar  v-if="showPlayer" :frosted="true">
      <text class=" tn-text-bold" v-if="showPlayer">{{ musicPlayerStore.currentSong?.title }}</text>
      <template #back>
        <tn-icon name="down" @click="closePlayer"></tn-icon>
      </template>
    </tn-navbar>
    <navbar v-else title="休息一下吧"></navbar>
    <!--顶部描述-->
    <!-- 顶部歌单信息 -->
    <view class="tn-flex-column tn-flex tn-p" style="background-color: #e4ecf7">
      <view class="tn-flex tn-flex-center-start ">
        <!-- 左侧-->
        <view class="tn-radius tn-mr-lg" style="position: relative">
          <tn-lazy-load style="border-radius: 30rpx" :src="image2" width="300rpx" height="300rpx"></tn-lazy-load>
          <view style="position: absolute;bottom: 30rpx;left: 20rpx;">
            <text class="tn-white_text tn-text-bold tn-text">
              忘记过去的自己
            </text>
          </view>
        </view>
        <!--右侧-->
        <view>
          <view>
            <text class="tn-text-lg tn-text-bold" style="color: #8e9eb5">具备疗愈的音乐</text>
          </view>
          <view class="tn-mt">
            <text class=" tn-text" style="color: #8e9eb5">精选动听舒心的音乐</text>
          </view>
        </view>
      </view>
      <view class="tn-flex tn-flex-center-between tn-mt-sm">
        <my-icon size="50rpx" name="like" text="收藏"></my-icon>
        <my-icon size="50rpx" name="down-arrow" text="下载"></my-icon>
        <my-icon size="50rpx" name="comment" text="收藏"></my-icon>
        <my-icon size="50rpx" name="share-triangle" text="收藏"></my-icon>
      </view>
    </view>
    <!-- 头部标题 -->
    <view class="header">
      <text class="title">推荐歌曲</text>
    </view>

    <!-- 音乐列表 -->
    <scroll-view scroll-y class="song-container">
      <view class="song-item" v-for="(song, index) in songs" :key="song.id" @click="handleSongClick(song,index)">
        <!-- 歌曲封面 -->
        <tn-lazy-load :src="song.cover" width="100rpx" height="100rpx"/>
        <!-- 歌曲信息 -->
        <view class="song-info">
          <text class="song-title">{{ song.title }}</text>
          <text class="song-artist">{{ song.artist }}</text>
        </view>
        <!-- 播放按钮或加载状态 -->
        <view class="play-button" @tap.stop="handleSongClick(song,index)">
          <tn-loading v-if="isSongLoading(song)" size="sm" type="flower" color="#FF7043"></tn-loading>
          <tn-icon v-else-if="musicPlayerStore.isPlaying && musicPlayerStore.currentSong?.id === song.id" 
                  name="stop" class="play-icon" color="tn-type-error"/>
          <tn-icon v-else name="play" class="play-icon" color="tn-type-error"/>
        </view>
      </view>
    </scroll-view>

    <!-- 音乐播放器 -->
    <view class="player" v-if="musicPlayerStore.currentSong" @click="openPlayer">
      <!-- 封面 -->
      <tn-lazy-load :src="musicPlayerStore.currentSong.cover" width="100rpx" height="100rpx"/>

      <!-- 歌曲信息 -->
      <view class="player-info">
        <view style="width: 350rpx">
          <text class="player-title tn-text-ellipsis-1">{{ musicPlayerStore.currentSong.title }}</text>
        </view>
        <view style="width: 350rpx">
          <text class="player-artist tn-text-ellipsis-1">{{ musicPlayerStore.currentSong.artist }}</text>
        </view>
      </view>

      <!-- 播放控制 -->
      <view class="player-controls">
        <button class="control-btn" @tap.stop="prevSong">
          <tn-icon name="previous-song" color="tn-type-error">
          </tn-icon>
        </button>
        <button class="control-btn" @tap.stop="togglePlay">
          <tn-loading v-if="musicPlayerStore.isLoading" size="sm" type="flower" color="#FF7043"></tn-loading>
          <tn-icon v-else-if="musicPlayerStore.isPlaying" name="stop" color="tn-type-error"></tn-icon>
          <tn-icon v-else name="play" color="tn-type-error"></tn-icon>
        </button>
        <button class="control-btn" @tap.stop="nextSong">
          <tn-icon name="next-song" color="tn-type-error"></tn-icon>
        </button>
      </view>
    </view>

    <view v-if="showPlayer" class="music_play">
      <!-- 音乐播放组件（弹出播放页面） -->
      <music-play
          :currentSong="musicPlayerStore.currentSong"
          :isPlaying="musicPlayerStore.isPlaying"
          :currentTime="musicPlayerStore.currentTime"
          :duration="musicPlayerStore.duration"
          :audioContext="musicPlayerStore.getAudioContext()"
          :lyrics="musicPlayerStore.lyrics"
          :currentLyricIndex="musicPlayerStore.currentLyricIndex"
          :scrollTop="scrollTop"
          :onSliderChange="onSliderChange"
          :onSliderChanging="onSliderChanging"
          :sliderMax="musicPlayerStore.sliderMax"
          @close="closePlayer"
          @play="togglePlay"
          @prev="prevSong"
          @next="nextSong"
      />
    </view>
  </view>
</template>

<script setup>
import navbar from '@/components/navbar.vue'
import myIcon from '@/components/myicon.vue'
import {ref, onMounted, computed, onUnmounted, watch} from "vue";

import MusicPlay from "@/function_pages/views/music-play.vue";
import { onShow, onHide, onBackPress,onLoad } from '@dcloudio/uni-app';
import { useMusicPlayerStore } from "@/stores/musicPlayer";
import {ossBgUrl} from "@/utils/ossUrl";
import {getMusicPages} from "@/service/api/musicController";


const LYRIC_LINE_HEIGHT = 40  // 每行歌词高度（px）
const LYRIC_CONTAINER_HEIGHT = 300  // 歌词容器高度（px）
const image1 = ossBgUrl.music_picture1
const image2 = ossBgUrl.music_picture2
const image3 = ossBgUrl.music_picture3
const image4 = ossBgUrl.music_picture4
// 使用全局音乐播放器状态
const musicPlayerStore = useMusicPlayerStore()
onLoad(async ()=>{
  const res = await getMusicPages({})
  console.log("获取音乐未：",res)
  songs.value = res.data
})
// 音乐列表
const songs = ref([
  {
    id: 1,
    title: "起风了",
    cover: image3,
    artist: "买辣椒也用券",
    coverBg: [image1, image2, image3,image4],
    url: "https://assets.example.invalid/media-not-included",
    lrcUrl: "https://assets.example.invalid/media-not-included"
  },
  {
    id: 2,
    title: "take me hand",
    artist: "不知道",
    cover: image2,
    coverBg: [image1, image2, image3,image4],
    url: "https://assets.example.invalid/media-not-included",
    lrcUrl: "https://assets.example.invalid/media-not-included"
  },
  {
    id: 3,
    title: "起风了",
    artist: "买辣椒也用券",
    cover: image1,
    coverBg: [image1, image2, image3,image4],
    url: "https://assets.example.invalid/media-not-included",
    lrcUrl: "https://assets.example.invalid/media-not-included"
  },
  {
    id: 4,
    title: "take me hand",
    artist: "不知道",
    cover: image3,
    coverBg: [image1, image2, image3,image4],
    url: "https://assets.example.invalid/media-not-included",
    lrcUrl: "https://assets.example.invalid/media-not-included"
  },
]);

const currentSongIndex = ref(0)
// 控制音乐播放组件是否显示
const showPlayer = ref(false);
const scrollTop = ref(0)         // 歌词滚动位置

// 计算当前歌曲是否正在加载
const isSongLoading = (song) => {
  return musicPlayerStore.isLoading && musicPlayerStore.currentSong && 
         musicPlayerStore.currentSong.id === song.id;
}

// 计算滚动位置
watch(() => musicPlayerStore.currentLyricIndex, (newIndex) => {
  scrollTop.value = Math.max(0, newIndex * LYRIC_LINE_HEIGHT - (LYRIC_CONTAINER_HEIGHT / 2) + (LYRIC_LINE_HEIGHT / 2));
})

//打开播放页面
const openPlayer = () => {
  showPlayer.value = true;
};

//关闭播放页面
const closePlayer = () => {
  showPlayer.value = false;
};

// 播放/暂停切换
const togglePlay = () => {
  musicPlayerStore.togglePlay();
}

// 上一首
const prevSong = () => {
  let index = currentSongIndex.value - 1
  if (index < 0) index = songs.value.length - 1
  currentSongIndex.value = index
  musicPlayerStore.loadSong(songs.value[index])
  musicPlayerStore.play()
}

// 下一首
const nextSong = () => {
  let index = (currentSongIndex.value + 1) % songs.value.length
  currentSongIndex.value = index
  musicPlayerStore.loadSong(songs.value[index])
  musicPlayerStore.play()
}

// 拖拽结束后更新播放进度
const onSliderChange = (e) => {
  if( musicPlayerStore.isLoading) return

  // 记录当前播放状态
  const wasPlaying = musicPlayerStore.isPlaying;
  if (wasPlaying) {
    musicPlayerStore.pause();
  }
  musicPlayerStore.seekTo(e.detail.value)

  // 如果之前是播放状态，等待一小段时间再恢复播放
  if (wasPlaying) {
    setTimeout(() => {
      musicPlayerStore.play();
    }, 500);
  }
}

// 拖拽过程中更新显示的当前时间（不会立即跳转）
const onSliderChanging = (e) => {
  // 只更新显示，不实际跳转
}


// 点击列表项或底部播放器时调用，加强重复加载判断
const handleSongClick = (song, index) => {
  // 如果点击的是当前正在播放的歌曲，且正在加载中，则不响应
  if (musicPlayerStore.currentSong && 
      musicPlayerStore.currentSong.id === song.id && 
      musicPlayerStore.isLoading) {
    uni.showToast({
      title: '音频加载中，请稍候',
      icon: 'none'
    })
    return
  }
  
  // 如果点击的是当前正在播放的歌曲，则直接打开播放器
  if (musicPlayerStore.currentSong && 
      musicPlayerStore.currentSong.id === song.id) {
    openPlayer()
    return
  }
  
  currentSongIndex.value = index
  
  // 确保清理之前的音频上下文
  musicPlayerStore.loadSong(song)
  
  // 延迟一点时间再播放，确保上一个实例已经完全停止
  setTimeout(() => {
    musicPlayerStore.play()
    openPlayer()
  }, 100)
}


// 处理返回按钮，防止直接销毁页面导致音频停止
onBackPress((e) => {
  if (showPlayer.value) {
    // 如果播放器页面打开，则关闭播放器而不是退出页面
    closePlayer()
    return true // 阻止默认返回行为
  }
  // 不阻止默认返回行为，音频会在后台继续播放
  return false
})

// 页面加载时初始化
onMounted(() => {
  // 如果没有当前歌曲，则加载第一首
  if (!musicPlayerStore.currentSong) {
    musicPlayerStore.loadSong(songs.value[0])
    currentSongIndex.value = 0
  } else {
    // 找到当前播放歌曲的索引
    const index = songs.value.findIndex(song => 
      song.id === musicPlayerStore.currentSong.id
    )
    if (index !== -1) {
      currentSongIndex.value = index
    }
  }
})

// 页面卸载时清理音频资源
onUnmounted(() => {
  // 不销毁音频实例，只暂停播放，这样切换页面后音乐仍然可以在后台播放
  if (!musicPlayerStore.isPlaying) {
    musicPlayerStore.pause()
  }
})
</script>

<style lang="scss" scoped>

.music_play {
  position: fixed;
  top: 0;
  width: 100%;
  height: 100%;
}

// 头部标题
.header {
  padding: 20rpx;
  font-size: 36rpx;
  font-weight: bold;
}

// 歌曲列表
.song-container {
  flex: 1;
  padding: 10rpx;
  padding-bottom: 60px;
}

// 歌曲项
.song-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 20rpx;
}

// 歌曲封面
.song-cover {
  width: 100rpx;
  height: 100rpx;
  border-radius: 12rpx;
}

// 歌曲信息
.song-info {
  flex: 1;
  margin-left: 20rpx;
  display: flex;
  flex-direction: column;
}

.song-title {
  font-size: 32rpx;
  font-weight: bold;
}

.song-artist {
  font-size: 28rpx;
  color: #666;
}

// 播放按钮
.play-button {
  width: 60rpx;
  height: 60rpx;
}

.play-icon {
  width: 100%;
  height: 100%;
}

// 播放器
.player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  background: #fff;
  box-shadow: 0 -4rpx 10rpx rgba(0, 0, 0, 0.1);
}

.player-cover {
  width: 80rpx;
  height: 80rpx;
  border-radius: 10rpx;
}

.player-info {
  flex: 1;
  margin-left: 20rpx;
}

.player-title {
  font-size: 32rpx;
  font-weight: bold;
}

.player-artist {
  font-size: 28rpx;
  color: #666;
}

.player-controls {
  display: flex;
  align-items: center;
}

.control-btn {
  background: none;
  border: none;
  font-size: 40rpx;
  padding: 0 10rpx;
}

/* 从下往上弹出播放组件的过渡动画 */
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

</style>
