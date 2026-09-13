<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import navbar from '@/components/navbar.vue'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'
import { queryFollowList } from '@/service/api/activityFollowController'
import { formatTime, formatDateByYear, formatRelativeTime } from '@/utils/formate'
import { useUserStore } from '@/stores/user'

// 获取用户信息
const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo.id)

// 活动数据
const activityData = reactive({
  records: [],
  total: 0,
  current: 1,
  hasMore: true
})

// 加载状态：0-加载前，1-加载中，2-没有更多了
const loadingStatus = ref(0)
const refreshRef = ref(null)
const enableScroll = ref(true)
const noMoreData = ref(false) // 是否没有更多数据

// 获取参与活动列表
const getActivityList = async (isRefresh = false) => {
  if (loadingStatus.value === 1) {
    console.log('正在加载中，请稍后再试')
    return
  }
  
  loadingStatus.value = 1
  
  // 如果是刷新，重置页码
  if (isRefresh) {
    activityData.current = 1
    noMoreData.value = false
  }
  
  try {
    const res = await queryFollowList({ 
      current: activityData.current.toString() 
    })
    
    if (res.code === 1) {
      const data = res.data || { records: [], total: 0 }
      
      if (isRefresh) {
        activityData.records = data.records || []
      } else {
        activityData.records = [...activityData.records, ...(data.records || [])]
      }
      
      activityData.total = data.total || 0
      activityData.hasMore = activityData.records.length < activityData.total
      
      // 判断是否还有更多数据
      if (!data.records || data.records.length === 0) {
        noMoreData.value = true
      } else {
        activityData.current++
      }
    } else {
      uni.showToast({
        title: res.msg || '获取数据失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取参与活动列表失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
  } finally {
    loadingStatus.value = 0
  }
}

// 判断是否是用户自己发起的活动
const isUserActivity = (userId) => {
  return userId === currentUserId.value
}

// 处理下拉刷新
const onRefresh = async () => {
  try {
    await getActivityList(true)
    
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
    refreshRef.value?.endPulldownRefresh()
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
  getActivityList()
}

// 设置是否可以滚动
const setEnableScroll = (value) => {
  enableScroll.value = value
}

// 前往活动详情页
const goToActivityDetail = (activityId) => {
  if (activityId) {
    uni.navigateTo({
      url: `/group_pages/views/active-detail?activityId=${activityId}`
    })
  }
}

// 格式化活动时间段
const formatActivityTime = (startTime, endTime) => {
  if (!startTime || !endTime) return ''
  
  const startDate = formatTime(startTime, 'MM月DD日 HH:mm')
  const endDate = formatTime(endTime, 'HH:mm')
  
  return `${startDate}-${endDate}`
}

onMounted(() => {
  getActivityList(true)
})
</script>

<template>
  <view class="my-active-container">
    <!-- 导航栏 -->
    <navbar title="我的活动"></navbar>
    
    <!-- 内容区域 -->
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
        <!-- 加载中 -->
        <view v-if="loadingStatus === 1 && activityData.records.length === 0" class="loading-container">
          <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
          <text class="loading-text">加载中...</text>
        </view>
        
        <!-- 没有数据 -->
        <view v-else-if="activityData.records.length === 0" class="empty-container">
          <tn-icon name="info" color="#999999" size="60rpx"></tn-icon>
          <text class="empty-text">您还没有参与活动~</text>
        </view>
        
        <!-- 活动列表 -->
        <view v-else class="activity-list">
          <view 
            v-for="item in activityData.records" 
            :key="item.id"
            class="activity-item"
            @click="goToActivityDetail(item.id)"
          >
            <view class="activity-header">
              <view class="activity-time">
                <tn-icon name="clock" color="#5677fc" size="32rpx"></tn-icon>
                <text class="time-text">{{ formatActivityTime(item.startTime, item.endTime) }}</text>
              </view>
              <view class="activity-tag-container">
                <!-- 我发起的活动标签 -->
                <view v-if="isUserActivity(item.userId)" class="my-activity-tag">
                  <tn-icon name="flag" color="#fff" size="22rpx"></tn-icon>
                  <text class="tag-text">我发起的活动</text>
                </view>
                <view class="activity-tag" :class="`tag-${item.color || 'blue'}`">
                  {{ item.label || '活动' }}
                </view>
              </view>
            </view>
            
            <view class="activity-content">
              <image 
                v-if="item.mainImage && item.mainImage.length > 0"
                class="activity-image" 
                :src="item.mainImage[0]" 
                mode="aspectFill"
              ></image>
              <view class="activity-info">
                <view class="activity-title">{{ item.title }}</view>
                <view class="activity-desc">{{ item.content }}</view>
                <view class="activity-location" v-if="item.address">
                  <tn-icon name="location" color="#999" size="30rpx"></tn-icon>
                  <text class="location-text">{{ item.address }}</text>
                </view>
                <view class="activity-stats">
                  <view class="stats-item">
                    <tn-icon name="friend" color="#999" size="28rpx"></tn-icon>
                    <text class="stats-text">{{ item.follow || 0 }}人参与</text>
                  </view>
                  <view class="stats-item">
                    <tn-icon name="eye" color="#999" size="28rpx"></tn-icon>
                    <text class="stats-text">{{ item.viewUserCount || 0 }}人浏览</text>
                  </view>
                  <view class="stats-item">
                    <tn-icon name="time" color="#999" size="28rpx"></tn-icon>
                    <text class="stats-text">{{ formatRelativeTime(item.createTime) }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
          
          <!-- 加载更多按钮 -->
          <view class="load-more-container">
            <tn-button 
              v-if="!noMoreData && activityData.records.length > 0" 
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
            <view v-else-if="noMoreData && activityData.records.length > 0" class="no-more-data tn-flex tn-flex-center-center">
              <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
              <text class="tn-gray_text tn-ml-xs">没有更多了</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<style scoped lang="scss">
.my-active-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f8f8;
}

:deep(.refresh-content) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.scroll-container {
  flex: 1;
  height: 100%;
}

.loading-container, .empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  
  .loading-text, .empty-text {
    font-size: 28rpx;
    color: #999999;
    margin-top: 20rpx;
  }
}

.activity-list {
  padding: 20rpx;
}

.activity-item {
  background-color: #ffffff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .activity-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 15rpx;
    border-bottom: 1px solid #f5f5f5;
    
    .activity-time {
      display: flex;
      align-items: center;
      
      .time-text {
        margin-left: 10rpx;
        font-size: 26rpx;
        color: #5677fc;
      }
    }
    
    .activity-tag-container {
      display: flex;
      align-items: center;
      
      .my-activity-tag {
        display: flex;
        align-items: center;
        padding: 4rpx 12rpx;
        border-radius: 6rpx;
        font-size: 22rpx;
        color: #ffffff;
        background-color: #6739b6;
        margin-right: 10rpx;
        
        .tag-text {
          margin-left: 6rpx;
        }
      }
    }
    
    .activity-tag {
      padding: 4rpx 12rpx;
      border-radius: 6rpx;
      font-size: 22rpx;
      color: #ffffff;
      
      &.tag-red {
        background-color: #FF4A4A;
      }
      
      &.tag-blue {
        background-color: #5677fc;
      }
      
      &.tag-green {
        background-color: #4CD964;
      }
      
      &.tag-orange {
        background-color: #FFAA00;
      }
      
      &.tag-purple {
        background-color: #6739b6;
      }
    }
  }
  
  .activity-content {
    display: flex;
    margin-top: 15rpx;
    
    .activity-image {
      width: 180rpx;
      height: 180rpx;
      border-radius: 8rpx;
      margin-right: 20rpx;
    }
    
    .activity-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      
      .activity-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333333;
        margin-bottom: 10rpx;
      }
      
      .activity-desc {
        font-size: 26rpx;
        color: #666666;
        margin-bottom: 10rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      
      .activity-location {
        display: flex;
        align-items: center;
        margin-bottom: 10rpx;
        
        .location-text {
          margin-left: 6rpx;
          font-size: 24rpx;
          color: #999999;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          flex: 1;
        }
      }
      
      .activity-stats {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        margin-top: 10rpx;
        
        .stats-item {
          display: flex;
          align-items: center;
          margin-right: 20rpx;
          
          .stats-text {
            margin-left: 6rpx;
            font-size: 24rpx;
            color: #999999;
          }
        }
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