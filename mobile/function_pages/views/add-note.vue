<template>
  <view class="container bg-white">
    <tn-navbar>
      <template #default>
        <view class="u-nav-slot tn-flex" style="margin-left: 20px">
          <view class="iconfont icon-undo" v-show="currentTextNum != 0" @tap="undo"></view>
          <view class="iconfont icon-redo tn-ml" v-show="currentTextNum != 0" @tap="redo"></view>
          <view class="iconfont icon-duigoux tn-ml" v-show="currentTextNum != 0 || noteName != ''"
                @tap="finishWriting"></view>
        </view>
      </template>
    </tn-navbar>
    <!-- 头部内容 -->
    <view class="tn-flex tn-flex-column justify-center header tn-pl tn-pr">
      <view class="tn-text-bold tn-mb-xs">
        <tn-input placeholder="标题" :border="false" v-model="noteName" maxlength="100"></tn-input>
      </view>
      <view class="tn-flex justify-between">
        <view class="tn-flex tn-text-xs" style="color: #CCCCCC;">
          <view class="tn-mr-xs">{{ noteTime || toLocaleString('time') }}</view>
          <tn-icon name="title" size="30rpx"></tn-icon>
          <view class="tn-ml-xs">{{ currentTextNum }}字</view>
        </view>
        <view class="iconfont icon-date" @tap="insertDate"></view>
      </view>
    </view>
    <view class="tn-p" style="flex: 1;overflow: hidden;margin-bottom: 50px">
      <editor
          id="editor"
          placeholder="开始输入..."
          showImgSize
          showImgToolbar
          showImgResize
          @statuschange="onStatusChange"
          :read-only="readOnly"
          @ready="onEditorReady"
          @input="onEditorInput"
      ></editor>
    </view>
    <view class="">
      <view v-show="firstKey == 'fontColor'">
        <view class="tn-flex tn-flex-column tn-white_bg tn-pl tn-pr tn-pt tn-radius fontColor">
          <view v-for="(item, index) in text1" class="tn-mb tn-flex tn-flex-column" :key="index">
            <text class="tn-pb tn-text-lg tn-text-bold">{{ item.title }}</text>
            <view class="tn-flex items-center">
              <view
                  @tap="setTextStyle(it)"
                  v-for="it in item.list"
                  :style="'background: ' + it.color"
                  :key="it.id"
                  class="tn-mr tn-flex items-center justify-center color_item"
              >
                <tn-icon v-if="!it.icon && formats[it.key] == it.val" name="success" width="30rpx"
                         height="30rpx"></tn-icon>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view v-show="firstKey == 'fontSize'">
        <view class="tn-flex tn-flex-column tn-white_bg tn-p tn-radius fontSize">
          <text class="tn-pb tn-text-lg tn-text-bold">{{ text2.title }}</text>
          <scroll-view scroll-x style="width: 648rpx;" class="">
            <view class="tn-flex items-center justify-start tn-pr">
              <view
                  @tap="setTextStyle(item)"
                  v-for="item in text2.list"
                  :key="item.id"
                  :class="[formats[item.key] == item.val ? 'bg-admin-secoed' : 'bg-page']"
                  class="size_item tn-pl-sm tn-pr-sm tn-mr tn-flex items-center justify-center"
              >
                <text :class="[formats[item.key] == item.val ? 'text-p-admin' : '']" class="font-md-l">{{
                    item.text
                  }}
                </text>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>
    </view>

    <view class="tn-flex items-center justify-around footer">
      <!-- 字体加粗 -->
      <!--      <view :class="formats.bold ? 'ql-active' : ''" class="iconfont icon-zitijiacu tn-mr" data-name="bold"></view>-->
      <!-- 字体斜体 -->
      <!--      <view :class="formats.italic ? 'ql-active' : ''" class="iconfont icon-zitixieti tn-mr" data-name="italic"></view>-->
      <!-- 字体加下划线 -->
      <!--      <view :class="formats.underline ? 'ql-active' : ''" class="iconfont icon-zitixiahuaxian tn-mr"-->
      <!--            data-name="underline"></view>-->
      <!-- 字体颜色 -->
      <view class="">
        <image
            :src="firstKey == 'fontColor' ? imag[1] : imag[0]"
            style="width: 32rpx;height: 32rpx"
            @tap="chooseFontSizeOrColor('fontColor')"
        ></image>
      </view>
      <!-- 字体大小 -->
      <view class="tn-ml tn-mr">
        <image
            :src="firstKey == 'fontSize' ? imag[3] : imag[2]"
            style="width: 32rpx;height: 32rpx"
            @tap="chooseFontSizeOrColor('fontSize')"
        ></image>
      </view>
      <!--  代办 -->
      <!--      <view class="iconfont icon&#45;&#45;checklist tn-mr" data-name="list" data-value="check"></view>-->
      <!-- 有序列表 -->
      <!--      <view :class="formats.list === 'ordered' ? 'ql-active' : ''" class="iconfont icon-youxupailie margin-right"-->
      <!--            data-name="list" data-value="ordered"></view>-->
      <!-- 无序列表 -->
      <!--      <view :class="formats.list === 'bullet' ? 'ql-active' : ''" class="iconfont icon-wuxupailie margin-right"-->
      <!--            data-name="list" data-value="bullet"></view>-->
      <!-- 加一条线 -->
      <view class="iconfont icon-fengexian tn-mr" @tap="insertDivider"></view>
      <!-- 加图片 -->
      <view class="iconfont icon-charutupian" @tap="insertImage"></view>
    </view>
  </view>
</template>

<script>
import Navbar from "@/components/navbar.vue";
import moment from 'moment'
import imag1 from '@/function_pages/static/swiper-image/chooseColor.png'
import imag2 from '@/function_pages/static/swiper-image/chooseColor_act.png'
import imag3 from '@/function_pages/static/swiper-image/fontSize.png'
import imag4 from '@/function_pages/static/swiper-image/font_act.png'

export default {
  components: {Navbar},
  data() {
    return {
      imag: [imag1, imag2, imag3, imag4],
      // 跳转页面传递过来的内容
      value: '',
      // 当前文本的内容
      currentTextContent: '',
      currentTextNum: 0,
      // 富文本对象
      editorCtx: {},
      // 选中的顶级选项框
      firstKey: null,
      readOnly: false,
      formats: {},
      noteName: '',
      noteTime: '',
      type: 'add',
      // 选择文字颜色
      text1: [
        {
          title: '文字颜色',
          list: [
            {
              id: 0,
              color: '#333333',
              key: 'color',
              val: '#333333'
            },
            {
              id: 1,
              color: '#666666',
              key: 'color',
              val: '#666666'
            },
            {
              id: 2,
              color: '#999999',
              key: 'color',
              val: '#999999'
            },
            {
              id: 3,
              color: '#ff0000',
              key: 'color',
              val: '#ff0000'
            },
            {
              id: 4,
              color: '#ff5f2d',
              key: 'color',
              val: '#ff5f2d'
            },
            {
              id: 5,
              color: '#f1b000',
              key: 'color',
              val: '#f1b000'
            },
            {
              id: 6,
              color: '#00c878',
              key: 'color',
              val: '#00c878'
            },
            {
              id: 7,
              color: '#09abff',
              key: 'color',
              val: '#09abff'
            },
            {
              id: 8,
              color: '#0963ff',
              key: 'color',
              val: '#0963ff'
            },
            {
              id: 9,
              color: '#6b5af7',
              key: 'color',
              val: '#6b5af7'
            }
          ]
        },
        {
          title: '文字背景颜色',
          list: [
            {
              id: 0,
              color: '#ffcfcf',
              key: 'backgroundColor',
              val: '#ffcfcf'
            },
            {
              id: 1,
              color: '#ffd6c9',
              key: 'backgroundColor',
              val: '#ffd6c9'
            },
            {
              id: 2,
              color: '#fff0c8',
              key: 'backgroundColor',
              val: '#fff0c8'
            },
            {
              id: 3,
              color: '#c2ffe6',
              key: 'backgroundColor',
              val: '#c2ffe6'
            },
            {
              id: 4,
              color: '#bee8ff',
              key: 'backgroundColor',
              val: '#bee8ff'
            },
            {
              id: 5,
              color: '#c5daff',
              key: 'backgroundColor',
              val: '#c5daff'
            },
            {
              id: 6,
              color: '#c9c2ff',
              key: 'backgroundColor',
              val: '#c9c2ff'
            },
            {
              id: 7,
              color: '',
              icon: '/static/icon/empty_fill.png',
              key: 'backgroundColor',
              val: ''
            }
          ]
        }
      ],
      // 选择字号
      text2: {
        title: '字号',
        list: []
      }
    };
  },
  onLoad(option) {
    if (decodeURIComponent(option.params)) {
      let queryParams = JSON.parse(decodeURIComponent(option.params));
      console.log(queryParams)
      // 当跳转带了参数就代表是修改内容哦（点击了具体的笔记）
      if (queryParams.noteContent || queryParams.noteName) {
        this.value = queryParams.noteContent;
        this.noteName = queryParams.noteName;
        this.noteTime = queryParams.noteTime;
        if (queryParams.noteTime) {
          this.type = 'update'
        } else {
          this.type = 'add'
        }
        if (queryParams.noteContent) {
          this.currentTextNum = this.removeHTMLTag(queryParams.noteContent).length;
        }
      }
    }
    // 计算出状态栏 + 导航栏的高度 px
    // #ifndef H5
    // 动态加载网络字体
    uni.loadFontFace({
      family: 'Bitstream Vera Serif Bold',
      source: 'url("http://localhost:8080/Pacifico.ttf")'
    });
    // #endif
  },
  mounted() {
    for (let i = 12; i <= 36; i++) {
      this.text2.list.push({
        id: i - 12,
        text: i,
        size: i + 'px',
        key: 'fontSize',
        val: i + 'px'
      });
    }
    // uni.onKeyboardHeightChange((res) => {
    //   this.keyHei = res.height
    // })
  },
  methods: {
    toLocaleString(type = 'date') {
      return type === 'time'
          ? moment().format('YYYY-MM-DD HH:mm')
          : moment().format('YYYY-MM-DD');
    },
    getPx(value, unit = false) {
      if (test.number(value)) {
        return unit ? `${value}px` : Number(value)
      }
      // 如果带有rpx，先取出其数值部分，再转为px值
      if (/(rpx|upx)$/.test(value)) {
        return unit ? `${uni.upx2px(parseInt(value))}px` : Number(uni.upx2px(parseInt(value)))
      }
      return unit ? `${parseInt(value)}px` : parseInt(value)
    },
    readOnlyChange() {
      this.readOnly = !this.readOnly;
    },
    onEditorReady() {
      uni
          .createSelectorQuery()
          .in(this)
          .select('#editor')
          .context(res => {
            this.editorCtx = res.context;
            let rtTxt = this.value;
            this.setContents(rtTxt); //设置富文本内容
          })
          .exec();
    },
    undo() {
      this.editorCtx.undo();
    },
    redo() {
      this.editorCtx.redo();
    },
    format(e) {
      let {name, value} = e.target.dataset;
      if (!name) return;
      this.editorCtx.format(name, value);
    },
    onStatusChange(e) {
      const formats = e.detail;
      this.formats = formats;
    },
    insertDivider() {
      this.editorCtx.insertDivider({
        success: function () {
          console.log('insert divider success');
        }
      });
    },
    clear() {
      this.editorCtx.clear({
        success: function (res) {
          console.log('clear success');
        }
      });
    },
    removeFormat() {
      this.editorCtx.removeFormat();
    },
    insertDate() {
      const date = new Date();
      const formatDate = `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
      this.editorCtx.insertText({
        text: formatDate
      });
    },
    insertImage() {
      uni.chooseImage({
        count: 1,
        success: res => {
          this.editorCtx.insertImage({
            src: res.tempFilePaths[0],
            alt: '图像',
            success: function () {
              console.log('insert image success');
            }
          });
        }
      });
    },
    leftClick() {
      // 关闭当前页面，跳转到应用内的某个页面。
      // 由于没有传参导致如果点击返回就会清空信息
      uni.$u.route({
        type: 'redirectTo',
        url: 'pages/home/home'
      });
    },
    //设置富文本内容
    setContents(rechtext) {
      this.editorCtx.setContents({
        html: rechtext,
        success: res => {
        }
      });
    },
    // 选择颜色或者字体大小
    chooseFontSizeOrColor(val) {
      // 判断如果点击的是当前的就代表需要关闭当前选中的
      if (this.firstKey == val) {
        this.firstKey = null;
        return;
      }
      // val值 1代表是颜色，2是字体大小
      this.firstKey = val;
    },
    setTextStyle(item) {
      const {key, val} = item;
      this.editorCtx.format(key, val);
    },
    onEditorInput() {
      var that = this;
      that.editorCtx.getContents({
        success: function (data) {
          that.currentTextContent = data.html;
          that.currentTextNum = that.removeHTMLTag(data.html).length;
        }
      });
    },
    removeHTMLTag(str) {
      str = str.replace(/<\/?[^>]*>/g, ''); // 去除HTML tag
      str = str.replace(/[ | ]*\n/g, '\n'); // 去除行尾空白
      str = str.replace(/\n[\s| | ]*\r/g, '\n'); //去除多余空行
      // str = str.replace(/ /gi, ''); // 去掉空格
      const arrEntities = {lt: '<', gt: '>', nbsp: ' ', amp: '&', quot: '"'}; // 转义符换成普通字符
      str = str.replace(/&(lt|gt|nbsp|amp|quot);/gi, function (all, t) {
        return arrEntities[t];
      });
      return str;
    },
    // 点击右上角的完成，即可退出当前页进行保存内容
    finishWriting() {
      var date = this.toLocaleString('time');
      const strNoteList = uni.getStorageSync('noteList')
      if (strNoteList) {
        const noteList = JSON.parse(strNoteList)
        //根据日期判断是否是新增的笔记，还是修改的笔记
        if (this.type === 'add') {
          noteList.unshift({
            noteContent: this.currentTextContent || this.value,
            noteTime: date,
            noteName: this.noteName
          })
          uni.setStorageSync('noteList', JSON.stringify(noteList))
          uni.navigateBack()
          console.log("新增笔记列表", noteList)
        } else {
          noteList.forEach((item, index) => {
            if (item.noteTime === this.noteTime) {
              //移除老的，新增新的
              noteList.splice(index, 1)
              //在数组最前面新增
              noteList.unshift({
                noteContent: this.currentTextContent || this.value,
                noteTime: date,
                noteName: this.noteName
              })
            }
          })
          uni.setStorageSync('noteList', JSON.stringify(noteList))
          uni.navigateBack()
          console.log("更新笔记列表", noteList)
        }
      } else {
        uni.setStorageSync('noteList', JSON.stringify([{
          noteContent: this.currentTextContent || this.value,
          noteTime: date,
          noteName: this.noteName
        }]))
        uni.navigateBack()
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/function_pages/static/editor-icon.css";

.container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header {
  height: 110upx;
}

.footer {
  //padding-top: 200px;
  background-color: white;
  width: 100%;
  padding: 10px;
  height: 80rpx;
  border-top: 1px solid #ededed;
}


#editor {
  height: 100%;
  // min-height: calc(100% - 120upx - 88upx - 80upx);
}

.fontColor {
  width: 678rpx;
  box-shadow: 4rpx 2rpx 12rpx rgba(0, 0, 0, 0.2);
}

.fontSize {
  width: 678rpx;
  box-shadow: 4rpx 2rpx 12rpx rgba(0, 0, 0, 0.2);
}

.bg-admin-secoed {
  background-color: rgba(107, 90, 247, 0.2);
}

.bg-page {
  background-color: #f5f5f5;
}

.text-p-admin {
  color: rgba(107, 90, 247, 1);
}

.font-md-l {
  font-size: 36rpx;
}

.color_item {
  height: 40rpx;
  width: 40rpx;
  border-radius: 10rpx;
}
</style>
