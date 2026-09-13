/**
 * 动作类型检查测试
 * 用于验证智能动作功能的类型安全性
 */

import { getSmartActions, getActionsByContent, getActionDelay } from './digitalHumanActions.js'
import { detectEmotion } from './digital-human.js'

/**
 * 测试不同类型的输入参数
 */
export const testInputTypes = () => {
  console.log('=== 动作类型检查测试 ===')
  
  const avatarId = '138805001' // 小满
  
  // 测试用例
  const testCases = [
    { name: '字符串', input: '你好，很高兴见到你' },
    { name: '数字', input: 123 },
    { name: '对象', input: { message: '你好' } },
    { name: '数组', input: ['你好', '再见'] },
    { name: 'null', input: null },
    { name: 'undefined', input: undefined },
    { name: '布尔值', input: true },
    { name: '空字符串', input: '' },
  ]
  
  testCases.forEach(testCase => {
    console.log(`\n--- 测试 ${testCase.name} ---`)
    console.log('输入:', testCase.input)
    
    try {
      // 测试 getActionsByContent
      const contentActions = getActionsByContent(testCase.input, avatarId, 1)
      console.log('内容动作结果:', contentActions.length > 0 ? '成功' : '无匹配')
      
      // 测试 getActionDelay
      const delay = getActionDelay(testCase.input)
      console.log('延迟计算结果:', delay)
      
      // 测试 detectEmotion
      const emotion = detectEmotion(testCase.input)
      console.log('情感分析结果:', emotion.primary || '无')
      
      // 测试 getSmartActions
      const smartActions = getSmartActions(testCase.input, avatarId, {
        emotion,
        maxActions: 1,
        includeRandom: false
      })
      console.log('智能推荐结果:', smartActions.length > 0 ? '成功' : '无匹配')
      
    } catch (error) {
      console.error('测试失败:', error.message)
    }
  })
  
  console.log('\n=== 测试完成 ===')
}

/**
 * 模拟uniapp消息测试
 */
export const testUniappMessage = () => {
  console.log('=== uniapp消息测试 ===')
  
  // 模拟不同类型的uniapp消息
  const uniappMessages = [
    '你好，很高兴见到你',
    { type: 'text', content: '你好' },
    { action: 'greeting', data: { message: '你好' } },
    123,
    null,
    undefined
  ]
  
  uniappMessages.forEach((message, index) => {
    console.log(`\n--- uniapp消息 ${index + 1} ---`)
    console.log('原始消息:', message)
    
    // 模拟App.vue中的处理逻辑
    const processedMessage = typeof message === 'string' ? message : JSON.stringify(message)
    console.log('处理后消息:', processedMessage)
    
    // 测试动作推荐
    try {
      const actions = getSmartActions(processedMessage, '138805001', {
        maxActions: 1,
        includeRandom: false
      })
      console.log('推荐动作:', actions.length > 0 ? actions[0].name : '无')
    } catch (error) {
      console.error('处理失败:', error.message)
    }
  })
  
  console.log('\n=== uniapp消息测试完成 ===')
}

/**
 * 运行所有测试
 */
export const runAllTests = () => {
  testInputTypes()
  testUniappMessage()
}

// 如果直接运行此文件，执行测试
if (typeof window !== 'undefined' && window.location) {
  // 在浏览器环境中，可以通过控制台调用
  window.testActionTypes = {
    testInputTypes,
    testUniappMessage,
    runAllTests
  }
  
  console.log('动作类型检查测试已加载，可以通过以下方式运行:')
  console.log('- window.testActionTypes.runAllTests() // 运行所有测试')
  console.log('- window.testActionTypes.testInputTypes() // 测试输入类型')
  console.log('- window.testActionTypes.testUniappMessage() // 测试uniapp消息')
}

export default {
  testInputTypes,
  testUniappMessage,
  runAllTests
} 