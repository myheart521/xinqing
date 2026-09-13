<script setup>
import chatFunctionVue from './views/index/chat-function.vue';
import chatContentVue from './views/index/chat-content.vue';
import backgroundImageVue from './views/index/background-image.vue';
import functionContentVue from './views/index/function-content.vue';
import chatHistoryVue from './views/index/chat-history.vue';

import {
  ref, onMounted, onBeforeUnmount,onUnmounted
} from 'vue';
import {useUserStore} from "@/stores/user";


const userStore = useUserStore()
//记录顶部功能选项
//0表示聊天，1表示选择聊天体，2表示历史记录
const functionIndex = ref(0)
const oldFunctionIndex = ref(0)

// chat-function 组件引用
const chatFunctionRef = ref(null);

const functionChange = (index) => {
  oldFunctionIndex.value = functionIndex.value
  functionIndex.value = index

  // 如果有引用，更新chat-function中的选中状态
  if (chatFunctionRef.value) {
    chatFunctionRef.value.updateSelectIndex(index);
  }
}

const inputBottomValue = ref(''); //输入框距离底部距离
// 响应式键盘高度
const keyboardHeight = ref(0)
// 防抖处理 (跨平台共用)
const debounce = (fn, delay = 300) => {
  let timer
  return (...args) => {
    clearTimeout(timer)
    timer = setTimeout(() => fn(...args), delay)
  }
}
// 核心处理逻辑
const handleHeightChange = debounce((event) => {
  try {
    // 微信小程序处理 (精确值)
    // #ifdef MP-WEIXIN
    keyboardHeight.value = event?.height || 0
    // #endif
    // 安卓/H5处理 (窗口高度计算)
    // #ifdef H5 || APP-ANDROID
    const sysInfo = uni.getSystemInfoSync()
    const { windowHeight, screenHeight } = sysInfo

    // 横屏模式特殊处理
    const isLandscape = sysInfo.screenWidth > sysInfo.screenHeight
    const scale = isLandscape ? 2 : 1

    // 计算键盘高度（确保非负数）
    keyboardHeight.value = Math.max(
        screenHeight - windowHeight * scale,
        0
    )
    // #endif
    console.log('Keyboard height changed:', keyboardHeight.value + 'px')
  } catch (error) {
    console.error('键盘高度计算错误:', error)
  }
})
// 输入框焦点事件处理（H5专用）
// #ifdef H5
const handleFocus = () => {
  window.addEventListener('resize', handleHeightChange)
  handleHeightChange() // 立即触发计算
}
const handleBlur = () => {
  window.removeEventListener('resize', handleHeightChange)
  keyboardHeight.value = 0
}
// #endif
// 生命周期管理
onMounted(() => {
  userStore.initUserInfo()
  // 微信初始化监听
  // #ifdef MP-WEIXIN
  uni.onKeyboardHeightChange(handleHeightChange)
  // #endif
  // 安卓初始化
  // #ifdef APP-ANDROID
  window.addEventListener('resize', handleHeightChange)
  // #endif
})
onUnmounted(() => {
  // 微信注销监听
  // #ifdef MP-WEIXIN
  uni.offKeyboardHeightChange(handleHeightChange)
  // #endif
  // 安卓/H5注销
  // #ifdef H5 || APP-ANDROID
  window.removeEventListener('resize', handleHeightChange)
  // #endif
})

// 添加控制头部显示的状态
const showHeader = ref(true);
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

  // 下拉显示顾问信息
  if (deltaY > threshold && !showHeader.value) {
    showHeader.value = true;
    uni.vibrateShort(); // 添加触感反馈
  }

  // 上滑隐藏顾问信息
  if (deltaY < -threshold && showHeader.value) {
    showHeader.value = false;
    uni.vibrateShort(); // 添加触感反馈
  }
}

// 添加一个新的方法来设置头部的显示状态
const toggleHeader = (value) => {
  showHeader.value = value;
}

onMounted(() => {
  // 监听memoryId变化事件
  uni.$on('chat-memory-change', handleMemoryChange);
  const memoryId = uni.getStorageSync('chatMemoryId');
  console.log("memoryId:", memoryId);
})

onBeforeUnmount(() => {
  // 移除事件监听
  uni.$off('chat-memory-change', handleMemoryChange);
})

// 处理memoryId变化
const handleMemoryChange = (memoryId) => {
  // 这里可以添加其他需要的处理逻辑
  console.log('Memory ID changed:', memoryId);
}
</script>

<template>
  <view class="content">
    <background-image-vue></background-image-vue>
    <!-- 顶部AI聊天 -->
    <view class="chat">
      <!-- 聊天顶部功能按钮 -->
      <view
          class="chatFunction"
          :class="{'header-visible': showHeader, 'header-hidden': !showHeader}"
      >
        <chatFunctionVue ref="chatFunctionRef" @functionChange='functionChange'></chatFunctionVue>
      </view>
      <!-- 当index为0时，聊天内容模块区域 -->
      <view
          @touchstart="handleTouchStart"
          @touchmove="handleTouchMove"
          class="chatContent"
          :style="{display:functionIndex===0?'':'none'}"
          :class="{'chat-full': !showHeader,'animate__fadeIn':functionIndex===0}">
        <chatContentVue :inputBottomValue='inputBottomValue' :showHeader="showHeader"></chatContentVue>
      </view>
      <!--      &lt;!&ndash; 当index为1时，聊天功能模块 &ndash;&gt;-->
      <!--      <view class="functionContent animate__animated" -->
      <!--            :style="{display:functionIndex==1?'':'none'}"-->
      <!--            :class="{'animate__fadeIn':functionIndex==1}">-->
      <!--        <functionContentVue :showHeader="showHeader" @toggleHeader="toggleHeader"></functionContentVue>-->
      <!--      </view>-->
      <!--当index为2时，是历史记录模块-->
      <view class="chatFullScreenContral animate__animated"
            :style="{display:functionIndex===1?'':'none'}"
            :class="{'animate__fadeIn':functionIndex===2}">
        <chatHistoryVue :showHeader="showHeader" @functionChange="functionChange"
                        @toggleHeader="toggleHeader"></chatHistoryVue>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.content {
  height: 100vh;

  .chat {
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .chatFunction {
    transition: all 0.3s ease;
    transform-origin: top;
    max-height: 400rpx;
    overflow: hidden;

    &.header-hidden {
      max-height: 0;
      opacity: 0;
    }

    &.header-visible {
      opacity: 1;
    }
  }

  .chatContent {
    flex: 1;
    transition: all 0.3s ease;

    &.chat-full {
      height: calc(100vh - 120rpx) !important;
    }
  }
}

// 改为SCSS变量定义
$tn-blue-light: #e8f3ff;
$tn-blue: #5B8FF9;
$tn-white: #ffffff;
$tn-gray-8: #2C3A4B;

:root {
  --tn-blue-light: #{$tn-blue-light};
  --tn-blue: #{$tn-blue};
  --tn-white: #{$tn-white};
  --tn-gray-8: #{$tn-gray-8};
}

page {
  // height: 90vh;
}
</style>