import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { tryOnUnmounted, useDebounceFn } from '@vueuse/core'
import { computed, unref, ref, nextTick } from 'vue'
import type { Ref } from 'vue'

export function useEcharts(elRef: Ref<HTMLDivElement>) {
  const chartInstance = ref<echarts.ECharts | null>(null)

  const resizeFn = useDebounceFn(() => {
    resize()
  }, 200)

  const getOptions = computed(() => {
    return {
      backgroundColor: 'transparent'
    } as EChartsOption
  })

  function initCharts(t = elRef.value) {
    if (!t) return

    chartInstance.value = echarts.init(t)
    window.addEventListener('resize', resizeFn)
  }

  function setOptions(options: EChartsOption, clear = true) {
    if (!chartInstance.value) {
      initCharts()
    }

    if (!chartInstance.value) return

    clear && chartInstance.value.clear()

    chartInstance.value.setOption({
      ...getOptions.value,
      ...options
    })
  }

  function resize() {
    chartInstance.value?.resize()
  }

  tryOnUnmounted(() => {
    if (!chartInstance.value) return
    window.removeEventListener('resize', resizeFn)
    chartInstance.value.dispose()
    chartInstance.value = null
  })

  function getInstance(): echarts.ECharts | null {
    if (!chartInstance.value) {
      initCharts()
    }
    return chartInstance.value
  }

  return {
    setOptions,
    resize,
    echarts,
    getInstance
  }
}