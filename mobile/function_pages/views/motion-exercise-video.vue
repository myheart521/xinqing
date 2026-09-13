<script setup>
import navbar from '@/components/navbar.vue'
import { ref, onMounted } from 'vue'
import { selectById,watch } from '@/service/api/sportController'
import { onLoad } from '@dcloudio/uni-app'

// 运动详情数据
const motionDetail = ref({
  id: 0,
  text: "",
  video: "",
  time: 0,
  title: "",
  league: "",
  leagueDescribe: "",
  cal: "",
  motion: "",
  motionFeatures: [],
  imageList: []
})

const loading = ref(true)
const currentImageIndex = ref(0)
const showImagePreview = ref(false)

// 获取运动详情
const fetchMotionDetail = async (id) => {
  try {
    loading.value = true
    const res = await selectById({ id })
    if (res.code === 1) {
      console.log("成功了11111111111：",res.data)
      motionDetail.value = res.data
      //观看次数加一
      await watch({ sportId:id })
    } else {
      uni.showToast({
        title: res.msg || '获取运动详情失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取运动详情失败:', error)
    uni.showToast({
      title: '获取运动详情失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}


// 预览图片
const previewImage = (index) => {
  currentImageIndex.value = index
  showImagePreview.value = true
}

// 关闭图片预览
const closeImagePreview = () => {
  showImagePreview.value = false
}

// 分享运动
const shareMotion = () => {
  uni.showToast({
    title: '分享功能开发中',
    icon: 'none'
  })
}

// 页面加载时获取详情
onLoad((options) => {
  if (options.id) {
    fetchMotionDetail(options.id)
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
})
</script>

<template>
  <view class="motion-exercise-video-container">
    <navbar title="运动视频" back="left" home="home"/>
    
    <!-- 加载中 -->
    <view v-if="loading" class="tn-flex tn-flex-center-center tn-py-xl">
      <tn-loading></tn-loading>
    </view>
    
    <!-- 详情内容 -->
    <view v-else class="motion-content">
      <!-- 视频部分 -->
      <view class="video-container">
        <x-video :src="motionDetail.video" :poster="motionDetail.imageList && motionDetail.imageList.length > 0 ? motionDetail.imageList[0] : ''"></x-video>
      </view>
      
      <view class="tn-p-sm">
        <!-- 标题和分享 -->
<!--        <view class="tn-flex tn-flex-row-between tn-flex-col-center">-->
<!--          <text class="motion-title tn-text-xl tn-text-bold">{{ motionDetail.title }}</text>-->
<!--          <tn-button size="sm" bg-color="#f0f0f0" @tap="shareMotion">-->
<!--            <tn-icon name="share" color="#5677fc" size="40rpx"></tn-icon>-->
<!--          </tn-button>-->
<!--        </view>-->
        
        <!-- 描述部分 -->
        <view class="motion-info tn-flex tn-flex-wrap tn-mt-sm">
          <view class="info-item tn-flex tn-flex-column tn-flex-center-center">
            <text class="info-value tn-text-bold">{{ motionDetail.league }}</text>
            <text class="info-label">{{ motionDetail.leagueDescribe }}</text>
          </view>
          
          <view class="info-item tn-flex tn-flex-column tn-flex-center-center">
            <text class="info-value tn-text-bold">{{ motionDetail.time }}</text>
            <text class="info-label">分钟</text>
          </view>
          
          <view class="info-item tn-flex tn-flex-column tn-flex-center-center">
            <text class="info-value tn-text-bold">{{ motionDetail.cal }}</text>
            <text class="info-label">千卡</text>
          </view>
          
          <view class="info-tags">
            <view 
              v-for="(tagItem, tagIndex) in motionDetail.motionFeatures" 
              :key="tagIndex"
              class="info-tag"
            >
              {{ tagItem }}
            </view>
          </view>
        </view>
        
        <!-- 分隔线 -->
        <view class="tn-divider tn-my-sm"></view>
        
        <!-- 运动说明 -->
        <view class="motion-description tn-mt-sm">
          <view class="section-title tn-mb-sm">
            <text class="tn-text-lg tn-text-bold">运动说明</text>
          </view>
          <tn-read-more>
            <text class="description-text">{{ motionDetail.text }}</text>
          </tn-read-more>
        </view>
        
        <!-- 运动图解 -->
        <view class="motion-images tn-mt-lg" v-if="motionDetail.imageList && motionDetail.imageList.length > 0">
          <view class="section-title tn-mb-sm">
            <text class="tn-text-lg tn-text-bold">运动图解</text>
          </view>
          
          <view class="image-grid">
            <view 
              v-for="(image, index) in motionDetail.imageList" 
              :key="index"
              class="image-item tn-shadow-sm"
              @tap="previewImage(index)"
            >
              <tn-lazy-load 
                :src="image" 
                width="220rpx" 
                height="220rpx" 
                mode="aspectFill"
                radius="12rpx"
              />
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 图片预览 -->
    <tn-popup 
      v-model="showImagePreview" 
      mode="center" 
      width="90%" 
      height="90%"
      background-color="rgba(0, 0, 0, 0.9)"
      @close="closeImagePreview"
    >
      <view class="image-preview tn-flex tn-flex-center-center">
        <image 
          :src="motionDetail.imageList[currentImageIndex]" 
          mode="aspectFit" 
          class="preview-image"
        />
        <view class="preview-close" @tap="closeImagePreview">
          <tn-icon name="close" color="#fff" size="40rpx"></tn-icon>
        </view>
        <view class="preview-index">{{ currentImageIndex + 1 }}/{{ motionDetail.imageList.length }}</view>
      </view>
    </tn-popup>
  </view>
</template>

<style scoped lang="scss">
.motion-exercise-video-container {
  min-height: 100vh;
  background-color: #f9f9f9;
}

.motion-content {
  padding-bottom: 40rpx;
}

.video-container {
  width: 100%;
  height: 420rpx;
  background-color: #000;
}

.motion-title {
  flex: 1;
  padding-right: 20rpx;
  line-height: 1.4;
}

.motion-info {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.info-item {
  margin-right: 40rpx;
  margin-bottom: 10rpx;
}

.info-value {
  font-size: 30rpx;
  color: #333;
}

.info-label {
  font-size: 24rpx;
  color: #666;
  margin-top: 4rpx;
}

.info-tags {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.info-tag {
  margin: 6rpx;
  color: #FF7043;
  border: 2rpx solid #FF7043;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  
  &:first-child {
    margin-left: 0;
  }
}

.tn-divider {
  height: 1rpx;
  background-color: #eee;
}

.section-title {
  position: relative;
  padding-left: 20rpx;
  
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 8rpx;
    height: 30rpx;
    background-color: #5677fc;
    border-radius: 4rpx;
  }
}

.motion-description {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.description-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
  text-align: justify;
}

.motion-images {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  margin: -10rpx;
}

.image-item {
  width: 220rpx;
  height: 220rpx;
  margin: 10rpx;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
  
  &:active {
    opacity: 0.8;
  }
}

.image-preview {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
}

.preview-close {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  width: 60rpx;
  height: 60rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-index {
  position: absolute;
  bottom: 20rpx;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 6rpx 20rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
}
</style>