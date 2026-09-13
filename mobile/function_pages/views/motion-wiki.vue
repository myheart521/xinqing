<template>
  <view class="template-screen tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <navbar title="百科"></navbar>

    <!-- 下拉刷新组件 -->
    <pulldownRefresh
        ref="refreshRef"
        :top="0"
        :threshold="80"
        @refresh="onRefresh"
        @setEnableScroll="setEnableScroll"
    >
      <scroll-view
          class="scroll-container"
          scroll-y
      >
        <view class="content-container">
      <view class="" style="margin-top: 50rpx;">
        <view class="tn-flex justify-between">
          <view class="tn-m tn-text-bold tn-text-xl">
            百科阅读
          </view>
          <view class="tn-m" style="font-size: 50rpx;">
            <tn-icon name="data"></tn-icon>
          </view>
        </view>
      </view>

      <view class="tn-ml tn-text-sm tn-gray_text" style="margin-top: -25rpx;">
        <text>阅读文章，请查收</text>
      </view>

          <!-- 加载中状态 -->
          <view v-if="loading && hotArticle.length === 0" class="loading-container tn-flex tn-flex-center-center tn-py-xl">
            <tn-loading></tn-loading>
                </view>

          <!-- 无数据提示 -->
          <view v-else-if="hotArticle.length === 0" class="empty-tip tn-flex tn-flex-direction-column tn-flex-center-center">
            <tn-icon name="info-circle" size="120rpx" color="#cccccc"></tn-icon>
            <text class="tn-gray_text tn-mt-sm">暂无百科内容</text>
              </view>

          <!-- 文章列表 -->
          <view v-else class="article-list">
            <view 
              v-for="(item, index) in hotArticle" 
              :key="index"
              class="article-item tn-shadow"
              @click="goToDetail(item.id, item.isLiked, item.isCollection)"
            >
              <view class="article-content" :class="{'with-image': item.coverImage}">
                <!-- 文章信息 -->
                <view class="article-info">
                  <view class="article-title">{{ item.title }}</view>
                  <view class="article-desc">{{ item.desc }}</view>
                  
                  <!-- 标签和统计信息 -->
                  <view class="article-meta">
                    <!-- 标签 -->
                    <view class="tags-wrap" v-if="item.tags && item.tags.length">
                      <view
                        v-for="(tag, tagIndex) in item.tags"
                        :key="tagIndex"
                        class="tag-item"
                        :class="[`tn-gradient-bg__${item.color || 'blue'}-light`, `tn-${item.color || 'blue'}_text`]"
                      >
                        <text class="tag-prefix">#</text>{{ tag }}
                </view>
                </view>
                    
                    <!-- 统计信息 -->
                    <view class="stats-wrap">
                      <view class="stat-item">
                        <tn-icon name="footprint" size="24rpx"></tn-icon>
                        <text>{{ item.viewCount || 0 }}</text>
                  </view>
                  <view
                        class="stat-item" 
                        @tap.stop="toggleLike(item)"
                      >
                        <tn-icon 
                          :name="item.isLiked ? 'like-fill' : 'like'"
                          :color="item.isLiked ? '#FF5722' : ''"
                          size="24rpx"
                        ></tn-icon>
                        <text>{{ item.likeCount || 0 }}</text>
                      </view>
                    </view>
                  </view>
                </view>

                <!-- 封面图片 -->
                <image
                  v-if="item.coverImage"
                  :src="item.coverImage"
                  class="article-image"
                  mode="aspectFill"
                  @error="handleImgError($event, index)"
                ></image>
              </view>
            </view>

            <!-- 加载更多 -->
            <view class="load-more">
              <tn-button
                v-if="!noMoreData && hotArticle.length > 0"
                @tap="loadMore"
                :loading="loadStatus === 1"
                bg-color="#5677fc"
                width="40%"
                height="70rpx"
                font-size="26rpx"
                padding="0"
                radius="35rpx"
                :shadow="true"
              >
                <view class="btn-content">
                  <tn-icon v-if="loadStatus !== 1" name="more-circle" color="#fff" size="30rpx"></tn-icon>
                  <text class="tn-white_text tn-ml-xs">{{ loadStatus !== 1 ? '加载更多' : '加载中...' }}</text>
                </view>
              </tn-button>
              <view v-else-if="noMoreData && hotArticle.length > 0" class="no-more">
                <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
                <text class="tn-gray_text tn-ml-xs">没有更多了</text>
              </view>
            </view>
          </view>
      </view>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue';
import {onShow} from '@dcloudio/uni-app'; // 添加onShow生命周期钩子
import navbar from "@/components/navbar.vue";
import {getKnowledgePages, like} from '@/service/api/newKnowledgeController';
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue';
import pulldownRefresh from '@/components/load/pulldownRefresh.vue';


// 文章列表数据
const hotArticle = ref([]);
const loading = ref(false);
const refreshRef = ref(null);
const enableScroll = ref(true);
const isFirstLoad = ref(true); // 标记是否是首次加载

// 分页相关
const pagination = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0
});

// 加载状态：0加载前，1加载中，2没有更多数据
const loadStatus = ref(0);
const noMoreData = ref(false);





// 获取文章列表
const fetchArticleList = async (isRefresh = false) => {
  if (loadStatus.value === 1) {
    console.log('正在加载中，请稍后再试');
    return;
  }

  if (isRefresh) {
    loading.value = true;
  }
  
  loadStatus.value = 1;
  
  try {
    console.log('获取文章列表, 页码:', pagination.pageNo);
    
    // 调用接口获取知识分页数据
    const res = await getKnowledgePages({
      pageDTO: {
        current: pagination.pageNo,
        size: pagination.pageSize
      }
    });

    if (res && res.code === 1 && res.data) {
      // 根据后端返回的数据结构处理响应
      const records = Array.isArray(res.data) ? res.data : (res.data.records || []);
      
      if (isRefresh) {
        hotArticle.value = records;
      } else {
        hotArticle.value = [...hotArticle.value, ...records];
      }

      // 更新总数和判断是否还有更多数据
      pagination.total = res.data.total || records.length;
      
      if (hotArticle.value.length >= pagination.total || records.length < pagination.pageSize) {
        noMoreData.value = true;
      } else {
        noMoreData.value = false;
      }
    } else {
      uni.showToast({
        title: res?.msg || '获取数据失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取文章列表失败:', error);
    
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    });
  } finally {
    loadStatus.value = 0;
    loading.value = false;
    
    // 结束下拉刷新
    if (isRefresh && refreshRef.value) {
      refreshRef.value.endPulldownRefresh();
    }
  }
};

// 重置加载状态
const resetLoadStatus = () => {
  pagination.pageNo = 1;
  loadStatus.value = 0;
  noMoreData.value = false;
};

// 下拉刷新
const onRefresh = async () => {
  try {
    console.log('开始刷新数据');
    resetLoadStatus();
    await fetchArticleList(true);
    
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
    // 结束下拉刷新状态
    if (refreshRef.value) {
      refreshRef.value.endPulldownRefresh();
    }
  }
  
  return true;
};

// 加载更多
const loadMore = async () => {
  if (noMoreData.value) {
    uni.showToast({
      title: '没有更多数据了',
      icon: 'none'
    });
    return;
  }
  
  if (loadStatus.value === 1) {
    return;
  }
  
  pagination.pageNo++;
  fetchArticleList(false);
};

// 设置是否可滚动
const setEnableScroll = (enable) => {
  enableScroll.value = enable;
};

// 跳转到详情页
const goToDetail = (id, isLiked, isCollection) => {
      uni.navigateTo({
    url: `/function_pages/views/motion-index-detail?id=${id}&isLiked=${isLiked}&isCollection=${isCollection}`
  });
};

// 点赞/取消点赞
const toggleLike = async (item) => {
  try {
    const res = await like({ id: item.id });
    if (res && res.code === 1) {
      // 更新本地状态
      item.isLiked = !item.isLiked;
      item.likeCount = item.isLiked ? item.likeCount + 1 : item.likeCount - 1;
      
      uni.showToast({
        title: item.isLiked ? '点赞成功' : '已取消点赞',
        icon: 'none'
      });
    } else {
      uni.showToast({
        title: res?.msg || '操作失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('点赞操作失败:', error);
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    });
  }
};

// 页面加载时获取数据
onMounted(() => {
  isFirstLoad.value = true;
  resetLoadStatus();
  fetchArticleList(true);
});

// 当页面显示时，如果不是首次加载，则重新请求数据
onShow(() => {
  if (!isFirstLoad.value) {
    console.log('页面返回，重新请求数据');
    resetLoadStatus();
    fetchArticleList(true);
  }
  isFirstLoad.value = false;
});

// 处理图片加载错误
const handleImgError = (e, index) => {
  if (hotArticle.value[index]) {
    // 图片加载失败时，直接移除图片
    hotArticle.value[index].coverImage = '';
  }
};
</script>

<style lang="scss" scoped>
.template-screen {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.scroll-container {
  flex: 1;
  height: 100%;
}

.content-container {
  padding: 20rpx;
}

/* 资讯主图 start*/
.image-article {
  border-radius: 8rpx;
  width: 200rpx;
  height: 200rpx;
  position: relative;
}

.image-pic {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: top;
  border-radius: 10rpx;
}

.article-shadow {
  border-radius: 15rpx;
  box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
  margin-bottom: 30rpx;
  transition: all 0.3s;
  
  &:active {
    transform: scale(0.98);
  }
}

/* 文字截取*/
.clamp-text-1 {
  -webkit-line-clamp: 1;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  overflow: hidden;
}

.clamp-text-2 {
  -webkit-line-clamp: 2;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* 标签内容 start*/
.tn-tag-content {
  &__item {
    display: inline-block;
    line-height: 35rpx;
    padding: 5rpx 25rpx;

    &--prefix {
      padding-right: 10rpx;
    }
  }
}

/* 加载状态 */
.loading-container, .empty-tip {
  padding: 200rpx 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 加载更多容器 */
.load-more {
  margin: 30rpx 0;
  display: flex;
  justify-content: center;
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-more {
  display: flex;
  align-items: center;
  padding: 12rpx 30rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
}

/* 文章列表样式 */
.article-list {
  .article-item {
    background-color: #fff;
    border-radius: 12rpx;
    margin-bottom: 20rpx;
    padding: 24rpx;
    transition: all 0.2s;
    
    &:active {
      transform: scale(0.98);
      background-color: #fafafa;
    }
  }
  
  .article-content {
    display: flex;
    gap: 20rpx;
    
    &.with-image {
      .article-info {
        flex: 1;
        min-width: 0; // 防止文本溢出
      }
    }
  }
  
  .article-info {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  
  .article-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 12rpx;
  }
  
  .article-desc {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 16rpx;
  flex: 1;
  }
  
  .article-meta {
    margin-top: auto;
  }
  
  .tags-wrap {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: 12rpx;
    
    .tag-item {
      padding: 4rpx 16rpx;
      font-size: 20rpx;
      border-radius: 20rpx;
      
      .tag-prefix {
        margin-right: 4rpx;
      }
    }
  }
  
  .stats-wrap {
    display: flex;
    align-items: center;
    gap: 20rpx;
    
    .stat-item {
      display: flex;
      align-items: center;
      gap: 4rpx;
      color: #999;
      font-size: 24rpx;
    }
  }
  
  .article-image {
    width: 200rpx;
    height: 160rpx;
    border-radius: 8rpx;
    flex-shrink: 0;
    background-color: #f5f5f5;
  }
}
</style>