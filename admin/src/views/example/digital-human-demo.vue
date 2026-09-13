<template>
  <div class="digital-human-demo">
    <div class="demo-header">
      <h1>数字人通信示例</h1>
      <p>这个页面展示了如何从其他页面发送文字和动作给数字人</p>
    </div>

    <!-- 数字人控制器 -->
    <DigitalHumanController />

    <!-- 快速操作按钮 -->
    <div class="quick-actions">
      <h3>快速操作</h3>
      <div class="action-buttons">
        <el-button 
          v-for="action in quickActions" 
          :key="action.text"
          :type="action.type"
          @click="sendQuickMessage(action.text)"
        >
          {{ action.text }}
        </el-button>
      </div>
    </div>

    <!-- 使用说明 -->
    <div class="usage-guide">
      <h3>使用说明</h3>
      <div class="guide-content">
        <h4>在其他页面中使用数字人：</h4>
        <ol>
          <li>导入数字人store：
            <pre>import { useDigitalHumanStore } from '@/stores/digitalHuman';</pre>
          </li>
          <li>获取store实例：
            <pre>const digitalHumanStore = useDigitalHumanStore();</pre>
          </li>
          <li>发送文字：
            <pre>await digitalHumanStore.sendTextToAvatar('你好，数字人！');</pre>
          </li>
          <li>执行动作：
            <pre>await digitalHumanStore.sendActionToAvatar('A_RH_hello_O');</pre>
          </li>
        </ol>

        <h4>代码示例：</h4>
        <pre>
// 在任意组件中使用
import { useDigitalHumanStore } from '@/stores/digitalHuman';

export default {
  methods: {
    async sendMessageToAvatar() {
      const digitalHumanStore = useDigitalHumanStore();
      
      // 检查数字人是否已初始化
      if (digitalHumanStore.isReady) {
        // 发送文字
        await digitalHumanStore.sendTextToAvatar('你好！');
        
        // 执行动作
        await digitalHumanStore.sendActionToAvatar('A_RH_wave_O');
      } else {
        console.log('数字人未初始化');
      }
    }
  }
}
        </pre>
      </div>
    </div>
  </div>
</template>

<script>
import { useDigitalHumanStore } from '@/stores/digitalHuman';

export default {
  name: 'DigitalHumanDemo',
  components: {
  },
  data() {
    return {
      quickActions: [
        { text: '你好', type: 'primary' },
        { text: '今天天气怎么样？', type: 'success' },
        { text: '请介绍一下自己', type: 'info' },
        { text: '再见', type: 'warning' }
      ]
    };
  },
  methods: {
    // 发送快速消息
    async sendQuickMessage(text) {
      const digitalHumanStore = useDigitalHumanStore();
      
      if (!digitalHumanStore.isReady) {
        this.$message.warning('数字人未初始化，请先配置数字人');
        return;
      }

      try {
        const success = await digitalHumanStore.sendTextToAvatar(text);
        if (success) {
          this.$message.success(`发送成功: ${text}`);
        } else {
          this.$message.error('发送失败');
        }
      } catch (error) {
        console.error('发送消息失败:', error);
        this.$message.error('发送失败: ' + error.message);
      }
    }
  }
};
</script>

<style scoped>
.digital-human-demo {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.demo-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
}

.demo-header h1 {
  margin: 0 0 10px 0;
  font-size: 2rem;
}

.demo-header p {
  margin: 0;
  opacity: 0.9;
}

.quick-actions {
  margin: 30px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.quick-actions h3 {
  margin-bottom: 15px;
  color: #333;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.action-buttons .el-button {
  margin: 0;
}

.usage-guide {
  margin-top: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.usage-guide h3 {
  color: #333;
  margin-bottom: 20px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.usage-guide h4 {
  color: #555;
  margin: 20px 0 10px 0;
}

.guide-content ol {
  padding-left: 20px;
}

.guide-content li {
  margin: 10px 0;
  line-height: 1.6;
}

.guide-content pre {
  background: #f4f4f4;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 10px 0;
  font-size: 14px;
  border-left: 3px solid #667eea;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .digital-human-demo {
    padding: 10px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style> 