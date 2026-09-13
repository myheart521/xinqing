<template>
  <el-card shadow="hover" class="line-chart">
    <div ref="chartRef" class="chart-box" />
  </el-card>
</template>

<script setup lang="ts">
/**
 * 折线图组件
 */
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'

interface Props {
  title: string
  xData: string[]
  yData: number[]
}

const props = defineProps<Props>()
const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | undefined

const initChart = () => {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  const option: echarts.EChartsOption = {
    title: { text: props.title, left: 'center', textStyle: { color: '#c0d2f5', fontSize: 14 } },
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: props.xData,
      axisLabel: { color: '#a6d4ff' },
      axisLine: { lineStyle: { color: '#4d7fff' } },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#a6d4ff' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
    },
    series: [
      {
        data: props.yData,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#4d7fff',
        },
        lineStyle: {
          width: 3,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#4d7fff' },
              { offset: 1, color: '#1d6fff' },
            ],
          },
        },
        areaStyle: {
          color: 'rgba(77,127,255,0.12)',
        },
      },
    ],
  }
  chart.setOption(option)
}

onMounted(() => initChart())

watch(() => [props.xData, props.yData], () => {
  if (chart) {
    chart.setOption({ xAxis: { data: props.xData }, series: [{ data: props.yData }] })
  }
})
</script>

<style scoped>
.line-chart {
  background-color: #24304b;
  width: 100%;
  height: 100%;
}
.chart-box {
  width: 100%;
  height: 300px;
}
</style>
