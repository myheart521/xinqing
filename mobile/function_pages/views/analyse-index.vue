<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import navbar from "@/components/navbar.vue"
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import TnTag from '@tuniao/tnui-vue3-uniapp/components/tag/src/tag.vue'
import TnLoading from '@tuniao/tnui-vue3-uniapp/components/loading/src/loading.vue'
import {analysis} from '@/service/api/aiClaudeController'
import {useUserStore} from '@/stores/user'

// 用户信息
const userStore = useUserStore()

// 分析状态
const isAnalyzing = ref(false)
const analysisComplete = ref(false)
const analysisData = ref(null)
const analysisError = ref(null)

// 雷达图配置
const radarChartData = ref(null)
const radarOpts = reactive({
  type: 'radar',
  canvasId: 'radarCanvas',
  background: '示例资料',
  animation: true,
  timing: 'easeOut',
  duration: 1000,
  dataLabel: false,
  dataPointShape: true,
  dataPointShapeType: 'solid',
  color: ['#5677fc', '#19be6b', '#ff7900'],
  padding: [15, 15, 0, 15],
  legend: {
    show: true,
    position: 'bottom',
    float: 'center',
    padding: 10,
    margin: 0,
    fontSize: 13
  },
  extra: {
    radar: {
      gridType: 'radar',
      gridColor: '#cccccc',
      gridCount: 3,
      opacity: 0.2,
      labelColor: '#666666',
      max: 10
    }
  }
})

// 折线图配置
const lineChartData = ref(null)
const lineOpts = reactive({
  type: 'line',
  canvasId: 'lineCanvas',
  background: '示例资料',
  animation: true,
  timing: 'easeOut',
  duration: 1000,
  dataLabel: true,
  dataPointShape: true,
  enableScroll: false,
  color: ['#5677fc', '#19be6b', '#ff7900'],
  padding: [15, 15, 0, 15],
  legend: {
    show: true,
    position: 'bottom',
    float: 'center',
    padding: 10,
    margin: 0,
    fontSize: 13
  },
  xAxis: {
    disableGrid: true,
    rotateLabel: false,
    type: 'category',
    gridType: 'dash',
    itemCount: 5,
    scrollShow: false,
    scrollAlign: 'left',
    scrollColor: '#A6A6A6',
    scrollBackgroundColor: '#F6F6F6'
  },
  yAxis: {
    gridType: 'dash',
    splitNumber: 5,
    min: 0,
    max: 10,
    format: val => val.toFixed(0)
  },
  extra: {
    line: {
      type: 'straight',
      width: 2,
      activeType: 'hollow'
    }
  }
})

// 情绪稳定性评分描述
const emotionalStabilityDesc = computed(() => {
  if (!analysisData.value) return ''
  const score = analysisData.value.metrics.emotionalStability
  if (score >= 8) return '您的情绪非常稳定，能够很好地应对压力和挑战。'
  if (score >= 6) return '您的情绪较为稳定，但在某些情况下可能会有波动。'
  if (score >= 4) return '您的情绪稳定性中等，可能需要更多的情绪管理技巧。'
  if (score >= 2) return '您的情绪波动较大，建议学习一些情绪调节方法。'
  return '您的情绪稳定性较低，建议寻求专业帮助来改善情绪管理能力。'
})

// 社交参与度评分描述
const socialEngagementDesc = computed(() => {
  if (!analysisData.value) return ''
  const score = analysisData.value.metrics.socialEngagement
  if (score >= 8) return '您的社交参与度非常高，人际关系丰富且积极。'
  if (score >= 6) return '您的社交参与度良好，能够维持健康的社交圈。'
  if (score >= 4) return '您的社交参与度中等，可能需要更多的社交互动。'
  if (score >= 2) return '您的社交参与度较低，建议增加社交活动。'
  return '您的社交参与度很低，可能存在社交孤立的风险，建议寻求支持。'
})

// 压力水平评分描述
const stressLevelDesc = computed(() => {
  if (!analysisData.value) return ''
  const score = analysisData.value.metrics.stressLevel
  if (score >= 8) return '您的压力水平非常高，需要采取措施来减轻压力。'
  if (score >= 6) return '您的压力水平较高，建议学习一些减压技巧。'
  if (score >= 4) return '您的压力水平中等，需要注意压力管理。'
  if (score >= 2) return '您的压力水平较低，能够较好地应对日常压力。'
  return '您的压力水平很低，心理状态良好。'
})

// 获取分析数据
const getAnalysisData = async () => {
  if (isAnalyzing.value) return

  isAnalyzing.value = true
  analysisError.value = null

  try {
    // 调用分析接口，使用userStore.userInfo.id作为请求参数
    const result = await analysis({
      id: userStore.userInfo.id
    })
    console.log("分析的结果数据为：", result)
    analysisData.value = result.data
    analysisComplete.value = true

    // 准备雷达图数据
    prepareRadarChartData()

    // 准备折线图数据
    prepareLineChartData()

  } catch (error) {
    console.error('分析失败:', error)
    analysisError.value = '分析失败，请稍后重试'
  } finally {
    isAnalyzing.value = false
  }
}

// 准备雷达图数据
const prepareRadarChartData = () => {
  if (!analysisData.value) return

  const metrics = analysisData.value.metrics

  radarChartData.value = {
    categories: ['情绪稳定性', '社交参与度', '压力水平'],
    series: [
      {
        name: '当前状态',
        data: [
          metrics.emotionalStability,
          metrics.socialEngagement,
          metrics.stressLevel
        ]
      },
      {
        name: '理想状态',
        data: [8, 7, 3]
      }
    ]
  }
}

// 准备折线图数据
const prepareLineChartData = () => {
  if (!analysisData.value) return

  // 模拟过去几周的数据
  lineChartData.value = {
    categories: ['4周前', '3周前', '2周前', '1周前', '当前'],
    series: [
      {
        name: '情绪稳定性',
        data: [
          Math.max(1, analysisData.value.metrics.emotionalStability - 2 + Math.random() * 2),
          Math.max(1, analysisData.value.metrics.emotionalStability - 1.5 + Math.random() * 2),
          Math.max(1, analysisData.value.metrics.emotionalStability - 1 + Math.random() * 2),
          Math.max(1, analysisData.value.metrics.emotionalStability - 0.5 + Math.random() * 1),
          analysisData.value.metrics.emotionalStability
        ].map(val => parseFloat(val.toFixed(1)))
      },
      {
        name: '社交参与度',
        data: [
          Math.max(1, analysisData.value.metrics.socialEngagement - 2 + Math.random() * 2),
          Math.max(1, analysisData.value.metrics.socialEngagement - 1.5 + Math.random() * 2),
          Math.max(1, analysisData.value.metrics.socialEngagement - 1 + Math.random() * 2),
          Math.max(1, analysisData.value.metrics.socialEngagement - 0.5 + Math.random() * 1),
          analysisData.value.metrics.socialEngagement
        ].map(val => parseFloat(val.toFixed(1)))
      },
      {
        name: '压力水平',
        data: [
          Math.min(10, analysisData.value.metrics.stressLevel + 2 - Math.random() * 2),
          Math.min(10, analysisData.value.metrics.stressLevel + 1.5 - Math.random() * 2),
          Math.min(10, analysisData.value.metrics.stressLevel + 1 - Math.random() * 2),
          Math.min(10, analysisData.value.metrics.stressLevel + 0.5 - Math.random() * 1),
          analysisData.value.metrics.stressLevel
        ].map(val => parseFloat(val.toFixed(1)))
      }
    ]
  }
}

// 模拟数据（仅用于开发测试）
const simulateAnalysisData = () => {
  analysisData.value = {
    summary: "根据您的使用数据分析，您近期的心理状态整体良好，但存在一定的压力和情绪波动。您的社交参与度中等，建议适当增加社交活动和户外运动，有助于缓解压力和提升情绪。您的睡眠质量有所下降，可以尝试在睡前放松身心，避免使用电子设备。总体而言，您的心理健康状况需要关注，但不必过度担忧，通过适当的自我调节和生活方式调整，可以有效改善当前状态。",
    metrics: {
      emotionalStability: 6.5,
      socialEngagement: 5.8,
      stressLevel: 7.2
    },
    keywords: ["情绪波动", "社交需求", "压力管理", "睡眠质量", "自我关怀"],
    visualization: {
      charts: ["RADAR_CHART", "LINE_CHART"],
      reason: "雷达图适合展示多维度评分对比，折线图可以展示心理状态的变化趋势。"
    },
    recommendArticles: {
      id: 1,
      title: "如何通过运动缓解压力",
      descriptions: "运动是一种有效的压力缓解方法，可以促进血液循环，释放内啡肽，提高情绪。建议您尝试进行有氧运动，如跑步、游泳、瑜伽等，每周至少进行3次，每次30分钟以上。"
    }
  }

  analysisComplete.value = true

  // 准备雷达图数据
  prepareRadarChartData()

  // 准备折线图数据
  prepareLineChartData()
}

//跳转到文章页面
const toWiki = (id)=>{
  uni.navigateTo({
    url: `/function_pages/views/motion-index-detail?id=${id}&isLiked=${false}&isCollection=${false}`
  });
}

// 页面加载时自动开始分析
onMounted(() => {
  // 实际环境中使用真实API
  getAnalysisData()

  // 开发环境使用模拟数据
  // simulateAnalysisData()
})
</script>

<template>
  <view class="analyse-container">
    <!-- 导航栏 -->
    <navbar title="心理状态分析"></navbar>

    <!-- 分析中状态 -->
    <view class="loading-section" v-if="isAnalyzing">
      <TnLoading type="flower" size="80"></TnLoading>
      <text class="loading-text">正在分析您的心理状态...</text>
      <text class="loading-desc">这可能需要一点时间，请耐心等待</text>
    </view>

    <!-- 分析错误 -->
    <view class="error-section" v-else-if="analysisError">
      <TnIcon name="error-circle" size="80rpx" color="#ff4d4f"></TnIcon>
      <text class="error-text">{{ analysisError }}</text>
      <TnButton @tap="getAnalysisData" margin="40rpx 0 0 0">重新分析</TnButton>
    </view>

    <!-- 分析结果 -->
    <view class="result-section" v-else-if="analysisComplete && analysisData">
      <!-- 分析摘要 -->
      <view class="summary-card">
        <view class="card-header">
          <TnIcon name="brain" size="40rpx" color="#5677fc"></TnIcon>
          <text class="card-title">分析摘要</text>
        </view>
        <view class="card-content">
          <text class="summary-text">{{ analysisData.summary }}</text>
        </view>
      </view>

      <!-- 关键词标签 -->
      <view class="keywords-card">
        <view class="card-header">
          <TnIcon name="tag" size="40rpx" color="#5677fc"></TnIcon>
          <text class="card-title">关键特征</text>
        </view>
        <view class="card-content">
          <view class="keywords-list">
            <TnTag
                v-for="(keyword, index) in analysisData.keywords"
                :key="index"
                type="light"
                :bg-color="['#5677fc', '#19be6b', '#ff7900', '#e03997', '#1cbbb4'][index % 5]"
                margin="0 20rpx 20rpx 0"
                padding="16rpx 30rpx"
            >
              {{ keyword }}
            </TnTag>
          </view>
        </view>
      </view>

      <!-- 评分指标 -->
      <view class="metrics-card">
        <view class="card-header">
          <TnIcon name="chart" size="40rpx" color="#5677fc"></TnIcon>
          <text class="card-title">心理评分</text>
        </view>
        <view class="card-content">
          <!-- 情绪稳定性 -->
          <view class="metric-item">
            <view class="metric-header">
              <text class="metric-name">情绪稳定性</text>
              <text class="metric-score">{{ analysisData.metrics.emotionalStability.toFixed(1) }}/10</text>
            </view>
            <view class="metric-progress">
              <view
                  class="progress-bar"
                  :style="{ width: `${analysisData.metrics.emotionalStability * 10}%`, backgroundColor: '#5677fc' }"
              ></view>
            </view>
            <text class="metric-desc">{{ emotionalStabilityDesc }}</text>
          </view>

          <!-- 社交参与度 -->
          <view class="metric-item">
            <view class="metric-header">
              <text class="metric-name">社交参与度</text>
              <text class="metric-score">{{ analysisData.metrics.socialEngagement.toFixed(1) }}/10</text>
            </view>
            <view class="metric-progress">
              <view
                  class="progress-bar"
                  :style="{ width: `${analysisData.metrics.socialEngagement * 10}%`, backgroundColor: '#19be6b' }"
              ></view>
            </view>
            <text class="metric-desc">{{ socialEngagementDesc }}</text>
          </view>

          <!-- 压力水平 -->
          <view class="metric-item">
            <view class="metric-header">
              <text class="metric-name">压力水平</text>
              <text class="metric-score">{{ analysisData.metrics.stressLevel.toFixed(1) }}/10</text>
            </view>
            <view class="metric-progress">
              <view
                  class="progress-bar"
                  :style="{ width: `${analysisData.metrics.stressLevel * 10}%`, backgroundColor: '#ff7900' }"
              ></view>
            </view>
            <text class="metric-desc">{{ stressLevelDesc }}</text>
          </view>
        </view>
      </view>

      <!-- 雷达图 -->
      <view class="chart-card" v-if="radarChartData">
        <view class="card-header">
          <TnIcon name="radar" size="40rpx" color="#5677fc"></TnIcon>
          <text class="card-title">多维度分析</text>
        </view>
        <view class="card-content">
          <view class="chart-container">
            <qiun-data-charts
                type="radar"
                :opts="radarOpts"
                :chartData="radarChartData"
            />
          </view>
        </view>
      </view>

      <!-- 折线图 -->
      <view class="chart-card" v-if="lineChartData">
        <view class="card-header">
          <TnIcon name="line-chart" size="40rpx" color="#5677fc"></TnIcon>
          <text class="card-title">趋势变化</text>
        </view>
        <view class="card-content">
          <view class="chart-container">
            <qiun-data-charts
                type="line"
                :opts="lineOpts"
                :chartData="lineChartData"
            />
          </view>
        </view>
      </view>

      <!-- 建议卡片 -->
      <view class="suggestion-card">
        <view class="card-header">
          <TnIcon name="lightbulb" size="40rpx" color="#5677fc"></TnIcon>
          <text class="card-title">改善建议</text>
        </view>
        <view class="card-content">
          <view class="suggestion-item">
            <TnIcon name="meditation" size="40rpx" color="#19be6b"></TnIcon>
            <text class="suggestion-text">每天进行15-20分钟的冥想练习，有助于提高情绪稳定性。</text>
          </view>
          <view class="suggestion-item">
            <TnIcon name="people" size="40rpx" color="#19be6b"></TnIcon>
            <text class="suggestion-text">增加社交活动，每周至少参加一次社交聚会或团体活动。</text>
          </view>
          <view class="suggestion-item">
            <TnIcon name="run" size="40rpx" color="#19be6b"></TnIcon>
            <text class="suggestion-text">保持规律运动，每周至少进行3次30分钟以上的有氧运动。</text>
          </view>
          <view class="suggestion-item">
            <TnIcon name="moon" size="40rpx" color="#19be6b"></TnIcon>
            <text class="suggestion-text">保持良好的睡眠习惯，每晚保证7-8小时的充足睡眠。</text>
          </view>
        </view>
      </view>


      <!--推荐观看的文章-->
      <view v-if="analysisData.recommendArticles.id">

        <view class="suggestion-card">
          <view class="card-header">
            <TnIcon name="lightbulb" size="40rpx" color="#5677fc"></TnIcon>
            <text class="card-title">推荐文章阅读：</text>
          </view>
          <view class="suggestion-item">
            <TnIcon name="book" size="40rpx" color="#19be6b"></TnIcon>
            <text class="suggestion-text tn-text-bold">{{ analysisData.recommendArticles.title }}</text>
          </view>
          <view class="suggestion-item">
            <text class="suggestion-text">{{ analysisData.recommendArticles.descriptions }}</text>
          </view>
        </view>
        <!--这里就是点击观看的跳转页面-->
        <view class="action-section">
          <TnButton
              @tap="toWiki(analysisData.recommendArticles.id)"
              bg-color="#5677fc"
              width="40%"
              height="50rpx"
          >
            <text class="tn-white_text">跳转到文章页面</text>
          </TnButton>
        </view>
      </view>
      <!-- 重新分析按钮 -->
      <view class="action-section">
        <TnButton
            @tap="getAnalysisData"
            bg-color="#5677fc"
            width="90%"
            height="90rpx"
            :loading="isAnalyzing"
        >
          <text class="tn-white_text">重新分析</text>
        </TnButton>
      </view>
    </view>

    <!-- 未分析状态 -->
    <view class="start-section" v-else>
      <view class="start-image">
        <image src="/static/images/analyse.png" mode="aspectFit"></image>
      </view>
      <text class="start-title">心理状态分析</text>
      <text class="start-desc">基于您的使用数据，我们将分析您的心理状态，并提供个性化的改善建议。</text>
      <TnButton
          @tap="getAnalysisData"
          bg-color="#5677fc"
          width="80%"
          height="90rpx"
          margin="60rpx 0 0 0"
          :loading="isAnalyzing"
      >
        <text class="tn-white_text">开始分析</text>
      </TnButton>
    </view>
  </view>
</template>

<style scoped lang="scss">
.analyse-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 100rpx;
}

// 加载中状态
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 30rpx;

  .loading-text {
    font-size: 32rpx;
    color: #333333;
    margin-top: 40rpx;
    font-weight: bold;
  }

  .loading-desc {
    font-size: 28rpx;
    color: #888888;
    margin-top: 20rpx;
  }
}

// 错误状态
.error-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 30rpx;

  .error-text {
    font-size: 32rpx;
    color: #ff4d4f;
    margin-top: 40rpx;
  }
}

// 开始分析状态
.start-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 30rpx;

  .start-image {
    width: 300rpx;
    height: 300rpx;
    margin-bottom: 40rpx;

    image {
      width: 100%;
      height: 100%;
    }
  }

  .start-title {
    font-size: 36rpx;
    color: #333333;
    font-weight: bold;
    margin-bottom: 30rpx;
  }

  .start-desc {
    font-size: 28rpx;
    color: #666666;
    text-align: center;
    line-height: 1.6;
    padding: 0 40rpx;
  }
}

// 分析结果
.result-section {
  padding: 30rpx;
}

// 卡片通用样式
.summary-card,
.keywords-card,
.metrics-card,
.chart-card,
.suggestion-card {
  background-color: #ffffff;
  border-radius: 12rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  overflow: hidden;

  .card-header {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;

    .card-title {
      font-size: 30rpx;
      color: #333333;
      font-weight: bold;
      margin-left: 16rpx;
    }
  }

  .card-content {
    padding: 30rpx;
  }
}

// 摘要卡片
.summary-card {
  .summary-text {
    font-size: 28rpx;
    color: #666666;
    line-height: 1.8;
    text-align: justify;
  }
}

// 关键词卡片
.keywords-card {
  .keywords-list {
    display: flex;
    flex-wrap: wrap;
  }
}

// 评分指标卡片
.metrics-card {
  .metric-item {
    margin-bottom: 40rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .metric-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16rpx;

      .metric-name {
        font-size: 28rpx;
        color: #333333;
        font-weight: bold;
      }

      .metric-score {
        font-size: 28rpx;
        color: #5677fc;
        font-weight: bold;
      }
    }

    .metric-progress {
      height: 16rpx;
      background-color: #f5f5f5;
      border-radius: 8rpx;
      overflow: hidden;
      margin-bottom: 16rpx;

      .progress-bar {
        height: 100%;
        border-radius: 8rpx;
        transition: width 0.5s;
      }
    }

    .metric-desc {
      font-size: 26rpx;
      color: #888888;
      line-height: 1.6;
    }
  }
}

// 图表卡片
.chart-card {
  .chart-container {
    width: 100%;
    height: 500rpx;
  }
}

// 建议卡片
.suggestion-card {
  .suggestion-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 30rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .suggestion-text {
      font-size: 28rpx;
      color: #666666;
      line-height: 1.6;
      margin-left: 20rpx;
      flex: 1;
    }
  }
}

// 操作区域
.action-section {
  display: flex;
  justify-content: center;
  margin-top: 60rpx;
}
</style>