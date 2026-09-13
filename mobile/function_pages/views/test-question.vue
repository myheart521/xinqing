<script lang="ts" setup>
import {ref, computed, onMounted, watch} from 'vue'
import navbar from "@/components/navbar.vue";
import { getAll1, submit1 } from '@/service/api/testController';
import {onLoad} from '@dcloudio/uni-app'

interface Question {
  questionNumber: number
  question: string
  options: string[]
  val: string[]
}

const questions = ref<Question[]>([])
const currentIndex = ref(0)
const answers = ref<number[]>([])
const showAnswerSheet = ref(false)
const progress = ref(0)
const isLoading = ref(true)
const testId = ref('')

// 获取测试题数据
const getQuestions = async (id: string) => {
  isLoading.value = true
  testId.value = id
  
  try {
    // 调用API获取题目
    const res = await getAll1({
      id: id
    })
    
    if (res && res.code === 1 && res.data) {
      questions.value = res.data
      // console.log("questions loaded:", questions.value)
      
      // 初始化答案数组，全部设为-1表示未选择
      answers.value = new Array(questions.value.length).fill(-1)
    } else {
      // 如果接口返回错误，使用模拟数据
      useSimulatedData()
      uni.showToast({
        title: '获取题目失败，使用模拟数据',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error("获取题目失败:", error)
    // 如果接口出错，使用模拟数据
    useSimulatedData()
    uni.showToast({
      title: '获取题目失败，使用模拟数据',
      icon: 'none'
    })
  } finally {
    isLoading.value = false
  }
}

// 模拟数据（开发阶段使用）
const useSimulatedData = () => {
  questions.value = [
    {
      questionNumber: 1,
      question: "我感到情绪沮丧，郁闷。",
      options: [
        "A.从无或偶尔",
        "B.有时",
        "C.经常",
        "D.总是如此"
      ],
      val: [
        "1",
        "2",
        "3",
        "4"
      ]
    },
    {
      questionNumber: 2,
      question: "我感到早晨心情最好。",
      options: [
        "A.从无或偶尔",
        "B.有时",
        "C.经常",
        "D.总是如此"
      ],
      val: [
        "1",
        "2",
        "3",
        "4"
      ]
    },
    {
      questionNumber: 3,
      question: "我常常哭泣或想哭。",
      options: [
        "A.从无或偶尔",
        "B.有时",
        "C.经常",
        "D.总是如此"
      ],
      val: [
        "1",
        "2",
        "3",
        "4"
      ]
    }
  ]
  
  // 初始化答案数组
  answers.value = new Array(questions.value.length).fill(-1)
}

// 计算是否为最后一题
const isLastQuestion = computed(() => {
  return currentIndex.value === questions.value.length - 1
})

// 计算是否为第一题
const isFirstQuestion = computed(() => {
  return currentIndex.value === 0
})

// 计算完成度
const completionRate = computed(() => {
  if (questions.value.length === 0) return 0
  const answered = answers.value.filter(a => a !== -1).length
  return Math.floor((answered / questions.value.length) * 100)
})

//根据answer计算完成了几道题
const hasSelect = computed(() => {
  const answered = answers.value.filter(a => a !== -1).length
  return Math.floor(answered)
})

// 选择答案
const selectOption = (index: number) => {
  if (!questions.value[currentIndex.value]) return
  
  // 更新当前题目的答案
  answers.value[currentIndex.value] = index
  progress.value = completionRate.value
  
  // console.log("选择了答案:", index, "当前题目:", currentIndex.value)
  // console.log("所有答案:", answers.value)

  // 选择后自动跳转到下一题
  setTimeout(() => {
    if (!isLastQuestion.value) {
      currentIndex.value++
    }
  }, 300)
}

// 提交答案
const submitAnswers = async () => {
  // 检查是否有未完成的题目
  if (answers.value.includes(-1)) {
    uni.showToast({
      title: '请完成所有题目',
      icon: 'none'
    })
    return
  }

  // 构建提交数据
  const answerValues = answers.value.map((index, i) => {
    if (index === -1) return null // 未选择的题目
    return questions.value[i].val[index]
  })
  
  // console.log("提交答案值:", answerValues)

  // 将答案数组转换为 JSON 字符串并进行 URL 编码
  const encodedAnswers = encodeURIComponent(JSON.stringify(answerValues))
  
  // 跳转到结果页面，并通过 URL 参数传递 moduleId 和 answers
  uni.redirectTo({
    url: `/function_pages/views/test-result?moduleId=${testId.value}&answers=${encodedAnswers}`
  })
}

// 跳转到指定题目
const goToQuestion = (index: number) => {
  currentIndex.value = index
  showAnswerSheet.value = false
}

// 上一题
const prevQuestion = () => {
  if (!isFirstQuestion.value) {
    currentIndex.value--
  }
}

// 下一题
const nextQuestion = () => {
  if (!isLastQuestion.value) {
    currentIndex.value++
  }
}

// 监听currentIndex变化，确保在范围内
watch(currentIndex, (newVal) => {
  if (questions.value.length === 0) return
  
  if (newVal < 0) {
    currentIndex.value = 0
  } else if (newVal >= questions.value.length) {
    currentIndex.value = questions.value.length - 1
  }
})
onLoad((options)=>{
  // @ts-ignore
  const id = options?.id || '1' // 默认使用ID 1

  // console.log("开始获取题目, id:", id)
  getQuestions(id)
})

// onMounted(() => {
//   const pages = getCurrentPages()
//   const currentPage = pages[pages.length - 1]
//   // @ts-ignore
//   const id = currentPage?.options?.id || '1' // 默认使用ID 1
//
//   // console.log("开始获取题目, id:", id)
//   getQuestions(id)
// })
</script>

<template>
  <view class="container">
    <!-- 导航栏 -->
    <navbar title="心理测试"></navbar>
    
    <!-- 加载中 -->
    <view class="loading-container" v-if="isLoading">
      <tn-loading type="flower" size="80"></tn-loading>
      <text class="loading-text">加载题目中...</text>
    </view>
    
    <template v-else>
      <!-- 进度显示 -->
      <view class="progress-container tn-flex tn-flex-center-between">
        <view class="progress-info tn-flex tn-flex-start-center">
          <tn-circle-progress
              :percent="progress"
              height="10"
              active-color="#19be6b"
              inactive-color="#f5f5f5"
              :show-percent="false"
              :size="60"
          >
            <view class="progress-text">
              {{ hasSelect }}/{{ questions.length }}
            </view>
          </tn-circle-progress>
          <text class="progress-label tn-text-bold tn-ml-sm">答题进度</text>
        </view>
        
        <tn-button 
          @tap="showAnswerSheet = true" 
          bg-color="#5677fc" 
          padding="12rpx 30rpx" 
          font-size="26rpx"
          radius="30rpx"
          :shadow="true"
        >
          <view class="tn-flex tn-flex-start-center">
            <tn-icon name="list" color="#fff" size="32rpx"></tn-icon>
            <text class="tn-ml-xs tn-white_text">题号</text>
          </view>
        </tn-button>
      </view>
      
      <!-- 题目内容 -->
      <view class="question-main-container">
        <view class="question-container" v-if="questions[currentIndex]">
          <view class="question-header tn-flex tn-flex-center-between">
            <view class="question-number tn-flex tn-flex-center-center">
              <text>{{ questions[currentIndex].questionNumber }}</text>
            </view>
            <view class="question-count tn-gray_text">
              {{ currentIndex + 1 }}/{{ questions.length }}
            </view>
          </view>
          
          <view class="question-title">
            {{ questions[currentIndex].question }}
          </view>
          
          <view class="options-list">
            <view
                v-for="(option, index) in questions[currentIndex].options"
                :key="index"
                class="option-item"
                :class="{ 'option-selected': answers[currentIndex] === index }"
                @click="selectOption(index)"
            >
              <view class="option-content tn-flex tn-flex-start-center">
                <view class="radio-circle">
                  <view class="radio-inner" v-if="answers[currentIndex] === index"></view>
                </view>
                <text class="option-text">{{ option }}</text>
              </view>
            </view>
          </view>
          
          <view class="question-actions tn-flex tn-flex-start-between">
            <tn-button 
              @tap="prevQuestion" 
              bg-color="#f8f8f8" 
              color="#333"
              padding="12rpx 40rpx" 
              font-size="28rpx"
              radius="30rpx"
              :disabled="isFirstQuestion"
            >
              <view class="tn-flex tn-flex-start-center">
                <tn-icon name="left" size="32rpx"></tn-icon>
                <text class="tn-ml-xs">上一题</text>
              </view>
            </tn-button>
            
            <tn-button 
              v-if="!isLastQuestion" 
              @tap="nextQuestion" 
              bg-color="#5677fc" 
              padding="12rpx 40rpx" 
              font-size="28rpx"
              radius="30rpx"
              :shadow="true"
            >
              <view class="tn-flex tn-flex-start-center">
                <text class="tn-mr-xs">下一题</text>
                <tn-icon name="right" color="#fff" size="32rpx"></tn-icon>
              </view>
            </tn-button>
            
            <tn-button 
              v-else 
              @tap="submitAnswers" 
              bg-color="#19be6b" 
              padding="12rpx 40rpx" 
              font-size="28rpx"
              radius="30rpx"
              :shadow="true"
            >
              <view class="tn-flex tn-flex-start-center">
                <tn-icon name="check-circle" color="#fff" size="32rpx"></tn-icon>
                <text class="tn-ml-xs tn-white_text">提交</text>
              </view>
            </tn-button>
          </view>
        </view>
      </view>

      <!-- 题号弹窗 -->
      <tn-popup v-model="showAnswerSheet" mode="center" :mask-close-able="true" radius="24rpx">
        <view class="answer-sheet">
          <view class="sheet-header tn-flex tn-flex-center-between">
            <text class="sheet-title tn-text-bold">答题卡</text>
            <tn-icon name="close" size="40rpx" @click="showAnswerSheet = false"></tn-icon>
          </view>
          
          <!-- 添加滚动视图包裹题号内容 -->
          <scroll-view scroll-y class="sheet-scroll-container">
            <view class="sheet-content">
              <view 
                v-for="(_, index) in questions" 
                :key="index"
                class="question-dot"
                :class="{ 
                  'answered': answers[index] !== -1,
                  'current': currentIndex === index
                }"
                @click="goToQuestion(index)"
              >
                {{ questions[index].questionNumber }}
              </view>
            </view>
          </scroll-view>
          
          <view class="sheet-footer tn-flex tn-flex-start-center tn-mt-lg">
            <view class="legend-item tn-flex tn-flex-start-center">
              <view class="legend-dot answered"></view>
              <text class="legend-text tn-gray_text">已答</text>
            </view>
            <view class="legend-item tn-flex tn-flex-start-center tn-ml-lg">
              <view class="legend-dot"></view>
              <text class="legend-text tn-gray_text">未答</text>
            </view>
            <view class="legend-item tn-flex tn-flex-start-center tn-ml-lg">
              <view class="legend-dot current"></view>
              <text class="legend-text tn-gray_text">当前题</text>
            </view>
          </view>
        </view>
      </tn-popup>
    </template>
  </view>
</template>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f8f9fc;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
  
  .loading-text {
    margin-top: 30rpx;
    font-size: 28rpx;
    color: #666;
  }
}

.progress-container {
  padding: 30rpx;
  background-color: #ffffff;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .progress-info {
    .progress-text {
      font-size: 24rpx;
      color: #19be6b;
      font-weight: bold;
    }
    
    .progress-label {
      font-size: 28rpx;
      color: #333;
    }
  }
}

.question-main-container {
  padding: 30rpx;
}

.question-container {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx 30rpx;
  box-shadow: 0 2rpx 20rpx rgba(0, 0, 0, 0.05);
  
  .question-header {
    margin-bottom: 30rpx;
    
    .question-number {
      width: 70rpx;
      height: 70rpx;
      background: linear-gradient(135deg, #5677fc, #5c8dff);
      border-radius: 50%;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
    }
    
    .question-count {
      font-size: 28rpx;
    }
  }

  .question-title {
    font-size: 34rpx;
    font-weight: bold;
    margin-bottom: 50rpx;
    line-height: 1.5;
    color: #333;
  }

  .options-list {
    .option-item {
      margin-bottom: 30rpx;
      
      .option-content {
        padding: 30rpx;
        background-color: #f8f9fc;
        border-radius: 12rpx;
        transition: all 0.3s;
        
        &:active {
          transform: scale(0.98);
        }
      }
      
      &.option-selected .option-content {
        background-color: rgba(86, 119, 252, 0.1);
        border: 2rpx solid #5677fc;
      }

      .radio-circle {
        width: 40rpx;
        height: 40rpx;
        border-radius: 50%;
        border: 2rpx solid #dcdfe6;
        margin-right: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        .radio-inner {
          width: 24rpx;
          height: 24rpx;
          border-radius: 50%;
          background-color: #5677fc;
        }
      }

      &.option-selected .radio-circle {
        border-color: #5677fc;
      }

      .option-text {
        font-size: 30rpx;
        color: #333;
        flex: 1;
      }
    }
  }
  
  .question-actions {
    margin-top: 60rpx;
  }
}

.answer-sheet {
  width: 600rpx;
  padding: 40rpx;
  
  .sheet-header {
    margin-bottom: 20rpx;
    
    .sheet-title {
      font-size: 34rpx;
      color: #333;
    }
  }

  /* 添加滚动容器样式 */
  .sheet-scroll-container {
    max-height: 500rpx; /* 设置最大高度，超出时显示滚动条 */
    margin-bottom: 20rpx;
  }

  .sheet-content {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    padding: 10rpx 0;

    .question-dot {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      margin: 15rpx;
      font-size: 28rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f5f5f5;
      color: #666;
      transition: all 0.3s;
      
      &:active {
        transform: scale(0.9);
      }

      &.answered {
        background-color: #19be6b;
        color: #fff;
      }
      
      &.current {
        background-color: #5677fc;
        color: #fff;
        transform: scale(1.1);
        box-shadow: 0 2rpx 10rpx rgba(86, 119, 252, 0.3);
      }
    }
  }
  
  .sheet-footer {
    .legend-item {
      .legend-dot {
        width: 30rpx;
        height: 30rpx;
        border-radius: 50%;
        background-color: #f5f5f5;
        margin-right: 10rpx;
        
        &.answered {
          background-color: #19be6b;
        }
        
        &.current {
          background-color: #5677fc;
        }
      }
      
      .legend-text {
        font-size: 24rpx;
      }
    }
  }
}
</style> 