<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import navbar from "@/components/navbar.vue"
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnLoading from '@tuniao/tnui-vue3-uniapp/components/loading/src/loading.vue'
import {submit1} from '@/service/api/testController'
import { onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import { getHistoryTestDetail as fetchHistoryDetail } from '@/service/api/historyTestController'
import {onLoad} from '@dcloudio/uni-app'


// 测试结果数据
// 定义测试结果接口
const testResult = ref(null)
const isLoading = ref(true)
const loadError = ref(false)
const testId = ref('')
const answers = ref([])

// 雷达图数据
const radarChartData = ref(null)
const radarOpts = reactive({
  type: 'radar',
  canvasId: 'radarCanvas',
  background: '示例资料',
  animation: true,
  timing: 'easeOut',
  duration: 1000,
  dataLabel: false,
  dataPointShape: false,
  dataPointShapeType: 'solid',
  color: ['#5677fc', '#19be6b'],
  padding: [15, 15, 60, 15],
  legend: {
    show: true,
    position: 'bottom',
    fontSize: 13,
    lineHeight: 25
  },
  extra: {
    radar: {
      gridType: 'radar',
      gridColor: '#cccccc',
      gridCount: 3,
      opacity: 0.2,
      max: 200,
      border:true
    }
  }
})

// 柱状图数据
const columnChartData = ref(null)
const columnOpts = reactive({
  type: 'column',
  canvasId: 'columnCanvas',
  background: '示例资料',
  animation: true,
  timing: 'easeOut',
  duration: 1000,
  dataLabel: true,
  color: ['#5677fc', '#19be6b', '#ff7900', '#e03997'],
  padding: [15, 15, 60, 15],
  legend: {
    show: false
  },
  xAxis: {
    disableGrid: true,
    rotateLabel: true,
    rotateAngle: 45,
    fontSize: 10,
    itemCount: 8,
    boundaryGap: 'center',
    margin: 25
  },
  yAxis: {
    data: [
      {
        min: 0,
        max: 100,
        title: '指数',
        fontColor: '#666666'
      }
    ],
    showTitle: true,
    disabled: false,
    disableGrid: false,
    gridType: 'dash',
    dashLength: 4,
    splitNumber: 5
  },
  extra: {
    column: {
      type: 'group',
      width: 20,
      activeBgColor: '#000000',
      activeBgOpacity: 0.08,
      seriesGap: 2,
      categoryGap: 8,
      barBorderRadius: [5, 5, 0, 0]
    }
  }
})

// 获取测试结果
const getTestResult = async (id, answerData) => {
  isLoading.value = true
  loadError.value = false

  try {
    console.log('提交测试ID:', id, '答案:', answerData)

    // 调用API获取测试结果
    const res = await submit1({
      moduleId: parseInt(id),
      ans: answerData
    })

    console.log('测试结果数据:', res)

    if (res && res.code === 1 && res.data) {
      testResult.value = res.data

      // 准备图表数据
      prepareChartData()
    } else {
      loadError.value = true
      uni.showToast({
        title: '获取测试结果失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取测试结果失败:', error)
    loadError.value = true
    uni.showToast({
      title: '获取测试结果失败',
      icon: 'none'
    })
  } finally {
    isLoading.value = false
  }
}

// 获取历史测试结果详情
const getHistoryResult = async (historyId) => {
  isLoading.value = true
  loadError.value = false

  try {
    console.log('获取历史测试记录:', historyId)
    const res = await fetchHistoryDetail({
      id: historyId
    })

    console.log('历史测试结果数据:', res)

    if (res && res.code === 1 && res.data) {
      testResult.value = res.data

      // 准备图表数据
      prepareChartData()
    } else {
      loadError.value = true
      uni.showToast({
        title: '获取历史测试结果失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取历史测试结果失败:', error)
    loadError.value = true
    uni.showToast({
      title: '获取历史测试结果失败',
      icon: 'none'
    })
  } finally {
    isLoading.value = false
  }
}

// 准备图表数据
const prepareChartData = () => {
  if (!testResult.value || !testResult.value.typeAndResult) return

  const typeAndResult = testResult.value.typeAndResult
  const categories = Object.keys(typeAndResult)
  const values = Object.values(typeAndResult)
  
  // 判断数据项数量，决定使用哪种图表
  const dataCount = categories.length
  
  if (dataCount <= 1) {
    // 单一数据项，不使用雷达图，只使用柱状图或进度条
    radarChartData.value = null
    
    // 柱状图数据
    columnChartData.value = {
      categories: categories,
      series: [
        {
          name: '指数值',
          data: values
        }
      ]
    }
  } else {
    // 多数据项，使用雷达图和柱状图
    // 雷达图数据
    radarChartData.value = {
      categories: categories,
      series: [
        {
          name: '指数值',
          data: values
        }
      ]
    }
    
    // 柱状图数据
    columnChartData.value = {
      categories: categories,
      series: [
        {
          name: '指数值',
          data: values
        }
      ]
    }
  }
}

// 获取性格类型 - 修改为根据实际数据判断
const personalityType = computed(() => {
  if (!testResult.value || !testResult.value.typeAndResult) return ''
  
  // 检查是否有多个能力维度
  const typeAndResult = testResult.value.typeAndResult
  const keys = Object.keys(typeAndResult)
  
  if (keys.length <= 1) return '' // 单一维度不显示性格类型
  
  // 获取最高分的能力维度
  let maxKey = keys[0]
  let maxValue = typeAndResult[maxKey]
  
  keys.forEach(key => {
    if (typeAndResult[key] > maxValue) {
      maxValue = typeAndResult[key]
      maxKey = key
    }
  })
  
  return maxKey // 返回最高分的能力维度作为主要特质
})

// 获取性格类型描述 - 修改为根据实际数据提供描述
const personalityDescription = computed(() => {
  if (!personalityType.value) return ''
  
  // 根据不同的能力维度提供描述
  const typeMap = {
    '学习适应能力': '学习能力优秀',
    '人际关系适应能力': '人际关系良好',
    '满意度': '满意度较高',
    '校园适应能力': '校园适应良好',
    '择业适应能力': '职业规划清晰',
    '情绪适应能力': '情绪管理能力强',
    '自我适应能力': '自我认知清晰'
  }
  
  return typeMap[personalityType.value] || '综合能力良好'
})

// 分享测试结果
const shareResult = () => {
  // 保留这个方法，但内部实现改为空，因为会通过onShareAppMessage处理
}

// 定义分享给朋友
onShareAppMessage(() => {
  if (!testResult.value) {
    return {
      title: '心理测试结果',
      path: '/function_pages/views/test-list'
    };
  }
  
  // 构建正确的分享路径，区分历史测试和新测试

  
  let path = ''
  if (historyId.value) {
    path = `/function_pages/views/test-result?historyId=${historyId.value}`
  } else {
    path = `/function_pages/views/test-result?moduleId=${testId.value}&answers=${encodeURIComponent(JSON.stringify(answers.value || []))}`
  }
  
  return {
    title: `我的${testResult.value.moduleName}：${personalityType.value || '测试结果'}`,
    path: path,
    imageUrl: testResult.value.image || '',
    success: function(res) {
      uni.showToast({
        title: '分享成功',
        icon: 'success'
      });
    },
    fail: function(err) {
      console.log("分享失败", err);
    }
  };
});

// 定义分享到朋友圈
onShareTimeline(() => {
  if (!testResult.value) {
    return {
      title: '心理测试结果',
      query: ''
    };
  }
  

  let query = ''
  if (historyId.value) {
    query = `historyId=${historyId}`
  } else {
    query = `moduleId=${testId.value}&answers=${encodeURIComponent(JSON.stringify(answers.value || []))}`
  }
  return {
    title: `我的${testResult.value.moduleName}：${personalityType.value || '测试结果'}`,
    query: query,
    imageUrl: testResult.value.image || '',
  };
});

// 重新测试
const retakeTest = () => {
  uni.redirectTo({
    url: `/function_pages/views/test-list`
  })
}

const previewImage = (url) => {
  if (!url) return
  uni.previewImage({
    urls: [url],
    current: url,
    indicator: 'number',
    loop: false
  })
}

const historyId = ref()
onLoad((options)=>{

  // @ts-ignore
  const id = options?.moduleId || '1'
  // @ts-ignore
  const encodedAnswers = options?.answers || ''
  // @ts-ignore
  historyId.value = options?.historyId || ''

  testId.value = id

  if (historyId.value) {
    // 如果有历史记录ID，则获取历史测试结果详情
    getHistoryResult(historyId.value)
  } else {
    // 否则获取新的测试结果
    console.log('获取新测试结果，测试ID:', id)
    let answerData = []
    try {
      // 解码并解析答案数组
      if (encodedAnswers) {
        answerData = JSON.parse(decodeURIComponent(encodedAnswers))
        answers.value = answerData
      }
    } catch (error) {
      console.error('解析答案数组失败:', error)
      uni.showToast({
        title: '解析答案失败',
        icon: 'none'
      })
    }
    getTestResult(id, answerData)
  }
})

// onMounted(() => {
//   const pages = getCurrentPages()
//   const currentPage = pages[pages.length - 1]
//   // @ts-ignore
//   const id = currentPage?.options?.moduleId || '1'
//   // @ts-ignore
//   const encodedAnswers = currentPage?.options?.answers || ''
//   // @ts-ignore
//   const historyId = currentPage?.options?.historyId || ''
//
//   testId.value = id
//
//   if (historyId) {
//     // 如果有历史记录ID，则获取历史测试结果详情
//     getHistoryResult(historyId)
//   } else {
//     // 否则获取新的测试结果
//     console.log('获取新测试结果，测试ID:', id)
//     let answerData = []
//     try {
//       // 解码并解析答案数组
//       if (encodedAnswers) {
//         answerData = JSON.parse(decodeURIComponent(encodedAnswers))
//         answers.value = answerData
//       }
//     } catch (error) {
//       console.error('解析答案数组失败:', error)
//       uni.showToast({
//         title: '解析答案失败',
//         icon: 'none'
//       })
//     }
//
//     getTestResult(id, answerData)
//   }
// })
</script>

<template>
  <view class="container">
    <!-- 导航栏 -->
    <navbar title="测试结果"></navbar>

    <!-- 加载中 -->
    <view class="loading-container" v-if="isLoading">
      <TnLoading type="flower" size="80"></TnLoading>
      <text class="loading-text">分析测试结果中...</text>
    </view>

    <!-- 加载错误 -->
    <view class="error-container" v-else-if="loadError">
      <TnIcon name="error-circle" size="120rpx" color="#ff4d4f"></TnIcon>
      <text class="error-text">获取测试结果失败</text>
      <TnButton @tap="getTestResult(testId)" margin="40rpx 0 0 0" bg-color="#5677fc">重新获取</TnButton>
    </view>

    <!-- 测试结果 -->
    <template v-else-if="testResult">
      <!-- 标题 -->
      <view class="result-header">
        <view class="result-title">{{ testResult.moduleName }}</view>
        <view class="personality-type" v-if="personalityType">
          <text class="type-code">{{ personalityType }}</text>
          <text class="type-name">{{ personalityDescription }}</text>
        </view>
      </view>

      <!-- 结果图片 -->
      <view class="result-image" v-if="testResult.image" @tap="previewImage(testResult.image)">
        <image :src="testResult.image" mode="aspectFill"></image>
        <view class="image-preview-hint">
          <TnIcon name="search" size="32rpx" color="#ffffff"></TnIcon>
          <text class="preview-text">点击查看大图</text>
        </view>
      </view>

      <!-- 结果描述 -->
      <view class="result-description" v-if="testResult.description">
        <view class="section-title">
          <TnIcon name="info-circle" size="40rpx" color="#5677fc"></TnIcon>
          <text class="title-text">结果解析</text>
        </view>
        <text class="description-text">{{ testResult.description }}</text>
      </view>

      <!-- 雷达图 -->
      <view class="chart-card" v-if="radarChartData && Object.keys(testResult.typeAndResult).length > 1">
        <view class="section-title">
          <TnIcon name="radar" size="40rpx" color="#5677fc"></TnIcon>
          <text class="title-text">多维度分析</text>
        </view>
        <view class="chart-container" v-if="radarChartData">
          <qiun-data-charts
              type="radar"
              :opts="radarOpts"
              :chartData="radarChartData"
          />
        </view>
      </view>

      <!-- 柱状图 -->
      <view class="chart-card">
        <view class="section-title">
          <TnIcon name="chart" size="40rpx" color="#5677fc"></TnIcon>
          <text class="title-text">{{ Object.keys(testResult.typeAndResult).length > 1 ? '维度对比' : '测试结果' }}</text>
        </view>
        <view class="chart-container column-chart-container" v-if="columnChartData">
          <qiun-data-charts
              type="column"
              :opts="columnOpts"
              :chartData="columnChartData"
          />
        </view>
      </view>

      <!-- 单一指标进度条 -->
      <view class="single-result-card" v-if="Object.keys(testResult.typeAndResult).length === 1">
        <view class="section-title">
          <TnIcon name="chart-pie" size="40rpx" color="#5677fc"></TnIcon>
          <text class="title-text">测试得分</text>
        </view>
        <view class="single-result-container">
          <view class="result-name">{{ Object.keys(testResult.typeAndResult)[0] }}</view>
          <view class="result-value">{{ Object.values(testResult.typeAndResult)[0] }}</view>
          <view class="result-progress-container">
            <view class="result-progress-bg">
              <view 
                class="result-progress-bar" 
                :style="{ width: `${(Object.values(testResult.typeAndResult)[0] / 100) * 100}%` }"
              ></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 性格特质解析 -->
      <view class="traits-card" v-if="testResult.typeAndResult">
        <view class="section-title">
          <TnIcon name="star" size="40rpx" color="#5677fc"></TnIcon>
          <text class="title-text">性格特质详解</text>
        </view>
        <view class="traits-list">
          <view
              class="trait-item"
              v-for="(value, key) in testResult.typeAndResult"
              :key="key"
          >
            <view class="trait-name">{{ key }}</view>
            <view class="trait-progress">
              <view
                  class="progress-bar"
                  :style="{ width: `${value}%`, backgroundColor: getTraitColor(key) }"
              ></view>
            </view>
            <view class="trait-value">{{ value }}%</view>
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-buttons">
        <view class="tn-m-xs tn-text-center" style="width: 45%">
          <button 
            class="share-button" 
            open-type="share">
            <TnButton
              bg-color="#19be6b"
              width="100%"
              height="90rpx"
              :shadow="true">
              <view class="tn-flex tn-flex-center-center">
                <TnIcon name="share" color="#fff" size="36rpx"></TnIcon>
                <text class="tn-ml-xs tn-white_text">分享结果</text>
              </view>
            </TnButton>
          </button>
        </view>

        <view class="tn-m-xs tn-text-center" style="width: 45%">
          <TnButton
            @tap="retakeTest"
            bg-color="#5677fc"
            width="100%"
            height="90rpx"
            :shadow="true">
            <view class="tn-flex tn-flex-center-center">
              <TnIcon name="refresh" color="#fff" size="36rpx"></TnIcon>
              <text class="tn-ml-xs tn-white_text">重新测试</text>
            </view>
          </TnButton>
        </view>
      </view>
    </template>

    <!-- 题号弹窗 -->
    <tn-popup v-model="showAnswerSheet" mode="center" :mask-close-able="true" radius="24rpx">
      <view class="answer-sheet">
        <view class="sheet-header tn-flex tn-flex-center-between">
          <text class="sheet-title tn-text-bold">答题卡</text>
          <tn-icon name="close" size="40rpx" @click="showAnswerSheet = false"></tn-icon>
        </view>
        
        <scroll-view class="sheet-content-scroll" scroll-y>
          <view class="sheet-content">
            <view 
              v-for="(_, index) in questions" 
              :key="index"
              class="question-dot"
              :class="{ 
                'answered': answers[index] !== -1,
                'current': currentIndex === index
              }"
              @click="goToQuestion(index)"
            >
              {{ questions[index].questionNumber }}
            </view>
          </view>
        </scroll-view>
        
        <view class="sheet-footer tn-flex tn-flex-start-center tn-mt-lg">
          <view class="legend-item tn-flex tn-flex-start-center">
            <view class="legend-dot answered"></view>
            <text class="legend-text tn-gray_text">已答</text>
          </view>
          <view class="legend-item tn-flex tn-flex-start-center tn-ml-lg">
            <view class="legend-dot"></view>
            <text class="legend-text tn-gray_text">未答</text>
          </view>
          <view class="legend-item tn-flex tn-flex-start-center tn-ml-lg">
            <view class="legend-dot current"></view>
            <text class="legend-text tn-gray_text">当前题</text>
          </view>
        </view>
      </view>
    </tn-popup>
  </view>
</template>

<script>
// 获取特质颜色
function getTraitColor(trait) {
  const colorMap = {
    '外向指数（E）': '#5677fc',
    '内向指数（I）': '#19be6b',
    '观察指数（S）': '#ff7900',
    '直觉指数（N）': '#e03997',
    '逻辑指数（T）': '#1cbbb4',
    '情感指数（F）': '#9c26b0',
    '判断指数（J）': '#fbbd08',
    '感知指数（P）': '#a5673f'
  }

  return colorMap[trait] || '#5677fc'
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f8f9fc;
  padding-bottom: 100rpx;
}

.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;

  .loading-text, .error-text {
    margin-top: 30rpx;
    font-size: 30rpx;
    color: #666;
  }

  .error-text {
    color: #ff4d4f;
  }
}

.result-header {
  padding: 40rpx 30rpx;
  background-color: #ffffff;
  text-align: center;

  .result-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .personality-type {
    display: flex;
    flex-direction: column;
    align-items: center;

    .type-code {
      font-size: 60rpx;
      font-weight: bold;
      color: #5677fc;
      margin-bottom: 10rpx;
    }

    .type-name {
      font-size: 32rpx;
      color: #666;
      background-color: #f0f5ff;
      padding: 10rpx 30rpx;
      border-radius: 30rpx;
    }
  }
}

.result-image {
  position: relative;
  margin: 0 30rpx;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);

  image {
    width: 100%;
    height: 360rpx;
    display: block;
  }

  .image-preview-hint {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.5);
    padding: 10rpx 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.result-description, .chart-card, .traits-card {
  margin: 30rpx;
  padding: 30rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .section-title {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    .title-text {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-left: 16rpx;
    }
  }

  .description-text {
    font-size: 28rpx;
    color: #666;
    line-height: 1.6;
    text-align: justify;
  }
}

.chart-container {
  height: 500rpx;
  width: 100%;
}

.chart-card {
  margin: 30rpx;
  padding: 30rpx 20rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.column-chart-container {
  height: 550rpx;
}

.traits-list {
  .trait-item {
    display: flex;
    align-items: center;
    margin-bottom: 30rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .trait-name {
      width: 200rpx;
      font-size: 26rpx;
      color: #666;
    }

    .trait-progress {
      flex: 1;
      height: 20rpx;
      background-color: #f5f5f5;
      border-radius: 10rpx;
      overflow: hidden;
      margin: 0 20rpx;

      .progress-bar {
        height: 100%;
        border-radius: 10rpx;
        transition: width 0.5s;
      }
    }

    .trait-value {
      width: 80rpx;
      font-size: 26rpx;
      color: #333;
      text-align: right;
      font-weight: bold;
    }
  }
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  padding: 30rpx;
  margin-top: 30rpx;
}

.single-result-card {
  margin: 30rpx;
  padding: 30rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .section-title {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    .title-text {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-left: 16rpx;
    }
  }

  .single-result-container {
    padding: 30rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;

    .result-name {
      font-size: 32rpx;
      color: #333;
      margin-bottom: 20rpx;
    }

    .result-value {
      font-size: 80rpx;
      font-weight: bold;
      color: #5677fc;
      margin-bottom: 30rpx;
    }

    .result-progress-container {
      width: 100%;
      padding: 0 30rpx;

      .result-progress-bg {
        width: 100%;
        height: 30rpx;
        background-color: #f5f5f5;
        border-radius: 15rpx;
        overflow: hidden;
      }
    }
  }
}

.result-progress-bar {
  height: 100%;
  background: linear-gradient(to right, #5677fc, #19be6b);
  border-radius: 15rpx;
  transition: width 1s ease-in-out;
}

.answer-sheet {
  width: 600rpx;
  padding: 40rpx;
  
  .sheet-header {
    margin-bottom: 20rpx;
    
    .sheet-title {
      font-size: 34rpx;
      color: #333;
    }
  }

  .sheet-content-scroll {
    max-height: 600rpx;
  }

  .sheet-content {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    padding-bottom: 20rpx;

    .question-dot {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      margin: 15rpx;
      font-size: 28rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f5f5f5;
      color: #666;
      transition: all 0.3s;
      
      &:active {
        transform: scale(0.9);
      }

      &.answered {
        background-color: #19be6b;
        color: #fff;
      }
      
      &.current {
        background-color: #5677fc;
        color: #fff;
        transform: scale(1.1);
        box-shadow: 0 2rpx 10rpx rgba(86, 119, 252, 0.3);
      }
    }
  }
  
  .sheet-footer {
    .legend-item {
      .legend-dot {
        width: 30rpx;
        height: 30rpx;
        border-radius: 50%;
        background-color: #f5f5f5;
        margin-right: 10rpx;
        
        &.answered {
          background-color: #19be6b;
        }
        
        &.current {
          background-color: #5677fc;
        }
      }
      
      .legend-text {
        font-size: 24rpx;
      }
    }
  }
}

/* 分享按钮 */
.share-button {
  background: none;
  border: none;
  margin: 0;
  padding: 0;
  line-height: normal;
  width: 100%;
  
  &::after {
    border: none;
  }
}

/* 底部按钮样式 */
.tn-button {
  transition: all 0.3s;
  
  &:active {
    transform: scale(0.96);
  }
}
</style> 