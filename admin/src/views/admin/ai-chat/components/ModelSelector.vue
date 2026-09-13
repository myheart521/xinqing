<template>
  <div class="model-selector">
    <div class="mode-tabs">
      <button 
        v-for="option in modeOptions" 
        :key="option.value"
        :class="['mode-tab', { active: mode === option.value }]"
        @click="changeMode(option.value)"
      >
        <div class="tab-icon">
          <component :is="option.icon" />
        </div>
        <span>{{ option.label }}</span>
      </button>
    </div>
    
    <div class="right-buttons">
      <button class="new-chat-button" @click="createNewChat">
        <svg viewBox="0 0 1024 1024" fill="currentColor">
          <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm192 472c0 4.4-3.6 8-8 8H544v152c0 4.4-3.6 8-8 8h-48c-4.4 0-8-3.6-8-8V544H328c-4.4 0-8-3.6-8-8v-48c0-4.4 3.6-8 8-8h152V328c0-4.4 3.6-8 8-8h48c4.4 0 8 3.6 8 8v152h152c4.4 0 8 3.6 8 8v48z"/>
        </svg>
        <span>新对话</span>
      </button>
    </div>
  </div>
</template>

<script>
import { defineComponent, h } from 'vue';

// 定义图标组件
const WebSearchIcon = () => h('svg', {
  viewBox: '0 0 1024 1024',
  fill: 'currentColor'
}, [
  h('path', { d: 'M909.6 854.5L649.9 594.8C690.2 542.7 712 479 712 412c0-80.2-31.3-155.4-87.9-212.1-56.6-56.7-132-87.9-212.1-87.9s-155.5 31.3-212.1 87.9C143.2 256.5 112 331.8 112 412c0 80.1 31.3 155.5 87.9 212.1C256.5 680.8 331.8 712 412 712c67 0 130.6-21.8 182.7-62l259.7 259.6c3.2 3.2 8.4 3.2 11.6 0l43.6-43.5c3.2-3.2 3.2-8.4 0-11.6zM570.4 570.4C528 612.7 471.8 636 412 636s-116-23.3-158.4-65.6C211.3 528 188 471.8 188 412s23.3-116.1 65.6-158.4C296 211.3 352.2 188 412 188s116.1 23.2 158.4 65.6S636 352.2 636 412s-23.3 116.1-65.6 158.4z' })
]);

const AdminAgentIcon = () => h('svg', {
  viewBox: '0 0 1024 1024',
  fill: 'currentColor'
}, [
  h('path', { d: 'M832 64H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V96c0-17.7-14.3-32-32-32zm-600 72h560v208H232V136zm560 480H232V408h560v208zm0 272H232V680h560v208z' })
]);

const MCPIcon = () => h('svg', {
  viewBox: '0 0 1024 1024',
  fill: 'currentColor'
}, [
  h('path', { d: 'M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zM338 304c35.3 0 64 28.7 64 64s-28.7 64-64 64-64-28.7-64-64 28.7-64 64-64zm513.9 437.9H172.1V523.8c0-13.1 9.7-23.8 21.6-23.8h96.6c11.9 0 23.4 10.2 23.4 23.8v80.1h96.7V523.8c0-13.1 11.6-23.8 23.5-23.8h95.7c11.9 0 21.7 10.2 21.7 23.8v80.1h97.6V523.8c0-13.1 10.1-23.8 22-23.8h97c11.9 0 21.5 10.2 21.5 23.8v218.1z' })
]);

export default defineComponent({
  name: 'ModelSelector',
  props: {
    mode: {
      type: String,
      default: 'webSearch'
    }
  },
  emits: ['mode-change', 'create-new-chat'],
  setup(props, { emit }) {
    // 模式选项定义
    const modeOptions = [
      { 
        label: '联网搜索', 
        value: 'webSearch', 
        icon: WebSearchIcon 
      },
      { 
        label: '管理端智能体', 
        value: 'adminAgent', 
        icon: AdminAgentIcon 
      },
      { 
        label: 'MCP智能体', 
        value: 'adminMCP', 
        icon: MCPIcon 
      }
    ];

    // 切换模式
    const changeMode = (newMode) => {
      if (newMode !== props.mode) {
        emit('mode-change', newMode);
      }
    };

    // 创建新对话
    const createNewChat = () => {
      emit('create-new-chat');
    };

    return {
      modeOptions,
      changeMode,
      createNewChat
    };
  }
});
</script>

<style scoped>
.model-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.mode-tabs {
  display: flex;
  border-radius: 8px;
  background-color: #f3f4f6;
  padding: 4px;
  gap: 4px;
}

.mode-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 6px;
  background-color: transparent;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: #4b5563;
  transition: all 0.2s;
}

.mode-tab:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.mode-tab.active {
  background-color: #ffffff;
  color: #3b82f6;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.tab-icon {
  display: flex;
  align-items: center;
}

.tab-icon svg {
  width: 16px;
  height: 16px;
}

.right-buttons {
  display: flex;
  gap: 8px;
}

.new-chat-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 6px;
  background-color: #3b82f6;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.new-chat-button:hover {
  background-color: #2563eb;
  transform: translateY(-1px);
}

.new-chat-button svg {
  width: 16px;
  height: 16px;
}
</style>