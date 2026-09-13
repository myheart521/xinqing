<script setup>
import {ref, onMounted, reactive, watch} from 'vue';
import navbar from "@/components/navbar.vue";
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import wuzi from "@/static/games/wu-zi-qi/wuziicon.png"
import tanchishe from "@/static/games/tan-chi-she/tanchisheicon.png"
import birdicon from "@/static/games/bird/birdicon.png"
import beasticon from "@/static/games/beast/beasticon.png"
import {onShow} from "@dcloudio/uni-app"

// 游戏列表
const gameList = reactive([
  {
    id: 1,
    name: '贪吃蛇',
    icon: tanchishe,
    color: '#5677fc',
    description: '经典的贪吃蛇游戏，通过控制蛇头方向吃掉食物，让蛇变得更长。',
    path: '/function_pages/views/games/tan-chi-she/index',
    storageKey: 'snakeGameRecords'
  },
  {
    id: 2,
    name: '五子棋',
    icon: wuzi,
    color: '#ff7900',
    description: '经典的五子棋游戏，先连成一条线赢得比赛。',
    path: '/function_pages/views/games/wu-zi-qi/index',
    storageKey: 'wuziqi_records'
  },
  {
    id: 3,
    name: '管道小鸟',
    icon: birdicon,
    color: '#19be6b',
    description: '考验你的反应能力。',
    path: '/function_pages/views/games/bird/index',
    storageKey: 'birdGameRecords'
  },
  {
    id: 4,
    name: '斗兽棋',
    icon: beasticon,
    color: '#e03997',
    description: '益智棋类小游戏',
    path: '/function_pages/views/games/beast/index',
    storageKey: 'beastGameRecords'
  }
]);

// 我的游戏统计
const myGameStats = reactive({
  playCount: 0,
  totalScore: 0,
  bestScore: 0,
  totalTime: '0分钟'
});

// 最近游戏记录
const recentRecords = ref([]);

// 显示游戏弹窗
const showGameModal = ref(false);
const currentGame = ref(null);

// 打开游戏
const openGame = (game) => {
  if (game.path) {
    uni.navigateTo({
      url: game.path
    });
  } else {
    uni.showToast({
      title: '游戏开发中，敬请期待！',
      icon: 'none'
    });
  }
};

// 加载所有游戏统计和记录
const loadAllGameStats = () => {
  // 加载综合统计数据
  const gameStats = uni.getStorageSync('gameStats') || {
    playCount: 0,
    totalPlayTime: 0,
    highScore: 0
  };

  // 更新总游戏次数
  myGameStats.playCount = gameStats.playCount || 0;

  // 计算总时长
  const totalSeconds = gameStats.totalPlayTime || 0;
  myGameStats.totalTime = formatTime(totalSeconds);

  // 获取各游戏最高分
  const snakeHighScore = uni.getStorageSync('snakeHighScore') || 0;
  myGameStats.bestScore = snakeHighScore;

  // 加载最近游戏记录
  let allRecords = [];

  // 遍历游戏列表获取记录
  gameList.forEach(game => {
    try {
      // 获取存储的记录并确保是数组
      let records = uni.getStorageSync(game.storageKey);

      // 判断记录是否存在
      if (!records) {
        return; // 如果没有记录，跳过这个游戏
      }

      // 如果记录是字符串，尝试解析为JSON
      if (typeof records === 'string') {
        try {
          records = JSON.parse(records);
        } catch (e) {
          console.error(`解析${game.name}记录失败:`, e);
          return; // 解析失败则跳过
        }
      }

      // 确保records是数组
      if (!Array.isArray(records)) {
        console.warn(`${game.name}的记录不是数组:`, records);
        return; // 如果不是数组，跳过
      }

      // 为每条记录添加游戏名称
      const gameRecords = records.map(record => ({
        ...record,
        gameName: game.name,
        gameColor: game.color
      }));

      allRecords = [...allRecords, ...gameRecords];
    } catch (e) {
      console.error(`处理${game.name}记录时出错:`, e);
    }
  });

  // 按日期排序（最新的在前）
  allRecords.sort((a, b) => {
    try {
      const dateA = a.date ? new Date(a.date) : new Date(0);
      const dateB = b.date ? new Date(b.date) : new Date(0);
      return dateB - dateA;
    } catch (e) {
      return 0; // 排序出错则保持原顺序
    }
  });

  // 只保留最近5条记录
  recentRecords.value = allRecords.slice(0, 5);

  // 计算总分数
  myGameStats.totalScore = allRecords.reduce((total, record) => {
    return total + (Number(record.score) || 0);
  }, 0);
};

// 格式化时间
const formatTime = (seconds) => {
  if (!seconds) return '0分钟';
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);

  if (hours > 0) {
    return `${hours}小时${minutes % 60}分钟`;
  } else {
    return `${minutes}分钟`;
  }
};

// 页面加载时加载数据
onMounted(() => {
  loadAllGameStats();
});

// 处理页面显示时更新数据，确保返回游戏后能看到最新记录
onShow(()=>{
  loadAllGameStats();

})

</script>

<template>
  <view class="container">
    <!-- 导航栏 -->
    <navbar title="休闲游戏"></navbar>

    <!-- 顶部用户信息 -->
    <view class="user-stats ">
      <view>
        <text class="stats-title">我的游戏统计</text>
        <view></view>
        <view class="tn-flex tn-flex-center-between">
          <view>
            <TnIcon name="clock" size="60rpx" color="#5677fc"></TnIcon>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ myGameStats.playCount }}</text>
            <text class="stat-label">游戏次数</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ myGameStats.totalScore }}</text>
            <text class="stat-label">总分数</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{ myGameStats.bestScore }}</text>
            <text class="stat-label">最高分</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 游戏列表 -->
    <view class="section-title tn-flex tn-flex-center-between">
      <text class="title-text">经典游戏</text>
      <text class="subtitle-text">放松心情 提升专注力</text>
    </view>

    <view class="game-list">
      <view
          class="game-card"
          v-for="game in gameList"
          :key="game.id"
          @tap="openGame(game)"
      >
        <view class="game-icon" :style="{ backgroundColor: game.color }">
          <tn-lazy-load :src="game.icon" :index="game.id" :height="60" :width="60"></tn-lazy-load>
        </view>
        <view class="game-info">
          <text class="game-name">{{ game.name }}</text>
          <view></view>
          <text class="game-description">{{ game.description }}</text>
        </view>
        <view class="game-action">
          <TnIcon name="right" size="40rpx" color="#aaaaaa"></TnIcon>
        </view>
      </view>
    </view>

    <!-- 最近记录 -->
    <view class="section-title tn-flex tn-flex-center-between">
      <text class="title-text">最近记录</text>
      <text class="subtitle-text">游戏历史</text>
    </view>

    <view class="records-list">
      <view class="empty-tip" v-if="recentRecords.length === 0">
        <text>暂无游戏记录，快去玩几局吧！</text>
      </view>
      <view
          class="record-card"
          v-for="record in recentRecords"
          :key="record.id"
      >
        <view class="record-info">
          <text class="record-game-name" :style="{color: record.gameColor}">{{ record.gameName }}</text>
          <text class="record-date">{{ record.date }}</text>
        </view>
        <view class="record-stats">
          <view class="record-score" v-if="record.score !== undefined">
            <text class="score-label">分数</text>
            <text class="score-value">{{ record.score }}</text>
          </view>
          <view class="record-result" v-if="record.result">
            <text class="result-label">结果</text>
            <text class="result-value">{{ record.result }}</text>
          </view>
          <view class="record-duration" v-if="record.duration">
            <text class="duration-label">时长</text>
            <text class="duration-value">{{ record.duration }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f8f9fc;
  padding-bottom: 100rpx;
}

.user-stats {
  margin: 30rpx;
  padding: 40rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .stats-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
  }

  .stat-item {
    margin-right: 40rpx;
    display: flex;
    flex-direction: column;

    .stat-value {
      font-size: 36rpx;
      font-weight: bold;
      color: #5677fc;
    }

    .stat-label {
      font-size: 24rpx;
      color: #999999;
      margin-top: 6rpx;
    }
  }
}

.section-title {
  margin: 30rpx 30rpx 20rpx 30rpx;

  .title-text {
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
  }

  .subtitle-text {
    font-size: 24rpx;
    color: #999999;
  }
}

.game-list {
  margin: 0 30rpx;

  .game-card {
    display: flex;
    align-items: center;
    background-color: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

    .game-icon {
      width: 100rpx;
      height: 100rpx;
      border-radius: 20rpx;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .game-info {
      flex: 1;
      margin-left: 20rpx;

      .game-name {
        font-size: 28rpx;
        font-weight: bold;
        color: #333333;
        margin-bottom: 10rpx;
      }

      .game-description {
        font-size: 24rpx;
        color: #999999;
        line-height: 1.5;
      }
    }

    .game-action {
      margin-left: 20rpx;
    }
  }
}

.records-list {
  margin: 0 30rpx;

  .empty-tip {
    padding: 40rpx 0;
    text-align: center;
    color: #999;
    background-color: #ffffff;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  }

  .record-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

    .record-info {
      .record-game-name {
        font-size: 28rpx;
        font-weight: bold;
        color: #333333;
      }

      .record-date {
        font-size: 24rpx;
        color: #999999;
        margin-top: 10rpx;
      }
    }

    .record-stats {
      display: flex;

      .record-score, .record-duration, .record-result {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-left: 30rpx;

        .score-label, .duration-label, .result-label {
          font-size: 24rpx;
          color: #999999;
        }

        .score-value, .duration-value, .result-value {
          font-size: 28rpx;
          font-weight: bold;
          color: #5677fc;
          margin-top: 6rpx;
        }
      }
    }
  }
}
</style>