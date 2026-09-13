<script setup>
import {defineProps, defineEmits} from 'vue';
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { userAvatar } = storeToRefs(userStore)

// 定义 props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '不说点啥子吗？'
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

// 定义 emits
const emit = defineEmits(['update:modelValue', 'send', 'blur']);

// 处理输入事件 (兼容各端：H5/安卓App/小程序)
const onInput = (event) => {
  // uni-app input 事件在不同平台返回值位置不同
  const value = (event?.detail && event.detail.value !== undefined)
    ? event.detail.value
    : (event?.target && event.target.value !== undefined)
      ? event.target.value
      : ''
  emit('update:modelValue', value)
};

// 处理发送事件
const onSend = () => {
  console.log('props.modelValue', props.modelValue)
  // 去除首尾空格，防止误判为空
  emit('send', (props.modelValue || '').trim())
};

// 处理失焦事件
const onBlur = () => {
  emit('blur');
};
</script>

<template>
  <view class="tabbar footerfixed dd-glass">
    <view class="tn-flex tn-flex-center-between tn-flex-row-center">
      <!--左侧 -->
      <view class="tn-flex-row-center">
        <view class="tn-flex tn-flex-center-center">
          <view class="tn-flex tn-flex-center-center tn-pr tn-pl-sm">
            <view class="avatar-all">
              <view class="tn-shadow-blur"
                    :style="{
                      backgroundImage: `url('${userAvatar}')`,
                      width: '60rpx',
                      height: '60rpx',
                      backgroundSize: 'cover'
                    }">
              </view>
            </view>
          </view>

          <view class="topic__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-center-start">
            <view class="topic__info__item__input__left-icon">
              <tn-icon name="emoji-good"></tn-icon>
            </view>
            <view class="topic__info__item__input__content">
              <input :value="modelValue" @input="onInput" @blur="onBlur" maxlength="200" placeholder-class="input-placeholder"
                     :cursor-spacing="18" :placeholder="placeholder" :disabled="disabled"/>
            </view>
          </view>
        </view>
      </view>

      <!-- 右侧 -->
      <view class="tn-flex-center-center tn-mr">
        <view class="topic__info__item__sure">
          <view class="tn-flex-1 tn-text-center" style="margin-top: 10rpx">
            <tn-button shape="round" bg-color="tn-gradient-bg__cool-15" width="100%" shadow @click="onSend">
              <text class="tn-white_text" hover-class="tn-hover" :hover-stay-time="150">
                发 送
              </text>
            </tn-button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
/* 底部 start*/
.footerfixed {
  position: fixed;
  width: 100%;
  bottom: 0;
  z-index: 999;
  background-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
}

.tabbar {
  align-items: center;
  min-height: 110rpx;
  padding: 20rpx 10rpx;
  height: calc(110rpx + env(safe-area-inset-bottom) / 2);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom) / 2);
}

.tn-flex-row-center {
  display: flex;
  align-items: center;
}

/* 毛玻璃*/
.dd-glass {
  width: 100%;
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
}

/* 头像*/
.avatar-all {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.05);
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
}

/* 内容*/
.topic {
  position: relative;
  height: 100%;
  z-index: 1;
  margin-bottom: 120rpx;


  /* 表单信息 start */
  &__info {
    margin: 0 50rpx;
    margin-top: 105rpx;
    padding: 30rpx 51rpx;
    border-radius: 20rpx;
    background-color: rgba(255, 255, 255, 1);
    border: 2rpx solid rgba(255, 255, 255, 0.1);
    box-shadow: 0rpx 10rpx 50rpx 0rpx rgba(0, 3, 72, 0.1);

    &__item {

      &__input {
        width: 400rpx;
        height: 60rpx;
        border: 1rpx solid #C6D1D8;
        border-radius: 39rpx;

        &__left-icon {
          width: 10%;
          font-size: 44rpx;
          margin-left: 20rpx;
          margin-right: 5rpx;
          color: #C6D1D8;
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
          font-size: 34rpx;
          margin-right: 20rpx;
          color: #78909C;
        }

        &__right-verify-code {
          width: 34%;
          margin-right: 20rpx;
        }
      }

      &__button {
        width: 100%;
        height: 60rpx;
        text-align: center;
        font-size: 31rpx;
        font-weight: bold;
        line-height: 77rpx;
        // text-indent: 1em;
        border-radius: 100rpx;
        color: #FFFFFF;
        background-color: rgba(255, 255, 255, 0.2);
        // border: 2rpx solid #FFFFFF;
      }

      &__sure {
        height: 60rpx;
        width: 140rpx;
      }

    }
  }

  /* 表单信息 end */

  /* 内容 end */

}

:deep(.input-placeholder) {
  font-size: 30rpx;
  color: #C6D1D8;
}
</style>