<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import navbar from "@/components/navbar.vue"
import { useMusicPlayerStore } from '@/stores/musicPlayer'
import { selectAll } from '@/service/api/typeController'
import { select1, updatePlay } from '@/service/api/sleepAudiosController'

// 使用全局音频播放器
const musicPlayer = useMusicPlayerStore()

// 状态变量
const currentAudioId = ref('')
const currentAudioName = ref('')
const volume = ref(80)
const isLoading = ref(false)
const isLoadingData = ref(true)

// 分类数据
const categories = ref([])

// 当前选中的分类
const currentCategory = ref('')

// 助眠音频数据
const sleepAudios = reactive({})

// 获取分类数据
const fetchCategories = async () => {
  try {
    const res = await selectAll()
    if (res && res.code === 1 && res.data) {
      categories.value = res.data
      // 设置默认选中第一个分类
      if (categories.value.length > 0) {
        currentCategory.value = String(categories.value[0].id)
      }
    } else {
      uni.showToast({
        title: '获取分类失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    uni.showToast({
      title: '获取分类失败',
      icon: 'none'
    })
  }
}

// 获取音频数据
const fetchAudios = async () => {
  try {
    const res = await select1()
    if (res && res.code === 1 && res.data) {
      // 转换数据结构为适合我们使用的格式
      res.data.forEach(item => {
        sleepAudios[item.typeId] = item.list
      })
    } else {
      uni.showToast({
        title: '获取音频数据失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取音频数据失败:', error)
    uni.showToast({
      title: '获取音频数据失败',
      icon: 'none'
    })
  } finally {
    isLoadingData.value = false
  }
}

// 更新音频播放次数
const updatePlayCount = async (audioId) => {
  try {
    await updatePlay({
      id: audioId
    })
  } catch (error) {
    console.error('更新播放次数失败:', error)
  }
}

// 获取当前分类的音频列表
const currentAudios = computed(() => {
  return sleepAudios[currentCategory.value] || []
})

// 格式化播放次数
const formatPlays = (plays) => {
  if (plays >= 10000) {
    return (plays / 10000).toFixed(1) + '万'
  }
  return plays
}

// 格式化时间 (秒 -> mm:ss)
const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 切换分类
const changeCategory = (id) => {
  currentCategory.value = id
}

// 播放音频
const playAudio = (audio) => {
  // 如果正在播放同一个音频，则切换播放/暂停状态
  if (currentAudioId.value === audio.id) {
    togglePlay()
    return
  }
  
  isLoading.value = true
  currentAudioId.value = audio.id
  currentAudioName.value = audio.name
  
  // 使用全局音频播放器加载歌曲
  musicPlayer.loadSong({
    id: audio.id,
    name: audio.name,
    url: audio.url,
    cover: audio.cover
  })
  
  // 设置音量
  const audioContext = musicPlayer.getAudioContext()
  if (audioContext) {
    audioContext.volume = volume.value / 100
  }
  
  // 延迟播放以确保上下文已准备好
  setTimeout(() => {
    musicPlayer.play()
    isLoading.value = false
    // 更新播放次数
    updatePlayCount(audio.id)
  }, 300)
}

// 暂停/继续播放
const togglePlay = () => {
  if (musicPlayer.isLoading) {
    uni.showToast({
      title: '音频加载中，请稍候',
      icon: 'none'
    })
    return
  }
  
  musicPlayer.togglePlay()
}

// 调整音量
const changeVolume = (e) => {
  volume.value = e.detail.value
  const audioContext = musicPlayer.getAudioContext()
  if (audioContext) {
    audioContext.volume = volume.value / 100
  }
}

// 调整进度
const changeProgress = (e) => {
  if (musicPlayer.isLoading) return
  
  // 记录当前播放状态
  const wasPlaying = musicPlayer.isPlaying;
  
  // 先暂停播放，避免出现多个音频同时播放的情况
  if (wasPlaying) {
    musicPlayer.pause();
  }
  
  // 使用 seekTo 方法跳转到新位置
  musicPlayer.seekTo(e.detail.value);
  
  // 如果之前是播放状态，等待一小段时间再恢复播放
  if (wasPlaying) {
    setTimeout(() => {
      musicPlayer.play();
    }, 500);
  }
}

// 设置定时关闭
const timerOptions = [
  { text: '不开启', value: 0 },
  { text: '15分钟', value: 15 },
  { text: '30分钟', value: 30 },
  { text: '45分钟', value: 45 },
  { text: '60分钟', value: 60 },
  { text: '90分钟', value: 90 }
]
const selectedTimer = ref(0)
let timerCountdown = ref(null)
const remainingTime = ref(0)
const showTimerPicker = ref(false)

// 设置定时器
const setTimer = (minutes) => {
  // 清除之前的定时器
  if (timerCountdown.value) {
    clearTimeout(timerCountdown.value)
    timerCountdown.value = null
  }
  
  if (minutes === 0) {
    remainingTime.value = 0
    return
  }
  
  // 设置新的定时器
  const ms = minutes * 60 * 1000
  remainingTime.value = minutes * 60
  
  // 倒计时更新
  const countdownInterval = setInterval(() => {
    remainingTime.value -= 1
    if (remainingTime.value <= 0) {
      clearInterval(countdownInterval)
    }
  }, 1000)
  
  // 设置关闭音频的定时器
  timerCountdown.value = setTimeout(() => {
    musicPlayer.pause()
    
    uni.showToast({
      title: '定时关闭已生效',
      icon: 'none'
    })
    
    selectedTimer.value = 0
    remainingTime.value = 0
    clearInterval(countdownInterval)
  }, ms)
}

// 监听定时器选择变化
const onTimerChange = (e) => {
  const index = e.detail.value
  const minutes = timerOptions[index].value
  selectedTimer.value = minutes
  setTimer(minutes)
  showTimerPicker.value = false
}

// 打开定时器选择器
const openTimerPicker = () => {
  showTimerPicker.value = true
}

// 格式化剩余时间
const formatRemainingTime = computed(() => {
  if (remainingTime.value <= 0) return ''
  
  const hours = Math.floor(remainingTime.value / 3600)
  const minutes = Math.floor((remainingTime.value % 3600) / 60)
  const seconds = remainingTime.value % 60
  
  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  }
  
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 显示音量控制面板
const showVolumePanel = ref(false)

// 切换音量面板显示
const toggleVolumePanel = () => {
  showVolumePanel.value = !showVolumePanel.value
}

// 组件卸载时清理资源
onUnmounted(() => {
  if (timerCountdown.value) {
    clearTimeout(timerCountdown.value)
  }
  
  // 只暂停音频，不销毁实例，允许在后台继续播放
  if (!musicPlayer.isPlaying) {
    musicPlayer.pause()
  }
})

// 页面加载时初始化
onMounted(async () => {
  // 初始化音频上下文
  musicPlayer.initAudioContext()
  
  // 获取分类和音频数据
  await fetchCategories()
  await fetchAudios()
})
</script>

<template>
  <view class="sleep-container">
    <!-- 导航栏 -->
    <navbar title="助眠空间" back-icon-color="#fff"></navbar>
    
    <!-- 加载状态 -->
    <view v-if="isLoadingData" class="loading-state">
      <tn-loading color="#fff"></tn-loading>
      <text class="loading-text">加载中...</text>
    </view>
    
    <template v-else>
      <!-- 分类选择 -->
      <scroll-view scroll-x class="category-scroll" show-scrollbar="false">
        <view class="category-list tn-flex">
          <view 
            v-for="category in categories" 
            :key="category.id"
            class="category-item"
            :class="{'category-active': currentCategory === String(category.id)}"
            @tap="changeCategory(String(category.id))"
          >
            <view class="category-icon">
              <tn-icon :name="category.icon" size="40rpx" :color="currentCategory === String(category.id) ? '#5677fc' : '#888'"></tn-icon>
            </view>
            <text class="category-name">{{ category.name }}</text>
          </view>
        </view>
      </scroll-view>
      
      <!-- 音频列表 -->
      <scroll-view scroll-y class="audio-scroll">
        <view class="audio-list">
          <view 
            v-for="audio in currentAudios" 
            :key="audio.id"
            class="audio-card tn-shadow-sm"
            :class="{'audio-playing': currentAudioId === audio.id}"
            @tap="playAudio(audio)"
          >
            <view class="audio-cover">
              <image :src="audio.cover" mode="aspectFill"></image>
              <view class="audio-play-icon" v-if="currentAudioId !== audio.id">
                <tn-icon name="play-fill" size="60rpx" color="#fff"></tn-icon>
              </view>
              <view class="audio-playing-icon" v-else>
                <tn-icon :name="musicPlayer.isPlaying ? 'pause-fill' : 'play-fill'" size="60rpx" color="#fff"></tn-icon>
              </view>
            </view>
            
            <view class="audio-info">
              <view class="audio-title tn-text-ellipsis">{{ audio.name }}</view>
              <view class="audio-desc tn-text-ellipsis">{{ audio.description }}</view>
              <view class="audio-meta tn-flex tn-flex-row-between">
                <text class="audio-duration">{{ audio.duration }}</text>
                <view class="audio-plays tn-flex tn-flex-row-center">
                  <tn-icon name="headset" size="24rpx" color="#888"></tn-icon>
                  <text class="tn-ml-xs">{{ formatPlays(audio.plays) }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 无音频提示 -->
        <view v-if="currentAudios.length === 0" class="empty-audio">
          <tn-icon name="info-circle" size="100rpx" color="#666"></tn-icon>
          <text>该分类下暂无音频</text>
        </view>
      </scroll-view>
    </template>
    
    <!-- 播放控制栏 -->
    <view class="player-bar tn-shadow" v-if="currentAudioId">
      <view class="player-progress">
        <slider 
          :value="musicPlayer.currentTime" 
          :max="musicPlayer.duration || 100"
          activeColor="#5677fc"
          backgroundColor="rgba(255, 255, 255, 0.2)"
          block-color="#ffffff"
          block-size="12"
          @change="changeProgress"
        ></slider>
        <view class="player-time tn-flex tn-flex-start-between">
          <text>{{ formatTime(musicPlayer.currentTime) }}</text>
          <text>{{ formatTime(musicPlayer.duration) }}</text>
        </view>
      </view>
      
      <view class="player-controls tn-flex tn-flex-center-between">
        <view class="player-info tn-flex tn-flex-center-start">
          <text class="player-name tn-text-ellipsis">{{ currentAudioName }}</text>
          <text class="player-timer" v-if="remainingTime > 0">
            {{ formatRemainingTime }} 后停止
          </text>
        </view>
        
        <view class="player-buttons tn-flex tn-flex-start-center">
          <view class="player-button" @tap="togglePlay">
            <tn-icon :name="musicPlayer.isPlaying ? 'stop' : 'play-fill'" size="60rpx" color="#fff"></tn-icon>
          </view>
        </view>
        
        <view class="player-options tn-flex tn-flex-start-center">
          <view class="player-option-button" @tap="toggleVolumePanel">
            <tn-icon name="sound-fill" size="40rpx" color="#fff"></tn-icon>
          </view>
          <view class="player-option-button" @tap="openTimerPicker">
            <tn-icon name="time-fill" size="40rpx" color="#fff"></tn-icon>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 音量控制面板 -->
    <view class="volume-panel tn-shadow" v-if="showVolumePanel">
      <view class="volume-header tn-flex tn-flex-start-between">
        <text class="volume-title">音量调节</text>
        <tn-icon name="sound-fill" size="40rpx" color="#fff" @tap="showVolumePanel = false"></tn-icon>
      </view>
      <view class="volume-slider tn-flex tn-flex-start-center tn-mt-md">
        <tn-icon name="sound-reduce-fill" size="40rpx" color="#888"></tn-icon>
        <slider 
          :value="volume" 
          min="0"
          max="100"
          activeColor="#5677fc"
          backgroundColor="rgba(255, 255, 255, 0.2)"
          block-color="#ffffff"
          block-size="12"
          @change="changeVolume"
          class="volume-control"
        ></slider>
        <tn-icon name="sound-add-fill" size="40rpx" color="#888"></tn-icon>
      </view>
    </view>
    
    <!-- 定时器选择器 -->
    <picker-view 
      v-if="showTimerPicker" 
      class="timer-picker"
      :indicator-style="'height: 80rpx;'"
      :value="[timerOptions.findIndex(item => item.value === selectedTimer)]"
      @change="onTimerChange"
    >
      <view class="timer-picker-header tn-flex tn-flex-start-between">
        <text class="timer-title">定时关闭</text>
        <tn-icon name="close" size="40rpx" color="#fff" @tap="showTimerPicker = false"></tn-icon>
      </view>
      <picker-view-column>
        <view class="timer-item" v-for="(item, index) in timerOptions" :key="index">
          {{ item.text }}
        </view>
      </picker-view-column>
    </picker-view>
    
    <!-- 遮罩层 -->
    <view 
      class="mask" 
      v-if="showVolumePanel || showTimerPicker" 
      @tap="showVolumePanel = false; showTimerPicker = false"
    ></view>
  </view>
</template>

<style scoped lang="scss">
.sleep-container {
  min-height: 100vh;
  background-color: #121212;
  color: #fff;
  padding-bottom: 180rpx;
}

.loading-state {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  
  .loading-text {
    color: #aaa;
    margin-top: 20rpx;
    font-size: 28rpx;
  }
}

.category-scroll {
  padding: 30rpx 0;
  white-space: nowrap;
  
  .category-list {
    padding: 0 20rpx;
    
    .category-item {
      display: inline-flex;
      flex-direction: column;
      align-items: center;
      margin-right: 40rpx;
      transition: all 0.3s;
      
      .category-icon {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        background-color: rgba(255, 255, 255, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 10rpx;
        transition: all 0.3s;
      }
      
      .category-name {
        font-size: 24rpx;
        color: #888;
        transition: all 0.3s;
      }
      
      &.category-active {
        .category-icon {
          background-color: rgba(86, 119, 252, 0.2);
          transform: scale(1.1);
        }
        
        .category-name {
          color: #fff;
        }
      }
    }
  }
}

.audio-scroll {
  height: calc(100vh - 400rpx);
  
  .audio-list {
    padding: 0 30rpx;
    
    .audio-card {
      display: flex;
      background-color: #1e1e1e;
      border-radius: 20rpx;
      margin-bottom: 30rpx;
      overflow: hidden;
      transition: all 0.3s;
      
      &.audio-playing {
        background-color: #2a2a2a;
        transform: scale(1.02);
        box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.3);
      }
      
      .audio-cover {
        width: 200rpx;
        height: 200rpx;
        position: relative;
        flex-shrink: 0;
        
        image {
          width: 100%;
          height: 100%;
        }
        
        .audio-play-icon,
        .audio-playing-icon {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background-color: rgba(0, 0, 0, 0.3);
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
      
      .audio-info {
        flex: 1;
        padding: 20rpx;
        display: flex;
        flex-direction: column;
        
        .audio-title {
          font-size: 32rpx;
          font-weight: bold;
          margin-bottom: 10rpx;
        }
        
        .audio-desc {
          font-size: 24rpx;
          color: #aaa;
          margin-bottom: 20rpx;
          flex: 1;
        }
        
        .audio-meta {
          font-size: 24rpx;
          color: #888;
          
          .audio-plays {
            font-size: 24rpx;
          }
        }
      }
    }
  }
}

.empty-audio {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 30rpx;
  color: #888;
  font-size: 28rpx;
  text-align: center;
  
  .tn-icon {
    margin-bottom: 20rpx;
  }
}

.player-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #1e1e1e;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  z-index: 10;
  
  .player-progress {
    margin-bottom: 20rpx;
    
    .player-time {
      margin-top: 10rpx;
      font-size: 22rpx;
      color: #888;
    }
  }
  
  .player-controls {
    .player-info {
      flex: 1;
      
      .player-name {
        font-size: 28rpx;
        font-weight: bold;
        max-width: 200rpx;
      }
      
      .player-timer {
        font-size: 22rpx;
        color: #5677fc;
        margin-top: 6rpx;
      }
    }
    
    .player-buttons {
      flex: 1;
      
      .player-button {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        background-color: rgba(86, 119, 252, 0.2);
        display: flex;
        align-items: center;
        justify-content: center;
        
        &:active {
          transform: scale(0.95);
        }
      }
    }
    
    .player-options {
      flex: 1;
      justify-content: flex-end;
      
      .player-option-button {
        width: 60rpx;
        height: 60rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-left: 20rpx;
        
        &:active {
          transform: scale(0.95);
        }
      }
    }
  }
}

.volume-panel {
  position: fixed;
  bottom: 200rpx;
  right: 30rpx;
  width: 600rpx;
  background-color: #2a2a2a;
  border-radius: 20rpx;
  padding: 30rpx;
  z-index: 100;
  
  .volume-header {
    .volume-title {
      font-size: 28rpx;
      font-weight: bold;
    }
  }
  
  .volume-slider {
    .volume-control {
      flex: 1;
      margin: 0 20rpx;
    }
  }
}

.timer-picker {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 600rpx;
  background-color: #2a2a2a;
  z-index: 100;
  
  .timer-picker-header {
    padding: 30rpx;
    
    .timer-title {
      font-size: 28rpx;
      font-weight: bold;
    }
  }
  
  .timer-item {
    line-height: 80rpx;
    text-align: center;
    color: #fff;
    font-size: 32rpx;
  }
}

.mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 99;
}
</style>