<template>
  <div class="chat-input-container" v-if="showInput">
    <div class="input-wrapper">
      <!-- 工具提示助手 -->
      <tool-prompt-helper
          :user-input="message"
          :visible="showToolHelper"
          @close="showToolHelper = false"
          @insert-text="insertText"
          @select-suggestion="selectSuggestion"
      />

      <div class="input-box">
        <textarea
            ref="messageInput"
            v-model="message"
            @keydown="handleKeydown"
            @input="handleInput"
            @focus="handleFocus"
            @blur="handleBlur"
            placeholder="输入消息... (支持智能工具提示)"
            class="message-textarea"
            rows="1"
            :disabled="!isChatActive"
        ></textarea>

        <div class="input-actions">
          <div class="left-actions">
            <!-- 工具助手切换按钮 -->
            <button
                @click="toggleToolHelper"
                class="tool-btn"
                :class="{ active: showToolHelper }"
                title="工具助手"
            >
              <svg viewBox="0 0 1024 1024" fill="currentColor">
                <path
                    d="M924.8 625.7l-65.5-56c3.1-19 4.7-38.4 4.7-57.8s-1.6-38.8-4.7-57.8l65.5-56c10.1-8.6 13.8-22.6 9.3-35.2l-0.9-2.6c-18.1-50.5-44.9-96.9-79.7-137.9l-1.8-2.1c-8.6-10.1-22.5-13.9-35.1-9.5l-81.3 28.9c-30-24.6-63.5-44-99.7-57.6l-15.7-85c-2.4-13.1-12.7-23.3-25.8-25.7l-2.7-0.5c-52.1-9.4-106.9-9.4-159 0l-2.7 0.5c-13.1 2.4-23.4 12.6-25.8 25.7l-15.8 85.4c-35.9 13.6-69.2 32.9-99 57.4l-81.9-29.1c-12.5-4.4-26.5-0.7-35.1 9.5l-1.8 2.1c-34.8 41-61.6 87.5-79.7 137.9l-0.9 2.6c-4.5 12.5-0.8 26.5 9.3 35.2l66.3 56.6c-3.1 18.8-4.6 38-4.6 57.1 0 19.2 1.5 38.4 4.6 57.1L99 625.5c-10.1 8.6-13.8 22.6-9.3 35.2l0.9 2.6c18.1 50.4 44.9 96.9 79.7 137.9l1.8 2.1c8.6 10.1 22.5 13.9 35.1 9.5l81.9-29.1c29.8 24.5 63.1 43.9 99 57.4l15.8 85.4c2.4 13.1 12.7 23.3 25.8 25.7l2.7 0.5c26.1 4.7 52.8 7.1 79.5 7.1 26.7 0 53.5-2.4 79.5-7.1l2.7-0.5c13.1-2.4 23.4-12.6 25.8-25.7l15.7-85c36.2-13.6 69.7-32.9 99.7-57.6l81.3 28.9c12.5 4.4 26.5 0.7 35.1-9.5l1.8-2.1c34.8-41 61.6-87.5 79.7-137.9l0.9-2.6c4.5-12.5 0.8-26.5-9.3-35.2zM512 690c-98.2 0-178-79.8-178-178s79.8-178 178-178 178 79.8 178 178-79.8 178-178 178z"/>
              </svg>
            </button>

            <!-- 模式切换 -->
            <div class="mode-selector">
              <select v-model="currentMode" @change="handleModeChange" class="mode-select">
                <option value="webSearch">联网搜索</option>
                <option value="adminAgent">管理端智能体</option>
                <option value="mcpAgent">MCP智能体</option>
              </select>
            </div>
          </div>

          <div class="right-actions">
            <button @click="startRecording">{{ recordText }}</button>
            <button @click="stopRecording">停止录音</button>
            <button
                @click="sendMessage"
                :disabled="!canSend"
                class="send-btn"
                :class="{ disabled: !canSend }"
            >
              <svg viewBox="0 0 1024 1024" fill="currentColor">
                <path
                    d="M931.36 439.808L85.44 85.44c-16.832-7.04-36.288-2.624-48.448 11.008-12.16 13.632-15.104 33.216-7.36 48.832l184.512 371.776c2.112 4.224 6.144 7.36 10.88 8.448l271.104 67.712c4.736 1.152 8-4.224 6.72-8.832l-67.712-271.104c-1.088-4.736-4.224-8.768-8.448-10.88L55.808 117.76c-15.616-7.744-35.2-4.8-48.832 7.36-13.632 12.16-18.048 31.616-11.008 48.448l354.368 845.92c7.744 15.616 24.704 24.64 42.176 22.336 17.472-2.304 31.424-16.256 34.624-34.624l79.488-459.392c1.024-5.888-3.072-11.264-8.96-11.776l-459.392-79.488c-18.368-3.2-32.32-17.152-34.624-34.624-2.304-17.472 6.72-34.432 22.336-42.176z"/>
              </svg>

              <span>发送</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 快捷工具栏 -->
      <div class="quick-tools" v-if="showQuickTools">
        <button
            v-for="tool in quickTools"
            :key="tool.id"
            @click="selectQuickTool(tool)"
            class="quick-tool-btn"
        >
          <svg v-html="tool.icon" viewBox="0 0 1024 1024" fill="currentColor"></svg>
          <span>{{ tool.name }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import {ref, computed, nextTick, watch} from 'vue'
import ToolPromptHelper from './ToolPromptHelper.vue'
import {useXfAsr} from "@/utils/useXfAsr.js";

export default {
  name: 'ChatInput',
  components: {
    ToolPromptHelper
  },
  props: {
    showInput: {
      type: Boolean,
      default: true
    },
    mode: {
      type: String,
      default: 'webSearch'
    },
    isChatActive: {
      type: Boolean,
      default: true
    }
  },
  emits: ['send-message', 'mode-change', 'create-new-chat'],
  setup(props, {emit}) {
    const {startRecording, stopRecording, recordText, resultText} = useXfAsr();
    watch(()=>resultText.value,(newVal) => {
      console.log("语音新值：",newVal);
      message.value = message.value + newVal;
    })
    const messageInput = ref(null)
    const message = ref('')
    const currentMode = ref(props.mode)
    const showToolHelper = ref(false)
    const showQuickTools = ref(true)
    const isFocused = ref(false)

    // 快捷工具
    const quickTools = ref([
      {
        id: 'student-info',
        name: '学生查询',
        icon: '<path d="M858.5 763.6a374 374 0 0 0-80.6-119.5 375.63 375.63 0 0 0-119.5-80.6c-.4-.2-.8-.3-1.2-.5C719.5 518 760 444.7 760 362c0-137-111-248-248-248S264 225 264 362c0 82.7 40.5 156 102.8 201.1-.4.2-.8.3-1.2.5-44.8 18.9-85 46-119.5 80.6a375.63 375.63 0 0 0-80.6 119.5A371.7 371.7 0 0 0 136 901.8a8 8 0 0 0 8 8.2h60c4.4 0 7.9-3.5 8-7.8 2-77.2 33-149.5 87.8-204.3 56.7-56.7 132-87.9 212.2-87.9s155.5 31.2 212.2 87.9C779 752.7 810 825 812 902.2c.1 4.4 3.6 7.8 8 7.8h60a8 8 0 0 0 8-8.2c-1-47.8-10.9-94.3-29.5-138.2zM512 534c-45.9 0-89.1-17.9-121.6-50.4S340 407.9 340 362c0-45.9 17.9-89.1 50.4-121.6S466.1 190 512 190s89.1 17.9 121.6 50.4S684 316.1 684 362c0 45.9-17.9 89.1-50.4 121.6S557.9 534 512 534z"/>',
        template: '请查询学号为 [学号] 的学生信息'
      },
      {
        id: 'student-activity',
        name: '活动记录',
        icon: '<path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm176 259.9c0 8.8-7.2 16-16 16H368c-8.8 0-16-7.2-16-16V296c0-8.8 7.2-16 16-16h304c8.8 0 16 7.2 16 16v27.9z"/>',
        template: '请查询学号为 [学号] 的学生最近 [条数] 条活动记录'
      },
      {
        id: 'student-blog',
        name: '博客动态',
        icon: '<path d="M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zM400 776H168V248h232v528zm424 0H632V468h192v308zm0-372H632V248h192v156z"/>',
        template: '请查询学号为 [学号] 的学生最近 [条数] 条博客动态'
      },
      {
        id: 'student-comment',
        name: '评论记录',
        icon: '<path d="M573 421c-23.1 0-41 17.9-41 40s17.9 40 41 40c21.1 0 39-17.9 39-40s-17.9-40-39-40zM293 421c-23.1 0-41 17.9-41 40s17.9 40 41 40c21.1 0 39-17.9 39-40s-17.9-40-39-40z"/>',
        template: '请查询学号为 [学号] 的学生最近 [条数] 条评论'
      },
      {
        id: 'mood-analysis',
        name: '心理分析',
        icon: '<path d="M880 112H144c-17.7 0-32 14.3-32 32v736c0 17.7 14.3 32 32 32h736c17.7 0 32-14.3 32-32V144c0-17.7-14.3-32-32-32zM380 696c-22.1 0-40-17.9-40-40s17.9-40 40-40 40 17.9 40 40-17.9 40-40 40zm192-64c-22.1 0-40-17.9-40-40s17.9-40 40-40 40 17.9 40 40-17.9 40-40 40zm192-64c-22.1 0-40-17.9-40-40s17.9-40 40-40 40 17.9 40 40-17.9 40-40 40zM512 496c-88.4 0-160-71.6-160-160S423.6 176 512 176s160 71.6 160 160-71.6 160-160 160z"/>',
        template: '请分析学号为 [学号] 的学生心理状态'
      },
      {
        id: 'sport-record',
        name: '运动记录',
        icon: '<path d="M426.8 417.9L320 385.5l-25.4 25.4c-7 7-16.4 10.9-26.2 10.9s-19.2-3.9-26.2-10.9L164.7 333c-14.5-14.5-14.5-37.9 0-52.4l77.6-77.6c14.5-14.5 37.9-14.5 52.4 0l77.5 77.5c7 7 10.9 16.4 10.9 26.2 0 9.8-3.9 19.2-10.9 26.2L346.8 359l32.4 106.8z"/>',
        template: '请查询学号为 [学号] 的学生运动记录'
      },
      {
        id: 'student-circle',
        name: '关注圈子',
        icon: '<path d="M719.4 499.1l-296.1-215A15.9 15.9 0 0 0 398 297v430c0 13.1 14.8 20.5 25.3 12.9l296.1-215a15.9 15.9 0 0 0 0-25.8zm-257.6 134V390.9L628.5 512 461.8 633.1z"/>',
        template: '请查询学号为 [学号] 的学生关注的圈子'
      },
      {
        id: 'student-attention',
        name: '关注用户',
        icon: '<path d="M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zM768 574c0 4.4-3.6 8-8 8h-80c-4.4 0-8-3.6-8-8V342c0-4.4 3.6-8 8-8h80c4.4 0 8 3.6 8 8v232z"/>',
        template: '请查询学号为 [学号] 的学生关注的用户'
      },
      {
        id: 'student-like',
        name: '点赞文章',
        icon: '<path d="M885.9 533.7c16.8-22.2 26.1-49.4 26.1-77.7 0-44.9-25.1-87.4-65.5-111.1a67.67 67.67 0 0 0-34.3-9.3H572.4l6-122.9c1.4-29.7-9.1-57.9-29.5-79.4A106.62 106.62 0 0 0 471 99.9c-52 0-98 35-111.8 85.1l-85.9 311H144c-17.7 0-32 14.3-32 32v364c0 17.7 14.3 32 32 32h133.5l4.2 0.9c39.7 9.2 79.9 13.8 120.3 13.8 55.2 0 109.7-8.5 162-25.3l335.7-109.7c32.4-10.6 53.3-40.3 53.3-75.7 0-18.2-6.2-35.6-17.5-49.8z"/>',
        template: '请查询学号为 [学号] 的学生点赞的文章'
      },
      {
        id: 'file-export',
        name: '文件导出',
        icon: '<path d="M854.6 288.6L639.4 73.4c-6-6-14.1-9.4-22.6-9.4H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V311.3c0-8.5-3.4-16.7-9.4-22.7zM790.2 326H602V137.8L790.2 326zm1.8 562H232V136h302v216a42 42 0 0 0 42 42h216v494z"/>',
        template: '请将 [内容] 导出为 [格式] 格式的文件'
      },
      {
        id: 'export-test-result',
        name: '导出测评',
        icon: '<path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9L343.9 862a32 32 0 0 0 50.2 0L918.3 202.9c4.1-5.2.4-12.9-6.3-12.9z"/>',
        template: '请导出学号为 [学号] 的学生最近 [条数] 条测评结果'
      },
      {
        id: 'export-student-list',
        name: '导出学生',
        icon: '<path d="M880 112H144c-17.7 0-32 14.3-32 32v736c0 17.7 14.3 32 32 32h736c17.7 0 32-14.3 32-32V144c0-17.7-14.3-32-32-32zM380 696c-22.1 0-40-17.9-40-40s17.9-40 40-40 40 17.9 40 40-17.9 40-40 40z"/>',
        template: '请导出最近 [条数] 条学生列表'
      },
      {
        id: 'browser-operate',
        name: '浏览器操作',
        icon: '<path d="M928 161H96c-17.7 0-32 14.3-32 32v618c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V193c0-17.7-14.3-32-32-32zM328 193c13.3 0 24 10.7 24 24s-10.7 24-24 24-24-10.7-24-24 10.7-24 24-24z"/>',
        template: '请执行浏览器操作：[操作内容]'
      },
      {
        id: 'publish-article',
        name: '发布文章',
        icon: '<path d="M864 256H736v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.4 523c1.6 34.1 29.8 61 63.9 61h454.6c34.2 0 62.3-26.8 63.9-61l24.4-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32z"/>',
        template: '请发布一篇标题为"[标题]"的文章，描述："[描述]"，标签："[标签]"'
      },
      {
        id: 'web-crawler',
        name: '网页爬取',
        icon: '<path d="M880 112c-3.8 0-7.7.7-11.6 2.3L292 345.9H128c-8.8 0-16 7.4-16 16.6v299c0 9.2 7.2 16.6 16 16.6h101.6c-3.7 11.6-5.6 23.9-5.6 36.4 0 65.9 53.8 119.2 120 119.2s120-53.3 120-119.2c0-12.5-1.9-24.8-5.6-36.4H928c8.8 0 16-7.4 16-16.6V144.6c0-9.2-7.2-16.6-16-16.6H880z"/>',
        template: '请爬取网址 [网址] 的内容'
      },
      {
        id: 'map-query',
        name: '地图查询',
        icon: '<path d="M854.6 289.1L639.4 73.9c-6-6-14.1-9.4-22.6-9.4H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V311.3c0-8.5-3.4-16.6-9.4-22.6z"/>',
        template: '地图查询：[问题描述]'
      }
    ])

    // 计算是否可以发送
    const canSend = computed(() => {
      return props.isChatActive && message.value.trim().length > 0
    })

    // 监听模式变化
    watch(() => props.mode, (newMode) => {
      currentMode.value = newMode
    })

    // 处理输入
    const handleInput = () => {
      // 自动调整textarea高度
      if (messageInput.value) {
        messageInput.value.style.height = 'auto'
        messageInput.value.style.height = `${Math.min(messageInput.value.scrollHeight, 120)}px`
      }

      // 检查是否需要显示工具助手
      if (message.value.length > 0) {
        showToolHelper.value = true
      } else {
        showToolHelper.value = false
      }
    }

    // 处理键盘事件
    const handleKeydown = (event) => {
      if (event.key === 'Enter') {
        if (event.shiftKey) {
          // Shift + Enter 换行
          return
        } else {
          // Enter 发送消息
          event.preventDefault()
          sendMessage()
        }
      } else if (event.key === 'Escape') {
        // ESC 关闭工具助手
        showToolHelper.value = false
      }
    }

    // 处理焦点
    const handleFocus = () => {
      isFocused.value = true
      if (message.value.length > 1) {
        showToolHelper.value = true
      }
    }

    const handleBlur = () => {
      isFocused.value = false
      // 延迟关闭，以便用户可以点击工具助手
      setTimeout(() => {
        if (!isFocused.value) {
          showToolHelper.value = false
        }
      }, 200)
    }

// 发送消息
    const sendMessage = () => {
      console.log('sendMessage clicked', canSend.value)
      if (!canSend.value) return

      const messageToSend = message.value.trim()
      message.value = ''
      showToolHelper.value = false

      // 重置textarea高度
      if (messageInput.value) {
        messageInput.value.style.height = 'auto'
      }

      emit('send-message', messageToSend)
    }

    // 处理模式变化
    const handleModeChange = () => {
      emit('mode-change', currentMode.value)
    }

    // 切换工具助手
    const toggleToolHelper = () => {
      console.log('toggleToolHelper clicked', showToolHelper.value)
      showToolHelper.value = !showToolHelper.value
      if (showToolHelper.value) {
        messageInput.value?.focus()
      }
    }

    // 插入文本
    const insertText = (text) => {
      message.value = text
      showToolHelper.value = false
      nextTick(() => {
        messageInput.value?.focus()
        // 将光标移到末尾
        const textLength = message.value.length
        messageInput.value?.setSelectionRange(textLength, textLength)
      })
    }

    // 选择建议
    const selectSuggestion = (suggestion) => {
      insertText(suggestion.tool.template)
    }

    // 选择快捷工具
    const selectQuickTool = (tool) => {
      insertText(tool.template)
    }

    // 切换快捷工具栏
    const toggleQuickTools = () => {
      showQuickTools.value = !showQuickTools.value
    }

    return {
      recordText,
      resultText,
      messageInput,
      message,
      currentMode,
      showToolHelper,
      showQuickTools,
      isFocused,
      quickTools,
      canSend,
      startRecording,
      stopRecording,
      handleInput,
      handleKeydown,
      handleFocus,
      handleBlur,
      sendMessage,
      handleModeChange,
      toggleToolHelper,
      insertText,
      selectSuggestion,
      selectQuickTool,
      toggleQuickTools
    }
  }
}
</script>

<style scoped>
.chat-input-container {
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
  position: relative;
}

.input-wrapper {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

.input-box {
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  transition: all 0.2s;
  position: relative;
}

.input-box:focus-within {
  border-color: #3b82f6;
  background: #ffffff;
}

.message-textarea {
  width: 100%;
  border: none;
  background: transparent;
  resize: none;
  outline: none;
  padding: 12px 16px 12px 16px;
  font-size: 14px;
  line-height: 1.5;
  color: #1f2937;
  min-height: 44px;
  max-height: 120px;
  font-family: inherit;
}

.message-textarea::placeholder {
  color: #9ca3af;
}

.message-textarea:disabled {
  color: #9ca3af;
  cursor: not-allowed;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-top: 1px solid #e5e7eb;
}

.left-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-btn {
  background: none;
  border: none;
  padding: 6px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tool-btn svg {
  width: 18px;
  height: 18px;
  color: #6b7280;
}

.tool-btn:hover,
.tool-btn.active {
  background: #f3f4f6;
}

.tool-btn.active svg {
  color: #3b82f6;
}

.mode-selector {
  position: relative;
}

.mode-select {
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 4px 8px;
  font-size: 12px;
  color: #374151;
  cursor: pointer;
  outline: none;
  transition: all 0.2s;
}

.mode-select:hover,
.mode-select:focus {
  border-color: #3b82f6;
  background: #ffffff;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.send-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn:hover:not(.disabled) {
  background: #2563eb;
  transform: translateY(-1px);
}

.send-btn.disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.send-btn svg {
  width: 16px;
  height: 16px;
}

.quick-tools {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  padding: 8px 0;
  overflow-x: auto;
}

.quick-tool-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  flex-shrink: 0;
}

.quick-tool-btn:hover {
  background: #f3f4f6;
  border-color: #3b82f6;
  color: #3b82f6;
}

.quick-tool-btn svg {
  width: 14px;
  height: 14px;
}

/* 滚动条样式 */
.quick-tools::-webkit-scrollbar {
  height: 4px;
}

.quick-tools::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.quick-tools::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.quick-tools::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>