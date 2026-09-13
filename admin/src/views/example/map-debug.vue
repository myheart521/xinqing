<template>
  <div class="map-debug-container">
    <h1>地图调试页面</h1>
    <div class="debug-info">
      <div>
        <button @click="loadData">加载测试数据</button>
        <button @click="logMapStatus">检查地图状态</button>
      </div>
      <div class="status-box">
        <p>地图状态：{{ mapStatus }}</p>
        <p>错误信息：{{ errorMsg }}</p>
      </div>
    </div>
    <div class="map-container">
      <MapScatter 
        title="地图测试" 
        :points="points" 
        ref="mapRef"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MapScatter from '@/components/dashboard/components/MapScatter.vue'
import axios from 'axios'

const points = ref([
  { name: '北京', value: [116.405285, 39.904989, 5] },
  { name: '上海', value: [121.472644, 31.231706, 8] },
  { name: '广州', value: [113.280637, 23.125178, 3] },
  { name: '成都', value: [104.065735, 30.659462, 4] },
  { name: '西安', value: [108.948024, 34.263161, 2] }
])

const mapRef = ref(null)
const mapStatus = ref('初始化中...')
const errorMsg = ref('')

const loadData = () => {
  points.value = [
    { name: '北京', value: [116.405285, 39.904989, 5] },
    { name: '上海', value: [121.472644, 31.231706, 8] },
    { name: '广州', value: [113.280637, 23.125178, 3] },
    { name: '成都', value: [104.065735, 30.659462, 4] },
    { name: '西安', value: [108.948024, 34.263161, 2] },
    { name: '武汉', value: [114.298572, 30.584355, 6] },
    { name: '南京', value: [118.767413, 32.041544, 3] },
    { name: '杭州', value: [120.153576, 30.287459, 7] }
  ]
  mapStatus.value = '已加载测试数据'
}

const logMapStatus = async () => {
  if (!mapRef.value) {
    mapStatus.value = '地图组件未初始化'
    return
  }
  
  try {
    // 检查地图JSON是否存在
    const response = await axios.get('/china.json')
    if (response.data) {
      mapStatus.value = '地图数据加载成功'
    } else {
      mapStatus.value = '地图数据加载失败'
      errorMsg.value = '地图数据为空'
    }
  } catch (error) {
    mapStatus.value = '地图数据加载失败'
    errorMsg.value = error.message
    console.error('地图检查错误:', error)
  }
}

onMounted(() => {
  mapStatus.value = '组件已挂载'
})
</script>

<style scoped>
.map-debug-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

.debug-info {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

button {
  background-color: #4CAF50;
  border: none;
  color: white;
  padding: 10px 15px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 14px;
  margin-right: 10px;
  cursor: pointer;
  border-radius: 4px;
}

button:hover {
  background-color: #45a049;
}

.status-box {
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 4px;
  flex: 1;
  margin-left: 20px;
}

.map-container {
  height: 600px;
  width: 100%;
  background-color: #24304b;
  border-radius: 8px;
}
</style> 