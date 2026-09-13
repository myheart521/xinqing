<template>
  <view class="help-page">
    <navbar title="帮助中心"></navbar>
    <view class="help-body">
      <view class="faq-item">
        <tn-title title="如何使用本应用？" mode="sub" />
        <text>进入首页后可体验AI聊天、心理测评、课程等功能，点击底部导航可切换不同模块。</text>
      </view>
      <view class="faq-item">
        <tn-title title="遇到问题如何反馈？" mode="sub" />
        <text>如遇到使用问题或建议，可通过"意见反馈"功能提交，或联系官方邮箱：support@example.com。</text>
      </view>
      <view class="faq-item">
        <tn-title title="如何保护我的隐私？" mode="sub" />
        <text>我们严格遵守隐私政策，您的数据仅用于提升服务体验，不会泄露给第三方。</text>
      </view>
      <view class="faq-item">
        <tn-title title="更多帮助" mode="sub" />
        <text>如需更多帮助，请关注官方公众号或联系客服。</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import navbar from "@/components/navbar.vue";

</script>

<style scoped lang="scss">
.help-page {
  min-height: 100vh;
  background: #fff;
  padding: 40rpx 30rpx;
  .help-body {
    margin-top: 40rpx;
    .faq-item {
      margin-bottom: 40rpx;
      background: #f8f8f8;
      border-radius: 16rpx;
      padding: 30rpx;
      box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.03);
      text {
        font-size: 28rpx;
        color: #333;
        line-height: 2;
      }
    }
  }
}
</style> 