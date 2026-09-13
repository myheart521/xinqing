<template>
  <view class="template-screen tn-safe-area-inset-bottom">

    <!-- 顶部自定义导航 -->
    <navbar title="运动与健康"></navbar>
    <!-- 页面内容 -->
    <view class="bg-contaniner " :style="{ 'background-color': hbgcolor }">

    </view>
    <view class="tn-mt-lg">
      <view class="tn-p">
        <tn-swiper @item-click="handleBannerClick" :data="banner" height="300" indicator
                   indicator-type="line" mode="aspectFill">
          <template #default="{ data }">
            <view style="height: 100%;width: 100%">
              <image :src="data.imageUrl" mode="aspectFill"/>
            </view>
          </template>
        </tn-swiper>
      </view>
      <!-- 方式10 start-->
      <view class="tn-flex tn-flex-center-around tn-mt" style="background-color:#FFF;">
        <view class=" tn-p-sm tn-m-xs tn-radius" @click="tn('/function_pages/views/motion-exercise')">
          <view class="tn-flex tn-flex-column tn-flex-center-center">
            <view
                class="icon10__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-blue_bg tn-white_text">
              <tn-icon name="sport-jog"></tn-icon>
            </view>
            <view class="tn-black_text tn-text-lg tn-text-center">
              <text class="tn-text-ellipsis">锻炼</text>
            </view>
          </view>
        </view>
        <view class=" tn-p-sm tn-m-xs tn-radius" @click="tn('/function_pages/views/motion-eating')">
          <view class="tn-flex tn-flex-column tn-flex-center-center">
            <view
                class="icon10__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-cyan_bg tn-white_text">
              <tn-icon name="food"></tn-icon>
            </view>
            <view class="tn-black_text tn-text-lg tn-text-center">
              <text class="tn-text-ellipsis">饮食</text>
            </view>
          </view>
        </view>
        <view class=" tn-p-sm tn-m-xs tn-radius" @click="tn('/function_pages/views/motion-wiki')">
          <view class="tn-flex tn-flex-column tn-flex-center-center">
            <view
                class="icon10__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-purple_bg tn-white_text">
              <tn-icon name="creative-fill"></tn-icon>
            </view>
            <view class="tn-black_text tn-text-lg tn-text-center">
              <text class="tn-text-ellipsis">百科</text>
            </view>
          </view>
        </view>
      </view>
      <!-- 方式10 end-->
    </view>

    <view style="background-color: #FFF;">

      <view>
        <view class="tn-flex justify-between">
          <view class=" tn-m tn-text-bold tn-text-xl">
            今日推荐
          </view>
          <view class="tn-m" style="font-size: 50rpx;">
            <tn-icon name="data"></tn-icon>
          </view>
        </view>
      </view>
      <swiper class="card-swiper" :circular="true" :autoplay="true" duration="500" interval="2500"
              @change="cardSwiper">
        <swiper-item v-for="(item,index) in swiperList" :key="index" :class="cardCur==index?'cur':''">
          <view @click="handleCardClick(item)" class="swiper-item image-banner tn-shadow-blur"
                :style="'background-image:url('+ item.url + ');background-size: cover;border-radius: 15rpx;'">
          </view>
          <view class="swiper-item-text card-shadow">
            <view class="tn-text-xxl tn-text-bold">{{ item.name }}</view>
            <view class="tn-text-sm tn-text-bold tn-pt-xs">{{ item.text }}</view>
          </view>
        </swiper-item>
      </swiper>
      <view class="indication">
        <block v-for="(item,index) in swiperList" :key="index">
          <view class="spot" :class="cardCur==index?'active':''"></view>
        </block>
      </view>


      <!--todo 此处有时间再做，因为涉及到搜索-->
      <!--      <view class="" style="margin-top: -20rpx;">-->
      <!--        <view class="tn-flex justify-between">-->
      <!--          <view class=" tn-m tn-text-bold tn-text-xl">-->
      <!--            热门搜索-->
      <!--          </view>-->
      <!--          <view class="tn-m" style="font-size: 50rpx;">-->
      <!--            <tn-icon name="data"></tn-icon>-->
      <!--          </view>-->
      <!--        </view>-->
      <!--      </view>-->
      <!--      <view class="tn-ml tn-text-sm tn-gray_text" style="margin-top: -25rpx;">-->
      <!--        <text>这是今日热门话题，请查收吖</text>-->
      <!--      </view>-->
      <!--      <view class="">-->
      <!--        <view class="tn-plan-content tn-m">-->

      <!--          <view v-for="(item,index) in planList" :key="index"-->
      <!--                class="tn-plan-content__item tn-mr tn-round tn-text-sm tn-text-bold"-->
      <!--                :class="[`tn-gradient-bg__${item.color}-light tn-${item.color}_text`]"  @click="tn('/pages/index/search?word=' + item.name)">-->
      <!--            <text class="tn-plan-content__item&#45;&#45;prefix">#</text> {{item.name}}-->
      <!--          </view>-->
      <!--        </view>-->
      <!--      </view>-->

      <view class="" style="margin-top: 50rpx;">
        <view class="tn-flex justify-between">
          <view class=" tn-m tn-text-bold tn-text-xl">
            热门阅读
          </view>
          <view class="tn-m" style="font-size: 50rpx;">
            <tn-icon name="data"></tn-icon>
          </view>
        </view>
      </view>

      <view class="tn-ml tn-text-sm tn-gray_text" style="margin-top: -25rpx;">
        <text>这是今日热门的阅读文章，请查收吖</text>
      </view>
      <!-- 不建议写时间，因为写了时间，你就要经常更新文章了鸭-->
      <view class="">
        <block v-for="(item, index) in hotArticle" :key="index">
          <view class="article-shadow tn-m" @click="handleHotClick(item)">
            <view class="tn-flex">
              <view class="image-pic tn-m-sm"
                    :style="'background-image:url(' + item.coverImage + ')'">
                <view class="image-article">
                </view>
              </view>
              <view class="tn-m-sm tn-t-xs" style="width: 100%;">
                <view class="tn-text-lg tn-text-bold clamp-text-1 ">
                  {{ item.title }}
                </view>
                <view class="tn-padding-top-xs" style="min-height: 105rpx;">
                  <text class=" tn-gray_text clamp-text-2 ">
                    {{ item.descriptions }}
                  </text>
                </view>
                <view class="tn-flex tn-flex-stretch-between">
                  <view
                      class=" tn-tag-content__item tn-mr tn-round tn-text-sm tn-text-bold"
                      :class="[`tn-gradient-bg__${item.color}-light tn-${item.color}_text`]">
                    <text class="tn-tag-content__item--prefix">#</text>
                    {{ item.tags }}
                  </view>
                  <view
                      class=" tn-gray_text tn-text-center tn-gray_text--disabled"
                      style="padding-top: 5rpx;">
                    <tn-icon name="footprint" class="tn-pr-xs" size="lg"></tn-icon>
                    <text class="tn-pr">{{ item.viewCount }}</text>
                    <tn-icon name="like" class="tn-pr-xs" size="lg"></tn-icon>
                    <text class="tn-text-df">{{ item.likeCount }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </block>
      </view>

    </view>
    <!-- 回到首页悬浮按钮-->
  </view>
</template>

<script>
import navbar from '@/components/navbar.vue'
import {getBanner, getCard} from '@/service/api/teacherController'
import {getHotKnowledge} from "@/service/api/newKnowledgeController"; // 导入所需的API

export default {
  name: 'motin-index',
  components: {
    navbar,
    // hotPage
  },
  data() {
    return {
      hbgcolor: '#FFF',
      banner: [], // Banner数据
      cardCur: 0,
      swiperList: [], // 推荐卡片数据
      planList: [
        {name: '运动', color: 'blue'},
        {name: '饮食', color: 'green'},
        {name: '百科', color: 'purple'},
        {name: '休闲', color: 'red'}
      ], // 模拟的 planList 数据
      hotArticle: [] // 模拟的热门文章数据
    }
  },
  onLoad() {
    // 加载所需数据
    this.fetchBannerData()
    this.fetchCardData() // 新增加载推荐卡片数据
    this.fetchHotData()
  },
  methods: {
    // 获取Banner数据
    async fetchBannerData() {
      try {
        const response = await getBanner()
        if (response && response.data) {
          this.banner = response.data
          console.log('获取banner数据成功', this.banner)
        } else {
          console.error('获取banner数据失败，响应结构不符合预期', response)
        }
      } catch (error) {
        console.error('获取banner数据发生错误', error)
        uni.showToast({
          title: '获取轮播图数据失败',
          icon: 'none'
        })
      }
    },

    // 获取推荐卡片数据
    async fetchCardData() {
      try {
        const response = await getCard()
        if (response && response.data) {
          this.swiperList = response.data
          console.log('获取推荐卡片数据成功', this.swiperList)
        } else {
          console.error('获取推荐卡片数据失败，响应结构不符合预期', response)
        }
      } catch (error) {
        console.error('获取推荐卡片数据发生错误', error)
        uni.showToast({
          title: '获取推荐数据失败',
          icon: 'none'
        })
      }
    },

    async fetchHotData() {
      try {
        const response = await getHotKnowledge()
        if (response && response.data) {
          this.hotArticle = response.data
          console.log('获取热门文章成功', this.swiperList)
        } else {
          console.error('获取热门文章数据失败，响应结构不符合预期', response)
        }
      } catch (error) {
        console.error('获取热门文章成功发生错误', error)
        uni.showToast({
          title: '获取热门文章失败',
          icon: 'none'
        })
      }
    },


    // 处理banner点击事件
    handleBannerClick(index) {
      const item = this.banner[index]
      this.handleNavigate(item)
    },

    // 处理卡片点击事件
    handleCardClick(item) {
      this.handleNavigate(item)
    },

    // 处理热门文章点击事件
    handleHotClick(item) {
      console.log("点击了")
      this.handleNavigate(item)
    },
    // 统一处理导航跳转逻辑
    handleNavigate(item) {
      if (!item) return

      try {
        if (item.link) {
          // 解析link格式
          const [type, id] = item.link.split(':')
          if (type === 'diet') {
            // 饮食详情页
            uni.navigateTo({
              url: `/function_pages/views/motion-eating-detail?id=${id}`
            })
          } else if (type === 'sport') {
            // 运动视频页
            uni.navigateTo({
              url: `/function_pages/views/motion-exercise-video?id=${id}`
            })
          }
        } else {
          console.log("111111:", item.id)
          uni.navigateTo({
            url: `/function_pages/views/motion-index-detail?id=${item.id}`
          })
        }
      } catch (error) {
        console.error('解析链接出错', error)
        uni.showToast({
          title: '链接格式错误',
          icon: 'none'
        })
      }
    },

    // 轮播卡片切换
    cardSwiper(e) {
      this.cardCur = e.detail.current
    },

    // 普通页面跳转
    tn(e) {
      uni.navigateTo({
        url: e,
      })
    },
  }
}
</script>

<style lang="scss" scoped>
@import '@/static/styles/custom_nav_bar.scss';

/* 自定义导航栏内容 start */
.custom-nav {
  height: 100%;

  &__back {
    margin: auto 5rpx;
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

/*logo start */
.logo-image {
  width: 65rpx;
  height: 65rpx;
  position: relative;
}

.logo-pic {
  background-size: cover;
  background-repeat: no-repeat;
  // background-attachment:fixed;
  background-position: top;
  border-radius: 50%;
}

/* 自定义导航栏内容 end */

.screen-shadow {
  box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.07);
  border-radius: 20rpx;
}

/* 图标容器10 start */
.icon10 {
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
      width: 84rpx;
      height: 65rpx;
      font-size: 45rpx;
      border-radius: 200rpx;
      margin-bottom: 18rpx;
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
        background-image: url(http://localhost:8080/images/cool_bg_image/icon_bg6.png);
      }
    }
  }
}

@media (prefers-reduced-motion: reduce) {
  .bg-contaniner::before {
    animation-duration: 0s;
  }
}

@keyframes bg {
  to {
    background-position: 0 calc(var(--bg-size) * -1);
  }
}

/* 移动背景部分 end*/
/* 胶囊*/
.tn-custom-nav-bar__back {
  width: 100%;
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

  &:before {
    content: " ";
    width: 1rpx;
    height: 110%;
    position: absolute;
    top: 22.5%;
    left: 0;
    right: 0;
    margin: auto;
    transform: scale(0.5);
    transform-origin: 0 0;
    pointer-events: none;
    box-sizing: border-box;
    opacity: 0.7;
    background-color: #FFFFFF;
  }
}

/* 轮播 start*/
.card-swiper {
  height: 570rpx !important;
}

.card-swiper swiper-item {
  width: 450rpx !important;
  left: 30rpx;
  box-sizing: border-box;
  padding: 0rpx 0rpx 80rpx 0rpx;
  overflow: initial;
}

.card-swiper swiper-item .swiper-item {
  width: 100%;
  display: block;
  height: 100%;
  border-radius: 10rpx;
  transform: scale(0.7) translate(0rpx, -104rpx);
  transition: all 0.3s ease-in 0s;
  will-change: transform;
  // overflow: hidden;
}

.card-swiper swiper-item.cur .swiper-item {
  transform: none;
  transition: all 0.3s ease-in 0s;
  will-change: transform;
}

.card-swiper swiper-item .swiper-item-text {
  margin-top: -100rpx;
  width: 100%;
  display: block;
  height: 50%;
  border-radius: 10rpx;
  transform: translate(20rpx, -30rpx) scale(0.7, 0.7);
  transition: all 0.4s ease 0s;
  will-change: transform;
  overflow: hidden;
  color: #000000;
  // background-image: linear-gradient(rgba(255, 255, 255, 0), rgba(0, 0, 0, 0));
  height: 140rpx;
}

.card-swiper swiper-item.cur .swiper-item-text {
  margin-top: -150rpx;
  padding-left: 30rpx;
  width: 500rpx !important;
  transform: translate(-25rpx, 20rpx) scale(0.9, 0.9);
  transition: all 0.4s ease 0s;
  will-change: transform;
  color: #FFFFFF;
  text-shadow: 0rpx 10rpx 20rpx rgba(0, 0, 0, 0.1);
  // background-image: linear-gradient(rgba(255, 255, 255, 0.01), rgba(0, 0, 0, 0.4));
  height: 137rpx;
}

.image-banner {
  display: flex;
  align-items: center;
  justify-content: center;
  // border: 1rpx solid red;
}

.image-banner image {
  width: 100%;
  height: 100%;
}

/* 轮播指示点 start*/
.indication {
  z-index: 9999;
  width: 100%;
  height: 36rpx;
  position: absolute;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.spot {
  background-color: #000;
  opacity: 0.4;
  width: 10rpx;
  height: 10rpx;
  border-radius: 20rpx;
  margin: 0 8rpx !important;
  right: -270rpx;
  top: -100rpx;
  position: relative;
}

.spot.active {
  opacity: 1;
  width: 10rpx;
  background-color: #000;
}

/* 计划内容 start*/
.tn-plan-content {
  &__item {
    display: inline-block;
    line-height: 45rpx;
    padding: 10rpx 30rpx;
    margin: 20rpx 20rpx 5rpx 0rpx;

    &--prefix {
      padding-right: 10rpx;
    }
  }
}

.tn-plan-content2 {
  &__item {
    line-height: 45rpx;
    padding: 15rpx 30rpx;
    margin: 30rpx 0rpx 10rpx 0rpx;

    &--prefix {
      padding-right: 10rpx;
    }
  }
}

/* 计划内容 end*/

/* 资讯主图 start*/
.image-article {
  border-radius: 8rpx;
  border: 1rpx solid #F8F7F8;
  width: 200rpx;
  height: 200rpx;
  position: relative;
}

.image-pic {
  background-size: cover;
  background-repeat: no-repeat;
  // background-attachment:fixed;
  background-position: top;
  border-radius: 10rpx;
}

.article-shadow {
  border-radius: 15rpx;
  box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
}

/* 文字截取*/
.clamp-text-1 {
  -webkit-line-clamp: 1;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  overflow: hidden;
}

.clamp-text-2 {
  -webkit-line-clamp: 2;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* 标签内容 start*/
.tn-tag-content {
  &__item {
    display: inline-block;
    line-height: 35rpx;
    padding: 5rpx 12rpx;

    &--prefix {
      padding-right: 2rpx;
    }
  }
}

/* 底部tabbar start*/
.footerfixed {
  position: fixed;
  width: 100%;
  bottom: 0;
  z-index: 999;
  background-color: #FFFFFF;
  box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
}

.tabbar {
  display: flex;
  align-items: center;
  min-height: 110rpx;
  justify-content: space-between;
  padding: 0;
  height: calc(110rpx + env(safe-area-inset-bottom) / 2);
  padding-bottom: calc(env(safe-area-inset-bottom) / 2);
}

.tabbar .action {
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
}

.tabbar .action .bar-icon {
  width: 100rpx;
  position: relative;
  display: block;
  height: auto;
  margin: 0 auto 10rpx;
  text-align: center;
  font-size: 42rpx;
  // line-height: 50rpx;
}

.tabbar .action .bar-icon image {
  width: 50rpx;
  height: 50rpx;
  display: inline-block;
}

.tabbar .action .bar-circle {
  position: relative;
  display: block;
  margin: -30rpx auto 20rpx;
  text-align: center;
  font-size: 52rpx;
  line-height: 90rpx;
  background-color: #FFCA28;
  width: 90rpx !important;
  height: 90rpx !important;
  overflow: hidden;
  border-radius: 50%;
  box-shadow: 0rpx 0rpx 20rpx 0rpx rgba(255, 202, 40, 0.5);
}

.tabbar .action .bar-circle image {
  width: 60rpx;
  height: 60rpx;
  display: inline-block;
  margin: 15rpx auto 15rpx;
}
</style>