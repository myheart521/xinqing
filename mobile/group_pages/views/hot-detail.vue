<script setup>
import navbar from '@/components/navbar.vue'
import {ref, onMounted} from 'vue'
import recommendGroupVue from '@/components/recommend-group.vue'
import {searchByTag} from '@/service/api/blogController'
import EmptyData from '@/components/empty-data.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'
import loadMore from '@/components/load/loadMore.vue'
import { onLoad } from '@dcloudio/uni-app'

const content = ref([])
const currentTag = ref('')
const loading = ref(false)
const refreshRef = ref(null)
const enableScroll = ref(true)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const loadingStatus = ref(0) // 0加载前，1加载中，2没有更多了

// 重置加载状态
const resetLoadMore = () => {
  currentPage.value = 1
  loadingStatus.value = 0
}

// 获取标签相关的帖子
const getTagPosts = async (isRefresh = false) => {
  if (loadingStatus.value !== 0) return
  
  loadingStatus.value = 1
  try {
    const res = await searchByTag({
      tag: currentTag.value,
      current: currentPage.value
    })
    
    if (res.code === 1) {
      const newData = (res.data || []).map(item => ({
        id: item.id,
        userAvatar: item.userAvatar,
        userName: item.userName,
        userProfile: item.userProfile,
        label: item.label,
        title: item.title,
        mainImage: item.mainImage,
        viewUserCount: item.viewUserCount,
        comments: item.comments,
        liked: item.liked,
        likedUser: item.likedUser
      }))

      if (isRefresh) {
        content.value = newData
      } else {
        content.value = [...content.value, ...newData]
      }

      // 判断是否还有更多数据
      if (!res.data || res.data.length < pageSize.value) {
        loadingStatus.value = 2 // 没有更多了
      } else {
        loadingStatus.value = 0 // 可以继续加载
        currentPage.value++
      }
    } else {
      uni.showToast({
        title: res.message || '获取数据失败',
        icon: 'none'
      })
      loadingStatus.value = 0
    }
  } catch (error) {
    console.error('获取标签帖子失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
    loadingStatus.value = 0
  }
}

// 处理下拉刷新
const onRefresh = async () => {
  try {
    console.log('开始刷新数据')
    resetLoadMore()
    await getTagPosts(true)
    uni.showToast({
      title: '刷新成功',
      icon: 'success'
    })
  } catch (error) {
    console.error('刷新失败:', error)
    uni.showToast({
      title: '刷新失败',
      icon: 'error'
    })
  } finally {
    refreshRef.value?.endPulldownRefresh()
  }
}

// 监听页面滚动到底部
const onReachBottom = () => {
  getTagPosts()
}

// 设置是否可以滚动
const setEnableScroll = (value) => {
  enableScroll.value = value
}

const tag = ref()
// onMounted(() => {
//   const pages = getCurrentPages()
//   const currentPage = pages[pages.length - 1]
//   tag.value = currentPage?.options?.tag
//   if (tag.value) {
//     currentTag.value = decodeURIComponent(tag.value)
//     resetLoadMore()
//     getTagPosts(true)
//   }
// })

onLoad((options)=>{
  tag.value = options?.tag
  if (tag.value) {
    currentTag.value = decodeURIComponent(tag.value)
    resetLoadMore()
    getTagPosts(true)
  }
})

const clickGroup = (item) => {
  console.log('点击了帖子:', item)
  uni.navigateTo({
    url: `/group_pages/views/group-detail?id=${item.id}`
  })
}
</script>

<template>
  <view class="hot-detail">
    <navbar :title="currentTag"></navbar>
    <view class="tn-m">
      <tn-title mode="vLine" size="xl" :title="currentTag" assist-color="tn-gradient-bg__cool-6"></tn-title>
    </view>

    <!-- 下拉刷新包裹器 -->
    <pulldownRefresh
      ref="refreshRef"
      top="-55px"
      :threshold="80"
      @refresh="onRefresh"
      @setEnableScroll="setEnableScroll"
    >
      <scroll-view
        class="scroll-container"
        scroll-y
        @scrolltolower="onReachBottom"
      >
        <template v-if="!loading">
          <!-- 有数据时显示推荐内容 -->
          <recommendGroupVue 
            v-if="content && content.length > 0" 
            :content="content" 
            @clickGroup="clickGroup"
          />
          <!-- 无数据时显示空状态 -->
          <EmptyData 
            v-else
            :title="`暂无'${currentTag}'相关的讨论`"
            description="快来发起第一个讨论吧"
          />
          <!-- 加载更多 -->
          <loadMore :status="loadingStatus"></loadMore>
        </template>
        
        <!-- 加载中状态 -->
        <view v-else class="loading-container">
          <tn-loading show></tn-loading>
        </view>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<style scoped lang="scss">
.hot-detail {
  height: 100vh;
  display: flex;
  flex-direction: column;

  :deep(.refresh-content) {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .scroll-container {
    flex: 1;
    height: 100%;
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>