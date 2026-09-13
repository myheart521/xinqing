<template>
  <view class="version-page">
    <navbar title="版本更新" />
    <view class="version-body">
      <view class="version-info">
        <text class="version-label">当前版本：</text>
        <text class="version-value">v1.0.0</text>
      </view>
      <view class="update-log">
        <tn-title title="更新日志" mode="sub" />
        <ul>
          <li>新增AI心理助手聊天功能</li>
          <li>优化心理测评体验</li>
          <li>修复已知bug，提升稳定性</li>
        </ul>
      </view>
    </view>
  </view>
</template>

<script setup>
import navbar from "@/components/navbar.vue";

</script>

<style scoped lang="scss">
.version-page {
  min-height: 100vh;
  background: #fff;
  padding: 40rpx 30rpx;
  .version-body {
    margin-top: 40rpx;
    .version-info {
      font-size: 30rpx;
      color: #333;
      margin-bottom: 30rpx;
      .version-label {
        font-weight: bold;
      }
      .version-value {
        color: #5677fc;
        margin-left: 20rpx;
      }
    }
    .update-log {
      background: #f8f8f8;
      border-radius: 16rpx;
      padding: 30rpx;
      box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.03);
      ul {
        margin: 0;
        padding-left: 30rpx;
        li {
          font-size: 28rpx;
          color: #333;
          line-height: 2;
        }
      }
    }
  }
}
</style> 