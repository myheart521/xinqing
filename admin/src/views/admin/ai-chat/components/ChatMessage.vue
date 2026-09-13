<template>
  <div>
    <div v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
      <!-- AI消息布局 - 左侧显示 -->
      <template v-if="message.role === 'assistant' && message.content">
        <div class="avatar-container">
          <div class="avatar">
            <img :src="aiAvatar" alt="AI头像"/>
          </div>
        </div>

        <div class="message-wrapper">
          <div class="message-header">
            <div class="role-name">智能体</div>
            <div class="message-time">{{ formatMessageTime(message.time) }}</div>
          </div>

          <div class="message-body assistant">
            <!-- AI正在读取网页的指示器 -->
            <div v-if="message.isReadingWebpage && !message.content" class="reading-webpage-indicator">
              <div class="icon">
                <svg viewBox="0 0 1024 1024" fill="currentColor">
                  <path
                      d="M909.6 854.5L649.9 594.8C690.2 542.7 712 479 712 412c0-80.2-31.3-155.4-87.9-212.1-56.6-56.7-132-87.9-212.1-87.9s-155.5 31.3-212.1 87.9C143.2 256.5 112 331.8 112 412c0 80.1 31.3 155.5 87.9 212.1C256.5 680.8 331.8 712 412 712c67 0 130.6-21.8 182.7-62l259.7 259.6c3.2 3.2 8.4 3.2 11.6 0l43.6-43.5c3.2-3.2 3.2-8.4 0-11.6zM570.4 570.4C528 612.7 471.8 636 412 636s-116-23.3-158.4-65.6C211.3 528 188 471.8 188 412s23.3-116.1 65.6-158.4C296 211.3 352.2 188 412 188s116.1 23.2 158.4 65.6S636 352.2 636 412s-23.3 116.1-65.6 158.4z"/>
                </svg>
              </div>
              <span>正在读取网页中的信息...</span>
            </div>

            <!-- AI正在输入的指示器 (仅当没有内容和不在读取网页时显示) -->
            <div v-if="message.isTyping && !message.content" class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>

            <!-- 消息内容区域 (如果有内容则始终显示) -->
            <template v-if="message.content">
              <div v-if="message.type === 'text'&&renderMessageContent(message) !==''" class="message-content markdown-body"
                   v-html="renderMessageContent(message)"></div>

              <div v-else-if="message.type === 'json'" class="message-content code-content">
                <pre><code>{{ formatJson(message.content) }}</code></pre>
              </div>

              <div v-else-if="message.type === 'file'" class="message-content file-content">
                <div class="file-item">
                  <div class="file-icon">
                    <svg viewBox="0 0 1024 1024" fill="currentColor">
                      <path
                          d="M854.6 288.6L639.4 73.4c-6-6-14.1-9.4-22.6-9.4H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V311.3c0-8.5-3.4-16.7-9.4-22.7zM790.2 326H602V137.8L790.2 326zm1.8 562H232V136h302v216c0 23.2 18.8 42 42 42h216v494z"/>
                    </svg>
                  </div>
                  <a :href="message.fileUrl" target="_blank" class="file-name">{{ message.fileName || '文件' }}</a>
                </div>
              </div>

              <!-- AI仍在输入时的指示器 (当有内容且仍在输入时显示) -->
              <div v-if="message.isTyping" class="still-typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </template>
          </div>
        </div>
      </template>

      <!-- 用户消息布局 - 右侧显示 -->
      <template v-else-if="message.content">
        <div class="message-wrapper user-message-wrapper">
          <div class="message-header">
            <div class="avatar-container">
              <div class="avatar">
                <img :src="actualUserAvatar" alt="用户头像"/>
              </div>
            </div>
            <div class="role-name">{{ actualUserName }}</div>
            <div class="message-time">{{ formatMessageTime(message.time) }}</div>
          </div>

          <div class="message-body user">
            <!-- 消息内容区域 -->
            <div v-if="message.type === 'text'" class="message-content" v-html="renderMessageContent(message)"></div>

            <div v-else-if="message.type === 'json'" class="message-content code-content">
              <pre><code>{{ formatJson(message.content) }}</code></pre>
            </div>

            <div v-else-if="message.type === 'file'" class="message-content file-content">
              <div class="file-item">
                <div class="file-icon">
                  <svg viewBox="0 0 1024 1024" fill="currentColor">
                    <path
                        d="M854.6 288.6L639.4 73.4c-6-6-14.1-9.4-22.6-9.4H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V311.3c0-8.5-3.4-16.7-9.4-22.7zM790.2 326H602V137.8L790.2 326zm1.8 562H232V136h302v216c0 23.2 18.8 42 42 42h216v494z"/>
                  </svg>
                </div>
                <a :href="message.fileUrl" target="_blank" class="file-name">{{ message.fileName || '文件' }}</a>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import {computed, watch} from 'vue';
import {useUserStore} from '@/stores/user';
import {formatTime} from '@/utils/timeUtils.js';
import {renderMarkdown, isJsonString} from '@/utils/markdownUtils.js';

// 定义 props
const props = defineProps({
  messages: {
    type: Array,
    required: true,
    default: () => []
  },
  userAvatar: {
    type: String,
    default: ''
  },
  aiAvatar: {
    type: String,
    default: 'https://api.dicebear.com/7.x/bottts/svg?seed=Kimi'
  }
});

// 使用用户 store
const userStore = useUserStore();

// 格式化消息时间
const formatMessageTime = (time) => {
  return formatTime(time);
};

// 渲染单个消息内容
const renderMessageContent = (message) => {
  if (!message.content) return '';
  message.content = message.content.replace(/登录用户id为:\d+。/g, '')
  // 如果内容是JSON，尝试美化显示
  if (isJsonString(message.content)) {
    try {
      const parsed = JSON.parse(message.content);
      return `<pre><code>${JSON.stringify(parsed, null, 2)}</code></pre>`;
    } catch (e) {
      // 解析失败则返回原内容的Markdown渲染
      return renderMarkdown(message.content || '');
    }
  }

  // 否则渲染为Markdown
  try {
    const content = message.content || '';
    return renderMarkdown(content);
  } catch (error) {
    console.error('Markdown渲染失败:', error);
    return message.content || ''; // 渲染失败时显示原始内容
  }
};

// 获取用户头像，优先使用props传入的，如果没有则使用store中的
const actualUserAvatar = computed(() => {
  return props.userAvatar || userStore.avatar || 'https://api.dicebear.com/7.x/bottts/svg?seed=User';
});

// 获取用户名
const actualUserName = computed(() => {
  return userStore.username || '用户';
});

// 格式化JSON显示
const formatJson = (content) => {
  try {
    const parsed = JSON.parse(content || '{}');
    return JSON.stringify(parsed, null, 2);
  } catch (e) {
    return content || '';
  }
};

// 监听整个消息数组的变化
watch(() => props.messages, (newMessages, oldMessages) => {
  // 对比最后一条消息的内容和状态
  if (newMessages.length > 0 && oldMessages.length > 0) {
    const newLastMsg = newMessages[newMessages.length - 1];
    const oldLastMsg = oldMessages[oldMessages.length - 1];

    if (newLastMsg.role === 'assistant') {
      // 监控内容变化
      if (newLastMsg.content !== oldLastMsg?.content) {
        console.log(`最后消息内容已更新: ${oldLastMsg?.content?.length || 0} -> ${newLastMsg.content?.length || 0}`);
      }

      // 监控输入状态变化
      if (newLastMsg.isTyping !== oldLastMsg?.isTyping) {
        console.log(`最后消息isTyping状态变化: ${oldLastMsg?.isTyping} -> ${newLastMsg.isTyping}`);
      }
    }
  }
}, {deep: true});
</script>

<style scoped>
.message {
  display: flex;
  margin-bottom: 24px;
  position: relative;
}

.message.user {
  flex-direction: row-reverse;
  margin-left: 60px;
}

.message.assistant {
  margin-right: 60px;
}

.avatar-container {
  flex-shrink: 0;
  width: 40px;
  margin: 0 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background-color: #f3f4f6;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-wrapper {
  flex: 1;
  min-width: 0;
}

.user-message-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 14px;
}

.user .message-header {
  flex-direction: row-reverse;
}

.role-name {
  font-weight: 500;
  color: #374151;
}

.message-time {
  margin: 0 12px;
  color: #9ca3af;
  font-size: 12px;
}

.message-body {
  position: relative;
  padding: 12px 16px;
  border-radius: 12px;
  max-width: 100%;
}

.message-body.user {
  background-color: #4F46E5;
  color: white;
  border-top-right-radius: 4px;
}

.message-body.assistant {
  background-color: #f0f9ff;
  border-top-left-radius: 4px;
}

.message-content {
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: break-word;
}

.user .message-content a {
  color: #E0E7FF;
  text-decoration: underline;
}

.code-content {
  background-color: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  font-family: monospace;
  overflow-x: auto;
}

.message-body.user .code-content {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.code-content pre {
  margin: 0;
  white-space: pre-wrap;
}

.file-content {
  padding: 8px 0;
}

.file-item {
  display: flex;
  align-items: center;
  background-color: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  border: 1px dashed #cbd5e1;
}

.message-body.user .file-item {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}

.file-icon {
  margin-right: 12px;
  color: #64748b;
}

.message-body.user .file-icon {
  color: white;
}

.file-icon svg {
  width: 24px;
  height: 24px;
}

.file-name {
  color: #2563eb;
  text-decoration: none;
  font-weight: 500;
}

.message-body.user .file-name {
  color: #E0E7FF;
}

.file-name:hover {
  text-decoration: underline;
}

.typing-indicator {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
}

.typing-indicator span {
  height: 8px;
  width: 8px;
  margin: 0 2px;
  background-color: #3b82f6;
  border-radius: 50%;
  display: inline-block;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0% {
    transform: scale(1);
    opacity: 0.6;
  }
  20% {
    transform: scale(1.2);
    opacity: 1;
  }
  40%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
}

.reading-webpage-indicator {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: rgba(243, 244, 246, 0.8);
  border-radius: 8px;
  margin-top: 8px;
}

.reading-webpage-indicator .icon {
  margin-right: 8px;
  color: #4b5563;
}

.reading-webpage-indicator .icon svg {
  width: 16px;
  height: 16px;
}

.reading-webpage-indicator span {
  font-size: 14px;
  color: #4b5563;
}

/* 添加持续输入指示器样式 */
.still-typing-indicator {
  display: inline-flex;
  align-items: center;
  margin-top: 8px;
  padding: 4px 8px;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
}

.still-typing-indicator span {
  height: 6px;
  width: 6px;
  margin: 0 2px;
  background-color: #3b82f6;
  border-radius: 50%;
  display: inline-block;
  animation: typing 1.4s infinite ease-in-out both;
}

.still-typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.still-typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.still-typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}
</style>

<style>
/* 这里使用非scoped样式来确保markdown内容正确渲染 */
.markdown-body {
  font-size: 15px;
  line-height: 1.6;
  color: #374151;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-body h1 {
  font-size: 2em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #e0e0e0;
}

.markdown-body h2 {
  font-size: 1.5em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #e0e0e0;
}

.markdown-body h3 {
  font-size: 1.25em;
}

.markdown-body h4 {
  font-size: 1em;
}

.markdown-body p {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body code {
  font-family: SFMono-Regular, Consolas, "Liberation Mono", Menlo, monospace;
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(175, 184, 193, 0.2);
  border-radius: 6px;
}

.markdown-body pre {
  font-family: SFMono-Regular, Consolas, "Liberation Mono", Menlo, monospace;
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body pre code {
  background-color: transparent;
  padding: 0;
  margin: 0;
  border-radius: 0;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 2em;
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body li {
  margin-bottom: 4px;
}

.markdown-body a {
  color: #0969da;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

.markdown-body blockquote {
  padding: 0 1em;
  color: #57606a;
  border-left: 0.25em solid #d0d7de;
  margin: 0 0 16px 0;
}

.markdown-body blockquote p {
  margin-top: 0;
}

.markdown-body blockquote p:last-child {
  margin-bottom: 0;
}

.markdown-body table {
  display: block;
  width: 100%;
  overflow: auto;
  margin-top: 0;
  margin-bottom: 16px;
  border-spacing: 0;
  border-collapse: collapse;
}

.markdown-body table tr {
  background-color: #ffffff;
  border-top: 1px solid #d0d7de;
}

.markdown-body table tr:nth-child(2n) {
  background-color: #f6f8fa;
}

.markdown-body table th,
.markdown-body table td {
  padding: 6px 13px;
  border: 1px solid #d0d7de;
}

.markdown-body table th {
  font-weight: 600;
}

.markdown-body img {
  max-width: 100%;
  box-sizing: content-box;
  background-color: #ffffff;
  border-radius: 4px;
}

.markdown-body hr {
  height: 0.25em;
  padding: 0;
  margin: 24px 0;
  background-color: #d0d7de;
  border: 0;
}
</style>