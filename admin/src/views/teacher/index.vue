<template>

  <div class="chat-container">
    <!-- 左侧学生列表 -->
    <div class="student-list">
      <student-list
          :students="studentList"
          @select-student="handleSelectStudent"
      />
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <!-- 聊天区头部 -->
      <div class="chat-header">
        <div class="student-info">
          <span>{{ currentStudent?.name || '请选择学生' }}</span>
        </div>
        <div class="action-buttons">
          <el-button
              type="primary"
              size="small"
              icon="el-icon-video-camera"
              @click="startVideoChat"
          >视频通话</el-button>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <teacher-chat
          ref="teacherChat"
          :messages="messages"
          :current-student="currentStudent"
          @send-message="handleSendMessage"
      />

      <!-- 视频聊天弹窗 -->
      <el-dialog
          title="视频通话"
          :visible.sync="showVideoChat"
          width="80%"
          :before-close="handleCloseVideo"
      >
        <video-chat
            v-if="showVideoChat"
            :student-id="currentStudent?.id"
            @close="handleCloseVideo"
        />
      </el-dialog>
    </div>
  </div>
</template>

<script>
import StudentList from '@/components/chat/StudentList.vue'
import TeacherChat from '@/components/chat/TeacherChat.vue'
import VideoChat from '@/components/chat/videochat.vue'

export default {
  name: 'TeacherView',
  components: {
    StudentList,
    TeacherChat,
    VideoChat
  },
  data() {
    return {
      studentList: [], // 学生列表
      currentStudent: null, // 当前选中的学生
      messages: [], // 聊天消息
      showVideoChat: false // 是否显示视频聊天
    }
  },
  methods: {
    // 处理选择学生
    handleSelectStudent(student) {
      this.currentStudent = student
      this.loadChatHistory(student.id)
    },
    // 加载聊天记录
    async loadChatHistory(studentId) {
      // TODO: 调用API获取与该学生的聊天记录
      this.messages = []
    },
    // 处理发送消息
    handleSendMessage(message) {
      if (!this.currentStudent) return

      const newMessage = {
        id: Date.now(),
        content: message,
        type: 'text',
        sender: 'teacher',
        timestamp: new Date().toISOString()
      }

      this.messages.push(newMessage)
      // TODO: 调用API发送消息
    },
    // 开始视频聊天
    startVideoChat() {
      if (!this.currentStudent) {
        this.$message.warning('请先选择学生')
        return
      }
      this.showVideoChat = true
    },
    // 关闭视频聊天
    handleCloseVideo() {
      this.showVideoChat = false
    }
  },
  async created() {
    // TODO: 获取学生列表
    this.studentList = []
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
}

.student-list {
  width: 250px;
  border-right: 1px solid #e0e0e0;
  background-color: #fff;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.student-info {
  font-size: 16px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

/* 适配Element UI的弹窗样式 */
:deep(.el-dialog__body) {
  padding: 0;
}
</style>
