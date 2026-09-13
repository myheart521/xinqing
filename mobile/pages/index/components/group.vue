<script>
import groupNavVue from './views/group/group-nav.vue';
import excellentGroupVue from './views/group/excellent-group.vue';
import groupDescribeVue from './views/group/group-describe.vue';
import hotTopicVue from './views/group/hot-topic.vue';
import recommendGroupVue from '@/components/recommend-group.vue'
import imagesHttp from "/static/constant/imag_http.json"
import circleActive from "./views/group/circle-active.vue"
import TnSuspendButton from 'tnuiv3p-tn-suspend-button/index.vue'
import landscape from './views/group/landscape.vue'
import nearby from './views/group/nearby.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'
import {queryTagTop} from '@/service/api/tagController'
import {getTop} from '@/service/api/circleController'
import {searchByContent} from '@/service/api/blogController'

export default {
  components: {
    groupNavVue,
    excellentGroupVue,
    groupDescribeVue,
    hotTopicVue,
    recommendGroupVue,
    circleActive,
    TnSuspendButton,
    landscape,
    nearby,
    pulldownRefresh
  },

  data() {
    return {
      //占位
      androidHeight: false,
      images: [imagesHttp.anime, imagesHttp.beautifulGirl, imagesHttp.historic],
      content: [],
      currentTabIndex: 0,
      showOverlay: false,
      hasLoadedData: {
        discover: false,  // 发现圈子页面
        activity: false,  // 活动页面
        nearby: false     // 附近页面
      },

      // 分页相关
      page: 1,
      pageSize: 10,
      loadingStatus: 0, // 0加载前，1加载中，2没有更多了
      noMoreData: false, // 是否没有更多数据

      // 其他数据
      refreshRef: null,
      enableScroll: true,
      hotTags: [],
      hotCircles: [],
      circleActiveRef: null,
      nearbyRef: null
    }
  },

  mounted() {
    // 初始化时只加载第一个标签页的数据
    if (this.currentTabIndex === 0 && !this.hasLoadedData.discover) {
      this.resetLoadMore()
      this.getBlogList(true)
      // 获取热门标签
      this.getHotTags()
      // 获取热门圈子
      this.getHotCircles()
    }
    // #ifdef APP-ANDROID
    this.androidHeight = true
    // #endif

  },

  methods: {
    changeTab(index) {
      this.currentTabIndex = index
      // 切换标签页时，只有在没有数据的情况下才加载
      switch (index) {
        case 0:
          if (!this.hasLoadedData.discover) {
            this.resetLoadMore()
            this.getBlogList(true)
          }
          break
        case 1:
          if (!this.hasLoadedData.activity && this.$refs.circleActiveRef) {
            this.$refs.circleActiveRef.resetAndRefresh()
          }
          break
        case 2:
          // 切换到附近页面时，始终刷新数据
          if (this.$refs.nearbyRef) {
            this.$refs.nearbyRef.refresh()
          }
          break
      }
    },

    clickGroup(item) {
      console.log('点击了圈子', item);
      uni.navigateTo({
        url: `/group_pages/views/group-detail?id=${item.id}`
      })
    },

    clickSuspend() {
      this.showOverlay = true
      console.log("应该打开遮罩层")
    },

    // 获取帖子列表
    async getBlogList(isRefresh = false) {
      if (this.loadingStatus === 1) {
        console.log('正在加载中，请稍后再试')
        return
      }
      this.loadingStatus = 1
      try {
        console.log('获取帖子列表,page:', this.page)
        const res = await searchByContent({
          text: '',
          current: this.page
        })

        if (res.code === 1) {
          // 确保res.data是数组，如果是null则转为空数组
          const dataArray = Array.isArray(res.data) ? res.data : []

          if (isRefresh) {
            this.content = dataArray
          } else {
            this.content = [...this.content, ...dataArray]
          }

          // 标记发现页面数据已加载
          if (isRefresh) {
            this.hasLoadedData.discover = true
          }

          // 判断是否还有更多数据
          if (!dataArray.length) {
            console.log('没有更多数据了，数据长度: 0')
            this.noMoreData = true
          } else {
            console.log('，页码加1，数据长度:', dataArray.length)
            this.noMoreData = false
            this.page++
          }
        } else {
          uni.showToast({
            title: res.msg || '获取数据失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('获取帖子列表失败:', error)
        uni.showToast({
          title: '获取数据失败',
          icon: 'none'
        })
      } finally {
        this.loadingStatus = 0
      }
    },

    // 重置加载状态
    resetLoadMore() {
      this.page = 1
      this.loadingStatus = 0
      this.noMoreData = false
    },

    // 获取热门标签
    async getHotTags() {
      try {
        const res = await queryTagTop()
        if (res.code === 1) {
          this.hotTags = res.data || []
        } else {
          console.error('获取热门标签失败:', res.msg)
        }
      } catch (error) {
        console.error('获取热门标签失败:', error)
      }
    },

    // 获取热门圈子
    async getHotCircles() {
      try {
        const res = await getTop()
        if (res.code === 1) {
          this.hotCircles = res.data || []
        } else {
          console.error('获取热门圈子失败:', res.msg)
        }
      } catch (error) {
        console.error('获取热门圈子失败:', error)
      }
    },

    // 处理下拉刷新
    async onRefresh() {
      try {
        console.log('开始刷新数据，当前标签页:', this.currentTabIndex)

        switch (this.currentTabIndex) {
          case 0:
            // 发现圈子页面
            this.resetLoadMore()
            await Promise.all([
              this.getBlogList(true),
              this.getHotTags(),
              this.getHotCircles()
            ])
            this.hasLoadedData.discover = true
            break
          case 1:
            // 活动页面
            if (this.$refs.circleActiveRef) {
              await this.$refs.circleActiveRef.refresh()
            }
            this.hasLoadedData.activity = true
            break
          case 2:
            // 附近页面
            if (this.$refs.nearbyRef) {
              await this.$refs.nearbyRef.refresh()
            }
            this.hasLoadedData.nearby = true
            break
        }

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
        if (this.$refs.refreshRef) {
          this.$refs.refreshRef.endPulldownRefresh()
        }
      }
    },

    // 加载更多
    async loadMore() {
      console.log('执行加载更多方法，当前标签页:', this.currentTabIndex)

      if (this.loadingStatus === 1) {
        console.log('正在加载中，请稍后再试')
        return
      }

      switch (this.currentTabIndex) {
        case 0:
          if (this.noMoreData) {
            uni.showToast({
              title: '没有更多数据了',
              icon: 'none'
            })
            return
          }
          await this.getBlogList()
          break
        case 1:
          if (this.$refs.circleActiveRef) {
            await this.$refs.circleActiveRef.loadMore()
          }
          break
        case 2:
          if (this.$refs.nearbyRef) {
            await this.$refs.nearbyRef.loadMore()
          }
          break
      }
    },

    // 设置是否可以滚动
    setEnableScroll(value) {
      this.enableScroll = value
    }
  }
}
</script>

<template>
  <view class="group">
    <!-- 导航栏 -->
    <groupNavVue @changeTab="changeTab"></groupNavVue>
    <!-- 下拉刷新包裹器 -->
    <pulldownRefresh
        ref="refreshRef"
        :top="0"
        :threshold="80"
        @refresh="onRefresh"
        @setEnableScroll="setEnableScroll"
    >
      <scroll-view
          class="scroll-container tn-u-safe-area--more"
          scroll-y
      >
        <!-- 发现  圈子 -->
        <view v-if="currentTabIndex === 0">

          <view style="height: 69px"></view>

          <!-- 圈子的大概状态 -->
          <groupDescribeVue></groupDescribeVue>

          <!-- 热门话题 -->
          <hotTopicVue :tags="hotTags"></hotTopicVue>

          <!-- 精选圈子 -->
          <excellentGroupVue :circles="hotCircles"></excellentGroupVue>

          <!-- 推荐内容 -->
          <recommendGroupVue
              :content="content"
              @clickGroup="clickGroup"
          ></recommendGroupVue>

          <!-- 加载更多按钮 -->
          <view class="load-more-container">
            <tn-button v-if="!noMoreData && content.length > 0"
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
            <view v-else-if="noMoreData && content.length > 0" class="no-more-data tn-flex tn-flex-center-center">
              <tn-icon name="info-circle" color="#aaa" size="28rpx"></tn-icon>
              <text class="tn-gray_text tn-ml-xs">没有更多了</text>
            </view>
          </view>
        </view>
        <!-- 活动页面  -->
        <view v-if="currentTabIndex === 1">
          <circleActive ref="circleActiveRef"/>
        </view>
        <!-- 附近 -->
        <view v-if="currentTabIndex === 2">
          <nearby ref="nearbyRef"/>
        </view>
      </scroll-view>
    </pulldownRefresh>
    <!--悬浮按钮-->
    <TnSuspendButton icon="edit" top="75%" right="30" @click="clickSuspend"/>
    <!--压缩屏-->
    <view>
      <tn-overlay
          :duration="250"
          :opacity="0.4"
          v-model:show="showOverlay"
          @click="showOverlay = false"
      >
        <landscape/>
      </tn-overlay>
    </view>
  </view>
</template>

<style lang="scss">
.group {
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

  // 加载更多容器
  .load-more-container {
    margin: 40rpx 0 60rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;

    .btn-content {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .no-more-data {
      padding: 15rpx 30rpx;
      font-size: 26rpx;
      color: #999;
      background-color: #f5f7fa;
      border-radius: 30rpx;
    }
  }
}
</style>