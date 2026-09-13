<script setup>
	import {
		ref,
		watch,
		nextTick,
  onMounted,
  onBeforeUnmount
	} from 'vue';
import {onLoad} from '@dcloudio/uni-app'


	import {
		useNavSize
	} from '@/utils/nav-height.js'
	import TnInput from '@tuniao/tnui-vue3-uniapp/components/input/src/input.vue'
	import typeWriterVue from './type-writer.vue';

import aiChat from '@/utils/ai-chat.js'
import TnPopup from '@tuniao/tnui-vue3-uniapp/components/popup/src/popup.vue'
import {useUserStore} from "@/stores/user";
import {selectMemory} from '@/service/api/aiChatMessageController';
import {ossImages} from "@/utils/ossUrl";

	const {
  status
	} = useNavSize()
const userStore = useUserStore()
const avatar2 = ossImages.avatar2
// const avatar1 = ossImages.avatar
// 模态框引用
const clearModalRef = ref(null);

	//接收传来的键盘高度信息
	const props = defineProps({
		inputBottomValue: {
			type: String,
			default: ''
  },
  showHeader: {
    type: Boolean,
    default: true
		}
	})

	//用户聊天的数据
	const chatData = ref([])

	//用户的头像
const userAvatar = ref(userStore.userAvatar)
	//ai的头像
const aIAvatar = ref(avatar2)
	//用户输入框输入内容
	const userInput = ref('')
	//输入框的高度 单位rpx
	const inputHeight = ref()
	//刷新距离顶部距离px
	const marginTopPx = ref(75 + status.value)

// 是否正在等待AI回复
const isWaitingResponse = ref(false)
// AI的当前回复内容（用于流式更新）
const currentAIResponse = ref('')

// 是否是新的对话
const isNewChat = ref(true);

// 添加一个用于控制滚动到底部的变量
const scrollToBottom = ref(true);

// 快捷跳转选项
const actionSheetRef = ref(null)
const quickActions = [
  {
    text: '心情记录',
    icon: 'heart',
    value: '/pages/mood/index',
    desc: '记录每一天的心情变化'
  },
  {
    text: '心理测评',
    icon: 'form',
    value: '/pages/test/index',
    desc: '专业的心理健康评估'
  },
  {
    text: '心理课程',
    icon: 'book',
    value: '/pages/course/index',
    desc: '系统的心理健康课程'
  },
  {
    text: '咨询预约',
    icon: 'calendar',
    value: '/pages/consult/index',
    desc: '预约专业心理咨询'
  }
]

// 快捷功能弹窗
const showQuickMenu = ref(false)

// 快捷功能选项 - 改为更适合大学生的选项
const quickFeatures = [
  {
    title: '情绪日记',
    icon: 'write',
    color: '#FF7D8B',
    bgColor: 'rgba(255, 125, 139, 0.1)',
    url: '/function_pages/views/note-page'
  },
  {
    title: '压力舒缓',
    icon: 'tips',
    color: '#5677fc',
    bgColor: 'rgba(86, 119, 252, 0.1)',
    url: '/function_pages/views/music-list'
  },
  {
    title: '睡眠改善',
    icon: 'eye-hide',
    color: '#9D8DF9',
    bgColor: 'rgba(157, 141, 249, 0.1)',
    url: '/function_pages/views/sleep-aid'
  },
  {
    title: '心理分析',
    icon: 'message',
    color: '#4CAF50',
    bgColor: 'rgba(76, 175, 80, 0.1)',
    url: '/function_pages/views/analyse-index'
  },
  {
    title: '测试题库',
    icon: 'search',
    color: '#FF9800',
    bgColor: 'rgba(255, 152, 0, 0.1)',
    url: '/function_pages/views/test-list'
  },
  {
    title: '运动健康',
    icon: 'sport-run',
    color: '#2196F3',
    bgColor: 'rgba(33, 150, 243, 0.1)',
    url: '/function_pages/views/motion-index'
  }
]

// 推荐问题数据
const showSuggestions = ref(false)
const suggestionQuestions = ref([])

// 针对不同主题的推荐问题集合
const suggestionSets = {
  general: [
    '我最近感到焦虑，有什么缓解方法吗？',
    '如何改善睡眠质量？',
    '有哪些简单的减压技巧？'
  ],
  anxiety: [
    '如何区分正常焦虑和焦虑障碍？',
    '我可以做什么放松练习来缓解焦虑？',
    '焦虑发作时应该怎么办？'
  ],
  depression: [
    '如何培养积极的思维方式？',
    '我可以做什么来提升自己的心情？',
    '长期情绪低落应该怎么寻求帮助？'
  ],
  stress: [
    '如何提高学习效率减轻压力？',
    '我可以如何更好地进行时间管理？',
    '面对考试压力有什么有效的应对方法？'
  ],
  relationship: [
    '如何提高人际交往能力？',
    '面对宿舍关系紧张应该怎么办？',
    '如何处理与室友的矛盾？'
  ]
}

// 用于存储和管理memoryId
const memoryId = ref('');
// 用于记录最早的消息时间，用于加载更多
const earliestMessageTime = ref('');
// 是否还有更多历史消息
const hasMoreHistory = ref(true);
// 每次加载的消息数量
const messageLimit = 10;

// 添加新的状态变量
const showLoadMoreBtn = ref(false);

// 添加对memoryId变化的监听
watch(() => uni.getStorageSync('chatMemoryId'), (newMemoryId, oldMemoryId) => {
  if (newMemoryId && newMemoryId !== oldMemoryId) {
    // memoryId发生变化，重新加载聊天记录
    memoryId.value = newMemoryId;
    loadHistoryMessages(true);
  }
}, {immediate: false});

onMounted(() => {
  userStore.initUserInfo()

  // 获取或创建memoryId
  getOrCreateMemoryId();

  // 加载历史消息
  loadHistoryMessages(true);

  // 添加自定义事件监听
  uni.$on('chat-memory-change', handleMemoryChange);
})

onBeforeUnmount(() => {
  // 清除事件监听
  uni.$off('chat-memory-change', handleMemoryChange);
})

// 处理memoryId变化的函数
const handleMemoryChange = (newMemoryId) => {
  if (newMemoryId && newMemoryId !== memoryId.value) {
    console.log('Chat content received new memoryId:', newMemoryId);
    memoryId.value = newMemoryId;

    // 重置相关状态
    earliestMessageTime.value = '';
    hasMoreHistory.value = true;
    showLoadMoreBtn.value = false;

    // 立即加载新的聊天记录
    loadHistoryMessages(true);
  }
};

	const typeWriterChange = (typeWriterHeight) => {
		inputHeight.value = 2 * typeWriterHeight
	}

	//获取焦点
	const inputFocus = ref(false)
	//输入框默认提示
const inputPlaceholder = ref('请输入消息...')
	//是否显示打字机
	const isShowWriter = ref(true)
	//点击打字机时
	const typeWriterClick = () => {
		isShowWriter.value = false
		inputFocus.value = true
	}

// 格式化内容，处理换行和链接
const formatContent = (content) => {
  if (!content) return '';
  // 将换行符转换为<br>
  let formatted = content.replace(/\n/g, '<br>');
  // 识别并转换URL为可点击链接
  formatted = formatted.replace(
      /(https?:\/\/[^\s]+)/g,
      '<a style="color:#5677fc;text-decoration:underline;" href="$1">$1</a>'
  );
  return formatted;
}

// 选择推荐问题
const selectQuestion = (question) => {
  userInput.value = question
  sendMessage()
  // 清空推荐问题
  showSuggestions.value = false
}

// 生成推荐问题
const generateSuggestions = (aiResponse) => {
  // 根据AI回复内容选择合适的问题集
  let selectedSet = 'general'

  if (aiResponse.includes('焦虑') || aiResponse.includes('紧张')) {
    selectedSet = 'anxiety'
  } else if (aiResponse.includes('抑郁') || aiResponse.includes('情绪低落')) {
    selectedSet = 'depression'
  } else if (aiResponse.includes('压力') || aiResponse.includes('学业')) {
    selectedSet = 'stress'
  } else if (aiResponse.includes('人际') || aiResponse.includes('关系') || aiResponse.includes('朋友')) {
    selectedSet = 'relationship'
  }

  // 随机选择3个问题
  const questions = [...suggestionSets[selectedSet]]
  // 洗牌算法
  for (let i = questions.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[questions[i], questions[j]] = [questions[j], questions[i]]
  }

  // 取前3个问题
  suggestionQuestions.value = questions.slice(0, 3)
  // 显示推荐问题
  showSuggestions.value = true
}

// 修改loadHistory方法为showLoadMoreButton方法
const showLoadMoreButton = () => {
  if (hasMoreHistory.value) {
    showLoadMoreBtn.value = true;
  }
};

// 修改loadHistoryMessages方法，处理后端返回的新数据格式
const loadHistoryMessages = async (isInitial = false) => {
  if (!memoryId.value || (!isInitial && !hasMoreHistory.value)) return;

  console.log('Loading messages for memoryId:', memoryId.value);

  try {
    loadData.value = true;
    const params = {
      memoryId: memoryId.value,
      startTime: isInitial ? formatCurrentTime() :
          // 如果earliestMessageTime是数组，则需要转换为ISO字符串
          (Array.isArray(earliestMessageTime.value) ?
              `${earliestMessageTime.value[0]}-${String(earliestMessageTime.value[1]).padStart(2, '0')}-${String(earliestMessageTime.value[2]).padStart(2, '0')}T${String(earliestMessageTime.value[3]).padStart(2, '0')}:${String(earliestMessageTime.value[4]).padStart(2, '0')}:${String(earliestMessageTime.value[5]).padStart(2, '0')}` :
              earliestMessageTime.value)
    };

    console.log('Request params:', params);
    const res = await selectMemory(params);
    console.log('Response from selectMemory:', res);

    if (res && res.code === 1 && res.data && res.data.length > 0) {
      // 记录滚动位置，用于加载更多后保持位置
      const scrollView = uni.createSelectorQuery().select('.scroll-Y');
      let oldHeight = 0;

      scrollView.boundingClientRect(rect => {
        if (rect) {
          oldHeight = rect.height;
        }
      }).exec();

      // 处理返回的消息数据 - 兼容新的数据格式
      const historyMessages = res.data.map(item => ({
        id: item.id,
        // 判断消息类型：如果是USER则显示为mine，如果是AI则显示为AI
        role: item.type === 'USER' ? 'mine' : 'AI',
        content: item.content,
        // 使用createTime数组作为时间
        createdAt: item.createTime
      }));

      // 更新最早消息的时间 - 使用数组格式
      if (historyMessages.length > 0) {
        // 假设消息是按时间降序排列的，最后一条是最早的
        const lastMsg = historyMessages[historyMessages.length - 1];
        // 保存完整的时间数组
        earliestMessageTime.value = lastMsg.createdAt;

        // 判断是否还有更多历史消息
        hasMoreHistory.value = historyMessages.length >= messageLimit;
      } else {
        hasMoreHistory.value = false;
      }

      if (isInitial) {
        // 首次加载，替换当前聊天数据
        chatData.value = historyMessages;
        // 滚动到底部
        scrollToView();
      } else {
        // 加载更多，将新消息添加到顶部
        chatData.value = [...historyMessages, ...chatData.value];

        // 保持滚动位置 - 使用nextTick确保DOM更新后执行
        nextTick(() => {
          const scrollView = uni.createSelectorQuery().select('.scroll-Y');
          scrollView.boundingClientRect(rect => {
            if (rect) {
              const newHeight = rect.height;
              const scrollViewComponent = uni.createSelectorQuery().select('.scroll-Y');
              scrollViewComponent.node(node => {
                if (node && node.scrollOffset) {
                  node.scrollOffset({
                    scrollTop: newHeight - oldHeight
                  });
                }
              }).exec();
            }
          }).exec();
        });
      }
    } else if (isInitial) {
      // 如果是初次加载且没有历史消息，显示欢迎消息
      console.log('No history messages, displaying welcome message');
      chatData.value = [{
        id: Date.now(),
        role: 'AI',
        content: '你好！我是你的心理健康助手小薇。有什么我可以帮你的吗？'
      }];

      // 创建新的会话ID
      aiChat.createNewSession();
      isNewChat.value = true;
    }
  } catch (error) {
    console.error('加载历史消息失败:', error);
    uni.showToast({
      title: '加载历史消息失败',
      icon: 'none'
    });

    if (isInitial) {
      // 如果初次加载失败，显示默认欢迎消息
      chatData.value = [{
        id: Date.now(),
        role: 'AI',
        content: '你好！我是你的心理健康助手小薇。有什么我可以帮你的吗？'
      }];
    }
  } finally {
    loadData.value = false;
    console.log('历史消息加载完成');
  }
};

// 修改格式化当前时间的方法，返回ISO 8601格式的字符串
const formatCurrentTime = () => {
  const now = new Date();
  // 返回格式为 2025-04-22T12:52:48
  return now.toISOString().split('.')[0];
};

// 获取或创建memoryId
const getOrCreateMemoryId = () => {
  // 尝试从本地存储获取memoryId
  const storedMemoryId = uni.getStorageSync('chatMemoryId');
  console.log("storedMemoryId: ", storedMemoryId)

  if (storedMemoryId) {
    memoryId.value = storedMemoryId;
    return storedMemoryId;
  } else {
    // 创建新的memoryId (UUID格式)
    const newMemoryId = generateUUID();
    memoryId.value = newMemoryId;
    uni.setStorageSync('chatMemoryId', newMemoryId);
    return newMemoryId;
  }
};

// 生成UUID
const generateUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = Math.random() * 16 | 0,
        v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
};

// 修改现有的loadHistory方法，用于处理下拉加载更多
const loadHistory = () => {
  if (hasMoreHistory.value && !loadData.value) {
    loadHistoryMessages(false);
  }
};

// 修改sendMessage方法，每次发送消息后需要更新本地的最早消息时间
const sendMessage = async () => {
  // 检查登录状态

  if (!userStore.checkLogin()) {
    return;
  }

  // 检查是否有内容可发送
  if (!userInput.value || isWaitingResponse.value) return;

  try {
    // 添加用户消息到聊天记录
    const userMessage = {
      id: Date.now(),
      role: 'mine',
      content: userInput.value
    }
    chatData.value.push(userMessage)

    // 清空输入框
    const sentMessage = userInput.value
    userInput.value = ''

    // 隐藏上一轮的推荐问题
    showSuggestions.value = false

    // 显示AI正在输入状态
    isWaitingResponse.value = true

    // 创建AI响应ID（但不立即添加到聊天记录）
    const aiResponseId = Date.now() + 1
    currentAIResponse.value = ''

    // 触发滚动到底部
    scrollToView()

    // 调用AI聊天工具进行对话
    await aiChat.sendMessage(
        sentMessage,
        chatData.value,
        // 流式更新回调
        (partialResponse) => {
          currentAIResponse.value = partialResponse
          // 检查是否已添加AI响应，如果没有则添加
          const aiResponseIndex = chatData.value.findIndex(item => item.id === aiResponseId)
          if (aiResponseIndex === -1 && partialResponse) {
            // 只有在有实际内容时才添加AI响应
            chatData.value.push({
              id: aiResponseId,
              role: 'AI',
              content: partialResponse
            })
          } else if (aiResponseIndex !== -1) {
            // 更新现有的AI响应
            chatData.value[aiResponseIndex].content = partialResponse
          }
          // 每次更新内容时滚动到底部
          scrollToView()
        },
        // 完成回调
        (finalResponse) => {
          isWaitingResponse.value = false;
          isNewChat.value = false;

          // 如果是新对话，更新memoryId
          if (isNewChat.value) {
            const newMemoryId = generateUUID();
            memoryId.value = newMemoryId;
            uni.setStorageSync('chatMemoryId', newMemoryId);
          }

          scrollToView();
          generateSuggestions(finalResponse);
        },
        // 错误回调
        (errorMsg) => {
          isWaitingResponse.value = false;
          let showMsg = '未登录或网络错误';

          if (errorMsg?.code === 401) {
            showMsg = '登录已过期，请重新登录';
          } else if (errorMsg?.code === 500) {
            showMsg = '服务异常，请稍后再试';
          }

          uni.showToast({
            title: showMsg,
            icon: 'none',
            duration: 2000
          });

          // 更新错误消息
          const aiResponseIndex = chatData.value.findIndex(item => item.id === aiResponseId)
          if (aiResponseIndex !== -1) {
            chatData.value[aiResponseIndex].content = '抱歉，我遇到了一点问题，请稍后再试。'
          }
        },
        isNewChat.value
    )
  } catch (error) {
    isWaitingResponse.value = false;
    console.error('发送消息失败:', error);
    uni.showToast({
      title: '网络请求失败，请检查连接'+error,
      icon: 'none',
      duration: 2000
    });
  }
}

	//输入框失去焦点事件
	const inputblur = () => {
		isShowWriter.value = true
		inputFocus.value = false
	}

	const inputFs = () => {
		inputFocus.value = true
	}

	//判断是否开启加载
	const loadData = ref(false)
	//防止重复加载变量
	const isLoading = ref(false)

	//请求数据
	const requestData = () => {
		//只有当没有加载时才可以触发加载数据
		if (!isLoading.value) {
			isLoading.value = true
			return new Promise((resolve) => {
				setTimeout(() => {
					isLoading.value = false
        resolve([]); // 返回空数组，不再使用模拟数据
      }, 2000);
			});
		} else {
			return
		}
	}

	//加载新数据
	const loadDataBefore = async () => {
		try {
			loadData.value = true
			const beforeData = await requestData()
    if (beforeData && beforeData.length > 0) {
			chatData.value.push(...beforeData.reverse())
    }
			loadData.value = false
		} catch (error) {
			console.log(error)
    loadData.value = false
		}
	}

	//初始设置为0表示直接到聊天底部
	const scrollTop = ref(0)
	let oldHeight = 0; // 加载之前的高度

// 显示清除对话确认模态框
const showClearModal = () => {
  clearModalRef.value?.showModal({
    title: '确认清除',
    showCancel: true,
    confirm: clearChat
  })
}

// 清空对话历史并重置memoryId
const clearChat = () => {
  chatData.value = [{
    id: Date.now(),
    role: 'AI',
    content: '你好！我是你的心理健康助手小薇。有什么我可以帮你的吗？'
  }];

  // 创建新的会话ID
  const newMemoryId = generateUUID();
  memoryId.value = newMemoryId;
  uni.setStorageSync('chatMemoryId', newMemoryId);

  // 创建新的会话ID
  aiChat.createNewSession();
  isNewChat.value = true;

  // 重置最早消息时间
  earliestMessageTime.value = '';
  hasMoreHistory.value = true;

  // 重置加载更多按钮状态
  showLoadMoreBtn.value = false;

  uni.showToast({
    title: '对话已清除',
    icon: 'success'
  });
}

// 添加滚动到底部的方法
const scrollToView = () => {
  // 等待DOM更新后滚动
  nextTick(() => {
    // 设置scroll-view滚动到底部
    scrollTop.value = 9999999;
  });
}

// 打开快捷操作菜单
const openQuickActions = () => {
  actionSheetRef.value?.show({
    title: '快捷功能',
    actions: quickActions,
    cancelText: '取消',
    safeAreaInsetBottom: true,
    round: true
  })
}

// 处理选项点击
const handleActionClick = (item) => {
  uni.navigateTo({
    url: item.value,
    fail: () => {
      // 如果页面不存在，显示提示
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      })
    }
  })
}

// 打开快捷功能
const openQuickFeatures = () => {
  showQuickMenu.value = true
}

// 选择功能
const selectFeature = (feature) => {
  uni.navigateTo({
    url: feature.url,
    fail: () => {
      uni.showToast({
        title: '功能即将上线',
        icon: 'none'
      })
    }
  })
  showQuickMenu.value = false
	}
</script>

<template>
  <view class="content" :class="{'content-full': !showHeader}"
        :style="{ height: showHeader ? 'calc(100vh - 480rpx)' : 'calc(100vh - 120rpx)' }">
    <!-- 下拉提示器 -->
    <view class="pull-indicator" v-show="!showHeader">
      <view class="indicator-arrow">
        <tn-icon name="arrowdown" size="36rpx" color="#ffffff"></tn-icon>
      </view>
      <text>下拉显示助手信息</text>
    </view>

    <!-- 清除对话按钮 -->
    <view class="clear-chat" @click="showClearModal">
      <tn-icon name="refresh" color="#5677fc" size="36rpx"></tn-icon>
    </view>

    <!-- 聊天的数据 - 移除transform样式 -->
    <scroll-view
        class="scroll-Y"
        scroll-y
        :scroll-top="scrollTop"
        @scrolltoupper="showLoadMoreButton"
    >
      <!-- 加载更多按钮 - 只在到达顶部且有更多历史记录时显示 -->
      <view class="load-more-btn" v-if="showLoadMoreBtn && hasMoreHistory.value">
        <button class="btn-content" @click="loadHistoryMessages(false)" :disabled="loadData">
          <view class="loading-spinner" v-if="loadData"></view>
          <text>{{ loadData ? '加载中...' : '加载更多历史消息' }}</text>
        </button>
      </view>

      <!-- 没有更多消息提示 -->
      <view class="no-more-messages" v-if="showLoadMoreBtn && !hasMoreHistory.value">
        <text>没有更多历史消息了</text>
      </view>

			<view class="tn-pt-sm">
				<view v-for="(item,index) in chatData" :key="index" class="chatPart">
					<!-- AI聊天的对话 -->
          <view v-if="item.role === 'AI'" class="aiStyle chatStyle">
						<view class="avatar">
              <tn-lazy-load :src="aIAvatar"></tn-lazy-load>
						</view>
						<view class="message tn-ml-sm">
              <rich-text :nodes="formatContent(item.content)"></rich-text>
              <!-- 推荐问题区域 - 当这是最后一条AI消息且不在等待状态时显示 -->
              <view v-if="showSuggestions && index === chatData.length - 1 && !isWaitingResponse"
                    class="suggestion-container">
                <text class="suggestion-title">你可能想问：</text>
                <view class="suggestion-buttons">
                  <view
                      v-for="(question, qIndex) in suggestionQuestions"
                      :key="qIndex"
                      class="suggestion-btn"
                      @click="selectQuestion(question)"
                  >
                    <text>{{ question }}</text>
                  </view>
                </view>
              </view>
						</view>
					</view>
					<!-- 用户聊天的对话 -->
          <view v-if="item.role === 'mine'" class="mineStyle chatStyle">
						<view class="message tn-mr-sm">
							{{ item.content }}
						</view>
						<view class="avatar">
              <tn-lazy-load :src='userAvatar'></tn-lazy-load>
						</view>
					</view>
				</view>
			</view>

      <!--			<view class="tn-flex-center-center" :style="{display:loadData?'':'none'}">-->
      <!--				<load-dot-vue></load-dot-vue>-->
      <!--			</view>-->

      <!-- 底部空白区域，确保有足够的滚动空间 -->
      <view style="height: 20rpx;"></view>

      <!-- AI正在思考中提示 - 移至正确位置，作为浮动提示 -->
      <view class="ai-typing-float" v-if="isWaitingResponse">
        <view class="typing-content">
          <view class="typing-dots">
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
          <text class="typing-text">AI正在思考中...</text>
        </view>
      </view>
		</scroll-view>

		<!-- 键盘弹起占位 -->
		<view :style="{height: inputBottomValue}"></view>

		<!-- 聊天的输入框 -->
		<view class="userInput animate__animated animate__zoomIn"
			:style="{ 'bottom': inputBottomValue ? inputBottomValue : 'calc(200rpx + constant(safe-area-inset-bottom))' }">
			<!-- 打字机效果 -->
			<view class="typeWriter" @click="typeWriterClick" :style="{display:isShowWriter?'':'none'}">
				<typeWriterVue></typeWriterVue>
			</view>
      <view class="inputWrapper" :style="{'display':isShowWriter?'none':''}">
        <view class="input">
				<TnInput v-model="userInput" :placeholder='inputPlaceholder' :border='false' type='textarea'
                   custom-class='TnInputClass' :focus='inputFocus' @focus='inputFs' @blur='inputblur'
                   @confirm="sendMessage">
				</TnInput>
			</view>
        <view class="sendBtn" @click="sendMessage"
              :class="{'sendBtn--disabled': !userInput|| isWaitingResponse}">
          <tn-icon name="send" color="#ffffff" size="40rpx"></tn-icon>
		</view>
      </view>
    </view>

    <!-- 确认清除对话的模态框 -->
    <tn-modal ref="clearModalRef">
      <view class="modal-content">
        <view class="modal-title">确认清除对话</view>
        <view class="modal-message">清除后当前对话内容将无法恢复，是否继续？</view>
      </view>
    </tn-modal>

    <!-- 快捷操作悬浮按钮 - 改为微光效果 -->
    <view class="mind-helper-fab" @click="openQuickFeatures">
      <view class="fab-ripple"></view>
      <view class="fab-ripple delay-1"></view>
      <view class="fab-ripple delay-2"></view>
      <view class="fab-inner">
        <view class="fab-icon">
          <tn-icon name="my-reduce" size="44rpx" color="#ffffff"></tn-icon>
        </view>
        <text class="fab-hint">心灵助手</text>
      </view>
    </view>

    <!-- 快捷功能弹窗 -->
    <TnPopup v-model="showQuickMenu" bg-color="#ffffff" radius="24" :mask-close-able="true">
      <view class="quick-features">
        <view class="feature-header">
          <text class="feature-title">心灵成长助手</text>
          <text class="feature-subtitle">为你的心理健康提供多方位支持</text>
        </view>

        <view class="feature-grid">
          <view
              v-for="(item, index) in quickFeatures"
              :key="index"
              class="feature-item"
              @click="selectFeature(item)"
          >
            <view class="feature-icon" :style="{ backgroundColor: item.bgColor }">
              <tn-icon :name="item.icon" size="44rpx" :color="item.color"></tn-icon>
            </view>
            <text class="feature-name">{{ item.title }}</text>
          </view>
        </view>
      </view>
    </TnPopup>
	</view>
</template>

<style lang="scss" scoped>
// 修正：添加缺失的SCSS变量
$tn-blue-light: #e8f3ff;
$tn-blue: #5B8FF9;
$tn-blue-dark: #3A6CD6; // 新增深蓝色变量
$ai-bg-color: #ffffff; // AI消息背景色
$user-bg-color: #4369F2; // 用户消息背景色
$text-color: #2C3A4B; // 主要文字颜色
$primary-color: #5B8FF9; // 主色调

	.content {
		padding: 0 20rpx 0 20rpx;
		box-sizing: border-box;
		width: 100%;
  position: relative;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(40rpx);
  border: 1px solid rgba(255, 255, 255, 0.2);
		border-radius: 20rpx;

  .clear-chat {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    width: 70rpx;
    height: 70rpx;
    border-radius: 50%;
    background-color: rgba(255, 255, 255, 0.9);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10;
    box-shadow: 0 4rpx 16rpx rgba(91, 143, 249, 0.1);
    transition: all 0.3s;

    &:active {
      transform: scale(0.9) rotate(180deg);
    }
  }

		.scroll-Y {
    height: calc(100% - 120rpx);
    padding: 30rpx 0;

			.chatPart {
      margin: 20rpx 0;

				.chatStyle {
					display: flex;
					align-content: start;

					.avatar {
          width: 88rpx;
          height: 88rpx;
          border: 2rpx solid rgba(255, 255, 255, 0.8);
          box-shadow: 0 4rpx 16rpx rgba(91, 143, 249, 0.1);
						border-radius: 50%;

						image {
							width: 70rpx;
							height: 70rpx;
            border-radius: 50%;
						}
					}
				}

				.mineStyle {
					justify-content: flex-end;

        .message {
          background: linear-gradient(
                  135deg,
                  #7B68EE 0%, /* 适合心理健康的柔和紫色 */
                  #9370DB 100% /* 稍浅的紫色 */
          ) !important;
          color: #ffffff;
          border-bottom-right-radius: 8rpx;
          box-shadow: 0 4rpx 12rpx rgba(123, 104, 238, 0.2);
        }
      }

      .aiStyle {
				.message {
          background: #ffffff !important;
          border: 1px solid #e8f3ff !important;
          color: #2C3A4B;
          border-top-left-radius: 8rpx;

          &::before {
            width: 100px;
            content: 'AI助手';
            position: absolute;
            top: -36rpx;
            left: 0;
            font-size: 20rpx;
            color: #7D95B6;
            padding: 4rpx 12rpx;
            background: rgba(246, 249, 254, 0.9);
            border-radius: 8rpx;
          }
        }
      }

      .message {
        padding: 24rpx;
        border-radius: 24rpx;
					margin-bottom: 10px;
        max-width: 70%;
					word-wrap: break-word;
        line-height: 1.6;
        position: relative;
        font-size: 28rpx;
        background: rgba(255, 255, 255, 0.9) !important;
        box-shadow: 0 4rpx 16rpx rgba(91, 143, 249, 0.08);
      }
    }

    .ai-typing-float {
      position: fixed;
      bottom: 160rpx;
      left: 30rpx;
      z-index: 999;
      max-width: 60%;

      .typing-content {
        display: flex;
        align-items: center;
        background: rgba(255, 255, 255, 0.95);
        padding: 12rpx 24rpx;
        border-radius: 100rpx;
        box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
        border: 1px solid rgba(91, 143, 249, 0.15);
        animation: pulse 2s infinite ease-in-out;

        .typing-dots {
          display: flex;
          align-items: center;

          .dot {
            width: 12rpx;
            height: 12rpx;
            border-radius: 50%;
            background: #7B68EE;
            margin: 0 4rpx;
            animation: bounce 1.4s infinite ease-in-out both;

            &:nth-child(1) {
              animation-delay: -0.32s;
            }

            &:nth-child(2) {
              animation-delay: -0.16s;
            }
          }
        }

        .typing-text {
          margin-left: 12rpx;
          font-size: 26rpx;
          color: #7B68EE;
          font-weight: 500;
        }
      }
    }
  }

		.userInput {
			position: absolute;
			display: flex;
			justify-content: center;
			align-items: center;
			box-sizing: border-box;
			width: 96%;
			padding: 20rpx;
    bottom: 20rpx;
    left: 2%;
    background: var(--tn-white) !important;
    border: 1px solid #e8f3ff !important;
    border-radius: 40rpx 8rpx 8rpx 40rpx !important;
    box-shadow: 0 -8rpx 24rpx rgba(91, 143, 249, 0.1);

    // 新增输入框装饰线
    &::after {
      content: '';
      position: absolute;
      right: -8rpx;
      top: 50%;
      transform: translateY(-50%);
      width: 4rpx;
      height: 80%;
      background: linear-gradient(
              to bottom,
              #e8f3ff 0%,
              #5B8FF9 50%,
              #e8f3ff 100%
      );
      border-radius: 4rpx;
      box-shadow: 2rpx 0 8rpx rgba(91, 143, 249, 0.2);
    }

			.typeWriter {
				z-index: 999;
				box-sizing: border-box;
				width: 88%;
      background: rgba(246, 249, 254, 0.9);
      border-radius: 40rpx;
      padding: 20rpx;
			}

    .inputWrapper {
      width: 96%;
      display: flex;
      align-items: center;

			.input {
        flex: 1;
        margin-right: 20rpx;
        background-color: rgba(255, 255, 255, 0.9) !important;
        border-radius: 40rpx;
        padding: 8rpx;
        box-shadow: 0 2rpx 12rpx rgba(91, 143, 249, 0.1);

				.TnInputClass {
          min-height: 80rpx;
          padding: 0 20rpx;
        }
      }

      .sendBtn {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        background: linear-gradient(
                135deg,
                #e8f3ff 0%,
                mix(white, #e8f3ff, 20%) 100%
        ) !important;
        box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s;

        &:active {
          background: linear-gradient(
                  135deg,
                  darken(#e8f3ff, 5%) 0%,
                  #e8f3ff 100%
          ) !important;
        }

        &--disabled {
          background-color: #b8c2e3;
          opacity: 0.8;
        }
      }
    }
  }
}

.modal-content {
  padding: 30rpx;

  .modal-title {
    font-size: 32rpx;
    font-weight: bold;
    margin-bottom: 20rpx;
  }

  .modal-message {
    font-size: 28rpx;
    color: #666;
    line-height: 1.5;
  }
}

@keyframes breath {
  0% {
    opacity: 0.9;
    transform: scale(0.98);
  }
  50% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0.9;
    transform: scale(0.98);
  }
}

.animate__zoomIn {
  animation: breath 3s ease-in-out infinite;
}

.aiStyle .message {
  border-left: 4rpx solid #5B8FF9;
}

.mineStyle .message {
  border-right: 4rpx solid darken(#4369F2, 10%);
}

.pull-indicator {
  position: absolute;
  top: 10rpx;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  background: rgba(0, 0, 0, 0.2);
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

.content {
  transition: all 0.3s ease;

  &.content-full {
    height: calc(100vh - 120rpx) !important;
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-6rpx);
  }
}

// 心灵助手悬浮按钮
.mind-helper-fab {
  position: fixed;
  right: 40rpx;
  bottom: 240rpx;
  z-index: 99;

  .fab-inner {
    width: 110rpx;
    height: 110rpx;
    background: linear-gradient(135deg, #8364e8 0%, #5677fc 100%);
    border-radius: 50%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    box-shadow: 0 6rpx 20rpx rgba(131, 100, 232, 0.3);
    position: relative;
    z-index: 2;
    transition: all 0.3s;

    .fab-icon {
      margin-bottom: 6rpx;
    }

    .fab-hint {
      font-size: 20rpx;
      color: #ffffff;
      font-weight: 500;
    }

    &:active {
      transform: scale(0.92);
    }
  }

  // 波纹效果
  .fab-ripple {
    position: absolute;
    top: 5rpx;
    left: 5rpx;
    right: 5rpx;
    bottom: 5rpx;
    border-radius: 50%;
    border: 2rpx solid rgba(131, 100, 232, 0.4);
    animation: ripple 3s infinite ease-out;

    &.delay-1 {
      animation-delay: 1s;
    }

    &.delay-2 {
      animation-delay: 2s;
    }
  }
}

// 快捷功能弹窗样式
.quick-features {
  width: 600rpx;
  padding: 40rpx 30rpx;

  .feature-header {
    text-align: center;
    margin-bottom: 40rpx;

    .feature-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 10rpx;
    }

    .feature-subtitle {
      font-size: 26rpx;
      color: #8D93A1;
    }
  }

  .feature-grid {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;

    .feature-item {
      width: 32%;
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 30rpx;
      transition: transform 0.2s;

      &:active {
        transform: scale(0.9);
      }

      .feature-icon {
        width: 100rpx;
        height: 100rpx;
        border-radius: 24rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 12rpx;
      }

      .feature-name {
        font-size: 24rpx;
        color: #333;
        font-weight: 500;
      }
    }
  }
}

@keyframes ripple {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(1.8);
    opacity: 0;
  }
}

// 添加推荐问题样式
.suggestion-container {
  margin-top: 24rpx;

  .suggestion-title {
    display: block;
    font-size: 22rpx;
    color: #8D93A1;
    margin-bottom: 12rpx;
  }

  .suggestion-buttons {
    display: flex;
    flex-direction: column;
    gap: 12rpx;

    .suggestion-btn {
      background: rgba(246, 249, 254, 0.7);
      border: 1px solid rgba(86, 119, 252, 0.2);
      border-radius: 12rpx;
      padding: 12rpx 16rpx;
      transition: all 0.2s;

      text {
        font-size: 24rpx;
        color: #5677fc;
        line-height: 1.5;
      }

      &:active {
        transform: scale(0.98);
        background: rgba(86, 119, 252, 0.1);
      }
    }
  }
}

// 移除旧的AI输入提示样式
.scroll-Y .ai-typing-indicator {
  display: none;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

// 优化用户消息样式
.mineStyle {
  justify-content: flex-end;

  .message {
    background: linear-gradient(
            135deg,
            #7B68EE 0%,
            #9370DB 100%
    ) !important;
    color: #ffffff;
    border-bottom-right-radius: 8rpx;
    box-shadow: 0 4rpx 12rpx rgba(123, 104, 238, 0.2);
  }
}

/* 加载更多样式 */
.loading-more {
  padding: 20rpx;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;

  .loading-spinner {
    width: 30rpx;
    height: 30rpx;
    border: 4rpx solid rgba(86, 119, 252, 0.2);
    border-top: 4rpx solid #5677fc;
    border-radius: 50%;
    margin-right: 10rpx;
    animation: spin 1s linear infinite;
  }

  text {
    font-size: 24rpx;
    color: #999;
  }
}

.no-more-messages {
  text-align: center;
  padding: 20rpx;

  text {
    font-size: 24rpx;
    color: #999;
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 加载更多按钮样式 */
.load-more-btn {
  padding: 20rpx;
  text-align: center;

  .btn-content {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
    color: #5677fc;
    font-size: 26rpx;
    padding: 16rpx 30rpx;
    border-radius: 100rpx;
    border: 1px solid rgba(86, 119, 252, 0.3);
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
    transition: all 0.2s;

    &:active {
      transform: scale(0.95);
      background-color: #e8f0fc;
    }

    &[disabled] {
      opacity: 0.7;
      color: #999;
    }

    .loading-spinner {
      width: 24rpx;
      height: 24rpx;
      border: 3rpx solid rgba(86, 119, 252, 0.2);
      border-top: 3rpx solid #5677fc;
      border-radius: 50%;
      margin-right: 12rpx;
      animation: spin 1s linear infinite;
    }
  }
	}
</style>



