<script setup>
import { getPublicIntegrations } from "@/utils/integrations.js"
import navbar from "@/components/navbar.vue";
import { ref, onMounted } from 'vue'

// 展厅数据
const exhibitions = ref([
  {
    id: 1,
    title: "心理健康科普展厅",
    subtitle: "基于ThreeJS技术打造的沉浸式体验",
    description: "通过3D互动方式，深入了解心理健康知识，感受科技与心理学的完美结合",
    url: getPublicIntegrations().galleryUrl,
    cover: "/static/images/default.jpg",
    tags: ["3D互动", "科普教育", "沉浸体验"],
    visitCount: "1.2万"
  },
    {
    id: 2,
    title: "心理健康教育基地",
    subtitle: "专业权威的心理健康教育平台", 
    description: "心理健康教育基地，提供专业的心理健康知识和教育资源",
    url: "https://kepu.qingdaonews.com/attachs/vr/healthjingshenweisheng/index.html",
    cover: "/static/images/default.jpg",
    tags: ["官方认证", "教育基地", "专业权威"],
    visitCount: "2.8万"
  }
])

// 获取平台信息
const platform = ref('')

onMounted(() => {
  // #ifdef MP-WEIXIN
  platform.value = 'mp-weixin'
  // #endif
  
  // #ifdef APP-PLUS
  platform.value = 'app'
  // #endif
  
  // #ifdef H5
  platform.value = 'h5'
  // #endif
})

// 跳转到展厅
const openExhibition = (exhibition) => {
  if (!exhibition.url) {
    uni.showToast({ title: '????????', icon: 'none' });
    return;
  }
  console.log('打开展厅：', exhibition.title, '平台：', platform.value)
  
    // 微信小程序：跳转到webview页面
    uni.navigateTo({
      url: `/function_pages/views/virtual-exhibition/webview?url=${encodeURIComponent(exhibition.url)}&title=${encodeURIComponent(exhibition.title)}`
    })
}

// 分享展厅
const shareExhibition = (exhibition) => {
  uni.showActionSheet({
    itemList: ['复制链接', '分享给好友'],
    success: (res) => {
      if (res.tapIndex === 0) {
        // 复制链接
        uni.setClipboardData({
          data: exhibition.url,
          success: () => {
            uni.showToast({
              title: '链接已复制',
              icon: 'success'
            })
          }
        })
      } else if (res.tapIndex === 1) {
        // 分享功能
        uni.share({
          provider: "weixin",
          scene: "WXSceneSession",
          type: 0,
          href: exhibition.url,
          title: exhibition.title,
          summary: exhibition.description,
          imageUrl: exhibition.cover
        })
      }
    }
  })
}
</script>

<template>
  <view class="exhibition-container">
    <navbar title="虚拟展厅列表"></navbar>
    
    <!-- 主要内容区域 -->
    <view class="content">
      <!-- 页面标题 -->
      <view class="page-header">
        <view class="header-title">心理健康虚拟展厅</view>
        <view class="header-subtitle">沉浸式体验 · 科普教育 · 专业权威</view>
      </view>
      
      <!-- 展厅列表 -->
      <view class="exhibition-list">
        <view 
          class="exhibition-card" 
          v-for="item in exhibitions" 
          :key="item.id"
          @click="openExhibition(item)"
        >
          <!-- 卡片封面 -->
          <view class="card-cover">
            <image :src="item.cover" mode="aspectFill" class="cover-image"></image>
                          <view class="visit-count">
                <text class="count-icon">👁</text>
<!--                <text class="count-text">{{item.visitCount}}次访问</text>-->
              </view>
          </view>
          
          <!-- 卡片内容 -->
          <view class="card-content">
            <view class="card-title">{{item.title}}</view>
            <view class="card-subtitle">{{item.subtitle}}</view>
            <view class="card-description">{{item.description}}</view>
            
            <!-- 标签 -->
            <view class="card-tags">
              <view class="tag" v-for="tag in item.tags" :key="tag">{{tag}}</view>
            </view>
            
            <!-- 操作按钮 -->
            <view class="card-actions">
              <view class="action-btn primary" @click.stop="openExhibition(item)">
                <text class="btn-icon">🎯</text>
                <text class="btn-text">立即体验</text>
              </view>
              <view class="action-btn secondary" @click.stop="shareExhibition(item)">
                <text class="btn-icon">📤</text>
                <text class="btn-text">分享</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 底部提示 -->
      <view class="footer-tip">
        <text class="tip-text">💡 建议在WiFi环境下访问，获得更佳体验</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.exhibition-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content {
  padding: 20rpx;
  padding-top: 40rpx;
}

.page-header {
  text-align: center;
  margin-bottom: 40rpx;
  
  .header-title {
    font-size: 48rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 16rpx;
    text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.3);
  }
  
  .header-subtitle {
    font-size: 28rpx;
    color: rgba(255,255,255,0.9);
    letter-spacing: 2rpx;
  }
}

.exhibition-list {
  .exhibition-card {
    background: #ffffff;
    border-radius: 24rpx;
    margin-bottom: 32rpx;
    overflow: hidden;
    box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.1);
    transition: all 0.3s ease;
    
    &:active {
      transform: scale(0.98);
    }
  }
}

.card-cover {
  position: relative;
  height: 320rpx;
  
  .cover-image {
    width: 100%;
    height: 100%;
    background: linear-gradient(45deg, #74b9ff, #0984e3);
  }
  
  .visit-count {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    background: rgba(0,0,0,0.6);
    border-radius: 20rpx;
    padding: 8rpx 16rpx;
    display: flex;
    align-items: center;
    
    .count-icon {
      font-size: 24rpx;
      margin-right: 8rpx;
    }
    
    .count-text {
      font-size: 22rpx;
      color: #ffffff;
    }
  }
}

.card-content {
  padding: 32rpx;
  
  .card-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #2d3436;
    margin-bottom: 12rpx;
  }
  
  .card-subtitle {
    font-size: 28rpx;
    color: #636e72;
    margin-bottom: 16rpx;
  }
  
  .card-description {
    font-size: 26rpx;
    color: #74b9ff;
    line-height: 1.6;
    margin-bottom: 24rpx;
  }
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 32rpx;
  
  .tag {
    background: linear-gradient(45deg, #fd79a8, #e84393);
    color: #ffffff;
    padding: 8rpx 16rpx;
    border-radius: 20rpx;
    font-size: 22rpx;
    margin-right: 16rpx;
    margin-bottom: 8rpx;
  }
}

.card-actions {
  display: flex;
  gap: 24rpx;
  
  .action-btn {
    flex: 1;
    padding: 24rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    
    .btn-icon {
      margin-right: 8rpx;
      font-size: 28rpx;
    }
    
    .btn-text {
      font-size: 28rpx;
      font-weight: bold;
    }
    
    &.primary {
      background: linear-gradient(45deg, #00b894, #00cec9);
      color: #ffffff;
      
      &:active {
        transform: scale(0.95);
      }
    }
    
    &.secondary {
      background: #f8f9fa;
      color: #74b9ff;
      border: 2rpx solid #74b9ff;
      
      &:active {
        background: #74b9ff;
        color: #ffffff;
      }
    }
  }
}

.footer-tip {
  text-align: center;
  margin-top: 60rpx;
  padding: 32rpx;
  
  .tip-text {
    font-size: 26rpx;
    color: rgba(255,255,255,0.8);
    line-height: 1.5;
  }
}
</style>
