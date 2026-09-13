<template>
  <view class="page-main">
    <view class="search-header">
      <image class="search-img" :src="searchImg"></image>
      <input class="search-input" :placeholder="placeholder" type="text" v-model.trim="searchText"
             @confirm="confirmSearch" @input="inputSearch" placeholder-class="placeholder-name"/>
      <tn-button size="lg" @click="confirmSearch">搜索</tn-button>
    </view>
    <view class="search-history" v-if="historyList.length">
      <view class="history-title">
        <view class="title-name">搜索历史</view>
        <view class="history-delete" v-if="isDelete">
          <view class="delete-all" @click="deleteAll">全部删除</view>
          <view class="delete-line"></view>
          <view class="delete-complete" @click="deleteHistory">完成</view>
        </view>
        <image v-else class="title-img" :src="deleteImg" @click="deleteHistory"></image>
      </view>
      <view class="history-list">
        <view class="history-name" :style="isDelete?'padding:0 32rpx 0 16rpx;':''"
              v-for="(item,index) in historyList" :key="index" @click="clickHis(item,index)">
          {{ item }}
          <span class="delete-icon" v-if="isDelete">x</span>
        </view>
      </view>
    </view>
    <view class="search-history" v-if="hotList.length">
      <view class="history-title">
        <view class="title-name">热门搜索</view>
        <image class="title-img" :src="seeMore?seeImg:noSeeImg" @click="seeMore=!seeMore"></image>
      </view>
      <view class="history-list" v-if="seeMore">
        <view class="history-name" v-for="(item,index) in hotList" :key="index" @click="clickHis(item)">
          {{ item }}
        </view>
      </view>
    </view>
    <scroll-view class="blur-list" v-if="searchText && blurList.length>0" scroll-y="true" scroll-with-animation>
      <view class="blur-item" v-for="(item,index) in blurList" :key="index" @click.stop="clickHis(item)">{{ item }}
      </view>
    </scroll-view>
  </view>
</template>
<script setup>
import {
  onMounted,
  ref
} from "vue";
import seeImg from "@/static/search/see.png"
import noSeeImg from "@/static/search/nosee.png"
import deleteImg from "@/static/search/delete.png"
import searchImg from "@/static/search/search.png"

const historyList = ref([])
const searchText = ref('') //搜索内容
const seeMore = ref(true)
const isDelete = ref(false)
const emits = defineEmits(['input', 'change'])

const props = defineProps({
  //搜索历史缓存key(多个页面使用时需要传)
  storageKey: {
    type: String,
    default: 'search-name-list',
  },
  // 输入框placeholder
  placeholder: {
    type: String,
    default: '请输入',
  },
  //模糊搜索内容
  blurList: {
    type: Array,
    default() {
      return []
    }
  },
  //热门搜索
  hotList: {
    type: Array,
    default() {
      return []
    }
  }
})

onMounted(() => {
  historyList.value = uni.getStorageSync(props.storageKey) || []
})

//删除历史或者完成
const deleteHistory = () => {
  isDelete.value = !isDelete.value
}
//全部删除
const deleteAll = () => {
  uni.setStorageSync(props.storageKey, '')
  historyList.value = []
  isDelete.value = !isDelete.value
}
//点击热门搜索
const clickHis = (name, index) => {
  if (isDelete.value) {
    historyList.value.splice(index, 1)
    uni.setStorageSync(props.storageKey, historyList.value)
  } else {
    searchText.value = name
    confirmSearch()
  }
}
//搜索框输入事件
const inputSearch = () => {
  emits('input', searchText.value)
}
//搜索
const confirmSearch = () => {
  let oldList = uni.getStorageSync(props.storageKey) || []
  let hasName = false
  oldList.forEach(res => {
    if (res === searchText.value) hasName = true
  })
  if (!hasName && searchText.value) {
    oldList.push(searchText.value)
    uni.setStorageSync(props.storageKey, oldList)
    historyList.value = uni.getStorageSync(props.storageKey) || []
  }
  emits('change', searchText.value)
}
</script>
<style>
/deep/ ::-webkit-scrollbar {
  width: 0;
  height: 0;
  color: transparent;
  display: none;
}
</style>
<style lang="scss" scoped>
page {
  background-color: #f8f8f8;
}

.page-main {
  width: 100%;
  height: 100vh;
  background-color: #f8f8f8;
  display: inline-block;
  position: relative;

  .search-header {
    width: calc(100% - 64rpx);
    padding: 0 32rpx;
    height: 112rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;

    .search-img {
      position: absolute;
      left: 64rpx;
      width: 32rpx;
      height: 32rpx;
    }

    .search-input {
      padding: 0 32rpx 0 76rpx;
      width: calc(90% - 90rpx);
      height: 72rpx;
      line-height: 72rpx;
      background-color: #F2F3F5;
      border: solid #e4e4e5 1rpx;
      border-radius: 36rpx;
      font-size: 30rpx;
      color: #666666;
    }

    .placeholder-name {
      font-size: 30rpx;
      color: #999999;
    }

    .search-name {
      width: 80rpx;
      text-align: right;
      font-size: 30rpx;
      color: #666666;
    }
  }

  .search-history {
    padding: 0 20rpx;
    margin-top: 24rpx;

    .history-title {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .title-name {
        font-size: 30rpx;
        color: #666666;
        font-weight: bold;
        margin-left: 12rpx;
      }

      .title-img {
        width: 34rpx;
        height: 34rpx;
        margin-right: 12rpx;
      }

      .history-delete {
        height: auto;
        display: flex;
        align-items: center;
        justify-content: flex-end;

        .delete-all {
          font-size: 26rpx;
          color: #666666;
        }

        .delete-line {
          width: 1px;
          height: 20rpx;
          background-color: #999999;
          margin: 0 12rpx;
        }

        .delete-complete {
          font-size: 26rpx;
          color: #F71E1E;
        }
      }
    }

    .history-list {
      width: 100%;
      display: flex;
      align-items: flex-start;
      justify-content: flex-start;
      flex-wrap: wrap;
      padding: 12rpx 0;

      .history-name {
        height: 48rpx;
        line-height: 48rpx;
        padding: 0 24rpx;
        background-color: #FFFFFF;
        border-radius: 25rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 10rpx 12rpx;
        font-size: 26rpx;
        color: #666666;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 1;
        position: relative;

        .delete-icon {
          position: absolute;
          font-size: 26rpx;
          color: #e4e4e5;
          right: 12rpx;
          bottom: 2rpx;
        }
      }
    }
  }

  .blur-list {
    width: 100%;
    height: calc(100vh - 112rpx);
    background-color: #f8f8f8;
    position: absolute;
    top: 112rpx;
    box-sizing: border-box;
    padding: 0 32rpx;

    .blur-item {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      color: #666666;
      font-size: 28rpx;
      border-bottom: solid #f0f0f0 1px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 1;
    }
  }
}
</style>