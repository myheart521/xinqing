<script>
export default {
  props: {
    circles: {
      type: Array,
      default: () => []
    }
  },

  data() {
    return {
      bloggerList: []
    }
  },

  watch: {
    circles: {
      handler(newCircles) {
        if (!newCircles) return
        this.bloggerList = newCircles.map(circle => ({
          id: circle.id,
          type: 'image',
          name: circle.name,
          text: `${circle.follow}人关注`,
          url: circle.url || 'https://assets.example.invalid/placeholder.png', // 默认图片
        }))
      },
      immediate: true
    }
  },

  methods: {
    tn(router, circleId,url) {
      console.log("跳转到：", router, "圈子ID:", circleId,'url:',url)
      uni.navigateTo({
        url: `${router}?id=${circleId}&circleUrl=${url}`,
      });
    },
    toAllCircle() {
      uni.navigateTo({
        url: '/group_pages/views/all-circle'
      })
    }

  }
}
</script>

<template>
  <view>
    <view class="tn-flex tn-flex-center-between tn-mt">
      <view class="tn-m tn-text-bold tn-text-xl">
        精选圈子
      </view>
      <view class="tn-m tn-text-lg tn-grey-dark_text" @click="toAllCircle">
        <text class="tn-padding-xs">全部</text>
        <tn-icon name="topics"></tn-icon>
      </view>
    </view>

    <view>
      <!-- 方式16 start-->
      <view class="tn-flex tn-flex-wrap tn-mb">
        <block v-for="(item, index) in bloggerList" :key="index">
          <view class="" style="width: 33.3%;" @click="tn('/group_pages/views/circle-detail', item.id,item.url)">
            <view class="tn-flex tn-flex-column tn-flex-center-center">
              <view class="tn-radius tn-p-sm">
                <view class="image-pic" :style="'background-image:url('+ item.url +')'">
                  <view class="image-circle">
                  </view>
                </view>
                <view class="tn-text-center tn-text-bold tn-pt-xs">{{ item.name }}</view>
                <view class="tn-text-center tn-text-xs tn-grey-dark_text tn-pt-xs">
                  {{ item.text }}
                </view>
              </view>
            </view>
          </view>
        </block>
      </view>
      <!-- 方式16 end-->
    </view>
  </view>
</template>

<style lang="scss">
/* 博主头像 start*/
.image-circle {
  // padding: 95rpx;
  width: 190rpx;
  height: 190rpx;
  font-size: 40rpx;
  font-weight: 300;
  position: relative;
}

.image-pic {
  background-size: cover;
  background-repeat: no-repeat;
  // background-attachment:fixed;
  background-position: top;
  border-radius: 10rpx;
}
</style>