<template>
  <view class="avatar-container">
    <navbar title="修改头像" home=""></navbar>

    <!-- 预览区域 -->
    <view class="preview-section">
      <view class="preview-title">当前头像</view>
      <view class="avatar-preview">
        <image
          :src="currentAvatar || image1"
          mode="aspectFill"
        ></image>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="button-group">
      <button class="upload-btn" @click="chooseImage">选择图片</button>

      <button
        class="confirm-btn"
        :disabled="!hasNewAvatar"
        @click="confirmUpload"
      >
        确认修改
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue";
import navbar from "@/components/navbar.vue";
import {ossAvatarUrl} from "@/utils/ossUrl";

const currentAvatar = ref(""); // 当前头像
const hasNewAvatar = ref(false); // 是否有新头像待上传
const image1 = ossAvatarUrl.avatar1

// 选择图片
const chooseImage = async () => {
  try {
    const res = await uni.chooseImage({
      count: 1,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
    });

    currentAvatar.value = res.tempFilePaths[0];
    hasNewAvatar.value = true;
  } catch (e) {
    console.error("选择图片失败:", e);
  }
};

// 确认上传
const confirmUpload = async () => {
  if (!hasNewAvatar.value) return;

  try {
    uni.showLoading({ title: "上传中..." });

    // 这里替换为实际的上传API
    // const res = await uploadFile(currentAvatar.value)

    uni.hideLoading();
    uni.showToast({
      title: "修改成功",
      icon: "success",
    });

    // 返回上一页并刷新
    setTimeout(() => {
      const pages = getCurrentPages();
      const prevPage = pages[pages.length - 2];
      if (prevPage) {
        // 更新上一页的头像
        prevPage.$vm.refreshAvatar && prevPage.$vm.refreshAvatar();
      }
      uni.navigateBack();
    }, 1500);
  } catch (error) {
    uni.hideLoading();
    uni.showToast({
      title: "上传失败，请重试",
      icon: "none",
    });
  }
};
</script>

<style lang="scss" scoped>
.avatar-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.preview-section {
  background-color: #fff;
  padding: 40rpx;
  margin: 20rpx;
  border-radius: 12rpx;

  .preview-title {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 30rpx;
  }

  .avatar-preview {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    overflow: hidden;
    margin: 0 auto;
    border: 2rpx solid #eee;

    image {
      width: 100%;
      height: 100%;
    }
  }
}

.button-group {
  position: fixed;
  bottom: 40rpx;
  left: 40rpx;
  right: 40rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;

  button {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    border-radius: 44rpx;
    font-size: 32rpx;
    border: none;

    &::after {
      border: none;
    }

    &.upload-btn {
      background: #fff;
      color: #409eff;
      border: 2rpx solid #409eff;
    }

    &.confirm-btn {
      background: linear-gradient(to right, #36D1DC, #5B86E5);
      color: #fff;

      &:disabled {
        opacity: 0.6;
      }
    }
  }
}
</style>
