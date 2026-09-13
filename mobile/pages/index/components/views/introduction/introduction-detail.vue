<script setup>
import navbar from "@/components/navbar.vue"
import { getPcc1 } from '@/service/api/introductionController';
import TnAvatar from '@tuniao/tnui-vue3-uniapp/components/avatar/src/avatar.vue';
import TnTabs from '@tuniao/tnui-vue3-uniapp/components/tabs/src/tabs.vue';
import TnTabsItem from '@tuniao/tnui-vue3-uniapp/components/tabs/src/tabs-item.vue';
import {onLoad} from '@dcloudio/uni-app'
import websocket from "@/utils/websocket";
import { ref } from 'vue';

// 详情数据
const detailData = ref({
  phoneNumber: '',
  email: 'demo@example.invalid',
  location: '',
  publicAccount: '',
  method: '',
  school: '',
  description: '',
  province: '',
  src: '',
  doctorList: [],
  timeSlots: []
});
const myWebsocket = ref(null);
const loading = ref(true);
const currentTab = ref(0);
const tabList = ['中心介绍', '咨询师', '咨询时间'];

// 获取详情数据
const fetchDetailData = async (id) => {
  try {
    loading.value = true;
    const res = await getPcc1({ id });
    if (res.code === 1) {
      detailData.value = res.data || {};
      // 处理描述文本的换行
      if (detailData.value.description) {
        detailData.value.description = detailData.value.description.replace(/\n/g, '<br>');
      }
    } else {
      uni.showToast({
        title: res.msg || '获取详情失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取心理咨询中心详情失败:', error);
    uni.showToast({
      title: '获取详情失败',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
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

// 复制文本
const copyText = (text, tipText = '复制成功') => {
  uni.setClipboardData({
    data: text,
    success: () => {
      uni.showToast({
        title: tipText,
        icon: 'success'
      });
    }
  });
};

// 切换标签页
const changeTab = (index) => {
  currentTab.value = index;
};

// 与咨询师交流
const chatWithDoctor = (doctor) => {
  if (!doctor) {
    uni.showToast({
      title: '咨询师信息不完整',
      icon: 'none'
    });
    return;
  }
  
  // 跳转到聊天页面
  uni.navigateTo({
    url: `/mine_pages/teacher-message-detail?receiverId=${doctor.userId}&name=${doctor.doctorName}&avatar=${doctor.photo}`,
    success: () => {
      console.log('跳转到聊天页面成功');
    },
    fail: (err) => {
      console.error('跳转到聊天页面失败:', err);
      uni.showToast({
        title: '跳转失败',
        icon: 'none'
      });
    }
  });
};

// 页面加载时获取详情
onLoad((options) => {
  if (options.id) {
    fetchDetailData(options.id);
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }
  myWebsocket.value = new websocket({receiverId:options.id})
});
</script>

<template>
  <view class="detail-container">
    <navbar :title="detailData.school || '心理咨询中心'" back="left" home="home"></navbar>
    
    <!-- 加载中 -->
    <view v-if="loading" class="tn-flex tn-flex-center-center tn-py-xl">
      <tn-loading></tn-loading>
    </view>
    
    <!-- 详情内容 -->
    <view v-else class="detail-content">
      <!-- 头部信息 -->
      <view class="header tn-white_bg tn-shadow-sm tn-radius tn-mb">
        <view class="header-top tn-flex tn-flex-row-between tn-flex-col-center tn-p">
          <view class="tn-flex tn-flex-row-center">
            <TnAvatar
                :url="detailData.src"
                size="xl"
                shape="round"
            />
            <view class="tn-ml-sm">
              <view class="tn-text-xl tn-text-bold">{{ detailData.school }}</view>
              <view class="tn-gray_text tn-text-sm">{{ detailData.province }}</view>
            </view>
          </view>
        </view>
        
        <view class="header-info tn-p">
          <!-- 地址信息 -->
          <view class="info-item tn-flex tn-flex-row-center tn-mb-sm">
            <tn-icon name="location" size="40rpx" color="#5677fc"></tn-icon>
            <view class="tn-ml-sm tn-gray_text">{{ detailData.location }}</view>
          </view>
          
          <!-- 电话信息 -->
          <view class="info-item tn-flex tn-flex-row-center tn-mb-sm" @tap="callPhone(detailData.phoneNumber)">
            <tn-icon name="phone" size="40rpx" color="#ff7900"></tn-icon>
            <view class="tn-ml-sm tn-gray_text">{{ detailData.phoneNumber }}</view>
          </view>
          
          <!-- 邮箱信息 -->
          <view class="info-item tn-flex tn-flex-row-center tn-mb-sm" @tap="copyText(detailData.email, '邮箱已复制')">
            <tn-icon name="email" size="40rpx" color="#00aaff"></tn-icon>
            <view class="tn-ml-sm tn-gray_text">{{ detailData.email }}</view>
          </view>
          
          <!-- 公众号信息 -->
          <view class="info-item tn-flex tn-flex-row-center" @tap="copyText(detailData.publicAccount, '公众号已复制')">
            <tn-icon name="wechat" size="40rpx" color="#19be6b"></tn-icon>
            <view class="tn-ml-sm tn-gray_text">{{ detailData.publicAccount }}</view>
          </view>
        </view>
      </view>
      
      <!-- 标签页 -->
      <view class="tabs-container tn-white_bg tn-shadow-sm tn-radius">
        <TnTabs v-model="currentTab" @change="changeTab">
          <TnTabsItem v-for="(item, index) in tabList" :key="index" :title="item" />
        </TnTabs>
        
        <!-- 中心介绍 -->
        <view v-if="currentTab === 0" class="tab-content">
          <view class="tn-section__title tn-section__title--primary tn-mt">
            <view class="tn-section__title__value">
              <text class="tn-section__title__value--prefix"></text>
              <text class="tn-section__title__value--text">中心介绍</text>
            </view>
          </view>
          
          <view class="description tn-p">
            <rich-text :nodes="detailData.description"></rich-text>
          </view>
          
          <view class="tn-section__title tn-section__title--primary tn-mt">
            <view class="tn-section__title__value">
              <text class="tn-section__title__value--prefix"></text>
              <text class="tn-section__title__value--text">预约方式</text>
            </view>
          </view>
          
          <view class="method tn-p">
            {{ detailData.method }}
          </view>
        </view>
        
        <!-- 咨询师 -->
        <view v-if="currentTab === 1" class="tab-content">
          <view class="tn-section__title tn-section__title--primary tn-mt">
            <view class="tn-section__title__value">
              <text class="tn-section__title__value--prefix"></text>
              <text class="tn-section__title__value--text">咨询师团队</text>
            </view>
          </view>
          
          <view class="doctor-list">
            <view 
              v-for="(doctor, index) in detailData.doctorList" 
              :key="index" 
              class="doctor-item tn-shadow-sm tn-radius tn-mb"
            >
              <view class="doctor-header tn-flex tn-flex-row-between tn-flex-col-center tn-p">
                <view class="tn-flex tn-flex-row-center">
                  <TnAvatar
                      :url="doctor.photo"
                      size="lg"
                      shape="round"
                  />
                  <view class="tn-ml-sm">
                    <view class="tn-text-lg tn-text-bold">{{ doctor.doctorName }}</view>
                    <view class="tn-gray_text tn-text-sm">{{ doctor.title }}</view>
                  </view>
                </view>
                <view class="rating tn-ml-xs">
                  <view class="tn-gray_text tn-text-sm">评分</view>
                  <view class="rating-value tn-text-lg tn-text-bold">{{ doctor.rating }}</view>
                </view>
              </view>
              
              <view class="doctor-info tn-p">
                <view class="info-row tn-mb-sm">
                  <text class="label">性别：</text>
                  <text>{{ doctor.gender }}</text>
                </view>
                <view class="info-row tn-mb-sm">
                  <text class="label">背景：</text>
                  <text>{{ doctor.background }}</text>
                </view>
                <view class="info-row tn-mb-sm">
                  <text class="label">专长：</text>
                  <text>{{ doctor.specializedFields }}</text>
                </view>
                <view class="info-row">
                  <text class="label">简介：</text>
                  <text>{{ doctor.otherInformation }}</text>
                </view>
                
                <!-- 添加交流按钮 -->
                <view class="tn-flex tn-flex-row-center-center tn-mt-lg">
                  <tn-button
                    size="sm"
                    padding="12rpx 40rpx"
                    bg-color="#5677fc"
                    @tap="chatWithDoctor(doctor)"
                  >
                    <tn-icon name="message" color="#fff" size="30rpx"></tn-icon>
                    <text class="tn-ml-xs tn-white_text">与咨询师交流</text>
                  </tn-button>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 咨询时间 -->
        <view v-if="currentTab === 2" class="tab-content">
          <view class="tn-section__title tn-section__title--primary tn-mt">
            <view class="tn-section__title__value">
              <text class="tn-section__title__value--prefix"></text>
              <text class="tn-section__title__value--text">咨询时间</text>
            </view>
          </view>
          
          <view class="time-list">
            <view 
              v-for="(timeSlot, index) in detailData.timeSlots" 
              :key="index" 
              class="time-item tn-shadow-sm tn-radius tn-mb tn-p"
            >
              <view class="time-header tn-flex tn-flex-row-between tn-flex-col-center">
                <view class="tn-text-lg tn-text-bold">{{ timeSlot.timeDesc }}</view>
                <view class="tn-tag tn-blue_bg tn-white_text tn-round">{{ timeSlot.approximateTime }}</view>
              </view>
              
              <view class="time-info tn-mt-sm">
                <view class="tn-flex tn-flex-row-center">
                  <tn-icon name="time" size="40rpx" color="#5677fc"></tn-icon>
                  <view class="tn-ml-sm tn-gray_text">{{ timeSlot.startTime }} - {{ timeSlot.endTime }}</view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.detail-container {
  min-height: 100vh;
  background-color: #f8f8f8;
}

.detail-content {
  padding: 30rpx;
}

.header {
  overflow: hidden;
  
  &-top {
    border-bottom: 1px solid #f5f5f5;
  }
  
  &-info {
    padding: 20rpx 30rpx;
  }
}

.info-item {
  padding: 10rpx 0;
  
  &:active {
    opacity: 0.7;
  }
}

.tabs-container {
  overflow: hidden;
}

.tab-content {
  padding: 20rpx 0 40rpx;
}

.description, .method {
  line-height: 1.8;
  color: #666;
  text-align: justify;
}

.doctor-list {
  padding: 20rpx 0;
}

.doctor-item {
  background-color: #fff;
  margin-bottom: 30rpx;
  overflow: hidden;
  
  .doctor-header {
    border-bottom: 1px solid #f5f5f5;
    
    .rating {
      text-align: center;
      
      &-value {
        color: #ff7900;
      }
    }
  }
  
  .doctor-info {
    padding: 20rpx 30rpx;
    
    .info-row {
      line-height: 1.6;
      
      .label {
        color: #999;
        margin-right: 10rpx;
      }
    }
  }
}

.time-list {
  padding: 20rpx 0;
}

.time-item {
  background-color: #fff;
  
  .time-header {
    .tn-tag {
      padding: 6rpx 20rpx;
      font-size: 24rpx;
    }
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
}
</style>