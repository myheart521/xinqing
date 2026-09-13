<script setup>
import {
  ref,
  watch
} from 'vue';

// 定义props接收外部传入的标签数据
const props = defineProps({
  tags: {
    type: Array,
    default: () => []
  }
})

// 标签颜色列表
const colors = ['red', 'cyan', 'blue', 'green', 'orange', 'purplered', 'purple', 'brown', 'yellowgreen', 'grey', 'orangered']

// 处理标签数据，添加颜色
const tagList = ref([])

// 监听props变化，处理标签数据
watch(() => props.tags, (newTags) => {
  console.log("tags变化",props.tags)
  tagList.value = newTags.map((tag, index) => ({
    color: colors[index % colors.length],
    title: tag.tagName || tag.title
  }))
}, { immediate: true })

const tn = (router, tag) => {
  uni.navigateTo({
    url: `${router}?tag=${encodeURIComponent(tag)}`
  })
}
</script>

<template>
  <view class="tn-p">
    <view>
      <view class="tn-flex tn-flex-row-between">
        <view class="tn-margin tn-text-bold tn-text-xl">
          热门话题
        </view>
      </view>
    </view>

    <view class="tn-text-sm tn-grey-dark_text">
      <text>这是今日热门话题，请查收吖</text>
    </view>
    <view class="tn-tag-content tn-mt tn-text-justify">
      <view v-for="(item, index) in tagList" :key="index"
            class="tn-tag-content__item tn-mr tn-round tn-text-sm tn-text-bold"
            :class="[`tn-${item.color}-light_bg tn-${item.color}_text`]" @click="tn('/group_pages/views/hot-detail', item.title)">
        <text class="tn-tag-content__item--prefix">#</text>
        {{ item.title }}
      </view>
    </view>

  </view>
</template>

<style lang="scss">
/* 标签内容 start*/
.tn-tag-content {
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

/* 标签内容 end*/
</style>