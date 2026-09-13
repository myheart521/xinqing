<script setup>
import {
  ref
} from 'vue';
import {
  useNavSize
} from '@/utils/nav-height.js'
import {ossAvatarUrl, ossImages} from "@/utils/ossUrl";

const avatar2 = ossImages.avatar2
const {
  status,
  navHeight
} = useNavSize()
const AiWei = ossAvatarUrl.AiWei

const aIRoles = ref([{
  id: 1,
  name: '晴晴',
  describe: '来自New Boy团队，你的知心小姐姐',
  avatar: [
    avatar2
  ]
},
  {
    id: 2,
    name: '艾薇',
    describe: '来自EmoLLM的专业心理咨询助手',
    avatar: [AiWei]
  },
  {
    id: 3,
    name: '天天',
    describe: '开朗活泼的小哥哥',
    avatar: ['https://assets.example.invalid/placeholder.png']
  }
])

// 添加 props和emits
const props = defineProps({
  showHeader: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['toggleHeader']);

// 添加控制头部显示的状态 - 与chat-content相同
const touchStartY = ref(0);
const touchMoveY = ref(0);
const threshold = 50; // 触发阈值

// 处理触摸事件
const handleTouchStart = (e) => {
  touchStartY.value = e.touches[0].clientY;
}

const handleTouchMove = (e) => {
  touchMoveY.value = e.touches[0].clientY;
  const deltaY = touchMoveY.value - touchStartY.value;
  
  // 下拉显示助手信息
  if (deltaY > threshold && !props.showHeader) {
    emit('toggleHeader', true);
    uni.vibrateShort(); // 添加触感反馈
  }
  
  // 上滑隐藏助手信息
  if (deltaY < -threshold && props.showHeader) {
    emit('toggleHeader', false);
    uni.vibrateShort(); // 添加触感反馈
  }
}
</script>

<template>
  <view 
    class="content" 
    :class="{'content-full': !showHeader}" 
    :style="{ height: showHeader ? 'calc(100vh - 480rpx)' : 'calc(100vh - 120rpx)' }"
    @touchstart="handleTouchStart"
    @touchmove="handleTouchMove"
  >
    <!-- 下拉提示器 - 当助手信息隐藏时 -->
    <view class="pull-indicator" v-show="!showHeader">
      <view class="indicator-arrow">
        <tn-icon name="arrowdown" size="36rpx" color="#ffffff"></tn-icon>
      </view>
      <text>下拉显示助手信息</text>
    </view>
    
    <!--人物列表 -->
    <scroll-view scroll-y class="scroll-Y">
      <view class="counselor-title">
        <tn-icon name="people-fill" size="38rpx" color="#5677fc"></tn-icon>
        <text>可选助手</text>
      </view>
      <view v-for="item in aIRoles" :key="item.id" class="counselor-card">
        <view class="avatar-container">
          <image :src="item.avatar[0]" mode="aspectFill" class="avatar"></image>
          <view class="status-badge">
            <tn-icon name="circle" size="16rpx" color="#4CAF50"></tn-icon>
            <text>在线</text>
          </view>
        </view>
        <view class="info-container">
          <view class="name-row">
            <text class="name">{{ item.name }}</text>
            <view class="badge">心理助手</view>
          </view>
          <view class="description">{{ item.describe }}</view>
          <view class="action-row">
            <button class="select-btn">
              <tn-icon name="chat" size="24rpx" color="#ffffff"></tn-icon>
              <text>选择</text>
            </button>
            <view class="more-info">
              <tn-icon name="info" size="32rpx" color="#8d93a1"></tn-icon>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.content {
  width: 100%;
  position: relative;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(40rpx);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 20rpx;
  transition: all 0.3s ease;
  
  .pull-indicator {
    position: absolute;
    top: 10rpx;
    left: 50%;
    transform: translateX(-50%);
    z-index: 100;
    background: rgba(0,0,0,0.2);
    border-radius: 20rpx;
    padding: 8rpx 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    text {
      color: #ffffff;
      font-size: 22rpx;
      margin-top: 4rpx;
    }
    
    .indicator-arrow {
      animation: bounce 2s infinite ease-in-out;
    }
  }

  .scroll-Y {
    height: calc(100% - 20rpx);
    padding: 20rpx;
    
    .counselor-title {
      display: flex;
      align-items: center;
      gap: 12rpx;
      margin-bottom: 20rpx;
      padding: 0 10rpx;
      
      text {
        font-size: 34rpx;
        font-weight: 600;
        color: #2C3A4B;
        background: linear-gradient(90deg, #5677fc, #7B68EE);
        -webkit-background-clip: text;
        color: transparent;
      }
    }
    
    .counselor-card {
      background: rgba(255,255,255,0.8);
      border-radius: 20rpx;
      padding: 20rpx;
      margin-bottom: 20rpx;
      display: flex;
      align-items: center;
      transition: all 0.3s;
      box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.05);
      
      &:active {
        transform: scale(0.98);
        background: rgba(246, 248, 255, 0.9);
      }
      
      .avatar-container {
        position: relative;
        margin-right: 20rpx;
        
        .avatar {
          width: 140rpx;
          height: 140rpx;
          border-radius: 50%;
          border: 4rpx solid rgba(255,255,255,0.8);
          box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.1);
        }
        
        .status-badge {
          position: absolute;
          bottom: 0;
          right: 0;
          background: rgba(255,255,255,0.95);
          padding: 4rpx 12rpx;
          border-radius: 20rpx;
          display: flex;
          align-items: center;
          gap: 4rpx;
          box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
          
          text {
            font-size: 20rpx;
            color: #4CAF50;
            font-weight: 500;
          }
        }
      }
      
      .info-container {
        flex: 1;
        overflow: hidden;
        
        .name-row {
          display: flex;
          align-items: center;
          margin-bottom: 10rpx;
          
          .name {
            font-size: 34rpx;
            font-weight: 600;
            color: #2C3A4B;
            margin-right: 16rpx;
          }
          
          .badge {
            background: rgba(86,119,252,0.1);
            color: #5677fc;
            font-size: 22rpx;
            padding: 4rpx 12rpx;
            border-radius: 8rpx;
            font-weight: 500;
          }
        }
        
        .description {
          font-size: 26rpx;
          color: #666;
          line-height: 1.4;
          margin-bottom: 16rpx;
        }
        
        .action-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          
          .select-btn {
            background: linear-gradient(135deg, #5677fc, #7B68EE);
            border: none;
            padding: 10rpx 30rpx;
            border-radius: 100rpx;
            color: #fff;
            font-size: 24rpx;
            display: flex;
            align-items: center;
            gap: 8rpx;
            box-shadow: 0 4rpx 16rpx rgba(86,119,252,0.3);
            
            &:active {
              transform: scale(0.95);
            }
          }
          
          .more-info {
            width: 60rpx;
            height: 60rpx;
            border-radius: 50%;
            background: rgba(0,0,0,0.03);
            display: flex;
            align-items: center;
            justify-content: center;
            
            &:active {
              background: rgba(0,0,0,0.06);
            }
          }
        }
      }
    }
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6rpx); }
}
</style>