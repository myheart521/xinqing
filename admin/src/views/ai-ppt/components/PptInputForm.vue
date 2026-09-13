<template>
  <div class="ppt-input-form">
    <div class="input-section">
      <el-select 
        v-model="selectType" 
        @change="handleTypeChange" 
        placeholder="选择输入方式"
        style="margin-bottom: 20px; width: 200px;"
      >
        <el-option label="根据主题" value="subject" />
        <el-option label="根据内容" value="text" />
        <el-option label="根据文件" value="file" />
        <el-option label="根据文件链接" value="dataUrl" />
        <el-option label="导入大纲" value="outline" />
      </el-select>

      <!-- 根据主题 -->
      <div v-if="selectType === 'subject'" class="input-item">
        <el-input
          v-model="formData.subject"
          type="textarea"
          :rows="3"
          placeholder="请输入PPT主题，例如：AI未来的发展"
          maxlength="200"
          show-word-limit
        />
      </div>

      <!-- 根据内容 -->
      <div v-if="selectType === 'text'" class="input-item">
        <el-input
          v-model="formData.text"
          type="textarea"
          :rows="5"
          placeholder="请粘贴文本内容"
          maxlength="6000"
          show-word-limit
        />
      </div>

      <!-- 根据文件 -->
      <div v-if="selectType === 'file'" class="input-item">
        <el-upload
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          accept=".doc,.docx,.xls,.xlsx,.pdf,.ppt,.pptx,.txt,.md"
        >
          <template #trigger>
            <el-button type="primary">选择文件</el-button>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              支持 .doc, .docx, .xls, .xlsx, .pdf, .ppt, .pptx, .txt, .md 格式
            </div>
          </template>
        </el-upload>
      </div>

      <!-- 根据文件链接 -->
      <div v-if="selectType === 'dataUrl'" class="input-item">
        <el-input
          v-model="formData.dataUrl"
          placeholder="请输入文件链接（公网可访问的URL）"
        />
      </div>

      <!-- 导入大纲 -->
      <div v-if="selectType === 'outline'" class="input-item">
        <el-upload
          :auto-upload="false"
          :limit="1"
          :on-change="handleOutlineFileChange"
          :on-remove="handleOutlineFileRemove"
          accept=".md,.doc,.docx,.xmind,.mm"
        >
          <template #trigger>
            <el-button type="primary">选择大纲文件</el-button>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              支持 .md, .doc, .docx, .xmind, .mm 格式
            </div>
          </template>
        </el-upload>
      </div>

      <div class="form-actions">
        <el-button 
          type="primary" 
          :loading="loading"
          :disabled="!canGenerate"
          @click="handleGenerate"
        >
          生成PPT
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

// Props
const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['generate'])

// 响应式数据
const selectType = ref('subject')
const formData = reactive({
  subject: '',
  text: '',
  file: null,
  dataUrl: '',
  outlineFile: null
})

// 计算属性
const canGenerate = computed(() => {
  switch (selectType.value) {
    case 'subject':
      return formData.subject.trim().length > 0
    case 'text':
      return formData.text.trim().length > 0
    case 'file':
      return formData.file !== null
    case 'dataUrl':
      return formData.dataUrl.trim().length > 0 && formData.dataUrl.startsWith('http')
    case 'outline':
      return formData.outlineFile !== null
    default:
      return false
  }
})

// 方法
const handleTypeChange = () => {
  // 切换类型时清空表单数据
  Object.assign(formData, {
    subject: '',
    text: '',
    file: null,
    dataUrl: '',
    outlineFile: null
  })
}

const handleFileChange = (uploadFile) => {
  formData.file = uploadFile.raw
}

const handleFileRemove = () => {
  formData.file = null
}

const handleOutlineFileChange = (uploadFile) => {
  formData.outlineFile = uploadFile.raw
}

const handleOutlineFileRemove = () => {
  formData.outlineFile = null
}

const handleGenerate = () => {
  if (!canGenerate.value) {
    ElMessage.warning('请完善输入信息')
    return
  }

  if (selectType.value === 'dataUrl' && !formData.dataUrl.startsWith('http')) {
    ElMessage.warning('文件链接格式错误，请输入有效的URL')
    return
  }

  // 发送生成事件
  emit('generate', {
    type: selectType.value,
    data: { ...formData }
  })
}

// 重置表单
const resetForm = () => {
  selectType.value = 'subject'
  Object.assign(formData, {
    subject: '',
    text: '',
    file: null,
    dataUrl: '',
    outlineFile: null
  })
}

// 暴露方法
defineExpose({
  resetForm
})
</script>

<style scoped>
.ppt-input-form {
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 20px;
}

.input-section {
  max-width: 600px;
  margin: 0 auto;
}

.input-item {
  margin-bottom: 20px;
}

.form-actions {
  text-align: center;
  margin-top: 20px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
</style> 