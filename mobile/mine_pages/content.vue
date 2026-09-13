<template>
  <view class="content-page">
    <navbar :title="pageTitle"></navbar>
    <view class="content-body">
      <rich-text :nodes="pageContent" />
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import navbar from "@/components/navbar.vue";
const props = defineProps({
  type: {
    type: String,
    default: 'copyright' // copyright, protocol, privacy
  }
})

const pageTitle = computed(() => {
  switch (props.type) {
    case 'protocol': return '使用协议';
    case 'privacy': return '隐私政策';
    default: return '版权所属';
  }
})

const pageContent = computed(() => {
  switch (props.type) {
    case 'protocol':
      return `<h3>使用协议</h3><p>本应用致力于为用户提供心理健康服务，使用本应用即表示您同意我们的服务条款和隐私政策。请勿将本应用用于非法用途。</p>`;
    case 'privacy':
      return `<h3>隐私政策</h3><p>我们非常重视您的隐私。您的个人信息仅用于提升服务体验，不会泄露给第三方。详情请参阅完整隐私政策内容。</p>`;
    default:
      return `<h3>版权所属</h3><p>本应用所有内容归开发团队所有，未经授权禁止转载和使用。如有疑问请联系官方。</p>`;
  }
})
</script>

<style scoped lang="scss">
.content-page {
  min-height: 100vh;
  background: #fff;
  padding: 40rpx 30rpx;
  .content-body {
    margin-top: 40rpx;
    font-size: 28rpx;
    color: #333;
    line-height: 2;
    background: #f8f8f8;
    border-radius: 16rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.03);
  }
}
</style>
