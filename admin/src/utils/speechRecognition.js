/**
 * 语音识别工具函数
 * 基于科大讯飞语音听写流式WebAPI
 * 参考Python代码实现
 */

import { ref } from 'vue'
import CryptoJS from 'crypto-js'

// 音频状态标识
const STATUS_FIRST_FRAME = 0  // 第一帧的标识
const STATUS_CONTINUE_FRAME = 1  // 中间帧标识
const STATUS_LAST_FRAME = 2  // 最后一帧的标识

/**
 * 语音识别参数类
 */
class SpeechRecognitionParams {
  constructor(APPID, APIKey, APISecret) {
    this.APPID = APPID
    this.APIKey = APIKey
    this.APISecret = APISecret
  }

  /**
   * 生成WebSocket URL（严格按官方文档）
   */
  createUrl() {
    const url = 'wss://iat.xf-yun.com/v1'
    const host = 'iat.xf-yun.com'
    const path = '/v1'
    const method = 'GET'
    const httpVersion = 'HTTP/1.1'

    // 生成RFC1123格式的时间戳
    const now = new Date()
    const date = now.toUTCString()

    // 1. signature_origin
    const signature_origin = `host: ${host}\ndate: ${date}\n${method} ${path} ${httpVersion}`

    // 2. signature_sha = hmac-sha256(signature_origin, APISecret)
    const hash = CryptoJS.HmacSHA256(signature_origin, this.APISecret)
    // 3. signature = base64(signature_sha)
    const signature = CryptoJS.enc.Base64.stringify(hash)

    // 4. authorization_origin
    const authorization_origin = `api_key=\"${this.APIKey}\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"${signature}\"`
    // 5. authorization = base64(authorization_origin)
    const authorization = btoa(authorization_origin)

    // 6. 拼接URL参数
    const v = {
      "authorization": authorization,
      "date": date,
      "host": host
    }
    const queryString = new URLSearchParams(v).toString()
    return url + '?' + queryString
  }

  /**
   * HMAC-SHA256加密
   */
  hmacSha256(key, message) {
    // 使用crypto-js实现HMAC-SHA256
    const hash = CryptoJS.HmacSHA256(message, key)
    return CryptoJS.enc.Base64.stringify(hash)
  }

  /**
   * 简单的HMAC-SHA256实现（已废弃，实际已用crypto-js实现）
   * 实际使用时建议使用crypto-js库
   */
  simpleHmacSha256(key, message) {
    // 已废弃
    return 'mock_signature_' + Date.now()
  }
}

/**
 * 语音识别类
 */
export class SpeechRecognition {
  constructor(APPID, APIKey, APISecret) {
    this.params = new SpeechRecognitionParams(APPID, APIKey, APISecret)
    this.ws = null
    this.isRecording = false
    this.recognitionResult = ref('')
    this.onResult = null
    this.onError = null
    this.onStatusChange = null
    this.seq = 1 // 帧序号
  }

  /**
   * 开始语音识别
   */
  startRecognition() {
    if (this.isRecording) {
      console.warn('语音识别已在进行中')
      return
    }

    // 清理之前的连接
    this.cleanup()

    this.isRecording = true
    this.recognitionResult.value = ''
    this.seq = 1 // 重置帧序号
    
    if (this.onStatusChange) {
      this.onStatusChange('start')
    }

    // 获取麦克风权限并开始录音
    this.startMicrophone()
  }

  /**
   * 停止语音识别
   */
  stopRecognition() {
    if (!this.isRecording) {
      console.warn('语音识别未在进行中')
      return
    }

    this.isRecording = false
    
    // 发送最后一帧音频数据
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.sendAudioData('', STATUS_LAST_FRAME)
    }

    if (this.onStatusChange) {
      this.onStatusChange('stop')
    }
  }

  /**
   * 开始麦克风录音
   */
  async startMicrophone() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ 
        audio: {
          sampleRate: 16000,
          channelCount: 1,
          echoCancellation: true,
          noiseSuppression: true
        } 
      })

      console.log('麦克风权限获取成功')

      // 创建WebSocket连接
      this.createWebSocketConnection()

      // 等待WebSocket连接建立
      await this.waitForWebSocketConnection()

      // 创建音频上下文
      const audioContext = new AudioContext({ sampleRate: 16000 })
      const source = audioContext.createMediaStreamSource(stream)
      
      // 使用AudioWorkletNode替代废弃的ScriptProcessorNode
      let processor
      try {
        // 尝试使用AudioWorkletNode
        await audioContext.audioWorklet.addModule(URL.createObjectURL(new Blob([`
          class AudioProcessor extends AudioWorkletProcessor {
            process(inputs, outputs, parameters) {
              const input = inputs[0];
              // 不连接输出，避免回音
              this.port.postMessage(input[0]);
              return true;
            }
          }
          registerProcessor('audio-processor', AudioProcessor);
        `], { type: 'application/javascript' })))
        
        processor = new AudioWorkletNode(audioContext, 'audio-processor')
        processor.port.onmessage = (e) => {
          this.handleAudioData(e.data)
        }
      } catch (error) {
        console.warn('AudioWorkletNode不可用，使用ScriptProcessorNode:', error)
        // 降级到ScriptProcessorNode
        processor = audioContext.createScriptProcessor(4096, 1, 1)
        processor.onaudioprocess = (e) => {
          this.handleAudioData(e.inputBuffer.getChannelData(0))
        }
      }

      // 只连接输入，不连接输出，避免回音
      source.connect(processor)
      // 移除这行：processor.connect(audioContext.destination)

      // 保存引用以便停止时清理
      this.audioContext = audioContext
      this.processor = processor
      this.stream = stream

      console.log('音频处理开始')

    } catch (error) {
      console.error('获取麦克风权限失败:', error)
      if (this.onError) {
        this.onError(error)
      }
      this.isRecording = false
    }
  }

  /**
   * 等待WebSocket连接建立
   */
  waitForWebSocketConnection() {
    return new Promise((resolve, reject) => {
      const checkConnection = () => {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          console.log('WebSocket连接已就绪，可以发送音频数据')
          resolve()
        } else if (this.ws && this.ws.readyState === WebSocket.CLOSED) {
          reject(new Error('WebSocket连接失败'))
        } else {
          setTimeout(checkConnection, 100)
        }
      }
      checkConnection()
    })
  }

  /**
   * 创建WebSocket连接
   */
  createWebSocketConnection() {
    const url = this.params.createUrl()
    console.log('创建WebSocket连接:', url)
    
    this.ws = new WebSocket(url)
    
    this.ws.onopen = () => {
      console.log('WebSocket连接已建立')
    }

    this.ws.onmessage = (event) => {
      this.handleWebSocketMessage(event.data)
    }

    this.ws.onerror = (error) => {
      console.error('WebSocket错误:', error)
      if (this.onError) {
        this.onError(error)
      }
    }

    this.ws.onclose = () => {
      console.log('WebSocket连接已关闭')
    }
  }

  /**
   * 处理WebSocket消息
   */
  handleWebSocketMessage(data) {
    try {
      console.log('收到WebSocket消息:', data)
      const message = JSON.parse(data)
      const code = message.header.code
      const status = message.header.status

      console.log('消息状态:', { code, status, message })

      if (code !== 0) {
        console.error(`请求错误：${code}`, message.header.message)
        if (this.onError) {
          this.onError(new Error(`语音识别错误：${code} - ${message.header.message}`))
        }
        return
      }

      // 第一帧返回：只有header，没有payload
      if (status === 0) {
        console.log('收到第一帧确认')
        return
      }

      // 中间帧和最后一帧：有payload.result
      const payload = message.payload
      if (payload && payload.result) {
        console.log('收到识别结果:', payload.result)
        
        const result = payload.result
        if (result.text) {
          try {
            // 修复UTF-8编码问题 - 正确解码base64
            const binaryString = atob(result.text)
            const bytes = new Uint8Array(binaryString.length)
            for (let i = 0; i < binaryString.length; i++) {
              bytes[i] = binaryString.charCodeAt(i)
            }
            const decodedText = new TextDecoder('utf-8').decode(bytes)
            console.log('解码后的文本:', decodedText)
            
            // 解析JSON
            const textData = JSON.parse(decodedText)
            console.log('解析后的数据:', textData)
            
            // 提取识别文本 - 根据文档，每次返回的都是完整结果
            let recognizedText = ''
            if (textData.ws) {
              for (const ws of textData.ws) {
                for (const cw of ws.cw) {
                  recognizedText += cw.w
                }
              }
            }

            console.log('提取的文本:', recognizedText)

            if (recognizedText.trim()) {
              // 修复：每次返回的都是完整结果，直接替换
              this.recognitionResult.value = recognizedText
              
              if (this.onResult) {
                this.onResult(recognizedText, true) // 总是传递true，表示完整结果
              }
            }
          } catch (parseError) {
            console.error('解析识别结果失败:', parseError)
          }
        }
      }

      // 最后一帧：不自动关闭连接，等待手动停止
      if (status === 2) {
        console.log('识别完成，等待手动停止')
        // 移除自动关闭：this.ws.close()
      }

    } catch (error) {
      console.error('处理WebSocket消息失败:', error)
    }
  }

  /**
   * 处理音频数据
   */
  handleAudioData(inputData) {
    if (!this.isRecording || !this.ws || this.ws.readyState !== WebSocket.OPEN) return

    const audioData = this.convertFloat32ToInt16(inputData)
    
    // 修复音频数据编码 - 正确转换Int16Array为base64
    const audioBytes = new Uint8Array(audioData.buffer)
    const audioBase64 = btoa(String.fromCharCode.apply(null, audioBytes))

    console.log(`发送音频帧 ${this.seq}, 状态: ${this.seq === 1 ? STATUS_FIRST_FRAME : STATUS_CONTINUE_FRAME}, 数据长度: ${audioBase64.length}`)

    // 发送音频数据
    this.sendAudioData(audioBase64, this.seq === 1 ? STATUS_FIRST_FRAME : STATUS_CONTINUE_FRAME)
    
    this.seq++
  }

  /**
   * 发送音频数据
   */
  sendAudioData(audio, status) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return

    const data = {
      header: {
        app_id: this.params.APPID,
        status: status
      },
      parameter: status === 0 ? { 
        iat: {
          domain: "slm",
          language: "zh_cn",
          accent: "mandarin",
          eos: 6000, // 静音6秒停止识别
          vinfo: 1,  // 句子级别帧对齐
          dwa: "wpgs", // 流式识别PGS
          result: {
            encoding: "utf8",
            compress: "raw",
            format: "json"
          }
        }
      } : undefined,
      payload: {
        audio: {
          audio: audio,
          sample_rate: 16000,
          encoding: "raw",
          channels: 1,
          bit_depth: 16,
          seq: this.seq,
          status: status
        }
      }
    }
    this.seq++
    this.ws.send(JSON.stringify(data))
  }

  /**
   * 转换Float32Array为Int16Array
   */
  convertFloat32ToInt16(float32Array) {
    const int16Array = new Int16Array(float32Array.length)
    for (let i = 0; i < float32Array.length; i++) {
      const s = Math.max(-1, Math.min(1, float32Array[i]))
      int16Array[i] = s < 0 ? s * 0x8000 : s * 0x7FFF
    }
    return int16Array
  }

  /**
   * 清理资源
   */
  cleanup() {
    this.isRecording = false
    
    // 关闭WebSocket连接
    if (this.ws) {
      if (this.ws.readyState === WebSocket.OPEN) {
        this.ws.close()
      }
      this.ws = null
    }
    
    // 清理音频处理器
    if (this.processor) {
      this.processor.disconnect()
      this.processor = null
    }
    
    // 关闭音频上下文
    if (this.audioContext) {
      this.audioContext.close()
      this.audioContext = null
    }
    
    // 停止麦克风流
    if (this.stream) {
      this.stream.getTracks().forEach(track => track.stop())
      this.stream = null
    }

    console.log('资源清理完成')
  }

  /**
   * 设置结果回调
   */
  setOnResult(callback) {
    this.onResult = callback
  }

  /**
   * 设置错误回调
   */
  setOnError(callback) {
    this.onError = callback
  }

  /**
   * 设置状态变化回调
   */
  setOnStatusChange(callback) {
    this.onStatusChange = callback
  }
}

/**
 * 创建语音识别实例
 */
export function createSpeechRecognition(APPID, APIKey, APISecret) {
  return new SpeechRecognition(APPID, APIKey, APISecret)
}

/**
 * 语音识别Hook（Vue 3 Composition API）
 */
export function useSpeechRecognition(APPID, APIKey, APISecret) {
  const recognition = new SpeechRecognition(APPID, APIKey, APISecret)
  const isRecording = ref(false)
  const result = ref('')
  const error = ref(null)

  recognition.setOnResult((text, isComplete) => {
    // 每次都是完整结果，直接替换
    result.value = text
  })

  recognition.setOnError((err) => {
    error.value = err
  })

  recognition.setOnStatusChange((status) => {
    isRecording.value = status === 'start'
  })

  return {
    recognition,
    isRecording,
    result,
    error,
    start: () => recognition.startRecognition(),
    stop: () => recognition.stopRecognition(),
    cleanup: () => recognition.cleanup()
  }
} 