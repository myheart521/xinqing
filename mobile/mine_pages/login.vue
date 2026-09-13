<script setup>
import {
  ref,
  watch,
  onUnmounted
} from 'vue';
import navbar from "@/components/navbar.vue";
import defaultImage from '@/static/avatar/default.png'
import {useUserStore} from '@/stores/user';
import {checkEmail, register} from '@/service/api/userController.ts'

const userStore = useUserStore();

// 当前选中的模式
const currentModeIndex = ref(0)
// 模式选中滑块
const modeSliderStyle = ref({
  left: 0
})

// 登录表单
const loginForm = ref({
  email: 'demo@example.invalid',
  password: ''
})

// 注册表单
const registerForm = ref({
  email: 'demo@example.invalid',
  password: '',
  checkPassword: '',
  code: '',
  userName: '示例资料',
})

// 验证码相关
const countdown = ref(0)
const timer = ref(null)

// 密码显示控制
const showPassword = ref(false)

// 发送验证码
const sendVerifyCode = async () => {
  const email = currentModeIndex.value === 0 ? loginForm.value.email : registerForm.value.email

  // 验证QQ邮箱格式
  const emailRegex = /^[1-9][0-9]{4,}@qq\.com$/
  if (!emailRegex.test(email)) {
    uni.showToast({
      title: '请输入正确的QQ邮箱格式',
      icon: 'none'
    })
    return
  }

  if (countdown.value > 0) return

  try {
    // 调用检查邮箱接口获取验证码
    const res = await checkEmail({
      email: email
    })

    if (res.code === 1) {
      // 开始倒计时
      countdown.value = 60
      timer.value = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer.value)
        }
      }, 1000)

      uni.showToast({
        title: '验证码已发送',
        icon: 'success'
      })
    } else {
      uni.showToast({
        title: res.msg || '发送失败',
        icon: 'none'
      })
    }
  } catch (error) {
    uni.showToast({
      title: '发送失败，请重试',
      icon: 'none'
    })
  }
}

// 登录
const handleLogin = async () => {
  // 表单验证
  if (!loginForm.value.email || !loginForm.value.password) {
    uni.showToast({
      title: '请填写完整登录信息',
      icon: 'none'
    })
    return
  }

  // 验证QQ邮箱格式
  const emailRegex = /^[1-9][0-9]{4,}@qq\.com$/
  if (!emailRegex.test(loginForm.value.email)) {
    uni.showToast({
      title: '请输入正确的QQ邮箱格式',
      icon: 'none'
    })
    return
  }

  try {
    const res = await userStore.emailLogin(loginForm.value.email, loginForm.value.password)
    // 登录成功，不需要做任何处理，因为 store 中已经处理了
  } catch (error) {
    // 只在真正的错误时才输出日志
    if (error?.message !== '登录失败') {
      console.error('登录出现异常:', error)
    }
  }
}

// 注册
const handleRegister = async () => {
  // 表单验证
  if (!registerForm.value.email || !registerForm.value.password ||
      !registerForm.value.checkPassword || !registerForm.value.code) {
    uni.showToast({
      title: '请填写完整注册信息',
      icon: 'none'
    })
    return
  }

  // 验证QQ邮箱格式
  const emailRegex = /^[1-9][0-9]{4,}@qq\.com$/
  if (!emailRegex.test(registerForm.value.email)) {
    uni.showToast({
      title: '请输入正确的QQ邮箱格式',
      icon: 'none'
    })
    return
  }

  // 验证密码长度
  if (registerForm.value.password.length < 6) {
    uni.showToast({
      title: '密码长度不能少于6位',
      icon: 'none'
    })
    return
  }

  if (registerForm.value.password !== registerForm.value.checkPassword) {
    uni.showToast({
      title: '两次密码输入不一致',
      icon: 'none'
    })
    return
  }

  // 验证是否获取了验证码
  if (!countdown.value && !registerForm.value.code) {
    uni.showToast({
      title: '请先获取验证码',
      icon: 'none'
    })
    return
  }

  try {
    const res = await register({
      email: registerForm.value.email,
      password: registerForm.value.password,
      checkPassword: registerForm.value.checkPassword,
      code: registerForm.value.code,
      userName: registerForm.value.userName || `用户${Math.floor(Math.random() * 1000000)}`
    })

    if (res.code === 1) {
      uni.showToast({
        title: '注册成功',
        icon: 'success'
      })
      // 清空表单
      registerForm.value = {
        email: 'demo@example.invalid',
        password: '',
        checkPassword: '',
        code: '',
        userName: '示例资料',
      }
      // 注册成功后切换到登录
      currentModeIndex.value = 0
    } else {
      uni.showToast({
        title: res.msg || '注册失败',
        icon: 'none'
      })
    }
  } catch (error) {
    uni.showToast({
      title: '注册失败，请重试',
      icon: 'none'
    })
  }
}

// 组件卸载时清除定时器
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})

watch(() => currentModeIndex.value, (value) => {
  const sliderWidth = uni.upx2px(476 / 2)
  modeSliderStyle.value.left = `${sliderWidth * value}px`
})

// 跳转
const tn = (e) => {
  console.log("页面跳转：", e)
  // uni.navigateTo({
  // 	url: e,
  // });
}
// 切换模式
const modeSwitch = (index) => {
  currentModeIndex.value = index
  showPassword.value = false
}
</script>

<template>
  <view>
    <!-- 顶部自定义导航 -->
    <navbar title="登录"></navbar>
    <view class="login">
      <!-- 顶部背景图片-->
      <view class="login__bg login__bg--top">
        <image class="bg" src="/mine_pages/static/login_top2.jpg" mode="widthFix">
        </image>
      </view>
      <view class="login__bg login__bg--top">
        <image class="rocket rocket-sussuspension"
               src="/mine_pages/static/login_top3.png" mode="widthFix"></image>
      </view>
      <view class="login__wrapper">
        <!-- 登录/注册切换 -->
        <view
            class="login__mode tn-flex tn-flex-nowrap tn-flex-center">
          <view class="login__mode__item tn-flex-1"
                :class="[{'login__mode__item--active': currentModeIndex === 0}]" @tap.stop="modeSwitch(0)">
            登录
          </view>
          <view class="login__mode__item tn-flex-1"
                :class="[{'login__mode__item--active': currentModeIndex === 1}]" @tap.stop="modeSwitch(1)">
            注册
          </view>
          <!-- 滑块-->
          <view class="login__mode__slider tn-gradient-bg__cool-15" :style="[modeSliderStyle]"></view>
        </view>

        <!-- 输入框内容-->
        <view class="login__info tn-flex tn-flex-column tn-flex-center">
          <!-- 登录 -->
          <block v-if="currentModeIndex === 0">
            <view
                class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center-start">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="phone"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input
                    v-model="loginForm.email"
                    maxlength="20"
                    placeholder-class="input-placeholder"
                    placeholder="请输入登录QQ邮箱号码"
                />
              </view>
            </view>

            <view
                class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center-start">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="lock"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input
                    v-model="loginForm.password"
                    :password="!showPassword"
                    placeholder-class="input-placeholder"
                    placeholder="请输入登录密码"
                />
              </view>
              <view class="login__info__item__input__right-icon" @click="showPassword = !showPassword">
                <tn-icon :name="showPassword ? 'eye' : 'eye-hide'"></tn-icon>
              </view>
            </view>
          </block>
          <!-- 注册 -->
          <block v-if="currentModeIndex === 1">
            <view
                class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center-start">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="phone"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input
                    v-model="registerForm.email"
                    maxlength="20"
                    placeholder-class="input-placeholder"
                    placeholder="请输入注册QQ邮箱号码"
                />
              </view>
            </view>

            <view
                class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center-start">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="code"></tn-icon>
              </view>
              <view
                  class="login__info__item__input__content login__info__item__input__content--verify-code">
                <input
                    v-model="registerForm.code"
                    placeholder-class="input-placeholder"
                    placeholder="请输入验证码"
                    maxlength="6"
                />
              </view>
              <view class="login__info__item__input__right-verify-code">
                <button
                    class="verify-code-btn"
                    :disabled="countdown > 0"
                    @click="sendVerifyCode"
                >
                  {{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
                </button>
              </view>
            </view>

            <view
                class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center-start">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="lock"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input
                    v-model="registerForm.password"
                    :password="!showPassword"
                    placeholder-class="input-placeholder"
                    placeholder="请输入登录密码"
                />
              </view>
              <view class="login__info__item__input__right-icon" @click="showPassword = !showPassword">
                <tn-icon :name="showPassword ? 'eye' : 'eye-hide'"></tn-icon>
              </view>
            </view>

            <view
                class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center-start">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="lock"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input
                    v-model="registerForm.checkPassword"
                    :password="!showPassword"
                    placeholder-class="input-placeholder"
                    placeholder="请再次输入密码"
                />
              </view>
            </view>
          </block>

          <view class="tn-flex login__info__item__button">
            <view class="tn-flex-1 tn-text-center">
              <tn-button
                  v-if="currentModeIndex === 0"
                  shape="round"
                  bg-color="tn-gradient-bg__cool-7"
                  padding="40rpx 0"
                  width="100%"
                  height="76rpx"
                  shadow
                  fontBold
                  @click="handleLogin"
              >
                <text class="tn-white_text" hover-class="tn-hover" :hover-stay-time="150">
                  登 录
                </text>
              </tn-button>
              <tn-button
                  v-if="currentModeIndex === 1"
                  shape="round"
                  bg-color="tn-gradient-bg__cool-7"
                  padding="40rpx 0"
                  width="100%"
                  height="76rpx"
                  shadow
                  fontBold
                  @click="handleRegister"
              >
                <text class="tn-white_text" hover-class="tn-hover" :hover-stay-time="150">
                  注 册
                </text>
              </tn-button>
            </view>
          </view>

          <!-- <view class="login__info__item__button tn-cool-bg-color-7--reverse" hover-class="tn-hover" :hover-stay-time="150">{{ currentModeIndex === 0 ? '登录' : '注册'}}</view> -->

          <view v-if="currentModeIndex === 0" class="login__info__item__tips">忘记密码?</view>
        </view>

        <!-- 其他登录方式 -->
        <view class="login__way tn-flex tn-flex-center-center">
          <view class="tn-p-sm tn-m-xs">
            <view
                class="login__way__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-green_bg tn-white_text">
              <tn-icon name="wechat-fill"></tn-icon>
            </view>
          </view>
          <view class="tn-p-sm tn-m-xs">
            <view
                class="login__way__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-red_bg tn-white_text">
              <tn-icon name="sina"></tn-icon>
            </view>
          </view>
          <view class="tn-p-sm tn-m-xs">
            <view
                class="login__way__item--icon tn-flex tn-flex-center-center tn-shadow-blur tn-blue_bg tn-white_text">
              <tn-icon name="qq"></tn-icon>
            </view>
          </view>
        </view>
      </view>
      <!-- 底部背景图片-->
      <view class="login__bg login__bg--bottom">
        <image src="https://assets.example.invalid/placeholder.png" mode="widthFix"></image>
      </view>
    </view>

  </view>
</template>

<style lang="scss">
/* 胶囊*/
.tn-custom-nav-bar__back {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  box-sizing: border-box;
  background-color: rgba(0, 0, 0, 0.15);
  border-radius: 1000rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.5);
  color: #FFFFFF;
  font-size: 18px;

  .icon {
    display: block;
    flex: 1;
    margin: auto;
    text-align: center;
  }

  &:before {
    content: " ";
    width: 1rpx;
    height: 110%;
    position: absolute;
    top: 22.5%;
    left: 0;
    right: 0;
    margin: auto;
    transform: scale(0.5);
    transform-origin: 0 0;
    pointer-events: none;
    box-sizing: border-box;
    opacity: 0.7;
    background-color: #FFFFFF;
  }
}

/* 悬浮 */
.rocket-sussuspension {
  animation: suspension 3s ease-in-out infinite;
}

@keyframes suspension {

  0%,
  100% {
    transform: translate(0, 0);
  }

  50% {
    transform: translate(-0.8rem, 1rem);
  }
}

.login {
  position: relative;
  height: 100%;
  z-index: 1;

  /* 背景图片 start */
  &__bg {
    z-index: -1;
    position: fixed;

    &--top {
      top: 0;
      left: 0;
      right: 0;
      width: 100%;

      .bg {
        width: 750rpx;
        will-change: transform;
      }

      .rocket {
        margin: 50rpx 28%;
        width: 400rpx;
        will-change: transform;
      }
    }

    &--bottom {
      bottom: -10rpx;
      left: 0;
      right: 0;
      width: 100%;
      // height: 144px;
      // margin-bottom: env(safe-area-inset-bottom);

      image {
        width: 750rpx;
        will-change: transform;
      }
    }
  }

  /* 背景图片 end */

  /* 内容 start */
  &__wrapper {
    margin-top: 403rpx;
    width: 100%;
  }

  /* 切换 start */
  &__mode {
    position: relative;
    margin: 0 auto;
    width: 476rpx;
    height: 77rpx;
    background-color: rgba(255, 255, 255, 0.9);
    box-shadow: 0rpx 10rpx 50rpx 0rpx rgba(0, 3, 72, 0.1);
    border-radius: 39rpx;

    &__item {
      display: flex;
      justify-content: center;
      height: 77rpx;
      width: 100%;
      line-height: 77rpx;
      text-align: center;
      font-size: 31rpx;
      color: #78909C;
      letter-spacing: 1em;
      text-indent: 1em;
      z-index: 2;
      transition: all 0.4s;

      &--active {
        font-weight: bold;
        color: #FFFFFF;
      }
    }

    &__slider {
      position: absolute;
      height: inherit;
      width: calc(476rpx / 2);
      border-radius: inherit;
      box-shadow: 0rpx 18rpx 72rpx 18rpx rgba(0, 195, 255, 0.1);
      z-index: 1;
      transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    }
  }

  /* 切换 end */

  /* 登录注册信息 start */
  &__info {
    margin: 0 30rpx;
    margin-top: 105rpx;
    padding: 30rpx 51rpx;
    padding-bottom: 0;
    border-radius: 20rpx;
    background-color: rgba(255, 255, 255, 0.9);
    box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.07);

    &__item {

      &__input {
        margin-top: 59rpx;
        width: 100%;
        height: 77rpx;
        border: 1rpx solid #C6D1D8;
        border-radius: 39rpx;

        &__left-icon {
          width: 10%;
          font-size: 44rpx;
          margin-left: 20rpx;
          color: #78909C;
        }

        &__content {
          width: 80%;
          padding-left: 10rpx;

          &--verify-code {
            width: 56%;
          }

          input {
            font-size: 30rpx;
            color: #78909C;
            // letter-spacing: 0.1em;
          }
        }

        &__right-icon {
          width: 10%;
          font-size: 44rpx;
          margin-right: 20rpx;
          color: #78909C;
        }

        &__right-verify-code {
          width: 34%;
          margin-right: 20rpx;
        }
      }

      &__button {
        margin-top: 75rpx;
        margin-bottom: 39rpx;
        width: 100%;
        letter-spacing: 0.5em;
      }

      &__tips {
        margin: 30rpx 0;
        color: #AAAAAA;
      }
    }
  }

  /* 登录注册信息 end */

  /* 登录方式切换 start */
  &__way {
    margin: 0 auto;
    margin-top: 20rpx;

    &__item {
      &--icon {
        width: 77rpx;
        height: 77rpx;
        font-size: 50rpx;
        border-radius: 100rpx;
        margin-bottom: 18rpx;
        position: relative;
        z-index: 1;

        &::after {
          content: " ";
          position: absolute;
          z-index: -1;
          width: 100%;
          height: 100%;
          left: 0;
          bottom: 0;
          border-radius: inherit;
          opacity: 1;
          transform: scale(1, 1);
          background-size: 100% 100%;
          background-image: url(http://localhost:8080/images/cool_bg_image/icon_bg5.png);
        }
      }
    }
  }

  /* 登录方式切换 end */
  /* 内容 end */

}

:deep(.input-placeholder) {
  font-size: 30rpx;
  color: #C6D1D8;
}

.verify-code-btn {
  width: 100%;
  height: 100%;
  background: none;
  border: none;
  font-size: 26rpx;
  color: #409eff;
  padding: 0;

  &:after {
    border: none;
  }

  &[disabled] {
    color: #999;
    background: none;
  }
}

.login__info__item__input {
  &__right-verify-code {
    width: 34%;
    height: 77rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    border-left: 1px solid #eee;
  }
}
</style>