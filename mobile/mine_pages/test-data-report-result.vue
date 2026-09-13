<script setup>
import {ref, reactive, onMounted} from 'vue'
import {dataReport2} from '@/service/api/historyTestController'
import Navbar from '@/components/navbar.vue'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnLoading from '@tuniao/tnui-vue3-uniapp/components/loading/src/loading.vue'
import TnEmpty from '@tuniao/tnui-vue3-uniapp/components/empty/src/empty.vue'
import {onLoad} from '@dcloudio/uni-app'
import { formatTime } from '@/utils/formate.js'
// 不再直接引入 uCharts，而是使用 qiun-data-charts 组件

const reportData = ref(null)
const loading = ref(true)
const loadError = ref(false)
const moduleId = ref(0)
const recent = ref(5)
const moduleName = ref('')
// 图表配置
const chartData = ref({})
const opts = ref({
  color: ['#5677fc', '#19be6b', '#ff7900', '#e03997', '#1cbbb4', '#9c26b0', '#fbbd08', '#a5673f'],
  padding: [15, 15, 10, 15],
  enableScroll: true,
  legend: {
    show: true,
    position: 'bottom',
    float: 'center',
    itemGap: 20
  },
  xAxis: {
    disableGrid: true,
    rotateLabel: true,
    rotateAngle: 45,
    fontSize: 10,
    scrollShow: true,
    boundaryGap: true,
    itemCount: 5,
    calibration: true,
    axisLine: true
  },
  yAxis: {
    data: [{
      min: 0,
      max: 100,
      title: '分数',
      titleFontSize: 12,
      tofix: 0,
      splitNumber: 5
    }],
    showTitle: true,
    gridType: 'dash',
    dashLength: 2,
    gridColor: '#CCCCCC'
  },
  extra: {
    line: {
      type: 'straight',
      width: 2,
      activeType: 'hollow',
      linearType: 'custom',
      lineCap: 'round'
    },
    tooltip: {
      showBox: true,
      showArrow: true,
      showCategory: true,
      borderWidth: 1,
      borderRadius: 5,
      borderColor: '#000000',
      backgroundColor: '#FFFFFF'
    }
  },
  dataLabel: false,
  dataPointShape: true,
  dataPointShapeType: 'solid',
  touchMoveLimit: 60,
  width: uni.upx2px(650),
  height: uni.upx2px(450),
  background: '示例资料'
})

// 格式化日期
const formatDate = (dateArr) => {
  if (!dateArr || !Array.isArray(dateArr) || dateArr.length < 3) {
    return ''
  }

  const [year, month, day, hour, minute] = dateArr
  return `${month}/${day} ${hour}:${minute}`
}

// 获取测试报告数据
const getReportData = async () => {
  loading.value = true
  loadError.value = false

  try {
    // console.log('获取测试报告，moduleId:', moduleId.value, 'recent:', recent.value)
    const res = await dataReport2({
      moduleId: moduleId.value,
      recent: recent.value
    })

    if (res && res.code === 1 && res.data) {
      // console.log('获取测试报告成功:', res.data)
      reportData.value = res.data

      // 处理图表数据
      prepareChartData(res.data)
    } else {
      loadError.value = true
      uni.showToast({
        title: res?.msg || '获取报告数据失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取报告数据失败:', error)
    loadError.value = true
    uni.showToast({
      title: '获取报告数据失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 准备图表数据
const prepareChartData = (data) => {
  try {
    if (!data || !data.yaxis || !data.xaxis) return

    const {yaxis, xaxis} = data

    if (xaxis.length === 0) {
      console.error('X轴数据为空')
      return
    }

    let limitedXaxis = [...xaxis]
    let limitedYaxis = {}

    Object.keys(yaxis).forEach(dim => {
      limitedYaxis[dim] = [...yaxis[dim]]
    })

    const categories = limitedXaxis.map(date => formatTime(date, 'MM-DD HH:mm'))
    // console.log('X轴标签:', categories)

    const dimensions = Object.keys(limitedYaxis).slice(0, 8)
    // console.log('数据维度:', dimensions)

    const series = dimensions.map(dim => {
      return {
        name: dim,
        data: limitedYaxis[dim],
        format: val => val + ''
      }
    })
    // console.log('数据系列:', series)

    chartData.value = {
      categories: categories,
      series: series
    }

    // console.log("图表数据准备完成:", chartData.value)
  } catch (error) {
    console.error('准备图表数据时出错:', error)
  }
}

// 切换不同数量的记录
const switchRecent = (num) => {
  if (recent.value === num) return

  recent.value = num
  getReportData()
}

// 获取模块名称
const getModuleName = async (id) => {
  // 这里可以调用API获取模块详情来获取名称，或者从路由参数中获取
  // 简化起见，这里仅根据模块ID设置一个固定名称
  if (id === 1) {
    moduleName.value = '性格测试'
  } else if (id === 2) {
    moduleName.value = '抑郁症筛查'
  } else if (id === 3) {
    moduleName.value = '焦虑自评量表'
  } else {
    moduleName.value = '测试报表'
  }
}

// 模拟数据（测试用）
const useSimulatedData = () => {
  const mockData = {
    xaxis: [
      [2025, 4, 7, 14, 51, 10],
      [2025, 4, 7, 14, 2, 32],
      [2025, 4, 7, 13, 57, 18],
      [2025, 4, 7, 13, 53, 45],
      [2025, 4, 7, 13, 53, 30]
    ],
    yaxis: {
      "感知指数（P）": [25, 38, 25, 25, 25],
      "内向指数（I）": [0, 17, 50, 17, 17],
      "观察指数（S）": [100, 83, 50, 67, 67],
      "外向指数（E）": [100, 83, 50, 83, 83]
    }
  }

  reportData.value = mockData
  prepareChartData(mockData)
}
onLoad((options) => {
  // 获取路由参数
  const id = parseInt(options?.moduleId || '0')
  // @ts-ignore
  const recentParam = parseInt(options?.recent || '5')

  if (id) {
    moduleId.value = id
    recent.value = recentParam

    // 获取模块名称
    getModuleName(id)

    // 获取报告数据
    getReportData()
  } else {
    uni.showToast({
      title: '缺少测试模块ID',
      icon: 'none'
    })

    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
})

// onMounted(() => {
//   // 获取路由参数
//   const pages = getCurrentPages()
//   const currentPage = pages[pages.length - 1]
//   // @ts-ignore
//   const id = parseInt(currentPage?.options?.moduleId || '0')
//   // @ts-ignore
//   const recentParam = parseInt(currentPage?.options?.recent || '5')
//
//   if (id) {
//     moduleId.value = id
//     recent.value = recentParam
//
//     // 获取模块名称
//     getModuleName(id)
//
//     // 获取报告数据
//     getReportData()
//   } else {
//     uni.showToast({
//       title: '缺少测试模块ID',
//       icon: 'none'
//     })
//
//     setTimeout(() => {
//       uni.navigateBack()
//     }, 1500)
//   }
// })
</script>

<template>
  <view class="report-container">
    <!-- 导航栏 -->
    <navbar :title="moduleName || '测试报表'"></navbar>

    <!-- 选择记录数量 -->
    <view class="selector-container">
      <view class="section-title">
        <text class="title-text">选择显示数量</text>
      </view>
      <view class="selector-buttons">
        <TnButton
            v-for="num in [3, 5, 10]"
            :key="num"
            :bg-color="recent === num ? '#5677fc' : '#f5f7fa'"
            :color="recent === num ? '#ffffff' : '#666666'"
            height="70rpx"
            width="160rpx"
            font-size="28rpx"
            margin="0 20rpx 0 0"
            radius="35rpx"
            :shadow="recent === num"
            @tap="switchRecent(num)"
        >
          最近{{ num }}次
        </TnButton>
      </view>
    </view>

    <!-- 加载中 -->
    <view class="loading-container" v-if="loading">
      <TnLoading type="circle" size="60"></TnLoading>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 加载错误 -->
    <view class="error-container" v-else-if="loadError">
      <TnIcon name="error-circle" size="120rpx" color="#ff4d4f"></TnIcon>
      <text class="error-text">获取报告数据失败</text>
      <TnButton @tap="getReportData" margin="40rpx 0 0 0" bg-color="#5677fc">重新获取</TnButton>
    </view>

    <!-- 无数据 -->
    <view class="empty-container" v-else-if="!reportData">
      <TnEmpty
          icon="info-circle"
          text="暂无报表数据"
          :tip-text="true"
      >
        <template #bottom>
          <TnButton
              @tap="uni.navigateBack()"
              bg-color="#5677fc"
              width="40%"
              height="80rpx"
              margin="40rpx 0 0 0"
              shape="round"
          >
            <view class="tn-flex tn-flex-center-center">
              <text class="tn-white_text">返回</text>
            </view>
          </TnButton>
        </template>
      </TnEmpty>
    </view>

    <!-- 报表展示 -->
    <view class="chart-container" v-else>
      <view class="chart-title">历史测试得分趋势图</view>
      <view class="chart-desc">本图表展示最近{{ recent }}次测试的各项指标得分变化</view>

      <!-- 图表区域 -->
      <view class="charts-box">
        <qiun-data-charts
            type="line"
            :opts="opts"
            :chartData="chartData"
            :ontouch="true"
            canvas-id="lineChart"
        />
      </view>

      <!-- 数据解释 -->
      <view class="data-explanation">
        <view class="explanation-title">数据说明：</view>
        <view class="explanation-item">
          <text class="item-point">•</text>
          <text class="item-text">横轴为测试时间，纵轴为测试得分（0-100）</text>
        </view>
        <view class="explanation-item">
          <text class="item-point">•</text>
          <text class="item-text">点击图例可以隐藏/显示对应的数据线</text>
        </view>
        <view class="explanation-item">
          <text class="item-point">•</text>
          <text class="item-text">触摸图表可以查看具体数值</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.report-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 60rpx;
}

/* 选择器样式 */
.selector-container {
  margin: 30rpx;
  padding: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .section-title {
    margin-bottom: 20rpx;

    .title-text {
      font-size: 30rpx;
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
        width: 6rpx;
        height: 30rpx;
        background-color: #5677fc;
        border-radius: 4rpx;
      }
    }
  }

  .selector-buttons {
    display: flex;
    flex-wrap: wrap;
  }
}

/* 加载中和错误状态 */
.loading-container, .error-container, .empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;

  .loading-text, .error-text {
    margin-top: 20rpx;
    font-size: 28rpx;
    color: #666;
  }

  .error-text {
    color: #ff4d4f;
  }
}

/* 图表容器 */
.chart-container {
  margin: 30rpx;
  padding: 30rpx 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .chart-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    margin-bottom: 10rpx;
  }

  .chart-desc {
    font-size: 24rpx;
    color: #999;
    text-align: center;
    margin-bottom: 30rpx;
  }

  .charts-box {
    width: 100%;
    height: 500rpx;
  }

  .data-explanation {
    margin-top: 30rpx;
    padding: 20rpx;
    background-color: #f9f9f9;
    border-radius: 8rpx;

    .explanation-title {
      font-size: 28rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 15rpx;
    }

    .explanation-item {
      display: flex;
      margin-bottom: 10rpx;

      .item-point {
        margin-right: 10rpx;
        color: #5677fc;
      }

      .item-text {
        font-size: 26rpx;
        color: #666;
        line-height: 1.5;
      }
    }
  }
}
</style>