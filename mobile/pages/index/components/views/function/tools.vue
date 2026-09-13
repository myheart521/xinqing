<script setup>
import {
  ref
} from 'vue';

const tools = ref([{
  url: '/static/images/xiangce.png',
  title: 'AI情绪分析',
  des: '通过用户使用数据，分析用户心理健康程度',
  router: '/function_pages/views/analyse-index'
},
  {
    url: '/static/images/youxi.png',
    title: '小游戏',
    des: '放松解压小游戏',
    router: '/function_pages/views/games/game-index'
  },
  {
    url: '/static/images/taikong.png',
    title: '虚拟形象生成',
    des: '结合自身情况生成虚拟形象，陪伴聊天'
  }
])

const tn = (router) => {
  uni.navigateTo({
    url: router
  })
}
</script>

<template>
  <view class="content tn-flex-center-between tn-p tn-flex-wrap">
    <view :class="{'animate__lightSpeedInRight':(index+1)%2==0,'animate__lightSpeedInLeft':(index+1)%2!=0}"
          :style="'animation-delay:'+index*400+'ms;'" v-for="(item,index) in tools"
          class="animate__animated tn-mt tn-radius toolItem tn-flex-column tn-p tn-flex-center-between" :key="index"
          @click="tn(item.router)">
      <!-- 上方图片 -->
      <view>
        <tn-lazy-load :src="item.url" width="120rpx" height="120rpx"></tn-lazy-load>
      </view>
      <!-- 下方标题和描述 -->
      <view class="tn-flex-column tn-flex-center-center">
        <view class="tn-text-bold texts tn-text-2xl tn-gradient-bg__blue">
          {{ item.title }}
        </view>
        <view class="texts tn-text-sm tn-gradient-bg__blue">
          {{ item.des }}
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss">
.content {
  .toolItem {
    height: 300rpx;
    width: 48%;
    background-color: white;

  }

  .texts {
    -webkit-background-clip: text;
    color: transparent;
  }
}
</style>