<script setup>
import {ref, onMounted} from 'vue';
import {getPcc} from '@/service/api/introductionController';
import TnAvatar from '@tuniao/tnui-vue3-uniapp/components/avatar/src/avatar.vue';

// 心理咨询中心数据
const pccList = ref([]);
const loading = ref(true);

// 获取心理咨询中心数据
const fetchPccData = async () => {
  try {
    loading.value = true;
    const res = await getPcc({});
    if (res.code === 1) {
      pccList.value = res.data || [];
    } else {
      uni.showToast({
        title: res.msg || '获取数据失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取心理咨询中心数据失败:', error);
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

// 打开地图
const openLocation = (item) => {
  // 这里可以添加打开地图的逻辑，如果有经纬度的话
  uni.showToast({
    title: `查看${item.school}地图`,
    icon: 'none'
  });
};

// 拨打电话
const callPhone = (phoneNumber) => {
  uni.makePhoneCall({
    phoneNumber,
    success: () => {
      console.log('拨打电话成功');
    },
    fail: (err) => {
      console.error('拨打电话失败:', err);
    }
  });
};

// 发送邮件
const sendEmail = (email) => {
  // 在小程序中可能无法直接发送邮件，这里只是复制邮箱地址
  uni.setClipboardData({
    data: email,
    success: () => {
      uni.showToast({
        title: '邮箱已复制',
        icon: 'success'
      });
    }
  });
};

// 查看详情
const viewDetail = (id) => {
  if (!id) {
    uni.showToast({
      title: '无效的ID',
      icon: 'none'
    });
    return;
  }
  
  uni.navigateTo({
    url: `/pages/index/components/views/introduction/introduction-detail?id=${id}`,
    success: () => {
      console.log('跳转到详情页成功');
    },
    fail: (err) => {
      console.error('跳转到详情页失败:', err);
      uni.showToast({
        title: '跳转失败',
        icon: 'none'
      });
    }
  });
};

onMounted(() => {
  fetchPccData();
});
</script>

<template>
  <view class="introduction-container">
    <view class="tn-section__title tn-section__title--primary tn-mt">
      <view class="tn-section__title__value">
        <text class="tn-section__title__value--prefix"></text>
        <text class="tn-section__title__value--text">心理咨询中心</text>
      </view>
      <view class="tn-section__title__sub-value">Psychological Counseling Center</view>
    </view>

    <!-- 刷新按钮 -->
    <view class="tn-flex tn-flex-center-center tn-mt">
      <tn-button
          size="sm"
          padding="12rpx 40rpx"
          bg-color="#f0f0f0"
          @tap="fetchPccData"
      >
        <tn-icon name="refresh" color="#666" size="40rpx"></tn-icon>
        <text class="tn-ml-xs">刷新数据</text>
      </tn-button>
    </view>
    <!-- 加载中 -->
    <view v-if="loading" class="tn-flex tn-flex-center-center tn-py-xl">
      <tn-loading></tn-loading>
    </view>


    <!-- 数据为空 -->
    <view v-else-if="pccList.length === 0" class="tn-flex tn-flex-center-center tn-py-xl">
      <view class="tn-gray_text">暂无数据</view>
    </view>

    <!-- 心理咨询中心列表 -->
    <view v-else class="pcc-list">
      <view
          v-for="(item, index) in pccList"
          :key="index"
          class="pcc-item tn-shadow-sm tn-white_bg tn-radius tn-mb"
      >
        <view class="pcc-item__header tn-flex tn-flex-row-between tn-flex-col-center tn-p">
          <view class="tn-flex tn-flex-row-center">
            <TnAvatar
                :url="item.src"
                size="lg"
                shape="round"
            />
            <view class="tn-ml-sm">
              <view class="tn-text-lg tn-text-bold">{{ item.school }}</view>
              <view class="tn-gray_text tn-text-sm">{{ item.province }}</view>
            </view>
          </view>
        </view>

        <view class="pcc-item__content tn-p">
          <!-- 地址信息 -->
          <view class="info-item tn-flex tn-flex-row-center tn-mb-sm" @tap="openLocation(item)">
            <tn-icon name="location" size="40rpx" color="#5677fc"></tn-icon>
            <view class="tn-ml-sm tn-gray_text">{{ item.location }}</view>
          </view>

          <!-- 电话信息 -->
          <view class="info-item tn-flex tn-flex-row-center tn-mb-sm" @tap="callPhone(item.phoneNumber)">
            <tn-icon name="phone" size="40rpx" color="#ff7900"></tn-icon>
            <view class="tn-ml-sm tn-gray_text">{{ item.phoneNumber }}</view>
          </view>

          <!-- 邮箱信息 -->
          <view class="info-item tn-flex tn-flex-row-center" @tap="sendEmail(item.email)">
            <tn-icon name="email" size="40rpx" color="#00aaff"></tn-icon>
            <view class="tn-ml-sm tn-gray_text">{{ item.email }}</view>
          </view>
        </view>

        <view class="pcc-item__footer tn-p tn-flex tn-flex-row-between">
          <tn-button
              size="sm"
              padding="12rpx 30rpx"
              bg-color="#5677fc"
              @tap="callPhone(item.phoneNumber)"
          >
            <tn-icon name="phone" color="#fff" size="30rpx"></tn-icon>
            <text class="tn-ml-xs tn-white_text">联系咨询</text>
          </tn-button>

          <tn-button
              size="sm"
              padding="12rpx 30rpx"
              bg-color="#00aaff"
              @tap="openLocation(item)"
              custom-class="tn-ml-xs"
          >
            <tn-icon name="location" color="#fff" size="30rpx"></tn-icon>
            <text class="tn-ml-xs tn-white_text">查看地图</text>
          </tn-button>
          
          <tn-button
              size="sm"
              padding="12rpx 30rpx"
              bg-color="#19be6b"
              @tap="viewDetail(item.id)"
              custom-class="tn-ml-xs"
          >
            <tn-icon name="info-circle" color="#fff" size="30rpx"></tn-icon>
            <text class="tn-ml-xs tn-white_text">查看详情</text>
          </tn-button>
        </view>
      </view>
    </view>


  </view>
</template>

<style scoped lang="scss">
.introduction-container {
  padding: 30rpx;
}

.pcc-list {
  margin-top: 30rpx;
}

.pcc-item {
  margin-bottom: 30rpx;
  overflow: hidden;

  &__header {
    border-bottom: 1px solid #f5f5f5;
  }

  &__content {
    padding: 20rpx 30rpx;
  }

  &__footer {
    border-top: 1px solid #f5f5f5;
    padding: 20rpx 30rpx;
  }
}

.info-item {
  padding: 10rpx 0;

  &:active {
    opacity: 0.7;
  }
}

// 标题样式
.tn-section__title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 20rpx 0;

  &--primary {
    .tn-section__title__value {
      &--prefix {
        background-color: #5677fc;
      }
    }
  }

  &__value {
    display: flex;
    align-items: center;

    &--prefix {
      width: 10rpx;
      height: 35rpx;
      border-radius: 10rpx;
      margin-right: 10rpx;
    }

    &--text {
      font-size: 34rpx;
      font-weight: bold;
    }
  }

  &__sub-value {
    font-size: 24rpx;
    color: #aaa;
    margin-top: 5rpx;
  }
}
</style>