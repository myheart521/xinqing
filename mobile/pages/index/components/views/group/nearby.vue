<script setup>
import {ref, onMounted, defineExpose, computed} from 'vue'
import {getLocation} from "@/utils/location-get"
import {queryBlogByGeo, queryUserByGeo} from '@/service/api/nearController'
import recommendGroupVue from '@/components/recommend-group.vue'
import blackTitle from '@/components/black-title.vue'

import nearbyAvatar from '@/components/nearby-avatar.vue'

const location = ref({})
const userAvatar = ref([])
const content = ref([])
const hasLocation = ref(false) // 添加标记
const loading = ref(true) // 加载状态

// 分页相关
const page = ref(1)
const pageSize = ref(10)
const loadingStatus = ref(0) // 0加载前，1加载中，2没有更多了
const noMoreData = ref(false) // 是否没有更多数据
//占位
const androidHeight = ref(false)

const displayAddress = computed(() => {
  const loc = location.value || {}
  const addr = typeof loc.address === 'string' ? loc.address : ''
  const formatted = typeof loc.formatted_addresses === 'string' ? loc.formatted_addresses : ''
  const text = (addr + formatted).trim()
  return text || '定位中...'
})

// 获取定位信息
const handleGetLocation = async () => {
  loading.value = true
  try {
    location.value = await getLocation()
    console.log('定位信息', location.value)
    hasLocation.value = true // 标记定位成功
    // 获取到定位后加载数据
    await Promise.all([
      getNearbyUsers(),
      getNearbyBlogs(true)
    ])
  } catch (error) {
    console.error('获取定位失败', error)
    uni.showToast({
      title: '获取定位失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 获取附近的用户
const getNearbyUsers = async () => {
  if (!hasLocation.value) {
    console.log('等待定位...')
    return
  }
  console.log('开始获取附近的用户...')

  try {
    const res = await queryUserByGeo({
      latitude: location.value.latitude,
      longitude: location.value.longitude
    })
    console.log('附近用户数据:', res)
    if (res.code === 1) {
      userAvatar.value = res.data || []
    } else {
      uni.showToast({
        title: res.msg || '获取附近用户失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取附近用户失败:', error)
  }
}

// 获取附近的博客
const getNearbyBlogs = async (isRefresh = false) => {
  if (!hasLocation.value) {
    console.log('等待定位...')
    return
  }

  if (loadingStatus.value === 1) {
    console.log('正在加载中，请稍后再试')
    return
  }

  loadingStatus.value = 1
  try {
    console.log('开始获取附近的博客...page', page.value)
    const res = await queryBlogByGeo({
      current: page.value,
      latitude: location.value.latitude,
      longitude: location.value.longitude
    })
    console.log('附近博客数据:', res)

    if (res.code === 1) {
      // 确保res.data是数组，如果是null则转为空数组
      const dataArray = Array.isArray(res.data) ? res.data : []
      
      if (isRefresh) {
        content.value = dataArray
      } else {
        content.value = [...content.value, ...dataArray]
      }

      // 判断是否还有更多数据
      if (!dataArray.length) {
        console.log('没有更多数据了，数据长度: 0')
        noMoreData.value = true
      } else {
        console.log('，页码加1，数据长度:', dataArray.length)
        noMoreData.value = false
        page.value++
      }
    } else {
      uni.showToast({
        title: res.msg || '获取附近动态失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取附近动态失败:', error)
  } finally {
    loadingStatus.value = 0
  }
}

// 点击群组
const clickGroup = (item) => {
  console.log('点击了群组', item)
  // 这里可以添加点击群组的处理逻辑
}

// 重置加载状态
const resetLoadMore = () => {
  page.value = 1
  loadingStatus.value = 0
  noMoreData.value = false
}

// 刷新数据
const refresh = async () => {
  loading.value = true
  if (!hasLocation.value) {
    await handleGetLocation()
  } else {
    resetLoadMore()
    await Promise.all([
      getNearbyUsers(),
      getNearbyBlogs(true)
    ])
  }
  loading.value = false

  // 显示刷新成功提示
  uni.showToast({
    title: '刷新成功',
    icon: 'success',
    duration: 1500
  })

  return true
}

// 加载更多
const loadMore = async () => {
  if (noMoreData.value) {
    uni.showToast({
      title: '没有更多数据了',
      icon: 'none'
    })
    return false
  }

  if (loadingStatus.value === 1) {
    return false
  }

  await getNearbyBlogs(false)
  return true
}

// 暴露方法给父组件
defineExpose({
  refresh,
  loadMore,
  resetAndRefresh: refresh
})

onMounted(() => {
  handleGetLocation()

  // #ifdef APP-ANDROID
  androidHeight.value = true
  // #endif
})
</script>

<template>
  <view class="nearby-container">
    <view style="height: 69px" v-if="androidHeight"></view>
    <!-- 顶部位置信息卡片 -->
    <view class="location-card tn-shadow-sm">
      <view class="location-header tn-flex tn-flex-row-between tn-flex-col-center">
        <view class="tn-flex tn-flex-row-center">
          <tn-icon name="location-fill" size="40rpx" color="#5677fc"></tn-icon>
          <text class="location-title tn-text-bold tn-ml-sm">{{ displayAddress }}</text>
        </view>
        <view class="refresh-btn" @tap="refresh">
          <tn-icon name="refresh" size="36rpx" color="#5677fc"></tn-icon>
        </view>
      </view>

      <!-- 地图区域 -->
      <view class="map-container tn-mt-sm">
        <map
            :latitude="location.latitude || 39.908823"
            :longitude="location.longitude || 116.397470"
            scale="16"
            :show-compass="true"
            :show-location="true"
            class="map"
            :markers="[{
            id: 1,
            latitude: location.latitude,
            longitude: location.longitude,
            iconPath: '/static/images/marker.png',
            width: 32,
            height: 32,
            callout: {
              content: '我在这里',
              color: '#ffffff',
              fontSize: 12,
              borderRadius: 4,
              bgColor: '#5677fc',
              padding: 6,
              display: 'ALWAYS'
            }
          }]"
        ></map>
      </view>
    </view>

    <!-- 附近的用户 -->
    <view class="section-container tn-mt-lg tn-shadow-sm">
      <view class="section-header">
        <black-title name="my-add" title="附近的用户"/>
        <view class="section-subtitle tn-gray_text">发现身边的朋友</view>
      </view>

      <tn-read-more height="360rpx" :showHeight="true">
        <view class="user-grid tn-flex tn-flex-wrap">
          <nearbyAvatar
              v-for="(item, index) in userAvatar"
              :key="index"
              :url="item.url"
              :desc="item.desc"
              :name="item.name"
              :sex="item.sex"
              class="user-item"
          ></nearbyAvatar>

          <!-- 无数据提示 -->
          <view v-if="userAvatar.length === 0" class="empty-tip tn-flex tn-flex-direction-column tn-flex-center-center">
            <tn-icon name="user" size="100rpx" color="#cccccc"></tn-icon>
            <text class="tn-gray_text tn-mt-sm">暂无附近用户</text>
          </view>
        </view>
      </tn-read-more>
    </view>

    <!-- 附近的消息 -->
    <view class="section-container tn-mt-lg tn-shadow-sm">
      <view class="section-header">
        <black-title name="topics" title="附近的消息"/>
        <view class="section-subtitle tn-gray_text">了解周边动态</view>
      </view>

      <!-- 内容为空时的提示 -->
      <view v-if="content.length === 0 && !loading"
            class="empty-tip tn-flex tn-flex-direction-column tn-flex-center-center">
        <tn-icon name="message" size="100rpx" color="#cccccc"></tn-icon>
        <text class="tn-gray_text tn-mt-sm">暂无附近消息</text>
      </view>

      <!-- 加载中提示 -->
      <view v-if="loading" class="loading-container tn-flex tn-flex-center-center tn-py-xl">
        <tn-loading></tn-loading>
      </view>

      <!-- 消息列表 -->
      <view v-else>
        <recommendGroupVue :content="content" @clickGroup="clickGroup"></recommendGroupVue>

        <!-- 加载更多按钮 -->
        <view class="load-more-container">
          <tn-button v-if="!noMoreData"
                     @tap="loadMore"
                     :loading="loadingStatus === 1"
                     bg-color="#5677fc"
                     width="40%"
                     height="70rpx"
                     font-size="26rpx"
                     padding="0"
                     radius="35rpx"
                     :shadow="true"
          >
            <view class="btn-content tn-flex tn-flex-row-center">
              <tn-icon v-if="loadingStatus !== 1" name="more-circle" color="#fff" size="30rpx"></tn-icon>
              <text class="tn-ml-xs tn-white_text">{{ loadingStatus !== 1 ? '加载更多' : '加载中...' }}</text>
            </view>
          </tn-button>
          <view v-else class="no-more-data tn-flex tn-flex-center-center">
            <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
            <text class="tn-gray_text tn-ml-xs">没有更多了</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.nearby-container {
  padding: 30rpx;
  background-color: #f8f9fc;
  min-height: 100vh;
}

// 位置卡片样式
.location-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 30rpx;
  overflow: hidden;

  .location-header {
    .location-title {
      font-size: 32rpx;
      color: #333;
      max-width: 500rpx;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .refresh-btn {
      width: 70rpx;
      height: 70rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background-color: #f5f7fa;
      transition: all 0.3s;

      &:active {
        transform: rotate(180deg);
        background-color: #e8eaed;
      }
    }
  }

  .map-container {
    width: 100%;
    height: 360rpx;
    border-radius: 16rpx;
    overflow: hidden;

    .map {
      width: 100%;
      height: 100%;
    }
  }
}

// 区块容器样式
.section-container {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 30rpx;

  .section-header {
    margin-bottom: 20rpx;

    .section-subtitle {
      font-size: 24rpx;
      margin-top: 6rpx;
      padding-left: 20rpx;
    }
  }

  .user-grid {
    padding: 10rpx 0;

    .user-item {
      margin-bottom: 30rpx;
      transition: all 0.3s;

      &:active {
        transform: scale(0.95);
      }
    }
  }
}

// 空状态提示
.empty-tip {
  padding: 80rpx 0;
  width: 100%;
}

// 加载状态
.loading-container {
  padding: 40rpx 0;
}

// 加载更多容器
.load-more-container {
  margin: 40rpx 0 20rpx;
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