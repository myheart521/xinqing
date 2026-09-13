<script setup>
import {ref} from 'vue'

const props = defineProps({})
const emit = defineEmits([])

const tn = (router) => {
  uni.navigateTo({
    url: router,
  });
}

</script>

<template>
  <view class="landscape_container">
    <!-- 压屏窗-->

    <view class="tn-flex" style="margin-bottom: 100rpx;padding-top: 46vh;">
      <!-- 发布动态-->
      <view class="tn-flex-1 tn-flex-center-center tn-m-sm tn-radius" @click="tn('/group_pages/views/add-group')">
        <view class="tn-flex tn-flex-column tn-flex-center-center">
          <view
              class="icon9__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-red_bg tn-white_text">
            <tn-icon name="camera-fill"></tn-icon>
          </view>
          <view class="tn-white_text tn-text-sm tn-text-center">
            <text class="tn-text-ellipsis">发布动态</text>
          </view>
        </view>
      </view>
      <!--发起活动-->
      <view class="tn-flex-1 tn-flex-center-center tn-m-sm tn-radius" @click="tn('/group_pages/views/add-active')">
        <view class="tn-flex tn-flex-column tn-flex-center-center">
          <view
              class="icon9__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-cyan_bg tn-white_text">
            <tn-icon name="flag-fill"></tn-icon>
          </view>
          <view class="tn-white_text tn-text-sm tn-text-center">
            <text class="tn-text-ellipsis">发起活动</text>
          </view>
        </view>
      </view>
    </view>

  </view>
</template>

<style scoped lang="scss">
.icon9 {
  &__item {
    width: 30%;
    background-color: #FFFFFF;
    border-radius: 10rpx;
    padding: 30rpx;
    margin: 20rpx 10rpx;
    transform: scale(1);
    transition: transform 0.3s linear;
    transform-origin: center center;

    &--icon {
      width: 110rpx;
      height: 110rpx;
      font-size: 65rpx;
      border-radius: 50%;
      margin: 20rpx 40rpx;
      position: relative;
      z-index: 1;

      &::after {
        content: " ";
        position: absolute;
        z-index: -1;
        width: 100%;
        height: 100%;
        left: 0;
        bottom: 0;
        border-radius: inherit;
        opacity: 1;
        transform: scale(1, 1);
        background-size: 100% 100%;
        background-image: url(http://localhost:8080/images/cool_bg_image/icon_bg5.png);
      }
    }
  }
}
</style>