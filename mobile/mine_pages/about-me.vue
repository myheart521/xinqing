<script setup>
import { ref } from 'vue'
import navbar from "@/components/navbar.vue"
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'
import {ossAvatarUrl, ossImages} from "@/utils/ossUrl";

// 项目介绍
const projectIntro = ref(`
  在现代社会，大学生面临着学业、就业、人际关系等多方面的压力，心理健康问题日益凸显。抑郁、焦虑等心理障碍的普遍性，对学生的全面发展构成了严峻挑战。然而，传统的心理健康服务往往受限于时间、地点和专业资源，很多大学生无法得到及时有效的心理支持和帮助。
  
  基于EmoChat的大学生心理AI陪聊与干预系统（以下简称"心晴"）是一款针对大学生群体的心理健康AI聊天缓解心理压力的APP，主要以帮助不适应大学生活、人际交往能力较差、心理问题凸显的大学生群体为主，同时还为学生和本校心理咨询老师沟通提供平台支持。旨在通过科技手段为大学生群体提供便捷、私密的心理健康服务。
`)

// 核心功能
const coreFunctions = ref([
  {
    title: '智能设备连接',
    icon: 'watch',
    color: '#5677fc',
    desc: '支持智能跑步机、智能体脂秤、智能手表等设备连接，实时同步健康数据。'
  },
  {
    title: '心理测评',
    icon: 'form',
    color: '#ff7900',
    desc: '提供抑郁、性格、自适应等多种专业心理测评，帮助了解自身心理状态。'
  },
  {
    title: '心理知识科普',
    icon: 'book',
    color: '#19be6b',
    desc: '提供权威心理文章，普及心理健康知识，提高心理素养。'
  },
  {
    title: '音乐冥想',
    icon: 'headset',
    color: '#e03997',
    desc: '提供雨声、流水声、治愈音乐等，支持评论、收藏、后台播放。'
  },
  {
    title: '心情日记',
    icon: 'edit',
    color: '#8799a3',
    desc: '记录、搜索、排序、预览、修改、删除日记，未来支持日记分享和位置地图功能。'
  },
  {
    title: 'AI陪聊',
    icon: 'message',
    color: '#39b54a',
    desc: '基于大学生心理问题Top6，采用EmoChat模型针对性陪聊，经过科学测评表现良好。'
  },
  {
    title: '论坛交流',
    icon: 'community',
    color: '#1cbbb4',
    desc: '分为个人成长、学业就业等板块，支持话题发布、评论和搜索，用户可通过互动缓解心理压力。'
  }
])

// 团队信息
const teamInfo = ref([
  {
    name: '张教授',
    role: '项目指导',
    avatar: ossImages.avatar2,
    desc: '心理学博士，拥有20年心理咨询经验'
  },
  {
    name: '李博士',
    role: '心理顾问',
    avatar: ossImages.avatar2,
    desc: '临床心理学专家，专注青少年心理健康研究'
  },
  {
    name: '王工程师',
    role: '技术负责人',
    avatar: ossImages.avatar2,
    desc: 'AI算法专家，负责EmoChat模型开发'
  },
  {
    name: '赵设计师',
    role: 'UI/UX设计',
    avatar: ossImages.avatar2,
    desc: '拥有丰富的用户体验设计经验'
  }
])

// 联系方式
const contactInfo = ref({
  email: 'demo@example.invalid',
  phone: '13000000000',
  website: 'www.xinqing.com',
  address: '河南省焦作市高新区世纪大道2001号'
})

// 版本信息
const versionInfo = ref({
  version: 'v1.0.0',
  updateDate: '2023-12-01',
  platform: 'Android/iOS/微信小程序'
})
</script>

<template>
  <view class="about-container">
    <!-- 导航栏 -->
    <navbar title="关于心晴" ></navbar>
    
    <!-- 顶部Logo -->
    <view class="logo-section tn-flex tn-flex-direction-column tn-flex-center">
      <image src="/static/logo.png" mode="aspectFit" class="logo-image"></image>
      <text class="app-name tn-text-bold">心晴</text>
      <text class="app-slogan">用AI温暖每一颗心灵</text>
      <text class="app-version">{{ versionInfo.version }} ({{ versionInfo.updateDate }})</text>
    </view>
    
    <!-- 项目介绍 -->
    <view class="content-section">
      <view class="section-header">
        <text class="section-title tn-text-bold">项目介绍</text>
        <text class="section-subtitle">Project Introduction</text>
      </view>
      <view class="section-content">
        <view class="intro-text">
          <text>{{ projectIntro }}</text>
        </view>
      </view>
    </view>
    
    <!-- 核心功能 -->
    <view class="content-section">
      <view class="section-header">
        <text class="section-title tn-text-bold">核心功能</text>
        <text class="section-subtitle">Core Functions</text>
      </view>
      <view class="section-content">
        <view class="function-list">
          <view 
            v-for="(item, index) in coreFunctions" 
            :key="index"
            class="function-item tn-shadow-sm"
          >
            <view class="function-icon" :style="{ backgroundColor: item.color + '20' }">
              <TnIcon :name="item.icon" :color="item.color" size="50rpx"></TnIcon>
            </view>
            <view class="function-content">
              <text class="function-title tn-text-bold">{{ item.title }}</text>
              <text class="function-desc">{{ item.desc }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 团队介绍 -->
    <view class="content-section">
      <view class="section-header">
        <text class="section-title tn-text-bold">团队成员</text>
        <text class="section-subtitle">Our Team</text>
      </view>
      <view class="section-content">
        <scroll-view scroll-x class="team-scroll" show-scrollbar="false">
          <view class="team-list tn-flex">
            <view 
              v-for="(member, index) in teamInfo" 
              :key="index"
              class="team-item tn-shadow-sm"
            >
              <image :src="member.avatar" mode="aspectFill" class="team-avatar"></image>
              <text class="team-name tn-text-bold">{{ member.name }}</text>
              <text class="team-role">{{ member.role }}</text>
              <text class="team-desc">{{ member.desc }}</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
    
    <!-- 联系我们 -->
    <view class="content-section">
      <view class="section-header">
        <text class="section-title tn-text-bold">联系我们</text>
        <text class="section-subtitle">Contact Us</text>
      </view>
      <view class="section-content">
        <view class="contact-list">
          <view class="contact-item tn-flex tn-flex-start-between">
            <view class="contact-label tn-flex tn-flex-row-center">
              <TnIcon name="email" color="#5677fc" size="40rpx"></TnIcon>
              <text class="tn-ml-sm">邮箱</text>
            </view>
            <text class="contact-value">{{ contactInfo.email }}</text>
          </view>
          
          <view class="contact-item tn-flex tn-flex-start-between">
            <view class="contact-label tn-flex tn-flex-row-center">
              <TnIcon name="phone" color="#5677fc" size="40rpx"></TnIcon>
              <text class="tn-ml-sm">电话</text>
            </view>
            <text class="contact-value">{{ contactInfo.phone }}</text>
          </view>
          
          <view class="contact-item tn-flex tn-flex-start-between">
            <view class="contact-label tn-flex tn-flex-row-center">
              <TnIcon name="link" color="#5677fc" size="40rpx"></TnIcon>
              <text class="tn-ml-sm">网站</text>
            </view>
            <text class="contact-value">{{ contactInfo.website }}</text>
          </view>
          
          <view class="contact-item tn-flex tn-flex-start-between">
            <view class="contact-label tn-flex tn-flex-row-center">
              <TnIcon name="map-marker" color="#5677fc" size="40rpx"></TnIcon>
              <text class="tn-ml-sm">地址</text>
            </view>
            <text class="contact-value">{{ contactInfo.address }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 底部版权 -->
    <view class="footer">
      <text class="copyright">Copyright © 2023 心晴团队 All Rights Reserved</text>
    </view>
  </view>
</template>

<style scoped lang="scss">
.about-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 100rpx;
}

.logo-section {
  padding: 60rpx 0;
  background-color: #ffffff;
  
  .logo-image {
    width: 150rpx;
    height: 150rpx;
    border-radius: 30rpx;
  }
  
  .app-name {
    font-size: 40rpx;
    margin-top: 20rpx;
    color: #333333;
  }
  
  .app-slogan {
    font-size: 28rpx;
    color: #888888;
    margin-top: 10rpx;
  }
  
  .app-version {
    font-size: 24rpx;
    color: #aaaaaa;
    margin-top: 10rpx;
  }
}

.content-section {
  margin: 30rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
  
  .section-header {
    padding: 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    .section-title {
      font-size: 32rpx;
      color: #333333;
      display: block;
    }
    
    .section-subtitle {
      font-size: 24rpx;
      color: #888888;
      margin-top: 6rpx;
      display: block;
    }
  }
  
  .section-content {
    padding: 30rpx;
  }
}

.intro-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.8;
  text-align: justify;
}

.function-list {
  .function-item {
    display: flex;
    align-items: center;
    background-color: #ffffff;
    border-radius: 12rpx;
    padding: 30rpx;
    margin-bottom: 30rpx;
    border: 1rpx solid #f5f5f5;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .function-icon {
      width: 80rpx;
      height: 80rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 30rpx;
      flex-shrink: 0;
    }
    
    .function-content {
      flex: 1;
      
      .function-title {
        font-size: 30rpx;
        color: #333333;
        margin-bottom: 10rpx;
      }
      
      .function-desc {
        font-size: 26rpx;
        color: #888888;
        line-height: 1.5;
      }
    }
  }
}

.team-scroll {
  width: 100%;
  white-space: nowrap;
  
  .team-list {
    padding: 20rpx 0;
    
    .team-item {
      display: inline-flex;
      flex-direction: column;
      align-items: center;
      background-color: #ffffff;
      border-radius: 12rpx;
      padding: 30rpx;
      margin-right: 30rpx;
      width: 250rpx;
      border: 1rpx solid #f5f5f5;
      
      .team-avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: 60rpx;
        margin-bottom: 20rpx;
      }
      
      .team-name {
        font-size: 28rpx;
        color: #333333;
        margin-bottom: 10rpx;
      }
      
      .team-role {
        font-size: 24rpx;
        color: #5677fc;
        margin-bottom: 10rpx;
      }
      
      .team-desc {
        font-size: 22rpx;
        color: #888888;
        text-align: center;
        white-space: normal;
        line-height: 1.5;
      }
    }
  }
}

.contact-list {
  background-color: #ffffff;
  border-radius: 12rpx;
  
  .contact-item {
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .contact-label {
      font-size: 28rpx;
      color: #333333;
    }
    
    .contact-value {
      font-size: 28rpx;
      color: #666666;
    }
  }
}

.footer {
  text-align: center;
  padding: 60rpx 0 30rpx;
  
  .copyright {
    font-size: 24rpx;
    color: #aaaaaa;
  }
}
</style>