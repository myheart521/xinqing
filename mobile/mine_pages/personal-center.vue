<template>
  <view class="template-screen tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <navbar title="个人资料" home=""></navbar>

    <!-- 用户头像和背景 -->
    <view class="user-header">
      <view class="bg-image">
        <!-- 背景图轮播 -->
        <swiper v-if="tempUserInfo.avatars" class="bg-swiper" circular autoplay>
          <swiper-item v-for="(item, index) in tempUserInfo.avatars.split(',')" :key="index">
            <image :src="item || defaultBg" mode="aspectFill" class="swiper-image"></image>
          </swiper-item>
        </swiper>
        <image v-else :src="defaultBg" mode="aspectFill"></image>

        <view class="change-bg" @click="openBgModal">
          <tn-icon name="image" color="#ffffff" size="30rpx"></tn-icon>
        </view>
      </view>

      <view class="avatar-wrap">
        <image class="avatar" :src="tempUserInfo.userAvatar || defaultAvatar" mode="aspectFill"
               @click="openAvatarModal"></image>
        <view class="avatar-edit">
          <tn-icon name="camera" color="#ffffff" size="30rpx"></tn-icon>
        </view>
      </view>
    </view>

    <!-- 基本信息卡片 -->
    <view class="info-card">
      <!-- 个人简介 -->
      <view class="info-item" @click="openProfileModal">
        <view class="info-label">个人简介</view>
        <view class="info-content">
          <text v-if="tempUserInfo.userProfile">{{ tempUserInfo.userProfile }}</text>
          <text v-else class="placeholder">添加个人简介</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 真实姓名 -->
      <view class="info-item" @click="openUserNameModal">
        <view class="info-label">真实姓名</view>
        <view class="info-content">
          <text v-if="tempUserInfo.userName">{{ tempUserInfo.userName }}</text>
          <text v-else class="placeholder">请输入您的真实姓名</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 用户角色 -->
      <view class="info-item" @click="openRoleModal">
        <view class="info-label">身份</view>
        <view class="info-content">
          <text v-if="tempUserInfo.userRole">{{ getRoleName(tempUserInfo.userRole) }}</text>
          <text v-else class="placeholder">选择身份</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 手机号码 -->
      <view class="info-item" @click="openPhoneModal">
        <view class="info-label">手机号码</view>
        <view class="info-content">
          <text v-if="tempUserInfo.phone">{{ tempUserInfo.phone }}</text>
          <text v-else class="placeholder">绑定手机号</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 省份选择 -->
      <view class="info-item" @click="openProvinceModal">
        <view class="info-label">所在省份</view>
        <view class="info-content">
          <text v-if="tempUserInfo.province">{{ tempUserInfo.province }}</text>
          <text v-else class="placeholder">选择省份</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 学校选择 -->
      <view class="info-item" @click="openSchoolModal">
        <view class="info-label">学校</view>
        <view class="info-content">
          <text v-if="tempUserInfo.schoolName">{{ tempUserInfo.schoolName }}</text>
          <text v-else class="placeholder">选择学校</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 性别选择 -->
      <view class="info-item" @click="openSexModal">
        <view class="info-label">性别</view>
        <view class="info-content">
          <text v-if="tempUserInfo.sex">{{ tempUserInfo.sex }}</text>
          <text v-else class="placeholder">选择性别</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 添加在学校选择的下方 -->
      <view class="info-item" @click="openCollegeModal">
        <view class="info-label">学院</view>
        <view class="info-content">
          <text v-if="tempUserInfo.college">{{ tempUserInfo.college }}</text>
          <text v-else class="placeholder">请输入学院名称，如"软件学院"</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 添加专业班级 -->
      <view class="info-item" @click="openMajorClassModal">
        <view class="info-label">专业班级</view>
        <view class="info-content">
          <text v-if="tempUserInfo.majorClass">{{ tempUserInfo.majorClass }}</text>
          <text v-else class="placeholder">请输入专业班级，如"示例专业班级"</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>

      <!-- 添加学号 -->
      <view class="info-item" @click="openStudentIdModal">
        <view class="info-label">学号</view>
        <view class="info-content">
          <text v-if="tempUserInfo.studentNumber">{{ tempUserInfo.studentNumber }}</text>
          <text v-else class="placeholder">请输入学号</text>
          <tn-icon name="right"></tn-icon>
        </view>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="save-btn-box">
      <tn-button type="primary" @click="saveUserInfo">保存修改</tn-button>
    </view>

    <!-- 个人简介弹窗 -->
    <tn-modal ref="profileModalRef">
      <view class="modal-content">
        <textarea
            v-model="modalData.userProfile"
            placeholder="请输入个人简介"
            maxlength="200"
            class="profile-textarea"
        />
      </view>
    </tn-modal>

    <!-- 角色选择弹窗 -->
    <tn-modal ref="roleModalRef">
      <view class="modal-content">
        <view class="modal-title">选择身份</view>
        <tn-radio-group v-model="modalData.userRole">
          <tn-radio label="admin">高校管理员</tn-radio>
          <tn-radio label="teacher">教师</tn-radio>
          <tn-radio label="student">学生</tn-radio>
          <tn-radio label="visitor">游客</tn-radio>
        </tn-radio-group>

        <!--        <view class="modal-actions">-->
        <!--          <tn-button @click="closeRoleModal">取消</tn-button>-->
        <!--          <tn-button type="primary" @click="confirmRole">确认</tn-button>-->
        <!--        </view>-->
      </view>
    </tn-modal>

    <!-- 手机号码弹窗 -->
    <tn-modal ref="phoneModalRef">
      <view class="modal-content">
        <input
            v-model="modalData.phone"
            type="number"
            placeholder="请输入手机号码"
            maxlength="11"
            class="phone-input"
        />
      </view>
    </tn-modal>

    <!-- 性别选择弹窗 -->
    <tn-modal ref="sexModalRef">
      <view class="modal-content">
        <tn-radio-group v-model="modalData.sex">
          <tn-radio label="男">男</tn-radio>
          <tn-radio label="女">女</tn-radio>
        </tn-radio-group>
      </view>
    </tn-modal>

    <!-- 省份选择弹窗 -->
    <tn-modal ref="provinceModalRef">
      <view class="modal-content">
        <scroll-view scroll-y class="select-scroll">
          <view
              v-for="item in provinceList"
              :key="item.id"
              class="select-item"
              :class="{'select-item--active': modalData.province === item.name}"
              @click="selectProvince(item)"
          >
            {{ item.name }}
          </view>
        </scroll-view>
      </view>
    </tn-modal>

    <!-- 学校选择弹窗 -->
    <tn-modal ref="schoolModalRef">
      <view class="modal-content">
        <scroll-view
            scroll-y
            class="select-scroll"
            @scrolltolower="loadMoreSchools"
        >
          <view
              v-for="item in schoolList"
              :key="item.id"
              class="select-item"
              :class="{'select-item--active': modalData.schoolName === item.name}"
              @click="selectSchool(item)"
          >
            {{ item.name }}
          </view>
          <view v-if="loading" class="loading-tip">加载中...</view>
        </scroll-view>
      </view>
    </tn-modal>

    <!-- 头像上传模态框 -->
    <tn-modal ref="avatarModalRef">
      <view class="modal-content">
        <view class="modal-title">上传头像</view>

        <view class="upload-preview" v-if="avatarFileList.length > 0">
          <image :src="avatarFileList[0]" mode="aspectFill" class="preview-image"></image>
        </view>

        <tn-image-upload
            ref="avatarUploadRef"
            v-model="avatarFileList"
            :action="uploadAction"
            :limit="1"
            :auto-upload="true"
            :custom-upload-callback="customAvatarUploadCallback"
        ></tn-image-upload>

        <view class="modal-actions">
          <tn-button @click="closeAvatarModal">取消</tn-button>
          <tn-button type="primary" @click="confirmAvatar">确认</tn-button>
        </view>
      </view>
    </tn-modal>

    <!-- 背景图上传模态框 -->
    <tn-modal ref="bgModalRef">
      <view class="modal-content">
        <view class="modal-title">上传背景图</view>
        <view class="upload-tip">可上传多张图片，最多6张</view>

        <view class="tn-flex tn-flex-center-between tn-pt-sm">
          <view class="tn-text-df">已选择：{{ bgFileList.length }}/6</view>
          <view class="tn-gray_text" @tap="clearBgImages">
            <text class="tn-p-xs">清空</text>
            <tn-icon name="delete"></tn-icon>
          </view>
        </view>

        <tn-image-upload
            ref="bgUploadRef"
            v-model="bgFileList"
            :action="uploadAction"
            :limit="6"
            :auto-upload="true"
            :custom-upload-callback="customBgUploadCallback"
        ></tn-image-upload>

        <view class="modal-actions">
          <tn-button @click="closeBgModal">取消</tn-button>
          <tn-button type="primary" @click="confirmBg">确认</tn-button>
        </view>
      </view>
    </tn-modal>

    <!-- 学院输入弹窗 -->
    <tn-modal ref="collegeModalRef">
      <view class="modal-content">
        <view class="modal-title">输入学院名称</view>
        <input
            v-model="modalData.college"
            type="text"
            placeholder="请输入学院名称，如：软件学院"
            class="college-input"
        />

      </view>
    </tn-modal>

    <!-- 专业班级输入弹窗 -->
    <tn-modal ref="majorClassModalRef">
      <view class="modal-content">
        <view class="modal-title">输入专业班级</view>
        <input
            v-model="modalData.majorClass"
            type="text"
            placeholder="请输入专业班级，如：示例专业班级"
            class="major-class-input"
        />
      </view>
    </tn-modal>

    <!-- 学号输入弹窗 -->
    <tn-modal ref="studentIdModalRef">
      <view class="modal-content">
        <view class="modal-title">输入学号</view>
        <input
            v-model="modalData.studentNumber"
            type="text"
            placeholder="请输入学号"
            class="student-id-input"
        />

      </view>
    </tn-modal>

    <!-- 真实姓名输入弹窗 -->
    <tn-modal ref="userNameModalRef">
      <view class="modal-content">
        <view class="modal-title">输入真实姓名</view>
        <input
            v-model="modalData.userName"
            type="text"
            placeholder="请输入您的真实姓名"
            class="user-name-input"
        />
      </view>
    </tn-modal>
  </view>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {useUserStore} from '@/stores/user'
import navbar from '@/components/navbar.vue'
import {modify, modifyUserInfo, province, universities} from '@/service/api/userController'
import {setUserInfo} from '@/utils/userStorage'
import {requestUrl} from "@/utils/URL";
import {ossBgUrl} from "@/utils/ossUrl";
const userStore = useUserStore()

// 默认图片
const defaultAvatar = ref('/static/avatar/default.png')
const defaultBg = ref(ossBgUrl.bg1)

// 图片上传
const uploadAction = ref(`${requestUrl}/common/upload`) // 替换为你的上传接口
const avatarUploadRef = ref(null)
const bgUploadRef = ref(null)
const avatarFileList = ref([])
const bgFileList = ref([])
const bgImageUrls = ref([])

// 模态框引用
const avatarModalRef = ref(null)
const bgModalRef = ref(null)
const profileModalRef = ref(null)
const roleModalRef = ref(null)
const phoneModalRef = ref(null)
const sexModalRef = ref(null)
const provinceModalRef = ref(null)
const schoolModalRef = ref(null)
const collegeModalRef = ref(null)
const majorClassModalRef = ref(null)
const studentIdModalRef = ref(null)
const userNameModalRef = ref(null)

// 用户信息
const userInfo = reactive({
  userAvatar: '',     // 用户头像
  userProfile: '',    // 用户简介
  userRole: '',       // 用户角色
  roleId: 0,          // 用户角色ID
  phone: '13000000000',          // 手机号码
  schoolName: '',     // 学校名称
  province: '',       // 省份
  sex: '',            // 性别
  avatars: '',         // 背景图(逗号分隔的多张图片URL)
  college: '',        // 学院
  majorClass: '',     // 专业班级
  studentNumber: '',      // 学号
})

// 临时用户信息（用于编辑）
const tempUserInfo = reactive({...userInfo})

// 模态框中的临时数据
const modalData = reactive({...userInfo})

// 角色ID映射表
const roleIdMap = {
  'admin': 1,    // 高校管理员
  'teacher': 2,  // 教师
  'student': 3,  // 学生
  'visitor': 4   // 游客
}

// 省份和学校列表
const provinceList = ref([])
const schoolList = ref([])
const loading = ref(false)
const schoolPage = reactive({
  pageNo: 1,
  pageSize: 10,
  hasMore: true
})

// 获取省份列表
const getProvinceList = async () => {
  try {
    const res = await province()
    if (res && res.code === 1) {
      provinceList.value = res.data || []
    }
  } catch (error) {
    console.error('获取省份列表失败:', error)
  }
}

// 获取学校列表
const getSchoolList = async (isRefresh = false) => {
  if (loading.value || !schoolPage.hasMore) return

  loading.value = true
  if (isRefresh) {
    schoolPage.pageNo = 1
    schoolList.value = []
  }

  // 获取省份ID
  const selectedProvince = provinceList.value.find(p => p.name === modalData.province)
  if (!selectedProvince) {
    loading.value = false
    return
  }

  try {
    const res = await universities({
      pageNo: schoolPage.pageNo.toString(),
      pageSize: schoolPage.pageSize.toString(),
      provinceId: selectedProvince.id.toString() // 使用省份ID而不是名称
    })

    if (res && res.code === 1) {
      const schools = res.data || []
      if (isRefresh) {
        schoolList.value = schools
      } else {
        schoolList.value.push(...schools)
      }

      schoolPage.hasMore = schools.length === schoolPage.pageSize
      if (schoolPage.hasMore) schoolPage.pageNo++
    }
  } catch (error) {
    console.error('获取学校列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 自定义头像上传回调函数
const customAvatarUploadCallback = (data) => {
  try {
    const res = JSON.parse(data.data)
    console.log('头像上传回调', res)
    if (res.code === 1 && res.data) {
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

// 自定义背景图上传回调函数
const customBgUploadCallback = (data) => {
  try {
    const res = JSON.parse(data.data)
    console.log('背景图上传回调', res)
    if (res.code === 1 && res.data) {
      // 将上传成功的URL添加到数组中
      bgImageUrls.value.push(res.data)
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

// 打开头像上传模态框
const openAvatarModal = () => {
  avatarFileList.value = tempUserInfo.userAvatar ? [tempUserInfo.userAvatar] : []
  avatarModalRef.value?.showModal({
    title: "上传头像",
    showCancel: false,
    mask: true
  })
}

// 确认头像
const confirmAvatar = () => {
  if (avatarFileList.value.length > 0) {
    tempUserInfo.userAvatar = avatarFileList.value[0]
    uni.showToast({
      title: '头像设置成功',
      icon: 'success'
    })
  }
  closeAvatarModal()
}

// 打开背景图上传模态框
const openBgModal = () => {
  // 初始化背景图URL数组
  bgImageUrls.value = []

  // 如果已有背景图，则设置初始值
  if (tempUserInfo.avatars) {
    bgFileList.value = tempUserInfo.avatars.split(',')
    bgImageUrls.value = [...bgFileList.value]
  } else {
    bgFileList.value = []
  }

  bgModalRef.value?.showModal({
    title: "上传背景图",
    showCancel: false,
    mask: true
  })
}

// 清空背景图
const clearBgImages = () => {
  if (bgUploadRef.value) {
    bgUploadRef.value.clear()
    bgFileList.value = []
    bgImageUrls.value = []
  }
}

// 确认背景图
const confirmBg = () => {
  if (bgFileList.value.length > 0) {
    // 将多张背景图URL用逗号连接存储
    tempUserInfo.avatars = bgImageUrls.value.join(',')
    uni.showToast({
      title: '背景图设置成功',
      icon: 'success'
    })
  }
  closeBgModal()
}

// 保存用户信息
const saveUserInfo = async () => {
  try {
    // 检查必填字段
    if (!tempUserInfo.userAvatar) {
      uni.showToast({
        title: '请上传头像',
        icon: 'none'
      })
      return
    }

    if (!tempUserInfo.userProfile) {
      uni.showToast({
        title: '请填写个人简介',
        icon: 'none'
      })
      return
    }

    if (!tempUserInfo.userRole) {
      uni.showToast({
        title: '请选择身份',
        icon: 'none'
      })
      return
    }

    if (!tempUserInfo.phone) {
      uni.showToast({
        title: '请填写手机号',
        icon: 'none'
      })
      return
    }

    if (!tempUserInfo.province) {
      uni.showToast({
        title: '请选择省份',
        icon: 'none'
      })
      return
    }

    if (!tempUserInfo.sex) {
      uni.showToast({
        title: '请选择性别',
        icon: 'none'
      })
      return
    }

    if (!tempUserInfo.avatars) {
      uni.showToast({
        title: '请上传背景图',
        icon: 'none'
      })
      return
    }

    if (!tempUserInfo.userName) {
      uni.showToast({
        title: '请填写真实姓名',
        icon: 'none'
      })
      return
    }

    // 转换角色ID
    const roleId = roleIdMap[tempUserInfo.userRole] || 1

    // 创建请求数据
    const requestData = {
      userAvatar: tempUserInfo.userAvatar,
      roleId: roleId,
      userProfile: tempUserInfo.userProfile,
      avatars: tempUserInfo.avatars,
      phone: tempUserInfo.phone,
      sex: tempUserInfo.sex,
      province: tempUserInfo.province,
      college: tempUserInfo.college,
      majorClass: tempUserInfo.majorClass,
      studentNumber: tempUserInfo.studentNumber,
      userName: tempUserInfo.userName
    }

    uni.showLoading({
      title: '保存中...',
      mask: true
    })

    // 使用新的modifyUserInfo接口
    const res = await modifyUserInfo(requestData)

    if (res && res.code === 1) {
      // 更新本地用户信息
      Object.assign(userInfo, tempUserInfo)
      userInfo.roleId = roleId
      // 更新存储
      setUserInfo(userInfo)
      userStore.setUserInfo(userInfo)
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
    } else {
      uni.showToast({
        title: res?.msg || '保存失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('保存用户信息失败:', error)
    uni.showToast({
      title: '保存失败',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
}

// 更新本地tempUserInfo
const updateTempInfo = (key, value) => {
  tempUserInfo[key] = value
}

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    'student': '学生',
    'teacher': '老师',
    'other': '其他'
  }
  return roleMap[role] || role
}

// 打开各种模态框
const openProfileModal = () => {
  modalData.userProfile = tempUserInfo.userProfile
  profileModalRef.value?.showModal({
    title: '编辑个人简介',
    showCancel: true,
    confirm: () => {
      updateTempInfo('userProfile', modalData.userProfile)
    }
  })
}

const openRoleModal = () => {
  modalData.userRole = tempUserInfo.userRole
  roleModalRef.value?.showModal({
    title: '选择身份',
    showCancel: true,
    mask: true,
    cancel: () => {
      closeRoleModal()
    },
    confirm: () => {
      confirmRole()
    },
  })
}

const openPhoneModal = () => {
  modalData.phone = tempUserInfo.phone
  phoneModalRef.value?.showModal({
    title: '输入手机号码',
    showCancel: true,
    confirm: () => {
      updateTempInfo('phone', modalData.phone)
    }
  })
}

const openSexModal = () => {
  modalData.sex = tempUserInfo.sex
  sexModalRef.value?.showModal({
    title: '选择性别',
    showCancel: true,
    confirm: () => {
      updateTempInfo('sex', modalData.sex)
    }
  })
}

const openProvinceModal = () => {
  if (provinceList.value.length === 0) {
    getProvinceList()
  }
  modalData.province = tempUserInfo.province
  provinceModalRef.value?.showModal({
    title: '选择省份',
    showCancel: true,
    confirm: () => {
      updateTempInfo('province', modalData.province)
      // 如果更改了省份，清空学校
      if (tempUserInfo.province !== userInfo.province) {
        updateTempInfo('schoolName', '')
      }
    }
  })
}

const openSchoolModal = () => {
  if (!tempUserInfo.province) {
    uni.showToast({
      title: '请先选择省份',
      icon: 'none'
    })
    return
  }

  // 重新根据省份名称查找省份ID
  const selectedProvince = provinceList.value.find(p => p.name === tempUserInfo.province)
  if (!selectedProvince) {
    uni.showToast({
      title: '无法获取省份信息',
      icon: 'none'
    })
    return
  }

  modalData.province = tempUserInfo.province
  modalData.provinceId = selectedProvince.id
  modalData.schoolName = tempUserInfo.schoolName

  getSchoolList(true)
  schoolModalRef.value?.showModal({
    title: '选择学校',
    showCancel: true,
    confirm: () => {
      updateTempInfo('schoolName', modalData.schoolName)
    }
  })
}

// 选择省份和学校
const selectProvince = (province) => {
  modalData.province = province.name
  modalData.provinceId = province.id // 保存省份ID
  modalData.schoolName = '' // 清空学校选择
}

const selectSchool = (school) => {
  modalData.schoolName = school.name
}

// 加载更多学校
const loadMoreSchools = () => {
  if (!loading.value && schoolPage.hasMore) {
    getSchoolList()
  }
}

// 改进模态框关闭方法
const closeAvatarModal = () => {
  return true
}

const closeBgModal = () => {
  return true
}

const closeRoleModal = () => {
  return true;
}

const confirmRole = () => {
  updateTempInfo('userRole', modalData.userRole)
  closeRoleModal()
}

// 打开学院模态框
const openCollegeModal = () => {
  modalData.college = tempUserInfo.college
  collegeModalRef.value?.showModal({
    title: '输入学院名称',
    showCancel: true,
    mask: true,
    confirm: () => {
      confirmCollege()
    },
    cancle: () => {
      closeCollegeModal()
    }
  })
}

// 关闭学院模态框
const closeCollegeModal = () => {
  return true
}

// 确认学院
const confirmCollege = () => {
  updateTempInfo('college', modalData.college)
  closeCollegeModal()
}

// 打开专业班级模态框
const openMajorClassModal = () => {
  modalData.majorClass = tempUserInfo.majorClass
  majorClassModalRef.value?.showModal({
    title: '输入专业班级',
    showCancel: true,
    mask: true,
    concle: () => {
      closeMajorClassModal()
    },
    confirm: () => {
      confirmMajorClass()
    }
  })
}

// 关闭专业班级模态框
const closeMajorClassModal = () => {
  return true
}

// 确认专业班级
const confirmMajorClass = () => {
  updateTempInfo('majorClass', modalData.majorClass)
  closeMajorClassModal()
}

// 打开学号模态框
const openStudentIdModal = () => {
  modalData.studentNumber = tempUserInfo.studentNumber
  studentIdModalRef.value?.showModal({
    title: '输入学号',
    showCancel: true,
    mask: true,
    cancle: () => {
      closeStudentIdModal()
    },
    confirm: () => {
      confirmStudentId()
    }
  })
}

// 关闭学号模态框
const closeStudentIdModal = () => {
  return true
}

// 确认学号
const confirmStudentId = () => {
  updateTempInfo('studentNumber', modalData.studentNumber)
  closeStudentIdModal()
}

// 打开真实姓名模态框
const openUserNameModal = () => {
  modalData.userName = tempUserInfo.userName
  userNameModalRef.value?.showModal({
    title: '输入真实姓名',
    showCancel: true,
    mask: true,
    cancel: () => {
      closeUserNameModal()
    },
    confirm: () => {
      confirmUserName()
    }
  })
}

// 关闭真实姓名模态框
const closeUserNameModal = () => {
  return true
}

// 确认真实姓名
const confirmUserName = () => {
  updateTempInfo('userName', modalData.userName)
  closeUserNameModal()
}

// 页面加载时初始化数据
onMounted(() => {
  const storedUserInfo = userStore.userInfo
  if (storedUserInfo) {
    Object.assign(userInfo, storedUserInfo)
    // 从roleId转换为userRole
    for (const [role, id] of Object.entries(roleIdMap)) {
      if (id === userInfo.roleId) {
        userInfo.userRole = role
        break
      }
    }
    // 将用户信息复制到临时对象
    Object.assign(tempUserInfo, userInfo)
  }
  getProvinceList()
})
</script>

<style lang="scss" scoped>
.template-screen {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 120rpx;
}

.user-header {
  position: relative;
  width: 100%;
  height: 400rpx;

  .bg-image {
    width: 100%;
    height: 100%;
    position: relative;

    image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .change-bg {
      position: absolute;
      right: 20rpx;
      bottom: 20rpx;
      width: 60rpx;
      height: 60rpx;
      border-radius: 50%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .avatar-wrap {
    position: absolute;
    left: 50%;
    bottom: -80rpx;
    transform: translateX(-50%);

    .avatar {
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
      border: 6rpx solid #fff;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
    }

    .avatar-edit {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 60rpx;
      height: 60rpx;
      border-radius: 50%;
      background-color: #5677fc;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
    }
  }
}

.info-card {
  margin: 110rpx 30rpx 30rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.info-item {
  padding: 30rpx 20rpx;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }

  .info-label {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 12rpx;
  }

  .info-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 26rpx;
    color: #666;

    .placeholder {
      color: #999;
    }
  }
}

.save-btn-box {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 20rpx 30rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 99;
}

.modal-content {
  padding: 20rpx 30rpx;

  .modal-title {
    font-size: 32rpx;
    font-weight: bold;
    text-align: center;
    margin-bottom: 30rpx;
  }

  .upload-tip {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 20rpx;
    text-align: center;
  }

  .upload-preview {
    width: 200rpx;
    height: 200rpx;
    margin: 0 auto 30rpx;
    border-radius: 50%;
    overflow: hidden;
    border: 2rpx solid #eee;

    .preview-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .modal-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 40rpx;
    gap: 20rpx;
  }
}

.select-scroll {
  max-height: 400rpx;
}

.select-item {
  padding: 24rpx;
  font-size: 28rpx;
  border-bottom: 1rpx solid #f5f5f5;

  &--active {
    color: #5677fc;
    background-color: rgba(86, 119, 252, 0.1);
  }
}

.loading-tip {
  text-align: center;
  padding: 20rpx;
  color: #999;
  font-size: 24rpx;
}

:deep(.tn-radio-group) {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

/* 添加轮播图样式 */
.bg-swiper {
  width: 100%;
  height: 100%;
}

.swiper-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 为新增的输入框添加样式 */
.college-input,
.major-class-input,
.student-id-input {
  width: 100%;
  height: 80rpx;
  border: 1px solid #eee;
  border-radius: 8rpx;
  padding: 0 20rpx;
  margin: 20rpx 0;
  font-size: 28rpx;
}

/* 为真实姓名输入框添加样式 */
.user-name-input {
  width: 100%;
  height: 80rpx;
  border: 1px solid #eee;
  border-radius: 8rpx;
  padding: 0 20rpx;
  margin: 20rpx 0;
  font-size: 28rpx;
}
</style>