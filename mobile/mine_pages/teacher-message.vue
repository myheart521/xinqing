<template>
  <view class="page">
    <navbar title="聊天消息列表"></navbar>
    <pulldownRefresh
        ref="refreshRef"
        :top="0"
        :threshold="80"
        @refresh="onRefresh"
    >
      <scroll-view class="user-list" scroll-y>
        <!-- 加载中 -->
        <view v-if="loading && users.length === 0" class="loading-container">
          <text>加载中...</text>
        </view>
        <!-- 没有数据 -->
        <view v-else-if="users.length === 0" class="empty-container">
          <text>暂无关注用户</text>
        </view>
        <!-- 用户列表 -->
        <view v-else>
          <view class="list-item" v-for="(item,index) in users" :key="index" @click="connect(item)">
            <view class="avatar">
              <tn-lazy-load :src="item.avatar || item.userAvatar" height="90rpx" width="90rpx"></tn-lazy-load>
<!--              <image style="height:90rpx;width: 90rpx " :src="item.avatar || item.userAvatar" mode="widthFix"></image>-->
            </view>
            <view class="content">
              <view class="title">
                <text class="name">{{ item.name || item.userName }}</text>
              </view>
              <view class="tn-gray-dark_text">
                <text class="name">{{ getRoleName(item.roleId) }}</text>
              </view>
              <view class="txt">{{ item.msg || '点击进入聊天' }}</view>
            </view>
          </view>
          <!-- 加载更多按钮 -->
          <view class="load-more-container">
            <button v-if="hasMore" @click="loadMore" :disabled="loading" class="load-more-btn">
              {{ loading ? '加载中...' : '加载更多' }}
            </button>
            <view v-else-if="!hasMore && users.length > 0" class="no-more-data">
              <text>没有更多了</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </pulldownRefresh>
  </view>
</template>

<script>
import navbar from "@/components/navbar.vue";
import pulldownRefresh from "@/components/load/pulldownRefresh.vue";
import {followerList} from "@/service/api/followController";
import {getRoleName} from "@/utils/role";

export default {
  components: {
    navbar,
    pulldownRefresh
  },
  data() {
    return {
      options: [{
        text: '取消',
        style: {
          backgroundColor: '#007aff'
        }
      }, {
        text: '确认',
        style: {
          backgroundColor: '#dd524d'
        }
      }],
      users: [],
      loading: false,
      refreshing: false,
      loadingMore: false,
      hasMore: true,
      current: 1,
      total: 0,
      refreshRef: null,
    };
  },
  methods: {
    getRoleName,
    // 分页获取关注用户
    async getUserList(isRefresh = false) {
      if (isRefresh) {
        this.refreshing = true;
        this.current = 1;
      } else {
        if (this.loading) return;
        this.loading = true;
        if (!this.hasMore) {
          this.loading = false;
          uni.showToast({icon: 'none', title: '没有更多数据了'});
          return;
        }
      }
      try {
        const res = await followerList({current: this.current.toString()});
        if (res.code === 1 && res.data) {
          let records = res.data.records || [];
          if (isRefresh || this.current === 1) {
            this.users = records;
          } else {
            this.users = [...this.users, ...records];
          }
          this.total = res.data.total || 0;
          this.hasMore = this.users.length < this.total;
          if (!isRefresh) this.current++;
        } else {
          uni.showToast({icon: 'none', title: res.msg || '获取关注用户失败'});
        }
      } catch (e) {
        uni.showToast({icon: 'none', title: '获取关注用户失败'});
      } finally {
        this.loading = false;
        this.refreshing = false;
      }
    },
    // 下拉刷新
    async onRefresh() {
      await this.getUserList(true);
      uni.showToast({title: '刷新成功', icon: 'success'});
      this.$refs.refreshRef?.endPulldownRefresh();
    },
    // 加载更多
    loadMore() {
      this.getUserList();
    },
    connect(item) {
      uni.navigateTo({
        url: `/mine_pages/teacher-message-detail?name=${item.name || item.userName}&avatar=${item.avatar || item.userAvatar}&receiverId=${item.id}`
      })
    }
  },
  mounted() {
    this.getUserList();
  }
}
</script>

<style lang="scss" scoped>
.page {
  padding: 0 32rpx;
  color: #333;
  min-height: 100vh;
  background: #f8f8f8;
}

.user-list {
  min-height: 60vh;
}

.loading-container, .empty-container {
  text-align: center;
  color: #999;
  padding: 100rpx 0;
}

.load-more-container {
  margin: 40rpx 0 60rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;

  .load-more-btn {
    background: #5677fc;
    color: #fff;
    border-radius: 30rpx;
    padding: 16rpx 60rpx;
    font-size: 28rpx;
    border: none;
  }

  .no-more-data {
    color: #aaa;
    font-size: 26rpx;
    background: #f5f7fa;
    border-radius: 30rpx;
    padding: 15rpx 30rpx;
  }
}

.list-item {
  display: flex;
  justify-content: start;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1px solid #ccced3;
  background: #fff;

  .avatar {
    width: 90rpx;
    height: 90rpx;
    border-radius: 10rpx;
    margin-right: 20rpx;

    image {
      width: 100%;
      height: 100%;
      border-radius: 10rpx;
    }
  }

  .content {
    flex: 1;

    .title {
      display: flex;
      justify-content: space-between;

      .name {
        font-weight: bold;
      }
    }

    .txt {
      margin-top: 10rpx;
      color: #999;
      font-size: 26rpx;
    }
  }
}
</style>