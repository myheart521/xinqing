<script setup>
import {ref, onMounted, reactive} from 'vue';
import {getKnowledgeCollectionsPages} from '@/service/api/newKnowledgeController';
import navbar from "@/components/navbar.vue";
import pulldownRefresh from '@/components/load/pulldownRefresh.vue';
import {formatDateByYear} from '@/utils/formate';
import {onShow} from '@dcloudio/uni-app';

// 默认图片路径
const defaultImage = ref('/static/images/default.jpg');

// 收藏列表数据
const collectionData = reactive({
  records: [],
  total: 0,
  current: 1,
  hasMore: true
});

// 每页显示数量
const pageSize = 10;

// 加载状态：0-加载前，1-加载中，2-没有更多了
const loadingStatus = ref(0);
const noMoreData = ref(false); // 是否没有更多数据
const refreshRef = ref(null);
const isRefreshing = ref(false);
const isFirstLoad = ref(true); // 标记是否是首次加载

// 获取收藏列表
const getCollectionList = async (isRefresh = false) => {
  if (loadingStatus.value === 1) {
    return;
  }

  loadingStatus.value = 1;

  // 如果是刷新，重置页码
  if (isRefresh) {
    collectionData.current = 1;
    noMoreData.value = false;
  }

  try {
    const res = await getKnowledgeCollectionsPages({
      pageNo: collectionData.current,
      pageSize: pageSize
    });

    console.log("收藏列表响应:", res);
    if (res.code === 1) {
      // 后端直接返回数组，不包含records字段
      const dataArray = res.data || [];

      if (isRefresh) {
        collectionData.records = dataArray;
      } else {
        collectionData.records = [...collectionData.records, ...dataArray];
      }

      // 更新总数据量，用于判断是否还有更多
      collectionData.total = collectionData.records.length;

      // 判断是否还有更多数据
      if (dataArray.length < pageSize) {
        noMoreData.value = true;
      } else {
        collectionData.current++;
        noMoreData.value = false;
      }
    } else {
      uni.showToast({
        title: res.msg || '获取数据失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error);
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    });
  } finally {
    loadingStatus.value = 0;
  }
};

// 下拉刷新
const onRefresh = async () => {
  isRefreshing.value = true;
  try {
    await getCollectionList(true);

    uni.showToast({
      title: '刷新成功',
      icon: 'success'
    });
  } catch (error) {
    console.error('刷新失败:', error);
    uni.showToast({
      title: '刷新失败',
      icon: 'error'
    });
  } finally {
    isRefreshing.value = false;
    refreshRef.value?.endPulldownRefresh();
  }
};

// 加载更多
const loadMore = () => {
  if (noMoreData.value) {
    uni.showToast({
      title: '没有更多数据了',
      icon: 'none'
    });
    return;
  }
  getCollectionList();
};

// 前往文章详情页
const goToDetail = (item) => {
  uni.navigateTo({
    url: `/function_pages/views/motion-index-detail?id=${item.id}&isLiked=${item.isLiked}&isCollection=${item.isCollection}`
  });
};

// 处理图片加载错误
const handleImgError = (e) => {
  // 当图片加载失败时，将src设置为默认图片
  e.target.src = defaultImage.value;
};

// 获取图片URL，处理内置图片名称情况
const getImageUrl = (imageName) => {
  if (!imageName) return defaultImage.value;
  
  // 处理可能的内置图片名称
  const imageMap = {
    'image1': '/static/images/default.jpg',
    'image2': '/static/images/default.jpg',
    'image3': '/static/images/default.jpg',
    'image4': '/static/images/default.jpg'
  };

  return imageMap[imageName] || imageName;
};

// 初始化
onMounted(() => {
  isFirstLoad.value = true;
  getCollectionList(true);
});

// 当页面显示时，如果不是首次加载，则重新请求数据
onShow(() => {
  if (!isFirstLoad.value) {
    console.log('页面返回，重新请求收藏数据');
    getCollectionList(true);
  }
  isFirstLoad.value = false;
});
</script>

<template>
  <view class="collection-container">
    <!-- 顶部导航栏 -->
    <navbar title="我的收藏"></navbar>
    
    <!-- 内容区域 -->
    <pulldownRefresh
        ref="refreshRef"
        :top="0"
        :threshold="80"
        @refresh="onRefresh"
    >

      <!-- 加载中状态 -->
      <view v-if="loadingStatus === 1 && collectionData.records.length === 0" class="loading-container">
        <tn-loading></tn-loading>
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 空状态 -->
      <view v-else-if="collectionData.records.length === 0" class="empty-container">
        <tn-icon name="star" color="#cccccc" size="120rpx"></tn-icon>
        <text class="empty-text">暂无收藏内容</text>
      </view>

      <!-- 收藏列表 -->
      <view v-else class="collection-list">
        <view
            v-for="item in collectionData.records"
            :key="item.id"
            class="collection-item"
            @tap="goToDetail(item)"
        >
          <!-- 有封面图时显示 -->
          <view class="item-content" :class="{'with-image': item.coverImage}">
            <view class="item-info">
              <view class="item-title">{{ item.title }}</view>
              <view class="item-desc" v-if="item.desc">{{ item.desc }}</view>

              <!-- 标签和数据 -->
              <view class="item-meta">
                <view class="tags-container" v-if="item.tags && item.tags.length > 0">
                  <view
                      v-for="(tag, tagIndex) in item.tags"
                      :key="tagIndex"
                      class="tag-item"
                      :class="[`tn-${item.color || 'blue'}_bg`]"
                  >
                    {{ tag }}
                  </view>
                </view>

                <view class="stats">
                  <view class="stat-item">
                    <tn-icon name="eye" color="#999" size="24rpx"></tn-icon>
                    <text>{{ item.viewCount || 0 }}</text>
                  </view>
                  <view class="stat-item">
                    <tn-icon
                        :name="item.isLiked ? 'like-fill' : 'like'"
                        :color="item.isLiked ? '#FF5722' : '#999'"
                        size="24rpx"
                    ></tn-icon>
                    <text>{{ item.likeCount || 0 }}</text>
                  </view>
                  <view class="stat-item">
                    <tn-icon
                        name="star-fill"
                        color="#FF9800"
                        size="24rpx"
                    ></tn-icon>
                    <text>{{ item.collectionCount || 0 }}</text>
                  </view>
                </view>
              </view>
            </view>

            <image
                v-if="item.coverImage"
                :src="getImageUrl(item.coverImage)"
                @error="handleImgError"
                class="item-image"
                mode="aspectFill"
            ></image>
            
            <!-- 无封面图但表示需要显示图片位置时使用默认图片 -->
            <image
                v-else-if="!item.coverImage && item.hasImage"
                :src="defaultImage"
                class="item-image"
                mode="aspectFill"
            ></image>
          </view>
        </view>

        <!-- 加载更多 -->
        <view class="load-more">
          <tn-button
              v-if="!noMoreData && collectionData.records.length > 0"
              @tap="loadMore"
              :loading="loadingStatus === 1"
              bg-color="#5677fc"
              width="40%"
              height="70rpx"
              font-size="26rpx"
              padding="0"
              radius="35rpx"
              :shadow="true"
          >
            <view class="btn-content tn-flex tn-flex-row-center">
              <tn-icon v-if="loadingStatus !== 1" name="more-circle" color="#fff" size="30rpx"></tn-icon>
              <text class="tn-ml-xs tn-white_text">{{ loadingStatus !== 1 ? '加载更多' : '加载中...' }}</text>
            </view>
          </tn-button>
          <view v-else-if="noMoreData && collectionData.records.length > 0"
                class="no-more tn-flex tn-flex-center-center">
            <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
            <text class="tn-gray_text tn-ml-xs">没有更多了</text>
          </view>
        </view>
      </view>
    </pulldownRefresh>
  </view>
</template>

<style lang="scss" scoped>
.collection-container {
  background-color: #f5f7fa;

  // 确保内容区域不被导航栏遮挡
  padding-bottom: 40rpx;
}

// 加载状态和空状态
.loading-container, .empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;

  .loading-text, .empty-text {
    font-size: 28rpx;
    color: #999;
    margin-top: 20rpx;
  }
}

// 收藏列表样式
.collection-list {
  padding: 20rpx;

  .collection-item {
    background-color: #ffffff;
    border-radius: 12rpx;
    margin-bottom: 24rpx;
    padding: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    transition: all 0.2s;

    &:active {
      transform: scale(0.98);
      background-color: #f9f9f9;
    }

    .item-content {
      display: flex;

      &.with-image {
        justify-content: space-between;
      }

      .item-info {
        flex: 1;
        overflow: hidden;

        .item-title {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 10rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .item-desc {
          font-size: 26rpx;
          color: #666;
          margin-bottom: 15rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .item-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .tags-container {
            display: flex;
            flex-wrap: wrap;

            .tag-item {
              padding: 4rpx 12rpx;
              font-size: 20rpx;
              color: #ffffff;
              border-radius: 20rpx;
              margin-right: 10rpx;
              margin-bottom: 5rpx;
            }
          }

          .stats {
            display: flex;
            align-items: center;

            .stat-item {
              display: flex;
              align-items: center;
              margin-left: 16rpx;

              text {
                font-size: 22rpx;
                color: #999;
                margin-left: 4rpx;
              }
            }
          }
        }
      }

      .item-image {
        width: 160rpx;
        height: 120rpx;
        margin-left: 20rpx;
        border-radius: 10rpx;
        flex-shrink: 0;
      }
    }
  }
}

// 加载更多按钮
.load-more {
  display: flex;
  justify-content: center;
  margin: 30rpx 0 60rpx;

  .btn-content {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .no-more {
    padding: 15rpx 30rpx;
    font-size: 26rpx;
    color: #999;
    background-color: #f5f7fa;
    border-radius: 30rpx;
  }
}
</style>