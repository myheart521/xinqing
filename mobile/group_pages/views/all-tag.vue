<script setup>
import {ref, watchEffect, onMounted, onUnmounted} from 'vue'
import navbar from '@/components/navbar.vue'
import {queryTagAll} from '@/service/api/tagController'
import {onLoad} from '@dcloudio/uni-app'

const tagTypeList = ref([])
const selectValue = ref([])
const modalRef = ref(null)
const tagList = ref([])

// 获取所有标签
const getTagList = async () => {
  try {
    const res = await queryTagAll()
    if (res.code === 1) {
      const formattedData = formatTagData(res.data)
      tagTypeList.value = formattedData
      console.log('获取所有标签', tagTypeList.value)
    } else {
      uni.showToast({
        title: '获取标签失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取标签失败:', error)
    uni.showToast({
      title: '获取标签失败',
      icon: 'none'
    })
  }
}

// 格式化标签数据
const formatTagData = (data) => {
  return data.map(item => ({
    title: item.title,
    list: item.list.map(tag => ({
      color: tag.color || 'blue',
      title: tag.title
    }))
  }))
}

// 确认选择
const upload = () => {
  // #ifdef MP-WEIXIN
  const pages = getCurrentPages()
  const prevPage = pages[pages.length - 2]
  // 获取所有选中的标签信息
  const selectedTags = getAllSelectedTags()
  try {
    if (prevPage?.$vm) {
      // 更新选中的标签
      prevPage.$vm.selectValue = selectValue.value
      // 更新标签列表 - 使用 selectedTags 而不是 tagList
      const existingTags = prevPage.$vm.tagList || []
      const newTags = [...existingTags]
      tagList.forEach(tag => {
        if (!newTags.some(t => t.title === tag.title)) {
          newTags.push(tag)
        }
      })
      // 更新上一页的标签列表
      prevPage.$vm.tagList = newTags
      console.log("selectValue.value：",selectValue.value,"selectedTags:",selectedTags)
    }
  } catch (error) {
    console.error("更新上一页数据失败", error)
  }
  // #endif
  // #ifndef MP-WEIXIN
  console.log("这里是除了微信都可以访问的")
  uni.$on('updateTags',{
    selectValue: selectValue.value,
    newTags: selectedTags
  })
  // #endif
  uni.navigateBack({
    delta: 1,
    success: () => {
      console.log("返回成功")
    },
    fail: (err) => {
      console.error("返回失败", err)
    }
  })
}

// 获取所有选中的标签信息
const getAllSelectedTags = () => {
  const selectedTags = []
  tagTypeList.value.forEach(category => {
    category.list.forEach(tag => {
      if (selectValue.value.includes(tag.title)) {
        selectedTags.push(tag)
      }
    })
  })
  return selectedTags
}

// 使用 onLoad 接收路由参数
onLoad((options) => {
  if (options.selected) {
    try {
      // 设置已选标签
      const selected = JSON.parse(decodeURIComponent(options.selected))
      selectValue.value = selected
    } catch (e) {
      console.error('解析已选标签失败:', e)
    }
  }

  if (options.tagList) {
    try {
      // 获取标签列表 - 修改这里的赋值方式
      const parsedTagList = JSON.parse(decodeURIComponent(options.tagList))
      // 正确的赋值方式是给 .value 属性赋值
      tagList.value = parsedTagList
    } catch (e) {
      console.error('解析标签列表失败:', e)
    }
  }

  // 获取所有标签
  getTagList()
})

//监听选择的长度
watchEffect(() => {
  if (selectValue.value.length > 2) {
    modalRef.value.showModal({
      title: '提示',
      content: '最多只能选择两个标签',
    })
    selectValue.value.pop()
  }
})

// 移除 onMounted 和 onUnmounted 中的事件监听，因为我们使用直接更新的方式
onMounted(() => {
  getTagList()
})

</script>

<template>
  <view class="all_tag_container tn-u-safe-area--more">
    <navbar title="全部标签"></navbar>
    <view v-for="(item,index) in tagTypeList" :key="index">
      <!--标题-->
      <view class="tn-m">
        <tn-title mode="vLine" size="xl" :title="item.title" assist-color="tn-gradient-bg__cool-6"></tn-title>
      </view>
      <view class="tn-tag-content tn-m tn-pb">
        <tn-checkbox-group v-model="selectValue">
          <tn-checkbox v-for="(item1,index1) in item.list" :key="item1.title"
                       :label="item1.title">
            <view
                class="tn-tag-content__item tn-round tn-text-sm tn-text-bold"
                :class="[`tn-gradient-bg__${item1.color}-light tn-${item1.color}_text`]">
              <text class="tn-tag-content__item--prefix">#</text>
              <text>{{ item1.title }}</text>
            </view>
          </tn-checkbox>
        </tn-checkbox-group>
      </view>
    </view>
    <!-- 悬浮按钮-->
    <view class="tn-flex tn-footerfixed">
      <view class="tn-flex-1 tn-m-sm tn-text-center tn-flex-center-center">
        <tn-button bg-color="#00FFC6" height="80rpx" padding="40rpx 0" width="60%" shadow bold
                   @tap="upload">
          <text class="tn-black_text">确 认</text>
          <tn-icon name="camera" class="tn-pl-xs tn-black_text"></tn-icon>
        </tn-button>
      </view>
    </view>
    <view style="margin-top: 40px"></view>
    <!--模态框-->
    <tn-modal ref="modalRef"/>
  </view>

</template>

<style scoped lang="scss">
/* 标签内容 start*/
.tn-tag-content {
  &__item {
    display: inline-block;
    line-height: 45rpx;
    padding: 5rpx 15rpx;
    margin: 10rpx 5rpx 2rpx 0rpx;

    &--prefix {
      padding-right: 10rpx;
    }
  }
}

/* 标签内容 end*/

.tn-tabbar-height {
  min-height: 100rpx;
  height: calc(120rpx + env(safe-area-inset-bottom) / 2);
}

.tn-footerfixed {
  position: fixed;
  width: 100%;
  bottom: calc(30rpx + env(safe-area-inset-bottom));
  z-index: 1024;
  box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0);

}
</style>