<script lang="ts" setup>
import {ref, onMounted, reactive} from 'vue'
import navbar from '@/components/navbar.vue'
import {getDefaultTest} from "@/service/api/modulesController.ts";
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import loadMoreVue from '@/components/load/loadMore.vue'
import pulldownRefresh from '@/components/load/pulldownRefresh.vue'

// 根据后端返回的数据结构调整接口
interface TestItem {
  id: number
  title: string
  introductions: string
  src: string
}

const testList = ref<TestItem[]>([])
const loading = ref(false)
const noMoreData = ref(false)
const loadStatus = ref(0) // 0加载前，1加载中，2没有更多了
const pulldownRef = ref(null)
const enableScroll = ref(true)

// 分页参数
const pagination = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0
})

// 获取测试列表数据
const fetchTestList = async (isRefresh = false) => {
  if (loading.value) return

  loading.value = true

  if (isRefresh) {
    loadStatus.value = 0
    pagination.pageNo = 1
    testList.value = []
  } else {
    loadStatus.value = 1
  }

  try {
    // 使用 getDefaultTest 接口获取数据
    const res = await getDefaultTest({
      pageDTO: {
        current: pagination.pageNo,
        size: pagination.pageSize
      }
    })

    if (res && res.code === 1 && res.data) {
      // 根据后端返回的数据结构调整处理逻辑
      const records = Array.isArray(res.data) ? res.data : (res.data.records || [])

      // 如果是第一页，直接赋值；否则追加数据
      if (pagination.pageNo === 1) {
        testList.value = records
      } else {
        testList.value = [...testList.value, ...records]
      }

      // 如果返回的是分页对象，则使用total属性；否则使用数组长度
      pagination.total = res.data.total || records.length

      // 判断是否还有更多数据
      if (testList.value.length >= pagination.total || records.length < pagination.pageSize) {
        noMoreData.value = true
        loadStatus.value = 2 // 没有更多数据
      } else {
        noMoreData.value = false
        loadStatus.value = 0 // 可以继续加载
      }
    } else {
      // 如果接口返回错误，使用模拟数据（开发阶段使用）
      if (pagination.pageNo === 1) {
        useSimulatedData()
      }
      uni.showToast({
        title: '获取测试列表失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取测试列表出错:', error)
    // 如果接口出错，使用模拟数据（开发阶段使用）
    if (pagination.pageNo === 1) {
      useSimulatedData()
    }
  } finally {
    loading.value = false

    // 如果是下拉刷新，结束下拉刷新状态
    if (isRefresh && pulldownRef.value) {
      // @ts-ignore
      pulldownRef.value.endPulldownRefresh()
    }
  }
}

// 模拟数据（开发阶段使用）
const useSimulatedData = () => {
  testList.value = [
    {
      id: 1,
      title: "性格测试",
      introductions: "国际标准个人mbti性格测试",
      src: "https://assets.example.invalid/placeholder.png"
    },
    {
      id: 2,
      title: "抑郁症筛查测试",
      introductions: "评估您是否有抑郁症倾向，及早发现心理问题",
      src: "https://assets.example.invalid/placeholder.png"
    },
    {
      id: 3,
      title: "焦虑自评量表",
      introductions: "测量您的焦虑水平，了解自己的心理状态",
      src: "https://assets.example.invalid/placeholder.png"
    },
  ]
  pagination.total = testList.value.length
  noMoreData.value = true
  loadStatus.value = 2
}

// 下拉刷新
const onRefresh = () => {
  fetchTestList(true)
}

// 加载更多
const loadMore = () => {
  if (noMoreData.value || loading.value) return

  pagination.pageNo++
  fetchTestList()
}

// 设置是否可以滚动
const setEnableScroll = (enable) => {
  enableScroll.value = enable
}

// 跳转到测试题页面
const goToTest = (id: number) => {
  console.log("跳转到测试题页面，id:", id)
  uni.navigateTo({
    url: `/function_pages/views/test-question?id=${id}`
  })
}

onMounted(() => {
  fetchTestList(true)
})
</script>

<template>
  <view class="container">
    <navbar title="测试题列表"/>

    <!-- 下拉刷新组件 -->
    <pulldownRefresh
        ref="pulldownRef"
        @refresh="onRefresh"
        @setEnableScroll="setEnableScroll"
    >
      <!-- 测试列表 -->
      <view class="test-list" :style="{ 'overflow': enableScroll ? 'auto' : 'hidden' }">
        <view v-if="loading && testList.length === 0" class="loading-container">
          <tn-loading></tn-loading>
        </view>

        <tn-empty v-else-if="testList.length === 0" text="暂无测试题"/>

        <view
            v-for="item in testList"
            :key="item.id"
            class="test-item-wrapper tn-shadow-sm"
            @click="goToTest(item.id)"
        >
          <view class="test-item tn-flex">
            <image
                class="test-image"
                :src="item.src"
                mode="aspectFill"
            />
            <view class="test-info tn-flex-1 tn-flex-column">
              <view class="title tn-text-lg tn-text-bold">{{ item.title }}</view>
              <view class="intro tn-text-sm tn-color-gray">{{ item.introductions }}</view>
            </view>
            <TnIcon name="right" color="#5677fc"/>
          </view>
        </view>

        <!-- 加载更多组件 -->
        <loadMoreVue :status="loadStatus" @tap="loadMore" v-if="testList.length > 0"></loadMoreVue>
      </view>
    </pulldownRefresh>
  </view>
</template>

<style lang="scss" scoped>
.container {
  padding: 30rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.test-list {
  padding-bottom: 30rpx;
}

.loading-container {
  padding: 40rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.test-item-wrapper {
  margin-bottom: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
  transition: transform 0.2s;

  &:active {
    transform: scale(0.98);
  }
}

.test-item {
  width: 100%;
  align-items: center;
  padding: 24rpx;

  .test-image {
    width: 120rpx;
    height: 120rpx;
    border-radius: 12rpx;
    margin-right: 20rpx;
    background-color: #e7f1fd;
  }

  .test-info {
    .title {
      margin-bottom: 10rpx;
      color: #333;
    }

    .intro {
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
      font-size: 26rpx;
      color: #666;
    }
  }
}

.load-more {
  margin-top: 30rpx;
  padding: 20rpx 0;
}
</style> 