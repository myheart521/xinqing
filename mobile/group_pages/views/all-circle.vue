<script setup>
import navbar from '@/components/navbar.vue'
import {ref, onMounted} from 'vue'
import {getAll3} from '@/service/api/circleController'

// 圈子列表数据
const circleList = ref([])
const loading = ref(false)

// 获取圈子列表
const getCircleList = async () => {
  loading.value = true
  try {
    const res = await getAll3()

    if (res.code === 1) {
      circleList.value = res.data.map(item => ({
        id: item.id,
        type: 'image',
        name: item.name,
        text: `${item.follow || 0}人关注`,
        url: item.url || 'https://assets.example.invalid/placeholder.png',
        content: item.content,
        createTime: item.createTime
      }))
    } else {
      uni.showToast({
        title: res.message || '获取圈子列表失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取圈子列表失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 跳转到圈子详情
const tn = (circleId) => {
  console.log('跳转到圈子:', circleId)
  uni.navigateTo({
    url: `/group_pages/views/circle-detail?id=${circleId}`
  })
}

onMounted(() => {
  getCircleList()
})
</script>

<template>
  <view class="all_circle_container">
    <navbar title="圈子列表"></navbar>
    <!--标题-->
    <view class="tn-m">
      <tn-title size="xl" title="所有圈子" mode="vLine" assist-color="tn-gradient-bg__cool-6"/>
    </view>

    <!--所有圈子列表-->
    <view class="tn-flex tn-flex-wrap tn-mb">
      <block v-for="(item, index) in circleList" :key="index">
        <view class="circle-item" style="width: 33.3%;" @click="tn(item.id)">
          <view class="tn-flex tn-flex-column tn-flex-center-center">
            <view class="tn-radius tn-p-sm">
              <view class="image-pic" :style="'background-image:url('+ item.url +')'">
                <view class="image-circle">
                </view>
              </view>
              <view class="tn-text-center tn-text-bold tn-pt-xs">{{ item.name }}</view>
              <view class="tn-text-center tn-text-xs tn-grey-dark_text tn-pt-xs">
                {{ item.text }}
              </view>
            </view>
          </view>
        </view>
      </block>
    </view>

    <!-- 加载中状态 -->
    <view v-if="loading" class="loading-container">
      <tn-loading show></tn-loading>
    </view>
  </view>
</template>

<style scoped lang="scss">
.all_circle_container {
  min-height: 100vh;
  background-color: #fff;
}

/* 圈子列表样式 */
.circle-item {
  margin-bottom: 30rpx;
}

/* 博主头像 start*/
.image-circle {
  width: 190rpx;
  height: 190rpx;
  position: relative;
  border-radius: 12rpx;
  overflow: hidden;
}

.image-pic {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  border-radius: 12rpx;
  width: 190rpx;
  height: 190rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>