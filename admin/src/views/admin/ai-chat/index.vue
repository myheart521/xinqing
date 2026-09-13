<template>
  <div class="app-container kimi-style">
    <!-- 左侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>智能体</h2>
      </div>

      <!-- 左侧菜单列表 -->
      <ul class="nav-list">
        <!-- 新建会话按钮 -->
        <li @click="createNewChat" class="nav-item">
          <div class="sidebar-icon-wrapper">
            <svg class="icon" viewBox="0 0 1024 1024" fill="currentColor">
              <path
                  d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm192 472c0 4.4-3.6 8-8 8H544v152c0 4.4-3.6 8-8 8h-48c-4.4 0-8-3.6-8-8V544H328c-4.4 0-8-3.6-8-8v-48c0-4.4 3.6-8 8-8h152V328c0-4.4 3.6-8 8-8h48c4.4 0 8 3.6 8 8v152h152c4.4 0 8 3.6 8 8v48z"/>
            </svg>
            <span>新建会话</span>
          </div>
        </li>

        <!-- 历史会话 -->
        <sidebar-history
            :history-chats="historyChats"
            :display-chats="displayChats"
            :show-all-history="showAllHistory"
            @load-history="loadHistoryChat"
            @toggle-history-view="toggleHistoryView"
        />
      </ul>
    </aside>

    <!-- 聊天主区域 -->
    <div class="chat-container">
      <!-- 头部标题区 - 固定在顶部 -->
      <div class="chat-header-fixed">
        <h2>智能体</h2>
        <!-- 当前模型指示器 -->
        <div class="current-model-badge">
          <div class="model-icon">
            <svg v-if="mode === 'webSearch'" viewBox="0 0 1024 1024" fill="currentColor">
              <path
                  d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"/>
              <path
                  d="M686.7 638.6L544.1 535.5V288c0-4.4-3.6-8-8-8H488c-4.4 0-8 3.6-8 8v275.4c0 2.6 1.2 5 3.3 6.5l165.4 120.6c3.6 2.6 8.6 1.8 11.2-1.7l28.6-39c2.6-3.7 1.8-8.7-1.8-11.2z"/>
            </svg>
            <svg v-else-if="mode === 'adminAgent'" viewBox="0 0 1024 1024" fill="currentColor">
              <path
                  d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"/>
              <path
                  d="M464 688a48 48 0 1 0 96 0 48 48 0 1 0-96 0zm24-112h48c4.4 0 8-3.6 8-8V296c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8z"/>
            </svg>
            <svg v-else viewBox="0 0 1024 1024" fill="currentColor">
              <path
                  d="M832 64H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V96c0-17.7-14.3-32-32-32zm-600 72h560v208H232V136zm560 480H232V408h560v208zm0 272H232V680h560v208z"/>
            </svg>
          </div>
          <span>当前模型：{{
          mode === 'webSearch' ? '联网搜索' :
            mode === 'adminAgent' ? '管理端智能体' : 'MCP智能体'
          }}</span>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-box" ref="chatBox" @scroll="handleScroll" :class="{'no-input': !showChatInput}">
        <!-- 加载更多提示 -->
        <div v-if="isLoadingMore" class="loading-more">
          <span>加载更多消息...</span>
        </div>

        <!-- 模型切换提示 -->
        <div v-if="showModelSwitchNotice" class="model-switch-notice">
          <div class="notice-icon">
            <svg viewBox="0 0 1024 1024" fill="currentColor">
              <path
                  d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"/>
              <path
                  d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372zm176 376c0 4.4-3.6 8-8 8H544v152c0 4.4-3.6 8-8 8h-48c-4.4 0-8-3.6-8-8V524H328c-4.4 0-8-3.6-8-8v-48c0-4.4 3.6-8 8-8h152V308c0-4.4 3.6-8 8-8h48c4.4 0 8 3.6 8 8v152h152c4.4 0 8 3.6 8 8v48z"/>
            </svg>
          </div>
          <div class="notice-content">
            <div class="notice-title">已切换到{{
              mode === 'webSearch' ? '联网搜索' :
              mode === 'adminAgent' ? '管理端智能体' : 'MCP智能体'
              }}模式
            </div>
            <div class="notice-desc">开始新的对话以使用新模型</div>
          </div>
        </div>

        <!-- 聊天消息列表 -->
        <div class="messages-container">
          <chat-message
              :messages="messages"
              :user-avatar="userAvatar"
              :ai-avatar="aiAvatar"
          />
          <!-- 底部填充区，确保消息滚动到底部时有空间 -->
          <div class="messages-bottom-space"></div>
        </div>
        
        <!-- 滚动到底部按钮 -->
        <div v-if="showScrollToBottom" class="scroll-to-bottom" @click="scrollToBottom">
          <svg viewBox="0 0 1024 1024" fill="currentColor">
            <path
                d="M840.4 300H183.6c-19.7 0-30.7 20.8-18.5 35l328.4 380.8c9.4 10.9 27.5 10.9 37 0L858.9 335c12.2-14.2 1.2-35-18.5-35z"/>
          </svg>
        </div>
      </div>

      <!-- 输入区域 -->
      <chat-input
          :show-input="showChatInput"
          :mode="mode"
          :is-chat-active="isChatActive"
          @send-message="handleSendMessage"
          @mode-change="onModeChange"
          @create-new-chat="createNewChat"
      />

    </div>
  </div>
</template>

<script>
import {ref, onMounted, nextTick, computed, onUnmounted, watch, reactive} from 'vue';
import {ElMessage} from 'element-plus';
import {
  fetchAllChatSessions,
  fetchChatMessages,
  loadMoreMessages, sendAdminAIAgentMessage, sendAdminMCPAIAgentMessage, sendAIChatMessage
} from '@/api/ai/chat.js';

// 导入组件
import ChatMessage from './components/ChatMessage.vue';
import ChatInput from './components/ChatInput.vue';
import SidebarHistory from './components/SidebarHistory.vue';

// 导入工具函数
import {sendChatMessage, detectUrl} from '@/utils/chatUtils.js';
import {formatTime, getChinaTime, generateMemoryId} from '@/utils/timeUtils.js';
import {renderMarkdown} from '@/utils/markdownUtils.js';
import {useUserStore} from "@/stores/user.js";
import SseFileDownloaderClass, {
  formatFileSize,
  getFullUrl,
} from '@/utils/sse-file-downloader.js';


export default {
  name: 'AIChatManagement',
  components: {
    ChatMessage,
    ChatInput,
    SidebarHistory
  },
  setup() {
    const chatBox = ref(null);
    const messages = ref([]);
    const historyChats = ref([]);
    const memoryId = ref(null);
    const startTime = ref(null);
    const oldestMessageTime = ref(null);
    const isFirstMessage = ref(true);
    const mode = ref('webSearch');
    const currentReader = ref(null);
    const isChatActive = ref(false);
    const isLoadingMore = ref(false);
    const canLoadMore = ref(true);
    const refreshInterval = ref(null);
    const showModelSwitchNotice = ref(false);
    const previousMode = ref('webSearch');
    const userAvatar = ref('');
    const aiAvatar = ref('https://api.dicebear.com/7.x/bottts/svg?seed=Kimi');
    const showScrollToBottom = ref(false);
    const showAllHistory = ref(false);
    const showChatInput = ref(false);
    let downloader = null;
    //----------------------------文件传输SSE相关----开始----------------------------
    const progressPercent = ref(0)
    const progressMessage = ref('未连接')
    const statusRef = ref(null);
    const logs = ref([]);
    const connected = ref(false);
    const fileData = ref(null);
    const userId = ref(useUserStore().userId);


    // 添加日志
    const addLog = (message, type = 'info') => {
      const now = new Date();
      const timeStr = now.toLocaleTimeString();
      logs.value.push({
        time: timeStr,
        message,
        type
      });

      // 下一个tick时滚动到底部
      nextTick(() => {
        if (statusRef.value) {
          statusRef.value.scrollTop = statusRef.value.scrollHeight;
        }
      });
    };

    // 更新进度条
    const updateProgress = (percent, message) => {
      progressPercent.value = percent;
      progressMessage.value = message || `${percent}%`;
    };
    // 事件处理函数
    const handleConnected = (data) => {
      addLog(`连接已建立: 用户ID ${data.userId}`, 'success');
    };

    const handleDisconnected = (data) => {
      addLog(`连接已断开: 用户ID ${data.userId}`, 'info');
      connected.value = false;
    };

    const handleFileInfo = (data) => {
      addLog(`接收到文件信息: ${data.fileName}, 大小: ${formatFileSize(data.fileSize)}`, 'info');
      updateProgress(25, '接收文件信息');
    };

    const handleFileData = (data) => {
      addLog(`接收到文件数据: ${data.fileName}, 数据大小: ${formatFileSize(data.data.length)}`, 'info');
      updateProgress(75, '接收文件数据');
      fileData.value = data;
    };

    const handleDownloadReady = (data) => {
      addLog(`文件下载就绪: ${data.fileName}, 类型: ${data.fileType}`, 'success');
      updateProgress(100, '下载就绪');
    };

    const handleError = (data) => {
      addLog(`发生错误: ${data.message}`, 'error');
    };

    const handleProgress = (data) => {
      if (data.progress !== undefined) {
        updateProgress(data.progress, data.message);
      }
      addLog(`进度更新: ${data.message} (${data.progress || 0}%)`, 'info');
    };


    // 连接到服务器
    const connect = async () => {
      if (!userId.value || userId.value < 1) {
        addLog('请输入有效的用户ID', 'error');
        return;
      }

      const fullUrl = getFullUrl();
      addLog(`正在建立与用户ID ${userId.value} 的SSE连接，URL: ${fullUrl}...`, 'info');

      try {
        // 创建下载器实例
        downloader = new SseFileDownloaderClass({
          baseUrl: fullUrl,
          debug: true,
          autoDownload: true,
          onConnected: handleConnected,
          onDisconnected: handleDisconnected,
          onFileInfo: handleFileInfo,
          onFileData: handleFileData,
          onDownloadReady: handleDownloadReady,
          onError: handleError,
          onProgress: handleProgress
        });

        // 连接到服务器
        const result = await downloader.connect(userId.value);
        addLog(`连接成功: 用户ID ${result.userId}`, 'success');
        connected.value = true;
      } catch (error) {
        addLog(`连接失败: ${error.message}`, 'error');
      }
    };

    // 断开连接
    const disconnect = () => {
      if (!downloader) {
        addLog('没有活跃的连接', 'warning');
        return;
      }

      addLog('正在断开SSE连接...', 'info');

      const result = downloader.disconnect();
      if (result) {
        addLog('已断开连接', 'success');
        connected.value = false;
        updateProgress(0, '未连接');
      } else {
        addLog('断开连接失败', 'error');
      }
    };

    //----------------------------文件传输SSE相关结束----------------------------
    // 在组件销毁时关闭流式读取和清除定时器
    onUnmounted(() => {
      window.removeEventListener('popstate', handleRouteChange);

      if (currentReader.value) {
        currentReader.value.cancel('Component unmounted');
      }

      if (refreshInterval.value) {
        clearInterval(refreshInterval.value);
      }

      disconnect()
    });

    // 计算属性：排序后的历史会话
    const sortedHistoryChats = computed(() => {
      if (!historyChats.value || !Array.isArray(historyChats.value)) {
        return [];
      }

      if (historyChats.value.length === 0) {
        return [];
      }

      // 过滤掉无效的聊天记录
      const validChats = historyChats.value.filter(chat => chat && (chat.startTime || chat.timestamp));

      // 排序处理 - 优先使用timestamp字段排序
      const sortedChats = [...validChats].sort((a, b) => {
        try {
          if (a.timestamp && b.timestamp) {
            return b.timestamp - a.timestamp;
          }

          const dateA = new Date(a.startTime || 0);
          const dateB = new Date(b.startTime || 0);
          return dateB.getTime() - dateA.getTime();
        } catch (e) {
          console.error('排序时发生错误:', e);
          return 0;
        }
      });

      return sortedChats.slice(0, 10);
    });

    // 计算属性：最近的6条历史会话
    const recentChats = computed(() => {
      return sortedHistoryChats.value.slice(0, 6);
    });

    // 用于显示的聊天会话
    const displayChats = computed(() => {
      return showAllHistory.value ? sortedHistoryChats.value : recentChats.value;
    });

    // 获取历史会话
    const fetchHistory = async (forceRefresh = false) => {
      if (historyChats.value && historyChats.value.length > 0 && !forceRefresh) {
        return;
      }

      try {
        const res = await fetchAllChatSessions();
        historyChats.value = [];

        if (res && res.data) {
          let chatList = [];

        if (Array.isArray(res.data)) {
            chatList = res.data;
          } else if (res.data.data && Array.isArray(res.data.data)) {
            chatList = res.data.data;
          } else if (res.code === 1 && Array.isArray(res.data)) {
            chatList = res.data;
          }

          if (chatList && chatList.length > 0) {
            historyChats.value = chatList.map(item => {
              const id = item.id || item.memoryId || '';
              let formattedTime = '';
              let timestamp = 0;

              if (Array.isArray(item.createTime)) {
                try {
                  const currentYear = new Date().getFullYear();
                  const year = parseInt(item.createTime[0]);
                  const normalizedYear = year > 2000 ? Math.min(year, currentYear) : 2000 + (year % 100);
                  const month = parseInt(item.createTime[1]) - 1;
                  const day = parseInt(item.createTime[2]);
                  const hour = parseInt(item.createTime[3]);
                  const minute = parseInt(item.createTime[4]);
                  const second = parseInt(item.createTime[5]);

                  const date = new Date(normalizedYear, month, day, hour, minute, second);
                  const now = new Date();
                  if (date.getTime() > now.getTime()) {
                    timestamp = now.getTime();
                    formattedTime = now.toISOString();
        } else {
                    timestamp = date.getTime();
                    formattedTime = date.toISOString();
                  }
                } catch (e) {
                  formattedTime = new Date().toISOString();
                  timestamp = Date.now();
                }
              } else {
                try {
                  formattedTime = item.createTime || item.startTime || new Date().toISOString();
                  timestamp = new Date(formattedTime).getTime();
                } catch (e) {
                  formattedTime = new Date().toISOString();
                  timestamp = Date.now();
                }
              }

              return {
                id: id,
                memoryId: id,
                startTime: formattedTime,
                updateTime: formattedTime,
                timestamp: timestamp,
                title: item.name || item.title || '未命名会话',
                userId: item.userId || ''
              };
            });
          }
        }
      } catch (error) {
        console.error('获取历史记录失败:', error);
        ElMessage.error('无法加载历史会话，请稍后再试');
        historyChats.value = [];
      }
    };

    // 加载历史消息
    const loadHistoryChat = async (chat) => {
      try {
        let memoryIdValue = '';

        if (typeof chat === 'string') {
          memoryIdValue = String(chat);
        } else if (chat && typeof chat === 'object') {
          memoryIdValue = String(chat.memoryId || '');
        } else {
          throw new Error('无效的会话参数');
        }

        if (!memoryIdValue) {
          ElMessage.error('无效的会话ID');
          return;
        }
        
        if (memoryId.value === memoryIdValue && messages.value.length > 0) {
          ElMessage.info('当前会话已加载');
          return;
        }

        messages.value = [];
        isLoadingMore.value = true;
        const originalStartTime = getChinaTime();
        showChatInput.value = true;
        isChatActive.value = true;
        
        try {
          const res = await fetchChatMessages(memoryIdValue, originalStartTime);

          let messageList = [];

          if (res && res.data && Array.isArray(res.data)) {
            messageList = res.data;
          } else if (res && res.data && res.data.data && Array.isArray(res.data.data)) {
            messageList = res.data.data;
          }

          messages.value = messageList.map(msg => {
            // 格式化时间
            let formattedTime;
            if (Array.isArray(msg.createTime)) {
              try {
                const [year, month, day, hour, minute, second] = msg.createTime;
                const date = new Date(year, month - 1, day, hour, minute, second);
                formattedTime = date.toISOString();
              } catch (e) {
                formattedTime = new Date().toISOString();
              }
            } else {
              formattedTime = msg.createTime || msg.time || new Date().toISOString();
            }

            // 适配角色类型
            let role;
            if (msg.type === 'USER') {
              role = 'user';
            } else if (msg.type === 'AI') {
              role = 'assistant';
            } else {
              role = msg.role || (msg.isUser ? 'user' : 'assistant');
            }

            return {
              role: role,
              content: msg.content || '',
              type: 'text',
              time: formattedTime,
              id: msg.id,
              memoryId: msg.memoryId,
              fileName: msg.fileName,
              fileUrl: msg.fileUrl
            };
          });

          // 过滤掉重复的消息
          if (messages.value.length > 1) {
            const uniqueMessages = [];
            const seenMessages = new Set();
            
            for (const msg of messages.value) {
              const key = `${msg.role}-${msg.content.substring(0, 50)}-${msg.time}`;
              if (!seenMessages.has(key)) {
                seenMessages.add(key);
                uniqueMessages.push(msg);
              }
            }
            
            messages.value = uniqueMessages;
          }

          // 更新最早消息的时间
          updateOldestMessageTime(messages.value);

          // 更新会话状态
          memoryId.value = memoryIdValue;
          startTime.value = originalStartTime;
          isFirstMessage.value = false;
          isChatActive.value = true;
          canLoadMore.value = true;
          showChatInput.value = true;

          // 滚动到底部
          scrollToBottom();

          ElMessage.success(`历史会话加载成功 (${messages.value.length}条消息)`);
        } catch (apiError) {
          throw new Error(`API请求失败: ${apiError.message || '未知错误'}`);
        } finally {
          isLoadingMore.value = false;
        }
      } catch (error) {
        console.error('加载历史聊天失败');
        ElMessage.error(`无法加载聊天记录: ${error.message || '请稍后再试'}`);
        isLoadingMore.value = false;

        // 初始化空对话，不阻塞用户操作
        messages.value = [{
          role: 'assistant',
          content: '很抱歉，无法加载历史消息。您可以开始新的对话，或者稍后再试。',
          type: 'text',
          time: new Date().toISOString()
        }];
      }
    };

    // 更新最早消息时间
    const updateOldestMessageTime = (msgs) => {
      if (!msgs || msgs.length === 0) {
        oldestMessageTime.value = null;
        return;
      }

      try {
        // 按照时间排序
        const sortedMsgs = [...msgs].sort((a, b) => {
          const timeA = new Date(a.time || a.createTime || 0).getTime();
          const timeB = new Date(b.time || b.createTime || 0).getTime();
          return timeA - timeB;
        });

        // 获取最早的消息时间
        const earliestMsg = sortedMsgs[0];
        oldestMessageTime.value = earliestMsg.time || earliestMsg.createTime;
      } catch (e) {
        console.error('更新最早消息时间失败:', e);
        oldestMessageTime.value = null;
      }
    };

    // 加载更多历史消息
    const loadMoreHistoryMessages = async () => {
      if (!memoryId.value || !oldestMessageTime.value || isLoadingMore.value || !canLoadMore.value) {
        return;
      }

      try {
        isLoadingMore.value = true;

        const res = await loadMoreMessages(memoryId.value, oldestMessageTime.value);
        let moreMessages = [];

        if (res && res.data && Array.isArray(res.data)) {
          moreMessages = res.data;
        } else if (res && res.data && res.data.data && Array.isArray(res.data.data)) {
          moreMessages = res.data.data;
        }

        // 如果没有更多消息，则禁用加载更多
        if (!moreMessages || moreMessages.length === 0) {
          canLoadMore.value = false;
          isLoadingMore.value = false;
          return;
        }

        // 格式化消息
        const formattedMessages = moreMessages.map(msg => {
          // 格式化时间
            let formattedTime;
            if (Array.isArray(msg.createTime)) {
              try {
                const [year, month, day, hour, minute, second] = msg.createTime;
              const date = new Date(year, month - 1, day, hour, minute, second);
                formattedTime = date.toISOString();
              } catch (e) {
                formattedTime = new Date().toISOString();
              }
            } else {
              formattedTime = msg.createTime || msg.time || new Date().toISOString();
            }

          // 适配角色类型
            let role;
            if (msg.type === 'USER') {
              role = 'user';
            } else if (msg.type === 'AI') {
              role = 'assistant';
            } else {
              role = msg.role || (msg.isUser ? 'user' : 'assistant');
            }

            return {
              role: role,
              content: msg.content || '',
            type: 'text',
              time: formattedTime,
              id: msg.id,
              memoryId: msg.memoryId,
              fileName: msg.fileName,
              fileUrl: msg.fileUrl
            };
          });

        // 保存滚动位置
        const scrollElement = chatBox.value;
        const oldScrollHeight = scrollElement.scrollHeight;

        // 在消息列表顶部添加新消息
        messages.value = [...formattedMessages, ...messages.value];

        // 更新最早消息的时间
        updateOldestMessageTime(messages.value);

        // 等待DOM更新后恢复滚动位置
        await nextTick();
        const newScrollHeight = scrollElement.scrollHeight;
        const heightDiff = newScrollHeight - oldScrollHeight;
        scrollElement.scrollTop = scrollElement.scrollTop + heightDiff;

        ElMessage.success(`加载了${formattedMessages.length}条历史消息`);
      } catch (error) {
        console.error('加载更多历史消息失败:', error);
        ElMessage.error('加载更多历史消息失败，请稍后再试');
        canLoadMore.value = false;
      } finally {
        isLoadingMore.value = false;
      }
    };

    // 滚动处理
    const handleScroll = () => {
      if (!chatBox.value) return;

      // 检测是否显示"滚动到底部"按钮
      const {scrollTop, scrollHeight, clientHeight} = chatBox.value;
      const isNearBottom = scrollHeight - scrollTop - clientHeight < 200;
      showScrollToBottom.value = !isNearBottom;

      // 检测是否滚动到顶部，加载更多消息
      if (scrollTop < 50 && memoryId.value && !isLoadingMore.value && canLoadMore.value) {
        loadMoreHistoryMessages();
      }
    };

        // 滚动到底部
    const scrollToBottom = () => {
      if (!chatBox.value) return;

      // 使用双重保险确保滚动到底部
        nextTick(() => {
        requestAnimationFrame(() => {
          if (chatBox.value) {
          chatBox.value.scrollTop = chatBox.value.scrollHeight;
            showScrollToBottom.value = false;
          }
        });
      });
    };

    const handleSendMessage = async (message) => {
      if (!message || message.trim() === '') {
        ElMessage.warning('请输入消息内容');
        return;
      }
      // 如果是新会话，先创建MemoryId
      if (!memoryId.value || isFirstMessage.value) {
        memoryId.value = generateMemoryId();
        startTime.value = getChinaTime();
        isFirstMessage.value = false;
      }
      // 添加用户消息
      const userMessage = {
        role: 'user',
        content: message,
        type: 'text',
        time: getChinaTime()
      };
      // 添加临时AI消息用于显示流式响应
      const tempAiMessage = {
        role: 'assistant',
        content: '',
        type: 'text',
        time: getChinaTime(),
        isTyping: true,
        isReadingWebpage: detectUrl(message)
      };
      // 使用新数组添加消息，确保响应式更新
      messages.value = [...messages.value, userMessage, tempAiMessage];
      // 立即滚动到底部
      await nextTick();
      scrollToBottom();
      try {
        // 根据不同模式调用不同API
        let apiFunction;
        if (mode.value === 'webSearch') {
          apiFunction = sendAIChatMessage;
        } else if (mode.value === 'adminAgent') {
          apiFunction = sendAdminAIAgentMessage;
        } else {
          apiFunction = sendAdminMCPAIAgentMessage;
        }
        const response = await apiFunction(message, memoryId.value);
        if (response && response.body && typeof response.body.getReader === 'function') {
          const reader = response.body.getReader();
          const decoder = new TextDecoder('utf-8');
          // 保存reader引用，以便组件销毁时取消
          currentReader.value = reader;
          try {
            while (true) {
              const {value, done} = await reader.read();
              if (done) {
                // 流结束
                tempAiMessage.isTyping = false;
                tempAiMessage.isReadingWebpage = false;
                // 强制更新视图
                messages.value = [...messages.value];
                break;
              }
              // 解码流数据
              const chunk = decoder.decode(value, {stream: true});
              // 处理SSE格式 (data:前缀)
              const dataLines = chunk.split('\n');
              let processedContent = '';
              for (const line of dataLines) {
                if (line.startsWith('data:')) {
                  // 去除data:前缀，保留实际内容
                  let content = line.substring(5);
                  // 特殊字符处理
                  if (content === '-') {
                    content = '\n';
                  }
                  processedContent += content;
                } else if (line.trim() !== '') {
                  processedContent += line;
                }
              }
              // 更新消息内容 (重要：必须创建一个新对象而不是直接修改)
              if (processedContent) {
                // 如果是第一个响应且之前是"正在读取网页"，则替换内容
                if (tempAiMessage.content === '' && tempAiMessage.isReadingWebpage) {
                  tempAiMessage.isReadingWebpage = false;
                  tempAiMessage.content = processedContent;
                  } else {
                  tempAiMessage.content += processedContent;
                }
                // 制造一个新的消息数组以触发视图更新
                const messagesClone = messages.value.map(msg =>
                    msg === tempAiMessage ? {...tempAiMessage} : msg
                );
                messages.value = messagesClone;
                  // 滚动到底部
                await nextTick();
                  scrollToBottom();
                }
              }
          } catch (streamError) {
            console.error('读取流数据失败:', streamError);
            tempAiMessage.isTyping = false;
            tempAiMessage.isReadingWebpage = false;
            tempAiMessage.content += '\n\n[读取响应失败，请尝试重新发送消息]';

            // 重新克隆消息数组以更新视图
            messages.value = messages.value.map(msg =>
                msg === tempAiMessage ? {...tempAiMessage} : msg
            );
          } finally {
              currentReader.value = null;
            }
        } else {
          // 非流式响应
          tempAiMessage.isTyping = false;
          tempAiMessage.isReadingWebpage = false;
          console.log("这是非流式响应")
          try {
            const text = await response.text();
            tempAiMessage.content = text || '未获取到有效回复';
          } catch (error) {
            tempAiMessage.content = '处理响应时出错: ' + error.message;
          }

          // 更新视图
          messages.value = [...messages.value];
        }
      } catch (error) {
        console.error('发送消息失败:', error);
        // 如果出错，更新错误消息到最后一条
        const lastMessage = messages.value[messages.value.length - 1];
        if (lastMessage && lastMessage.role === 'assistant') {
          lastMessage.isTyping = false;
          lastMessage.isReadingWebpage = false;
          lastMessage.content = '抱歉，发送消息失败: ' + (error.message || '请稍后再试');

          // 更新视图
          messages.value = [...messages.value];
        }

        ElMessage.error('发送消息失败，请稍后再试');
      }
    };

    // 切换模式
    const onModeChange = (newMode) => {
      if (newMode === mode.value) return;

      previousMode.value = mode.value;
      mode.value = newMode;

      if (messages.value.length > 0) {
        showModelSwitchNotice.value = true;
        setTimeout(() => {
          showModelSwitchNotice.value = false;
        }, 5000);
      }
    };

    // 创建新对话
    const createNewChat = () => {
      memoryId.value = generateMemoryId();
      startTime.value = getChinaTime();
      messages.value = [];
      isFirstMessage.value = true;
      isChatActive.value = true;
      showChatInput.value = true;
      canLoadMore.value = true;
      oldestMessageTime.value = null;
      ElMessage.success('已创建新会话');

      // 添加欢迎消息
      messages.value.push({
        role: 'assistant',
        content: `智能体辅助解决学生心理问题。我可以帮助您回答问题、提供信息或进行轻松对话。请告诉我您想了解什么？`,
        type: 'text',
        time: getChinaTime()
      });

      scrollToBottom();
    };

    // 切换历史视图
    const toggleHistoryView = () => {
      showAllHistory.value = !showAllHistory.value;
    };

    // 处理路由变化
    const handleRouteChange = () => {
      const path = window.location.pathname;
      isChatActive.value = path.includes('/admin/ai-chat');
    };

    // 刷新历史记录
    const refreshHistoryChats = async () => {
      try {
        await fetchHistory(true);
        console.log('历史会话已刷新');
      } catch (error) {
        console.error('刷新历史会话失败:', error);
      }
    };

    // 第一步：触发文件生成并获取用户ID（SSE订阅凭证）
    async function startGeneration(content) {
      const formData = new FormData();
      formData.append('content', content);
      formData.append('type', 'MD'); // 使用全大写！
      formData.append('userId', '123'); // 假设当前用户ID

      const response = await fetch('/sse/generate', {
        method: 'POST',
        body: formData
      });

      return await response.json(); // 返回 { status: "STARTED", userId: 123 }
    }


    // 监听模式变化
    watch(() => mode.value, (newMode) => {
      console.log('模式已切换到:', newMode);
    });

    // 组件挂载时执行
    onMounted(async () => {
      // 示例字符串（模拟用户输入的 msg.content）
      const content = "登录用户id为:24，后面的内容需要保留";

// 核心替换逻辑
      const result = content.replace(/登录用户id为:\d+/g, '');
      console.log(result); // 输出: ,后面的内容需要保留
      window.addEventListener('popstate', handleRouteChange);
      //建立sse连接
      await connect()
      // await connect()
      try {
        // 获取历史会话列表
        await fetchHistory();

        // 创建新会话
        createNewChat();

        // 设置定时刷新历史记录
        refreshInterval.value = setInterval(refreshHistoryChats, 300000); // 5分钟刷新一次
      } catch (error) {
        console.error('初始化聊天失败:', error);
        ElMessage.error('初始化聊天失败，请刷新页面重试');
      }
    });

    // 获取当前路由
    const checkCurrentRoute = () => {
      const path = window.location.pathname;
      return path.includes('/admin/ai-chat');
    };

    return {
      messages,
      historyChats,
      displayChats,
      memoryId,
      mode,
      chatBox,
      userAvatar,
      aiAvatar,
      isChatActive,
      isLoadingMore,
      canLoadMore,
      showScrollToBottom,
      showAllHistory,
      showModelSwitchNotice,
      showChatInput,
      renderMarkdown,
      formatTime,
      handleSendMessage,
      loadHistoryChat,
      scrollToBottom,
      handleScroll,
      createNewChat,
      onModeChange,
      toggleHistoryView,
      detectUrl,
      checkCurrentRoute
    };
  }
};
</script>

<style scoped>
.app-container.kimi-style {
  display: flex;
  height: calc(100vh - 60px);
  width: 100%;
  max-width: 100%;
  overflow: hidden;
  background-color: #f9fafb;
  position: relative;
}

.sidebar {
  width: 280px;
  background-color: #f5f5f7;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e0e0e0;
  height: 100%;
  overflow-y: auto;
  transition: width 0.3s ease;
}

.sidebar-header {
  padding: 24px 16px 16px;
  border-bottom: 1px solid #e0e0e0;
}

.sidebar-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a202c;
  margin: 0;
}

.nav-list {
  list-style: none;
  padding: 8px;
  margin: 0;
  overflow-y: auto;
}

.nav-item {
  margin: 4px 0;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.nav-item.active {
  background-color: rgba(0, 0, 0, 0.08);
  font-weight: 500;
}

.sidebar-icon-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.sidebar-icon-wrapper .icon {
  width: 18px;
  height: 18px;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  max-width: calc(100% - 280px);
  position: relative;
  background-color: #ffffff;
}

.chat-header-fixed {
  padding: 16px 24px;
  background-color: #ffffff;
  border-bottom: 1px solid #e0e0e0;
  z-index: 50;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header-fixed h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #1a202c;
}

.current-model-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4b5563;
  background-color: #f3f4f6;
  padding: 6px 12px;
  border-radius: 16px;
}

.model-icon {
  display: flex;
  align-items: center;
}

.model-icon svg {
  width: 16px;
  height: 16px;
  color: #4b5563;
}

.chat-box {
  flex: 1;
  overflow-y: auto;
  padding: 0;
  position: relative;
  display: flex;
    flex-direction: column;
  height: calc(100% - 130px);
}

.chat-box.no-input {
  height: calc(100% - 60px);
}

.messages-container {
  padding: 16px 0;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.messages-bottom-space {
  height: 24px;
}

.loading-more {
  text-align: center;
  padding: 16px;
  color: #6b7280;
  font-size: 14px;
}

.model-switch-notice {
  margin: 16px auto;
  padding: 12px 16px;
  background-color: #f0f9ff;
  border: 1px solid #e0f2fe;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-icon {
  color: #0284c7;
}

.notice-icon svg {
  width: 24px;
  height: 24px;
}

.notice-content {
  flex: 1;
}

.notice-title {
  font-weight: 500;
  color: #0369a1;
  font-size: 15px;
}

.notice-desc {
  color: #0369a1;
  font-size: 13px;
  margin-top: 4px;
}

.scroll-to-bottom {
  position: absolute;
  bottom: 100px;
  right: 20px;
  width: 40px;
  height: 40px;
  background-color: #ffffff;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: all 0.2s;
  z-index: 10;
}

.scroll-to-bottom:hover {
  background-color: #f5f5f7;
  transform: translateY(-2px);
}

.scroll-to-bottom svg {
  width: 24px;
  height: 24px;
  color: #374151;
}
</style>