<script setup>
import {ref, onMounted, reactive, watch} from 'vue'
import navbar from "@/components/navbar.vue"
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnTabs from '@tuniao/tnui-vue3-uniapp/components/tabs/src/tabs.vue'
import TnTabsItem from '@tuniao/tnui-vue3-uniapp/components/tabs/src/tabs-item.vue'
import TnTimeLine from 'tnuiv3p-tn-time-line/time-line.vue'
import TnTimeLineItem from 'tnuiv3p-tn-time-line/time-line-item.vue'
import TnTimeLineData from 'tnuiv3p-tn-time-line/time-line-data.vue'
import Navbar from "@/components/navbar.vue";
import {watchHistory, watchGraph} from '@/service/api/sportController'

// 当前选中的模块（历史记录/运动报表）
const currentTab = ref(0)

// 状态管理
const loading = ref(false)
const noData = ref(false)
const chartLoading = ref(false)
const chartNoData = ref(false)

// 运动记录
const motionRecords = ref([])
const timeLineData = ref([])

// 图表相关
const currentDays = ref(7)

const currentTabDays = ref(0)
const daysOptions = [
  {value: 7, label: '近7天'},
  {value: 30, label: '近30天'},
  {value: 90, label: '近90天'}
]

// 柱状图数据和配置
const chartData = ref({
  categories: [],
  series: []
})

const opts = ref({
  color: ["#1890FF", "#91CB74", "#FAC858", "#EE6666", "#73C0DE", "#3CA272", "#FC8452", "#9A60B4", "#ea7ccc"],
  padding: [15, 15, 0, 15],
  enableScroll: false,
  legend: {
    show: true,
    position: 'bottom',
    float: 'center',
    padding: 10,
    margin: 5
  },
  xAxis: {
    disableGrid: true,
    itemCount: 5,
    boundaryGap: true,
    axisLine: true,
    axisLineColor: '#CCCCCC'
  },
  yAxis: {
    data: [
      {
        min: 0
      }
    ],
    gridType: 'dash',
    dashLength: 4
  },
  extra: {
    column: {
      type: "group",
      width: 30,
      activeBgColor: "#000000",
      activeBgOpacity: 0.08,
      barBorderRadius: [5, 5, 0, 0]
    }
  }
})

// 处理日期转换为可读格式
const formatDate = (dateArray) => {
  if (!dateArray || !Array.isArray(dateArray)) return ''
  const [year, month, day, hour, minute] = dateArray
  return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`
}

// 处理日期获取月份和日期
const getMonthDay = (dateString) => {
  if (!dateString) return {month: '', day: ''}
  const date = new Date(dateString)
  return {
    month: `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`,
    day: date.getDate().toString()
  }
}

// 缩短标题
const shortenTitle = (title, length = 10) => {
  if (!title) return ''
  if (title.length <= length) return title
  return title.substring(0, length) + '...'
}

// 获取运动记录历史
const getMotionHistory = async () => {
  loading.value = true
  noData.value = false
  try {
    const res = await watchHistory()
    if (res.code === 1 && res.data && Array.isArray(res.data)) {
      motionRecords.value = res.data

      // 处理时间轴数据格式
      const groupedData = {}

      motionRecords.value.forEach(record => {
        const dateObj = getMonthDay(formatDate(record.createTime))
        const monthKey = dateObj.month

        if (!groupedData[monthKey]) {
          groupedData[monthKey] = {
            month: monthKey,
            icon: 'fitness',
            data: []
          }
        }

        groupedData[monthKey].data.push({
          date: dateObj.day,
          content: record.title,
          record: record
        })
      })

      timeLineData.value = Object.values(groupedData)
      noData.value = timeLineData.value.length === 0
    } else {
      noData.value = true
    }
  } catch (error) {
    console.error('获取运动记录失败:', error)
    noData.value = true
  } finally {
    loading.value = false
  }
}

// 查看运动详情
const viewExerciseDetail = (record) => {
  if (!record || !record.id) return
  uni.navigateTo({
    url: `/function_pages/views/motion-exercise-video?id=${record.id}`
  })
}

// 获取运动图表数据
const getMotionChartData = async () => {
  chartLoading.value = true
  chartNoData.value = false
  try {
    const res = await watchGraph({scale: daysOptions[currentTabDays.value].value})
    if (res.code === 1 && res.data) {
      // 处理图表数据
      const categories = []
      const seriesMap = {}

      // 获取所有的运动类型
      const motionTypes = new Set()
      Object.keys(res.data).forEach(date => {
        Object.keys(res.data[date]).forEach(motionName => {
          motionTypes.add(shortenTitle(motionName, 8))
        })
      })

      // 初始化每种运动类型的数据数组
      motionTypes.forEach(type => {
        seriesMap[type] = {
          name: type,
          data: []
        }
      })

      // 遍历每个日期
      Object.keys(res.data).forEach(date => {
        const formattedDate = date.split('T')[0].substring(5) // 只保留月-日
        categories.push(formattedDate)

        // 为每种运动类型在当前日期添加数据
        motionTypes.forEach(type => {
          let found = false

          Object.keys(res.data[date]).forEach(motionName => {
            if (shortenTitle(motionName, 8) === type) {
              seriesMap[type].data.push(res.data[date][motionName])
              found = true
            }
          })

          // 如果当前日期没有该运动类型的数据，填充0
          if (!found) {
            seriesMap[type].data.push(0)
          }
        })
      })

      // 转换为图表所需格式
      chartData.value = {
        categories,
        series: Object.values(seriesMap)
      }

      chartNoData.value = categories.length === 0
    } else {
      chartNoData.value = true
    }
  } catch (error) {
    console.error('获取运动图表数据失败:', error)
    chartNoData.value = true
  } finally {
    chartLoading.value = false
  }
}

// 监听 currentDays 变化，触发数据请求
watch(currentTabDays, (newValue) => {
  getMotionChartData()
})

// 页面加载时获取数据
onMounted(() => {
  getMotionHistory()
  getMotionChartData()
})
</script>

<template>
  <view class="motion-history-container">
    <!-- 导航栏 -->
    <navbar title="运动记录"></navbar>

    <!-- 主要内容区 -->
    <view class="main-content">
      <!-- 顶部tab切换 -->
      <view class="tab-container">
        <TnTabs v-model="currentTab" height="90rpx" font-size="30" active-bold bar-width="60">
          <TnTabsItem title="运动记录"/>
          <TnTabsItem title="运动报表"/>
        </TnTabs>
      </view>

      <!-- 历史记录部分 -->
      <view v-if="currentTab === 0" class="section-container">
        <!-- 加载中 -->
        <view v-if="loading" class="loading-container">
          <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
          <text class="loading-text">加载中...</text>
        </view>

        <!-- 无数据提示 -->
        <view v-else-if="noData" class="empty-container">
          <tn-icon name="info" color="#999999" size="60rpx"></tn-icon>
          <text class="empty-text">暂无运动记录</text>
        </view>

        <!-- 时间轴展示运动记录 -->
        <view v-else class="timeline-container">
          <TnTimeLine>
            <TnTimeLineItem
                v-for="(item, index) in timeLineData"
                :key="index"
                :title="item.month"
                :title-icon="item.icon"
            >
              <TnTimeLineData
                  v-for="(dataItem, dataIndex) in item.data"
                  :key="dataIndex"
                  @click="viewExerciseDetail(dataItem.record)"
              >
                <view class="timeline-item">
                  <view class="timeline-date tn-flex justify-between items-center">
                    <view class="date">{{ dataItem.date }}日</view>
                    <view class="cal" v-if="dataItem.record.cal">{{ dataItem.record.cal }}kcal</view>
                  </view>
                  <view class="timeline-content tn-flex">
                    <image
                        class="exercise-image"
                        :src="dataItem.record.image"
                        mode="aspectFill"
                    ></image>
                    <view class="exercise-info">
                      <text class="exercise-title">{{ dataItem.record.title }}</text>
                      <text class="exercise-motion">{{ dataItem.record.motion }}</text>
                    </view>
                  </view>
                  <view class="tn-mt" >
                    <TnButton
                        @click=viewExerciseDetail(dataItem.record)
                        bg-color="#5677fc"
                        width="180rpx"
                        height="60rpx"
                        font-size="24rpx"
                        radius="30rpx"
                        :shadow="true"
                    >
                      <view class="tn-flex tn-flex-center-center">
                        <TnIcon name="chart" color="#fff" size="28rpx"></TnIcon>
                        <text class="tn-ml-xs tn-white_text">查看视频</text>
                      </view>
                    </TnButton>
                  </view>
                </view>
              </TnTimeLineData>
            </TnTimeLineItem>
          </TnTimeLine>
        </view>
      </view>

      <!-- 运动统计图表部分 -->
      <view v-else class="section-container">
        <view class="days-selector-container">
          <text class="days-label">选择时间范围：</text>
          <TnTabs
              v-model="currentTabDays"
            :value="currentDays" 
            @update:value="changeDays"
            height="70rpx" 
            font-size="26" 
            bottom-shadow="false"
            bar-color="#ff5a5f"
            active-color="#ff5a5f"
            bg-color="#f8f8f8"
          >
            <TnTabsItem
                v-for="(option,index) in daysOptions"
                :key="option.value"
                :value="option.value"
                :title="option.label"
            />
          </TnTabs>
        </view>

        <!-- 加载中 -->
        <view v-if="chartLoading" class="loading-container">
          <tn-icon name="loading" color="#5677fc" size="60rpx"></tn-icon>
          <text class="loading-text">加载中...</text>
        </view>

        <!-- 无数据提示 -->
        <view v-else-if="chartNoData" class="empty-container">
          <tn-icon name="info" color="#999999" size="60rpx"></tn-icon>
          <text class="empty-text">暂无运动统计数据</text>
        </view>

        <!-- 图表 -->
        <view v-else class="chart-container">
          <view class="charts-box">
            <qiun-data-charts
                type="column"
                :opts="opts"
                :chartData="chartData"
            />
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.motion-history-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 50rpx;
}

.main-content {
  padding-bottom: 30rpx;
}

.tab-container {
  background-color: #ffffff;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.section-container {
  margin: 30rpx;
  padding: 30rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.days-selector-container {
  display: flex;
  align-items: center;
  padding-bottom: 20rpx;
  margin-bottom: 20rpx;
  border-bottom: 1px solid #f0f0f0;

  .days-label {
    font-size: 28rpx;
    color: #666666;
    margin-right: 20rpx;
  }
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

.timeline-container {
  margin-top: 20rpx;

  .timeline-item {
    padding: 20rpx;
    background-color: #f9f9f9;
    border-radius: 12rpx;
    margin-bottom: 20rpx;

    .timeline-date {
      margin-bottom: 15rpx;

      .date {
        font-size: 28rpx;
        color: #666666;
      }

      .cal {
        font-size: 24rpx;
        color: #ff5a5f;
        background-color: rgba(255, 90, 95, 0.1);
        padding: 4rpx 12rpx;
        border-radius: 50rpx;
      }
    }

    .timeline-content {
      .exercise-image {
        width: 160rpx;
        height: 120rpx;
        border-radius: 8rpx;
        margin-right: 20rpx;
      }

      .exercise-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .exercise-title {
          font-size: 28rpx;
          color: #333333;
          line-height: 1.4;
          font-weight: bold;
          margin-bottom: 10rpx;
        }

        .exercise-motion {
          font-size: 24rpx;
          color: #999999;
        }
      }
    }
  }
}

.chart-container {
  margin-top: 20rpx;
}

.charts-box {
  width: 100%;
  height: 500rpx;
}
</style>