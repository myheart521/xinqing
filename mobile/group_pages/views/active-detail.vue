<script setup>
import navbar from '@/components/navbar.vue'
import {ref, onMounted, defineProps} from 'vue'
import {selectById2} from '@/service/api/activityController'
import {follow2, followOrNot2} from '@/service/api/activityFollowController'
import { onLoad } from '@dcloudio/uni-app'


// 接收父组件传递的数据
const props = defineProps({
  activityId: {
    type: [Number, String],
    default: ''
  }
})

const activityData = ref({})
const imageList = ref([])
const groupList = ref([])
const content = ref('')
const isParticipated = ref(false)

//活动id
const activeId = ref()

// 格式化时间数组为易读的字符串
const formatTimeArray = (timeArr) => {
  if (!timeArr || !Array.isArray(timeArr) || timeArr.length < 2) {
    return '未设置';
  }

  // 提取年月日
  const [year, month, day] = timeArr;
  let result = `${year}年${month}月${day}日`;

  // 如果有时分，添加时间
  if (timeArr.length >= 5) {
    const [, , , hour, minute] = timeArr;
    result += ` ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
  }

  return result;
}

// 检查用户是否参与活动
const checkParticipation = async (id) => {
  try {
    const res = await followOrNot2({
      id: Number(id)
    })
    if (res.code === 1) {
      isParticipated.value = res.data
    }
  } catch (error) {
    console.error('检查活动参与状态失败:', error)
  }
}

// 参与或取消参与活动
const handleParticipate = async () => {
  // try {
  const res = await follow2({
    id: Number(props.activityId || activeId.value),
    isFollow: !isParticipated.value
  })

  if (res.code === 1) {
    isParticipated.value = !isParticipated.value
    // 更新参与人数
    if (isParticipated.value) {
      activityData.value.viewUserCount = (activityData.value.viewUserCount || 0) + 1
      // 将当前用户添加到参与者列表
      const userInfo = uni.getStorageSync('userInfo')
      if (userInfo) {
        groupList.value.unshift({
          userAvatar: userInfo.avatar
        })
      }
      uni.showToast({
        title: '参与成功',
        icon: 'success'
      })
    } else {
      activityData.value.viewUserCount = Math.max(0, (activityData.value.viewUserCount || 1) - 1)
      // 从参与者列表中移除当前用户
      const userInfo = uni.getStorageSync('userInfo')
      if (userInfo) {
        const index = groupList.value.findIndex(item => item.userAvatar === userInfo.avatar)
        if (index > -1) {
          groupList.value.splice(index, 1)
        }
      }
      uni.showToast({
        title: '已取消参与',
        icon: 'success'
      })
    }
  } else {
    uni.showToast({
      title: res.msg || '操作失败',
      icon: 'none'
    })
  }
  // } catch (error) {
  //   console.error('参与活动失败:', error)
  //   uni.showToast({
  //     title: '操作失败，请稍后重试',
  //     icon: 'none'
  //   })
  // }
}

// 获取活动详情
const getActivityDetail = async (id) => {
  try {
    console.log('请求活动详情，ID:', id)
    const res = await selectById2({
      id: Number(id)
    })
    console.log('活动详情响应:', res)

    if (res.code === 1) {
      if (!res.data) {
        uni.showToast({
          title: '该活动已被删除',
          icon: 'none',
          duration: 2000
        })
        setTimeout(() => {
          uni.navigateBack()
        }, 2000)
        return
      }

      activityData.value = res.data
      content.value = res.data.content || ''

      // 处理图片列表
      imageList.value = res.data.mainImage || []

      // 处理参与者列表
      groupList.value = res.data.groupList || []

      // 检查用户是否参与
      await checkParticipation(id)
    } else {
      console.error('获取活动详情失败:', res)
      uni.showToast({
        title: '该活动不存在或已被删除',
        icon: 'none',
        duration: 2000
      })
      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 2000)
    }
  } catch (error) {
    console.error('获取活动详情失败:', error)
    uni.showToast({
      title: '获取活动信息失败，请稍后重试',
      icon: 'none',
      duration: 2000
    })
    // 延迟返回上一页
    setTimeout(() => {
      uni.navigateBack()
    }, 2000)
  }
}

// 打开地图
const openLocation = () => {
  const {latitude, longitude, address} = activityData.value
  if (!latitude || !longitude) {
    uni.showToast({
      title: '位置信息不完整',
      icon: 'none'
    })
    return
  }
  uni.openLocation({
    latitude: Number(latitude),
    longitude: Number(longitude),
    name: address || '活动地点',
    address: address || '活动地点'
  })
}

const toOtherCircle = (userId)=>{
  if (userId) {
    uni.navigateTo({
      url: `/group_pages/views/my-others-circle?userId=${userId}`
    })
  }
}

onLoad((options)=>{
  activeId.value = props.activityId || options.activityId
  console.log('页面加载，获取到ID:', activeId.value)
  if (activeId.value) {
    getActivityDetail(activeId.value)
  } else {
    uni.showToast({
      title: '活动不存在',
      icon: 'none'
    })
  }
})
// onMounted(() => {
//   const id = props.activityId || getCurrentPages()[getCurrentPages().length - 1].options.id
//   console.log('页面加载，获取到ID:', id)
//   if (id) {
//     getActivityDetail(id)
//   } else {
//     uni.showToast({
//       title: '活动ID不存在',
//       icon: 'none'
//     })
//   }
// })

// 暴露刷新方法给父组件
defineExpose({
  refresh: getActivityDetail
})
</script>

<template>
  <view class="active-detail-container">
    <!-- 导航栏 -->
    <navbar title="活动详情" back="left" home="home"></navbar>

    <!-- 活动封面图 -->
    <view class="cover-image" v-if="imageList.length > 0">
      <image :src="imageList[0]" mode="aspectFill"></image>
      <view class="cover-gradient"></view>
    </view>

    <!-- 活动内容 -->
    <view class="content-container">
      <!-- 活动标题和标签 -->
      <view class="activity-header">
        <view class="activity-title">{{ activityData.title || '活动标题' }}</view>
        <view class="activity-tag" v-if="activityData.label"
              :class="[`tn-${activityData.color || 'blue'}-light_bg`]">
          <text class="tag-prefix">#</text>
          <text>{{ activityData.label }}</text>
        </view>
            </view>

      <!-- 活动详情 -->
      <view class="activity-detail">
        <rich-text :nodes="content" class="activity-content"></rich-text>
          </view>

      <!-- 活动时间地点 -->
      <view class="activity-info">
        <view class="info-item">
          <tn-icon name="time" size="36rpx" color="#5677fc"></tn-icon>
          <text class="info-text">开始：{{ formatTimeArray(activityData.startTime) }}</text>
        </view>
        <view class="info-item">
          <tn-icon name="time-fill" size="36rpx" color="#5677fc"></tn-icon>
          <text class="info-text">结束：{{ formatTimeArray(activityData.endTime) }}</text>
        </view>
        <view class="info-item">
          <tn-icon name="calendar" size="36rpx" color="#5677fc"></tn-icon>
          <text class="info-text">创建于：{{ formatTimeArray(activityData.createTime) }}</text>
        </view>
        <view class="info-item" @click="openLocation">
          <tn-icon name="location" size="36rpx" color="#5677fc"></tn-icon>
          <text class="info-text location-text">{{ activityData.address || '未设置' }}</text>
        </view>
      </view>

      <!-- 参与者 -->
      <view class="participants-section">
        <view class="section-title">
          <text>活动参与者</text>
          <text class="count">({{ activityData.viewUserCount || 0 }}人)</text>
                </view>

        <view class="participants-list" v-if="groupList.length > 0">
          <view class="participant-item" v-for="(item, index) in groupList.slice(0, 12)" :key="index">
            <image class="avatar" :src="item.userAvatar" mode="aspectFill" @click="toOtherCircle(item.id)"></image>
            <text class="tn-gray-dark_text">{{item.userName}}</text>
              </view>
          <view class="more-participants" v-if="groupList.length > 12">
            <tn-icon name="more-circle" size="50rpx" color="#999"></tn-icon>
          </view>
        </view>

        <view class="empty-participants" v-else>
          <text>暂无参与者</text>
            </view>
          </view>
        </view>

    <!-- 底部操作栏 -->
    <view class="footer-action tn-safe-area-inset-bottom">
      <view class="organizer">
        <image
            class="organizer-avatar"
            :src="activityData.userAvatar"
            mode="aspectFill"
            @error="e => e.target.src = 'https://assets.example.invalid/placeholder.png'"
        ></image>
        <text class="organizer-label">发起人: {{ activityData.userName || '未知' }}</text>
          </view>

      <view class="action-buttons">
        <tn-button
            class="participate-btn"
            :bg-color="isParticipated ? '#f8f9fa' : '#5677fc'"
            :color="isParticipated ? '#333' : '#fff'"
            shape="round"
            shadow
            @click="handleParticipate"
        >
          <tn-icon :name="isParticipated ? 'check' : 'add'" :color="isParticipated ? '#333' : '#fff'"
                   class="tn-pr-xs"></tn-icon>
          <text>{{ isParticipated ? '已参与' : '参与活动' }}</text>
        </tn-button>

        <view class="location-btn" @click="openLocation">
          <tn-icon name="location-fill" size="40rpx" color="#5677fc"></tn-icon>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.active-detail-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* 封面图样式 */
.cover-image {
  position: relative;
  width: 100%;
  height: 400rpx;

  image {
  width: 100%;
  height: 100%;
  }

  .cover-gradient {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100rpx;
    background: linear-gradient(to bottom, rgba(248, 249, 250, 0), #f8f9fa);
  }
}

/* 内容区样式 */
.content-container {
  flex: 1;
  padding: 30rpx;
  margin-top: -50rpx;
  background-color: #f8f9fa;
  border-radius: 30rpx 30rpx 0 0;
  position: relative;
  z-index: 2;
}

/* 活动标题和标签 */
.activity-header {
  margin-bottom: 30rpx;

  .activity-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .activity-tag {
    display: inline-block;
    padding: 8rpx 20rpx;
    border-radius: 10rpx;
    font-size: 26rpx;

    .tag-prefix {
      margin-right: 6rpx;
      color: #5677fc;
    }
  }
}

/* 活动详情 */
.activity-detail {
  margin-bottom: 30rpx;

  .activity-content {
    font-size: 28rpx;
    line-height: 1.6;
    color: #666;
  }
}

/* 活动信息 */
.activity-info {
  margin-bottom: 40rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .info-text {
      margin-left: 15rpx;
      font-size: 28rpx;
      color: #333;
    }

    .location-text {
      color: #5677fc;
    }
  }
}

/* 参与者部分 */
.participants-section {
  margin-bottom: 130rpx;

  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;

    .count {
      font-size: 28rpx;
      color: #999;
      font-weight: normal;
      margin-left: 10rpx;
    }
  }

  .participants-list {
    display: flex;
    flex-wrap: wrap;

    .participant-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-right: 20rpx;
      margin-bottom: 20rpx;

      .avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        border: 2rpx solid #eee;
      }
    }

    .more-participants {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 80rpx;
      height: 80rpx;
      background-color: #f0f2f5;
      border-radius: 50%;
    }
  }

  .empty-participants {
    padding: 30rpx;
    text-align: center;
    color: #999;
    font-size: 28rpx;
  }
}

/* 底部操作栏 */
.footer-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 10;

  .organizer {
    display: flex;
    flex-direction: column;
    align-items: center;

    .organizer-avatar {
  width: 70rpx;
  height: 70rpx;
      border-radius: 50%;
      background-color: #f0f2f5;
    }

    .organizer-label {
      font-size: 22rpx;
      color: #999;
      margin-top: 5rpx;
      max-width: 120rpx;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
    }
  }

  .action-buttons {
    display: flex;
    align-items: center;

    .participate-btn {
      margin-right: 20rpx;
    }

    .location-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 80rpx;
      height: 80rpx;
      background-color: #f0f2f5;
      border-radius: 50%;
    }
  }
}
</style>