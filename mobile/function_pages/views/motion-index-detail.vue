<template>
  <view class="template-details tn-u-safe-area--more">
    <!-- 顶部自定义导航 -->
    <navbar title="文章详情"></navbar>

    <view v-if="loading" class="loading-container tn-flex tn-flex-center-center">
      <tn-loading></tn-loading>
    </view>

    <view v-else-if="!content" class="empty-tip tn-flex tn-flex-direction-column tn-flex-center-center">
      <tn-icon name="info-circle" size="120rpx" color="#cccccc"></tn-icon>
      <text class="tn-gray_text tn-mt-sm">未找到文章内容</text>
    </view>

    <view v-else>
      <!-- 图文信息 -->
      <view>
        <view class="blogger__item">
          <view class="" style="text-align: center;font-weight: 800;font-size: 36rpx;">
            <view>{{ content.title }}</view>
          </view>
          <view style="text-align: center;" class="tn-pr tn-pl-sm tn-pt-xs tn-gray_text">发布于 {{
              formatDate(content.creatTime)
            }}
          </view>
        </view>
        <view class="blogger__content">
          <rich-text :nodes="content.content"></rich-text>
        </view>
      </view>

      <!--底部操作按钮：点赞、收藏、分享-->
      <view class="tn-flex tn-flex-start-center" style="margin: 40rpx 0 60rpx 0;">
        <!-- 点赞按钮 -->
        <view v-if="liked===true || liked===false" class="tn-m-xs tn-text-center" style="width: 33.33%">
          <tn-button 
            class="tn-flex-1" 
            :bg-color="content.isLiked ? '#FF5722' : '#00FFC6'" 
            padding="40rpx 0" 
            width="90%" 
            height="80rpx" 
            shadow 
            bold
            @click="toggleLike">
            <tn-icon :name="content.isLiked ? 'like-fill' : 'like'" class="tn-pr-xs tn-black_text"></tn-icon>
            <text class="tn-black_text">{{ content.isLiked ? '已点赞' : '点 赞' }}</text>
          </tn-button>
        </view>
        
        <!-- 收藏按钮 -->
        <view v-if="myCollection===true || collection===false" class="tn-m-xs tn-text-center" style="width: 33.33%">
          <tn-button 
            class="tn-flex-1" 
            :bg-color="content.isCollected ? '#FF9800' : '#8BC34A'" 
            padding="40rpx 0" 
            width="90%" 
            height="80rpx" 
            shadow 
            bold
            @click="toggleCollection">
            <tn-icon :name="content.isCollected ? 'star-fill' : 'star'" class="tn-pr-xs tn-black_text"></tn-icon>
            <text class="tn-black_text">{{ content.isCollected ? '已收藏' : '收 藏' }}</text>
          </tn-button>
        </view>

        <!-- 分享按钮 -->
        <view class="tn-m-xs tn-text-center" style="width: 33.33%">
          <button 
            class="share-button" 
                     open-type="share">
            <tn-button 
              class="tn-flex-1" 
              bg-color="#FFF00D" 
              padding="40rpx 0" 
              width="90%" 
              height="80rpx" 
              shadow 
              bold>
            <tn-icon name="share" class="tn-pr-xs tn-black_text"></tn-icon>
            <text class="tn-black_text">分 享</text>
          </tn-button>
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onLoad, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app';
import Navbar from "@/components/navbar.vue";
import { getDietPages, like, collection } from '@/service/api/newKnowledgeController';

const content = ref(null);
const loading = ref(true);
const articleId = ref(null);

// 格式化日期
const formatDate = (dateArr) => {
  if (!dateArr || !Array.isArray(dateArr) || dateArr.length < 3) {
    return '';
  }
  
  const [year, month, day] = dateArr;
  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
};

// 获取文章详情
const getArticleDetail = async (id, isLiked = false, isCollected = false) => {
  loading.value = true;
  
  try {
    const res = await getDietPages({
      id: id
    });
    
    if (res && res.code === 1 && res.data) {
      content.value = res.data;
      
      // 优先使用传递过来的状态，如果没有传递则使用后端返回的状态
      content.value.isLiked = isLiked !== undefined ? isLiked : (content.value.isLiked || false);
      content.value.isCollected = isCollected !== undefined ? isCollected : (content.value.isCollection || false);
      
      // 确保 likeCount 字段存在
      if (content.value.likeCount === undefined) {
        content.value.likeCount = 0;
      }
    } else {
      uni.showToast({
        title: res?.msg || '获取文章详情失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取文章详情失败:', error);
    
    uni.showToast({
      title: '获取文章详情失败',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

// 点赞/取消点赞
const toggleLike = async () => {
  if (!content.value || !articleId.value) {
    uni.showToast({
      title: '操作失败，文章信息不完整',
      icon: 'none'
    });
    return;
  }
  
  try {
    const res = await like({ id: articleId.value });
    
    if (res && res.code === 1) {
      // 更新本地状态
      content.value.isLiked = !content.value.isLiked;
      content.value.likeCount = content.value.isLiked ? 
        (content.value.likeCount || 0) + 1 : 
        Math.max(0, (content.value.likeCount || 1) - 1);
      
      uni.showToast({
        title: content.value.isLiked ? '点赞成功' : '已取消点赞',
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

// 收藏/取消收藏
const toggleCollection = async () => {
  if (!content.value || !articleId.value) {
    uni.showToast({
      title: '操作失败，文章信息不完整',
      icon: 'none'
    });
    return;
  }
  
  try {
    const res = await collection({ id: articleId.value });
    
    if (res && res.code === 1) {
      // 更新本地状态
      content.value.isCollected = !content.value.isCollected;
      
      uni.showToast({
        title: content.value.isCollected ? '收藏成功' : '已取消收藏',
        icon: 'none'
        });
      } else {
      uni.showToast({
        title: res?.msg || '操作失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('收藏操作失败:', error);
    
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    });
  }
};

const liked = ref()
const myCollection = ref()
// 监听页面加载
onLoad((query) => {
  if (query && query.id) {
    articleId.value = query.id;
    
    // 接收传递过来的点赞和收藏状态
    const isLiked = query.isLiked==="true";
    const isCollected = query.isCollection==="true";
    liked.value = query.isLiked==="true"
    myCollection.value = query.isCollection==="true"
    getArticleDetail(query.id, isLiked, isCollected);
  } else {
    uni.showToast({
      title: '未获取到文章ID',
      icon: 'none'
    });
    loading.value = false;
  }
});

// 定义分享给朋友
onShareAppMessage(() => {
  if (!content.value) {
    return {
      title: '精彩文章',
      path: '/function_pages/views/motion-wiki'
    };
  }
  
  return {
    title: content.value.title,
    path: `/function_pages/views/motion-index-detail?id=${articleId.value}`,
    imageUrl: content.value.coverImage || '', // 如果后端返回了封面图则使用
    success: function(res) {
      uni.showToast({
        title: '分享成功',
        icon: 'success'
      });
    },
    fail: function(err) {
      console.log("分享失败", err);
    }
  };
});

// 定义分享到朋友圈
onShareTimeline(() => {
  if (!content.value) {
    return {
      title: '精彩文章',
      query: ''
    };
  }
  
  return {
    title: content.value.title,
    query: `id=${articleId.value}`,
    imageUrl: content.value.coverImage || '',
  };
});
</script>

<style lang="scss" scoped>
/* 胶囊*/
.tn-custom-nav-bar__back {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  box-sizing: border-box;
  background-color: rgba(0, 0, 0, 0.15);
  border-radius: 1000rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.5);
  color: #FFFFFF;
  font-size: 18px;

  .icon {
    display: block;
    flex: 1;
    margin: auto;
    text-align: center;
  }

  &:before {
    content: " ";
    width: 1rpx;
    height: 110%;
    position: absolute;
    top: 22.5%;
    left: 0;
    right: 0;
    margin: auto;
    transform: scale(0.5);
    transform-origin: 0 0;
    pointer-events: none;
    box-sizing: border-box;
    opacity: 0.7;
    background-color: #FFFFFF;
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

/* 文章内容 start*/
.blogger {
  &__item {
    padding: 30rpx;
  }

  &__content {
    background-color: white;
    max-width: 97%;
    margin: 0 auto;
    padding: 20px;
    line-height: 1.5;
    
    :deep(img) {
      max-width: 100%;
      height: auto;
    }
    
    :deep(p) {
      margin: 10px 0;
    }
    
    :deep(ul), :deep(ol) {
      padding-left: 20px;
    }
    
    :deep(li) {
      margin: 5px 0;
    }
  }
}

/* 底部按钮样式 */
.tn-button {
  transition: all 0.3s;
  
  &:active {
    transform: scale(0.96);
  }
}

/* 分享按钮 */
.share-button {
  background: none;
  border: none;
  margin: 0;
  padding: 0;
  line-height: normal;
  
  &::after {
    border: none;
  }
}
</style>