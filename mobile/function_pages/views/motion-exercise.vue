<template>
  <view class="course-page">
    <navbar title="运动一下吧"></navbar>

    <!-- 搜索区域 -->
    <view class="search-section tn-m-xs">
      <!-- 搜索框 -->
      <view class="search-bar">
        <tn-search-box
            v-model="searchQuery"
            @search="searchCourses"
            placeholder="搜索运动的标题吧"
            :round="true"
            background-color="#f5f5f5"
        >
        </tn-search-box>
      </view>

      <!-- 高级筛选 -->
      <view class="filter-section tn-mt-sm">
        <view class="filter-row tn-flex tn-flex-center-between tn-flex-wrap">
          <!-- 难度等级筛选 -->
          <view class="filter-item">
            <view class="filter-label">
              <tn-icon name="flag" size="28rpx" color="#5677fc"></tn-icon>
              <text class="tn-ml-xs">难度等级</text>
            </view>
            <picker @change="onLeagueChange" :value="leagueIndex" :range="leagueOptions" range-key="text">
              <view class="uni-picker">
                <text>{{ leagueOptions[leagueIndex].text }}</text>
                <tn-icon name="down" size="24rpx" color="#666"></tn-icon>
              </view>
            </picker>
          </view>

          <!-- 时长筛选 -->
          <view class="filter-item">
            <view class="filter-label">
              <tn-icon name="time" size="28rpx" color="#5677fc"></tn-icon>
              <text class="tn-ml-xs">运动时长</text>
            </view>
            <picker @change="onTimeChange" :value="timeIndex" :range="timeOptions" range-key="text">
              <view class="uni-picker">
                <text>{{ timeOptions[timeIndex].text }}</text>
                <tn-icon name="down" size="24rpx" color="#666"></tn-icon>
              </view>
            </picker>
          </view>

          <!-- 重置和筛选按钮 -->
          <view class="filter-buttons tn-flex tn-flex-row tn-flex-center-center">
            <view class="custom-button reset-button" @tap="resetFilters">
              <tn-icon name="refresh" size="28rpx" color="#666"></tn-icon>
              <text class="tn-ml-xs">重置</text>
            </view>
            <view class="custom-button search-button" @tap="searchCourses">
              <tn-icon name="search" size="28rpx" color="#fff"></tn-icon>
              <text class="tn-ml-xs">筛选</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="tn-flex tn-flex-center-center tn-py-xl">
      <tn-loading></tn-loading>
    </view>

    <!-- 无数据提示 -->
    <view v-else-if="courses.length === 0" class="tn-flex tn-flex-center-center tn-py-xl">
      <view class="tn-flex tn-flex-direction-column tn-flex-center-center">
        <tn-icon name="fitness" size="120rpx" color="#cccccc"></tn-icon>
        <text class="tn-gray_text tn-mt-sm">暂无运动课程</text>
      </view>
    </view>

    <!-- 课程列表 -->
    <view v-else class="course-list">
      <view
          v-for="item in courses"
          :key="item.id"
          class="course-item tn-shadow-sm"
          @click="goToDetail(item.id)"
          hover-class="course-item-hover"
      >
        <!--图片-->
        <view class="course-image tn-radius">
          <tn-lazy-load width="200rpx" height="200rpx" :src="item.image" mode="aspectFill" radius="12rpx"/>
        </view>
        <!--右侧-->
        <view class="course-details tn-ml tn-flex tn-flex-column tn-flex-start-center">
          <view class="tn-mt-sm" style="width: 100%">
            <view class="tn-mb-xs">
              <text class="course-title tn-text-ellipsis-1">{{ item.title }}</text>
            </view>
            <view class="tn-flex tn-flex-row tn-flex-wrap">
              <view class="level-badge">
                <text>{{ item.league }}</text>
              </view>
              <text class="course-description tn-ml-xs">{{ item.leagueDescribe }}</text>
              <view class="tn-flex tn-flex-row tn-flex-center-center tn-ml-sm">
                <tn-icon name="time" size="30rpx" color="#666"></tn-icon>
                <text class="course-description tn-ml-xs">{{ item.time }}分钟</text>
              </view>
            </view>
          </view>
          <view class="item__data tn-mt-sm">
            <view class="item__tags-container">
              <view
                  v-for="(tagItem, tagIndex) in item.motionFeatures"
                  :key="tagIndex"
                  class="item__tag"
              >
                {{ tagItem }}
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 回到顶部按钮 -->
    <view class="back-to-top" v-if="showBackToTop" @tap="scrollToTop">
      <tn-icon name="up" size="40rpx" color="#fff"></tn-icon>
    </view>
  </view>
</template>


<script setup>
import navbar from "@/components/navbar.vue";
import {ref, computed, onMounted} from 'vue';
import {select} from '@/service/api/sportController';
import {onPageScroll} from "@dcloudio/uni-app"

// 搜索和筛选参数
const searchQuery = ref(''); // 搜索关键字
const loading = ref(true);
const courses = ref([]);
const showBackToTop = ref(false);

// 难度等级选项
const leagueOptions = [
  {text: '全部', value: ''},
  {text: 'K1 零基础', value: 'K1'},
  {text: 'K2 初学', value: 'K2'},
  {text: 'K3 进阶', value: 'K3'}
];
const leagueIndex = ref(0);
const filterLeague = ref('');

// 时长选项
const timeOptions = [
  {text: '全部', value: ''},
  {text: '5分钟以内', value: '0-5'},
  {text: '5-10分钟', value: '5-10'},
  {text: '10分钟以上', value: '10-999'}
];
const timeIndex = ref(0);
const filterTime = ref('');

// 处理难度等级选择变化
const onLeagueChange = (e) => {
  leagueIndex.value = e.detail.value;
  filterLeague.value = leagueOptions[leagueIndex.value].value;
};

// 处理时长选择变化
const onTimeChange = (e) => {
  timeIndex.value = e.detail.value;
  filterTime.value = timeOptions[timeIndex.value].value;
};

// 获取课程列表
const searchCourses = async () => {
  loading.value = true;
  try {
    // 构建查询参数
    const params = {
      title: searchQuery.value || undefined,
      league: filterLeague.value || undefined
    };

    // 处理时间范围
    if (filterTime.value) {
      const [start, end] = filterTime.value.split('-').map(Number);
      params.startTime = start;
      params.endTime = end;
    }

    const res = await select(params);
    if (res.code === 1 && res.data) {
      courses.value = res.data;
    } else {
      uni.showToast({
        title: res.msg || '获取运动列表失败',
        icon: 'none'
      });
      courses.value = [];
    }
  } catch (error) {
    console.error('获取运动列表失败:', error);
    uni.showToast({
      title: '获取运动列表失败',
      icon: 'none'
    });
    courses.value = [];
  } finally {
    loading.value = false;
  }
};

// 重置筛选条件
const resetFilters = () => {
  searchQuery.value = '';
  leagueIndex.value = 0;
  timeIndex.value = 0;
  filterLeague.value = '';
  filterTime.value = '';
};

// 跳转到详情页
const goToDetail = (id) => {
  uni.navigateTo({
    url: `/function_pages/views/motion-exercise-video?id=${id}`,
  });
};

// 监听页面滚动
onPageScroll(({scrollTop}) => {
  showBackToTop.value = scrollTop > 200;
});

// 回到顶部
const scrollToTop = () => {
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 300
  });
};

// 页面加载时获取数据
onMounted(() => {
  searchCourses();
});
</script>

<style lang="scss" scoped>
.course-page {
  padding: 20rpx;
  background-color: #f9f9f9;
  min-height: 100vh;
}

.search-section {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: #f9f9f9;
  padding: 10rpx 0;
}

.search-bar {
  width: 100%;
}

.filter-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.filter-row {
  width: 100%;
}

.filter-item {
  margin-bottom: 16rpx;
  width: 45%;
}

.filter-label {
  font-size: 26rpx;
  color: #333;
  margin-bottom: 12rpx;
  display: flex;
  align-items: center;
}

.uni-picker {
  height: 76rpx;
  border: 1px solid #e0e0e0;
  border-radius: 12rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 28rpx;
  background-color: #f8f8f8;
  transition: all 0.3s;
  
  &:active {
    background-color: #f0f0f0;
  }
}

.filter-buttons {
  margin-top: 16rpx;
  width: 100%;
  gap: 20rpx;
}

.custom-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 76rpx;
  border-radius: 12rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
  transition: all 0.3s;
  flex: 1;
}

.reset-button {
  background-color: #f0f0f0;
  color: #666;
  border: 1px solid #e0e0e0;
  
  &:active {
    background-color: #e5e5e5;
  }
}

.search-button {
  background-color: #5677fc;
  color: #fff;
  
  &:active {
    background-color: #4a67d6;
  }
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  margin-top: 24rpx;
  padding-bottom: 30rpx;
}

.course-item {
  display: flex;
  background-color: #fff;
  padding: 24rpx;
  border-radius: 16rpx;
  transition: all 0.3s;
  border-left: 6rpx solid #5677fc;
}

.course-item-hover {
  transform: scale(0.98);
  box-shadow: 0 6rpx 20rpx rgba(86, 119, 252, 0.1);
}

.course-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.course-details {
  flex: 1;
  padding: 0 16rpx;
}

.course-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
}

.level-badge {
  background-color: rgba(86, 119, 252, 0.1);
  color: #5677fc;
  font-size: 24rpx;
  font-weight: bold;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  display: inline-block;
}

.course-description {
  color: #666;
  font-size: 26rpx;
}

.item {
  &__data {
    width: 100%;
  }

  &__tags-container {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    justify-content: flex-start;
  }

  &__tag {
    margin: 6rpx;
    color: #FF7043;
    border: 2rpx solid #FF7043;
    padding: 4rpx 16rpx;
    border-radius: 30rpx;
    font-size: 22rpx;
    background-color: rgba(255, 112, 67, 0.05);

    &:first-child {
      margin-left: 0rpx !important;
    }
  }
}

.back-to-top {
  position: fixed;
  right: 30rpx;
  bottom: 100rpx;
  width: 90rpx;
  height: 90rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #5677fc, #4a67d6);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 20rpx rgba(86, 119, 252, 0.3);
  z-index: 99;
  transition: all 0.3s;
  
  &:active {
    transform: scale(0.9);
  }
}
</style>


