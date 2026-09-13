<script setup></script>
<template>
  <div class="ai-ppt">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>AI PPT 生成器</h1>
      <p>基于文多多AI技术，快速生成专业PPT</p>
    </div>

    <!-- 示例切换提示 -->
    <div class="demo-tips">
      <el-alert 
        title="使用说明" 
        type="info" 
        :closable="false"
        show-icon
      >
        <div>
          <p><strong>支持多种输入方式：</strong></p>
          <p>• 根据主题：直接输入PPT主题</p>
          <p>• 根据内容：粘贴文本内容</p>
          <p>• 根据文件：上传文档文件</p>
          <p>• 根据文件链接：提供可访问的文件URL</p>
          <p>• 导入大纲：上传大纲文件</p>
        </div>
      </el-alert>
    </div>

    <!-- 页面导航 -->
    <PageNavigation 
      :current-page="currentPage"
      @page-change="handlePageChange"
    />

    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 输入表单（仅在creator页面显示且未显示iframe时显示） -->
      <div v-if="currentPage === 'creator' && !showIframe" class="input-section">
        <PptInputForm 
          :loading="generating"
          @generate="handleGenerate"
          ref="inputFormRef"
        />
      </div>

      <!-- DocmeeUI iframe -->
      <div v-if="showIframe && token" class="iframe-section">
        <DocmeeUIFrame
          :token="token"
          :page="currentPage"
          :ppt-id="currentPptId"
          @token-invalid="handleTokenInvalid"
          @page-change="handleIframePageChange"
          @before-generate="handleBeforeGenerate"
          @before-download="handleBeforeDownload"
          @error="handleError"
          ref="docmeeUIRef"
        />
      </div>

      <!-- Token获取失败提示 -->
      <div v-if="!token && !tokenLoading" class="error-section">
        <el-result 
          icon="warning"
          title="无法获取访问令牌"
          sub-title="请检查API密钥配置是否正确"
        >
          <template #extra>
            <el-button type="primary" @click="initToken">重新获取Token</el-button>
          </template>
        </el-result>
      </div>

      <!-- Token加载中 -->
      <div v-if="tokenLoading" class="loading-section">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
        <div>正在获取访问令牌...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import PageNavigation from './components/PageNavigation.vue'
import PptInputForm from './components/PptInputForm.vue'
import DocmeeUIFrame from './components/DocmeeUIFrame.vue'
import { docmeeApiClient } from './utils/docmeeApiClient.js'

// 响应式数据
const currentPage = ref('creator')
const showIframe = ref(false)
const token = ref('')
const tokenLoading = ref(false)
const generating = ref(false)
const currentPptId = ref(null)

// 组件引用
const inputFormRef = ref(null)
const docmeeUIRef = ref(null)

// 初始化Token
const initToken = async () => {
  tokenLoading.value = true
  try {
    const newToken = await docmeeApiClient.createApiToken('vue_user', null)
    token.value = newToken
  } catch (error) {
    console.error('获取Token失败:', error)
    ElMessage.error('获取访问令牌失败: ' + error.message)
  } finally {
    tokenLoading.value = false
  }
}

// 页面切换处理
const handlePageChange = (page) => {
  currentPage.value = page
  if (page !== 'creator') {
    showIframe.value = true
  }
}

// iframe内页面切换处理
const handleIframePageChange = (page) => {
  currentPage.value = page
}

// 生成PPT处理
const handleGenerate = async (generateData) => {
  console.log('开始生成PPT:', generateData)
  
  generating.value = true
  showIframe.value = true
  
  try {
    let creatorData = {}
    
    switch (generateData.type) {
      case 'subject':
        // 根据主题
        creatorData.subject = generateData.data.subject
        break
        
      case 'text':
        // 根据内容
        creatorData.text = generateData.data.text
        break
        
      case 'file':
        // 根据文件
        const dataUrl = await docmeeApiClient.parseFileData(generateData.data.file, token.value)
        creatorData.dataUrl = dataUrl
        break
        
      case 'dataUrl':
        // 根据文件链接
        creatorData.dataUrl = generateData.data.dataUrl
        break
        
      case 'outline':
        // 导入大纲
        const outlineData = await docmeeApiClient.extractFileOutline(generateData.data.outlineFile, token.value)
        creatorData.outlineMarkdown = outlineData.outlineText
        creatorData.dataUrl = outlineData.dataUrl
        break
        
      default:
        throw new Error('未知的生成类型')
    }
    
    // 等待iframe加载完成后传输数据
    setTimeout(() => {
      if (docmeeUIRef.value) {
        docmeeUIRef.value.navigate({ page: 'creator' })
        docmeeUIRef.value.changeCreatorData(creatorData, true)
      }
    }, 1000)
    
  } catch (error) {
    console.error('生成PPT失败:', error)
    ElMessage.error('生成PPT失败: ' + error.message)
    showIframe.value = false
  } finally {
    generating.value = false
  }
}

// Token无效处理
const handleTokenInvalid = async () => {
  await initToken()
}

// 生成前处理
const handleBeforeGenerate = (data) => {
  console.log('生成前回调:', data)
  if (data.type === 'outline') {
    ElMessage.success('开始生成PPT大纲...')
  } else if (data.type === 'ppt') {
    ElMessage.success('开始生成PPT...')
  }
  return true
}

// 下载前处理
const handleBeforeDownload = (data) => {
  console.log('下载前回调:', data)
  const { id, subject } = data
  return `PPT_${subject || 'Document'}_${new Date().getTime()}.pptx`
}

// 错误处理
const handleError = (error) => {
  console.error('DocmeeUI错误:', error)
}

// 生命周期
onMounted(() => {
  // 检查是否在file协议下运行
  if (location.protocol === 'file:') {
    ElMessage.error('不支持file协议，请使用http服务访问')
    return
  }
  
  // 初始化Token
  initToken()
})
</script>

<style scoped>
.ai-ppt {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 40px 20px;
  background: linear-gradient(-157deg, #f57bb0, #867dea);
  color: white;
  border-radius: 12px;
}

.page-header h1 {
  margin: 0 0 10px 0;
  font-size: 2.5rem;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  font-size: 1.1rem;
  opacity: 0.9;
}

.demo-tips {
  margin-bottom: 20px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.demo-tips :deep(.el-alert__content) p {
  margin: 5px 0;
}

.content-area {
  max-width: 1200px;
  margin: 0 auto;
}

.input-section {
  margin-bottom: 30px;
}

.iframe-section {
  min-height: 600px;
}

.error-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.loading-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  color: #666;
  font-size: 16px;
  gap: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-ppt {
    padding: 10px;
  }
  
  .page-header {
    padding: 30px 15px;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
  
  .page-header p {
    font-size: 1rem;
  }
}
</style>