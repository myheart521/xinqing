<script setup lang="ts">
import { RouterView } from 'vue-router'
import DigitalHumanAvatar from '@/components/DigitalHumanAvatar.vue'
import { nextTick, onMounted, ref } from 'vue'
import { filterForDigitalHuman } from './utils/textFilter.js'

const text = ref('')
const digitalRef = ref(null)
// 处理来自uniapp的消息
const handleUniappMessage = async (event) => {
  //event.data就是我发的东西
  text.value = event.data
  console.log('来自uniapp的消息：', text.value)
  if (digitalRef.value) {
    // 确保传入的是字符串类型
    const message = typeof event.data === 'string' ? event.data : JSON.stringify(event.data)
    
    // 过滤文本内容
    const filteredMessage = filterForDigitalHuman(message)
    if (filteredMessage) {
      console.log('过滤前消息:', message)
      console.log('过滤后消息:', filteredMessage)
      digitalRef.value.appCome(filteredMessage) // 发送过滤后的消息
    } else {
      console.log('消息过滤后为空，跳过发送')
    }
  }
}

onMounted(() => {
  // 监听来自uniapp的消息
  window.addEventListener('message', handleUniappMessage)
})
</script>

<template>
  <div class="app-container">
  <DigitalHumanAvatar ref="digitalRef" />
  <RouterView />
  </div>
</template>

<style scoped></style>
