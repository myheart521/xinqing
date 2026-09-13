# WebSocket工具类使用说明

这是一个用于处理WebSocket连接、消息发送接收、断线重连等功能的工具类。本文档将详细介绍如何使用该工具类以及如何根据业务需求进行修改。

## 一、基本使用

### 1. 引入工具类

```javascript
import WebSocketUtil from '@/utils/websocket.js'
```

### 2. 创建实例

```javascript
// 使用默认配置
const ws = new WebSocketUtil()

// 或者自定义配置
const ws = new WebSocketUtil({
  baseUrl: 'ws://localhost:8080/path',
  autoReconnect: true,
  reconnectInterval: 5000,
  maxReconnectAttempts: 10
})
```

### 3. 建立连接

```javascript
// 建立连接
ws.connect()
  .then(() => {
    console.log('WebSocket连接成功')
  })
  .catch(error => {
    console.error('WebSocket连接失败:', error)
  })
```

### 4. 发送消息

```javascript
// 发送字符串消息
ws.send('Hello, server!')

// 发送JSON对象（会自动转换为JSON字符串）
ws.send({
  type: 'chat',
  content: '你好，这是一条测试消息',
  timestamp: Date.now()
})

// 使用Promise处理发送结果
ws.send({ type: 'request', action: 'getHistory' })
  .then(() => {
    console.log('消息发送成功')
  })
  .catch(error => {
    console.error('消息发送失败:', error)
  })
```

### 5. 关闭连接

```javascript
// 关闭连接
ws.close()

// 带参数关闭
ws.close({
  code: 1000,
  reason: '用户主动关闭'
})

// 使用Promise处理关闭结果
ws.close()
  .then(() => {
    console.log('WebSocket已关闭')
  })
  .catch(error => {
    console.error('WebSocket关闭失败:', error)
  })
```

### 6. 销毁实例

在组件销毁时，应该调用destroy方法释放资源：

```javascript
// 在组件的onUnmounted生命周期中调用
onUnmounted(() => {
  ws.destroy()
})
```

## 二、需要根据业务修改的地方

### 1. WebSocket服务器地址

修改`baseUrl`配置或重写`getUrl()`方法：

```javascript
// 方式1：通过配置传入
const ws = new WebSocketUtil({
  baseUrl: 'ws://localhost:8080/path'
})

// 方式2：继承并重写getUrl方法
class MyWebSocket extends WebSocketUtil {
  getUrl() {
    const userInfo = userStorage.getUserInfo()
    const token = userInfo?.token || ''
    const userId = userInfo?.id || ''
    
    // 构建URL，添加token和userId参数
    return `${this.config.baseUrl}?token=${token}&userId=${userId}`
  }
}
```

### 2. 消息处理逻辑

重写或扩展消息处理方法：

```javascript
// 方式1：通过继承重写方法
class MyWebSocket extends WebSocketUtil {
  handleMessage(res) {
    console.log('收到消息:', res.data)
    
    try {
      const message = JSON.parse(res.data)
      
      // 根据业务需求处理不同类型的消息
      switch (message.type) {
        case 'chat':
          this.handleChatMessage(message)
          break
        case 'notification':
          this.handleNotification(message)
          break
        // 其他消息类型...
      }
    } catch (error) {
      console.error('消息解析失败:', error)
    }
  }
  
  handleChatMessage(message) {
    // 处理聊天消息的逻辑
    console.log('收到聊天消息:', message)
    // 更新聊天记录、显示新消息提醒等
  }
  
  handleNotification(message) {
    // 处理通知消息的逻辑
    console.log('收到通知:', message)
    // 显示通知、更新未读消息数等
  }
}

// 方式2：使用回调函数
const ws = new WebSocketUtil()

// 设置消息处理回调
ws.onChatMessage = (message) => {
  console.log('收到聊天消息:', message)
  // 处理聊天消息
}

ws.onNotificationMessage = (message) => {
  console.log('收到通知消息:', message)
  // 处理通知消息
}

ws.onMessage = (message) => {
  console.log('收到其他消息:', message)
  // 处理其他类型消息
}
```

### 3. 心跳机制

调整心跳相关配置：

```javascript
const ws = new WebSocketUtil({
  // 心跳间隔（毫秒）
  heartbeatInterval: 30000,
  
  // 心跳消息内容
  heartbeatMessage: JSON.stringify({ type: 'ping' })
})
```

### 4. 重连策略

调整重连相关配置：

```javascript
const ws = new WebSocketUtil({
  // 是否自动重连
  autoReconnect: true,
  
  // 重连间隔（毫秒）
  reconnectInterval: 3000,
  
  // 最大重连次数，-1表示无限重连
  maxReconnectAttempts: 5
})
```

## 三、在Vue组件中使用

### 在setup中使用

```vue
<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import WebSocketUtil from '@/utils/websocket.js'

// 创建WebSocket实例
const ws = new WebSocketUtil()
const messages = ref([])

// 处理接收到的消息
ws.onChatMessage = (message) => {
  messages.value.push(message)
}

// 发送消息
const sendMessage = (content) => {
  ws.send({
    type: 'chat',
    content,
    timestamp: Date.now()
  })
}

// 组件挂载时连接WebSocket
onMounted(() => {
  ws.connect()
    .then(() => {
      console.log('WebSocket连接成功')
    })
    .catch(error => {
      console.error('WebSocket连接失败:', error)
    })
})

// 组件卸载时销毁WebSocket
onUnmounted(() => {
  ws.destroy()
})
</script>
```

### 在Vuex/Pinia中使用

```javascript
// store/chat.js
import WebSocketUtil from '@/utils/websocket.js'

export const useChatStore = defineStore('chat', {
  state: () => ({
    ws: null,
    connected: false,
    messages: []
  }),
  
  actions: {
    // 初始化WebSocket
    initWebSocket() {
      if (this.ws) return
      
      this.ws = new WebSocketUtil()
      
      // 设置消息处理回调
      this.ws.onChatMessage = (message) => {
        this.messages.push(message)
      }
      
      // 连接WebSocket
      this.ws.connect()
        .then(() => {
          this.connected = true
        })
        .catch(error => {
          console.error('WebSocket连接失败:', error)
        })
    },
    
    // 发送消息
    sendMessage(content) {
      if (!this.ws || !this.connected) {
        console.error('WebSocket未连接')
        return
      }
      
      return this.ws.send({
        type: 'chat',
        content,
        timestamp: Date.now()
      })
    },
    
    // 关闭WebSocket
    closeWebSocket() {
      if (!this.ws) return
      
      this.ws.destroy()
      this.ws = null
      this.connected = false
    }
  }
})
```

## 四、常见问题

### 1. 消息发送失败

可能原因：
- WebSocket未连接
- 网络异常
- 服务器拒绝消息

解决方案：
- 检查连接状态
- 添加重试机制
- 检查消息格式是否符合服务器要求

### 2. 连接频繁断开

可能原因：
- 网络不稳定
- 服务器限制
- 心跳机制未正确配置

解决方案：
- 调整重连策略
- 优化心跳机制
- 检查网络环境

### 3. 消息重复接收

可能原因：
- 重连后服务器重发消息
- 客户端重复处理

解决方案：
- 添加消息去重机制
- 使用消息ID标识唯一消息

## 五、安全建议

1. **使用WSS协议**：在生产环境中使用WSS（WebSocket Secure）协议，确保通信安全。

2. **身份验证**：在WebSocket连接URL中包含token或其他身份验证信息。

3. **消息验证**：对接收到的消息进行验证，防止恶意消息。

4. **敏感信息加密**：对传输的敏感信息进行加密处理。

5. **限制重连次数**：防止恶意攻击导致的频繁重连。

## 六、性能优化

1. **消息队列**：当WebSocket未连接时，将消息存入队列，连接成功后再发送。

2. **批量发送**：将多条消息合并后一次性发送，减少通信次数。

3. **消息压缩**：对大型消息进行压缩，减少传输数据量。

4. **心跳优化**：根据网络环境动态调整心跳间隔。

5. **资源释放**：不再使用时及时调用destroy方法释放资源。 