<script setup>
import { ref } from 'vue'
import navbar from "@/components/navbar.vue"
import TnCircleProgress from '@tuniao/tnui-vue3-uniapp/components/circle-progress/src/circle-progress.vue'
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'

// 发展愿景
const vision = ref(`
  心晴致力于成为大学生心理健康领域的领先应用，通过科技手段为大学生提供全方位的心理健康服务。我们希望通过不断创新和完善，帮助更多大学生缓解心理压力，提高心理健康水平，促进身心和谐发展。
`)

// 发展阶段
const developmentStages = ref([
  {
    stage: '第一阶段（已完成）',
    title: '基础功能建设',
    time: '2023年1月 - 2023年12月',
    content: [
      '完成核心功能开发，包括心理测评、AI陪聊、心理知识科普、音乐冥想、心情日记等',
      '建立基础用户体系和数据分析系统',
      '完成与校内心理咨询中心的合作对接',
      '初步建立EmoChat模型，针对大学生心理问题Top6进行优化'
    ],
    progress: 100
  },
  {
    stage: '第二阶段（进行中）',
    title: '功能完善与用户拓展',
    time: '2024年1月 - 2024年6月',
    content: [
      '优化用户界面和用户体验，提高应用易用性',
      '扩展心理测评类型，增加专业性和针对性',
      '完善AI陪聊功能，提高对话质量和情感理解能力',
      '拓展更多高校合作，建立校园心理健康服务网络',
      '增加社区互动功能，促进用户之间的交流和支持'
    ],
    progress: 40
  },
  {
    stage: '第三阶段（规划中）',
    title: '智能化与个性化服务',
    time: '2024年7月 - 2024年12月',
    content: [
      '引入更多智能设备连接，实现健康数据全面监测',
      '基于用户数据分析，提供个性化心理健康建议',
      '开发心理健康预警系统，及时发现潜在风险',
      '增加专业心理咨询预约功能，线上线下结合',
      '拓展心理健康课程和工作坊，提供系统化学习'
    ],
    progress: 0
  },
  {
    stage: '第四阶段（规划中）',
    title: '生态系统构建',
    time: '2025年1月 - 2025年12月',
    content: [
      '建立完整的大学生心理健康生态系统',
      '开放平台API，支持第三方应用和服务接入',
      '构建心理健康大数据平台，为高校提供数据支持',
      '拓展国际化服务，支持多语言和跨文化心理健康服务',
      '探索心理健康与其他领域的融合创新'
    ],
    progress: 0
  }
])

// 创新计划
const innovationPlans = ref([
  {
    title: 'EmoChat 2.0',
    icon: 'robot',
    color: '#5677fc',
    desc: '升级EmoChat模型，增强情感理解和表达能力，提供更加自然、有温度的对话体验。'
  },
  {
    title: '心理健康预警系统',
    icon: 'alert',
    color: '#ff7900',
    desc: '基于用户行为和情绪变化，建立心理健康风险预警机制，及时发现潜在问题并提供干预。'
  },
  {
    title: '校园心理地图',
    icon: 'map',
    color: '#19be6b',
    desc: '构建校园心理资源地图，帮助学生快速找到附近的心理咨询室、减压空间等资源。'
  },
  {
    title: '心理健康积分系统',
    icon: 'gift',
    color: '#e03997',
    desc: '建立心理健康积分激励机制，鼓励学生主动关注心理健康，参与心理健康活动。'
  },
  {
    title: '虚拟现实减压空间',
    icon: 'vr',
    color: '#1cbbb4',
    desc: '利用VR/AR技术，打造沉浸式减压体验，帮助学生缓解压力，提升心理韧性。'
  }
])

// 合作计划
const cooperationPlans = ref([
  {
    title: '高校合作',
    content: '计划与全国100所高校建立合作关系，为高校提供定制化的心理健康服务解决方案。'
  },
  {
    title: '心理咨询机构合作',
    content: '与专业心理咨询机构合作，为用户提供线上线下结合的心理咨询服务。'
  },
  {
    title: '科研机构合作',
    content: '与心理学研究机构合作，开展大学生心理健康研究，提升产品的科学性和专业性。'
  },
  {
    title: '企业合作',
    content: '与相关企业合作，为即将步入职场的大学生提供职场心理健康支持。'
  }
])
</script>

<template>
  <view class="future-container">
    <!-- 导航栏 -->
    <navbar title="未来规划"></navbar>
    
    <!-- 顶部愿景 -->
    <view class="vision-section tn-shadow-sm">
      <view class="vision-title tn-flex tn-flex-row-center">
        <TnIcon name="flag" color="#5677fc" size="40rpx"></TnIcon>
        <text class="tn-text-bold tn-ml-sm">我们的愿景</text>
      </view>
      <view class="vision-content">
        <text>{{ vision }}</text>
      </view>
    </view>
    
    <!-- 发展路线图 -->
    <view class="content-section">
      <view class="section-header">
        <text class="section-title tn-text-bold">发展路线图</text>
        <text class="section-subtitle">Development Roadmap</text>
      </view>
      <view class="section-content">
        <view class="roadmap">
          <view 
            v-for="(stage, index) in developmentStages" 
            :key="index"
            class="roadmap-item tn-shadow-sm"
          >
            <view class="roadmap-header">
              <view class="stage-info">
                <text class="stage-name">{{ stage.stage }}</text>
                <text class="stage-title tn-text-bold">{{ stage.title }}</text>
                <text class="stage-time">{{ stage.time }}</text>
              </view>
              <view class="stage-progress">
                <TnCircleProgress
                  :percent="stage.progress"
                  :size="100"
                  active-color="#5677fc"
                  inactive-color="#eeeeee"
                >
                  <text class="progress-text">{{ stage.progress }}%</text>
                </TnCircleProgress>
              </view>
            </view>
            
            <view class="roadmap-content">
              <view 
                v-for="(item, itemIndex) in stage.content" 
                :key="itemIndex"
                class="content-item tn-flex"
              >
                <view class="item-dot" :style="{ backgroundColor: stage.progress > 0 ? '#5677fc' : '#cccccc' }"></view>
                <text class="item-text">{{ item }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 创新计划 -->
    <view class="content-section">
      <view class="section-header">
        <text class="section-title tn-text-bold">创新计划</text>
        <text class="section-subtitle">Innovation Plans</text>
      </view>
      <view class="section-content">
        <view class="innovation-list">
          <view 
            v-for="(plan, index) in innovationPlans" 
            :key="index"
            class="innovation-item tn-shadow-sm"
          >
            <view class="innovation-icon" :style="{ backgroundColor: plan.color + '20' }">
              <TnIcon :name="plan.icon" :color="plan.color" size="50rpx"></TnIcon>
            </view>
            <view class="innovation-content">
              <text class="innovation-title tn-text-bold">{{ plan.title }}</text>
              <text class="innovation-desc">{{ plan.desc }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 合作计划 -->
    <view class="content-section">
      <view class="section-header">
        <text class="section-title tn-text-bold">合作计划</text>
        <text class="section-subtitle">Cooperation Plans</text>
      </view>
      <view class="section-content">
        <view class="cooperation-list">
          <view 
            v-for="(plan, index) in cooperationPlans" 
            :key="index"
            class="cooperation-item tn-shadow-sm"
          >
            <view class="cooperation-header tn-flex tn-flex-row-center">
              <view class="cooperation-number">{{ index + 1 }}</view>
              <text class="cooperation-title tn-text-bold">{{ plan.title }}</text>
            </view>
            <view class="cooperation-content">
              <text>{{ plan.content }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 加入我们 -->
    <view class="join-section tn-shadow-sm">
      <view class="join-title tn-text-bold">加入我们，共创未来</view>
      <view class="join-desc">
        <text>我们诚邀各界人士加入心晴团队，共同推动大学生心理健康事业发展。无论您是心理学专业人士、技术开发者、设计师，还是对心理健康领域感兴趣的大学生，都可以通过以下方式联系我们：</text>
      </view>
      <view class="join-contact tn-flex tn-flex-direction-column tn-flex-row-center">
        <text>招聘邮箱：join@xinqing.com</text>
        <text>合作电话：400-123-4567</text>
      </view>
      <TnButton bg-color="#5677fc" margin="30rpx 0 0 0">
        <text class="tn-white_text">立即加入</text>
      </TnButton>
    </view>
  </view>
</template>

<style scoped lang="scss">
.future-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 100rpx;
}

.vision-section {
  margin: 30rpx;
  padding: 40rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  
  .vision-title {
    font-size: 32rpx;
    color: #333333;
    margin-bottom: 20rpx;
  }
  
  .vision-content {
    font-size: 28rpx;
    color: #666666;
    line-height: 1.8;
    text-align: justify;
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

.roadmap {
  .roadmap-item {
    background-color: #ffffff;
    border-radius: 12rpx;
    margin-bottom: 30rpx;
    overflow: hidden;
    border: 1rpx solid #f5f5f5;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .roadmap-header {
      padding: 30rpx;
      display: flex;
      justify-content: space-between;
      align-items: center;
      background-color: #f9f9f9;
      
      .stage-info {
        flex: 1;
        
        .stage-name {
          font-size: 24rpx;
          color: #5677fc;
          margin-bottom: 10rpx;
          display: block;
        }
        
        .stage-title {
          font-size: 32rpx;
          color: #333333;
          margin-bottom: 10rpx;
          display: block;
        }
        
        .stage-time {
          font-size: 24rpx;
          color: #888888;
          display: block;
        }
      }
      
      .stage-progress {
        .progress-text {
          font-size: 24rpx;
          color: #5677fc;
          font-weight: bold;
        }
      }
    }
    
    .roadmap-content {
      padding: 30rpx;
      
      .content-item {
        margin-bottom: 20rpx;
        align-items: flex-start;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .item-dot {
          width: 16rpx;
          height: 16rpx;
          border-radius: 50%;
          margin-top: 12rpx;
          margin-right: 20rpx;
          flex-shrink: 0;
        }
        
        .item-text {
          font-size: 28rpx;
          color: #666666;
          line-height: 1.6;
          flex: 1;
        }
      }
    }
  }
}

.innovation-list {
  .innovation-item {
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
    
    .innovation-icon {
      width: 80rpx;
      height: 80rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 30rpx;
      flex-shrink: 0;
    }
    
    .innovation-content {
      flex: 1;
      
      .innovation-title {
        font-size: 30rpx;
        color: #333333;
        margin-bottom: 10rpx;
      }
      
      .innovation-desc {
        font-size: 26rpx;
        color: #888888;
        line-height: 1.5;
      }
    }
  }
}

.cooperation-list {
  .cooperation-item {
    background-color: #ffffff;
    border-radius: 12rpx;
    margin-bottom: 30rpx;
    overflow: hidden;
    border: 1rpx solid #f5f5f5;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .cooperation-header {
      padding: 20rpx 30rpx;
      background-color: #f9f9f9;
      
      .cooperation-number {
        width: 40rpx;
        height: 40rpx;
        border-radius: 50%;
        background-color: #5677fc;
        color: #ffffff;
        font-size: 24rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20rpx;
      }
      
      .cooperation-title {
        font-size: 30rpx;
        color: #333333;
      }
    }
    
    .cooperation-content {
      padding: 20rpx 30rpx;
      
      text {
        font-size: 28rpx;
        color: #666666;
        line-height: 1.6;
      }
    }
  }
}

.join-section {
  margin: 30rpx;
  padding: 40rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  text-align: center;
  
  .join-title {
    font-size: 32rpx;
    color: #333333;
    margin-bottom: 20rpx;
  }
  
  .join-desc {
    font-size: 28rpx;
    color: #666666;
    line-height: 1.8;
    text-align: justify;
    margin-bottom: 30rpx;
  }
  
  .join-contact {
    margin-bottom: 20rpx;
    
    text {
      font-size: 28rpx;
      color: #5677fc;
      margin-bottom: 10rpx;
    }
  }
}
</style>