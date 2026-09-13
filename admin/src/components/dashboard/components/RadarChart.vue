<template>
  <el-card shadow="hover" class="radar-chart">
    <div ref="chartRef" class="chart-box" />
  </el-card>
</template>

<script setup lang="ts">
/** 雷达图组件 */
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

interface Props {
  title: string
  indicators: { name: string; max: number }[]
  data: number[]
}

const props = defineProps<Props>()
const chartRef = ref<HTMLDivElement>()

onMounted(() => {
  if (!chartRef.value) return
  const chart = echarts.init(chartRef.value)
  const option: echarts.EChartsOption = {
    title: { text: props.title, left: 'center', textStyle: { color: '#a6d4ff', fontSize: 14 } },
    tooltip: {},
    radar: {
      indicator: props.indicators,
      radius: '65%',
      axisName: { color: '#a6d4ff' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
      splitArea: { areaStyle: { color: ['rgba(0,0,0,0)', 'rgba(0,0,0,0)'] } },
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: props.data,
            areaStyle: { color: 'rgba(0,212,255,0.2)' },
            lineStyle: { color: '#00d4ff' },
            itemStyle: { color: '#00d4ff' },
          },
        ],
      },
    ],
  }
  chart.setOption(option)
})
</script>

<style scoped>
.radar-chart {
  background-color: #24304b;

  width: 100%;
  height: 100%;
}
.chart-box {
  width: 100%;
  height: 300px;
}
</style>
