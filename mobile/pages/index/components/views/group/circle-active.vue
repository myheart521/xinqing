<script setup>
import {ref, onMounted, defineExpose} from 'vue'
import {selectAll1} from '@/service/api/activityController'
import {onLoad} from '@dcloudio/uni-app'
// 活动列表数据
const reserve = ref([])
const loading = ref(true)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const loadingStatus = ref(0) // 0加载前，1加载中，2没有更多了
const noMoreData = ref(false) // 是否没有更多数据
//占位
const androidHeight = ref(false)
// 获取活动列表
const getActivityList = async (isRefresh = false) => {
  if (loadingStatus.value === 1) {
    console.log('正在加载中，请稍后再试')
    return
  }

  loading.value = isRefresh
  loadingStatus.value = 1
  try {
    console.log('获取活动列表,currentPage:', currentPage.value)
    const res = await selectAll1({
      current: currentPage.value
    })

    if (res.code === 1) {
      // 确保res.data是数组，如果是null则转为空数组
      const dataArray = Array.isArray(res.data) ? res.data : []
      
      const newData = dataArray.map(item => ({
        id: item.id,
        userAvatar: item.userAvatar || 'https://assets.example.invalid/placeholder.png',
        userName: item.userName || '活动发起人',
        title: item.title || '未命名活动',
        mainImage: item.mainImage && item.mainImage.length > 0
            ? item.mainImage[0]
            : 'https://assets.example.invalid/placeholder.png',
        color: item.color || 'red',
        label: [item.tag || '未分类'],
    viewUser: {
      latestUserAvatar: [{
            src: item.userAvatar || 'https://assets.example.invalid/placeholder.png'
          }],
          viewUserCount: item.viewUserCount || 0
        },
        collectionCount: item.follow || 0,
        likeCount: item.heatCount || 0,
        createTime: item.createTime || new Date().toISOString()
      }))

      if (isRefresh) {
        reserve.value = newData
      } else {
        reserve.value = [...reserve.value, ...newData]
      }

      // 判断是否还有更多数据
      if (!dataArray.length) {
        console.log('没有更多数据了，数据长度: 0')
        noMoreData.value = true
      } else {
        console.log('，页码加1，数据长度:', dataArray.length)
        noMoreData.value = false
        currentPage.value++
      }
    } else {
      uni.showToast({
        title: res.msg || '获取数据失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
  } finally {
    loadingStatus.value = 0
    loading.value = false
  }
}

// 重置加载状态
const resetLoadMore = () => {
  currentPage.value = 1
  loadingStatus.value = 0
  noMoreData.value = false
}

// 提供给父组件调用的刷新方法
const refresh = async () => {
  resetLoadMore()
  await getActivityList(true)

  // 显示刷新成功提示
  uni.showToast({
    title: '刷新成功',
    icon: 'success',
    duration: 1500
  })

  return true
}

// 重置并刷新 - 提供给父组件调用
const resetAndRefresh = async () => {
  resetLoadMore()
  await getActivityList(true)
  return true
}

// 加载更多 - 提供给父组件调用
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

  await getActivityList(false)
  return true
}

const tn = (router, id) => {
  uni.navigateTo({
    url: `${router}?activityId=${id}`
  })
}

onLoad(()=>{
  getActivityList(true)
  // #ifdef APP-ANDROID
  androidHeight.value = true
  // #endif

})


// 暴露方法给父组件
defineExpose({
  refresh,
  resetAndRefresh,
  loadMore
})
</script>

<template>
  <view class="activity-container">
    <view style="height: 69px"></view>
    <!-- 页面标题 -->
    <view class="page-header">
      <view class="page-title tn-flex tn-flex-row-between tn-flex-col-center">
        <text class="title-text">活动广场</text>
        <view class="refresh-btn" @tap="refresh">
          <tn-icon name="refresh" size="36rpx" color="#5677fc"></tn-icon>
        </view>
      </view>
      <view class="page-subtitle">发现精彩活动，结交志同道合的朋友</view>
    </view>

    <!-- 加载中状态 -->
    <view v-if="loading && reserve.length === 0" class="loading-container tn-flex tn-flex-center-center tn-py-xl">
      <tn-loading></tn-loading>
    </view>

    <!-- 无数据提示 -->
    <view v-else-if="reserve.length === 0" class="empty-tip tn-flex tn-flex-direction-column tn-flex-center-center">
      <tn-icon name="calendar" size="120rpx" color="#cccccc"></tn-icon>
      <text class="tn-gray_text tn-mt-sm">暂无活动信息</text>
    </view>

    <!-- 活动列表 -->
    <view v-else class="activity-list">
      <view
          v-for="(item, index) in reserve"
          :key="index"
          class="activity-card tn-shadow-sm"
          hover-class="activity-card-hover"
          @click="tn('/group_pages/views/active-detail', item.id)"
      >

        <!-- 活动内容 -->
        <view class="activity-content tn-flex tn-flex-row">
          <!-- 活动图片 -->
          <view class="activity-image">
            <image :src="item.mainImage" mode="aspectFill" class="main-image"></image>
          </view>

          <!-- 活动信息 -->
          <view class="activity-info">
            <!-- 活动标题 -->
            <view class="activity-title tn-text-lg tn-text-bold tn-text-ellipsis-2">
              {{ item.title }}
            </view>
            <!-- 参与人员 -->
            <view class="activity-participants tn-flex tn-flex-row-between tn-flex-col-center">
              <view class="tn-flex tn-flex-row-center">
                <text class="participant-count tn-gray_text tn-ml-xs">{{ item.collectionCount }} 人参与</text>
              </view>
            </view>
            <!-- 活动标签和热度 -->
            <view class="activity-footer tn-flex tn-flex-center-between">
              <!-- 标签 -->
              <view
                  v-if="item.label[0]"
                  class="activity-tag tn-round"
                  :class="[`tn-gradient-bg__${item.color}-light tn-${item.color}_text`]"
              >
                <text class="tag-prefix">#</text>
                {{ item.label[0] }}
              </view>
              <view v-else class="activity-tag tn-round tn-gradient-bg__gray-light tn-gray_text">
                <text class="tag-prefix">#</text>
                未分类
              </view>

              <!-- 热度指标 -->
              <view class="activity-stats tn-flex tn-flex-row-center tn-ml-xs">
                <view class="stat-item tn-flex tn-flex-start-center">
                  <tn-icon name="eye" size="32rpx" color="#999"></tn-icon>
                  <text class="stat-value tn-gray_text tn-ml-xs">{{ item.viewUser.viewUserCount }}</text>
                </view>
                <view class="stat-item tn-flex tn-flex-start-center tn-ml-sm">
                  <tn-icon name="fire" size="32rpx" color="#FF7043"></tn-icon>
                  <text class="stat-value tn-gray_text tn-ml-xs">{{ item.likeCount }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

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

    <!-- 底部安全区域 -->
    <view class='tn-tabbar-height'></view>
  </view>
</template>

<style scoped lang="scss">
.activity-container {
  padding: 30rpx;
  background-color: #f8f9fc;
  min-height: 100vh;
}

// 页面标题
.page-header {
  margin-bottom: 30rpx;

  .page-title {
    margin-bottom: 10rpx;

    .title-text {
      font-size: 40rpx;
      font-weight: bold;
      color: #333;
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

  .page-subtitle {
    font-size: 26rpx;
    color: #999;
  }
}

// 活动卡片
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.activity-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 30rpx;
  transition: all 0.3s;

  &-hover {
    transform: scale(0.98);
    box-shadow: 0 10rpx 20rpx rgba(0, 0, 0, 0.05);
  }
}

// 活动发起人
.activity-author {
  margin-bottom: 20rpx;

  .author-name {
    font-size: 28rpx;
    color: #333;
  }

  .activity-time {
    font-size: 24rpx;
  }
}

// 活动内容
.activity-content {
  .activity-image {
  width: 200rpx;
  height: 200rpx;
    border-radius: 16rpx;
    overflow: hidden;
    margin-right: 20rpx;

    .main-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .activity-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .activity-title {
      margin-bottom: 10rpx;
      line-height: 1.4;
    }

    .activity-participants {
      margin-bottom: 20rpx;

      .participant-count {
        font-size: 24rpx;
      }
    }

    .activity-footer {
      .activity-tag {
        font-size: 24rpx;
        font-weight: bold;
        padding: 6rpx 20rpx;

        .tag-prefix {
          margin-right: 6rpx;
        }
      }

      .activity-stats {
        .stat-item {
          .stat-value {
            font-size: 24rpx;
          }
        }
      }
    }
  }
}

// 加载和空状态
.loading-container, .empty-tip {
  padding: 100rpx 0;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

// 加载更多容器
.load-more-container {
  margin: 40rpx 0;
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

// 底部安全区域
.tn-tabbar-height {
  min-height: 120rpx;
  height: calc(140rpx + env(safe-area-inset-bottom) / 2);
}
</style>