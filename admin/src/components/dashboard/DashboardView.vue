<template>
  <div class="mental-health-container">
    <!-- 统计数据卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col
          v-for="card in statsCards"
          :key="card.title"
          :xs="12"
          :sm="12"
          :md="6"
          :lg="6"
      >
        <StatsCard :title="card.title" :value="card.value"/>
      </el-col>
    </el-row>

    <!-- 图表第一行 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <PieChart
            title="学生心理状态分布"
            :data="pieData"
            :key="pieChartKey"
        />
      </el-col>
      <el-col :xs="24" :md="12" class="chart-row">
        <el-card shadow="hover" class="map-card">
          <div class="map-title">系统推广高校分布</div>
          <map-scatter :collegePoints="collegePoints"/>
        </el-card>
      </el-col>

    </el-row>

    <!-- 图表第二行 -->
    <el-row :gutter="20" class="chart-row" style="margin-top: 20px">
      <el-col :xs="24" :md="8">
        <BarChart
            title="测评完成学生人数排序"
            :xData="evaluationNames"
            :yData="evaluationCounts"
        />
      </el-col>
      <el-col :xs="24" :md="8">
        <BarChart
            title="学生发布动态数量排序"
            :xData="postNames"
            :yData="postCounts"
        />
      </el-col>
      <el-col :xs="24" :md="8">
        <BarChart
            title="学生参与活动数量排序"
            :xData="activityNames"
            :yData="activityCounts"
        />
      </el-col>
    </el-row>

    <!-- 图表第三行 -->
    <el-row :gutter="20" class="chart-row" style="margin-top: 20px">
      <el-col :xs="24">
        <LineChart
            title="本周咨询量趋势"
            :xData="lineX"
            :yData="lineY"
        />
      </el-col>
    </el-row>

    <!-- 图表第四行 -->
    <el-row :gutter="20" class="chart-row" style="margin-top: 20px">
      <el-col :xs="24" :md="12" class="chart-row">
        <RadarChart
            title="学生心理维度对比"
            :indicators="radarIndicators"
            :data="radarData"
            :key="radarChartKey"
        />
      </el-col>
      <el-col :xs="24" :md="12">

        <BarChart
            title="与学生沟通次数教师排行"
            :xData="teacherCommNames"
            :yData="teacherCommCounts"
        />
      </el-col>

    </el-row>
    <!-- 右上角后台按钮 -->
    <el-button
        class="admin-button"
        type="primary"
        @click="goToAdmin"
    >
      进入后台
    </el-button>
  </div>
</template>
<script setup>
import {ref, onMounted, computed, watch} from 'vue';
import StatsCard from './components/StatsCard.vue';
import BarChart from './components/BarChart.vue';
import PieChart from './components/PieChart.vue';
import LineChart from './components/LineChart.vue';
import RadarChart from './components/RadarChart.vue';
import {
  getActivityData, getCollegePoints,
  getEvaluationData, getLine,
  getPieData,
  getPostData, getRadar,
  getStatsCards,
  getTeacherCommData
} from "@/api/dataReport/dataReport.js";
import MapScatter from "@/components/dashboard/components/MapScatter.vue";

// 静态数据
const studentCount = 1500;
const crisisStudents = 50;

const statsCards = ref([]);

const pieData = ref([]);

// 添加一个用于强制重新渲染的key
const pieChartKey = ref(0);

// 教师沟通数据
const teacherCommData = ref([]);
const teacherCommNames = computed(() => teacherCommData.value.map(v => v.name));
const teacherCommCounts = computed(() => teacherCommData.value.map(v => v.value));

// 测评数据
const evaluationData = ref([]);
const evaluationNames = computed(() => evaluationData.value.map(v => v.name));
const evaluationCounts = computed(() => evaluationData.value.map(v => v.value));

// 动态发布数据
const postData = ref([]);
const postNames = computed(() => postData.value.map(v => v.name));
const postCounts = computed(() => postData.value.map(v => v.value));

// 活动数据
const activityData = ref([]);
const activityNames = computed(() => activityData.value.map(v => v.name));
const activityCounts = computed(() => activityData.value.map(v => v.value));

// 折线图数据
const lineData = ref({xData: [], yData: []});
const lineX = computed(() => lineData.value.xData || []);
const lineY = computed(() => lineData.value.yData || []);

// 雷达图数据：示例心理维度分值（满分100）
const radarIndicators = ref([
  {name: '压力', max: 100},
  {name: '焦虑', max: 100},
  {name: '抑郁', max: 100},
  {name: '自尊', max: 100},
  {name: '人际关系', max: 100},
]);
const radarData = ref([60, 50, 40, 70, 65]);

// 添加雷达图重新渲染的key
const radarChartKey = ref(0);

// 修改雷达图数据处理函数
const processRadarData = (data) => {
  if (!data || !Array.isArray(data)) return null;

  // 直接返回符合 ECharts 雷达图要求的数据格式
  let result = [];
  data.forEach(item => {
    result.push(item)
  })
  return result;
};

// 地理散点：当前推广高校
const collegePoints = ref([
  {name: '河南理工大学', value: [113.175, 34.73, 1]}, // 经度,纬度,自定义值
]);

// 修改数据处理逻辑
const processPieData = (data) => {
  // 计算总数
  const total = data.reduce((sum, item) => sum + item.value, 0);

  // 转换为百分比
  return data.map(item => ({
    name: item.name,
    value: item.value,
    percentage: ((item.value / total) * 100).toFixed(1) // 添加百分比
  }));
};

// 生命周期
onMounted(async () => {
  try {
    const resStats = await getStatsCards();
    statsCards.value = resStats.data;

    const resPieData = await getPieData();
    if (resPieData && resPieData.data) {
      // 处理饼图数据
      pieData.value = processPieData(resPieData.data);
      // 强制重新渲染
      pieChartKey.value += 1;
    }

    const resTeacherCommData = await getTeacherCommData();
    teacherCommData.value = resTeacherCommData.data;

    const resEvaluationData = await getEvaluationData();
    evaluationData.value = resEvaluationData.data;

    const resPostData = await getPostData();
    postData.value = resPostData.data;

    const resActivityData = await getActivityData();
    activityData.value = resActivityData.data;

    const resLineData = await getLine();
    lineData.value.xData = resLineData.data.lineX;
    lineData.value.yData = resLineData.data.lineY;

    const resRadarIndicators = await getRadar();

    // 修改数据获取和处理逻辑
    if (resRadarIndicators && resRadarIndicators.data) {
      // 更新指标数据
      radarIndicators.value = resRadarIndicators.data.radarIndicators || [];

      // 直接设置处理后的数据，不需要再次包装成数组
      radarData.value = processRadarData(resRadarIndicators.data.radarData);

      // 强制重新渲染
      radarChartKey.value += 1;
    }

    const resCollegePoints = await getCollegePoints();
    console.log("test-resCollegePoints", resCollegePoints.data)

    if (resCollegePoints && resCollegePoints.data) {
      resCollegePoints.data.forEach(item => {
        collegePoints.value.push(item)
      })
    }
    console.log('test-5555', collegePoints.value)
  } catch (error) {
    console.error('数据获取失败:', error);
  }
});

// 添加监听器确保数据变化时图表更新
watch(pieData, (newData) => {
  console.log('饼图数据更新:', newData);
  // 强制重新渲染
  pieChartKey.value += 1;
}, {deep: true});

// 使用 watch 监听数据变化
watch(teacherCommData, (newData) => {
  console.log('教师沟通数据发生变化:', newData);
  // 可以在这里执行其他逻辑
}, {deep: true});

// 或者监听多个数据源
watch([teacherCommData, evaluationData, postData], ([teachers, evaluations, posts]) => {
  console.log('多个数据源发生变化');
  // 处理数据变化后的逻辑
});

// 添加雷达图数据监听
watch([radarIndicators, radarData], ([newIndicators, newData]) => {
  console.log('雷达图数据更新:', {indicators: newIndicators, data: newData});
  // 强制重新渲染
  radarChartKey.value += 1;
}, {deep: true});

// 后台按钮点击事件
const goToAdmin = () => {
  console.log('进入后台');
  // 这里可以添加跳转到后台页面的逻辑，例如：
  window.location.href = '/admin';
};
</script>

<style scoped>
/* 确保图表与卡片位于 Three.js 画布之上 */
.chart-row,
.stats-card,
.pie-chart,
.bar-chart {
  position: relative;
  z-index: 3;
  background-color: #24304b;
}

.mental-health-container {
  position: relative;
  overflow: hidden;
}


.chart-row {
  margin-top: 20px;
}

.map-card {
  height: 100%;
  background-color: #24304b;
}

.map-title {
  font-size: 14px;
  color: #c0d2f5;
  text-align: center;
  margin-bottom: 12px;
  font-weight: 500;
}


.chart-middle canvas {
  width: 100% !important;
  height: 100% !important;
}

/* 后台按钮样式 */
.admin-button {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}
</style>
