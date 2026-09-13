<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '暂无数据'
  },
  description: {
    type: String,
    default: '这里空空如也，去看看其他内容吧'
  },
  image: {
    type: String,
    default: '/static/images/empty.png' // 你需要添加一个默认的空状态图片
  }
})
</script>

<template>
  <view class="empty-container">
    <image class="empty-image" :src="image" mode="aspectFit"></image>
    <text class="empty-title">{{ title }}</text>
    <text class="empty-description">{{ description }}</text>
  </view>
</template>

<style lang="scss" scoped>
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx;
  
  .empty-image {
    width: 240rpx;
    height: 240rpx;
    margin-bottom: 30rpx;
  }
  
  .empty-title {
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
    margin-bottom: 16rpx;
  }
  
  .empty-description {
    font-size: 28rpx;
    color: #999;
    text-align: center;
  }
}
</style> 