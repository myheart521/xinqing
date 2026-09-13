<template>
  <view class="template-preferred tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <navbar title="健康食谱"></navbar>
    
    <!-- 下拉刷新组件 -->
    <pulldownRefresh
      ref="refreshRef"
      :top="0"
      :threshold="80"
      @refresh="onRefresh"
      @setEnableScroll="setEnableScroll"
    >
      <scroll-view
        class="scroll-container"
        scroll-y
      >
        <view>
          <view class="tn-flex tn-mt-xl">
            <view class="tn-m tn-text-bold tn-text-xl">
              健康食谱
            </view>
          </view>
          
          <!-- 页面内容 -->
          <view>
            <!-- 加载中状态 -->
            <view v-if="loading && data.length === 0" class="loading-container tn-flex tn-flex-center-center tn-py-xl">
              <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
              <text class="loading-text">加载中...</text>
            </view>

            <!-- 无数据提示 -->
            <view v-else-if="data.length === 0" class="empty-tip tn-flex tn-flex-direction-column tn-flex-center-center">
              <tn-icon name="info-circle" size="120rpx" color="#cccccc"></tn-icon>
              <text class="tn-gray_text tn-mt-sm">暂无食谱内容</text>
            </view>
            
            <!-- 瀑布流展示 -->
            <view class="tn-p-sm" v-else>
              <tn-water-fall :data="data">
                <template #left="{item}">
                  <view class="product__item"
                        @click="tn(`/function_pages/views/motion-eating-detail?id=${item.id}`)">
                    <view class="item__image">
                      <tn-lazy-load height="100%" :src="getImageUrl(item.mainImage)"
                                    :index="item.id" mode="widthFix"></tn-lazy-load>
                    </view>
                    <view class="item__data">
                      <view class="item__title-container">
                        <text class="item__title">{{ item.title }}</text>
                      </view>
                      <view v-if="item.tags && item.tags.length > 0" class="item__tags-container">
                        <view v-for="(tagItem, tagIndex) in item.tags" :key="tagIndex"
                              class="item__tag">{{ tagItem }}</view>
                      </view>
                      <view class="item__price-container">
                        <text class="item__price--integer">{{ item.name }}</text>
                      </view>
                    </view>
                  </view>
                </template>
                <template #right="{item}">
                  <view class="product__item"
                        @click="tn(`/function_pages/views/motion-eating-detail?id=${item.id}`)">
                    <view class="item__image">
                      <tn-lazy-load height="100%" :src="getImageUrl(item.mainImage)"
                                    :index="item.id" mode="widthFix"></tn-lazy-load>
                    </view>
                    <view class="item__data">
                      <view class="item__title-container">
                        <text class="item__title">{{ item.title }}</text>
                      </view>
                      <view class="item__tags-container">
                        <view v-for="(tagItem, tagIndex) in item.tags" :key="tagIndex"
                              class="item__tag">{{ tagItem }}</view>
                      </view>
                      <view class="item__price-container">
                        <text class="item__price--integer">{{ item.name }}</text>
                      </view>
                    </view>
                  </view>
                </template>
              </tn-water-fall>
              
              <!-- 加载更多按钮 -->
              <view class="load-more-container">
                <tn-button 
                  v-if="!noMoreData && data.length > 0" 
                  @tap="loadMore" 
                  :loading="loading"
                  bg-color="#5677fc"
                  width="40%"
                  height="70rpx"
                  font-size="26rpx"
                  padding="0"
                  radius="35rpx"
                  :shadow="true"
                >
                  <view class="btn-content tn-flex tn-flex-row-center">
                    <tn-icon v-if="!loading" name="more-circle" color="#fff" size="30rpx"></tn-icon>
                    <text class="tn-ml-xs tn-white_text">{{ !loading ? '加载更多' : '加载中...' }}</text>
                  </view>
                </tn-button>
                <view v-else-if="noMoreData && data.length > 0" class="no-more-data tn-flex tn-flex-center-center">
                  <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
                  <text class="tn-gray_text tn-ml-xs">没有更多了</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </pulldownRefresh>
    
    <view class='tn-tabbar-height'></view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import navbar from '@/components/navbar.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import { getDietPages1 } from '@/service/api/dietController'
import {ossBgUrl} from "@/utils/ossUrl";
const image1 = ossBgUrl.music_picture1
const image2 = ossBgUrl.music_picture2
const image3 = ossBgUrl.music_picture3
const image4 = ossBgUrl.music_picture4
const image5 = ossBgUrl.music_picture5

// 食谱数据
const data = ref([])
const refreshRef = ref(null)
const enableScroll = ref(true)
const loading = ref(false)
const noMoreData = ref(false)

// 分页参数
const pagination = reactive({
  pageNo: 1,
  pageSize: 10
})

// 图片映射
const imageMap = {
  'image1': image1,
  'image2': image2,
  'image3': image3,
  'image4': image4,
  'image5': image5
}

// 获取图片URL
const getImageUrl = (imageName) => {
  return imageName // 如果找不到对应图片，返回默认图片
}

// 获取食谱数据
const fetchDietList = async (isRefresh = false) => {
  if (loading.value) {
    console.log('正在加载中，请稍后再试')
    return
  }
  
  loading.value = true
  
  // 如果是刷新，重置页码
  if (isRefresh) {
    pagination.pageNo = 1
    noMoreData.value = false
  }
  
  try {
    console.log('获取食谱数据，页码:', pagination.pageNo)
    
    const res = await getDietPages1({
      pageDTO: {
        current: pagination.pageNo,
        size: pagination.pageSize
      }
    })
    
    if (res && res.code === 1) {
      const newData = res.data || []
      
      if (isRefresh) {
        data.value = newData
      } else {
        data.value = [...data.value, ...newData]
      }
      
      // 判断是否还有更多数据
      if (!newData.length || newData.length < pagination.pageSize) {
        noMoreData.value = true
      } else {
        pagination.pageNo++
      }
    } else {
      uni.showToast({
        title: res?.msg || '获取数据失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取食谱数据失败:', error)
    
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = async () => {
  try {
    console.log('开始刷新数据')
    await fetchDietList(true)
    
    uni.showToast({
      title: '刷新成功',
      icon: 'success'
    })
  } catch (error) {
    console.error('刷新失败:', error)
    uni.showToast({
      title: '刷新失败',
      icon: 'error'
    })
  } finally {
    // 结束下拉刷新状态
    if (refreshRef.value) {
      refreshRef.value.endPulldownRefresh()
    }
  }
}

// 加载更多
const loadMore = () => {
  if (noMoreData.value) {
    uni.showToast({
      title: '没有更多数据了',
      icon: 'none'
    })
    return
  }
  
  fetchDietList(false)
}

// 设置是否可以滚动
const setEnableScroll = (value) => {
  enableScroll.value = value
}

// 跳转
const tn = (url) => {
  uni.navigateTo({
    url: url
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchDietList(true)
})
</script>

<style lang="scss" scoped>
.template-preferred {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.scroll-container {
  flex: 1;
  height: 100%;
}

.tn-tabbar-height {
  min-height: 120rpx;
  height: calc(140rpx + env(safe-area-inset-bottom) / 2);
}

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

/* 加载状态和空数据提示 */
.loading-container, .empty-tip {
  padding: 100rpx 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  
  .loading-text {
    font-size: 28rpx;
    color: #999999;
    margin-top: 20rpx;
  }
}

/* 瀑布流商品*/
.product__item {
  background-color: #FFFFFF;
  border-radius: 15rpx;
  overflow: hidden;
  margin: 0 15rpx 30rpx 15rpx;
  box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
  transition: all 0.3s;
  
  &:active {
    transform: scale(0.98);
  }

  .item {
    /* 图片 start */
    &__image {
      width: 100%;
      height: auto;
      background-color: #FFFFFF;
    }

    /* 内容 start */
    &__data {
      padding: 20rpx 20rpx;
    }

    /* 标题 start */
    &__title-container {
      text-align: justify;
      line-height: 38rpx;
      vertical-align: middle;
    }

    &__store-type {
      height: 28rpx;
      font-size: 20rpx;
      position: relative;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0 4rpx;
      border-radius: 6rpx;
      white-space: nowrap;
      text-align: center;
      top: -2rpx;
      margin-right: 6rpx;
    }

    &__title {}

    /* 标签 start */
    &__tags-container {
      display: flex;
      flex-direction: row;
      flex-wrap: nowrap;
      align-items: center;
      justify-content: flex-start;
    }

    &__tag {
      margin: 10rpx;
      color: #FF7043;
      border: 2rpx solid #FF7043;
      padding: 0 6rpx;
      border-radius: 10rpx;
      font-size: 20rpx;

      &:first-child {
        margin-left: 0rpx !important;
      }
    }

    /* 价格 start */
    &__price-container {
      padding: 10rpx 0;
      font-size: 24rpx;
      font-weight: bold;
    }

    &__price {
      &--integer {
        font-size: 38rpx;
      }
    }
  }
}

// 加载更多容器
.load-more-container {
  margin: 40rpx 0 60rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  
  .btn-content {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .no-more-data {
    padding: 15rpx 30rpx;
    font-size: 26rpx;
    color: #999;
    background-color: #f5f7fa;
    border-radius: 30rpx;
  }
}
</style>