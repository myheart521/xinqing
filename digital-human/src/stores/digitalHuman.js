import { defineStore } from 'pinia'

// 全局SDK实例，与配置页面保持一致
let avatarPlatform2 = null;

export const useDigitalHumanStore = defineStore('digitalHuman', {
  state: () => ({
    // 是否已初始化
    isInitialized: false,
    // 当前配置
    config: null,
    // 消息队列
    messageQueue: [],
    // 是否正在播放
    isPlaying: false,
    // 连接状态
    isConnected: false
  }),

  getters: {
    // 获取数字人实例
    getAvatarPlatform: (state) => avatarPlatform2,
    // 检查是否已初始化
    isReady: (state) => state.isInitialized && avatarPlatform2,
    // 检查是否已连接
    isReadyToSend: (state) => state.isInitialized && avatarPlatform2 && state.isConnected,
    // 获取当前配置
    getConfig: (state) => state.config
  },

  actions: {
    // 设置数字人实例
    setAvatarPlatform(platform) {
      avatarPlatform2 = platform;
      this.isInitialized = true;
    },

    // 设置配置
    setConfig(config) {
      this.config = config;
    },

    // 设置连接状态
    setConnectedStatus(status) {
      this.isConnected = status;
    },

    // 发送文字给数字人
    async sendTextToAvatar(text) {
      if (!this.isReady) {
        console.warn('数字人未初始化，无法发送文字');
        return false;
      }

      if (!this.isConnected) {
        console.warn('数字人未连接，无法发送文字');
        return false;
      }

      try {
        console.log('发送文字给数字人:', text);
        console.log('SDK实例类型:', typeof avatarPlatform2);
        console.log('SDK实例方法:', Object.getOwnPropertyNames(avatarPlatform2));
        console.log('当前配置:', this.config);
        
        // 检查SDK实例是否有writeText方法
        if (typeof avatarPlatform2.writeText !== 'function') {
          console.error('SDK实例没有writeText方法');
          return false;
        }
        
        // 构建TTS参数，使用配置中的设置
        const ttsParams = {
          volume: 100,
        };
        
        // 如果配置中有TTS设置，使用配置中的设置
        if (this.config && this.config.setglobalparamsform && this.config.setglobalparamsform.tts) {
          const configTts = this.config.setglobalparamsform.tts;
          ttsParams.vcn = configTts.vcn; // 声音ID
          ttsParams.speed = configTts.speed; // 语速
          ttsParams.pitch = configTts.pitch; // 语调
          ttsParams.volume = configTts.volume; // 音量
          console.log('使用配置中的TTS设置:', ttsParams);
        } else {
          console.log('使用默认TTS设置:', ttsParams);
        }
        
        // 调用SDK的文本投递方法，使用正确的参数结构
        const result = await avatarPlatform2.writeText(text, {
          nlp: false, // 关闭语义理解
          tts: ttsParams,
        });
        
        console.log('文字发送成功，结果:', result);
        return true;
      } catch (error) {
        console.error('发送文字失败:', error);
        console.error('错误详情:', {
          message: error.message,
          stack: error.stack,
          name: error.name
        });
        return false;
      }
    },

    // 发送动作给数字人
    async sendActionToAvatar(action) {
      if (!this.isReady) {
        console.warn('数字人未初始化，无法发送动作');
        return false;
      }

      if (!this.isConnected) {
        console.warn('数字人未连接，无法发送动作');
        return false;
      }

      try {
        console.log('发送动作给数字人:', action);
        console.log('SDK实例类型:', typeof avatarPlatform2);
        console.log('SDK实例方法:', Object.getOwnPropertyNames(avatarPlatform2));
        
        // 检查SDK实例是否有writeCmd方法
        if (typeof avatarPlatform2.writeCmd !== 'function') {
          console.error('SDK实例没有writeCmd方法');
          return false;
        }
        
        // 调用SDK的动作执行方法
        const result = await avatarPlatform2.writeCmd("action", action);
        
        console.log('动作发送成功，结果:', result);
        return true;
      } catch (error) {
        console.error('发送动作失败:', error);
        console.error('错误详情:', {
          message: error.message,
          stack: error.stack,
          name: error.name
        });
        return false;
      }
    },

    // 清空数字人实例
    clearAvatarPlatform() {
      if (avatarPlatform2) {
        try {
          avatarPlatform2.stop();
          avatarPlatform2.destroy();
          avatarPlatform2 = null;
        } catch (error) {
          console.error('销毁数字人实例失败:', error);
        }
      }
      this.isInitialized = false;
      this.isConnected = false;
    },

    // 重置状态
    reset() {
      this.clearAvatarPlatform();
      this.config = null;
      this.messageQueue = [];
      this.isPlaying = false;
      this.isConnected = false;
    },

    // 恢复音频播放
    async resumeAudio() {
      if (!this.isReady) {
        console.warn('数字人未初始化，无法恢复音频');
        return false;
      }

      try {
        console.log('尝试恢复音频播放');
        
        // 检查SDK实例是否有createPlayer方法
        if (typeof avatarPlatform2.createPlayer !== 'function') {
          console.error('SDK实例没有createPlayer方法');
          return false;
        }
        
        // 创建播放器并恢复播放
        const player = avatarPlatform2.createPlayer();
        if (player && typeof player.resume === 'function') {
          player.resume();
          console.log('音频播放已恢复');
          return true;
        } else {
          console.error('播放器没有resume方法');
          return false;
        }
      } catch (error) {
        console.error('恢复音频播放失败:', error);
        console.error('错误详情:', {
          message: error.message,
          stack: error.stack,
          name: error.name
        });
        return false;
      }
    }
  }
}) 