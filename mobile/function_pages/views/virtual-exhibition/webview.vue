<script setup>
import navbar from "@/components/navbar.vue";
import { ref, onMounted } from 'vue'
import {onLoad} from '@dcloudio/uni-app'

const url = ref('')
const title = ref('虚拟展厅')

onLoad((options) => {
  url.value = decodeURIComponent(options.url || '')
  title.value = decodeURIComponent(options.title || '虚拟展厅')
  console.log('WebView加载URL:', url.value)
})

// 处理webview消息
const handleMessage = (e) => {
  console.log('收到webview消息:', e.detail.data)
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 在外部浏览器打开
const openExternal = () => {
  if (!url.value) return
  
  // #ifdef APP-PLUS
  plus.runtime.openURL(url.value)
  // #endif
  
  // #ifdef H5
  window.open(url.value, '_blank')
  // #endif
}
</script>

<template>
  <view class="webview-container">
    <navbar :title="title" back="left" @back="goBack"></navbar>
    
    <!-- webview内容 -->
    <view class="webview-content">
      <web-view
        :src="url" 
        @message="handleMessage"
        class="webview"
      ></web-view>
    </view>
    
    <!-- 加载提示 -->
    <view class="loading-tip" v-if="!url">
      <view class="loading-icon">⏳</view>
      <view class="loading-text">正在加载展厅...</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.webview-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.webview-content {
  flex: 1;
  position: relative;
  
  .webview {
    width: 100%;
    height: 100%;
  }
}

.not-support {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 60rpx 40rpx;
  text-align: center;
  
  .not-support-icon {
    font-size: 120rpx;
    margin-bottom: 40rpx;
  }
  
  .not-support-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #2d3436;
    margin-bottom: 20rpx;
  }
  
  .not-support-desc {
    font-size: 28rpx;
    color: #636e72;
    line-height: 1.5;
    margin-bottom: 60rpx;
  }
  
  .open-external-btn {
    background: linear-gradient(45deg, #74b9ff, #0984e3);
    color: #ffffff;
    padding: 28rpx 60rpx;
    border-radius: 50rpx;
    font-size: 30rpx;
    font-weight: bold;
    
    &:active {
      transform: scale(0.95);
    }
  }
}

.loading-tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .loading-icon {
    font-size: 60rpx;
    margin-bottom: 20rpx;
    animation: rotate 2s linear infinite;
  }
  
  .loading-text {
    font-size: 28rpx;
    color: #74b9ff;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style> 