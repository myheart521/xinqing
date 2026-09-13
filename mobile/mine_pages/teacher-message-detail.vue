<template>
  <view class="page">
    <scroll-view class="scroll-view" scroll-y scroll-with-animation :scroll-top="top">
      <navbar :title="'与'+title+'聊天中'" back="left" home="home"></navbar>
      <view style="padding: 30rpx 30rpx 240rpx;">
        <!-- 顶部加载更多按钮 -->
        <view v-if="hasMore" class="history-load-btn">
          <button @click="loadMoreHistory" :disabled="loadingHistory" class="load-btn">
            <text v-if="!loadingHistory">加载更多历史消息</text>
            <text v-else>加载中...</text>
          </button>
        </view>
        <!-- 没有更多历史消息 -->
        <view v-else class="history-no-more">
          <text>没有了</text>
        </view>
        <!-- 聊天消息列表 -->
        <view class="message" :class="[item.userType]" v-for="(item,index) in list" :key="index">
          <image :src="item.avatar" v-if="item.userType === 'friend'" class="avatar" mode="aspectFill"></image>
          <view class="content" v-if="item.messageType === 'image'">
            <image :src="item.content" mode="widthFix"></image>
          </view>
          <view class="content" v-else>
            {{ item.content }}
          </view>
          <image :src="item.avatar" v-if="item.userType === 'self'" class="avatar" mode="aspectFill"></image>
        </view>
      </view>
    </scroll-view>

    <!-- 底部工具栏 -->
    <view class="tool">
      <!-- 输入框 -->
      <view class="input-container">
        <input type="text" v-model="content" class="input" placeholder="请输入消息..." @confirm="send"/>
      </view>

      <!-- 功能按钮区 -->
      <view class="button-group">
        <!-- 图片按钮 -->
        <view class="tool-button" @tap="chooseImage">
          <tn-icon name="image-fill" size="48rpx" color="#5677fc"></tn-icon>
        </view>

        <!-- 视频通话按钮 -->
        <view class="tool-button" @tap="startVideoCall">
          <tn-icon name="video-fill" size="48rpx" color="#5677fc"></tn-icon>
        </view>

        <!-- 发送按钮 -->
        <view class="send-button" @tap="send" :class="{'send-button--active': content.trim().length > 0}">
          <tn-icon name="send" size="40rpx" color="#fff"></tn-icon>
        </view>
      </view>
    </view>

    <!-- 视频通话弹窗 -->
    <tn-popup v-model="showVideoCallPopup" mode="center" width="80%" height="auto">
      <view class="video-call-popup">
        <view class="video-call-header">
          <text class="video-call-title">视频通话</text>
        </view>
        <view class="video-call-content">
          <image :src="_friendAvatar" class="video-call-avatar" mode="aspectFill"></image>
          <text class="video-call-name">{{ title }}</text>
          <text class="video-call-status">正在呼叫...</text>
        </view>
        <view class="video-call-actions">
          <view class="video-call-action video-call-action--decline" @tap="cancelVideoCall">
            <tn-icon name="close" size="48rpx" color="#fff"></tn-icon>
            <text>取消</text>
          </view>
        </view>
      </view>
    </tn-popup>
  </view>
</template>

<script setup>
import navbar from "@/components/navbar.vue";
import {ref, onMounted, onUnmounted} from 'vue';
import {onLoad} from '@dcloudio/uni-app';
import {useUserStore} from "@/stores/user";
import WebSocketUtil from "@/utils/websocket";
import {ossAvatarUrl} from "@/utils/ossUrl";
import {getHistory} from "@/service/api/chatController";

const content = ref('');
const list = ref([]);
const top = ref(0);
const title = ref("");
const _friendAvatar = ref('');
const receiverId = ref('')

const showVideoCallPopup = ref(false);
const wsUtil = ref(null);

const userStore = useUserStore();
const _selfAvatar = userStore.userAvatar
// 分页相关
const currentPage = ref(1);
const pageSize = 20;
const hasMore = ref(true);
const loadingHistory = ref(false);

// 页面加载
onLoad((options) => {
  title.value = options.name;
  _friendAvatar.value = options.avatar;
  if (options.receiverId) {
    receiverId.value = options.receiverId;
    initWebSocket()
    // 获取历史消息第一页
    getHistoryMessages(true);
  }
});

// 获取历史聊天记录
const getHistoryMessages = async (isInit = false) => {
  if (loadingHistory.value || !hasMore.value) return;
  loadingHistory.value = true;
  try {
    const res = await getHistory({
      receiverId: receiverId.value,
      currentPage: currentPage.value,
      pageSize: pageSize
    });
    
    // 检查返回数据结构
    if (res && res.code === 1 && res.data && Array.isArray(res.data.history)) {
      const historyItems = res.data.history;
      
      // 格式化历史消息
      const history = historyItems.map(item => {
        // 尝试解析 content 字段（可能是 JSON 字符串）
        let parsedContent = item.content;
        try {
          parsedContent = JSON.parse(item.content);
        } catch (e) {
          // 解析失败，保持原样
        }
        
        // 提取实际消息内容
        const content = parsedContent?.content || parsedContent || item.content;
        
        // 判断消息类型
        const messageType = parsedContent?.type === 'image' ? 'image' : 'text';
        
        return {
          content,
          userType: item.sender === 'me' ? 'self' : 'friend',
          avatar: item.sender === 'me' ? _selfAvatar : _friendAvatar.value,
          messageType
        };
      });
      
      // 新消息插入到顶部
      if (isInit) {
        list.value = [...history.reverse()];
      } else {
        // 加载更多时，添加到现有列表顶部
        list.value = [...history.reverse(), ...list.value];
      }
      
      // 判断是否还有更多
      hasMore.value = historyItems.length === pageSize;
      
      // 下一页
      currentPage.value++;
      
      // 首次加载滚动到底部
      if (isInit) scrollToBottom();
    } else {
      hasMore.value = false;
      // 如果是第一页且没有数据，清空list
      if (isInit) list.value = [];
    }
  } catch (e) {
    uni.showToast({title: '加载历史消息失败', icon: 'none'});
  } finally {
    loadingHistory.value = false;
  }
};

// 点击按钮加载更多
const loadMoreHistory = () => {
  getHistoryMessages(false);
};

// 初始化WebSocket
const initWebSocket = () => {
  wsUtil.value = new WebSocketUtil({
    receiverId: receiverId.value,
    token: userStore.userInfo.token
  });

  // 重写onMessage方法来处理接收到的消息
  wsUtil.value.onMessage = (message) => {
    try {
      // 如果收到的是字符串，尝试解析为 JSON；解析失败则按纯文本处理
      if (typeof message === 'string') {
        try {
          message = JSON.parse(message);
        } catch (err) {
          // 解析失败，说明是纯文本消息
        }
      }

      // 如果还是字符串或数字，直接作为文本内容显示
      if (typeof message === 'string' || typeof message === 'number') {
        list.value.push({
          content: String(message),
          userType: 'friend',
          avatar: _friendAvatar.value
        });
        scrollToBottom();
        return;
      }

      // 处理 JSON 格式消息（必须包含 content 字段）
      if (message && message.content) {
        list.value.push({
          content: message.content,
          userType: 'friend',
          avatar: _friendAvatar.value,
          messageType: message.type === 'image' ? 'image' : 'text'
        });
        scrollToBottom();
      }
    } catch (e) {
      console.error('解析消息失败:', e);
    }
  };

  // 连接WebSocket
  wsUtil.value.connect().catch(error => {
    const errorMsg = error?.errMsg || error?.message || JSON.stringify(error);
    uni.showToast({
      title: `连接失败，请重试：${errorMsg}`,
      icon: 'none'
    })
  });
};

// 发送消息
const send = () => {
  if (!content.value.trim()) return;

  const message = content.value

  // 使用WebSocket工具类发送消息
  wsUtil.value.send(message).then(() => {
    // 发送成功，添加到消息列表
    list.value.push({
      content: content.value,
      userType: 'self',
      avatar: _selfAvatar
    });
    content.value = '';
    scrollToBottom();
  }).catch(error => {
    console.error('发送消息失败:', error);
    uni.showToast({
      title: '发送失败，请重试',
      icon: 'none'
    });
  });
};

// 选择图片
const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      // 这里可以先上传图片到服务器，获取URL后再通过WebSocket发送
      const imageMessage = {
        type: 'image',
        content: res.tempFilePaths[0],
        receiverId: receiverId.value
      };

      wsUtil.value.send(imageMessage).then(() => {
        list.value.push({
          content: res.tempFilePaths[0],
          userType: 'self',
          messageType: 'image',
          avatar: _selfAvatar
        });
        scrollToBottom();
      }).catch(error => {
        console.error('发送图片失败:', error);
        uni.showToast({
          title: '发送图片失败',
          icon: 'none'
        });
      });
    }
  });
};

// 滚动到底部
const scrollToBottom = () => {
  setTimeout(() => {
    top.value = 9999999;
  }, 100);
};

// 组件卸载时清理WebSocket连接
onUnmounted(() => {
  if (wsUtil.value) {
    wsUtil.value.destroy();
  }
});

</script>

<style lang="scss" scoped>
.page {
  position: relative;
  height: 100vh;
  background-color: #f5f7fa;
}

.scroll-view {
  /* #ifdef H5 */
  height: calc(100vh - 44px);
  /* #endif */
  /* #ifndef H5 */
  height: 100vh;
  /* #endif */
  box-sizing: border-box;
}

.history-load-btn {
  text-align: center;
  margin-bottom: 20rpx;

  .load-btn {
    background: #f5f7fa;
    color: #5677fc;
    border-radius: 30rpx;
    padding: 12rpx 40rpx;
    font-size: 26rpx;
    border: none;
  }
}

.history-no-more {
  text-align: center;
  color: #999;
  font-size: 26rpx;
  margin-bottom: 20rpx;
}

.message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 30rpx;

  .avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-right: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
    border: 2rpx solid #fff;
  }

  .content {
    min-height: 80rpx;
    max-width: 60vw;
    box-sizing: border-box;
    font-size: 28rpx;
    line-height: 1.5;
    padding: 20rpx 24rpx;
    border-radius: 20rpx;
    background: #fff;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    word-break: break-all;

    image {
      width: 200rpx;
      border-radius: 12rpx;
    }
  }

  &.self {
    justify-content: flex-end;

    .avatar {
      margin: 0 0 0 20rpx;
    }

    .content {
      position: relative;
      background-color: #5677fc;
      color: #fff;

      &::after {
        position: absolute;
        content: '';
        width: 0;
        height: 0;
        border: 16rpx solid transparent;
        border-left: 16rpx solid #5677fc;
        right: -28rpx;
        top: 24rpx;
      }
    }
  }

  &.friend {
    .content {
      position: relative;
      background-color: #fff;

      &::after {
        position: absolute;
        content: '';
        width: 0;
        height: 0;
        border: 16rpx solid transparent;
        border-right: 16rpx solid #fff;
        left: -28rpx;
        top: 24rpx;
      }
    }
  }
}

.tool {
  position: fixed;
  width: 100%;
  left: 0;
  bottom: 0;
  background: #fff;
  box-sizing: border-box;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + constant(safe-area-inset-bottom) / 2) !important;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom) / 2) !important;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.input-container {
  width: 100%;
  margin-bottom: 16rpx;

  .input {
    width: 100%;
    background: #f5f7fa;
    border-radius: 40rpx;
    height: 80rpx;
    padding: 0 30rpx;
    box-sizing: border-box;
    font-size: 28rpx;
    border: 2rpx solid #eee;
  }
}

.button-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tool-button {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f5f7fa;
  margin-right: 20rpx;
  transition: all 0.2s;

  &:active {
    transform: scale(0.9);
    background-color: #e8eaed;
  }
}

.send-button {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #cccccc;
  margin-left: auto;
  transition: all 0.2s;

  &--active {
    background-color: #5677fc;
  }

  &:active {
    transform: scale(0.9);
  }
}

// 视频通话弹窗样式
.video-call-popup {
  background-color: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .video-call-header {
    padding: 30rpx;
    text-align: center;
    border-bottom: 2rpx solid #f5f7fa;

    .video-call-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }

  .video-call-content {
    padding: 40rpx;
    display: flex;
    flex-direction: column;
    align-items: center;

    .video-call-avatar {
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
      margin-bottom: 20rpx;
      border: 4rpx solid #5677fc;
    }

    .video-call-name {
      font-size: 32rpx;
      font-weight: bold;
      margin-bottom: 10rpx;
    }

    .video-call-status {
      font-size: 28rpx;
      color: #666;
    }
  }

  .video-call-actions {
    display: flex;
    justify-content: center;
    padding: 30rpx;

    .video-call-action {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      text {
        font-size: 24rpx;
        margin-top: 10rpx;
        color: #fff;
      }

      &--decline {
        background-color: #ff4d4f;
      }
    }
  }
}
</style>
