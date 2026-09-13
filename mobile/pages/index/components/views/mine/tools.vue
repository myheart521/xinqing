<script setup>
import {
  ref
} from 'vue';
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

// 跳转
const tn = (e) => {
  // 如果没有路由地址，直接返回
  if (!e) return;
  
  // 需要登录才能访问的页面路由列表
  const needLoginRoutes = [
    '/group_pages/views/my-others-circle',
    '/mine_pages/personal-center',
    "/mine_pages/test-history",
    "/mine_pages/test-data-report",
    "/mine_pages/test-data-report-result",
    "/mine_pages/motion-exercise-history",
    "/mine_pages/my-active",
    "/mine_pages/my-like",
    "/mine_pages/messages",
    "/mine_pages/test-data-report",
    "/mine_pages/personal-center",
    "/mine_pages/motion-exercise-history"
  ];
  
  // 如果是需要登录的页面，先检查登录状态
  if (needLoginRoutes.includes(e)) {
    if (!userStore.checkLogin()) {
      return; // 如果未登录，checkLogin方法会自动跳转到登录页面
    }
  }
  
  uni.navigateTo({
    url: e,
  });
}


const toolsIcon = ref([
  {
    id: 0,
    name: 'forum',
    color: 'rgba(131, 100, 232, 0.1)',
    iconColor: '#8364e8',
    title: '我的圈子',
    router:'/group_pages/views/my-others-circle'+'?userId='+userStore.userInfo.id
  },
  {
    id: 1,
    name: 'collect',
    color: 'rgba(255, 125, 139, 0.1)',
    iconColor: '#FF7D8B',
    title: '我的收藏',
    router:'/mine_pages/my-collection'
  },
  {
    id: 2,
    name: 'history',
    color: 'rgba(86, 119, 252, 0.1)',
    iconColor: '#5677fc',
    title: '参与活动',
    router:'/mine_pages/my-active'
  },
  {
    id: 3,
    name: 'interest',
    color: 'rgba(76, 175, 80, 0.1)',
    iconColor: '#4CAF50',
    title: '我的关注',
    router:'/mine_pages/my-like'
  },
  {
    id: 4,
    name: 'information',
    color: 'rgba(33, 150, 243, 0.1)',
    iconColor: '#2196F3',
    title: '消息通知',
    router:"/mine_pages/messages"
  },
  {
    id: 5,
    name: 'chart',
    color: 'rgba(255, 152, 0, 0.1)',
    iconColor: '#FF9800',
    title: '测评报表',
    router:'/mine_pages/test-data-report'
  },
  {
    id: 6,
    name: 'personal-data',
    color: 'rgba(157, 141, 249, 0.1)',
    iconColor: '#9D8DF9',
    title: '个人资料',
    router: '/mine_pages/personal-center'
  },
  {
    id: 7,
    name: 'motion-history',
    color: 'rgba(0, 150, 136, 0.1)',
    iconColor: '#009688',
    title: '运动记录',
    router:"/mine_pages/motion-exercise-history"
  }
])
</script>

<template>
  <view class="tools-card">
    <view class="card-header">
      <view class="card-title">
        <tn-icon name="star" color="#FF7D8B" size="40rpx"></tn-icon>
        <text>心灵成长工具</text>
      </view>
      <view class="card-subtitle">个性化的心理健康支持工具</view>
    </view>
    
    <view class="tools-grid">
      <view class="tool-item" v-for="(tool, index) in toolsIcon" :key="index" @click="tn(tool.router)">
        <view class="tool-icon" :style="{backgroundColor: tool.color}">
          <tn-lazy-load :src="`/static/mine-image/${tool.name}.png`" width="60rpx" height="60rpx" :style="{filter: `drop-shadow(0 2px 4px ${tool.iconColor}80)`}">
          </tn-lazy-load>
        </view>
        <text class="tool-name">{{tool.title}}</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.tools-card {
  margin-top: 30rpx;
  background: #ffffff;
  border-radius: 24rpx;
  padding: 30rpx;
  box-shadow: 0 10rpx 20rpx rgba(131, 100, 232, 0.08);
  
  .card-header {
    margin-bottom: 30rpx;
    
    .card-title {
      display: flex;
      align-items: center;
      
      text {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
        margin-left: 16rpx;
      }
    }
    
    .card-subtitle {
      font-size: 24rpx;
      color: #8D93A1;
      margin-top: 8rpx;
      margin-left: 56rpx;
    }
  }
  
  .tools-grid {
    display: flex;
    flex-wrap: wrap;
    
    .tool-item {
      width: 25%;
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 30rpx;
      
      .tool-icon {
        width: 100rpx;
        height: 100rpx;
        border-radius: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 12rpx;
        transition: transform 0.2s;
        
        &:active {
          transform: scale(0.95);
        }
      }
      
      .tool-name {
        font-size: 24rpx;
        color: #333;
      }
    }
  }
}
</style>