<script setup>
import {
  ref,
  onMounted
} from 'vue';
import {
  onLoad
} from '@dcloudio/uni-app'
import indexVue from './components/index.vue';
import mineVue from '@/pages/index/components/mine.vue'
import groupVue from '@/pages/index/components/group.vue'
import functionVue from './components/function.vue';
import {getAll3} from "@/service/api/circleController.ts";
import {useUserStore} from "@/stores/user";
import {useMusicPlayerStore} from "@/stores/musicPlayer";
import Introduction from "@/pages/index/components/introduction.vue";

const userStore = useUserStore()
const musicPlayerStore = useMusicPlayerStore()

// 初始化音乐播放器
musicPlayerStore.initAudioContext()

//tabbar下标
const currentTabbarIndex = ref(0)
// 自定义底栏对应页面的加载情况
const tabberPageLoadFlag = ref([])
onLoad((options) => {
  const index = Number(options.index || 0)
  // 根据底部tabbar菜单列表设置对应页面的加载情况
  for (let i = 0; i < 5; i++) {
    tabberPageLoadFlag.value.push(i === index)
  }
  changeTabbar(index)
})

// 导航页面滚动到底部
const tabbarPageScrollLower = (e) => {
  if (currentTabbarIndex.value === 1) {
    // this.$refs.pageB.getRandomData && this.$refs.pageB.getRandomData()
  }
}

// 修改当前选中的tabbar
const changeTabbar = (index) => {
  if (currentTabbarIndex.value === index) return
  _switchTabbarPage(index)
  currentTabbarIndex.value = index
}

// 切换导航页面
const _switchTabbarPage = (index) => {
  // 底部tabbar短震动骚操作，真机效果很不错，模拟器忽略就行
  // wx.vibrateShort();
  // 正式代码
  const selectPageFlag = tabberPageLoadFlag.value[index]
  if (selectPageFlag === undefined) {
    return
  }
  if (selectPageFlag === false) {
    tabberPageLoadFlag.value[index] = true
  }
}

onMounted(() => {
  userStore.initUserInfo()
})

// onMounted(async () => {
//   const res = await getAll3()
//   console.log("从后端获取的数据为：", res)
// })
</script>

<template>
  <view class="index">
    <!-- 二级页面 -->
    <!-- 首页 -->
    <view v-if="tabberPageLoadFlag[0]" :style="{
		  display: currentTabbarIndex === 0 ? '' : 'none'
		}">
      <scroll-view class="custom-tabbar-page" enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <!-- <page-a ref="pageA"></page-a> -->
        <indexVue></indexVue>
      </scroll-view>
    </view>
    <!-- 圈子 -->
    <view v-if="tabberPageLoadFlag[1]" :style="{
		  display: currentTabbarIndex === 1 ? '' : 'none'
		}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <groupVue></groupVue>
      </scroll-view>
    </view>
    <!-- 工具 -->
    <view v-if="tabberPageLoadFlag[2]" :style="{
		  display: currentTabbarIndex === 2 ? '' : 'none'
		}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <functionVue></functionVue>
      </scroll-view>
    </view>
    <!-- 心理咨询中心 -->
    <view v-if="tabberPageLoadFlag[3]" :style="{
		  display: currentTabbarIndex === 3 ? '' : 'none'
		}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <introduction></introduction>
      </scroll-view>
    </view>
    <!-- 我的 -->
    <view v-if="tabberPageLoadFlag[4]" :style="{
		  display: currentTabbarIndex === 4 ? '' : 'none'
		}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top>
        <!-- <page-a ref="pageA"></page-a> -->
        <mine-vue></mine-vue>
      </scroll-view>
    </view>


    <view class="tabbar tn-bg-white">
      <div class="">
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
        <div class="g-bubble"></div>
      </div>
      <view class="action" @tap.stop="changeTabbar(0)">
        <view class="bar-icon">
          <image class="" :src="`/static/tabbar/tn-tabbar0${currentTabbarIndex === 0 ? '-curnew' : ''}.png`">
          </image>
        </view>
        <view class="" :class="[currentTabbarIndex === 0 ? 'tn-color-blue' : 'tn-color-gray']">首页</view>
      </view>
      <view class="action" @tap.stop="changeTabbar(1)">
        <view class="bar-icon">
          <image class="" :src="`/static/tabbar/tn-tabbar1${currentTabbarIndex === 1 ? '-curnew' : ''}.png`">
          </image>
        </view>
        <view class="" :class="[currentTabbarIndex === 1 ? 'tn-color-blue' : 'tn-color-gray']">圈子</view>
      </view>

      <view class="action bar-center" @tap.stop="changeTabbar(2)">
        <view class="nav-index-button">
          <view class="nav-index-button__content">
            <view class="nav-index-button__content--icon tn-flex tn-flex-row-center tn-flex-col-center">
              <view class="bar-circle">
                <image class=""
                       src='/static/tabbar/faxian.png'>
                </image>
              </view>
            </view>
          </view>

          <view class="nav-index-button__meteor">
            <view class="nav-index-button__meteor__wrapper">
              <view v-for="(item,index) in 6" :key="index" class="nav-index-button__meteor__item"
                    :style="{transform: `rotateX(${-60 + (30 * index)}deg) rotateZ(${-60 + (30 * index)}deg)`}">
                <view class="nav-index-button__meteor__item--pic"></view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="action" @tap.stop="changeTabbar(3)">
        <view class="bar-icon">
          <image class="" :src="`/static/tabbar/tn-tabbar2${currentTabbarIndex === 3 ? '-curnew' : ''}.png`">
          </image>
        </view>
        <view class="" :class="[currentTabbarIndex === 3 ? 'tn-color-blue' : 'tn-color-gray']">咨询</view>
      </view>
      <view class="action" @tap.stop="changeTabbar(4)">
        <view class="bar-icon">
          <image class="" :src="`/static/tabbar/tn-tabbar3${currentTabbarIndex === 4 ? '-curnew' : ''}.png`">
          </image>
        </view>
        <view class="" :class="[currentTabbarIndex === 4 ? 'tn-color-blue' : 'tn-color-gray']">我的</view>
      </view>
    </view>

    <view class="tnwave waveAnimation">
      <view class="waveWrapperInner bgTop">
        <view class="wave waveTop"
              style="background-image: url('/static/tabbar/wave-2.png')"></view>
      </view>
      <view class="waveWrapperInner bgMiddle">
        <view class="wave waveMiddle"
              style="background-image: url('/static/tabbar/wave-2.png')"></view>
      </view>
      <view class="waveWrapperInner bgBottom">
        <view class="wave waveBottom"
              style="background-image: url('/static/tabbar/wave-1.png')"></view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@use 'sass:math';

.template-outset {
  // background-image: linear-gradient(to top, #4C3FAE 20%, #6E26BA 80%);
  width: 100vw;
  height: 100vh;
}

@keyframes move_wave {
  0% {
    transform: translateX(0) translateZ(0) scaleY(1)
  }

  50% {
    transform: translateX(-25%) translateZ(0) scaleY(1)
  }

  100% {
    transform: translateX(-50%) translateZ(0) scaleY(1)
  }
}

.tnwave {
  overflow: hidden;
  position: absolute;
  left: 0;
  right: 0;
  // bottom: 100rpx;
  bottom: calc(50rpx + constant(safe-area-inset-bottom));
  bottom: calc(50rpx + env(safe-area-inset-bottom));
  // top: 0;
  z-index: 997;
  margin: auto;
  height: 90rpx;
  // background-color: chartreuse;
}

.waveWrapperInner {
  position: absolute;
  width: 100%;
  overflow: hidden;
  height: 100%;
}

.wave {
  position: absolute;
  left: 0;
  width: 200%;
  height: 100%;
  background-repeat: repeat no-repeat;
  background-position: 0 bottom;
  transform-origin: center bottom;
}


.bgTop {
  opacity: 0.4;
}

.waveTop {
  background-size: 50% 45px;
}

.waveAnimation .waveTop {
  animation: move_wave 4s linear infinite;
}

.bgMiddle {
  opacity: 0.6;
}

.waveMiddle {
  background-size: 50% 40px;
}

.waveAnimation .waveMiddle {
  animation: move_wave 3.5s linear infinite;
}

.bgBottom {
  opacity: 0.95;
}

.waveBottom {
  background-size: 50% 35px;
}

.waveAnimation .waveBottom {
  animation: move_wave 2s linear infinite;
}


@for $i from 0 through 20 {
  .g-bubble:nth-child(#{$i}) {
    opacity: 0.2;
    border-radius: 100%;
    position: fixed;
    background: #26b4f5;
    $width: random(20)+px;
    right: #{(random(100)) + '%'};
    bottom: - #{(random(60))}px;
    width: $width;
    height: $width;
    animation: moveToTop #{math.div(random(9500) + 1500, 1000)}s ease-in-out -#{math.div(random(9000), 1000)}s infinite;
  }
}

@keyframes moveToTop {
  70% {
    opacity: 0.05;
  }

  100% {
    opacity: 0.01;
    transform: translate(-50%, -180px) scale(0.3);
  }
}

.index {
  width: 100%;
  height: 100vh;
  position: relative;
  // background-color: #f7f7f7;

  .custom-tabbar-page {
    width: 100%;
    height: calc(100vh - 110rpx);
    // height: 100vh;
    box-sizing: border-box;
    padding-bottom: 0rpx;
    padding-bottom: calc(0rpx + constant(safe-area-inset-bottom));
    padding-bottom: calc(0rpx + env(safe-area-inset-bottom));
  }

  .tabbar {
    width: 100%;
    height: 45rpx;
    height: calc(45rpx + constant(safe-area-inset-bottom));
    height: calc(45rpx + env(safe-area-inset-bottom));
    position: fixed;
    bottom: 0rpx;
    left: 0;
    right: 0;
    background-color: white;
    z-index: 998;
    padding-bottom: calc(65rpx + constant(safe-area-inset-bottom));
    padding-bottom: calc(65rpx + env(safe-area-inset-bottom));
    display: flex;
    align-items: center;
    justify-content: space-between;
    // box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);

    .action {
      font-size: 22rpx;
      position: relative;
      flex: 1;
      text-align: center;
      padding: 0;
      display: block;
      height: auto;
      line-height: 1;
      margin: 0;
      overflow: initial;

      .bar-icon {
        width: 100rpx;
        position: relative;
        display: block;
        height: auto;
        margin: 0 auto 10rpx;
        text-align: center;
        font-size: 42rpx;

        image {
          width: 55rpx;
          height: 55rpx;
          display: inline-block;
        }
      }

      .bar-circle {
        position: relative;
        display: block;
        margin: 0rpx auto 0rpx;
        text-align: center;
        font-size: 52rpx;
        line-height: 90rpx;
        width: 100rpx !important;
        height: 100rpx !important;
        overflow: hidden;

        image {
          width: 100rpx;
          height: 100rpx;
          display: inline-block;
          margin: 0rpx auto 0rpx;
        }
      }
    }

    .bar-center {
      animation: suspension 3s ease-in-out infinite;

      .nav-index-button {
        animation: suspension 3s ease-in-out infinite;
        z-index: 999999;


        &__content {
          position: absolute;
          width: 100rpx;
          height: 100rpx;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);

          &--icon {
            width: 100rpx;
            height: 100rpx;
            font-size: 60rpx;
            border-radius: 50%;
            margin-bottom: 18rpx;
            position: relative;
            z-index: 1;
            transform: scale(0.85);

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
              // background-image: url(http://localhost:8080/images/cool_bg_image/icon_bg6.png);
            }
          }
        }

        &__meteor {
          position: absolute;
          top: 50%;
          left: 50%;
          width: 100rpx;
          height: 100rpx;
          transform-style: preserve-3d;
          transform: translate(-50%, -50%) rotateY(75deg) rotateZ(10deg);

          &__wrapper {
            width: 100rpx;
            height: 100rpx;
            transform-style: preserve-3d;
            animation: spin 20s linear infinite;
          }

          &__item {
            position: absolute;
            width: 100rpx;
            height: 100rpx;
            border-radius: 1000rpx;
            left: 0;
            top: 0;

            &--pic {
              display: block;
              width: 100%;
              height: 100%;
              background: url(http://localhost:8080/images/cool_bg_image/arc3.png) no-repeat center center;
              background-size: 100% 100%;
              animation: arc 4s linear infinite;
            }
          }
        }
      }
    }
  }
}


@keyframes suspension {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-0.6rem);
  }
}

@keyframes spin {
  0% {
    transform: rotateX(0deg);
  }

  100% {
    transform: rotateX(-360deg);
  }
}

@keyframes arc {
  to {
    transform: rotate(360deg);
  }
}
</style>