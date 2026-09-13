<script setup>
import {ref, onMounted} from "vue";
import {
  queryUserdataMessage,
  queryUserDate,
  selectByUserId,
} from "@/service/api/blogController";
import pulldownRefresh from "@/components/load/pulldownRefresh.vue";
import loadMore from "@/components/load/loadMore.vue";
import recommendGroupVue from "@/components/recommend-group.vue";
import {useUserStore} from "@/stores/user";
import navbar from "@/components/navbar.vue"
import { onLoad } from '@dcloudio/uni-app'


const userStore = useUserStore();
const userId = ref();

// 用户统计数据
const userStats = ref({
  likeCount: 0,
  hotReviewsCount: 0,
  fansCount: 0,
  focusCount: 0,
});

// 用户基本信息
const userInfo = ref({
  avatar: [],
  username: "示例资料",
  desc: "",
});

// 用户动态列表
const content = ref([]);
const page = ref(1);
const pageSize = ref(10);
const loadingStatus = ref(0); // 0加载前，1加载中，2没有更多了
const refreshRef = ref(null);
const enableScroll = ref(true);

// 获取用户统计数据
const getUserStats = async () => {
  try {
    const res = await queryUserdataMessage({userId:userId.value});
    if (res.code === 1) {
      userStats.value = res.data;
    }
  } catch (error) {
    console.error("获取用户统计数据失败:", error);
  }
};

// 获取用户基本信息
const getUserInfo = async () => {
  try {
    console.log("获取用户信息：",)
    const res = await queryUserDate({userId: userId.value});
    if (res.code === 1) {
      userInfo.value = res.data;

      // 检查并清理头像数组中的无效URL
      if (userInfo.value.avatar && userInfo.value.avatar.length > 0) {
        // 先清理数组中的无效URL
        userInfo.value.avatar = userInfo.value.avatar.map(url => {
          // 检查URL是否包含"null"前缀
          if (typeof url === 'string' && url.startsWith('null')) {
            // 移除"null"前缀
            return url.replace(/^null/, '')
          }
          return url
        }).filter(url => url && typeof url === 'string' && url.trim() !== '')

        const avatarCount = userInfo.value.avatar.length

        // 需要6个头像用于立方体的6个面
        if (avatarCount < 6) {
          // 创建一个新数组，长度为6
          const filledAvatars = [...userInfo.value.avatar]

          // 计算需要填充的数量
          const needToFill = 6 - avatarCount

          // 使用循环填充头像
          for (let i = 0; i < needToFill; i++) {
            // 使用取模运算循环使用已有头像
            filledAvatars.push(userInfo.value.avatar[i % avatarCount])
          }

          // 更新头像数组
          userInfo.value.avatar = filledAvatars
        }
      } else {
        // 如果没有头像，设置默认头像
        userInfo.value.avatar = Array(6).fill('https://assets.example.invalid/placeholder.png')
      }

      console.log('填充后的头像数组:', userInfo.value.avatar)
    }
  } catch (error) {
    console.error('获取用户基本信息失败:', error)
  }
}

// 获取用户动态列表
const getUserBlogs = async (isRefresh = false) => {
  if (loadingStatus.value !== 0) return;
  loadingStatus.value = 1;
  try {
    const res = await selectByUserId({
      current: page.value,
      userId: userId.value
    });

    if (res.code === 1) {
      if (isRefresh) {
        content.value = res.data || [];
      } else {
        content.value = [...content.value, ...(res.data || [])];
      }
      // 判断是否还有更多数据
      if (!res.data || res.data.length < pageSize.value) {
        loadingStatus.value = 2; // 没有更多了
      } else {
        loadingStatus.value = 0; // 可以继续加载
        page.value++;
      }
    } else {
      uni.showToast({
        title: res.msg || "获取数据失败",
        icon: "none",
      });
      loadingStatus.value = 0;
    }
  } catch (error) {
    console.error("获取用户动态列表失败:", error);
    loadingStatus.value = 0;
  }
};

// 重置加载状态
const resetLoadMore = () => {
  page.value = 1;
  loadingStatus.value = 0;
};

// 处理下拉刷新
const onRefresh = async () => {
  try {
    resetLoadMore();
    await Promise.all([getUserStats(), getUserInfo(), getUserBlogs(true)]);

    uni.showToast({
      title: "刷新成功",
      icon: "success",
    });
  } catch (error) {
    console.error("刷新失败:", error);
    uni.showToast({
      title: "刷新失败",
      icon: "error",
    });
  } finally {
    refreshRef.value?.endPulldownRefresh();
  }
};

// 监听页面滚动到底部
const onReachBottom = () => {
  getUserBlogs();
};

// 设置是否可以滚动
const setEnableScroll = (value) => {
  enableScroll.value = value;
};

// 点击圈子
const clickGroup = (item) => {
  console.log("点击了圈子", item);
  uni.navigateTo({
    url: `/group_pages/views/group-detail?id=${item.id}`,
  });
};

onLoad((options)=>{
  console.log(" userId.value", userId.value)
  if(options.userId){
    userId.value = options.userId
  }
  if (!userId.value) {
    uni.showToast({
      title: '获取用户信息失败',
      icon: 'none'
    })
    return
  }
  getUserStats();
  getUserInfo();
  getUserBlogs(true);
})
</script>

<template>
  <view class="my_other_circle_container">
    <navbar :title="userStore.userInfo.userName+'的动态'" back="left" home="home"></navbar>
    <!-- 下拉刷新包裹器 -->
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
          @scrolltolower="onReachBottom"
      >
        <!-- 立体头像-->
        <view
            class="cube"
            :style="'background-image: url(http://localhost:8080/images/blogger/bg_image_1.jpg);'"
        >
          <view class="cube__container">
            <view class="cube__container__body">
              <view
                  class="cube__container__body__item cube__container__body__item--front"
                  :style="{ backgroundImage: `url(${userInfo.avatar[0]})` }"
              ></view>
              <view
                  class="cube__container__body__item cube__container__body__item--back"
                  :style="{ backgroundImage: `url(${userInfo.avatar[1]})` }"
              ></view>
              <view
                  class="cube__container__body__item cube__container__body__item--right"
                  :style="{ backgroundImage: `url(${userInfo.avatar[2]})` }"
              ></view>
              <view
                  class="cube__container__body__item cube__container__body__item--left"
                  :style="{ backgroundImage: `url(${userInfo.avatar[3]})` }"
              ></view>
              <view
                  class="cube__container__body__item cube__container__body__item--top"
                  :style="{ backgroundImage: `url(${userInfo.avatar[4]})` }"
              ></view>
              <view
                  class="cube__container__body__item cube__container__body__item--bottom"
                  :style="{ backgroundImage: `url(${userInfo.avatar[5]})` }"
              ></view>
            </view>
          </view>

          <view class="tn-text-center tn-mt-lg">
            <view class="tn-p tn-text-bold tn-text-lg">{{
                userInfo.username
              }}
            </view>
            <view class="tn-pb-xl tn-text-lg">{{ userInfo.desc }}</view>
          </view>
        </view>

        <!-- 消息&数据 -->
        <view class="blogger-tips-data">
          <view class="blogger-tips-data__wrap tn-white_bg">
            <view class="blogger-tips-data__info tn-flex">
              <view class="tn-flex-1 tn-p-sm tn-m-xs">
                <view class="tn-flex tn-flex-column tn-flex-center-center">
                  <view class="">
                    <view class="tn-text-xxl tn-orange_text">
                      {{ userStats.likeCount || 0 }}
                    </view>
                  </view>
                  <view class="tn-mt-xs tn-gray_text tn-text-center">
                    <tn-icon name="like"></tn-icon>
                    <text class="tn-padding-left-xs">爱心</text>
                  </view>
                </view>
              </view>
              <view class="tn-flex-1 tn-p-sm tn-m-xs">
                <view class="tn-flex tn-flex-column tn-flex-center-center">
                  <view class="">
                    <view class="tn-text-xxl tn-blue_text">
                      {{ userStats.hotReviewsCount }}
                    </view>
                  </view>
                  <view class="tn-mt-xs tn-gray_text tn-text-center">
                    <tn-icon name="message"></tn-icon>
                    <text class="tn-padding-left-xs">热评</text>
                  </view>
                </view>
              </view>
              <view class="tn-flex-1 tn-p-sm tn-m-xs">
                <view class="tn-flex tn-flex-column tn-flex-center-center">
                  <view class="">
                    <view class="tn-text-xxl tn-red_text"
                    >{{ userStats.fansCount }}
                    </view>
                  </view>
                  <view class="tn-mt-xs tn-gray_text tn-text-center">
                    <tn-icon name="vip"></tn-icon>
                    <text class="tn-padding-left-xs">粉丝</text>
                  </view>
                </view>
              </view>
              <view class="tn-flex-1 tn-p-sm tn-m-xs">
                <view class="tn-flex tn-flex-column tn-flex-center-center">
                  <view class="">
                    <view class="tn-text-xxl tn-cyan_text">
                      {{ userStats.focusCount }}
                    </view>
                  </view>
                  <view class="tn-mt-xs tn-gray_text tn-text-center">
                    <tn-icon name="star"></tn-icon>
                    <text class="tn-padding-left-xs">关注</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!--内容-->
        <!--附近的人发的动态消息-->
        <black-title class="tn-mt" name="topics" title="Ta的动态"/>
        <recommendGroupVue
            :content="content"
            @clickGroup="clickGroup"
        ></recommendGroupVue>

        <!-- 加载更多 -->
        <loadMore :status="loadingStatus"/>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<style scoped lang="scss">
$cube-size: 120rpx;
$cube-split: 60rpx;

/* 立体头像 start*/
.cube {
  background: #fff;
  background-repeat: no-repeat;
  background-size: cover;
  height: 550rpx;
  display: flex;
  justify-content: center;
  padding-top: 40rpx;
  overflow: hidden;
  position: relative;
  flex-direction: column;
  align-items: center;
  font-weight: 300;

  &__container {
    margin-top: 180rpx;
    position: relative;
    width: $cube-size;
    height: $cube-size;
    -webkit-perspective: 500px;
    perspective: 500px; //透视太大会超过屏幕就不好了吖

    &:before {
      content: '';
      width: $cube-size;
      height: $cube-size;
      position: absolute;
      background-color: #3c6496;
      filter: blur(60px);
      opacity: .8;
    }

    &__body {
      width: 100%;
      height: 100%;
      position: absolute;
      transform-style: preserve-3d;
      animation: cubeFrame 20s linear infinite;
      transform: translateZ(-75px) rotateX(0deg) rotateY(0deg);

      &__item {
        position: absolute;
        width: 100%;
        height: 100%;
        border-radius: 10rpx;
        border: 2rpx solid #fff;
        background-size: cover;
        background-position: center;

        &--front {
          transform: translateZ($cube-split);
          background-color: #BEEBFF;
        }

        &--back {
          transform: rotateX(180deg) translateZ($cube-split);
          background-color: #BEEBFF;
        }

        &--right {
          transform: rotateY(90deg) translateZ($cube-split);
          background-color: #BEEBFF;
        }

        &--left {
          transform: rotateY(-90deg) translateZ($cube-split);
          background-color: #BEEBFF;
        }

        &--top {
          transform: rotateX(90deg) translateZ($cube-split);
          background-color: #BEEBFF;
        }

        &--bottom {
          transform: rotateX(-90deg) translateZ($cube-split);
          background-color: #BEEBFF;
        }
      }
    }
  }
}

@keyframes cubeFrame {
  10% {
    transform: translateZ(-75px) rotateX(40deg) rotateY(60deg);
  }
  15% {
    transform: translateZ(-75px) rotateX(80deg) rotateY(20deg);
  }
  20% {
    transform: translateZ(-75px) rotateX(-180deg) rotateY(-70deg);
  }
  30% {
    transform: translateZ(-75px) rotateX(90deg) rotateY(180deg);
  }
  40% {
    transform: translateZ(-75px) rotateX(-10deg) rotateY(-140deg);
  }
  45% {
    transform: translateZ(-75px) rotateX(-100deg) rotateY(20deg);
  }
  55% {
    transform: translateZ(-75px) rotateX(-10deg) rotateY(-35deg);
  }
  60% {
    transform: translateZ(-75px) rotateX(180deg) rotateY(360deg);
  }
  70% {
    transform: translateZ(-75px) rotateX(-180deg) rotateY(-360deg);
  }
  80% {
    transform: translateZ(-75px) rotateX(45deg) rotateY(-70deg);
  }
  90% {
    transform: translateZ(-75px) rotateX(-45deg) rotateY(70deg);
  }
  100% {
    transform: translateZ(-75px) rotateX(-360deg) rotateY(360deg);
  }
}

/* 立体头像 end*/

/* 信息提示 start */
.blogger-tips-data {
  background-color: #F8F7F3;

  &__wrap {
    border-radius: 60rpx 60rpx 0 0;
  }

  &__message {
    padding-top: 60rpx;

    &__container {
      padding: 5rpx;
      border-radius: 100rpx;
    }

    &__avatar {
      margin: 6rpx 0 0 6rpx;
    }
  }

  &__info {
    padding: 40rpx 0 0 0;
  }
}

/* 信息提示 end */

/* 下拉刷新和滚动容器样式 */
.my_other_circle_container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

:deep(.refresh-content) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.scroll-container {
  flex: 1;
  height: 100%;
}
</style>
