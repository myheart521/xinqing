<template>
  <view class="cropper-wrapper">
    <canvas
      type="2d"
      id="myCanvas"
      canvas-id="myCanvas"
      class="cropper-canvas"
      :style="{ width: `${cropWidth}px`, height: `${cropHeight}px` }"
      disable-scroll
      @touchstart="touchStart"
      @touchmove="touchMove"
      @touchend="touchEnd"
    ></canvas>
    <view class="cropper-buttons">
      <view class="btn cancel" @tap="cancel">取消</view>
      <view class="btn confirm" @tap="confirm">确定</view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ling-imgcrop',
  props: {
    src: {
      type: String,
      default: ''
    },
    width: {
      type: Number,
      default: 200
    },
    height: {
      type: Number,
      default: 200
    },
    maxWidth: {
      type: Number,
      default: 1024
    },
    maxHeight: {
      type: Number,
      default: 1024
    },
    quality: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {
      cropWidth: 0,
      cropHeight: 0,
      ctx: null,
      imgInfo: null,
      scaleWidth: 1,
      scaleHeight: 1,
      offsetX: 0,
      offsetY: 0
    }
  },
  watch: {
    src: {
      handler(newVal) {
        if (newVal) {
          this.init()
        }
      },
      immediate: true
    }
  },
  methods: {
    async init() {
      // 获取图片信息
      try {
        const imgInfo = await new Promise((resolve, reject) => {
          uni.getImageInfo({
            src: this.src,
            success: (res) => {
              resolve(res)
            },
            fail: (err) => {
              reject(err)
            }
          })
        })
        
        this.imgInfo = imgInfo
        
        // 计算缩放比例
        const scale = Math.min(
          this.maxWidth / imgInfo.width,
          this.maxHeight / imgInfo.height
        )
        
        this.cropWidth = this.width
        this.cropHeight = this.height
        this.scaleWidth = imgInfo.width * scale
        this.scaleHeight = imgInfo.height * scale
        
        // 居中显示
        this.offsetX = (this.cropWidth - this.scaleWidth) / 2
        this.offsetY = (this.cropHeight - this.scaleHeight) / 2
        
        await this.$nextTick()
        const query = uni.createSelectorQuery().in(this)
        const res = await new Promise(resolve => {
          query.select('#myCanvas')
            .fields({ node: true, size: true })
            .exec((res) => resolve(res))
        })
        
        if (res && res[0]) {
          const canvas = res[0].node
          this.ctx = canvas.getContext('2d')
          this.draw()
        }
      } catch (error) {
        console.error('图片加载失败:', error)
        uni.showToast({
          title: '图片加载失败',
          icon: 'none'
        })
      }
    },
    draw() {
      const { ctx, imgInfo, scaleWidth, scaleHeight, offsetX, offsetY } = this
      if (!ctx || !imgInfo) return
      
      // 清空画布
      ctx.clearRect(0, 0, this.cropWidth, this.cropHeight)
      
      // 绘制图片
      try {
        // 微信小程序直接绘制图片
        ctx.drawImage(
          imgInfo.path,
          offsetX,
          offsetY,
          scaleWidth,
          scaleHeight
        )
      } catch (error) {
        console.error('绘制图片失败:', error)
      }
    },
    touchStart(e) {
      // 实现拖动和缩放逻辑
    },
    touchMove(e) {
      // 实现拖动和缩放逻辑
    },
    touchEnd(e) {
      // 实现拖动和缩放逻辑
    },
    cancel() {
      this.$emit('cancel')
    },
    async confirm() {
      const tempFilePath = await new Promise(resolve => {
        uni.canvasToTempFilePath({
          canvasId: 'myCanvas',
          quality: this.quality,
          success: res => resolve(res.tempFilePath)
        }, this)
      })
      
      this.$emit('confirm', { tempFilePath })
    }
  }
}
</script>

<style lang="scss" scoped>
.cropper-wrapper {
  width: 100%;
  height: 100%;
  background-color: #000;
  display: flex;
  flex-direction: column;
}

.cropper-canvas {
  flex: 1;
  width: 100%;
}

.cropper-buttons {
  height: 100rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40rpx;
  
  .btn {
    padding: 20rpx 40rpx;
    border-radius: 10rpx;
    font-size: 32rpx;
    
    &.cancel {
      color: #fff;
    }
    
    &.confirm {
      background: #409eff;
      color: #fff;
    }
  }
}
</style> 