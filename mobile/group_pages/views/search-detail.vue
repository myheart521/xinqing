<script setup>
import searchVue from '@/components/search.vue';
import navbar from '@/components/navbar.vue'

const hotList = ['分手', '考研', '学不进', '迷茫', '就业', '压力好大', '吵架'] //热门搜索
const blurList = ['就业', '就业形势', '就业形势不好', '压力', '压力好大'] //模糊搜索内容
// const marginTop = uni.getStorageSync("marginTop")

const userInput = (value) => {
  console.log("用户输入：", value)
}

const userSearch = (value) => {
  console.log("用户搜索：", value)
}
</script>

<template>
  <view>
    <navbar title="搜索"></navbar>
    <view style="margin-top: 10rpx">
      <searchVue :hotList="hotList" :blurList="blurList" :input="userInput" :change="userSearch"></searchVue>
    </view>

  </view>
</template>

<style lang="scss">

</style>