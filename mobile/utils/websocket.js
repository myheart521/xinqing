/**
 * WebSocket工具类
 * 用于处理WebSocket连接、消息发送接收、断线重连等功能
 *
 * 使用说明：
 * 1. 根据业务需求修改配置参数
 * 2. 在需要使用WebSocket的页面引入并初始化
 * 3. 根据业务需求实现onMessage回调处理接收到的消息
 *
 * 需要根据业务修改的地方：
 * 1. baseUrl: WebSocket服务器地址
 * 2. getUrl(): 根据业务需求构建完整的WebSocket连接URL
 * 3. onMessage(): 处理接收到的消息
 * 4. 心跳间隔和重连策略
 */
import {requestBaseUrl, requestPort, requestUrl} from "@/utils/URL";


class WebSocketUtil {
    constructor(options = {}) {
        // WebSocket实例
        this.socketTask = null
        //发送人id
        this.receiverId = options.receiverId
        this.token=options.token

        // 配置参数，可在初始化时传入覆盖默认值
        this.config = {
            // WebSocket服务器基础地址 - 动态选择 ws / wss 并附带端口
            baseUrl: `${requestUrl.startsWith('https') ? 'wss' : 'ws'}://${requestBaseUrl}/xinqing/chat`,

            // 是否自动重连
            autoReconnect: true,

            // 重连间隔（毫秒）
            reconnectInterval: 3000,

            // 最大重连次数，-1表示无限重连
            maxReconnectAttempts: 5,

            // 心跳间隔（毫秒）
            heartbeatInterval: 30000,

            // 心跳消息内容
            // heartbeatMessage: JSON.stringify({type: 'ping'}),

            // 连接超时时间（毫秒）
            connectTimeout: 10000,

            ...options
        }

        // 当前重连次数
        this.reconnectAttempts = 0

        // 心跳定时器
        this.heartbeatTimer = null

        // 重连定时器
        this.reconnectTimer = null

        // 连接状态
        this.connected = false

        // 是否人为关闭连接
        this.manualClose = false

        // 消息队列，存储连接建立前发送的消息
        this.messageQueue = []
        // 如果在 options 中定义了 onMessage 回调，则覆盖默认实现
        if (typeof options.onMessage === 'function') {
            this.onMessage = options.onMessage
        }
    }

    /**
     * 获取完整的WebSocket连接URL
     * 【需要修改】根据业务需求构建URL，如添加token、用户ID等参数
     * @returns {string} 完整的WebSocket连接URL
     */
    getUrl() {

        // 构建URL，添加token及接收人
        return `${this.config.baseUrl}?token=${this.token}&receiverId=${this.receiverId}`
    }

    /**
     * 初始化WebSocket连接
     * @returns {Promise} 返回Promise，连接成功时resolve，失败时reject
     */
    connect() {
        // 如果已经连接，则直接返回
        if (this.connected && this.socketTask) {
            return Promise.resolve(this.socketTask)
        }

        // 重置手动关闭标志
        this.manualClose = false

        return new Promise((resolve, reject) => {
            // 创建连接超时定时器
            const timeoutTimer = setTimeout(() => {
                reject(new Error('WebSocket连接超时'))
            }, this.config.connectTimeout)

            try {
                // 创建WebSocket连接（使用官方 API，支持多平台）
                this.socketTask = uni.connectSocket({
                    url: this.getUrl(),
                    timeout: this.config.connectTimeout,
                    header: {
                      // 可按需添加鉴权信息
                      'token': this.token || ''
                    },
                    success: () => {
                      console.log('WebSocket 连接创建成功')
                    },
                    fail: (error) => {
                      console.error('WebSocket 连接创建失败:', error)
                      clearTimeout(timeoutTimer)
                      reject(error)
                    }
                })

                // 监听WebSocket连接打开
                this.socketTask.onOpen(() => {
                    console.log('WebSocket连接已打开')
                    clearTimeout(timeoutTimer)
                    this.connected = true
                    this.reconnectAttempts = 0

                    // 发送队列中的消息
                    this.flushMessageQueue()

                    // 开启心跳
                    // this.startHeartbeat()

                    resolve(this.socketTask)
                })

                // 监听WebSocket错误
                this.socketTask.onError((res) => {
                    console.error('WebSocket连接错误:', res)
                    this.connected = false
                    clearTimeout(timeoutTimer)
                    reject(res)

                    // 尝试重连
                    if (this.config.autoReconnect && !this.manualClose) {
                        this.reconnect()
                    }
                })

                // 监听WebSocket关闭
                this.socketTask.onClose(() => {
                    console.log('WebSocket连接已关闭')
                    this.connected = false

                    // 清除心跳定时器
                    this.stopHeartbeat()

                    // 尝试重连
                    if (this.config.autoReconnect && !this.manualClose) {
                        this.reconnect()
                    }
                })

                // 监听WebSocket接收到消息
                this.socketTask.onMessage((res) => {
                    // 处理接收到的消息
                    this.handleMessage(res)
                })

            } catch (error) {
                console.error('WebSocket初始化异常:', error)
                clearTimeout(timeoutTimer)
                reject(error)
            }
        })
    }

    /**
     * 处理接收到的消息
     * 【需要修改】根据业务需求处理不同类型的消息
     * @param {Object} res 接收到的消息对象
     */
    handleMessage(res) {
        console.log('收到WebSocket消息:', res.data)
        try {
            // 尝试解析JSON消息
            const message = JSON.parse(res.data)

            // 处理心跳响应
            if (message.type === 'pong') {
                console.log('收到心跳响应')
                return
            }

            // 【需要修改】根据业务需求处理不同类型的消息
            // 例如：根据message.type区分不同类型的消息
            this.onMessage(message)
            // this.onChatMessage(message)

            // switch (message.type) {
            //     case 'chat':
            //         // 处理聊天消息
            //         this.onChatMessage(message)
            //         break
            //     case 'notification':
            //         // 处理通知消息
            //         this.onNotificationMessage(message)
            //         break
            //     default:
            //         // 处理其他类型消息
            //         this.onMessage(message)
            //         console.log('收到其他类型消息:', message)
            //         break
            // }
        } catch (error) {
            // 非JSON格式消息，直接传递原始数据
            this.onMessage(res.data)
        }
    }

    /**
     * 处理聊天消息的回调
     * 【需要修改】根据业务需求实现
     * @param {Object} message 聊天消息对象
     */
    onChatMessage(message) {
        console.log('收到聊天消息:', message)
        // 在这里实现聊天消息的处理逻辑
    }

    /**
     * 处理通知消息的回调
     * 【需要修改】根据业务需求实现
     * @param {Object} message 通知消息对象
     */
    onNotificationMessage(message) {
        console.log('收到通知消息:', message)
        // 在这里实现通知消息的处理逻辑
    }

    /**
     * 处理消息的通用回调
     * 【需要修改】根据业务需求实现
     * @param {Object|string} message 消息内容
     */
    onMessage(message) {
        // console.log('收到消息:', message)
        // 在这里实现通用消息处理逻辑
    }

    /**
     * 发送消息
     * @param {Object|string} message 要发送的消息
     * @returns {Promise} 返回Promise，发送成功时resolve，失败时reject
     */
    send(message) {
        // 如果消息是对象，则转换为JSON字符串
        const data = typeof message === 'object' ? JSON.stringify(message) : message

        return new Promise((resolve, reject) => {
            // 如果未连接，则将消息加入队列
            if (!this.connected || !this.socketTask) {
                this.messageQueue.push({
                    data,
                    resolve,
                    reject
                })

                // 尝试连接
                if (!this.connected && !this.manualClose) {
                    this.connect().catch(error => {
                        console.error('自动连接失败:', error)
                    })
                }
                return
            }

            // 发送消息
            this.socketTask.send({
                data,
                success: (res) => {
                    resolve(res)
                },
                fail: (error) => {
                    console.error('消息发送失败:', error)
                    reject(error)
                }
            })
        })
    }

    /**
     * 发送队列中的消息
     */
    flushMessageQueue() {
        if (this.messageQueue.length === 0) return

        console.log(`发送队列中的${this.messageQueue.length}条消息`)

        // 复制队列并清空原队列
        const queue = [...this.messageQueue]
        this.messageQueue = []

        // 发送队列中的消息
        queue.forEach(item => {
            this.socketTask.send({
                data: item.data,
                success: (res) => {
                    item.resolve(res)
                },
                fail: (error) => {
                    console.error('队列消息发送失败:', error)
                    item.reject(error)
                }
            })
        })
    }

    /**
     * 开始心跳
     */
    startHeartbeat() {
        // 清除现有心跳定时器
        this.stopHeartbeat()

        // 创建新的心跳定时器
        // this.heartbeatTimer = setInterval(() => {
        //     if (this.connected) {
        //         console.log('发送心跳消息')
        //         this.send(this.config.heartbeatMessage).catch(error => {
        //             console.error('心跳消息发送失败:', error)
        //         })
        //     }
        // }, this.config.heartbeatInterval)
    }

    /**
     * 停止心跳
     */
    stopHeartbeat() {
        if (this.heartbeatTimer) {
            clearInterval(this.heartbeatTimer)
            this.heartbeatTimer = null
        }
    }

    /**
     * 重新连接
     */
    reconnect() {
        // 如果已经在重连或已达到最大重连次数，则不再重连
        if (
            this.reconnectTimer ||
            (this.config.maxReconnectAttempts !== -1 && this.reconnectAttempts >= this.config.maxReconnectAttempts)
        ) {
            return
        }

        this.reconnectAttempts++

        console.log(`尝试第${this.reconnectAttempts}次重连，${this.config.reconnectInterval / 1000}秒后重连`)

        // 创建重连定时器
        this.reconnectTimer = setTimeout(() => {
            this.reconnectTimer = null

            console.log('开始重新连接...')
            this.connect().catch(error => {
                console.error('重连失败:', error)
            })
        }, this.config.reconnectInterval)
    }

    /**
     * 关闭WebSocket连接
     * @param {Object} options 关闭选项
     * @param {number} [options.code=1000] 关闭码
     * @param {string} [options.reason=''] 关闭原因
     * @returns {Promise} 返回Promise，关闭成功时resolve，失败时reject
     */
    close(options = {}) {
        return new Promise((resolve, reject) => {
            // 如果未连接，则直接返回
            if (!this.connected || !this.socketTask) {
                resolve()
                return
            }

            // 设置手动关闭标志
            this.manualClose = true

            // 停止心跳
            this.stopHeartbeat()

            // 清除重连定时器
            if (this.reconnectTimer) {
                clearTimeout(this.reconnectTimer)
                this.reconnectTimer = null
            }

            // 关闭连接
            this.socketTask.close({
                code: options.code || 1000,
                reason: options.reason || '',
                success: () => {
                    this.connected = false
                    this.socketTask = null
                    resolve()
                },
                fail: (error) => {
                    console.error('WebSocket关闭失败:', error)
                    reject(error)
                }
            })
        })
    }

    /**
     * 销毁WebSocket实例，释放资源
     */
    destroy() {
        // 关闭连接
        this.close()

        // 清空消息队列
        this.messageQueue = []

        // 重置状态
        this.reconnectAttempts = 0
        this.connected = false
        this.manualClose = true
    }
}

export default WebSocketUtil 