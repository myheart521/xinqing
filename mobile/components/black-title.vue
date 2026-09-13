<script setup>

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  name:{
    type: String,
    default: ''
  }
})
</script>

<template>
  <view class="black_title_container">
    <view class="tn-flex">
      <view class="tn-black_bg tn-white_text tn-text-center"
            style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
        <tn-icon :name="name" style="font-size: 30rpx;"></tn-icon>
      </view>
      <view class="tn-text-lg tn-pr-xs tn-text-bold">{{title}}</view>
    </view>
  </view>
</template>

<style scoped lang="scss">

</style>