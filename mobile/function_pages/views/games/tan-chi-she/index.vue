<script setup>
import {ref, reactive, computed, onMounted, onBeforeUnmount} from 'vue';
import navbar from "@/components/navbar.vue";
import {ossGames} from "@/utils/ossUrl";

// 修改图片资源引用方式
const bgImg = ossGames.bg1;
const foodImg = "/static/games/tan-chi-she/food.png";
const headImg = "/static/games/tan-chi-she/head.png";
const bodyImg = "/static/games/tan-chi-she/body.png";
const tail1Img = "/static/games/tan-chi-she/5-1.png";
const tail2Img = "/static/games/tan-chi-she/5-2.png";
const tail3Img = "/static/games/tan-chi-she/5-3.png";
const tail4Img = "/static/games/tan-chi-she/5-4.png";
const corner1Img = "/static/games/tan-chi-she/6-1.png";
const corner2Img = "/static/games/tan-chi-she/6-2.png";
const corner3Img = "/static/games/tan-chi-she/6-3.png";
const corner4Img = "/static/games/tan-chi-she/6-4.png";

// 地图
const map = reactive([]);
const row = 18; // 行
const col = 14; // 列

// 游戏状态
const score = ref(0); // 分数
const snake = reactive([]); // 蛇
const dir = ref('right'); // 移动方向 默认向右
const speed = ref(150); // 速度
const isStart = ref(false); // 是否开始
const isStop = ref(true); // 是否暂停
const isOver = ref(false); // 判断游戏是否结束
let movetimer = null; // 移动定时器

// 游戏统计
const gameStats = reactive({
  playCount: 0,
  totalPlayTime: 0
});

// 开始时间
let gameStartTime = 0;

// 蛇头旋转度数
const headRotate = computed(() => {
  let rotate = 0;
  if (dir.value === 'right') rotate = 0;
  if (dir.value === 'down') rotate = 90;
  if (dir.value === 'left') rotate = 180;
  if (dir.value === 'up') rotate = 270;
  return rotate;
});

// 初始化
const createInit = () => {
  // 地图初始化
  map.length = 0; // 清空地图
  for (let i = 0; i < row; i++) {
    map[i] = [];
    for (let j = 0; j < col; j++) {
      map[i][j] = 0;
    }
  }

  // 蛇初始化
  snake.length = 0; // 清空蛇
  for (let i = 4; i > 0; i--) {
    snake.push([1, i]);
  }

  // 墙体 (可以自定义墙体位置)
  let wall = [
    // [9, 0], [9, 1], [9, 2], [9, 3], [9, 4], [9, 5],
    // [9, 8], [9, 9], [9, 10], [9, 11],[9, 12], [9, 13]
  ];

  if (wall.length) {
    for (let i = 0; i < wall.length; i++) {
      let [x, y] = wall[i];
      map[x][y] = 1;
    }
  }

  // 渲染蛇和食物
  snakeStyle();
  foodStyle();

  // 重置游戏状态
  score.value = 0;
  dir.value = 'right';
  isStart.value = false;
  isStop.value = true;
  isOver.value = false;

  // 记录游戏开始时间
  gameStartTime = Date.now();
};

// 画蛇
// 蛇头(3)  蛇身(4)  蛇尾(5-1 5-2 5-3 5-4)  蛇拐弯处(6-1 6-2 6-3 6-4)
const snakeStyle = () => {
  // 蛇头
  map[snake[0][0]][snake[0][1]] = 3;

  // 尾巴
  // 我们需要比较最后两个数据，来断定尾巴的朝向
  let [x1, y1] = snake[snake.length - 1];
  let [x2, y2] = snake[snake.length - 2];

  if (x1 === x2 && y1 < y2) map[x1][y1] = '5-1';
  if (x1 === x2 && y1 > y2) map[x1][y1] = '5-2';
  if (x1 > x2 && y1 === y2) map[x1][y1] = '5-3';
  if (x1 < x2 && y1 === y2) map[x1][y1] = '5-4';

  // 身体 
  // 先把身体都用普通身体代替
  for (let i = 1; i < snake.length - 1; i++) {
    let [x, y] = snake[i];
    map[x][y] = 4;
  }

  // 再去判断拐弯处
  // 我们只要判断某一段身体的前一个和后一个就行了
  for (let i = 1; i < snake.length - 1; i++) {
    let [a, b] = snake[i];
    let [a1, b1] = snake[i - 1];
    let [a2, b2] = snake[i + 1];

    if (((a1 < a2 && b1 > b2) || (a1 > a2 && b1 < b2)) && ((a < a1 && b === b1) || (a === a1 && b < b1))) {
      map[a][b] = '6-1';
    }
    if (((a1 > a2 && b1 > b2) || (a1 < a2 && b1 < b2)) && ((a > a1 && b === b1) || (a === a1 && b < b1))) {
      map[a][b] = '6-2';
    }
    if (((a1 > a2 && b1 > b2) || (a1 < a2 && b1 < b2)) && ((a < a1 && b === b1) || (a === a1 && b > b1))) {
      map[a][b] = '6-3';
    }
    if (((a1 < a2 && b1 > b2) || (a1 > a2 && b1 < b2)) && ((a > a1 && b === b1) || (a === a1 && b > b1))) {
      map[a][b] = '6-4';
    }
  }
};

// 生成食物
const foodStyle = () => {
  let arr = [];
  
  // 确保食物只在地图范围内生成，不包括边界
  for (let i = 1; i < row - 1; i++) {
    for (let j = 1; j < col - 1; j++) {
      if (map[i][j] === 0) {
        arr.push({x: i, y: j});
      }
    }
  }
  
  // 如果没有可用空间，则不生成食物
  if (arr.length === 0) return;
  
  let randomNum = Math.floor(Math.random() * arr.length);
  let {x, y} = arr[randomNum];

  // 给食物添加样式
  map[x][y] = 2;
};

// 游戏开始/暂停
const playStart = () => {
  if (isOver.value) {
    isStart.value = false;
    uni.showModal({
      title: '游戏结束',
      content: '得分：' + score.value,
      showCancel: false
    });
    return;
  }

  if (!isStart.value) {
    isStart.value = true;
    isStop.value = false;
    snakeMove();
    return;
  }

  if (isStop.value) {
    isStop.value = false;
    snakeMove();
  } else {
    isStop.value = true;
    clearInterval(movetimer);
  }
};

// 蛇移动
const snakeMove = () => {
  if (isOver.value) return;

  movetimer = setInterval(() => {
    // 首先定义一个行和列
    let x = null;
    let y = null;

    // 判断蛇的方向
    switch (dir.value) {
      case 'right':
        x = snake[0][0]; // 如果往右移动，则行不变，列++
        y = snake[0][1] + 1;
        break;
      case 'down':
        x = snake[0][0] + 1;
        y = snake[0][1];
        break;
      case 'left':
        x = snake[0][0];
        y = snake[0][1] - 1;
        break;
      case 'up':
        x = snake[0][0] - 1;
        y = snake[0][1];
        break;
    }

    // 撞到墙
    // 判断是否超过row、col临界值
    if (x < 0 || x > row - 1 || y < 0 || y > col - 1 || map[x][y] === 1) {
      handleGameOver();
      return;
    }

    // 撞到自己了
    // 判断头部是否与身体其他部位重合
    let arrX = snake.map(item => item[0]);
    let arrY = snake.map(item => item[1]);
    let bodys = [4, '5-1', '5-2', '5-3', '5-4', '6-1', '6-2', '6-3', '6-4'];

    if (arrX.includes(x) && arrY.includes(y) && bodys.includes(map[x][y])) {
      handleGameOver();
      return;
    }

    // 如果什么都没撞到，则需要判断是否吃到苹果了
    // 首先无论有没有吃到苹果，都在蛇数组开头加一节
    snake.unshift([x, y]);

    // 如果没吃到，则就会把最后一个尾巴清掉
    if (map[x][y] !== 2) {
      const [a, b] = snake.pop();
      map[a][b] = 0;
    } else {
      // 如果吃到了，那就不用减去最后一个，相当于直接把苹果变成了蛇头
      score.value += 1;
      foodStyle();
    }

    snakeStyle();
  }, speed.value);
};

// 修改游戏结束处理函数
const handleGameOver = () => {
  isOver.value = true;
  clearInterval(movetimer);
  
  // 更新游戏统计
  updateGameStats();
  
  // 保存最高分
  const highScore = uni.getStorageSync('snakeHighScore') || 0;
  if (score.value > highScore) {
    uni.setStorageSync('snakeHighScore', score.value);
  }
};

// 修改重新开始游戏函数
const restartGame = () => {
  if (movetimer) {
    clearInterval(movetimer);
    movetimer = null;
  }
  createInit();
  isOver.value = false;
};

// 修改更新游戏统计函数
const updateGameStats = () => {
  const playTime = Math.floor((Date.now() - gameStartTime) / 1000);
  
  // 获取已有记录
  let records = uni.getStorageSync('snakeGameRecords') || [];
  
  // 添加新记录
  const newRecord = {
    date: new Date().toISOString(),
    score: score.value,
    duration: formatTime(playTime),
    playTimeSeconds: playTime
  };
  
  // 将新记录添加到开头
  records.unshift(newRecord);
  
  // 只保留最近的10条记录
  if (records.length > 10) {
    records = records.slice(0, 10);
  }
  
  // 保存记录
  uni.setStorageSync('snakeGameRecords', records);
  
  // 更新统计数据
  const stats = uni.getStorageSync('gameStats') || {
    playCount: 0,
    totalPlayTime: 0,
    highScore: 0
  };
  
  stats.playCount += 1;
  stats.totalPlayTime += playTime;
  if (score.value > (stats.highScore || 0)) {
    stats.highScore = score.value;
  }
  
  uni.setStorageSync('gameStats', stats);
};

// 格式化时间
const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}分${remainingSeconds}秒`;
};

// 处理按键事件的节流函数
const throttle = (fn, delay = 100) => {
  let lastTime = 0;
  return function () {
    const now = Date.now();
    if (now - lastTime >= delay) {
      fn.apply(this, arguments);
      lastTime = now;
    }
  };
};

// 处理方向键控制
const handleDirection = (direction) => {
  if (isOver.value || isStop.value) return;

  // 当蛇向右移动的时候，不能按左，向上的时候，不能按下，其他同理
  if (direction === 'left' && dir.value !== 'right') {
    dir.value = 'left';
  } else if (direction === 'up' && dir.value !== 'down') {
    dir.value = 'up';
  } else if (direction === 'right' && dir.value !== 'left') {
    dir.value = 'right';
  } else if (direction === 'down' && dir.value !== 'up') {
    dir.value = 'down';
  }
};

// 页面加载时初始化
onMounted(() => {
  createInit();

  // 加载游戏统计
  const stats = uni.getStorageSync('gameStats');
  if (stats) {
    gameStats.playCount = stats.playCount;
    gameStats.totalPlayTime = stats.totalPlayTime;
  }
});

// 移除 document 相关的代码，小程序中不存在 document 对象
onBeforeUnmount(() => {
  if (movetimer) {
    clearInterval(movetimer);
    movetimer = null;
  }
});
</script>

<template>
  <view class="container">
    <navbar title="贪吃蛇"></navbar>
    <view class="home">
      <image :src="bgImg" alt="" class="bg"/>
      <!-- 游戏区域容器 -->
      <view class="game-container">
        <!-- 分数显示 -->
        <view class="score" v-if="score >= 0">得分：{{ score }}</view>
        <!-- 地图 -->
        <view class="map">
          <view v-for="(item1, index1) in map" :key="index1" class="mapRow">
            <view v-for="(item2, index2) in item1" :key="index2" ref="zml" class="mapCol">
              <!-- 砖块 -->
              <view class="brick" v-if="item2 == 1"></view>
              <!-- 苹果 -->
              <image class="food" :src="foodImg" alt="" v-show="item2 == 2"/>
              <!-- 蛇头 -->
              <image class="food" :src="headImg" alt="" v-show="item2 == 3"
                     :style="{'transform': 'rotate('+ headRotate +'deg)'}"/>
              <!-- 蛇身 -->
              <image class="food" :src="bodyImg" alt="" v-show="item2 == 4"
                     :style="{'transform': 'rotate('+ headRotate +'deg)'}"/>
              <!-- 尾巴 -->
              <image class="food" :src="tail1Img" alt="" v-show="item2 == '5-1'"/>
              <image class="food" :src="tail2Img" alt="" v-show="item2 == '5-2'"/>
              <image class="food" :src="tail3Img" alt="" v-show="item2 == '5-3'"/>
              <image class="food" :src="tail4Img" alt="" v-show="item2 == '5-4'"/>
              <!-- 拐弯处 -->
              <image class="food" :src="corner1Img" alt="" v-show="item2 == '6-1'"/>
              <image class="food" :src="corner2Img" alt="" v-show="item2 == '6-2'"/>
              <image class="food" :src="corner3Img" alt="" v-show="item2 == '6-3'"/>
              <image class="food" :src="corner4Img" alt="" v-show="item2 == '6-4'"/>
            </view>
          </view>
        </view>
        
        <!-- 游戏遮罩层 -->
        <view class="gameMask" v-if="isStart && isStop">
          <view class="mask-content">
            <text>游戏暂停</text>
            <view class="btn" @tap="playStart">继续游戏</view>
          </view>
        </view>
        <view class="gameMask" v-if="isOver">
          <view class="mask-content">
            <text>游戏结束！</text>
            <text>最终得分：{{ score }}</text>
            <view class="btn" @tap="restartGame">重新开始</view>
          </view>
        </view>
      </view>

      <!-- 控制区域 -->
      <view class="control-area">
        <!-- 方向控制 -->
        <view class="direction-controls">
          <view class="up" @tap="handleDirection('up')">↑</view>
          <view class="middle">
            <view class="left" @tap="handleDirection('left')">←</view>
            <view class="right" @tap="handleDirection('right')">→</view>
          </view>
          <view class="down" @tap="handleDirection('down')">↓</view>
        </view>

        <!-- 开始/暂停按钮 -->
        <view class="action-btn" @tap="playStart">
          {{ isStart && !isStop ? '暂停' : '开始' }}
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
view, text, button {
  box-sizing: border-box;
}

.home {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.bg {
  width: 120%;
  height: 120%;
  object-fit: cover;
  position: absolute;
  top: -10%;
  left: -10%;
  z-index: 0;
}

.game-container {
  position: relative;
  z-index: 1;
  width: 90%;
  max-width: 650rpx;
  margin: 0 auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx;
}

.map {
  width: 100%;
  height: 80vh;
  max-height: 960rpx;
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  border-radius: 40px;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(5px);
  box-shadow: 0 0 20rpx rgba(0, 0, 0, 0.2);
}

.map .mapRow {
  flex: 1;
  display: flex;
}

.map .mapCol {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

img.food {
  width: 100%;
  height: 100%;
}


/* 墙壁 */
.brick {
  width: 100%;
  height: 100%;
  background: rgb(90, 150, 0);
  border: 8px solid;
  border-top-color: rgb(152, 253, 0);
  border-right-color: rgb(59, 99, 0);
  border-bottom-color: rgb(40, 68, 0);
  border-left-color: rgb(121, 202, 0);
}

/* 蛇头 */
.snake-head {
  width: 100%;
  height: 100%;
  background: rgb(255, 87, 87);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2px;
}

.snake-head::before {
  content: none !important;
}

.snake-head span {
  display: block;
  width: 12px;
  height: 12px;
  background-color: #fff;
  border-radius: 50%;
  position: relative;
}

.snake-head span::before {
  content: "";
  position: absolute;
  width: 50%;
  height: 50%;
  background-color: #2C3E50;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* 蛇身 */
.snake-body {
  width: 100%;
  height: 100%;
  background: rgb(255, 87, 87);
  overflow: hidden;
}

/* 立体蛇 */
.threeD {
  background: rgb(255, 87, 87);
  box-shadow: 0px 0px 30px rgb(255, 87, 87);
  overflow: hidden;
  position: relative;
}

.threeD::before {
  content: "";
  background: #fff;
  border-radius: 100%;
  position: absolute;
  border: 0.3em solid rgb(255, 87, 87);
  filter: blur(5px);
  width: 50%;
  height: 50%;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.control-area {
  z-index: 999;
  position: fixed;
  bottom: 40rpx;
  left: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40rpx;
}

.direction-controls {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;

  .up, .down, .left, .right {
    width: 100rpx;
    height: 100rpx;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40rpx;
    color: #333;
    box-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.2);

    &:active {
      transform: scale(0.95);
    }
  }

  .middle {
    display: flex;
    gap: 40rpx;
  }
}

.action-btn {
  width: 200rpx;
  height: 80rpx;
  background: #19be6b;
  color: #fff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  box-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.2);

  &:active {
    transform: scale(0.95);
  }
}

// 修改游戏遮罩样式
.gameMask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 40px;
  
  .mask-content {
    background: rgba(255, 255, 255, 0.85);
    padding: 40rpx;
    border-radius: 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 30rpx;
    
    text {
      font-size: 36rpx;
      color: #333;
      font-weight: bold;
    }
    
    .btn {
      background: #19be6b;
      color: #fff;
      padding: 20rpx 40rpx;
      border-radius: 100rpx;
      font-size: 32rpx;
      
      &:active {
        transform: scale(0.95);
      }
    }
  }
}

// 调整分数显示
.score {
  font-size: 60rpx;
  color: #ff7900;
  text-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.3);
  margin-bottom: 30rpx;
  font-weight: bold;
}
</style> 