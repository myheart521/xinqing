<template>
  <div class="docmee-ui-frame">
    <div 
      ref="containerRef" 
      class="iframe-container"
      :style="containerStyle"
    ></div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <div class="loading-text">正在加载...</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  token: {
    type: String,
    required: true
  },
  page: {
    type: String,
    default: 'creator'
  },
  pptId: {
    type: String,
    default: null
  },
  background: {
    type: String,
    default: 'linear-gradient(-157deg, #f57bb0, #867dea)'
  },
  padding: {
    type: String,
    default: '40px 20px 0px'
  },
  lang: {
    type: String,
    default: 'zh'
  },
  mode: {
    type: String,
    default: 'light'
  },
  isMobile: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits([
  'message',
  'page-change',
  'token-invalid',
  'before-generate',
  'before-download',
  'before-create-custom-template',
  'error'
])

// 响应式数据
const containerRef = ref(null)
const loading = ref(true)
const docmeeUI = ref(null)

// 计算属性
const containerStyle = computed(() => ({
  background: props.background,
  padding: props.padding
}))

// 监听器
watch(() => props.token, (newToken) => {
  if (docmeeUI.value && newToken) {
    docmeeUI.value.updateToken(newToken)
  }
})

watch(() => props.page, (newPage) => {
  if (docmeeUI.value) {
    docmeeUI.value.navigate({ page: newPage, pptId: props.pptId })
  }
})

// 消息处理函数
const handleMessage = (message) => {
  console.log('DocmeeUI消息:', message)
  
  switch (message.type) {
    case 'mounted':
    case 'user-info':
      loading.value = false
      break
      
    case 'invalid-token':
      emit('token-invalid', message)
      ElMessage.error('Token认证失败，请重新获取')
      break
      
    case 'beforeGenerate':
      const { subtype, fields } = message.data
      if (subtype === 'outline') {
        console.log('即将生成PPT大纲', fields)
        emit('before-generate', { type: 'outline', fields })
        return true
      } else if (subtype === 'ppt') {
        console.log('即将生成PPT', fields)
        emit('before-generate', { type: 'ppt', fields })
        return true
      }
      break
      
    case 'beforeCreateCustomTemplate':
      const { file, totalPptCount } = message.data
      console.log('用户自定义完整模版，PPT文件：', file.name)
      const result = emit('before-create-custom-template', { file, totalPptCount })
      return result !== false // 默认允许
      
    case 'pageChange':
      emit('page-change', message.data.page)
      break
      
    case 'beforeDownload':
      const downloadResult = emit('before-download', message.data)
      return downloadResult || `PPT_${new Date().getTime()}.pptx`
      
    case 'error':
      if (message.data.code === 88) {
        ElMessage.error('您的次数已用完')
      } else {
        ElMessage.error(`发生错误：${message.data.message}`)
      }
      emit('error', message.data)
      break
      
    default:
      emit('message', message)
      break
  }
}

// 初始化DocmeeUI
const initDocmeeUI = () => {
  if (!window.DocmeeUI) {
    console.error('DocmeeUI未加载，请确保引入了docmee-ui-sdk-iframe.min.js')
    return
  }
  
  if (!props.token) {
    console.error('Token不能为空')
    return
  }
  
  try {
    docmeeUI.value = new window.DocmeeUI({
      token: props.token,
      container: containerRef.value,
      page: props.page,
      pptId: props.pptId,
      lang: props.lang,
      mode: props.mode,
      isMobile: props.isMobile,
      background: props.background,
      padding: props.padding,
      onMessage: handleMessage
    })
    
    console.log('DocmeeUI初始化成功')
  } catch (error) {
    console.error('DocmeeUI初始化失败:', error)
    ElMessage.error('UI组件初始化失败')
    loading.value = false
  }
}

// 公共方法
const navigate = (options) => {
  if (docmeeUI.value) {
    docmeeUI.value.navigate(options)
  }
}

const changeCreatorData = (data, createNow = false) => {
  if (docmeeUI.value) {
    docmeeUI.value.changeCreatorData(data, createNow)
  }
}

const updateTemplate = (templateId) => {
  if (docmeeUI.value) {
    docmeeUI.value.updateTemplate(templateId)
  }
}

const showTemplateDialog = (type = 'system') => {
  if (docmeeUI.value) {
    docmeeUI.value.showTemplateDialog(type)
  }
}

const getCurrentPptInfo = () => {
  if (docmeeUI.value) {
    docmeeUI.value.getCurrentPptInfo()
  }
}

const sendMessage = (message) => {
  if (docmeeUI.value) {
    docmeeUI.value.sendMessage(message)
  }
}

const getInfo = () => {
  if (docmeeUI.value) {
    docmeeUI.value.getInfo()
  }
}

const destroy = () => {
  if (docmeeUI.value) {
    docmeeUI.value.destroy()
    docmeeUI.value = null
  }
}

// 生命周期
onMounted(() => {
  // 检查DocmeeUI是否已加载
  if (window.DocmeeUI) {
    initDocmeeUI()
  } else {
    // 动态加载SDK
    const script = document.createElement('script')
    script.src = '/docmee-ui-sdk-iframe.min.js'
    script.onload = () => {
      initDocmeeUI()
    }
    script.onerror = () => {
      console.error('加载DocmeeUI SDK失败')
      ElMessage.error('加载UI组件失败')
      loading.value = false
    }
    document.head.appendChild(script)
  }
})

onUnmounted(() => {
  destroy()
})

// 暴露方法
defineExpose({
  navigate,
  changeCreatorData,
  updateTemplate,
  showTemplateDialog,
  getCurrentPptInfo,
  sendMessage,
  getInfo,
  destroy
})
</script>

<style scoped>
.docmee-ui-frame {
  position: relative;
  width: 100%;
  height: 600px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.iframe-container {
  width: 100%;
  height: 100%;
  border-radius: 12px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
}

.loading-text {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}
</style> 