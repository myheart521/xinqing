<script setup>
import {
  ref
} from 'vue';
import TnTabsItem from '@tuniao/tnui-vue3-uniapp/components/tabs/src/tabs-item.vue'
import {useUserStore} from "@/stores/user";

const userStore = useUserStore()

const tn = (e) => {
  // 如果是跳转到个人圈子页面，先检查登录状态
  if (e === '/group_pages/views/my-others-circle') {
    if (!userStore.checkLogin()) {
      return // 如果未登录，checkLogin方法会自动跳转到登录页面
    }
  }
   // 添加userId参数到跳转链接
    e = `${e}?userId=${userStore.userInfo.id}`
  uni.navigateTo({
    url: e,

  });
  console.log("跳转到：", e)
}

const scrollList = ref([{
  name: '发现'
},
  {
    name: '活动'
  },
  {
    name: '附近'
  }
])
const current = ref(0)

const emits = defineEmits('changeTab')

// tab选项卡切换
const tabChange = (index) => {
  current.value = index
  emits('changeTab', index)
}
</script>

<template>
  <!-- 顶部自定义导航 -->
  <tn-navbar :frosted="true" fixed height="80px" back-icon="" home-icon="" :bottom-shadow='false' bg-color='rgba(255,255,255,0)'>
    <view class="custom-nav tn-flex tn-flex-end-left">
      <!-- 个人圈子，默认显示自己头像，当有消息互动的时候，显示别人的头像，并且有一个红点点 -->
      <view class="custom-nav__back" @click="tn('/group_pages/views/my-others-circle')">
        <view class="logo-pic tn-shadow-blur"
              :style="'background-image:url('+ userStore.userInfo.userAvatar+')'">
          <view class="logo-image">
            <tn-badge bg-color="#E72F8C" :dot="true" :size="16" :absolute="true"
                      :absolute-center="false"></tn-badge>
          </view>
        </view>
      </view>

      <view>
        <tn-tabs height="100" bg-color='rgba(255,255,255,0)' :bottom-shadow='false' :bar='true'
                 v-model="current" @change="tabChange" active-color="#000" :active-bold="true" bar-width="60">
          <TnTabsItem v-for="(item, index) in scrollList" active-color :key="index" :title="item.name"/>
        </tn-tabs>
      </view>
      <tn-icon style="margin-top: 14px" name="search" size="40rpx"
               @click="tn('/group_pages/views/search-detail')"></tn-icon>
    </view>
  </tn-navbar>
</template>

<style lang="scss">
::v-deep .tn-tabs__bar {
  transform: translateX(10px) !important;
}

/* 自定义导航栏内容 start */
.custom-nav {
  height: 100%;
  align-items: flex-end;

  &__back {
    margin: 10rpx 5rpx;
    font-size: 40rpx;
    margin-right: 10rpx;
    margin-left: 30rpx;
    flex-basis: 5%;

  }

  &__search {
    flex-basis: 60%;
    width: 100%;
    height: 100%;

    &__box {
      width: 100%;
      height: 70%;
      padding: 10rpx 0;
      margin: 0 30rpx;
      border-radius: 60rpx 60rpx 0 60rpx;
      font-size: 24rpx;
    }

    &__icon {
      padding-right: 10rpx;
      margin-left: 20rpx;
      font-size: 30rpx;
    }

    &__text {
      color: #AAAAAA;
    }
  }
}

.logo-image {
  width: 60rpx;
  height: 60rpx;
  position: relative;
  margin-top: -15rpx;
}

.logo-pic {
  background-size: cover;
  background-repeat: no-repeat;
  // background-attachment:fixed;
  background-position: top;
  border-radius: 50%;
}

/* 自定义导航栏内容 end */
</style>