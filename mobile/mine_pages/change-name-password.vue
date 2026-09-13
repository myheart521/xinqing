<template>
  <view class="template-safety tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <navbar title="修改昵称和密码" home=""></navbar>
    <view class="login">
      <!-- 顶部背景图片-->
      <view class="login__bg login__bg--top">
        <image class="bg" src="//static/images/login-top2.png" mode="widthFix"></image>
      </view>

      <view class="login__wrapper tn-p">
        <view class="tn-ml tn-mr tn-text-bold" style="font-size: 50rpx;">
          修改昵称、密码
        </view>
        <view class="tn-m tn-gray_text tn-text-lg">
          选择合适的昵称和密码，方便您找回密码
        </view>
        <!-- 切换 -->
        <view
            class="login__mode tn-flex tn-flex-nowrap tn-flex-center">
          <view class="login__mode__item tn-flex-1"
                :class="[{'login__mode__item--active': currentModeIndex === 0}]" @tap.stop="modeSwitch(0)">
            昵称
          </view>
          <view class="login__mode__item tn-flex-1"
                :class="[{'login__mode__item--active': currentModeIndex === 1}]" @tap.stop="modeSwitch(1)">
            密码
          </view>
          <!-- 滑块-->
          <view class="login__mode__slider tn-gradient-bg__cool-15" :style="[modeSliderStyle]"></view>
        </view>

        <!-- 输入框内容-->
        <view class="login__info tn-flex tn-flex-column tn-flex-center">
          <!-- 昵称 -->
          <block v-if="currentModeIndex === 0">
            <view class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="my"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input maxlength="20" placeholder-class="input-placeholder" placeholder="请输入新昵称" />
              </view>
            </view>

          </block>
          <!-- 密码 -->
          <block v-if="currentModeIndex === 1">
            <view class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="qq"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input 
                  v-model="email"
                  maxlength="20" 
                  placeholder-class="input-placeholder" 
                  placeholder="请输入QQ邮箱" 
                />
              </view>
            </view>

            <view class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="safe"></tn-icon>
              </view>
              <view class="login__info__item__input__content login__info__item__input__content--verify-code">
                <input 
                  v-model="verifyCode"
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

            <view class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="lock"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input :password="!showPassword" placeholder-class="input-placeholder" placeholder="请输入新密码" />
              </view>
              <view class="login__info__item__input__right-icon" @click="showPassword = !showPassword">
                <tn-icon :name="showPassword ? 'eye' : 'eye-hide'"></tn-icon>
              </view>
            </view>
            <view class="login__info__item__input tn-flex tn-flex-nowrap tn-flex-center">
              <view class="login__info__item__input__left-icon">
                <tn-icon name="lock"></tn-icon>
              </view>
              <view class="login__info__item__input__content">
                <input :password="!showPassword" placeholder-class="input-placeholder" placeholder="请再次输入新密码" />
              </view>
              <view class="login__info__item__input__right-icon" @click="showPassword = !showPassword">
                <tn-icon :name="showPassword ? 'eye' : 'eye-hide'"></tn-icon>
              </view>
            </view>
          </block>

          <view class="login__info__item__button tn-blue_bg tn-white_text" hover-class="tn-hover" :hover-stay-time="150">{{ currentModeIndex === 0 ? '修改昵称' : '修改密码'}}</view>


        </view>

      </view>

      <!-- 底部背景图片-->
      <view class="login__bg login__bg--bottom">
        <image src="https://assets.example.invalid/placeholder.png" mode="widthFix"></image>
      </view>
    </view>
  </view>
</template>

<script>
import navbar from "@/components/navbar.vue";
export default {
  components: {
    navbar
  },
  name: 'change-name-passward',
  data() {
    return {
      // 当前选中的模式
      currentModeIndex: 0,
      // 模式选中滑块
      modeSliderStyle: {
        left: 0
      },
      // 是否显示密码
      showPassword: false,
      // 验证码相关
      email: 'demo@example.invalid',
      verifyCode: '',
      countdown: 0,
      timer: null
    }
  },
  beforeUnmount() {
    // 组件卸载时清除定时器
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  watch: {
    currentModeIndex(value) {
      const sliderWidth = uni.upx2px(476 / 2)
      this.modeSliderStyle.left = `${sliderWidth * value}px`
    }
  },
  methods: {
    // 切换模式
    modeSwitch(index) {
      this.currentModeIndex = index
      this.showPassword = false
    },
    // 获取验证码
    async sendVerifyCode() {
      // 验证QQ邮箱格式
      const emailRegex = /^[1-9][0-9]{4,}@qq\.com$/
      if (!emailRegex.test(this.email)) {
        uni.showToast({
          title: '请输入正确的QQ邮箱格式',
          icon: 'none'
        })
        return
      }
      
      if (this.countdown > 0) return
      
      try {
        // 这里替换为实际的API调用
        // await api.sendVerifyCode(this.email)
        
        // 开始倒计时
        this.countdown = 60
        this.timer = setInterval(() => {
          this.countdown--
          if (this.countdown <= 0) {
            clearInterval(this.timer)
          }
        }, 1000)
        
        uni.showToast({
          title: '验证码已发送',
          icon: 'success'
        })
      } catch (error) {
        uni.showToast({
          title: '发送失败，请重试',
          icon: 'none'
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
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
    width: 100%;
  }

  /* 切换 start */
  &__mode {
    position: relative;
    margin: 0 auto;
    width: 476rpx;
    height: 77rpx;
    margin-top: 50rpx;
    background-color: rgba(255,255,255,0.1);
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
    margin: 80rpx 30rpx 10rpx 30rpx;
    padding-bottom: 0;
    border-radius: 20rpx;

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
        height: 77rpx;
        text-align: center;
        font-size: 31rpx;
        font-weight: bold;
        line-height: 77rpx;
        letter-spacing: 1em;
        text-indent: 1em;
        border-radius: 39rpx;
        box-shadow: 1rpx 10rpx 24rpx 0rpx rgba(60, 129, 254, 0.35);
      }

    }
  }
  /* 登录注册信息 end */

  /* 内容 end */

}

:deep(.input-placeholder ){
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
