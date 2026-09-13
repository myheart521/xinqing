<template>
  <view class="template-set">
    <!-- 顶部自定义导航 -->
    <navbar title="个人中心" home=""></navbar>
    <view class="tn-mt">
<!--      <view class="tn-flex tn-flex-center-between tn-strip-bottom-min tn-p" @click="tn('/mine_pages/change-avatar')">-->
<!--        <view>-->
<!--          <view class="tn-text-bold tn-text-lg">-->
<!--            用户头像-->
<!--          </view>-->
<!--          <view class="tn-gray_text tn-pt-xs">-->
<!--            有趣的头像，百里挑一-->
<!--          </view>-->
<!--        </view>-->
<!--        <view class="tn-text-lg tn-gray_text">-->
<!--          <tn-icon name="right" class="tn-pt"></tn-icon>-->
<!--        </view>-->
<!--      </view>-->

      <view class="tn-flex tn-flex-center-between tn-strip-bottom tn-p" @click="tn('/mine_pages/change-name-password')">
        <view>
          <view class="tn-text-bold tn-text-lg">
            账号安全
          </view>
          <view class="tn-gray_text tn-pt-xs">
            修改用户昵称、密码
          </view>
        </view>
        <view class="tn-text-lg tn-gray_text">
          <tn-icon name="right" class="tn-pt"></tn-icon>
        </view>
      </view>

      <view class="tn-flex tn-flex-center-between tn-strip-bottom-min tn-p tn-mt-xs" v-for="(item, index) in setList" :key="index" @click="tn(item.url)">
        <view>
          <view class="tn-text-bold tn-text-lg">
            {{ item.title }}
          </view>
        </view>
        <view class="tn-text-lg tn-gray_text">
          <tn-icon name="right" class="tn-pt"></tn-icon>
        </view>
      </view>

      <!-- 悬浮按钮-->
      <view class="tn-flex tn-flex-center-center tn-footerfixed">
        <view class="tn-flex-1 tn-m-sm tn-flex-center" @click="tn('/mine_pages/login')">
          <tn-button shape="round" height="80rpx" bg-color="tn-gradient-bg__cool-15" padding="40rpx 0" width="60%" shadow bold>
            <!-- <text class="tn-icon-light tn-padding-right-xs tn-color-black"></text> -->
            <text class="tn-white_text">退出登录</text>
            <!-- <text class="tn-icon-light tn-padding-left-xs tn-color-black"></text> -->
          </tn-button>
        </view>
      </view>

    </view>


  </view>
</template>

<script>
import navbar from '@/components/navbar.vue'
import {useUserStore} from "@/stores/user";

export default {
  components: {
    navbar
  },
  name: 'user-detail',
  data(){
    return {
      setList: [
        {
          title: "版权所属",
          url: "/mine_pages/content?type=copyright",
        },
        {
          title: "使用协议",
          url: "/mine_pages/content?type=protocol",
        },
        {
          title: "隐私政策",
          url: "/mine_pages/content?type=privacy",
        },
        {
          title: "帮助中心",
          url: "/mine_pages/help",
        },
        {
          title: "版本更新",
          url: "/mine_pages/version",
        }
      ]
    }
  },
  methods: {
    tn(url) {
      const userStore = useUserStore()
      if(url==="/mine_pages/login"){
        userStore.logout()
      }
      try {
        uni.navigateTo({
          url,
        });
      } catch (e) {
        console.error(e);
        uni.showToast({
          title: "跳转失败",
          icon: "none",
          mask: true,
        });
      }
    },
  }
}
</script>

<style lang="scss" scoped>
/* 胶囊*/
.tn-custom-nav-bar__back {
  width: 60%;
  height: 100%;
  position: relative;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  box-sizing: border-box;
  background-color: rgba(0, 0, 0, 0.15);
  border-radius: 1000rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.5);
  color: #FFFFFF;
  font-size: 18px;

  .icon {
    display: block;
    flex: 1;
    margin: auto;
    text-align: center;
  }

}


/* 间隔线 start*/
.tn-strip-bottom-min {
  width: 100%;
  border-bottom: 1rpx solid #F8F9FB;
}

.tn-strip-bottom {
  width: 100%;
  border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
}
/* 间隔线 end*/



/* 底部悬浮按钮 start*/
.tn-tabbar-height {
  min-height: 100rpx;
  height: calc(120rpx + env(safe-area-inset-bottom) / 2);
}
.tn-footerfixed {
  position: fixed;
  width: 100%;
  bottom: calc(30rpx + env(safe-area-inset-bottom));
  z-index: 1024;
  box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0);

}
/* 底部悬浮按钮 end*/

</style>
