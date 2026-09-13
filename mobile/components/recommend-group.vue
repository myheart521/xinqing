<script setup>
import {
  ref, defineProps, defineEmits
} from 'vue';
import
  TnGraphicCard
  from 'tnuiv3p-tn-graphic-card/index.vue'


const props = defineProps({
  content: {
    type: Array,
    default: () => []
  }
})

const emits = defineEmits(['clickGroup'])

const clickGroup = (item) => {
  emits('clickGroup', item)
}

const tn = (router) => {
  console.log(router)
}
</script>

<template>
  <view>
    <view v-for="(item,index) in content" :key="item.id" @click="clickGroup(item)">
      <TnGraphicCard 
        :avatar="item.userAvatar" 
        :title="item.userName" 
        :description="item.userProfile"
        :tags="item.label"
        :content="item.title"
        :images="item.mainImage || []"
        :view-count="item.viewUserCount"
        :comment-count="item.comments"
        :like-count="item.liked"
        :view-user-avatars="item.likedUser"
        :show-view="false" 
        active-like
      ></TnGraphicCard>
    </view>
  </view>
</template>

<style lang="scss">
/* 文章内容 start*/
.blogger {
  &__item {
    padding: 30rpx;
  }

  &__author {
    &__btn {
      margin-right: -12rpx;
      opacity: 0.5;
    }
  }

  &__desc {
    line-height: 55rpx;

    &__tags {
      padding: 0 20rpx;
      margin: 0rpx 18rpx 0 0;

      &--prefix {
        color: #00FFC8;
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
      color: #82B2FF;
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

    ::v-deep .uni-swiper-slide-frame {
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

/* 文章内容 end*/
</style>