<script setup>
import { ref, reactive, onMounted } from 'vue'
import navbar from "@/components/navbar.vue"
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'
import { selectBlogByUserId } from '@/service/api/commentsController'
import { formatTime, formatDateByYear, formatRelativeTime } from '@/utils/formate'

// 评论消息数据
const commentData = reactive({
  records: [],
  total: 0,
  current: 1,
  hasMore: true
})

// 加载状态：0-加载前，1-加载中，2-没有更多了
const loadingStatus = ref(0)
const noMoreData = ref(false) // 是否没有更多数据
const refreshRef = ref(null)
const enableScroll = ref(true)

// 获取评论消息列表
const getCommentList = async (isRefresh = false) => {
  if (loadingStatus.value === 1) {
    console.log('正在加载中，请稍后再试')
    return
  }
  
  loadingStatus.value = 1
  
  // 如果是刷新，重置页码
  if (isRefresh) {
    commentData.current = 1
    noMoreData.value = false
  }
  
  try {
    const res = await selectBlogByUserId({ 
      current: commentData.current.toString() 
    })
    
    if (res.code === 1) {
      const data = res.data || { records: [], total: 0 }
      
      if (isRefresh) {
        commentData.records = data.records || []
      } else {
        commentData.records = [...commentData.records, ...(data.records || [])]
      }
      
      commentData.total = data.total || 0
      commentData.hasMore = commentData.records.length < commentData.total
      
      // 判断是否还有更多数据
      if (!data.records || data.records.length === 0) {
        noMoreData.value = true
      } else {
        commentData.current++
      }
    } else {
      uni.showToast({
        title: res.msg || '获取数据失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取评论消息失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
  } finally {
    loadingStatus.value = 0
  }
}

// 下拉刷新
const onRefresh = async () => {
  try {
    await getCommentList(true)
    
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
  getCommentList()
}

// 设置是否可以滚动
const setEnableScroll = (value) => {
  enableScroll.value = value
}

// 前往动态详情页
const goToBlogDetail = (blogId) => {
  if (blogId) {
    uni.navigateTo({
      url: `/group_pages/views/group-detail?id=${blogId}`
    })
  }
}

onMounted(() => {
  getCommentList(true)
})
</script>

<template>
  <view class="messages_container">
    <!-- 导航栏 -->
    <navbar title="评论消息"></navbar>
    
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
        <view v-if="loadingStatus === 1 && commentData.records.length === 0" class="loading-container">
          <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
          <text class="loading-text">加载中...</text>
        </view>
        
        <!-- 没有数据 -->
        <view v-else-if="commentData.records.length === 0" class="empty-container">
          <tn-icon name="info" color="#999999" size="60rpx"></tn-icon>
          <text class="empty-text">暂无评论消息~</text>
        </view>
        
        <!-- 评论列表 -->
        <view v-else class="comment-list">
          <view 
            v-for="item in commentData.records" 
            :key="item.id"
            class="comment-item"
            @tap="goToBlogDetail(item.blogId)"
          >
            <view class="user-info">
              <image :src="item.userAvatar" class="user-avatar" mode="aspectFill"></image>
              <view class="user-details">
                <text class="user-name">{{ item.userName }}</text>
                <text class="comment-action">回复了你的评论</text>
              </view>
              <view class="comment-time">
                <text class="time-text">{{ formatRelativeTime(item.createTime) }}</text>
              </view>
            </view>
            
            <view class="comment-info">
              <view class="original-comment" v-if="item.answerText">
                <text class="original-text">你的评论: {{ item.answerText }}</text>
              </view>
              <view class="comment-content">{{ item.content }}</view>
              <view class="comment-details">
                <view class="blog-info">
                  <tn-icon name="link" color="#999" size="28rpx"></tn-icon>
                  <text class="blog-text">查看详情</text>
                </view>
                <view class="comment-stats">
                  <view class="stats-item">
                    <tn-icon name="like" color="#999" size="28rpx"></tn-icon>
                    <text class="stats-text">{{ item.liked || 0 }}</text>
                  </view>
                  <view class="stats-item">
                    <tn-icon name="comment" color="#999" size="28rpx"></tn-icon>
                    <text class="stats-text">{{ item.commentCount || 0 }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
          
          <!-- 加载更多按钮 -->
          <view class="load-more-container">
            <tn-button 
              v-if="!noMoreData && commentData.records.length > 0" 
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
            <view v-else-if="noMoreData && commentData.records.length > 0" class="no-more-data tn-flex tn-flex-center-center">
              <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
              <text class="tn-gray_text tn-ml-xs">没有更多了</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<style lang="scss">
.messages_container {
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

.comment-list {
  padding: 20rpx;
}

.comment-item {
  background-color: #ffffff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .user-info {
    display: flex;
    align-items: center;
    margin-bottom: 15rpx;
    
    .user-avatar {
      width: 70rpx;
      height: 70rpx;
      border-radius: 50%;
    }
    
    .user-details {
      flex: 1;
      margin-left: 15rpx;
      
      .user-name {
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        display: block;
      }
      
      .comment-action {
        font-size: 24rpx;
        color: #999;
      }
    }
    
    .comment-time {
      .time-text {
        font-size: 24rpx;
        color: #999999;
      }
    }
  }
  
  .comment-info {
    margin-left: 85rpx;
    
    .original-comment {
      background-color: #f8f8f8;
      padding: 12rpx 15rpx;
      border-radius: 8rpx;
      margin-bottom: 10rpx;
      
      .original-text {
        font-size: 26rpx;
        color: #666;
      }
    }
    
    .comment-content {
      font-size: 30rpx;
      color: #333333;
      margin-bottom: 15rpx;
      line-height: 1.5;
    }
    
    .comment-details {
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-top: 1px solid #f0f0f0;
      padding-top: 15rpx;
      
      .blog-info {
        display: flex;
        align-items: center;
        
        .blog-text {
          margin-left: 6rpx;
          font-size: 24rpx;
          color: #5677fc;
        }
      }
      
      .comment-stats {
        display: flex;
        align-items: center;
        
        .stats-item {
          display: flex;
          align-items: center;
          margin-left: 20rpx;
          
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