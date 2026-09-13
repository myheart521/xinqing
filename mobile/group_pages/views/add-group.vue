<script>
import navbar from '@/components/navbar.vue'
import mpHtml from '@/components/mp-html/mp-html'
import {queryTagTop} from '@/service/api/tagController'
import {collapseContextKey} from '@tuniao/tnui-vue3-uniapp/tokens'
import {getAll3} from '@/service/api/circleController'
import {add2} from '@/service/api/blogController'
import {requestUrl} from "@/utils/URL";

export default {
  components: {
    mpHtml,
    navbar
  },
  data() {
    return {
      // 上传图片的url地址
      action: `${requestUrl}/common/upload`,
      imageUpload: null,
      formData: {
  apiType: 'this,ali',
  token: 'dffc1e06e636cff0fdf7d877b6ae6a2e',
        image: null,
        title: ''
      },
      fileList: [],
      tagList: [],
      selectValue: [],
      autoUpload: true,
      showProgress: false,
      maxCount: 6,
      disabled: false,
      modalRef: null,
      content: "",
      modal: null,
      dialog: false,
      editable: true,
      value: '',
      callback: null,
      // 预设的标签样式
      tagStyle: {
        table: 'box-sizing:border-box;border-top:1px solid #dfe2e5;border-left:1px solid #dfe2e5',
        th: 'border-right:1px solid #dfe2e5;border-bottom:1px solid #dfe2e5',
        td: 'border-right:1px solid #dfe2e5;border-bottom:1px solid #dfe2e5',
        li: 'margin:5px 0'
      },
      rows: 1,
      cols: 1,
      // 用于插入的 emoji 表情
      emojis: [
        ['😄', '😷', '😂', '😝', '😳', '😱', '😔', '😒', '😉'],
        ['😎', '😭', '😍', '😘', '🤔', '😕', '🙃', '🤑', '😲'],
        ['🙄', '😤', '😴', '🤓', '😡', '😑', '😮', '🤒', '🤮']
      ],
      // 用于插入的 html 模板
      templates: [
        '<section style="text-align: center; margin: 0px auto;"><section style="border-radius: 4px; border: 1px solid #757576; display: inline-block; padding: 5px 20px;"><span style="font-size: 18px; color: #595959;">标题</span></section></section>',
        '<div style="width: 100%; box-sizing: border-box; border-radius: 5px; background-color: #f6f6f6; padding: 10px; margin: 10px 0"><div>卡片</div><div style="font-size: 12px; color: gray">正文</div></div>',
        '<div style="border: 1px solid gray; box-shadow: 3px 3px 0px #cfcfce; padding: 10px; margin: 10px 0">段落</div>'
      ],
      circles: [], // 存储圈子列表
      selectedCircleId: null, // 存储选中的圈子id
      uploadedImages: [], // 存储上传成功的图片地址
    }
  },
  watch: {
    selectValue: {
      handler(val) {
        if (val.length > 3) {
          this.$refs.modalRef.showModal({
            title: '提示',
            content: '最多只能选择三个标签',
          })
          console.log("检测到超过三个")
          this.selectValue.pop()
        }
      },
      deep: true
    }
  },

  onReady() {
    /**
     * @description 设置获取链接的方法
     * @param {String} type 链接的类型（img/video/audio/link）
     * @param {String} value 修改链接时，这里会传入旧值
     * @returns {Promise} 返回线上地址
     *   type 为音视频时可以返回一个数组作为源地址
     *   type 为 audio 时，可以返回一个 object，包含 src、name、author、poster 等字段
     */
    this.$refs.article.getSrc = (type, value) => {
      return new Promise((resolve, reject) => {
        if (type === 'img' || type === 'video') {
          uni.showActionSheet({
            itemList: ['本地选取', '远程链接'],
            success: res => {
              if (res.tapIndex === 0) {
                // 本地选取
                if (type === 'img') {
                  uni.chooseImage({
                    count: value === undefined ? 9 :
                        1, // 2.2.0 版本起插入图片时支持多张（修改图片链接时仅限一张）
                    success: res => {
                      // #ifdef MP-WEIXIN
                      if (res.tempFilePaths.length == 1 && wx
                          .editImage) {
                        // 单张图片时进行编辑
                        wx.editImage({
                          src: res.tempFilePaths[
                              0],
                          complete: res2 => {
                            uni.showLoading({
                              title: '上传中'
                            })
                            this.uploadFile(res2
                                    .tempFilePath ||
                                res
                                    .tempFilePaths[
                                    0],
                                type)
                                .then(
                                    res => {
                                      uni.hideLoading()
                                      resolve
                                      (
                                          res)
                                    })
                          }
                        })
                      } else {
                        // #endif
                        uni.showLoading({
                          title: '上传中'
                        });
                        (async () => {
                          const arr = []
                          for (let item of res
                              .tempFilePaths) {
                            // 依次上传
                            const src =
                                await this.uploadFile(
                                    item, type)
                            arr.push(src)
                          }
                          return arr
                        })().then(res => {
                          uni.hideLoading()
                          resolve(res)
                        })
                        // #ifdef MP-WEIXIN
                      }
                      // #endif
                    },
                    fail: reject
                  })
                } else {
                  uni.chooseVideo({
                    success: res => {
                      uni.showLoading({
                        title: '上传中'
                      })
                      this.uploadFile(res.tempFilePath, type).then(
                          res => {
                            uni.hideLoading()
                            resolve(res)
                          })
                    },
                    fail: reject
                  })
                }
              } else {
                // 远程链接
                this.callback = {
                  resolve,
                  reject
                }
                this.$set(this, 'modal', {
                  title: (type === 'img' ? '图片' : '视频') + '链接',
                  value
                })
              }
            }
          })
        } else {
          this.callback = {
            resolve,
            reject
          }
          let title
          if (type === 'audio') {
            title = '音频链接'
          } else if (type === 'link') {
            title = '链接地址'
          }
          this.$set(this, 'modal', {
            title,
            value
          })
        }
      })
    }
  },

  mounted() {
    // 在mounted中获取ref引用
    this.modalRef = this.$refs.modalRef
    this.imageUpload = this.$refs.imageUpload
  },
  methods: {
    // 上传文件方法
    uploadFile(filePath, type) {
      return new Promise((resolve, reject) => {
        uni.uploadFile({
          url: this.action,
          filePath: filePath,
          name: 'image',
          formData: this.formData,
          success: res => {
            try {
              const data = JSON.parse(res.data)
              if (data.code === 200 && data.data && data.data.url) {
                resolve(data.data.url)
              } else {
                uni.showToast({
                  title: '上传失败',
                  icon: 'none'
                })
                reject(new Error('上传失败'))
              }
            } catch (e) {
              uni.showToast({
                title: '上传失败',
                icon: 'none'
              })
              reject(e)
            }
          },
          fail: err => {
            uni.showToast({
              title: '上传失败',
              icon: 'none'
            })
            reject(err)
          }
        })
      })
    },

    // 删除图片/视频/音频标签事件
    remove(e) {
      // 删除线上资源
      console.log('删除资源:', e.src)
    },

    // 处理模态框
    modalInput(e) {
      this.value = e.detail.value
    },

    modalConfirm() {
      if (this.callback && this.callback.resolve) {
        this.callback.resolve(this.value || this.modal.value || '')
      }
      this.$set(this, 'modal', null)
    },

    modalCancel() {
      if (this.callback && this.callback.reject) {
        this.callback.reject()
      }
      this.$set(this, 'modal', null)
    },

    pickerChange(e) {
      this.$set(this, e.currentTarget.dataset.type, parseInt(e.detail.value) + 1)
    },

    // 处理底部弹窗
    openDialog(e) {
      this.checkEditable().then(() => {
        this.$set(this, 'dialog', e.currentTarget.dataset.type)
      }).catch(() => {
      })
    },

    closeDialog() {
      this.$set(this, 'dialog', false)
    },

    // 检查是否可编辑
    checkEditable() {
      return new Promise((resolve, reject) => {
        if (this.editable) {
          resolve()
        } else {
          uni.showModal({
            content: '需要继续编辑吗？',
            success: res => {
              if (res.confirm) {
                // 切换编辑状态
                this.save()
                resolve()
              } else {
                reject()
              }
            }
          })
        }
      })
    },

    // 调用编辑器接口
    edit(e) {
      this.checkEditable().then(() => {
        this.$refs.article[e.currentTarget.dataset.method](e.currentTarget.dataset.param)
      }).catch(() => {
      })
    },

    // 插入 head 系列标签
    insertHead() {
      this.checkEditable().then(() => {
        uni.showActionSheet({
          itemList: ['大标题', '中标题', '小标题'],
          success: res => {
            let tagName = ['h1', 'h3', 'h5'][res.tapIndex]
            this.$refs.article.insertHtml(`<${tagName}>标题</${tagName}>`)
          }
        })
      }).catch(() => {
      })
    },

    // 插入表格
    insertTable() {
      this.checkEditable().then(() => {
        this.$set(this, 'modal', {
          title: '插入表格'
        })
        this.callback = {
          resolve: () => {
            this.$refs.article.insertTable(this.rows, this.cols)
          },
          reject: () => {
          }
        }
      }).catch(() => {
      })
    },

    // 插入代码
    insertCode() {
      this.checkEditable().then(() => {
        uni.showActionSheet({
          itemList: ['css', 'javascript', 'json'],
          success: res => {
            const lan = ['css', 'javascript', 'json'][res.tapIndex]
            this.$refs.article.insertHtml(
                `<pre><code class="language-${lan}">${lan} code</code></pre>`)
          }
        })
      }).catch(() => {
      })
    },

    // 插入 emoji
    insertEmoji(e) {
      this.$refs.article.insertHtml(e.currentTarget.dataset.emoji)
      this.closeDialog()
    },

    // 插入模板
    insertTemplate(e) {
      this.$refs.article.insertHtml(e.currentTarget.dataset.template)
      this.closeDialog()
    },

    // 清空编辑器内容
    clearEditor() {
      uni.showModal({
        title: '确认',
        content: '确定清空内容吗？',
        success: res => {
          if (res.confirm) {
            this.$refs.article.clear()
            this.content = ''
          }
        }
      })
    },

    // 清空图片上传列表
    clearUpload() {
      if (this.$refs.imageUpload) {
        this.$refs.imageUpload.clear()
        this.uploadedImages = []
      }
    },

    // 加载内容
    load() {
      this.checkEditable().then(() => {
        uni.showModal({
          title: '提示',
          content: '导入内容将覆盖现有内容，是否继续？',
          success: res => {
            if (res.confirm) {
              if (!uni.chooseMessageFile) {
                return uni.showModal({
                  title: '失败',
                  content: '暂时无法使用此功能',
                  showCancel: false
                })
              }
              uni.chooseMessageFile({
                count: 1,
                type: 'file',
                extension: ['txt', 'html'],
                success: res => {
                  const content = uni.getFileSystemManager()
                      .readFileSync(res.tempFiles[0]
                          .path, 'utf8')
                  this.$refs.article.setContent(content)
                }
              })
            }
          }
        })
      }).catch(() => {
      })
    },

    // 保存编辑器内容
    save() {
      setTimeout(() => {
        if (this.editable) {
          var content = this.$refs.article.getContent()
          uni.showModal({
            title: '保存',
            content,
            confirmText: '完成',
            success: res => {
              if (res.confirm) {
                // 复制到剪贴板
                uni.setClipboardData({
                  data: content,
                })
                this.content = content
                // 结束编辑
                this.editable = false
              }
            }
          })
        } else {
          this.editable = true
        }
      }, 50)
    },

// 手动上传文件
    upload() {
      this.$refs.imageUpload.upload()
    },

    // 页面跳转
    tn(router) {
      if (router === '/group_pages/views/all-tag') {
        // 将当前选中的标签和标签列表都传递给 all-tag 页面
        uni.navigateTo({
          url: `${router}?selected=${encodeURIComponent(JSON.stringify(this.selectValue))}&tagList=${encodeURIComponent(JSON.stringify(this.tagList))}`
        })
      } else {
        uni.navigateTo({url: router})
      }
    },

    // 发布动态
    async publishPost() {
      // 获取富文本内容
      const content = this.$refs.article ? this.$refs.article.getContent() : ''

      // 验证表单
      if (!this.formData.title) {
        uni.showToast({
          title: '请填写标题',
          icon: 'none'
        })
        return
      }

      if (!this.selectedCircleId) {
        uni.showToast({
          title: '请选择一个圈子',
          icon: 'none'
        })
        return
      }

      try {
        // 构造请求数据
        const blogData = {
          title: this.formData.title,
          content: content,
          tags: this.selectValue, // 已经是数组格式
          images: this.uploadedImages, // 使用收集的图片地址数组
          circleId: this.selectedCircleId
        }

        // 调用添加动态接口
        const res = await add2(blogData)
        if (res.code === 1) {
          uni.showToast({
            title: '发布成功',
            icon: 'success'
          })
          // 发布成功后清空表单
          this.formData.title = ''
          this.content = ''
          this.selectValue = []
          this.uploadedImages = []
          this.fileList = []
          this.selectedCircleId = null

          // 返回上一页
          uni.navigateBack()
        } else {
          uni.showToast({
            title: res.msg || '发布失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('发布动态失败:', error)
        uni.showToast({
          title: '发布失败',
          icon: 'none'
        })
      }
    },

    // 清空已选标签
    clearTags() {
      uni.showModal({
      title: '提示',
        content: '确定清空已选标签吗？',
        success: (res) => {
          if (res.confirm) {
            this.selectValue = []
          }
        }
      })
    },

    async getTopTags() {
      try {
        const res = await queryTagTop()
        console.log('获取热门标签', res)
        if (res.code === 1) {
          // 保存当前已选标签
          const currentSelectedTags = this.tagList.filter(tag => this.selectValue.includes(tag.title))
          // 格式化热门标签数据
          const topTags = res.data.map(tag => ({
            color: tag.color || 'blue',
            title: tag.title
          }))
          // 合并已选标签和热门标签
          const newTags = [...currentSelectedTags]
          topTags.forEach(tag => {
            if (!newTags.some(t => t.title === tag.title)) {
              newTags.push(tag)
            }
          })
          this.tagList = newTags
        } else {
          uni.showToast({
            title: '获取热门标签失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('获取热门标签失败:', error)
        uni.showToast({
          title: '获取热门标签失败',
          icon: 'none'
        })
      }
    },

    // 自定义上传回调函数
    customUploadCallback(data) {
      try {
        const res = JSON.parse(data.data)
        console.log('上传回调', res)
        if (res.code === 1 && res.data) {
          // 将上传成功的图片地址添加到数组中
          this.uploadedImages.push(res.data)
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
    },

    // 获取圈子列表
    async getCircles() {
      try {
        const res = await getAll3()
        if (res.code === 1) {
          this.circles = res.data
        } else {
          uni.showToast({
            title: '获取圈子列表失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('获取圈子列表失败:', error)
        uni.showToast({
          title: '获取圈子列表失败',
          icon: 'none'
        })
      }
    },

    // 选择圈子
    selectCircle(id) {
      this.selectedCircleId = id
    },
  },
  computed: {
    selectedTags() {
      return this.tagList.filter(tag => this.selectValue.includes(tag.title))
    }
  },
  onLoad() {
    this.getTopTags()
    this.getCircles() // 加载圈子列表
  }
}
</script>

<template>
  <view class="add_group_container">
    <navbar title="编辑动态" back="left" home="home"></navbar>

    <view class="tn-u-safe-area--more">
      <view class="tn-m tn-gray-light_bg" style="border-radius: 10rpx;padding: 20rpx 30rpx;">
        <input v-model="formData.title" placeholder="请填写动态标题吖" name="input" placeholder-style="color:#AAAAAA"/>
      </view>
      <view class="tn-flex tn-flex-center-between tn-pt tn-m">

        <view class="tn-flex">
          <view class="tn-black_bg tn-white_text tn-text-center"
                style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
            <tn-icon name="topics" style="font-size: 30rpx;"></tn-icon>
          </view>
          <view class="tn-text-lg tn-pr-xs tn-text-bold">想说点什么</view>
        </view>
        <view class="tn-gray_text">
          <text class="tn-p-xs">500字内</text>
          <tn-icon name="keyboard-circle"></tn-icon>
        </view>
      </view>
      <!--编辑区域-->
      <view style="display: flex;">
        <i class="iconfont icon-undo" data-method="undo" @tap="edit"/>
        <i class="iconfont icon-redo" data-method="redo" @tap="edit"/>
        <i class="iconfont icon-emoji" data-type="emoji" @tap="openDialog"/>
        <i class="iconfont icon-link" data-method="insertLink" @tap="edit"/>
        <i class="iconfont icon-text" data-method="insertText" @tap="edit"/>
        <i class="iconfont icon-heading" @tap="insertHead"/>
      </view>
      <view style="display: flex;">
        <i class="iconfont icon-quote" data-method="insertHtml"
           data-param="<blockquote style='padding:0 1em;color:#6a737d;border-left:.25em solid #dfe2e5'>引用</blockquote>"
           @tap="edit"/>
        <i class="iconfont icon-table" @tap="insertTable"/>
        <i class="iconfont icon-code" @tap="insertCode"/>
        <i class="iconfont icon-template" data-type="template" @tap="openDialog"/>
        <i class="iconfont icon-clear" @tap="clearEditor"/>
        <i :class="'iconfont icon-'+(editable?'save':'edit')" @tap="save"/>
      </view>
      <!-- 富文本 -->
      <view style="padding-top: 20px;">
        <mp-html ref="article" container-style="padding:20px;min-height:calc(100vh - 450px)" :content="content"
                 domain="https://assets.example.invalid/placeholder.png" :tag-style="tagStyle" :editable="editable"
                 @remove="remove"/>
      </view>
      <!--      <view class="tn-m tn-gray-light_bg tn-p" style="border-radius: 10rpx;">-->

      <!--        <textarea maxlength="500" placeholder="说点什么 , 万一火了呢" placeholder-style="color:#AAAAAA"></textarea>-->
      <!--      </view>-->

      <view class="tn-flex tn-flex-center-between tn-pt-xl tn-m">
        <view class="tn-flex">
          <view class="tn-black_bg tn-white_text tn-text-center"
                style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
            <tn-icon name="image" style="font-size: 30rpx;"></tn-icon>
          </view>
          <view class="tn-text-lg tn-pr-xs tn-text-bold">发点什么图咧</view>
        </view>
        <view class="tn-text-df tn-gray_text" @tap="clearUpload">
          <text class="tn-p-xs">清空上传</text>
          <tn-icon name="delete"></tn-icon>
        </view>
      </view>
      <view class="tn-ml tn-pt-xs">
        <tn-image-upload ref="imageUpload" :action="action" :form-data="formData" v-model="fileList"
                         :disabled="disabled" :auto-upload="autoUpload" :limit="maxCount"
                         :show-upload-progress="showProgress" :custom-upload-callback="customUploadCallback">
        </tn-image-upload>
      </view>
      <!--此处展示圈子类型,只展示圈子的name-->
      <view class="tn-flex tn-flex-center-between tn-pt-xl tn-m">
        <view class="tn-flex">
          <view class="tn-black_bg tn-white_text tn-text-center"
                style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
            <tn-icon name="group-circle" style="font-size: 30rpx;"></tn-icon>
          </view>
          <view class="tn-text-lg tn-pr-xs tn-text-bold">选择圈子</view>
        </view>
      </view>
      <!--此处是选择的圈子-->
      <view class="tn-m">
        <scroll-view scroll-x class="tn-flex">
          <view v-for="circle in circles"
                :key="circle.id"
                class="tn-tag-content__item tn-round tn-text-sm tn-text-bold tn-mr-sm "
                :class="[selectedCircleId === circle.id ? 'tn-blue_bg tn-white_text' : 'tn-gray-light_bg']"
                @tap="selectCircle(circle.id)">
            <text>{{ circle.name }}</text>
          </view>
        </scroll-view>
      </view>

      <!-- 在此处展示 已选标签  -->
      <view v-if="selectValue.length > 0" class="tn-m tn-pb">
        <view class="tn-flex tn-flex-center-between tn-mb">
        <view class="tn-flex">
          <view class="tn-black_bg tn-white_text tn-text-center"
                style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
            <tn-icon name="tag" style="font-size: 30rpx;"></tn-icon>
            </view>
            <view class="tn-text-lg tn-pr-xs tn-text-bold">已选标签</view>
          </view>
          <view class="tn-text-df tn-gray_text" @tap="clearTags">
            <text class="tn-p-xs">清空</text>
            <tn-icon name="delete"></tn-icon>
          </view>
        </view>
        <view class="tn-flex tn-flex-wrap">
          <view v-for="(tag, index) in selectedTags" :key="index"
                class="tn-tag-content__item tn-round tn-text-sm tn-text-bold tn-mr-sm tn-mb-sm"
                :class="[`tn-gradient-bg__${tag.color}-light tn-${tag.color}_text`]">
            <text class="tn-tag-content__item--prefix">#</text>
            <text>{{ tag.title }}</text>
          </view>
        </view>
      </view>

      <view class="tn-tag-content tn-m tn-pb">
        <tn-checkbox-group v-model="selectValue">
          <tn-checkbox v-for="(item, index) in tagList" :key="item.title"
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

      <!-- 悬浮按钮-->
      <view class="tn-flex tn-footerfixed">
        <view class="tn-flex-1 tn-m-sm tn-text-center tn-flex-center-center">
          <tn-button bg-color="#00FFC6" height="80rpx" padding="40rpx 0" width="60%" shadow bold
                     @tap="publishPost">
            <text class="tn-black_text">发 布</text>
            <tn-icon name="camera" class="tn-pl-xs tn-black_text"></tn-icon>
          </tn-button>
        </view>
      </view>

    </view>
    <view class='tn-tabbar-height'></view>

    <!-- 模态框 -->
    <block v-if="modal">
      <view class="mask"/>
      <view class="modal">
        <view class="modal_title">{{ modal.title }}</view>
        <view class="modal_body">
          <block v-if="modal.title === '插入表格'">
            <view style="display:flex;justify-content:space-between;margin-bottom:10px">
              <text>行数</text>
              <picker :value="rows-1" :range="[1,2,3,4,5,6,7,8,9,10]" data-type="rows" @change="pickerChange">
                <view class="modal_picker">{{ rows }}</view>
              </picker>
            </view>
            <view style="display:flex;justify-content:space-between">
              <text>列数</text>
              <picker :value="cols-1" :range="[1,2,3,4,5,6,7,8,9,10]" data-type="cols" @change="pickerChange">
                <view class="modal_picker">{{ cols }}</view>
              </picker>
            </view>
          </block>
          <input v-else class="modal_input" :value="modal.value" maxlength="-1" auto-focus @input="modalInput"/>
        </view>
        <view class="modal_foot">
          <view class="modal_button" @tap="modalCancel">取消</view>
          <view class="modal_button" style="color:#576b95;border-left:1px solid rgba(0,0,0,.1)" @tap="modalConfirm">
            确定
          </view>
        </view>
      </view>
    </block>

    <!-- 底部弹窗 -->
    <block v-if="dialog">
      <view class="mask" @tap="closeDialog"/>
      <view class="weui-half-screen-dialog">
        <view class="weui-half-screen-dialog__hd">
          <view class="weui-half-screen-dialog__hd__side" @tap="closeDialog">
            <view class="weui-icon-btn weui-icon-btn_close">
              <i class="weui-icon-close-thin"></i>
            </view>
          </view>
          <view class="weui-half-screen-dialog__hd__main">
            <text class="weui-half-screen-dialog__title">{{ dialog === 'emoji' ? '插入表情' : '插入模板' }}</text>
          </view>
        </view>
        <view class="weui-half-screen-dialog__bd">
          <block v-if="dialog === 'emoji'">
            <view v-for="(line, index) in emojis" :key="index" style="display:flex">
              <view v-for="(item, idx) in line" :key="idx"
                    style="flex:1;text-align:center;line-height:3em;font-size:16px" :data-emoji="item"
                    @tap="insertEmoji">
                {{ item }}
              </view>
            </view>
          </block>
          <block v-else-if="dialog === 'template'">
            <view v-for="(item, index) in templates" :key="index"
                  style="margin:10px 0;padding:10px;border:1px solid #eee" :data-template="item" @tap="insertTemplate">
              <rich-text :nodes="item"></rich-text>
            </view>
          </block>
        </view>
      </view>
    </block>

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


.editor_toolbox {
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  background-color: #ededed;
  padding: 5px;
  box-sizing: border-box;
}

@font-face {
  font-family: "iconfont";
  src: url('data:application/x-font-woff2;charset=utf-8;base64,AAEAAAALAIAAAwAwR1NVQiCLJXoAAAE4AAAAVE9TLzI8c0+vAAABjAAAAGBjbWFwnIeZxQAAAjQAAALIZ2x5Zurk4B8AAAUkAAALXGhlYWQhfzifAAAA4AAAADZoaGVhB+ADkgAAALwAAAAkaG10eEgAAAAAAAHsAAAASGxvY2EdyhrcAAAE/AAAACZtYXhwASYAbgAAARgAAAAgbmFtZRCjPLAAABCAAAACZ3Bvc3R6cXe1AAAS6AAAAO8AAQAAA4D/gABcBAAAAP//BAEAAQAAAAAAAAAAAAAAAAAAABIAAQAAAAEAAF6mF8lfDzz1AAsEAAAAAADfL3pRAAAAAN8velEAAP9/BAEDgQAAAAgAAgAAAAAAAAABAAAAEgBiAAoAAAAAAAIAAAAKAAoAAAD/AAAAAAAAAAEAAAAKADAAPgACREZMVAAObGF0bgAaAAQAAAAAAAAAAQAAAAQAAAAAAAAAAQAAAAFsaWdhAAgAAAABAAAAAQAEAAQAAAABAAgAAQAGAAAAAQAAAAQEAAGQAAUAAAKJAswAAACPAokCzAAAAesAMgEIAAACAAUDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFBmRWQAwOZd7N8DgP+AAAAD3ACBAAAAAQAAAAAAAAAAAAAAAAACBAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAAAAABQAAAAMAAAAsAAAABAAAAewAAQAAAAAA5gADAAEAAAAsAAMACgAAAewABAC6AAAAIgAgAAQAAuZd5vznNec950fnoef96brp7unw6wPrBesK63Hrdezf//8AAOZd5vznNec950fnoef96brp7unw6wPrBesJ63Hrdezf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABACIAIgAiACIAIgAiACIAIgAiACIAIgAiACIAJAAkACQAAAAPABAADgANAAEAAgADAAQABQAGAAcACAAJAAoACwAMABEAAAEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAANwAAAAAAAAAEQAA5l0AAOZdAAAADwAA5vwAAOb8AAAAEAAA5zUAAOc1AAAADgAA5z0AAOc9AAAADQAA50cAAOdHAAAAAQAA56EAAOehAAAAAgAA5/0AAOf9AAAAAwAA6boAAOm6AAAABAAA6e4AAOnuAAAABQAA6fAAAOnwAAAABgAA6wMAAOsDAAAABwAA6wUAAOsFAAAACAAA6wkAAOsJAAAACQAA6woAAOsKAAAACgAA63EAAOtxAAAACwAA63UAAOt1AAAADAAA7N8AAOzfAAAAEQAAAAAAcgD4ARQBaAHeAkYCkAMOA5QD7gRIBKIE7AUEBWAFlAWuAAAAAwAA/38EAQOBAAwALgBQAAABNTQmIgYdARQWMjY1AzMnERQGIyEiJjURNCYiBhURFB4BMyEyPgE1ESMiBhQWMxMhIiY1EQchMhYVERQWMjY1ETQuASMhERQeATMhMjY0JiMCmg8WDw8WDxqzGS0g/k0gLQ8VDyI7IwGzIjsjzQsPDwvN/TMgLRkDZiAtDxUPIjsj/IAiOyMCzQoPDwoCM80LDw8LzQoPDwoBGhn+miAtLSABZgsPDwv+miM7IiI7IwGADxUP/GYtIANmGS0g/JoLDw8LA2YjOyL8gCM7Ig8VDwAABQAA/8ADvwNBABQAKQA+AEsAWAAABSInJicmNDc2NzYyFxYXFhQHBgcGAyIHBgcGFBcWFxYyNzY3NjQnJicmAyImJyY+ARYXHgEyNjc+AR4BBw4BAyImPQE0NjIWHQEUBiEiJj0BNDYyFh0BFAYB/3poZTw9PTxlaPNpZTw9PTxlaXluXlw2Nzc2XF7cXlw2Nzc2XF5tRHgpBgIOEgUkZnRoIwUSDgIFKnnODRMTGxISAQgOEhIbExNAPTxlafJpZTw9PTxlafJpZTw9A1U3Nlxe3F5cNjc3Nlxe3F5cNjf9bjo1BxILAgcuMjMuBwMLEgc2OwEKEw1ADRMTDUANExMNQA0TEw1ADRMAAAEAAAAAA5EBpQAPAAABISIGHQEUFjMhMjY9ATQmA4j88AMFBQMDEAMFBQGkBQM4AwUFAzgDBQAAAwAA/9UEAQMsAAwAHAAtAAAFIiY3Ez4BHgEHAw4BJSImNxMDJj4BFwEWFAcBBiEiJwEmNDcBPgEeAQcDExYGAcAKDQKAAQ8RCgGAAQwBIw4LCPX1CAkZBwEABQX/AAb+IAoG/wAFBQEABRIOAgb19QgLKw8KAysICwMOCfzVBwtWGAoBMwEzCRcECv7ABg4G/sAICAFABg4GAUAHAgsRB/7N/s0KGAAABgAA/9UDqwMrAA8AGQAkADcARABNAAAFISImNRE0NjMhMhYVERQGJSEyNj0BJyYiBwEVFBY7ATcnJiIHAyIGFRE3NjIfATc2Mh8BETQmIwEiLgE0PgEyHgEUDgEnIgYUFjI2NCYDdf0WFiAgFgLqFiAg/ZICWAUGuQMJA/3IBgVWwFkECAStBQaaECwQWdsPLBCbBgX9yx0xHR0xOjEdHTEdGyUlNiUmKyAWAuoWICAW/RYWICsGBde5AwP+x1cFBsFZAwMB5gYF/amaEBBZ2hAQmwHXBQb+1R0xOjEdHTE6MR2rJTYlJTUmAAAEAAAAAAQBAqsADwAfADQARAAAASEiBhURFBYzITI2NRE0JgMUBiMhIiY1ETQ2MyEyFhUlJgYPAQ4BFREUFh8BHgE+ATURNCYDFAYvASY1ETQ/ATYyFxYVAkv96hYfHxYCFhYfHwwGBP3qBAYGBAIWBAYBkAwdDcAMDg4MwA0dGQ4OHQoGwAUFwAMFAwUCqyAW/hYWICAWAeoWIP3gBQYGBQHqBQYGBS4IAQdyBxkO/voOGQdyBwEPGQ4B6g8Y/e8GBgNyAwYBBgYDcgICAwYAAgAA/9UDrwMsACMALgAABSEiJjURNDYzITIWFAYjISIGFREUFjMhMjY1ETQ2MhYVERQGASImNwE2HgEHAQYDdf0WFiAgFgHgCQwMCf4gBQYGBQLqBQYMEg0g/ooNDAgBgAcZCgf+gAYrIBYC6hYgDRIMBgX9FgUGBgUB4AkMDAn+IBYgAQAXCwIqCwIXCv3WCgAAAAIAAP/XA6kDKQApAFQAAAUiLgI0Nj8BPgEyFhcWFAYiJy4BIgYPAQ4BFB4CMjY/ATYeAQ8BDgEBNj8BPgE0LgIiBg8BBhQWMj8BPgEyHgIUBg8BDgEiJicmIgYUFx4BMgEtKk89Hx8fiB5PU08eBw0RBxg+RT4YiBgaGjA/RD4YSAkYBglHH08BRigeiB8fHz5OVE4fRwcNEQdIGD5EPzAaGhiIGD5FPhgGEg0HHk9TKB89T1NPH4cfICAfBhENBhgaGhiIGD5EPzAaGhhICQcYCEgfHwEuEB+HH09TTz0fHx9IBhIMBkgYGhowP0Q+GIgYGhoYBg0RBh8gAAQAAP/VA9YDAQAgADAAUQBhAAAFIyImPQE0Nz4BNzYzMhYUBiMiBgcOAQc2OwEyFh0BFAYDIgYdARQWOwEyNj0BNCYjASMiJj0BNDc+ATc2MzIWFAYjIgYHDgEHNjsBMhYdARQGAyIGHQEUFjsBMjY9ATQmIwOA1SQyHBtjQUVLCQwMCT50LiQvCBIU1SMyMvgSGRkS1RIZGRL91dUjMhsbY0FFSwkNDQk+cy4kLwkSFNUkMjL5EhkZEtUSGRkSKzIk1WFeW5EpLAwSDUhENIJHCTIj1SQyAVYZEtUSGRkS1RIZ/qoyJNVhXluRKSwMEg1IRDSCRwkyI9UkMgFWGRLVEhkZEtUSGQAAAAAIAAD/1QPWAwAADwAWABoAIQAlACkALQA3AAAFISImNRE0NjMhMhYVERQGJTMyNj0BIQUhNSkBFRQWOwE1JSE1IQUhNSEFITUhJTU0JiMhIgYdAQOg/MAWHx8WA0AWHx/+9fUEB/8A/tUBAP8A/tUHBPUBVgEA/wD+1QEA/wD+1QEA/wADVgcE/MAEBysgFgLAFh8fFv1AFiArBgXK1dXKBQbVK9XV1dXVK8sEBgYEywAAAAABAAD/qgPWAzgAOwAABTI3Njc2LgEGBw4BIyInJicmNDc2NzYzMh4BFyMiBhQWOwEyNj0BNCYiBh0BJicmJyYGBwYHBhYXFhcWAhVkWVc/BgINEgU6oVhuXlw2Nzc2XF5uVJZxHqMJDQ0J1QkMDBIML1dVaGzRT0wjIyBAPl9hVSkpSAcRDAEHQko4Nltf3F5cNjdAc00MEg0NCdUJDAwJlmNCQRMVQExKZWfZWVYxMgAAAAABAAD/qgPiAzgAOwAABSInJicmPgEWFx4BMzI3Njc2NCcmJyYjIg4BBzMyFhQGKwEiJj0BNDYyFh0BNjc2NzYWFxYXFgYHBgcGAhVjWlY/BgEOEQY6oFhuX1s2ODg2W19uU5ZxH6QJDAwJ1QkNDRIML1dUaWvSTk0iJCBAP15hVSkpSAcRDAEHQko4Nltf3F5cNjdAc00MEg0NCdUJDAwJlmNCQRMVQExKZWfZWVYxMgAAAAAKAAD/oAPgA18AAwAHAAsADwATABcAGwAfACMAJwAAExEhEQMhESEHIRUhJyE1IQEhNSEXMxUjByE1IRczFSMFIREhFzMRIyEDvin8lANsXP1NArMh/Y4Ccv7vATL+ziHw8CEBMv7OIfDw/mUBMf7PIPHxA1/8QgO+/GsDa3D1IbP+b30hO899ITtjAYch/roAAAAAAQAAAAADKwLWAAsAAAERMxEjESERIxEzEQLVVlb+VlZWAasBKv0rAVX+qwLV/tYAAgAAAAADdQL1ABsAQAAAATU0JiIGHQEUFjsBMjY0JisBIic3Ni4CDwEmJSIGFREUBiMhIiY1ETQ2MyEyNjQmIyEiBhURFBYzITI2NRE0JgIFDhMNNid0Cg0NCnQGBvgEAQwQB/kCAVgKDRsU/dIUGxsUAXQKDQ0K/ownNjYnAi4nNg0BtHQKDQ0KdCc2DRMOAvgHEAwCBPgGRg0K/owUGxsUAi4UGw0UDTYn/dInNjYnAXQKDQAAAAUAAP/hA6ADHAAHAAsAEwAXABsAAAE1IRUjFSE1JSEVIQEhESMRIREjJTMRIxMzESMC0/5PvwM9/a0BW/6lAcD92ysCeyv+eysruisrAmqysisrh4f9owHz/eICFQn+ewGF/nsAAAAAAQAA/7wDTAMIAA8AAAEVIzUjETMVITUzESMVIzUDSzzxY/7+Y/E8Awi1eP0tPDwC03i1AAAAEgDeAAEAAAAAAAAAEwAAAAEAAAAAAAEACAATAAEAAAAAAAIABwAbAAEAAAAAAAMACAAiAAEAAAAAAAQACAAqAAEAAAAAAAUACwAyAAEAAAAAAAYACAA9AAEAAAAAAAoAKwBFAAEAAAAAAAsAEwBwAAMAAQQJAAAAJgCDAAMAAQQJAAEAEACpAAMAAQQJAAIADgC5AAMAAQQJAAMAEADHAAMAAQQJAAQAEADXAAMAAQQJAAUAFgDnAAMAAQQJAAYAEAD9AAMAAQQJAAoAVgENAAMAAQQJAAsAJgFjQ3JlYXRlZCBieSBpY29uZm9udGljb25mb250UmVndWxhcmljb25mb250aWNvbmZvbnRWZXJzaW9uIDEuMGljb25mb250R2VuZXJhdGVkIGJ5IHN2ZzJ0dGYgZnJvbSBGb250ZWxsbyBwcm9qZWN0Lmh0dHA6Ly9mb250ZWxsby5jb20AQwByAGUAYQB0AGUAZAAgAGIAeQAgAGkAYwBvAG4AZgBvAG4AdABpAGMAbwBuAGYAbwBuAHQAUgBlAGcAdQBsAGEAcgBpAGMAbwBuAGYAbwBuAHQAaQBjAG8AbgBmAG8AbgB0AFYAZQByAHMAaQBvAG4AIAAxAC4AMABpAGMAbwBuAGYAbwBuAHQARwBlAG4AZQByAGEAdABlAGQAIABiAHkAIABzAHYAZwAyAHQAdABmACAAZgByAG8AbQAgAEYAbwBuAHQAZQBsAGwAbwAgAHAAcgBvAGoAZQBjAHQALgBoAHQAdABwADoALwAvAGYAbwBuAHQAZQBsAGwAbwAuAGMAbwBtAAACAAAAAAAAAAoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIBAgEDAQQBBQEGAQcBCAEJAQoBCwEMAQ0BDgEPARABEQESARMABHNhdmUKZW1vamlsaWdodARsaW5lCTI0Z2wtY29kZQwyNGdsLXBpY3R1cmUQMjRnbC12aWRlb0NhbWVyYQkyNGdsLWVkaXQJMjRnbC1saW5rDjI0Z2wtcXVvdGVMZWZ0CjI0Z2wtdGFibGUKMjRnbC1yZWRvMwoyNGdsLXVuZG8zCHRlbXBsYXRlB2hlYWRpbmcFZGFvcnUIQ2xlYXItMDEEdGV4dAAAAA==') format('woff2');
}

.iconfont {
  flex: 1;
  text-align: center;
  font-family: "iconfont" !important;
  font-size: 22px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-undo:before {
  content: "\eb75";
}

.icon-redo:before {
  content: "\eb71";
}

.icon-img:before {
  content: "\e9ee";
}

.icon-video:before {
  content: "\e9f0";
}

.icon-link:before {
  content: "\eb05";
}

.icon-text:before {
  content: "\ecdf";
}

.icon-line:before {
  content: "\e7fd";
}

.icon-heading:before {
  content: "\e735";
}

.icon-heading:before {
  content: "\e735";
}

.icon-quote:before {
  content: "\eb09";
}

.icon-table:before {
  content: "\eb0a";
}

.icon-code:before {
  content: "\e9ba";
}

.icon-emoji:before {
  content: "\e7a1";
}

.icon-template:before {
  content: "\e73d";
}

.icon-clear:before {
  content: "\e6fc";
}

.icon-load:before {
  content: "\e65d";
}

.icon-edit:before {
  content: "\eb03";
}

.icon-save:before {
  content: "\e747";
}


/* 模态框 */
.modal {
  position: fixed;
  top: 50%;
  left: 16px;
  right: 16px;
  background-color: #fff;
  border-radius: 12px;
  transform: translateY(-50%);
  text-align: center;
}

.modal_title {
  padding: 32px 24px 16px;
  font-size: 17px;
  font-weight: 700;
  text-align: center;
}

.modal_body {
  padding: 0 24px;
  margin-bottom: 32px;
  font-size: 17px;
  line-height: 1.4;
  word-wrap: break-word;
  color: rgba(0, 0, 0, .5);
}

.modal_input {
  display: block;
  padding: 5px;
  font-size: 14px;
  border: 1px solid #dfe2e5;
}

.modal_picker {
  display: inline-block;
  margin: 0 5px;
  border-bottom: 1px solid gray;
  padding: 0 5px;
}

.modal_foot {
  display: flex;
  line-height: 56px;
  font-weight: 700;
  border-top: 1px solid rgba(0, 0, 0, .1);
}

.modal_button {
  flex: 1;
  text-align: center;
}

.fadeIn {
  -webkit-animation: c .3s forwards;
  animation: c .3s forwards;
}

.weui-half-screen-dialog {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  max-height: 75%;
  z-index: 5000;
  line-height: 1.4;
  background-color: #fff;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  overflow: hidden;
  padding: 0 24px;
  padding: 0 calc(24px + constant(safe-area-inset-right)) constant(safe-area-inset-bottom) calc(24px + constant(safe-area-inset-left));
  padding: 0 calc(24px + env(safe-area-inset-right)) env(safe-area-inset-bottom) calc(24px + env(safe-area-inset-left));
}

.weui-half-screen-dialog__hd {
  font-size: 8px;
  height: 8em;
  display: -webkit-box;
  display: -webkit-flex;
  display: flex;
  -webkit-box-align: center;
  -webkit-align-items: center;
  align-items: center;
}

.weui-half-screen-dialog__hd .weui-icon-btn {
  position: absolute;
  top: 50%;
  -webkit-transform: translateY(-50%);
  transform: translateY(-50%);
}

.weui-half-screen-dialog__hd__side {
  position: relative;
  left: -8px;
}

.weui-half-screen-dialog__hd__main {
  -webkit-box-flex: 1;
  -webkit-flex: 1;
  flex: 1;
}

.weui-half-screen-dialog__title {
  display: block;
  color: rgba(0, 0, 0, .9);
  font-weight: 700;
  font-size: 15px;
}

.weui-half-screen-dialog__hd__side + .weui-half-screen-dialog__hd__main {
  text-align: center;
  padding: 0 40px;
}

.weui-half-screen-dialog__hd .weui-icon-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}

.weui-icon-close-thin {
  mask-image: url(data:image/svg+xml,%3Csvg%20width%3D%2224%22%20height%3D%2224%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D%22M12.25%2010.693L6.057%204.5%205%205.557l6.193%206.193L5%2017.943%206.057%2019l6.193-6.193L18.443%2019l1.057-1.057-6.193-6.193L19.5%205.557%2018.443%204.5z%22%20fill-rule%3D%22evenodd%22%2F%3E%3C%2Fsvg%3E);
  display: inline-block;
  vertical-align: middle;
  width: 24px;
  height: 24px;
  mask-position: 50% 50%;
  mask-repeat: no-repeat;
  mask-size: 100%;
  background-color: currentColor;
}

.weui-half-screen-dialog__bd {
  padding-top: 4px;
  padding-bottom: 40px;
  font-size: 14px;
  overflow-y: auto;
}

/* 蒙版 */
.mask {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: black;
  opacity: 0.5;
}

.tn-tag-content__item {
  display: inline-block;
  line-height: 45rpx;
  padding: 5rpx 25rpx;
  margin: 10rpx 5rpx 2rpx 0rpx;
  transition: all 0.3s;

  &:active {
    transform: scale(0.95);
  }
}
</style>