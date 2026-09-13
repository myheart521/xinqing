<template>
  <div class="history-section">
    <div class="section-header">
      <h3>历史会话</h3>
      <button class="toggle-btn" @click="toggleHistoryView">
        {{ showAllHistory ? '收起' : '查看全部' }}
      </button>
    </div>
    
    <div v-if="displayChats && displayChats.length > 0" class="history-list">
      <li
        v-for="chat in displayChats"
        :key="chat.memoryId || chat.id"
        class="nav-item history-item"
        @click="loadHistory(chat)"
      >
        <div class="sidebar-icon-wrapper">
<!--          <svg class="icon" viewBox="0 0 1024 1024" fill="currentColor">-->
<!--            <path d="M832 64H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V96c0-17.7-14.3-32-32-32zm-260 72h96v209.9L621.5 312 572 347.4V136zm220 752H232V136h280v296.9c0 3.3 1 6.6 3 9.3 5.1 7.2 15 8.9 22.2 3.7l83.5-59.4 81.8 59.4c2 1.5 4.4 2.3 6.8 2.3 6.8 0 12.8-4.7 14.3-11.3.8-3.4.3-7-1.2-10.3V136h96v752z"/>-->
<!--          </svg>-->
          <span class="history-title">{{ chat.title || '未命名会话' }}</span>
        </div>
        <div class="history-time">{{ formatTime(chat.startTime || chat.updateTime) }}</div>
      </li>
    </div>
    
    <div v-else class="empty-history">
      <div class="empty-icon">
        <svg viewBox="0 0 1024 1024" fill="currentColor">
          <path d="M832 64H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V96c0-17.7-14.3-32-32-32zm-260 72h96v209.9L621.5 312 572 347.4V136zm220 752H232V136h280v296.9c0 3.3 1 6.6 3 9.3 5.1 7.2 15 8.9 22.2 3.7l83.5-59.4 81.8 59.4c2 1.5 4.4 2.3 6.8 2.3 6.8 0 12.8-4.7 14.3-11.3.8-3.4.3-7-1.2-10.3V136h96v752z"/>
        </svg>
      </div>
      <div class="empty-text">暂无历史会话</div>
    </div>
  </div>
</template>

<script>
import { formatTime } from '@/utils/timeUtils.js';

export default {
  name: 'SidebarHistory',
  props: {
    historyChats: {
      type: Array,
      default: () => []
    },
    displayChats: {
      type: Array,
      default: () => []
    },
    showAllHistory: {
      type: Boolean,
      default: false
    }
  },
  emits: ['load-history', 'toggle-history-view'],
  setup(props, { emit }) {
    // 加载历史会话
    const loadHistory = (chat) => {
      emit('load-history', chat);
    };

    // 切换历史视图
    const toggleHistoryView = () => {
      emit('toggle-history-view');
    };

    return {
      formatTime,
      loadHistory,
      toggleHistoryView
    };
  }
};
</script>

<style scoped>
.history-section {
  margin-top: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 12px 8px;
  margin-bottom: 4px;
}

.section-header h3 {
  font-size: 15px;
  font-weight: 500;
  color: #4b5563;
  margin: 0;
}

.toggle-btn {
  background: none;
  border: none;
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
}

.toggle-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #374151;
}

.history-list {
  margin-top: 4px;
}

.history-item {
  position: relative;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-bottom: 2px;
  display: flex;
  flex-direction: column;
}

.history-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.history-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #374151;
  flex: 1;
}

.history-time {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 2px;
  margin-left: 26px;
}

.empty-history {
  padding: 24px 16px;
  text-align: center;
  color: #9ca3af;
}

.empty-icon {
  margin-bottom: 12px;
}

.empty-icon svg {
  width: 32px;
  height: 32px;
  opacity: 0.5;
}

.empty-text {
  font-size: 14px;
}
</style>