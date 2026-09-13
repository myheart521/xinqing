<script>
export default {
  data() {
    return {
      image: 'http://localhost:8080/modelinfo/d670d0b2c810411bb271409efacfb168/SDXL-Colorful-Second-Dimension',
      list: [{
        url: 'https://assets.example.invalid/placeholder.png',
        title: "测试题库",
        subTitle: "帮助你探索情感、认知与行为的微妙联系",
        router: '/function_pages/views/test-list'
      }, {
        url: 'https://assets.example.invalid/placeholder.png',
        title: "音乐",
        subTitle: "在疗愈的旋律中找到宁静，让音乐成为你心灵的庇护所",
        router: '/function_pages/views/music-list'
      }, {
        url: 'https://assets.example.invalid/placeholder.png',
        title: "白噪声",
        subTitle: "帮助你摆脱日常的纷扰，将心灵带入深度的放松状态",
        router: '/function_pages/views/sleep-aid'
      }, {
        url: 'https://assets.example.invalid/placeholder.png',
        title: "运动",
        subTitle: "每一次心跳都是对生命的热爱，每一次挥汗如雨都是对自我的挑战",
        router: '/function_pages/views/motion-index'
      }, {
        url: 'https://assets.example.invalid/placeholder.png',
        title: "成长记录",
        subTitle: "每一段文字都是对过往经历的深思，每一张图片都承载着难忘的瞬间",
        router: '/function_pages/views/note-page'
      }],
      options: {
        speed: 600,
        grabCursor: true,
        slidesPerView: 'auto',
        centeredSlides: true,
        spaceBetween: 24,
        watchSlidesProgress: true
      },
      planetStyle: {},
      itemStyle: {
        'width': 'calc(100vw * 0.8)',
        'max-width': '640px',
        'box-sizing': 'border-box',
        'position': 'relative'
      },
      swiperStyle: {
        'height': '300px',
        'padding-top': '64px',
        'padding-bottom': '64px'
      },
      activeIndex: 0
    }
  },

  methods: {
    init(swiper) {
      this.$refs.zSwiper.swiper.on("progress", (s, progress) => {
        const max =
            s.slides.length > 4 ? 360 - (8 - s.slides.length + 1) * 45 : 270;
        this.$set(this.planetStyle, 'transform', `translate(-50%, -50%) rotate(${
            max * -progress
        }deg)`);
      })
      this.$refs.zSwiper.swiper.on("setTransition", (s, duration) => {
        const max =
            s.slides.length > 4 ? 360 - (8 - s.slides.length + 1) * 45 : 270;
        this.$set(this.planetStyle, 'transitionDuration', `${duration}ms`);

      })
      this.$refs.zSwiper.swiper.on("slideChange", (swiper) => {
        this.activeIndex = swiper.activeIndex
      })
    },
    tn(router) {
      uni.navigateTo({
        url: router
      })
    },
    handleSwiperClick(index){
      console.log(index)
    },
  },
}
</script>

<template>
  <view class="travel-body">
    <view class="travel-wrapper">
      <view class="travel-slider">
        <!-- Rotating Planet -->
        <view class="travel-slider-planet" :style="[planetStyle]">
          <image class="planet-image" src="@/function_pages/static/swiper-image/earth.svg" mode="heightFix"/>
          <view :class="['travel-slider-cities',`travel-slider-cities-${list.length > 4 ? '8' : '4'}`]">
            <image class="cities-image animate__animated base-transform" :class="{'swing':activeIndex == 0}"
                   src="@/function_pages/static/swiper-image/sikao.svg"/>
            <image class="cities-image animate__animated base-transform" :class="{'swing':activeIndex == 1}"
                   src="@/function_pages/static/swiper-image/yinyue.svg"/>
            <image class="cities-image animate__animated base-transform" :class="{'swing':activeIndex == 2}"
                   src="@/function_pages/static/swiper-image/yueliang.svg"/>
            <image class="cities-image animate__animated base-transform" :class="{'swing':activeIndex == 3}"
                   src="@/function_pages/static/swiper-image/lizhi.svg"/>
            <image class="cities-image animate__animated base-transform" :class="{'swing':activeIndex == 4}"
                   src="@/function_pages/static/swiper-image/chengzhang.svg"/>
          </view>
        </view>
        <!-- Swiper -->
        <z-swiper ref="zSwiper" v-model="list" :custom-style="swiperStyle" :options="options"
                  @swiper="init">
          <z-swiper-item @click="tn(item.router)" v-for="(item,index) in list" :key="index" :custom-style="itemStyle">
            <image :src="item.url" mode="aspectFill" class="travel-slider-bg-image"/>
            <view class="travel-slider-content">
              <view class="travel-slider-title">{{ item.title }}</view>
              <view class="travel-slider-subtitle">{{ item.subTitle }}</view>
            </view>
          </z-swiper-item>
        </z-swiper>
      </view>
    </view>
  </view>
</template>


<style lang="scss" scoped>
.travel-body {
  position: relative;
  // height: 90vh;
  height: 900rpx;
  margin: 0;
  padding: 0;
  // background: #ccc;
  color: #000;
  line-height: 1.5;
  font-family: -apple-system, system-ui, 'Helvetica Neue', Helvetica, Arial,
  'Segoe UI', Roboto, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  // background-image: linear-gradient(to bottom, #6c08ca, #fff);
}

.travel-wrapper {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.travel-slider {
  position: relative;
  box-sizing: border-box;
  width: 100%;
  margin: 0 auto;


  &-bg-image {
    position: absolute;
    width: 630rpx;
    height: 430rpx;
    object-fit: cover;
    left: 0;
    top: 0;
    border-radius: 16px;
    box-sizing: border-box;
    box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.5),
    0px -1px 0px rgba(255, 255, 255, 0.5);
  }

  &-content {
    position: absolute;
    left: 0;
    right: 0;
    bottom: -45px;
    padding: 15px;
    box-sizing: border-box;
    color: #fff;
    text-shadow: 1px 1px 1px #000;
    line-height: 1.25;
    border-radius: 0 0 16px 16px;
    background-image: linear-gradient(to top,
        rgba(0, 0, 0, 0.5),
        rgba(0, 0, 0, 0));
  }

  &-title {
    font-weight: bold;
    font-size: 32px;
  }

  &-subtitle {
    font-size: 18px;
  }

  &-planet {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    height: 100%;

    > .planet-image {
      display: block;
      width: auto;
      height: 100%;
      margin: 0 auto;
    }
  }

  &-cities {
    .base-transform {
      --travel-slider-image-rotate: 0deg;
      --travel-slider-image-scale: 1;
      transform: translateX(-50%) scale(var(--travel-slider-image-scale)) rotate(var(--travel-slider-image-rotate)) !important;
    }

    @keyframes swing {
      20% {
        -webkit-transform: rotate3d(0, 0, 1, 15deg);
        transform: translateX(-50%) scale(var(--travel-slider-image-scale)) rotate(var(--travel-slider-image-rotate)) rotate3d(0, 0, 1, 15deg);
      }

      40% {
        -webkit-transform: rotate3d(0, 0, 1, -10deg);
        transform: translateX(-50%) scale(var(--travel-slider-image-scale)) rotate(var(--travel-slider-image-rotate)) rotate3d(0, 0, 1, -10deg);
      }

      60% {
        -webkit-transform: rotate3d(0, 0, 1, 5deg);
        transform: translateX(-50%) scale(var(--travel-slider-image-scale)) rotate(var(--travel-slider-image-rotate)) rotate3d(0, 0, 1, 5deg);
      }

      80% {
        -webkit-transform: rotate3d(0, 0, 1, -5deg);
        transform: translateX(-50%) scale(var(--travel-slider-image-scale)) rotate(var(--travel-slider-image-rotate)) rotate3d(0, 0, 1, -5deg);
      }

      to {
        -webkit-transform: rotate3d(0, 0, 1, 0deg);
        transform: translateX(-50%) scale(var(--travel-slider-image-scale)) rotate(var(--travel-slider-image-rotate)) rotate3d(0, 0, 1, 0deg);
      }
    }

    .swing {
      -webkit-transform-origin: top center;
      transform-origin: top center;
      -webkit-animation-name: swing;
      animation-name: swing;
    }

    .cities-image {
      --travel-slider-planet-size: calc(220px);
      --travel-slider-image-rotate: 0deg;
      --travel-slider-image-scale: 1;
      position: absolute;
      bottom: 95% !important;
      left: 50% !important;
      transform-origin: center calc(var(--travel-slider-planet-size) / 2 * 0.95 * 0.95 + 120px * 1) !important;
      height: 80px;
      width: 80px;
      object-position: center bottom;
      object-fit: contain;
      transition: all;


    }

    &-8 {
      .cities-image:nth-child(2) {
        --travel-slider-image-rotate: 45deg;
      }

      .cities-image:nth-child(3) {
        --travel-slider-image-rotate: 90deg;
      }

      .cities-image:nth-child(4) {
        --travel-slider-image-rotate: 135deg;
      }

      .cities-image:nth-child(5) {
        --travel-slider-image-rotate: 180deg;
      }

      .cities-image:nth-child(6) {
        --travel-slider-image-rotate: 225deg;
      }

      .cities-image:nth-child(7) {
        --travel-slider-image-rotate: 270deg;
      }

      .cities-image:nth-child(8) {
        --travel-slider-image-rotate: 315deg;
      }
    }

    &-4 {
      img:nth-child(2) {
        --travel-slider-image-rotate: 90deg;
      }

      img:nth-child(3) {
        --travel-slider-image-rotate: 180deg;
      }

      img:nth-child(4) {
        --travel-slider-image-rotate: 270deg;
      }
    }
  }
}

.travel-slider:after {
  content: '';
  position: absolute;
  height: 50vh;
  top: 50%;
  width: 100%;
  left: 0;
  // background-image: linear-gradient(to bottom,
  // 		rgba(255, 255, 255, 0),
  // 		#b0a5d1 50%);
}
</style>