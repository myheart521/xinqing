<template>
  <view class="template-details tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <navbar title="食谱详情"></navbar>

    <view class="">
      <!-- 图文信息 -->
      <view>
        <view class="blogger__item">
          <view
            class=""
            style="text-align: center; font-weight: 800; font-size: 36rpx"
          >
            <view>{{ content.title }}</view>
          </view>
          <view
            style="text-align: center"
            class="tn-pr tn-pl-sm tn-pt-xs tn-gray_text"
          >
            发布于 {{ content.createtime }}
          </view>
        </view>
        <view class="blogger__content">
          <view v-html="content.content"></view>
        </view>
      </view>

      <!--这里是分享和点赞-->
      <view
        class="tn-flex tn-flex-start-center"
        style="margin: 40rpx 0 60rpx 0"
      >
        <view class="tn-m-xs tn-text-center" style="width: 50%">
          <tn-button
            class="tn-flex-1"
            bg-color="#00FFC6"
            padding="40rpx 0"
            width="90%"
            height="80rpx"
            shadow
            bold
          >
            <tn-icon name="like" class="tn-pr-xs tn-black_text"></tn-icon>
            <text class="tn-black_text">点 赞</text>
          </tn-button>
        </view>
        <view
          class="tn-m-xs tn-text-center"
          @click="share()"
          style="width: 50%"
        >
          <tn-button
            class="tn-flex-1"
            bg-color="#FFF00D"
            padding="40rpx 0"
            width="90%"
            height="80rpx"
            shadow
            bold
            open-type="share"
          >
            <tn-icon name="share" class="tn-pr-xs tn-black_text"></tn-icon>
            <text class="tn-black_text">分 享</text>
          </tn-button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import navbar from "@/components/navbar.vue";
import { formatTime } from "@/utils/formate";
import { getDietPages2 } from "@/service/api/dietController";

export default {
  name: "motion-eating-detail",
  components: { navbar },
  data() {
    return {
      // 食谱详情数据
      content: {
        title: "",
        createtime: "",
        content: ""
      },
    };
  },
  onLoad(query) {
    const articleId = query.id;
    this.getArticleInfo(articleId);
  },
  methods: {
    share() {
      // 确保在小程序平台下调用
      if (uni.getSystemInfoSync().platform === "mp-weixin") {
        uni.share({
          provider: "weixin",
          scene: "WXSceneTimeline",
          type: 2,
          imageUrl: "https://assets.example.invalid/placeholder.png",
          success: function (res) {
            console.log("分享成功", res);
          },
          fail: function (err) {
            console.log("分享失败", err);
          },
        });
      } else {
        console.log("当前平台不支持分享功能");
      }
    },

    tn(e) {
      uni.navigateTo({
        url: e,
      });
    },
    
    getArticleInfo(articleId) {
      getDietPages2({ id: articleId })
        .then(res => {
          if (res && res.code === 1) {
            // 成功获取数据
            const data = res.data;
            this.content = {
              title: data.title,
              createtime: formatTime(data.createTime),
              content: data.content
            };
          } else {
            // 处理请求失败的情况
            console.error("接口请求失败");
            uni.showToast({
              title: "获取食谱详情失败",
              icon: "none"
            });
          }
        })
        .catch(err => {
          // 处理请求失败的情况
          console.error("接口请求失败", err);
          uni.showToast({
            title: "网络请求失败",
            icon: "none"
          });
        });
    },
  },
};
</script>

<style lang="scss" scoped>
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
  color: #ffffff;
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
    background-color: #ffffff;
  }
}

/* 文章内容 start*/
.blogger {
  &__item {
    padding: 30rpx;
  }

  &__author {
    &__btn {
      margin-right: -12rpx;
      padding: 0 20rpx;
    }
  }

  &__desc {
    line-height: 55rpx;

    &__label {
      padding: 0 20rpx;
      margin: 0rpx 18rpx 0 0;

      &--prefix {
        color: #00ffc8;
        padding-right: 10rpx;
      }
    }

    &__content {
    }
  }

  &__content {
    margin-top: 18rpx;
    padding-right: 18rpx;

    &__data {
      line-height: 46rpx;
      text-align: justify;
      overflow: hidden;
      transition: all 0.25s ease-in-out;
    }

    &__status {
      margin-top: 10rpx;
      font-size: 26rpx;
      color: #82b2ff;
    }
  }

  &__main-image {
    border-radius: 16rpx;

    &--1 {
      max-width: 80%;
      max-height: 300rpx;
    }

    &--2 {
      max-width: 260rpx;
      max-height: 260rpx;
    }

    &--3 {
      height: 212rpx;
      width: 100%;
    }
  }

  &__count-icon {
    font-size: 40rpx;
    padding-right: 5rpx;
  }

  &__ad {
    width: 100%;
    height: 500rpx;
    transform: translate3d(0px, 0px, 0px) !important;

    :deep(.uni-swiper-slide-frame) {
      transform: translate3d(0px, 0px, 0px) !important;
    }

    .uni-swiper-slide-frame {
      transform: translate3d(0px, 0px, 0px) !important;
    }

    &__item {
      position: absolute;
      width: 100%;
      height: 100%;
      transform-origin: left center;
      transform: translate3d(100%, 0px, 0px) scale(1) !important;
      transition: transform 0.25s ease-in-out;
      z-index: 1;

      &--0 {
        transform: translate3d(0%, 0px, 0px) scale(1) !important;
        z-index: 4;
      }

      &--1 {
        transform: translate3d(13%, 0px, 0px) scale(0.9) !important;
        z-index: 3;
      }

      &--2 {
        transform: translate3d(26%, 0px, 0px) scale(0.8) !important;
        z-index: 2;
      }
    }

    &__content {
      border-radius: 40rpx;
      width: 640rpx;
      height: 500rpx;
      overflow: hidden;
    }

    &__image {
      width: 100%;
      height: 100%;
    }
  }
}

.blogger__content {
  background-color: white;
  // text-align: left;
  max-width: 97%;
  margin: 0 auto; /* 居中 */
  padding: 20px; /* 根据需要调整内边距 */
}

/* 文章内容 end*/

/* 间隔线 start*/
.tn-strip-bottom {
  width: 100%;
  border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
}

/* 间隔线 end*/

/* 头像 start */
.logo-image {
  width: 60rpx;
  height: 60rpx;
  position: relative;
}

.logo-pic {
  background-size: cover;
  background-repeat: no-repeat;
  // background-attachment:fixed;
  background-position: top;
  box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
  border-radius: 50%;
  overflow: hidden;
  // background-color: #FFFFFF;
}

/* 底部 start*/
.footerfixed {
  position: fixed;
  width: 100%;
  bottom: 0;
  z-index: 999;
  background-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
}

.tabbar {
  align-items: center;
  min-height: 130rpx;
  padding: 0;
  height: calc(130rpx + env(safe-area-inset-bottom) / 2);
  padding-bottom: calc(30rpx + env(safe-area-inset-bottom) / 2);
  padding-left: 10rpx;
  padding-right: 10rpx;
}

/* 毛玻璃*/
.dd-glass {
  width: 100%;
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
}

/* 头像*/
.avatar-all {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.05);
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
}

/* 内容*/
.topic {
  position: relative;
  height: 100%;
  z-index: 1;
  margin-bottom: 120rpx;

  /* 表单信息 start */
  &__info {
    margin: 0 50rpx;
    margin-top: 105rpx;
    padding: 30rpx 51rpx;
    border-radius: 20rpx;
    background-color: rgba(255, 255, 255, 1);
    border: 2rpx solid rgba(255, 255, 255, 0.1);
    box-shadow: 0rpx 10rpx 50rpx 0rpx rgba(0, 3, 72, 0.1);

    &__item {
      &__input {
        width: 400rpx;
        height: 60rpx;
        border: 1rpx solid #c6d1d8;
        border-radius: 39rpx;

        &__left-icon {
          width: 10%;
          font-size: 44rpx;
          margin-left: 20rpx;
          margin-right: 5rpx;
          color: #c6d1d8;
        }

        &__content {
          width: 80%;
          padding-left: 10rpx;

          &--verify-code {
            width: 56%;
          }

          input {
            font-size: 30rpx;
            color: #78909c;
            // letter-spacing: 0.1em;
          }
        }

        &__right-icon {
          width: 10%;
          font-size: 34rpx;
          margin-right: 20rpx;
          color: #78909c;
        }

        &__right-verify-code {
          width: 34%;
          margin-right: 20rpx;
        }
      }

      &__button {
        width: 100%;
        height: 60rpx;
        text-align: center;
        font-size: 31rpx;
        font-weight: bold;
        line-height: 77rpx;
        // text-indent: 1em;
        border-radius: 100rpx;
        color: #ffffff;
        background-color: rgba(255, 255, 255, 0.2);
        // border: 2rpx solid #FFFFFF;
      }

      &__sure {
        height: 60rpx;
        width: 140rpx;
      }
    }
  }

  /* 表单信息 end */

  /* 内容 end */
}

:deep(.input-placeholder) {
  font-size: 30rpx;
  color: #c6d1d8;
}
</style>
