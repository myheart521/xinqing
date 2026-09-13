<script setup>
import TnNavbar from '@tuniao/tnui-vue3-uniapp/components/navbar/src/navbar.vue'
import {
  useNavSize
} from '@/utils/nav-height.js'
const {
  marginTop
} = useNavSize()
// 跳转
const tn = (e) => {
  uni.navigateTo({
    url: e
  });
}
</script>

<template>
  <!-- 隐藏返回上一页和返回home页面的按钮 -->
  <TnNavbar class="tnNavBar" height="45px" back-icon="" home-icon="" bg-color='none'>
    <view class="custom-nav tn-flex-center-start tn-flex">
      <view class="custom-nav__back">
        <tn-icon name='notice' size='50rpx' color="tn-gray-dark" @click="tn('/mine_pages/messages')"></tn-icon>
      </view>
    </view>
  </TnNavbar>
</template>

<style lang="scss">
/*自定义导航栏内容 start*/
.tnNavBar {
  position: fixed;
  z-index: 999;
  top: 30rpx;

  .custom-nav {
    height: 100%;
    width: 100%;

    &__back {
      margin: auto 5rpx;
      font-size: 40rpx;
      margin-right: 10rpx;
      flex-basis: 5%;
      width: 100rpx;
      position: absolute;

    }
  }
}
</style>