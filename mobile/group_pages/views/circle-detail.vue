<template>
  <view class="circle_detail_container">
    <navbar :title="title || '圈子详情'" :placeholder="false"></navbar>

    <template v-if="!loading">
      <template v-if="circleInfo">
        <!--顶部的滚动页面-->
        <swiper class="card-swiper" :circular="true" :autoplay="true" duration="500" interval="28000"
                @change="cardSwiper">
          <swiper-item v-for="(item,index) in swiperList" :key="index" :class="cardCur===index?'cur':''">
            <view class="swiper-item image-banner">
              <image :src="item.url" mode="aspectFill" v-if="item.type==='image'"></image>
            </view>
          </swiper-item>
        </swiper>

        <view class="indication">
          <block v-for="(item,index) in swiperList" :key="index">
            <view class="spot" :class="cardCur===index?'active':''"></view>
          </block>
        </view>

        <!-- 头部start-->
        <view class="shop-function tn-mb-xl" :style="{paddingTop: 90 + 'px'}">
          <view class="tn-flex tn-flex-center-between tn-m">
            <view class="">
              <view class="tn-flex tn-flex-center-start">
                <view class="logo-pic tn-shadow-blur"
                      :style="'background-image:url(' + (circleInfo.avatar || 'https://assets.example.invalid/placeholder.png') + ')'">
                  <view class="logo-image">
                  </view>
                </view>
                <view class="tn-white_text" style="width: 50vw;">
                  <view class="tn-pr tn-pl-sm tn-text-xl tn-text-bold">
                    {{ title }}
                  </view>
                  <view class="tn-pr tn-pt-xs tn-text-ellipsis-1 tn-pl-sm tn-text-sm">
                    <text class="tn-pr-sm">{{ circleInfo.description || '暂无描述' }}</text>
                  </view>
                </view>
              </view>
            </view>
            <view class="tn-flex-center-center">
              <view class="tn-p-xs tn-white_bg tn-round tn-shadow-blur" @click="tn('/group_pages/views/search-detail')">
                <tn-icon name="search" size="lg"></tn-icon>
              </view>
              <view class="tn-flex-start-center tn-ml-xs" @click="showModal">
                <view class="tn-p-xs tn-white_bg tn-round tn-shadow-blur">
                  <tn-icon name="qr-code" size="lg" class="tn-p-xs"></tn-icon>
                </view>
              </view>
            </view>
          </view>
        </view>
        <!-- 头部 end-->

        <view class="group-wrap" id="page_tips">
          <!-- 悬浮按钮-->
          <view class="tn-flex tn-flex-center-around">
            <view class="tn-flex-1 tn-m-xs tn-text-center">
              <tn-button 
                height="90rpx" 
                :bg-color="isFollowed ? '#ffe0e0' : '#00FFC6'" 
                padding="40rpx 0" 
                width="90%" 
                shadow 
                bold
                @click="handleFollow"
              >
                <tn-icon :name="isFollowed ? 'minus' : 'add'" class="tn-pr-xs tn-black_text"></tn-icon>
                <text class="tn-black_text">{{ isFollowed ? '取消关注' : '关 注' }}</text>
              </tn-button>
            </view>
            <view class="tn-flex-1 tn-m-xs tn-text-center">
              <tn-button height="90rpx" color="#FFF00D" padding="40rpx 0" width="90%" shadow bold
                         open-type="share">
                <tn-icon name="share" class="tn-pr-xs tn-black_text"></tn-icon>
                <text class="tn-black_text">分 享</text>
              </tn-button>
            </view>
          </view>

          <!--圈子内容模块-->
          <template v-if="content.length > 0">
            <recommendGroupVue :content="content" @clickGroup="clickGroup"></recommendGroupVue>
          </template>
          <template v-else>
            <EmptyData 
              title="暂无帖子" 
              description="来发布第一个帖子吧"
            />
          </template>
        </view>

        <!-- 展示圈子的一些信息-->
        <tn-modal :maskClosable="true" ref="modalRef">
          <view class="custom-modal-content">
            <image @tap="previewQRCodeImage" :src='circleInfo.qrCode'
                   mode='aspectFill' style="width: 100%;"></image>
            <view class="tn-text-center tn-padding-top">欢迎加入【{{ title }}】圈子群</view>
            <view class="tn-text-center tn-padding-top tn-text-lg">点击上图，可识别微信二维码</view>
          </view>
        </tn-modal>
      </template>
      <template v-else>
        <EmptyData 
          title="圈子不存在" 
          description="该圈子可能已被删除"
        />
      </template>
    </template>
    
    <!-- 加载中状态 -->
    <view v-else class="loading-container">
      <tn-loading show></tn-loading>
    </view>
  </view>
</template>

<script>
import navbar from "@/components/navbar.vue";
import imagesHttp from "/static/constant/imag_http.json"
import recommendGroupVue from '@/components/recommend-group.vue'
import EmptyData from '@/components/empty-data.vue'
import {queryById1} from '@/service/api/circleController'
import {follow1, followOrNot1} from '@/service/api/circleFollowController'
import {formatTime} from "@/utils/formate";

export default {
  components: {
    navbar,
    recommendGroupVue,
    EmptyData
  },
  
  data() {
    return {
      cardCur: 0,
      show1: false,
      modalRef: null,
      title: '',
      loading: false,
      circleInfo: {},
      content: [],
      isFollowed: false,
      swiperList: [
        {
          id: 0,
          type: 'image',
          url: 'https://assets.example.invalid/placeholder.png',
        }
      ]
    }
  },
  onLoad(options) {
    const id = options.id
    this.circleInfo.avatar = options.circleUrl
    this.swiperList[0].url = options.circleUrl
    if (id) {
      this.getCircleDetail(Number(id))
    } else {
      console.error('没有获取到圈子ID')
    }
  },
  
  // mounted() {
  //   const pages = getCurrentPages()
  //   const currentPage = pages[pages.length - 1]
  //   const id = currentPage?.options?.id
  //   console.log('当前圈子ID:', id)
  //   if (id) {
  //     this.getCircleDetail(Number(id))
  //   } else {
  //     console.error('没有获取到圈子ID')
  //   }
  // },
  
  methods: {
    // 获取关注状态
    async getFollowStatus(id) {
      try {
        const res = await followOrNot1({id})
        if (res.code === 1) {
          this.isFollowed = res.data || false
        }
      } catch (error) {
        console.error('获取关注状态失败:', error)
      }
    },
    
    // 获取圈子详情
    async getCircleDetail(id) {
      this.loading = true
      try {
        const res = await queryById1({id})
        console.log('圈子详情原始数据:', res)
        if (res.code === 1 && res.data) {
          this.circleInfo = res.data
          this.title = res.data.name || '圈子详情'
          
          // 获取关注状态
          await this.getFollowStatus(id)
          
          // 处理轮播图数据
          if (res.data.banners && res.data.banners.length > 0) {
            this.swiperList = res.data.banners.map((url, index) => ({
              id: index,
              type: 'image',
              url
            }))
          }
          
          // 处理帖子列表数据
          if (res.data.blogList && res.data.blogList.length > 0) {
            this.content = res.data.blogList.map(post => ({
              id: post.id,
              userAvatar: post.userAvatar,
              userName: post.userName,
              userProfile: post.userProfile,
              label: post.label || [],
              title: post.title,
              content: post.content,
              mainImage: post.mainImage || [],
              viewUserCount: post.viewUserCount || 0,
              comments: post.comments || 0,
              liked: post.liked || 0,
              likedUser: post.likedUser || [],
              createTime: formatTime(post.createTime),
              isLike: post.isLike || false,
              collectionCount: post.collectionCount || 0
            }))
          } else {
            this.content = []
          }
          console.log('处理后的圈子信息:', this.circleInfo)
          console.log('处理后的帖子列表:', this.content)
        }
      } catch (error) {
        console.error('获取圈子详情失败:', error)
        uni.showToast({
          title: '获取数据失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 预览圈子管理员微信图片
    previewQRCodeImage() {
      wx.previewImage({
        urls: [this.circleInfo?.qrCode || 'https://assets.example.invalid/placeholder.png']
      })
    },
    
    // 弹出模态框
    showModal(event) {
      this.openModal()
    },
    
    // 打开模态框
    openModal() {
      this.$refs.modalRef.showModal()
    },
    
    tn(e) {
      uni.navigateTo({
        url: e,
      })
    },
    
    // 轮播图切换
    cardSwiper(e) {
      this.cardCur = e.detail.current
    },
    
    // 点击帖子
    clickGroup(item) {
      uni.navigateTo({
        url: `/group_pages/views/group-detail?id=${item.id}`
      })
    },
    
    // 处理关注/取消关注
    async handleFollow() {
      try {
        if (!this.circleInfo?.id) {
          uni.showToast({
            title: '圈子ID不存在',
            icon: 'none'
          })
          return
        }
  
        const res = await follow1({
          id: this.circleInfo.id,
          isFollow: !this.isFollowed
        })
  
        if (res.code === 1) {
          this.isFollowed = !this.isFollowed
          // 更新关注人数
          if (this.circleInfo.follow !== undefined) {
            this.circleInfo.follow += this.isFollowed ? 1 : -1
          }
          uni.showToast({
            title: this.isFollowed ? '关注成功' : '已取消关注',
            icon: 'success'
          })
        } else {
          uni.showToast({
            title: res.msg || '操作失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('关注操作失败:', error)
        uni.showToast({
          title: '操作失败，请稍后重试',
          icon: 'none'
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
/* 轮播视觉差 start */
.card-swiper {
  height: 500rpx !important;
  margin-top: 20px;
}

.card-swiper swiper-item {
  width: 750rpx !important;
  left: 0;
  box-sizing: border-box;
  // padding: 0rpx 30rpx 90rpx 30rpx;
  overflow: initial;
}

.card-swiper swiper-item .swiper-item {
  width: 100%;
  display: block;
  height: 100%;
  transform: scale(1);
  transition: all 0.2s ease-in 0s;
  will-change: transform;
  overflow: hidden;
}

.card-swiper swiper-item.cur .swiper-item {
  transform: none;
  transition: all 0.2s ease-in 0s;
  will-change: transform;
}

.image-banner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-banner image {
  width: 100%;
  height: 100%;
}

/* 轮播指示点 start*/
.indication {
  z-index: 9999;
  width: 100%;
  height: 36rpx;
  position: absolute;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  opacity: 0;
}

.spot {
  background-color: #FFFFFF;
  opacity: 0.6;
  width: 10rpx;
  height: 10rpx;
  border-radius: 20rpx;
  top: -60rpx;
  margin: 0 8rpx !important;
  position: relative;
}

.spot.active {
  opacity: 1;
  width: 30rpx;
  background-color: #FFFFFF;
}

/* 顶部店铺 */
.shop-function {
  position: relative;
  z-index: 1;
  margin-top: -450rpx;
  padding-bottom: 110rpx;
  background-image: linear-gradient(rgba(255, 255, 255, 0.01), rgba(0, 0, 0, 0.4));
}

/* 阴影 start*/
.group-shadow {
  border-radius: 15rpx;
  box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
}

/* 头像 start */
.logo-image {
  width: 110rpx;
  height: 110rpx;
  position: relative;
}

.logo-pic {
  background-size: cover;
  background-repeat: no-repeat;
  // background-attachment:fixed;
  background-position: top;
  box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
  border-radius: 50%;
  overflow: hidden;
  // background-color: #FFFFFF;
}

/* 内容*/
.group-wrap {
  position: relative;
  z-index: 1;
  // padding: 20rpx 30rpx;
  margin-top: -130rpx;
  margin-bottom: 40rpx;
  border-radius: 30rpx 30rpx 0 0;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
  height: 100vh;
}
</style>
