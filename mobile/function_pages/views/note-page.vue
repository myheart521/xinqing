<template>
  <view class="all">
    <navbar title="随便记"></navbar>
    <!--头部区域-->
    <view class="head">
      <!-- 新增文件夹模态框 -->
      <tn-modal
          ref="addFolderModal"
          showCancel
          :cancelText="'取消'"
          :confirmText="'确定'"
          :mask="true"
          @confirm="addFolderConfirm"
          @cancel="() => (addFolderShow = false)"
      >
        <template #default>
          <tn-input
              custom-class="tn-round"
              v-model="newFolder"
              placeholder="新增文件夹"
          />
        </template>
      </tn-modal>
      <!-- 新增代办模态框 -->
      <tn-modal
          ref="addAgencyModal"
          :cancelText="'取消'"
          :confirmText="'完成'"
          :mask="true"
      >
        <template #default>
            <textarea
                v-model="agencyInfo"
                placeholder="添加代办"
                border="none"
                :maxlength="100"
            />
        </template>
      </tn-modal>
      <!--展示日记删除菜单-->
      <tn-action-sheet ref="actionSheetRef"></tn-action-sheet>
      <!--头部的文字-->
      <view class="head-top tn-flex justify-between tn-text-xl tn-mt-sm" :style="{ paddingTop: paddingTop }">
        <view class="head-left tn-flex justify-between tn-ml-sm">
          <view :class="tabNum == 0 ? 'bold-style tn-text-bold' : ''" @tap="tabClick(0)">随便记</view>
          <view class="tn-ml-xs" :class="tabNum == 1 ? 'bold-style tn-text-bold' : ''" @tap="tabClick(1)">代办</view>
        </view>
        <view class="tn-flex tn-flex-stretch-between tn-mr-sm head-right">
          <tn-icon name="folder-add" size="50rpx" @tap="newFolderClick">
          </tn-icon>
        </view>
      </view>
      <!--搜索框-->
      <view class="tn-flex-center-center tn-mt-sm">
        <view style="width: 90%">
          <tn-search-box
              v-model="keyWord"
              shape="round"
              size="sm"
              :search-button="false"
              :placeholder="tabNum == 0 ? '搜索笔记' : '搜索代办'"
          >
          </tn-search-box>
        </view>
      </view>
    </view>
    <!--内容区域-->
    <swiper class="content swiper-box" :current="swiperCurrent" :style="{ paddingTop: paddingTop }"
            @animationfinish="animationfinish">
      <!--日记部分-->
      <swiper-item>
        <scroll-view scroll-y style="height: 100%;width: 100%;">
          <view class="tn-pb tn-pl tn-pr">
            <view v-if="noteList.length > 0">
              <!-- v-for使用/不使用key -->
              <!-- 不加key渲染时候会依次替换渲染，加了key会直接将其放在指定位置，加key提升效率。 -->
              <view class="tn-white_bg radius tn-p-sm noteList" v-for="(item, index) in noteListFilter"
                    :key="index">
                <view class="" @tap="noteListClick(item)">
                  <view class="tn-text-bold tn-text-lg">{{ item.noteName || '' }}</view>
                  <view class="tn-mt-xs" v-html="getEllipsis(item.noteContent, 30)"></view>
                  <view class="tn-flex justify-between tn-mt-xs">
                    <view class="">{{ item.noteTime }}</view>
                    <view class="" @tap.stop="menuClick(index)">
                      <tn-icon name="more-horizontal" width="20px" height="20px"></tn-icon>
                    </view>
                  </view>
                </view>
              </view>
            </view>
            <view class="tn-white_bg tn-radius tn-p-sm noteList" v-else>
              <view class="tn-text-bold tn-text-lg">你好，欢迎使用心晴的随便记</view>
              <view class="tn-mt-xs">
                {{
                  getEllipsis('随便记是一款高效简洁的内容记录工具，能在你灵感迸发时记录内容：轻巧的文字拥有无限力量。', 30)
                }}
              </view>
              <view class="tn-flex tn-flex-center-between tn-mt-xs">
                <view class="">05-20 13:14</view>
                <view>
                  <tn-icon name="more-horizontal" width="20px"
                           height="20px"></tn-icon>
                </view>
              </view>
            </view>
          </view>
        </scroll-view>
      </swiper-item>
      <!--代办部分-->
      <swiper-item>
        <scroll-view scroll-y style="height: 100%;width: 100%;">
          <view v-if="agencyListFilter.length > 0">
            <view v-for="(item, index) in agencyListFilter" :key="index">
              <tn-checkbox-group v-model="selectAgency">
                <view class="tn-flex-column tn-flex" style="width: 100%">
                  <view class="tn-text-bold tn-text-xl tn-pl tn-mt-sm">
                    {{ contrastDate(item.agencyTime) }}
                  </view>
                  <view class="tn-flex tn-flex-column">
                    <view class="tn-white_bg tn-mt-xs tn-mb-xs  tn-ml tn-mr tn-radius tn-p"
                          v-for="(itemList, indexList) in item.list" :key="indexList"
                          @click="cancleAgency(index,indexList)">
                      <tn-checkbox :active-value="itemList.checked" :label="index+indexList">
                        {{ itemList.agencyName }}
                      </tn-checkbox>
                    </view>
                  </view>
                </view>
              </tn-checkbox-group>
            </view>
          </view>
          <view class="tn-flex tn-flex-center-center" style="height: 100%;width: 100%;" v-else>
            <image src="/static/images/null.png" style="width: 250px;height: 250px"></image>
          </view>
        </scroll-view>
      </swiper-item>
    </swiper>
    <!-- 添加内容-固定定位 -->
    <view class="addIcon tn-round" style="background-color:#3b968c;width: 100rpx;height: 100rpx" @tap="add">
      <tn-icon name="add" size="100rpx" color="#fff"></tn-icon>
    </view>
  </view>
</template>
<script setup>
import navbar from '@/components/navbar.vue'
import {getEllipsis, contrastDate} from "@/utils/formate";
import {ref, computed, watch} from 'vue'
import {onLoad, onShow} from '@dcloudio/uni-app'

const addFolderModal = ref(null)
const addAgencyModal = ref(null)
const actionSheetRef = ref()

//swiper
const swiperCurrent = ref(0)

//导航栏选项
const tabNum = ref(0)

//文件夹内容
const newFolder = ref('文件夹')

//代办内容
const agencyInfo = ref('')

//搜索框内容
const keyWord = ref()

//点击删除的日记的序号
const menuClickIndex = ref()

const agencyList = ref()
//选中的标签
const selectAgency = computed(() => {
  const selectedIndices = [];
  rawDataAgencyList.value.forEach((dateItem, index) => {
    dateItem.list.forEach((agencyItem, indexList) => {
      if (agencyItem.checked) {
        selectedIndices.push(index + indexList);
      }
    });
  });
  return selectedIndices;
});
//取消选中,或者选中
const cancleAgency = (index, indexList) => {
  rawDataAgencyList.value[index].list[indexList].checked = !rawDataAgencyList.value[index].list[indexList].checked
  uni.setStorageSync('rawDataAgencyList', JSON.stringify(rawDataAgencyList.value))
}
//代办数据
const rawDataAgencyList = ref([
  // {
  //   agencyTime: '2025-02-07',
  //   list: [
  //     {agencyName: '代办1', checked: true},
  //     {agencyName: '代办2', checked: false}
  //   ]
  // }
])

//日记数据
const noteList = ref([])

//添加代办
const addAgencyConfirm = () => {
  if (!agencyInfo.value) {
    uni.showToast({
      icon: 'none',
      title: '添加失败，代办不能为空！'
    })
    return
  }
  const date = new Date().toLocaleDateString()
  // 判断是不是同一天加的数据
  if (rawDataAgencyList.value.length > 0 && rawDataAgencyList.value[0].agencyTime === date) {
    rawDataAgencyList[0].list.push({
      agencyName: agencyInfo.value,
      checked: false
    })
    uni.setStorageSync('rawDataAgencyList', JSON.stringify(rawDataAgencyList.value))
  } else {
    rawDataAgencyList.value.unshift({
      agencyTime: date,
      list: [{
        agencyName: agencyInfo.value,
        checked: false
      }]
    })
    uni.setStorageSync('rawDataAgencyList', JSON.stringify(rawDataAgencyList.value))
  }
}


// 新增文件夹
const newFolderClick = () => {
  addFolderModal.value.showModal({
    title: '新增文件夹',
    confirmText: '确定',
    cancelText: '取消',
    showCancel: true,
    showConfirm: true,
    mask: true,
    confirm: () => {
      // 确认以后关闭弹窗 + 清空默认展示条件
      newFolder.value = '文件夹'
    }
  })
}

//swiper切换触发，索引更新
const animationfinish = (event) => {
  const current = event.detail.current;
  swiperCurrent.value = current;
  tabNum.value = current;
}

//日记过滤操作
const noteListFilter = computed(() => {
  //filter是过滤函数去除了不包含关键字的情况
  return noteList.value.filter(p => {
    //返回过滤后的数组
    if (p.noteContent) {
      return p.noteContent.indexOf(keyWord.value) !== -1 || p.noteName.indexOf(keyWord.value) !== -1;
    }
    return [];
  });
})

//代办过滤操作
const agencyListFilter = computed(() => {
  let list = [];
  if (keyWord.value !== '') {
    list = rawDataAgencyList.value
        .map(p => {
          let o = {
            agencyTime: p.agencyTime,
            list: []
          };
          o.list = p.list.filter(c => {
            return c.agencyName.indexOf(keyWord.value) !== -1;
          });
          return o;
        })
        .filter(p => p.list.length > 0);
  } else {
    list = rawDataAgencyList.value;
  }
  return list;
})

//打开日记删除操作
const openActionSheet = () => {
  actionSheetRef.value?.show({
    actions: [
      {text: '删除', value: '0'},
    ],
    cancelText: '取消',
    select: (index, value) => {
      if (index == 0) {
        console.log(menuClickIndex.value)
        noteList.value.splice(menuClickIndex.value, 1);
        uni.setStorageSync('noteList', JSON.stringify(noteList.value))
        return true
      }
    }
  })
}

//点击删除日记
const menuClick = (index) => {
  menuClickIndex.value = index;
  openActionSheet()
}

// 点击单条日记进行跳转
const noteListClick = (e) => {
  let queryParams = encodeURIComponent(JSON.stringify(e));
  uni.navigateTo({
    url: `/function_pages/views/add-note?params=${queryParams}`
  });
}

const tabClick = (index) => {
  tabNum.value = index;
  swiperCurrent.value = index;
  keyWord.value = '';
  if (tabNum.value == 1) {
    agencyList.value = rawDataAgencyList.value;
  }
}

const add = () => {
  // 0 随便记，1 代办
  if (tabNum.value == 0) {
    // 出现随便记的内容，md文件内容
    uni.navigateTo({
      url: `/function_pages/views/add-note`
    });
  } else if (tabNum.value == 1) {
    // 打开窗口前先清空内容
    agencyInfo.value = '';
    // 出现代办的内容，弹窗填写内容
    addAgencyModal.value.showModal({
      title: '新增代办',
      confirm: () => {
        addAgencyConfirm()
        return true
      },
      showCancel: true
    })
  }
}
onShow(() => {
  tabClick(0)
  const strNoteList = uni.getStorageSync('noteList');
  const strRawDataAgencyList = uni.getStorageSync('rawDataAgencyList');
  if (strNoteList) {
    noteList.value = JSON.parse(strNoteList);
    console.log("show笔记列表", noteList.value);
  } else {
    console.log("show笔记列表为空");
  }
  if (strRawDataAgencyList) {
    rawDataAgencyList.value = JSON.parse(strRawDataAgencyList);
    console.log("show代办列表", rawDataAgencyList.value);
  } else {
    console.log("show代办列表为空");
  }
});

onLoad(() => {
  const strNoteList = uni.getStorageSync('noteList');
  const strRawDataAgencyList = uni.getStorageSync('rawDataAgencyList');
  if (strNoteList) {
    noteList.value = JSON.parse(strNoteList); // 直接更新 noteList 响应式变量
    console.log("笔记列表", noteList.value);
  } else {
    console.log("笔记列表为空");
  }
  if (strRawDataAgencyList) {
    rawDataAgencyList.value = JSON.parse(strRawDataAgencyList); // 同理更新 rawDataAgencyList
    console.log("代办列表", rawDataAgencyList.value);
  } else {
    console.log("代办列表为空");
  }
});

//监听本地存储数据
watch(()=> uni.getStorageSync('noteList'),()=>{
  const strNoteList = uni.getStorageSync('noteList');
  const strRawDataAgencyList = uni.getStorageSync('rawDataAgencyList');
  if (strNoteList) {
    noteList.value = JSON.parse(strNoteList);
    console.log("show笔记列表", noteList.value);
  } else {
    console.log("show笔记列表为空");
  }
  if (strRawDataAgencyList) {
    rawDataAgencyList.value = JSON.parse(strRawDataAgencyList);
    console.log("show代办列表", rawDataAgencyList.value);
  } else {
    console.log("show代办列表为空");
  }
})
</script>
<style scoped lang="scss">
page {
  height: 100%;
}

.all {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background-color: #f8f8f8;
  // overflow: hidden;

  .head {
    height: 200rpx;

    .head-top {
      .head-left {
        width: 200rpx;
      }

      .head-right {
        width: 140rpx;
      }

      .bold-style {
        position: relative;
      }

      .bold-style::before {
        content: '';
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 14rpx;
        background-color: #2a9d8f;
        border-radius: 10rpx;
        // z-index: 2;
      }
    }
  }

  .content {
    flex: 1;

    .noteList + .noteList {
      margin-top: 20rpx;
    }
  }

  .addIcon {
    position: fixed;
    bottom: 120rpx;
    right: 60rpx;
  }
}

</style>