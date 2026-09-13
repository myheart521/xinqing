<template>
    <div class="report-container">
      <el-card v-for="report in reports" :key="report.moduleId" class="report-card">
        <div class="report-title">{{ report.title }}</div>
        <v-chart :option="getOption(report)" autoresize style="height: 350px;" />
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import {getStudentTestReport} from '@/api/warning/warning.js';
  import { useRoute } from 'vue-router';
  import * as echarts from 'echarts';
  import { use } from "echarts/core";
  import VChart from 'vue-echarts';
  import { CanvasRenderer } from 'echarts/renderers';
  import { LineChart } from 'echarts/charts';
  import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components';
  
  use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent]);
  
  const route = useRoute();
  const studentId = route.query.studentId || route.params.studentId; // 兼容两种传参
  const reports = ref([]);
  const props = defineProps({
  studentId: {
    type: [String, Number],
    default: null
  }
});
  const fetchData = async () => {
    const res = await getStudentTestReport({studentId: props.studentId});
    // const res = await axios.get('http://localhost:8080/historyTest/admin/dataReport', {
    //   params: { studentId: props.studentId, recent: 5 }
    // });
    console.info("报表返回数据",res)
    if (res.data && res.code === 1) {
        console.info("成功进入")
      reports.value = res.data || [];
    }
  };
  
  onMounted(fetchData);
  
  // 格式化时间
  function formatArrayTime(arr) {
    if (!arr || arr.length < 3) return '';
    return arr.map(a => a.toString().padStart(2, '0')).slice(0, 3).join('-');
  }
  
  // 生成 ECharts 配置
  function getOption(report) {
    // x轴时间
    const xLabels = (report.xaxis || []).map(arr => formatArrayTime(arr));
    // y轴多条线
    const series = Object.entries(report.yaxis || {}).map(([name, data]) => ({
      name,
      type: 'line',
      data,
      smooth: true,
      symbol: 'circle'
    }));
    return {
    //   title: { text: report.title, left: 'center', textStyle: { fontSize: 18 } },
      tooltip: { trigger: 'axis' },
      legend: { data: Object.keys(report.yaxis || {}), top: 30 },
      grid: { left: 40, right: 20, bottom: 40, top: 70 },
      xAxis: { type: 'category', data: xLabels, boundaryGap: false },
      yAxis: { type: 'value', min: 0, max: 100 },
      series
    };
  }
  </script>
  
  <style scoped>
  .report-container {
    display: flex;
    flex-wrap: wrap;
    gap: 24px;
    justify-content: center;
    padding: 24px;
  }
  .report-card {
    width: 600px;
    min-width: 350px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  }
  .report-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 12px;
    text-align: center;
  }
  </style>