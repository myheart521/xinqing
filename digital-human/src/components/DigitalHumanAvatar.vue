<template>
  <div class="digital-human-avatar" v-if="hasConfig">
    <div class="avatar-container">
      <div class="avatar-wrapper" id="digitalHumanWrapper" ref="avatarWrapper"></div>
      <!--      <h1>识别结果：{{ resultText }}</h1>-->
      <!-- 新增输入区域 -->
      <div class="input-area">
        <!-- 快捷提示区域 -->
        <div class="quick-tips">
          <div class="tips-header" @click="toggleTips">
            <span>💡 快捷提示</span>
            <span class="toggle-icon" :class="{ active: showTips }">▼</span>
          </div>
          <div class="tips-content" v-show="showTips">
            <div class="tips-row">
              <button class="tip-btn" @click="insertTip('我想听一些音乐')">🎵 推荐音乐</button>
              <button class="tip-btn" @click="insertTip('推荐一些博客文章')">📖 推荐博客</button>
              <button class="tip-btn" @click="insertTip('有什么活动可以参加')">🎉 推荐活动</button>
            </div>
            <div class="tips-row">
              <button class="tip-btn" @click="insertTip('我想学习一些知识')">🧠 科普文章</button>
              <button class="tip-btn" @click="insertTip('推荐一些健康饮食')">🥗 饮食建议</button>
              <button class="tip-btn" @click="insertTip('推荐一些运动视频')">🏃 运动视频</button>
            </div>
            <div class="tips-row">
              <button class="tip-btn" @click="insertTip('我想做一些心理测试')">📝 心理测试</button>
              <button class="tip-btn emergency" @click="insertTip('我感觉很难受，有些消极的想法')">🆘 心理求助</button>
            </div>
          </div>
        </div>
        
        <input
          type="text"
          v-model="messageText"
          placeholder="输入或语音输入..."
          @keyup.enter="handleSendMessage"
        />
        <div class="voice-controls">
          <button @click="toggleRecording">
            {{ isRecording ? '停止录音' : '语音输入' }}
          </button>
          <button @click="handleSendMessage(messageText)">发送</button>
        </div>
      </div>
      <!--      <div class="opacity-control">-->
      <!--        <span>透明度</span>-->
      <!--        <input type="range" id="opacityRange" min="0" max="1" step="0.1" value="1">-->
      <!--      </div>-->
    </div>
  </div>
</template>
<script>
// 导入SDK和store
import AvatarPlatform, { PlayerEvents } from '@/utils/vm-sdk/avatar-sdk-web_3.1.1.1011/index.js'
import { useDigitalHumanStore } from '@/stores/digitalHuman'
import { useXfAsr } from '@/utils/useXfAsr.js'
import { nextTick, watch, ref, onBeforeUnmount, defineExpose } from 'vue'
import { ElMessage } from 'element-plus'
import { generateMemoryId, getChinaTime } from '@/utils/timeUtils.js'
import { detectUrl } from '@/utils/chatUtils.js'
import {
  sendAdminAIAgentMessage,
  sendAdminMCPAIAgentMessage,
  sendAIChatMessage, sendStudentAIAgentMessage
} from '@/api/ai/chat.js'
import { detectEmotion, getComfort, getRandomStart } from '@/utils/digital-human.js'
import { getSmartActions, getActionDelay } from '@/utils/digitalHumanActions.js'
import { filterForDigitalHuman, checkTextForDigitalHuman } from '@/utils/textFilter.js'
// 全局SDK实例，与配置页面保持一致
let avatarPlatform2 = null
export default {
  setup() {
    const { startRecording, stopRecording, recordText, resultText } = useXfAsr()
    const messages = ref([])
    const memoryId = ref([])
    const startTime = ref([])
    const currentReader = ref([])
    const messageText = ref('') // 绑定输入框内容
    const isRecording = ref()
    const showTips = ref(false) // 控制快捷提示显示

    const digitalHumanStore = useDigitalHumanStore()

    // 切换快捷提示显示状态
    const toggleTips = () => {
      showTips.value = !showTips.value
    }

    // 插入快捷提示文本
    const insertTip = (text) => {
      messageText.value = text
      showTips.value = false // 插入后收起提示
    }

    // 从setup函数中执行智能动作
    const executeSmartActionFromSetup = async (content, emotion = null) => {
      try {
        // 确保content是字符串类型
        if (!content) {
          console.log('内容为空，跳过动作执行')
          return
        }
        
        const textContent = typeof content === 'string' ? content : JSON.stringify(content)
        
        // 获取当前数字人ID - 需要从本地存储获取
        const savedConfig = localStorage.getItem('digitalHumanConfig')
        if (!savedConfig) {
          console.log('没有找到配置，跳过动作执行')
          return
        }
        
        const config = JSON.parse(savedConfig)
        const avatarId = config?.setglobalparamsform?.avatar?.avatar_id
        if (!avatarId) {
          console.log('没有找到数字人ID，跳过动作执行')
          return
        }

        // 获取智能推荐的动作
        const actions = getSmartActions(textContent, avatarId, {
          emotion,
          maxActions: 1,
          includeRandom: false
        })

        if (actions.length > 0) {
          const action = actions[0]
          const delay = getActionDelay(textContent)
          
          console.log(`将在 ${delay}ms 后执行动作: ${action.name} (${action.id})`)
          
          // 延迟执行动作
          setTimeout(async () => {
            const success = await digitalHumanStore.sendActionToAvatar(action.id)
            if (success) {
              console.log(`动作执行成功: ${action.name}`)
            } else {
              console.log(`动作执行失败: ${action.name}`)
            }
          }, delay)
        }
      } catch (error) {
        console.error('执行智能动作失败:', error)
      }
    }

    // 监听语音识别结果
    watch(
      () => resultText.value,
      (newVal) => {
        console.log('识别结果：', newVal)
        messageText.value += resultText.value // 自动填充到输入框
      },
      { deep: true },
    )
    // 录音开关控制
    const toggleRecording = () => {
      if (isRecording.value) {
        stopRecording()
      } else {
        messageText.value = '' // 开始录音时清空输入框
        startRecording()
      }
      isRecording.value = !isRecording.value
    }
    const handleSendMessage = async (message) => {
      if (!messageText.value.trim()) return
      
      // 过滤文本内容
      const filteredMessage = filterForDigitalHuman(message)
      if (!filteredMessage) {
        console.log('消息过滤后为空，跳过发送')
        return
      }
      
      console.log('过滤后的消息:', filteredMessage)
      
      const emotion = detectEmotion(filteredMessage)
      const comfort = getComfort(emotion)
      if (comfort !== null && comfort.description !== null && comfort.description !== '') {
        const filteredComfort = filterForDigitalHuman(comfort.description)
        if (filteredComfort) {
          await digitalHumanStore.sendTextToAvatar(filteredComfort)
        }
      }
      // const text = messageText.value;
      messageText.value = '' // 清空输入框
      // 如果是新会话，先创建MemoryId
      if (!memoryId.value) {
        memoryId.value = generateMemoryId()
        startTime.value = getChinaTime()
      }
      // 添加用户消息
      const userMessage = {
        role: 'user',
        content: message,
        type: 'text',
        time: getChinaTime(),
      }
      // 添加临时AI消息用于显示流式响应
      const tempAiMessage = {
        role: 'assistant',
        content: '',
        type: 'text',
        time: getChinaTime(),
        isTyping: true,
        isReadingWebpage: detectUrl(message),
      }
      // 使用新数组添加消息，确保响应式更新
      messages.value = [...messages.value, userMessage, tempAiMessage]
      try {
        // 根据不同模式调用不同API
        let apiFunction
        apiFunction = sendStudentAIAgentMessage
        const response = await apiFunction(message, memoryId.value)
        if (response && response.body && typeof response.body.getReader === 'function') {
          const reader = response.body.getReader()
          const decoder = new TextDecoder('utf-8')
          // 保存reader引用，以便组件销毁时取消
          currentReader.value = reader
          try {
            while (true) {
              const { value, done } = await reader.read()
              if (done) {
                console.log('流结束', tempAiMessage)
                // AI回复完成时发送给数字人
                if (done && tempAiMessage.content) {
                  // 过滤AI回复内容
                  const filteredAiContent = filterForDigitalHuman(tempAiMessage.content)
                  if (filteredAiContent) {
                    await digitalHumanStore.sendTextToAvatar(filteredAiContent)
                    // 执行智能动作
                    await executeSmartActionFromSetup(filteredAiContent, emotion)
                  } else {
                    console.log('AI回复过滤后为空，跳过发送')
                  }
                }
                // 流结束
                tempAiMessage.isTyping = false
                tempAiMessage.isReadingWebpage = false
                // 强制更新视图
                messages.value = [...messages.value]
                break
              }
              // 解码流数据
              const chunk = decoder.decode(value, { stream: true })
              // 处理SSE格式 (data:前缀)
              const dataLines = chunk.split('\n')
              let processedContent = ''
              for (const line of dataLines) {
                if (line.startsWith('data:')) {
                  // 去除data:前缀，保留实际内容
                  let content = line.substring(5)
                  // 特殊字符处理
                  if (content === '-') {
                    content = '\n'
                  }
                  processedContent += content
                } else if (line.trim() !== '') {
                  processedContent += line
                }
              }
              // 更新消息内容 (重要：必须创建一个新对象而不是直接修改)
              if (processedContent) {
                // 如果是第一个响应且之前是"正在读取网页"，则替换内容
                if (tempAiMessage.content === '' && tempAiMessage.isReadingWebpage) {
                  tempAiMessage.isReadingWebpage = false
                  tempAiMessage.content = processedContent
                } else {
                  tempAiMessage.content += processedContent
                }
                // 制造一个新的消息数组以触发视图更新
                const messagesClone = messages.value.map((msg) =>
                  msg === tempAiMessage ? { ...tempAiMessage } : msg,
                )
                messages.value = messagesClone
              }
            }
          } catch (streamError) {
            console.error('读取流数据失败:', streamError)
            tempAiMessage.isTyping = false
            tempAiMessage.isReadingWebpage = false
            tempAiMessage.content += '\n\n[读取响应失败，请尝试重新发送消息]'
            // 重新克隆消息数组以更新视图
            messages.value = messages.value.map((msg) =>
              msg === tempAiMessage ? { ...tempAiMessage } : msg,
            )
          } finally {
            currentReader.value = null
          }
        } else {
          // 非流式响应
          tempAiMessage.isTyping = false
          tempAiMessage.isReadingWebpage = false
          console.log('这是非流式响应')
          try {
            const text = await response.text()
            tempAiMessage.content = text || '未获取到有效回复'
          } catch (error) {
            tempAiMessage.content = '处理响应时出错: ' + error.message
          }
          // 更新视图
          messages.value = [...messages.value]
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        // 如果出错，更新错误消息到最后一条
        const lastMessage = messages.value[messages.value.length - 1]
        if (lastMessage && lastMessage.role === 'assistant') {
          lastMessage.isTyping = false
          lastMessage.isReadingWebpage = false
          lastMessage.content = '抱歉，发送消息失败: ' + (error.message || '请稍后再试')

          // 更新视图
          messages.value = [...messages.value]
        }
        ElMessage.error('发送消息失败，请稍后再试')
      }
    }
    // 组件销毁时释放资源
    return {
      startRecording,
      stopRecording,
      handleSendMessage,
      toggleRecording,
      recordText,
      resultText,
      messages,
      isRecording,
      messageText,
      showTips,
      toggleTips,
      insertTip,
      executeSmartActionFromSetup,
    }
  },
  name: 'DigitalHumanAvatar',
  data() {
    return {
      hasConfig: false,
      config: null,
      isInitialized: false,
      digitalHumanStore: null,
    }
  },
  methods: {
    async appCome (message) {
      console.log('uniapp消息传递到组件内部，将要发送给数字人:', message)
      
      // 过滤文本内容
      const filteredMessage = filterForDigitalHuman(message)
      if (!filteredMessage) {
        console.log('消息过滤后为空，跳过发送')
        return
      }
      
      console.log('过滤后的消息:', filteredMessage)
      
      const emotion = detectEmotion(filteredMessage)
      const comfort = getComfort(emotion)
      if (comfort !== null && comfort.description !== null && comfort.description !== '') {
        const filteredComfort = filterForDigitalHuman(comfort.description)
        if (filteredComfort) {
          await this.digitalHumanStore.sendTextToAvatar(filteredComfort)
        }
      }
      await this.digitalHumanStore.sendTextToAvatar(filteredMessage)
      
      // 执行智能动作（使用原始消息进行动作分析）
      await this.executeSmartAction(message, emotion)
    },

    // 执行智能动作
    async executeSmartAction(content, emotion = null) {
      try {
        // 确保content是字符串类型
        if (!content) {
          console.log('内容为空，跳过动作执行')
          return
        }
        
        const textContent = typeof content === 'string' ? content : JSON.stringify(content)
        
        // 获取当前数字人ID
        const avatarId = this.config?.setglobalparamsform?.avatar?.avatar_id
        if (!avatarId) {
          console.log('没有找到数字人ID，跳过动作执行')
          return
        }

        // 获取智能推荐的动作
        const actions = getSmartActions(textContent, avatarId, {
          emotion,
          maxActions: 1,
          includeRandom: false
        })

        if (actions.length > 0) {
          const action = actions[0]
          const delay = getActionDelay(textContent)
          
          console.log(`将在 ${delay}ms 后执行动作: ${action.name} (${action.id})`)
          
          // 延迟执行动作
          setTimeout(async () => {
            const success = await this.digitalHumanStore.sendActionToAvatar(action.id)
            if (success) {
              console.log(`动作执行成功: ${action.name}`)
            } else {
              console.log(`动作执行失败: ${action.name}`)
            }
          }, delay)
        }
      } catch (error) {
        console.error('执行智能动作失败:', error)
      }
    },
    // 检查是否有本地配置
    checkLocalConfig() {
      try {
        const savedConfig = localStorage.getItem('digitalHumanConfig')
        if (savedConfig) {
          this.config = JSON.parse(savedConfig)
          this.hasConfig = true
          console.log('读取到本地配置:', this.config)
          return true
        }
        console.log('没有找到本地配置')
        return false
      } catch (error) {
        console.error('读取配置失败:', error)
        return false
      }
    },
    // 初始化SDK
    async initSDK() {
      if (!this.config) {
        console.log('没有配置，跳过初始化')
        return
      }

      try {
        console.log('开始初始化数字人SDK')
        // 确保容器存在
        await this.$nextTick()
        const wrapper = this.$refs.avatarWrapper
        if (!wrapper) {
          console.error('数字人容器未找到')
          return
        }
        console.log('找到数字人容器:', wrapper)
        // 实例化SDK，使用全局变量
        console.log('开始实例化AvatarPlatform...')
        avatarPlatform2 = new AvatarPlatform()
        console.log('SDK实例化成功，实例类型:', typeof avatarPlatform2)
        console.log('SDK实例方法:', Object.getOwnPropertyNames(avatarPlatform2))
        // 设置API信息
        const apiParams = {
          appId: this.config.form.appid,
          apiKey: this.config.form.apikey,
          apiSecret: this.config.form.apisecret,
          serverUrl: this.config.form.serverurl,
          sceneId: this.config.form.sceneid,
        }
        console.log('设置API信息:', apiParams)
        avatarPlatform2.setApiInfo(apiParams)

        // 设置全局参数
        let globalParams = Object.assign({}, this.config.setglobalparamsform)
        if (globalParams.enable === false) {
          delete globalParams.background
          delete globalParams.enable
        }

        // 强制设置透明背景
        globalParams.stream.alpha = 1

        console.log('设置全局参数:', globalParams)
        avatarPlatform2.setGlobalParams(globalParams)

        // 启动数字人
        console.log('开始启动数字人，容器:', wrapper)
        await avatarPlatform2.start({ wrapper: wrapper })

        // 创建播放器并监听事件
        const player = avatarPlatform2.createPlayer()
        player
          .on(PlayerEvents.play, function () {
            console.log('播放开始')
          })
          .on(PlayerEvents.playing, function () {
            console.log('正在播放')
          })
          .on(PlayerEvents.waiting, function () {
            console.log('等待播放')
          })
          .on(PlayerEvents.stop, function () {
            console.log('播放停止')
          })
          .on(PlayerEvents.playNotAllowed, function () {
            console.log(
              '播放被阻止：触发了浏览器限制自动播放策略，需要用户交互后调用player.resume()',
            )
            // 自动恢复播放
            setTimeout(() => {
              try {
                player.resume()
                console.log('已自动恢复播放')
              } catch (error) {
                console.error('恢复播放失败:', error)
              }
            }, 100)
          })

        this.isInitialized = true
        console.log('数字人初始化成功')
        console.log('启动后的SDK实例方法:', Object.getOwnPropertyNames(avatarPlatform2))

        // 将实例保存到store中，供其他组件使用
        this.digitalHumanStore.setAvatarPlatform(avatarPlatform2)
        this.digitalHumanStore.setConfig(this.config)

        // 设置连接状态为已连接
        this.digitalHumanStore.setConnectedStatus(true)
        console.log('数字人连接状态已设置为已连接')

        // 设置透明度控制
        this.setupOpacityControl()

        // 添加页面点击事件，确保音频能播放
        this.addAudioResumeListener()

        // 自动发送欢迎语
        await this.sendWelcomeMessage()
      } catch (error) {
        console.error('数字人初始化失败:', error)
        console.error('错误详情:', {
          message: error.message,
          stack: error.stack,
          name: error.name,
        })
        this.hasConfig = false
        this.digitalHumanStore.setConnectedStatus(false)
      }
    },

    // 检查是否需要发送开场白
    shouldSendWelcome() {
      try {
        const now = Date.now()
        const lastWelcomeTime = localStorage.getItem('lastWelcomeTime')
        
        if (!lastWelcomeTime) {
          console.log('首次使用，将发送开场白')
          return true
        }
        
        const timeDiff = now - parseInt(lastWelcomeTime)
        const oneHour = 60 * 60 * 1000 // 1小时的毫秒数
        
        if (timeDiff >= oneHour) {
          console.log(`距离上次开场白已过 ${Math.floor(timeDiff / (60 * 1000))} 分钟，将发送开场白`)
          return true
        } else {
          const remainingTime = Math.ceil((oneHour - timeDiff) / (60 * 1000))
          console.log(`距离上次开场白仅 ${Math.floor(timeDiff / (60 * 1000))} 分钟，还需等待 ${remainingTime} 分钟才会重新发送`)
          return false
        }
      } catch (error) {
        console.error('检查开场白时间失败:', error)
        return true // 出错时默认发送
      }
    },

    // 记录开场白发送时间
    recordWelcomeTime() {
      try {
        const now = Date.now()
        localStorage.setItem('lastWelcomeTime', now.toString())
        console.log('开场白时间已记录:', new Date(now).toLocaleString())
      } catch (error) {
        console.error('记录开场白时间失败:', error)
      }
    },

    // 发送欢迎语
    async sendWelcomeMessage() {
      try {
        // 检查是否需要发送开场白
        if (!this.shouldSendWelcome()) {
          console.log('跳过开场白发送')
          return
        }

        console.log('准备发送开场白...')
        const welcomeText = getRandomStart().description
        
        // 延迟2秒发送，确保数字人完全初始化
        setTimeout(async () => {
          try {
            const success = await this.digitalHumanStore.sendTextToAvatar(welcomeText)
            if (success) {
              console.log('开场白发送成功:', welcomeText)
              // 记录发送时间
              this.recordWelcomeTime()
            } else {
              console.error('开场白发送失败')
            }
          } catch (error) {
            console.error('发送开场白时出错:', error)
          }
        }, 2000)
      } catch (error) {
        console.error('发送欢迎语失败:', error)
      }
    },

    // 添加音频恢复监听器
    addAudioResumeListener() {
      const handleUserInteraction = () => {
        try {
          // 尝试恢复播放器
          if (avatarPlatform2) {
            const player = avatarPlatform2.createPlayer()
            if (player && typeof player.resume === 'function') {
              player.resume()
              console.log('用户交互后恢复音频播放')
            }
          }
        } catch (error) {
          console.error('恢复音频播放失败:', error)
        }
      }

      // 监听用户交互事件
      document.addEventListener('click', handleUserInteraction, { once: true })
      document.addEventListener('touchstart', handleUserInteraction, { once: true })
      document.addEventListener('keydown', handleUserInteraction, { once: true })

      console.log('已添加音频恢复监听器')
    },

    // 设置透明度控制
    setupOpacityControl() {
      const div = this.$refs.avatarWrapper
      const range = document.getElementById('opacityRange')

      if (div && range) {
        range.addEventListener('input', function () {
          div.style.opacity = this.value
        })
        console.log('透明度控制设置成功')
      } else {
        console.log('透明度控制元素未找到')
      }
    },

    // 销毁SDK
    destroySDK() {
      if (avatarPlatform2) {
        try {
          avatarPlatform2.stop()
          avatarPlatform2.destroy()
          avatarPlatform2 = null
        } catch (error) {
          console.error('销毁数字人实例失败:', error)
        }
      }
      this.digitalHumanStore.clearAvatarPlatform()
      console.log('数字人SDK已销毁')
    },
  },

  async mounted() {
    console.log('DigitalHumanAvatar组件已挂载')
    console.log('当前localStorage内容:', localStorage.getItem('digitalHumanConfig'))

    // 初始化store
    this.digitalHumanStore = useDigitalHumanStore()

    // 检查配置并自动初始化
    if (this.checkLocalConfig()) {
      console.log('找到配置，开始自动初始化')
      console.log('配置内容:', this.config)
      await this.initSDK()
    } else {
      console.log('没有找到配置，组件不显示')
      console.log('hasConfig状态:', this.hasConfig)
    }
  },

  beforeDestroy() {
    this.destroySDK()
  },
}
</script>

<style scoped>
/* 新增输入区域样式 */
.input-area {
  width: 100%;
  margin-top: 15px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 快捷提示样式 */
.quick-tips {
  margin-bottom: 8px;
}

.tips-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  background: #f8fafc;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 12px;
  color: #4a5568;
}

.tips-header:hover {
  background: #e2e8f0;
}

.toggle-icon {
  transition: transform 0.2s;
  font-size: 10px;
}

.toggle-icon.active {
  transform: rotate(180deg);
}

.tips-content {
  margin-top: 6px;
  padding: 8px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.tips-row {
  display: flex;
  gap: 4px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.tips-row:last-child {
  margin-bottom: 0;
}

.tip-btn {
  flex: 1;
  min-width: 80px;
  padding: 4px 6px;
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 10px;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.tip-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.tip-btn.emergency {
  background: #fed7d7;
  border-color: #fc8181;
  color: #c53030;
}

.tip-btn.emergency:hover {
  background: #fc8181;
  color: white;
}

.input-area input {
  width: 90%;
  padding: 10px 15px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin-bottom: 10px;
  font-size: 14px;
}

.voice-controls {
  display: flex;
  gap: 10px;
}

.voice-controls button {
  flex: 1;
  padding: 8px 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s;
}

.voice-controls button:hover {
  background: #5a67d8;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .input-area {
    padding: 8px;
  }

  .voice-controls button {
    padding: 6px 10px;
    font-size: 13px;
  }

  .tips-header {
    font-size: 11px;
    padding: 5px 6px;
  }

  .tip-btn {
    font-size: 9px;
    padding: 3px 4px;
    min-width: 60px;
  }

  .tips-content {
    padding: 6px;
  }
}

.digital-human-avatar {
  margin: auto;
  z-index: 1000;
  width: 300px;
  height: 400px;
  pointer-events: none;
}

.avatar-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: auto;
  position: relative;
}

.avatar-wrapper {
  width: 100%;
  height: 100%;
  background: transparent;
  position: relative;
  overflow: hidden;
  border-radius: 16px;
}

.opacity-control {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.93);
  padding: 8px 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-radius: 0 0 16px 16px;
  box-shadow: 0 2px 8px 0 rgba(102, 126, 234, 0.06);
}

.opacity-control span {
  color: #4a5568;
  font-weight: 600;
  font-size: 12px;
  white-space: nowrap;
}

.opacity-control input[type='range'] {
  flex: 1;
  height: 4px;
  border-radius: 2px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.opacity-control input[type='range']::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
  border: 1px solid #fff;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.18);
}

.opacity-control input[type='range']::-moz-range-thumb {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
  border: 1px solid #fff;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.18);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .digital-human-avatar {
    right: 50%;
    width: 200px;
    height: 300px;
  }

  .opacity-control {
    padding: 6px 8px;
  }

  .opacity-control span {
    font-size: 10px;
  }
}
</style>
