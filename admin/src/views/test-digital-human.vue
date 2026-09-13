<template>
  <div class="test-page">
    <h1>数字人组件测试</h1>
    <p>如果下方显示数字人，说明组件工作正常</p>
    <div class="debug-info">
      <h3>调试信息：</h3>
      <p>本地配置: {{ hasLocalConfig ? '有' : '无' }}</p>
      <p>组件状态: {{ componentStatus }}</p>
      <button @click="checkConfig">检查配置</button>
      <button @click="clearConfig">清除配置</button>
    </div>
    <DigitalHumanAvatar />
  </div>
</template>

<script>
import DigitalHumanAvatar from '@/components/DigitalHumanAvatar.vue';

export default {
  name: 'TestDigitalHuman',
  components: {
    DigitalHumanAvatar
  },
  data() {
    return {
      hasLocalConfig: false,
      componentStatus: '未初始化'
    };
  },
  methods: {
    checkConfig() {
      const config = localStorage.getItem('digitalHumanConfig');
      this.hasLocalConfig = !!config;
      this.componentStatus = config ? '有配置' : '无配置';
      console.log('当前配置:', config);
      if (config) {
        console.log('解析后的配置:', JSON.parse(config));
      }
    },
    clearConfig() {
      localStorage.removeItem('digitalHumanConfig');
      this.hasLocalConfig = false;
      this.componentStatus = '已清除配置';
      console.log('配置已清除');
    }
  },
  mounted() {
    this.checkConfig();
  }
};
</script>

<style scoped>
.test-page {
  padding: 20px;
  min-height: 100vh;
  background: #f5f5f5;
}

h1 {
  color: #333;
  margin-bottom: 20px;
}

p {
  color: #666;
  margin-bottom: 20px;
}

.debug-info {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 40px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.debug-info h3 {
  margin-top: 0;
  color: #333;
}

.debug-info button {
  margin-right: 10px;
  padding: 8px 16px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.debug-info button:hover {
  background: #337ecc;
}
</style> 