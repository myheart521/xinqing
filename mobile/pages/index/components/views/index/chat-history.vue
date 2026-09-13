<script setup>
import { ref, onMounted } from 'vue';
import { formatTime, formatRelativeTime } from '@/utils/formate.js';
import { getMemoryList } from '@/service/api/aiChatMemoryController';
import {useUserStore} from "@/stores/user";
import {ossAvatarUrl} from "@/utils/ossUrl";
import {onLoad} from '@dcloudio/uni-app'



const userStore = useUserStore();
// 历史聊天数据
const chatHistoryList = ref([]);
// 加载状态
const isLoading = ref(false);

// 定义emits，用于向父组件传递事件
const emit = defineEmits(['functionChange', 'toggleHeader']);

// 处理点击聊天记录项
const handleChatItemClick = (memoryId) => {
  // 存储当前选中的memoryId到本地
  uni.setStorageSync('chatMemoryId', memoryId);
  
  // 触发事件通知父组件更新 memoryId 变化
  uni.$emit('chat-memory-change', memoryId);
  
  // 切换到聊天内容页面（functionIndex=0）
  emit('functionChange', 0);
};

// 计算相对时间
const getRelativeTime = (dateArray) => {
  return formatRelativeTime(dateArray);
};

// 删除聊天记录
const deleteChatHistory = (event, memoryId) => {
  event.stopPropagation(); // 阻止事件冒泡
  uni.showModal({
    title: '删除确认',
    content: '确定要删除这条聊天记录吗？',
    success: (res) => {
      if (res.confirm) {
        // 这里应该调用删除会话的API，暂时只进行本地操作
        chatHistoryList.value = chatHistoryList.value.filter(item => item.id !== memoryId);
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        });
      }
    }
  });
};

// 清空所有聊天记录
const clearAllHistory = () => {
  uni.showModal({
    title: '清空确认',
    content: '确定要清空所有聊天记录吗？',
    success: (res) => {
      if (res.confirm) {
        chatHistoryList.value = [];
        uni.showToast({
          title: '已清空所有记录',
          icon: 'success'
        });
      }
    }
  });
};

// 新建聊天
const createNewChat = () => {
  // 清除本地存储的memoryId，让chat-content.vue创建新会话
  uni.removeStorageSync('chatMemoryId');
  
  // 切换到聊天内容页面（functionIndex=0）
  emit('functionChange', 0);
};

// 加载会话列表
const loadChatMemoryList = async () => {
  // 检查登录状态
  // if (!userStore.isLogin) {
  //   uni.showToast({
  //     title: '请先登录',
  //     icon: 'none'
  //   });
  //   return;
  // }
  if(!userStore.checkLogin()){
    return
  }
  try {
    isLoading.value = true;
    const res = await getMemoryList();
    
    if (res && res.code === 1) {
      chatHistoryList.value = res.data.map(item => ({
        id: item.id,
        title: item.name || '未命名对话',
        lastMessage: '点击查看对话内容...',
        createTime: formatTime(item.createTime,"YYYY-MM-DD"),
        updateTime: getRelativeTime(formatTime(item.updateTime)),
        avatar:  ossAvatarUrl.avatar2
      }));
    } else {
      uni.showToast({
        title: res?.msg || '获取会话记录失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取会话记录失败:', error);
    uni.showToast({
      title: '获取会话记录失败',
      icon: 'none'
    });
  } finally {
    isLoading.value = false;
  }
};

// 添加 props
const props = defineProps({
  showHeader: {
    type: Boolean,
    default: true
  }
});

// 动画控制状态
const isAddButtonHovered = ref(false);

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
onMounted(()=>{
  loadChatMemoryList();

})

</script>

<template>
  <view 
    class="chat-history-container" 
    :class="{'content-full': !showHeader}" 
    :style="{ height: showHeader ? 'calc(100vh - 480rpx)' : 'calc(100vh - 120rpx)' }"
    @touchstart="handleTouchStart"
    @touchmove="handleTouchMove"
  >
    
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="title-section">
        <tn-icon name="conversation-fill" size="40rpx" color="#5677fc"></tn-icon>
        <text class="title">聊天历史</text>
      </view>
      <view class="actions">
        <view class="action-btn refresh-btn" @click="loadChatMemoryList">
          <tn-icon name="refresh" size="36rpx" color="#666"></tn-icon>
        </view>

        <view class="action-btn" @click="createNewChat">
          <tn-icon name="add-circle" size="40rpx" color="#5677fc"></tn-icon>
        </view>
      </view>
    </view>
    
    <!-- 下拉提示器 - 当助手信息隐藏时 -->
    <view class="pull-indicator" v-show="!showHeader">
      <view class="indicator-arrow">
        <tn-icon name="arrowdown" size="36rpx" color="#ffffff"></tn-icon>
      </view>
      <text>下拉显示助手信息</text>
    </view>
    
    <!-- 正在加载状态 -->
    <view class="loading-state" v-if="isLoading">
      <view class="loading-icon">
        <view class="loading-spinner"></view>
      </view>
      <text class="loading-text">正在加载聊天历史...</text>
    </view>
    
    <!-- 无聊天记录时显示 -->
    <view class="empty-state" v-if="!isLoading && chatHistoryList.length === 0">
      <view class="empty-illustration">
        <tn-icon name="message" size="140rpx" color="#e0e5f3"></tn-icon>
        <view class="empty-glow"></view>
      </view>
      <text class="empty-text">暂无聊天记录</text>
      <text class="empty-subtext">开始一段新对话，探索心灵之旅</text>
      <view class="new-chat-btn" @click="createNewChat">
        <tn-icon name="add" size="32rpx" color="#ffffff"></tn-icon>
        <text>开始新对话</text>
      </view>
    </view>
    
    <!-- 聊天历史列表 -->
    <scroll-view 
      scroll-y 
      class="history-list" 
      v-if="!isLoading && chatHistoryList.length > 0"
    >
      <view 
        class="chat-item" 
        v-for="chat in chatHistoryList" 
        :key="chat.id"
        @click="handleChatItemClick(chat.id)"
      >
        <view class="chat-info">
          <view class="top-line">
            <view class="title-container">
              <tn-icon name="tag" size="28rpx" color="#5677fc"></tn-icon>
              <text class="title">{{ chat.title }}</text>
            </view>
            <view class="date-container">
              <tn-icon name="time" size="22rpx" color="#8d93a1"></tn-icon>
              <text class="date">{{ chat.updateTime }}</text>
            </view>
          </view>
          
          <view class="message-container">
            <tn-icon name="comment" size="26rpx" color="#8d93a1"></tn-icon>
            <view class="message">{{ chat.lastMessage }}</view>
          </view>
          
          <view class="bottom-line">
            <text class="time">创建于: {{ chat.createTime }}</text>
            
          </view>
        </view>
      </view>
    </scroll-view>
    
    <!-- 新建聊天按钮 -->
    <view 
      class="floating-btn" 
      @click="createNewChat"
      @touchstart="isAddButtonHovered = true"
      @touchend="isAddButtonHovered = false"
      @touchcancel="isAddButtonHovered = false"
      :class="{'floating-btn--active': isAddButtonHovered}"
    >
      <view class="floating-btn__inner">
        <tn-icon name="add" size="44rpx" color="#ffffff"></tn-icon>
      </view>
      <view class="floating-btn__label">新对话</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
$primary-color: #5677fc;
$secondary-color: #7B68EE;
$bg-color: rgba(255, 255, 255, 0.3);
$card-bg: rgba(255, 255, 255, 0.8);
$text-color: #333;
$text-secondary: #666;
$text-hint: #8d93a1;

.chat-history-container {
  position: relative;
  background: $bg-color;
  backdrop-filter: blur(40rpx);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 24rpx;
  transition: all 0.3s ease;
  padding: 30rpx 20rpx;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.05);
  overflow: hidden;
  
  // 添加下拉提示器样式
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
  
  .header {
    padding: 20rpx 10rpx 40rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(86, 119, 252, 0.1);
    
    .title-section {
      display: flex;
      align-items: center;
      gap: 12rpx;
      
      .title {
        font-size: 36rpx;
        font-weight: 600;
        color: $text-color;
        background: linear-gradient(90deg, $primary-color, $secondary-color);
        -webkit-background-clip: text;
        color: transparent;
      }
    }
    
    .actions {
      display: flex;
      align-items: center;
      
      .action-btn {
        width: 70rpx;
        height: 70rpx;
        margin-left: 10rpx;
        border-radius: 50%;
        background: rgba(255,255,255,0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s;
        
        &:active {
          transform: scale(0.9);
          background: rgba(246, 249, 255, 0.9);
        }
        
        &.refresh-btn {
          &:active {
            animation: rotate 0.5s linear;
          }
        }
      }
    }
  }
  
  .loading-state {
    height: 60vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    
    .loading-icon {
      .loading-spinner {
        width: 80rpx;
        height: 80rpx;
        border: 8rpx solid rgba(86, 119, 252, 0.1);
        border-top: 8rpx solid $primary-color;
        border-radius: 50%;
        animation: spin 1s linear infinite;
      }
    }
    
    .loading-text {
      margin-top: 30rpx;
      font-size: 28rpx;
      color: $text-secondary;
      letter-spacing: 2rpx;
    }
  }
  
  .empty-state {
    height: 60vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    
    .empty-illustration {
      position: relative;
      margin-bottom: 20rpx;
      
      .empty-glow {
        position: absolute;
        width: 180rpx;
        height: 180rpx;
        border-radius: 50%;
        background: radial-gradient(circle, rgba(86,119,252,0.2) 0%, rgba(255,255,255,0) 70%);
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: -1;
        animation: pulse 3s infinite ease-in-out;
      }
    }
    
    .empty-text {
      font-size: 32rpx;
      font-weight: 500;
      color: $text-color;
      margin-bottom: 12rpx;
    }
    
    .empty-subtext {
      font-size: 26rpx;
      color: $text-hint;
      margin-bottom: 40rpx;
    }
    
    .new-chat-btn {
      display: flex;
      align-items: center;
      gap: 8rpx;
      background: linear-gradient(135deg, $primary-color, $secondary-color);
      color: white;
      padding: 20rpx 60rpx;
      border-radius: 100rpx;
      font-size: 28rpx;
      box-shadow: 0 8rpx 24rpx rgba(86, 119, 252, 0.3);
      transition: all 0.3s;
      
      &:active {
        transform: scale(0.95);
        box-shadow: 0 4rpx 12rpx rgba(86, 119, 252, 0.2);
      }
    }
  }
  
  .history-list {
    height: calc(100% - 120rpx);
    padding-bottom: 100rpx;
    
    .chat-item {
      background: $card-bg;
      border-radius: 24rpx;
      margin-bottom: 24rpx;
      padding: 24rpx;
      transition: all 0.2s;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
      border-left: 4rpx solid $primary-color;
      
      &:active {
        transform: scale(0.98);
        background: rgba(246, 248, 255, 0.9);
      }
      
      .chat-info {
        flex: 1;
        overflow: hidden;
        
        .top-line {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16rpx;
          
          .title-container {
            display: flex;
            align-items: center;
            gap: 8rpx;
            
            .title {
              font-size: 32rpx;
              font-weight: 600;
              color: $text-color;
            }
          }
          
          .date-container {
            display: flex;
            align-items: center;
            gap: 4rpx;
            background: rgba(86, 119, 252, 0.1);
            padding: 6rpx 12rpx;
            border-radius: 8rpx;
            
            .date {
              font-size: 22rpx;
              color: $primary-color;
            }
          }
        }
        
        .message-container {
          display: flex;
          align-items: center;
          gap: 8rpx;
          margin-bottom: 16rpx;
          
          .message {
            font-size: 26rpx;
            color: $text-secondary;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            flex: 1;
          }
        }
        
        .bottom-line {
          display: flex;
          justify-content: space-between;
          align-items: center;
          border-top: 1px dashed rgba(86, 119, 252, 0.1);
          padding-top: 12rpx;
          
          .time {
            font-size: 22rpx;
            color: $text-hint;
          }
          
          .delete-btn {
            width: 60rpx;
            height: 60rpx;
            border-radius: 50%;
            background: rgba(255, 100, 100, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            
            &:active {
              transform: scale(0.9);
              background: rgba(255, 100, 100, 0.2);
            }
          }
        }
      }
    }
  }
  
  .floating-btn {
    position: fixed;
    right: 40rpx;
    bottom: 100rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    z-index: 100;
    
    .floating-btn__inner {
      width: 100rpx;
      height: 100rpx;
      border-radius: 50%;
      background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 8rpx 32rpx rgba(86, 119, 252, 0.4);
      transition: all 0.3s;
    }
    
    .floating-btn__label {
      font-size: 22rpx;
      color: $text-secondary;
      background: rgba(255, 255, 255, 0.9);
      padding: 6rpx 16rpx;
      border-radius: 100rpx;
      margin-top: 8rpx;
      opacity: 0;
      transform: translateY(-10rpx);
      transition: all 0.3s;
    }
    
    &--active {
      .floating-btn__inner {
        transform: scale(0.92) rotate(90deg);
      }
      
      .floating-btn__label {
        opacity: 1;
        transform: translateY(0);
      }
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: translate(-50%, -50%) scale(0.9); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.1); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20rpx); }
}
</style>