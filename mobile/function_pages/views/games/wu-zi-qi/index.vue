<script setup>
import { ref, reactive, onMounted } from 'vue';
import navbar from "@/components/navbar.vue";
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue';

// 正确导入图片路径
// 注意：在Vue的template中使用静态导入的图片时，需要确保路径正确指向图片资源
import blackPath from '/static/games/wu-zi-qi/black.png';
import whitePath from '/static/games/wu-zi-qi/white.png';
import {ossGames} from "@/utils/ossUrl";

const bgPath = ossGames.bg
// 更新视图的触发器
const updateView = ref(true);

// 玩家渲染
const player = {
  0: null,
  1: blackPath,
  2: whitePath,
};

// 棋盘数组
const chessBoard = reactive([]);

// 初始化棋盘
const initBoard = () => {
  // 确保棋盘数组初始化为15x15
  for (let i = 0; i < 15; i++) {
    chessBoard[i] = [];
    for (let j = 0; j < 15; j++) {
      chessBoard[i][j] = 0;
    }
  }
};

// 游戏状态
const isMe = ref(true); // 下一个该谁下
const over = ref(false); // 是否结束
const wins = []; // 赢法数组
let count = 0; // 赢法总数
const mywins = []; // 玩家赢法统计数组
const computerwins = []; // 电脑赢法统计数组

// 游戏统计
const gameStats = reactive({
  playCount: 0,
  totalPlayTime: 0
});

// 开始时间
let gameStartTime = 0;

// 初始化游戏
const init = () => {
  // 填充棋盘数组
  initBoard();

  // 赢法数组初始化
  for (let i = 0; i < 15; i++) {
    wins[i] = [];
    for (let j = 0; j < 15; j++) {
      wins[i][j] = [];
    }
  }

  count = 0;

  // 横向赢法
  for (let i = 0; i < 15; i++) {
    for (let j = 0; j < 11; j++) {
      for (let k = 0; k < 5; k++) {
        if (!wins[i][j + k]) wins[i][j + k] = [];
        wins[i][j + k][count] = true;
      }
      count++;
    }
  }

  // 竖向赢法
  for (let i = 0; i < 15; i++) {
    for (let j = 0; j < 11; j++) {
      for (let k = 0; k < 5; k++) {
        if (!wins[j + k][i]) wins[j + k][i] = [];
        wins[j + k][i][count] = true;
      }
      count++;
    }
  }

  // 正斜（左上-右下）
  for (let i = 0; i < 11; i++) {
    for (let j = 0; j < 11; j++) {
      for (let k = 0; k < 5; k++) {
        if (!wins[i + k][j + k]) wins[i + k][j + k] = [];
        wins[i + k][j + k][count] = true;
      }
      count++;
    }
  }

  // 反斜（右上-左下）
  for (let i = 0; i < 11; i++) {
    for (let j = 14; j > 3; j--) {
      for (let k = 0; k < 5; k++) {
        if (!wins[i + k][j - k]) wins[i + k][j - k] = [];
        wins[i + k][j - k][count] = true;
      }
      count++;
    }
  }

  // 赢法统计数组
  for (let i = 0; i < count; i++) {
    mywins[i] = 0;
    computerwins[i] = 0;
  }

  // 重置游戏状态
  isMe.value = true;
  over.value = false;
  
  // 记录游戏开始时间
  gameStartTime = Date.now();
};

// 玩家落子
const handleClick = (x, y) => {
  // 如果当前位置有子 或者 有获胜者，则不可再点击
  if (chessBoard[x][y] || over.value) {
    return;
  }

  // 如果该点没有子，可以落子
  if (chessBoard[x][y] === 0) {
    updateView.value = !updateView.value; // 触发视图更新
    chessBoard[x][y] = 1;

    // 判断输赢
    setTimeout(() => {
      for (let k = 0; k < count; k++) { // 遍历所有赢法
        if (wins[x][y][k] === true) { // 判断当前赢法中有子时
          mywins[k]++;
          computerwins[k] = 6; // 电脑在该赢法中已无法获胜
          if (mywins[k] === 5) {
            uni.showModal({
              title: '游戏结束',
              content: '恭喜你赢了！',
              showCancel: false
            });
            over.value = true;
            isMe.value = false; // 设置为玩家胜利
            // 更新游戏统计
            updateGameStats();
          }
        }
      }
    }, 50);

    // 如果玩家没赢，则游戏继续，电脑落子
    setTimeout(() => {
      if (!over.value) {
        isMe.value = !isMe.value;
        computerAi();
      }
    }, 100);
  }
};

// 电脑落子
const computerAi = () => {
  const myscore = []; // 对于玩家的每一空点的分值数组
  const computerscore = []; // 对于电脑的每一个空点的分值数组
  let max = 0; // 最大分值
  let x = 0, y = 0; // 决定最后电脑所落子的点

  // 初始化分值数组
  for (let i = 0; i < 15; i++) {
    myscore[i] = [];
    computerscore[i] = [];
    for (let j = 0; j < 15; j++) {
      myscore[i][j] = 0;
      computerscore[i][j] = 0;
    }
  }

  // 遍历棋盘
  for (let i = 0; i < 15; i++) {
    for (let j = 0; j < 15; j++) {
      // 是否可落子
      if (chessBoard[i][j] === 0) {
        for (let k = 0; k < count; k++) {
          // 判断当前赢法中是否含有双方棋子
          if (wins[i][j][k] === true) {
            // 玩家分值
            if (mywins[k] === 1) {
              myscore[i][j] += 100;
            } else if (mywins[k] === 2) {
              myscore[i][j] += 400;
            } else if (mywins[k] === 3) {
              myscore[i][j] += 800;
            } else if (mywins[k] === 4) {
              myscore[i][j] += 2000;
            }

            // 电脑分值
            if (computerwins[k] === 1) {
              computerscore[i][j] += 150;
            } else if (computerwins[k] === 2) {
              computerscore[i][j] += 450;
            } else if (computerwins[k] === 3) {
              computerscore[i][j] += 950;
            } else if (computerwins[k] === 4) {
              computerscore[i][j] += 10000;
            }
          }
        }

        // 比较分值，找出最佳落子点
        if (myscore[i][j] > max) {
          max = myscore[i][j];
          x = i;
          y = j;
        } else if (myscore[i][j] === max) {
          if (computerscore[i][j] > computerscore[x][y]) {
            x = i;
            y = j;
          }
        }

        if (computerscore[i][j] > max) {
          max = computerscore[i][j];
          x = i;
          y = j;
        } else if (computerscore[i][j] === max) {
          if (myscore[i][j] > myscore[x][y]) {
            x = i;
            y = j;
          }
        }
      }
    }
  }

  // 电脑落子
  updateView.value = !updateView.value; // 触发视图更新
  chessBoard[x][y] = 2;

  // 判断电脑是否获胜
  setTimeout(() => {
    for (let k = 0; k < count; k++) {
      if (wins[x][y][k] === true) {
        computerwins[k]++;
        mywins[k] = 6; // 玩家在该赢法中已无法获胜
        if (computerwins[k] === 5) {
          uni.showModal({
            title: '游戏结束',
            content: '电脑赢了，再接再厉！',
            showCancel: false
          });
          over.value = true;
          isMe.value = true; // 保持为电脑胜利状态
          // 更新游戏统计
          updateGameStats();
        }
      }
    }

    if (!over.value) {
      isMe.value = !isMe.value;
    }
  }, 100);
};

// 更新游戏统计
const updateGameStats = () => {
  // 计算游戏时长（秒）
  const playTime = Math.floor((Date.now() - gameStartTime) / 1000);
  
  // 获取已有记录
  let records = uni.getStorageSync('wuziqi_records') || [];
  
  // 添加新记录
  const newRecord = {
    id: Date.now(),
    date: new Date().toISOString().split('T')[0],
    result: over.value ? (isMe.value ? "电脑胜利" : "玩家胜利") : "平局",
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
  uni.setStorageSync('wuziqi_records', records);
  
  // 更新统计数据
  const stats = uni.getStorageSync('gameStats') || {
    playCount: 0,
    totalPlayTime: 0,
    wuziqiWins: 0,
    wuziqiLosses: 0
  };
  
  stats.playCount += 1;
  stats.totalPlayTime += playTime;
  
  // 记录胜负情况
  if (over.value) {
    if (isMe.value) {
      // 电脑赢了
      stats.wuziqiLosses = (stats.wuziqiLosses || 0) + 1;
    } else {
      // 玩家赢了
      stats.wuziqiWins = (stats.wuziqiWins || 0) + 1;
    }
  }
  
  // 保存回本地缓存
  uni.setStorageSync('gameStats', stats);
  
  // 更新当前页面显示
  gameStats.playCount = stats.playCount;
  gameStats.totalPlayTime = stats.totalPlayTime;
};

// 格式化时间
const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}分${remainingSeconds}秒`;
};

// 重新开始
const start = () => {
  // 如果游戏已开始但未结束，记录之前的游戏
  if (!over.value && chessBoard.some(row => row.some(cell => cell !== 0))) {
    updateGameStats();
  }
  
  chessBoard.forEach(row => row.fill(0));
  isMe.value = true;
  over.value = false;
  init();
};

// 悔棋（暂不实现具体逻辑）
const regret = () => {
  uni.showToast({
    title: '悔棋功能暂未实现',
    icon: 'none'
  });
};

// 认输
const admit = () => {
  if (over.value) {
    uni.showToast({
      title: '游戏已结束',
      icon: 'none'
    });
  } else {
    over.value = true;
    isMe.value = true; // 设置为电脑胜利
    uni.showModal({
      title: '游戏结束',
      content: '你认输了，电脑赢了！',
      showCancel: false
    });
    updateGameStats();
  }
};

// 添加新方法：展示游戏记录
const showGameRecords = () => {
  const records = uni.getStorageSync('wuziqi_records') || [];
  if (records.length === 0) {
    uni.showToast({
      title: '暂无游戏记录',
      icon: 'none'
    });
    return;
  }
  
  let message = '最近游戏记录：\n';
  records.forEach((record, index) => {
    message += `${index + 1}. ${record.date} ${record.result} 用时${record.duration}\n`;
  });
  
  uni.showModal({
    title: '游戏记录',
    content: message,
    showCancel: false
  });
};

// 页面加载时初始化
onMounted(() => {
  // 先初始化棋盘，确保数组结构正确
  initBoard();
  // 然后初始化游戏逻辑
  init();
  
  // 加载游戏统计
  const stats = uni.getStorageSync('gameStats');
  if (stats) {
    gameStats.playCount = stats.playCount;
    gameStats.totalPlayTime = stats.totalPlayTime;
  }
});
</script>

<template>
  <view class="container">
    <!-- 导航栏 -->
    <navbar title="五子棋"></navbar>
    
    <view class="game-index">
      <!-- 触发视图更新的隐藏元素 -->
      <text v-show="false">{{ updateView }}</text>
      
      <view class="game-box">
        <!-- 标题栏 -->
        <view class="header">
          <view class="player">
            玩家<image :src="blackPath" mode="aspectFit"></image>
          </view>
          <text class="vs">VS</text>
          <view class="computer">
            电脑<image :src="whitePath" mode="aspectFit"></image>
          </view>
        </view>
        
        <!-- 棋盘 -->
        <view class="chessboard">
          <view class="chess-box">
            <!-- 棋盘背景和网格 -->
            <view class="chess-grid" :style="{ backgroundImage: `url(${bgPath})` }">
              <view class="grid-lines">
                <view v-for="i in 14" :key="`h-${i}`" class="h-line"></view>
                <view v-for="i in 14" :key="`v-${i}`" class="v-line"></view>
              </view>
              <view class="center-point"></view>
              <view class="point1"></view>
              <view class="point2"></view>
              <view class="point3"></view>
              <view class="point4"></view>
            </view>
            
            <!-- 落子区域 -->
            <view class="chess-pieces">
              <view 
                v-for="(row, rowIndex) in chessBoard" 
                :key="`row-${rowIndex}`" 
                class="piece-row"
              >
                <view 
                  v-for="(cell, colIndex) in row" 
                  :key="`cell-${rowIndex}-${colIndex}`" 
                  class="piece-cell"
                  @tap="handleClick(rowIndex, colIndex)"
                >
                  <image 
                    v-if="cell !== 0" 
                    :src="player[cell]" 
                    mode="aspectFit" 
                    class="piece-image"
                  ></image>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 功能按钮 -->
        <view class="function-buttons">
          <TnButton @tap="start" bg-color="#5677fc" width="30%" height="90rpx">重新开始</TnButton>
          <TnButton @tap="showGameRecords" bg-color="#3498db" width="30%" height="90rpx">查看记录</TnButton>
          <TnButton @tap="admit" bg-color="#ff7900" width="30%" height="90rpx">认输</TnButton>
        </view>
        
        <!-- 游戏提示 -->
        <view class="game-tips">
          <text class="tips-title">游戏规则:</text>
          <text class="tips-content">
            1. 黑白双方交替落子，先形成五子连线者获胜
            2. 连线可以是横向、纵向或斜向
            3. 玩家执黑先行，电脑执白后行
            4. 点击"重新开始"可随时开始新游戏
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f8f9fc;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.game-index {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx;
  box-sizing: border-box;
}

.game-box {
  width: 100%;
  background-color: #ffffff;
  border-radius: 20rpx;
  box-shadow: 0 2rpx 20rpx rgba(0, 0, 0, 0.1);
  padding: 30rpx;
  box-sizing: border-box;
}

.header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30rpx;
  
  .player, .computer {
    display: flex;
    align-items: center;
    font-size: 32rpx;
    font-weight: bold;
    
    image {
      width: 40rpx;
      height: 40rpx;
      margin-left: 10rpx;
    }
  }
  
  .vs {
    margin: 0 30rpx;
    color: #ff7900;
    font-weight: bold;
    font-size: 36rpx;
  }
}

.chessboard {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 30rpx;
  
  .chess-box {
    position: relative;
    width: 100%;
    aspect-ratio: 1;
    max-width: 600rpx;
    margin: 0 auto;
    border: 1px solid #ccc;
    border-radius: 4px;
    overflow: hidden;
    
    .chess-grid {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: #f1c27d; /* 默认背景色，如果图片加载失败 */
      background-size: cover;
      background-position: center;
      
      .grid-lines {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        
        .h-line, .v-line {
          position: absolute;
          background-color: rgba(0, 0, 0, 0.6);
        }
        
        .h-line {
          width: 100%;
          height: 1px;
        }
        
        .v-line {
          width: 1px;
          height: 100%;
        }
        
        @for $i from 1 through 14 {
          .h-line:nth-child(#{$i}) {
            top: calc(#{$i} / 15 * 100%);
          }
          
          .v-line:nth-child(#{$i + 14}) {
            left: calc(#{$i} / 15 * 100%);
          }
        }
      }
      
      .center-point, .point1, .point2, .point3, .point4 {
        position: absolute;
        width: 10rpx;
        height: 10rpx;
        border-radius: 50%;
        background-color: #333;
      }
      
      .center-point {
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
      
      .point1 {
        top: 25%;
        left: 25%;
        transform: translate(-50%, -50%);
      }
      
      .point2 {
        top: 25%;
        left: 75%;
        transform: translate(-50%, -50%);
      }
      
      .point3 {
        top: 75%;
        left: 25%;
        transform: translate(-50%, -50%);
      }
      
      .point4 {
        top: 75%;
        left: 75%;
        transform: translate(-50%, -50%);
      }
    }
    
    .chess-pieces {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      z-index: 2;
      
      .piece-row {
        flex: 1;
        display: flex;
        
        .piece-cell {
          flex: 1;
          display: flex;
          justify-content: center;
          align-items: center;
        }
      }
      
      .piece-image {
        width: 80%;
        height: 80%;
      }
    }
  }
}

.function-buttons {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.game-tips {
  width: 100%;
  padding: 20rpx;
  background-color: rgba(86, 119, 252, 0.1);
  border-radius: 10rpx;
  margin-top: 20rpx;

  .tips-title {
    font-size: 26rpx;
    font-weight: bold;
    color: #5677fc;
    margin-bottom: 10rpx;
    display: block;
  }

  .tips-content {
    font-size: 24rpx;
    color: #666666;
    line-height: 1.5;
  }
}
</style> 