<script setup>
import {onMounted, ref} from 'vue';
// 头部导航栏
import mineNavVue from './views/mine/mine-nav.vue';
// 滑稽表情（感谢内容）
import navThanksVue from './views/mine/navThanks.vue';
// 头像昵称
import avatarNameVue from './views/mine/avatar-name.vue';
// 测试记录和老师消息
import testRecordVue from './views/mine/test-record.vue';
// 工具
import toolsVue from './views/mine/tools.vue';
// 关于心晴
import aboutVue from './views/mine/about.vue';
import {getUserCount} from "@/service/api/userController";
import {useUserStore} from "@/stores/user";

const userStore = useUserStore();
const userCount = ref({
  userCount: 0,
  dynamicCount: 0,
  evaluateCount: 0
})

onMounted(async () => {
  if (!userStore.checkLogin()) {
    return
  }
  //获取用户参与活动等信息
  const res = await getUserCount()
  if (res.code === 1) {
    userCount.value = res.data
  }
})
</script>

<template>
  <view class="content">
    <!-- 顶部导航栏 -->
    <mineNavVue></mineNavVue>

    <!-- 顶部背景图片 -->
    <view class="top-backgroup">
      <image src='https://assets.example.invalid/placeholder.png' mode='widthFix' class='backgroud-image'>
      </image>
      <view class="gradient-overlay"></view>
    </view>

    <!-- 滑稽的感谢名单 -->
    <view class="thanks-container">
      <nav-thanks-vue></nav-thanks-vue>
    </view>

    <view class="profile-container">
      <!-- 用户的头像和昵称等 -->
      <avatarNameVue></avatarNameVue>

      <!-- 心情打卡与成长数据 -->
      <view class="mood-tracking-card">
        <view class="card-title">
          <tn-icon name="chart" color="#8364e8" size="40rpx"></tn-icon>
          <text>心理健康追踪</text>
        </view>
        <view class="mood-stats">
          <view class="stat-item">
            <text class="stat-number">{{ userCount.userCount }}</text>
            <text class="stat-label">参与活动</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-number">{{ userCount.dynamicCount }}</text>
            <text class="stat-label">发布动态</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-number">{{ userCount.evaluateCount }}</text>
            <text class="stat-label">完成测评</text>
          </view>
        </view>
      </view>

      <!-- 测评记录和老师消息 -->
      <testRecordVue></testRecordVue>
      <!-- 工具 -->
      <tools-vue></tools-vue>
      <!-- 使用协议开源地址等 -->
      <about-vue></about-vue>
    </view>
    <view class='tn-tabbar-height'></view>
  </view>
</template>

<style lang="scss" scoped>
.tn-tabbar-height {
  min-height: 120rpx;
  height: calc(130rpx + env(safe-area-inset-bottom) / 2);
}

.content {
  background-color: #f8f9fd;
  min-height: 100vh;
  position: relative;

  /* 顶部背景图 */
  .top-backgroup {
    height: 450rpx;
    position: relative;
    overflow: hidden;

    .backgroud-image {
      width: 100%;
      height: 450rpx;
      object-fit: cover;
    }

    .gradient-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(to bottom, rgba(123, 104, 238, 0.4), rgba(86, 119, 252, 0.8));
      z-index: 1;
    }
  }

  .thanks-container {
    position: relative;
    z-index: 999;
    margin-top: -80rpx;
  }

  .profile-container {
    position: relative;
    z-index: 2;
    margin: 0 30rpx;
    margin-top: -180rpx;
    padding-bottom: 30rpx;
  }

  .mood-tracking-card {
    margin-top: 30rpx;
    background: #ffffff;
    border-radius: 24rpx;
    padding: 30rpx;
    box-shadow: 0 10rpx 20rpx rgba(131, 100, 232, 0.08);

    .card-title {
      display: flex;
      align-items: center;
      margin-bottom: 30rpx;

      text {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
        margin-left: 16rpx;
      }
    }

    .mood-stats {
      display: flex;
      justify-content: space-around;
      align-items: center;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .stat-number {
          font-size: 48rpx;
          font-weight: bold;
          color: #8364e8;
          margin-bottom: 8rpx;
        }

        .stat-label {
          font-size: 24rpx;
          color: #8D93A1;
        }
      }

      .stat-divider {
        width: 2rpx;
        height: 80rpx;
        background: linear-gradient(to bottom, rgba(131, 100, 232, 0.1), rgba(86, 119, 252, 0.3));
      }
    }
  }
}
</style>