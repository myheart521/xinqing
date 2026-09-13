<template>
  <el-card shadow="hover" class="pie-chart">
    <div ref="chartRef" class="chart-box" />
  </el-card>
</template>

<script setup lang="ts">
/**
 * 饼图组件，基于 echarts
 */
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

interface Props {
  title: string
  data: { name: string; value: number }[]
}

const props = defineProps<Props>()
const chartRef = ref<HTMLDivElement>()

const initChart = () => {
  if (!chartRef.value) return
  const chart = echarts.init(chartRef.value)
  const option: echarts.EChartsOption = {
    title: { text: props.title, left: 'center', textStyle: { color: '#c0d2f5', fontSize: 14 } },
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', left: 'center', textStyle: { color: '#a6d4ff' } },
    series: [
      {
        name: props.title,
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: props.data,
        itemStyle: {
          color: (params: any) => {
            const colors = ['#4d7fff', '#1d6fff', '#698cff', '#8fa3ff', '#b1bfff'];
            return colors[params.dataIndex % colors.length];
          },
        },
      },
    ],
  }
  chart.setOption(option)
}

onMounted(() => {
  initChart()
})
</script>

<style scoped>
.pie-chart {
  width: 100%;
  height: 100%;
  background-color: transparent;
}
.chart-box {
  width: 100%;
  height: 300px;
}
</style>
