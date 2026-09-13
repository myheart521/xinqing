<script setup>
import { ref, reactive, onMounted } from 'vue'
import navbar from "@/components/navbar.vue"
import TnIcon from '@/uni_modules/tuniaoui-vue3/components/icon/src/icon.vue'
import TnButton from '@/uni_modules/tuniaoui-vue3/components/button/src/button.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'
import { getStudentAppointments } from '@/service/api/appointmentController'

// 预约数据
const appointmentData = reactive({
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

// 获取预约列表
const getAppointmentList = async (isRefresh = false) => {
  if (isRefresh) {
    appointmentData.refreshing = true
    appointmentData.current = 1
  } else {
    if (appointmentData.loading) return
    appointmentData.loading = true
    
    if (!appointmentData.hasMore) {
      appointmentData.loading = false
      uni.showToast({
        icon: 'none',
        title: '没有更多数据了'
      })
      return
    }
  }
  
  try {
    const res = await getStudentAppointments({ 
      current: appointmentData.current.toString(),
      size: '10'
    })
    
    if (res.code === 1 && res.data) {
      if (isRefresh || appointmentData.current === 1) {
        appointmentData.records = res.data.records || []
      } else {
        appointmentData.records = [...appointmentData.records, ...(res.data.records || [])]
      }
      
      appointmentData.total = res.data.total || 0
      appointmentData.hasMore = appointmentData.records.length < appointmentData.total
      
      if (!isRefresh) {
        appointmentData.current++
      }
    } else {
      uni.showToast({
        icon: 'none',
        title: res.msg || '获取预约列表失败'
      })
    }
  } catch (error) {
    console.error('获取预约列表失败:', error)
    uni.showToast({
      icon: 'none',
      title: '获取预约列表失败'
    })
  } finally {
    appointmentData.loading = false
    appointmentData.refreshing = false
  }
}

// 加载更多
const loadMore = () => {
  getAppointmentList()
}

// 处理下拉刷新
const onRefresh = async () => {
  try {
    await getAppointmentList(true)
    
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

// 获取状态显示信息
const getStatusInfo = (status) => {
  const statusMap = {
    '0': { text: '待处理', color: '#ff7900', bgColor: '#fff4e6', icon: 'time' },
    '1': { text: '已通过', color: '#19be6b', bgColor: '#f0f9ff', icon: 'check-circle' },
    '2': { text: '已驳回', color: '#ed4014', bgColor: '#fff2f0', icon: 'close-circle' },
    '3': { text: '已取消', color: '#999999', bgColor: '#f5f5f5', icon: 'minus-circle' }
  }
  return statusMap[status] || statusMap['0']
}

// 取消预约
const cancelAppointment = (item) => {
  if (item.status !== '0') {
    uni.showToast({
      title: '该预约无法取消',
      icon: 'none'
    })
    return
  }
  
  uni.showModal({
    title: '确认取消',
    content: '确定要取消这个预约吗？',
    success: (res) => {
      if (res.confirm) {
        // TODO: 调用取消预约接口
        uni.showToast({
          title: '预约已取消',
          icon: 'success'
        })
        // 刷新列表
        getAppointmentList(true)
      }
    }
  })
}

// 联系老师
const contactTeacher = (item) => {
  uni.showModal({
    title: '联系方式',
    content: `请通过系统内消息联系 ${item.teacherName} 老师`,
    showCancel: false
  })
}

// 格式化日期显示
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr
}

// 格式化时间显示
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.substring(0, 5) // 只显示 HH:mm
}

// 页面加载时获取数据
onMounted(() => {
  getAppointmentList()
})
</script>

<template>
  <view class="appointment-container">
    <!-- 导航栏 -->
    <navbar title="我的预约"></navbar>
    
    <!-- 内容区域 -->
    <pulldownRefresh
      ref="refreshRef"
      :top="0"
      :threshold="80"
      @refresh="onRefresh"
      @setEnableScroll="setEnableScroll"
    >
      <scroll-view class="content-scroll" scroll-y>
        <view class="content-container">
          <!-- 加载中 -->
          <view v-if="appointmentData.loading && appointmentData.records.length === 0" class="loading-container">
            <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
            <text class="loading-text">加载中...</text>
          </view>
          
          <!-- 没有数据 -->
          <view v-else-if="appointmentData.records.length === 0" class="empty-container">
            <tn-icon name="calendar" color="#999999" size="60rpx"></tn-icon>
            <text class="empty-text">您还没有预约记录~</text>
            <text class="empty-hint">去找个心理咨询师聊聊吧</text>
          </view>
          
          <!-- 预约列表 -->
          <view v-else class="appointment-list">
            <view 
              v-for="item in appointmentData.records" 
              :key="item.id || `${item.date}-${item.startTime}`"
              class="appointment-item"
            >
              <!-- 预约状态标签 -->
              <view 
                class="status-tag"
                :style="{
                  color: getStatusInfo(item.status).color,
                  backgroundColor: getStatusInfo(item.status).bgColor
                }"
              >
                <tn-icon 
                  :name="getStatusInfo(item.status).icon" 
                  :color="getStatusInfo(item.status).color" 
                  size="24rpx"
                ></tn-icon>
                <text class="status-text">{{ getStatusInfo(item.status).text }}</text>
              </view>
              
              <!-- 预约信息 -->
              <view class="appointment-info">
                <!-- 老师信息 -->
                <view class="teacher-info">
                  <view class="teacher-name">
                    <tn-icon name="user" color="#5677fc" size="32rpx"></tn-icon>
                    <text class="name-text">{{ item.teacherName }}</text>
                  </view>
                </view>
                
                <!-- 时间信息 -->
                <view class="time-info">
                  <view class="date-item">
                    <tn-icon name="calendar" color="#ff7900" size="28rpx"></tn-icon>
                    <text class="time-text">{{ formatDate(item.date) }}</text>
                  </view>
                  <view class="time-item">
                    <tn-icon name="time" color="#19be6b" size="28rpx"></tn-icon>
                    <text class="time-text">{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</text>
                  </view>
                </view>
                
                <!-- 预约理由 -->
                <view v-if="item.excuse" class="excuse-info">
                  <view class="excuse-label">
                    <tn-icon name="message" color="#666" size="24rpx"></tn-icon>
                    <text class="excuse-text">预约理由：{{ item.excuse }}</text>
                  </view>
                </view>
              </view>
              
              <!-- 操作按钮 -->
              <view class="action-buttons">
                <TnButton 
                  v-if="item.status === '0'"
                  bg-color="#ed4014" 
                  width="140rpx" 
                  height="60rpx" 
                  radius="30rpx"
                  font-size="24rpx"
                  @tap="cancelAppointment(item)"
                >
                  <view class="tn-flex tn-flex-center-center">
                    <tn-icon name="close" color="#fff" size="24rpx"></tn-icon>
                    <text class="tn-white_text tn-ml-xs">取消</text>
                  </view>
                </TnButton>
                
                <TnButton 
                  v-if="item.status === '1'"
                  bg-color="#5677fc" 
                  width="140rpx" 
                  height="60rpx" 
                  radius="30rpx"
                  font-size="24rpx"
                  @tap="contactTeacher(item)"
                >
                  <view class="tn-flex tn-flex-center-center">
                    <tn-icon name="message" color="#fff" size="24rpx"></tn-icon>
                    <text class="tn-white_text tn-ml-xs">联系</text>
                  </view>
                </TnButton>
                
                <view v-if="item.status === '2'" class="reject-reason">
                  <text class="reason-text">驳回原因：系统繁忙</text>
                </view>
                
                <view v-if="item.status === '3'" class="cancel-info">
                  <text class="cancel-text">预约已取消</text>
                </view>
              </view>
            </view>
            
            <!-- 加载更多按钮 -->
            <view class="load-more-container">
              <tn-button 
                v-if="appointmentData.hasMore" 
                @tap="loadMore" 
                :loading="appointmentData.loading"
                bg-color="#5677fc"
                width="40%"
                height="70rpx"
                font-size="26rpx"
                padding="0"
                radius="35rpx"
                :shadow="true"
              >
                <view class="btn-content tn-flex tn-flex-row-center">
                  <tn-icon v-if="!appointmentData.loading" name="more-circle" color="#fff" size="30rpx"></tn-icon>
                  <text class="tn-ml-xs tn-white_text">{{ !appointmentData.loading ? '加载更多' : '加载中...' }}</text>
                </view>
              </tn-button>
              <view v-else-if="!appointmentData.hasMore && appointmentData.records.length > 0" class="no-more-data tn-flex tn-flex-center-center">
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
.appointment-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  display: flex;
  flex-direction: column;
}

.content-scroll {
  flex: 1;
  height: calc(100vh - 120rpx);
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
  
  .empty-hint {
    font-size: 24rpx;
    color: #ccc;
    margin-top: 10rpx;
  }
}

.appointment-list {
  padding-bottom: 20rpx;
}

.appointment-item {
  position: relative;
  margin: 20rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
  overflow: hidden;
  
  .status-tag {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    display: flex;
    align-items: center;
    padding: 8rpx 16rpx;
    border-radius: 20rpx;
    
    .status-text {
      font-size: 22rpx;
      margin-left: 6rpx;
      font-weight: bold;
    }
  }
  
  .appointment-info {
    padding: 30rpx;
    padding-right: 160rpx; // 为状态标签留空间
    
    .teacher-info {
      margin-bottom: 20rpx;
      
      .teacher-name {
        display: flex;
        align-items: center;
        
        .name-text {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-left: 10rpx;
        }
      }
    }
    
    .time-info {
      margin-bottom: 20rpx;
      
      .date-item, .time-item {
        display: flex;
        align-items: center;
        margin-bottom: 10rpx;
        
        .time-text {
          font-size: 26rpx;
          color: #666;
          margin-left: 8rpx;
        }
      }
    }
    
    .excuse-info {
      .excuse-label {
        display: flex;
        align-items: flex-start;
        
        .excuse-text {
          font-size: 24rpx;
          color: #666;
          margin-left: 8rpx;
          line-height: 1.5;
          word-break: break-all;
        }
      }
    }
  }
  
  .action-buttons {
    padding: 0 30rpx 30rpx;
    display: flex;
    justify-content: flex-end;
    
    .reject-reason, .cancel-info {
      display: flex;
      align-items: center;
      
      .reason-text, .cancel-text {
        font-size: 24rpx;
        color: #999;
      }
    }
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
