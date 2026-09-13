# 语音识别功能使用说明

## 概述

基于科大讯飞语音听写流式WebAPI实现的语音识别功能，支持实时语音转文字。

## 功能特性

- 🎤 实时语音识别
- 🔄 流式处理，边说话边识别
- 📱 支持移动端和桌面端
- 🎯 高精度中文识别
- 🔧 可配置的API参数
- 📋 识别结果可复制和发送

## 文件结构

```
src/utils/
├── speechRecognition.js          # 语音识别核心功能
└── speechRecognition.md          # 使用说明文档

src/components/
└── SpeechRecognitionDemo.vue     # 语音识别演示组件
```

## API配置

使用前需要在科大讯飞开放平台获取以下参数：

1. **APPID**: 应用ID
2. **APIKey**: API密钥
3. **APISecret**: API密钥对应的密钥

### 获取方式

1. 访问 [科大讯飞开放平台](https://www.xfyun.cn/)
2. 注册并登录账号
3. 创建新应用，选择"语音听写（流式）"服务
4. 获取APPID、APIKey和APISecret

## 使用方法

### 1. 基本使用

```javascript
import { createSpeechRecognition } from '@/utils/speechRecognition'

// 创建语音识别实例
const recognition = createSpeechRecognition(APPID, APIKey, APISecret)

// 设置回调函数
recognition.setOnResult((text) => {
  console.log('识别结果:', text)
})

recognition.setOnError((error) => {
  console.error('识别错误:', error)
})

recognition.setOnStatusChange((status) => {
  console.log('状态变化:', status)
})

// 开始录音
recognition.startRecognition()

// 停止录音
recognition.stopRecognition()

// 清理资源
recognition.cleanup()
```

### 2. Vue 3 Composition API 使用

```javascript
import { useSpeechRecognition } from '@/utils/speechRecognition'

export default {
  setup() {
    const { 
      recognition, 
      isRecording, 
      result, 
      error,
      start, 
      stop, 
      cleanup 
    } = useSpeechRecognition(APPID, APIKey, APISecret)

    return {
      isRecording,
      result,
      error,
      start,
      stop,
      cleanup
    }
  }
}
```

### 3. 在组件中使用

```vue
<template>
  <div>
    <el-button @click="start" :loading="isRecording">
      {{ isRecording ? '录音中...' : '开始录音' }}
    </el-button>
    
    <el-button @click="stop" :disabled="!isRecording">
      停止录音
    </el-button>
    
    <div>识别结果: {{ result }}</div>
    
    <div v-if="error">错误: {{ error.message }}</div>
  </div>
</template>

<script>
import { useSpeechRecognition } from '@/utils/speechRecognition'

export default {
  setup() {
    const { 
      isRecording, 
      result, 
      error,
      start, 
      stop 
    } = useSpeechRecognition('your_appid', 'your_apikey', 'your_apisecret')

    return {
      isRecording,
      result,
      error,
      start,
      stop
    }
  }
}
</script>
```

## 演示页面

访问 `/admin/speech-recognition-demo` 查看完整的语音识别演示。

### 演示页面功能

- ✅ API配置管理
- ✅ 实时语音识别
- ✅ 识别结果显示
- ✅ 结果复制功能
- ✅ 发送给数字人
- ✅ 状态监控
- ✅ 错误处理

## 技术实现

### 核心特性

1. **WebSocket连接**: 与科大讯飞服务器建立实时连接
2. **音频采集**: 使用WebRTC API获取麦克风音频
3. **实时处理**: 边录音边发送音频数据进行识别
4. **结果解析**: 解析服务器返回的识别结果

### 音频处理流程

1. 获取麦克风权限
2. 创建音频上下文
3. 采集音频数据
4. 转换为Int16格式
5. Base64编码
6. 通过WebSocket发送
7. 接收识别结果

### 错误处理

- 麦克风权限错误
- 网络连接错误
- API认证错误
- 音频格式错误

## 注意事项

### 浏览器兼容性

- ✅ Chrome 66+
- ✅ Firefox 60+
- ✅ Safari 11+
- ✅ Edge 79+

### 权限要求

- 麦克风访问权限
- HTTPS环境（生产环境必需）

### 性能优化

- 音频采样率: 16kHz
- 音频格式: 16bit PCM
- 缓冲区大小: 4096字节
- 发送间隔: 40ms

### 安全考虑

- API密钥请妥善保管
- 生产环境使用HTTPS
- 定期更新API密钥

## 故障排除

### 常见问题

1. **无法获取麦克风权限**
   - 检查浏览器设置
   - 确保网站使用HTTPS
   - 手动允许麦克风权限

2. **WebSocket连接失败**
   - 检查网络连接
   - 验证API配置
   - 确认服务器状态

3. **识别结果为空**
   - 检查麦克风是否正常工作
   - 确认说话音量足够
   - 检查音频格式设置

4. **识别准确率低**
   - 使用高质量麦克风
   - 在安静环境中使用
   - 说话清晰，语速适中

### 调试方法

1. 打开浏览器开发者工具
2. 查看Console日志
3. 检查Network面板的WebSocket连接
4. 验证API参数配置

## 更新日志

### v1.0.0
- 初始版本
- 支持基本语音识别功能
- 提供Vue 3 Composition API支持
- 包含完整的演示页面

## 许可证

本项目基于MIT许可证开源。 