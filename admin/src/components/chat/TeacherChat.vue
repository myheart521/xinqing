<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed, watch } from 'vue';
import { ElMessage, ElButton, ElInput } from 'element-plus';
import { getUsers } from "@/api/teacher/students.js";
import { createWebSocketConnection, getChatMessages, sendMessage as sendWsMessage } from "@/api/teacher/chat.js";
import StudentList from '@/components/chat/StudentList.vue';
import { uploadImage as uploadImageAPI } from "@/api/teacher/article.js";
import { fetchUserInfo } from '@/api/top/index';

// WebSocket 相关
const ws = ref(null);
const token = ref(localStorage.getItem('accessToken'));
const reconnectAttempts = ref(0);
const maxReconnectAttempts = 5;
const reconnectInterval = 2000;
const isConnecting = ref(false);

// 聊天相关
const students = ref([]);
const selectedStudentId = ref(null);
const studentName = ref('');
const messages = ref([]);
const pendingMessages = ref([]);
const newMessage = ref('');
const showEmojiPicker = ref(false);
const isChatReady = ref(false);
const messageList = ref(null);
const showScrollToBottom = ref(false);

// 头像配置
const teacherAvatar = ref(localStorage.getItem('userAvatar') || 'https://img.51miz.com/Element/00/88/08/25/72f298b2_E880825_b1a3e083.png');
const studentAvatar = ref('https://img.51miz.com/Element/00/37/79/24/1e088f56_E377924_9c413239.png');

// 获取用户头像
const fetchUserAvatar = async () => {
  try {
    const userToken = localStorage.getItem('accessToken');
    if (userToken) {
      const userData = await fetchUserInfo(userToken);
      if (userData && userData.avatar) {
        teacherAvatar.value = userData.avatar;
        localStorage.setItem('userAvatar', userData.avatar);
      }
    }
  } catch (error) {
    console.error('获取用户头像失败:', error);
  }
};

// Emoji 列表
const emojis = ref([
  '😊', '👍', '😢', '😎', '❤️', '🔥', '👀', '👏', '😀', '😁',
  '😆', '🤣', '😂', '🙂', '🤗', '🥰', '😍', '🤩', '😘', '😗',
  '😙', '😚', '☺️', '🙂', '🤗',
]);

// 从 localStorage 加载待发送消息
const loadPendingMessages = () => {
  const stored = localStorage.getItem(`pendingMessages_${selectedStudentId.value}`);
  return stored ? JSON.parse(stored) : [];
};

// 保存待发送消息到 localStorage
const savePendingMessages = () => {
  localStorage.setItem(`pendingMessages_${selectedStudentId.value}`, JSON.stringify(pendingMessages.value));
};

// 检测是否需要显示滚动到底部按钮
const checkScrollPosition = () => {
  if (!messageList.value) return;
  
  const { scrollTop, scrollHeight, clientHeight } = messageList.value;
  const isScrolledToBottom = scrollHeight - scrollTop - clientHeight < 100;
  showScrollToBottom.value = !isScrolledToBottom && messages.value.length > 3;
};

// 滚动到消息列表底部
const scrollToBottom = async () => {
  try {
    await nextTick(); // 等待DOM更新
    if (messageList.value) {
      const scrollHeight = messageList.value.scrollHeight;
      messageList.value.scrollTop = scrollHeight;
      showScrollToBottom.value = false;
    }
  } catch (error) {
    console.error('滚动到底部失败:', error);
  }
};

// 监听消息列表的滚动事件
const handleScroll = () => {
  checkScrollPosition();
};

// 建立 WebSocket 连接
async function connectWebSocket(receiverId) {
  if (!token.value) {
    ElMessage.error('未找到有效的认证令牌');
    return;
  }
  if (isConnecting.value) return;
  isConnecting.value = true;
  try {
    ws.value = createWebSocketConnection(token.value, receiverId);
    ws.value.onopen = async () => {
      console.log('WebSocket 连接已建立');
      reconnectAttempts.value = 0;
      await syncPendingMessages();
      await fetchChatMessages(receiverId);
      isChatReady.value = true;
      isConnecting.value = false;
    };
    ws.value.onmessage = (event) => {
      let content = event.data;
      try {
        const data = JSON.parse(event.data);
        content = data.content || event.data;
      } catch (e) {
        console.warn('WebSocket消息非JSON:', event.data);
      }
      console.log('收到WebSocket消息:', content);
      messages.value.push({ id: Date.now(), content, sender: 'other' });
      scrollToBottom();
    };
    ws.value.onclose = (event) => {
      console.log('WebSocket 连接已关闭, code:', event.code);
      isConnecting.value = false;
      if (reconnectAttempts.value < maxReconnectAttempts && event.code !== 1000) {
        setTimeout(() => {
          reconnectAttempts.value++;
          console.log(`尝试重新连接 (${reconnectAttempts.value}/${maxReconnectAttempts})...`);
          connectWebSocket(receiverId);
        }, reconnectInterval);
      }
    };
    ws.value.onerror = (error) => {
      console.error('WebSocket 发生错误:', error);
      isConnecting.value = false;
    };
  } catch (error) {
    console.error('创建 WebSocket 连接失败:', error);
    isConnecting.value = false;
  }
}

// 同步待处理消息
const syncPendingMessages = async () => {
  if (pendingMessages.value.length === 0) return;
  try {
    for (const msg of pendingMessages.value) {
      sendWsMessage(ws.value, msg.content);
      messages.value.push({ id: msg.id, content: msg.content, sender: 'me' });
      console.log('同步待发送消息:', msg.content);
      scrollToBottom();
    }
    pendingMessages.value = [];
    savePendingMessages();
  } catch (error) {
    console.error('同步历史消息失败:', error);
  }
};

// 加载学生列表和用户头像
onMounted(async () => {
  try {
    // 获取用户头像
    await fetchUserAvatar();
    
    // 获取学生列表
    const res = await getUsers();
    if (res.code === 1 && Array.isArray(res.data)) {
      students.value = res.data;
    } else {
      ElMessage.warning('没有学生数据');
    }
  } catch (error) {
    ElMessage.error('初始化失败');
  }
});

// 选择学生
const selectStudent = async (studentId) => {
  const student = students.value.find((s) => s.id === studentId);
  if (student) {
    if (ws.value && ws.value.readyState !== WebSocket.CLOSED) {
      ws.value.close();
    }
    selectedStudentId.value = student.id;
    studentName.value = student.userName || student.name;
    // 使用学生的真实头像（如果有）
    studentAvatar.value = student.userAvatar || student.avatar || 'https://img.51miz.com/Element/00/37/79/24/1e088f56_E377924_9c413239.png';
    console.log('选择学生:', student, '设置学生头像:', studentAvatar.value);
    messages.value = [];
    pendingMessages.value = loadPendingMessages();
    isChatReady.value = false;
    await connectWebSocket(student.id);
  } else {
    ElMessage.warning('没有找到此学生');
  }
};

// 获取聊天记录
const fetchChatMessages = async (studentId) => {
  try {
    const chatMessages = await getChatMessages(studentId, 1, 10);
    console.log('原始聊天记录:', chatMessages);
    if (chatMessages && Array.isArray(chatMessages)) {
      messages.value = chatMessages
          .map((msg) => ({
            id: msg.id || Date.now() + Math.random().toString(36).slice(2),
            content: msg.content,
            sender: msg.sender === 'me' ? 'me' : 'other',
          }))
          .reverse(); // 反转确保最新消息在数组末尾（显示在底部）
      
      console.log('处理后的消息数组:', messages.value);
      scrollToBottom();
      if (chatMessages.length === 0 && pendingMessages.value.length === 0) {
        ElMessage.info('暂无聊天记录');
      }
    } else {
      messages.value = [];
      ElMessage.info('暂无聊天记录');
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error);
    ElMessage.error('加载聊天记录失败');
  }
};

// 发送消息
const sendMessage = async () => {
  if (!selectedStudentId.value) {
    ElMessage.warning('请先选择一个学生');
    return;
  }
  if (!newMessage.value.trim()) {
    ElMessage.warning('消息内容不能为空');
    return;
  }
  const messageObj = {
    id: Date.now() + Math.random().toString(36).slice(2),
    content: newMessage.value,
    sender: 'me',
    type: 'text',
  };
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    sendWsMessage(ws.value, newMessage.value);
    messages.value.push({id: messageObj.id, content: newMessage.value, sender: 'me'});
    console.log('发送消息:', newMessage.value);
    scrollToBottom();
  } else {
    pendingMessages.value.push(messageObj);
    savePendingMessages();
    await connectWebSocket(selectedStudentId.value);
  }
  newMessage.value = '';
  showEmojiPicker.value = false;
};

// 上传图片
const uploadImage = async () => {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.onchange = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    try {
      const formData = new FormData();
      formData.append('file', file);
      const imageUrl = await uploadImageAPI(formData);
      const messageObj = {
        id: Date.now() + Math.random().toString(36).slice(2),
        content: imageUrl,
        sender: 'me',
        type: 'image',
      };
      if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        sendWsMessage(ws.value, imageUrl);
        messages.value.push({id: messageObj.id, content: imageUrl, sender: 'me'});
        console.log('发送图片:', imageUrl);
        scrollToBottom();
      } else {
        pendingMessages.value.push(messageObj);
        savePendingMessages();
        await connectWebSocket(selectedStudentId.value);
      }
    } catch (error) {
      console.error('发送图片失败:', error);
      ElMessage.error('发送图片失败');
    }
  };
  input.click();
};

const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value;
};

const addEmoji = (emoji) => {
  newMessage.value += emoji;
  showEmojiPicker.value = false;
};

// 监听消息列表的变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom();
  });
});

onBeforeUnmount(() => {
  if (ws.value && ws.value.readyState !== WebSocket.CLOSED) {
    ws.value.close();
  }
});
</script>

<template>
  <div class="teacher-chat">
    <!-- 左侧联系人列表 -->
    <div class="student-list">
      <StudentList
          :students="students"
          @select-student="selectStudent"
          :selected-id="selectedStudentId"
      />
    </div>
    
    <!-- 右侧聊天区域 -->
    <div class="chat-content">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <div class="chat-title">{{ studentName || '请选择联系人' }}</div>
      </div>
      
      <!-- 聊天主体区域 -->
      <div class="chat-body">
        <!-- 消息列表区域 -->
        <div class="messages-container" ref="messageList" @scroll="handleScroll">
          <div v-if="!isChatReady && selectedStudentId" class="loading-message">
            正在加载聊天记录...
          </div>
          
          <div v-if="!selectedStudentId" class="welcome-message">
            请选择一个联系人开始聊天
          </div>
          
          <template v-if="isChatReady">
            <div 
                v-for="msg in messages" 
                :key="msg.id" 
                :class="['message-row', msg.sender === 'me' ? 'message-row-me' : 'message-row-other']"
            >
              <!-- 头像区域 -->
              <div class="avatar-container">
                <img :src="msg.sender === 'me' ? teacherAvatar : studentAvatar" class="avatar" :alt="msg.sender === 'me' ? '我' : studentName" />
              </div>
              
              <!-- 消息气泡 -->
              <div :class="['message-bubble', msg.sender === 'me' ? 'message-me' : 'message-other']">
                <div class="message-content">{{ msg.content }}</div>
              </div>
            </div>
          </template>
          
          <!-- 滚动到底部按钮 -->
          <div v-if="showScrollToBottom" class="scroll-bottom-btn" @click="scrollToBottom">
            <svg viewBox="0 0 1024 1024" width="16" height="16" fill="currentColor">
              <path d="M840.4 300H183.6c-19.7 0-30.7 20.8-18.5 35l328.4 380.8c9.4 10.9 27.5 10.9 37 0L858.9 335c12.2-14.2 1.2-35-18.5-35z"/>
            </svg>
          </div>
        </div>
      </div>
      
      <!-- 输入区域 - 固定在底部 -->
      <div class="input-container">
        <!-- 工具栏 -->
        <div class="toolbar">
          <div class="toolbar-btn" @click="toggleEmojiPicker">
            <img src="/assets/placeholder.svg" alt="表情" class="toolbar-icon" />
          </div>
          <div class="toolbar-btn" @click="uploadImage">
            <img src="/assets/placeholder.svg" alt="上传图片" class="toolbar-icon" />
          </div>
          
          <!-- 表情选择器 -->
          <div v-if="showEmojiPicker" class="emoji-picker">
            <span
                v-for="emoji in emojis"
                :key="emoji"
                @click="addEmoji(emoji)"
                class="emoji-item"
            >
              {{ emoji }}
            </span>
          </div>
        </div>
        
        <!-- 消息输入框 -->
        <div class="input-area">
          <ElInput
              v-model="newMessage"
              placeholder="请输入消息..."
              @keyup.enter="sendMessage"
              class="message-input"
              type="textarea"
              :rows="1"
              :autosize="{ minRows: 1, maxRows: 4 }"
          />
          <ElButton type="primary" class="send-button" @click="sendMessage">发送</ElButton>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 整体布局 */
.teacher-chat {
  display: flex;
  height: 100vh;
  width: 100%;
  background-color: #f5f5f5;
  overflow: hidden;
}

/* 学生列表样式 */
.student-list {
  width: 250px;
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  overflow-y: auto;
  flex-shrink: 0;
}

/* 聊天内容区域 */
.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  height: 100%;
  overflow: hidden;
}

/* 聊天头部 */
.chat-header {
  height: 60px;
  background-color: #f8f8f8;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
  flex-shrink: 0;
  z-index: 10;
}

.chat-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 聊天主体区域 */
.chat-body {
  flex: 1;
  position: relative;
  height: calc(100% - 60px); /* 只减去头部60px */
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 消息列表容器 */
.messages-container {
  height: 90%;
  padding: 15px;
  overflow-y: auto;
  background-color: #f5f5f5;
  background-image: url('/assets/placeholder.svg');
  background-size: cover;
  position: relative;
}

/* 加载消息提示 */
.loading-message, .welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
  color: #999;
  font-size: 14px;
}

/* 消息行 */
.message-row {
  display: flex;
  margin-bottom: 15px;
  position: relative;
  align-items: flex-start;
}

.message-row-me {
  flex-direction: row-reverse;
}

.message-row-other {
  flex-direction: row;
}

/* 头像容器 */
.avatar-container {
  flex-shrink: 0;
  margin: 0 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

/* 消息气泡 */
.message-bubble {
  max-width: 60%;
  padding: 10px 15px;
  border-radius: 4px;
  position: relative;
  word-break: break-word;
}

.message-me {
  background-color: #95ec69; /* 微信绿色气泡 */
  margin-right: 15px;
  border-radius: 12px 4px 12px 12px;
}

.message-other {
  background-color: #fff;
  margin-left: 15px;
  border-radius: 4px 12px 12px 12px;
}

.message-content {
  font-size: 14px;
  line-height: 1.5;
}

/* 输入区域 - 固定在底部 */
.input-container {
  height: 25%;
  background-color: #f8f8f8;
  border-top: 1px solid #e6e6e6;
  padding: 10px;
  z-index: 9999999;
  display: flex;
  flex-direction: column;
}

/* 工具栏 */
.toolbar {
  display: flex;
  padding: 5px 0;
  position: relative;
}

.toolbar-btn {
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.toolbar-btn:hover {
  background-color: #e0e0e0;
}

.toolbar-icon {
  width: 24px;
  height: 24px;
}

/* 表情选择器 */
.emoji-picker {
  position: absolute;
  top: -200px;
  left: 0;
  background: #fff;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 300px;
  z-index: 20;
}

.emoji-item {
  font-size: 20px;
  padding: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.emoji-item:hover {
  background-color: #e9ecef;
  border-radius: 4px;
}

/* 输入区域 */
.input-area {
  display: flex;
  align-items: flex-end;
  margin-top: 5px;
}

.message-input {
  flex: 1;
  border-radius: 4px;
  background-color: #fff;
  margin-right: 10px;
}

.message-input :deep(.el-textarea__inner) {
  height: 30px;
  border-radius: 4px;
  padding: 8px 10px;
  resize: none;
  box-shadow: none;
  border: 1px solid #e6e6e6;
}

.send-button {
  height: 36px;
  border-radius: 4px;
  padding: 0 15px;
  background-color: #07c160; /* 微信绿色 */
  border-color: #07c160;
}

.send-button:hover {
  background-color: #06ad56;
  border-color: #06ad56;
}

/* 滚动到底部按钮 */
.scroll-bottom-btn {
  position: absolute;
  right: 20px;
  bottom: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 5;
  color: #07c160;
}

.scroll-bottom-btn:hover {
  background-color: #fff;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
}
</style>
