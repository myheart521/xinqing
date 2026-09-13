<script setup>
import {ref, computed, onMounted, reactive} from 'vue'
import navbar from '@/components/navbar.vue'
import { province, universities } from '@/service/api/userController'
import TnImageUpload from '@tuniao/tnui-vue3-uniapp/components/image-upload/src/image-upload.vue'

// 用户信息
const userProfile = ref('')
const tempProfile = ref('')
const avatars = ref([])
const school = ref('')
const tempSchool = ref('')
const phone = ref('')
const sex = ref('')
const tempSex = ref('')
const provinceData = ref([])
const selectedProvince = ref('')
const selectedProvinceId = ref('')

// 图片上传相关
const imageUploadRef = ref(null)
const fileList = ref([])
const action = 'http://localhost:8080/api/common/upload' // 上传图片API地址
const formData = reactive({
  image: null,
  token: '' // 如果需要Token，可以从用户Store中获取
})
const autoUpload = true
const showProgress = true
const maxCount = 4

// 学校相关
const schoolList = ref([])
const tempSchoolList = ref([])
const searchSchool = ref('')
const schoolPage = reactive({
  pageNo: 1,
  pageSize: 10,
  loading: false,
  hasMore: true
})

// 弹窗引用
const profileModalRef = ref(null)
const sexModalRef = ref(null)
const schoolModalRef = ref(null)
const provinceModalRef = ref(null)

// 信息列表
const infoList = computed(() => [
  {
    title: '学校',
    value: school.value,
    placeholder: '添加学校信息',
    type: 'school'
  },
  {
    title: '手机号',
    value: phone.value,
    placeholder: '绑定手机号',
    type: 'phone'
  },
  {
    title: '性别',
    value: sex.value,
    placeholder: '选择性别',
    type: 'sex'
  },
  {
    title: '省份',
    value: selectedProvince.value || '',
    placeholder: '选择所在省份',
    type: 'province'
  }
])

// 获取省份列表
const getProvinceList = async () => {
  try {
    const res = await province()
    if (res && res.code === 1) {
      provinceData.value = res.data || []
    } else {
      uni.showToast({
        title: res?.msg || '获取省份失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取省份列表失败:', error)
    uni.showToast({
      title: '获取省份失败',
      icon: 'none'
    })
  }
}

// 根据省份获取学校列表
const getSchoolList = async (provinceId, isRefresh = false) => {
  if (schoolPage.loading) return
  
  schoolPage.loading = true
  
  if (isRefresh) {
    schoolPage.pageNo = 1
    tempSchoolList.value = []
  }
  
  try {
    const res = await universities({
      pageNo: schoolPage.pageNo.toString(),
      pageSize: schoolPage.pageSize.toString(),
      provinceId: provinceId
    })
    
    if (res && res.code === 1) {
      const schools = res.data || []
      tempSchoolList.value = [...tempSchoolList.value, ...schools]
      
      schoolPage.hasMore = schools.length === schoolPage.pageSize
      if (schoolPage.hasMore) {
        schoolPage.pageNo++
      }
    } else {
      uni.showToast({
        title: res?.msg || '获取学校失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取学校列表失败:', error)
    uni.showToast({
      title: '获取学校失败',
      icon: 'none'
    })
  } finally {
    schoolPage.loading = false
  }
}

// 自定义图片上传方法
const customUploadHandler = (file) => {
  return new Promise((resolve, reject) => {
    const filePath = file.path
    
    uni.uploadFile({
      url: action,
      filePath,
      name: 'image',
      formData,
      success: (res) => {
        try {
          // 尝试解析响应数据
          if (res.data) {
            const data = JSON.parse(res.data)
            if (data.code === 1 && data.data) {
              // 将上传的图片添加到头像列表中
              avatars.value.push(data.data)
              resolve(data.data)
            } else {
              uni.showToast({
                title: data.msg || '上传失败',
                icon: 'none'
              })
              reject(new Error('上传失败'))
            }
          } else {
            uni.showToast({
              title: '服务器返回数据为空',
              icon: 'none'
            })
            reject(new Error('服务器返回数据为空'))
          }
        } catch (e) {
          console.error('解析上传响应数据失败:', e, res)
          uni.showToast({
            title: '解析上传响应失败',
            icon: 'none'
          })
          reject(e)
        }
      },
      fail: (err) => {
        console.error('上传请求失败:', err)
        uni.showToast({
          title: '上传请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// 选择图片
const chooseImage = () => {
  if (imageUploadRef.value) {
    imageUploadRef.value.chooseFile()
  }
}

// 预览图片
const previewImage = (index) => {
  uni.previewImage({
    urls: avatars.value,
    current: index
  })
}

// 删除图片
const deleteImage = (index) => {
  avatars.value.splice(index, 1)
}

// 打开个人简介弹窗
const openProfileModal = () => {
  tempProfile.value = userProfile.value
  profileModalRef.value?.showModal({
    title: '编辑个人简介',
    showCancel: true,
    confirmText: '保存',
    cancelText: '取消',
    confirm: () => {
        saveProfile()
    },
    cancel: () => {
      tempProfile.value = userProfile.value
    }
  })
}

// 打开性别选择弹窗
const openSexModal = () => {
  tempSex.value = sex.value
  sexModalRef.value?.showModal({
    title: '选择性别',
    showCancel: true,
    confirm: () => {
        saveSex()
    },
    cancel: () => {
      tempSex.value = sex.value
    }
  })
}

// 打开省份选择弹窗
const openProvinceModal = () => {
  provinceModalRef.value?.showModal({
    title: '选择省份',
    showCancel: true,
    confirm: () => {
      if (selectedProvinceId.value) {
        saveProvince()
      } else {
        uni.showToast({
          title: '请选择省份',
          icon: 'none'
        })
      }
    }
  })
}

// 打开学校选择弹窗
const openSchoolModal = () => {
  if (!selectedProvinceId.value) {
    uni.showToast({
      title: '请先选择省份',
      icon: 'none'
    })
    return
  }
  
  tempSchool.value = school.value
  searchSchool.value = ''
  tempSchoolList.value = []
  schoolPage.pageNo = 1
  schoolPage.hasMore = true
  
  // 获取学校列表
  getSchoolList(selectedProvinceId.value, true)
  
  schoolModalRef.value?.showModal({
    title: '选择学校',
    showCancel: true,
    confirm: () => {
      saveSchool()
    },
    cancel: () => {
      tempSchool.value = school.value
    }
  })
}

// 处理信息点击
const handleInfoClick = (type) => {
  switch (type) {
    case 'sex':
      tempSex.value = sex.value
      openSexModal()
      break
    case 'school':
      openSchoolModal()
      break
    case 'phone':
      // 处理手机号修改
      break
    case 'province':
      // 获取省份列表
      if (provinceData.value.length === 0) {
        getProvinceList()
      }
      openProvinceModal()
      break
  }
}

// 保存个人简介
const saveProfile = () => {
  userProfile.value = tempProfile.value
  uni.showToast({
    title: '保存成功',
    icon: 'success'
  })
}

// 保存性别选择
const saveSex = () => {
  sex.value = tempSex.value
  uni.showToast({
    title: '保存成功',
    icon: 'success'
  })
}

// 保存省份选择
const saveProvince = () => {
  const province = provinceData.value.find(p => p.id === selectedProvinceId.value)
  if (province) {
    selectedProvince.value = province.name
    // 修改省份后清空学校信息
    school.value = ''
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })
  }
}

// 保存学校选择
const saveSchool = () => {
  school.value = tempSchool.value
  uni.showToast({
    title: '保存成功',
    icon: 'success'
  })
}

// 选择省份
const selectProvince = (province) => {
  selectedProvinceId.value = province.id
}

// 选择学校
const selectSchool = (school) => {
  tempSchool.value = school.name
}

// 搜索学校
const handleSchoolSearch = () => {
  if (selectedProvinceId.value) {
    tempSchoolList.value = []
    schoolPage.pageNo = 1
    schoolPage.hasMore = true
    getSchoolList(selectedProvinceId.value, true)
  }
}

// 加载更多学校
const loadMoreSchools = () => {
  if (!schoolPage.loading && schoolPage.hasMore && selectedProvinceId.value) {
    getSchoolList(selectedProvinceId.value)
  }
}

// 清空头像
const clearAvatars = () => {
  uni.showModal({
    title: '提示',
    content: '确定清空已上传的头像吗？',
    success: (res) => {
      if (res.confirm) {
        avatars.value = []
        fileList.value = []
      }
    }
  })
}

onMounted(() => {
  // 初始化加载省份数据
  getProvinceList()
})
</script>

<template>
  <view class="template-set">
    <!-- 顶部自定义导航 -->
    <navbar title="个人资料" home=""></navbar>
    <view class="tn-mt">
      <!-- 头像上传 -->
      <view class="tn-flex tn-flex-center-between tn-strip-bottom-min tn-p">
        <view>
          <view class="tn-text-bold tn-text-lg">
            头像相册
          </view>
          <view class="tn-gray_text tn-pt-xs tn-text-ellipsis-1">
            最多上传4张照片
          </view>
        </view>
        <view class="tn-text-df tn-gray_text" @tap="clearAvatars" v-if="avatars.length > 0">
          <text class="tn-p-xs">清空</text>
          <tn-icon name="delete"></tn-icon>
        </view>
      </view>
      
      <!-- 使用TnImageUpload组件 -->
      <view class="tn-ml tn-pt-xs">
        <tn-image-upload 
          ref="imageUploadRef"
          v-model="fileList"
          :action="action"
          :form-data="formData"
          :limit="maxCount"
          :auto-upload="autoUpload"
          :show-upload-progress="showProgress"
          :custom-upload-handler="customUploadHandler"
        />
      </view>

      <!-- 基本信息 -->
      <view 
        class="tn-flex tn-flex-center-between tn-strip-bottom tn-p" 
        @click="openProfileModal"
      >
        <view>
          <view class="tn-text-bold tn-text-lg">
            个人简介
          </view>
          <view class="tn-gray_text tn-pt-xs">
            {{ userProfile || '添加个人简介' }}
          </view>
        </view>
        <view class="tn-text-lg tn-gray_text">
          <tn-icon name="right" class="tn-pt"></tn-icon>
        </view>
      </view>

      <!-- 其他信息列表 -->
      <view
          class="tn-flex tn-flex-center-between tn-strip-bottom-min tn-p"
          v-for="(item, index) in infoList"
          :key="index"
          @click="handleInfoClick(item.type)"
      >
        <view>
          <view class="tn-text-bold tn-text-lg">
            {{ item.title }}
          </view>
          <view class="tn-gray_text tn-pt-xs">
            <text v-if="item.value">{{ item.value }}</text>
            <text v-else class="tn-gray_text">{{ item.placeholder }}</text>
          </view>
        </view>
        <view class="tn-text-lg tn-gray_text">
          <tn-icon name="right" class="tn-pt"></tn-icon>
        </view>
      </view>
    </view>

    <!-- 个人简介弹窗 -->
    <tn-modal ref="profileModalRef">
      <view class="modal-content">
        <textarea
          v-model="tempProfile"
          placeholder="请输入个人简介"
          maxlength="200"
          class="profile-textarea"
        />
      </view>
    </tn-modal>

    <!-- 性别选择弹窗 -->
    <tn-modal ref="sexModalRef">
      <view class="modal-content">
        <tn-radio-group v-model="tempSex">
          <tn-radio label="男">男</tn-radio>
          <tn-radio label="女">女</tn-radio>
        </tn-radio-group>
      </view>
    </tn-modal>
    
    <!-- 省份选择弹窗 -->
    <tn-modal ref="provinceModalRef">
      <view class="modal-content">
        <scroll-view scroll-y class="province-scroll">
          <view 
            class="province-item" 
            v-for="item in provinceData" 
            :key="item.id"
            :class="{'province-item--active': selectedProvinceId === item.id}"
            @click="selectProvince(item)"
          >
            {{ item.name }}
          </view>
          <view v-if="provinceData.length === 0" class="empty-tip">
            <tn-icon name="info" color="#999" size="40rpx"></tn-icon>
            <text class="empty-text">暂无省份数据</text>
          </view>
        </scroll-view>
      </view>
    </tn-modal>
    
    <!-- 学校选择弹窗 -->
    <tn-modal ref="schoolModalRef">
      <view class="modal-content">
        <!-- 搜索框 -->
        <view class="search-box">
          <input 
            type="text" 
            v-model="searchSchool" 
            placeholder="搜索学校" 
            confirm-type="search"
            @confirm="handleSchoolSearch"
          />
          <view class="search-btn" @click="handleSchoolSearch">
            <tn-icon name="search" color="#5677fc" size="40rpx"></tn-icon>
          </view>
        </view>
        
        <!-- 学校列表 -->
        <scroll-view 
          scroll-y 
          class="school-scroll"
          @scrolltolower="loadMoreSchools"
        >
          <view 
            class="school-item" 
            v-for="item in tempSchoolList" 
            :key="item.id"
            :class="{'school-item--active': tempSchool === item.name}"
            @click="selectSchool(item)"
          >
            {{ item.name }}
          </view>
          
          <!-- 加载状态 -->
          <view v-if="schoolPage.loading" class="loading-tip">
            <tn-icon name="loading" color="#5677fc" size="40rpx"></tn-icon>
            <text class="loading-text">加载中...</text>
          </view>
          
          <!-- 没有更多数据 -->
          <view v-else-if="!schoolPage.hasMore && tempSchoolList.length > 0" class="no-more-tip">
            <text class="no-more-text">没有更多学校</text>
          </view>
          
          <!-- 暂无数据 -->
          <view v-else-if="tempSchoolList.length === 0" class="empty-tip">
            <tn-icon name="info" color="#999" size="40rpx"></tn-icon>
            <text class="empty-text">暂无学校数据</text>
          </view>
        </scroll-view>
      </view>
    </tn-modal>
  </view>
</template> 