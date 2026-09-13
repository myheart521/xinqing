<script setup>
import {ref, reactive, onMounted} from 'vue'
import {getHistoryRecent} from '@/service/api/historyTestController'
import {getDefaultTest} from "@/service/api/modulesController";
import {useUserStore} from '@/stores/user'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnLoading from '@tuniao/tnui-vue3-uniapp/components/loading/src/loading.vue'
import TnEmpty from '@tuniao/tnui-vue3-uniapp/components/empty/src/empty.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'

import TnTimeLine from 'tnuiv3p-tn-time-line/time-line.vue'
import TnTimeLineItem from 'tnuiv3p-tn-time-line/time-line-item.vue'
import TnTimeLineData from 'tnuiv3p-tn-time-line/time-line-data.vue'
import Navbar from "@/components/navbar.vue";

const userStore = useUserStore()
const recordList = ref([])
const loading = ref(false)
const loadingStatus = ref(0) // 0加载前，1加载中，2没有更多了
const noMoreData = ref(false) // 是否没有更多数据
const refreshRef = ref(null)
const enableScroll = ref(true)

// 测试列表相关数据
const testList = ref([])
const loadingTestList = ref(false)
const testListSwiper = ref(null)
const currentTestIndex = ref(0)

// 分页参数
const pageParams = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 获取测试记录
const getTestRecords = async (isRefresh = false) => {
  if (loadingStatus.value === 1) {
    console.log('正在加载中，请稍后再试')
    return
  }
  
  loadingStatus.value = 1
  
  try {
    console.log('获取测试记录，页码:', pageParams.pageNum)
    const res = await getHistoryRecent({
      pageDTO: {
        pageNum: pageParams.pageNum,
        pageSize: pageParams.pageSize
      }
    })

    if (res && res.code === 1) {
      // 处理日期格式
      const processedData = (res.data || []).map(item => {
        return {
          ...item,
          formattedTime: formatDate(item.createTime)
        }
      })

      // 按日期分组
      const groupedRecords = groupRecordsByDate(processedData)

      if (isRefresh) {
        recordList.value = groupedRecords
      } else {
        recordList.value = [...recordList.value, ...groupedRecords]
      }
      
      // 设置分页信息
      pageParams.total = processedData.length
      
      // 判断是否还有更多数据
      if (!processedData.length) {
        console.log('没有更多数据了，数据长度: 0')
        noMoreData.value = true
      } else {
        console.log('加载到数据，页码加1，数据长度:', processedData.length)
        noMoreData.value = false
        pageParams.pageNum++
      }
    } else {
      uni.showToast({
        title: res?.msg || '获取测试记录失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取测试记录失败:', error)
    uni.showToast({
      title: '获取测试记录失败',
      icon: 'none'
    })
  } finally {
    loadingStatus.value = 0
    loading.value = false
  }
}

// 获取测试模块列表
const getTestList = async () => {
  if (loadingTestList.value) return
  
  loadingTestList.value = true
  
  try {
    const res = await getDefaultTest({
      pageDTO: {
        current: 1,
        size: 6 // 只获取几个热门测试
      }
    })
    
    if (res && res.code === 1 && res.data) {
      const records = Array.isArray(res.data) ? res.data : (res.data.records || [])
      testList.value = records
    } else {
      // 如果获取失败，使用模拟数据
      useTestListDemoData()
    }
  } catch (error) {
    console.error('获取测试列表失败:', error)
    // 使用模拟数据
    useTestListDemoData()
  } finally {
    loadingTestList.value = false
  }
}

// 使用测试列表模拟数据
const useTestListDemoData = () => {
  testList.value = [
    {
      id: 1,
      title: "性格测试",
      introductions: "国际标准个人mbti性格测试",
      src: "https://assets.example.invalid/placeholder.png"
    },
    {
      id: 2,
      title: "抑郁症筛查测试",
      introductions: "评估您是否有抑郁症倾向，及早发现心理问题",
      src: "https://assets.example.invalid/placeholder.png"
    },
    {
      id: 3,
      title: "焦虑自评量表",
      introductions: "测量您的焦虑水平，了解自己的心理状态",
      src: "https://assets.example.invalid/placeholder.png"
    },
  ]
}

// 日期格式化
const formatDate = (dateArr) => {
  if (!dateArr || !Array.isArray(dateArr) || dateArr.length < 3) {
    return ''
  }

  const [year, month, day] = dateArr
  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
}

// 按日期分组记录
const groupRecordsByDate = (records) => {
  const grouped = {}

  records.forEach(item => {
    // 提取年月作为分组键
    const dateObj = new Date(item.formattedTime)
    const yearMonth = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}`

    if (!grouped[yearMonth]) {
      grouped[yearMonth] = {
        month: yearMonth,
        icon: 'star',
        data: []
      }
    }

    grouped[yearMonth].data.push({
      date: new Date(item.formattedTime).getDate(),
      moduleName: item.title || '未知测试',
      content: item.description || '暂无描述',
      image: item.src || '',
      id: item.id,
      moduleId: item.moduleId || 0
    })
  })

  // 转换为数组并按日期降序排序
  return Object.values(grouped).sort((a, b) => {
    return new Date(b.month) - new Date(a.month)
  })
}

// 重置分页加载状态
const resetLoadMore = () => {
  pageParams.pageNum = 1
  loadingStatus.value = 0
  noMoreData.value = false
}

// 查看测试详情
const viewTestDetail = (id) => {
  if (!id) return

  // 加载状态
  uni.showLoading({
    title: '加载中...',
    mask: true
  })
  
  try {
    // 带上historyId跳转到测试结果页面
    uni.navigateTo({
      url: `/function_pages/views/test-result?historyId=${id}`,
      success: () => {
        uni.hideLoading()
      },
      fail: (err) => {
        console.error('跳转失败:', err)
        uni.hideLoading()
        uni.showToast({
          title: '跳转失败',
          icon: 'none'
        })
      }
    })
  } catch (error) {
    console.error('跳转错误:', error)
    uni.hideLoading()
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    })
  }
}

// 跳转到测试题页面
const goToTest = (id) => {
  if (!id) return
  
  console.log("跳转到测试题页面，id:", id)
  uni.navigateTo({
    url: `/function_pages/views/test-question?id=${id}`
  })
}

// 重新测试
const retakeTest = () => {
  uni.navigateTo({
    url: '/function_pages/views/test-list'
  })
}

// 下拉刷新处理
const onRefresh = async () => {
  try {
    console.log('开始刷新数据')
    resetLoadMore()
    await getTestRecords(true)
    await getTestList()
    
    uni.showToast({
      title: '刷新成功',
      icon: 'success'
    })
  } catch (error) {
    console.error('刷新失败:', error)
    uni.showToast({
      title: '刷新失败',
      icon: 'none'
    })
  } finally {
    refreshRef.value?.endPulldownRefresh()
  }
}

// 加载更多
const loadMore = async () => {
  console.log('执行加载更多方法')
  
  if (loadingStatus.value === 1) {
    console.log('正在加载中，请稍后再试')
    return
  }
  
  if (noMoreData.value) {
    uni.showToast({
      title: '没有更多数据了',
      icon: 'none'
    })
    return
  }
  
  await getTestRecords()
}

// 设置是否可以滚动
const setEnableScroll = (value) => {
  enableScroll.value = value
}

// 切换测试项
const onSwiperChange = (e) => {
  currentTestIndex.value = e.detail.current
}

onMounted(() => {
  // 检查登录状态
  if (!userStore.checkLogin()) {
    return
  }

  // 获取测试列表
  getTestList()
  
  // 获取测试记录
  resetLoadMore()
  loading.value = true
  getTestRecords(true)
})
</script>

<template>
  <view class="test-record-page">
    <!-- 顶部自定义导航 -->
    <navbar title="测试历史记录"></navbar>
    
    <!-- 测试模块展示区域 -->
    <view class="test-modules-container" v-if="testList.length > 0">
      <view class="section-title">
        <text class="title-text">推荐测试</text>
        <view class="title-more" @tap="retakeTest">
          <text class="tn-blue_text">查看更多</text>
          <TnIcon name="right" size="26rpx" color="#5677fc"></TnIcon>
        </view>
      </view>
      
      <!-- 滑动区域 -->
      <swiper
        class="test-swiper"
        :indicator-dots="true"
        :autoplay="true"
        :interval="4000"
        :duration="500"
        :circular="true"
        indicator-color="rgba(255, 255, 255, 0.6)"
        indicator-active-color="#5677fc"
        @change="onSwiperChange"
        ref="testListSwiper"
      >
        <swiper-item v-for="(item, index) in testList" :key="item.id" class="swiper-item">
          <view class="test-item-card" @tap="goToTest(item.id)">
            <image class="test-item-image" :src="item.src" mode="aspectFill" />
            <view class="test-item-info">
              <view class="test-item-title">{{ item.title }}</view>
              <view class="test-item-desc">{{ item.introductions }}</view>
              <view class="test-item-action">
                <TnButton 
                  bg-color="#5677fc" 
                  height="64rpx"
                  width="160rpx"
                  font-size="26rpx"
                  margin="16rpx 0 0 0"
                  radius="32rpx"
                  :shadow="true"
                >
                  立即测试
                </TnButton>
              </view>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 测试记录标题 -->
    <view class="section-title records-title">
      <text class="title-text">历史记录</text>
    </view>

    <!-- 下拉刷新包裹器 -->
    <pulldownRefresh
      ref="refreshRef"
      :top="testList.length > 0 ? '470rpx' : '120rpx'"
      :threshold="80"
      @refresh="onRefresh"
      @setEnableScroll="setEnableScroll"
    >
      <scroll-view
        class="scroll-container"
        scroll-y
        :enable-flex="true"
      >
        <!-- 加载中 -->
<!--        <view class="loading-container" v-if="loading">-->
<!--          <TnLoading type="circle" size="60"></TnLoading>-->
<!--          <text class="loading-text">加载中...</text>-->
<!--        </view>-->

        <!-- 无数据 -->
        <view v-if="recordList.length === 0" class="empty-container">
          <TnEmpty
            icon="empty-history"
            text="暂无测评记录"
            :tip-text="true"
          >
            <template #bottom>
              <TnButton
                @tap="retakeTest"
                bg-color="#5677fc"
                width="40%"
                height="80rpx"
                margin="40rpx 0 0 0"
                shape="round"
              >
                <view class="tn-flex tn-flex-center-center">
                  <text class="tn-white_text">去测评</text>
                </view>
              </TnButton>
            </template>
          </TnEmpty>
        </view>

        <!-- 测评记录列表 -->
        <view v-else class="record-list-container">
          <tn-time-line>
            <tn-time-line-item
              v-for="(item, index) in recordList"
              :key="index"
              :title="item.month"
              :title-icon="item.icon"
              :custom-style="{ marginBottom: '30rpx' }"
            >
              <tn-time-line-data
                v-for="(dataItem, dataIndex) in item.data"
                :key="dataIndex"
                @click="viewTestDetail(dataItem.id)"
              >
                <view class="time-line__title tn-flex tn-flex-row-between tn-flex-col-center">
                  <view class="date tn-gray_text">{{ dataItem.date }}日</view>
                </view>
                <view class="time-line__data tn-flex tn-flex-direction-column">
                  <view class="test-item-content tn-flex">
                    <!-- 测试图片 -->
                    <image 
                      v-if="dataItem.image" 
                      :src="dataItem.image" 
                      mode="aspectFill" 
                      class="test-image"
                    ></image>
                    
                    <view class="test-info">
                      <view class="test-name tn-text-bold">{{ dataItem.moduleName }}</view>
                      <view class="test-desc tn-gray_text" v-if="dataItem.content">{{ dataItem.content }}</view>
                      <view class="view-detail tn-flex tn-flex-row-end tn-mt-xs">
                        <view class="detail-btn tn-blue_text">
                          <TnIcon name="right" size="24rpx"></TnIcon>
                          <text>查看详情</text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
              </tn-time-line-data>
            </tn-time-line-item>
          </tn-time-line>

          <!-- 加载更多 -->
          <view class="load-more-container">
            <TnButton 
              v-if="!noMoreData && recordList.length > 0" 
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
                <TnIcon v-if="loadingStatus !== 1" name="more-circle" color="#fff" size="30rpx"></TnIcon>
                <text class="tn-ml-xs tn-white_text">{{ loadingStatus !== 1 ? '加载更多' : '加载中...' }}</text>
              </view>
            </TnButton>
            <view v-else-if="noMoreData && recordList.length > 0" class="no-more-data tn-flex tn-flex-center-center">
              <TnIcon name="info-circle" color="#aaa" size="28rpx"></TnIcon>
              <text class="tn-gray_text tn-ml-xs">没有更多了</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<style lang="scss" scoped>
.test-record-page {
  min-height: 100vh;
  background-color: #f8f9fc;
  padding-bottom: 100rpx;
  display: flex;
  flex-direction: column;
}

/* 测试模块容器 */
.test-modules-container {
  padding: 20rpx 30rpx;
  background-color: #fff;
  margin-bottom: 10rpx;
  
  .test-swiper {
    height: 320rpx;
    width: 100%;
    margin-top: 20rpx;
    
    .swiper-item {
      display: flex;
      justify-content: center;
      align-items: center;
      
      .test-item-card {
        width: 92%;
        height: 280rpx;
        border-radius: 16rpx;
        overflow: hidden;
        box-shadow: 0 2rpx 20rpx rgba(0, 0, 0, 0.1);
        position: relative;
        
        .test-item-image {
          width: 100%;
          height: 100%;
          position: absolute;
          top: 0;
          left: 0;
        }
        
        .test-item-info {
          position: absolute;
          bottom: 0;
          left: 0;
          width: 100%;
          padding: 20rpx;
          background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
          color: #fff;
          
          .test-item-title {
            font-size: 32rpx;
            font-weight: bold;
            margin-bottom: 8rpx;
          }
          
          .test-item-desc {
            font-size: 24rpx;
            opacity: 0.9;
            margin-bottom: 10rpx;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
        }
      }
    }
  }
}

/* 标题样式 */
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10rpx;
  
  .title-text {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    position: relative;
    padding-left: 20rpx;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 8rpx;
      height: 30rpx;
      background-color: #5677fc;
      border-radius: 4rpx;
    }
  }
  
  .title-more {
    display: flex;
    align-items: center;
    font-size: 26rpx;
  }
}

.records-title {
  padding: 20rpx 30rpx;
  background-color: #fff;
  margin-bottom: 10rpx;
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
  height: 60vh;

  .loading-text {
    margin-top: 20rpx;
    font-size: 28rpx;
    color: #666;
  }
}

.record-list-container {
  padding: 30rpx;

  .time-line__title {
    margin-bottom: 16rpx;

    .date {
      font-size: 28rpx;
    }
  }

  .time-line__data {
    background-color: #ffffff;
    padding: 20rpx;
    border-radius: 12rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    margin-bottom: 10rpx;
    
    .test-item-content {
      display: flex;
      
      .test-image {
        width: 120rpx;
        height: 120rpx;
        border-radius: 10rpx;
        margin-right: 20rpx;
        flex-shrink: 0;
      }
      
      .test-info {
        flex: 1;
        display: flex;
        flex-direction: column;
      }
    }

    .test-name {
      font-size: 30rpx;
      color: #333;
      margin-bottom: 8rpx;
    }

    .test-desc {
      font-size: 26rpx;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }

    .view-detail {
      margin-top: 10rpx;

      .detail-btn {
        display: flex;
        align-items: center;
        font-size: 24rpx;
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