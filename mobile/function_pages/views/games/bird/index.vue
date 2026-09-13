<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import navbar from "@/components/navbar.vue";
import {ossGames} from "@/utils/ossUrl";

// 图片资源路径
const birdBgImg = ossGames.bg;
const birdImg = "/static/games/bird/bird.gif";
const pipeTImg = "/static/games/bird/pipe_t.png";
const pipeBImg = "/static/games/bird/pipe_b.png";

// 游戏状态
const isStart = ref(false);  // 游戏是否开始
const isEnd = ref(false);    // 游戏是否结束
const number = ref(0);       // 分数

// 地图
const map = reactive({
  width: 438, 
  height: 600
});

// 小鸟
const bird = reactive({
  width: 34,
  height: 24,
  left: 80,
  top: 300,
  speedStart: 1,       // 初始速度
  speedPlus: 0.03,     // 加速度
  speedMax: 4,         // 上限速度
  flyMaxHeight: 50,    // 每点一次飞行的最大高度
  flyStageHeight: 5,   // 飞行的阶梯增加高度
  flyRelaHeight: 0,    // 当前飞行的相对高度
  flyDirect: null,     // up上升，down下落
});

// 管道
const pipe = reactive({
  width: 45,
  height: 364,
});

const pipeVerticel = 100;  // 上下管道之间的垂直距离
const pipeDistance = 200;  // 左右管道之间的水平距离
const pipeList = reactive([
  { pRight: -150, pTop: -200, pBottom: -200 }
]);

// 游戏统计数据
const gameStats = reactive({
  playCount: 0,
  totalPlayTime: 0,
  highScore: 0
});

// 定时器
let flyTimer = null;
let gameStartTime = 0;

// 添加记录相关的状态
const showRecords = ref(false);
const gameRecords = ref([]);

// 游戏开始
const start = () => {
  if (isEnd.value) {
    resetGame();
    return;
  }
  
  if (flyTimer) {
    clearInterval(flyTimer);
    flyTimer = null;
  }
  
  isStart.value = true;
  bird.flyDirect = 'up';
  gameStartTime = Date.now();
  
  flyTimer = setInterval(() => {
    birdFly();
    pipeMove();
  }, 10);
};

// 小鸟飞行
const birdFly = () => {
  // 上升
  if (bird.flyDirect === 'up') {
    if (bird.flyRelaHeight <= bird.flyMaxHeight) {
      assets.example.invalid -= bird.flyStageHeight;
      bird.flyRelaHeight += bird.flyStageHeight;
    } else {
      bird.flyDirect = 'down';
      bird.flyRelaHeight = 0;
      bird.speedStart = 1;
    }
  }
  
  // 到达顶部
  if (assets.example.invalid <= 0) {
    assets.example.invalid = 0;
    die();
  }
  
  // 下降
  if (bird.flyDirect === 'down') {
    assets.example.invalid = assets.example.invalid + bird.speedStart * bird.speedStart;
    if (bird.speedStart < bird.speedMax) {
      bird.speedStart += bird.speedPlus;
    }
  }
  
  // 到达底边
  if (assets.example.invalid >= map.height - bird.height) {
    assets.example.invalid = map.height - bird.height;
    die();
  }
};

// 管道移动
const pipeMove = () => {
  // 如果数组中的最后一个到达右边的时候，增加一个
  if (pipeList[pipeList.length - 1].pRight >= 0) {
    // 随机生成管道高度
    let pipeRandom = random(50, 300);
    let obj = {
      pRight: -(pipeList[pipeList.length - 1].pRight * 1 + pipeDistance * 1),
      pTop: -pipeRandom,
      pBottom: -(map.height * 1 - pipeRandom * 1 - pipeVerticel * 1),
    };
    pipeList.push(obj);
  }
  
  // 如果第一个到达左边的时候，删除第一个
  if (pipeList[0].pRight >= map.width) {
    pipeList.shift();
  }
  
  // 遍历管道移动
  for (let i = 0; i < pipeList.length; i++) {
    let birdMouthRight = map.width - bird.width - bird.left; // 鸟嘴到右边的距离
    
    // 判断是否碰撞管道
    if (pipeList[i].pRight <= map.width - bird.left) {
      // 有没有撞到上管道
      if (
        (pipeList[i].pRight + pipe.width) >= birdMouthRight && 
        (pipe.height + pipeList[i].pTop) >= assets.example.invalid
      ) {
        die();
        return;
      }
      
      // 有没有撞到下管道
      if (
        (pipeList[i].pRight + pipe.width) >= birdMouthRight && 
        (pipe.height + pipeList[i].pBottom) >= (map.height - bird.height - assets.example.invalid)
      ) {
        die();
        return;
      }
    }
    
    // 判断分数，也就是刚好飞过某一个管道
    const pRight = Math.floor(pipeList[i].pRight);
    if (pRight === 358) {
      number.value++;
    }
    
    pipeList[i].pRight += 1.2;
  }
};

// 死亡处理
const die = () => {
  if (flyTimer) {
    clearInterval(flyTimer);
    flyTimer = null;
  }
  
  isEnd.value = true;
  
  // 保存游戏记录
  saveGameRecord();
  
  // 显示游戏结束提示
  uni.showModal({
    title: '游戏结束',
    content: `得分：${number.value}`,
    confirmText: '重新开始',
    cancelText: '返回',
    success: (res) => {
      if (res.confirm) {
        resetGame();
      }
    }
  });
};

// 保存游戏记录
const saveGameRecord = () => {
  // 计算游戏时长（秒）
  const playTime = Math.floor((Date.now() - gameStartTime) / 1000);
  
  // 获取已有记录
  let records = uni.getStorageSync('birdGameRecords') || [];
  
  // 添加新记录
  const newRecord = {
    id: Date.now(),
    date: new Date().toISOString().split('T')[0],
    score: number.value,
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
  uni.setStorageSync('birdGameRecords', records);
  
  // 更新统计数据
  const stats = uni.getStorageSync('gameStats') || {
    playCount: 0,
    totalPlayTime: 0,
    highScore: 0
  };
  
  stats.playCount += 1;
  stats.totalPlayTime += playTime;
  
  // 更新最高分
  if (number.value > stats.highScore) {
    stats.highScore = number.value;
  }
  
  // 保存回本地缓存
  uni.setStorageSync('gameStats', stats);
  
  // 更新当前页面显示
  gameStats.playCount = stats.playCount;
  gameStats.totalPlayTime = stats.totalPlayTime;
  gameStats.highScore = stats.highScore;
};

// 重置游戏
const resetGame = () => {
  number.value = 0;
  assets.example.invalid = 300;
  bird.flyDirect = null;
  bird.flyRelaHeight = 0;
  bird.speedStart = 1;
  
  pipeList.length = 0;
  pipeList.push({ pRight: -150, pTop: -200, pBottom: -200 });
  
  isStart.value = false;
  isEnd.value = false;
};

// 修改显示游戏记录函数
const showGameRecords = () => {
  const records = uni.getStorageSync('birdGameRecords') || [];
  if (records.length === 0) {
    uni.showToast({
      title: '暂无游戏记录',
      icon: 'none'
    });
    return;
  }
  
  // 更新记录并显示弹窗
  gameRecords.value = records;
  showRecords.value = true;
};

// 格式化时间
const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}分${remainingSeconds}秒`;
};

// 生成随机数
const random = (min, max) => {
  if (min >= 0 && max > 0 && max >= min) {
    let gab = max - min + 1;
    return Math.floor(Math.random() * gab + min);
  } else {
    return 0;
  }
};

// 屏幕点击处理
const screenTap = () => {
  if (!isStart.value && !isEnd.value) {
    start();
  } else if (isStart.value && !isEnd.value) {
    bird.flyDirect = 'up';
    bird.flyRelaHeight = 0;
  } else if (isEnd.value) {
    resetGame();
  }
};

// 页面加载时初始化
onMounted(() => {
  // 加载游戏统计
  const stats = uni.getStorageSync('gameStats');
  if (stats) {
    gameStats.playCount = stats.playCount || 0;
    gameStats.totalPlayTime = stats.totalPlayTime || 0;
    gameStats.highScore = stats.highScore || 0;
  }
});

// 页面卸载时清理
onBeforeUnmount(() => {
  if (flyTimer) {
    clearInterval(flyTimer);
    flyTimer = null;
  }
});
</script>

<template>
  <view class="container">
    <!-- 导航栏 -->
    <navbar title="管道小鸟"></navbar>
    
    <view class="bird-game">
      <view class="bird-box" @tap="screenTap">
        <image :src="birdBgImg" class="bg" mode="aspectFill" />
        <view class="bird-map">
          <!-- 分数 -->
          <view class="bird-number">{{ number }}</view>
          
          <!-- 开始游戏提示 -->
          <view class="bird-start" v-if="!isStart && !isEnd">开始游戏</view>
          
          <!-- 鸟 -->
          <view 
            class="bird"
            :class="{'bird-move': !isStart}" 
            :style="{'top': assets.example.invalid + 'px', 'left': bird.left + 'px'}"
          >
            <image :src="birdImg" mode="widthFix" />
          </view>
          
          <!-- 管道 -->
          <view 
            class="bird-pipe" 
            v-for="(item, index) in pipeList" 
            :key="index" 
            :style="{'right': item.pRight + 'px', 'width': pipe.width+'px'}"
          >
            <view class="bird-pipe-top" :style="{'top': item.pTop +'px'}">
              <image :src="pipeTImg" width="100%" mode="widthFix" />
            </view>
            <view class="bird-pipe-bottom" :style="{'bottom': item.pBottom + 'px'}">
              <image :src="pipeBImg" width="100%" mode="widthFix" />
            </view>
          </view>
        </view>
      </view>
      
      <!-- 地板 - 使用CSS样式代替图片 -->
      <view class="bird-land">
        <view class="land-1"></view>
        <view class="land-2"></view>
        <view class="land-box" :class="{'land-move': isStart && !isEnd}"></view>
        <view class="land-3"></view>
        <view class="land-4"></view>
      </view>
      
      <!-- 游戏统计信息 -->
      <view class="game-stats" v-if="!isStart">
        <view class="stats-item">
          <text>游戏次数:</text>
          <text>{{ gameStats.playCount }}</text>
        </view>
        <view class="stats-item">
          <text>最高分:</text>
          <text>{{ gameStats.highScore }}</text>
        </view>
        <view class="stats-item" v-if="gameStats.totalPlayTime > 0">
          <text>总游戏时长:</text>
          <text>{{ formatTime(gameStats.totalPlayTime) }}</text>
        </view>
        <view class="stats-btn" @tap="showGameRecords">查看游戏记录</view>
      </view>
    </view>
    
    <!-- 添加游戏记录弹窗 -->
    <view class="records-popup" v-if="showRecords">
      <view class="records-content">
        <view class="records-header">
          <text class="title">游戏记录</text>
          <text class="close-btn" @tap="showRecords = false">×</text>
        </view>
        
        <scroll-view scroll-y class="records-list">
          <view class="empty-tip" v-if="gameRecords.length === 0">
            <text>暂无游戏记录</text>
          </view>
          <view 
            class="record-item"
            v-for="(record, index) in gameRecords" 
            :key="record.id"
          >
            <view class="record-rank">{{ index + 1 }}</view>
            <view class="record-info">
              <view class="record-score">{{ record.score }}分</view>
              <view class="record-detail">
                <text class="record-date">{{ record.date }}</text>
                <text class="record-time">{{ record.duration }}</text>
              </view>
            </view>
            <view class="record-medal" v-if="index < 3">
              <view :class="['medal', index === 0 ? 'gold' : index === 1 ? 'silver' : 'bronze']"></view>
            </view>
          </view>
        </scroll-view>
        
        <view class="records-footer">
          <view class="confirm-btn" @tap="showRecords = false">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #DED895;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.bird-game {
  width: 438px;
  height: 100vh;
  background-color: #DED895;
  position: relative;
  display: flex;
  flex-direction: column;
}

.bird-box {
  width: 100%;
  height: 600px;
  background-color: rgb(0, 135, 147);
  position: relative;
  overflow: hidden;
  
  .bg {
    width: 438px;
    height: 600px;
    display: block;
  }
}

.bird-map {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 2;
  overflow: hidden;
}

.bird-number {
  position: absolute;
  font-size: 35px;
  color: #fff;
  font-weight: bold;
  left: 50%;
  transform: translateX(-50%);
  top: 30px;
  text-shadow: 4px 4px 2px rgba(0, 0, 0, 1);
  z-index: 999;
}

.bird-start {
  position: absolute;
  font-size: 40px;
  color: rgb(47, 227, 43);
  font-weight: bold;
  left: 50%;
  transform: translateX(-50%);
  top: 150px;
  text-shadow: 2px 2px 2px rgba(255, 255, 255, 1);
}

.bird {
  width: 34px;
  height: 24px;
  position: absolute;
  transition: transform .4s;
  
  image {
    display: block;
    width: 100%;
    height: 100%;
  }
}

.bird-move {
  animation: ani_bird .75s infinite cubic-bezier(0.215, 0.61, 0.355, 1);
}

@keyframes ani_bird {
  0% {transform: translateY(0);}
  50% {transform: translateY(-10px);}
  100% {transform: translateY(0);}
}

.bird-pipe {
  position: absolute;
  top: 0;
  bottom: 0;
}

.bird-pipe-bottom,
.bird-pipe-top {
  position: absolute;
  width: 100%;
}

/* 地面 */
.bird-land {
  flex: 1;
  
  .land-box {
    width: 100%;
    height: 20px;
    background: linear-gradient(-45deg, #9BE557 25%, #73BF2E 0%, #73BF2E 50%, #9BE557 0%, #9BE557 75%, #73BF2E 0%);
    background-size: 40px 40px;
    background-position: 250% 0;
  }
  
  .land-1 {
    width: 100%;
    height: 2px;
    background-color: #523546;
  }
  
  .land-2 {
    width: 100%;
    height: 2px;
    background-color: #DEF886;
  }
  
  .land-3 {
    width: 100%;
    height: 2px;
    background-color: #558121;
  }
  
  .land-4 {
    width: 100%;
    height: 2px;
    background-color: #E0B24E;
  }
}

.land-move {
  animation: ani_land 8.5s linear infinite;
}

@keyframes ani_land {
  0% {background-position: 250% 0;}
  100% {background-position: 0 0;}
}

.game-stats {
  margin-top: 20px;
  width: 90%;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  padding: 15px;
  align-self: center;
  
  .stats-item {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    text:first-child {
      color: #666;
    }
    
    text:last-child {
      color: #333;
      font-weight: bold;
    }
  }
  
  .stats-btn {
    margin-top: 15px;
    background-color: #73BF2E;
    color: #fff;
    text-align: center;
    padding: 8px;
    border-radius: 5px;
    font-weight: bold;
    
    &:active {
      opacity: 0.8;
    }
  }
}

.records-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.records-content {
  width: 85%;
  max-width: 600rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 0 20rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.records-header {
  padding: 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #f2f2f2;
  
  .title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
  }
  
  .close-btn {
    font-size: 48rpx;
    color: #999;
    line-height: 1;
    padding: 0 10rpx;
  }
}

.records-list {
  max-height: 700rpx;
  padding: 20rpx 30rpx;
  
  .empty-tip {
    padding: 60rpx 0;
    text-align: center;
    color: #999;
    font-size: 28rpx;
  }
}

.record-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f8f8f8;
  
  &:last-child {
    border-bottom: none;
  }
  
  .record-rank {
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 28rpx;
    color: #666;
  }
  
  .record-info {
    flex: 1;
    margin-left: 20rpx;
    
    .record-score {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .record-detail {
      display: flex;
      align-items: center;
      margin-top: 8rpx;
      font-size: 24rpx;
      color: #999;
      
      .record-date {
        margin-right: 20rpx;
      }
    }
  }
  
  .record-medal {
    .medal {
      width: 40rpx;
      height: 40rpx;
      border-radius: 50%;
    }
    
    .gold {
      background: linear-gradient(135deg, #ffd86b, #fc9601);
    }
    
    .silver {
      background: linear-gradient(135deg, #e2e2e2, #b0b0b0);
    }
    
    .bronze {
      background: linear-gradient(135deg, #dd9f6c, #905d3a);
    }
  }
}

.records-footer {
  padding: 20rpx 30rpx 40rpx;
  display: flex;
  justify-content: center;
  
  .confirm-btn {
    width: 70%;
    height: 80rpx;
    background-color: #73BF2E;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 100rpx;
    font-size: 28rpx;
    font-weight: bold;
    box-shadow: 0 8rpx 16rpx rgba(115, 191, 46, 0.3);
    
    &:active {
      transform: scale(0.98);
    }
  }
}
</style> 