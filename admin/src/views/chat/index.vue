<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { ElMessage } from 'element-plus';
import { getUsers } from "@/api/teacher/students.js";
import { createWebSocketConnection, getChatMessages, sendMessage as sendWsMessage } from "@/api/teacher/chat.js";
import StudentList from '@/components/chat/StudentList.vue';
import ChatContent from '@/components/chat/TeacherChat.vue';
import VideoChat from '@/components/chat/videochat.vue';
import { uploadImage as uploadImageAPI } from '@/api/teacher/article.js';

// WebSocket 相关
const ws = ref(null);
const token = ref(localStorage.getItem('accessToken'));
const reconnectAttempts = ref(0);
const maxReconnectAttempts = 5;
const reconnectInterval = 2000;
const isConnecting = ref(false);

const students = ref([]);
const selectedStudentId = ref(null);
const studentName = ref('');
const messages = ref([]); // 前端显示的消息，主要使用 content
const pendingMessages = ref([]); // 未发送成功的消息，保留完整对象
const isVideoChatActive = ref(false);
const hasFetchedMessages = ref(false);

// 从 localStorage 加载待发送消息
const loadPendingMessages = () => {
  const stored = localStorage.getItem(`pendingMessages_${selectedStudentId.value}`);
  return stored ? JSON.parse(stored) : [];
};

// 保存待发送消息到 localStorage
const savePendingMessages = () => {
  localStorage.setItem(`pendingMessages_${selectedStudentId.value}`, JSON.stringify(pendingMessages.value));
};

// 建立 WebSocket 连接
async function connectWebSocket(receiverId) {
  // 检查 receiverId 是否有效
  if (!receiverId) {
    ElMessage.error('无效的学生ID');
    return;
  }
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
      if (!hasFetchedMessages.value) {
        await fetchChatMessages(receiverId);
        hasFetchedMessages.value = true;
      }
      isConnecting.value = false;
    };
    ws.value.onmessage = (event) => {
      // 假设 WebSocket 返回纯文本消息
      messages.value.push({ content: event.data });
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
      console.error('WebSocket 发生错误: ', error);
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
      messages.value.push({ content: msg.content });
    }
    pendingMessages.value = [];
    savePendingMessages();
  } catch (error) {
    console.error('同步历史消息失败:', error);
  }
};

onMounted(async () => {
  try {
    const res = await getUsers();
    if (res.code === 1 && Array.isArray(res.data)) {
      students.value = res.data;
    } else {
      ElMessage.warning('没有学生数据');
    }
  } catch (error) {
    ElMessage.error('加载学生列表失败');
  }
});

// 选择学生
const selectStudent = async (studentId) => {
  const student = students.value.find((s) => s.id === studentId);
  if (student) {
    selectedStudentId.value = student.id;
    studentName.value = student.userName || student.name; // 适配后端返回的字段
    if (ws.value && ws.value.readyState !== WebSocket.CLOSED) {
      ws.value.close();
    }
    messages.value = [];
    pendingMessages.value = loadPendingMessages();
    hasFetchedMessages.value = false;
    await connectWebSocket(student.id);
  } else {
    ElMessage.warning('没有找到此学生');
  }
};

// 获取聊天记录
const fetchChatMessages = async (studentId) => {
  try {
    const chatMessages = await getChatMessages(studentId, 1, 5);
    console.log('获取聊天记录:', chatMessages);
    if (chatMessages && Array.isArray(chatMessages)) {
      messages.value = chatMessages.map(msg => ({ content: msg.content }));
      console.log('加载聊天记录成功:', messages.value);
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
const sendMessage = async (msg = '') => {
  if (!selectedStudentId.value) {
    ElMessage.warning('请先选择一个学生');
    return;
  }
  if (!msg.trim()) {
    ElMessage.warning('消息不能为空');
    return;
  }
  const messageObj = {
    id: Date.now(),
    content: msg,
    type: 'text',
  };
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    sendWsMessage(ws.value, msg);
    messages.value.push({content: msg});
  } else {
    pendingMessages.value.push(messageObj);
    savePendingMessages();
    await connectWebSocket(selectedStudentId.value);
  }
};

// 上传图片
const uploadImage = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    const imageUrl = await uploadImageAPI(formData);
    const messageObj = {
      id: Date.now(),
      content: imageUrl,
      type: 'image',
    };
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      sendWsMessage(ws.value, imageUrl);
      messages.value.push({content: imageUrl});
    } else {
      pendingMessages.value.push(messageObj);
      savePendingMessages();
      await connectWebSocket(selectedStudentId.value);
    }
  } catch (error) {
    ElMessage.error('发送图片失败');
  }
};

// 开始视频聊天
const startVideoChat = () => {
  if (!selectedStudentId.value) {
    ElMessage.warning('请先选择一个学生');
    return;
  }
  isVideoChatActive.value = true;
};

// 关闭视频聊天
const closeVideoChat = () => {
  isVideoChatActive.value = false;
};

onBeforeUnmount(() => {
  if (ws.value && ws.value.readyState !== WebSocket.CLOSED) {
    ws.value.close();
  }
});
</script>

<template>
  <div class="teacher-chat">

    <div class="chat-content">
      <ChatContent
          :messages="messages"
          :student-name="studentName"
          @send-message="sendMessage"
          @upload-image="uploadImage"
          @start-video-chat="startVideoChat"
      />
    </div>
<!--    <div class="video-chat">-->
<!--      <VideoChat-->
<!--          v-if="isVideoChatActive"-->
<!--          :peer-id="selectedStudentId"-->
<!--          @close="closeVideoChat"-->
<!--      />-->
<!--    </div>-->
  </div>
</template>

<style scoped>
.teacher-chat {
  display: flex;
  height: 100vh; /* 使用父容器的高度 */
  width: 100%;
  background-color: #f5f5f5;
  padding: 10px;
  box-sizing: border-box;
  overflow: hidden; /* 防止溢出 */
}

.student-list {
  width: 180px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  overflow-y: auto;
}

.chat-content {
  flex-grow: 1;
  height: 90%; /* 继承父容器高度 */
  display: flex;
  overflow: hidden; /* 防止溢出 */
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  padding: 15px;
}

.video-chat {
  width: 300px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.teacher-chat > div {
  transition: all 0.3s ease;
}

.teacher-chat > div:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}
</style>
