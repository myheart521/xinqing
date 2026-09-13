<script setup>
import navbar from '@/components/navbar.vue'
import {ref, onMounted} from 'vue'
import TnCommentList from 'tnuiv3p-tn-comment-list/index.vue'
import commentInput from '@/components/comment-input.vue'
import {selectById1} from '@/service/api/blogController'
import {likeBlog, selectTwoComment} from '@/service/api/commentsController'
import {likeBlog1} from '@/service/api/blogController'
import {add1, delectById} from '@/service/api/commentsController'
import BlackTitle from "@/components/black-title.vue";
import {useUserStore} from '@/stores/user'
import {storeToRefs} from 'pinia'
import {getToken, getUserInfo} from '@/utils/userStorage'
import {follow, followOrNot} from '@/service/api/followController'
import {formatTime} from "@/utils/formate";
import {onLoad} from '@dcloudio/uni-app'

const userStore = useUserStore()
const {isLogin, userInfo} = storeToRefs(userStore)

const commentListRef = ref(null)
const content = ref([])
const commentList = ref([])
const isLike = ref(false)
const inputValue = ref('')
const replyInfo = ref(null)
const openReplyPopup = ref(false)
const replyCommentNickname = ref('')
const replyContent = ref('')
let replyCommentId = ''
const inputPlaceholder = ref('不说点啥子吗？')
const currentBlogId = ref(null)
const currentPage = ref({}) // 用于存储每个一级评论的当前页码
const isFollowed = ref(false) // 添加关注状态

// 检查用户是否登录
const checkLogin = () => {
  // 使用userStorage中的方法检查登录状态
  const token = getToken()
  const localUserInfo = getUserInfo()

  // 如果本地有token和用户信息，但store中显示未登录，则同步状态
  if (token && localUserInfo && !isLogin.value) {
    userStore.setToken(token)
    userStore.setUserInfo(localUserInfo)
    return true
  }

  if (!isLogin.value || !userInfo.value) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateTo({
        url: '/mine_pages/login'
      })
    }, 1500)
    return false
  }
  return true
}

// 获取关注状态
const getFollowStatus = async (userId) => {
  try {
    const res = await followOrNot({id: userId})
    if (res.code === 1) {
      isFollowed.value = res.data || false

    } else {
      uni.showToast({
        title: '请先登录获取发表评论关注的权限',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取关注状态失败:', error)
  }
}

// 处理关注/取消关注
const handleFollow = async () => {
  if (!checkLogin()) return

  try {
    const userId = content.value[0]?.userId
    if (!userId) {
      uni.showToast({
        title: '用户ID不存在',
        icon: 'none'
      })
      return
    }

    const res = await follow({
      id: userId,
      isFollow: !isFollowed.value
    })

    if (res.code === 1) {
      isFollowed.value = !isFollowed.value
      uni.showToast({
        title: isFollowed.value ? '关注成功' : '已取消关注',
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

// 获取详情数据
const getDetail = async (id) => {
  try {
    currentBlogId.value = id
    const res = await selectById1({id})
    if (res.code === 1) {
      content.value = [res.data]
      isLike.value = res.data.isLike

      // 获取关注状态
      if (res.data.userId) {
        await getFollowStatus(res.data.userId)
      }

      // 处理评论数据
      if (res.data.commentData) {
        console.log("userInfo", userInfo.value)
        commentList.value = res.data.commentData.map(item => ({
          id: item.id,
          avatar: item.userAvatar,
          nickname: item.userName,
          date: formatTime(item.createTime),
          content: item.content,
          likeActive: item.likeActive,
          likeCount: item.liked,
          commentCount: item.commentCount || 0,
          position: '', // 可选
          dislikeActive: false,
          disabledReply: false,
          allowDelete: item.userId === userInfo.value?.id // 只允许删除自己的评论
        }))
      }
    }
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 刷新评论列表
const refreshComments = async () => {
  if (currentBlogId.value) {
    await getDetail(currentBlogId.value)
  }
}

// 递归查找评论
const findComment = (commentId) => {
  // 在一级评论中查找
  const firstLevelComment = commentList.value.find(item => item.id === commentId)
  if (firstLevelComment) {
    return {
      comment: firstLevelComment,
      parentComment: null
    }
  }
  // 遍历所有评论的回复列表
  for (const parentComment of commentList.value) {
    if (parentComment.comment && parentComment.comment.length > 0) {
      const found = parentComment.comment.find(reply => reply.id === commentId)
      if (found) {
        return {
          comment: found,
          parentComment: parentComment
        }
      }
    }
  }
  return null
}

// 获取二级评论
const showMoreClickHandle = async ({id}) => {
  try {
    // 初始化或递增页码
    if (!currentPage.value[id]) {
      currentPage.value[id] = 1
    } else {
      currentPage.value[id]++
    }

    const res = await selectTwoComment({
      parentId: id,
      current: currentPage.value[id]
    })
    if (res.code === 1) {
      // 如果没有数据，说明已经加载完毕
      if (!res.data || res.data.length === 0) {
        uni.showToast({
          title: '没有更多评论了',
          icon: 'none'
        })
        // 将页码回退一页，因为这一页没有数据
        currentPage.value[id]--
        return
      }

      // 分别存储直接回复和二级回复
      const directReplies = []
      const replyComments = []

          // 处理二级评论数据
      ;(res.data || []).forEach(item => {
        const baseComment = {
          id: item.id,
          avatar: item.userAvatar,
          nickname: item.userName,
          date: formatTime(item.createTime),
          content: item.content,
          likeActive: item.likeActive,
          likeCount: item.liked,
          commentCount: item.commentCount || 0,
          position: '',
          dislikeActive: false,
          disabledReply: false,
          allowDelete: item.userId === userInfo.value?.id,
          answerId: item.answerId,
          parentId: item.parentId,
          comment: [] // 添加comment数组以支持多层级回复
        }

        // 找到对应的一级评论
        const parentComment = commentList.value.find(c => c.id === id)

        if (parentComment) {
          // 初始化comment数组（如果还没有的话）
          if (!parentComment.comment) parentComment.comment = []
          // 将评论添加到一级评论的comment数组中
          parentComment.comment.push(baseComment)
        }

        // 分类存储评论
        if (!item.answerId || item.answerId === 0) {
          directReplies.push(baseComment)
        } else {
          replyComments.push(baseComment)
        }
      })

      console.log('直接回复的评论:', directReplies)
      console.log('二级回复的评论:', replyComments)
      // 第一步：添加直接回复的评论
      if (directReplies.length > 0) {
        commentListRef.value?.addCommentData(id, directReplies)
      }

      // 第二步：添加二级回复
      replyComments.forEach(comment => {
        commentListRef.value?.addCommentReply(comment.answerId, comment)
      })

      console.log('更新后的评论列表:', commentList.value)
    }
  } catch (error) {
    console.error('获取二级评论失败:', error)
    // 发生错误时，将页码回退一页
    if (currentPage.value[id]) {
      currentPage.value[id]--
    }
  }
}

// 添加点赞处理函数
const handleLike = async () => {
  if (!checkLogin()) return

  // try {
  const id = content.value[0]?.id
  if (!id) return

  const res = await likeBlog1({id})
  // console.log(res)
  if (res.code === 1) {
    isLike.value = !isLike.value
    // 更新点赞数
    if (isLike.value) {
      content.value[0].liked++
    } else {
      content.value[0].liked--
    }
    uni.showToast({
      title: isLike.value ? '点赞成功' : '取消点赞',
      icon: 'none'
    })
  } else {
    uni.showToast({
      title: res.msg || '操作失败',
      icon: 'none'
    })
  }
  // } catch (error) {
  //   console.error('点赞操作失败:', error)
  //   uni.showToast({
  //     title: '操作失败',
  //     icon: 'none'
  //   })
  // }
}

// 处理评论点赞
const handleCommentLike = async (id) => {
  // 检查登录状态
  if (!checkLogin()) return

  try {
    const res = await likeBlog({id})
    console.log('点赞是否成功：', res)
    if (res.code === 1) {
      // 更新评论列表中的点赞状态
      const comment = commentList.value.find(item => item.id === id)
      if (comment) {
        comment.likeActive = !comment.likeActive
        comment.likeCount = comment.likeActive ? comment.likeCount + 1 : comment.likeCount - 1
      }

      uni.showToast({
        title: comment?.likeActive ? '点赞成功' : '取消点赞',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('评论点赞失败:', error)
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    })
  }
}

// 处理评论回复
const handleCommentReply = (params) => {
  console.log('回复参数:', params)
  replyContent.value = ''
  openReplyPopup.value = true
  replyCommentNickname.value = params.nickname
  replyCommentId = params.id

  // 查找当前评论及其父评论
  const result = findComment(params.id)
  console.log('查找结果:', result)

  if (result) {
    const {comment, parentComment} = result
    replyInfo.value = {
      id: params.id, // 被回复评论的ID（将作为answerId）
      parentId: parentComment ? parentComment.id : params.id, // 如果有父评论用父评论id，否则用当前评论id（说明当前就是一级评论）
      nickname: params.nickname,
      isFirstLevel: !parentComment // 如果没有父评论说明是一级评论
    }
  } else {
    // 如果在commentList中没找到，可能是因为还没有同步到commentList
    // 尝试从params中获取parentId
    replyInfo.value = {
      id: params.id,
      parentId: params.parentId || params.id, // 如果有parentId用parentId，否则用当前评论id
      nickname: params.nickname,
      isFirstLevel: !params.parentId // 如果没有parentId说明是一级评论
    }
  }

  console.log('回复信息:', replyInfo.value)
}

// 发送评论回复
const handleReplyComment = async () => {
  if (!checkLogin()) return
  if (!replyContent.value || !replyCommentId) {
    uni.showToast({
      title: '请输入回复内容',
      icon: 'none'
    })
    return
  }

  try {
    const commentData = {
      blogId: content.value[0]?.id,
      content: replyContent.value,
      parentId: replyInfo.value.parentId, // 一级评论的ID
      answerId: replyInfo.value.isFirstLevel ? undefined : replyInfo.value.id // 如果不是一级评论，使用被回复评论的ID作为answerId
    }

    console.log('发送评论数据:', commentData)
    const res = await add1(commentData)
    console.log("新的回复评论结果：", res)
    if (res.code === 1) {
      // 构造新评论数据
      const newComment = {
        id: res.data.id,
        avatar: userInfo.value.userAvatar,
        nickname: userInfo.value.userName,
        date: new Date().toISOString().split('T')[0],
        content: replyContent.value,
        likeActive: false,
        likeCount: 0,
        dislikeActive: false,
        disabledReply: false,
        allowDelete: true,
        position: '',
        parentId: commentData.parentId, // 保存一级评论的ID
        comment: [],
        answerId: commentData.answerId // 保存被回复评论的ID
      }

      // 使用图鸟评论列表的addCommentReply方法添加回复
      commentListRef.value?.addCommentReply(replyCommentId, newComment)

      // 同步更新到commentList
      const parentComment = commentList.value.find(c => c.id === commentData.parentId)
      if (parentComment) {
        if (!parentComment.comment) parentComment.comment = []
        parentComment.comment.push(newComment)
      }

      // 重置状态
      openReplyPopup.value = false
      replyContent.value = ''
      replyInfo.value = null

      uni.showToast({
        title: '回复成功',
        icon: 'success'
      })
    }
  } catch (error) {
    console.error('发送评论失败:', error)
    uni.showToast({
      title: '回复失败',
      icon: 'none'
    })
  }
}

// 发送一级评论
const handleSendComment = async (commentContent) => {
  if (!checkLogin()) {
    uni.showToast({
      title: '请先登录或者重新登录',
      icon: 'none'
    })
    return
  }
  if (!commentContent) {
    uni.showToast({
      title: '请输入评论内容',
      icon: 'none'
    })
    return
  }

  try {
    const commentData = {
      blogId: content.value[0]?.id,
      content: commentContent,
      parentId: 0
    }

    const res = await add1(commentData)
    if (res.code === 1) {
      // 重置输入框
      inputValue.value = ''

      // 刷新评论列表
      await refreshComments()

      uni.showToast({
        title: '评论成功',
        icon: 'success'
      })
    }
  } catch (error) {
    console.error('发送评论失败:', error)
    uni.showToast({
      title: '评论失败',
      icon: 'none'
    })
  }
}

// 重置回复状态
const resetReplyStatus = () => {
  replyInfo.value = null
  inputPlaceholder.value = '不说点啥子吗？'
}

// 处理评论删除
const handleCommentDelete = async (id) => {
  try {
    uni.showModal({
      title: '提示',
      content: '确定要删除这条评论吗？',
      success: async (res) => {
        if (res.confirm) {
          const result = await delectById({id})
          if (result.code === 1) {
            // 查找评论位置
            const commentResult = findComment(id)

            if (commentResult) {
              if (!commentResult.parentComment) {
                // 删除一级评论
                const index = commentList.value.findIndex(item => item.id === id)
                if (index > -1) {
                  commentList.value.splice(index, 1)
                  // 使用图鸟组件的删除方法
                  commentListRef.value?.deleteCommentReply(id)
                }
              } else {
                // 删除二级评论
                const parentComment = commentResult.parentComment
                const replyIndex = parentComment.comment.findIndex(reply => reply.id === id)
                if (replyIndex > -1) {
                  parentComment.comment.splice(replyIndex, 1)
                  // 使用图鸟组件的删除回复方法
                  commentListRef.value?.deleteCommentReply(id)
                }
              }
            }

            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
          } else {
            uni.showToast({
              title: '删除失败',
              icon: 'none'
            })
          }
        }
      }
    })
  } catch (error) {
    console.error('删除评论失败:', error)
    uni.showToast({
      title: '删除失败',
      icon: 'none'
    })
  }
}

onLoad((options) => {
  // 检查并同步登录状态
  const token = getToken()
  const localUserInfo = getUserInfo()
  if (token && localUserInfo && !isLogin.value) {
    userStore.setToken(token)
    userStore.setUserInfo(localUserInfo)
  }
  const id = options?.id
  console.log('id:', id)
  if (id) {
    getDetail(Number(id))
  }
})
// onMounted(() => {
//   // 检查并同步登录状态
//   const token = getToken()
//   const localUserInfo = getUserInfo()
//   if (token && localUserInfo && !isLogin.value) {
//     userStore.setToken(token)
//     userStore.setUserInfo(localUserInfo)
//   }
//
//   const pages = getCurrentPages()
//   const currentPage = pages[pages.length - 1]
//   const id = currentPage?.options?.id
//   console.log('id:', id)
//   if (id) {
//     getDetail(Number(id))
//   }
// })
</script>

<template>
  <view class="group_detail_container">
    <navbar :title="content[0]?.userName ? content[0]?.userName+ '的讨论' :'...' + '的讨论'" back="left"
            home="home"></navbar>
    <scroll-view scroll-y class="content-scroll">
      <view class="content-wrap">
        <block v-for="(item,index) in content" :key="index">
          <view class="blogger__item">
            <view class="blogger__author tn-flex tn-flex-center-between">
              <view class="justify__author__info">
                <view class="tn-flex tn-flex-start-center">
                  <view class="tn-flex tn-flex-center-center">
                    <view>
                      <tn-avatar
                          class=""
                          shape="circle"
                          :url="item.userAvatar"
                          size="lg">
                      </tn-avatar>
                    </view>
                    <view class="tn-pr tn-text-ellipsis">
                      <view class="tn-pr tn-pl-sm tn-text-bold tn-text-lg">{{ item.userName }}</view>
                      <view class="tn-pr tn-pl-sm tn-pt-xs tn-gray_text">{{ formatTime(item.createTime) }}</view>
                    </view>
                  </view>
                </view>
              </view>
              <view class="blogger__author__btn tn-flex-center-center">
                <text
                    class="tn-round tn-text-df tn-text-bold"
                    :class="isFollowed ? 'tn-gray-light_bg tn-gray_text' : 'tn-brown-light_bg tn-brown_text'"
                    style="padding: 10rpx 24rpx;"
                    @click="handleFollow"
                >
                  {{ isFollowed ? '已关注' : '+ 关注' }}
                </text>
              </view>
            </view>
            <view class="blogger__desc tn-mt-sm tn-mb-sm tn-flex-center-start">
              <view v-for="(label_item,label_index) in item.label" :key="label_index"
                    class="blogger__desc__label tn-mr tn-gray-light_bg tn-round tn-text-sm tn-text-bold">
                <text class="blogger__desc__label--prefix">#</text>
                <text class="">{{ label_item }}</text>
              </view>

            </view>
            <view>
              <text v-if="!item.label || item.label.length < 4"
                    class="blogger__desc__content tn-flex-1 tn-text-bold tn-text-df tn-text-ellipsis-2 tn-mb">
                {{ item.title }}
              </text>
            </view>
            <view>
              <tn-read-more :expand="false">
                <rich-text :nodes="item.content"/>
              </tn-read-more>
            </view>
            <block v-if="item.mainImage">
              <view v-if="[1,2,4].indexOf(item.mainImage.length) !== -1" class="tn-pt-xs">
                <image v-for="(image_item,image_index) in item.mainImage" :key="image_index"
                       class="blogger__main-image"
                       :class="{
                    'blogger__main-image--1 tn-mb-sm': item.mainImage.length === 1,
                    'blogger__main-image--2 tn-mr-sm tn-mb-sm': item.mainImage.length === 2 || item.mainImage.length === 4
                  }"
                       :src="image_item"
                       mode="aspectFill"
                ></image>
              </view>
              <view v-else class="tn-pt-xs">
                <tn-photo-album :data="item.mainImage" :column="3"/>
              </view>
            </block>
            <view class="tn-flex tn-flex-center-between tn-mt">
              <view class="tn-flex tn-flex-center-start">
                <tn-icon name="eye"></tn-icon>
                <text class="tn-gray_text">{{ item.viewUserCount }}人</text>
              </view>
              <view class="tn-gray_text tn-text-center">
                <view class="">
                  <tn-icon name="star"></tn-icon>
                  <text class="tn-pr">{{ item.collectionCount }}</text>
                  <tn-icon name="message"></tn-icon>
                  <text class="tn-pr">{{ item.comments }}</text>
                  <tn-icon name="like"></tn-icon>
                  <text class="">{{ item.liked }}</text>
                </view>
              </view>
            </view>
          </view>
        </block>
        <view class="tn-flex tn-flex-start-center" style="margin: 40rpx 0 60rpx 0;">
          <view class="tn-m-xs tn-text-center" style="width: 50%">
            <tn-button class="tn-flex-1"
                       :bg-color="isLike ? '#FFE0E0' : '#00FFC6'"
                       padding="40rpx 0"
                       width="90%"
                       height="80rpx"
                       shadow
                       bold
                       @click="handleLike">
              <tn-icon :name="isLike ? 'like-fill' : 'like'" class="tn-pr-xs tn-black_text"></tn-icon>
              <text class="tn-black_text">{{ isLike ? '已点赞' : '点 赞' }}</text>
            </tn-button>
          </view>
          <view class=" tn-m-xs tn-text-center" style="width: 50%">
            <tn-button class="tn-flex-1" bg-color="#FFF00D" padding="40rpx 0" width="90%" height="80rpx" shadow bold
                       open-type="share">
              <tn-icon name="share" class="tn-pr-xs tn-black_text"></tn-icon>
              <text class="tn-black_text">分 享</text>
            </tn-button>
          </view>

          <view class=" tn-m-xs tn-text-center" style="width: 50%">
            <tn-button class="tn-flex-1" bg-color="#32c0fd" padding="40rpx 0" width="90%" height="80rpx" shadow bold
                       open-type="share">
              <tn-icon name="star" class="tn-pr-xs tn-black_text"></tn-icon>
              <text class="tn-black_text">收 藏</text>
            </tn-button>
          </view>
        </view>

        <view class="comment-list">
          <view class="tn-ml">
            <black-title name="comment" title="评论"/>
          </view>
          <TnCommentList
              ref="commentListRef"
              @like="handleCommentLike"
              @reply="handleCommentReply"
              @delete="handleCommentDelete"
              :dislike-icon="false"
              :data="commentList"
              @show-more="showMoreClickHandle"
          />
        </view>
        <view style="height: 120rpx;"></view>
      </view>
    </scroll-view>
    <view class="comment-input-wrap">
      <commentInput
          v-model="inputValue"
          :placeholder="isLogin ? inputPlaceholder : '请先登录后发表评论'"
          @send="handleSendComment"
          @blur="resetReplyStatus"
          :disabled="!isLogin"
      />
    </view>

    <!-- 回复弹出框 -->
    <tn-popup v-model="openReplyPopup" open-direction="center" width="90%">
      <view class="reply-container">
        <view class="tn-input-wrap">
          <tn-input
              v-model="replyContent"
              type="text"
              :placeholder="'回复 ' + replyCommentNickname + ':'"
              :focus="openReplyPopup"
          />
        </view>
        <view class="tn-flex justify-end tn-mt">
          <view class="tn-flex-center-center tn-mr">
            <view class="topic__info__item__sure">
              <view class="tn-flex-1 tn-text-center" style="margin-top: 10rpx">
                <tn-button shape="round" bg-color="tn-gradient-bg__cool-15" width="100%" shadow
                           @click="handleReplyComment">
                  <text class="tn-white_text" hover-class="tn-hover" :hover-stay-time="150">
                    发送回复
                  </text>
                </tn-button>
              </view>
            </view>
          </view>
          <!--          <tn-button  bg-color="tn-type-primary">发送回复</tn-button>-->
        </view>
      </view>
    </tn-popup>
  </view>
</template>

<style scoped lang="scss">
.group_detail_container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #fff;
}

.content-scroll {
  flex: 1;
  height: 0;
}

.content-wrap {
  padding-bottom: env(safe-area-inset-bottom);
}

.comment-input-wrap {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 99;
}

/* 文章内容 start*/
.blogger {
  &__item {
    padding: 30rpx;
  }

  &__author {
    &__btn {
      margin-right: -12rpx;
      padding: 0 20rpx;
    }
  }

  &__desc {
    line-height: 55rpx;

    &__label {
      padding: 0 20rpx;
      margin: 0rpx 18rpx 0 0;

      &--prefix {
        color: #00FFC8;
        padding-right: 10rpx;
      }
    }

    &__content {

    }
  }

  &__content {
    margin-top: 18rpx;
    padding-right: 18rpx;

    &__data {
      line-height: 46rpx;
      text-align: justify;
      overflow: hidden;
      transition: all 0.25s ease-in-out;

    }

    &__status {
      margin-top: 10rpx;
      font-size: 26rpx;
      color: #82B2FF;
    }
  }

  &__main-image {
    border-radius: 16rpx;

    &--1 {
      max-width: 80%;
      max-height: 300rpx;
    }

    &--2 {
      max-width: 260rpx;
      max-height: 260rpx;
    }

    &--3 {
      height: 212rpx;
      width: 100%;
    }
  }

  &__count-icon {
    font-size: 40rpx;
    padding-right: 5rpx;
  }

  &__ad {
    width: 100%;
    height: 500rpx;
    transform: translate3d(0px, 0px, 0px) !important;

    ::v-deep .uni-swiper-slide-frame {
      transform: translate3d(0px, 0px, 0px) !important;
    }

    .uni-swiper-slide-frame {
      transform: translate3d(0px, 0px, 0px) !important;
    }

    &__item {
      position: absolute;
      width: 100%;
      height: 100%;
      transform-origin: left center;
      transform: translate3d(100%, 0px, 0px) scale(1) !important;
      transition: transform 0.25s ease-in-out;
      z-index: 1;

      &--0 {
        transform: translate3d(0%, 0px, 0px) scale(1) !important;
        z-index: 4;
      }

      &--1 {
        transform: translate3d(13%, 0px, 0px) scale(0.9) !important;
        z-index: 3;
      }

      &--2 {
        transform: translate3d(26%, 0px, 0px) scale(0.8) !important;
        z-index: 2;
      }
    }

    &__content {
      border-radius: 40rpx;
      width: 640rpx;
      height: 500rpx;
      overflow: hidden;
    }

    &__image {
      width: 100%;
      height: 100%;
    }
  }
}

/* 文章内容 end*/

/* 间隔线 start*/
.tn-strip-bottom {
  width: 100%;
  border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
}

/* 间隔线 end*/

/* 头像 start */
.logo-image {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
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

.reply-container {
  padding: 30rpx;
  background-color: #fff;
  border-radius: 24rpx;

  .tn-input-wrap {
    margin-bottom: 20rpx;
  }

  .reply-input {
    width: 100%;
    height: 80rpx;
    padding: 20rpx;
    border: 1px solid #eee;
    border-radius: 12rpx;
    font-size: 28rpx;
  }
}

</style>