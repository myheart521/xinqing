<script setup>

const props = defineProps({
  url: {
    type: String,
    default: ''
  },
  name: {
    type: String,
    default: ''
  },
  desc: {
    type: String,
    default: ''
  },
  sex: {
    type: String,
    default: ''
  }
})

const badgeConfig = {
  type: 'danger',
}
</script>

<template>
  <view class="nearby_avatar_container">
    <view class="tn-text tn-flex-center-start">
      <tn-avatar :url="url" size="sm" :badge="sex" :badge-config=" sex == 'icon-sex-female'?badgeConfig:{}">
      </tn-avatar>
      <view class="tn-ml tn-flex-column tn-flex-center-start">
        <text class="tn-text-bold tn-text-lg tn-text-ellipsis" style="width: 124rpx">{{ name }}</text>
        <text class="tn-gray_text tn-text-sm tn-text-ellipsis" style="width: 124rpx">{{ desc }}</text>
      </view>
      <!--关注按钮-->
      <view class="blogger__author__btn tn-flex-center-center">
        <text class="tn-brown-light_bg tn-round tn-text-bold tn-brown_text"
              style="padding: 5rpx 12rpx;">+ 关注
        </text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.blogger {
  &__author {
    &__btn {
      margin-right: -12rpx;
      padding: 0 20rpx;
    }
  }
}
</style>