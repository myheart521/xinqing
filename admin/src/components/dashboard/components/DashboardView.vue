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
        <StatsCard :title="card.title" :value="card.value" />
      </el-col>
    </el-row>

    <!-- 图表第一行 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <PieChart title="学生心理状态分布" :data="pieData" />
      </el-col>
      <el-col :xs="24" :md="12">
        <BarChart
          title="与学生沟通次数教师排行"
          :xData="teacherCommNames"
          :yData="teacherCommCounts"
        />
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
        />
      </el-col>
      <el-col :xs="24" :md="12" class="chart-row">
        <MapScatter
          title="系统推广高校分布"
          :points="collegePoints"
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
import { ref, onMounted, onUnmounted } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import * as topojson from 'topojson-client';
import chinaTopoJson from '/public/china.json';
import StatsCard from './components/StatsCard.vue';
import BarChart from './components/BarChart.vue';
import PieChart from './components/PieChart.vue';
import LineChart from './components/LineChart.vue';
import RadarChart from './components/RadarChart.vue';
import MapScatter from './components/MapScatter.vue';

// 数据模拟
const consultationCount = ref(128);
const consultationGrowth = ref(12);
const satisfactionRate = ref(92);
const satisfactionGrowth = ref(5);
const onlineCounselors = ref(45);

// 静态数据
const studentCount = 1500;
const counselorCount = 30;
const crisisStudents = 50;
const healthyStudents = studentCount - crisisStudents;

const statsCards = [
  { title: '学生人数', value: studentCount },
  { title: '心理咨询老师人数', value: counselorCount },
  { title: '心理危机学生人数', value: crisisStudents },
  { title: '心理健康学生人数', value: healthyStudents },
];

const pieData = [
  { name: '心理危机学生', value: crisisStudents },
  { name: '心理健康学生', value: healthyStudents },
];

const teacherCommData = [
  { name: '王老师', value: 120 },
  { name: '李老师', value: 110 },
  { name: '张老师', value: 95 },
  { name: '赵老师', value: 80 },
  { name: '陈老师', value: 70 },
];
const teacherCommNames = teacherCommData.map((v) => v.name);
const teacherCommCounts = teacherCommData.map((v) => v.value);

const evaluationData = [
  { name: '学生A', value: 96 },
  { name: '学生B', value: 93 },
  { name: '学生C', value: 90 },
  { name: '学生D', value: 85 },
  { name: '学生E', value: 80 },
];
const evaluationNames = evaluationData.map((v) => v.name);
const evaluationCounts = evaluationData.map((v) => v.value);

const postData = [
  { name: '学生F', value: 30 },
  { name: '学生G', value: 28 },
  { name: '学生H', value: 25 },
  { name: '学生I', value: 21 },
  { name: '学生J', value: 18 },
];
const postNames = postData.map((v) => v.name);
const postCounts = postData.map((v) => v.value);

const activityData = [
  { name: '学生K', value: 15 },
  { name: '学生L', value: 14 },
  { name: '学生M', value: 12 },
  { name: '学生N', value: 11 },
  { name: '学生O', value: 10 },
];
const activityNames = activityData.map((v) => v.name);
const activityCounts = activityData.map((v) => v.value);

// 折线图数据：示例一周咨询量
const lineX = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
const lineY = [30, 45, 50, 60, 55, 40, 35];

// 雷达图数据：示例心理维度分值（满分100）
const radarIndicators = [
  { name: '压力', max: 100 },
  { name: '焦虑', max: 100 },
  { name: '抑郁', max: 100 },
  { name: '自尊', max: 100 },
  { name: '人际关系', max: 100 },
];
const radarData = [60, 50, 40, 70, 65];

// 地理散点：当前推广高校
const collegePoints = [
  { name: '河南理工大学', value: [113.175, 34.73, 1] }, // 经度,纬度,自定义值
];

// Three.js 场景相关（主背景）
const canvasRef = ref(null);
let scene, camera, renderer, controls;

// Three.js 场景相关（地图区域）
const mapCanvasRef = ref(null);
let mapScene, mapCamera, mapRenderer, mapControls;
const earthRadius = 5;

// 模拟地形高度（根据经纬度生成高度）
const getTerrainHeight = (lng, lat) => {
  const height = Math.sin((lng - 105) * 0.1) * Math.cos((lat - 35) * 0.1) * 0.2;
  return Math.max(0, height);
};

// 经纬度转球面坐标
const latLngToSphere = (lng, lat, radius = earthRadius, heightOffset = 0) => {
  const phi = (90 - lat) * (Math.PI / 180);
  const theta = (lng + 180) * (Math.PI / 180);
  const adjustedRadius = radius + heightOffset;
  const x = -adjustedRadius * Math.sin(phi) * Math.cos(theta);
  const z = adjustedRadius * Math.sin(phi) * Math.sin(theta);
  const y = adjustedRadius * Math.cos(phi);
  return new THREE.Vector3(x, y, z);
};

// 初始化主背景 Three.js 场景
const initThreeJS = () => {
  const width = window.innerWidth;
  const height = window.innerHeight;

  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x0a0a1a);

  camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
  camera.position.set(0, 0, 50);

  renderer = new THREE.WebGLRenderer({antialias: true, alpha: true});
  renderer.setSize(width, height);
  canvasRef.value.appendChild(renderer.domElement);

  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;

  const animate = () => {
    requestAnimationFrame(animate);
    controls.update();
    renderer.render(scene, camera);
  };
  animate();

  window.addEventListener('resize', () => {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
  });
};

// 初始化地图区域的 Three.js 场景
const initMapThreeJS = () => {
  // 如果模板里不存在 mapCanvasRef 对应的元素则直接返回，避免空指针
  if (!mapCanvasRef.value) {
    console.warn('mapCanvasRef 元素不存在，跳过地图 Three.js 初始化');
    return;
  }
  const width = mapCanvasRef.value.clientWidth;
  const height = mapCanvasRef.value.clientHeight;

  // 场景
  mapScene = new THREE.Scene();
  mapScene.background = new THREE.Color(0x0a0a1a);

  // 相机
  mapCamera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
  mapCamera.position.set(0, -2, 10); // 调整相机以聚焦中国区域
  mapCamera.lookAt(0, 0, 0);

  // 渲染器
  mapRenderer = new THREE.WebGLRenderer({antialias: true, alpha: true});
  mapRenderer.setSize(width, height);
  mapRenderer.shadowMap.enabled = true;
  mapRenderer.shadowMap.type = THREE.PCFSoftShadowMap;
  mapCanvasRef.value.appendChild(mapRenderer.domElement);

  // 控制器
  mapControls = new OrbitControls(mapCamera, mapRenderer.domElement);
  mapControls.enableDamping = true;
  mapControls.dampingFactor = 0.05;
  mapControls.enableZoom = true;
  mapControls.minDistance = 5;
  mapControls.maxDistance = 15;

  // 添加光源
  const ambientLight = new THREE.AmbientLight(0x404040, 0.5);
  mapScene.add(ambientLight);

  const directionalLight = new THREE.DirectionalLight(0x00d4ff, 0.8);
  directionalLight.position.set(5, 5, 5);
  directionalLight.castShadow = true;
  directionalLight.shadow.mapSize.width = 1024;
  directionalLight.shadow.mapSize.height = 1024;
  directionalLight.shadow.camera.near = 0.5;
  directionalLight.shadow.camera.far = 50;
  mapScene.add(directionalLight);

  // 添加背景星空
  const starGeometry = new THREE.BufferGeometry();
  const starCount = 5000;
  const starPositions = new Float32Array(starCount * 3);
  for (let i = 0; i < starCount * 3; i += 3) {
    starPositions[i] = (Math.random() - 0.5) * 2000;
    starPositions[i + 1] = (Math.random() - 0.5) * 2000;
    starPositions[i + 2] = (Math.random() - 0.5) * 2000;
  }
  starGeometry.setAttribute('position', new THREE.BufferAttribute(starPositions, 3));
  const starMaterial = new THREE.PointsMaterial({
    color: 0xffffff,
    size: 1,
    transparent: true,
    opacity: 0.7,
  });
  const stars = new THREE.Points(starGeometry, starMaterial);
  mapScene.add(stars);

  // 将 TopoJSON 转换为 GeoJSON
  const chinaGeoJson = topojson.feature(chinaTopoJson, chinaTopoJson.objects.default);

  // 添加中国地图（更详细）
  const mapGroup = new THREE.Group();
  try {
    chinaGeoJson.features.forEach((feature, index) => {
      if (feature.geometry.type === 'Polygon' || feature.geometry.type === 'MultiPolygon') {
        const level = feature.properties.level || 1;
        const coordinates = feature.geometry.type === 'Polygon' ? [feature.geometry.coordinates] : feature.geometry.coordinates;

        coordinates.forEach((polygon) => {
          polygon.forEach((ring) => {
            const shape = new THREE.Shape();
            let firstPoint = null;

            ring.forEach(([lng, lat], i) => {
              const planarPoint = new THREE.Vector2(
                  (lng - 105) * 0.05,
                  (lat - 35) * 0.05
              );
              if (i === 0) {
                shape.moveTo(planarPoint.x, planarPoint.y);
                firstPoint = planarPoint;
              } else {
                shape.lineTo(planarPoint.x, planarPoint.y);
              }
            });

            if (firstPoint) {
              shape.lineTo(firstPoint.x, firstPoint.y);
            }

            const extrudeSettings = {
              depth: level === 1 ? 0.3 : 0.15,
              bevelEnabled: false,
            };
            const geometry = new THREE.ExtrudeGeometry(shape, extrudeSettings);
            const material = new THREE.MeshPhongMaterial({
              color: level === 1 ? 0x00d4ff : 0x0077b6,
              transparent: true,
              opacity: 0.7,
              shininess: 100,
            });
            const mesh = new THREE.Mesh(geometry, material);
            mesh.castShadow = true;
            mesh.receiveShadow = true;

            // 添加发光边界（更详细）
            const edges = new THREE.EdgesGeometry(geometry);
            const lineMaterial = new THREE.LineBasicMaterial({
              color: level === 1 ? 0x00ffcc : 0x00b7eb,
              linewidth: level === 1 ? 2 : 1, // 省级边界更粗
              transparent: true,
              opacity: 0.9,
            });
            const lines = new THREE.LineSegments(edges, lineMaterial);
            mesh.add(lines);

            // 投影到球面（考虑地形高度）
            const positions = geometry.attributes.position.array;
            for (let i = 0; i < positions.length; i += 3) {
              const lng = 105 + (positions[i] / 0.05);
              const lat = 35 + (positions[i + 1] / 0.05);
              const terrainHeight = getTerrainHeight(lng, lat);
              const spherePoint = latLngToSphere(lng, lat, earthRadius + 0.1, terrainHeight);
              positions[i] = spherePoint.x;
              positions[i + 1] = spherePoint.y;
              positions[i + 2] = spherePoint.z;
            }
            geometry.attributes.position.needsUpdate = true;

            // 添加标签（省级和重点市级）
            if (feature.properties.name) {
              const center = feature.properties.center || [lng, lat];
              const labelPos = latLngToSphere(center[0], center[1], earthRadius + 0.5);
              const sprite = createLabel(feature.properties.name, level);
              sprite.position.copy(labelPos);
              mesh.add(sprite);
            }

            mapGroup.add(mesh);
          });
        });
      }
    });

    mapScene.add(mapGroup);

    // 调整相机以聚焦中国区域
    const box = new THREE.Box3().setFromObject(mapGroup);
    const center = box.getCenter(new THREE.Vector3());
    const size = box.getSize(new THREE.Vector3());
    mapCamera.position.set(center.x, center.y - 2, size.z * 3);
    mapCamera.lookAt(center);
    mapControls.target.copy(center);
  } catch (error) {
    console.error('Error processing TopoJSON:', error);
  }

  // 动画
  const animate = () => {
    requestAnimationFrame(animate);
    mapControls.update();
    mapRenderer.render(mapScene, mapCamera);
  };
  animate();

  window.addEventListener('resize', () => {
    const newWidth = mapCanvasRef.value.clientWidth;
    const newHeight = mapCanvasRef.value.clientHeight;
    mapCamera.aspect = newWidth / newHeight;
    mapCamera.updateProjectionMatrix();
    mapRenderer.setSize(newWidth, newHeight);
  });
};

// 创建标签（根据级别调整大小）
const createLabel = (text, level) => {
  const canvas = document.createElement('canvas');
  const context = canvas.getContext('2d');
  const fontSize = level === 1 ? 20 : 14; // 省级标签更大
  context.font = `Bold ${fontSize}px Arial`;
  const textWidth = context.measureText(text).width;
  canvas.width = textWidth + 20;
  canvas.height = fontSize + 10;
  context.font = `Bold ${fontSize}px Arial`;
  context.fillStyle = level === 1 ? 'rgba(0, 212, 255, 0.8)' : 'rgba(0, 183, 235, 0.8)';
  context.fillText(text, 10, fontSize);
  const texture = new THREE.CanvasTexture(canvas);
  const spriteMaterial = new THREE.SpriteMaterial({map: texture, transparent: true});
  const sprite = new THREE.Sprite(spriteMaterial);
  sprite.scale.set(level === 1 ? 1 : 0.7, level === 1 ? 0.5 : 0.35, 1);
  return sprite;
};

// 生命周期
onMounted(() => {
  initThreeJS();
  initMapThreeJS();
});

onUnmounted(() => {
  if (renderer) renderer.dispose();
  if (mapRenderer) mapRenderer.dispose();
});

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
