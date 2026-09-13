<script setup>
import { ref, reactive, onMounted } from 'vue'
import navbar from "@/components/navbar.vue"
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnImageUpload from '@tuniao/tnui-vue3-uniapp/components/image-upload/src/image-upload.vue'
import { getFeedback } from '@/service/api/typeController'
import { create } from '@/service/api/feedbackController'
import { useUserStore } from '@/stores/user'
import {requestUrl} from "@/utils/URL";

const userStore = useUserStore()

// 反馈类型选项
const feedbackTypes = ref([])
const isLoading = ref(false)

// 表单数据
const formData = reactive({
  typeId: '',
  content: '',
  phone: '13000000000',
  imageList: []
})

// 图片上传相关配置
const imageUploadRef = ref(null)
const fileList = ref([])
const uploadedImages = ref([])
// 上传图片的url地址
const action = `${requestUrl}/common/upload`
// 上传图片的表单数据
const imageFormData = reactive({
  apiType: 'this,ali',
  token: userStore.token
})
// 上传相关配置
const autoUpload = true
const showProgress = true
const maxCount = 3
const disabled = false

// 验证状态
const isSubmitting = ref(false)
const contentLength = ref(0)

// 获取反馈类型
const getFeedbackTypes = async () => {
  try {
    const res = await getFeedback()
    if (res && res.code === 1 && res.data) {
      feedbackTypes.value = res.data || []

      // 设置默认选中第一个类型
      if (feedbackTypes.value.length > 0) {
        formData.typeId = feedbackTypes.value[0].id.toString()
      }
    } else {
      uni.showToast({
        title: '获取反馈类型失败',
        icon: 'none'
      })
      // 使用默认类型作为备选
      feedbackTypes.value = [
        {id: 1, name: '功能异常'},
        {id: 2, name: '体验问题'},
        {id: 3, name: '内容建议'},
        {id: 4, name: '其他问题'}
      ]
      formData.typeId = '1'
    }
  } catch (error) {
    console.error('获取反馈类型失败:', error)
    // 使用默认类型作为备选
    feedbackTypes.value = [
      {id: 1, name: '功能异常'},
      {id: 2, name: '体验问题'},
      {id: 3, name: '内容建议'},
      {id: 4, name: '其他问题'}
    ]
    formData.typeId = '1'
  }
}

// 监听内容变化
const handleContentInput = (e) => {
  contentLength.value = e.detail.value.length
  formData.content = e.detail.value
}

// 自定义上传回调函数
const customUploadCallback = (data) => {
  try {
    const res = JSON.parse(data.data)
    console.log('上传回调', res)
    if (res.code === 1 && res.data) {
      // 将上传成功的图片地址添加到数组中
      uploadedImages.value.push(res.data)
      return res.data
    } else {
      uni.showToast({
        title: '上传失败',
        icon: 'none'
      })
      return Promise.reject(new Error('上传失败'))
    }
  } catch (e) {
    uni.showToast({
      title: '上传失败',
      icon: 'none'
    })
    return Promise.reject(e)
  }
}

// 清空图片上传列表
const clearUpload = () => {
  if (imageUploadRef.value) {
    imageUploadRef.value.clear()
    uploadedImages.value = []
    fileList.value = []
  }
}

// 提交表单
const submitFeedback = async () => {
  // 表单验证
  if (!formData.typeId) {
    uni.showToast({
      title: '请选择反馈类型',
      icon: 'none'
    })
    return
  }

  if (!formData.content.trim()) {
    uni.showToast({
      title: '请填写问题描述',
      icon: 'none'
    })
    return
  }

  isSubmitting.value = true

  try {
    // 提交反馈
    const feedbackData = {
      typeId: formData.typeId,
      content: formData.content,
      phone: formData.phone,
      imageList: uploadedImages.value
    }

    const res = await create(feedbackData)

    if (res && res.code === 1) {
      uni.showToast({
        title: '反馈提交成功',
        icon: 'success'
      })

      // 重置表单
      formData.content = ''
      formData.phone = '13000000000'
      uploadedImages.value = []
      fileList.value = []
      contentLength.value = 0

      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      uni.showToast({
        title: res?.msg || '提交失败，请稍后再试',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('提交反馈失败:', error)
    uni.showToast({
      title: '提交失败，请稍后再试',
      icon: 'none'
    })
  } finally {
    isSubmitting.value = false
  }
}

// 页面加载时获取反馈类型
onMounted(() => {
  getFeedbackTypes()
})
</script>

<template>
  <view class="feedback-container">
    <!-- 导航栏 -->
    <navbar title="问题反馈"></navbar>

    <!-- 表单内容 -->
    <view class="form-container">
      <!-- 反馈类型 -->
      <view class="form-item">
        <view class="form-label">
          <text class="required">*</text>
          <text>反馈类型</text>
        </view>
        <view class="type-options">
          <view
              v-for="type in feedbackTypes"
              :key="type.id"
              class="type-option"
              :class="{ 'type-selected': formData.typeId === type.id.toString() }"
              @tap="formData.typeId = type.id.toString()"
          >
            <text>{{ type.name }}</text>
          </view>
        </view>
      </view>

      <!-- 问题描述 -->
      <view class="form-item">
        <view class="form-label">
          <text class="required">*</text>
          <text>问题描述</text>
        </view>
        <view class="textarea-box">
          <textarea
              class="feedback-textarea"
              placeholder="请详细描述您遇到的问题，以便我们更好地帮助您解决问题"
              placeholder-class="placeholder"
              :value="formData.content"
              @input="handleContentInput"
              maxlength="500"
          ></textarea>
          <view class="word-count">{{ contentLength }}/500</view>
        </view>
      </view>

      <!-- 上传图片 -->
      <view class="form-item">
        <view class="form-label-area">
          <view class="form-label">
            <text>问题截图（选填，最多3张）</text>
          </view>
          <view class="text-df tn-gray_text" @tap="clearUpload">
            <text class="tn-p-xs">清空上传</text>
            <TnIcon name="delete"></TnIcon>
          </view>
        </view>

        <!-- 使用TnImageUpload组件 -->
        <view class="image-upload-container">
          <TnImageUpload
              ref="imageUploadRef"
              v-model="fileList"
              :action="action"
              :form-data="imageFormData"
              :disabled="disabled"
              :auto-upload="autoUpload"
              :limit="maxCount"
              :show-upload-progress="showProgress"
              :custom-upload-callback="customUploadCallback"
          />
        </view>
      </view>

      <!-- 联系方式 -->
      <view class="form-item">
        <view class="form-label">
          <text>联系方式（选填）</text>
        </view>
        <input
            class="contact-input"
            placeholder="请留下您的联系方式，方便我们联系您"
            placeholder-class="placeholder"
            v-model="formData.phone"
        />
      </view>

      <!-- 提交按钮 -->
      <view class="submit-area">
        <TnButton
            bg-color="#5677fc"
            width="90%"
            height="90rpx"
            :loading="isSubmitting"
            :disabled="isSubmitting"
            @tap="submitFeedback"
        >
          <text class="tn-white_text">提交反馈</text>
        </TnButton>
      </view>

      <!-- 提示信息 -->
      <view class="tips-area">
        <view class="tips-item tn-flex tn-flex-row-center">
          <TnIcon name="info-circle" color="#5677fc" size="30rpx"></TnIcon>
          <text class="tn-ml-xs">您的反馈将发送给管理员</text>
        </view>
        <view class="tips-item tn-flex tn-flex-row-center">
          <TnIcon name="info-circle" color="#5677fc" size="30rpx"></TnIcon>
          <text class="tn-ml-xs">我们会尽快处理您的反馈，感谢您的支持！</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.feedback-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 100rpx;
}

.form-container {
  padding: 30rpx;
}

.form-item {
  margin-bottom: 40rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .form-label {
    font-size: 28rpx;
    color: #333333;
    margin-bottom: 20rpx;
    font-weight: bold;

    .required {
      color: #ff0000;
      margin-right: 6rpx;
    }
  }
}

.form-label-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;

  .text-df {
    font-size: 24rpx;
    cursor: pointer;
  }
}

.type-options {
  display: flex;
  flex-wrap: wrap;

  .type-option {
    padding: 16rpx 30rpx;
    background-color: #f5f5f5;
    border-radius: 8rpx;
    margin-right: 20rpx;
    margin-bottom: 20rpx;
    font-size: 26rpx;
    color: #666666;
    transition: all 0.3s;

    &.type-selected {
      background-color: rgba(86, 119, 252, 0.1);
      color: #5677fc;
      border: 1rpx solid #5677fc;
    }
  }
}

.textarea-box {
  position: relative;

  .feedback-textarea {
    width: 100%;
    height: 300rpx;
    background-color: #f8f8f8;
    border-radius: 8rpx;
    padding: 20rpx;
    font-size: 28rpx;
    color: #333333;
    box-sizing: border-box;
  }

  .word-count {
    position: absolute;
    right: 20rpx;
    bottom: 20rpx;
    font-size: 24rpx;
    color: #999999;
  }
}

.placeholder {
  color: #999999;
  font-size: 28rpx;
}

.image-upload-container {
  width: 100%;
  margin-top: 20rpx;
}

.contact-input {
  width: 100%;
  height: 80rpx;
  background-color: #f8f8f8;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333333;
  box-sizing: border-box;
}

.submit-area {
  display: flex;
  justify-content: center;
  margin-top: 60rpx;
  margin-bottom: 40rpx;
}

.tips-area {
  padding: 30rpx;

  .tips-item {
    font-size: 24rpx;
    color: #888888;
    margin-bottom: 10rpx;

    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>