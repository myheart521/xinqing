<script setup>
import { ref, reactive, onMounted } from 'vue'
import navbar from "@/components/navbar.vue"
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnTabs from '@tuniao/tnui-vue3-uniapp/components/tabs/src/tabs.vue'
import TnTabsItem from '@tuniao/tnui-vue3-uniapp/components/tabs/src/tabs-item.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'
import { getList } from '@/service/api/circleFollowController'
import { followerList } from '@/service/api/followController'

// 当前选中的标签页
const currentTab = ref(0)

// 关注的圈子数据
const circleData = reactive({
  loading: false,
  refreshing: false,
  loadingMore: false,
  hasMore: true,
  current: 1,
  total: 0,
  records: []
})

// 关注的用户数据
const userData = reactive({
  loading: false,
  refreshing: false,
  loadingMore: false,
  hasMore: true,
  current: 1,
  total: 0,
  records: []
})

// 刷新引用
const refreshRef = ref(null)
const enableScroll = ref(true)

// 获取关注的圈子列表
const getCircleList = async (isRefresh = false) => {
  if (isRefresh) {
    circleData.refreshing = true
    circleData.current = 1
  } else {
    if (circleData.loading) return
    circleData.loading = true
    
    if (!circleData.hasMore) {
      circleData.loading = false
      uni.showToast({
        icon: 'none',
        title: '没有更多数据了'
      })
      return
    }
  }
  
  try {
    const res = await getList({ current: circleData.current.toString() })
    
    if (res.code === 1 && res.data) {
      if (isRefresh || circleData.current === 1) {
        circleData.records = res.data.records || []
      } else {
        circleData.records = [...circleData.records, ...(res.data.records || [])]
      }
      
      circleData.total = res.data.total || 0
      circleData.hasMore = circleData.records.length < circleData.total
      
      if (!isRefresh) {
        circleData.current++
      }
    } else {
      uni.showToast({
        icon: 'none',
        title: '获取关注圈子失败'
      })
    }
  } catch (error) {
    console.error('获取关注圈子失败:', error)
    uni.showToast({
      icon: 'none',
      title: '获取关注圈子失败'
    })
  } finally {
    circleData.loading = false
    circleData.refreshing = false
  }
}

// 获取关注的用户列表
const getUserList = async (isRefresh = false) => {
  if (isRefresh) {
    userData.refreshing = true
    userData.current = 1
  } else {
    if (userData.loading) return
    userData.loading = true
    
    if (!userData.hasMore) {
      userData.loading = false
      uni.showToast({
        icon: 'none',
        title: '没有更多数据了'
      })
      return
    }
  }
  
  try {
    const res = await followerList({ current: userData.current.toString() })
    
    if (res.code === 1 && res.data) {
      if (isRefresh || userData.current === 1) {
        userData.records = res.data.records || []
      } else {
        userData.records = [...userData.records, ...(res.data.records || [])]
      }
      
      userData.total = res.data.total || 0
      userData.hasMore = userData.records.length < userData.total
      
      if (!isRefresh) {
        userData.current++
      }
    } else {
      uni.showToast({
        icon: 'none',
        title: '获取关注用户失败'
      })
    }
  } catch (error) {
    console.error('获取关注用户失败:', error)
    uni.showToast({
      icon: 'none',
      title: '获取关注用户失败'
    })
  } finally {
    userData.loading = false
    userData.refreshing = false
  }
}

// 加载更多
const loadMore = () => {
  if (currentTab.value === 0) {
    getCircleList()
  } else {
    getUserList()
  }
}

// 处理下拉刷新
const onRefresh = async () => {
  try {
    if (currentTab.value === 0) {
      await getCircleList(true)
    } else {
      await getUserList(true)
    }
    
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

// 设置是否可以滚动
const setEnableScroll = (value) => {
  enableScroll.value = value
}

// 前往圈子详情页
const goToCircleDetail = (circleId) => {
  if (circleId) {
    uni.navigateTo({
      url: `/group_pages/views/circle-detail?id=${circleId}`
    })
  }
}

// 前往用户圈子
const goToUserDetail = (userId) => {
  if (userId) {
    uni.navigateTo({
      url: `/group_pages/views/my-others-circle?userId=${userId}`
    })
  }
}

// 监听Tab切换
const handleTabChange = (index) => {
  if (index === 0 && circleData.records.length === 0) {
    getCircleList()
  } else if (index === 1 && userData.records.length === 0) {
    getUserList()
  }
}

// 页面加载时获取数据
onMounted(() => {
  getCircleList()
})
</script>

<template>
  <view class="my-like-container">
    <!-- 导航栏 -->
    <navbar title="我的关注"></navbar>
    
    <!-- 标签页切换 -->
    <view class="tab-container">
      <TnTabs 
        v-model="currentTab" 
        height="90rpx" 
        font-size="32" 
        active-bold 
        bar-width="60" 
        active-color="#5677fc"
        bar-color="#5677fc"
        @change="handleTabChange"
      >
        <TnTabsItem title="关注的圈子" />
        <TnTabsItem title="关注的用户" />
      </TnTabs>
    </view>
    
    <!-- 内容区域 -->
    <pulldownRefresh
      ref="refreshRef"
      :top="0"
      :threshold="80"
      @refresh="onRefresh"
      @setEnableScroll="setEnableScroll"
    >
      <scroll-view class="content-scroll" scroll-y>
        <!-- 关注的圈子 -->
        <view v-if="currentTab === 0" class="content-container">
          <!-- 加载中 -->
          <view v-if="circleData.loading && circleData.records.length === 0" class="loading-container">
            <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
            <text class="loading-text">加载中...</text>
          </view>
          
          <!-- 没有数据 -->
          <view v-else-if="circleData.records.length === 0" class="empty-container">
            <tn-icon name="info" color="#999999" size="60rpx"></tn-icon>
            <text class="empty-text">您还没有关注圈子~</text>
          </view>
          
          <!-- 圈子列表 -->
          <view v-else class="circle-list">
            <view 
              v-for="item in circleData.records" 
              :key="item.id"
              class="circle-item"
              @tap="goToCircleDetail(item.id)"
            >
              <image class="circle-avatar" :src="item.url" mode="aspectFill"></image>
              <view class="circle-info">
                <view class="circle-name">{{ item.name }}</view>
                <view class="circle-content">{{ item.content }}</view>
                <view class="circle-stats">
                  <text class="stats-text">{{ item.follow }}人关注</text>
                  <text class="stats-text">创建于 {{ formatDate(item.createTime) }}</text>
                </view>
              </view>
              <view class="circle-action">
                <TnButton 
                  bg-color="#5677fc" 
                  width="150rpx" 
                  height="60rpx" 
                  radius="30rpx"
                  font-size="24rpx"
                >
                  <view class="tn-flex tn-flex-center-center">
                    <TnIcon name="star-fill" color="#fff" size="28rpx"></TnIcon>
                    <text class="tn-white_text tn-ml-xs">已关注</text>
                  </view>
                </TnButton>
              </view>
            </view>
            
            <!-- 加载更多按钮 -->
            <view class="load-more-container">
              <tn-button 
                v-if="circleData.hasMore" 
                @tap="loadMore" 
                :loading="circleData.loading"
                bg-color="#5677fc"
                width="40%"
                height="70rpx"
                font-size="26rpx"
                padding="0"
                radius="35rpx"
                :shadow="true"
              >
                <view class="btn-content tn-flex tn-flex-row-center">
                  <tn-icon v-if="!circleData.loading" name="more-circle" color="#fff" size="30rpx"></tn-icon>
                  <text class="tn-ml-xs tn-white_text">{{ !circleData.loading ? '加载更多' : '加载中...' }}</text>
                </view>
              </tn-button>
              <view v-else-if="!circleData.hasMore && circleData.records.length > 0" class="no-more-data tn-flex tn-flex-center-center">
                <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
                <text class="tn-gray_text tn-ml-xs">没有更多了</text>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 关注的用户 -->
        <view v-else class="content-container">
          <!-- 加载中 -->
          <view v-if="userData.loading && userData.records.length === 0" class="loading-container">
            <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
            <text class="loading-text">加载中...</text>
          </view>
          
          <!-- 没有数据 -->
          <view v-else-if="userData.records.length === 0" class="empty-container">
            <tn-icon name="info" color="#999999" size="60rpx"></tn-icon>
            <text class="empty-text">您还没有关注用户~</text>
          </view>
          
          <!-- 用户列表 -->
          <view v-else class="user-list">
            <view 
              v-for="item in userData.records" 
              :key="item.id"
              class="user-item"
              @tap="goToUserDetail(item.id)"
            >
              <image class="user-avatar" :src="item.userAvatar" mode="aspectFill"></image>
              <view class="user-info">
                <view class="user-name">{{ item.userName }}</view>
                <view class="user-role">{{ getRoleName(item.roleId) }}</view>
              </view>
              <view class="user-action">
                <TnButton 
                  bg-color="#5677fc" 
                  width="150rpx" 
                  height="60rpx" 
                  radius="30rpx"
                  font-size="24rpx"
                >
                  <view class="tn-flex tn-flex-center-center">
                    <TnIcon name="star-fill" color="#fff" size="28rpx"></TnIcon>
                    <text class="tn-white_text tn-ml-xs">已关注</text>
                  </view>
                </TnButton>
              </view>
            </view>
            
            <!-- 加载更多按钮 -->
            <view class="load-more-container">
              <tn-button 
                v-if="userData.hasMore" 
                @tap="loadMore" 
                :loading="userData.loading"
                bg-color="#5677fc"
                width="40%"
                height="70rpx"
                font-size="26rpx"
                padding="0"
                radius="35rpx"
                :shadow="true"
              >
                <view class="btn-content tn-flex tn-flex-row-center">
                  <tn-icon v-if="!userData.loading" name="more-circle" color="#fff" size="30rpx"></tn-icon>
                  <text class="tn-ml-xs tn-white_text">{{ !userData.loading ? '加载更多' : '加载中...' }}</text>
                </view>
              </tn-button>
              <view v-else-if="!userData.hasMore && userData.records.length > 0" class="no-more-data tn-flex tn-flex-center-center">
                <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
                <text class="tn-gray_text tn-ml-xs">没有更多了</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<style scoped lang="scss">
.my-like-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  display: flex;
  flex-direction: column;
}

.tab-container {
  background-color: #ffffff;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.content-scroll {
  flex: 1;
  height: calc(100vh - 220rpx);
}

.content-container {
  flex: 1;
  position: relative;
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

.circle-list, .user-list {
  padding-bottom: 20rpx;
}

.circle-item {
  display: flex;
  padding: 30rpx;
  margin: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .circle-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    margin-right: 20rpx;
  }
  
  .circle-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    
    .circle-name {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
    }
    
    .circle-content {
      font-size: 26rpx;
      color: #666666;
      margin: 10rpx 0;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    
    .circle-stats {
      display: flex;
      
      .stats-text {
        font-size: 24rpx;
        color: #999999;
        margin-right: 20rpx;
      }
    }
  }
  
  .circle-action {
    display: flex;
    align-items: center;
  }
}

.user-item {
  display: flex;
  padding: 30rpx;
  margin: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .user-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50rpx;
    margin-right: 20rpx;
  }
  
  .user-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    
    .user-name {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 10rpx;
    }
    
    .user-role {
      font-size: 26rpx;
      color: #999999;
    }
  }
  
  .user-action {
    display: flex;
    align-items: center;
  }
}

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

<script>
// 辅助函数
function formatDate(dateArray) {
  if (!dateArray || !Array.isArray(dateArray)) return ''
  const [year, month, day] = dateArray
  return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`
}

function getRoleName(roleId) {
  const roleMap = {
    1: '管理员',
    2: '教师',
    3: '学生',
    4: '游客'
  }
  return roleMap[roleId] || '游客'
}
</script>