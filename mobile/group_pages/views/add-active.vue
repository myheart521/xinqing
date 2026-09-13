<script setup>
import navbar from '@/components/navbar.vue'
import {ref, watchEffect, onMounted, computed} from 'vue'
import QQMapWX from '@/common/qqmap-wx-jssdk.js';
import {TENGXUN_MAP_KEY} from "@/common/TengXunMapConstant";
import {add3} from '@/service/api/activityController'
import {queryTagAll, queryTagTop} from '@/service/api/tagController'

import {useUserStore} from "@/stores/user"
import {requestBaseUrl, requestUrl} from "@/utils/URL";
import {watch} from "@/service/api/sportController";

const userStore = useUserStore()

//上传图片的url地址
const action = ref(`${requestUrl}/common/upload`)
const imageUpload = ref(null)
const formData = ref({
  title: '',
  content: '',
  imageList: [],
  startTime: '',
  endTime: '',
  label: '',
  color: 'red', // 默认颜色
  address: '',
  longitude: '',
  latitude: '',
})

const fileList = ref([])
const tagList = ref([])
const autoUpload = ref(true)
const showProgress = ref(false)
const maxCount = ref(4)
const disabled = ref(false)
//日期
const openStartDateTimePicker = ref(false)
const openEndDateTimePicker = ref(false)
const startTimeValue = ref('')
const endTimeValue = ref('')

//位置
const longitude = ref()
const latitude = ref()
//位置信息默认数据
let getLocationInfo = ref({
  longitude: 0,
  latitude: 0,
  province: "",
  city: "",
  area: "",
  street: "",
  address: "",
  formatted_addresses: ""
})


const choseLocationInfo = ref({
  title: '',
  address: '',
  latitude: '',
  longitude: '',
})

// 手动上传文件
const upload = () => {
  imageUpload.value.upload()
}
// 手动清空列表
const clear = () => {
  imageUpload.value.clear()
}

const tn = (router) => {
  if (router === '/group_pages/views/all-tag') {
    // 将当前选中的标签和标签列表都传递给 all-tag 页面
    uni.navigateTo({
      url: `${router}?selected=${encodeURIComponent(JSON.stringify(selectValue.value))}&tagList=${encodeURIComponent(JSON.stringify(tagList.value))}`
    })
  } else {
    uni.navigateTo({url: router})
  }
}

//获取地址
const getLocation = () => {
  return new Promise((resolve, reject) => {
    uni.showLoading({
      title: '正在获取定位中...'
    })
    uni.getLocation({
      type: 'gcj02',
      isHighAccuracy: true,
      geocode: 'true',
      highAccuracyExpireTime: 3500,
      success: (res) => {
        uni.hideLoading()
        getLocationInfo.value.longitude = res.longitude
        getLocationInfo.value.latitude = res.latitude
        longitude.value = res.longitude
        latitude.value = res.latitude

        // 更新表单数据
        formData.value.longitude = res.longitude
        formData.value.latitude = res.latitude

        console.log('获取位置成功', res)
        // 腾讯地图Api
        const qqmapsdk = new QQMapWX({
          key: TENGXUN_MAP_KEY
        });
        let location = {
          longitude: getLocationInfo.value.longitude,
          latitude: getLocationInfo.value.latitude
        }
        qqmapsdk.reverseGeocoder({
          location,
          success(response) {
            let info = response.result;
            console.log("逆地址解析结果：", info);
            getLocationInfo.value.province = info.address_component.province;
            getLocationInfo.value.city = info.address_component.city;
            getLocationInfo.value.area = info.address_component.district;
            getLocationInfo.value.street = info.address_component.street;
            getLocationInfo.value.address = info.address;
            getLocationInfo.value.formatted_addresses = info.formatted_addresses.recommend;

            // 更新选择地址和表单数据
            choseLocationInfo.value = {
              title: info.formatted_addresses.recommend,
              address: info.address,
              latitude: getLocationInfo.value.latitude,
              longitude: getLocationInfo.value.longitude,
            }

            // 更新表单地址
            formData.value.address = info.address

            resolve(location);
          },
          fail: (err) => {
            console.log('地址反向解析失败', err)
            reject('地址反向解析失败');
          }
        });
      },
      fail: (res) => {
        uni.hideLoading()
        uni.showToast({
          icon: 'none',
          title: '获取地址失败,请稍后再试',
          duration: 2000
        });
        console.log('获取位置失败', res)
        if (res.errMsg == 'getLocation:fail auth deny') {
          uni.showModal({
            content: '检测到您没打开获取信息功能权限，是否去设置打开？',
            confirmText: '确认',
            cancelText: '取消',
            success: (res) => {
              if (res.confirm) {
                uni.openSetting({
                  success: (res) => {
                  }
                })
              }
            }
          })
        }
        reject(res)
      }
    })
  });
}


// 选择地址、修改编辑地址
const chooseAddressView = () => {
  uni.chooseLocation({
    longitude: getLocationInfo.value.longitude,
    latitude: getLocationInfo.value.latitude,
    success: (res) => {
      if (!res.name || !res.address || !res.latitude || !res.longitude) return
      console.log("选择地址结果", res)
      const addressObj = {
        title: res.name,
        address: res.address,
        latitude: res.latitude,
        longitude: res.longitude,
      }
      // 更新表单数据
      formData.value.address = res.address
      formData.value.longitude = res.longitude
      formData.value.latitude = res.latitude

      // 更新显示数据
      latitude.value = res.latitude
      longitude.value = res.longitude
      Object.assign(choseLocationInfo.value, addressObj)
    },
  })
}

const modalRef = ref(null)
const selectValue = ref([])
const clearClick = (event) => {
  console.log("阻止事件冒泡啊", event)
  event.stopPropagation();
  choseLocationInfo.value = {
    title: '',
    address: '',
    latitude: '',
    longitude: '',
  }
}

//监听选择的长度
watchEffect(() => {
  if (selectValue.value.length > 1) {
    modalRef.value.showModal({
      title: '提示',
      content: '最多只能选择一个标签',
    })
    selectValue.value.pop()
  }
})

// 自定义上传回调函数
const customUploadCallback = (data) => {
  try {
    const res = JSON.parse(data.data)
    console.log('上传回调', res)
    if (res.code === 1 && res.data) {
      formData.value.imageList.push(res.data)
      return res.data
    } else {
      uni.showToast({
        title: '上传失败',
        icon: 'none'
      })
      return Promise.reject(new Error('上传失败'))
    }
  } catch (e) {
    uni.showToast({
      title: '上传失败',
      icon: 'none'
    })
    return Promise.reject(e)
  }
}

// 发布活动
const publishActivity = async () => {
  // 表单验证
  if (!formData.value.title) {
    uni.showToast({
      title: '请填写活动标题',
      icon: 'none'
    })
    return
  }

  if (!formData.value.content) {
    uni.showToast({
      title: '请填写活动详情',
      icon: 'none'
    })
    return
  }

  if (!formData.value.startTime || !formData.value.endTime) {
    uni.showToast({
      title: '请选择活动时间',
      icon: 'none'
    })
    return
  }

  if (!formData.value.address) {
    uni.showToast({
      title: '请选择活动地点',
      icon: 'none'
    })
    return
  }

  if (selectValue.value.length === 0) {
    uni.showToast({
      title: '请选择活动标签',
      icon: 'none'
    })
    return
  }

  try {
    // 构造请求数据
    const activityData = {
      title: formData.value.title,
      content: formData.value.content,
      imageList: formData.value.imageList,
      startTime: formData.value.startTime,
      endTime: formData.value.endTime,
      label: selectValue.value[0], // 取第一个选中的标签作为主标签
      color: 'red', // 默认红色
      address: formData.value.address,
      longitude: formData.value.longitude,
      latitude: formData.value.latitude
    }

    console.log('提交的活动数据：', activityData)
    const res = await add3(activityData)

    if (res.code === 1) {
      uni.showToast({
        title: '发布成功',
        icon: 'success'
      })

      // 清空表单
      formData.value = {
        title: '',
        content: '',
        imageList: [],
        startTime: '',
        endTime: '',
        label: '',
        color: 'red',
        address: '',
        longitude: '',
        latitude: ''
      }

      // 清空其他相关数据
      selectValue.value = []
      fileList.value = []
      startTimeValue.value = ''
      endTimeValue.value = ''
      choseLocationInfo.value = {
        title: '',
        address: '',
        latitude: '',
        longitude: ''
      }

      // 返回上一页
      uni.navigateBack()
    } else {
      uni.showToast({
        title: res.msg || '发布失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('发布活动失败:', error)
    uni.showToast({
      title: '发布失败',
      icon: 'none'
    })
  }
}

// 时间格式转换函数
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace(' ', 'T')
}

// 处理开始时间选择
const handleStartTimeChange = (value) => {
  startTimeValue.value = value
  formData.value.startTime = formatDateTime(value)
}

// 处理结束时间选择
const handleEndTimeChange = (value) => {
  endTimeValue.value = value
  formData.value.endTime = formatDateTime(value)
}

// 处理开始时间确认
const handleStartTimeConfirm = (value) => {
  startTimeValue.value = value
  formData.value.startTime = formatDateTime(value)
  openStartDateTimePicker.value = false
}

// 处理结束时间确认
const handleEndTimeConfirm = (value) => {
  endTimeValue.value = value
  formData.value.endTime = formatDateTime(value)
  openEndDateTimePicker.value = false
}

// 打开开始时间选择器
const openStartTimePicker = () => {
  openStartDateTimePicker.value = true
}

// 打开结束时间选择器
const openEndTimePicker = () => {
  openEndDateTimePicker.value = true
}

// 清空图片
const clearUpload = () => {
  if (imageUpload.value) {
    imageUpload.value.clear()
    formData.value.imageList = []
    fileList.value = []
  }
}

// 获取热门标签
// const getTopTags = async () => {
//   try {
//     const res = await queryTagTop()
//     console.log('获取热门标签', res)
//     if (res.code === 1) {
//       // 保存当前已选标签
//       const currentSelectedTags = tagList.value.filter(tag => selectValue.value.includes(tag.title))
//       // 格式化热门标签数据
//       const topTags = res.data.map(tag => ({
//         color: tag.color || 'blue',
//         title: tag.title
//       }))
//       // 合并已选标签和热门标签
//       const newTags = [...currentSelectedTags]
//       topTags.forEach(tag => {
//         if (!newTags.some(t => t.title === tag.title)) {
//           newTags.push(tag)
//         }
//       })
//       tagList.value = newTags
//     } else {
//       uni.showToast({
//         title: '获取热门标签失败',
//         icon: 'none'
//       })
//     }
//   } catch (error) {
//     console.error('获取热门标签失败:', error)
//     uni.showToast({
//       title: '获取热门标签失败',
//       icon: 'none'
//     })
//   }
// }
//

// 获取所有标签
const getTagList = async () => {
  try {
    const res = await queryTagAll()
    console.log("获取结果：", res)
    if (res.code === 1) {
      // const formatted = formatTagData(res.data)
      tagList.value = res.data
      console.log('获取所有标签', tagList.value)
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
  const newData = []
  data.forEach((item) => {
    newData.push(item.list)
  })
  // const newData = data.map(item => ({
  //   title: item.title,
  //   list: item.list.map(tag => ({
  //     color: tag.color || 'blue',
  //     title: tag.title
  //   }))
  // }))
  //去掉title转换为数组对象
  return newData

}
// 添加计算属性获取已选标签
const selectedTags = computed(() => {
  return tagList.value.filter(tag => selectValue.value.includes(tag.title))
})

// 清空已选标签
const clearTags = () => {
  uni.showModal({
    title: '提示',
    content: '确定清空已选标签吗？',
    success: (res) => {
      if (res.confirm) {
        selectValue.value = []
      }
    }
  })
}

defineExpose({
  tagList,
  selectValue,
  selectedTags
})


onMounted(async () => {
  try {
    // 获取位置信息
    await getLocation()

    // 设置默认的开始时间（当前时间）和结束时间（当前时间加1小时）
    const now = new Date()
    const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000)

    const formatDate = (date) => {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }

    // 设置开始时间
    const startTime = formatDate(now)
    handleStartTimeChange(startTime)

    // 设置结束时间
    const endTime = formatDate(oneHourLater)
    handleEndTimeChange(endTime)

    // 获取热门标签
    await getTagList()
  } catch (error) {
    console.error('初始化失败:', error)
  }
})
</script>

<template>
  <view class="add_active_container">
    <navbar title="发起活动"></navbar>
    <view class="tn-u-safe-area--more">

      <view class="tn-m tn-gray-light_bg" style="border-radius: 10rpx;padding: 20rpx 30rpx;">
        <input placeholder="请填写活动标题吖"
               v-model="formData.title"
               name="input"
               placeholder-style="color:#AAAAAA"/>
      </view>

      <view class="tn-m tn-gray-light_bg tn-p" style="border-radius: 10rpx;">
        <textarea maxlength="500"
                  placeholder="请描述活动详情"
                  placeholder-style="color:#AAAAAA"
                  v-model="formData.content">
        </textarea>
      </view>

      <view class="tn-flex tn-flex-center-between tn-pt-xl tn-m">
        <view class="tn-flex">
          <view class="tn-black_bg tn-white_text tn-text-center"
                style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
            <tn-icon name="image" style="font-size: 30rpx;"></tn-icon>
          </view>
          <view class="tn-text-lg tn-pr-xs tn-text-bold">活动图片</view>
        </view>
        <view class="tn-gray_text" @tap="clear">
          <text class="tn-p-xs">清空上传</text>
          <tn-icon name="delete"></tn-icon>
        </view>
      </view>

      <view class="tn-ml tn-pt-xs">
        <tn-image-upload
            ref="imageUpload"
            :action="action"
            v-model="fileList"
            :disabled="disabled"
            :auto-upload="autoUpload"
            :limit="maxCount"
            :show-upload-progress="showProgress"
            :custom-upload-callback="customUploadCallback">
        </tn-image-upload>
      </view>
      <!--活动时间-->
      <view class="tn-flex tn-flex-col tn-flex-center-between tn-pt-xl tn-m">
        <view class="tn-flex tn-flex-row-between">
          <view class="tn-flex">
            <view class="tn-black_bg tn-white_text tn-text-center"
                  style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
              <tn-icon name="time" style="font-size: 30rpx;"></tn-icon>
            </view>
            <view class="tn-text-lg tn-pr-xs tn-text-bold">活动时间</view>
          </view>
        </view>
        <view class="tn-flex tn-flex-center tn-flex-col tn-margin-top">
          <view class="tn-gray_text tn-margin-bottom" @click="openStartTimePicker">
            <text class="tn-text-bold">开始时间:</text>
            <text class="tn-p-xs">{{ startTimeValue || '请选择' }}</text>
          </view>
        </view>
      </view>

      <!--活动时间-->
      <view class="tn-flex tn-flex-col tn-flex-center-between tn-pt-xl tn-m">
        <view class="tn-flex tn-flex-row-between">
          <view class="tn-flex">
            <view class="tn-black_bg tn-white_text tn-text-center"
                  style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
              <tn-icon name="time" style="font-size: 30rpx;"></tn-icon>
            </view>
            <view class="tn-text-lg tn-pr-xs tn-text-bold">活动时间</view>
          </view>
        </view>
        <view class="tn-flex tn-flex-center  tn-flex-col tn-margin-top">
          <view class="tn-gray_text" @click="openEndTimePicker">
            <text class="tn-text-bold">结束时间:</text>
            <text class="tn-p-xs">{{ endTimeValue || '请选择' }}</text>
          </view>
        </view>
      </view>

      <!--活动地点-->
      <view class="tn-flex tn-flex-center-between tn-pt-xl tn-m">
        <view class="tn-flex">
          <view class="tn-black_bg tn-white_text tn-text-center"
                style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
            <tn-icon name="location" style="font-size: 30rpx;"></tn-icon>
          </view>
          <view class="tn-text-lg tn-pr-xs tn-text-bold">活动地点</view>
        </view>
        <view class="tn-gray_text tn-flex tn-flex-center-end" style="width: 70%" @click="chooseAddressView">
          <text class="tn-p-xs">
            {{
              (choseLocationInfo.address ? choseLocationInfo.address : '') +
              (choseLocationInfo.title ? choseLocationInfo.title : '') || '点击选择'
            }}
          </text>
          <tn-icon v-if="!choseLocationInfo.address" name="right"></tn-icon>
          <tn-icon v-else name="close" @tap.prevent="clearClick"></tn-icon>
        </view>
      </view>

      <!--活动标签-->
      <view class="tn-flex tn-flex-center-between tn-pt-xl tn-m">
        <view class="tn-flex">
          <view class="tn-black_bg tn-white_text tn-text-center"
                style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
            <tn-icon name="tag" style="font-size: 30rpx;"></tn-icon>
          </view>
          <view class="tn-text-lg tn-pr-xs tn-text-bold">活动主题</view>
        </view>
        <!--        <view class="tn-gray_text" @click="tn('/group_pages/views/all-tag')">-->
        <!--          <text class="tn-p-xs">所有标签</text>-->
        <!--          <tn-icon name="right"></tn-icon>-->
        <!--        </view>-->
      </view>

      <!-- 已选标签展示区域 -->
      <!--      <view v-if="selectValue.length > 0" class="tn-m tn-pb">-->
      <!--        <view class="tn-flex tn-flex-center-between tn-mb">-->
      <!--          <view class="tn-flex">-->
      <!--            <view class="tn-black_bg tn-white_text tn-text-center"-->
      <!--                  style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">-->
      <!--              <tn-icon name="tag" style="font-size: 30rpx;"></tn-icon>-->
      <!--            </view>-->
      <!--            <view class="tn-text-lg tn-pr-xs tn-text-bold">已选标签</view>-->
      <!--          </view>-->
      <!--          <view class="tn-text-df tn-gray_text" @tap="clearTags">-->
      <!--            <text class="tn-p-xs">清空</text>-->
      <!--            <tn-icon name="delete"></tn-icon>-->
      <!--          </view>-->
      <!--        </view>-->
      <!--        <view class="tn-flex tn-flex-wrap">-->
      <!--          <view v-for="(tag, index) in selectedTags" :key="index"-->
      <!--                class="tn-tag-content__item tn-round tn-text-sm tn-text-bold tn-mr-sm tn-mb-sm"-->
      <!--                :class="[`tn-gradient-bg__${tag.color}-light tn-${tag.color}_text`]">-->
      <!--            <text class="tn-tag-content__item&#45;&#45;prefix">#</text>-->
      <!--            <text>{{ tag.title }}</text>-->
      <!--          </view>-->
      <!--        </view>-->
      <!--      </view>-->

      <!-- 标签列表选择区域 -->
      <view class="tn-tag-content tn-m tn-pb">
        <view v-for="(tag,tagIndex) in tagList" :key="tagIndex">
          <view class="tn-text-bold tn-text-lg tn-mb-sm">{{ tag.title }}</view>
          <tn-checkbox-group v-model="selectValue">
            <tn-checkbox v-for="(item, index) in tag.list" :key="item.title"
                         :label="item.title">
              <view
                  class="tn-tag-content__item tn-round tn-text-sm tn-text-bold"
                  :class="[`tn-gradient-bg__${item.color}-light tn-${item.color}_text`]">
                <text class="tn-tag-content__item--prefix">#</text>
                <text>{{ item.title }}</text>
              </view>
            </tn-checkbox>
          </tn-checkbox-group>
        </view>
      </view>

      <view>
        <!--日期选择-->
        <tn-date-time-picker
            v-model="startTimeValue"
            v-model:open="openStartDateTimePicker"
            mode="datetimeNoSecond"
            format="YYYY-MM-DD HH:mm"
            :init-current-date-time="true"
            :min-time="new Date()"
            :mask="true"
            confirm-color="blue"
            @change="handleStartTimeChange"
            @confirm="handleStartTimeConfirm"
        />
      </view>

      <view>
        <!-- 结束时间选择器 -->
        <tn-date-time-picker
            v-model="endTimeValue"
            v-model:open="openEndDateTimePicker"
            mode="datetimeNoSecond"
            format="YYYY-MM-DD HH:mm"
            :init-current-date-time="true"
            :min-time="startTimeValue || new Date()"
            :mask="true"
            confirm-color="blue"
            @change="handleEndTimeChange"
            @confirm="handleEndTimeConfirm"
        />
      </view>

      <!-- 悬浮按钮-->
      <view class="tn-flex tn-footerfixed">
        <view class="tn-flex-1 tn-m-sm tn-text-center tn-flex-center-center">
          <tn-button bg-color="#00FFC6" height="80rpx" padding="40rpx 0" width="60%" shadow bold @tap="publishActivity">
            <text class="tn-black_text">发 起</text>
            <tn-icon name="camera" class="tn-pl-xs tn-black_text"></tn-icon>
          </tn-button>
        </view>
      </view>

      <!--模态框-->
      <tn-modal ref="modalRef"/>

    </view>

    <view class='tn-tabbar-height'></view>

  </view>

</template>

<style scoped lang="scss">

/* 标签内容 start*/
.tn-tag-content {
  &__item {
    display: inline-block;
    line-height: 45rpx;
    padding: 10rpx 30rpx;
    margin: 20rpx 20rpx 5rpx 0rpx;

    &--prefix {
      padding-right: 10rpx;
    }
  }
}

/* 标签内容 end*/

/* 底部悬浮按钮 start*/
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

/* 底部悬浮按钮 end*/

.tn-margin-top {
  margin-top: 20rpx;
}

.tn-margin-bottom {
  margin-bottom: 20rpx;
}

.tn-mb {
  margin-bottom: 20rpx;
}

.tn-mr-sm {
  margin-right: 10rpx;
}

.tn-mb-sm {
  margin-bottom: 10rpx;
}

</style>